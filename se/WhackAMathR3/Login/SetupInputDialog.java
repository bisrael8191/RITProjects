/*  SetupInputDialog.java
 * 
 *  Version:
 *  	$Id: SetupInputDialog.java,v 1.10 2006/11/07 05:55:16 exl2878 Exp $
 *  
 *  Revisions:
 *  	$Log: SetupInputDialog.java,v $
 *  	Revision 1.10  2006/11/07 05:55:16  exl2878
 *  	Changed instruction message and made default strings empty.
 *  	
 *  	Revision 1.9  2006/11/07 03:38:24  emm4674
 *  	added instructions & tooltips
 *  	
 *  	Revision 1.8  2006/11/06 01:34:53  emm4674
 *  	set the default default name to "Admin"
 *  	
 *  	Revision 1.7  2006/11/05 04:18:14  exl2878
 *  	Clicking the X on the title bar now exits the program
 *  	
 *  	Revision 1.6  2006/11/05 02:51:23  exl2878
 *  	Removed format mask for IP address so that hostnames can be used
 *  	
 *  	Revision 1.5  2006/11/04 23:54:27  exl2878
 *  	Configuration information is once again stored in config file
 *  	
 *  	Revision 1.4  2006/11/03 03:49:04  exl2878
 *  	Added header comments to file
 *  	
 */

package Login;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SetupInputDialog extends JDialog implements ActionListener,
		DocumentListener, WindowListener {

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

	/** */
	private static final String DEFAULT_CLASS = "";

	/** */
	private static final String CLASS_TOOLTIP = "The class which is loaded when the program is run.";

	/** */
	private static final String DEFAULT_IP = "";

	/** */
	private static final String IP_TOOLTIP = "The remove server's address.";

	private JTextField classNameInput;

	private JTextField ipInput;

	private JButton okayButton;

	/**
	 * Used to determine whether the application should end
	 */
	private boolean okayButtonPressed;

	private File configFile;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new SetupInputDialog(new File("config.ini"));
	}

	/**
	 * Creates a new instance of SetupInputDialog
	 * 
	 * @param configFile
	 *            the configuration file to save to
	 */
	public SetupInputDialog(File configFile) {
		// create a modal dialog with no owner
		super((Frame) null, true);

		// reference to the file we're writing to
		this.configFile = configFile;

		// Dialog properties
		setTitle("Missing Information");
		setLayout(null);
		setResizable(false);
		addWindowListener(this);

		// Components
		// padding distance between components and borders
		int pad = 5;
		// size of our components
		int compWidth = 150;
		int compHeight = 20;
		int instrHeight = 60;

		// static labels
		// html'd to allow for multiple lines
		JLabel instructionLabel = new JLabel(
				"<html>Please enter the address of the server and the class that "
				+ "will be playing this game.  You should contact your IT " +
				"department if you do not know this information.</html>");

		JLabel classNameLabel = new JLabel("Default class name: ");
		classNameLabel.setToolTipText(CLASS_TOOLTIP);
		classNameLabel.setSize(compWidth, compHeight);

		JLabel ipLabel = new JLabel("Server Address: ");
		ipLabel.setToolTipText(IP_TOOLTIP);
		ipLabel.setSize(compWidth, compHeight);

		// input fields
		classNameInput = new JTextField(DEFAULT_CLASS);
		classNameInput.setToolTipText(CLASS_TOOLTIP);
		classNameInput.setSize(compWidth, compHeight);
		classNameInput.getDocument().addDocumentListener(this);

		// create our ip input field
		ipInput = new JTextField(DEFAULT_IP);
		ipInput.setToolTipText(IP_TOOLTIP);
		ipInput.setSize(compWidth, compHeight);
		ipInput.getDocument().addDocumentListener(this);

		// buttons
		okayButton = new JButton("Okay");
		okayButton.setSize(compWidth, compHeight);
		okayButton.addActionListener(this);
		okayButtonPressed = false;
		// set to a size based on how much space our components take up
		setSize(classNameLabel.getWidth() + classNameInput.getWidth() + 3 * pad
				+ BORDER_WIDTH, classNameLabel.getHeight()
				+ classNameInput.getHeight() + okayButton.getHeight() + 4 * pad
				+ BORDER_HEIGHT + instrHeight);
		setLocation((SCREEN_WIDTH - getWidth()) / 2,
				(SCREEN_HEIGHT - getHeight()) / 2);

		// set instruction size here
		instructionLabel.setSize(getWidth() - 2 * pad, instrHeight);

		// position components on screen
		instructionLabel.setLocation(pad, pad);
		classNameLabel.setLocation(instructionLabel.getX(), instructionLabel
				.getY()
				+ instructionLabel.getHeight());
		classNameInput.setLocation(classNameLabel.getX()
				+ classNameLabel.getWidth() + pad, classNameLabel.getY());

		ipLabel.setLocation(classNameLabel.getX(), classNameLabel.getY()
				+ classNameLabel.getHeight() + pad);
		ipInput.setLocation(ipLabel.getX() + ipLabel.getWidth() + pad, ipLabel
				.getY());

		okayButton.setLocation((getWidth() - okayButton.getWidth()) / 2,
				ipLabel.getY() + ipLabel.getHeight() + pad);

		// set default button
		getRootPane().setDefaultButton(okayButton);

		// add components
		add(instructionLabel);
		add(classNameLabel);
		add(ipLabel);
		add(classNameInput);
		add(ipInput);
		add(okayButton);

		// show me
		setVisible(true);
	}// SetupInputDialog()

	/**
	 * Called whenever the text in our input boxes changes. Used to check for
	 * valid input and set the okayButton enabled status accordingly. Since we
	 * use a MaskFormatter to format the ip address, we only need to check the
	 * class name.
	 */
	private void textChanged() {
		// button enabled if the class name text has length at least 1
		okayButton.setEnabled(classNameInput.getText().length() >= 1);
	}// textChanged()

	/**
	 * Saves the given data to the file defined above.
	 * 
	 * @param className
	 *            the default class name
	 * @param ipAddress
	 *            the server ip address
	 */
	private void saveData(String className, String ipAddress) {
		try {
			// create the file
			configFile.createNewFile();
			PrintWriter fout = new PrintWriter(new FileOutputStream(configFile));

			// write our data
			fout.println(className);
			fout.println(ipAddress);
			fout.flush();
			fout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}// try-catch

		// close the dialog
		setVisible(false);
		dispose();
	}// saveData(String,String);

	// ActionListener methods
	/**
	 * Called when the okay button is pressed. Creates necessary files and
	 * closes the dialog.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void actionPerformed(ActionEvent ev) {
		// save the data
		saveData(classNameInput.getText(), ipInput.getText());
		okayButtonPressed = true;
	}// actionPerformed(ActionEvent)

	// DocumentListener methods
	/**
	 * Simply passes on a call to textChanged to handle input checking.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void insertUpdate(DocumentEvent ev) {
		textChanged();
	}// insertUpdate(DocumentEvent)

	/**
	 * Simply passes on a call to textChanged to handle input checking.
	 * 
	 * @param ev
	 *            the event causing this call
	 */
	public void removeUpdate(DocumentEvent ev) {
		textChanged();
	}// removeUpdate(DocumentEvent)

	// WindowListener methods
	/**
	 * Catches the window closing event when the close button was pressed.
	 * Writes default values to file.
	 */
	public void windowClosing(WindowEvent ev) {
		// save the data
		saveData(DEFAULT_CLASS, DEFAULT_IP);
	}// windowClosing(WindowEvent)

	public void windowClosed(WindowEvent arg0) {
		if (!okayButtonPressed) {
			System.exit(0);
		}
	}

	// unused Listener methods
	public void changedUpdate(DocumentEvent ev) {
	}

	public void windowActivated(WindowEvent arg0) {
	}

	public void windowDeactivated(WindowEvent arg0) {
	}

	public void windowDeiconified(WindowEvent arg0) {
	}

	public void windowIconified(WindowEvent arg0) {
	}

	public void windowOpened(WindowEvent arg0) {
	}

}// SetupInputDialog
