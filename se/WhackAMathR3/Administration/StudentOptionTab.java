package Administration;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import GameLogic.GameHistory;
import GameLogic.Student;
import GameUI.PrintUtility;

/**
 * Allows the user to view student histories and add and remove students from
 * the database.
 * 
 * @author Eric M
 */
public class StudentOptionTab extends AdminOptionTab implements ActionListener,
		AdjustmentListener {

	// button text
	private static final String PRINT_TXT = "Print";

	private static final String ADD_STUDENT_TXT = "Add Student";

	private static final String DELETE_STUDENT_TXT = "Delete Student";

	/** The full instructions for the student tab */
	private static final String FULL_INSTR = "<html>"
			+ "To view a student's progress:<br>"
			+ "1) Select the student's name in the list to view his"
			+ " or her profile.<br><br>To add a new student:<br>"
			+ "1) Click the \"" + ADD_STUDENT_TXT + "\" button below.<br>"
			+ "2) Enter his or her name in the field provided. "
			+ "A new entry will be made for the new student.<br>"
			+ "3) Click the \"Save\" button to save changes.<br><br>"
			+ "To remove a student:<br>"
			+ "1) Select the student's name in the list.<br>"
			+ "2) Click the \"" + DELETE_STUDENT_TXT + "\" button below.<br>"
			+ "3) Click the \"Save\" button to save changes.<br><br>"
			+ "To print a student's profile:<br>"
			+ "1) Select the student's name in the list.<br>"
			+ "2) Click the \"" + PRINT_TXT + "\" button below.<br>"
			+ "</html>";

	/** JList header */
	private static final String STUDENTS = "Students";

	/** Static labels displaying data titles */
	private static final JLabel[] studentDataLabels = new JLabel[] {
			new JLabel("Student name: "),
			new JLabel("Total number of games played: "),
			new JLabel("Total number of questions attempted: "),
			new JLabel("Total number of questions answered correctly: "),
			new JLabel("Percent correct: "),
			new JLabel("Total number correct on first try: "),
			new JLabel("Cumulative score: "),
			new JLabel("Addition/Subtraction game level: "),
			new JLabel("Multiplication/Division game level: "),
			new JLabel("Position choosing game level: "), new JLabel(),
			new JLabel("-Game History-") };

	/** Dynamic labels holding the selected student information */
	private JLabel[] studentData;

	// private ArrayList<JLabel> studentHistory;

	/** Student game histories will be loaded this for display */
	private JTable historyTable;

	/** */
	private DefaultTableModel tableModel;

	/** actually holds the student data */
	private StudentListModel dataModel;

	/** reference kept for enabling/disabling */
	private JButton printButton;

	/** reference kept for enabling/disabling */
	private JButton deleteStudentButton;

	/** */
	private String selectedItem;

	/**
	 * Creates a new instance of StudentOptionTab and instantiates components
	 * 
	 * @param user
	 *            the admin user logged into this session
	 * @param tabPane
	 *            the parent tabbed pane of this tab
	 */
	public StudentOptionTab(String user, AdministrationTabbedPane tabPane) {
		super(user, tabPane);

		// create data holders
		// studentHistory = new ArrayList<JLabel>();

		// make new anonymous DefaultTableModel with overridden isCellEditable
		// method to disallow editing of any cell
		tableModel = new DefaultTableModel() {
			public boolean isCellEditable(int row, int col) {
				return false;
			}
		};

		historyTable = new JTable(tableModel);
		selectedItem = "";

		// set up components
		init();

	}// StudentOptionTab()

	/**
	 * Set up stuff specific to this tab
	 */
	protected void init() {

		// Set the instruction label
		setInstructions(FULL_INSTR);

		// setup student display components
		studentData = new JLabel[studentDataLabels.length];

		// static sizes of and distance between components
		int labelWidth = 300;
		int labelHeight = 30;
		int pad = 5;

		// position each data label on the rightPanel
		for (int i = 0; i < studentDataLabels.length; i++) {
			// set each to the same size
			studentDataLabels[i].setSize(labelWidth, labelHeight);
			// the x location of each is the same,
			// the y location is based off of the one above it
			studentDataLabels[i].setLocation(pad, i * labelHeight);
			// initially not visible
			studentDataLabels[i].setVisible(false);
			// add it to the rightPanel
			rightPanel.add(studentDataLabels[i]);

			// set up the dynamic studentData labels next to the current
			// studentDataLabels label
			studentData[i] = new JLabel("");
			studentData[i].setSize(labelWidth, labelHeight);
			studentData[i].setLocation(studentDataLabels[i].getX()
					+ studentDataLabels[i].getWidth(), studentDataLabels[i]
					.getY());
			studentData[i].setVisible(false);
			rightPanel.add(studentData[i]);
		}// for

		// center history text
		studentDataLabels[studentDataLabels.length - 1]
				.setHorizontalAlignment(JLabel.CENTER);

		// Load student data from the database
		getStudents();

		// set the jlist title text
		setTitleText(STUDENTS);

		// set up the scroll bar
		scrollBar.setMinimum(0);
		scrollBar.setVisible(true);
		scrollBar.setEnabled(false);
		scrollBar.addAdjustmentListener(this);

		// setup the history table
		// column names
		String[] columnHeaders = { "Date", "Game Type", "Score", "# Correct",
				"# Attempted" };

		// adding column headers to the table
		for (String header : columnHeaders) {
			tableModel.addColumn(header);
		}// for

		// only able to select one item at a time
		historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// auto resize mode
		historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		// don't let the user switch the order of columns
		historyTable.getTableHeader().setReorderingAllowed(false);
		// size and position on screen will be set in a listener
		// put table on the right panel
		rightPanel.add(historyTable.getTableHeader());
		rightPanel.add(historyTable);

		// Create buttons
		printButton = new JButton(PRINT_TXT);
		JButton addStudentButton = new JButton(ADD_STUDENT_TXT);
		deleteStudentButton = new JButton(DELETE_STUDENT_TXT);
		// Listen for button clicks
		printButton.addActionListener(this);
		addStudentButton.addActionListener(this);
		deleteStudentButton.addActionListener(this);

		// Add buttons to the buttonPanel
		addButton(addStudentButton);
		addButton(deleteStudentButton);
		addButton(printButton);

		// start off with the instructions showing
		setSelectedIndex(0);

	}// init()

	/**
	 * Loads student data from the database into the dataModel
	 */
	private void getStudents() {
		// get the class list from the database
		ArrayList<String> classList = dbc.getClassList(user);
		// hist will be used to retrieve the student history for each student
		ArrayList<GameHistory> hist = null;

		// if the class list is null, don't do anything
		if (classList != null) {
			// iterate through our list
			for (String studentName : classList) {
				// get the history for this student
				hist = dbc.getStudentHistory(user, studentName);

				// add the student and their history to the data model
				dataModel.addElement(dbc.getStudentSummary(user, studentName),
						hist);
			}// for
		}// if

	}// getStudents()

	/**
	 * Prints the contents of the rightPanel
	 */
	private void print() {
		PrintUtility.printComponent(rightPanel);
	}// print()

	/**
	 * Asks for input of a student name; if valid, adds it to the data model
	 */
	private void addStudent() {
		// use a dialog popup to get the user input
		String newName = JOptionPane.showInputDialog(this,
				"Enter new student name", "New Student",
				JOptionPane.QUESTION_MESSAGE);

		Student newStudent;
		// if the user didn't click cancel, and if the name is not the empty
		// string, add the new student to the data model
		if (newName != null && !newName.equals("")) {
			// create a new student
			newStudent = new Student(newName, user);
			// add the new student with a null history
			dataModel.addElement(newStudent, null);
		}// if

	}// addStudent

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
	 * Loads the data of the student with studentName into the data labels
	 * 
	 * @param studentName
	 *            the student data to display
	 */
	private void loadStudentData(String studentName) {
		// if the name is empty, set all the labels to be empty;
		// else load the data
		if (studentName.equals("")) {
			for (JLabel l : studentData) {
				l.setText("");
			}
		} else {
			// grab student data from the dataModel
			Student s = dataModel.getStudent(dataModel.getIndexOf(studentName));

			// load it into the labels
			studentData[0].setText(s.getMyName());
			studentData[1].setText(String.valueOf(s.getNumGames()));
			studentData[2].setText(String.valueOf(s.getTotalQuestions()));
			studentData[3].setText(String.valueOf(s.getNumCorrect()));
			studentData[4].setText((int) s.getPercentCorrect() + "%");
			studentData[5].setText(String.valueOf(s.getNumCorrectFirst()));
			studentData[6].setText(String.valueOf(s.getScore()));
			studentData[7].setText(String.valueOf(s.getLevel(0)));
			studentData[8].setText(String.valueOf(s.getLevel(1)));
			studentData[9].setText(String.valueOf(s.getLevel(2)));

			// now we lay out the student game history labels
			// int pad = 5;
			// JLabel temp = null;
			// all labels will have the same size as the first data display
			// label and will have their locations based off of the last
			// display label
			// Dimension labelSize = studentDataLabels[0].getSize();
			// Point labelLoc = studentDataLabels[studentDataLabels.length - 1]
			// .getLocation();

			// remove whatever history is currently displayed
			while (tableModel.getRowCount() != 0) {
				tableModel.removeRow(0);
			}// while

			// for (JLabel l : studentHistory) {
			// rightPanel.remove(l);
			// }
			// studentHistory.clear();

			// load the student history into an array list so we can load
			// them into labels
			ArrayList<GameHistory> hist = dataModel.getHistory(dataModel
					.getIndexOf(studentName));
			// keep track of the row we're adding to
			int currentRow = 0;
			// if the history exists...
			if (hist != null) {
				// iterate through the history list
				for (GameHistory h : hist) {
					// add a new row of data
					tableModel.addRow(new String[] { null, null, null, null,
							null });

					// set the data in the row:
					// date, type, score, num, total
					tableModel.setValueAt(h.getDateString(), currentRow, 0);
					tableModel.setValueAt(h.getGameTypeString(), currentRow, 1);
					tableModel.setValueAt(h.getScore(), currentRow, 2);
					tableModel.setValueAt(h.getQuestionsCorrect(), currentRow,
							3);
					tableModel.setValueAt(h.getQuestionsTotal(), currentRow, 4);

					currentRow++;

					// for each entry create a new label
					// temp = new JLabel();

					// set its location to "just under" the last location
					// temp.setLocation((int) labelLoc.getX(), (int) (labelLoc
					// .getY()
					// + labelSize.getHeight() + pad));
					// update the last location
					// labelLoc = temp.getLocation();

					// set the current label text to the current
					// GameHistory object
					// temp.setText(h.toString());
					// resize to be the same size as all other labels
					// temp.setSize(labelSize);

					// store the label reference so it can be removed later
					// studentHistory.add(temp);
					// add the label to the rightPanel
					// rightPanel.add(temp);
				}// for
			}// if

			// if there is no history for this student just display a single
			// label with "<none>" in it
			// if (hist == null) {
			// temp = new JLabel("<none>");
			// temp.setSize(labelSize);
			// temp.setLocation((int) labelLoc.getX(), (int) (labelLoc.getY()
			// + labelSize.getHeight() + pad));
			//
			// studentHistory.add(temp);
			// rightPanel.add(temp);
			// }// if

			// if temp was created - should always happen now - set the
			// rightPanel height to be able to hold all the history labels

			// set the rightPanel height to be able to hold the whole table
			// if (temp != null) {
			// rightPanel.setSize(rightPanel.getWidth(), Math.max(temp.getY()
			// + temp.getHeight() + pad, rightBackPanel.getHeight()));

			int tableHeight = (historyTable.getRowCount() + 1)
					* historyTable.getRowHeight();

			rightPanel.setSize(rightPanel.getWidth(), Math.max(historyTable
					.getY()
					+ tableHeight, rightBackPanel.getHeight()));

			// if necessary, enable the scroll bar
			scrollBar.setEnabled(rightPanel.getHeight() > rightBackPanel
					.getHeight());
			// set the scroll bar max value
			scrollBar.setMaximum(rightPanel.getHeight()
					- rightBackPanel.getHeight());
			scrollBar.setUnitIncrement(scrollBar.getMaximum() / 10);
			scrollBar.setBlockIncrement(scrollBar.getMaximum() / 3);
			// }// if

			// repaint me to keep up to date
			rightPanel.repaint();
		}// if-else

	}// loadStudentData(String)

	/**
	 * Actually saves data to the database
	 */
	public void saveData() {
		// student data must be stored as an ArrayList of Strings,
		// so that's how we're going to extract it
		ArrayList<String> classNames = new ArrayList<String>();

		// build the class list
		for (Student s : dataModel.getStudentList()) {
			classNames.add(s.getMyName());
		}// for

		// put the list in the database
		dbc.putClassList(user, classNames);

		// now get our change data from the dataModel to send in the correct
		// order to the database
		ArrayList<String> addList = dataModel.getAddList();
		ArrayList<String> delList = dataModel.getDeleteList();
		String order = dataModel.getOrder();

		// number of adds and deletes processed so far
		int a = 0;
		int d = 0;

		Student s = null;

		// iterate through the order string
		for (int i = 0; i < order.length(); i++) {
			// if the current character is an 'a'
			if (order.charAt(i) == 'a') {
				// get the next student in the add list
				s = dataModel.getStudent(dataModel.getIndexOf(addList.get(a)));

				// if that student is not null
				// (if it wasn't removed after being added)
				if (s != null) {
					// add the student to the database
					dbc.putStudentSummary(user, addList.get(a), dataModel
							.getStudent(dataModel.getIndexOf(addList.get(a))));
				}// if

				// increment the add counter
				a++;
			} else if (order.charAt(i) == 'd') {
				// if the current character is a 'd'

				dbc.deleteStudent(user, delList.get(d));

				// get the next student in the delete list
				// s =
				// dataModel.getStudent(dataModel.getIndexOf(delList.get(d)));

				// if that student isn't null
				// (should always be true?)
				// if (s != null) {
				// remove the student from the database
				// dbc.deleteStudent(user, delList.get(d));
				// }// if

				// increment the delete counter
				d++;
			}// if-else
		}// for

		// no matter what is added or deleted, we now want to clear any changes
		// that have been saved
		dataModel.clearChanges();
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

		if (jbText.equals(PRINT_TXT)) {
			// print the right panel
			print();
		} else if (jbText.equals(ADD_STUDENT_TXT)) {
			// add a new student
			addStudent();
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		} else if (jbText.equals(DELETE_STUDENT_TXT)) {
			// delete the selected student
			removeItemAt(getDataList().getSelectedIndex());
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		}// if-else

	}// actionPerformed(ActionEvent)

	// AdjustmentListener methods
	/**
	 * Handles the scrolling of the rightPanel when student history data runs
	 * off screen
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void adjustmentValueChanged(AdjustmentEvent ev) {
		// since the scrollbar max size is set to the amount rightPanel can
		// scroll, all we need to do is set it's location to the negative of the
		// scrollbar value (scrolls up)

		rightPanel.setLocation(rightPanel.getX(), -ev.getValue());
	}// adjustmentValueChanged(AdjustmentEvent)

	// ListSelection methods
	/**
	 * Called when the dataList selected value has changed. Loads the selected
	 * student data onto the rightPanel and handles component visibility.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void valueChanged(ListSelectionEvent ev) {
		// when a new item is selected, set the scrollbar to initially be
		// "unscrolled" and disabled
		scrollBar.setValue(0);
		// scrollBar.setEnabled(false);

		// get the source and selectedIndex for easy reference
		JList source = (JList) ev.getSource();
		int selectedIndex = source.getSelectedIndex();

		if (!selectedItem.equals(dataModel.getElementAt(selectedIndex))) {
			selectedItem = dataModel.getElementAt(selectedIndex);
			// if the selected element is a student...
			if (dataModel.isValidStudent(selectedIndex)) {
				// grab the selected student name
				String studentName = dataModel.getElementAt(selectedIndex);

				// load the selected student data
				loadStudentData(studentName);

				// enable the print and delete buttons
				deleteStudentButton.setEnabled(true);
				printButton.setEnabled(true);

				// invisibilize the instructions and show the other data labels
				setInstrVisible(false);
				for (JLabel l : studentDataLabels) {
					l.setVisible(true);
				}// for
				for (JLabel l : studentData) {
					l.setVisible(true);
				}// for
				historyTable.setVisible(true);
				historyTable.getTableHeader().setVisible(true);
				// for (JLabel l : studentHistory) {
				// l.setVisible(true);
				// }// for

			} else {
				// if the selected element isn't a student...

				// disable the print and delete buttons
				deleteStudentButton.setEnabled(false);
				printButton.setEnabled(false);

				// show the instructions and hide the other data labels
				setInstrVisible(true);
				for (JLabel l : studentDataLabels) {
					l.setVisible(false);
				}// for
				for (JLabel l : studentData) {
					l.setVisible(false);
				}// for
				historyTable.setVisible(false);
				historyTable.getTableHeader().setVisible(false);
				// for (JLabel l : studentHistory) {
				// l.setVisible(false);
				// }// for
			}// if-else
		}// if

		// force the right panel to lay itself out again
		componentResized(new ComponentEvent(rightBackPanel,
				ComponentEvent.COMPONENT_RESIZED));
	}// valueChanged(ListSelectionEvent)

	// ComponentListener methods
	/**
	 * Called when rightBackPanel is resized; if an invalid element is selected,
	 * we will resize rightPanel to it's parent panel. Otherwise we want to let
	 * the loadStudentData method size it
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void componentResized(ComponentEvent ev) {
		// resize if we can
		if (!dataModel.isValidStudent(getDataList().getSelectedIndex())) {
			rightPanel.setSize(rightBackPanel.getSize());
		} else {
			int tableHeight = (historyTable.getRowCount() + 1)
					* historyTable.getRowHeight();

			rightPanel.setSize(rightPanel.getWidth(), Math.max(historyTable
					.getY()
					+ tableHeight, rightBackPanel.getHeight()));
		} // if

		// position history table
		JLabel lastLabel = studentDataLabels[studentDataLabels.length - 1];

		// have to position this separately for some reason
		JTableHeader h = historyTable.getTableHeader();
		h.setLocation(0, lastLabel.getY() + lastLabel.getHeight());
		h.setSize(rightPanel.getWidth(), historyTable.getRowHeight());

		// set the table height no matter what; it may be hidden
		int tableHeight = historyTable.getRowCount()
				* historyTable.getRowHeight();
		historyTable.setSize(rightPanel.getWidth(), tableHeight);
		historyTable.setLocation(h.getX(), h.getY() + h.getHeight());

		// center student history label
		studentDataLabels[studentDataLabels.length - 1].setSize(rightPanel
				.getWidth(), studentDataLabels[studentDataLabels.length - 1]
				.getHeight());

		// let the superclass do what it needs to do afterwards
		super.componentResized(ev);
	}// componentResized()

	/**
	 * Used by AdminOptionTab to force subclasses to create a ListModel for
	 * dataList and return a reference to it; needed to properly construct the
	 * components.
	 * 
	 * @return ListModel the list model used by dataList
	 */
	protected ListModel createListModel() {
		dataModel = new StudentListModel();
		return dataModel;
	}// createListModel()

}// StudentOptionTab
