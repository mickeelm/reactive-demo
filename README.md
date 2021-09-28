# Reactive-demo

Reactive demo CTS 2021

## Part 1 - Non-blocking I/O

This part demonstrates calling a server with poor response times (ca 2s latency).

- Single thread blocking, three requests
- Multithread blocking - 20 requests/20 threads thread pool
- Multithread blocking - 20 requests/5 threads thread pool
- Single thread (forced) reactive - 200 requests

See README [here](part1.md).

## Part 2 - Project Reactor / BurgerTime

This part contains of a fast food ordering mechanism simulator where the Reactor parts have been removed, except from
the StepVerifier-tests. Your task is to make the application work (make the tests green by adding the missing reactive
streams).

See README [here](part2.md).