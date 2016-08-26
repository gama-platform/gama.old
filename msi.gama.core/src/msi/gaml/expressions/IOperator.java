/*********************************************************************************************
 * 
 * 
 * 'IOperator.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

/**
 * Written by drogoul Modified on 22 ao�t 2010
 * 
 * @todo Description
 * 
 */
public interface IOperator extends IExpression {

	public abstract IExpression arg(int i);

	// public abstract OperatorProto getPrototype();

}