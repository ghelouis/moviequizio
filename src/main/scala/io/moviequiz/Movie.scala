package io.moviequiz

import scala.scalajs.js
import scala.scalajs.js.JSON

case class Movie(slug: String, difficulty: Int, titles: List[String])

case class MovieToGuess(slug: String, titles: List[String], screenshot: Int)

case class MovieTitle(title: String, normalizedTitle: String)

object MovieParser:

  def fromJsonText(text: String): List[Movie] =
    JSON
      .parse(text)
      .selectDynamic("movies")
      .asInstanceOf[js.Array[js.Dynamic]]
      .map(json =>
        Movie(
          json.slug.asInstanceOf[String],
          json.difficulty.asInstanceOf[js.UndefOr[Int]].toOption.getOrElse(0),
          json.titles.asInstanceOf[js.Array[String]].toList
        )
      )
      .toList
