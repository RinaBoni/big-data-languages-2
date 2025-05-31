
object Matt {
  // 1. Вычисление 2^n
  def powerOfTwo(n: Int): BigInt = {
    require(n >= 0, "n must be non-negative")
    BigInt(2).pow(n)
  }

  // 2. Подсчёт нечётных чисел в последовательности
  def countOddNumbers(sequence: Seq[Int]): Int = {
    sequence.count(_ % 2 != 0)
  }

  // 3. Вычисление двойной суммы
  def calculateDoubleSum(): BigInt = {
    (1 to 10).map { k =>
      val cube = BigInt(k).pow(3)
      val innerSum = (1 to 15).map(l => (k - l) * (k - l)).sum
      cube * innerSum
    }.sum
  }
}