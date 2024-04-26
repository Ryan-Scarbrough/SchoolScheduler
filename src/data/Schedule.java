// Ryan Scarbrough
// 11/24/23
// Stores info about a given schedule

package data;

import java.util.ArrayList;

public class Schedule {
	private ArrayList<TimeSlot> timeslots;

	public Schedule() {
		timeslots = Data.getTimeslots();
	}

	public ArrayList<TimeSlot> getTimeslots() {
		return timeslots;
	}

	// Returns a subject given the grade and period (x and y coords of matrix)
	public Subject getSubject(int grade, int period) {
		for (TimeSlot timeslot : timeslots) {
			if (timeslot.getPeriod() == period) {
				for (Subject s : timeslot.getSubjects()) {
					for (Grade g : s.getGrades()) {
						if (g.getNumber() == grade) {
							return s;
						}
					}
				}
			}
		}
		ArrayList<Grade> tempGrades = new ArrayList<>();
		ArrayList<Teacher> tempTeachers = new ArrayList<>();
		return new Subject("", tempGrades.toArray(new Grade[0]), tempTeachers.toArray(new Teacher[0]), '\0');
	}

	public ArrayList<Subject> getSubjects() {
		ArrayList<Subject> sa = new ArrayList<>();
		for (TimeSlot ts : timeslots) {
			sa.addAll(ts.getSubjects());
		}
		return sa;
	}

	public void setSubject(Subject sub, int period) {
		for (TimeSlot timeslot : timeslots) {
			if (timeslot.getPeriod() == period) {
				if (!timeslot.containsSubject(sub)) {
					timeslot.addSubject(sub);
				}
			}
		}
	}

	public void removeSubject(Subject s) {
		for (TimeSlot ts : timeslots) {
			if (ts.containsSubject(s)) {
				ts.removeSubject(s);
			}
		}
	}

	public void removeSubject(int grade, int period) {
		for (TimeSlot ts : timeslots) {
			if (ts.getPeriod() == period) {
				ArrayList<Subject> temp = ts.getSubjects();
				for (Subject s : temp) {
					for (Grade g : s.getGrades()) {
						if (g.getNumber() == grade) {
							ts.removeSubject(s);
						}
					}
				}
			}
		}
	}

	public void setTimeslots(ArrayList<TimeSlot> timeslots) {
		this.timeslots = timeslots;
	}

	public Schedule copy() {
		Schedule newSchedule = new Schedule();
		ArrayList<TimeSlot> newTimeSlots = new ArrayList<>();
		for (TimeSlot originalTimeSlot : timeslots) {
			TimeSlot newTimeSlot = new TimeSlot(originalTimeSlot.getPeriod(), originalTimeSlot.getStartTime(), originalTimeSlot.getEndTime());
			for (Subject subject : originalTimeSlot.getSubjects()) {
				Subject newSubject = new Subject(subject.getName(), subject.getGrades().toArray(new Grade[0]), subject.getTeachers().toArray(new Teacher[0]), '\0');
				newTimeSlot.addSubject(newSubject);
			}
			newTimeSlots.add(newTimeSlot);
		}
		newSchedule.setTimeslots(newTimeSlots);
		return newSchedule;
	}

	public void clearTimeSlotSubjects() {
		for (TimeSlot ts : timeslots) {
			ts.clearSubjects();
		}
	}

	public String toString() {
		String s = "";
		for (TimeSlot ts : timeslots) {
			s += ts;
		}
		return s;
	}

	public boolean isEmpty() {
		for (TimeSlot ts : timeslots) {
			for (Subject s: ts.getSubjects()) {
				return false;
			}
		}
		return true;
	}
}
