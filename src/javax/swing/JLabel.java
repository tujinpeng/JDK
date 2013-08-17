/*
 * @(#)JLabel.java	1.84 98/08/28
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

package javax.swing;

import java.awt.Component;
import java.awt.Font;
import java.awt.Image;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.plaf.LabelUI;
import javax.accessibility.*;


/**
 * A display area for a short text string or an image,
 * or both.
 * A label does not react to input events.
 * As a result, it cannot get the keyboard focus.
 * A label can, however, display a keyboard alternative
 * as a convenience for a nearby component
 * that has a keyboard alternative but can't display it.
 * <p>
 * A <code>JLabel</code> object can display
 * either text, an image, or both.
 * You can specify where in the label's display area
 * the label's contents are aligned
 * by setting the vertical and horizontal alignment.
 * By default, labels are vertically centered 
 * in their display area.
 * Text-only labels are left-aligned, by default;
 * image-only labels are horizontally centered, by default.
 * <p>
 * You can also specify the position of the text
 * relative to the image.
 * By default, text is to the right of the image,
 * with the text and image vertically aligned.
 * <p>
 * Finally, you can use the <code>setIconTextGap</code> method
 * to specify how many pixels
 * should appear between the text and the image.
 * The default is 4 pixels.
 * <p>
 * See <a href="http://java.sun.com/docs/books/tutorial/ui/swing/label.html">How to Use Labels</a>
 * in <a href="http://java.sun.com/Series/Tutorial/index.html"><em>The Java Tutorial</em></a>
 * for further documentation.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 * description: A component that displays a short string and an icon.
 * 
 * @version 1.84 08/28/98
 * @author Hans Muller
 */
public class JLabel extends JComponent implements SwingConstants, Accessible
{
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "LabelUI";

    private int mnemonic = '\0';

    private String text = "";         // "" rather than null, for BeanBox
    private Icon defaultIcon = null;
    private Icon disabledIcon = null;
    private boolean disabledIconSet = false;
            
    private int verticalAlignment = CENTER;
    private int horizontalAlignment = LEFT;
    private int verticalTextPosition = CENTER;
    private int horizontalTextPosition = RIGHT;
    private int iconTextGap = 4;

    protected Component labelFor = null;


    /**
     * Client property key used to determine what label is labeling the
     * component.  This is generally not used by labels, but is instead
     * used by components such as text areas that are being labeled by
     * labels.  When the labelFor property of a label is set, it will
     * automatically set the LABELED_BY_PROPERTY of the component being
     * labelled.
     *
     * @see #setLabelFor
     */
    static final String LABELED_BY_PROPERTY = "labeledBy";

    /**
     * Creates a <code>JLabel</code> instance with the specified
     * text, image, and horizontal alignment.
     * The label is centered vertically in its display area.
     * The text is to the right of the image.
     *
     * @param text  The text to be displayed by the label.
     * @param icon  The image to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, or
     *           <code>RIGHT</code>.
     */
    public JLabel(String text, Icon icon, int horizontalAlignment) {
        setText(text);
        setIcon(icon);
        setHorizontalAlignment(horizontalAlignment);
        updateUI();
        setAlignmentX(LEFT_ALIGNMENT);
    }
            
    /**
     * Creates a <code>JLabel</code> instance with the specified
     * text and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param text  The text to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, or
     *           <code>RIGHT</code>.
     */
    public JLabel(String text, int horizontalAlignment) {
        this(text, null, horizontalAlignment);
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified text.
     * The label is aligned against the left side of its display area,
     * and centered vertically.
     *
     * @param text  The text to be displayed by the label.
     */
    public JLabel(String text) {
        this(text, null, LEFT);
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified
     * image and horizontal alignment.
     * The label is centered vertically in its display area.
     *
     * @param icon  The image to be displayed by the label.
     * @param horizontalAlignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, or
     *           <code>RIGHT</code>.
     */
    public JLabel(Icon image, int horizontalAlignment) {
        this(null, image, horizontalAlignment);
    }

    /**
     * Creates a <code>JLabel</code> instance with the specified image.
     * The label is centered vertically and horizontally
     * in its display area.
     *
     * @param icon  The image to be displayed by the label.
     */
    public JLabel(Icon image) {
        this(null, image, CENTER);
    }

    /**
     * Creates a <code>JLabel</code> instance with 
     * no image and with an empty string for the title.
     * The label is centered vertically 
     * in its display area.
     * The label's contents, once set, will be displayed at the left 
     * of the label's display area.
     */
    public JLabel() {
        this("", null, LEFT);
    }


    /**
     * Returns the L&F object that renders this component.
     *
     * @return LabelUI object
     */
    public LabelUI getUI() {
        return (LabelUI)ui;
    }


    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the LabelUI L&F object
     * @see UIDefaults#getUI
     * @beaninfo
     *      expert: true
     *  description: The L&F object that renders this component.
     */
    public void setUI(LabelUI ui) {
        super.setUI(ui);
    }


    /**
     * Notification from the UIFactory that the L&F
     * has changed. 
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((LabelUI)UIManager.getUI(this));
    }


    /**
     * Returns a string that specifies the name of the l&f class
     * that renders this component.
     *
     * @return String "LabelUI"
     *
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /** 
     * Returns the text string that the label displays.
     *
     * @return a String
     * @see #setText
     */
    public String getText() {
        return text;
    }


    /**
     * Defines the single line of text this component will display.  If
     * the value of text is null or empty string, nothing is displayed.
     * <p>
     * The default value of this property is null.
     * <p>
     * This is a JavaBeans bound property.  
     * 
     * @see #setVerticalTextPosition
     * @see #setHorizontalTextPosition
     * @see #setIcon
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Defines the single line of text this component will display.
     */
    public void setText(String text) {

        String oldAccessibleName = null;
        if (accessibleContext != null) {
            oldAccessibleName = accessibleContext.getAccessibleName();
        }

        String oldValue = this.text;
        this.text = text;
        firePropertyChange("text", oldValue, text);

        if ((accessibleContext != null) 
            && (accessibleContext.getAccessibleName() != oldAccessibleName)) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                        oldAccessibleName,
                        accessibleContext.getAccessibleName());
        }
        if (text == null || oldValue == null || !text.equals(oldValue)) {
            revalidate();
            repaint();
        }
    }

    
    /**
     * Returns the graphic image (glyph, icon) that the label displays.
     *
     * @return an Icon
     * @see #setIcon
     */
    public Icon getIcon() {
        return defaultIcon;
    }

    /**
     * Defines the icon this component will display.  If
     * the value of icon is null, nothing is displayed.
     * <p>
     * The default value of this property is null.
     * <p>
     * This is a JavaBeans bound property.  
     * 
     * @see #setVerticalTextPosition
     * @see #setHorizontalTextPosition
     * @see #getIcon
     * @beaninfo
     *    preferred: true
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The icon this component will display.
     */
    public void setIcon(Icon icon) {
        Icon oldValue = defaultIcon;
        defaultIcon = icon;

        /* If the default icon has really changed and we had
         * generated the disabled icon for this component,
         * (i.e. setDiabledIcon() was never called) then 
         * clear the disabledIcon field.
         */
        if ((defaultIcon != oldValue) && !disabledIconSet) {
            disabledIcon = null;
        }

        firePropertyChange("icon", oldValue, defaultIcon);

        if ((accessibleContext != null) && (oldValue != defaultIcon)) {
                accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY, 
                        oldValue, defaultIcon);
        }

        /* If the default icon has changed and the new one is 
         * a different size, then revalidate.   Repaint if the
         * default icon has changed.
         */
        if (defaultIcon != oldValue) {
            if ((defaultIcon == null) || 
                (oldValue == null) ||
                (defaultIcon.getIconWidth() != oldValue.getIconWidth()) ||
                (defaultIcon.getIconHeight() != oldValue.getIconHeight())) {
                revalidate();
            } 
            repaint();
        }
    }


    /**
     * Returns the value of the disabledIcon property if it's been set,
     * If it hasn't been set and the value of the icon property is
     * an ImageIcon, we compute a "grayed out" version of the icon and
     * update the disabledIcon property with that.
     * 
     * @return The value of the disabledIcon property.
     * @see #setDisabledIcon
     * @see ImageIcon
     */
    public Icon getDisabledIcon() 
    {
        if(!disabledIconSet && 
           (disabledIcon == null) &&
           (defaultIcon != null) && 
           (defaultIcon instanceof ImageIcon)) {
            Image grayImage = GrayFilter.createDisabledImage(((ImageIcon)defaultIcon).getImage());
            disabledIcon = new ImageIcon(grayImage);
            firePropertyChange("disabledIcon", null, disabledIcon);
        }
        return disabledIcon;
    }


    /**
     * Set the icon to be displayed if this JLabel is "disabled", i.e.
     * JLabel.setEnabled(false).
     * <p>
     * The default value of this property is null.
     * 
     * @param disabledIcon the Icon to display when the component is disabled
     * @see #getDisabledIcon
     * @see #setEnabled
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The icon to display if the label is disabled.
     */
    public void setDisabledIcon(Icon disabledIcon) {
        Icon oldValue = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        disabledIconSet = true;
        firePropertyChange("disabledIcon", oldValue, disabledIcon);
        if (disabledIcon != oldValue) {
            if (disabledIcon == null || oldValue == null ||
                disabledIcon.getIconWidth() != oldValue.getIconWidth() ||
                disabledIcon.getIconHeight() != oldValue.getIconHeight()) {
                revalidate();
            } 
            if (!isEnabled()) {
                repaint();
            }
        }
    }


    /**
     * Specify a keycode that indicates a mnemonic key.
     * This property is used when the label is part of a larger component.  
     * If the labelFor property of the label is not null, the label will
     * call the requestFocus method of the component specified by the
     * labelFor property when the mnemonic is activated.
     *
     * @see #getLabelFor
     * @see #setLabelFor
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The mnemonic keycode.
     */
    public void setDisplayedMnemonic(int key) {
        int oldKey = mnemonic;
        mnemonic = key;
        firePropertyChange("displayedMnemonic", oldKey, mnemonic);
        if (key != oldKey) {
            revalidate();
            repaint();
        }
    }


    /**
     * Specifies the displayedMnemonic as a char value.
     *
     * @param aChar  a char specifying the mnemonic to display
     * @see #setDisplayedMnemonic(int)
     */
    public void setDisplayedMnemonic(char aChar) {
        int vk = (int) aChar;
        if(vk >= 'a' && vk <='z')
            vk -= ('a' - 'A');
        setDisplayedMnemonic(vk);
    }


    /**
     * Return the keycode that indicates a mnemonic key.
     * This property is used when the label is part of a larger component.
     * If the labelFor property of the label is not null, the label will
     * call the requestFocus method of the component specified by the
     * labelFor property when the mnemonic is activated.
     *
     * @return int value for the mnemonic key
     *
     * @see #getLabelFor
     * @see #setLabelFor
     */
    public int getDisplayedMnemonic() {
        return mnemonic;
    }


    /**
     * Verify that key is a legal value for the horizontalAlignment properties.
     *
     * @param key the property value to check
     * @param message the IllegalArgumentException detail message 
     * @exception IllegalArgumentException if key isn't LEFT, CENTER, RIGHT,
     * LEADING or TRAILING.
     * @see #setHorizontalTextPosition
     * @see #setHorizontalAlignment
     */
    protected int checkHorizontalKey(int key, String message) {
        if ((key == LEFT) ||
            (key == CENTER) ||
            (key == RIGHT) ||
            (key == LEADING) ||
            (key == TRAILING)) {
            return key;
        }
        else {
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * Verify that key is a legal value for the 
     * verticalAlignment or verticalTextPosition properties.
     *
     * @param key the property value to check
     * @param message the IllegalArgumentException detail message 
     * @exception IllegalArgumentException if key isn't TOP, CENTER, or BOTTOM.
     * @see #setVerticalAlignment
     * @see #setVerticalTextPosition
     */
    protected int checkVerticalKey(int key, String message) {
        if ((key == TOP) || (key == CENTER) || (key == BOTTOM)) {
            return key;
        }
        else {
            throw new IllegalArgumentException(message);
        }
    }


    /**
     * Returns the amount of space between the text and the icon
     * displayed in this label.
     *
     * @return an int equal to the number of pixels between the text
     *         and the icon.
     * @see #setIconTextGap
     */
    public int getIconTextGap() {
        return iconTextGap;
    }


    /**
     * If both the icon and text properties are set, this property
     * defines the space between them.  
     * <p>
     * The default value of this property is 4 pixels.
     * <p>
     * This is a JavaBeans bound property.
     * 
     * @see #getIconTextGap
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: If both the icon and text properties are set, this
     *               property defines the space between them.
     */
    public void setIconTextGap(int iconTextGap) {
        int oldValue = this.iconTextGap;
        this.iconTextGap = iconTextGap;
        firePropertyChange("iconTextGap", oldValue, iconTextGap);
        if (iconTextGap != oldValue) {
            revalidate();
            repaint();
        }
    }



    /**
     * Returns the alignment of the label's contents along the Y axis.
     *
     * @return   The value of the verticalAlignment property, one of the 
     *           following constants defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>, or
     *           <code>BOTTOM</code>.
     *
     * @see SwingConstants
     * @see #setVerticalAlignment
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }


    /**
     * Sets the alignment of the label's contents along the Y axis.  
     * <p>
     * The default value of this property is CENTER.
     * 
     * @param alignment One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code> (the default), or
     *           <code>BOTTOM</code>.
     *
     * @see SwingConstants
     * @see #getVerticalAlignment
     * @beaninfo
     *        bound: true
     *         enum: TOP    SwingConstants.TOP
     *               CENTER SwingConstants.CENTER
     *               BOTTOM SwingConstants.BOTTOM
     *    attribute: visualUpdate true
     *  description: The alignment of the label's contents along the Y axis.  
     */
    public void setVerticalAlignment(int alignment) {
        if (alignment == verticalAlignment) return;
        int oldValue = verticalAlignment;
        verticalAlignment = checkVerticalKey(alignment, "verticalAlignment");
        firePropertyChange("verticalAlignment", oldValue, verticalAlignment); 
        repaint();
    }


    /**
     * Returns the alignment of the label's contents along the X axis.
     *
     * @return   The value of the horizontalAlignment property, one of the 
     *           following constants defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, or
     *           <code>RIGHT</code>.
     *
     * @see #setHorizontalAlignment
     * @see SwingConstants
     */
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }

    /**
     * Sets the alignment of the label's contents along the X axis.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param alignment  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code> (the default for text-only labels),
     *           <code>CENTER</code> (the default for image-only labels),
     *           <code>RIGHT</code>,
     *           <code>LEADING</code> or
     *           <code>TRAILING</code>.
     *
     * @see SwingConstants
     * @see #getHorizontalAlignment
     * @beaninfo
     *        bound: true
     *         enum: LEFT     SwingConstants.LEFT
     *               CENTER   SwingConstants.CENTER
     *               RIGHT    SwingConstants.RIGHT
     *               LEADING  SwingConstants.LEADING
     *               TRAILING SwingConstants.TRAILING
     *    attribute: visualUpdate true
     *  description: The alignment of the label's content along the X axis.
     */
    public void setHorizontalAlignment(int alignment) {
        if (alignment == horizontalAlignment) return;
        int oldValue = horizontalAlignment;
        horizontalAlignment = checkHorizontalKey(alignment,
                                                 "horizontalAlignment");
        firePropertyChange("horizontalAlignment",
                           oldValue, horizontalAlignment);
        repaint();
    }


    /**
     * Returns the vertical position of the label's text,
     * relative to its image.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code>, or
     *           <code>BOTTOM</code>.
     *
     * @see #setVerticalTextPosition
     * @see SwingConstants
     */
    public int getVerticalTextPosition() {
        return verticalTextPosition;
    }


    /**
     * Sets the vertical position of the label's text,
     * relative to its image.
     * <p>
     * The default value of this property is CENTER.
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param textPosition  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>TOP</code>,
     *           <code>CENTER</code> (the default), or
     *           <code>BOTTOM</code>.
     *
     * @see SwingConstants
     * @see #getVerticalTextPosition
     * @beaninfo
     *        bound: true
     *         enum: TOP    SwingConstants.TOP
     *               CENTER SwingConstants.CENTER
     *               BOTTOM SwingConstants.BOTTOM
     *       expert: true
     *    attribute: visualUpdate true
     *  description: The vertical position of the text relative to it's image.
     */
    public void setVerticalTextPosition(int textPosition) {
        if (textPosition == verticalTextPosition) return;
        int old = verticalTextPosition;
        verticalTextPosition = checkVerticalKey(textPosition,
                                                "verticalTextPosition");
        firePropertyChange("verticalTextPosition", old, verticalTextPosition);
        repaint();
    }


    /**
     * Returns the horizontal position of the label's text,
     * relative to its image.
     *
     * @return   One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>, or
     *           <code>RIGHT</code>.
     *
     * @see SwingConstants
     */
    public int getHorizontalTextPosition() {
        return horizontalTextPosition;
    }


    /**
     * Sets the horizontal position of the label's text,
     * relative to its image.
     *
     * @param x  One of the following constants
     *           defined in <code>SwingConstants</code>:
     *           <code>LEFT</code>,
     *           <code>CENTER</code>,
     *           <code>RIGHT</code> (the default),
     *           <code>LEADING</code>, or
     *           <code>TRAILING</code>.
     * @exception IllegalArgumentException
     *
     * @see SwingConstants
     * @beaninfo
     *       expert: true
     *        bound: true
     *         enum: LEFT     SwingConstants.LEFT
     *               CENTER   SwingConstants.CENTER
     *               RIGHT    SwingConstants.RIGHT
     *               LEADING  SwingConstants.LEADING
     *               TRAILING SwingConstants.TRAILING
     *    attribute: visualUpdate true
     *  description: The horizontal position of the label's text, 
     *               relative to its image.
     */
    public void setHorizontalTextPosition(int textPosition) {
        int old = horizontalTextPosition;
        this.horizontalTextPosition = checkHorizontalKey(textPosition,
                                                "horizontalTextPosition");
        firePropertyChange("horizontalTextPosition",
                           old, horizontalTextPosition);
        repaint();
    }


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        if ((ui != null) && (getUIClassID().equals(uiClassID))) {
            ui.installUI(this);
        }
    }


    /**
     * Returns a string representation of this JLabel. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this JLabel.
     */
    protected String paramString() {
	String textString = (text != null ?
			     text : "");
	String defaultIconString = (defaultIcon != null ?
				    defaultIcon.toString() : "");
	String disabledIconString = (disabledIcon != null ?
				     disabledIcon.toString() : "");
	String labelForString = (labelFor  != null ?
				 labelFor.toString() : "");
        String verticalAlignmentString;
        if (verticalAlignment == TOP) {
            verticalAlignmentString = "TOP";
        } else if (verticalAlignment == CENTER) {
            verticalAlignmentString = "CENTER";
        } else if (verticalAlignment == BOTTOM) {
            verticalAlignmentString = "BOTTOM";
        } else verticalAlignmentString = "";
        String horizontalAlignmentString;
        if (horizontalAlignment == LEFT) {
            horizontalAlignmentString = "LEFT";
        } else if (horizontalAlignment == CENTER) {
            horizontalAlignmentString = "CENTER";
        } else if (horizontalAlignment == RIGHT) {
            horizontalAlignmentString = "RIGHT";
        } else horizontalAlignmentString = "";
        String verticalTextPositionString;
        if (verticalTextPosition == TOP) {
            verticalTextPositionString = "TOP";
        } else if (verticalTextPosition == CENTER) {
            verticalTextPositionString = "CENTER";
        } else if (verticalTextPosition == BOTTOM) {
            verticalTextPositionString = "BOTTOM";
        } else verticalTextPositionString = "";
        String horizontalTextPositionString;
        if (horizontalTextPosition == LEFT) {
            horizontalTextPositionString = "LEFT";
        } else if (horizontalTextPosition == CENTER) {
            horizontalTextPositionString = "CENTER";
        } else if (horizontalTextPosition == RIGHT) {
            horizontalTextPositionString = "RIGHT";
        } else horizontalTextPositionString = "";

	return super.paramString() +
	",defaultIcon=" + defaultIconString +
	",disabledIcon=" + disabledIconString +
	",horizontalAlignment=" + horizontalAlignmentString +
	",horizontalTextPosition=" + horizontalTextPositionString +
	",iconTextGap=" + iconTextGap +
	",labelFor=" + labelForString +
	",text=" + textString +
	",verticalAlignment=" + verticalAlignmentString +
	",verticalTextPosition=" + verticalTextPositionString;
    }

    /**
     * --- Accessibility Support ---
     */

    /** 
     * Get the AccessibleContext of this object 
     *
     * @return the AccessibleContext of this object
     * @beaninfo
     *       expert: true
     *  description: The AccessibleContext associated with this Label.
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJLabel();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJLabel extends AccessibleJComponent {

        /**
         * Get the accessible name of this object.  
         * 
         * @return the localized name of the object -- can be null if this 
         * object does not have a name
         * @see AccessibleContext#setAccessibleName
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                if (getText() == null) {
                    return super.getAccessibleName();
                } else {
                    return getText();
                }
            }
        }

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.LABEL;
        }

    }  // AccessibleJComponent

    /**
     * Get the component this is labelling.
     *
     * @return the Component this is labelling.  Can be null if this
     * does not label a Component.  If the displayedMnemonic 
     * property is set and the labelFor property is also set, the label 
     * will call the requestFocus method of the component specified by the
     * labelFor property when the mnemonic is activated.
     *
     * @see #getDisplayedMnemonic
     * @see #setDisplayedMnemonic
     */
    public Component getLabelFor() {
        return labelFor;
    }

    /**
     * Set the component this is labelling.  Can be null if this does not 
     * label a Component.  If the displayedMnemonic property is set
     * and the labelFor property is also set, the label will
     * call the requestFocus method of the component specified by the
     * labelFor property when the mnemonic is activated.
     *
     * @param c  the Component this label is for, or null if the label is
     *           not the label for a component
     *
     * @see #getDisplayedMnemonic
     * @see #setDisplayedMnemonic
     * @beaninfo
     *        bound: true
     *  description: The component this is labelling.
     */
    public void setLabelFor(Component c) {
        Component oldC = labelFor;
        labelFor = c;
        firePropertyChange("labelFor", oldC, c);        

        if (oldC instanceof JComponent) {
            ((JComponent) c).putClientProperty(LABELED_BY_PROPERTY,null);
        }
        if (c instanceof JComponent) {
            ((JComponent) c).putClientProperty(LABELED_BY_PROPERTY,this);
        }
    }

}