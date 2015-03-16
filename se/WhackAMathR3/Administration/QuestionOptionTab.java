package Administration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;

import GameLogic.PositionQuestionException;
import GameLogic.DivisionRemainderException;
import GameLogic.Question;
import GameUI.GameWindow;

/**
 * Allows user to create, change, view, and save Questions.
 * 
 * @author Eric M
 */
public class QuestionOptionTab extends AdminOptionTab implements ActionListener {

	// button text
	private static final String ADD_QUESTION_TXT = "Add New Question";

	private static final String DELETE_QUESTION_TXT = "Delete Question";

	private static final String SAVE_QUESTION_TXT = "Save Question";
	
	private static final String FIRST_PLACES_LABEL = "Digit (0-9):";
	
	private static final String SECOND_PLACES_LABEL = "Place (1, 10, 100):";
	
	private static final String FIRST_DIVISION_LABEL = "Dividend:";
	
	private static final String SECOND_DIVISION_LABEL = "Divisor:";
	
	private static final String DIV_OP = "\u00F7";

	/** The full instructions for the question tab */
	private static final String FULL_INSTR = "<html>"
			+ "To add a new question:<br>"
			+ "1) Click the \""
			+ ADD_QUESTION_TXT
			+ "\" button below.<br>"
			+ "2) Enter the data in the areas provided.<br>"
			+ "- When creating a 'Select the number with <i>[digit]</i> "
			+ "in the <i>[number]</i> place' type question "
			+ "'First term' refers to the digit and "
			+ "'Second term' refer to the place to find it in (1,10,100).<br>"
			+ "- Note: The symbol for this type of question is the colon ':'<br>"
			+ "- When creating any other type of question "
			+ "'First term' and 'Second term' refer to the numbers to "
			+ "perform the selected operation on.<br>"
			+ "There is no need to enter an answer; "
			+ "they will be calculated for you.<br>"
			+ "3) Click the \""
			+ SAVE_QUESTION_TXT
			+ "\" button to save changes.<br><br>"
			+ "To edit an existing question:<br>"
			+ "1) Select the question you would like to edit.<br>"
			+ "2) Change the fields as in the add question process.<br>"
			+ "3) Click the \""
			+ SAVE_QUESTION_TXT
			+ "\" button to save changes.<br><br>"
			+ "Remember to click the \"Save\" button below to save any changes to the database."
			+ "</html>";

	/** The partial instructions for adding a question */
	private static final String PART_INSTR = "<html>" + "New: \""
			+ ADD_QUESTION_TXT + "\" button, edit fields, \""
			+ SAVE_QUESTION_TXT + "\" button<br>"
			+ "Edit: Select question, edit fields, \"" + SAVE_QUESTION_TXT
			+ "\"<br>" + "When done click \"Save\"" + "</html>";

	/** JList header */
	private static final String QUESTIONS = "Questions";

	/**
	 * holds the partial instruction text for quick reference when editing a
	 * question
	 */
	private JLabel instrPartLabel;

	/** editQuestPanel used for convenience in centering the question options */
	private JPanel editQuestPanel;

	/** the first term of the question */
	private JTextField firstTermInput;

	/** the second term of the question */
	private JTextField secondTermInput;
	
	/** label for the first term */
	private JLabel fTermLabel;
	
	/** label forthe second term */
	private JLabel sTermLabel;

	/** provides operation symbol choices for questions */
	private JComboBox opChoice;

	/** actually holds the question data */
	private QuestionListModel dataModel;

	// used for enabling/disabling
	/** reference to delete button */
	private JButton deleteQuestionButton;

	/** reference to save button */
	private JButton saveQuestionButton;

	/**
	 * Creates a new instance of QuestionOptionTab and instantiates components
	 * 
	 * @param tabPane
	 *            the parent tabbed pane of this tab
	 */
	public QuestionOptionTab(String user, AdministrationTabbedPane tabPane) {
		super(user, tabPane);

		// if we need to keep a reference to a component, create it here
		editQuestPanel = new JPanel(new BorderLayout());
		editQuestPanel.setBackground(Color.WHITE);
		editQuestPanel.setVisible(false);

		firstTermInput = new JTextField();
		secondTermInput = new JTextField();

		// set the partial instructions
		instrPartLabel = new JLabel(PART_INSTR);

		// give the combo box its choices
		opChoice = new JComboBox(new String[] { "+", "-", "x", DIV_OP, ":" });
		opChoice.setBackground(Color.WHITE);

		// create everything else
		init();
	}// QuestionOptionTab()

	/**
	 * Set up components
	 */
	protected void init() {
		// Set the instruction label
		setInstructions(FULL_INSTR);

		// fill the list model with questions
		loadQuestions();

		// set the jlist header title
		setTitleText(QUESTIONS);

		// setup question editing panel
		JPanel editQuestCenterPanel = new JPanel(null);
		editQuestCenterPanel.setBackground(Color.WHITE);

		// create term input boxes
		fTermLabel = new JLabel("First term:");
		sTermLabel = new JLabel("Second term:");

		// label and text box size
		Dimension componentSize = new Dimension(100, 30);
		// opChoice size different so it doesn't look strange
		Dimension opChoiceSize = new Dimension(70, 30);

		// all components same size
		fTermLabel.setSize(componentSize);
		sTermLabel.setSize(componentSize);
		firstTermInput.setSize(componentSize);
		secondTermInput.setSize(componentSize);

		opChoice.setSize(opChoiceSize);
		opChoice.setSelectedIndex( 0 );
		opChoice.addActionListener( this );
		// position components
		// put pad distance between components
		int pad = 5;
		// all other component positions based off fTermLabel
		fTermLabel.setLocation(pad, (int) componentSize.getHeight() + pad);
		firstTermInput.setLocation(fTermLabel.getX(), fTermLabel.getY()
				+ fTermLabel.getHeight() + pad);
		opChoice.setLocation(firstTermInput.getX() + firstTermInput.getWidth()
				+ pad, firstTermInput.getY());
		secondTermInput.setLocation(
				opChoice.getX() + opChoice.getWidth() + pad, opChoice.getY());
		sTermLabel.setLocation(secondTermInput.getX(), fTermLabel.getY());

		// total size so far
		int totalWidth = firstTermInput.getWidth() + opChoice.getWidth()
				+ secondTermInput.getWidth() + (pad * 4);
		// the amount of room allocated for the partial instructions
		int instrPartLabelHeight = 60;

		// set the question editing panel size based on its components
		editQuestPanel.setSize(totalWidth, firstTermInput.getY()
				+ firstTermInput.getHeight() + instrPartLabelHeight + pad);

		// Create buttons
		JButton addQuestionButton = new JButton(ADD_QUESTION_TXT);
		deleteQuestionButton = new JButton(DELETE_QUESTION_TXT);
		saveQuestionButton = new JButton(SAVE_QUESTION_TXT);

		// set initial state of buttons to not enabled
		deleteQuestionButton.setEnabled(false);
		saveQuestionButton.setEnabled(false);

		// listen for clicks
		addQuestionButton.addActionListener(this);
		deleteQuestionButton.addActionListener(this);
		saveQuestionButton.addActionListener(this);

		// add buttons to the button panel
		addButton(addQuestionButton);
		addButton(deleteQuestionButton);
		addButton(saveQuestionButton);

		// add components to the center panel
		editQuestCenterPanel.add(fTermLabel);
		editQuestCenterPanel.add(sTermLabel);
		editQuestCenterPanel.add(firstTermInput);
		editQuestCenterPanel.add(opChoice);
		editQuestCenterPanel.add(secondTermInput);

		// add components to the question panel
		editQuestPanel.add(editQuestCenterPanel, BorderLayout.CENTER);
		editQuestPanel.add(instrPartLabel, BorderLayout.NORTH);

		// add components to the right panel
		rightPanel.add(editQuestPanel);

		// start off with the instructions showing
		setSelectedIndex(0);
	}// init()

	/**
	 * Loads questions from the database and adds them to our data model
	 */
	private void loadQuestions() {
		ArrayList<Question> questions = null;

		// load add/sub questions
		questions = dbc.getQuestions(user, 0);
		if (questions != null) {
			for (Question q : questions) {
				System.out.println(q);
				dataModel.addElement(q);
			}// for
		}// if

		// load mult/div questions
		questions = dbc.getQuestions(user, 1);
		if (questions != null) {
			for (Question q : questions) {
				System.out.println(q);
				dataModel.addElement(q);
			}// for
		}// if

		// load position questions
		questions = dbc.getQuestions(user, 2);
		if (questions != null) {
			for (Question q : questions) {
				System.out.println(q);
				dataModel.addElement(q);
			}// for
		}// if

	}// loadQuestions()

	/**
	 * Adds a new default Question to our data model.
	 */
	private void addQuestion() {
		// add a default "0+0" question
		dataModel.addElement(new Question(Question.Type.SUM, 0, 0));

		// find the index we just added this question to
		int insertIndex = dataModel.getLastIndexOf(Question.Type.SUM);

		// select the new question, ready for editing
		setSelectedIndex(insertIndex);

		// Clear the input fields
		firstTermInput.setText("");
		secondTermInput.setText("");

		// Give the focus to the first text field if possible
		firstTermInput.requestFocusInWindow();

	}// addQuestion()

	/**
	 * Saves the Question currently being edited to the specified index
	 * 
	 * @param index
	 *            the index to save to
	 * @param newQuest
	 *            the new question to replace the selected one with
	 */
	private void saveQuestion(int index, Question newQuest) {
		// remove the old question
		dataModel.remove(index);
		// add the new question
		dataModel.addElement(newQuest);

		// since the new question is added to the end of the list we want to
		// select the last index of the new question type
		int newIndex = dataModel.getLastIndexOf(newQuest.getTheType());
		setSelectedIndex(newIndex);
	}// saveQuestion(int,Question)

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
		if (index >= dataModel.getSize() - 2) {
			setSelectedIndex(dataModel.getSize() - 2);
		}// if

		// make sure to inform the list of the changes
		valueChanged(new ListSelectionEvent(getDataList(), index, dataModel
				.getSize(), false));
	}// removeItemAt(int)

	/**
	 * Actually saves data to the database
	 */
	public void saveData() {
		ArrayList<Question> questions = null;

		// save add/sub questions
		questions = dataModel.getQuestionsOfType(Question.Type.SUM);
		questions.addAll(dataModel.getQuestionsOfType(Question.Type.DIFF));
		dbc.putQuestions(user, 0, questions);

		// save mult/div questions
		questions = dataModel.getQuestionsOfType(Question.Type.MULT);
		questions.addAll(dataModel.getQuestionsOfType(Question.Type.DIV));
		dbc.putQuestions(user, 1, questions);

		// save position questions
		questions = dataModel.getQuestionsOfType(Question.Type.POSITION);
		dbc.putQuestions(user, 2, questions);

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

		// find the button pressed and perform its operation
		if (ev.getActionCommand() == ADD_QUESTION_TXT) {
			// add a new question
			addQuestion();
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		} else if (ev.getActionCommand() == DELETE_QUESTION_TXT ) {
			// remove the question at the selected index
			removeItemAt(getDataList().getSelectedIndex());
			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		} else if (ev.getActionCommand() == SAVE_QUESTION_TXT ) {
			// save the question being edited to the list

			// first and second term, as entered by the user
			int firstTerm = 0;
			int secondTerm = 0;

			// try to parse the numbers; if an invalid entry is found
			// set it to 0
			try {
				firstTerm = Integer.parseInt((String) firstTermInput.getText());
			} catch (NumberFormatException ex) {
				firstTermInput.setText("0");
				firstTerm = 0;
			}
			try {
				secondTerm = Integer.parseInt((String) secondTermInput
						.getText());
			} catch (NumberFormatException ex) {
				secondTermInput.setText("0");
				secondTerm = 0;
			}

			// build a new Question object based on the inputs and the
			// operator chosen in the combo box
			try {
				Question q = new Question(Question.findType((String) opChoice
						.getSelectedItem()), firstTerm, secondTerm);
				// add it to the list at the selected index
				saveQuestion(getDataList().getSelectedIndex(), q);
			} catch (DivisionRemainderException ex) {
				JOptionPane.showMessageDialog(GameWindow.getInstance(),
						"Please enter a question without a remainder.",
						"Remainder in division",
						JOptionPane.ERROR_MESSAGE);
			} catch ( PositionQuestionException  pqe ) {
				JOptionPane.showMessageDialog( GameWindow.getInstance(), 
								pqe.getMessage(), "Position Question Error", 
								JOptionPane.ERROR_MESSAGE );
			}

			// tell the tabbed pane we've made changes
			tabPane.setSaveEnabled(true);
		} else if ( ev.getSource() == opChoice ) {
			String op = opChoice.getSelectedItem().toString();
			if ( op.equals( "+" ) || op.equals( "-" ) ) {
				if ( !fTermLabel.getText().equals( "First term:" ) ) 
					fTermLabel.setText( "First term:" );
				if ( !sTermLabel.getText().equals( "Second term:" ) ) 
					sTermLabel.setText( "Second term:" );
			} else if ( op.equals( "x" ) ) {
				if ( !fTermLabel.getText().equals( "First factor:" ) ) 
					fTermLabel.setText( "First factor:" );
				if ( !sTermLabel.getText().equals( "Second factor:" ) ) 
					sTermLabel.setText( "Second factor:" );
			} else if ( op.equals( DIV_OP ) ) {
				if ( !fTermLabel.getText().equals( FIRST_DIVISION_LABEL ) ) 
					fTermLabel.setText( FIRST_DIVISION_LABEL );
				if ( !sTermLabel.getText().equals( SECOND_DIVISION_LABEL ) ) 
					sTermLabel.setText( SECOND_DIVISION_LABEL );
			} else if ( op.equals( ":" ) ) {
				if ( !fTermLabel.getText().equals( FIRST_PLACES_LABEL ) ) 
					fTermLabel.setText( FIRST_PLACES_LABEL );
				if ( !sTermLabel.getText().equals( SECOND_PLACES_LABEL ) ) 
					sTermLabel.setText( SECOND_PLACES_LABEL );
			}
		} // if-else
	}// actionPerformed(ActionEvent)

	// ListSelectionListener methods
	/**
	 * Called when the dataList selected value has changed. Loads the selected
	 * question data into the edit fields.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void valueChanged(ListSelectionEvent ev) {
		// get the text of the selected data; if null, text = null
		String text = (getDataList().getSelectedValue() == null) ? null
				: getDataList().getSelectedValue().toString();

		// if the selected value is null or invalid...
		if (text == null
				|| !dataModel.isValidQuestion(getDataList().getSelectedIndex())) {

			// show the full instructions
			setInstrVisible(true);
			// hide the editing panel
			editQuestPanel.setVisible(false);

		} else {
			// if the selected value is valid...

			// hide the full instructions
			setInstrVisible(false);
			// show the editing panel
			editQuestPanel.setVisible(true);

			// question is in the form:
			// [(first term)" "(symbol)" "(second term)" = "(answer)]
			// let's parse it

			// TODO: check to see if this 'if' is necessary
			if (text != null) {
				// parse the text on a space or equal sign
				String delimeters = " =";
				StringTokenizer parser = new StringTokenizer(text, delimeters);

				firstTermInput.setText(parser.nextToken());
				opChoice.setSelectedItem(parser.nextToken());
				secondTermInput.setText(parser.nextToken());
				// the input fields now contain the correct symbols
			}// if

		}// if-else

		// simulate a resize on the rightPanel to force a reposition of
		// components
		// compResized(rightPanel);

		// resize the rightPanel, force components to reposition
		rightPanel.setSize(rightBackPanel.getSize());
		componentResized(new ComponentEvent(rightBackPanel,
				ComponentEvent.COMPONENT_RESIZED));

		// allow data modification only if there is data to modify
		int selectedIndex = getDataList().getSelectedIndex();
		deleteQuestionButton.setEnabled(dataModel
				.isValidQuestion(selectedIndex));
		saveQuestionButton.setEnabled(dataModel.isValidQuestion(selectedIndex));

	}// valueChanged(ListSelectionEvent)

	/**
	 * Called when rightBackPanel is resized; repositions editing panel to be
	 * centered on screen.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void componentResized(ComponentEvent ev) {

		JPanel source = (JPanel) ev.getSource();

		editQuestPanel.setLocation((source.getWidth() - editQuestPanel
				.getWidth()) / 2, (source.getHeight() - editQuestPanel
				.getHeight()) / 2);

		// resize if we can
		if (!dataModel.isValidQuestion(getDataList().getSelectedIndex())) {
			rightPanel.setSize(rightBackPanel.getSize());
		}// if

		// let the superclass do what it needs to do afterwards
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
		dataModel = new QuestionListModel();
		return dataModel;
	}// createListModel()

}// QuestionOptionTab
