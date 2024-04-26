// Ryan Scarbrough
// 04/20/24
// TimeSlot section for UI

package display;

import data.Data;
import data.TimeSlot;
import com.dlsc.gemsfx.TimePicker;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalTime;

public class TimeSlotDisplay extends Display {
	protected static ObservableList<TimeSlot> timeSlotData = FXCollections.observableArrayList();

	protected static void dataTimeSlots() {
		// Make sure all data is up-to-date
		timeSlotData.clear();
		timeSlotData.addAll(Data.getTimeslots());

		// Input Labels
		Label periodLabel = new Label("Period:");
		Label startTimeLabel = new Label("Start Time:");
		Label endTimeLabel = new Label("End Time:");

		// User Inputs
		TextField periodInput = new TextField();
		periodInput.setMinWidth(250);
		periodInput.setMaxWidth(250);
		TimePicker startTimeInput = new TimePicker();
		startTimeInput.setMinWidth(150);
		startTimeInput.setMaxWidth(150);
		TimePicker endTimeInput = new TimePicker();
		endTimeInput.setMinWidth(150);
		endTimeInput.setMaxWidth(150);

		// Table Creation to display.Display DataClasses.Data
		TableView<TimeSlot> tableView = new TableView<>();
		TableColumn<TimeSlot, Integer> periodColumn = new TableColumn<>("Period");
		periodColumn.setCellValueFactory(new PropertyValueFactory<>("period"));
		periodColumn.setPrefWidth(100);
		TableColumn<TimeSlot, String> startTimeColumn = new TableColumn<>("Start Time");
		startTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStartTime()));
		startTimeColumn.setPrefWidth(300);
		TableColumn<TimeSlot, String> endTimeColumn = new TableColumn<>("End Time");
		endTimeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEndTime()));
		endTimeColumn.setPrefWidth(300);

		tableView.getColumns().addAll(periodColumn, startTimeColumn, endTimeColumn);
		tableView.setPlaceholder(new Label("No data to display") {
			{
				setStyle("-fx-text-fill: white;"); // Set the text color to white
			}
		});
		tableView.setItems(timeSlotData);

		// Making a Delete Button When Right-Clicking Item
		ContextMenu contextMenu = new ContextMenu();
		MenuItem deleteMenuItem = new MenuItem("Delete");
		deleteMenuItem.setStyle("-fx-text-fill: white;");
		deleteMenuItem.setOnAction(e -> {
			TimeSlot selectedTimeSlot = tableView.getSelectionModel().getSelectedItem();
			timeSlotData.remove(selectedTimeSlot);
			Data.removeTimeSlot(selectedTimeSlot);
		});
		contextMenu.getItems().add(deleteMenuItem);

		// Adding the Button to All Items in the Table
		tableView.setRowFactory(tv -> {
			TableRow<TimeSlot> row = new TableRow<>();
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
			String periodString = periodInput.getText();
			int period;
			LocalTime startTime = startTimeInput.getTime();
			LocalTime endTime = endTimeInput.getTime();

			// Check for integer here
			try {
				period = Integer.parseInt(periodString);
			} catch (NumberFormatException er) {
				System.out.println("Invalid input. Please enter a valid period number.");
				return;
			}

			// Updating all data fields
			TimeSlot ts = new TimeSlot(period, startTime.toString(), endTime.toString());
			timeSlotData.add(ts);
			Data.addTimeSlot(ts);

			// Clear the input fields after submission
			periodInput.clear();
		});

		// DataClasses.Subject Name VBox
		VBox periodVB = new VBox(10);
		periodVB.getChildren().addAll(periodLabel, periodInput);

		// DataClasses.Subject Grades VBox
		VBox startTimeVB = new VBox(10);
		startTimeVB.getChildren().addAll(startTimeLabel, startTimeInput);

		// DataClasses.Subject Teachers VBox
		VBox endTimeVB = new VBox(10);
		endTimeVB.getChildren().addAll(endTimeLabel, endTimeInput);

		// Submit button alignment shenanigans
		VBox submitButtonVB = new VBox(10);
		submitButtonVB.getChildren().addAll(new Label(""), submitButton);

		// Entire User Input HBox
		HBox userInput = new HBox(10);
		userInput.getChildren().addAll(periodVB, startTimeVB, endTimeVB, submitButtonVB);

		// CSS Before Adding All Items
		VBox vbox = new VBox(10);
		periodLabel.getStyleClass().add("inputLabel");
		startTimeLabel.getStyleClass().add("inputLabel");
		endTimeLabel.getStyleClass().add("inputLabel");
		periodInput.getStyleClass().add("input");
		startTimeInput.getStyleClass().add("input");
		startTimeInput.getStyleClass().add("timeInput");
		endTimeInput.getStyleClass().add("input");
		endTimeInput.getStyleClass().add("timeInput");
		vbox.getStyleClass().add("inputContainer");

		// Final VBox
		vbox.getChildren().addAll(userInput, tableView);

		VBox windowContainer = new VBox(10);
		windowContainer.getChildren().addAll(vbox);

		((BorderPane) stage.getScene().getRoot()).setCenter(vbox);
	}
}
