// Ryan Scarbrough
// 11/15/23
// The main part of the algorithm to find schedules for classes

package algorithm;

import constraints.Constraint;
import data.*;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

public class Algorithm {
	private static Set<Schedule> generatedSchedules = new HashSet<>();
	public static int scheduleIndex = -1;
	private static final int TIMEOUT_SECONDS = 1;
	private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private static final int MAX_RETRIES = 5;

	public static void importData(String path) throws IOException {
		Data.importData(path);
	}

	public static void exportData(String path) throws IOException {
		Data.exportData(path);
	}

	public static Set<Schedule> getSchedules() {
		return generatedSchedules;
	}

	// Calls _generateSchedule to find new schedules and adds them to list
	public static Schedule generateNewSchedule() {
		Schedule schedule;
		do {
			schedule = _generateSchedule(); // changed
		} while (generatedSchedules.contains(schedule));

		if (schedule != null) {
			Schedule tempS = schedule.copy();
			generatedSchedules.add(tempS);
		}

		if (scheduleIndex == -1) {
			scheduleIndex = 0;
		} else if (schedule != null) {
			scheduleIndex++;
		}

		return schedule;
	}

	// unused at the moment, causes more problems than it fixes
	private static Schedule _generateScheduleWithTimeout() {
		final Schedule[] result = {null}; // funky java shenanigans, dont know why this is required
		int retryCount = 0;

		while (retryCount < MAX_RETRIES && result[0] == null) {
			Runnable task = () -> {
				result[0] = _generateSchedule();
			};

			Future<?> future = executor.schedule(task, TIMEOUT_SECONDS, TimeUnit.SECONDS);
			try {
				future.get(TIMEOUT_SECONDS + 1, TimeUnit.SECONDS); // Wait for completion with a bit of extra time
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				// Timeout occurred, cancel the task
				future.cancel(true);
			}

			retryCount++;
		}

		return result[0];
	}

	// Jump starts recursive function, resets schedule values, and shuffles subjects
	private static Schedule _generateSchedule() {
		Schedule schedule = new Schedule();
		ArrayList<TimeSlot> timeslots = Data.getFreshTimeslots();
		ArrayList<Subject> subjects = Data.getFreshSubjects();

		if (Constraint.constraints != null) {
			Schedule copy = Constraint.constraints.copy();
			timeslots = copy.getTimeslots();
			subjects.removeAll(Constraint.constraints.getSubjects());
		}
		// Shuffle subjects to introduce randomness, allowing new schedules to be generated
		Collections.shuffle(subjects);

		boolean success = _assignSubjects(schedule, timeslots, subjects, 0);
		if (!success) {
			return null;
		}

		schedule.setTimeslots(timeslots);
		return schedule;
	}

	private static boolean _assignSubjects(Schedule schedule, ArrayList<TimeSlot> timeslots, ArrayList<Subject> subjects, int subjectIndex) {
		// Base case: If all subjects are assigned, return true
		if (subjectIndex >= subjects.size()) {
			return true;
		}

		Subject currentSubject = subjects.get(subjectIndex);

		// Iterate through each time slot
		for (TimeSlot timeSlot : timeslots) {
			if (timeSlot.validSubject(currentSubject)) {
				timeSlot.addSubject(currentSubject);

				// Recursive call to assign the next subject
				boolean success = _assignSubjects(schedule, timeslots, subjects, subjectIndex + 1);

				// If the recursive call succeeds, return true
				if (success) {
					return true;
				}

				// If the recursive call fails, backtrack by removing the subject assignment
				timeSlot.removeSubject(currentSubject);
			}
		}

		// If no time slot can accommodate the subject, return false
		return false;
	}

	// Allows cycling to the next schedule from the view menu
	public static Schedule nextSchedule() {
		if (scheduleIndex+2 > generatedSchedules.size()) {
			return generateNewSchedule();
		}
		scheduleIndex++;
		List<Schedule> list = new ArrayList<>(getSchedules());
		Schedule s = list.get(scheduleIndex);
		return s;
	}

	// Cycles to the previous schedule
	public static Schedule prevSchedule() {
		if (scheduleIndex-1 < 0) {
			return null;
		}
		scheduleIndex--;
		List<Schedule> list = new ArrayList<>(getSchedules());
		Schedule s = list.get(scheduleIndex);
		return s;
	}

	public static void clearSchedules() {
		generatedSchedules = new HashSet<>();
	}

}
