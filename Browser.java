import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Stack;

public class Browser extends Application {
    private TextField bar = new TextField("Enter Action");
    private BorderPane layout = new BorderPane();
    private ArrayList<String> totalCommands = new ArrayList<>();

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
        VBox fullArea = new VBox();

        StackPane topHeader = new StackPane();
        Label headerLabel = new Label("Start of the Simulation!");
        headerLabel.setPrefSize(100,30);
        topHeader.setStyle("-fx-background-color: #66CC33");
        topHeader.getChildren().addAll(headerLabel);

        StackPane topArea = new StackPane();
        topArea.setStyle("-fx-background-color: #66CC33");
        Button newCommand = new Button("New Command");

        newCommand.setOnAction(new CommandHandler());
        topArea.getChildren().add(newCommand);

        ScrollPane commandsArea = new ScrollPane();
        VBox everyCommand = new VBox();
        if (totalCommands.isEmpty()) {
            Label firstCommand = new Label("Start the Simulation with your first command...");
            everyCommand.getChildren().add(firstCommand);
        } else {
            for (int i = totalCommands.size() - 1; i >= 0; i--) {
                String commandToPrint = totalCommands.get(i);
                HBox thisCommand = new HBox();
                Label writer = new Label(commandToPrint);
                thisCommand.getChildren().add(writer);
            }
        }
    }

    private class CommandHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            newCommand();
        }
    }

    private void newCommand() {

    }
}
