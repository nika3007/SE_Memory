@main def MemoryGame(): Unit =
  println("Welcome to Memory!")

  //Spielfeld
  val field =
    """|[🍇] [] [] []
       |[] [🍎] [] []
       |[] [] [] []
       |[🍇] [] [] [] """.stripMargin

  //Spielfeld ausgeben
  println(field)