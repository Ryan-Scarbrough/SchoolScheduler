// Ryan Scarbrough
// 11/15/23
// Stores all data to be accessed by other classes, making it more organized (kind of)

package data;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

public class Data {
	private static ArrayList<Teacher> teachers = new ArrayList<>();
	private static ArrayList<Subject> subjects = new ArrayList<>();
	private static ArrayList<TimeSlot> timeslots = new ArrayList<>();
	private static ArrayList<Grade> grades = new ArrayList<>();
	private static String lastExported = null;
	private static String lastImported = null;

	// Append methods
	public static void addTeacher(String name) {
		Teacher t = new Teacher(name);
		teachers.add(t);
	}

	public static void addGrade(int name) {
		Grade g = new Grade(name);
		grades.add(g);
	}

	public static void addTimeSlot(int period, String start, String end) {
		TimeSlot ts = new TimeSlot(period, start, end);
		timeslots.add(ts);
	}

	public static void addSubject(String name, int[] grades, String[] teachers) {
		addSubject(name, grades, teachers, '\0');
	}

	public static void addSubject(String name, int[] grades, String[] teachers, char group) {
		Grade[] tempGrades = Arrays.stream(grades).mapToObj(Data::_getGradeByNumber).toArray(Grade[]::new);
		Teacher[] tempTeachers = Arrays.stream(teachers).map(Data::_getTeacherByName).toArray(Teacher[]::new);

		Subject s = new Subject(name, tempGrades, tempTeachers, group);
		subjects.add(s);
	}

	public static void addSubject(Subject s) {
		subjects.add(s);
	}

	public static void addTimeSlot(TimeSlot ts) {
		timeslots.add(ts);
	}

	// Remove methods
	public static void removeGrade(Grade g) {
		grades.remove(g);
	}

	public static void removeTeacher(Teacher t) {
		teachers.remove(t);
	}

	public static void removeSubject(Subject s) {
		subjects.remove(s);
	}

	public static void removeTimeSlot(TimeSlot ts) {
		timeslots.remove(ts);
	}

	// Methods that return an object given their 'main' value
	private static Grade _getGradeByNumber(int n) {
		for (Grade g : grades) {
			if (g.getNumber() == n)
				return g;
		}
		return null;
	}

	private static Teacher _getTeacherByName(String name) {
		for (Teacher t : teachers) {
			if (t.getName().equals(name))
				return t;
		}
		return null;
	}

	// Getters
	public static ArrayList<Subject> getSubjects() {
		return subjects;
	}

	public static ArrayList<Grade> getGrades() {
		return grades;
	}

	public static ArrayList<TimeSlot> getTimeslots() {
		return timeslots;
	}

	public static ArrayList<Teacher> getTeachers() {
		return teachers;
	}

	public static int getFirstPeriod() {
		int smallestPeriod = Integer.MAX_VALUE;
		for (TimeSlot ts : timeslots) {
			if (ts.getPeriod() < smallestPeriod) {
				smallestPeriod = ts.getPeriod();
			}
		}
		return smallestPeriod;
	}

	public static int getLastPeriod() {
		int smallestPeriod = Integer.MIN_VALUE;
		for (TimeSlot ts : timeslots) {
			if (ts.getPeriod() > smallestPeriod) {
				smallestPeriod = ts.getPeriod();
			}
		}
		return smallestPeriod;
	}

	public static int getStartGrade() {
		int smallestGrade = Integer.MAX_VALUE;
		for (Grade g : grades) {
			if (g.getNumber() < smallestGrade) {
				smallestGrade = g.getNumber();
			}
		}
		return smallestGrade;
	}

	public static int getEndGrade() {
		int largestGrade = Integer.MIN_VALUE;
		for (Grade g : grades) {
			if (g.getNumber() > largestGrade) {
				largestGrade = g.getNumber();
			}
		}
		return largestGrade;
	}

	public static String getMasterString() {
		return getTeachers().toString() + getSubjects() + getTimeslots() + getGrades();
	}

	public static String getHash() {
		return hash(getMasterString());
	}

	public static ArrayList<Subject> getFreshSubjects() {
		return new ArrayList<>(subjects);
	}

	public static ArrayList<TimeSlot> getFreshTimeslots() {
		ArrayList<TimeSlot> ts = new ArrayList<>(timeslots);
		for (TimeSlot t : ts) {
			t.clearSubjects();
		}

		return ts;
	}

	public static String[] getSubjectNames() {
		String[] sa = new String[subjects.size()];
		for (int i = 0; i < sa.length; i++) {
			sa[i] = "[" + i + "] " + subjects.get(i).getName() + " {" + subjects.get(i).getTeachersString() + "}";
		}
		return sa;
	}

	// Imports data from a JSON file
	public static void importData(String path) throws IOException {
		String jsonContent = new String(Files.readAllBytes(Paths.get(path)));
		JSONObject jsonObject = new JSONObject(jsonContent);

		// Adding teachers to data
		JSONArray teachers = jsonObject.getJSONArray("Teachers");
		for (int i = 0; i < teachers.length(); i++) {
			JSONObject item = teachers.getJSONObject(i);
			String name = item.getString("name");
			addTeacher(name);
		}

		// Adding Grades to data
		JSONArray grades = jsonObject.getJSONArray("Grades");
		for (int i = 0; i < grades.length(); i++) {
			JSONObject item = grades.getJSONObject(i);
			int name = item.getInt("name");
			addGrade(name);
		}

		// Adding TimeSlots to data
		JSONArray timeslots = jsonObject.getJSONArray("TimeSlots");
		for (int i = 0; i < timeslots.length(); i++) {
			JSONObject item = timeslots.getJSONObject(i);
			int period = item.getInt("period");
			String start = item.getString("start");
			String end = item.getString("end");
			addTimeSlot(period, start, end);
		}

		// Adding Subjects to data
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonNode = objectMapper.readTree(jsonContent);
		JsonNode subjectsArray = jsonNode.get("Subjects");

		for (JsonNode subject : subjectsArray) {
			// Extract the "name" field
			String name = subject.get("name").asText();

			// Extract the "grade" array
			JsonNode gradeArray = subject.get("grade");
			ArrayList<Integer> gradeList = new ArrayList<>();
			for (JsonNode node : gradeArray) {
				gradeList.add(node.asInt());
			}

			// Extract the "teacher" array
			JsonNode teacherArray = subject.get("teacher");
			ArrayList<String> teacherList = new ArrayList<>();
			for (JsonNode node : teacherArray) {
				teacherList.add(node.asText());
			}
			String[] teacherArr = new String[teacherList.size()];
			teacherArr = teacherList.toArray(teacherArr);

			// Extract the "group" field if it exists
			String group;
			try {
				group = subject.get("group").asText();
			} catch (NullPointerException e) {
				group = null;
			}

			if (group != null) {
				addSubject(name, gradeList.stream().mapToInt(i -> i).toArray(), teacherArr, group.charAt(0));
			} else {
				addSubject(name, gradeList.stream().mapToInt(i -> i).toArray(), teacherArr);
			}
		}

		lastImported = getHash();
	}

	// Exports data to a JSON file
	public static void exportData(String path) throws IOException {
		JSONObject jsonObject = new JSONObject();

		// Export teachers
		JSONArray teachersArray = new JSONArray();
		for (Teacher teacher : teachers) {
			JSONObject teacherObj = new JSONObject();
			teacherObj.put("name", teacher.getName());
			teachersArray.put(teacherObj);
		}
		jsonObject.put("Teachers", teachersArray);

		// Export grades
		JSONArray gradesArray = new JSONArray();
		for (Grade grade : grades) {
			JSONObject gradeObj = new JSONObject();
			gradeObj.put("name", grade.getNumber());
			gradesArray.put(gradeObj);
		}
		jsonObject.put("Grades", gradesArray);

		// Export time slots
		JSONArray timeSlotsArray = new JSONArray();
		for (TimeSlot timeSlot : timeslots) {
			JSONObject timeSlotObj = new JSONObject();
			timeSlotObj.put("period", timeSlot.getPeriod());
			timeSlotObj.put("start", timeSlot.getStartTime());
			timeSlotObj.put("end", timeSlot.getEndTime());
			timeSlotsArray.put(timeSlotObj);
		}
		jsonObject.put("TimeSlots", timeSlotsArray);

		// Export subjects
		JSONArray subjectsArray = new JSONArray();
		for (Subject subject : subjects) {
			JSONObject subjectObj = new JSONObject();
			subjectObj.put("name", subject.getName());

			JSONArray gradeArray = new JSONArray();
			for (Grade grade : subject.getGrades()) {
				gradeArray.put(grade.getNumber());
			}
			subjectObj.put("grade", gradeArray);

			JSONArray teacherArray = new JSONArray();
			for (Teacher teacher : subject.getTeachers()) {
				teacherArray.put(teacher.getName());
			}
			subjectObj.put("teacher", teacherArray);

			if (subject.getGroup() != null && subject.getGroup() != '\0') {
				subjectObj.put("group", subject.getGroup());
			}

			subjectsArray.put(subjectObj);
		}
		jsonObject.put("Subjects", subjectsArray);

		// Write JSON to file
		try (FileWriter file = new FileWriter(path)) {
			file.write(jsonObject.toString(4));
		}

		lastExported = getHash();
	}

	// Reset all data
	public static void clear() {
		teachers = new ArrayList<>();
		subjects = new ArrayList<>();
		timeslots = new ArrayList<>();
		grades = new ArrayList<>();
	}

	// Hashes all stored data to allow for checking equality of all stored data at once more easy
	public static String hash(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = digest.digest(input.getBytes());
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) { // java being annoying
			e.printStackTrace();
			return null;
		}
	}

	// Just checks if there is actually data stored
	public static boolean isNull() {
		return teachers.isEmpty() && subjects.isEmpty() && timeslots.isEmpty() && grades.isEmpty();
	}

	// Used by the Display class to determine whether it should prompt the user to save before closing
	public static boolean shouldPrompt() {
		if (lastExported != null) {
			if (lastExported.equals(getHash())) {
				return false; // the current data is the same as exported (nothing changed since export)
			}
		}

		if (!isNull()) {
			if (lastImported == null) {
				return true; // we have not imported and data is not null, so something was added
			} else {
				// this if statement can be simplified, but it makes it easier to read like this
				if (lastImported.equals(getHash())) {
					return false; // we imported and nothing changed since we imported
				} else {
					return true; // we imported, but current data is different from imported
				}
			}
		}

		return false; // data is null
	}
}
