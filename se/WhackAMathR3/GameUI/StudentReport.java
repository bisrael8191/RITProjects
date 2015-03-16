/*
 * StudentReport.java
 * 
 * Version:
 *     $Id: StudentReport.java,v 1.20 2006/11/06 04:11:48 emm4674 Exp $
 *
 * Revisions:
 *     $Log: StudentReport.java,v $
 *     Revision 1.20  2006/11/06 04:11:48  emm4674
 *     loads the login panel with the default teacher
 *
 *     Revision 1.19  2006/11/01 02:35:26  emm4674
 *     finished commenting
 *     removed useless constructor
 *     no longer accepts GameWindow in a constructor
 *
 *     Revision 1.18  2006/11/01 02:20:39  emm4674
 *     no longer accepts a GameWindow in the constructor
 *
 *     Revision 1.17  2006/10/26 06:01:27  exl2878
 *     Updated to use method DBConnector.getInstance()
 *
 *     Revision 1.16  2006/10/26 04:37:19  exl2878
 *     Updated to use method GameWindow.getInstance()
 *
 *     Revision 1.15  2006/10/26 00:15:06  emm4674
 *     loads login panel on "close" button hit
 *
 *     Revision 1.14  2006/10/21 21:32:17  emm4674
 *     *** empty log message ***
 *
 *     Revision 1.13  2006/10/21 21:31:04  emm4674
 *     *** empty log message ***
 *
 *     Revision 1.12  2006/10/21 05:41:05  emm4674
 *     is now a jpanel
 *     constructors updated to accept a parent window
 *     resized image and repositioned components to fir
 *
 *     Revision 1.11  2006/10/20 19:12:13  emm4674
 *     can accept a Student object in constructor
 *
 *     Revision 1.10  2006/10/20 02:30:32  emm4674
 *     had to change format a bit after correcting a mistake in the image
 *
 *     Revision 1.9  2006/10/12 23:42:50  emm4674
 *     removed unnecessary labels and panels
 *     added date label
 *     lined up text with 800x600 image
 *
 *     Revision 1.8  2006/10/12 06:56:44  emm4674
 *     initial attempt to put a certificate type background on
 *
 *     Revision 1.7  2006/10/10 23:54:00  emm4674
 *     added temp fix to make the background white
 *
 *     Revision 1.6  2006/10/09 02:20:43  emm4674
 *     added check to make sure the percentage calculation doesn't perform div by 0
 *
 *     Revision 1.5  2006/10/09 02:02:35  emm4674
 *     moved student name to a label; no longer the title
 *     created labels to display the percentage
 *
 *     Revision 1.4  2006/10/09 01:42:01  exl2878
 *     Implemented print button functionality
 *
 */

package GameUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import Login.LoginPanel;

/**
 * @author Eric M
 */
public class StudentReport extends JPanel {

	/** certificate icon */
	private static final ImageIcon CERT_ICON = new ImageIcon("images"
			+ File.separator + "certificate.png");

	/** the name of the student */
	private String studentName;

	/** total score */
	private long score;

	/** number of correct questions the student answered */
	private long numCorrect;

	/** total number of questions the student attempted */
	private long totalQuestions;

	/** number of questions answered on the first try */
	private long numCorrectFirst;

	/** percont correct */
	private long percentCorrect;

	/** Container that holds the game report labels. */
	private JPanel centerPanel;

	/** reference to my parent window */
	private GameWindow parent;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// GameWindow gw = GameWindow.getInstance();

		// add the login panel
		// gw.add(new StudentReport("Student Name", 5000, 50, 100, 25, gw));
		// gw.validate();
	}

	/**
	 * Creates a new instance of StudentReport
	 * 
	 * @param studentName
	 *            the name of the student
	 * @param score
	 *            the student's score
	 * @param numCorrect
	 *            the number of correct answers
	 * @param totalQuestions
	 *            the total number of questions answered
	 * @param numCorrectFirst
	 *            the number correct on the first try
	 */
	public StudentReport(String studentName, int score, int numCorrect,
			int totalQuestions, int numCorrectFirst) {

		this.parent = GameWindow.getInstance();

		// initialize student data
		this.studentName = studentName;
		this.score = score;
		this.numCorrect = numCorrect;
		this.totalQuestions = totalQuestions;
		this.numCorrectFirst = numCorrectFirst;

		// calculate percent correct; check for div by 0
		percentCorrect = (totalQuestions > 0) ? (100 * numCorrect / totalQuestions)
				: 0;

		// create visual components
		init();

	}// StudentReport()

	/**
	 * Initializes components of this StudentReport
	 */
	private void init() {
		// window properties
		setLayout(new BorderLayout());

		// set the title
		parent.setTitle("Student Report");

		// change font/size
		// create static labels
		JLabel studentNameLabel = new JLabel(studentName);
		JLabel dateLabel = new JLabel(DateFormat.getDateInstance().format(
				new Date()));
		JLabel scoreLabel = new JLabel(String.valueOf(score));
		JLabel numCorrectLabel = new JLabel(String.valueOf(numCorrect));
		JLabel totalQuestLabel = new JLabel(String.valueOf(totalQuestions));
		JLabel actualPercentLabel = new JLabel(percentCorrect + "%");
		JLabel correctFirstLabel = new JLabel(String.valueOf(numCorrectFirst));

		// create buttons
		JButton printButton = new JButton("Print");
		// move me somewhere else later?
		printButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				printData();
			}
		});// addActionListener()

		JButton closeButton = new JButton("Close");
		// move me somewhere else later?
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				close();
			}
		});// addActionListener()

		// set all components to the same size
		Dimension componentSize = new Dimension(300, 60);
		studentNameLabel.setSize(componentSize);
		dateLabel.setSize(componentSize);
		scoreLabel.setSize(componentSize);
		numCorrectLabel.setSize(componentSize);
		totalQuestLabel.setSize(componentSize);
		actualPercentLabel.setSize(componentSize);
		correctFirstLabel.setSize(componentSize);

		// set all components to the same font
		Font componentFont = new Font("Trebuchet MS", Font.PLAIN, 16);
		studentNameLabel.setFont(componentFont);
		dateLabel.setFont(componentFont);
		scoreLabel.setFont(componentFont);
		numCorrectLabel.setFont(componentFont);
		totalQuestLabel.setFont(componentFont);
		actualPercentLabel.setFont(componentFont);
		correctFirstLabel.setFont(componentFont);

		studentNameLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// set components to their proper, static, positions
		studentNameLabel.setLocation((parent.getWidth() - studentNameLabel
				.getWidth()) / 2, 205);
		dateLabel.setLocation(327, 258);
		scoreLabel.setLocation(300, 292);
		numCorrectLabel.setLocation(334, 323);
		totalQuestLabel.setLocation(458, 323);
		actualPercentLabel.setLocation(334, 355);
		correctFirstLabel.setLocation(421, 385);

		// add components
		// panels separating main content pane
		// create the background panel with proper image
		centerPanel = new BackgroundPanel(CERT_ICON);
		JPanel southPanel = new JPanel();

		// add labels to center panel
		centerPanel.add(studentNameLabel);
		centerPanel.add(dateLabel);
		centerPanel.add(scoreLabel);
		centerPanel.add(numCorrectLabel);
		centerPanel.add(totalQuestLabel);
		centerPanel.add(actualPercentLabel);
		centerPanel.add(correctFirstLabel);

		// add buttons to south panel
		southPanel.add(printButton);
		southPanel.add(closeButton);

		// add subpanels to content pane
		add(centerPanel, BorderLayout.CENTER);
		add(southPanel, BorderLayout.SOUTH);

		// white background for consistancy
		southPanel.setBackground(Color.WHITE);

	}// init()

	/**
	 * Close the window
	 */
	private void close() {
		// read default class from the database
		String className = "Admin";
		try {
			BufferedReader in = new BufferedReader(new FileReader("config.ini"));

			className = in.readLine();

			in.close();
		} catch (IOException ex) {
		}// try-catch

		parent.remove(this);
		parent.add(new LoginPanel(className));
	}// close()

	/**
	 * Print the report
	 */
	private void printData() {
		PrintUtility.printComponent(centerPanel, PageFormat.LANDSCAPE);
	}// printData()

}// StudentReport
