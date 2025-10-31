# MovieQuiz.io

How well can you guess a movie from a single screenshot?

Built with [Scala.js](https://www.scala-js.org/)

## Local development

### Compile Scala code to JavaScript
- Run this command in `sbt`:

```
> ~fastLinkJS
```

### Play the game
- Open [index.html](./index.html) in your favourite browser

## Contributing
### Coding style
Make sure files are linted properly. This is done via [Prettier](https://prettier.io/) for HTML/CSS and [Scalafmt](https://scalameta.org/scalafmt/) for Scala. This can be checked automatically when you commit by installing [pre-commit](https://pre-commit.com/#install) and copying [scripts/pre-commit](./scripts/pre-commit) to `.git/hooks/pre-commit`. For a smooth experience, set up your editor of choice with each tool to format code automatically as you write it.

### Git
Please write commit messages in [imperative mood](https://cbea.ms/git-commit/#imperative).