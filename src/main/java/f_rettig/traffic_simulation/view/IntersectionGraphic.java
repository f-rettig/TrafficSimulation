/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class IntersectionGraphic: 
 * 
 * A JavaFX UI component that visually represents an intersection with four traffic lights 
 * (North, South, East, West). Each light reflects the current state of the simulation’s 
 * Intersection traffic light phases.
 * 
 * Uses TrafficLightGraphic for each direction’s display.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;


public class IntersectionGraphic extends BorderPane {

	private Intersection intersection;

	private TrafficLightGraphic north;
	private TrafficLightGraphic south;
	private TrafficLightGraphic east;
	private TrafficLightGraphic west;
	private Pane spacerPane;				// Spacer pane for visual purposes only


	public IntersectionGraphic(Intersection intersection) {

		this.intersection = intersection;

		// Create all the light graphics
		north = new TrafficLightGraphic();
		south = new TrafficLightGraphic();
		east = new TrafficLightGraphic();
		west = new TrafficLightGraphic();

		spacerPane = new Pane();
		spacerPane.setPrefSize(20,  20);

		// Rotate arrows where needed
		north.rotateArrow(270);
		south.rotateArrow(90);
		west.rotateArrow(0);
		east.rotateArrow(180);

		// Layout nicely inside BorderPane
		this.setTop(north);
		BorderPane.setAlignment(north, Pos.CENTER);

		this.setRight(east);
		BorderPane.setAlignment(east, Pos.CENTER);

		this.setBottom(south);
		BorderPane.setAlignment(south, Pos.CENTER);

		this.setLeft(west);
		BorderPane.setAlignment(west, Pos.CENTER);

		this.setCenter(spacerPane);

		this.setPadding(new Insets(0, 0, 0, 5));
	}


	/**
	 * Updates the visual state of each traffic light based on the current 
	 * traffic light phases in the Intersection.
	 */
	public void updateLights() {
		TrafficLight nsLight = intersection.getNSLight();
		TrafficLight ewLight = intersection.getEWLight();

		// Update based on NS TrafficLight phases
		if (nsLight.isTurnGreen()) {
			north.setTurnGreen();
			south.setTurnGreen();

		} else if (nsLight.isTurnYellow()) {
			north.setTurnYellow();
			south.setTurnYellow();

		} else if (nsLight.isStraightGreen()) {
			north.setStraightGreen();
			south.setStraightGreen();

		} else if (nsLight.isStraightYellow()) {
			north.setStraightYellow();
			south.setStraightYellow();

		} else {
			north.setRed();
			south.setRed();
		}

		// Update based on EW TrafficLight phases
		if (ewLight.isTurnGreen()) {
			east.setTurnGreen();
			west.setTurnGreen();

		} else if (ewLight.isTurnYellow()) {
			east.setTurnYellow();
			west.setTurnYellow();

		} else if (ewLight.isStraightGreen()) {
			east.setStraightGreen();
			west.setStraightGreen();

		} else if (ewLight.isStraightYellow()) {
			east.setStraightYellow();
			west.setStraightYellow();

		} else {
			east.setRed();
			west.setRed();

		}
	}
}
