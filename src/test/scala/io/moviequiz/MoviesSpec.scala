package io.moviequiz

import io.moviequiz.Movies
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec

class MoviesSpec extends AnyFunSpec with GivenWhenThen with MockFactory:

  describe("fromJsonText") {
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
      val movies = Movies.fromJsonText(moviesJsonText)

      Then("the slugs should be as expected")
      val expectedSlugs = Seq(
        "movie-1",
        "movie-2",
        "movie-3",
        "movie-4"
      )
      assert(movies.slugs == expectedSlugs)

      And("the slugsToTitles map should be as expected")
      val expectedSlugsToTitles = Map(
        "movie-1" -> Set("The Movie 1"),
        "movie-2" -> Set(
          "The Movie 2",
          "Le Movie 2"
        ),
        "movie-3" -> Set(
          "The Movie 3",
          "Le Movie 3"
        ),
        "movie-4" -> Set(
          "The Movie 4",
          "Le Movie 4",
          "Il Movie 4"
        )
      )
      assert(movies.slugsToTitles == expectedSlugsToTitles)
    }
  }
