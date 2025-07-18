JAVAFX_PATH = /full/path/to/javafx-sdk-21/lib
JFXMODS = javafx.controls

default:
	javac -d . --module-path $(JAVAFX_PATH) --add-modules $(JFXMODS) *.java	 **/*.java
	# On macOS JavaFX must start on the first (AppKit) thread; also fall back to software pipeline if GPU not detected
	java -XstartOnFirstThread -Dprism.order=sw --module-path $(JAVAFX_PATH) --add-modules $(JFXMODS) Main

clean:
	rm -f *.class
	rm -f **/*.class
	
	