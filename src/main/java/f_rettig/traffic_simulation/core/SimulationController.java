/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class SimulationController: Manages the core logic of the traffic simulation, including roads,
 * cars, intersections, and their visual representations. This class handles simulation state
 * (running, paused, reset), updates car movements and traffic light logic per frame, and dynamically 
 * spawns roads and cars. It also manages the visual layout for both the simulation area and traffic 
 * light display.
 * 
 * 1 unit = 100 meters = 2.5 pixels
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import application.Car.CarState;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SimulationController {
	private ArrayList<Road> roads;
	private ArrayList<Car> cars;
	private ArrayList<CarSprite> carSprites;
	private ArrayList<Intersection> intersections;
	private boolean isRunning;
	private boolean isInitiated;
	private int intersectionCount = 0;				// Intersection count for road spacing
	private Pane simulationPane;
	private HBox backgroundPane;
	private AnimationTimer animationTimer;
	private TrafficLightDisplay trafficLightPanel;


	public SimulationController() {
		this.roads = new ArrayList<>();
		this.cars = new ArrayList<>();
		this.carSprites = new ArrayList<>();
		this.intersections = new ArrayList<>();
		this.isRunning = false;
		this.simulationPane = new Pane();
		this.backgroundPane = new HBox();

		BackgroundFill backgroundFill = new BackgroundFill(
				Color.valueOf("#228B22"),
				new CornerRadii(0),
				new Insets(10));
		Background background = new Background(backgroundFill);
		backgroundPane.setBackground(background);
		backgroundPane.setMinWidth(1025);
		backgroundPane.setMinHeight(275);
		backgroundPane.setMaxHeight(275);	

		animationTimer = new AnimationTimer() {
			private long lastUpdate = 0;

			@Override
			public void handle(long now) {
				if (lastUpdate > 0) {

					// nanoseconds to seconds
					double deltaTime = (now - lastUpdate) / 1_000_000_000.0; 

					// Only update cars if simulation is running
					if (isRunning) {
						updateAllCars(deltaTime);
					}
				}

				lastUpdate = now;
			}
		};
	}


	public void startSimulation() {

		intersectionCount = 0;
		isRunning = true;
		isInitiated = true;

		// Set up initial roads and cars
		createMainRoad();
		addRoad("1st St.");
		addRoad("2nd St.");
		addRoad("3rd St.");
		spawnRandomCar();
		spawnRandomCar();
		spawnRandomCar();

		animationTimer.start();
	}


	public void pauseSimulation() {

		// Stop car updates
		isRunning = false;

		// Stop traffic light state updates
		for (Intersection intersection : intersections) {
			intersection.pauseLightLogic();
		}
	}


	public void continueSimulation() {

		// Resume car updates
		isRunning = true;

		// Resume traffic light state updates
		for (Intersection intersection : intersections) {
			intersection.resumeLightLogic();
		}
	}


	public void resetSimulation() {

		// stop simulation to avoid null pointers
		pauseSimulation();

		// Remove UI elements and travel area
		trafficLightPanel.clear();
		simulationPane.getChildren().clear();
		backgroundPane.setPrefWidth(1025);
		removeCars();

		// Clear array lists
		cars.clear();
		roads.clear();
		carSprites.clear();
		intersections.clear();

		// Set isInitiated to false so start simulation button will function
		isInitiated = false;

	}


	public void updateAllCars(double deltaTime) {

		// Find Main Road's right-side X boundary
		double maxX = roads.get(0).getSpawnPoints().get(1).getX();

		// Update traffic light states
		for (Intersection intersection : intersections) {
			intersection.update(deltaTime);
		}

		// Search for intersections during travel
		for (Car car : cars) {
			checkCarAgainstIntersections(car, deltaTime);

			// Check if car exceeds boundaries and re-spawn upon breach
			if (car.getPosition().getY() > 50 || car.getPosition().getY() < -50 ||
					car.getPosition().getX() < 0 || car.getPosition().getX() > maxX) {

				spawnCar(car);
			}
		}

		// Update CarSprite position on display
		for (CarSprite sprite : carSprites) {
			sprite.updatePosition();
		}
	}


	private void checkCarAgainstIntersections(Car car, double deltaTime) {

		// If traffic light has been detected, check traffic light status
		if (car.getCarState() != CarState.MOVING) {
			car.checkTrafficLight(car.getCurrentIntersection(), deltaTime);
			return;
		}

		for (Intersection intersection : intersections) {

			if (car.isApproaching(intersection)) {

				// Assign stopTarget and currentIntersection during approach
				if (car.getCurrentIntersection() == null || car.getCurrentIntersection() != intersection) {
					car.setStopTarget(intersection);
					car.setCurrentIntersection(intersection);										
				}	

				// Update CarState to APROACHING and continue checking light status
				car.setCarState(CarState.APPROACHING);
				car.checkTrafficLight(intersection, deltaTime);

				// Exit method since intersection was found
				return;
			}
		}

		// Continue moving if not approaching an intersection
		car.move(deltaTime);
		return;
	}


	public void createMainRoad() {
		Road mainRoad = new Road("Main Road", true, 0);
		roads.add(mainRoad);
		simulationPane.getChildren().add(mainRoad.getRoadRectangle());
		intersectionCount ++;
	}


	public void addRoad(String roadName) {
		Road road = new Road(roadName, false, intersectionCount);
		roads.add(road);

		// Create new intersection for new road crossing
		Intersection newIntersection = new Intersection(new Point(road.getSpawnPoints().get(0).getX(), 0), 
				roadName);
		intersections.add(newIntersection);
		simulationPane.getChildren().add(road.getRoadRectangle());
		intersectionCount ++;

		// Create traffic light graphic for new intersection
		if (trafficLightPanel != null) {
			trafficLightPanel.addIntersection(newIntersection);
		}

		// Ensure CarSprites are on top of roads visually
		for (CarSprite sprite : carSprites) {
			sprite.getRectangle().toFront();
		}	    
	}


	// Spawn method for re-spawning cars
	public void spawnCar(Car car) {

		// Randomly select a road
		Road randomRoad = roads.get(ThreadLocalRandom.current().nextInt(roads.size()));

		// Randomly select an end of the selected road to spawn the car
		Point spawnPoint = randomRoad.getSpawnPoints().get(ThreadLocalRandom.current().nextInt(2));

		car.setPosition(new Point(spawnPoint.getX(), spawnPoint.getY()));
		car.setSpeed(ThreadLocalRandom.current().nextDouble(55, 90) / 3.6);
		car.setCurrentRoadName(randomRoad.getRoadName());
		chooseDirection(car);

		// Begin movement for new car
		car.setCarState(CarState.MOVING);
	}


	public void spawnRandomCar() {
		Car car = new Car();
		spawnCar(car);

		CarSprite carSprite = new CarSprite(car);

		cars.add(car);
		carSprites.add(carSprite);

		simulationPane.getChildren().add(carSprite.getRectangle());
	}


	public void removeCars() {
		for (CarSprite sprite : carSprites) {
			simulationPane.getChildren().remove(sprite);
		}
	}


	// Method to determine car direction based on spawn point
	public void chooseDirection(Car car) {

		// Check if car is on Main Road
		if (car.getPosition().getY() == 0.0) {

			// Check at which end of the road the car is location
			if (car.getPosition().getX() < 50) {
				car.setCarDirection(Car.Direction.EAST);
			} else {
				car.setCarDirection(Car.Direction.WEST);
			}

		} else {
			if (car.getPosition().getY() > 0.0) {
				car.setCarDirection(Car.Direction.SOUTH);
			} else {
				car.setCarDirection(Car.Direction.NORTH);
			}
		}
	}

	// Collection of Setters

	public void setIsRunning(boolean running) {
		isRunning = running;
	}


	public void setTrafficLightPanel(TrafficLightDisplay newTrafficLightPanel) {
		trafficLightPanel = newTrafficLightPanel;
	}

	// Collection of Getters

	public ArrayList<Road> getRoads() {
		return roads;
	}


	public ArrayList<Car> getCars() {
		return cars;
	}


	public ArrayList<Intersection> getIntersections() {
		return intersections;
	}


	public int getIntersectionCount() {
		return intersectionCount;
	}


	public boolean getIsRunning() {
		return isRunning;
	}


	public boolean getIsInitiated() {
		return isInitiated;
	}


	public TrafficLightDisplay getTrafficLightPanel() {
		return trafficLightPanel;
	}


	public Pane getSimulationPane() {
		return simulationPane;
	}


	public HBox getBackgroundPane() {
		return backgroundPane;
	}
}
