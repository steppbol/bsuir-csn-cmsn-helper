import org.scalatest._

class Tests extends FlatSpec with Matchers{
  it should "task#1 empty string" in {
    val stringWithLongWords = ""
    Main.convertString(stringWithLongWords) should be (" ")
  }

  it should "task#1 one long word" in {
    val stringWithLongWords = "longlonglong"
    Main.convertString(stringWithLongWords) should be ("")
  }

  it should "task#1 many long word" in {
    val stringWithLongWords = "longlonglong longlonglong longlonglong longlonglong longlonglong"
    Main.convertString(stringWithLongWords) should be ("")
  }

  it should "task#2 Windows & Internet Explorer" in {
    Main.getDownLoadLink(Windows("Internet Explorer")) should be
    ("Error! Download another browser, for example \"Google Chrome\".")
  }

  it should "task#2 Linux & Google Chrome" in {
    Main.getDownLoadLink(Linux("Google Chrome")) should be
    ("https://www.jetbrains.com/idea/download/#section=Linux$")
  }

  it should "task#2 Linux & Mozila Firefox" in {
    Main.getDownLoadLink(Linux("Mozila Firefox")) should be
    ("https://www.jetbrains.com/idea/download/#section=Mac0S$")
  }
}

