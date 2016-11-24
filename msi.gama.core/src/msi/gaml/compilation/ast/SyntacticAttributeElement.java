/*********************************************************************************************
 *
 * 'SyntacticAttributeElement.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.compilation.ast;

import org.eclipse.emf.ecore.EObject;

public class SyntacticAttributeElement extends SyntacticSingleElement {

	final String name;

	public SyntacticAttributeElement(final String keyword, final String name, final EObject statement) {
		super(keyword, null, statement);
		 this.name = name;
	}

	@Override
	public String toString() {
		return "Attribute " + getName();
	}

	 @Override
	 public String getName() {
	 return name;
	 }

}
