package io.moviequiz

import scala.scalajs.js.Date
import scala.util.Random

case class Movie(slug: String, name: String)

class GameController(ui: UI):

  private val maxNumberOfMoviesPerGame = 10

  private val numberOfScreenshotsPerMovie = 3

  private val moviesOrdered = List(
    Movie("bound-(1996)", "Bound (1996)"),
    Movie("le-roi-lion-(1994)", "Le Roi Lion (1994)"),
    Movie("never-back-down-(2008)", "Never Back Down 2008)"),
    Movie("the-guest-(2014)", "The Guest (2014)")
  )

  private val numberOfDaysSinceInception: Int =
    val rootDate = new Date(2025, 9, 22)
    val currentDate = new Date()
    ((currentDate.getTime() - rootDate.getTime()) / (1000 * 60 * 60 * 24)).toInt

  private val rand = Random(numberOfDaysSinceInception)

  private val movies = rand.shuffle(moviesOrdered)

  private var currentMovieIndex = 0

  private var score = 0

  def init(): Unit =
    ui.onStart = () => startGame()
    ui.onGuess = movieName => guess(movieName)
    ui.renderWelcomeScreen()

  private def startGame(): Unit =
    ui.renderTitleAndScore()
    displayMovie()
    ui.renderGuessBox(movies.map(_.name))

  private def displayMovie(): Unit =
    val movie = movies(currentMovieIndex)
    val screenshotNumber = rand.nextInt(numberOfScreenshotsPerMovie) + 1
    ui.renderScreenshot(movie.slug, screenshotNumber)

  private def guess(movieName: String): Unit =
    if movieName == movies(currentMovieIndex).name then winRound()
    else lose()

  private def winRound(): Unit =
    score += 1
    if score == maxNumberOfMoviesPerGame then ui.renderVictoryScreen(score, numberOfDaysSinceInception)
    else
      ui.refreshScore(score)
      ui.clearGuessBox()
      currentMovieIndex += 1
      displayMovie()

  private def lose(): Unit =
    ui.renderFailScreen(score, numberOfDaysSinceInception)

object GameController:
  def apply(ui: UI): GameController = new GameController(ui)
