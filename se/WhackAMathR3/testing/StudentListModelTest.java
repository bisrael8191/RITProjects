package testing;

import java.util.ArrayList;

import junit.framework.TestCase;
import Administration.StudentListModel;
import GameLogic.Student;

/**
 * JUnit test class for the StudentListModel class.
 * 
 * @author Eric M
 */
public class StudentListModelTest extends TestCase {

	// copied from StudentListModel to test for elements in the correct places
	private static final String SEPARATOR = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	private static final String INSTRUCTIONS = "Instructions";

	/**
	 * Main method for JUnit StudentListModelTest class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(StudentListModelTest.class);
	}

	/**
	 * Tests to make sure that the list data contains the correct number of
	 * elements after construction, adding an element, and removing an element
	 */
	public void testSize() {
		StudentListModel model = new StudentListModel();
		// should only have "Instructions" data in it
		assertEquals(model.getSize(), 1);

		model.addElement(new Student("S1", "C1"), null);
		// should have "Instructions", separator string, and student name
		assertEquals(model.getSize(), 3);

		model.remove(2);
		// should only have "Instructions" data in it
		assertEquals(model.getSize(), 1);
	}

	/**
	 * Tests to make sure that the list data is correct
	 */
	public void testContents() {
		StudentListModel model = new StudentListModel();
		model.addElement(new Student("S1", "C1"), null);

		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "S1");

		model.remove(2);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
	}

	/**
	 * Tests to make sure that the basic list data (INSTRUCTIONS and SEPARATOR)
	 * can't be removed from the list
	 */
	public void testValidRemove() {
		StudentListModel model = new StudentListModel();
		model.addElement(new Student("S1", "C1"), null);

		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "S1");

		model.remove(0);
		assertEquals(model.getSize(), 3);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "S1");

		model.remove(1);
		assertEquals(model.getSize(), 3);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "S1");
	}

	/**
	 * Tests the "isValidStudent" method
	 */
	public void testIsValidStudent() {
		StudentListModel model = new StudentListModel();
		model.addElement(new Student("S1", "C1"), null);
		// the only valid index at this point should be 2

		assertEquals(model.isValidStudent(0), false);
		assertEquals(model.isValidStudent(1), false);
		assertEquals(model.isValidStudent(2), true);
	}

	/**
	 * Tests the "clear" method
	 */
	public void testClear() {
		StudentListModel model = new StudentListModel();
		model.addElement(new Student("S1", "C1"), null);
		model.addElement(new Student("S2", "C1"), null);
		model.addElement(new Student("S3", "C1"), null);
		model.addElement(new Student("S4", "C1"), null);
		model.addElement(new Student("S5", "C1"), null);
		model.addElement(new Student("S6", "C1"), null);
		model.addElement(new Student("S7", "C1"), null);
		model.addElement(new Student("S8", "C1"), null);
		model.addElement(new Student("S9", "C1"), null);
		model.addElement(new Student("S0", "C1"), null);

		// size should be 12
		assertEquals(model.getSize(), 12);

		model.clear();

		// size should now be 1
		assertEquals(model.getSize(), 1);

	}

	/**
	 * Tests the "getStudentList" method
	 */
	public void testGetStudentList() {
		// create a bunch of students
		Student[] students = new Student[] { new Student("S1", "C1"),
				new Student("S2", "C2"), new Student("S3", "C3") };

		// add our students to the model
		StudentListModel model = new StudentListModel();
		for (Student s : students) {
			model.addElement(s, null);
		}

		// retrieve the list from the model
		ArrayList<Student> list = model.getStudentList();

		// test for the number of students to be the list size minus the static
		// strings (INSTRUCTIONS and SEPARATOR)
		assertEquals(list.size(), model.getSize() - 2);

		// test each element for equality to what it should be
		for (int i = 0; i < list.size(); i++) {
			assertEquals(list.get(i).getMyName(), students[i].getMyName());
		}
	}
}
