package maath
import scala.util.Random



object SecondTask {

  /**
   * Подсчитывает количество нечётных чисел в последовательности.
   *
   * @param sequence Входная последовательность целых чисел 
   * @return Количество нечётных чисел в последовательности
   * @example countOddNumbers(Seq(1, 2, 3)) вернёт 2
   */
  def countOddNumbers(sequence: Seq[Int]): Int = {
    sequence.count(_ % 2 != 0) // Фильтруем числа, где остаток от деления на 2 не равен 0
  }

  /**
   * Генерирует последовательность случайных целых чисел.
   *
   * @param n        Длина последовательности (должна быть положительной)
   * @param maxValue Максимальное значение элементов (по умолчанию 100)
   * @return Последовательность из `n` случайных чисел от 0 до `maxValue - 1`
   * @throws IllegalArgumentException если `n <= 0`
   * @example generateRandomSeq(3, 10) может вернуть Seq(2, 7, 4)
   */
  def generateRandomSeq(n: Int, maxValue: Int = 100): Seq[Int] = {
    require(n > 0, "Длина последовательности `n` должна быть положительной")
    (1 to n).map(_ => Random.nextInt(maxValue)) // Генерируем `n` случайных чисел
  }
}
