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
      val movieNames = (1 to 250).map(i => s"movie-$i")
      val moviesPerGame = 10
      val screenshotsPerMovie = 10
      val days = 1000 // 40 epochs (1000 / 25)

      When(s"getMoviesToGuess() is invoked every day for $days days")
      val moviesToGuess = (0 until days).flatMap(i =>
        Randomizer.getMoviesToGuess(
          gameDayIndex + i,
          movieNames,
          moviesPerGame,
          screenshotsPerMovie
        )
      )

      Then(s"$days * $moviesPerGame movies should be returned")
      val movies = moviesToGuess.map(_.slug)
      assert(movies.size == days * moviesPerGame)

      And("no consecutive day should have the same movie")
      movies
        .grouped(moviesPerGame)
        .toSeq
        .sliding(2)
        .foreach { case Seq(today, tomorrow) =>
          assert(today.toSet.intersect(tomorrow.toSet).isEmpty)
        }

      And("movies should have occurred evenly")
      movies
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
