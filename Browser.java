import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

public class Browser extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Hello, JavaFX!");
        Button button = new Button("Click me!");
        button.setOnAction(e -> label.setText("Button was clicked!"));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, button);
        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setTitle("Testing");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
