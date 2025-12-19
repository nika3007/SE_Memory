package controller

enum GameStatus:
  case InvalidSelection(i: Int)
  case FirstCard             // wird gesetzt NACH dem Aufdecken der 1. Karte (human + AI)
  case SecondCard            // wird gesetzt NACH der 2. Karte (human + AI)
  case Match
  case NoMatch
  case NextRound
  case Idle

object GameStatus:
  def message(status: GameStatus): String = status match
    case InvalidSelection(i) => s"❗ Karte $i kann nicht gewählt werden."
    case Match               => "🎯 Match! nochmal dran!"
    case NoMatch             => "❌ No Match!"
    case NextRound           => "nächste Runde..."
    case _                   => ""      // FirstCard, SecondCard, Idle → KEINE Prints