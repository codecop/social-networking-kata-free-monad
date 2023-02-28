# Social Networking Kata with Free Monad

Exploring Free Monads in Java on the Social Networking Kata. 

## Kata

[The Social Networking Kata](https://monospacedmonologues.com/2013/04/the-social-networking-kata/)

* users can post messages
* users can read messages
* users can follow users
* users can see walls including following

## Monad

### On Monad

The common theme is that the monad chains operations in some specific, useful way.

* [How Optional breaks the monad laws and why it matters](https://www.sitepoint.com/how-optional-breaks-the-monad-laws-and-why-it-matters/) (2016) ... `map` breaks associativity.

### Free Monad

We have talked about monads and interpreters. I said the free monad is just the combination of the two. Concretely this means the free monad provides:

* an AST to express monadic operations;
* an API to write interpreters that give meaning to this AST.

Articles and Presentations

* [Dead-Simple Dependency Injection](https://www.youtube.com/watch?v=ZasXwtTRkio) presentation by Runar Oli Bjarnason (2013)
* [Free Monad Are Simple](https://underscore.io/blog/posts/2015/04/14/free-monads-are-simple.html) blog post by Noel Welsh (2014)
* Gregor's blog post [Refactoring towards a transaction monad](https://gtrefs.github.io/code/refactoring-towards-a-transaction-monad/) (2017)
* [DSLs with the Free Monad in Java 8 part 1](https://medium.com/modernnerd-code/dsls-with-the-free-monad-in-java-8-part-i-701408e874f8) by John McClean, [part 2](https://medium.com/@johnmcclean/dsls-with-the-free-monad-in-java-8-part-ii-f0010f012ae1) (2017)
* [Free in Java](https://github.com/xuwei-k/free-monad-java/tree/master/src/main/java/free)
* [Free Monad in Scala](https://blog.rockthejvm.com/free-monad/) (2022), Scala sample in source.

## Retrospective

### 13.2.2023

* Gregor kommt durcheinander mit Java Generics.
* Peter versucht nur, es zu kompilieren.
* Java ist dafür nicht gemacht.
* Typen gleichsetzen geht nicht, muss immer extends, kann keine Functors machen.
* Spassig war's.
* Peter tippt ganze Zeit, OK.
* Wieder reinkommen und mal etwas anderes sehen, andere Sprachen bieten andere Lösungen.
* Energie in etwas investiert, was vielleicht wo anders leichter geht?

### 28.2.2023
* Gregor hat etwas gelernt durch den Scala Artikel, interessant.
* Peter sieht, dass die Java Lösung besser sein könnte.
