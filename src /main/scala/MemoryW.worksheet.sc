case class Card(symbol: String, isRevealed: Boolean) //Bauplan für eine Karte

//erstellen ein Paar
val card1 = Card("A", false)
val card2 = Card("A", false)
val card3 = Card("T", false)

//Spieler deckt erste Karte auf
val card1Revealed = card1.copy(isRevealed = true) //Card1 🍎 aufdecken

//Spieler deckt 3. Karte auf
val card3Revealed = card3.copy(isRevealed = true) // Card3 🍇 aufdecken

//ist es ein Paar?
val isNoMatch = card1.symbol == card3.symbol //ist match 🍎 und 🍇 = false

//Spieler deckt 2. Karte auf
val card2Revealed = card2.copy(isRevealed = true) //Card2 🍎 aufdecken

//ist es ein Paar?
val isMatch = card1.symbol == card2.symbol //ist match 🍎 und 🍎 = true
