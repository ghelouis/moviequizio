package io.moviequiz

import scala.scalajs.js
import scala.scalajs.js.JSStringOps.*

object TitleNormalizer:

  def normalize(title: String): String =
    title
      .normalize(js.UnicodeNormalizationForm.NFD)
      .replaceAll("[\\u0300-\\u036f']", "")
      .toLowerCase
      .replaceAll("[-:.,]", " ")
      .replaceAll("\\s+", " ")
      .trim
