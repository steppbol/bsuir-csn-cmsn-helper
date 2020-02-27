package main.scala

import scala.annotation.tailrec

object Main {
  def main(args: Array[String]): Unit = {
    val listOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1, 18.4, 22.9, 100.0, 12.6, 15.6)
    getListSumTailrec(listOfNumbers)
    getListSum(listOfNumbers)

    val sequenceOfNumbers = List[Double](12.2, 14.0, 17.7, 14.5, 22.1, 18.4, 22.9, 100.0, 12.6, 15.6)
    println(getIndexIncSequenceTailrec(sequenceOfNumbers))
    println(getIndexIncSequence(sequenceOfNumbers))
  }

  def getListSumTailrec(listOfNumbers: List[Double]): Double = {
    val sizeOfList = listOfNumbers.length

    @tailrec
    def accumulator(acc: Double, listOfNumbers: List[Double], sizeOfList: Int): Double = {
      if (sizeOfList != 0)
        accumulator(acc + listOfNumbers(sizeOfList - 1), listOfNumbers, sizeOfList - 1)
      else
        acc
    }

    accumulator(0, listOfNumbers, sizeOfList)
  }

  def getListSum(listOfNumbers: List[Double]): Double = {

    val sizeOfList = listOfNumbers.length

    def accumulator(listOfNumbers: List[Double], sizeOfList: Int): Double = {
      if (sizeOfList != 0)
        listOfNumbers(sizeOfList - 1) + accumulator(listOfNumbers, sizeOfList - 1)
      else
        0
    }

    accumulator(listOfNumbers, sizeOfList)
  }

  def getIndexIncSequenceTailrec(sequenceOfNumbers: List[Double]): Int = {
    val sizeOfList = sequenceOfNumbers.length

    def getDifference(firstParam: Double, secondParam: Double): Double = {
      secondParam - firstParam
    }

    @tailrec
    def findMaxSequence(sequenceOfNumbers: List[Double], sizeOfList: Int, minIndex: Int,
                        maxDifference: Double, currentIndex: Int, currentMinIndex: Int): Int = {
      if (currentIndex != sizeOfList) {
        if (sequenceOfNumbers(currentIndex) < sequenceOfNumbers(currentIndex - 1)) {
          val difference = getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex - 1))

          if (difference > maxDifference)
            findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex, difference, currentIndex + 1, currentIndex)
          else
            findMaxSequence(sequenceOfNumbers, sizeOfList, minIndex, maxDifference, currentIndex + 1, currentIndex)
        } else
          findMaxSequence(sequenceOfNumbers, sizeOfList, minIndex, maxDifference, currentIndex + 1, currentMinIndex)
      } else if (getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex - 1)) > maxDifference)
        currentMinIndex
      else if (getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex - 1)) > maxDifference)
        currentMinIndex
      else
        minIndex
    }

    findMaxSequence(sequenceOfNumbers, sizeOfList, 0, 0, 1, 0)
  }

  def getIndexIncSequence(sequenceOfNumbers: List[Double]): Int = {
    val sizeOfList = sequenceOfNumbers.length

    def getDifference(firstParam: Double, secondParam: Double): Double = {
      secondParam - firstParam
    }

    def findMaxSequence(sequenceOfNumbers: List[Double], sizeOfList: Int, minIndex: Int,
                        maxDifference: Double, currentIndex: Int, currentMinIndex: Int, acc: Int): Int = {
      if (currentIndex == sizeOfList) {
        -acc
      } else if (sequenceOfNumbers(currentIndex) < sequenceOfNumbers(currentIndex - 1)) {
        if (maxDifference < getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex - 1))) {
          1 + findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex,
            getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex - 1)), currentIndex + 1,
            currentIndex, currentIndex - currentMinIndex)
        } else {
          1 + findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex,
            maxDifference, currentIndex + 1,
            currentIndex, acc + 1)
        }
      } else if (currentIndex == sizeOfList - 1) {
        if (maxDifference < getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex))) {
          1 + findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex,
            getDifference(sequenceOfNumbers(currentMinIndex), sequenceOfNumbers(currentIndex)), currentIndex + 1,
            currentIndex, currentIndex - currentMinIndex)
        } else {
          1 + findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex,
            maxDifference, currentIndex + 1,
            currentMinIndex, acc + 1)
        }
      } else {
        1 + findMaxSequence(sequenceOfNumbers, sizeOfList, currentMinIndex,
          maxDifference, currentIndex + 1,
          currentMinIndex, acc + 1)
      }
    }
    findMaxSequence(sequenceOfNumbers, sizeOfList, 0, 0, 1, 0, 1)
  }
}


