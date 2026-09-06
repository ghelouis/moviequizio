package io.moviequiz

import io.moviequiz.Randomizer
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec

class RandomizerSpec extends AnyFunSpec with GivenWhenThen with MockFactory:

  describe("getMoviesToGuess") {
    it("should not repeat movies on consecutive days and movies should occur evenly") {
      Given("input params")
      val gameDayIndex = 300 // start of an epoch (300 % 25 is 0)
      val movies = (1 to 250)
        .map(i =>
          Movie(
            s"movie-$i",
            if i % 9 == 0 then 7 else if i % 20 == 0 then 10 else 0,
            List(s"Movie $i", s"Le Movie $i")
          )
        )
        .toList
      val moviesPerGame = 10
      val conf = Config(moviesPerGame = moviesPerGame)
      val days = 1000 // 40 epochs (1000 / 25)

      When(s"getMoviesToGuess() is invoked every day for $days days")
      val moviesToGuess =
        (0 until days).flatMap(i => Randomizer.getMoviesToGuess(gameDayIndex + i, movies, conf))

      Then(s"$days * $moviesPerGame movies should be returned")
      assert(moviesToGuess.size == days * moviesPerGame)

      And("no consecutive day should have the same movie")
      moviesToGuess
        .map(_.slug)
        .grouped(moviesPerGame)
        .toSeq
        .sliding(2)
        .foreach { case Seq(today, tomorrow) =>
          assert(today.toSet.intersect(tomorrow.toSet).isEmpty)
        }

      And("movies should have been ordered by difficulty")
      val moviesToGuessForDay12 = moviesToGuess
        .grouped(moviesPerGame)
        .toSeq(12)
        .toList
      assert(
        moviesToGuessForDay12 == List(
          MovieToGuess("movie-183", List("Movie 183", "Le Movie 183"), 3), // difficulty: 0
          MovieToGuess("movie-143", List("Movie 143", "Le Movie 143"), 4), // difficulty: 0
          MovieToGuess("movie-32", List("Movie 32", "Le Movie 32"), 1), // difficulty: 0
          MovieToGuess("movie-67", List("Movie 67", "Le Movie 67"), 1), // difficulty: 0
          MovieToGuess("movie-68", List("Movie 68", "Le Movie 68"), 9), // difficulty: 0
          MovieToGuess("movie-50", List("Movie 50", "Le Movie 50"), 8), // difficulty: 0
          MovieToGuess("movie-94", List("Movie 94", "Le Movie 94"), 5), // difficulty: 0
          MovieToGuess("movie-97", List("Movie 97", "Le Movie 97"), 6), // difficulty: 0
          MovieToGuess("movie-225", List("Movie 225", "Le Movie 225"), 4), // 225 % 9 = 0 -> difficulty: 7
          MovieToGuess("movie-240", List("Movie 240", "Le Movie 240"), 2) // 240 % 20 = 0 -> difficulty: 10
        )
      )

      And("movies should have occurred evenly")
      moviesToGuess
        .map(_.slug)
        .groupBy(identity)
        .values
        .foreach(movieOccurrences => assert(movieOccurrences.size == 40))

      And("screenshot numbers should be varied (close to the average since picked randomly)")
      moviesToGuess
        .map(_.screenshot)
        .groupBy(identity)
        .values
        .foreach(screenshotNumberOccurrences =>
          assert(screenshotNumberOccurrences.size > 900 && screenshotNumberOccurrences.size < 1100)
        )
    }
  }
