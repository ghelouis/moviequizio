package io.moviequiz

class GameController(conf: Config, movies: Movies, gameDayIndex: Int, ui: UI, storage: Storage):

  private val moviesToGuess =
    Randomizer.getMoviesToGuess(gameDayIndex, movies.slugs, conf.moviesPerGame, conf.screenshotsPerMovie)

  private val movieTitles = movies.slugsToTitles.values.flatten.toSeq

  private var score = 0

  def init(): Unit =
    ui.onStart = () => startGame()
    ui.onGuess = movieName => guess(movieName)
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
      displayMovie(score - 1)
      ui.renderVictoryScreen(score, gameDayIndex)
    else if game.isFinished then
      displayMovie(score)
      ui.renderFailScreen(score, gameDayIndex)
    else
      displayMovie(score)
      ui.renderTitleAndScore(score)
      ui.renderGuessBox(movieTitles)

  private def startGame(): Unit =
    ui.renderTitleAndScore(score)
    displayMovie(score)
    ui.renderGuessBox(movieTitles)

  private def displayMovie(movieToGuessIndex: Int): Unit =
    val movieToGuess = moviesToGuess(movieToGuessIndex)
    val url = s"${conf.cdn}/images/${movieToGuess.slug}/${movieToGuess.screenshot}.avif"
    ui.renderScreenshot(url)

  private def guess(movieName: String): Unit =
    if movies.slugsToTitles(moviesToGuess(score).slug).contains(movieName) then winRound()
    else lose()

  private def isVictory = score == conf.moviesPerGame

  private def winRound(): Unit =
    score += 1
    if isVictory then
      ui.renderVictoryScreen(score, gameDayIndex)
      storage.saveGame(Game(gameDayIndex, score, true))
    else
      ui.refreshScore(score)
      ui.clearGuessBox()
      displayMovie(score)
      storage.saveGame(Game(gameDayIndex, score, false))

  private def lose(): Unit =
    ui.renderFailScreen(score, gameDayIndex)
    storage.saveGame(Game(gameDayIndex, score, true))

object GameController:
  def apply(conf: Config, movies: Movies, gameDayIndex: Int, ui: UI, storage: Storage): GameController =
    new GameController(conf, movies, gameDayIndex, ui, storage)
