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
        Label headerLabel = new Label("Simulation Start!!!");
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
            StackPane centerAlign = new StackPane();
            Label firstCommand = new Label("Start the Simulation with your first command...");
            centerAlign.getChildren().add(firstCommand);
            everyCommand.getChildren().add(centerAlign);
        } else {
            for (int i = totalCommands.size() - 1; i >= 0; i--) {
                HBox thisCommand = new HBox();
                Label writer = new Label(totalCommands.get(i));
                Label spacing = new Label("-----------------------------------");
                thisCommand.getChildren().addAll(writer, spacing);
                everyCommand.getChildren().add(thisCommand);
            }
        }
        commandsArea.setContent(everyCommand);
        commandsArea.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

        fullArea.getChildren().addAll(topHeader, topArea, commandsArea);
        layout.setCenter(fullArea);
    }

    private class CommandHandler implements EventHandler<ActionEvent> {
        public void handle(ActionEvent event) {
            newCommand();
        }
    }

    private void newCommand() {
        Stage commandStage = new Stage();
        commandStage.setTitle("New Command");
        BorderPane completeArea = new BorderPane();
        VBox commandArea = new VBox();

        StackPane centerAlign = new StackPane();
        Label writer = new Label("Category");
        centerAlign.getChildren().add(writer);

        ComboBox<String> dropdown = new ComboBox<>();
        dropdown.setPrefWidth(400);
        dropdown.getItems().addAll("Route", "Location", "Vehicle", "Passenger", "Hazard", "Time");
        dropdown.setOnAction(e -> {
            String selection = dropdown.getValue();
            VBox actualCommands = new VBox();
            if (selection.equalsIgnoreCase("route")) {
                HBox createRoute = new HBox();
                Label createRouteWriting = new Label("create_route");
                TextField written = new TextField("ID");
                actualCommands.getChildren().add(written);
                completeArea.setCenter(actualCommands);
            }
                }
        );

        commandArea.getChildren().addAll(centerAlign, dropdown);

        completeArea.setTop(commandArea);

        Scene scene = new Scene(completeArea, 400, 200);
        commandStage.setScene(scene);
        commandStage.showAndWait();
    }
}