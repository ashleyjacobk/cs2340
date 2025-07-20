
SRC=$(shell find . -name "*.java")

.PHONY: default clean gui

default: cli

cli:
	@echo "Compiling Java simulation..."
	javac $(SRC)
	@echo "Running CLI simulation (type 'help' for commands)"
	java Main

clean:
	find . -name "*.class" -delete

# Existing gui target (React frontend)
gui:
	cd frontend && npm install && npm run dev
	
	