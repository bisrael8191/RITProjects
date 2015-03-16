package Administration;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import DatabaseClient.DBConnector;
import GameUI.GameWindow;
import Login.LoginPanel;

/**
 * Holds the tabs for changing options, and the controls common to all tabs
 * (save, quit, etc.)
 * 
 * @author Eric M
 */
public class AdministrationTabbedPane extends JPanel {

	/** reference to the parent window */
	private GameWindow parent;

	/** reference to the student tab */
	private StudentOptionTab studentTab;

	/** reference to the question tab */
	private QuestionOptionTab questionTab;

	/** reference to the teacher tab */
	private TeacherOptionTab teacherTab;

	/** reference to the misc tab */
	private MiscOptionTab miscTab;

	/** reference to the save button */
	private JButton saveButton;

	/** true if the signed in user is "Admin" */
	private boolean isAdmin;

	/**
	 * Used for testing only
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		GameWindow gw = GameWindow.getInstance();
		gw.add(new AdministrationTabbedPane("Teacher"), BorderLayout.CENTER);
	}

	/**
	 * Creates a new instance of <code>AdministrationTabbedPane</code> with
	 * <code>user</code> logged in
	 * 
	 * @param user
	 *            the currently logged in user
	 */
	public AdministrationTabbedPane(String user) {
		isAdmin = user.equals("Admin");

		// set the parent window to our game window
		parent = GameWindow.getInstance();

		// tabbed pane in the center, button panel in the south
		setLayout(new BorderLayout());

		// setup tabs
		JTabbedPane tabs = new JTabbedPane();
		if (!isAdmin) {
			studentTab = new StudentOptionTab(user, this);
			questionTab = new QuestionOptionTab(user, this);
		}
		teacherTab = new TeacherOptionTab(user, this);
		miscTab = new MiscOptionTab(user, DBConnector.getInstance());

		// add tabs
		if (!isAdmin) {
			tabs.addTab("Students", studentTab);
			tabs.addTab("Questions", questionTab);
		}
		tabs.addTab("Teachers", teacherTab);
		tabs.addTab("Miscellaneous", miscTab);

		// setup buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
		saveButton = new JButton("Save");
		saveButton.setEnabled(false);
		JButton closeButton = new JButton("Return to Login");
		JButton quitButton = new JButton("Quit");

		// action listeners
		// "Save" - save data
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				saveData();
			}
		});

		// "Return to Login" - load login panel
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				returnToLogin();
			}
		});

		// "Quit" - quit program
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				quit();
			}
		});

		// add buttons
		buttonPanel.add(saveButton);
		buttonPanel.add(closeButton);
		buttonPanel.add(quitButton);

		// add subpanels
		add(tabs, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}// AdministrationPanel()

	/**
	 * Changed the enabled state of saveButton
	 * 
	 * @param enabled
	 *            the new enabled state of the saveButton
	 */
	public void setSaveEnabled(boolean enabled) {
		saveButton.setEnabled(enabled);
	}// setSaveEnabled(boolean)

	/**
	 * Calls the saveData method of each tab, saving information to the database
	 */
	private void saveData() {
		if (!isAdmin) {
			studentTab.saveData();
			questionTab.saveData();
		}
		teacherTab.saveData();
	}// saveData()

	/**
	 * Clears the window and returns the user to the login screen
	 */
	private void returnToLogin() {
		int answer = -1;
		// check to see if we have any unsaved changes
		if (saveButton.isEnabled()) {
			// 0 = yes, 1 = no, 2 = cancel
			answer = JOptionPane.showConfirmDialog(this,
					"Would you like to save the changes you have made?",
					"Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);

		}// if

		// if cancel button pressed, don't close
		if (answer != 2) {
			// save if we were told to
			if (answer == 0) {
				saveData();
			}// if

			// clear the frame
			parent.getContentPane().removeAll();
			// load the admin option pane into the frame
			parent.getContentPane().add(new LoginPanel(getDefaultClass()),
					BorderLayout.CENTER);

			// refresh the frame
			parent.repaint();
			parent.validate();
		}// if
	}// returnToLogin()

	/**
	 * Returns the default class.
	 * 
	 * @return the default class
	 */
	public String getDefaultClass() {
		String retVal = "Admin";
		// the configuration file
		File configFile = new File("config.ini");
		// only modify if the file exists
		if (configFile.exists()) {
			try {
				// read in the default class
				BufferedReader in = new BufferedReader(new FileReader(
						configFile));
				retVal = in.readLine();
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}// try-catch
		}// if

		return retVal;
	}// getDefaultClass

	/**
	 * Dispose of the window and quit the program
	 */
	private void quit() {
		int answer = -1;
		// check to see if we have any unsaved changes
		if (saveButton.isEnabled()) {
			// 0 = yes, 1 = no, 2 = cancel
			answer = JOptionPane.showConfirmDialog(this,
					"Would you like to save the changes you have made?",
					"Confirm Exit", JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);

		}// if

		// if cancel button pressed, don't close
		if (answer != 2) {
			// save if we were told to
			if (answer == 0) {
				saveData();
			}// if

			parent.dispose();
		}// if
	}// quit()

}// AdministrationTabbedPane
