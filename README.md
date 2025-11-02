# MovieQuiz.io

Daily movie guessing challenge.

Built with [Scala.js](https://www.scala-js.org/)

## Local development

### Compile Scala code to JavaScript
- Run this command in `sbt`:

```
> ~fastLinkJS
```

### Play the game
- Open [index.html](./index.html) in your favourite browser

### Linting
- [Prettier](https://prettier.io/) for HTML/CSS
- [Scalafmt](https://scalameta.org/scalafmt/) for Scala.

Can be checked automatically by installing [pre-commit](https://pre-commit.com/#install) and copying [scripts/pre-commit](./scripts/pre-commit) to `.git/hooks/pre-commit`.