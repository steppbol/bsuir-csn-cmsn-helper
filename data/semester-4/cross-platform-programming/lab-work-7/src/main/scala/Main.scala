object Main {
  def main(args: Array[String]): Unit = {
    val stringWithLongWords = "How long how longlong how longlonglonglong will I slide"
    convertString(stringWithLongWords)
    println(getDownLoadLink(Windows("Mozila Firefox")))
  }

  def convertString(stringWithLongWords: String): String = {
    def transform(stringWithLongWordsList: List[String]): String = {
      stringWithLongWordsList match {
        case Nil => ""
        case x :: xs if x.length > 10 => transform(xs)
        case x :: xs if x.length >= 5 && x.length < 10 => x.slice(0, 3) + " " + transform(xs)
        case x :: xs => x + " " + transform(xs)
      }
    }
    transform(stringWithLongWords.split(" ").toList)
  }

  def getDownLoadLink(operatingSystem: OperatingSystem): String = {
    operatingSystem match {
      case Windows(browser) if browser.equals("Enternet Explorer")
      => "Error! Download another browser, for example \"Google Chrome\"."
      case Windows(browser) if !browser.equals("Enternet Explorer")
      => "https://www.jetbrains.com/idea/download/#section=" + Windows.getClass.getName
      case Linux(browser) => "https://www.jetbrains.com/idea/download/#section=" + Linux.getClass.getName
      case MacOS(browser) => "https://www.jetbrains.com/idea/download/#section=macos" + MacOS.getClass.getName
    }
  }
}
