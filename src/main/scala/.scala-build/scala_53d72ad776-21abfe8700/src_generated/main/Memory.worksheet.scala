

final class Memory$u002Eworksheet$_ {
def args = Memory$u002Eworksheet_sc.args$
def scriptPath = """c:\memory\src\main\scala\Memory.worksheet.sc"""
/*<script>*/
case class Card(symbol: String, isRevealed: Boolean) //Bauplan für eine Karte

//erstellen ein Paar
val card1 = Card("🍎", false)
val card2 = Card("🍎", false)
val card3 = Card("🍇", false)

val card1Revealed = card1.copy(isRevealed = true) //Card1 🍎 aufdecken

val card3Revealed = card3.copy(isRevealed = true) // Card3 🍇 aufdecken

val isNoMatch = card1.symbol == card3.symbol //ist match 🍎 und 🍇 = false



val card2Revealed = card2.copy(isRevealed = true) //Card2 🍎 aufdecken

val isMatch = card1.symbol == card2.symbol //ist match 🍎 und 🍎 = true




/*</script>*/ /*<generated>*//*</generated>*/
}

object Memory$u002Eworksheet_sc {
  private var args$opt0 = Option.empty[Array[String]]
  def args$set(args: Array[String]): Unit = {
    args$opt0 = Some(args)
  }
  def args$opt: Option[Array[String]] = args$opt0
  def args$: Array[String] = args$opt.getOrElse {
    sys.error("No arguments passed to this script")
  }

  lazy val script = new Memory$u002Eworksheet$_

  def main(args: Array[String]): Unit = {
    args$set(args)
    val _ = script.hashCode() // hashCode to clear scalac warning about pure expression in statement position
  }
}

export Memory$u002Eworksheet_sc.script as `Memory.worksheet`

