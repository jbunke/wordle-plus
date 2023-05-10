# ![logo](logo.png)

## About
Wordle+ is my own implementation of the massively popular word game [Wordle](https://www.nytimes.com/games/wordle/index.html), originally created by Josh Wardle and owned and maintained by the New York Times.
My game extends the original 5-letter, 6-guess format of the game to 4-7 letters, and 3-10 guesses, depending on how long the words in play are.

## How It Works
W+ is mostly faithful to the gameplay of the original, but the goal words for `x` letters are chosen randomly from a sub-array of an exhaustive list of English words with `x` letters, sorted in approximate order of how frequently they occur in the entire of corpus of contemporary English-language literature.

Because the goal words are not manually selected for each day like in the original game, this means that <b>any</b> word above a certain frequnecy threshold is a valid word, including plural nouns like *TENTS* or conjugated verb forms like *WAKES*, which don't seem to occur as goal words in the original game.

(It can also be played as many times a day as you want.)

## Download
You can download the game on [Itch.io](https://flinkerflitzer.itch.io/wordle-plus).
You will need at least the Java 17 runtime installed to run the game directly.

## Solver
The codebase also includes a Wordle [solver command-line interface](https://github.com/jbunke/wordle-plus/blob/master/src/com/jordanbunke/wordleplus/WordleSearch.java).
The solver prompts the user for a word pattern, omitted letters, and letters to include in the solution, and produces the likeliest goal word candidates.

Here is an example:

Pattern: `s--r-`  
Omit: `a,d`  
Include: `o`  
Candidates: `short, story, sorry, score, store, sport, storm, shore, sworn, swore, scorn, snort, snore, shorn, spore, stork, sours, shorl, skort`
