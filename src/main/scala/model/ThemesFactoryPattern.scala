package model

//Interface:
trait Theme:
  def symbols: Vector[String]


//Themes:
class FruitsTheme extends Theme:
  val symbols = Vector("🍎","🍇","🍉","🍓","🍍","🍒","🍑","🍌")

class AnimalTheme extends Theme:
  val symbols = Vector("🐱","🐶","🐸","🐧","🐼","🦊","🐨","🐯")

class FlagsTheme extends Theme:
  val symbols = Vector("🇩🇪","🇫🇷","🇮🇹","🇪🇸","🇬🇧","🇺🇸","🇨🇦","🇯🇵")

class LandscapeTheme extends Theme:
  val symbols = Vector("🏞️","🏜️","🏝️","🏖️","🌋","🏔️","🏕️","🌅")

class VehiclesTheme extends Theme:
  val symbols = Vector("🚗","🚌","🏎️","🚓","🚑","🚒","🚜","✈️")

class SportsTheme extends Theme:
  val symbols = Vector("⚽","🏀","🏈","🎾","🏐","🏉","🎱","🏓")

class EmojiTheme extends Theme:
  val symbols = Vector("😀","😂","😍","😎","😭","😡","😴","🤢")


//FactoryMethod:
object ThemeFactory:
  def getTheme(themeName: String): Theme =
    themeName.toLowerCase match
      case "fruits"     => FruitsTheme()
      case "animals"    => AnimalTheme()
      case "flags"      => FlagsTheme()
      case "landscape"  => LandscapeTheme()
      case "vehicles"   => VehiclesTheme()
      case "sports"     => SportsTheme()
      case "emoji"      => EmojiTheme()
      case _            => FruitsTheme() // default theme
