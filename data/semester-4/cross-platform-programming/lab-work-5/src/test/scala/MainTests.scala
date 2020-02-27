import main.scala.Main
import org.scalatest._

class MainTests extends FlatSpec with Matchers {

  it should "getListSum equal getListSumTailrec" in {
    val listOfNumbers = List[Double](12.0, 14.0, -14.0, 26.0)
    assert(Main.getListSum(listOfNumbers) === Main.getListSumTailrec(listOfNumbers))
  }

  it should "index equal index(tailrec)" in {
    val sequenceOfNumbers = List[Double](12.0, 14.0, -14.0, 26.0)
    assert(Main.getIndexIncSequence(sequenceOfNumbers) === Main.getIndexIncSequenceTailrec(sequenceOfNumbers))
  }

  it should "Sum elements of list" in {
    val listOfNumbers = List[Double](12.0, 14.0, -14.0, 26.0)
    Main.getListSum(listOfNumbers) should be (38.0)
  }

  it should "Sum elements of empty list" in {
    val listOfNumbers = List[Double]()
    Main.getListSum(listOfNumbers) should be (0.0)
  }

  it should "Sum elements of list tailrec" in {
    val listOfNumbers = List[Double](12.0, 14.0, -26.0)
    Main.getListSumTailrec(listOfNumbers) should be (0.0)
  }

  it should "Sum elements of empty list tailrec" in {
    val listOfNumbers = List[Double]()
    Main.getListSumTailrec(listOfNumbers) should be (0.0)
  }

  it should "index of the beginning of the maximum interval" in {
    val sequenceOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1, 18.4, 22.9, 100.0, 12.6, 15.6)
    Main.getIndexIncSequence(sequenceOfNumbers) should be (5)
  }

  it should "index of the beginning of the maximum interval(all equal)" in {
    val sequenceOfNumbers = List[Double](10.0, 10.0, 10.0)
    Main.getIndexIncSequence(sequenceOfNumbers) should be (-1)
  }

  it should "index of the beginning of the maximum interval tailrec(all equal)" in {
    val sequenceOfNumbers = List[Double](10.0, 10.0, 10.0)
    Main.getIndexIncSequenceTailrec(sequenceOfNumbers) should be (0)
  }

  it should "index of the beginning interval" in {
    val sequenceOfNumbers = List[Double](10.0, 6.4, 2.3)
    Main.getIndexIncSequence(sequenceOfNumbers) should be (-1)
  }

  it should "index of the beginning interval tailrec" in {
    val sequenceOfNumbers = List[Double](10.0, 6.4, 2.3)
    Main.getIndexIncSequenceTailrec(sequenceOfNumbers) should be (0)
  }

}