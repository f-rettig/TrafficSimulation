module finalProject335 {
	requires javafx.controls;
	requires java.net.http;
	requires javafx.graphics;
	requires javafx.base;
	requires javafx.fxml;
	
	opens application to javafx.graphics, javafx.fxml, javafx.base;
}
