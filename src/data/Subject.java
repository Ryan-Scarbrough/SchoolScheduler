// Ryan Scarbrough
// 11/24/23
// Stores subject data

package data;

import java.util.ArrayList;
import java.util.List;

public class Subject {
	private final String name;
	private final ArrayList<Teacher> teachers;
	private final ArrayList<Grade> grades;
	private Character group;

	public Subject(String name, Grade[] grades, Teacher[] teachers, char group) {
		this.name = name;
		this.grades = new ArrayList<>(List.of(grades));
		this.teachers = new ArrayList<>(List.of(teachers));
		if (group == '\0')
			this.group = null;
		else
			this.group = group;
	}

	// Getters
	public String getName() {
		return name;
	}

	public ArrayList<Teacher> getTeachers() {
		return teachers;
	}

	public ArrayList<Grade> getGrades() {
		return grades;
	}

	public Character getGroup() {
		return group;
	}

	public String getGradesString() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < grades.size(); i++) {
			result.append(grades.get(i));

			// Add a comma after each element, except for the last one
			if (i < grades.size() - 1) {
				result.append(", ");
			}
		}

		return result.toString();
	}

	public String getTeachersString() {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < teachers.size(); i++) {
			result.append(teachers.get(i));

			// Add a comma after each element, except for the last one
			if (i < teachers.size() - 1) {
				result.append(", ");
			}
		}

		return result.toString();
	}

	public String toString() {
		String s = 	"Name: " + name + "\n" +
				"Teachers: " + teachers.toString().replaceAll("[\\[\\]]", "") + "\n" +
				"Grades: " + grades.toString().replaceAll("[\\[\\]]", "");
		if (group != null) {
			s += "\n" + "Group: " + group;
		}
		return s;
	}
}
