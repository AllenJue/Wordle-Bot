# Wordle-Bot
## Introduction
Wordle-Bot is an interactive, user-friendly Wordle solver that takes in feedback from a user's Wordle results to give suggestions for finding a solution.

## Features
Wordle-Bot has a 6x5 grid, which resembles a Wordle Board. Upon clicking a square, it will toggle between grey, yellow, and green, which correspond with [Wordle's](https://www.nytimes.com/games/wordle/index.html) features of invalid letters, misplaced letters, and correct letters. Typing letters will populate the empty squares for the current row, and pressing enter will attempt the guess. The Wordle-Bot will then process the given information and attempt to narrow down possible answers and provide a calculated guess.

Here's an example of the Wordle-Bot in action, where the solution is the word *QUILL*
![Wordle-Bot gif](https://user-images.githubusercontent.com/66751933/179063820-62961906-1d3e-4c1b-b89f-4d05985a15b3.gif)

## Technologies
* Java 8 (```java version "1.8.0_301"
Java(TM) SE Runtime Environment (build 1.8.0_301-b09)
Java HotSpot(TM) 64-Bit Server VM (build 25.301-b09, mixed mode)``` preferred)
* Java Swing
* Java FX

## Setup and Launch
Download the code as a zip and navigate to the bin directory. From there run:

```java Gui``` 

to launch the chess game.
