/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class TrafficLight: Represents a traffic light that cycles through different phases 
 * (turn green, turn yellow, straight green, straight yellow, and red). 
 * 
 * Controls light state transitions based on timing, handles waiting requests from vehicles 
 * needing turn signals, and provides methods to query the current light state.
 * 
 * Also manages timing for minimum green/yellow durations and allows external triggers 
 * to request turn greens when vehicles are waiting.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

public class TrafficLight {

	// Light phases to control flow of traffic
	public enum LightPhase {
		TURN_GREEN, TURN_YELLOW, STRAIGHT_GREEN, STRAIGHT_YELLOW, RED
	}


	private LightPhase lightPhase;
	private double timer;
	private boolean waitingRequest;

	public static final double MIN_TURN_GREEN_DURATION = 10.0;
	public static final double MIN_GREEN_DURATION = 20.0;
	public static final double YELLOW_DURATION = 4.0;


	public TrafficLight() {

		this.lightPhase = LightPhase.RED;
		this.timer = 0;
		this.waitingRequest = false;
	}

	// Cycle through light phases based on assigned times
	// (TURN_GREEN) -> (TURN_YELLOW) -> STRAIGHT_GREEN -> STRAIGHT_YELLOW -> RED
	public void update(double deltaTime) {

		timer -= deltaTime;

		if (timer <= 0) {

			switch (lightPhase) {

				case TURN_GREEN:
					lightPhase = LightPhase.TURN_YELLOW;
					timer = YELLOW_DURATION;
					break;

				case STRAIGHT_GREEN:
					lightPhase = LightPhase.STRAIGHT_YELLOW;
					timer = YELLOW_DURATION;
					break;

				case TURN_YELLOW:
					lightPhase = LightPhase.STRAIGHT_GREEN;
					timer = MIN_GREEN_DURATION;
					break;

				case STRAIGHT_YELLOW:
					lightPhase = LightPhase.RED;
					timer = 0;
					break;

				case RED:
					// Stay RED
					break;
			}
		}
	}


	public void requestTurnGreen() {
		waitingRequest = true;
	}


	public boolean hasWaitingRequest() {
		return waitingRequest;
	}


	public void clearWaitingRequest() {
		waitingRequest = false;
	}


	public boolean isStraightGreen() {
		return lightPhase == LightPhase.STRAIGHT_GREEN;
	}


	public boolean isTurnGreen() {
		return lightPhase == LightPhase.TURN_GREEN;
	}


	public boolean isStraightYellow() {
		return lightPhase == LightPhase.STRAIGHT_YELLOW;
	}


	public boolean isTurnYellow() {
		return lightPhase == LightPhase.TURN_YELLOW;
	}


	public boolean isRed() {
		return lightPhase == LightPhase.RED;
	}


	// Collection of Setters

	public void setTurnGreen() {
		lightPhase = LightPhase.TURN_GREEN;
		timer = MIN_TURN_GREEN_DURATION;
	}


	public void setGreen() {
		lightPhase = LightPhase.STRAIGHT_GREEN;
		timer = MIN_GREEN_DURATION;
	}

	// Collection of Getters

	public LightPhase getLightPhase() {
		return lightPhase;
	}
}
