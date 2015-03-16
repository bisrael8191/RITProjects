
/**
 * MiscOptionTab.java
 * 
 * Version:
 * $Id$
 * 
 * Revisions:
 * $Log$
 * Revision 1.11  2006/11/07 05:29:22  jmf8241
 * Added not about needing to restart program to change IP.
 *
 * Revision 1.10  2006/11/06 02:13:03  exl2878
 * Removed text from server address text field
 *
 * Revision 1.9  2006/11/05 03:22:14  jmf8241
 * IP Address change now writes to a config file.
 *
 * Revision 1.8  2006/11/04 04:54:06  jmf8241
 * Added functionality to change the IP Address and added the help instructions to labels instead of buttons.
 *
 * Revision 1.7  2006/11/03 05:32:24  emm4674
 * centered components on screen
 *
 * Revision 1.6  2006/11/03 04:16:38  jmf8241
 * Added password change functionality.  Needs to be tested when we have a database up and running.  IP change still needs to be implemented.
 *
 * Revision 1.5  2006/10/31 01:35:58  jmf8241
 * Made some slight changes.
 *
 * Revision 1.4  2006/10/25 21:37:15  jmf8241
 * Added main method.
 *
 * Revision 1.3  2006/10/25 03:34:22  jmf8241
 * Functionality added.  Does not yet connect to the database.  Could posibly use an image or something similar to take up some of the empty space.  May possibly change the background color back to grey.
 *
 * Revision 1.2  2006/10/25 02:34:38  jmf8241
 * Decent looking GUI.  No functionality.
 *
 * Revision 1.1  2006/10/24 04:36:30  jmf8241
 * Intial Revision
 *
 */
package Administration;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import DatabaseClient.DBConnector;

/**
 * This class displays the miscellanous options tab(Password and
 * IP setting).
 * 
 * @author Justin Field
 */
public class MiscOptionTab extends JPanel implements ActionListener, ComponentListener {

    /**
     * Componets.
     */
    private JPanel passwordChangePanel; // JPanel for the password side.
    private JPanel passwordButtonPanel; // JPanel for password buttons.
    private JPanel passwordTextPanel;   // JPanel for text buttons.
    private JPanel IPChangePanel;   // JPanel for the IP side.
    private JPanel IPButtonPanel;   // JPanel for IP Buttons.
    private JPanel IPTextPanel;     // JPanel for IP Buttons.
    
    private JPasswordField newPassword; // The new Password.
    private JPasswordField confirmNewPassword; // Confirms the new Password.
    
    private JTextField IPAddress;   // Enter IP address here.
    
    private JButton changePassword; // This button changes your password.
    private JButton changeIPAddress; // This button changes your IPAddress.
    private JButton helpPassword;  // Displays the password help string in a popup box.
    
    private MD5Hash passwordHash;   // Hashes the password the user enters.
    /**
     * Instruction Strings
     */
    private static final String CHANGE_PASSWORD_INSTRUCTIONS = 
        "<html>" + "To change your password, enter your new password<br> "
        + "in both the \"New Password Textbox\" and the \"Confirm <br>"
        + "New Password Textbox\".  Then press the \"Change password\" button." + "</html>";
    
    private static final String CHANGE_IP_ADDRESS_INSTRUCTIONS = "<html>" 
        + "To change the IP Address of your database "
        + "enter the new<br> IP Address in the \"New IP Address"
        + "Textbox\". Then press the<br> \"Change IP Address\" button.<br>" 
        + "Note: Program must be restart for this to take effect. " + "</html>";
    
    private DBConnector dbc; // Used for connecting to the database.
    private String user;    // The currently logged in user.
    
    // Variables for read/write to config file.
    public static String CONFIG_FILE = "config.ini";
    
    /**
     * The constructor of the class.
     * 
     * @param theDbc - used for connecting to the database. 
     * @param user - the currently logged in user.
     */
    public MiscOptionTab ( String user, DBConnector theDbc ){
        this.user = user;
        dbc = theDbc;
        
        // Setup components.
        init();
    }
    
    /**
     * Sets up the componets.
     */
    public void init(){
        
        /*
         * Initialize the panels.
         */
        passwordChangePanel = new JPanel( new BorderLayout());
        IPChangePanel = new JPanel( new BorderLayout() );
        
        /*
         *  Initialize and place the password panels, labels
         *  and buttons.
         */
        newPassword = new JPasswordField();
        confirmNewPassword = new JPasswordField();
        changePassword = new JButton( "Change Password");
        changePassword.addActionListener( this );
        helpPassword = new JButton( "Change Password Help");
        helpPassword.addActionListener( this );
        
        /*
         * Add items to the password panel.
         */
        passwordButtonPanel = new JPanel( new FlowLayout() );
        passwordTextPanel = new JPanel( new GridLayout(0,1) );
        passwordTextPanel.add( new JLabel("Enter New Password:"));
        passwordTextPanel.add( newPassword );
        passwordTextPanel.add( new JLabel ("Confirm New Password:"));
        passwordTextPanel.add( confirmNewPassword );
        passwordButtonPanel.add( changePassword );
        passwordButtonPanel.setBackground( Color.WHITE );
        passwordTextPanel.setBackground( Color.WHITE );
        
        /*
         * Add the two panels to the main password panel.
         */
        passwordChangePanel.add( passwordTextPanel, "Center" );
        passwordChangePanel.add( passwordButtonPanel, "South" );
        
        /*
         * Add the password help.
         */
        JLabel passwordHelp = new JLabel( CHANGE_PASSWORD_INSTRUCTIONS );
        passwordHelp.setBackground( Color.WHITE );
        passwordChangePanel.add( passwordHelp, "North");
        passwordChangePanel.setBackground( Color.WHITE );
        
        /*
         * Initialize and palce the IP panels, labels, etc.
         */
        IPAddress = new JTextField();
        changeIPAddress = new JButton( "Change IP Address" );
        changeIPAddress.addActionListener( this );
        
        /*
         *  Add items to various IP panels. 
         */
        IPButtonPanel = new JPanel( new FlowLayout());
        IPTextPanel = new JPanel( new GridLayout(0,1) );
        JLabel IPHelp = new JLabel( CHANGE_IP_ADDRESS_INSTRUCTIONS );
        IPTextPanel.add( new JLabel( "Enter New IP Address:" ));
        IPTextPanel.add( IPAddress );
        IPButtonPanel.add( changeIPAddress );
        IPButtonPanel.add( IPHelp );
        IPButtonPanel.setBackground( Color.WHITE );
        IPTextPanel.setBackground( Color.WHITE );
        
        /*
         *  Add the items to the main IP panel.
         */
        IPChangePanel.add( IPTextPanel, "North" );
        IPChangePanel.add( IPButtonPanel, "Center" );
        IPChangePanel.setBackground( Color.WHITE );
       
        /*
         * Place and set the size of the items.
         */
        setLayout( null );
        passwordChangePanel.setSize( 340, 200 );
//        passwordChangePanel.setLocation( 0, 0 );
        add(passwordChangePanel);
        IPChangePanel.setSize( 340,200 );
//        IPChangePanel.setLocation( 0, 201 );
        add( IPChangePanel );
        setBackground( Color.WHITE );
        
        addComponentListener(this);
    }
    
    /**
     * Performs actions when buttons are clicked.
     * 
     * @param e - the source event.
     */
    public void actionPerformed(ActionEvent e) {
        String sNewPassword; // Changes char[] to String.  
        String sConfirmPassword; // Changes char[] to String.
        int changeIP;       // 1 if user presses yes.
        String hashedPW;    // The hashed password.
        
        /*
         * If the change password button is clicked.
         */
        
        if( e.getSource() == changePassword ){
            
            /*
             * Creates Strings.  Equals does not work with char[]
             */
            passwordHash = new MD5Hash();
            sNewPassword = new String( newPassword.getPassword() );
            sConfirmPassword = new String( confirmNewPassword.getPassword());
            
            if( sNewPassword.equals( sConfirmPassword)){
                 JOptionPane.showMessageDialog(null, "Password successfully changed."
                         ,"Password Change", JOptionPane.INFORMATION_MESSAGE);
                 
                 /* Hash the password and send over the database. */
                 hashedPW = passwordHash.getHash( new String (newPassword.getPassword()));
                 
                 try{
                     dbc.addTeacher( user, hashedPW );
                 }
                 catch( NullPointerException exc ){
                     System.err.println( "A Null Pointer excpetion has occurred when" 
                             + " attempting to change the teacher's password ");
                 }
                 
                 newPassword.setText( "" );
                 confirmNewPassword.setText("");
                 newPassword.requestFocus( true );
                 
             }
             else{
                 JOptionPane.showMessageDialog(null, "Password entered in confirm"
                         + " box does not match password entered in new box!", 
                         "Password Change Error", JOptionPane.ERROR_MESSAGE);
                 newPassword.setText( "" );
                 confirmNewPassword.setText("");
                 newPassword.requestFocus();
             }
        }
        
        /*
         * If the change IP Address Textbox is clicked.
         */
        else if( e.getSource() == changeIPAddress ){
           changeIP = JOptionPane.showConfirmDialog( null, "Are you sure you want to " 
                    + "change the IP Address?", "Change IP Address", 
                    JOptionPane.YES_NO_OPTION );
           
           if( changeIP == JOptionPane.YES_OPTION ){
               // Allows the user to change their IP address.
               
               // Determine if the config file already exists.
               File f = new File( CONFIG_FILE );
               String temp = null; // Holds the current default teacher.
               boolean fExist;  // True if f already exists.
               try{
                   fExist = f.exists();
                   if( fExist == true ){
                       BufferedReader dataIn = new BufferedReader( new FileReader(f));
                       temp = dataIn.readLine();
                   }
                   else{
                       f.createNewFile();
                   }
                   PrintWriter fout = new PrintWriter( new FileOutputStream(f));
                   if( fExist == true ){
                       fout.println( temp );
                   }
                   else{
                       fout.println( user );
                   }
                   fout.println( IPAddress.getText());
                   fout.flush();
                   fout.close();
               }
               catch( IOException exc ){
                   JOptionPane.showMessageDialog( null, "AN Error has Occurred", 
                           "Error", JOptionPane.ERROR_MESSAGE );
               }
               
               JOptionPane.showMessageDialog( null, "IP Address has been succusfully changed.", 
                       "IP Address Change", JOptionPane.INFORMATION_MESSAGE);
           }
        }
    }
    
    /**
     * Used for testing the class.
     */
    public static void main( String args[] ){
        JFrame testFrame = new JFrame();
        testFrame.setSize( 800, 600 );
        testFrame.setResizable( false );
        testFrame.add( new MiscOptionTab( "Teacher", null ));
        testFrame.setVisible( true );
    }

    //ComponentListener methods
    /**
     * Handles resizing of the panel. Should only be called once, 
     * but it's needed to center the subpanels.
     * 
     * @param ev the event causing this call
     */
    public void componentResized(ComponentEvent ev) {
    	passwordChangePanel.setLocation(
    			(getWidth() - passwordChangePanel.getWidth())/2,
    			(getHeight() - passwordChangePanel.getHeight() - IPChangePanel.getHeight())/2
    			);
    	IPChangePanel.setLocation(
    			(getWidth() - IPChangePanel.getWidth())/2,
    			passwordChangePanel.getY() + passwordChangePanel.getHeight()
    			);
	}
    
	public void componentHidden(ComponentEvent ev) {
	}

	public void componentMoved(ComponentEvent ev) {
	}

	public void componentShown(ComponentEvent ev) {
	}
}
