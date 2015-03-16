package Administration;

import java.util.ArrayList;

import javax.swing.AbstractListModel;

public class TeacherListModel extends AbstractListModel {

	/** */
	private ArrayList<String> teachers;

	/** keeps track of the teachers who have been added */
	private ArrayList<String> teachersAdded;

	/** keeps track of the teachers who have been deleted */
	private ArrayList<String> teachersDeleted;

	/**
	 * String consisting of "a" and "d"; used to keep track of the order in
	 * which teachers were added or deleted for saving to the database
	 */
	private String order;

	/**
	 * Creates a new instance of TeacherListModel
	 */
	public TeacherListModel() {
		// create data list
		teachers = new ArrayList<String>();

		// create change lists
		teachersAdded = new ArrayList<String>();
		teachersDeleted = new ArrayList<String>();
		order = "";
	}// TeacherListModel

	/**
	 * Adds the specified list as the initial list; does not add to the
	 * teachersAdded list.
	 * 
	 * @param names
	 *            initial list of teacher names/class lists
	 */
	public void addInitial(ArrayList<String> names) {
		if (names != null) {
			teachers.addAll(names);
		}
	}// addInitial(ArrayList)

	/**
	 * Adds the specified component to the list in alphabetical order.
	 * 
	 * @param teacherName
	 *            the teacher to be added
	 */
	public void addElement(String teacherName) {
		// used as escape from loop; true if the teacher has been added
		boolean isAdded = false;

		// iterate through our teachers
		int i = 0;
		for (; i < teachers.size() && !isAdded; i++) {
			// keep looping until we find a teacher whose name is "greater than"
			// the new teacher
			if (teacherName.compareTo(teachers.get(i)) < 0) {
				// set flag
				isAdded = true;

				// add the teacher at the current index
				teachers.add(i, teacherName);

				// update change status
				teachersAdded.add(teacherName);
				order += "a";
			}// if
		}// for

		// if the loop exits and no teacher has been added, we need to add it to
		// the end of the list
		if (!isAdded) {
			// add data to the end of the lists
			teachers.add(teacherName);
			// update change status
			teachersAdded.add(teacherName);
			order += "a";
		}// if

		// inform the list that my contents have changed
		fireContentsChanged(this, 0, i);
	}// addElement(String

	/**
	 * Returns the length of the list.
	 * 
	 * @return the length of the list
	 */
	public int getSize() {
		return teachers.size();
	}// getSize())

	/**
	 * Removes the element at the specified position in this list. Returns the
	 * element that was removed from the list.
	 * 
	 * @param index
	 *            the index of the element to removed
	 * 
	 * @return the removed element
	 */
	public String remove(int index) {
		String retVal = null;

		// only remove if the element at index is in bounds
		if (isInBounds(index)) {
			// remove the teacher
			retVal = teachers.remove(index);

			// update change status
			teachersDeleted.add(retVal);
			order += "d";

		}// if

		// inform the list that my contents have changed
		fireContentsChanged(this, index, index);

		return retVal;
	}// remove(int)

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
			// return the teacher at that index
			retVal = teachers.get(index);
		}// if

		return retVal;
	}// getElementAt(int)

	/**
	 * Returns the index of a teacher with name teacherName
	 * 
	 * @param teacherName
	 *            the name of the teacher to search for
	 * 
	 * @return the index of a teacher with name teacherName
	 */
	public int getIndexOf(String teacherName) {
		int index = 0;

		// search for teacherName
		for (; index < getSize(); index++) {
			// if found, break and return the index we were on
			if (getElementAt(index).equals(teacherName)) {
				break;
			}// if
		}// for
		return index;
	}// getHistory(Student)

	/**
	 * Returns the list of teachers who have been added.
	 * 
	 * @return the list of teachers who have been added
	 */
	public ArrayList<String> getAddList() {
		return teachersAdded;
	}// getAddList()

	/**
	 * Returns the list of teachers who have been removed.
	 * 
	 * @return the list of teachers who have been removed
	 */
	public ArrayList<String> getDeleteList() {
		return teachersDeleted;
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
		teachersAdded.clear();
		teachersDeleted.clear();
		order = "";
	}// clearChanges()

}// TeacherListModel
