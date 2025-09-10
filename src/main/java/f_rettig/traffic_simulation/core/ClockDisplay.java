/**
 * UMGC CMSC 335
 * Final Project: Traffic Simulation 
 * Class ClockDisplay: Handles updating and displaying the current time to the UI 
 * by running a background thread. The time is updated every second and passed to 
 * the UI via a callback function.
 * 
 * @author Felicia Rettig
 * Date: May 6, 2025 
 * Java 22
 */


package application;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

import javafx.application.Platform;

public class ClockDisplay implements Runnable {

	// Callback function to send the updated time back to the UI
	private Consumer<String> timeUpdateCallback;

	//Consumer function that takes a String (formatted time) and updates the UI.
	public ClockDisplay(Consumer<String> timeUpdateCallback) {
		this.timeUpdateCallback = timeUpdateCallback;
	}


	/**
	 * The run method continuously updates the current time every second 
	 * and uses Platform.runLater() to update the UI thread safely.
	 */
	@Override
	public void run() {

		while (true) {
			String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss a"));

			// Pass the current time to the UI thread safely
			Platform.runLater(() -> timeUpdateCallback.accept(currentTime));

			try {

				// Wait for 1 second before updating the time again
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
