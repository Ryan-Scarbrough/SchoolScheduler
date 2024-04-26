// Ryan Scarbrough
// 11/24/23
// Boring class to store data about grades

package data;

import javafx.beans.property.SimpleIntegerProperty;

public class Grade {
	private final SimpleIntegerProperty number;

	public Grade(int number) {
		this.number = new SimpleIntegerProperty(number);
	}

	public int getNumber() {
		return number.get();
	}

	public String toString() {
		return "" + number.get();
	}
}
