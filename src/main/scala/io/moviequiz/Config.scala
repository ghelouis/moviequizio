package io.moviequiz

case class Config(
    cdn: String = "https://cdn.moviequiz.io",
    moviesPerGame: Int = 10,
    screenshotsPerMovie: Int = 3
)
