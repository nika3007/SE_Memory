package model

import scala.util.Random
import scala.collection.mutable


//Strategy Interface:--------------------------------------
trait AIPlayer:
  def chooseCard(board: Board): Int


//No AI/ Singleplayer:----------------------------------------------
class NoAI extends AIPlayer:
  override def chooseCard(board: Board): Int = -1 // wird einfach ignoriert


//Random AI (Easy):----------------------------------------------
class RandomAI extends AIPlayer:
  override def chooseCard(board: Board): Int =
    val hidden = board.cards.filter(c => !c.isFaceUp && !c.isMatched)
    hidden(Random.nextInt(hidden.size)).id

/*
//Medium AI (merkt sich letzte 4 Karten):----------------------------------------------
class MediumAI extends AIPlayer:

  // symbol → bekannte Positionen (max 2)
  private val memory = mutable.Map[String, mutable.ListBuffer[Int]]()
  private val maxEntries = 4  // maximal 4 Symbole merken

  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.zipWithIndex.collect {
        case (c, idx) if !c.isFaceUp && !c.isMatched => (c, idx)
      }
    
    cleanup(board)

    // 1️) Prüfen, ob wir ein bekanntes Paar haben
    var pairSelection: Option[Int] = None
    for (sym, list) <- memory do
      if list.size >= 2 then
        pairSelection = Some(list.head)

    pairSelection match
      case Some(i) => return i
      case None => ()

    // 2️) Prüfen, ob wir eine erste passende Karte kennen
    var singleSelection: Option[Int] = None
    for (card, idx) <- hidden do
      if memory.contains(card.symbol) then
        val list = memory(card.symbol)
        if list.nonEmpty then
          singleSelection = Some(list.head)

    singleSelection match
      case Some(i) => return i
      case None => ()

    // 3️) Neue Karte wählen
    val (pick, idx) = hidden(Random.nextInt(hidden.size))
    remember(pick.symbol, idx)
    idx

  private def remember(sym: String, idx: Int): Unit =
    val list = memory.getOrElseUpdate(sym, mutable.ListBuffer())
    if !list.contains(idx) then list += idx

    if memory.size > maxEntries then
      val oldest = memory.keys.head
      memory.remove(oldest)

  private def cleanup(board: Board): Unit =
    for (sym, list) <- memory do
      list --= list.filter(id =>
        board.cards(id).isFaceUp || board.cards(id).isMatched
      )
*/

//Hard AI (merkt alles aber nicht perfekt):----------------------------------------------
class HardAI extends AIPlayer:

  // symbol → alle bekannten Positionen
  private val memory = mutable.Map[String, mutable.ListBuffer[Int]]()

  override def chooseCard(board: Board): Int =
    val hidden = board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    cleanup(board)

    // If we know a matching pair → choose one position
    val knownPair =
      memory.collectFirst {
        case (sym, list) if list.size >= 2 =>
          list.head
      }
    if knownPair.isDefined then
      return knownPair.get


    // Else take random new card
    val pick = hidden(Random.nextInt(hidden.size))
    remember(pick.symbol, pick.id)
    pick.id

  private def cleanup(board: Board): Unit =
    for (sym, list) <- memory do
      list --= list.filter(id => board.cards(id).isMatched)

  private def remember(symbol: String, id: Int): Unit =
    val list = memory.getOrElseUpdate(symbol, mutable.ListBuffer())
    if !list.contains(id) then list += id


//MemoryAI (PRO- perfekte AI):------------------------
class MemoryAI extends AIPlayer:

  // symbol → alle bekannten Positionen
  private val memory = mutable.Map[String, mutable.ListBuffer[Int]]()
  private var firstPick: Option[(String, Int)] = None

  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.filter(c => !c.isFaceUp && !c.isMatched)

    cleanup(board)

    // 1) If first pick exists → check if memory has a match
    val secondMatch =
      firstPick.flatMap { (sym, pos) =>
        memory.get(sym).flatMap(_.find(_ != pos))
      }

    if secondMatch.isDefined then
      firstPick = None
      return secondMatch.get


    // 2) Try to find any matching pair
    val known =
      memory.collectFirst {
        case (sym, list) if list.size >= 2 =>
          (sym, list.head)
      }

    known match
      case Some((sym, pos)) =>
        firstPick = Some(sym -> pos)
        pos
      case None =>
        // 3) Otherwise → pick new unseen card
        val pick = hidden(Random.nextInt(hidden.size))
        remember(pick)
        firstPick = Some((pick.symbol, pick.id))
        pick.id

  private def remember(card: Card): Unit =
    val list = memory.getOrElseUpdate(card.symbol, mutable.ListBuffer())
    if !list.contains(card.id) then list += card.id

  private def cleanup(board: Board): Unit =
    for (sym, list) <- memory do
      list --= list.filter(id => board.cards(id).isMatched)



/* 
Die Ai's laufen aber meist nur bis sie die gematched Karten versuchen zu wählen, 
-> muss verbessert werden.
Weiß auch nicht so sicher ob alle andere schwierigkeitsgrade haben
-> sonst  nur 1 Ai und none zu wahl.
Test: easy lief gut, medium sehr gut, hard ab zu viele matches wählte zu oft die schon 
gematched und dann prints bisschen unpassend, pro naja
die tonne.
*/


/* 
neue Version HardAi: 
class HardAI extends AIPlayer:

  private val memory = mutable.Map[String, mutable.ListBuffer[Int]]()

  override def chooseCard(board: Board): Int =
    val hidden =
      board.cards.zipWithIndex.collect {
        case (c, idx) if !c.isFaceUp && !c.isMatched => idx
      }

    cleanup(board)

    // 1️⃣ Prüfen, ob wir ein bekanntes Paar haben
    var best: Option[Int] = None
    for (sym, list) <- memory do
      val validPositions = list.filter(hidden.contains)
      if validPositions.size >= 2 then
        best = Some(validPositions.head)

    best match
      case Some(i) => return i
      case None => ()

    // 2️⃣ Prüfen, ob wir zumindest 1 passende Karte kennen
    var single: Option[Int] = None
    for (sym, list) <- memory do
      val validPositions = list.filter(hidden.contains)
      if validPositions.nonEmpty then
        single = Some(validPositions.head)

    single match
      case Some(i) => return i
      case None => ()

    // 3️⃣ Zufallszug
    val pick = hidden(Random.nextInt(hidden.size))
    remember(board.cards(pick).symbol, pick)
    pick

  private def remember(symbol: String, id: Int): Unit =
    val list = memory.getOrElseUpdate(symbol, mutable.ListBuffer())
    if !list.contains(id) then
      list += id

  private def cleanup(board: Board): Unit =
    for (sym, list) <- memory do
      list --= list.filter(id =>
        board.cards(id).isFaceUp || board.cards(id).isMatched
      )

*/


/* 
neue Version Pro Ai:
class MemoryAI extends AIPlayer:

  private val memory = mutable.Map[String, mutable.ListBuffer[Int]]()
  private var firstPick: Option[Int] = None

  override def chooseCard(board: Board): Int =

    val hidden =
      board.cards.zipWithIndex.collect {
        case (c, idx) if !c.isFaceUp && !c.isMatched => idx
      }

    cleanup(board)

    // 1️⃣ Falls wir schon eine erste Karte gewählt haben → passende zweite suchen
    firstPick match
      case Some(pos) =>
        val sym = board.cards(pos).symbol
        val list = memory.getOrElse(sym, mutable.ListBuffer())
        val rest = list.filter(i => i != pos && hidden.contains(i))
        if rest.nonEmpty then
          firstPick = None
          return rest.head
      case None => ()

    // 2️⃣ Prüfen, ob ein bekanntes Paar existiert
    var known: Option[Int] = None
    for (sym, list) <- memory do
      val valid = list.filter(hidden.contains)
      if valid.size >= 2 then
        known = Some(valid.head)

    known match
      case Some(i) =>
        firstPick = Some(i)
        return i
      case None => ()

    // 3️⃣ Neue Karte aufdecken
    val pick = hidden(Random.nextInt(hidden.size))
    val sym = board.cards(pick).symbol
    remember(sym, pick)
    firstPick = Some(pick)
    pick

  private def remember(sym: String, id: Int): Unit =
    val list = memory.getOrElseUpdate(sym, mutable.ListBuffer())
    if !list.contains(id) then list += id

  private def cleanup(board: Board): Unit =
    for (sym, list) <- memory do
      list --= list.filter(id =>
        board.cards(id).isFaceUp || board.cards(id).isMatched
      )


*/

/* aufgaben:
1. neue AI-versionen mal testen.
2. evtl prints anpassen wenn AI gematched karten wählen will.
3. ALLE PACKS anpassen, coverage gesunken auf 47-56% und Test errors!!
*/