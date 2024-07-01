module CardMatcher {
	requires javafx.controls;
	requires javafx.fxml;
	requires opencv;
	requires transitive javafx.graphics;
	requires javafx.media;
	requires javafx.swing;
	requires java.desktop;
	requires java.sql;
	opens org.front to javafx.fxml;

	exports org.front;
}