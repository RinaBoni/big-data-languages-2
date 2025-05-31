
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Matt extends AnyFlatSpec with Matchers {
  "powerOfTwo" should "correctly calculate 2^n" in {
    Matt.powerOfTwo(0) shouldBe BigInt(1)
    Matt.powerOfTwo(5) shouldBe BigInt(32)
    Matt.powerOfTwo(10) shouldBe BigInt(1024)
  }

  it should "throw exception for negative n" in {
    an[IllegalArgumentException] should be thrownBy Matt.powerOfTwo(-1)
  }
}