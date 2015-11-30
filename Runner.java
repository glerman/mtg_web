import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by glerman on 14/11/15.
 */
public class Runner {

  private static final String BASE_URI = "https://deckbox.org";

  public static void main(String[] args) throws IOException {
    // Fetch and parse the first deck search result page
    final String deckSearchUrl = BASE_URI + "/decks/mtg";
    final Document firstDeckSearchPage = fetchUrl(deckSearchUrl);
    parseDeckPage(firstDeckSearchPage);

    // Parse out next search page url and the number of pages
    final Element paginationControls = firstDeckSearchPage.getElementsByClass("pagination_controls").first();
    final Elements pageLinks = paginationControls.select("a[href]");
    final Element lastPageLink = pageLinks.last();
    final String lastPageHref = lastPageLink.attr("href");
    final String[] hrefSplit = lastPageHref.split("=");
    final String pageLessHref = hrefSplit[0];
    final int lastPage = Integer.valueOf(hrefSplit[1]);

    // Parse all the remaining deck pages
    for (int currPage = 2 ; currPage <= lastPage ; currPage++) {
      final String nextPageUri = BASE_URI + pageLessHref + currPage;
      final Document nextDeckSearchPage = fetchUrl(nextPageUri);
      parseDeckPage(nextDeckSearchPage);
    }
  }

  private static void parseDeckPage(final Document deckSearchMainPage) throws IOException {
    System.out.println("Parsing deck page: " + deckSearchMainPage.baseUri());
    final Elements links = deckSearchMainPage.select("a[href]");
    for(final Element deckLink : links) {
      String linkHref = deckLink.attr("href");

      if (linkHref.startsWith("/sets/")) {
        final String deckUrl = BASE_URI + linkHref;
        final Element deckNameElem = deckLink.children().last();
        final String deckName = deckNameElem.childNode(0).toString();
        final Document deckPage = fetchUrl(deckUrl);
        parseDeckPage(deckPage, deckName);
      }
    }
  }

  private static void parseDeckPage(final Document deckPage, final String deckName) {
    System.out.println("Deck name:" + deckName);

    final Elements possibleCards = deckPage.select("tr:matches(\\d+)");

    final Elements mainBoardCards = new Elements();
    final Elements sideBoardCards = new Elements();
    for (final Element card : possibleCards) {
      if (card.id().contains("_main")) {
        mainBoardCards.add(card);
      } else if (card.id().contains("_sideboard")) {
        sideBoardCards.add(card);
      }
    }
    System.out.println("Main Board");
    parseCardTable(mainBoardCards);
    System.out.println("Side Board");
    parseCardTable(sideBoardCards);
  }

  private static void parseCardTable(final Elements cards) {
    for (final Element card : cards) {
      final String cardCount = card.getElementsByClass("card_count").first().childNode(0).toString();
      final String cardName = card.getElementsByClass("card_name").first().childNode(1).childNode(0).toString();
      System.out.println(cardCount + " " + cardName);
    }

  }

  private static Document fetchUrl(final String url) throws IOException {
    HttpClient client = new HttpClient();

    // Create a method instance.
    GetMethod method = new GetMethod(url);

    // Provide custom retry handler is necessary
    method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
            new DefaultHttpMethodRetryHandler(3, false));

    try {
      // Execute the method.
      int statusCode = client.executeMethod(method);

      if (statusCode != HttpStatus.SC_OK) {
        System.err.println("Method failed: " + method.getStatusLine());
      }

      // Read the response body.
      final InputStream responseBody = method.getResponseBodyAsStream();
      final String responseCharSet = method.getResponseCharSet();
      // Deal with the response.
      // Use caution: ensure correct character encoding and is not binary data
      return Jsoup.parse(responseBody, responseCharSet, url);

    } finally {
      // Release the connection.
      method.releaseConnection();
    }
  }
}
