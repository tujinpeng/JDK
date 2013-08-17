/*
 * @(#)FileSystemView.java	1.9 98/08/26
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

package javax.swing.filechooser;

import javax.swing.event.*;
import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

/**
 * FileSystemView is JFileChooser's gateway to the
 * file system. Since the JDK1.1 File api doesn't allow
 * access to such information as root partitians, file type
 * information, or hidden file bits, this class is designed
 * to intuit as much OS specific file system information as
 * possible.
 *
 * FileSystemView will eventually delegate its responsibilities
 * to io File classes when JDK1.X provides more direct access to
 * file system information.
 *
 * Java Licenses may want to provide a different implemenation of
 * FileSystemView to better handle a given operation system.
 *
 * PENDING(jeff) - need to provide a specification for
 * how Mac/OS2/BeOS/etc file systems can modify FileSystemView
 * to handle their particular type of file system.
 *
 * @version 1.9 08/26/98
 * @author Jeff Dinkins
 */
public abstract class FileSystemView {

    static FileSystemView windowsFileSystemView = null;
    static FileSystemView unixFileSystemView = null;
    //static FileSystemView macFileSystemView = null;
    static FileSystemView genericFileSystemView = null;

    public static FileSystemView getFileSystemView() {
	if(File.separatorChar == '\\') {
	    if(windowsFileSystemView == null) {
		windowsFileSystemView = new WindowsFileSystemView();
	    }
	    return windowsFileSystemView;
	}

	if(File.separatorChar == '/') {
	    if(unixFileSystemView == null) {
		unixFileSystemView = new UnixFileSystemView();
	    }
	    return unixFileSystemView;
	}

	// if(File.separatorChar == ':') {
	//    if(macFileSystemView == null) {
	//	macFileSystemView = new MacFileSystemView();
	//    }
	//    return macFileSystemView;
	//}

	if(genericFileSystemView == null) {
	    genericFileSystemView = new GenericFileSystemView();
	}
	return genericFileSystemView;
    }

    /**
     * Determines if the given file is a root partition or drive.
     */
    public abstract boolean isRoot(File f);

    /**
     * creates a new folder with a default folder name.
     */
    public abstract File createNewFolder(File containingDir) throws IOException;

    /**
     * Returns whether a file is hidden or not.
     */
    public abstract boolean isHiddenFile(File f);


    /**
     * Returns all root partitians on this system. For example, on Windows,
     * this would be the A: through Z: drives.
     */
    public abstract File[] getRoots();



    // Providing default implemenations for the remaining methods
    // because most OS file systems will likely be able to use this
    // code. If a given OS can't, override these methods in its
    // implementation.

    public File getHomeDirectory() {
	return createFileObject(System.getProperty("user.home"));
    }

    /**
     * Returns a File object constructed in dir from the given filename.
     */
    public File createFileObject(File dir, String filename) {
	if(dir == null) {
	    return new File(filename);
	} else {
	    return new File(dir, filename);
	}
    }

    /**
     * Returns a File object constructed from the given path string.
     */
    public File createFileObject(String path) {
	return new File(path);
    }


    /**
     * gets the list of shown (i.e. not hidden) files
     */
    public File[] getFiles(File dir, boolean useFileHiding) {
	Vector files = new Vector();

	// add all files in dir
	String[] names = dir.list();

	File f;

	int nameCount = names == null ? 0 : names.length;

	for (int i = 0; i < nameCount; i++) {
	    f = createFileObject(dir, names[i]);
	    if(useFileHiding) {
		if(!isHiddenFile(f)) {
		    files.addElement(f);
		}
	    } else {
		files.addElement(f);
	    }
	}

	File[] fileArray = new File[files.size()];
	files.copyInto(fileArray);

	return fileArray;
    }

    /**
     * Returns the parent directory of dir.
     */
    public File getParentDirectory(File dir) {
	if(dir != null) {
	    File f = createFileObject(dir.getAbsolutePath());
	    String parentFilename = f.getParent();
	    if(parentFilename != null) {
		return new File(parentFilename);
	    }
	}
	return null;
    }
}

/**
 * FileSystemView that handles some specific unix-isms.
 */
class UnixFileSystemView extends FileSystemView {

    public boolean isRoot(File f) {
	String path = f.getAbsolutePath();
	if(path.length() == 1 && path.charAt(0) == '/') {
	    return true;
	}
	return false;
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Unix - using OpenWindow's default folder name. Can't find one for Motif/CDE.
	newFolder = createFileObject(containingDir, "NewFolder");
	int i = 1;
	while (newFolder.exists() && (i < 100)) {
	    newFolder = createFileObject(containingDir, "NewFolder." + i);
	    i++;
	}

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. On Unix,
     * all files that begin with "." are hidden.
     */
    public boolean isHiddenFile(File f) {
	if(f != null) {
	    String filename = f.getName();
	    if(filename.charAt(0) == '.') {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    /**
     * Returns the root partitian on this system. On Unix, this is just "/".
     */
    public File[] getRoots() {
	File[] roots = new File[1];
	roots[0] = new File("/");
	if(roots[0].exists() && roots[0].isDirectory()) {
	    return roots;
	}
	return null;
    }

}


/**
 * FileSystemView that handles some specific windows concepts.
 */
class WindowsFileSystemView extends FileSystemView {

    /**
     * Returns true if the given file is a root.
     */
    public boolean isRoot(File f) {
	if(!f.isAbsolute()) {
	    return false;
	}

        String parentPath = f.getParent();
        if(parentPath == null) {
            return true;
        } else {
            File parent = new File(parentPath);
            return parent.equals(f);
        }
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Using NT's default folder name
	newFolder = createFileObject(containingDir, "New Folder");
	int i = 2;
	while (newFolder.exists() && (i < 100)) {
	    newFolder = createFileObject(containingDir, "New Folder (" + i + ")");
	    i++;
	}

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. On Windows
     * there is currently no way to get this information from
     * io.File, therefore always return false.
     */
    public boolean isHiddenFile(File f) {
	return false;
    }

    /**
     * Returns all root partitians on this system. On Windows, this
     * will be the A: through Z: drives.
     */
    public File[] getRoots() {
	Vector rootsVector = new Vector();

	// Create the A: drive whether it is mounted or not
	WindowsFloppy floppy = new WindowsFloppy();
	rootsVector.addElement(floppy);

	// Run through all possible mount points and check
	// for their existance.
	for (char c = 'C'; c <= 'Z'; c++) {
	    char device[] = {c, ':', '\\'};
	    String deviceName = new String(device);
	    File deviceFile = new File(deviceName);
	    if (deviceFile != null && deviceFile.exists()) {
		rootsVector.addElement(deviceFile);
	    }
	}
	File[] roots = new File[rootsVector.size()];
	rootsVector.copyInto(roots);
	return roots;
    }

    /**
     * Fake the floppy drive. There is no way to know whether
     * it is mounted or not, and doing a file.isDirectory or
     * file.exists() causes Windows to pop up the "Insert Floppy"
     * dialog. We therefore assume that A: is the floppy drive,
     * and force it to always return true for isDirectory()
     */
    class WindowsFloppy extends File {
	public WindowsFloppy() {
	    super("A" + ":" + "\\");
	}

	public boolean isDirectory() {
	    return true;
	};
    }

}


/**
 * Fallthrough FileSystemView in case we can't determine the OS.
 */
class GenericFileSystemView extends FileSystemView {

    /**
     * Returns true if the given file is a root.
     */
    public boolean isRoot(File f) {
	if(!f.isAbsolute()) {
	    return false;
	}

        String parentPath = f.getParent();
        if(parentPath == null) {
            return true;
        } else {
            File parent = new File(parentPath);
            return parent.equals(f);
        }
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Using NT's default folder name
	newFolder = createFileObject(containingDir, "NewFolder");

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. Since we don't
     * know the OS type, always return false
     */
    public boolean isHiddenFile(File f) {
	return false;
    }

    /**
     * Returns all root partitians on this system. Since we
     * don't know what OS type this is, return a null file
     * list.
     */
    public File[] getRoots() {
	File[] roots = new File[0];
	return roots;
    }

}