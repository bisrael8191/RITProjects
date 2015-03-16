/**
 * ControlPanelGUI.java
 * 
 * Version:
 * $Id: ControlPanelGUI.java,v 1.6 2007/05/20 07:05:20 bisrael Exp $
 * 
 * Revisions:
 * $Log: ControlPanelGUI.java,v $
 * Revision 1.6  2007/05/20 07:05:20  bisrael
 * Minor updates in the action methods.
 *
 * Revision 1.5  2007/05/15 05:50:43  bisrael
 * Minor changes
 *
 * Revision 1.4  2007/05/14 09:10:25  bisrael
 * Made some changes so that it now uses a JList instead of a JTable.
 *
 *
 */
 
package gui;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
//import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.Iterator;


public class ControlPanelGUI extends JFrame implements ActionListener{

	
	//JTable with list of current avaliable problems
	//private JTable problemList;
	private JList problemList;
	
	private ArrayList<String> problems;
	
	//Buttons on the Frame
	JButton startButton;
	JButton stopButton;
	JButton createButton;
	JButton quitButton;
	
	//The interface to call fo changes
	ControlPanelInter GUIinterface;
	
	//Holds table model and information;
	//ProblemTableModel tableModel;
	
	
	/*
	 * Contruct the GUI
	 */
	public ControlPanelGUI(ControlPanelInter inter){
		super("Distributed Fractal Solver");
		
		GUIinterface = inter;
		
		setSize(600,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    public void windowClosing(WindowEvent winEvt) {
		        //When the window is closed from the window manager
		    	GUIinterface.quit();
		    }
		});
		
		problems = new ArrayList<String>();
		
		Container c = getContentPane();
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
		
		//Create The scrollPane with the table
		//String[] columnNames = {"Current Available Problems", "Working On"};
		
//		tableModel = new ProblemTableModel();
//		
//		problemList = new JTable(tableModel); 
//		problemList.setPreferredSize(new Dimension(275,300));
//		problemList.getColumnModel().getColumn(0).setMaxWidth(200);
//		problemList.getColumnModel().getColumn(1).setMaxWidth(75);
//		problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		
//		problemList.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new JCheckBox()));
//		problemList.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer());
		
		problemList = new JList();
		problemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		//problemList.setCellRenderer(new JlabelRenderer());
		problemList.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged( ListSelectionEvent event ){
				if(problemList.getSelectedIndex() > -1){
					startButton.setEnabled(true);
					stopButton.setEnabled(true);
				}else{
					startButton.setEnabled(false);
					stopButton.setEnabled(false);
				}
			}
		});
		
		JScrollPane problemScrollPane = new JScrollPane();
		problemScrollPane.setPreferredSize(new Dimension(275,300));

		problemScrollPane.setViewportView(problemList);
		JPanel leftPanel = new JPanel();
		leftPanel.add(problemScrollPane);
		
		panel.add(leftPanel, BorderLayout.WEST);
		
		//Create ButtonPanel
		JPanel buttonPanel = new JPanel(new GridLayout(4,1));
		startButton = new JButton("Start");
		startButton.setEnabled(false);
		stopButton = new JButton("Stop");
		stopButton.setEnabled(false);
		createButton = new JButton("Create Problem");
		quitButton = new JButton("Quit");
		
		buttonPanel.setPreferredSize(new Dimension(150, 200));
		
		buttonPanel.add(startButton);
		buttonPanel.add(stopButton);
		buttonPanel.add(createButton);
		buttonPanel.add(quitButton);
		startButton.addActionListener(this);
		stopButton.addActionListener(this);
		createButton.addActionListener(this);
		quitButton.addActionListener(this);
		
		
		//buttonPanel.setBorder(BorderFactory.createEmptyBorder(60,60,60,60));
		
		panel.add(buttonPanel, BorderLayout.EAST);
		
		c.add(panel);
		
		//repaint();
		validate();
	}
	
	
	
	
	public void actionPerformed(ActionEvent e){
		
		if(e.getActionCommand() == "Start"){
			String selected = (String)problemList.getSelectedValue();
			if(!selected.contains(" -- Running")){
				GUIinterface.start(selected);
				problemList.clearSelection();
			}else{
				JOptionPane.showMessageDialog(this, "Already solving this problem, please select a different one", 
						"", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getActionCommand() == "Stop"){
			String selected = (String)problemList.getSelectedValue();
			if(selected.contains(" -- Running")){
				GUIinterface.stop(selected.replace(" -- Running", ""));
				problemList.clearSelection();
			}else{
				JOptionPane.showMessageDialog(this, "Problem must be running first", 
						"", JOptionPane.ERROR_MESSAGE);
			}
		}
		else if(e.getActionCommand() == "Create Problem"){
		    JFileChooser chooser = new JFileChooser();
		    // Note: source for ExampleFileFilter can be found in FileChooserDemo,
		    // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
		    ExtensionFileFilter filter = new ExtensionFileFilter();
		    filter.addExtension("class");
		    filter.setDescription("Java class files");
		    chooser.setFileFilter(filter);
		    int returnVal = chooser.showOpenDialog(this);
		    if(returnVal == JFileChooser.APPROVE_OPTION) {
		       //Open a create problem GUI, passing it the file you opened
		       GUIinterface.createNew(chooser.getSelectedFile());
		    }
		}
		else if(e.getActionCommand() == "Quit"){
			
			GUIinterface.quit();
		}
	}
	
	
	/**
	 * Sets the list of active problems
	 */
	public void setProblemList(Map currentProblems){
		//Clear the list
		problems.clear();
		
		//Iterate through the map to create the new list
		Set entries = currentProblems.entrySet();
		for(Iterator i = entries.iterator(); i.hasNext();){
			Map.Entry tmp = (Map.Entry)i.next();
			String probName = (String)tmp.getKey();
			Boolean working = (Boolean)tmp.getValue();

			//Create the JLabels for the JList
			if(working){
				problems.add(probName + " -- Running");
			}else{
				problems.add(probName);
			}
		}
		
		//Set the new list
		problemList.setListData(problems.toArray());
		
		//Redraw the list
		problemList.revalidate();
	}
}
