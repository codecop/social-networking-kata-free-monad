# Social Networking Kata with Free Monad

## Kata

[The Social Networking Kata](https://monospacedmonologues.com/2013/04/the-social-networking-kata/)

* users can post messages
* users can read messages
* users can follow users
* users can see walls including following


## Free Monad

### Question: What is free Monad?

* [Gregors blog post "Refactoring towards a transaction monad"](https://gtrefs.github.io/code/refactoring-towards-a-transaction-monad/)
* [How Optional breaks the monad laws and why it matters](https://www.sitepoint.com/how-optional-breaks-the-monad-laws-and-why-it-matters/)
* ["Dead-Simple Dependency Injection" presentation by Rúnar Óli Bjarnason](https://www.youtube.com/watch?v=ZasXwtTRkio)
* ["Free Moands Are Simple" blog post by Noel Welsh ](https://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html)

### What is a monad? (copied from StackOverflow)

The common theme is that the monad chains operations in some specific, useful way.

### What is a free monad? (copied from the blog "Free Moands Are Simple")

We have talked about monads and interpreters. I said the free monad is just the combination of the two. Concretely this means the free monad provides:

* an AST to express monadic operations;
* an API to write interpreters that give meaning to this AST.
