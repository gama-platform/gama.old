/*********************************************************************************************
 * 
 *
 * 'GamlExpressionCompilerProvider.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.utils;

import msi.gaml.expressions.IExpressionCompiler;
import msi.gaml.expressions.IExpressionCompilerProvider;

/**
 * Class GamlExpressionCompilerProvider.
 * 
 * @author drogoul
 * @since 11 avr. 2014
 * 
 */
public class GamlExpressionCompilerProvider implements IExpressionCompilerProvider {

	// private static int count = 0;

	public GamlExpressionCompilerProvider() {
	}

	@Override
	public IExpressionCompiler newParser() {
		// System.out.println("Generation of expression compilers so far: " +
		// count++);
		return new GamlExpressionCompiler();
	}
}
