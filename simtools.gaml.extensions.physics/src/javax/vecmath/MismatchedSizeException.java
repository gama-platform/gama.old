/*******************************************************************************************************
 *
 * MismatchedSizeException.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;

/**
 * Indicates that an operation cannot be completed properly because
 * of a mismatch in the sizes of object attributes.
 */
public class MismatchedSizeException extends RuntimeException{


/**
 * Create the exception object with default values.
 */
  public MismatchedSizeException(){
  }

/**
 * Create the exception object that outputs a message.
 * @param str the message string to be output.
 */
  public MismatchedSizeException(String str){

    super(str);
  }

}

