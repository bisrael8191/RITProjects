package GameUI;

/*
 * PrintUtility.java
 *
 * Version:
 *     $Id: PrintUtility.java,v 1.3 2006/10/13 18:52:55 exl2878 Exp $
 *
 * Revisions:
 *     $Log: PrintUtility.java,v $
 *     Revision 1.3  2006/10/13 18:52:55  exl2878
 *     Components are now scaled automatically to fit on one page
 *
 *     Revision 1.2  2006/10/12 23:43:19  emm4674
 *     added ability to change the print orientation (profile, landscape, etc)
 *
 *     Revision 1.1  2006/10/09 01:33:54  exl2878
 *     Moved to GameUI package
 *
 *     Revision 1.1  2006/09/29 18:45:01  exl2878
 *     Initial revision
 *
 */

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JOptionPane;
import javax.swing.RepaintManager;

/**
 * 
 * Prints a Java Swing component.
 * 
 * @author Eric Lutley exl2878@rit.edu
 */
public class PrintUtility implements Printable {

	private Component component;

	public static void printComponent(Component component) {
		new PrintUtility(component, PageFormat.PORTRAIT);
	}

	public static void printComponent(Component component, int orientation) {
		new PrintUtility(component, orientation);
	}

	public PrintUtility(Component component, int orientation) {
		this.component = component;
		PrinterJob printJob = PrinterJob.getPrinterJob();
		Book report = new Book();
		PageFormat format = printJob.defaultPage();
		format.setOrientation(orientation);
		report.append(this, format);
		printJob.setPageable(report);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (PrinterException pe) {
				JOptionPane.showMessageDialog(null, "Printing Error: "
						+ pe.getMessage(), "Printing Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public int print(Graphics g, PageFormat pageFormat, int pageNumber)
			throws PrinterException {
		int retVal = NO_SUCH_PAGE;
		if (pageNumber == 0) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.translate(pageFormat.getImageableX(), pageFormat
					.getImageableY());
			// Scale the graphics context so everything fits on one page
			Double scaleX = pageFormat.getImageableWidth() / component.getWidth();
			Double scaleY = pageFormat.getImageableHeight() / component.getHeight();
			g2d.scale( scaleX, scaleY );
			// Disable double buffering
			RepaintManager.currentManager(component).setDoubleBufferingEnabled(
					false);
			component.print(g2d);

			// Re-enable double buffering
			RepaintManager.currentManager(component).setDoubleBufferingEnabled(
					true);

			retVal = PAGE_EXISTS;
		}
		return retVal;
	}

}
