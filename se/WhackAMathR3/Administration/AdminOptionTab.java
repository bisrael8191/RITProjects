package Administration;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import DatabaseClient.DBConnector;

/**
 * Super class to StudentOptionTab and QuestionOptionTab. Holds all common
 * components, and ensures a similar layout for both components.
 * 
 * @author Eric M
 */
public abstract class AdminOptionTab extends JPanel implements
		ComponentListener, ListSelectionListener {

	// Components
	/**
	 * the "title" of the list; static jlabel telling the user what's being
	 * stored
	 */
	private JLabel leftNorthLabel;

	/** holds action buttons */
	private JPanel buttonPanel;

	/** holds (full) instructions */
	private JLabel instructionLabel;

	/** represents the data stored as a list of strings */
	private JList dataList;

	/**
	 * holds the rightBackPanel and scrollBar; used to easily display a scroll
	 * bar and the panel it scrolls
	 */
	private JPanel rightBackgroundPanel;

	/**
	 * holds only the rightPanel; used to allow scrolling of the rightBackPanel
	 * while still using a BorderLayout manager
	 */
	protected JPanel rightBackPanel;

	/** allows for scrolling of the rightBackPanel */
	protected JScrollBar scrollBar;

	/** holds all the components added by subclasses */
	protected JPanel rightPanel;

	// Data

	/**
	 * the list model used by our dataList; "overridden" by subclasses, only
	 * used here to actually display data in dataList
	 */
	private ListModel dataModel;

	/** reference to the database connector */
	protected DBConnector dbc;

	/** the logged in user */
	protected String user;

	/** the parent tabbed pane of this tab */
	protected AdministrationTabbedPane tabPane;

	/**
	 * Creates a new instance of AdminOptionTab
	 * 
	 * @param user
	 *            the logged in user
	 * @param tabPane
	 *            the parent tabbed pane of this tab
	 */
	public AdminOptionTab(String user, AdministrationTabbedPane tabPane) {
		this.user = user;
		this.tabPane = tabPane;

		// get a reference to the dbconnector for simplicity
		dbc = DBConnector.getInstance();
		setLayout(new BorderLayout());

		// TODO move these all into one method?
		// create instances of components
		createComponents();
		// position and size the components
		setupComponents();
		// listen for actions
		addListeners();
	}// AdminOptionTab

	/**
	 * Instantiates and initializes components
	 */
	private void createComponents() {
		// Data
		dataModel = createListModel();

		// Components
		dataList = new JList(dataModel);
		instructionLabel = new JLabel("");
		leftNorthLabel = new JLabel("");
		scrollBar = new JScrollBar();

		// Panels
		// used to format a scrollpane with a data header
		JPanel leftPanel = new JPanel(new BorderLayout());
		// scrolls our dataList
		JScrollPane leftScrollPane = new JScrollPane(dataList,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		rightBackgroundPanel = new JPanel(new BorderLayout());
		rightBackPanel = new JPanel(null);
		rightPanel = new JPanel(null);
		buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// allow for easy layout of the screen on a split pane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, leftPanel, rightBackgroundPanel);
		splitPane.setDividerLocation(100);

		// adding components
		leftPanel.add(leftNorthLabel, BorderLayout.NORTH);
		leftPanel.add(leftScrollPane, BorderLayout.CENTER);

		rightBackgroundPanel.add(rightBackPanel, BorderLayout.CENTER);
		rightBackgroundPanel.add(scrollBar, BorderLayout.EAST);

		rightBackPanel.add(rightPanel);

		rightPanel.add(instructionLabel);

		add(splitPane, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);

	}// createComponents()

	/**
	 * Places components on screen and sets properties
	 */
	private void setupComponents() {
		// Components
		dataList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		leftNorthLabel.setHorizontalAlignment(SwingConstants.CENTER);
		// initially not visible; only StudentOptionTab uses it
		scrollBar.setVisible(false);

		// Panels
		rightPanel.setBackground(Color.WHITE);
		rightBackPanel.setBackground(Color.WHITE);

	}// setupComponents()

	/**
	 * Adds whatever listeners are needed to listen for actions
	 */
	private void addListeners() {
		// listens for changes in which cell is selected
		dataList.addListSelectionListener(this);
		// listens for resizes
		rightBackPanel.addComponentListener(this);
	}// addListeners()

	/**
	 * Adds a button to the button panel; used by subclasses.
	 * 
	 * @param button
	 *            the new button to add
	 */
	protected void addButton(JButton button) {
		buttonPanel.add(button);
	}// addButton(JButton)

	/**
	 * Sets whether or not the instructions show up.
	 * 
	 * @param isVisible
	 *            if true instructions will be visible, if false not visible
	 */
	protected void setInstrVisible(boolean isVisible) {
		instructionLabel.setVisible(isVisible);
	}// setInstrVisible(boolean)

	/**
	 * Sets the text in the instructions label
	 * 
	 * @param instructions
	 *            the instructions to display to the user
	 */
	protected void setInstructions(String instructions) {
		instructionLabel.setText(instructions);
	}// setInstructions(String)

	/**
	 * Sets the instruction text as well as its location
	 * 
	 * @param instructions
	 *            the instructions to display to the user
	 * @param x
	 *            the x location of the component
	 * @param y
	 *            the y location of the component
	 */
	protected void setInstructions(String instructions, int x, int y) {
		setInstructions(instructions);
		instructionLabel.setLocation(x, y);
	}// setInstructions(String,int,int)

	/**
	 * Sets the dataList "title" (the JLabel to its north.
	 * 
	 * @param title
	 *            the new dataList title
	 */
	protected void setTitleText(String title) {
		leftNorthLabel.setText(title);
	}// setTitleText(String)

	/**
	 * Used to simplify the process of setting a selected index and calling the
	 * proper listener method.
	 * 
	 * @param index
	 *            the index to be selected
	 */
	protected void setSelectedIndex(int index) {
		getDataList().setSelectedIndex(index);
		valueChanged(new ListSelectionEvent(getDataList(), index, index, false));
	}// setSelectedIndex(int)

	/**
	 * Returns a reference to dataList
	 * 
	 * @return a reference to dataList
	 */
	protected JList getDataList() {
		return dataList;
	}// getDataList()

	// Abstract methods
	/**
	 * Used by AdminOptionTab to force subclasses to create a ListModel for
	 * dataList and return a reference to it; needed to properly construct the
	 * components.
	 * 
	 * @return ListModel the list model used by dataList
	 */
	protected abstract ListModel createListModel();

	// Listener methods

	// ListSelectionListener methods
	/**
	 * Called when the dataList selected value has changed.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public abstract void valueChanged(ListSelectionEvent ev);

	// ComponentListener methods
	/**
	 * Called when rightBackPanel is resized; informs subclasses and resizes
	 * rightPanel and instructionLabel to fit the new size.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void componentResized(ComponentEvent ev) {
		instructionLabel.setSize(rightPanel.getWidth(), getHeight());
		instructionLabel.setLocation((rightPanel.getWidth() - instructionLabel
				.getWidth()) / 2, (rightPanel.getHeight() - instructionLabel
				.getHeight()) / 2);
	}// componentResized(ComponentEvent)

	// unused Listener methods
	/** @param ev */
	public void componentHidden(ComponentEvent ev) {
	}

	/** @param ev */
	public void componentMoved(ComponentEvent ev) {
	}

	/** @param ev */
	public void componentShown(ComponentEvent ev) {
	}
}// AdminOptionTab
