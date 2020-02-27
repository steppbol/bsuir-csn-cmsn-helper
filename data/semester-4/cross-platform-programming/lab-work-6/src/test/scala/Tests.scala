import main.scala.Main
import org.scalatest._
class Tests extends FlatSpec with Matchers{

  it should "add element in list by index" in {
    val listOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1)
    Main.insertElementByIndex(listOfNumbers, 2, 42.0) should be (List(12.2, 14.0, 42.0, 17.7, 14.5, 42.0, 22.1))
  }

  it should "add element in empty list by index" in {
    val listOfNumbers = List[Double]()
    Main.insertElementByIndex(listOfNumbers, 2, 42.0) should be (List())
  }

  it should "add element in list with one element by index" in {
    val listOfNumbers = List[Double](43.0)
    Main.insertElementByIndex(listOfNumbers, 2, 42.0) should be (List(43.0))
  }

  it should "double to string in list" in {
    val listOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1)
    Main.doubleToString(listOfNumbers) should be (List("12.2", "14.0", "17.7", "14.5", "22.1"))
  }

  it should "double to string in empty list" in {
    val listOfNumbers = List[Double]()
    Main.doubleToString(listOfNumbers) should be (List())
  }

  it should "double to string in list with one element" in {
    val listOfNumbers = List[Double](43.0)
    Main.doubleToString(listOfNumbers) should be (List("43.0"))
  }

  it should "product even lines in matrix" in {
    val matrixOfNumbers = List[List[Double]](List(12.0, 14.0), List(17.7, 14.5), List(22.1, 18.4), List(22.9, 100.0))
    Main.productEvenRowInMatrix(matrixOfNumbers) should be (List(168.0, 406.64))
  }

  it should "product even lines in empty matrix" in {
    val matrixOfNumbers = List[List[Double]]()
    Main.productEvenRowInMatrix(matrixOfNumbers) should be (List())
  }

  it should "product even lines in matrix of two lines" in {
    val matrixOfNumbers = List[List[Double]](List(12.0, 14.0), List(17.7, 14.5))
    Main.productEvenRowInMatrix(matrixOfNumbers) should be (List(168.0))
  }

}
