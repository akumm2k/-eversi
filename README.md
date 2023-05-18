# Яeversi

This project has the following implementations:

- [CLI reversi](#reversi-cli) 
- [Multiplayer Reversi Web Application](#reversi-web-app)

## Reversi CLI 

A Java implementation of the game [Reversi](https://en.wikipedia.org/wiki/Reversi) with an AI agent
using minimax with alpha-beta pruning.

### Implemented Minimax Strategies

- Counting corners
- Counting disks
- End to End stable occupation

  The agent prefers scenarios where it has captured entire rows and columns, which obviously can't 
  be recaptured by the opponent.

### Example Run

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

### Running instructions

- run `Main::main` from Intellij.
- Use the following commands:
  ```shell
  mvn exec:java@run-cli
  ```
    ```
  ...                                                                                                                               
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

### Documentation

* Use IntelliJ directly or `mvn javadoc:javadoc` on the command line to generate the javadoc.

#### Program Architecture

- The game is implemented using a Model-View-Controller architecture, such that the user only interacts with the controller.
- The view and controller are singletons.

#### Potential extensions

- A GUI - desktop / web
- More minimax strategies for the heuristics evaluation
- Add difficulty levels and modify the minimax heuristics to conform to the selected difficulty level
- Server serving multiple games to users - plus a web application


## Reversi Web App

A Java multiplayer implementation of the game [Reversi](https://en.wikipedia.org/wiki/Reversi).


## Usage

```shell
mvn exec:java@run-webapp
```
```
[INFO] 
[INFO] ------------------------< org.reversi:Reversi >-------------------------
[INFO] Building Reversi 1.0-SNAPSHOT
[INFO]   from pom.xml
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- exec:3.0.0:java (run-webapp) @ Reversi ---

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.0.6)

2023-05-18T16:08:24.920+02:00  INFO 73095 --- [lication.main()] org.reversi.web.ReversiApplication       : Starting ReversiApplication using Java 20.0.1
```

The webpage is the available at `localhost:8080`.

### Documentation

* Use IntelliJ directly or `mvn javadoc:javadoc` on the command line to generate the javadoc.

#### Program Architecture

- The web app uses the [Spring Boot framework](https://spring.io/projects/spring-boot) to implement its backend.
- The frontend is implemented in Vanilla JS, with CSS3 and [Thymeleaf template engine](https://www.thymeleaf.org).
- Multiplayer communication is implemented via WebSockets.
