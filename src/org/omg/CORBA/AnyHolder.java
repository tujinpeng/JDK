/*
 * @(#)AnyHolder.java	1.21 98/08/31
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

/**
 * A Holder class for <code>Any</code> objects
 * that is used to store "out" and "inout" parameters in IDL methods.
 * If an IDL method signature has an IDL <code>any</code> as an "out"
 * or "inout" parameter, the programmer must pass an instance of
 * <code>AnyHolder</code> as the corresponding
 * parameter in the method invocation; for "inout" parameters, the programmer
 * must also fill the "in" value to be sent to the server.
 * Before the method invocation returns, the ORB will fill in the
 * value corresponding to the "out" value returned from the server.
 * <P>
 * If <code>myAnyHolder</code> is an instance of <code>AnyHolder</code>,
 * the value stored in its <code>value</code> field can be accessed with
 * <code>myAnyHolder.value</code>.
 *
 * @version	1.14, 09/09/97
 * @since       JDK1.2
 */
public final class AnyHolder implements  Streamable {
    /**
     * The <code>Any</code> value held by this <code>AnyHolder</code> object.
     */

    public Any value;

    /**
     * Constructs a new <code>AnyHolder</code> object with its
     * <code>value</code> field initialized to <code>null</code>.
     */
    public AnyHolder() {
    }

    /**
     * Constructs a new <code>AnyHolder</code> object for the given
     * <code>Any</code> object.
     * @param initial the <code>Any</code> object with which to initialize
     *                the <code>value</code> field of the new
     *                <code>AnyHolder</code> object
     */
    public AnyHolder(Any initial) {
	value = initial;
    }

    /**
     * Reads from <code>input</code> and initalizes the value in the Holder
     * with the unmarshalled data.
     *
     * @param input the InputStream containing CDR formatted data from the wire.
     */
    public void _read(InputStream input) {
	value = input.read_any();
    }

    /**
     * Marshals to <code>output</code> the value in
	 * this <code>AnyHolder</code> object.
     *
     * @param output the OutputStream which will contain the CDR formatted data.
     */
    public void _write(OutputStream output) {
	output.write_any(value);
    }

    /**
     * Returns the <code>TypeCode</code> object corresponding to the value 
	 * held in this <code>AnyHolder</code> object.
     *
     * @return    the TypeCode of the value held in 
	 *              this <code>AnyHolder</code> object
     */
    public TypeCode _type() {
	return ORB.init().get_primitive_tc(TCKind.tk_any);
    }
}