package Login;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

import Administration.AdministrationTabbedPane;
import Administration.MD5Hash;
import DatabaseClient.DBConnector;
import GameLogic.GameManager;
import GameLogic.Student;
import GameUI.BackgroundPanel;
import GameUI.GameWindow;
import GameUI.ImageLabel;

/**
 * 
 * @author Eric M
 */
public class LoginPanel extends BackgroundPanel implements ActionListener,
		MouseListener {

	// Constants
	/** horizontal size of the screen */
	private static final int SCREEN_WIDTH = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth();

	/** vertical size of the screen */
	private static final int SCREEN_HEIGHT = (int) Toolkit.getDefaultToolkit()
			.getScreenSize().getHeight();

	/** fix for border compensation */
	private static final int BORDER_HEIGHT = 32;

	/** fix for border compensation */
	private static final int BORDER_WIDTH = 14;

	/** the image directory */
	private static final String IMAGES = "images" + File.separator;

	// Label icons
	/** left scroll arrow */
	private static final Icon LEFT_ARROW = new ImageIcon(IMAGES
			+ "login_arrow_left.png");

	/** right scroll arrow */
	private static final Icon RIGHT_ARROW = new ImageIcon(IMAGES
			+ "login_arrow_right.png");

	/** selected left scroll arrow */
	private static final Icon LEFT_ARROW_SEL = new ImageIcon(IMAGES
			+ "login_arrow_left_sel.png");

	/** selected right scroll arrow */
	private static final Icon RIGHT_ARROW_SEL = new ImageIcon(IMAGES
			+ "login_arrow_right_sel.png");

	/** the student name button */
	private static final Icon LOGIN_BUTTON = new ImageIcon(IMAGES
			+ "login_button.png");

	/** for the selected student name button */
	private static final Icon LOGIN_BUTTON_SEL = new ImageIcon(IMAGES
			+ "login_button_selected.png");

	/** start an "add" game */
	private static final Icon LOGIN_BUTTON_ADD = new ImageIcon(IMAGES
			+ "login_button_addSub.png");

	/** start a "subtract" game */
	private static final Icon LOGIN_BUTTON_MULT = new ImageIcon(IMAGES
			+ "login_button_multDiv.png");

	/** start a mixxed game */
	private static final Icon LOGIN_BUTTON_MIX = new ImageIcon(IMAGES
			+ "login_button_mix.png");

	/** the teacher button */
	private static final Icon LOGIN_BUTTON_TCH = new ImageIcon(IMAGES
			+ "login_button_teacherLogin.png");

	/** the quit button */
	private static final Icon LOGIN_BUTTON_QUIT = new ImageIcon(IMAGES
			+ "login_button_quit.png");

	/** the logo image */
	private static final Icon LOGIN_LOGO = new ImageIcon(IMAGES
			+ "login_logo.png");

	/** the short instructions image */
	private static final Icon LOGIN_FINDNAME = new ImageIcon(IMAGES
			+ "login_findName.png");

	// Fonts
	/** font used on all student name labels (and teacher label) */
	private static final Font STUDENT_FONT = new Font("Trebuchet MS",
			Font.BOLD, 10);

	// Private data members
	/** true if we can scroll through names */
	private boolean scrollingEnabled;

	/** the admin login button */
	private JButton adminButton;

	/** the quit button */
	private JButton quitButton;

	/** used to display the password input fields and quit button confirmation */
	// private JPanel buttonTab;
	/** */
	// private Point buttonTabHiddenLoc;
	/** */
	// private Point buttonTabShownLoc;
	/** list of the labels representing different game choices */
	private Collection<JLabel> gameChoices;

	/** Thread used to shift components using the arrow buttons */
	private ComponentShiftThread shiftThread;

	/** login panels are put onto this class */
	private GameWindow parent;

	/** the name of the class we're displaying */
	private String className;

	/** the connection to our database */
	private DBConnector dbc;

	// Static members
	/** static reference to this loginPanel; used in anonymous threads */
	public static LoginPanel loginPanel;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create a login panel
		new LoginPanel("Teacher");

	}

	/**
	 * Creates a new LoginPanel object on the parent window
	 * 
	 * @param className
	 *            the name of the class being loaded
	 */
	public LoginPanel(String className) {
		super(new ImageIcon("images/login_background.png"));

		this.className = className;

		// will be changed to false later if scrolling isn't necessary
		scrollingEnabled = true;

		// get our references
		parent = GameWindow.getInstance();
		dbc = DBConnector.getInstance();

		// panel properties
		setLayout(null);
		setSize(parent.getWidth(), parent.getHeight());
		addMouseListener(this);

		// set up components
		init();

		// add me to the game window
		parent.add(this);
		parent.validate();

		// reference to this class so it can be removed later
		loginPanel = this;

	}// LoginWindow

	/**
	 * Initialize components of this panel
	 */
	private void init() {
		// used to keep the components in the center of the screen
		JPanel backgroundPanel = new JPanel(new BorderLayout());
		backgroundPanel.setOpaque(false);
		backgroundPanel.addMouseListener(this);
		// set the background panel size to the window width and scroll arrow
		// height
		backgroundPanel.setSize(parent.getWidth() - BORDER_WIDTH, LEFT_ARROW
				.getIconHeight());
		backgroundPanel.setLocation(4, (getHeight() - backgroundPanel
				.getHeight()) / 2);
		backgroundPanel.setBackground(Color.WHITE);

		// create left and right scroll arrows for the window
		JLabel leftButton = new JLabel(LEFT_ARROW);
		leftButton.setSize(LEFT_ARROW.getIconWidth(), LEFT_ARROW
				.getIconHeight());
		leftButton.addMouseListener(this);

		JLabel rightButton = new JLabel(RIGHT_ARROW);
		rightButton.setSize(RIGHT_ARROW.getIconWidth(), RIGHT_ARROW
				.getIconHeight());
		rightButton.addMouseListener(this);

		// center panel of the main pane; null layout manager to allow for more
		// control over where our components are
		BackgroundPanel backPanel = new BackgroundPanel(new ImageIcon(
				"images/login_names_background.png"));

		// temp button; to be remade for each student name
		JLabel studentButton = null;

		// "padding" distance between buttons
		int pad = 10;
		// counter
		int c = 0;

		// centerPanel holds the student buttons
		JPanel centerPanel = new JPanel(null);
		centerPanel.addMouseListener(this);
		centerPanel.setOpaque(false);

		// create the list of student names
		Collection<String> studentNames = getStudentNames();
		// create list of student buttons
		ArrayList<JLabel> studentButtons = new ArrayList<JLabel>();

		// size centerPanel to fit all of our labels
		int centerWidth = Math
				.max(backgroundPanel.getWidth() - LEFT_ARROW.getIconWidth()
						- RIGHT_ARROW.getIconWidth(), studentNames.size()
						* (LOGIN_BUTTON.getIconWidth() + pad) - pad);

		centerPanel.setSize(centerWidth, backgroundPanel.getHeight());
		centerPanel.setLocation(0, 0);

		// create our game choice buttons on the center panel
		createGameChoices(centerPanel);

		// create new shiftThread with bounds of the viewable area
		shiftThread = new ComponentShiftThread(centerPanel, centerPanel
				.getWidth()
				- parent.getWidth()
				+ RIGHT_ARROW.getIconWidth()
				+ LEFT_ARROW.getIconWidth() + BORDER_WIDTH);

		// create all of our student buttons
		for (String studentName : studentNames) {
			// all student buttons have the same: size, icon, text format, and
			// font. they differ only in horizontal location
			studentButton = new JLabel(studentName, LOGIN_BUTTON,
					SwingConstants.CENTER);
			studentButton.setHorizontalTextPosition(SwingConstants.CENTER);
			studentButton.setFont(STUDENT_FONT);
			studentButton.setForeground(Color.WHITE);
			studentButton.setSize(LOGIN_BUTTON.getIconWidth(), LOGIN_BUTTON
					.getIconHeight());
			studentButton.setLocation(
					(c * ((int) LOGIN_BUTTON.getIconWidth() + pad)),
					backgroundPanel.getHeight() - studentButton.getHeight()
							- 10);

			// listen for mouse events
			studentButton.addMouseListener(this);

			// add to the center panel and the student button list
			centerPanel.add(studentButton);
			studentButtons.add(studentButton);

			c++;
		}// for

		// if there aren't enough buttons to scroll, center what we have in the
		// middle of the centerPanel
		if (studentButtons.size() > 0) {
			JLabel lastLabel = studentButtons.get(studentButtons.size() - 1);
			if ((lastLabel.getX() + lastLabel.getWidth()) < centerPanel
					.getWidth()) {

				// "disable" the left and right scroll arrows if there aren't
				// enough students to scroll
				scrollingEnabled = false;

				int totalWidth = lastLabel.getX() + lastLabel.getWidth()
						- studentButtons.get(0).getX();
				c = 0;

				for (JLabel current : studentButtons) {
					current.setLocation((centerPanel.getWidth() - totalWidth)
							/ 2
							+ (c * ((int) LOGIN_BUTTON.getIconWidth() + pad)),
							current.getY());

					c++;
				}// for
			}// if
		} else {
			// "disable" the left and right scroll arrows if there aren't any
			// students in the list
			scrollingEnabled = false;
		}// if-else

		// logo
		JLabel logoLabel = new JLabel(LOGIN_LOGO);
		logoLabel
				.setSize(LOGIN_LOGO.getIconWidth(), LOGIN_LOGO.getIconHeight());
		logoLabel.setLocation((getWidth() - logoLabel.getWidth()) / 2, 0);

		// find name
		JLabel findNameLabel = new JLabel(LOGIN_FINDNAME);
		findNameLabel.setSize(LOGIN_FINDNAME.getIconWidth(), LOGIN_FINDNAME
				.getIconHeight());
		findNameLabel.setLocation((getWidth() - logoLabel.getWidth()) / 2,
				backgroundPanel.getY() + backgroundPanel.getHeight() + pad);

		// start the thread here, after the components it depends on are created
		shiftThread.start();

		// add fading effects
		ImageLabel leftGradient = new ImageLabel(
				"images/login_gradient_left.png");
		ImageLabel rightGradient = new ImageLabel(
				"images/login_gradient_right.png");
		leftGradient.setPosition(0, 8);
		rightGradient.setPosition(681 - rightGradient.getWidth(), 8);

		// "tab" TODO get an image that fits
		// ImageIcon tabIcon = new ImageIcon(IMAGES + "login_tab.png");
		// buttonTab = new BackgroundPanel(tabIcon);
		// buttonTab.setOpaque(false);
		// buttonTab.setSize(tabIcon.getIconWidth(), tabIcon.getIconHeight());

		// two possible tab locations
		// buttonTabHiddenLoc = new Point((getWidth() - buttonTab.getWidth()) /
		// 2,
		// getHeight() - BORDER_HEIGHT);
		// buttonTabShownLoc = new Point((getWidth() - buttonTab.getWidth()) /
		// 2,
		// getHeight() - buttonTab.getHeight());
		// // start in the "hidden" location
		// buttonTab.setLocation(buttonTabHiddenLoc);
		// buttonTab.addMouseListener(this);

		// quit button TODO reposition me
		int buttonPos = 522;
		quitButton = new JButton(LOGIN_BUTTON_QUIT);
		quitButton.setSize(LOGIN_BUTTON_QUIT.getIconWidth(), LOGIN_BUTTON_QUIT
				.getIconHeight());
		// quitButton.setLocation(getWidth() - quitButton.getWidth()
		// - BORDER_WIDTH, buttonTab.getY() - quitButton.getHeight());
		quitButton.setLocation(getWidth() - quitButton.getWidth()
				- BORDER_WIDTH, buttonPos);
		quitButton.setContentAreaFilled(false);
		quitButton.setFocusPainted(false);
		quitButton.setBorderPainted(false);
		quitButton.addActionListener(this);
		quitButton.addMouseListener(this);

		// admin button
		// how much the quit button overlaps the admin button
		int overlap = 30;
		adminButton = new JButton(LOGIN_BUTTON_TCH);
		adminButton.setSize(LOGIN_BUTTON_TCH.getIconWidth(), LOGIN_BUTTON_TCH
				.getIconHeight());
		adminButton.setLocation(quitButton.getX() - adminButton.getWidth()
				+ overlap, quitButton.getY());
		adminButton.setContentAreaFilled(false);
		adminButton.setFocusPainted(false);
		adminButton.setBorderPainted(false);
		adminButton.addActionListener(this);
		adminButton.addMouseListener(this);

		// get the default ip and class name and display them in the corner
		String ipAddress = dbc.getServerAddress();

		// setup the display labels
		int compWidth = 250;
		int compHeight = 10;
		pad = 5;
		// create labels with the text loaded into it
		JLabel defaultClassLabel = new JLabel("Class loaded: " + className);
		JLabel defaultIPLabel = new JLabel("Connected to: " + ipAddress);
		// set to the same font as the student labels use
		defaultClassLabel.setFont(STUDENT_FONT);
		defaultIPLabel.setFont(STUDENT_FONT);
		// white color to not stand out too much
		// TODO change if not easily visible?
		defaultClassLabel.setForeground(Color.WHITE);
		defaultIPLabel.setForeground(Color.WHITE);
		// both labels the same size
		defaultClassLabel.setSize(compWidth, compHeight);
		defaultIPLabel.setSize(compWidth, compHeight);
		// set them to the bottom left corner
		defaultIPLabel.setLocation(pad, parent.getRootPane().getHeight()
				- defaultIPLabel.getHeight() - pad);
		defaultClassLabel.setLocation(pad, defaultIPLabel.getY()
				- defaultClassLabel.getHeight() - pad);

		// back panel components
		backPanel.add(leftGradient);
		backPanel.add(rightGradient);
		backPanel.add(centerPanel);

		// background panel components
		backgroundPanel.add(leftButton, BorderLayout.WEST);
		backgroundPanel.add(rightButton, BorderLayout.EAST);
		backgroundPanel.add(backPanel, BorderLayout.CENTER);

		// this panel components
		add(backgroundPanel);
		add(findNameLabel);
		add(logoLabel);
		add(quitButton);
		add(adminButton);
		add(defaultClassLabel);
		add(defaultIPLabel);

		// add(buttonTab);

	}// init()

	/**
	 * Create the Collection of possible game choice labels (moved here to avoid
	 * clutter)
	 * 
	 * @param panel
	 *            the panel these choices will be visible on
	 */
	private void createGameChoices(JPanel panel) {
		// gameChoices[0] = bottom
		// gameChoices[n] = top
		gameChoices = new ArrayList<JLabel>();

		// create each label and add to the gameChoices ArrayList
		// do stuff here that can't be done in the loop below
		JLabel tempLabel;
		tempLabel = new JLabel(LOGIN_BUTTON_MIX, SwingConstants.CENTER);
		gameChoices.add(tempLabel);
		// tempLabel = new JLabel(LOGIN_BUTTON_PLC, SwingConstants.CENTER);
		// gameChoices.add(tempLabel);
		tempLabel = new JLabel(LOGIN_BUTTON_MULT, SwingConstants.CENTER);
		gameChoices.add(tempLabel);
		tempLabel = new JLabel(LOGIN_BUTTON_ADD, SwingConstants.CENTER);
		gameChoices.add(tempLabel);

		// culmHeight keeps track of the height of the past icons so we know
		// where to put the new ones
		int culmHeight = LOGIN_BUTTON.getIconHeight() + BORDER_HEIGHT;
		// -put the choice labels on the panel, but keep them hidden for now
		// -set properties common to all choices; size, listeners, etc.
		for (JLabel choice : gameChoices) {
			choice.setSize(choice.getIcon().getIconWidth(), choice.getIcon()
					.getIconHeight());
			culmHeight += choice.getIcon().getIconHeight();
			choice.setLocation(0, panel.getHeight() - culmHeight
					+ BORDER_HEIGHT);
			choice.addMouseListener(this);
			choice.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			choice.setVisible(false);
			panel.add(choice);
		}// for
	}// createGameChoices(JPanel)

	/**
	 * Returns a Collection of student names from the database
	 * 
	 * @return the collection of student names from the database
	 */
	private Collection<String> getStudentNames() {
		Collection<String> retVal = null;

		// retVal = new ArrayList<String>();
		retVal = dbc.getClassList(className);

		if (retVal == null) {
			retVal = new ArrayList<String>();
		}// if

		return retVal;
	}// getStudentNames()

	// ActionListener methods
	/**
	 * Handles the teacher login and quit buttons
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void actionPerformed(ActionEvent ev) {
		JButton source = (JButton) ev.getSource();

		// identify button then do action
		if (source == adminButton) {
			// set the adminButton to be "on top"
			setComponentZOrder(adminButton, 0);
			setComponentZOrder(quitButton, 1);
			// show a new password box
			new PasswordDialog();
		} else if (source == quitButton) {
			// set the quitButton to be "on top"
			setComponentZOrder(adminButton, 1);
			setComponentZOrder(quitButton, 0);
			// close the window
			close();
		}// if-else
	}// actionPerformed(ActionEvent)

	// MouseListener methods & members
	/** the currently selected student label */
	private JLabel selected;

	/** true iff a game choice has been clicked */
	private boolean clicked;

	/** the selected game type */
	private int selectedType;

	/**
	 * Handles mouse clicks on the labels
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void mouseClicked(MouseEvent ev) {
		if (ev.getSource() instanceof JLabel) {
			final JLabel source = (JLabel) ev.getSource();
			Icon icon = source.getIcon();

			// ensure we don't try to open more than one game at a time
			if (!clicked) {
				// if a game choice hasn't been clicked already, start a game
				// based on which one has been
				if (icon == LOGIN_BUTTON_ADD) {
					clicked = true;
					selectedType = GameManager.ADD_SUBT;
				} else if (icon == LOGIN_BUTTON_MULT) {
					clicked = true;
					selectedType = GameManager.MULT_DIVIDE;
				} else if (icon == LOGIN_BUTTON_MIX) {
					clicked = true;
					selectedType = GameManager.MIX;
				}// if-else

				// if a game has been clicked, start a new game of the selected
				// type
				if (clicked) {
					// starting a new game with the selected student
					// name; had to wrap a Thread around this to make it work
					new Thread() {
						public void run() {
							parent.remove(loginPanel);
							Student selectedStudent = dbc.getStudentSummary(
									className, selected.getText());
							new GameManager(selectedStudent, parent,
									selectedType, dbc);
						}
					}.start();
				}// if
			}// if-else

		}// if
	}// mouseClicked(MouseEvent)

	/**
	 * Begins scrolling the students' names when mouse is pressed
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void mousePressed(MouseEvent ev) {
		// if (ev.getSource() instanceof JLabel) {
		// Icon icon = ((JLabel) ev.getSource()).getIcon();
		//
		// if (icon == RIGHT_ARROW || icon == RIGHT_ARROW_SEL) {
		// shiftThread.setRunning(-1);
		// } else if (icon == LEFT_ARROW || icon == LEFT_ARROW_SEL) {
		// shiftThread.setRunning(1);
		// }// if-else
		// }// if
	}// mousePressed(MouseEvent)

	/**
	 * Stops scrolling students' names when mouse is released
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void mouseReleased(MouseEvent ev) {
		// shiftThread.setRunning(0);
	}// mouseReleased(MouseEvent)

	/**
	 * Handles icon changing and game choice menu visibility
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void mouseEntered(MouseEvent ev) {
		if (ev.getSource() instanceof JLabel) {
			JLabel source = (JLabel) ev.getSource();
			Icon icon = source.getIcon();

			// identify the entered label based on icon
			if (icon == LOGIN_BUTTON) {
				// show game choices on a mouse-over on a student name
				showChoices(source);
				if (selected != null && selected != source) {
					selected.setIcon(LOGIN_BUTTON);
				}// if
				selected = source;
			} else if (icon == RIGHT_ARROW) {
				// scrolling right
				hideChoices();
				// only do scrolling stuff if we're enabled
				if (scrollingEnabled) {
					shiftThread.setRunning(-1);
					source.setIcon(RIGHT_ARROW_SEL);
				}// if
			} else if (icon == LEFT_ARROW) {
				// scrolling left
				hideChoices();
				// only do scrolling stuff if we're enabled
				if (scrollingEnabled) {
					shiftThread.setRunning(1);
					source.setIcon(LEFT_ARROW_SEL);
				}// if
			}// if-else
		} else if (ev.getSource() instanceof JButton) {
			JButton source = (JButton) ev.getSource();

			// reposition components on mouse over
			// setTabLocation(buttonTabShownLoc);

			// identify button then do action
			if (source == adminButton) {
				// set the adminButton to be "on top"
				setComponentZOrder(adminButton, 0);
				setComponentZOrder(quitButton, 1);
			} else if (source == quitButton) {
				// set the quitButton to be "on top"
				setComponentZOrder(adminButton, 1);
				setComponentZOrder(quitButton, 0);
			}// if-else

			// we want to hide our choices if we mouse over a button
			hideChoices();
			// } else if (ev.getSource() == buttonTab) {
			// // reposition components on mouse over
			// setTabLocation(buttonTabShownLoc);
			// // we want to hide our choices if we mouse over the button tab
			// hideChoices();
		} else {
			// if mouse enters anything that's not a
			// login button, hide game choices
			hideChoices();
		}// if-else
	}// mouseEntered(MouseEvent)

	/**
	 * Handles icon changing and game choice menu visibility
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void mouseExited(MouseEvent ev) {
		if (ev.getSource() instanceof JLabel) {
			JLabel source = (JLabel) ev.getSource();
			Icon icon = source.getIcon();

			// stop scrolling and set correct icons when mouse
			// leave the right and left scroll arrows
			if (icon == RIGHT_ARROW_SEL) {
				source.setIcon(RIGHT_ARROW);
			} else if (icon == LEFT_ARROW_SEL) {
				source.setIcon(LEFT_ARROW);
			}// if-else

			shiftThread.setRunning(0);
			// } else if (ev.getSource() instanceof JButton
			// || ev.getSource() == buttonTab) {
			// // reposition components on mouse exit
			// setTabLocation(buttonTabHiddenLoc);
		}// if-else
	}// mouseExited(MouseEvent)

	/**
	 * 
	 */
	// private void setTabLocation(Point loc) {
	// buttonTab.setLocation(loc);
	// adminButton.setLocation(adminButton.getX(), buttonTab.getY()
	// - adminButton.getHeight());
	// quitButton.setLocation(quitButton.getX(), adminButton.getY());
	// }// setTabLocation(Point)
	/**
	 * Hides the game choice menu
	 */
	private void hideChoices() {
		// make sure the last login button icon is reset
		if (selected != null) {
			selected.setIcon(LOGIN_BUTTON);
		}// if
		// hide the choice labels
		for (JLabel choice : gameChoices) {
			choice.setVisible(false);
		}// for
	}// hideChoices()

	/**
	 * Shows the game choice menu
	 * 
	 * @param source
	 *            the source label
	 */
	private void showChoices(JLabel source) {
		// set the "selected" state of the selected icon
		source.setIcon(LOGIN_BUTTON_SEL);
		// set the x location of each of the choices to the
		// source location then show them
		for (JLabel choice : gameChoices) {
			choice.setLocation(source.getX(), choice.getY());
			choice.setVisible(true);
		}// for
	}// showChoices(JLabel)

	/**
	 * Closes the program
	 */
	private void close() {
		parent.dispose();
	}// close()

	// password verifier for teacher login
	/**
	 * Popup dialog used to validate a password; launches the administration
	 * mode
	 */
	private class PasswordDialog extends JDialog implements ActionListener {

		/** input for username */
		private JTextField userText;

		/** input for password */
		private JPasswordField passText;

		/** owner frame */
		private GameWindow owner;

		/**
		 * Creates a new instance of <CODE>PasswordDialog</CODE>
		 */
		public PasswordDialog() {
			super(GameWindow.getInstance(), "Sign In", true);

			owner = GameWindow.getInstance();
			owner.repaint();
			// Components
			// the labels we use tell the user which field is for which
			JLabel userLabel = new JLabel("Username: ");
			JLabel passLabel = new JLabel("Password: ");

			// create the input fields
			userText = new JTextField();
			passText = new JPasswordField();

			// create the buttons used
			JButton okayButton = new JButton("Okay");
			JButton cancelButton = new JButton("Cancel");

			// "padding" distance between components
			int pad = 5;
			// component dimensions
			int labelWidth = 65;
			int labelHeight = 20;
			int buttonWidth = 75;
			int buttonHeight = 20;

			// Window

			// no layout manager; we'll position the components ourselves
			setLayout(null);

			// setting window to remove itself on close
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

			// sizing window to be only as large as we need
			setSize((3 * labelWidth) + (3 * pad) + BORDER_WIDTH,
					(2 * labelHeight) + buttonHeight + (4 * pad)
							+ BORDER_HEIGHT);

			// user cannot resize window
			setResizable(false);

			// window centered on screen
			setLocation((int) ((SCREEN_WIDTH - getWidth()) / 2),
					(int) ((SCREEN_HEIGHT - getHeight()) / 2));

			// not gonna explain each component individually
			// -labels and buttons are labelHeight high
			// -labels are labelWidth wide, text fields are 2*labelWidth wide
			// -userLabel is set at the upper left corner with 'pad' distance
			// between it and the edges of the frame
			// -all other components are based off this location

			// username label
			userLabel.setSize(labelWidth, labelHeight);
			userLabel.setLocation(pad, pad);

			// username text
			userText.setSize(2 * labelWidth, labelHeight);
			userText.setLocation(userLabel.getX() + labelWidth + pad, userLabel
					.getY());

			// password label
			passLabel.setSize(labelWidth, labelHeight);
			passLabel.setLocation(userLabel.getX(), userLabel.getY()
					+ labelHeight + pad);

			// password text
			passText.setSize(2 * labelWidth, labelHeight);
			passText.setLocation(passLabel.getX() + labelWidth + pad, passLabel
					.getY());

			// okay button
			okayButton.setSize(buttonWidth, buttonHeight);
			okayButton.setLocation(getWidth() / 2 - buttonWidth, passLabel
					.getY()
					+ labelHeight + pad);
			// action when the okay button is pressed
			okayButton.addActionListener(this);

			// cancel button
			cancelButton.setSize(okayButton.getSize());
			cancelButton.setLocation((getWidth() + pad) / 2, okayButton.getY());
			// action when the cancel button is pressed
			cancelButton.addActionListener(this);

			// adding components to the frame
			getContentPane().add(userLabel);
			getContentPane().add(passLabel);
			getContentPane().add(userText);
			getContentPane().add(passText);
			getContentPane().add(okayButton);
			getContentPane().add(cancelButton);

			// setting the okay button to be the default action for when the
			// return key is pressed
			getRootPane().setDefaultButton(okayButton);

			// show me
			setVisible(true);
		}// PasswordDialog()

		/**
		 * Handles mouse clicks on the buttons
		 * 
		 * @param ev
		 *            the event causing this call
		 */
		public void actionPerformed(ActionEvent ev) {
			if (ev.getSource() instanceof JButton) {
				JButton source = (JButton) ev.getSource();
				if (source.getText().equals("Okay")) {
					okayActionPerformed();
				} else if (source.getText().equals("Cancel")) {
					close();
				}// if
			}// if
		}// actionPerformed(ActionEvent)

		/**
		 * Checks the teacher's name and password; if valid, enters admin mode
		 */
		private void okayActionPerformed() {
			String userName = userText.getText();
			String pass = new String(passText.getPassword());

			// check for password validity
			if (dbc.checkTeacherPassword(userName, new MD5Hash().getHash(pass))) {
				pass = "";
				// stop the thread that controls scrolling
				shiftThread.kill();
				// clear the frame
				owner.getContentPane().remove(LoginPanel.loginPanel);
				// load the admin option pane into the frame
				owner.getContentPane().add(
						new AdministrationTabbedPane(userName),
						BorderLayout.CENTER);

				// refresh the frame
				owner.repaint();
				owner.validate();
				// close this dialog
				close();
			} else {
				JOptionPane.showMessageDialog(this,
						"Incorrent username and/or password.", "Login Error",
						JOptionPane.ERROR_MESSAGE);
				passText.setText("");
			} // if-else
		}// okayActionPerformed()

		/**
		 * Closes this dialog
		 */
		private void close() {
			passText.setText("");
			setVisible(false);
			dispose();
		}// close()

	}// PasswordDialog

}// LoginWindow
