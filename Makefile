JAVAFX_PATH = lib/javafx21/lib
JFXMODS = javafx.controls

default:
	javac -d . --module-path $(JAVAFX_PATH) --add-modules $(JFXMODS) *.java	 **/*.java
	java --module-path $(JAVAFX_PATH) --add-modules $(JFXMODS) Main

clean:
	rm -f *.class
	rm -f **/*.class
	
	