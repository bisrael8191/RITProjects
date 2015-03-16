/**
 * ClassListModel.java
 * 
 * Version
 * $Id$
 * 
 * Revisions:
 * $Log$
 * Revision 1.6  2006/11/06 00:14:50  jmf8241
 * Some changes.
 *
 * Revision 1.5  2006/11/03 19:09:33  jmf8241
 * Added a method.
 *
 * Revision 1.4  2006/11/03 06:09:08  jmf8241
 * Should work.
 *
 * Revision 1.3  2006/11/03 04:04:45  jmf8241
 * Moved class.java to here.
 *
 * Revision 1.2  2006/10/31 04:16:39  jmf8241
 * Still Working on this.
 *
 * Revision 1.1  2006/10/31 03:19:44  jmf8241
 * Initial Revision.
 *
 */
package SchoolBoard;

import java.util.ArrayList;
import javax.swing.JLabel;

import DatabaseClient.ClassObject;
import GameLogic.GameHistory;
import GameLogic.Student;

/**
 * An implementation of AbstractListModel which sorts the data as it is added
 * 
 * @author Justin Field
 */
public class ClassListModel extends javax.swing.AbstractListModel {

    /*
     * Holds a list of classObjects.
     */
    private ArrayList<Class> classes;
    
    /*
     * Holds values that are not classes.
     */
    private ArrayList<String> staticStrings;
    
    /*
     * Holds the value of Strings.
     */
    private final static String INSTRUCTIONS = "Instructions";
    
    /*
     * ugly, but working; separator between questions types large enough to fill
     * the screen horizontally; currently 200 characters long
     */
    private static final String SEPARATOR = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";
    
    /**
     * Constructor
     */
    public ClassListModel(){
        classes = new ArrayList<Class>();
        staticStrings = new ArrayList<String>();
        staticStrings.add( INSTRUCTIONS );
    }
    
    /**
     * Returns the size of all items currently in the list.
     * 1 is added due to instructions being added.
     */
    public int getSize() {
        return classes.size() + staticStrings.size();
    }
    
    /**
     * Determines if the the index is in bounds.
     */
    private boolean isInBounds(int index) {
        return (index >= 0 && index < getSize());
    }
    
    /**
     * Returns the value of an element.
     */
    public String getElementAt(int index) {
        String retVal = "";
        if (isInBounds(index)) {
            if (index < staticStrings.size()) {
                retVal = staticStrings.get(index);
            }
            else {
                retVal = classes.get(index - staticStrings.size()).getNameOfClass();
            }// if-else
        }// if
        return retVal;
    }
    
    /**
     * Returns true if the element at index is a valid Class. true iff: the
     * index is in bounds of the list size and the index is not a SEPARATOR or
     * INSTRUCTIONS String
     * 
     * @param index
     *            the index to check
     * 
     * @return true if the element at index is a class string
     */
    public boolean isValidClass(int index) {
        return isInBounds(index)
                && !(getElementAt(index).equals(SEPARATOR) || getElementAt(
                        index).equals(INSTRUCTIONS));
    }// isValidStudent(int)
    
    /**
     * Returns an ArrayList of the Class objects held in this model
     * 
     * @return an ArrayList of the Class objects held in this model
     */
    public ArrayList<Class> getClassList() {
        return classes;
    }// getStudentList()
    
    /**
     * Returns the Class at index.
     *  
     * @param index
     *            the index of the Class to return
     * 
     * @return the Class at index; null if index is invalid
     * 
     */
    public Class getClass(int index) {
        Class retVal = null;

        // if element at index is valid, return the student at index
        if (isValidClass(index)) {
            retVal = classes.get(index - staticStrings.size());
        }// if

        return retVal;
    }// getStudent()
    
    /**
     * Adds the specified component to the list in alphabetical order.
     * 
     * @param theClass
     *            the class to be added
     */
    public void addElement(Class theClass) {
        // get student name for comparison
        String className = theClass.getNameOfClass();
        // used as escape from loop; true if the student has been added
        boolean isAdded = false;

        // if no data is being stored at this point, we need to add an initial
        // separator string to the staticStrings list
        
        if (getSize() == 1) {
          staticStrings.add(SEPARATOR);
        }

        // iterate through our clasess
        int i = 0;
        for (; i < classes.size() && !isAdded; i++) {
            // keep looping until we find a student whose name is "greater than"
            // the new student
            if (className.compareTo(classes.get(i).getNameOfClass()) < 0) {
                // set flag
                isAdded = true;

                // add the student and history data at the current index
                classes.add(i, theClass);
            }// if
        }// for

        // if the loop exits and no student has been added, we need to add it to
        // the end of the list
        if (!isAdded) {
            // add data to the end of the lists
            classes.add(theClass);
        }// if

        // inform the list that my contents have changed
        fireContentsChanged(this, 0, i);
    }// addElement(Student,ArrayList<GameHistory>)
    
    /**
     * Returns the index of a Class with name className
     * 
     * @param className
     *            the name of the Class to search for
     * 
     * @return the index of a Class with name className
     */
    public int getIndexOf(String className) {
        int index = 0;

        // search for studentName
        for (; index < getSize(); index++) {
            // if found, break and return the index we were on
            if (getElementAt(index).equals(className)) {
                break;
            }// if
        }// for
        return index;
    }// getHistory(Student)
}
