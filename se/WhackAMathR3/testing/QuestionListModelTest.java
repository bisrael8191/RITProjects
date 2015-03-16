package testing;

import java.util.ArrayList;

import junit.framework.TestCase;
import Administration.QuestionListModel;
import GameLogic.Question;

public class QuestionListModelTest extends TestCase {
	// copied from StudentListModel to test for elements in the correct places
	private static final String SEPARATOR = "--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------";

	private static final String INSTRUCTIONS = "Instructions";

	/**
	 * Main method for JUnit QuestionListModelTest class
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		junit.swingui.TestRunner.run(QuestionListModelTest.class);
	}

	/**
	 * Tests to make sure that the list data contains the correct number of
	 * elements after construction, adding an element, and removing an element
	 */
	public void testSize() {
		QuestionListModel model = new QuestionListModel();
		// should only have "Instructions" data in it
		assertEquals(model.getSize(), 1);
		assertEquals(model.isEmpty(), true);

		model.addElement(new Question(Question.Type.SUM, 1, 1));
		// should have "Instructions", separator string, question string, and a
		// trailing separator string
		assertEquals(model.getSize(), 4);
		assertEquals(model.isEmpty(), false);

		model.remove(2);
		// should only have "Instructions" data in it
		assertEquals(model.getSize(), 1);
		assertEquals(model.isEmpty(), true);
	}

	/**
	 * Tests to make sure that the list data is correct
	 */
	public void testContents() {
		QuestionListModel model = new QuestionListModel();
		model.addElement(new Question(Question.Type.SUM, 1, 1));

		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "1 + 1 = 2");

		model.remove(2);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
	}

	/**
	 * Tests to make sure that the basic list data (INSTRUCTIONS and SEPARATOR)
	 * can't be removed from the list
	 */
	public void testValidRemove() {
		QuestionListModel model = new QuestionListModel();
		model.addElement(new Question(Question.Type.SUM, 1, 1));

		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "1 + 1 = 2");
		assertEquals(model.getElementAt(3), SEPARATOR);

		model.remove(0);
		assertEquals(model.getSize(), 4);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "1 + 1 = 2");
		assertEquals(model.getElementAt(3), SEPARATOR);

		model.remove(1);
		assertEquals(model.getSize(), 4);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "1 + 1 = 2");
		assertEquals(model.getElementAt(3), SEPARATOR);

		model.remove(3);
		assertEquals(model.getSize(), 4);
		assertEquals(model.getElementAt(0), INSTRUCTIONS);
		assertEquals(model.getElementAt(1), SEPARATOR);
		assertEquals(model.getElementAt(2), "1 + 1 = 2");
		assertEquals(model.getElementAt(3), SEPARATOR);
	}

	/**
	 * Tests the "isValidQuestion" method
	 */
	public void testIsValidQuestion() {
		QuestionListModel model = new QuestionListModel();
		model.addElement(new Question(Question.Type.SUM, 1, 1));
		// the only valid index at this point should be 2

		assertEquals(model.isValidQuestion(0), false);
		assertEquals(model.isValidQuestion(1), false);
		assertEquals(model.isValidQuestion(2), true);
		assertEquals(model.isValidQuestion(3), false);
	}

	/**
	 * Tests the "getQuestionsOfType" method
	 */
	public void testGetQuestionsOfType() {
		// create one question of each type
		Question qSum = new Question(Question.Type.SUM, 1, 1);
		Question qDiff = new Question(Question.Type.DIFF, 2, 2);
		Question qMult = new Question(Question.Type.MULT, 3, 3);
		Question qDiv = new Question(Question.Type.DIV, 4, 4);
		Question qPos = new Question(Question.Type.POSITION, 5, 5);

		// add them to the model
		QuestionListModel model = new QuestionListModel();
		model.addElement(qSum);
		model.addElement(qDiff);
		model.addElement(qMult);
		model.addElement(qDiv);
		model.addElement(qPos);

		ArrayList<Question> q = null;

		q = model.getQuestionsOfType(Question.Type.SUM);
		assertEquals(q.size(), 1);
		assertEquals(q.get(0).getFirstTerm(), qSum.getFirstTerm());
		assertEquals(q.get(0).getSecondTerm(), qSum.getSecondTerm());
		assertEquals(q.get(0).getTheType(), qSum.getTheType());
		q = null;

		q = model.getQuestionsOfType(Question.Type.DIFF);
		assertEquals(q.size(), 1);
		assertEquals(q.get(0).getFirstTerm(), qDiff.getFirstTerm());
		assertEquals(q.get(0).getSecondTerm(), qDiff.getSecondTerm());
		assertEquals(q.get(0).getTheType(), qDiff.getTheType());
		q = null;

		q = model.getQuestionsOfType(Question.Type.MULT);
		assertEquals(q.size(), 1);
		assertEquals(q.get(0).getFirstTerm(), qMult.getFirstTerm());
		assertEquals(q.get(0).getSecondTerm(), qMult.getSecondTerm());
		assertEquals(q.get(0).getTheType(), qMult.getTheType());
		q = null;

		q = model.getQuestionsOfType(Question.Type.DIV);
		assertEquals(q.size(), 1);
		assertEquals(q.get(0).getFirstTerm(), qDiv.getFirstTerm());
		assertEquals(q.get(0).getSecondTerm(), qDiv.getSecondTerm());
		assertEquals(q.get(0).getTheType(), qDiv.getTheType());
		q = null;

		q = model.getQuestionsOfType(Question.Type.POSITION);
		assertEquals(q.size(), 1);
		assertEquals(q.get(0).getFirstTerm(), qPos.getFirstTerm());
		assertEquals(q.get(0).getSecondTerm(), qPos.getSecondTerm());
		assertEquals(q.get(0).getTheType(), qPos.getTheType());
	}
}
