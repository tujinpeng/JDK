package com.sun.corba.se.ActivationIDL;


/**
* com/sun/corba/se/ActivationIDL/InitialNameServiceOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../../../src/share/classes/com/sun/corba/se/ActivationIDL/activation.idl
* Wednesday, January 30, 2002 8:23:27 AM PST
*/

public interface InitialNameServiceOperations 
{

  // bind initial name
  void bind (String name, org.omg.CORBA.Object obj, boolean isPersistant) throws com.sun.corba.se.ActivationIDL.InitialNameServicePackage.NameAlreadyBound;
} // interface InitialNameServiceOperations