/*
 * @(#)Stylepad.java	1.8 98/08/26
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


import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.swing.text.*;
import javax.swing.*;

import java.io.*;

/**
 * Sample application using JTextPane.
 *
 * @author Timothy Prinzing
 * @version 1.8 08/26/98
 */
public class Stylepad extends Notepad {

    private static ResourceBundle resources;

    static {
        try {
            resources = ResourceBundle.getBundle("resources.Stylepad", 
                                                 Locale.getDefault());
        } catch (MissingResourceException mre) {
            System.err.println("Stylepad.properties not found");
            System.exit(0);
        }
    }

    public Stylepad() {
	super();
    }
    
    public static void main(String[] args) {
        String vers = System.getProperty("java.version");
        if (vers.compareTo("1.1.2") < 0) {
            System.out.println("!!!WARNING: Swing must be run with a " +
                               "1.1.2 or higher version VM!!!");
        }
        JFrame frame = new JFrame();
        frame.setTitle(resources.getString("Title"));
	frame.setBackground(Color.lightGray);
	frame.getContentPane().setLayout(new BorderLayout());
	frame.getContentPane().add("Center", new Stylepad());
	frame.addWindowListener(new AppCloser());
	frame.pack();
	frame.setSize(600, 480);
        frame.show();
    }

    /**
     * Fetch the list of actions supported by this
     * editor.  It is implemented to return the list
     * of actions supported by the superclass
     * augmented with the actions defined locally.
     */
    public Action[] getActions() {
        Action[] defaultActions = {
            new NewAction(),
            new OpenAction(),
            new SaveAction(),
            new ChangeKeymapAction("English"),
            new ChangeKeymapAction("Hebrew"),
            new ChangeKeymapAction("Arabic")
        };
        Action[] a = TextAction.augmentList(super.getActions(), defaultActions);
        a = TextAction.augmentList(a, hebrewActions);
        a = TextAction.augmentList(a, arabicActions);
	return a;
    }

    /**
     * Try and resolve the resource name in the local
     * resource file, and if not found fall back to
     * the superclass resource file.
     */
    protected String getResourceString(String nm) {
	String str;
	try {
	    str = this.resources.getString(nm);
	} catch (MissingResourceException mre) {
	    str = super.getResourceString(nm);
	}
	return str;
    }

    /**
     * Create an editor to represent the given document.  
     */
    protected JTextComponent createEditor() {
	StyleContext sc = new StyleContext();
	DefaultStyledDocument doc = new DefaultStyledDocument(sc);
	initDocument(doc, sc);
        JTextPane p = new JTextPane(doc);
        
        Keymap parent = p.getKeymap();
        Keymap english = p.addKeymap("English", parent);
        Keymap hebrew = p.addKeymap("Hebrew", parent);
        JTextComponent.loadKeymap( hebrew, hebrewBindings, hebrewActions );
        Keymap arabic = p.addKeymap("Arabic", parent);
        JTextComponent.loadKeymap( arabic, arabicBindings, arabicActions );
        p.setKeymap(english);

        //p.getCaret().setBlinkRate(0);
        
        return p;
    }

    /**
     * Create a menu for the app.  This is redefined to trap 
     * a couple of special entries for now.
     */
    protected JMenu createMenu(String key) {
	if (key.equals("color")) {
	    return createColorMenu();
	} 
	return super.createMenu(key);
    }


    // this will soon be replaced
    JMenu createColorMenu() {
	ActionListener a;
	JMenuItem mi;
	JMenu menu = new JMenu(getResourceString("color" + labelSuffix));
	mi = new JMenuItem(resources.getString("Red"));
	mi.setHorizontalTextPosition(JButton.RIGHT);
	mi.setIcon(new ColoredSquare(Color.red));
	a = new StyledEditorKit.ForegroundAction("set-foreground-red", Color.red);
	//a = new ColorAction(se, Color.red);
	mi.addActionListener(a);
	menu.add(mi);
	mi = new JMenuItem(resources.getString("Green"));
	mi.setHorizontalTextPosition(JButton.RIGHT);
	mi.setIcon(new ColoredSquare(Color.green));
	a = new StyledEditorKit.ForegroundAction("set-foreground-green", Color.green);
	//a = new ColorAction(se, Color.green);
	mi.addActionListener(a);
	menu.add(mi);
	mi = new JMenuItem(resources.getString("Blue"));
	mi.setHorizontalTextPosition(JButton.RIGHT);
	mi.setIcon(new ColoredSquare(Color.blue));
	a = new StyledEditorKit.ForegroundAction("set-foreground-blue", Color.blue);
	//a = new ColorAction(se, Color.blue);
	mi.addActionListener(a);
	menu.add(mi);

	return menu;
    }

    void initDocument(DefaultStyledDocument doc, StyleContext sc) {
	Wonderland w = new Wonderland(doc, sc);
	//HelloWorld h = new HelloWorld(doc, sc);
	Icon alice = new ImageIcon(resources.getString("aliceGif"));
	w.loadDocument();
    }

    JComboBox createFamilyChoices() {
        JComboBox b = new JComboBox();
	String[] fonts = getToolkit().getFontList();
	for (int i = 0; i < fonts.length; i++) {
	    b.addItem(fonts[i]);
	}
	return b;
    }

    /**
     * Trys to read a file which is assumed to be a 
     * serialization of a document.
     */
    class OpenAction extends AbstractAction {

	OpenAction() {
	    super(openAction);
	}

        public void actionPerformed(ActionEvent e) {
	    Frame frame = getFrame();
	    if (fileDialog == null) {
		fileDialog = new FileDialog(frame);
	    }
	    fileDialog.setMode(FileDialog.LOAD);
	    fileDialog.show();
	    
	    String file = fileDialog.getFile();
	    if (file == null) {
		return;
	    }
	    String directory = fileDialog.getDirectory();
	    File f = new File(directory, file);
	    if (f.exists()) {
		try {
		    FileInputStream fin = new FileInputStream(f);
		    ObjectInputStream istrm = new ObjectInputStream(fin);
		    Document doc = (Document) istrm.readObject();
		    getEditor().setDocument(doc);
		    frame.setTitle(file);
		    validate();
		} catch (IOException io) {
		    // should put in status panel
		    System.err.println("IOException: " + io.getMessage());
		} catch (ClassNotFoundException cnf) {
		    // should put in status panel
		    System.err.println("Class not found: " + cnf.getMessage());
		}
	    } else {
		// should put in status panel
		System.err.println("No such file: " + f);
	    }
	}
    }

    /**
     * Trys to write the document as a serialization.
     */
    class SaveAction extends AbstractAction {

	SaveAction() {
	    super(saveAction);
	}

        public void actionPerformed(ActionEvent e) {
	    Frame frame = getFrame();
	    if (fileDialog == null) {
		fileDialog = new FileDialog(frame);
	    }
	    fileDialog.setMode(FileDialog.SAVE);
	    fileDialog.show();
	    String file = fileDialog.getFile();
	    if (file == null) {
		return;
	    }
	    String directory = fileDialog.getDirectory();
	    File f = new File(directory, file);
	    try {
		FileOutputStream fstrm = new FileOutputStream(f);
		ObjectOutput ostrm = new ObjectOutputStream(fstrm);
		ostrm.writeObject(getEditor().getDocument());
		ostrm.flush();
	    } catch (IOException io) {
		// should put in status panel
		System.err.println("IOException: " + io.getMessage());
	    }
	}
    }

    /**
     * Creates an empty document.
     */
    class NewAction extends AbstractAction {

	NewAction() {
	    super(newAction);
	}

        public void actionPerformed(ActionEvent e) {
	    if(getEditor().getDocument() != null)
		getEditor().getDocument().removeUndoableEditListener
		            (undoHandler);
	    getEditor().setDocument(new DefaultStyledDocument());
	    getEditor().getDocument().addUndoableEditListener(undoHandler);
	    validate();
	}
    }

    class ColoredSquare implements Icon {
	Color color;
	public ColoredSquare(Color c) {
	    this.color = c;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
	    Color oldColor = g.getColor();
	    g.setColor(color);
	    g.fill3DRect(x,y,getIconWidth(), getIconHeight(), true);
	    g.setColor(oldColor);
	}
	public int getIconWidth() { return 12; }
	public int getIconHeight() { return 12; }

    }

    /**
     * Change the keyboard mapping.
     * @see DefaultEditorKit#insertBreakAction
     * @see DefaultEditorKit#getActions
     */
    static class ChangeKeymapAction extends TextAction {

        /**
         * Creates this object with the appropriate identifier.
         */
        public ChangeKeymapAction( String keymapName ) {
            super("change-keymap-" + keymapName);
            this.keymapName = keymapName;
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                Keymap map = target.getKeymap( keymapName );
                if( map != null )
                    target.setKeymap( map );
            }
        }
        
        private String keymapName;
    }

    /**
     * Places a predetermined character into the content.
     * @see DefaultEditorKit#insertBreakAction
     * @see DefaultEditorKit#getActions
     */
    static class InsertMeAction extends TextAction {

        /**
         * Creates this object with the appropriate identifier.
         */
        public InsertMeAction( String s ) {
            super("insert-me " + s);
            this.s = s;
        }

        /**
         * The operation to perform when this action is triggered.
         *
         * @param e the action event
         */
        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.replaceSelection(s);
            }
        }
        
        private String s;
    }

    static final JTextComponent.KeyBinding[] hebrewBindings = {
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('a'),
                                      "insert-me \u05E9"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('b'),
                                      "insert-me \u05E0"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('c'),
                                      "insert-me \u05D1"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('d'),
                                      "insert-me \u05D2"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('e'),
                                      "insert-me \u05E7"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('f'),
                                      "insert-me \u05DB"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('g'),
                                      "insert-me \u05E2"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('h'),
                                      "insert-me \u05D9"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('i'),
                                      "insert-me \u05DF"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('j'),
                                      "insert-me \u05D7"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('k'),
                                      "insert-me \u05DC"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('l'),
                                      "insert-me \u05DA"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('m'),
                                      "insert-me \u05E6"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('n'),
                                      "insert-me \u05DE"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('o'),
                                      "insert-me \u05DD"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('p'),
                                      "insert-me \u05E4"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('q'),
                                      "insert-me /"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('r'),
                                      "insert-me \u05E8"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('s'),
                                      "insert-me \u05D3"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('t'),
                                      "insert-me \u05D0"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('u'),
                                      "insert-me \u05D5"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('v'),
                                      "insert-me \u05D4"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('w'),
                                      "insert-me '"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('x'),
                                      "insert-me \u05E1"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('y'),
                                      "insert-me \u05D8"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('z'),
                                      "insert-me \u05D6"),
    };

    static final Action[] hebrewActions = {
        new InsertMeAction("\u05D6"),
        new InsertMeAction("\u05D8"),
        new InsertMeAction("\u05E1"),
        new InsertMeAction("'"),
        new InsertMeAction("\u05D4"),
        new InsertMeAction("\u05D5"),
        new InsertMeAction("\u05D0"),
        new InsertMeAction("\u05D3"),
        new InsertMeAction("\u05E8"),
        new InsertMeAction("/"),
        new InsertMeAction("\u05E4"),
        new InsertMeAction("\u05DD"),
        new InsertMeAction("\u05DE"),
        new InsertMeAction("\u05E6"),
        new InsertMeAction("\u05DA"),
        new InsertMeAction("\u05DC"),
        new InsertMeAction("\u05D7"),
        new InsertMeAction("\u05DF"),
        new InsertMeAction("\u05D9"),
        new InsertMeAction("\u05E2"),
        new InsertMeAction("\u05DB"),
        new InsertMeAction("\u05E7"),
        new InsertMeAction("\u05D2"),
        new InsertMeAction("\u05D1"),
        new InsertMeAction("\u05E0"),
        new InsertMeAction("\u05E9")
    };
    
    static final JTextComponent.KeyBinding[] arabicBindings = {
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('a'),
                                      "insert-me \u0634"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('b'),
                                      "insert-me \u0644\u0627"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('c'),
                                      "insert-me \u0624"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('d'),
                                      "insert-me \u064A"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('e'),
                                      "insert-me \u062B"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('f'),
                                      "insert-me \u0628"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('g'),
                                      "insert-me \u0644"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('h'),
                                      "insert-me \u0627"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('i'),
                                      "insert-me \u0647"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('j'),
                                      "insert-me \u062A"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('k'),
                                      "insert-me \u0646"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('l'),
                                      "insert-me \u0645"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('m'),
                                      "insert-me \u0629"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('n'),
                                      "insert-me \u0649"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('o'),
                                      "insert-me \u062E"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('p'),
                                      "insert-me \u062D"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('q'),
                                      "insert-me \u0636"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('r'),
                                      "insert-me \u0642"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('s'),
                                      "insert-me \u0633"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('t'),
                                      "insert-me \u0641"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('u'),
                                      "insert-me \u0639"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('v'),
                                      "insert-me \u0631"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('w'),
                                      "insert-me \u0635"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('x'),
                                      "insert-me \u0621"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('y'),
                                      "insert-me \u063A"),
        new JTextComponent.KeyBinding(KeyStroke.getKeyStroke('z'),
                                      "insert-me \u0626")
    };

    static final Action[] arabicActions = {
        new InsertMeAction("\u0634"),
        new InsertMeAction("\u0644\u0627"),
        new InsertMeAction("\u0624"),
        new InsertMeAction("\u064A"),
        new InsertMeAction("\u062B"),
        new InsertMeAction("\u0628"),
        new InsertMeAction("\u0644"),
        new InsertMeAction("\u0627"),
        new InsertMeAction("\u0647"),
        new InsertMeAction("\u062A"),
        new InsertMeAction("\u0646"),
        new InsertMeAction("\u0645"),
        new InsertMeAction("\u0629"),
        new InsertMeAction("\u0649"),
        new InsertMeAction("\u062E"),
        new InsertMeAction("\u062D"),
        new InsertMeAction("\u0636"),
        new InsertMeAction("\u0642"),
        new InsertMeAction("\u0633"),
        new InsertMeAction("\u0641"),
        new InsertMeAction("\u0639"),
        new InsertMeAction("\u0631"),
        new InsertMeAction("\u0635"),
        new InsertMeAction("\u0621"),
        new InsertMeAction("\u063A"),
        new InsertMeAction("\u0626")
    };
}