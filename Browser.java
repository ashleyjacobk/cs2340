import controllers.TravelController;
import entities.Location;
import entities.Route;
import entities.Vehicle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

public class Browser extends Application {
    private static TravelController controller;

    public static void setController(TravelController ctrl) {
        controller = ctrl;
    }

    // GUI elements
    private final Canvas canvas = new Canvas(800, 600);
    private final TextArea logArea = new TextArea();
    private final Label timeLabel = new Label("Time: 0");

    @Override
    public void start(Stage primaryStage) {
        if (controller == null) {
            controller = new TravelController();
        }

        primaryStage.setTitle("Mass Transit Simulation – GUI");
        BorderPane root = new BorderPane();

        // Top toolbar
        ToolBar toolbar = new ToolBar();
        Button addLocBtn = new Button("Add Location");
        Button addRouteBtn = new Button("Add Route");
        Button addVehicleBtn = new Button("Add Vehicle");
        Button advanceTimeBtn = new Button("Advance Time");
        toolbar.getItems().addAll(addLocBtn, addRouteBtn, addVehicleBtn, new Separator(), advanceTimeBtn, new Separator(), timeLabel);

        addLocBtn.setOnAction(e -> openAddLocationDialog());
        addRouteBtn.setOnAction(e -> openAddRouteDialog());
        addVehicleBtn.setOnAction(e -> openAddVehicleDialog());
        advanceTimeBtn.setOnAction(e -> {
            try {
                controller.advanceTime(1);
                updateTimeLabel();
                redrawCanvas();
            } catch (Exception ex) {
                log("ERROR: " + ex.getMessage());
            }
        });

        // Left lists
        TabPane leftTabs = new TabPane();
        ListView<String> locList = new ListView<>();
        ListView<String> routeList = new ListView<>();
        ListView<String> vehicleList = new ListView<>();
        leftTabs.getTabs().addAll(new Tab("Locations", locList), new Tab("Routes", routeList), new Tab("Vehicles", vehicleList));
        leftTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        leftTabs.setPrefWidth(200);

        // Center canvas holder
        VBox center = new VBox(canvas);
        VBox.setVgrow(canvas, Priority.ALWAYS);
        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Simple click adds a location for quick demo
                String id = "L" + (controller.getLocations().size() + 1);
                controller.createLocation(id, id, e.getX(), e.getY());
                log("INFO: Quick location added " + id);
                refreshLists(locList, routeList, vehicleList);
                redrawCanvas();
            }
        });

        // Bottom log
        logArea.setEditable(false);
        logArea.setPrefRowCount(4);
        VBox bottom = new VBox(new Label("Event Log:"), logArea);
        VBox.setVgrow(logArea, Priority.ALWAYS);

        // Layout
        root.setTop(toolbar);
        root.setLeft(leftTabs);
        root.setCenter(center);
        root.setBottom(bottom);
        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        refreshLists(locList, routeList, vehicleList);
        redrawCanvas();
    }

    private void updateTimeLabel() {
        timeLabel.setText("Time: " + controller.getTime());
    }

    private void log(String msg) {
        Platform.runLater(() -> {
            logArea.appendText(msg + "\n");
        });
    }

    private void refreshLists(ListView<String> locList, ListView<String> routeList, ListView<String> vehicleList) {
        locList.getItems().setAll(controller.getLocations().keySet());
        routeList.getItems().setAll(controller.getRoutes().keySet());
        vehicleList.getItems().setAll(controller.getVehicles().keySet());
    }

    private void redrawCanvas() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw locations
        gc.setFill(Color.CORNFLOWERBLUE);
        for (Location loc : controller.getLocations().values()) {
            gc.fillOval(loc.getX() - 5, loc.getY() - 5, 10, 10);
            gc.fillText(loc.getName(), loc.getX() + 6, loc.getY());
        }
        // Future: draw routes & vehicles
    }

    private void openAddLocationDialog() {
        Dialog<Location> dialog = new Dialog<>();
        dialog.setTitle("Create Location");
        Label nameLbl = new Label("Name:");
        TextField nameFld = new TextField();
        Label idLbl = new Label("ID:");
        TextField idFld = new TextField();
        Label xLbl = new Label("X:");
        TextField xFld = new TextField();
        Label yLbl = new Label("Y:");
        TextField yFld = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, nameLbl, nameFld);
        grid.addRow(1, idLbl, idFld);
        grid.addRow(2, xLbl, xFld);
        grid.addRow(3, yLbl, yFld);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    controller.createLocation(nameFld.getText(), idFld.getText(), Double.parseDouble(xFld.getText()), Double.parseDouble(yFld.getText()));
                    redrawCanvas();
                    updateTimeLabel();
                    return controller.getLocations().get(idFld.getText());
                } catch (Exception ex) {
                    log("ERROR: " + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void openAddRouteDialog() {
        Dialog<Route> dialog = new Dialog<>();
        dialog.setTitle("Create Route");
        Label idLbl = new Label("ID:");
        TextField idFld = new TextField();
        Label typeLbl = new Label("Vehicle Type (BUS/TRAM/U-BAHN/S-BAHN):");
        TextField typeFld = new TextField();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.addRow(0, idLbl, idFld);
        grid.addRow(1, typeLbl, typeFld);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    controller.createRoute(idFld.getText(), typeFld.getText());
                    return controller.getRoutes().get(idFld.getText());
                } catch (Exception ex) {
                    log("ERROR: " + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }

    private void openAddVehicleDialog() {
        Dialog<Vehicle> dialog = new Dialog<>();
        dialog.setTitle("Create Vehicle");
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField capacityFld = new TextField();
        TextField typeFld = new TextField();
        TextField idFld = new TextField();
        TextField routeFld = new TextField();
        TextField locFld = new TextField();
        TextField speedFld = new TextField();
        grid.addRow(0, new Label("Capacity:"), capacityFld);
        grid.addRow(1, new Label("Type (BUS/TRAM/U-BAHN/S-BAHN):"), typeFld);
        grid.addRow(2, new Label("ID:"), idFld);
        grid.addRow(3, new Label("Route ID:"), routeFld);
        grid.addRow(4, new Label("Current Location ID:"), locFld);
        grid.addRow(5, new Label("Speed (kph):"), speedFld);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    controller.createVehicle(Integer.parseInt(capacityFld.getText()), typeFld.getText(), idFld.getText(), routeFld.getText(), locFld.getText(), Double.parseDouble(speedFld.getText()));
                    redrawCanvas();
                    updateTimeLabel();
                    return controller.getVehicles().get(idFld.getText());
                } catch (Exception ex) {
                    log("ERROR: " + ex.getMessage());
                }
            }
            return null;
        });
        dialog.showAndWait();
    }
}