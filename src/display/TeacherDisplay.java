// Ryan Scarbrough
// 04/20/24
// Teacher section of UI

package display;

import data.Data;
import data.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class TeacherDisplay extends Display {
	protected static ObservableList<Teacher> teacherData = FXCollections.observableArrayList();

	protected static void dataTeacher() {
		teacherData.clear();
		teacherData.addAll(Data.getTeachers());

		Label inputLabel = new Label("Teacher Name:");
		TextField input = new TextField();
		input.setMaxWidth(400);

		TableView<Teacher> tableView = new TableView<>();
		TableColumn<Teacher, Integer> teacherColumn = new TableColumn<>("Teachers");
		teacherColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		tableView.getColumns().add(teacherColumn);
		tableView.setPlaceholder(new Label("No data to display") {
			{
				setStyle("-fx-text-fill: white;"); // Set the text color to white
			}
		});
		tableView.setItems(teacherData);

		// Ensure the first column is expanded enough
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		teacherColumn.setMaxWidth(1f * Integer.MAX_VALUE * Double.MAX_VALUE);

		input.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				try {
					String teacherName = input.getText();
					Teacher newTeacher = new Teacher(teacherName);
					teacherData.add(newTeacher);
					Data.addTeacher(newTeacher.getName());
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
			Teacher selectedTeacher = tableView.getSelectionModel().getSelectedItem();
			teacherData.remove(selectedTeacher);
			Data.removeTeacher(selectedTeacher);
		});
		contextMenu.getItems().add(deleteMenuItem);

		// Set the context menu for each row
		tableView.setRowFactory(tv -> {
			TableRow<Teacher> row = new TableRow<>();
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
