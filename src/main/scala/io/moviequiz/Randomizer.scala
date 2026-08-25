package io.moviequiz

import scala.util.Random

object Randomizer:

  /** The idea:
    *   - Shuffle all movies and play them in a row (an epoch). Then shuffle differently and repeat the
    *     process.
    *   - The first day of a new epoch has a risk of repeating movies from the day before (last day of
    *     previous epoch). To prevent that, we:
    *     - Compare both days and compute movies in common
    *     - If needed, swap out repeats with movies from subsequent days of the current epoch. Which gives us
    *       the final list for the current epoch.
    */
  def getMoviesToGuess(
      gameDayIndex: Int,
      movies: Seq[String],
      moviesPerGame: Int,
      screenshotsPerMovie: Int
  ): Seq[MovieToGuess] =
    // Compute epoch dynamically
    val daysInEpoch = movies.size / moviesPerGame
    val dayWithinEpoch = gameDayIndex % daysInEpoch
    val epoch = gameDayIndex / daysInEpoch

    // Epoch without adjustments for potential 1st day repeats
    val rawEpochMovies = Random(epoch).shuffle(movies)

    val yesterdayStartIndex = (daysInEpoch - 1) * moviesPerGame
    val yesterdayBatch = Random(epoch - 1)
      .shuffle(movies)
      .slice(yesterdayStartIndex, yesterdayStartIndex + moviesPerGame)

    val repeatedMovies = rawEpochMovies.take(moviesPerGame).intersect(yesterdayBatch)

    // We compute the final epoch by swapping out repeats if need be
    val finalEpochMovies = if repeatedMovies.isEmpty then rawEpochMovies
    else
      // Find movies from tomorrow onwards that weren't in yesterday's batch
      val replacements = rawEpochMovies
        .drop(moviesPerGame)
        .diff(yesterdayBatch)
        .take(repeatedMovies.size)

      // Mapping to swap elements in raw list (repeated movies by their replacements and vice versa)
      val swapMap = repeatedMovies.zip(replacements).toMap ++ replacements.zip(repeatedMovies).toMap
      rawEpochMovies.map(movie => swapMap.getOrElse(movie, movie))

    // Get today's batch from the final epoch
    val startIndex = dayWithinEpoch * moviesPerGame
    val todayBatch = finalEpochMovies.slice(startIndex, startIndex + moviesPerGame)

    // Compute final list of movies to guess (movie + screenshot)
    val rand = Random(gameDayIndex)
    todayBatch.map(movie => MovieToGuess(movie, rand.nextInt(screenshotsPerMovie) + 1))
