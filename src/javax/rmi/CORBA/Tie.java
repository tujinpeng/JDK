/*
 * @(#)Tie.java	1.18 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Licensed Materials - Property of IBM
 * RMI-IIOP v1.0
 * Copyright IBM Corp. 1998 1999  All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or
 * disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */

package javax.rmi.CORBA;

import java.rmi.Remote;
import java.util.Hashtable;

import org.omg.CORBA.portable.ApplicationException;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.ObjectImpl; 
import org.omg.CORBA.portable.ResponseHandler;
import org.omg.CORBA.portable.Delegate;
import org.omg.CORBA.ORB;

/**
 * Defines methods which all RMI-IIOP server side ties must implement.
 */
public interface Tie extends org.omg.CORBA.portable.InvokeHandler {
    /**
     * Returns an object reference for the target object represented by
     * this tie.
     * @return an object reference for the target object.
     */
    org.omg.CORBA.Object thisObject();
    
    /**
     * Deactivates the target object represented by this tie.
     */
    void deactivate() throws java.rmi.NoSuchObjectException;
    
    /**
     * Returns the ORB for this tie.
     * @return the ORB.
     */
    ORB orb();
    
    /**
     * Sets the ORB for this tie.
     * @param orb the ORB.
     */
    void orb(ORB orb);
    
    /**
     * Called by {@link Util#registerTarget} to set the target
     * for this tie.
     * @param target the object to use as the target for this tie.
     */
    void setTarget(java.rmi.Remote target);
   
    /**
     * Returns the target for this tie.
     * @return the target.
     */
    java.rmi.Remote getTarget();
}