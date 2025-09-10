/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class TrafficLightDisplay:
 * 
 * A JavaFX HBox-based UI component that visually displays the traffic lights 
 * for all intersections in the simulation. Each Intersection is represented 
 * by an IntersectionGraphic.
 * 
 * Runs in a background thread to periodically update the visual state of 
 * each traffic light to reflect the simulation logic.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import java.util.ArrayList;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

public class TrafficLightDisplay extends HBox implements Runnable {

	// List of graphics representing each intersection's lights
	private ArrayList<IntersectionGraphic> intersectionGraphics;

	private volatile boolean isRunning = true; 


	public TrafficLightDisplay(SimulationController controller) {
		intersectionGraphics = new ArrayList<>();
		this.setAlignment(Pos.CENTER);
	}


	@Override
	public void run() {
		while (isRunning) {

			try {
				Thread.sleep(250);

				// Run UI updates on the JavaFX thread
				Platform.runLater(() -> updateLights());

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}


	public void addIntersection(Intersection intersection) {
		IntersectionGraphic ig = new IntersectionGraphic(intersection);
		intersectionGraphics.add(ig);
		Platform.runLater(() -> this.getChildren().add(ig));
	}


	// Updates the visual state of all intersection graphics.
	private void updateLights() {
		for (IntersectionGraphic ig : intersectionGraphics) {
			ig.updateLights();
		}
	}


	public void clear() {
		this.getChildren().clear();
	}


	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}


	public boolean getIsRunning() {
		return isRunning;
	}
}