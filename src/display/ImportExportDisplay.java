// Ryan Scarbrough
// 11/25/23
// The Import/Export section for the user interface

package display;

import algorithm.Algorithm;
import data.Data;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.IOException;

public class ImportExportDisplay extends Display {
	// Handles import/export section of secondary nav bar
	public static void dataImportExport() {
		Button Import = new Button("Import");
		Button Export = new Button("Export");
		Button Clear = new Button("Clear Data");
		VBox hb = new VBox();

		// Create the label for file path
		importPathLabel = new Label("");
		exportPathLabel = new Label("");

		Import.setOnAction(e -> {
			try {
				handleImportButton();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
		Export.setOnAction(e -> {
			try {
				handleExportButton();
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		});
		Clear.setOnAction(e -> handleClearButton());

		// CSS shenanigans
		Import.setId("button");
		Export.setId("button");
		Clear.setId("button");
		importPathLabel.setId("filePathLabel");
		exportPathLabel.setId("filePathLabel");
		hb.setId("buttonContainer");

		// Create an HBox to arrange the Import button and file path label horizontally
		HBox importHBox = new HBox(10);
		importHBox.getChildren().addAll(Import, importPathLabel);

		HBox exportHBox = new HBox(10);
		exportHBox.getChildren().addAll(Export, exportPathLabel);

		hb.getChildren().addAll(importHBox, exportHBox, Clear);
		((BorderPane) stage.getScene().getRoot()).setCenter(hb);
	}

	// Handles import button
	public static void handleImportButton() throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a file to import");

		// Set extension filter
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("JSON files (*.json)", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);

		var selectedFile = fileChooser.showOpenDialog(stage);

		if (selectedFile != null) {
			String filePath = selectedFile.getAbsolutePath();

			Algorithm.importData(filePath);

			importPathLabel.setText("Imported data from: " + filePath);
			exportPathLabel.setText("");
		} else {
			System.out.println("No file selected");
		}
	}

	// Export button
	public static void handleExportButton() throws IOException {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Output File");

		java.io.File selectedFile = fileChooser.showSaveDialog(stage);

		if (selectedFile != null) {
			String filePath = selectedFile.getAbsolutePath();
			filePath += ".json";

			Algorithm.exportData(filePath);

			exportPathLabel.setText("Exported data to: " + filePath);
			importPathLabel.setText("");

		} else {
			System.out.println("No file selected.");
		}
	}

	// Logic for clearing data as well as confirmation
	public static void handleClearButton() {
		// Show confirmation dialog
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText("Clear All Data");
		alert.setContentText("Are you sure you want to clear all data?");

		alert.setGraphic(createWarningIcon());

		// Option for user
		alert.showAndWait().ifPresent(response -> {
			if (response == ButtonType.OK) {
				Data.clear();
			}
		});
	}

	// Just a warning icon, probably easier than loading from a png
	private static StackPane createWarningIcon() {
		Polygon triangle = new Polygon();
		triangle.getPoints().addAll(
				0.0, 0.0,
				20.0, 40.0,
				-20.0, 40.0
		);
		triangle.setFill(Color.ORANGERED);

		// Create the exclamation mark
		Text exclamation = new Text("!");
		exclamation.setFont(Font.font("Arial", 20));
		exclamation.setFill(Color.WHITE);

		// Place the exclamation mark in the middle of the triangle
		StackPane icon = new StackPane();
		icon.getChildren().addAll(triangle, exclamation);

		return icon;
	}
}
