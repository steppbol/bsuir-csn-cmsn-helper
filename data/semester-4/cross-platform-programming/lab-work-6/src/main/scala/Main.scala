package main.scala

object Main{
  def main(args: Array[String]): Unit = {
    val listOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1, 18.4, 22.9, 100.0, 12.6, 15.6)
    val matrixOfNumbers = List[List[Double]](List(12.2, 14.0), List(17.7, 14.5), List(22.1, 18.4), List(22.9, 100.0))
    insertElementByIndex(listOfNumbers, 2, 42.0)
    doubleToString(listOfNumbers)
    productEvenRowInMatrix(matrixOfNumbers)
  }

  def insertElementByIndex(listOfNumbers: List[Double], index: Int, element: Double): List[Double] = {
    val sizeOfList = listOfNumbers.length

    def insert(listOfNumbers: List[Double], index: Int, element: Double,
               sizeOfList: Int, currentIndex: Int): List[Double] = {
      if (sizeOfList != 0) {
        if (currentIndex >= index) {
          listOfNumbers.head :: element :: insert(listOfNumbers.tail, index, element, sizeOfList - 1, 1)
        } else listOfNumbers.head :: insert(listOfNumbers.tail, index, element, sizeOfList - 1, currentIndex + 1)
      } else Nil
    }
    insert(listOfNumbers, index, element, sizeOfList, 1)
  }

  def doubleToString(listOfNumbers: List[Double]): List[String] = {
    listOfNumbers.foldRight(List[String]()){
      (currentElement: Double, newList: List[String]) => currentElement.toString :: newList
    }
  }

  def productEvenRowInMatrix(listOfNumbers: List[List[Double]]): List[Double] = {
    def product(currentRow: List[Double]): Double = {
      def step(currentRow: List[Double], acc: Double): Double = {
        currentRow match {
          case Nil => acc
          case x :: xs => step(xs, x * acc)
        }
      }
      step(currentRow, 1)
    }

    val listWithIndexOfElements = listOfNumbers.zipWithIndex
    val listWithEvenRows = listWithIndexOfElements.filter(_._2 % 2 == 0).map(_._1).map(product(_))
    listWithEvenRows
  }
}
