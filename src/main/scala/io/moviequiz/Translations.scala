package io.moviequiz

import org.scalajs.dom.window.navigator

object Translations:

  enum Lang:
    case FR, EN

  private val translations = Map(
    "about" -> Map(
      Lang.FR -> "À propos",
      Lang.EN -> "About"
    ),
    "about_content_1" -> Map(
      Lang.FR -> "Jeu créé par Guillaume Hélouis.",
      Lang.EN -> "Game created par Guillaume Hélouis."
    ),
    "about_content_2" -> Map(
      Lang.FR -> "100% gratuit et ",
      Lang.EN -> "100% free and "
    ),
    "about_content_3" -> Map(
      Lang.FR -> "open source",
      Lang.EN -> "open source"
    ),
    "about_content_4" -> Map(
      Lang.FR -> ", sans publicité ni suivi des utilisateurs d'aucune sorte.",
      Lang.EN -> ", without ads or user tracking of any sort."
    ),
    "about_content_5" -> Map(
      Lang.FR -> "Envoyer un mot : ",
      Lang.EN -> "Say hi: "
    ),
    "about_content_6" -> Map(
      Lang.FR -> "Merci pour la visite :)",
      Lang.EN -> "Thanks for visiting :)"
    ),
    "close" -> Map(
      Lang.FR -> "Fermer",
      Lang.EN -> "Close"
    ),
    "come_back_tomorrow_for_another_challenge" -> Map(
      Lang.FR -> "Reviens demain pour une nouvelle partie !",
      Lang.EN -> "Come back tomorrow for another challenge!"
    ),
    "copied" -> Map(
      Lang.FR -> "Copié !",
      Lang.EN -> "Copied!"
    ),
    "correct" -> Map(
      Lang.FR -> "Correct !",
      Lang.EN -> "Correct!"
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
    "support_this_project" -> Map(
      Lang.FR -> "Supporter ce projet",
      Lang.EN -> "Support this project"
    ),
    "you_won" -> Map(
      Lang.FR -> "GAGNÉ",
      Lang.EN -> "YOU WON"
    )
  )

  private val lang = if navigator.language.startsWith("fr") then Lang.FR else Lang.EN

  def t(key: String): String =
    translations(key)(lang)
