/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class Point: Represents a 2D point in the simulation world. 
 * 
 * Used for positioning cars, spawn points, intersections, etc.
 * The x and y values are in simulation **units** where:
 * - 1 unit = 10 meters = 2.5 pixels.
 * 
 * Provides getter and setter methods to modify or retrieve 
 * the coordinates and a toString for readable output.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */

package application;

public class Point {
	private double x;
	private double y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	// Collection of Setters

	public void setX(double newX) {
		x = newX;
	}


	public void setY(double newY) {
		y = newY;
	}


	public void set(double newX, double newY) {
		x = newX;
		y = newY;
	}

	// Collection of getters

	public double getX() {
		return x;
	}


	public double getY() {
		return y;
	}


	// Point to String for TableView
	public String toString() {
		return "(" + (int)(x * 100) / 100.0 + "," + (int)(y * 100) / 100.0 + ")";
	}
}
