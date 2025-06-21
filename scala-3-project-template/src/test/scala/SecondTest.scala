package test

import munit.FunSuite
import scala.util.Random

import maath.SecondTask

class SecondTaskTest extends FunSuite {

  // Тесты для countOddNumbers
    test("countOddNumbers should correctly count odd numbers") {
        assertEquals(SecondTask.countOddNumbers(Seq.empty), 0)
        assertEquals(SecondTask.countOddNumbers(Seq(1, 2, 3)), 2)
        assertEquals(SecondTask.countOddNumbers(Seq(2, 4, 6)), 0)
        assertEquals(SecondTask.countOddNumbers(Seq(-1, -3, 0)), 2)
    }

    test("countOddNumbers should work with large sequences") {
        val largeSeq = Seq.fill(1000)(1)
        assertEquals(SecondTask.countOddNumbers(largeSeq), 1000)
    }

  // Тесты для generateRandomSeq
    test("generateRandomSeq should generate a sequence of correct length") {
        assertEquals(SecondTask.generateRandomSeq(5).length, 5)
        assertEquals(SecondTask.generateRandomSeq(10, 50).length, 10)
    }

    test("generateRandomSeq should throw IllegalArgumentException for n <= 0") {
        intercept[IllegalArgumentException] {
            SecondTask.generateRandomSeq(0)
        }
        intercept[IllegalArgumentException] {
            SecondTask.generateRandomSeq(-5)
        }
    }

    test("generateRandomSeq should generate values within bounds") {
        val seq = SecondTask.generateRandomSeq(100, 10)
        assert(seq.forall(x => x >= 0 && x < 10), "Все элементы должны быть в [0, 10)")
    }

    test("generateRandomSeq should produce different sequences (flaky but useful)") {
        val seq1 = SecondTask.generateRandomSeq(10)
        val seq2 = SecondTask.generateRandomSeq(10)
        assertNotEquals(seq1, seq2)
    }
}
