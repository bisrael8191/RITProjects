package Administration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;

import GameUI.GameWindow;

public class TeacherOptionTab extends AdminOptionTab implements ActionListener {

	// Constants
	// button text
	private static final String ADD_TEACHER_TXT = "Add New Teacher";

	private static final String DEL_TEACHER_TXT = "Delete Teacher";

	private static final String DEF_TEACHER_TXT = "Set as Default";

	/** The instructions for the teacher tab */
	private static final String INSTRUCTIONS = "<html>"
			+ "To add a new teacher:<br>"
			+ "1) Click the \""
			+ ADD_TEACHER_TXT
			+ "\" button below.<br>"
			+ "2) Enter his or her name in the field provided.<br>"
			+ "3) Click the \"Save\" button to save changes.<br><br>"
			+ "To remove a teacher:<br>"
			+ "1) Select the teacher's name in the list.<br>"
			+ "2) Click the \""
			+ DEL_TEACHER_TXT
			+ "\" button below.<br>"
			+ "3) Click the \"Save\" button to save changes.<br><br>"
			+ "To change the default class (the one which loads on startup):<br>"
			+ "1) Select the teacher's name in the list.<br>"
			+ "2) Click the \"" + DEF_TEACHER_TXT + "\" button below.<br>"
			+ "3) Click the \"Save\" button to save changes.<br><br>"
			+ "</html>";

	/** JList header */
	private static final String TEACHERS = "Teachers";

	// Components
	/** holds the teacher names */
	private TeacherListModel dataModel;

	/** reference kept for enabling/disabling */
	private JButton deleteTeacherButton;

	/** reference kept for enabling/disabling */
	private JButton defaultTeacherButton;

	/** keep a copy of the default class for saving */
	private String defaultClass;

	/**
	 * Creates a new instance of TeacherOptionTab
	 * 
	 * @param tabPane
	 *            the parent tabbed pane of this tab
	 */
	public TeacherOptionTab(String user, AdministrationTabbedPane tabPane) {
		super(user, tabPane);

		defaultClass = tabPane.getDefaultClass();
		init();
	}// TeacherOptionTab()

	/**
	 * Set up stuff specific to this tab
	 */
	private void init() {

		// set the instruction label
		setInstructions(INSTRUCTIONS);

		// get list of teachers from the internet
		getTeachers();

		// set the jlist title text
		setTitleText(TEACHERS);

		// create buttons
		JButton addTeacherButton = new JButton(ADD_TEACHER_TXT);
		deleteTeacherButton = new JButton(DEL_TEACHER_TXT);
		defaultTeacherButton = new JButton(DEF_TEACHER_TXT);
		// listen for clicks
		addTeacherButton.addActionListener(this);
		deleteTeacherButton.addActionListener(this);
		defaultTeacherButton.addActionListener(this);
		// add buttons to the button panel
		addButton(addTeacherButton);
		addButton(deleteTeacherButton);
		addButton(defaultTeacherButton);

		// start off with the first index selected
		setSelectedIndex(0);

	}// init()

	/**
	 * Loads teacher data from the database into the dataModel
	 */
	private void getTeachers() {
		// get the class names from the database (same as teacher names)
		ArrayList<String> classNames = dbc.getClassNames();
		// load the names as the initial list
		dataModel.addInitial(classNames);
		// load each one into the data model
		// if (classNames != null) {
		// for (String s : classNames) {
		// dataModel.addElement(s);
		// }// for
		// }// if
	}// getTeachers

	/**
	 * Asks for input of a teacher name; if valid, adds it to the data model
	 */
	private void addTeacher() {
		// use a dialog popup to get the user input
		String newName = JOptionPane.showInputDialog(this,
				"Enter new teacher (class) name", "New Teacher",
				JOptionPane.QUESTION_MESSAGE);

		// if the user didn't click cancel, and if the name is not the empty
		// string, add the new tracher to the data model
		if (newName != null && !newName.equals("")) {
			dataModel.addElement(newName);
			// set the new index as selected
			setSelectedIndex(dataModel.getIndexOf(newName));
		}// if

	}// addTeacher

	/**
	 * Removes the element at index from the dataModel
	 * 
	 * @param index
	 *            index of the student to remove
	 */
	private void removeItemAt(int index) {
		// remove the student
		dataModel.remove(index);

		// if the element removed was the last element in the list, reset the
		// selected index to be the new last element
		if (index >= dataModel.getSize() - 1) {
			setSelectedIndex(dataModel.getSize() - 1);
		}// if

		// make sure to inform the list of the changes
		valueChanged(new ListSelectionEvent(getDataList(), index, dataModel
				.getSize(), false));
	}// removeItemAt(int)

	/**
	 * Actually saves data to the database
	 */
	public void saveData() {
		// TEACHER NAME SAVE
		ArrayList<String> data = new ArrayList<String>();

		for (int i = 0; i < dataModel.getSize(); i++) {
			data.add((String) dataModel.getElementAt(i));
		}// for

		// put the class names in the database
		dbc.putClassNames(data);

		// now get our change data from the dataModel to send in the correct
		// order to the database
		ArrayList<String> addList = dataModel.getAddList();
		ArrayList<String> delList = dataModel.getDeleteList();
		String order = dataModel.getOrder();

		// number of adds and deletes processed so far
		int a = 0;
		int d = 0;

		String current = null;

		// iterate through the order string
		for (int i = 0; i < order.length(); i++) {
			// if the current character is an 'a'
			if (order.charAt(i) == 'a') {
				// get the next teacher in the add list
				current = dataModel.getElementAt(dataModel.getIndexOf(addList
						.get(a)));

				// if that teacher is not null
				// (if it wasn't removed after being added)
				if (current != null) {
					// add the teacher to the database
					dbc.addTeacher(current, new MD5Hash().getHash(current));
				}// if

				// increment the add counter
				a++;
			} else if (order.charAt(i) == 'd') {
				// if the current character is a 'd'

				// get the next student in the delete list
				// current = dataModel.getElementAt(dataModel.getIndexOf(delList
				// .get(d)));

				// if that teacher isn't null
				// if (current != null) {
				// remove the teacher from the database
				dbc.deleteClass(delList.get(d));
				// }// if

				// increment the delete counter
				d++;
			}// if-else
		}// for

		// no matter what is added or deleted, we now want to clear any changes
		// that have been saved
		dataModel.clearChanges();

		// DEFAULT CLASS SAVE
		// the configuration file
		File configFile = new File("config.ini");
		// only modify if the file exists
		if (configFile.exists()) {
			try {
				// read in the specified ip address so we can rewrite it later
				BufferedReader in = new BufferedReader(new FileReader(
						configFile));
				in.readLine();
				String ipAddress = in.readLine();
				in.close();

				// create an output stream
				PrintWriter fout = new PrintWriter(new FileOutputStream(
						configFile));

				// write our data
				fout.println(defaultClass);
				fout.println(ipAddress);
				fout.flush();
				fout.close();
			} catch (IOException e) {
				e.printStackTrace();
			}// try-catch

		}// if

		// tell the tabbed pane we have no changes saved
		tabPane.setSaveEnabled(false);

	}// saveData()

	// ActionListener methods
	/**
	 * Called when a button is pressed; performs the correct operations for
	 * each.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void actionPerformed(ActionEvent ev) {
		// button identity is found by comparing the text on the button
		String jbText = ((JButton) ev.getSource()).getText();

		if (jbText.equals(ADD_TEACHER_TXT)) {
			addTeacher();
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		} else if (jbText.equals(DEL_TEACHER_TXT)) {
			// ask for confirmation of deleting a teacher before deleting
			if (JOptionPane.showConfirmDialog(GameWindow.getInstance(),
					"<html>Deleting a class will delete the profiles "
							+ "for all students in that class.<br>Are you "
							+ "sure you want to proceed?</html>",
					"Are you sure?", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE) == 0) {

				// flag to see if we're deleting the default teacher
				boolean defaultDeleted = !defaultTeacherButton.isEnabled();
				// remove name from the list
				removeItemAt(getDataList().getSelectedIndex());

				// if the default was deleted, reset the default to the 0th
				// element
				if (defaultDeleted) {
					defaultClass = (dataModel.getSize() != 0) ? dataModel
							.getElementAt(0) : "Admin";
				}// if

				// tell the tabbed pane we've made changes
				tabPane.setSaveEnabled(true);
			}// if
		} else if (jbText.equals(DEF_TEACHER_TXT)) {
			// store the selected element as the default
			defaultClass = (String) getDataList().getSelectedValue();
			// false because the default teacher is selected
			defaultTeacherButton.setEnabled(false);
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		}// if-else
	}// actionPerformed(ActionEvent)

	// ComponentListener methods
	/**
	 * Called when rightBackPanel is resized; we want to resize rightPanel to
	 * completely fill it.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void componentResized(ComponentEvent ev) {
		// resize our rightPanel
		rightPanel.setSize(rightBackPanel.getSize());
		super.componentResized(ev);
	}// componentResized(ComponentEvent)

	/**
	 * Used by AdminOptionTab to force subclasses to create a ListModel for
	 * dataList and return a reference to it; needed to properly construct the
	 * components.
	 * 
	 * @return ListModel the list model used by dataList
	 */
	protected ListModel createListModel() {
		dataModel = new TeacherListModel();
		return dataModel;
	}// createListModel()

	// ListSelection methods
	/**
	 * Called when the dataList selected value has changed.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void valueChanged(ListSelectionEvent ev) {
		// only enable the delete button if a teacher is selected
		// -this is guaranteed to be true if the list model is not empty;
		// the normal getSelectedIndex != -1 wasn't working
		deleteTeacherButton.setEnabled(dataModel.getSize() > 0
						&& !getDataList().getSelectedValue().equals(user) );
		// only enable the default button if a teacher is selected, and that
		// teacher isn't the current default
		defaultTeacherButton.setEnabled( ( dataModel.getSize() > 0)
				&& !getDataList().getSelectedValue().equals(defaultClass) );
				
	}// valueChanged(ListSelectionEvent)
}// TeacherOptionTab
