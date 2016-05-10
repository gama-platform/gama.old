/*********************************************************************************************
 * 
 *
 * 'GamlScopeProvider.java', in plugin 'msi.gama.lang.gaml', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.lang.gaml.scoping;

/**
 * This class contains custom scoping description.
 * 
 * see : http://www.eclipse.org/Xtext/documentation/latest/xtext.html#scoping on
 * how and when to use it
 *
 */
public class GamlScopeProvider extends org.eclipse.xtext.scoping.impl.SimpleLocalScopeProvider {

	// @Override
	// public IScope getScope(final EObject context, final EReference reference)
	// {
	// final long begin = System.nanoTime();
	// final IScope scope = delegateGetScope(context, reference);
	// System.out.println("scoped in " + (System.nanoTime() - begin) / 1000000d
	// + " ms");
	// System.out.println("****************************************************");
	// return scope;
	//
	// }

}
