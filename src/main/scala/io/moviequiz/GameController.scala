package io.moviequiz

import org.scalajs.dom
import scala.scalajs.js.timers.setTimeout

class GameController(conf: Config, movies: List[Movie], gameDayIndex: Int, ui: UI, storage: Storage):

  private val moviesToGuess = Randomizer.getMoviesToGuess(gameDayIndex, movies, conf)

  private val slugToTitles = moviesToGuess.map(movie => movie.slug -> movie.titles).toMap

  private val movieTitles =
    movies.flatMap(movie => movie.titles.map(title => MovieTitle(title, TitleNormalizer.normalize(title))))

  private val titlesToMovies = movies.flatMap(movie => movie.titles.map(title => title -> movie.slug)).toMap

  private var score = 0

  def init(): Unit =
    ui.onStart = () => startGame()
    ui.onGuess = movieTitle => guess(movieTitle)
    ui.addListener(clear)
    storage.getGame(gameDayIndex) match
      case Some(game) if game.gameDayIndex == gameDayIndex =>
        loadGame(game)
      case Some(game) =>
        storage.clear()
        ui.renderWelcomeScreen()
      case None =>
        ui.renderWelcomeScreen()

  private def loadGame(game: Game): Unit =
    score = game.score
    if game.isFinished && isVictory then
      displayMovie(score - 1, () => ui.renderVictoryScreen(score, gameDayIndex))
    else if game.isFinished then displayMovie(score, () => ui.renderFailScreen(score, gameDayIndex))
    else
      displayMovie(
        score,
        () =>
          ui.renderTitleAndScore(score)
          ui.renderGuessBox(filterMovies)
      )

  private def startGame(): Unit =
    ui.renderTitleAndScore(score)
    displayMovie(score, () => ui.renderGuessBox(filterMovies))

  private def displayMovie(movieToGuessIndex: Int, onMovieDisplayed: () => Unit): Unit =
    val movieToGuess = moviesToGuess(movieToGuessIndex)
    val url = s"${conf.cdn}/images/${movieToGuess.slug}/${movieToGuess.screenshot}.avif"
    ui.renderScreenshot(url, onMovieDisplayed)

  private def guess(movieTitle: String): Unit =
    if slugToTitles(moviesToGuess(score).slug).contains(movieTitle) then winRound()
    else lose()

  private def clear(): Unit =
    storage.clear()
    dom.window.location.reload()

  private def isVictory = score == conf.moviesPerGame

  private def winRound(): Unit =
    score += 1
    if isVictory then
      ui.renderVictoryScreen(score, gameDayIndex)
      storage.saveGame(Game(gameDayIndex, score, true))
    else
      ui.refreshScore(score)
      ui.showSuccess()
      ui.clearGuessBox()
      if conf.nextRoundTimeout == 0 then displayMovie(score, () => ui.hideSuccess())
      else
        setTimeout(conf.nextRoundTimeout) {
          displayMovie(score, () => ui.hideSuccess())
        }
      storage.saveGame(Game(gameDayIndex, score, false))

  private def lose(): Unit =
    ui.renderFailScreen(score, gameDayIndex)
    storage.saveGame(Game(gameDayIndex, score, true))

  private def filterMovies(searchInput: String): List[String] =
    movieTitles
      .filter(_.normalizedTitle.contains(TitleNormalizer.normalize(searchInput)))
      .map(_.title)
      .take(conf.maxSearchResults)
      .reverse
      .map(title => titlesToMovies(title) -> title)
      .toMap
      .values
      .toList

object GameController:
  def apply(conf: Config, movies: List[Movie], gameDayIndex: Int, ui: UI, storage: Storage): GameController =
    new GameController(conf, movies, gameDayIndex, ui, storage)
