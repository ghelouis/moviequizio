package io.moviequiz

import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.prop.TableDrivenPropertyChecks.*

class TitleNormalizerSpec extends AnyFunSpec with GivenWhenThen with MockFactory:

  private val tests = Table(
    ("input", "expected"),
    ("test-test", "test test"),
    ("test:test", "test test"),
    ("test: test", "test test"),
    ("test : test", "test test"),
    ("test.test", "test test"),
    ("test,test", "test test"),
    ("test, test", "test test"),
    ("test'test", "testtest"),
    ("testïùôéèàç", "testiuoeeac")
  )

  describe("normalize") {
    it("should normalize all input strings as expected") {
      forAll(tests) { (input, expected) =>
        Given(s"an input of $input")

        When("we invoke normalize")
        val normalized = TitleNormalizer.normalize(input)
        val normalizedFromUpperCase = TitleNormalizer.normalize(input.toUpperCase)

        Then(s"the result should be $expected")
        assert(normalized == expected)

        And(s"the result should be $expected for an upper case input")
        assert(normalizedFromUpperCase == expected)
      }
    }
  }
