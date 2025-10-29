package io.moviequiz

import org.scalajs.dom
import org.scalajs.dom.document

object App:

  def main(args: Array[String]): Unit =
    println("Welcome to MovieQuiz.io!")

    val ui = UI()
    val gameController = GameController(ui)

    document.addEventListener(
      "DOMContentLoaded",
      (e: dom.Event) => gameController.init()
    )
