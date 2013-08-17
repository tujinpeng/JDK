/*
 * @(#)PrinterJob.java	1.17 98/10/19
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.print;

import java.awt.AWTError;
import java.util.Enumeration;

import sun.security.action.GetPropertyAction;

/**
 * The <code>PrinterJob</code> class is the principal class that controls
 * printing. An application calls methods in this class to set up a job,
 * optionally to invoke a print dialog with the user, and then to print
 * the pages of the job.
 */
public abstract class PrinterJob {


 /* Public Class Methods */

    /**
     * Creates and returns a <code>PrinterJob</code>.
     * @return a new <code>PrinterJob</code>.
     */
    public static PrinterJob getPrinterJob() {
	SecurityManager security = System.getSecurityManager();
	if (security != null) {
	    security.checkPrintJobAccess();
	}
	return (PrinterJob) java.security.AccessController.doPrivileged(
	    new java.security.PrivilegedAction() {
	    public Object run() {
		String nm = System.getProperty("java.awt.printerjob", null);
		try {
		    return (PrinterJob)Class.forName(nm).newInstance();
		} catch (ClassNotFoundException e) {
		    throw new AWTError("PrinterJob not found: " + nm);
		} catch (InstantiationException e) {
		 throw new AWTError("Could not instantiate PrinterJob: " + nm);
		} catch (IllegalAccessException e) {
		    throw new AWTError("Could not access PrinterJob: " + nm);
		}
	    }
	});
    }

 /* Public Methods */

    /**
     * A <code>PrinterJob</code> object should be created using the
     * static {@link #getPrinterJob() <code>getPrinterJob</code>} method.
     */
     public PrinterJob() {
     }

    /**
     * Calls <code>painter</code> to render the pages.  The pages in the
     * document to be printed by this 
     * <code>PrinterJob</code> are rendered by the {@link Printable}
     * object, <code>painter</code>.  The {@link PageFormat} for each page
     * is the default page format.
     * @param painter the <code>Printable</code> that renders each page of
     * the document.
     */
    public abstract void setPrintable(Printable painter);

    /**
     * Calls <code>painter</code> to render the pages in the specified
     * <code>format</code>.  The pages in the document to be printed by
     * this <code>PrinterJob</code> are rendered by the
     * <code>Printable</code> object, <code>painter</code>. The
     * <code>PageFormat</code> of each page is <code>format</code>.
     * @param painter the <code>Printable</code> called to render
     *		each page of the document
     * @param format the size and orientation of each page to
     *                   be printed
     */
    public abstract void setPrintable(Printable painter, PageFormat format);

    /**
     * Queries <code>document</code> for the number of pages and 
     * the <code>PageFormat</code> and <code>Printable</code> for each
     * page held in the <code>Pageable</code> instance,  
     * <code>document</code>.
     * @param document the pages to be printed. It can not be
     * <code>null</code>.
     * @exception NullPointerException the <code>Pageable</code> passed in
     * was <code>null</code>.
     * @see PageFormat
     * @see Printable
     */
    public abstract void setPageable(Pageable document)
	throws NullPointerException;

    /**
     * Presents a dialog to the user for changing the properties of
     * the print job.
     * @return <code>true</code> if the user does not cancel the dialog;
     * <code>false</code> otherwise.
     */
    public abstract boolean printDialog();

    /**
     * Displays a dialog that allows modification of a
     * <code>PageFormat</code> instance.
     * The <code>page</code> argument is used to initialize controls
     * in the page setup dialog.
     * If the user cancels the dialog then this method returns the
     * original <code>page</code> object unmodified.
     * If the user okays the dialog then this method returns a new
     * <code>PageFormat</code> object with the indicated changes.
     * In either case, the original <code>page</code> object is
     * not modified.
     * @param page the default <code>PageFormat</code> presented to the
     *			user for modification
     * @return    the original <code>page</code> object if the dialog
     *            is cancelled; a new <code>PageFormat</code> object
     *		  containing the format indicated by the user if the
     *		  dialog is acknowledged.
     * @since     JDK1.2
     */
    public abstract PageFormat pageDialog(PageFormat page);

    /**
     * Clones the <code>PageFormat</code> argument and alters the
     * clone to describe a default page size and orientation.
     * @param page the <code>PageFormat</code> to be cloned and altered
     * @return clone of <code>page</code>, altered to describe a default
     *                      <code>PageFormat</code>.
     */
    public abstract PageFormat defaultPage(PageFormat page);

    /**
     * Creates a new <code>PageFormat</code> instance and
     * sets it to a default size and orientation.
     * @return a <code>PageFormat</code> set to a default size and
     *		orientation.
     */
    public PageFormat defaultPage() {
        return defaultPage(new PageFormat());
    }

    /**
     * Alters the <code>PageFormat</code> argument to be usable on
     * this <code>PrinterJob</code> object's current printer.
     * @param page this page description is cloned and then its settings
     *		are altered to be usuable for this
     *		<code>PrinterJob</code>
     * @return a <code>PageFormat</code> that is cloned from
     *		the <code>PageFormat</code> parameter and altered
     *		to conform with this <code>PrinterJob</code>.
     */
    public abstract PageFormat validatePage(PageFormat page);

    /**
     * Prints a set of pages.
     * @exception PrinterException an error in the print system
     *            caused the job to be aborted.
     * @see Book
     * @see Pageable
     * @see Printable
     */
    public abstract void print() throws PrinterException;

    /**
     * Sets the number of copies to be printed.
     * @param copies the number of copies to be printed
     */
    public abstract void setCopies(int copies);

    /**
     * Gets the number of copies to be printed.
     * @return the number of copies to be printed.
     */
    public abstract int getCopies();

    /**
     * Gets the name of the printing user.
     * @return the name of the printing user
     */
    public abstract String getUserName();

    /**
     * Sets the name of the document to be printed.
     * The document name can not be <code>null</code>.
     * @param jobName the name of the document to be printed
     */
    public abstract void setJobName(String jobName);

    /**
     * Gets the name of the document to be printed.
     * @return the name of the document to be printed.
     */
    public abstract String getJobName();

    /**
     * Cancels a print job that is in progress.  If 
     * {@link #print() print} has been called but has not 
     * returned then this method signals
     * that the job should be cancelled at the next
     * chance. If there is no print job in progress then
     * this call does nothing.
     */
    public abstract void cancel();

    /**
     * Returns <code>true</code> if a print job is 
     * in progress, but is going to be cancelled
     * at the next opportunity; otherwise returns
     * <code>false</code>.
     * @return <code>true</code> if the job in progress
     * is going to be cancelled; <code>false</code> otherwise.
     */
    public abstract boolean isCancelled();

}