import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class Browser extends Application {
    private TextField bar = new TextField("Enter Action");
    private BorderPane layout = new BorderPane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Phase 4 GUI");
        VBox topBar = new VBox();
        HBox imageBar = new HBox();
        imageBar.setStyle("-fx-background-color: #FFFFFF");
        Image image = new Image("logo.jpg");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(70);
        Label imageLabel = new Label("Verkehrsverbund Berlin-Brandenburg Page");
        imageLabel.setPrefSize(250,30);
        imageBar.getChildren().addAll(imageView, imageLabel);

        HBox urlTop = new HBox();
        bar.setPrefWidth(550);
        Button go = new Button("Go");
        go.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent e) {
                urlChecker();
            }
        }
        );
        go.setPrefWidth(50);

        urlTop.getChildren().addAll(bar, go);
        topBar.getChildren().addAll(imageBar, urlTop);

        StackPane centralArea = new StackPane();
        Label centralLabel = new Label("Welcome! Enter simulation.com to get started...");
        centralArea.getChildren().addAll(centralLabel);
        layout.setTop(topBar);
        layout.setCenter(centralArea);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void urlChecker() {
        String urlText = bar.getText();
        if (urlText.equalsIgnoreCase("simulation.com")) {
            FadeTransition fade = new FadeTransition(Duration.seconds(1), layout);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            simulationPage();
        } else {
            Alert invalidAlert = new Alert(Alert.AlertType.ERROR);
            invalidAlert.setTitle("Invalid Alert");
            invalidAlert.setContentText("This URL does not exist. Please try again...");
            invalidAlert.showAndWait();
        }
    }

    private void simulationPage() {

    }
}
