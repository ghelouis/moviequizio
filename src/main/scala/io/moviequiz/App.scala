package io.moviequiz

import org.scalajs.dom
import org.scalajs.dom.{KeyCode, KeyboardEvent, document, html}

import scala.scalajs.js.Date
import scala.util.Random

object App:

  private val screenshotsPerMovie = 3

  private val movies = List(
    Movie("bound-(1996)", "Bound (1996)"),
    Movie("bound-(1996)", "Bound1 (1996)"),
    Movie("bound-(1996)", "Bound2 (1996)"),
    Movie("bound-(1996)", "Bound3 (1996)"),
    Movie("bound-(1996)", "Bound1 (1996)"),
    Movie("bound-(1996)", "Bound2 (1996)"),
    Movie("bound-(1996)", "Bound3 (1996)"),
    Movie("bound-(1996)", "Bound1 (1996)"),
    Movie("bound-(1996)", "Bound2 (1996)"),
    Movie("bound-(1996)", "Bound3 (1996)"),
    Movie("bound-(1996)", "Bound1 (1996)"),
    Movie("bound-(1996)", "Bound2 (1996)"),
    Movie("bound-(1996)", "Bound3 (1996)"),
    Movie("le-roi-lion-(1994)", "Le Roi Lion (1994)"),
    Movie("never-back-down-(2008)", "Never Back Down 2008)"),
    Movie("the-guest-(2014)", "The Guest (2014)")
  )

  private val movieNames = movies.map(_.name)

  private val rand = Random(getSeed)

  private var picked = Set.empty[Int]

  def main(args: Array[String]): Unit =
    println("Welcome to Movie Quiz!")
    document.addEventListener(
      "DOMContentLoaded",
      { (e: dom.Event) =>
        setupUI()
      }
    )

  private def setupUI(): Unit =
    val title = document.createElement("h1").asInstanceOf[html.Heading]
    title.textContent = "MovieQuiz.io"
    title.classList.add("title")
    document.body.appendChild(title)

    val button = dom.document.createElement("button").asInstanceOf[html.Button]
    button.classList.add("pushable")
    val shadow = dom.document.createElement("span")
    shadow.classList.add("shadow")
    val edge = dom.document.createElement("span")
    edge.classList.add("edge")
    val front = dom.document.createElement("span")
    front.classList.add("front")
    front.textContent = "▶"
    button.appendChild(shadow)
    button.appendChild(edge)
    button.appendChild(front)
    dom.document.body.appendChild(button)

    button.addEventListener(
      "click",
      (e: dom.MouseEvent) =>
        title.remove()
        button.remove()
        startGame()
    )

  private def startGame(): Unit =
    val title = document.createElement("h1").asInstanceOf[html.Heading]
    title.textContent = "MovieQuiz.io"
    title.classList.add("header")
    document.body.appendChild(title)

    val score = document.createElement("h1").asInstanceOf[html.Heading]
    score.textContent = "Score: 0"
    score.classList.add("score")
    document.body.appendChild(score)

    val movie = getRandomMovie
    addScreenshot(movie.slug)
    addGuessBox()

  private def addScreenshot(movieSlug: String): Unit =
    val screenshotNumber = rand.nextInt(screenshotsPerMovie) + 1

    document.body.style.backgroundImage = s"url('images/$movieSlug/screenshot$screenshotNumber.jpg')"
    document.body.style.backgroundPosition = "center"
    document.body.style.backgroundSize = "cover"
    document.body.style.backgroundRepeat = "no-repeat"

  private def addGuessBox(): Unit =
    val container = document.createElement("div")
    container.classList.add("suggestion-container")

    val input = document.createElement("input").asInstanceOf[html.Input]
    input.placeholder = "Guess the movie..."

    val clearBtn = document.createElement("span").asInstanceOf[html.Span]
    clearBtn.title = "Clear"
    clearBtn.classList.add("clear-btn")
    clearBtn.innerHTML = "&times;"

    val suggestions = document.createElement("ul").asInstanceOf[html.UList]
    container.appendChild(input)
    container.appendChild(clearBtn)
    container.appendChild(suggestions)
    document.body.appendChild(container)

    var filtered = Seq.empty[String]

    def renderList(): Unit =
      if filtered.isEmpty then input.classList.remove("has-suggestions")
      else input.classList.add("has-suggestions")
      suggestions.innerHTML = ""
      filtered.foreach { movieName =>
        val li = document.createElement("li").asInstanceOf[html.LI]
        li.textContent = movieName
        li.addEventListener("click", (_: dom.Event) => selectValue(movieName))
        li.style.cursor = "pointer"
        suggestions.appendChild(li)
      }

    def selectValue(value: String): Unit =
      input.value = value
      filtered = List.empty[String]
      renderList()

    input.addEventListener(
      "input",
      (_: dom.Event) =>
        if input.value.nonEmpty then clearBtn.style.display = "block"
        else clearBtn.style.display = "none"
        if input.value.length > 2 then
          filtered = movieNames.filter(_.toLowerCase.contains(input.value.toLowerCase)).take(7)
        else filtered = List.empty[String]
        renderList()
    )

    input.addEventListener(
      "keydown",
      (e: KeyboardEvent) =>
        if e.keyCode == KeyCode.Enter && filtered.nonEmpty then
          e.preventDefault()
          selectValue(filtered.head)
    )

    input.addEventListener(
      "blur",
      (_: dom.Event) =>
        dom.window.setTimeout(
          () =>
            filtered = List.empty[String]
            renderList()
          ,
          150
        )
    )

    clearBtn.addEventListener(
      "click",
      (_: dom.Event) =>
        input.value = ""
        clearBtn.style.display = "none"
        input.focus()
    )

    input.focus()

  private def getRandomMovie: Movie =
    var index = rand.nextInt(movies.size)
    while picked.contains(index) do index = rand.nextInt(movies.size)
    picked += index
    movies(index)

  private def getSeed: Int =
    val rootDate = new Date(2025, 9, 22)
    val currentDate = new Date()
    ((currentDate.getTime() - rootDate.getTime()) / (1000 * 60 * 60 * 24)).toInt
