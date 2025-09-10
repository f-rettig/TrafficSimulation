/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation
 * Class Main: Launches the JavaFX application, constructs the UI, 
 * sets up the simulation controller, and initializes UI components 
 * including the simulation pane, car status table, clock, and control buttons.
 * @author Felicia Rettig
 * Date: May 6, 2025
 * Java 22
 */

package application;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Font;


public class Main extends Application {
	private SimulationController simulationController;

	@Override
	public void start(Stage primaryStage) {

		try {

			BorderPane root = new BorderPane();

			// HBox spacer for layout padding
			HBox topSpacer1 = new HBox();
			HBox topSpacer2 = new HBox();
			HBox leftSpacer = new HBox();
			HBox bottomSpacer1 = new HBox();
			HBox bottomSpacer2 = new HBox();

			// Pref width for spacers
			topSpacer1.setPrefWidth(100);
			topSpacer2.setPrefWidth(300);
			leftSpacer.setPrefWidth(100);
			bottomSpacer1.setPrefWidth(300);
			bottomSpacer2.setPrefWidth(300);

			// Simulation Controller & Pane
			simulationController = new SimulationController();	
			Pane simulationPane = simulationController.getSimulationPane();
			HBox backgroundPane = simulationController.getBackgroundPane();
			backgroundPane.getChildren().add(simulationPane);

			// Center Scroll Pane for Simulation Pane
			ScrollPane scrollPane = new ScrollPane();
			scrollPane.setMaxHeight(285);
			scrollPane.setPrefWidth(1045);
			scrollPane.setContent(backgroundPane);
			root.setCenter(scrollPane);

			// Traffic Light Panel
			TrafficLightDisplay trafficLightPanel = new TrafficLightDisplay(simulationController);
			Thread trafficLightThread = new Thread(trafficLightPanel);
			trafficLightThread.setDaemon(true); // closes with app
			trafficLightThread.start();
			simulationController.setTrafficLightPanel(trafficLightPanel);

			// Top Panel with clock and traffic light display
			HBox topPanel = new HBox();
			topPanel.setPadding(new Insets(10, 10, 0, 10));
			topPanel.setAlignment(Pos.CENTER_LEFT);
			Label clockLabel = new Label();
			clockLabel.setFont(new Font("Courier New", 24));
			ClockDisplay clockDisplay = new ClockDisplay(time -> clockLabel.setText(time));
			topPanel.getChildren().addAll(clockLabel, topSpacer1, trafficLightPanel, topSpacer2);
			root.setTop(topPanel);

			// Bottom panel with car status display
			Thread clockThread = new Thread(clockDisplay);
			clockThread.setDaemon(true);
			clockThread.start();

			CarStatusDisplay carStatusPanel = new CarStatusDisplay(simulationController);
			carStatusPanel.setPadding(new Insets(10, 20, 10, 10));
			Thread statusThread = new Thread(carStatusPanel);
			statusThread.setDaemon(true);

			HBox bottomPanel = new HBox();
			bottomPanel.getChildren().addAll(bottomSpacer1, carStatusPanel, bottomSpacer2);
			bottomPanel.setAlignment(Pos.CENTER);
			bottomPanel.setPadding(new Insets(0, 0, 20, 0));
			HBox.setHgrow(bottomSpacer1, Priority.ALWAYS);
			HBox.setHgrow(bottomSpacer2, Priority.ALWAYS);
			HBox.setHgrow(carStatusPanel, Priority.ALWAYS);

			root.setBottom(bottomPanel);

			// Right side control panel
			VBox rightSidePanel = new VBox(10);
			rightSidePanel.setAlignment(Pos.CENTER_LEFT);
			rightSidePanel.setPadding(new Insets(10, 10, 10, 10));
			rightSidePanel.setFillWidth(true);

			// Start simulation
			Button start = new Button("Start Simulation");
			start.setPrefWidth(150);
			start.setOnAction(e -> {
				if (!simulationController.getIsInitiated()) {
					simulationController.startSimulation();
				}
			});

			// Pause & continue simulation
			Button playPauseButton = new Button("Play/Pause");
			playPauseButton.setPrefWidth(150);
			playPauseButton.setOnAction(e -> {
				if (simulationController.getIsRunning() == true) {
					simulationController.pauseSimulation();
					carStatusPanel.stop();

				} else {
					simulationController.continueSimulation();	
					carStatusPanel.start();
					
					// Create new thread and start carStatusDisplay again
					Thread newStatusThread = new Thread(carStatusPanel);
					newStatusThread.setDaemon(true);
					newStatusThread.start();
				}
			});

			// Reset Simulation
			Button resetButton = new Button("Reset Simulation");
			resetButton.setPrefWidth(150);
			resetButton.setOnAction(e -> {
				simulationController.resetSimulation();
			});

			// Add new road to simulation (must input road name)
			VBox newRoadUI = new VBox(5);
			newRoadUI.setAlignment(Pos.CENTER_LEFT);
			Label newRoadLabel = new Label("New Road Name");
			TextField roadNameTf = new TextField();
			Button newRoadButton = new Button("Add Road");
			newRoadButton.setPrefWidth(150);
			newRoadButton.setOnAction(e -> {
				if (roadNameTf.getText() != "") {
					simulationController.addRoad(roadNameTf.getText());
					simulationController.getRoads().get(0).adjustEastSpawnPoint(simulationController.getIntersectionCount());

					simulationController.getBackgroundPane().setPrefWidth(simulationPane.getWidth() + 260);
				}
			});

			newRoadUI.getChildren().addAll(newRoadLabel, roadNameTf, newRoadButton);

			// Add new car
			Button addCarButton = new Button("Add Car");
			addCarButton.setPrefWidth(150);
			addCarButton.setOnAction(e -> {
				simulationController.spawnRandomCar();
			});

			rightSidePanel.getChildren().addAll(start, playPauseButton, resetButton, newRoadUI, addCarButton);
			root.setRight(rightSidePanel);			

			statusThread.start();

			Scene scene = new Scene(root,1200,800);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
