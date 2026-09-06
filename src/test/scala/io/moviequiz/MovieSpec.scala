package io.moviequiz

import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec

class MovieSpec extends AnyFunSpec with GivenWhenThen with MockFactory:

  describe("MovieParser.fromJsonText") {
    it("should parse JSON text to movies as expected") {
      Given("movies as JSON text")
      val moviesJsonText =
        """
        {
          "movies": [
            {
              "slug": "movie-1",
              "titles": [
                "The Movie 1"
              ]
            },
            {
              "slug": "movie-2",
              "difficulty": 7,
              "titles": [
                "The Movie 2",
                "Le Movie 2"
              ]
            },
            {
              "slug": "movie-3",
              "titles": [
                "The Movie 3",
                "Le Movie 3"
              ]
            },
            {
              "slug": "movie-4",
              "difficulty": 10,
              "titles": [
                "The Movie 4",
                "Le Movie 4",
                "Il Movie 4"
              ]
            }
          ]
        }
      """

      When("we parse the text to movies")
      val movies = MovieParser.fromJsonText(moviesJsonText)

      Then("the result should be as expected")
      val expectedList = List(
        Movie("movie-1", 0, List("The Movie 1")),
        Movie("movie-2", 7, List("The Movie 2", "Le Movie 2")),
        Movie("movie-3", 0, List("The Movie 3", "Le Movie 3")),
        Movie("movie-4", 10, List("The Movie 4", "Le Movie 4", "Il Movie 4"))
      )
      assert(movies == expectedList)
    }
  }
