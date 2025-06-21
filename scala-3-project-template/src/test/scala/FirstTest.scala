package test

import munit.FunSuite
import maath.FirstTask

class FirstTaskTest extends FunSuite {
    test("powerOfTwo computes 2^n correctly") {
        assertEquals(FirstTask.powerOfTwo(0), BigInt(1))
        assertEquals(FirstTask.powerOfTwo(3), BigInt(8))
    }

    test("powerOfTwo throws exception for negative n") {
        intercept[IllegalArgumentException] {
            FirstTask.powerOfTwo(-5)
        }
    }
    
}
