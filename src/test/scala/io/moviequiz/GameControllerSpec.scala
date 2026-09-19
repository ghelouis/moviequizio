package io.moviequiz

import io.moviequiz.{Config, Game, GameController, Storage, UI}
import org.scalamock.scalatest.MockFactory
import org.scalatest.GivenWhenThen
import org.scalatest.funspec.AnyFunSpec

import scala.scalajs.js.Date

class GameControllerSpec extends AnyFunSpec with GivenWhenThen with MockFactory:

  private val movies = (1 to 250)
    .map(i =>
      Movie(
        s"movie-$i",
        if i % 9 == 0 then 7 else if i % 20 == 0 then 10 else 0,
        List(s"Movie $i", s"Le Movie $i")
      )
    )
    .toList

  private val movieTitles =
    movies.flatMap(movie => movie.titles.map(title => MovieTitle(title, TitleNormalizer.normalize(title))))

  private val conf = Config("https://test.cdn.moviequiz.io", 10, 10, 5, 0)

  private val storage = mock[Storage]

  private val gameDayIndex: Int =
    val rootDate = new Date(2025, 9, 22)
    ((new Date(2025, 10, 1).getTime() - rootDate.getTime()) / (1000 * 60 * 60 * 24)).toInt

  describe("init") {
    it("should register the ui.onStart callback as expected") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("no game is returned from storage")
      storage.getGame.expects(gameDayIndex).returns(None)

      And("we mock the expected UI calls")
      mockWelcomeRendered(ui)
      val renderTitleAndScoreCallHandler = ui.renderTitleAndScore.expects(0)
      val url = "https://test.cdn.moviequiz.io/images/movie-19/4.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val renderGuessBoxCallHandler = ui.renderGuessBox.expects(*)

      When("we init the game and invoke ui.onStart()")
      gameController.init()
      ui.onStart()

      Then("the title, score, screenshot and guess box should have been rendered")
      renderTitleAndScoreCallHandler.once()
      renderScreenshotCallHandler.once()
      renderGuessBoxCallHandler.once()
    }

    it("should register the ui.onGuess callback as expected for a correct answer leading to victory") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController =
        GameController(conf.copy(moviesPerGame = 1), movies, gameDayIndex, ui, storage)

      And("no game is returned from storage")
      storage.getGame.expects(gameDayIndex).returns(None)

      And("we mock the expected UI calls")
      mockWelcomeRendered(ui)
      val renderVictoryScreenCallHandler = ui.renderVictoryScreen.expects(1, gameDayIndex)
      val saveGameCallHandler = storage.saveGame.expects(Game(gameDayIndex, 1, true))

      When("we init the game and invoke ui.onGuess() with a correct answer")
      gameController.init()
      ui.onGuess("Le Movie 32")

      Then("the victory screen should have been rendered and the game saved")
      renderVictoryScreenCallHandler.once()
      saveGameCallHandler.once()
    }

    it(
      "should register the ui.onGuess callback as expected for a correct answer not leading to victory yet"
    ) {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("no game is returned from storage")
      storage.getGame.expects(gameDayIndex).returns(None)

      And("we mock the expected UI calls")
      mockWelcomeRendered(ui)
      val refreshScoreCallHandler = ui.refreshScore.expects(1)
      val showSuccessCallHandler = (() => ui.showSuccess()).expects()
      val clearGuessBoxCallHandler = (() => ui.clearGuessBox()).expects()
      val url = "https://test.cdn.moviequiz.io/images/movie-84/1.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val saveGameCallHandler = storage.saveGame.expects(Game(gameDayIndex, 1, false))
      val hideSuccessCallHandler = (() => ui.hideSuccess()).expects()

      When("we init the game and invoke ui.onGuess() with a correct answer")
      gameController.init()
      ui.onGuess("Le Movie 19")

      Then("the score should have been refreshed, guess box cleared, screenshot rendered and game saved")
      refreshScoreCallHandler.once()
      clearGuessBoxCallHandler.once()
      renderScreenshotCallHandler.once()
      hideSuccessCallHandler.once()
      saveGameCallHandler.once()
    }

    it("should register the ui.onGuess callback as expected for a wrong answer") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("no game is returned from storage")
      storage.getGame.expects(gameDayIndex).returns(None)

      And("we mock the expected UI calls")
      mockWelcomeRendered(ui)
      val renderFailScreenCallHandler = ui.renderFailScreen.expects(0, gameDayIndex)
      val saveGameCallHandler = storage.saveGame.expects(Game(gameDayIndex, 0, true))

      When("we init the game and invoke ui.onGuess() with a wrong answer")
      gameController.init()
      ui.onGuess("Le Movie 6")

      Then("the fail screen should have been rendered and the game saved")
      renderFailScreenCallHandler.once()
      saveGameCallHandler.once()
    }

    it("should load the current game properly if one is returned from storage for a victory") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return a victorious game from storage")
      val game = makeGame(score = conf.moviesPerGame, isFinished = true)
      storage.getGame.expects(gameDayIndex).returns(Some(game))

      And("we mock the expected UI calls")
      ui.addListener.expects(*)
      val url = "https://test.cdn.moviequiz.io/images/movie-20/5.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val renderVictoryScreenCallHandler = ui.renderVictoryScreen.expects(game.score, gameDayIndex)

      When("we init the game")
      gameController.init()

      Then("the screenshot and victory screen should have been rendered")
      renderScreenshotCallHandler.once()
      renderVictoryScreenCallHandler.once()
    }

    it("should load the game properly if returned from storage for a defeat with a 0 score victory") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return a lost game with a 0 score from storage")
      val game = makeGame(score = 0, isFinished = true)
      storage.getGame.expects(gameDayIndex).returns(Some(game))

      And("we mock the expected UI calls")
      ui.addListener.expects(*)
      val url = "https://test.cdn.moviequiz.io/images/movie-19/4.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val renderFailScreenCallHandler = ui.renderFailScreen.expects(game.score, gameDayIndex)

      When("we init the game")
      gameController.init()

      Then("the screenshot and fail screen should have been rendered")
      renderScreenshotCallHandler.once()
      renderFailScreenCallHandler.once()
    }

    it("should load the game properly if returned from storage for a non-zero defeat") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return a lost game with a non-zero defeat from storage")
      val game = makeGame(score = 1, isFinished = true)
      storage.getGame.expects(gameDayIndex).returns(Some(game))

      And("we mock the expected UI calls")
      ui.addListener.expects(*)
      val url = "https://test.cdn.moviequiz.io/images/movie-84/1.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val renderFailScreenCallHandler = ui.renderFailScreen.expects(game.score, gameDayIndex)

      When("we init the game")
      gameController.init()

      Then("the screenshot and fail screen should have been rendered")
      renderScreenshotCallHandler.once()
      renderFailScreenCallHandler.once()
    }

    it("should load the game properly if returned from storage in progress") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return an in-progress game from storage")
      val game = makeGame(score = 1, isFinished = false)
      storage.getGame.expects(gameDayIndex).returns(Some(game))

      And("we mock the expected UI calls")
      ui.addListener.expects(*)
      val url = "https://test.cdn.moviequiz.io/images/movie-84/1.avif"
      val renderScreenshotCallHandler = ui.renderScreenshot.expects(url, *).onCall { (_, onMovieDisplayed) =>
        onMovieDisplayed()
      }
      val renderTitleAndScoreCallHandler = ui.renderTitleAndScore.expects(game.score)
      val renderGuessBoxCallHandler = ui.renderGuessBox.expects(*)

      When("we init the game")
      gameController.init()

      Then("the screenshot, title, score and guess box should have been rendered")
      renderScreenshotCallHandler.once()
      renderTitleAndScoreCallHandler.once()
      renderGuessBoxCallHandler.once()
    }

    it("should clear the storage and render the welcome screen if there is an old saved game") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return a game with a different game day index from today's from storage")
      val game = makeGame(gameDayIndex - 1)
      storage.getGame.expects(gameDayIndex).returns(Some(game))

      And("we mock the expected UI calls")
      ui.addListener.expects(*)
      val clearStorageCallHandler = (() => storage.clear()).expects()
      val renderWelcomeScreenCallHandler = (() => ui.renderWelcomeScreen()).expects()

      When("we init the game")
      gameController.init()

      Then("the storage should have been cleared and the welcome screen rendered")
      clearStorageCallHandler.once()
      renderWelcomeScreenCallHandler.once()
    }

    it("should render the welcome screen if there is no saved game to load") {
      Given("a gameController with mocked dependencies")
      val ui = mock[UI]
      val gameController = GameController(conf, movies, gameDayIndex, ui, storage)

      And("we mock the call to return no game from storage and render the welcome screen")
      storage.getGame.expects(gameDayIndex).returns(None)
      val addListenerCallHandler = ui.addListener.expects(*)
      val renderWelcomeScreenCallHandler = (() => ui.renderWelcomeScreen()).expects()

      When("we init the game")
      gameController.init()

      Then("the welcome screen should have been rendered")
      renderWelcomeScreenCallHandler.once()
    }
  }

  private def mockWelcomeRendered(ui: UI): Unit =
    val addListenerCallHandler = ui.addListener.expects(*)
    val renderWelcomeScreenCallHandler = (() => ui.renderWelcomeScreen()).expects()

  private def makeGame(gameDayIndex: Int = gameDayIndex, score: Int = 2, isFinished: Boolean = false) =
    Game(gameDayIndex, score, isFinished)
