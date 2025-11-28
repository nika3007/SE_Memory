package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ThemeFactorySpec extends AnyWordSpec with Matchers {

  "ThemeFactory" should {

    "return FruitsTheme for 'fruits'" in {
      ThemeFactory.getTheme("fruits").symbols should contain ("🍎")
    }

    "return AnimalsTheme for 'animals'" in {
      ThemeFactory.getTheme("animals").symbols should contain ("🐱")
    }

    "return FlagsTheme for 'flags'" in {
      ThemeFactory.getTheme("flags").symbols should contain ("🇩🇪")
    }

    "return LandscapeTheme for 'landscape'" in {
      ThemeFactory.getTheme("landscape").symbols should contain ("🏞️")
    }

    "return VehiclesTheme for 'vehicles'" in {
      ThemeFactory.getTheme("vehicles").symbols should contain ("🚗")
    }

    "return SportsTheme for 'sports'" in {
      ThemeFactory.getTheme("sports").symbols should contain ("⚽")
    }

    "return EmojiTheme for 'emoji'" in {
      ThemeFactory.getTheme("emoji").symbols should contain ("😀")
    }

    "use FruitsTheme as default fallback" in {
      ThemeFactory.getTheme("unknown").symbols should contain ("🍎")
    }

  }
}
