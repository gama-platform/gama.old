/*******************************************************************************************************
 *
 * SingularMatrixException.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package javax.vecmath;

/**
 * Indicates that inverse of a matrix can not be computed.
 */
public class SingularMatrixException extends RuntimeException{

/**
 * Create the exception object with default values.
 */
  public SingularMatrixException(){
  }

/**
 * Create the exception object that outputs message.
 * @param str the message string to be output.
 */
  public SingularMatrixException(String str){

    super(str);
  }

}
