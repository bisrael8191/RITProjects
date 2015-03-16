package Administration;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.AbstractListModel;

import GameLogic.Question;

/**
 * Keeps the data from the QuestionOptionTab list sorted and categorized
 * 
 * @author Eric M
 */
public class QuestionListModel extends AbstractListModel {

	/**
	 * ugly, but working; separator between questions types large enough to fill
	 * the screen horizontally; currently 200 characters long
	 */
	private static final String SEPARATOR = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	/** text for the instructions cell */
	private static final String INSTRUCTIONS = "Instructions";

	/** holds items at the top of the list */
	private ArrayList<String> staticStrings;

	/** actual data; stored as Strings, extracted as Questions */
	private ArrayList<ArrayList<String>> data;

	/**
	 * Creates a new instance of QuestionListModel
	 */
	public QuestionListModel() {
		// create staticStrings, defaulting with only the INSTRUCTIONS String
		// displayed
		staticStrings = new ArrayList<String>();
		staticStrings.add(INSTRUCTIONS);

		// create the data list
		data = new ArrayList<ArrayList<String>>();

		// data[0] = staticStrings
		// data[1] = addition questions
		// data[2] = subtraction questions
		// data[3] = multiplication questions
		// data[4] = division questions
		// data[5] = position questions
		data.add(staticStrings);
		data.add(new ArrayList<String>());// +
		data.add(new ArrayList<String>());// -
		data.add(new ArrayList<String>());// x
		data.add(new ArrayList<String>());// /
		data.add(new ArrayList<String>());// :

	}// QuestionListModel();

	/**
	 * Adds the specified Question to the list.
	 * 
	 * @param q
	 *            the Question to be added
	 */
	public void addElement(Question q) {
		// get the question in a displayable format
		String qstring = q.formatQuestion();

		// if no data is being stored at this point, we need to add an initial
		// separator string to the staticStrings list
		if (isEmpty()) {
			staticStrings.add(SEPARATOR);
		}

		// figure out the question type and add it to the correct list
		if (q.getTheType() == Question.Type.SUM) {
			addToList(qstring, 1);
		} else if (q.getTheType() == Question.Type.DIFF) {
			addToList(qstring, 2);
		} else if (q.getTheType() == Question.Type.MULT) {
			addToList(qstring, 3);
		} else if (q.getTheType() == Question.Type.DIV) {
			addToList(qstring, 4);
		} else if (q.getTheType() == Question.Type.POSITION) {
			addToList(qstring, 5);
		}

		// inform the list that my contents have changed
		fireContentsChanged(this, 0, getSize());
	}// addElement(Question)

	/**
	 * Returns the length of the list.
	 * 
	 * @return the length of the list
	 */
	public int getSize() {
		// sum up the lengths of the individual lists and return it
		int len = 0;
		for (ArrayList<String> d : data) {
			len += d.size();
		}
		return len;
	}// getSize()

	/**
	 * Removes the element at the specified position in this list. Returns the
	 * element that was removed from the list.
	 * 
	 * @param index
	 *            the index of the element to removed
	 * 
	 * @return the removed element
	 */
	public String remove(int index) {
		String retVal = null;
		// if the element at index is not a valid question don't do anything;
		// return null
		if (isValidQuestion(index)) {
			// used as a loop escape; true iff an element has been removed
			boolean removed = false;

			// iterate through our question type lists
			for (int i = 0; i < data.size() && !removed; i++) {
				// if the index is in bounds of the current list size remove it,
				// otherwise subtract the current list size from the index and
				// continue with the next one
				if (index < data.get(i).size()) {
					// remove the element
					retVal = data.get(i).remove(index);
					// if the current list only contains the separator string,
					// remove it
					if (data.size() > 2 && data.get(i).size() == 1) {
						data.get(i).remove(0);
					}
					// flag is now true
					removed = true;
				}// if
				// subtract the size of the current list to prepare for next
				// iteration
				index -= data.get(i).size();
			}// for

			// if, after removal, the size is 2 it means the list consists only
			// of the INSTRUCTIONS and SEPARATOR Strings; remove the SEPARATOR
			if (getSize() == 2) {
				staticStrings.remove(SEPARATOR);
			}

		}// if

		// inform the list that my contents have changed
		fireContentsChanged(this, index, index);

		return retVal;
	}// remove(int)

	/**
	 * Returns true if the element at index is a valid question. true iff: the
	 * index is in bounds of the list size and the index is not a SEPARATOR or
	 * INSTRUCTIONS String
	 * 
	 * @param index
	 *            the index to check
	 * 
	 * @return true if the element at index is a valid question
	 */
	public boolean isValidQuestion(int index) {
		return isInBounds(index)
				&& !(getElementAt(index).equals(SEPARATOR) || getElementAt(
						index).equals(INSTRUCTIONS));
	}// isValidQuestion(int)

	/**
	 * Returns true if index is within the bounds of our list
	 * 
	 * @param index
	 *            the index to check
	 * @return true if index is within the bounds of our list
	 */
	private boolean isInBounds(int index) {
		return (index >= 0 && index < getSize());
	}// isInBounds(int)

	/**
	 * Returns the value at the specified index.
	 * 
	 * @param index
	 *            the requested index
	 * 
	 * @return the value at index
	 */
	public String getElementAt(int index) {
		// if index isn't in bounds, return null
		if (!isInBounds(index)) {
			return null;
		}

		// similar looping stucture as the remove method; check a list, return
		// or decrease index, loop
		for (ArrayList<String> d : data) {
			if (index < d.size()) {
				return d.get(index);
			}
			index -= d.size();
		}

		// default catch; should never get here
		return null;
	}// getElementAt(int)

	/**
	 * Adds the given question string to the given list number
	 * 
	 * @param questionString
	 *            the question to add to the list
	 * @param listNum
	 *            the list to add questionString to
	 */
	private void addToList(String questionString, int listNum) {
		if (data.get(listNum).size() == 0) {
			data.get(listNum).add(SEPARATOR);
		}
		data.get(listNum).add(data.get(listNum).size() - 1, questionString);
	}// addToList(String,int)

	/**
	 * Returns true if the list contains no question data
	 * 
	 * @return true if the list contains no question data
	 */
	public boolean isEmpty() {
		// if size is 1, then the only element is INSTRUCTIONS,
		// which means empty
		return (getSize() == 1);
	}// isEmpty()

	/**
	 * Given a Question Type, returns the index of the last question of that
	 * type
	 * 
	 * @param questionType
	 *            the Question Type to search for
	 * 
	 * @return the index of the last question of the given Type
	 */
	public int getLastIndexOf(Question.Type questionType) {
		// start off by adding the number of elements of staticStrings - 1
		int retVal = staticStrings.size() - 1;

		// for each question type:
		// if this is the type we're looking for,
		// add the (size - 1) to the sum and return it.
		// else add the size and move on
		if (questionType == Question.Type.SUM) {
			retVal += data.get(1).size() - 1;
			return retVal;
		}
		retVal += data.get(1).size();

		if (questionType == Question.Type.DIFF) {
			retVal += data.get(2).size() - 1;
			return retVal;
		}
		retVal += data.get(2).size();

		if (questionType == Question.Type.MULT) {
			retVal += data.get(3).size() - 1;
			return retVal;
		}
		retVal += data.get(3).size();

		if (questionType == Question.Type.DIV) {
			retVal += data.get(4).size() - 1;
			return retVal;
		}
		retVal += data.get(4).size();

		if (questionType == Question.Type.POSITION) {
			retVal += data.get(5).size() - 1;
			return retVal;
		}
		retVal += data.get(5).size();

		// if the Type specified is not one of the ones recognized by this
		// model, the last element in the list will be returned (should be a
		// SEPARATOR string)
		return retVal;
	}// getLastIndexOf(Question.Type)

	/**
	 * Returns an ArrayList consisting of Questions of the specified Type. Note:
	 * These Questions will be built from the Strings we have.
	 * 
	 * @param questionType
	 *            the Question Type to search for
	 * 
	 * @return an ArrayList of Questions of the specified Type
	 */
	public ArrayList<Question> getQuestionsOfType(Question.Type questionType) {
		ArrayList<Question> retVal = new ArrayList<Question>();
		int i = -1;

		// set i to the correct index for questionType
		if (questionType == Question.Type.SUM) {
			i = 1;
		} else if (questionType == Question.Type.DIFF) {
			i = 2;
		} else if (questionType == Question.Type.MULT) {
			i = 3;
		} else if (questionType == Question.Type.DIV) {
			i = 4;
		} else if (questionType == Question.Type.POSITION) {
			i = 5;
		}

		// the first and second terms
		int first = 0;
		int second = 0;

		// parse on space, equals, and operators
		String delimeters = " +-x:\u00F7=";
		StringTokenizer parser;

		// iterate through the list we've chosen
		for (String s : data.get(i)) {

			// for each element not a SEPARATOR string...
			if (!s.equals(SEPARATOR)) {

				parser = new StringTokenizer(s, delimeters);
				
				first = Integer.parseInt(parser.nextToken());
				second = Integer.parseInt(parser.nextToken());

				retVal.add(new Question(questionType, first, second));

				//
				// // grab the first term based on the first space
				// first = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				//
				// // parse differently if it's a position question
				// if (s.contains(":")) {
				// // chop off the first term and the operator
				// second = Integer.parseInt(s
				// .substring(s.lastIndexOf(" ") + 1));
				// } else {
				// // chop off the first term and the operator
				// s = s.substring(s.indexOf(" ")).substring(
				// s.indexOf(" ") + 1);
				//
				// // grab the second term
				// second = Integer.parseInt(s.substring(0, s.indexOf(" ")));
				// }// if
				//
				// // build and add a Question based on the terms we got
				// retVal.add(new Question(questionType, first, second));
			}// if
		}// for

		// return the complete ArrayList of Question objects
		return retVal;
	}// getQuestionsOfType(Type)
}// QuestionListModel
