package test

import munit.FunSuite
import maath.ThirdTask

class ThirdTaskTest extends FunSuite {

    // Основной тест для проверки корректности вычислений
    // Использует оригинальные параметры из задания (k=1..10, l=1..15)
    test("calculateDoubleSum should compute correct sum for default case (k=10, l=15)") {
        // Ожидаемый результат был предварительно вычислен вручную/альтернативным способом
        val expected = BigInt(983455)
        
        // Проверяем совпадение фактического и ожидаемого результатов
        assertEquals(ThirdTask.calculateDoubleSum(10, 15), expected)
    }

    // Тест для различных комбинаций k и l
    test("calculateDoubleSum should work with different k and l values") {

        assertEquals(ThirdTask.calculateDoubleSum(1, 1), BigInt(0))
        
        assertEquals(ThirdTask.calculateDoubleSum(2, 2), BigInt(9))
    
        assertEquals(ThirdTask.calculateDoubleSum(3, 3), BigInt(156))
    }

    // Тест на обработку недопустимых входных данных
    test("calculateDoubleSum should throw exception for non-positive parameters") {
        // Проверяем случай, когда kMax <= 0
        intercept[IllegalArgumentException] {
        ThirdTask.calculateDoubleSum(0, 5)  // Нулевое значение k
        }
        
        // Проверяем случай, когда lMax <= 0
        intercept[IllegalArgumentException] {
        ThirdTask.calculateDoubleSum(5, -1)  // Отрицательное значение l
        }
        
        // Проверяем оба недопустимых параметра одновременно
        intercept[IllegalArgumentException] {
        ThirdTask.calculateDoubleSum(-3, 0)  // Оба параметра невалидны
        }
    }

    // Тест на неотрицательность результата
    // (По логике формулы результат всегда должен быть ≥ 0)
    test("calculateDoubleSum should return non-negative result") {
        // Проверяем для разных комбинаций параметров
        assert(ThirdTask.calculateDoubleSum(5, 5) >= 0)     // Маленькие значения
        assert(ThirdTask.calculateDoubleSum(10, 15) >= 0)   // Оригинальные параметры
        assert(ThirdTask.calculateDoubleSum(100, 100) >= 0) // Большие значения
    }

    // Тест на тип возвращаемого значения
    test("calculateDoubleSum should return BigInt") {
        // Проверяем, что возвращаемый тип - BigInt
        // даже для небольших результатов
        assert(ThirdTask.calculateDoubleSum(1, 1).isInstanceOf[BigInt])
        
        // И для больших результатов
        assert(ThirdTask.calculateDoubleSum(20, 20).isInstanceOf[BigInt])
    }
}