package maath

object FirstTask {
  
  /**
   * Метод powerOfTwo вычисляет 2 в степени n
   * 
   * @param n - показатель степени (должен быть неотрицательным)
   * @return BigInt - результат возведения 2 в степень n
   * @throws IllegalArgumentException если n отрицательное
   */
  def powerOfTwo(n: Int): BigInt = {
    // Проверка что n не отрицательное
    require(n >= 0, "n должно быть положительным")
    
    // Вычисление 2^n 
    BigInt(2).pow(n)
  }
}