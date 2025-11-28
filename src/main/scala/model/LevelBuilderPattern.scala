package model

//Board-Size: -----------------------------------------------
case class BoardSize(rows: Int, cols: Int)

object BoardSizes:
  val Small3x3     = BoardSize(2, 2)
  val Medium4x4    = BoardSize(4, 4)
  val Large6x6     = BoardSize(6, 6)
  val ExtraLarge8x8 = BoardSize(8, 8)



//Difficulty: -----------------------------------------------
case class Difficulty(matchAmount: Int)

object Difficulties:
  val Easy   = Difficulty(2)
  //val Medium = Difficulty(3)
  val Hard   = Difficulty(4)



//Level-Aufbau (size + difficulty): -----------------------------------------------
final case class Level(
    size: BoardSize, 
    difficulty: Difficulty,
    timeLimitSeconds: Int = 0
)


//LevelBuilder: -----------------------------------------------
class LevelBuilder:
    private var size: BoardSize = BoardSizes.Medium4x4
    private var difficulty: Difficulty = Difficulties.Easy
    private var timeLimitSeconds: Int = 0

    def setSize(s: BoardSize): LevelBuilder =
        size = s
        this

    def setDifficulty(d: Difficulty): LevelBuilder =
        difficulty = d
        this

    def setTimeLimit(seconds: Int): LevelBuilder =
        timeLimitSeconds = seconds
        this

    def build(): Level =
        Level(size, difficulty, timeLimitSeconds)