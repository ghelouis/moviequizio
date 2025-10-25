package io.moviequiz

import org.scalajs.dom.{Event, KeyCode, KeyboardEvent, MouseEvent, document, html, window}

class UI:

  var onStart: () => Unit = () => ()

  def renderWelcomeScreen(): Unit =
    val title = document.createElement("h1").asInstanceOf[html.Heading]
    title.textContent = "MovieQuiz.io"
    title.classList.add("title")
    document.body.appendChild(title)

    val button = document.createElement("button").asInstanceOf[html.Button]
    button.classList.add("pushable")
    val shadow = document.createElement("span")
    shadow.classList.add("shadow")
    val edge = document.createElement("span")
    edge.classList.add("edge")
    val front = document.createElement("span")
    front.classList.add("front")
    front.textContent = "▶"
    button.appendChild(shadow)
    button.appendChild(edge)
    button.appendChild(front)
    document.body.appendChild(button)

    button.addEventListener(
      "click",
      (e: MouseEvent) =>
        title.remove()
        button.remove()
        onStart()
    )

  def renderTitleAndScore(): Unit =
    val title = document.createElement("h1").asInstanceOf[html.Heading]
    title.textContent = "MovieQuiz.io"
    title.classList.add("header")
    document.body.appendChild(title)

    val score = document.createElement("h1").asInstanceOf[html.Heading]
    score.textContent = "Score: 0"
    score.classList.add("score")
    document.body.appendChild(score)

  def renderScreenshot(movieSlug: String, screenshotNumber: Int): Unit =
    document.body.style.backgroundImage = s"url('images/$movieSlug/screenshot$screenshotNumber.jpg')"
    document.body.style.backgroundPosition = "center"
    document.body.style.backgroundSize = "cover"
    document.body.style.backgroundRepeat = "no-repeat"

  def renderGuessBox(movieNames: Seq[String]): Unit =
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
    var indexHighlighted: Option[Int] = None

    def renderList(): Unit =
      if filtered.isEmpty then input.classList.remove("has-suggestions")
      else input.classList.add("has-suggestions")

      if indexHighlighted.isDefined then
        val items = suggestions.getElementsByTagName("li")
        items(indexHighlighted.get).classList.remove("highlighted")
        indexHighlighted = None

      suggestions.innerHTML = ""
      filtered.foreach { movieName =>
        val li = document.createElement("li").asInstanceOf[html.LI]
        li.textContent = movieName
        li.addEventListener("click", (_: Event) => selectValue(movieName))
        li.style.cursor = "pointer"
        suggestions.appendChild(li)
      }

    def selectValue(value: String): Unit =
      input.value = value
      filtered = List.empty[String]
      renderList()

    input.addEventListener(
      "input",
      (_: Event) =>
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
          if indexHighlighted.isDefined then selectValue(filtered(indexHighlighted.get))
          else selectValue(filtered.head)
        if e.keyCode == KeyCode.Tab && filtered.nonEmpty then
          e.preventDefault()
          val items = suggestions.getElementsByTagName("li")
          if indexHighlighted.isDefined then
            items(indexHighlighted.get).classList.remove("highlighted")
            if e.shiftKey then
              indexHighlighted = Some((indexHighlighted.get - 1 + filtered.size) % filtered.size)
            else indexHighlighted = Some((indexHighlighted.get + 1) % filtered.size)
          else indexHighlighted = Some(0)
          items(indexHighlighted.get).classList.add("highlighted")
    )

    input.addEventListener(
      "blur",
      (_: Event) =>
        window.setTimeout(
          () =>
            filtered = List.empty[String]
            renderList()
          ,
          150
        )
    )

    clearBtn.addEventListener(
      "click",
      (_: Event) =>
        input.value = ""
        clearBtn.style.display = "none"
        input.focus()
    )

    input.focus()
