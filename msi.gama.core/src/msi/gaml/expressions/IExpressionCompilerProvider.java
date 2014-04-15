/*********************************************************************************************
 * 
 *
 * 'IExpressionCompilerProvider.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gaml.expressions;

import msi.gaml.expressions.IExpressionCompiler;

/**
 * Class IExpressionCompilerProvider.
 * 
 * @author drogoul
 * @since 11 avr. 2014
 * 
 */
public interface IExpressionCompilerProvider {

	public abstract IExpressionCompiler newParser();

}