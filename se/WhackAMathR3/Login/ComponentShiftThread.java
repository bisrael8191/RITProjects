package Login;

import java.awt.Component;

/**
 * Used to shift the components of the login screen when a scroll button is
 * pressed
 * 
 * @author Eric
 */
public class ComponentShiftThread extends Thread {

	/** the amount to shift the component by */
	private static final int SHAMT = 12;

	/** if true, will stop the thread */
	private boolean kill;

	/** if true, will cause the shifting part of the thread to run */
	private boolean running;

	/** minimum x location to shift to */
	private int lowerBound;

	/** maimum x location to shift to */
	private int upperBound;

	/** changed based on SHAMT and the state set in the setRunning method */
	private int shift;

	/** component to shift */
	private Component comp;

	/**
	 * Creates a new instance of ComponentShiftThread
	 * 
	 * @param comp
	 *            the component to shift
	 * @param upperBound
	 *            the upper bound of the shift range
	 */
	public ComponentShiftThread(Component comp, int upperBound) {
		this(comp, 0, upperBound);
	}// ComponentShiftThread()

	/**
	 * Creates a new instance of ComponentShiftThread; begins in a "not running"
	 * state
	 * 
	 * @param comp
	 *            the component to shift
	 * @param lowerBound
	 *            the lower bound of the shift range
	 * @param upperBound
	 *            the upper bound of the shift range
	 */
	public ComponentShiftThread(Component comp, int lowerBound, int upperBound) {
		this.comp = comp;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		shift = 0;
		kill = false;
		running = false;
	}// ComponentShiftThread()

	/**
	 * Thread actions; if running, will shift the component by the specified
	 * amount
	 */
	public void run() {

		// time for thread to pause between shifts
		long sleepTime = 30;

		// the current "location" of the items
		int currentX = 0;

		// run until told to kill
		while (!kill) {
			// check running flag
			if (running) {
				// check to be sure we're in bounds of the value range
				if ((-currentX - shift) > lowerBound
						&& (-currentX - shift) < upperBound) {

					// if all checks out, do the shift
					currentX += shift;
					comp.setLocation(comp.getX() + shift, comp.getY());

					// sleep between shifts
					try {
						sleep(sleepTime);
					} catch (InterruptedException ex) {
						kill();
					}// try-catch
				} else if ((-currentX - shift) <= lowerBound) {
					// if we go past the lower bount, set the location to the
					// absolute lowest point
					comp.setLocation(-lowerBound, comp.getY());
				} else if ((-currentX - shift) >= upperBound) {
					// if we go past the upper bount, set the location to the
					// absolute highest point
					comp.setLocation(-upperBound, comp.getY());
				}// if-else
			} else {

				// while we're not running, sleep twice as long as normal
				try {
					sleep(2 * sleepTime);
				} catch (InterruptedException ex) {
					kill();
				}// try-catch
			}
		}// while
	}// run()

	/**
	 * Stops the thread
	 */
	public void kill() {
		kill = true;
	}// kill()

	/**
	 * Sets the state of running based on i
	 * 
	 * @param i
	 *            on i = 0, set running to false; on i = 1, set running to true,
	 *            shift to SHAMT; on i = -1, set running to true, shift to
	 *            -SHAMT
	 */
	public void setRunning(int i) {
		if (i == 0) {
			running = false;
		} else {
			running = true;
			shift = i * SHAMT;
		}// if-else
	}// setRunning(int)

}// ComponentShiftThread
