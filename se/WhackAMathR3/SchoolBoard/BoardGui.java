/**
 * 
 */
package SchoolBoard;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.JFrame;
import javax.swing.event.ListSelectionEvent;

import Administration.AdminOptionTab;
import Administration.AdministrationTabbedPane;
import DatabaseClient.DBConnector;

/**
 * This class allows the user to view stats on entire classes.
 * 
 * @author Justin Field
 */
public class BoardGui extends AdminOptionTab implements ActionListener, WindowListener {

    /**
     * The instructions for the pane.
     */
    private static final String INSTRUCTIONS = "<html><center> To view the statistics of a class:<br> " +
            " 1) Select the class name in the list on the left.</center></html>";
    
    /**
     * JList Header
     */
    private static final String CLASSES = "Classes";
    
    /**
     * Holds list of JLabels that display data tables.
     */
    private static final JLabel[] classDataLabels = new JLabel[]{
        new JLabel("Class Name:"),
        new JLabel("Total Number of Games Played: "),
        new JLabel("Total Number of Questions Answered Correctly: ")};
    
    /** 
     * Dyanamic Labels holding the student data.
     */
    private JLabel[] classData;
    
    /**
     * Holds the class data.
     */
    private ClassListModel dataModel;
    
    /**
     * Holds the list of classes..
     */
    private ArrayList<Class> classList;
    
    /**
     * Connects to the database.
     */
    private DBConnector dbc;
   
    
    /**
     * String holding path of Config file.
     */
    private String BOARD_CONFIG_FILE = "boardconfig.ini"; 
    
    /**
     * Button for IP change.
     */
    private JButton IPChange;
    
    /**
     * Constructor for the class.
     * 
     * @param user - the user of the pane.(Will always be school board.
     * @param thePane - Does nothing.
     */
    public BoardGui(String user, AdministrationTabbedPane thePane) {
        super(user, null);
        
        
        File f = new File( BOARD_CONFIG_FILE );
        IPChange = new JButton( "Change IP" );
        IPChange.addActionListener( this );
        
        String result;  // Result of reading from the file.
                        // If null, exit the program.
        String IP;      // The IP address we are trying to connect to.
        
        /*
         * Determine if the file exists.  If it already does
         * read in the data.  If not create it.
         */
        try{
            if( f.exists() ){
                BufferedReader dataIn = new BufferedReader( new FileReader(f) );
                IP = dataIn.readLine();
                dbc = DBConnector.getInstance( IP );
                //dbc.connectToDB( IP );
                
                while( !dbc.isConnected() ){
                    result = updateIP();
                        if( result == null ){
                            System.exit( 0 );
                        }
                    dataIn.close();
                    dataIn = new BufferedReader( new FileReader(f) );
                    dbc.connectToDB( dataIn.readLine() );
                }
            }
            
            else{
                do{
                    result = updateIP();
                    if( result == null ){
                        System.exit( 0 );
                    }
                    BufferedReader dataIn = new BufferedReader( new FileReader(f) );
                    IP = dataIn.readLine();
                    dbc = DBConnector.getInstance( IP );
                   // dbc.connectToDB( IP );
                    dataIn.close();
                }while ( !dbc.isConnected() );
            }
        }
        catch( IOException e ){
            
        }
        
        classList = new ArrayList<Class>();

        // Setup the componets.
        init();
    }

    /**
     * Sets up the componets.
     */
    protected void init() {
        // Set the instruction label.
        setInstructions( INSTRUCTIONS );
        setTitleText( CLASSES );
        addButton( IPChange );
        
        // Initialize array.
        classData = new JLabel[ classDataLabels.length];
        
        // Static sizes of length and width between componets.
        int labelWidth = 300;
        int labelHeight = 30;
        int pad = 5;
        
        // Position each data label on the right column.
        for( int i = 0; i < classDataLabels.length; i++ ){
            // Set each label to the same size.
            classDataLabels[i].setSize( labelWidth, labelHeight );
            
            // The x location of each label is the same.
            // The y location depends on the previous label.
            classDataLabels[i].setLocation( pad, i * labelHeight );
            
            // Initially not visible.
            classDataLabels[i].setVisible( false );
            
            // add it to the rightPanel.
            rightPanel.add( classDataLabels[i] );
        
            // Setup the statistic labels next to the other labels.
            classData[i] = new JLabel("");
            classData[i].setSize( labelWidth, labelHeight );
            classData[i].setLocation( classDataLabels[i].getX() 
                    + classDataLabels[i].getWidth(), classDataLabels[i].getY());
            classData[i].setVisible( false );
            rightPanel.add( classData[i] );
            
        }
        // Load class data from the database.
        getClassData();
    }
    
    /**
     * Gets the class data from the database.
     */
    private void getClassData(){
       ArrayList<String> classNames = new ArrayList<String>();
       
       // Get the list of class names from the database.
       classNames = dbc.getClassNames();
       
       // If nothing is there don't do anything.
       if( classNames != null ){
       
           // Create new classes using each name.
           for( int i = 0; i < classNames.size(); i++ ){
               classList.add( new Class( classNames.get(i), dbc ));
           }
           
           // Add the classes.
           for( int i = 0; i < classList.size(); i++ ){
               dataModel.addElement( classList.get(i) );
           }
       }
    }


    /**
     * Called when the dataList selected value has changed. Loads the selected
     * class data onto the rightPanel and handles component visibility.
     * 
     * @param ev
     *            the event causing this call
     */
    public void valueChanged(ListSelectionEvent ev) {
        // when a new item is selected, set the scrollbar to initially be
        // "unscrolled" and disabled
        scrollBar.setValue(0);
        scrollBar.setEnabled(false);

        // get the source and selectedIndex for easy reference
        JList source = (JList) ev.getSource();
        int selectedIndex = source.getSelectedIndex();

        // if the selected element is a student...
        if (dataModel.isValidClass(selectedIndex)) {
            // grab the selected student name
            String className = dataModel.getElementAt(selectedIndex);
            // load the selected student data
            loadClassData(className);

            // invisibilize the instructions and show the other data labels
            setInstrVisible(false);
            for( JLabel l: classDataLabels ){
                l.setVisible( true );
            }// for
            for (JLabel l : classData) {
                l.setVisible(true);
            }// for
        } else {
            // if the selected element isn't a class...

            // show the instructions and hide the other data labels
            setInstrVisible(true);
            for (JLabel l : classDataLabels) {
                l.setVisible(false);
            }// for
            for (JLabel l : classData) {
                l.setVisible(false);
            }// for
        }// if-else

        // force the right panel to lay itself out again
        componentResized(new ComponentEvent(rightBackPanel,
                ComponentEvent.COMPONENT_RESIZED));
    }// valueChanged(ListSelectionEvent)
    
    /**
     * Loads the data of the class with className into the data labels
     * 
     * @param className
     *            the student data to display
     */
    private void loadClassData(String className) {
        // if the name is empty, set all the labels to be empty;
        // else load the data
        if (className.equals("")) {
            for (JLabel l : classData) {
                l.setText("");
            }
        } else {
            // grab student data from the dataModel
            Class c = dataModel.getClass(dataModel.getIndexOf(className));
            
            // load it into the labels
            classData[0].setText(c.getNameOfClass());
            classData[1].setText(String.valueOf(c.getTotalGamesPlayed()));
            classData[2].setText(String.valueOf(c.getTotalNumberCorrect()));

            // now we lay out the student game history labels
            int pad = 5;
            JLabel temp = null;
            // all labels will have the same size as the first data display
            // label and will have their locations based off of the last
            // display label
            Dimension labelSize = classDataLabels[0].getSize();
            Point labelLoc = classDataLabels[classDataLabels.length - 1]
                    .getLocation();

            rightPanel.repaint();
        }

    }// loadStudentData(String)

    /**
     * Handles the scrolling of the rightPanel when class data runs
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
    
    // ComponentListener methods
    /**
     * Called when rightBackPanel is resized; if an invalid element is selected,
     * we will resize rightPanel to it's parent panel. Otherwise we want to let
     * the loadClassData method size it
     * 
     * @param ev
     *            the event causing this call
     */
    public void componentResized(ComponentEvent ev) {
        // resize if we can
        if (!dataModel.isValidClass(getDataList().getSelectedIndex())) {
            rightPanel.setSize(rightBackPanel.getSize());
        }// if

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
        dataModel = new ClassListModel();
        return dataModel;
    }// createListModel()
    
    /**
     * Updates/creates the Board_Config_File
     */
    public String updateIP(){
        File theFile = new File( BOARD_CONFIG_FILE );
        String IP;  // Read in from an input box.
        String result = null; // Holds results of Input dialog.
        
        result = JOptionPane.showInputDialog( null, "Enter the address " 
        							+ "of the WhackAMath Database:", 
        							"IP Address Setup", JOptionPane.QUESTION_MESSAGE);
        
        if( result == null ){
            
        }
        else{
            try{
                PrintWriter fout = new PrintWriter( new FileOutputStream( theFile ));
                fout.println( result );
                fout.flush();
                fout.close();
            
            JOptionPane.showMessageDialog( null, "<html>IP Address has been successfully changed.<br>" 
                    + "If you are already connected to the database,<br> " 
                    + "restart the program to connect to the new database.</html>", 
                    "IP Adress Setup", JOptionPane.INFORMATION_MESSAGE);
            }
            catch( FileNotFoundException e ){
                System.err.println( e.getMessage());
            }
        }
        return result;
    }
    
    /**
     * Listener for button.
     * 
     * @param e - the event source.
     */
    public void actionPerformed(ActionEvent e) {
        if( e.getSource() == IPChange ){
            updateIP();
        }
    }
    
    
    /**
     * Used for running the class.
     * 
     * @param args - Does nothing.
     */
    public static void main(String[] args) {
        JFrame aFrame = new JFrame( "School Board Access" );
        BoardGui theGui = new BoardGui( "School Board", null );
        aFrame.add( theGui );
        aFrame.setSize( 800, 600 );
        aFrame.setVisible( true );
        aFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        DBConnector.getInstance().killConnection();
    }
    
    // Does nothing.
    public void windowOpened(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowClosing(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowIconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowDeiconified(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowActivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowDeactivated(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }

    public void windowClosed(WindowEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
