/*
 * @(#)MetalComboBoxUI.java	1.17 98/07/09
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.io.Serializable;
import java.beans.*;


/**
 * Metal UI for JComboBox
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @see MetalComboBoxListCellRenderer
 * @see MetalPopupMenuBorder
 * @version 1.17 07/09/98
 * @author Tom Santos
 */
public class MetalComboBoxUI extends BasicComboBoxUI {

    FocusListener focusDelegator;

    public static ComponentUI createUI(JComponent c) {
        return new MetalComboBoxUI();
    }

    public void installUI(JComponent c) {
        super.installUI(c);
        comboBox.setRequestFocusEnabled( true );
    }

    public void uninstallUI( JComponent c ) {
        super.uninstallUI( c ); 
    }

    public void paint(Graphics g, JComponent c) {
    }

    protected ComboBoxEditor createEditor() {
        return new MetalComboBoxEditor.UIResource();
    }

    protected ComboPopup createPopup() {
        return new MetalComboPopup( comboBox );
    }

    protected JButton createArrowButton() {
        JButton button = new MetalComboBoxButton( comboBox,
                                                  new MetalComboBoxIcon(),
                                                  comboBox.isEditable() ? true : false,
                                                  currentValuePane,
                                                  listBox );
        button.setMargin( new Insets( 0, 1, 1, 3 ) );
        return button;
    }

    FocusListener createFocusDelegator() {
        return new FocusDelegator();
    }

    class FocusDelegator extends FocusAdapter {
        public void focusGained( FocusEvent e ) {
            if ( metalGetComboBox().isEditable() ) {
                metalGetEditor().requestFocus();
            }
            else {
                metalGetArrowButton().requestFocus();
            }
        }
    }

    public PropertyChangeListener createPropertyChangeListener() {
        return new MetalPropertyChangeListener();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class MetalPropertyChangeListener extends BasicComboBoxUI.PropertyChangeHandler {
        public void propertyChange(PropertyChangeEvent e) {
            super.propertyChange( e );
            metalGetComboBox().setRequestFocusEnabled( true );

            String propertyName = e.getPropertyName();

            if ( propertyName.equals( "editable" ) ) {
                editablePropertyChanged( e );
            }
            else if ( propertyName.equals( "enabled" ) ) {
                enabledPropertyChanged( e );
            }
        }
    }

    protected void editablePropertyChanged( PropertyChangeEvent e ) {
        if ( arrowButton instanceof MetalComboBoxButton ) {
            MetalComboBoxButton button = (MetalComboBoxButton)arrowButton;
            button.setIconOnly( comboBox.isEditable() );
            button.setRequestFocusEnabled( (!comboBox.isEditable()) && comboBox.isEnabled() );
            comboBox.repaint();
        }
    }

    void enabledPropertyChanged( PropertyChangeEvent e ) {
        if ( arrowButton instanceof MetalComboBoxButton ) {
            arrowButton.setRequestFocusEnabled( (!comboBox.isEditable()) && comboBox.isEnabled() );
            comboBox.repaint();
        }
    }

    protected LayoutManager createLayoutManager() {
        return new MetalComboBoxLayoutManager();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class MetalComboBoxLayoutManager extends BasicComboBoxUI.ComboBoxLayoutManager {
        public void layoutContainer( Container parent ) {
            layoutComboBox( parent, this );
        }
        public void superLayout( Container parent ) {
            super.layoutContainer( parent );
        }
    }

    // This is here because of a bug in the compiler.  When a protected-inner-class-savvy compiler comes out we
    // should move this into MetalComboBoxLayoutManager.
    public void layoutComboBox( Container parent, MetalComboBoxLayoutManager manager ) {
        if ( comboBox.isEditable() ) {
            manager.superLayout( parent );
        }
        else {
            if ( arrowButton != null ) {
                Insets insets = comboBox.getInsets();
                int width = comboBox.getWidth();
                int height = comboBox.getHeight();
                arrowButton.setBounds( insets.left, insets.top,
                                       width - (insets.left + insets.right),
                                       height - (insets.top + insets.bottom) );
            }
        }
    }

    public boolean isFocusTraversable( JComboBox c ) {
        return false;
    }

    protected void installListeners() {
        if ( (itemListener = createItemListener()) != null ) {
            comboBox.addItemListener( itemListener );
        }
        if ( (propertyChangeListener = createPropertyChangeListener()) != null ) {
            comboBox.addPropertyChangeListener( propertyChangeListener );
        }

        keyListener = createKeyListener();
        focusListener = createFocusListener();
        popupKeyListener = popup.getKeyListener();
        popupMouseListener = popup.getMouseListener();
        popupMouseMotionListener = popup.getMouseMotionListener();

        if ( comboBox.getModel() != null ) {
            if ( (listDataListener = createListDataListener()) != null ) {
                comboBox.getModel().addListDataListener( listDataListener );
            }
        }

        if ( (focusDelegator = createFocusDelegator()) != null ) {
            comboBox.addFocusListener( focusDelegator );
        }
    }

    protected void uninstallListeners() {
        if ( itemListener != null ) {
            comboBox.removeItemListener( itemListener );
        }
        if ( propertyChangeListener != null ) {
            comboBox.removePropertyChangeListener( propertyChangeListener );
        }
        if ( comboBox.getModel() != null ) {
            if ( listDataListener != null ) {
                comboBox.getModel().removeListDataListener( listDataListener );
            }
        }
        if ( focusDelegator != null ) {
            comboBox.removeFocusListener( focusDelegator );
        }
    }

    protected void removeListeners() {
        if ( itemListener != null ) {
            comboBox.removeItemListener( itemListener );
        }
        if ( propertyChangeListener != null ) {
            comboBox.removePropertyChangeListener( propertyChangeListener );
        }
    }

    public void configureEditor() {
        super.configureEditor();
        if ( popupKeyListener != null ) {
            editor.removeKeyListener( popupKeyListener );
        }
        if ( focusListener != null ) {
            editor.addFocusListener( focusListener );
        }
    }

    public void unconfigureEditor() {
        super.unconfigureEditor();
        if ( focusListener != null ) {
            editor.removeFocusListener( focusListener );
        }
    }

    public void configureArrowButton() {
        if ( arrowButton != null ) {
            arrowButton.setRequestFocusEnabled( (!comboBox.isEditable()) && comboBox.isEnabled() );
            if ( keyListener != null ) {
                arrowButton.addKeyListener( keyListener );
            }
            if ( popupKeyListener != null ) {
                arrowButton.addKeyListener( popupKeyListener );
            }
            if ( focusListener != null ) {
                arrowButton.addFocusListener( focusListener );
            }
            if ( popupMouseListener != null ) {
                arrowButton.addMouseListener( popupMouseListener );
            }
            if ( popupMouseMotionListener != null ) {
                arrowButton.addMouseMotionListener( popupMouseMotionListener );
            }
        }
    }

    public void unconfigureArrowButton() {
        if ( arrowButton != null ) {
            super.unconfigureArrowButton();

            if ( keyListener != null ) {
                arrowButton.removeKeyListener( keyListener );
            }
            if ( popupKeyListener != null ) {
                arrowButton.removeKeyListener( popupKeyListener );
            }
            if ( focusListener != null ) {
                arrowButton.removeFocusListener( focusListener );
            }
        }
    }

    public Dimension getMinimumSize( JComponent c ) {
        if ( !isMinimumSizeDirty ) {
            return new Dimension( cachedMinimumSize );
        }

        Dimension size = null;

        if ( !comboBox.isEditable() &&
             arrowButton != null &&
             arrowButton instanceof MetalComboBoxButton ) {

            MetalComboBoxButton button = (MetalComboBoxButton)arrowButton;
            Insets buttonInsets = button.getInsets();
            Insets insets = comboBox.getInsets();

            size = getDisplaySize();
            size.width += insets.left + insets.right;
            size.width += buttonInsets.left + buttonInsets.right;
            size.width += buttonInsets.right + button.getComboIcon().getIconWidth();
            size.height += insets.top + insets.bottom;
            size.height += buttonInsets.top + buttonInsets.bottom;
        }
        else if ( comboBox.isEditable() &&
                  arrowButton != null &&
                  editor != null ) {
            size = super.getMinimumSize( c );
            Insets margin = arrowButton.getMargin();
            Insets insets = comboBox.getInsets();
            if ( editor instanceof JComponent ) {
                Insets editorInsets = ((JComponent)editor).getInsets();
                size.height += editorInsets.top + editorInsets.bottom;
            }
            size.height += margin.top + margin.bottom;
            size.height += insets.top + insets.bottom;
        }
        else {
            size = super.getMinimumSize( c );
        }

        cachedMinimumSize.setSize( size.width, size.height ); 
        isMinimumSizeDirty = false;

        return new Dimension( cachedMinimumSize );
    }

    protected void selectNextPossibleValue() { super.selectNextPossibleValue();}
    protected void selectPreviousPossibleValue() { super.selectPreviousPossibleValue();}

    /**
     * This method is here as a workaround for a bug in the javac compiler.
     */
    JComboBox metalGetComboBox() {
        return comboBox;
    }

    /**
     * This method is here as a workaround for a bug in the javac compiler.
     */
    JButton getArrowButton() {
        return arrowButton;
    }

    boolean isPopupVisible() {
        return super.isPopupVisible( comboBox );
    }

    void togglePopup() {
       toggleOpenClose();
    }

    protected void installKeyboardActions() {
        super.installKeyboardActions();

        AbstractAction downAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if ( isPopupVisible() ) {
                    selectNextPossibleValue();
                }
                else {
                    setPopupVisible( metalGetComboBox(), true );
                }
            }
            public boolean isEnabled() {
                return metalGetComboBox().isEnabled();
            }
        };

        metalGetComboBox().registerKeyboardAction( downAction,
                                                   KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),
                                                   JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        AbstractAction altAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                    togglePopup();
            }
            public boolean isEnabled() {
                return metalGetComboBox().isEnabled();
            }
        };
        
        metalGetComboBox().registerKeyboardAction( altAction,
                                                   KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.ALT_MASK),
                                                   JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        metalGetComboBox().registerKeyboardAction( altAction,
                                                   KeyStroke.getKeyStroke(KeyEvent.VK_UP,InputEvent.ALT_MASK),
                                                   JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        AbstractAction upAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                selectPreviousPossibleValue();
            }
            public boolean isEnabled() {
                return metalGetComboBox().isEnabled() && isPopupVisible();
            }
        };

        metalGetComboBox().registerKeyboardAction( upAction,
                                                   KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),
                                                   JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    protected void uninstallKeyboardActions() {
        super.uninstallKeyboardActions();
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0));
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,InputEvent.ALT_MASK));
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0));
        comboBox.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_UP,InputEvent.ALT_MASK));
    }

    Component metalGetEditor() {
        return editor;
    }

    JButton metalGetArrowButton() {
        return arrowButton;
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <FooUI>.
     */          
    public class MetalComboPopup extends BasicComboPopup {
        public MetalComboPopup( JComboBox cBox ) {
            super( cBox );
        }

        public void delegateFocus( MouseEvent e ) {
            if ( metalGetComboBox().isEditable() ) {
                metalGetEditor().requestFocus();
            }
        }
    }
}

