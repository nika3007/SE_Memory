@main def MemorySkalierbar(): Unit =
  println("Welcome to Memory!")

  val size = 6 // Anzahl der Reihen und Spalten
  val field = generateField(size) // hilfsfunktion unten definiert
  println(field)

def generateField(size: Int): String = //hilfsfunktion Spielfeld zu String
  val row = "[]" // eine leere Karte
  val line = (1 to size).map(_ => row).mkString(" ") //von 1 bis size wird leere karte wie eine liste gemacht
  (1 to size).map(_ => line).mkString("\n") //wiederholt line so oft bis size
