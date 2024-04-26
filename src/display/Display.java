// Ryan Scarbrough
// 11/25/23
// Main class for the user interface

package display;

import algorithm.Algorithm;
import constraints.Constraint;
import data.Data;
import data.Schedule;
import data.Subject;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;

public class Display extends Application {
	// Globals
	protected static Stage stage;
	protected static Label importPathLabel, exportPathLabel;
	public static Schedule validSchedule;
	protected static String lastHash;
	protected static String lastSeenConstraints;

	public static void main(String[] args) {
		launch(args);
	}

	// Pretty much the main function for application
	public void start(Stage stage) {
		// Logic for saving file once the user tries to close the application
		stage.setOnCloseRequest(event -> {
			if (!Data.shouldPrompt()) {
				stage.close();
				System.exit(0);
			}

			event.consume(); // Consume the event, so the window does not close immediately

			// Create a confirmation dialog
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Exit Confirmation");
			alert.setHeaderText("Do you want to save your data before exiting?");
			alert.setContentText("");

			// Add Save, Don't Save, and Cancel buttons to the dialog
			ButtonType buttonTypeSave = new ButtonType("Save");
			ButtonType buttonTypeDontSave = new ButtonType("Don't Save");
			ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonType.CANCEL.getButtonData());
			alert.getButtonTypes().setAll(buttonTypeSave, buttonTypeDontSave, buttonTypeCancel);

			// Show the confirmation dialog and wait for user input
			alert.showAndWait().ifPresent(response -> {
				if (response == buttonTypeSave) {
					// Save data here
					FileChooser fileChooser = new FileChooser();
					fileChooser.setTitle("Select Output File");

					java.io.File selectedFile = fileChooser.showSaveDialog(stage);

					if (selectedFile != null) {
						String filePath = selectedFile.getAbsolutePath();
						filePath += ".json";

						try {
							Algorithm.exportData(filePath);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					} else {
						System.out.println("No file selected.");
					}

					stage.close();
				} else if (response == buttonTypeDontSave) {
					stage.close();
				} else {
					// User canceled, do nothing
				}
			});
		});

		stage.setTitle("School Scheduler");
		Display.stage = stage;

		// Basic stage setup
		BorderPane borderPane = new BorderPane();
		Scene scene = new Scene(borderPane, 1015, 700);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		stage.setMinWidth(1015);
		stage.setMinHeight(700);

		// Create the main navbar
		HBox mainNavBar = createMainNavBar();
		HBox dataNavBar = GradesDisplay.createDataNavBar();
		dataNavBar.setVisible(false); // initially, hide the secondary navbar

		// Create a VBox to hold both the main and secondary navbars
		VBox navBarsContainer = new VBox(mainNavBar, dataNavBar);
		borderPane.setTop(navBarsContainer);

		// Show the stage
		stage.setScene(scene);
		stage.show();

		// Send the user to the home page
		ImportExportDisplay.dataImportExport();
		showDataNavBar(true);
	}

	// Creates the main navigation bar at the top
	private static HBox createMainNavBar() {
		HBox mainNavBar = new HBox();
		mainNavBar.setId("navbar");

		Label dataLabel = new Label("Data");
		Label constraintLabel = new Label("Constraints");
		Label viewLabel = new Label("View");

		// CSS for labels/buttons
		dataLabel.getStyleClass().add("label-section");
		constraintLabel.getStyleClass().add("label-section");
		viewLabel.getStyleClass().add("label-section");

		// Set actions for each section
		dataLabel.setOnMouseClicked(event -> {
			ImportExportDisplay.dataImportExport();
			showDataNavBar(true);
		});

		constraintLabel.setOnMouseClicked(event -> {
			showConstraintsContent();
			showDataNavBar(false);
		});

		viewLabel.setOnMouseClicked(event -> {
			showViewContent();
			showDataNavBar(false);
		});

		// Add sections to the main navbar
		mainNavBar.getChildren().addAll(dataLabel, constraintLabel, viewLabel);

		return mainNavBar;
	}

	// Creates secondary navbar when "Data" section is clicked
	protected static HBox createDataNavBar() {
		HBox dataNavBar = new HBox();
		dataNavBar.setId("datanavbar");

		Label dataHome = new Label("Import / Export");
		Label subjects = new Label("Add Subject");
		Label teachers = new Label("Add Teacher");
		Label grades = new Label("Add Grades");
		Label timeslots = new Label("Add TimeSlot");

		// CSS for labels
		dataHome.getStyleClass().add("label-section");
		subjects.getStyleClass().add("label-section");
		teachers.getStyleClass().add("label-section");
		grades.getStyleClass().add("label-section");
		timeslots.getStyleClass().add("label-section");

		// Set actions for each option in the data navbar
		dataHome.setOnMouseClicked(event -> ImportExportDisplay.dataImportExport());
		grades.setOnMouseClicked(event -> GradesDisplay.dataGrades());
		teachers.setOnMouseClicked(event -> TeacherDisplay.dataTeacher());
		subjects.setOnMouseClicked(event -> SubjectDisplay.dataSubject());
		timeslots.setOnMouseClicked(event -> TimeSlotDisplay.dataTimeSlots());

		// Add options to the data navbar
		dataNavBar.getChildren().addAll(dataHome, grades, teachers, subjects, timeslots);

		return dataNavBar;
	}

	// Show or hide the "data" secondary nav bar
	protected static void showDataNavBar(boolean visible) {
		((VBox) ((BorderPane) stage.getScene().getRoot()).getTop()).getChildren().get(1).setVisible(visible);
	}

	private static void showConstraintsContent() {
		VBox vb = new VBox();
		GridPane gp = new GridPane();

		gp.getStyleClass().add("grid-pane");

		int firstPeriod = Data.getFirstPeriod();
		int lastPeriod = Data.getLastPeriod();
		int startGrade = Data.getStartGrade();
		int endGrade = Data.getEndGrade();

		// Add labels for periods and grades
		for (int period = firstPeriod; period <= lastPeriod; period++) {
			Label label = new Label("Period " + period);
			GridPane.setMargin(label, new Insets(5));
			label.getStyleClass().add("grid-pane-cell-first-column");
			gp.add(label, 0, period);
		}

		for (int grade = startGrade; grade <= endGrade; grade++) {
			Label label = new Label("Grade " + grade);
			GridPane.setMargin(label, new Insets(5));
			label.getStyleClass().add("grid-pane-cell-first-column");
			gp.add(label, grade, 0);
		}

		try {
			if (Constraint.constraints == null) {
				Constraint.init();
			}
		} catch (NullPointerException e) {
			Label constraintsContent = new Label("Please load a schedule first by clicking \"View\" at the top.");
			constraintsContent.setId("content");
			((BorderPane) stage.getScene().getRoot()).setCenter(constraintsContent);
			return;
		}

		populateGrid(gp, Constraint.constraints, true);

		// Set padding for the GridPane
		gp.setPadding(new Insets(10));

		// Add GridPane to VBox
		vb.getChildren().add(gp);

		((BorderPane) stage.getScene().getRoot()).setCenter(vb);
	}

	private static void addButtonsToBottomRow(GridPane gp) {
		// Please don't look at this. I tried for so long and this is the only thing I got to work. I know it's bad
		// Remove bottom row if it has save button
		boolean saveButtonExists = gp.getChildren().stream()
				.filter(node -> node instanceof Button)
				.map(node -> (Button) node)
				.anyMatch(button -> button.getText().equals("Save") && GridPane.getRowIndex(button) == gp.getRowCount() - 1);
		if (saveButtonExists) {
			int lastRow = gp.getRowCount() - 1;
			gp.getChildren().removeIf(node -> GridPane.getRowIndex(node) == lastRow);
		}

		// Save button
		Button save = new Button("Save");
		save.setOnAction(e -> {
			handleSave(gp);
		});
		save.setId("button");

		// Arrow buttons
		Button left = new Button("<");
		Button right = new Button(">");
		left.setId("button");
		right.setId("button");

		// Button actions
		left.setOnAction(e -> {
			populateGrid(gp, Algorithm.prevSchedule(), false);
		});
		right.setOnAction(e -> {
			populateGrid(gp, Algorithm.nextSchedule(), false);
		});

		HBox hb = new HBox(left, right);
		gp.add(hb, 0, gp.getRowCount());
		gp.add(save, gp.getColumnCount()-1, gp.getRowCount()-1);
		GridPane.setHalignment(save, HPos.RIGHT);
	}

	private static void showViewContent() {
		if (lastSeenConstraints == null && Constraint.constraints != null) {
			lastSeenConstraints = Data.hash(String.valueOf(Constraint.constraints.getSubjects()));
		}

		// if constraints changed, clear list
		if (Constraint.constraints != null) {
			System.out.println(lastSeenConstraints);
			System.out.println(Data.hash(Constraint.constraints.getSubjects().toString()));
			if (!lastSeenConstraints.equals(Data.hash(Constraint.constraints.getSubjects().toString()))) {
				validSchedule.clearTimeSlotSubjects();
				Algorithm.clearSchedules();
				Algorithm.scheduleIndex = -1;
			}
		}

		if (Data.isNull()) {
			Label constraintsContent = new Label("Please add data before attempting to view a schedule.");
			constraintsContent.setId("content");
			((BorderPane) stage.getScene().getRoot()).setCenter(constraintsContent);
			return;
		}

		GridPane gp = new GridPane();
		VBox vb = new VBox();

		gp.getStyleClass().add("grid-pane");

		int firstPeriod = Data.getFirstPeriod();
		int lastPeriod = Data.getLastPeriod();
		int startGrade = Data.getStartGrade();
		int endGrade = Data.getEndGrade();

		// Add labels for periods and grades
		for (int period = firstPeriod; period <= lastPeriod; period++) {
			Label label = new Label("Period " + period);
			GridPane.setMargin(label, new Insets(5));
			label.getStyleClass().add("grid-pane-cell-first-column");
			gp.add(label, 0, period);
		}

		for (int grade = startGrade; grade <= endGrade; grade++) {
			Label label = new Label("Grade " + grade);
			GridPane.setMargin(label, new Insets(5));
			label.getStyleClass().add("grid-pane-cell-first-column");
			gp.add(label, grade, 0);
		}

		if (validSchedule == null || lastHash == null || !lastHash.equals(Data.getHash())) {
			validSchedule = Algorithm.generateNewSchedule();
			lastHash = Data.getHash();
		} else if (validSchedule.isEmpty()) {
			List<Schedule> temp = new ArrayList<>(Algorithm.getSchedules());
			if (Algorithm.getSchedules().size() == 0) {
				validSchedule = Algorithm.generateNewSchedule();
			} else {
				validSchedule = temp.get(Algorithm.scheduleIndex);
			}
		}

		populateGrid(gp, validSchedule, false);

		addButtonsToBottomRow(gp); // Call the function to add buttons

		// Set padding for the GridPane
		gp.setPadding(new Insets(10));

		// Add GridPane to VBox
		vb.getChildren().add(gp);

		((BorderPane) stage.getScene().getRoot()).setCenter(vb);
	}

	public static void populateGrid(GridPane gp, Schedule s, boolean enableCellClick) {
		if (s == null) {
			System.out.println("WARNING: Attempted to populate grid, but Schedule was null");
			return;
		}

		int firstPeriod = Data.getFirstPeriod();
		int lastPeriod = Data.getLastPeriod();
		int startGrade = Data.getStartGrade();
		int endGrade = Data.getEndGrade();

		// Populate the grid with class information
		for (int period = firstPeriod; period <= lastPeriod; period++) {
			for (int grade = startGrade; grade <= endGrade; grade++) {
				String className = s.getSubject(grade, period).getName();
				Label label = new Label(className);
				label.getStyleClass().add("label");

				String teacher = String.valueOf(s.getSubject(grade, period).getTeachers()).replaceAll("[\\]\\[]", "");
				String grades = String.valueOf(s.getSubject(grade, period).getGrades()).replaceAll("[\\[\\]]", "");
				// Create a tooltip with additional information
				Tooltip tooltip = new Tooltip("Subject: " + className + "\nTeacher(s): " + teacher + "\nGrade(s): " + grades);
				Tooltip.install(label, tooltip);

				// Add the label to the cell
				GridPane.setRowIndex(label, period);
				GridPane.setColumnIndex(label, grade);
				GridPane.setMargin(label, new Insets(5));

				// Add hover effect to the cell
				label.setOnMouseEntered(event -> label.getStyleClass().add("hovered"));
				label.setOnMouseExited(event -> label.getStyleClass().remove("hovered"));

				if (enableCellClick) {
					// Add click event to set value
					label.setOnMouseClicked(event -> {
						// Get the clicked label's row and column index
						int rowIndex = GridPane.getRowIndex(label);
						int colIndex = GridPane.getColumnIndex(label);

						// Get the subject from Schedule
						Subject clickedSubject = s.getSubject(colIndex, rowIndex);

						// Open a dialog to allow subject selection
						List<String> choices = new ArrayList<>(List.of(Data.getSubjectNames()));
						// Add an option to remove the subject
						choices.add(0, "None");

						ChoiceDialog<String> dialog = new ChoiceDialog<>(clickedSubject != null ? clickedSubject.getName() : "None", choices);
						dialog.setTitle("Select Subject");
						dialog.setHeaderText("Select a subject:");
						dialog.setContentText("Subject:");

						// Show the dialog and wait for user input
						dialog.showAndWait().ifPresent(selectedSubjectName -> {
							if (selectedSubjectName.equals("None")) {
								// If user selects "None", remove the subject from the cell
								showViewContent();
								try {
									s.removeSubject(colIndex, rowIndex);
								} catch (ConcurrentModificationException e) { // this fixes a concurrency bug. please ignore
									showViewContent();
									showConstraintsContent();
								}
							} else {
								int index = Integer.parseInt(selectedSubjectName.substring(1, 3).replace("]", ""));
								Subject chosenSubject = Data.getSubjects().get(index);
								Constraint.constraints.setSubject(chosenSubject, rowIndex);
							}
							showConstraintsContent();
						});
					});
				}

				// Add the cell to the GridPane
				gp.getChildren().add(label);
			}
		}
	}


	public static void handleSave(GridPane gp) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select Output File");

		java.io.File selectedFile = fileChooser.showSaveDialog(stage);

		if (selectedFile != null) {
			String filePath = selectedFile.getAbsolutePath();
			filePath += ".png";
			exportToPNG(gp, filePath);
		} else {
			System.out.println("No file selected.");
		}
	}

	private static void exportToPNG(GridPane gridPane, String filePath) {
		// Convert GridPane to image
		SnapshotParameters snapshotParams = new SnapshotParameters();
		WritableImage image = gridPane.snapshot(snapshotParams, null);

		// Save image as PNG
		File file = new File(filePath);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void unfocusAll(Parent root) {
		unfocusAll(root, false);
	}

	public static void unfocusAll(Parent root, boolean includeHidden) {
		root.requestFocus(); // Request focus on the root to unfocus other elements
		unfocusAllChildren(root, includeHidden);
	}

	private static void unfocusAllChildren(Parent parent, boolean includeHidden) {
		for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
			if (node instanceof Control) {
				Control control = (Control) node;
				control.setFocusTraversable(false);
			} else if (node instanceof Region) {
				Region region = (Region) node;
				region.setFocusTraversable(false);
			}

			if (includeHidden || node.isVisible()) {
				if (node instanceof Parent) {
					unfocusAllChildren((Parent) node, includeHidden);
				}
			}
		}
	}
}
