/*
 * @(#)ServiceInformationHolder.java	1.6 98/09/01
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

package org.omg.CORBA;

/**
 * A Holder class for a <code>ServiceInformation</code> object
 * that is used to store "out" and "inout" parameters in IDL methods.
 * If an IDL method signature has an IDL <code>xxx</code> as an "out"
 * or "inout" parameter, the programmer must pass an instance of
 * <code>ServiceInformationHolder</code> as the corresponding
 * parameter in the method invocation; for "inout" parameters, the programmer
 * must also fill the "in" value to be sent to the server.
 * Before the method invocation returns, the ORB will fill in the
 * value corresponding to the "out" value returned from the server.
 * <P>
 * If <code>myServiceInformationHolder</code> is an instance of <code>ServiceInformationHolder</code>,
 * the value stored in its <code>value</code> field can be accessed with
 * <code>myServiceInformationHolder.value</code>.
 */
public final class ServiceInformationHolder
     implements org.omg.CORBA.portable.Streamable {

    /**
     * The <code>ServiceInformation</code> value held by this
	 * <code>ServiceInformationHolder</code> object in its <code>value</code> field.
     */
    public ServiceInformation value;

    /**
     * Constructs a new <code>ServiceInformationHolder</code> object with its
     * <code>value</code> field initialized to null.
     */
    public ServiceInformationHolder() {
        this(null);
    }
	
    /**
     * Constructs a new <code>ServiceInformationHolder</code> object with its
     * <code>value</code> field initialized to the given
     * <code>ServiceInformation</code> object.
	 *
     * @param arg the <code>ServiceInformation</code> object with which to initialize
     *                the <code>value</code> field of the newly-created
     *                <code>ServiceInformationHolder</code> object
     */
    public ServiceInformationHolder(org.omg.CORBA.ServiceInformation arg) {
        value = arg;
    }


    /**
     * Marshals the value in this <code>ServiceInformationHolder</code> object's
	 * <code>value</code> field to the output stream <code>out</code>.
     *
     * @param out the <code>OutputStream</code> object that will contain
	 *               the CDR formatted data
     */
    public void _write(org.omg.CORBA.portable.OutputStream out) {
        org.omg.CORBA.ServiceInformationHelper.write(out, value);
    }

    /**
     * Reads unmarshalled data from the input stream <code>in</code> and assigns it to
	 * the <code>value</code> field in this <code>ServiceInformationHolder</code> object.
     *
     * @param in the <code>InputStream</code> object containing CDR 
	 *              formatted data from the wire
     */
    public void _read(org.omg.CORBA.portable.InputStream in) {
        value = org.omg.CORBA.ServiceInformationHelper.read(in);
    }

    /**
     * Retrieves the <code>TypeCode</code> object that corresponds
	 * to the value held in this <code>ServiceInformationHolder</code> object's
	 * <code>value</code> field.
     *
     * @return    the type code for the value held in this <code>ServiceInformationHolder</code>
	 *            object
     */
    public org.omg.CORBA.TypeCode _type() {
        return org.omg.CORBA.ServiceInformationHelper.type();
    }
}
