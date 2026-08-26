package io.moviequiz

import org.scalajs.dom.window.navigator

object Translations:

  enum Lang:
    case FR, EN

  private val translations = Map(
    "come_back_tomorrow_for_another_challenge" -> Map(
      Lang.FR -> "Reviens demain pour une nouvelle partie !",
      Lang.EN -> "Come back tomorrow for another challenge!"
    ),
    "copied" -> Map(
      Lang.FR -> "Copié !",
      Lang.EN -> "Copied!"
    ),
    "game_over" -> Map(
      Lang.FR -> "PERDU",
      Lang.EN -> "GAME OVER"
    ),
    "guess_the_movie" -> Map(
      Lang.FR -> "Devine le film...",
      Lang.EN -> "Guess the movie..."
    ),
    "play" -> Map(
      Lang.FR -> "Jouer",
      Lang.EN -> "Play"
    ),
    "score" -> Map(
      Lang.FR -> "Score",
      Lang.EN -> "Score"
    ),
    "share" -> Map(
      Lang.FR -> "Partager",
      Lang.EN -> "Share"
    ),
    "you_won" -> Map(
      Lang.FR -> "GAGNÉ",
      Lang.EN -> "YOU WON"
    )
  )

  private val lang = if navigator.language.startsWith("fr") then Lang.FR else Lang.EN

  def t(key: String): String =
    translations(key)(lang)
