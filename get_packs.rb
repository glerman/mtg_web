require 'watir-webdriver'
require 'byebug'

b = Watir::Browser.new :chrome
b.goto 'http://mtgtop8.com/search'



addresses = []

start_ind = 2
end_ind = 2
for ind in start_ind..end_ind

	packs = b.trs(:class =>'hover_tr')
	for row in packs
		addresses.push(row.td(:index => 1).a.attribute_value("href"))
	end

	b.div(:class => 'Nav_norm', :text => "#{ind}").click
	sleep 5
end

class Card

	def initialize(name, id)
    	@name = name
    	@id = id
    end

    def getName()
    	return @name
    end

    def getId()
    	return @id
    end

end

class Deckcard

	def initialize(card, amount)
		@card = card
		@amount = amount
	end

	def getCard
		return @card
	end

	def getAmount
		return @amount
	end

end

class Deck

	def initialize()
		@cards = []
	end

	def addCard(card)
		@cards.push(card)
	end

	# def removeCard(card)
	# 	#@cards.remove(card)
	# end

	def getCards()
		return @cards
	end
end

c = Watir::Browser.new :chrome
for address in addresses
	c.goto address
	# table = c.table(:class => 'Stable', :index => 1)
	# row1 = table.tr(:index => 2).table.trs
	# row2 = table.tr(:index => 3).table.trs
	cards = c.tds(:class => 'G14')
	deck = Deck.new()
	for card in cards
		new_card = Card.new(card.span.text, card.div.attribute_value('id')[2..-1])
		deck_card = Deckcard.new(new_card, card.div.value)
		deck.addCard(deck_card)
	end

	byebug

	c.close
	b.close
	return
end

b.close