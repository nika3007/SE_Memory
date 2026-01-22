[![Coverage Status](https://coveralls.io/repos/github/nika3007/SE_Memory/badge.svg?branch=main&delay=5)](https://coveralls.io/github/nika3007/SE_Memory?branch=main)

A Memory card game implemented in Scala 3 for a SE.
The game can be played via:

	•	TUI (Text-based UI)
	
	•	GUI (ScalaFX)

	•	or both simultaneously
	

Core Features

	•	Multiple levels
	
	•	Human player vs AI
	
	•	Undo / Redo 
	
	•	Hint system

Architecture

	•	Model: game logic, board, cards, levels
	
	•	View: TUI and GUI
	
	•	Controller: game flow and user interaction
	


Design Patterns Used

	•	MVC – clear separation of concerns
	
	•	Command – user actions with undo/redo
	
	•	Strategy – AI behaviors
	
	•	Factory Method – creation of components via APIs
	
	•	Builder – flexible construction of levels
	
	•	Memento – storing and restoring game state
	
	•	Dependency Injection (Guice) – central configuration of dependencies
	
  

Persistence
Game state persistence is implemented via a common interface,
allowing easy switching between XML and JSON storage.



▶ Running the Game


Locally: sbt run


With Docker (TUI): "docker build -t se-memory" & "docker run -it se-memory"



Testing

	•	Unit tests written with ScalaTest
	
	•	Core logic is testable independently of the UI
	

