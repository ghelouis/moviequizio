package io.moviequiz

import org.scalajs.dom.window.localStorage

case class Game(gameDayId: Int, score: Int, isFinished: Boolean)

object Storage:

  private enum Key:
    case GameDayId, Score, IsFinished

  def loadGame(gameDayId: Int): Option[Game] =
    val gameDayId = getIntItem(Key.GameDayId)
    val score = getIntItem(Key.Score)
    val isFinished = getBoolItem(Key.IsFinished)
    if gameDayId.isEmpty || score.isEmpty || isFinished.isEmpty then None
    else Some(Game(gameDayId.get, score.get, isFinished.get))

  def saveGame(game: Game): Unit =
    setItem(Key.GameDayId, game.gameDayId.toString)
    setItem(Key.Score, game.score.toString)
    setItem(Key.IsFinished, game.isFinished.toString)

  def clear(): Unit =
    Key.values.foreach(k => localStorage.removeItem(k.toString))

  private def setItem(key: Key, value: String): Unit =
    localStorage.setItem(key.toString, value)

  private def getIntItem(key: Key): Option[Int] =
    Option(localStorage.getItem(key.toString)).map(_.toInt)

  private def getBoolItem(key: Key): Option[Boolean] =
    Option(localStorage.getItem(key.toString)).map(_.toBoolean)
