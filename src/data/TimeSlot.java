// Ryan Scarbrough
// 11/24/23
// Basically my own time object to store data about each period, including the subjects taught at that time

package data;

import javafx.beans.property.SimpleIntegerProperty;
import java.util.ArrayList;

public class TimeSlot {
	private final SimpleIntegerProperty period;
	private final String startTime;
	private final String endTime;
	private ArrayList<Subject> subjects;

	public TimeSlot(int period, String startTime, String endTime) {
		this.period = new SimpleIntegerProperty(period);
		this.startTime = startTime;
		this.endTime = endTime;
		this.subjects = new ArrayList<>();
	}

	// Getters
	public int getPeriod() {
		return period.get();
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public ArrayList<Subject> getSubjects() {
		return subjects;
	}

	// Add/remove methods
	public void clearSubjects() {
		this.subjects = new ArrayList<>();
	}

	public void addSubject(Subject s) {
		subjects.add(s);
	}

	public void removeSubject(Subject s) {
		subjects.remove(s);
	}

	// Query methods to see if things are available or valid
	public boolean teachersAvailable(ArrayList<Teacher> tchrs) {
		for (Subject s : subjects) {
			for (Teacher t : tchrs) {
				if (s.getTeachers().contains(t)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean gradesAvailable(ArrayList<Grade> grds) {
		for (Subject s : subjects) {
			for (Grade g : grds) {
				if (s.getGrades().contains(g)) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean containsSubject(Subject s) {
		if (subjects == null)
			return false;
		else
			return subjects.contains(s);
	}

	public boolean validSubject(Subject s) {
		return !this.containsSubject(s) && this.teachersAvailable(s.getTeachers()) && this.gradesAvailable(s.getGrades());
	}

	public void updateSubject(Subject oldSubject, Subject newSubject) {
		for (int i = 0; i < subjects.size(); i++) {
			if (subjects.get(i) == oldSubject) {
				System.out.println("done");
				subjects.set(i, newSubject);
				break;
			}
		}
	}

	public String toString() {
		String str  = "";
		str += period + " period; " + startTime + " -> " + endTime + "\n";
		return str;
	}
}
