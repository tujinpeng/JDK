/*
 * @(#)Paper.java	1.10 98/10/19
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

import java.awt.geom.Rectangle2D;

/**
 * The <code>Paper</code> class describes the physical characteristics of
 * a piece of paper.
 */
public class Paper implements Cloneable {

 /* Private Class Variables */

    private static final int INCH = 72;
    private static final double LETTER_WIDTH = 8.5 * INCH;
    private static final double LETTER_HEIGHT = 11 * INCH;

 /* Instance Variables */

    /**
     * The height of the physical page in 1/72nds
     * of an inch. The number is stored as a floating
     * point value rather than as an integer
     * to facilitate the conversion from metric
     * units to 1/72nds of an inch and then back.
     * (This may or may not be a good enough reason
     * for a float).
     */
    private double mHeight;

    /**
     * The width of the physical page in 1/72nds
     * of an inch.
     */
    private double mWidth;

    /**
     * The area of the page on which drawing will
     * be visable. The area outside of this
     * rectangle but on the Page generally
     * reflects the printer's hardware margins.
     * The origin of the physical page is
     * at (0, 0) with this rectangle provided
     * in that coordinate system.
     */
    private Rectangle2D mImageableArea;

 /* Constructors */

    /**
     * Creates a letter sized piece of paper
     * with one inch margins.
     */
    public Paper() {
	mHeight = LETTER_HEIGHT;
	mWidth = LETTER_WIDTH;
	mImageableArea = new Rectangle2D.Double(INCH, INCH,
						mWidth - 2 * INCH,
						mHeight - 2 * INCH);
    }

 /* Instance Methods */

    /**
     * Creates a copy of this <code>Paper</code> with the same contents
     * as this <code>Paper</code>.
     * @return a copy of this <code>Paper</code>.
     */
    public Object clone() {

	Paper newPaper;

	try {
	    /* It's okay to copy the reference to the imageable
	     * area into the clone since we always return a copy
	     * of the imageable area when asked for it.
	     */
	    newPaper = (Paper) super.clone();

	} catch (CloneNotSupportedException e) {
	    e.printStackTrace();
	    newPaper = null;	// should never happen.
	}

	return newPaper;
    }

    /**
     * Returns the height of the page in 1/72nds of an inch.
     * @return the height of the page described by this
     *		<code>Paper</code>.
     */
    public double getHeight() {
	return mHeight;
    }

    /**
     * Sets the width and height of this <code>Paper</code>.
     * The dimensions are supplied in 1/72nds of
     * and inch.
     * @param width the value to which to set this <code>Paper</code>
     * object's width
     * @param height the value to which to set this <code>Paper</code>
     * object's height
     */
    public void setSize(double width, double height) {
	mWidth = width;
	mHeight = height;
    }

    /**
     * Returns the width of the page in 1/72nds
     * of an inch.
     * @return the width of the page described by this
     * <code>Paper</code>.
     */
    public double getWidth() {
	return mWidth;
    }

    /**
     * Sets the imageable area of this <code>Paper</code>. 
     * @param x,&nbsp;y the coordinates to which to set the
     * upper-left corner of the imageable area of this <code>Paper</code>
     * @param width the value to which to set the width of the 
     * imageable area of this <code>Paper</code>
     * @param height the value to which to set the height of the
     * imageable area of this <code>Paper</code>
     */
    public void setImageableArea(double x, double y,
				 double width, double height) {
	mImageableArea = new Rectangle2D.Double(x, y, width,height);
    }

    /**
     * Returns the x coordinate of the upper-left corner of this
     * <code>Paper</code> object's imageable area.
     * @return the x coordinate of the imageable area.
     */
    public double getImageableX() {
	return mImageableArea.getX();
    }

    /**
     * Returns the y coordinate of the upper-left corner of this
     * <code>Paper</code> object's imageable area.
     * @return the y coordinate of the imageable area.
     */
    public double getImageableY() {
	return mImageableArea.getY();
    }

    /**
     * Returns the width of this <code>Paper</code> object's imageable
     * area.
     * @return the width of the imageable area.
     */
    public double getImageableWidth() {
	return mImageableArea.getWidth();
    }

    /**
     * Returns the height of this <code>Paper</code> object's imageable
     * area.
     * @return the height of the imageable area.   
     */
    public double getImageableHeight() {
	return mImageableArea.getHeight();
    }
}