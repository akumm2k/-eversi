# Яeversi

A Java implementation of the game [Reversi](https://en.wikipedia.org/wiki/Reversi) with an AI agent
using minimax with alpha-beta pruning.

## Implemented Minimax Strategies

- Counting corners
- Counting disks
- End to End stable occupation

  The agent prefers scenarios where it has captured entire rows and columns, which obviously can't 
  be recaptured by the opponent.

## Example Run

```
Welcome to Яeversi
Enter q to exit

  0 1 2 3 4 5 6 7 
0 _ _ _ _ _ _ _ _ 
1 _ _ _ _ _ _ _ _ 
2 _ _ _ _ * _ _ _ 
3 _ _ _ o x * _ _ 
4 _ _ * x o _ _ _ 
5 _ _ _ * _ _ _ _ 
6 _ _ _ _ _ _ _ _ 
7 _ _ _ _ _ _ _ _ 
Turn: o
2 4

  0 1 2 3 4 5 6 7 
0 _ _ _ _ _ _ _ _ 
1 _ _ _ _ _ _ _ _ 
2 _ _ _ * o * _ _ 
3 _ _ _ o o _ _ _ 
4 _ _ _ x o * _ _ 
5 _ _ _ _ _ _ _ _ 
6 _ _ _ _ _ _ _ _ 
7 _ _ _ _ _ _ _ _ 
Turn: x
Agent: 2 5

  0 1 2 3 4 5 6 7 
0 _ _ _ _ _ _ _ _ 
1 _ _ _ _ _ _ _ _ 
2 _ _ _ _ o x * _ 
3 _ _ _ o x * _ _ 
4 _ _ * x o _ _ _ 
5 _ _ _ * _ _ _ _ 
6 _ _ _ _ _ _ _ _ 
7 _ _ _ _ _ _ _ _ 
Turn: o
q
```

## Running instructions

- run `Main::main` from Intellij.
- Use the following commands:
    ```
  > javac src/main/java/org/reversi/**/*.java -d target/classes                                                                                                                                main
  > java -classpath ./target/classes org.reversi.Main   
  Welcome to Яeversi
  Enter q to exit
  
      0 1 2 3 4 5 6 7
  0 _ _ _ _ _ _ _ _
  1 _ _ _ _ _ _ _ _
  2 _ _ _ _ * _ _ _
  3 _ _ _ o x * _ _
  4 _ _ * x o _ _ _
  5 _ _ _ * _ _ _ _
  6 _ _ _ _ _ _ _ _
  7 _ _ _ _ _ _ _ _
  Turn: o
  q
    ```

## Documentation

* Use IntelliJ directly or `mvn javadoc:javadoc` on the command line to generate the javadoc.

### Program Architecture

- The game is implemented using a Model-View-Controller architecture, such that the user only interacts with the controller.
- The view and controller are singletons.

### Potential extensions

- A GUI - desktop / web
- More minimax strategies for the heuristics evaluation
- Server serving multiple games to users - plus a web application