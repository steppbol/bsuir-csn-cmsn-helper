val stringWithLongWords = "How long how longlong how longlonglonglong will I slide"
convertString(stringWithLongWords)

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