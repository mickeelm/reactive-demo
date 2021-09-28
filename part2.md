# Project Reactor / BurgerTime

As an homage to one of my favourite games as a kid, [BurgerTime](https://en.wikipedia.org/wiki/BurgerTime), your task is
to implement a fast food restaurant simulation.

There aren't really any similarities to the game though, I just wanted to grab the name :)

#### Features

- Place an order (there's only one generic type)
- Simulate a payment scenario for the order (paid, insufficient funds, timeout)
- Prepare the order, with the status transitions new-preparing-ready
- Pick up the order
- Prune picked up orders from the database

#### Technical Notes

- The application is a Spring Boot application (Java)
- The database used is an in memory H2 (no persistence, so a restart of the application clears the db) along with the
  reactive R2DBC driver.
- Every operation has a corresponding endpoint in `OrderController`.

*Note:* There are a lot of things that aren't covered by this application, both in regard to logic and race conditions
etc. You'll have to take it with a grain of salt. The purpose is for you to practice and understand more
of [Project Reactor](https://projectreactor.io/).

## Your task

- Make the tests green!
- For clarity, you mustn't adjust the tests :)
- When they're all done, fire up the application by running `./gradlew bootRun` (or pressing play in your IDE...), and
  use your favourite HTTP client to fire requests at it!

## Getting started

- The code resides within the `burgertime` folder. Import to your favorite IDE
- Navigate to the tests. `OrderService`/`OrderServiceTest` is the biggest, perhaps you want to start with the smaller
  services.
- There is already some code present in order to make life easier when you are composing your streams, like methods for
  generating response messages etc.

## Can I see your solution?

- Sure. Check out the `solutions` branch. Note that there of course are several ways to attack the different problems. I'm happy to discuss!