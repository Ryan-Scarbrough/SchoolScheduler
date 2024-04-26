// Ryan Scarbrough
// 11/27/23
// The Subject section for the UI

package display;

import data.Data;
import data.Grade;
import data.Subject;
import data.Teacher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.controlsfx.control.CheckComboBox;

public class SubjectDisplay extends Display {
	protected static ObservableList<Subject> subjectData = FXCollections.observableArrayList();
	protected static ObservableList<Grade> gradesData = FXCollections.observableArrayList();
	protected static ObservableList<Teacher> teachersData = FXCollections.observableArrayList();

	protected static void dataSubject() {
		// Make sure all data is up-to-date
		subjectData.clear();
		subjectData.addAll(Data.getSubjects());
		gradesData.clear();
		gradesData.addAll(Data.getGrades());
		teachersData.clear();
		teachersData.addAll(Data.getTeachers());

		// Input Labels
		Label nameLabel = new Label("Subject Name:");
		Label gradesLabel = new Label("Grades Taking:");
		Label teachersLabel = new Label("Teachers:");

		// User Inputs
		TextField nameInput = new TextField();
		nameInput.setMinWidth(250);
		nameInput.setMaxWidth(250);
		CheckComboBox<Grade> gradesInput = new CheckComboBox<>();
		gradesInput.getItems().addAll(gradesData);
		gradesInput.setMinWidth(200);
		gradesInput.setMaxWidth(200);
		CheckComboBox<Teacher> teachersInput = new CheckComboBox<>();
		teachersInput.getItems().addAll(teachersData);
		teachersInput.setMinWidth(350);
		teachersInput.setMaxWidth(350);

		// Table Creation to display.Display DataClasses.Data
		TableView<Subject> tableView = new TableView<>();
		TableColumn<Subject, Integer> subjectNameColumn = new TableColumn<>("Subject Name");
		subjectNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		subjectNameColumn.setPrefWidth(300);
		TableColumn<Subject, String> subjectGradesColumn = new TableColumn<>("Grades");
		subjectGradesColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGradesString()));
		subjectGradesColumn.setPrefWidth(290);
		TableColumn<Subject, String> subjectTeachersColumn = new TableColumn<>("Teachers");
		subjectTeachersColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTeachersString()));
		subjectTeachersColumn.setPrefWidth(300);

		tableView.getColumns().addAll(subjectNameColumn, subjectGradesColumn, subjectTeachersColumn);
		tableView.setPlaceholder(new Label("No data to display") {
			{
				setStyle("-fx-text-fill: white;"); // Set the text color to white
			}
		});
		tableView.setItems(subjectData);

		// Making a Delete Button When Right-Clicking Item
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setStyle("-fx-text-fill: white;");
		deleteMenuItem.setOnAction(e -> {
			Subject selectedSubject = tableView.getSelectionModel().getSelectedItem();
			subjectData.remove(selectedSubject);
			Data.removeSubject(selectedSubject);
		});
		contextMenu.getItems().add(deleteMenuItem);

		// Adding the Button to All Items in the Table
		tableView.setRowFactory(tv -> {
			TableRow<Subject> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (!row.isEmpty() && event.getButton() == MouseButton.SECONDARY) {
					contextMenu.show(row, event.getScreenX(), event.getScreenY());
				}
			});
			return row;
		});

		// Create a Submit Button
		Button submitButton = new Button("Add");
		submitButton.setStyle("-fx-padding: 6 25 6 25");
		submitButton.setOnAction(e -> {
			//DataClasses.Subject selectedSubject = tableView.getSelectionModel().getSelectedItem();
			String subjectName = nameInput.getText();
			ObservableList<Grade> selectedGrades = gradesInput.getCheckModel().getCheckedItems();
			ObservableList<Teacher> selectedTeachers = teachersInput.getCheckModel().getCheckedItems();

			Grade[] gradeAttr = selectedGrades.toArray(new Grade[0]);
			Teacher[] teachersAttr = selectedTeachers.toArray(new Teacher[0]);

			// Updating all data fields
			Subject sub = new Subject(subjectName, gradeAttr, teachersAttr, '\0');
			subjectData.add(sub);
			Data.addSubject(sub);

			// Clear the input fields after submission
			nameInput.clear();
			gradesInput.getCheckModel().clearChecks();
			teachersInput.getCheckModel().clearChecks();
		});

		// DataClasses.Subject Name VBox
		VBox subjectNameVB = new VBox(10);
		subjectNameVB.getChildren().addAll(nameLabel, nameInput);

		// DataClasses.Subject Grades VBox
		VBox subjectGradesVB = new VBox(10);
		subjectGradesVB.getChildren().addAll(gradesLabel, gradesInput);

		// DataClasses.Subject Teachers VBox
		VBox subjectTeachersVB = new VBox(10);
		subjectTeachersVB.getChildren().addAll(teachersLabel, teachersInput);

		// Submit button alignment shenanigans
		VBox submitButtonVB = new VBox(10);
		submitButtonVB.getChildren().addAll(new Label(""), submitButton);

		// Entire User Input HBox
		HBox userInput = new HBox(10);
		userInput.getChildren().addAll(subjectNameVB, subjectGradesVB, subjectTeachersVB, submitButtonVB);

		// CSS Before Adding All Items
		VBox vbox = new VBox(10);
		nameLabel.getStyleClass().add("inputLabel");
		gradesLabel.getStyleClass().add("inputLabel");
		teachersLabel.getStyleClass().add("inputLabel");
		nameInput.getStyleClass().add("input");
		gradesInput.getStyleClass().add("input");
		teachersInput.getStyleClass().add("input");
		vbox.getStyleClass().add("inputContainer");

		// Final VBox
		vbox.getChildren().addAll(userInput, tableView);

		VBox windowContainer = new VBox(10);
		windowContainer.getChildren().addAll(vbox);

		((BorderPane) stage.getScene().getRoot()).setCenter(vbox);
	}
}
