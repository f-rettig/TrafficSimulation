/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class Intersection: Manages the behavior and state of a single traffic intersection.
 * Controls traffic light phases, handles requests for turn signals, and updates light 
 * timing to regulate vehicle flow. Supports pausing and resuming the traffic logic.
 * 
 * Each intersection has:
 * - Two TrafficLights (North/South and East/West).
 * - A phase cycle controlling straight and turn signals.
 * - Logic to respond to waiting turn requests from cars.
 * 
 * 1 unit = 100 meters = 2.5 pixels
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

public class Intersection {

	// Basic intersection phase for flow of traffic
	public enum IntersectionPhase {
		NS_TURN, NS_STRAIGHT, EW_TURN, EW_STRAIGHT
	}

	private TrafficLight nsLight;
	private TrafficLight ewLight;
	private IntersectionPhase currentPhase;
	private double phaseTimer;
	private Point position;
	private String streetName;
	private boolean lightLogicRunning = true;


	public Intersection (Point position, String streetName) {
		this.position = position;
		this.streetName = streetName;
		this.nsLight = new TrafficLight();
		this.ewLight = new TrafficLight();
		this.currentPhase = IntersectionPhase.EW_STRAIGHT;
		this.phaseTimer = 5;

		// Set East/West traffic to green light upon creation
		ewLight.setGreen();
	}


	public void update(double deltaTime) {

		if (lightLogicRunning) {
			nsLight.update(deltaTime);
			ewLight.update(deltaTime);

			phaseTimer -= deltaTime;

			if (phaseTimer <= 0) {
				switchPhase();
			}
		}
	}


	private void switchPhase() {

		// Rotate through traffic light phases here based on waitingRequests
		// NS_TURN (on request) -> NS_STRAIGHT -> EW_TURN (on request) -> EW_STRAIGHT
		switch (currentPhase) {


			case NS_TURN:
				if (nsLight.hasWaitingRequest()) {
					nsLight.setTurnGreen();
					phaseTimer = TrafficLight.MIN_TURN_GREEN_DURATION + TrafficLight.YELLOW_DURATION;

				} else {
					currentPhase = IntersectionPhase.NS_STRAIGHT;
					switchPhase(); // Immediately rotate to NS_STRAIGHT
				}

				break;


			case NS_STRAIGHT:
				nsLight.setGreen();
				phaseTimer = TrafficLight.MIN_GREEN_DURATION + TrafficLight.YELLOW_DURATION;
				currentPhase = IntersectionPhase.EW_TURN; // Prep next phase
				break;


			case EW_TURN:
				if (ewLight.hasWaitingRequest()) {
					ewLight.setTurnGreen();
					phaseTimer = TrafficLight.MIN_TURN_GREEN_DURATION + TrafficLight.YELLOW_DURATION;

				} else {
					currentPhase = IntersectionPhase.EW_STRAIGHT;
					switchPhase(); // Immediately rotate to EW_STRAIGHT
				}

				break;


			case EW_STRAIGHT:
				ewLight.setGreen();
				phaseTimer = TrafficLight.MIN_GREEN_DURATION + TrafficLight.YELLOW_DURATION;
				currentPhase = IntersectionPhase.NS_TURN; // Loop back to start
				break;
		}
	}


	public void pauseLightLogic() {
		lightLogicRunning = false;
	}


	public void resumeLightLogic() {
		lightLogicRunning = true;
	}

	//Collection of Getters

	public TrafficLight getNSLight() {
		return nsLight;
	}


	public TrafficLight getEWLight() {
		return ewLight;
	}


	public Point getPosition() {
		return position;
	}


	public String getStreetName() {
		return streetName;
	}
}