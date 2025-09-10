/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class Road: Represents a roadway in the simulation. 
 * 
 * Roads have a name, defined spawn points for cars, and a visual Rectangle for display.
 * The main road runs horizontally, while other roads (side roads) run vertically 
 * and can intersect the main road.
 * 
 * Coordinates and sizing are based on simulation units where:
 * - 1 unit = 10 meters = 2.5 pixels.
 * - Main road extends from 0 to 400 units (4000 meters, 1000 pixels wide).
 * - Side roads are spaced based on their index and scaled accordingly.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

import java.util.ArrayList;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class Road {
	private String name;
	private ArrayList<Point> spawnPoints;		// Points where cars can spawn
	private Rectangle roadRectangle;
	private boolean isMainRoad;


	public Road(String name, boolean isMainRoad, int index) {
		this.name = name;
		this.isMainRoad = isMainRoad;
		this.spawnPoints = new ArrayList<>();

		generateSpawnPoints(index);
	}


	/**
	 * Generates spawn points and visual rectangle for the road.
	 * 
	 * - Main Road runs East/West and is horizontal.
	 * - Side roads run North/South and are vertical.
	 * 
	 * Positioning and dimensions are scaled using:
	 */
	private void generateSpawnPoints(int index) {
		if (this.isMainRoad) {
			spawnPoints.add(new Point(0.0,0.0));
			spawnPoints.add(new Point(400.0, 0.0));

			roadRectangle = new Rectangle(1005.0, 10.0);
			roadRectangle.setLayoutX(10.0);
			roadRectangle.setLayoutY(132.5);
			roadRectangle.setFill(Color.DARKGRAY);

		} else {
			double xPosition = index * 100.0;
			spawnPoints.add(new Point(xPosition,50.0));
			spawnPoints.add(new Point(xPosition, -50.0));

			roadRectangle = new Rectangle(10.0, 255);
			roadRectangle.setLayoutX((xPosition * 2.5) + 7.5);
			roadRectangle.setLayoutY(0.0 + 10.0);
			roadRectangle.setFill(Color.DARKGRAY);
		}
	}


	// Adjust the eastward spawn point when extending the main road
	public void adjustEastSpawnPoint(int intersectionCount) {
		spawnPoints.removeLast();
		spawnPoints.add(new Point((intersectionCount * 100.0), 0.0));
		roadRectangle.setWidth(roadRectangle.getWidth() + 250);;
	}

	// Collection of Getters

	public String getRoadName() {
		return name;
	}


	public ArrayList<Point> getSpawnPoints() {
		return spawnPoints;
	}


	public Rectangle getRoadRectangle() {
		return roadRectangle;
	}
}
