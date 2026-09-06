package io.moviequiz

import io.moviequiz.Translations.t
import org.scalajs.dom.html.{Button, Div, Heading, Image, Input, LI, Link, Span, UList}
import org.scalajs.dom.window.navigator
import org.scalajs.dom.{Element, Event, KeyCode, KeyboardEvent, MouseEvent, document, window}

import scala.scalajs.js.timers.setTimeout

class UI:

  var onStart: () => Unit = () => ()

  var onGuess: String => Unit = (_: String) => ()

  private val minInputLengthForSuggestion = 3

  private def createButton(
      textContent: String,
      title: String,
      className: Option[String] = None
  ): Button =
    val button = document.createElement("button").asInstanceOf[Button]
    button.classList.add("pushable")
    button.title = title
    val shadow = document.createElement("span")
    shadow.classList.add("shadow")
    val edge = document.createElement("span")
    edge.classList.add("edge")
    val front = document.createElement("span")
    front.classList.add("front")
    className.foreach(cls =>
      button.classList.add(cls)
      edge.classList.add(cls)
      front.classList.add(cls)
    )
    front.textContent = textContent
    button.appendChild(shadow)
    button.appendChild(edge)
    button.appendChild(front)
    button

  private def createTitle(textContent: String): Heading =
    val title = document.createElement("h1").asInstanceOf[Heading]
    title.textContent = textContent
    title.classList.add("title")
    title

  def addListener(onClear: () => Unit): Unit =
    document.addEventListener(
      "keydown",
      (event: KeyboardEvent) =>
        if event.target.asInstanceOf[Element].tagName != "INPUT" && event.key == "C" then onClear()
    )

  def renderWelcomeScreen(): Unit =
    val title = createTitle("MovieQuiz.io")
    document.body.appendChild(title)

    val startButton = createButton("▶", t("play"))
    document.body.appendChild(startButton)

    val aboutButtonContainer = document.createElement("div").asInstanceOf[Div]
    aboutButtonContainer.classList.add("about-button-container")
    val aboutButton = createButton(t("about"), t("about"), Some("about-button"))
    val closeButton = createButton("X", t("close"), Some("about-button"))
    closeButton.classList.add("hidden")
    aboutButtonContainer.append(aboutButton)
    aboutButtonContainer.append(closeButton)
    document.body.appendChild(aboutButtonContainer)

    val aboutContainer = createAboutContainer()
    aboutContainer.classList.add("hidden")
    document.body.appendChild(aboutContainer)

    def toggleAbout() =
      title.classList.toggle("hidden")
      startButton.classList.toggle("hidden")
      aboutButton.classList.toggle("hidden")
      closeButton.classList.toggle("hidden")
      aboutContainer.classList.toggle("hidden")

    startButton.addEventListener(
      "click",
      (e: MouseEvent) =>
        title.remove()
        startButton.remove()
        aboutButtonContainer.remove()
        onStart()
    )

    aboutButton.addEventListener(
      "click",
      (e: MouseEvent) => toggleAbout()
    )

    closeButton.addEventListener(
      "click",
      (e: MouseEvent) => toggleAbout()
    )

  private def createLink(text: String, url: String): Link =
    val link = document.createElement("a").asInstanceOf[Link]
    link.textContent = text
    link.href = url
    link

  private def createAboutContainer(): Element =
    val header = createTitle(t("about"))
    val content = document.createElement("p")
    content.textContent = t("about_content_1")

    val content2 = document.createElement("p")
    content2.appendChild(document.createTextNode(t("about_content_2")))
    val sourceCodeLink = createLink(t("about_content_3"), "https://github.com/ghelouis/moviequizio")
    content2.appendChild(sourceCodeLink)
    content2.appendChild(document.createTextNode(t("about_content_4")))

    val content3 = document.createElement("p")
    content3.appendChild(document.createTextNode(t("about_content_5")))
    val contactLink = createLink("hellothere@moviequiz.io", "mailto:hellothere@moviequiz.io")
    content3.appendChild(contactLink)
    val sponsorLink = createLink(s"❤️ ${t("support_this_project")}", "https://github.com/sponsors/ghelouis")

    val content4 = document.createElement("p")
    content4.textContent = t("about_content_6")

    val contentContainer = document.createElement("div")
    contentContainer.appendChild(content)
    contentContainer.appendChild(content2)
    contentContainer.appendChild(content3)
    contentContainer.appendChild(sponsorLink)
    contentContainer.appendChild(content4)

    val aboutContainer = document.createElement("div")
    aboutContainer.classList.add("about")
    aboutContainer.appendChild(header)
    aboutContainer.appendChild(contentContainer)
    aboutContainer

  def renderTitleAndScore(score: Int): Unit =
    val header = document.createElement("h1").asInstanceOf[Heading]
    header.textContent = "MovieQuiz.io"
    header.id = "left-header"
    document.body.appendChild(header)

    val scoreHeading = document.createElement("h1").asInstanceOf[Heading]
    scoreHeading.id = "right-header"
    scoreHeading.textContent = s"${t("score")}: "
    val scoreValue = document.createElement("span").asInstanceOf[Span]
    scoreValue.textContent = score.toString
    scoreHeading.appendChild(scoreValue)
    document.body.appendChild(scoreHeading)

  def renderScreenshot(url: String, onMovieDisplayed: () => Unit): Unit =
    val containers = document.querySelectorAll(".img-container").map(_.asInstanceOf[Div])
    val (activeContainer, inactiveContainer, inactiveImg, inactiveBg) = if containers.nonEmpty then
      val activeContainer = containers.find(_.classList.contains("active")).get
      val inactiveContainer = containers.find(!_.classList.contains("active")).get
      val inactiveImg = inactiveContainer.getElementsByTagName("img").head.asInstanceOf[Image]
      val inactiveBg = inactiveContainer.getElementsByClassName("bg").head.asInstanceOf[Div]
      (activeContainer, inactiveContainer, inactiveImg, inactiveBg)
    else
      val activeContainer = document.createElement("div").asInstanceOf[Div]
      activeContainer.classList.add("img-container")
      activeContainer.classList.add("active")
      val activeBg = document.createElement("div").asInstanceOf[Div]
      activeBg.classList.add("bg")
      val activeMain = document.createElement("div").asInstanceOf[Div]
      activeMain.classList.add("main")
      val activeImg = document.createElement("img").asInstanceOf[Image]

      val inactiveContainer = document.createElement("div").asInstanceOf[Div]
      inactiveContainer.classList.add("img-container")
      val inactiveBg = document.createElement("div").asInstanceOf[Div]
      inactiveBg.classList.add("bg")
      val inactiveMain = document.createElement("div").asInstanceOf[Div]
      inactiveMain.classList.add("main")
      val inactiveImg = document.createElement("img").asInstanceOf[Image]

      activeMain.appendChild(activeImg)
      activeContainer.appendChild(activeBg)
      activeContainer.appendChild(activeMain)

      inactiveMain.appendChild(inactiveImg)
      inactiveContainer.appendChild(inactiveBg)
      inactiveContainer.appendChild(inactiveMain)

      document.body.appendChild(activeContainer)
      document.body.appendChild(inactiveContainer)

      (activeContainer, inactiveContainer, inactiveImg, inactiveBg)

    inactiveImg.onload = _ =>
      inactiveBg.style.backgroundImage = s"url('$url')"
      inactiveContainer.classList.add("active")
      activeContainer.classList.remove("active")
      onMovieDisplayed()

    inactiveImg.src = url

  def renderGuessBox(filterMovies: (searchInput: String) => List[String]): Unit =
    val container = document.createElement("div")
    container.id = "suggestion-container"

    val input = document.createElement("input").asInstanceOf[Input]
    input.placeholder = t("guess_the_movie")

    val clearButton = document.createElement("span").asInstanceOf[Span]
    clearButton.title = "Clear"
    clearButton.classList.add("clear-btn")
    clearButton.innerHTML = "&times;"

    val suggestions = document.createElement("ul").asInstanceOf[UList]
    container.appendChild(input)
    container.appendChild(clearButton)
    container.appendChild(suggestions)
    document.body.appendChild(container)

    var filtered = List.empty[String]
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
        val li = document.createElement("li").asInstanceOf[LI]
        li.textContent = movieName
        li.addEventListener("click", (_: Event) => selectValue(movieName))
        li.style.cursor = "pointer"
        suggestions.appendChild(li)
      }

    def selectValue(value: String): Unit =
      input.value = value
      filtered = List.empty[String]
      renderList()
      onGuess(input.value)

    input.addEventListener(
      "input",
      (_: Event) =>
        if input.value.nonEmpty then clearButton.style.display = "block"
        else clearButton.style.display = "none"
        if input.value.length >= minInputLengthForSuggestion then filtered = filterMovies(input.value)
        else filtered = List.empty[String]
        renderList()
    )

    def highlightNextItem(): Unit =
      val items = suggestions.getElementsByTagName("li")
      if indexHighlighted.isDefined then
        items(indexHighlighted.get).classList.remove("highlighted")
        indexHighlighted = Some((indexHighlighted.get + 1) % filtered.size)
      else indexHighlighted = Some(0)
      items(indexHighlighted.get).classList.add("highlighted")

    def highlightPreviousItem(): Unit =
      val items = suggestions.getElementsByTagName("li")
      if indexHighlighted.isDefined then
        items(indexHighlighted.get).classList.remove("highlighted")
        indexHighlighted = Some((indexHighlighted.get - 1 + filtered.size) % filtered.size)
      else indexHighlighted = Some(0)
      items(indexHighlighted.get).classList.add("highlighted")

    input.addEventListener(
      "keydown",
      (e: KeyboardEvent) =>
        if filtered.nonEmpty then
          if e.keyCode == KeyCode.Enter then
            e.preventDefault()
            if indexHighlighted.isDefined then selectValue(filtered(indexHighlighted.get))
            else selectValue(filtered.head)
          else if (e.keyCode == KeyCode.Tab && e.shiftKey) || e.keyCode == KeyCode.Up then
            e.preventDefault()
            highlightPreviousItem()
          else if e.keyCode == KeyCode.Tab || e.keyCode == KeyCode.Down then
            e.preventDefault()
            highlightNextItem()
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

    clearButton.addEventListener(
      "click",
      (_: Event) => clearInput(input, clearButton)
    )

    if !isMobile then input.focus()

  private def clearInput(input: Input, clearButton: Span): Unit =
    input.value = ""
    clearButton.style.display = "none"
    if !isMobile then input.focus()

  def refreshScore(newScore: Int): Unit =
    val score = document.getElementById("right-header").querySelector("span").asInstanceOf[Span]
    score.textContent = newScore.toString

  private def createSuccess(): Heading =
    val success = document.createElement("h1").asInstanceOf[Heading]
    success.classList.add("success")
    success.textContent = t("correct")
    success.style.display = "flex"
    success

  def showSuccess(): Unit =
    val successElements = document.querySelectorAll(".success").map(_.asInstanceOf[Heading])
    if successElements.isEmpty then
      document
        .getElementsByClassName("main")
        .foreach(_.appendChild(createSuccess()))
    successElements.foreach(_.style.display = "flex")

  def hideSuccess(): Unit =
    document
      .getElementsByClassName("success")
      .map(_.asInstanceOf[Heading])
      .foreach(_.style.display = "none")

  def clearGuessBox(): Unit =
    val container = document.getElementById("suggestion-container")
    val input = container.getElementsByTagName("input").head.asInstanceOf[Input]
    val clearButton = container.getElementsByTagName("span").head.asInstanceOf[Span]
    clearInput(input, clearButton)
    if !isMobile then input.focus()

  private def renderEndScreen(titleText: String, finalScore: Int, gameDayIndex: Int): Unit =
    Option(document.getElementById("left-header")).foreach(_.remove())
    Option(document.getElementById("right-header")).foreach(_.remove())
    document.getElementsByTagName("img").foreach(_.remove())
    Option(document.getElementById("suggestion-container")).foreach(_.remove())

    val container = document.createElement("div")
    container.classList.add("end-container")

    val title = document.createElement("h1").asInstanceOf[Heading]
    title.textContent = titleText
    title.classList.add("title")
    container.append(title)

    val score = document.createElement("h1")
    score.textContent = s"${t("score")}: $finalScore"
    container.append(score)

    val shareButton = createButton(t("share"), t("share"))
    container.append(shareButton)
    val shareText = s"MovieQuiz.io #$gameDayIndex ${t("score").toLowerCase}: $finalScore"
    shareButton.addEventListener(
      "click",
      (_: Event) =>
        navigator.clipboard.writeText(shareText)
        shareButton.querySelector(".front").textContent = t("copied")
        setTimeout(5000) {
          shareButton.querySelector(".front").textContent = t("share")
        }
    )

    val text = document.createElement("h2").asInstanceOf[Heading]
    text.textContent = t("come_back_tomorrow_for_another_challenge")
    container.append(text)

    document.body.appendChild(container)

  def renderVictoryScreen(finalScore: Int, gameDayIndex: Int): Unit =
    document.querySelectorAll(".img-container:not(.active)").foreach(_.remove())
    document.getElementsByTagName("img").foreach(_.remove())
    renderEndScreen(t("you_won"), finalScore, gameDayIndex)

  def renderFailScreen(finalScore: Int, gameDayIndex: Int): Unit =
    document.querySelectorAll(".img-container:not(.active)").foreach(_.remove())
    document.getElementsByTagName("img").foreach(_.remove())
    renderEndScreen(t("game_over"), finalScore, gameDayIndex)

  private def isMobile =
    window.innerWidth < 768
