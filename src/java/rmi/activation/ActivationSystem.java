/*
 * @(#)ActivationSystem.java	1.8 98/07/08
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

package java.rmi.activation;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.activation.UnknownGroupException;
import java.rmi.activation.UnknownObjectException;

/**
 * The <code>ActivationSystem</code> provides a means for registering
 * groups and "activatable" objects to be activated within those groups.
 * The <code>ActivationSystem</code> works closely with the
 * <code>Activator</code>, which activates objects registered via the
 * <code>ActivationSystem</code>, and the <code>ActivationMonitor</code>,
 * which obtains information about active and inactive objects,
 * and inactive groups.
 *
 * @author 	Ann Wollrath
 * @version	1.8, 07/08/98
 * @see		Activator
 * @see		ActivationMonitor
 * @since	JDK1.2
 */
public interface ActivationSystem extends Remote {

    /** The port to lookup the activation system */
    public static final int SYSTEM_PORT = 1098;
    
    /**
     * The <code>registerObject</code> method is used to register an
     * activation descriptor, <code>desc</code>, and obtain an
     * activation identifier for a activatable remote object. The
     * <code>ActivationSystem</code> creates an
     * <code>ActivationID</code> (a activation identifier) for the
     * object specified by the descriptor, <code>desc</code>, and
     * records, in stable storage, the activation descriptor and its
     * associated identifier for later use. When the <code>Activator</code>
     * receives an <code>activate</code> request for a specific identifier, it
     * looks up the activation descriptor (registered previously) for
     * the specified identifier and uses that information to activate
     * the object. <p>
     *
     * @param desc the object's activation descriptor
     * @return the activation id that can be used to activate the object
     * @exception ActivationException if registration fails (e.g., database
     * update failure, etc).
     * @exception UnknownGroupException if group referred to in
     * <code>desc</code> is not registered with this system
     * @exception RemoteException if remote call fails
     * @since JDK1.2
     */
    public ActivationID registerObject(ActivationDesc desc)
	throws ActivationException, UnknownGroupException, RemoteException;
    
    /**
     * Remove the activation id and associated descriptor previously
     * registered with the <code>ActivationSystem</code>; the object
     * can no longer be activated via the object's activation id.
     *
     * @param id the object's activation id (from previous registration)
     * @exception ActivationException if unregister fails (e.g., database
     * update failure, etc).
     * @exception UnknownObjectException if object is unknown (not registered)
     * @exception RemoteException if remote call fails
     * @since JDK1.2
     */
    public void unregisterObject(ActivationID id)
	throws ActivationException, UnknownObjectException, RemoteException;
    
    /**
     * Register the activation group. An activation group must be
     * registered with the <code>ActivationSystem</code> before objects
     * can be registered within that group.
     *
     * @param desc the group's descriptor
     * @return an identifier for the group
     * @exception ActivationException if group registration fails
     * @exception RemoteException if remote call fails
     * @since JDK1.2
     */
    public ActivationGroupID registerGroup(ActivationGroupDesc desc)
	throws ActivationException, RemoteException;

    /**
     * Callback to inform activation system that group is now
     * active. This call is made internally by the
     * <code>ActivationGroup.createGroup</code> method to inform
     * the <code>ActivationSystem</code> that the group is now
     * active.
     *
     * @param id the activation group's identifier
     * @param group the group's instantiator
     * @param incarnation the group's incarnation number
     *
     * @exception UnknownGroupException if group is not registered
     * @exception ActivationException if group is already active
     * @exception RemoteException if remote call fails
     * @since JDK1.2
     */
    public ActivationMonitor activeGroup(ActivationGroupID id,
					 ActivationInstantiator group,
					 long incarnation)
	throws UnknownGroupException, ActivationException, RemoteException;
    
    /**
     * Remove the activation group. An activation group makes this call back
     * to inform the activator that the group should be removed (destroyed).
     * If this call completes successfully, objects can no longer be
     * registered or activated within the group. All information of the
     * group and its associated objects is removed from the system.
     *
     * @param id the activation group's identifier
     * @exception ActivationException if unregister fails (e.g., database
     * update failure, etc).
     * @exception UnknownGroupException if group is not registered
     * @exception RemoteException if remote call fails
     * @since JDK1.2
     */
    public void unregisterGroup(ActivationGroupID id)
	throws ActivationException, UnknownGroupException, RemoteException;

    /**
     * Shutdown the activation system. Destroys all groups spawned by
     * the activation daemon and exits the activation daemon.
     * @since JDK1.2
     */
    public void shutdown() throws RemoteException;

    /**
     * Set the activation descriptor, <code>desc</code> for the object with
     * the activation identifier, <code>id</code>. The change will take
     * effect upon subsequent activation of the object.
     *
     * @param id the activation identifier for the activatable object
     * @param desc the activation descriptor for the activatable object
     * @exception UnknownGroupException the group associated with
     * <code>desc</code> is not a registered group
     * @exception UnknownObjectException the activation <code>id</code>
     * is not registered
     * @exception ActivationException for general failure (e.g., unable
     * to update log)
     * @exception RemoteException if remote call fails
     * @return the previous value of the activation descriptor
     * @since JDK1.2
     */
    public ActivationDesc setActivationDesc(ActivationID id,
					    ActivationDesc desc)
	throws ActivationException, UnknownObjectException,
	    UnknownGroupException, RemoteException;

    /**
     * Set the activation group descriptor, <code>desc</code> for the object
     * with the activation group identifier, <code>id</code>. The change will
     * take effect upon subsequent activation of the group.
     * 
     * @param id the activation group identifier for the activation group
     * @param desc the activation group descriptor for the activation group
     * @exception UnknownGroupException the group associated with
     * <code>id</code> is not a registered group
     * @exception ActivationException for general failure (e.g., unable
     * to update log)
     * @exception RemoteException if remote call fails
     * @return the previous value of the activation group descriptor
     * @since JDK1.2
     */
    public ActivationGroupDesc setActivationGroupDesc(ActivationGroupID id,
						      ActivationGroupDesc desc)
       throws ActivationException, UnknownGroupException, RemoteException;

    /**
     * Returns the activation descriptor, for the object with the activation
     * identifier, <code>id</code>.
     * 
     * @param id the activation identifier for the activatable object
     * @exception UnknownObjectException if <code>id</code> is not registered
     * @exception ActivationException for general failure
     * @exception RemoteException if remote call fails
     * @return the activation descriptor
     * @since JDK1.2
     */
    public ActivationDesc getActivationDesc(ActivationID id)
       throws ActivationException, UnknownObjectException, RemoteException;
	      
    /**
     * Returns the activation group descriptor, for the group
     * with the activation group identifier, <code>id</code>. 
     * 
     * @param id the activation group identifier for the group
     * @exception UnknownGroupException if <code>id</code> is not registered
     * @exception ActivationException for general failure
     * @exception RemoteException if remote call fails
     * @return the activation group descriptor
     * @since JDK1.2
     */
    public ActivationGroupDesc getActivationGroupDesc(ActivationGroupID id)
       throws ActivationException, UnknownGroupException, RemoteException;
}