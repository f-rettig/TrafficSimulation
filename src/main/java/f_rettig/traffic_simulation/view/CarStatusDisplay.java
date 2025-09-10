/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class CarStatusDisplay: Displays real-time status of all cars in the simulation 
 * using a JavaFX TableView.
 * 
 * Updates include:
 * - License Plate
 * - Speed (km/h)
 * - Position
 * - Car State
 * - Direction
 * - Current Road
 * 
 * This class runs in a separate thread to periodically refresh the display without 
 * blocking the main JavaFX Application Thread.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;


public class CarStatusDisplay extends VBox implements Runnable {

	private TableView<Car> carStatusTable;
	private ObservableList<Car> carData;
	private SimulationController simulationController;
	private volatile boolean isRunning = true;

	public CarStatusDisplay(SimulationController simulationController) {
		this.simulationController = simulationController;
		carData = FXCollections.observableArrayList();
		setupTable();
	}


	private void setupTable() {
		carStatusTable = new TableView();

		TableColumn<Car, String> licenseCol = new TableColumn<>("License Plate");
		licenseCol.setCellValueFactory(new PropertyValueFactory<>("licensePlate"));

		TableColumn<Car, Double> speedCol = new TableColumn<>("Speed (km/h)");
		speedCol.setCellValueFactory(new PropertyValueFactory<>("speed"));

		TableColumn<Car, String> positionCol = new TableColumn<>("Position");
		positionCol.setCellValueFactory(new PropertyValueFactory<>("positionString"));

		TableColumn<Car, String> stateCol = new TableColumn<>("State");
		stateCol.setCellValueFactory(new PropertyValueFactory<>("carState"));

		TableColumn<Car, String> directionCol = new TableColumn<>("Direction");
		directionCol.setCellValueFactory(new PropertyValueFactory<>("carDirectionString"));

		TableColumn<Car, String> roadCol = new TableColumn<>("Current Road");
		roadCol.setCellValueFactory(new PropertyValueFactory<>("currentRoadName"));

		carStatusTable.getColumns().addAll(licenseCol, speedCol, positionCol, stateCol, directionCol, roadCol);
		this.getChildren().add(carStatusTable);
	}


	@Override
	public void run() {

		while (isRunning) {

			try {
				Thread.sleep(500);

				// Run update on JavaFX Application Thread
				Platform.runLater(() -> {
					carData.setAll(simulationController.getCars());
					carStatusTable.setItems(carData);
				});

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}


	public void stop() {
		isRunning = false;
	}


	public void start() {
		isRunning = true;
	}


	public void setIsRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
