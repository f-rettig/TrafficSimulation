/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class Car: Represents a single car in the simulation, including its properties 
 * (license plate, color, position, speed, direction) and behaviors. The Car class 
 * manages its own movement, interaction with intersections and traffic lights, 
 * decision-making for turns, and state transitions (moving, braking, stopping, turning).
 * 
 * Car objects update their position each frame based on their current state and 
 * respond to traffic light signals and intersection logic.
 * 
 * 1 unit = 100 meters = 2.5 pixels
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.paint.Color;

public class Car {
	private String licensePlate;
	private Color color;
	private Point position;
	private double speed;
	private double targetSpeed;
	private Direction direction;
	private NextDirection nextDirection = null;
	private double turnProgress = 0.0;
	private String currentRoadName;
	private TrafficLight currentTrafficLight;
	private Intersection currentIntersection;
	private CarState carState;
	private Point stopTarget = null;
	private Random random = new Random();
	private boolean directionChosen;

	// waitStartTime meanings:
	// -1 = not waiting
	//  0 = request sent, waiting for light
	// >0 = timestamp of wait start
	private long  waitStartTime = -1;


	enum CarState {
		MOVING, APPROACHING, REASONING, BRAKING, STOPPED, TURNING, CONTINUING
	}


	enum Direction {
		NORTH, SOUTH, EAST, WEST,
	}


	enum NextDirection {
		LEFT, RIGHT, STRAIGHT
	}


	public Car() {
		this.licensePlate = generateLicensePlate();
		this.color = Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());

		// Randomly assign speed between 55 km/h and 90 km/h, convert to meters per second (divide by 3.6)
		this.speed = ThreadLocalRandom.current().nextDouble(55, 90) / 3.6;

		// Set CarState to MOVING for all new cars
		this.carState = CarState.MOVING;
	}


	// Initial movement logic deciding when to turn or move straight
	public void move(double deltaTime) {

		if (currentIntersection != null) {

			// Reset car state after exiting intersection vicinity 
			if (hasClearedIntersection()) {

				turnProgress = 0;
				carState = CarState.MOVING;

				currentIntersection = null;
				currentTrafficLight = null;
				directionChosen = false;
				nextDirection = null;

				moveStraight(deltaTime);
				return;
			}
		}

		// From CarState decide movement type or check traffic light
		switch (carState) {
			case STOPPED: 
				checkTrafficLight(currentIntersection, deltaTime);
				break;
			case TURNING:
				moveTurn(deltaTime);
				break; 
			case BRAKING:
			case MOVING:	
			case CONTINUING:
				moveStraight(deltaTime);
		}
	}


	private void moveStraight(double deltaTime) {

		if (carState == Car.CarState.BRAKING) {

			// Gradually slow down
			speed -= 5.0 * deltaTime;

			// Set car to Stopped if speed is <= 0 or car is at stop point
			if (speed <= 0 || (stopTarget != null && isAtStopPoint())) {
				speed = 0;
				carState = CarState.STOPPED;
			}

		} else if (carState == Car.CarState.STOPPED) {
			// No movement — movement handled in checkTrafficLight when light allows
			speed = 0;

		} else {

			// if speed < target speed, accelerate car speed until target speed reached
			if (speed < targetSpeed) {
				speed += 2.5 * deltaTime;

				if (speed >= targetSpeed) {
					speed = targetSpeed;
				}
			}
		}


		// Calculate movement and new position
		double distance = speed * deltaTime;
		double unitDistance = distance / 10;

		switch (direction) {
			case EAST:
				position.setX(position.getX() + unitDistance);
				break;
			case WEST:
				position.setX(position.getX() - unitDistance);
				break;
			case NORTH:
				position.setY(position.getY() + unitDistance);
				break;
			case SOUTH:
				position.setY(position.getY() - unitDistance);
				break;
		}
	}


	private void moveTurn(double deltaTime) {
		double distance = speed * deltaTime;
		double unitDistance = distance / 10;
		double turnLength = getTurnLength() + 5;

		// Stage 1: move straight into the intersection.
		moveStraight(deltaTime);
		turnProgress += unitDistance;

		// Stage 2: change direction and continue
		if (turnProgress >= turnLength && isInIntersection()) {

			// Apply direction change
			switch (nextDirection) {
				case LEFT:
					turnLeft();
					carState = CarState.CONTINUING;
					break;
				case RIGHT:
					turnRight();
					carState = CarState.CONTINUING;
					break;

					// Shouldn't happen, but just in case.
					default:
						carState = CarState.CONTINUING;
						break;
			}


			// Update current road
			if (direction == Direction.EAST || direction == Direction.WEST) {
				setCurrentRoadName("Main Road");

			} else if (currentIntersection != null) {
				setCurrentRoadName(currentIntersection.getStreetName());
			}
		}
	}

	// Method to set new direction after left turn
	private void turnLeft() {

		switch (direction) {
			case NORTH: direction = Direction.WEST; break;
			case SOUTH: direction = Direction.EAST; break;
			case EAST: direction = Direction.NORTH; break;
			case WEST: direction = Direction.SOUTH; break;
		}
	}

	// Method to set new direction after right turn
	private void turnRight() {

		switch (direction) {
			case NORTH: direction = Direction.EAST; break;
			case SOUTH: direction = Direction.WEST; break;
			case EAST: direction = Direction.SOUTH; break;
			case WEST: direction = Direction.NORTH; break;
		}
	}


	public boolean isApproaching(Intersection intersection) {

		// Car must be in MOVING state without an assigned intersection to be approaching
		if (carState != CarState.MOVING || currentIntersection != null) {
			return false;
		}

		// Check if car is within 10 units
		double distanceX = Math.abs(position.getX() - intersection.getPosition().getX());
		double distanceY = Math.abs(position.getY() - intersection.getPosition().getY());

		return distanceX <= 10.0 && distanceY < 10.0; 
	}


	public void checkTrafficLight(Intersection intersection, double deltaTime) {

		// Check correct traffic light pair according to direction traveled
		currentTrafficLight = (direction == Direction.EAST || direction == Direction.WEST)
				? intersection.getEWLight()
						: intersection.getNSLight();

		Point stop = intersection.getPosition();
		boolean isPastStopLine = isPastStopLine(stop);

		switch (carState) {

			// APPROACHING -> REASONING -> MOVEMENT LOGIC
			case APPROACHING:

				nextDirection = NextDirection.values()[ThreadLocalRandom.current().nextInt(0, 3)];
				carState = CarState.REASONING;
				checkTrafficLight(currentIntersection, deltaTime);
				break;


			case REASONING:

				switch (nextDirection) {


					case STRAIGHT:
						if (currentTrafficLight.isStraightGreen() || (isPastStopLine)) {
							carState = CarState.CONTINUING;
							move(deltaTime);

						} else if (!isPastStopLine) {
							carState = CarState.BRAKING;
							move(deltaTime);
						}

						break;


					case LEFT:
						if (currentTrafficLight.isTurnGreen()) {
							carState = CarState.TURNING;
							move(deltaTime);

						} else  {
							carState = CarState.BRAKING;
							move(deltaTime);
						}

						break;


					case RIGHT:
						// Allow right turn on RED
						carState = CarState.TURNING;
						move(deltaTime);

						break;
				}
				break;


			case BRAKING:

				if (nextDirection == NextDirection.STRAIGHT && currentTrafficLight.isStraightGreen()) {
					carState = CarState.CONTINUING;
					move(deltaTime);

				} else if (nextDirection == NextDirection.LEFT && currentTrafficLight.isTurnGreen()) {
					carState = CarState.TURNING;
					move(deltaTime);

				} else if (nextDirection == NextDirection.RIGHT) {
					carState = CarState.TURNING;
					move(deltaTime);

					// If car cannot continue through light, continue to slow until stopped
				} else {
					move(deltaTime);
				}

				break;


				// Wait for green light
			case STOPPED:

				switch (nextDirection) {

					case STRAIGHT:
						if (currentTrafficLight.isStraightGreen()) {
							carState = CarState.CONTINUING;
							move(deltaTime);
						}

						break;


					case LEFT:
						if (currentTrafficLight.isTurnGreen()) {
							carState = CarState.TURNING;
							currentTrafficLight.clearWaitingRequest();
							waitStartTime = -1; // not waiting
							move(deltaTime);

							// Repeatedly send turn request every 30 seconds until light is green
						} else {
							if (!currentTrafficLight.hasWaitingRequest()) {
								currentTrafficLight.requestTurnGreen();
								waitStartTime = 0; // Request sent
							}

							// If not waiting, but light has wait request, log wait start time
							if (waitStartTime == -1) {
								waitStartTime = System.currentTimeMillis();

								// if waiting, check time elapsed and send request if elapsed > 30 seconds
							} else if (waitStartTime != -1) {
								long elapsed = System.currentTimeMillis() - waitStartTime;
								if (elapsed >= 30000) {
									currentTrafficLight.requestTurnGreen();
									waitStartTime = -1; // back to not waiting
								}
							}

							break;
						}

						break;


					case RIGHT:
						// Allow right turn on RED
						carState = CarState.TURNING;
						move(deltaTime);

						break;
				}

				break;


			case MOVING:
				// Handled by SimulationController when checking for approaching
				break;

				// Follow logic to complete turn or continue through light	
			case TURNING:				
			case CONTINUING:
				move(deltaTime);
				break;
		}
	}


	private boolean isInIntersection() {
		double dx = Math.abs(position.getX() - currentIntersection.getPosition().getX());
		double dy = Math.abs(position.getY() - currentIntersection.getPosition().getY());
		return dx < 0.1 && dy < 0.1;
	}


	private boolean isAtStopPoint() {
		double dx = Math.abs(position.getX() - stopTarget.getX());
		double dy = Math.abs(position.getY() - stopTarget.getY());
		return dx < 0.1 && dy < 0.1;
	}


	private boolean isPastStopLine(Point stopPoint) {

		switch (direction) {
			case EAST:
				return position.getX() > stopPoint.getX();
			case WEST:
				return position.getX() < stopPoint.getX();
			case NORTH:
				return position.getY() > stopPoint.getY();
			case SOUTH:
				return position.getY() < stopPoint.getY();
			default:
				return false;
		}
	}


	private String generateLicensePlate() {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		StringBuilder plates = new StringBuilder();
		Random random = new Random();

		for (int i = 0; i < 7; i++) {
			plates.append(chars.charAt(random.nextInt(chars.length())));
		}

		return plates.toString();
	}


	public boolean hasClearedIntersection() {
		if (currentIntersection == null) { return false; }

		if (carState != CarState.CONTINUING) { return false; }

		// Buffer amount before state is reset
		double buffer = 10.5; 

		boolean movedPastX = direction == Direction.EAST && position.getX() > currentIntersection.getPosition().getX() + buffer
				|| direction == Direction.WEST && position.getX() < currentIntersection.getPosition().getX() - buffer;

		boolean movedPastY = direction == Direction.NORTH && position.getY() > currentIntersection.getPosition().getY() + buffer
				|| direction == Direction.SOUTH && position.getY() < currentIntersection.getPosition().getY() - buffer;

		return movedPastX || movedPastY;
	}

	// Collection of Setters

	public void setPosition(Point position) {
		this.position = position;
	}


	public void setSpeed(double newSpeed) {
		speed = newSpeed;
		targetSpeed = newSpeed;
	}


	public void setCarDirection(Direction newDirection) {
		direction = newDirection;
	}


	public void setCurrentIntersection(Intersection newIntersection) {
		currentIntersection = newIntersection;
	}


	public void setCurrentRoadName(String roadName) {
		this.currentRoadName = roadName;
	}


	public void setCarState(CarState newCarState) {
		carState = newCarState;
	}


	public void setStopTarget(Intersection intersection) {

		// Stop about 5 units before the intersection
		double buffer = 5.0;

		switch (direction) {
			case EAST:
				stopTarget = new Point(intersection.getPosition().getX() - buffer, position.getY());
				break;
			case WEST:
				stopTarget = new Point(intersection.getPosition().getX() + buffer, position.getY());
				break;
			case NORTH:
				stopTarget = new Point(position.getX(), intersection.getPosition().getY() - buffer);
				break;
			case SOUTH:
				stopTarget = new Point(position.getX(), intersection.getPosition().getY() + buffer);
				break;
		}
	}

	// Collection of Getters

	public String getLicensePlate() {
		return licensePlate;
	}


	public Color getColor() {
		return color;
	}


	public Point getPosition() {
		return position;
	}


	// Position getter as string for TableView
	public String getPositionString() {
		return position.toString();
	}


	// Speed at km/h
	public double getSpeed() {

		// Convert speed from units/sec back to km/h for display:
		// speed (units/sec) × 10 (to meters/sec) × 3.6 = km/h
		// Simplified: speed × 36
		// Dividing by 100.0 to round to 2 decimal places.
		return (int)(speed * 360) / 100.0;
	}


	public Direction getCarDirection() {
		return direction;
	}


	// Direction getter as string for TableView
	public String getCarDirectionString() {
		return direction.toString();
	}


	// Calculate distance until turn is required
	private double getTurnLength() {
		switch (direction) {
			case NORTH:
			case SOUTH:

				// Distance from Y = 0
				return Math.abs(position.getY());
			case EAST:
			case WEST:

				// Remainder of distance to next X divisible by 100
				return Math.abs(position.getX()) % 100;

			default:
				throw new IllegalStateException("Unknown direction: " + direction);
		}
	}

	public String getCurrentRoadName() {
		return currentRoadName;
	}


	public TrafficLight getCurrentTrafficLight() {
		return currentTrafficLight;
	}


	public Intersection getCurrentIntersection() {
		return currentIntersection;
	}


	public CarState getCarState() {
		return carState;
	}


	// CarState getter as string for TableView
	public String getCarStateString() {
		return carState.toString();
	}
}

