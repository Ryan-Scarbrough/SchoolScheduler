// Ryan Scarbrough
// 11/25/23
// The Grades section of the UI

package display;

import data.Data;
import data.Grade;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GradesDisplay extends Display {
	protected static ObservableList<Grade> gradesData = FXCollections.observableArrayList();

	protected static void dataGrades() {
		gradesData.clear();
		gradesData.addAll(Data.getGrades());

		Label inputLabel = new Label("Enter Grade:");
		TextField input = new TextField();
		input.setMaxWidth(200);

		TableView<Grade> tableView = new TableView<>();
		TableColumn<Grade, Integer> gradeColumn = new TableColumn<>("Grades");
		gradeColumn.setCellValueFactory(new PropertyValueFactory<>("number"));

		tableView.getColumns().add(gradeColumn);
		tableView.setPlaceholder(new Label("No data to display") {
			{
				setStyle("-fx-text-fill: white;");
			}
		});
		tableView.setItems(gradesData);

		// Ensure the first column is expanded enough
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		gradeColumn.setMaxWidth(1f * Integer.MAX_VALUE * Double.MAX_VALUE);

		input.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				try {
					int gradeNumber = Integer.parseInt(input.getText());
					Grade newGrade = new Grade(gradeNumber);
					gradesData.add(newGrade);
					Data.addGrade(newGrade.getNumber());
					input.clear();
				} catch (NumberFormatException e) {
					System.out.println("Invalid input. Please enter a valid grade number.");
				}
			}
		});

		// Add context menu to delete rows
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setStyle("-fx-text-fill: white;");

		deleteMenuItem.setOnAction(e -> {
			Grade selectedGrade = tableView.getSelectionModel().getSelectedItem();
			gradesData.remove(selectedGrade);
			Data.removeGrade(selectedGrade);
		});
		contextMenu.getItems().add(deleteMenuItem);

		// Set the context menu for each row
		tableView.setRowFactory(tv -> {
			TableRow<Grade> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
					contextMenu.show(row, event.getScreenX(), event.getScreenY());
				}
			});
			return row;
		});

		VBox vbox = new VBox(10);
		inputLabel.getStyleClass().add("inputLabel");
		input.getStyleClass().add("input");
		vbox.getStyleClass().add("inputContainer");
		vbox.getChildren().addAll(inputLabel, input, tableView);

		VBox windowContainer = new VBox(10);
		windowContainer.getChildren().addAll(vbox);

		((BorderPane) stage.getScene().getRoot()).setCenter(vbox);
	}
}
