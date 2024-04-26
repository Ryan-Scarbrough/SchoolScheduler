// Ryan Scarbrough
// 04/25/24
// Constraint class to allow user to find tune schedule creation

package constraints;

import data.*;
import display.Display;

public class Constraint {
	public static Schedule constraints;

	public static void init() throws NullPointerException {
		Schedule s = Display.validSchedule.copy(); // exception can be thrown from here
		s.clearTimeSlotSubjects();
		constraints = s;
	}
}
