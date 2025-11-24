package controller

enum GameStatus:
  case InvalidSelection(i: Int)
  case SecondCard
  case Match
  case NoMatch
  case NextRound
  case Idle

object GameStatus:
  def message(status: GameStatus): String = status match
    case InvalidSelection(i) => s"❗ Karte $i kann nicht gewählt werden."
    case SecondCard           => "zweite Karte wählen..."
    case Match               => "✅ Treffer!"
    case NoMatch             => "❌ Kein Treffer!"
    case NextRound           => "nächste Runde..."
    case Idle                => "" //neutraler Zustand->die TUI soll keine Nachricht ausgeben