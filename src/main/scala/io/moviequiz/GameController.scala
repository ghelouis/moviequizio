package io.moviequiz

import scala.scalajs.js.Date
import scala.util.Random

class GameController(ui: UI):

  private val screenshotsPerMovie = 3

  private val movies = List(
    Movie("bound-(1996)", "Bound (1996)"),
    Movie("le-roi-lion-(1994)", "Le Roi Lion (1994)"),
    Movie("never-back-down-(2008)", "Never Back Down 2008)"),
    Movie("the-guest-(2014)", "The Guest (2014)")
  )

  private val movieNames = movies.map(_.name)

  private val rand = Random(getSeed)

  private var pastMoviesPicked = Set.empty[Int]

  private var score = 0

  def init(): Unit =
    ui.onStart = () => startGame()
    ui.renderWelcomeScreen()

  private def startGame(): Unit =
    ui.renderTitleAndScore()
    val movie = getRandomMovie
    val screenshotNumber = rand.nextInt(screenshotsPerMovie) + 1
    ui.renderScreenshot(movie.slug, screenshotNumber)
    ui.renderGuessBox(movieNames)

  private def getRandomMovie: Movie =
    var index = rand.nextInt(movies.size)
    while pastMoviesPicked.contains(index) do index = rand.nextInt(movies.size)
    pastMoviesPicked += index
    movies(index)

  private def getSeed: Int =
    val rootDate = new Date(2025, 9, 22)
    val currentDate = new Date()
    ((currentDate.getTime() - rootDate.getTime()) / (1000 * 60 * 60 * 24)).toInt

object GameController:
  def apply(ui: UI): GameController = new GameController(ui)
