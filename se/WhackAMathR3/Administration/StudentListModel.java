package Administration;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

import GameLogic.GameHistory;
import GameLogic.Student;

//TODO: make me more abstract
/**
 * An implementation of AbstractListModel which sorts the data as it is added
 * 
 * @author Eric M
 */
public class StudentListModel extends AbstractListModel {

	/**
	 * ugly, but working; separator between questions types large enough to fill
	 * the screen horizontally; currently 200 characters long
	 */
	private static final String SEPARATOR = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	/** text for the instructions cell */
	private static final String INSTRUCTIONS = "Instructions";

	/** holds items at the top of the list */
	private ArrayList<String> staticStrings;

	/** holds actual student data */
	private ArrayList<Student> students;

	/** holds the game history for the students */
	private ArrayList<ArrayList<GameHistory>> history;

	// TODO find a better way to do this?
	/** keeps track of the students who have been added */
	private ArrayList<String> studentsAdded;

	/** keeps track of the students who have been deleted */
	private ArrayList<String> studentsDeleted;

	/**
	 * String consisting of "a" and "d"; used to keep track of the order in
	 * which students were added or deleted for saving to the database
	 */
	private String order;

	/**
	 * Creates a new instance of StudentListModel
	 */
	public StudentListModel() {
		// create staticStrings, defaulting with only the INSTRUCTIONS String
		// displayed
		staticStrings = new ArrayList<String>();
		staticStrings.add(INSTRUCTIONS);

		// create data lists
		students = new ArrayList<Student>();
		history = new ArrayList<ArrayList<GameHistory>>();

		// create change lists
		studentsAdded = new ArrayList<String>();
		studentsDeleted = new ArrayList<String>();
		order = "";
	}// SortingListModel()

	/**
	 * Adds the specified component to the list in alphabetical order.
	 * 
	 * @param student
	 *            the student to be added
	 * @param studentHist
	 *            the history to go along with student
	 */
	public void addElement(Student student, ArrayList<GameHistory> studentHist) {
		// get student name for comparison
		String studentName = student.getMyName();
		// used as escape from loop; true if the student has been added
		boolean isAdded = false;

		// if no data is being stored at this point, we need to add an initial
		// separator string to the staticStrings list
		if (getSize() == 1) {
			staticStrings.add(SEPARATOR);
		}

		// iterate through our students
		int i = 0;
		for (; i < students.size() && !isAdded; i++) {
			// keep looping until we find a student whose name is "greater than"
			// the new student
			if (studentName.compareTo(students.get(i).getMyName()) < 0) {
				// set flag
				isAdded = true;

				// add the student and history data at the current index
				students.add(i, student);
				history.add(i, studentHist);

				// update change status
				studentsAdded.add(studentName);
				order += "a";
			}// if
		}// for

		// if the loop exits and no student has been added, we need to add it to
		// the end of the list
		if (!isAdded) {
			// add data to the end of the lists
			students.add(student);
			history.add(studentHist);
			// update change status
			studentsAdded.add(studentName);
			order += "a";
		}// if

		// inform the list that my contents have changed
		fireContentsChanged(this, 0, i);
	}// addElement(Student,ArrayList<GameHistory>)

	/**
	 * Returns the length of the list.
	 * 
	 * @return the length of the list
	 */
	public int getSize() {
		return staticStrings.size() + students.size();
	}// getSize()

	/**
	 * Removes the element at the specified position in this list. Returns the
	 * element that was removed from the list.
	 * 
	 * @param index
	 *            the index of the element to removed
	 * 
	 * @return the removed element
	 */
	public Student remove(int index) {
		Student retVal = null;

		// only remove if the element at index is a Student
		if (isValidStudent(index)) {
			// change the index to account for the size of our staticStrings
			index -= staticStrings.size();
			// remove the student
			retVal = students.remove(index);
			// remove the game data
			history.remove(index);

			// update change status
			studentsDeleted.add(retVal.getMyName());
			order += "d";

			// if, after removal, the size is 2 it means the list consists only
			// of the INSTRUCTIONS and SEPARATOR Strings; remove the SEPARATOR
			if (getSize() == 2) {
				staticStrings.remove(SEPARATOR);
			}
		}// if

		// inform the list that my contents have changed
		fireContentsChanged(this, index, index);

		return retVal;
	}// remove(int)

	/**
	 * Returns true if the element at index is a valid student. true iff: the
	 * index is in bounds of the list size and the index is not a SEPARATOR or
	 * INSTRUCTIONS String
	 * 
	 * @param index
	 *            the index to check
	 * 
	 * @return true if the element at index is a question string
	 */
	public boolean isValidStudent(int index) {
		return isInBounds(index)
				&& !(getElementAt(index).equals(SEPARATOR) || getElementAt(
						index).equals(INSTRUCTIONS));
	}// isValidStudent(int)

	/**
	 * Returns true if index is within the bounds of our list
	 * 
	 * @param index
	 *            the index to check
	 * 
	 * @return true if index is within the bounds of our list
	 */
	private boolean isInBounds(int index) {
		return (index >= 0 && index < getSize());
	}// isInBounds(int)

	/**
	 * Removes all elements from this list.
	 */
	public void clear() {
		// until we're empty, keep removing elements
		while (getSize() != 1) {
			remove(getSize() - 1);
		}
	}// clear()

	/**
	 * Returns the value at the specified index.
	 * 
	 * @param index
	 *            the requested index
	 * 
	 * @return the value at index
	 */
	public String getElementAt(int index) {
		// default to empty string if index is out of bounds
		String retVal = "";

		// if index is in bounds of our list...
		if (isInBounds(index)) {
			// check to see which list index is in and return the String
			// representation of that element
			if (index < staticStrings.size()) {
				retVal = staticStrings.get(index);
			} else {
				retVal = students.get(index - staticStrings.size()).getMyName();
			}// if-else
		}// if

		return retVal;
	}// getElementAt(int)

	/**
	 * Returns an ArrayList of the Student objects held in this model
	 * 
	 * @return an ArrayList of the Student objects held in this model
	 */
	public ArrayList<Student> getStudentList() {
		return students;
	}// getStudentList()

	/**
	 * Returns the Student at index.
	 * 
	 * @param index
	 *            the index of the Student to return
	 * 
	 * @return the Student at index; null if index is invalid
	 * 
	 */
	public Student getStudent(int index) {
		Student retVal = null;

		// if element at index is valid, return the student at index
		if (isValidStudent(index)) {
			retVal = students.get(index - staticStrings.size());
		}// if

		return retVal;
	}// getStudent()

	/**
	 * Returns the index of a Student with name studentName
	 * 
	 * @param studentName
	 *            the name of the Student to search for
	 * 
	 * @return the index of a Student with name studentName
	 */
	public int getIndexOf(String studentName) {
		int index = 0;

		// search for studentName
		for (; index < getSize(); index++) {
			// if found, break and return the index we were on
			if (getElementAt(index).equals(studentName)) {
				break;
			}// if
		}// for
		return index;
	}// getHistory(Student)

	/**
	 * Returns the list of GameHistory objects associated with the student at
	 * index
	 * 
	 * @param index
	 *            the student index to get the history of
	 * 
	 * @return the list of GameHistory objects associated with the student at
	 *         index
	 */
	public ArrayList<GameHistory> getHistory(int index) {
		ArrayList<GameHistory> retVal = null;
		// if the index is valid return the history for that index,
		// else return null
		if (isValidStudent(index)) {
			retVal = history.get(index - staticStrings.size());
		}// if
		return retVal;
	}

	/**
	 * Returns the list of students who have been added.
	 * 
	 * @return the list of students who have been added
	 */
	public ArrayList<String> getAddList() {
		return studentsAdded;
	}// getAddList()

	/**
	 * Returns the list of students who have been removed.
	 * 
	 * @return the list of students who have been removed
	 */
	public ArrayList<String> getDeleteList() {
		return studentsDeleted;
	}// getDeleteList()

	/**
	 * Returns the String holding the order of add and delete operations.
	 * 
	 * @return the String holding the order of add and delete operations
	 */
	public String getOrder() {
		return order;
	}// getOrder()

	/**
	 * Resets the change state objects
	 */
	public void clearChanges() {
		studentsAdded.clear();
		studentsDeleted.clear();
		order = "";
	}// clearChanges()

}// SortingListModel
