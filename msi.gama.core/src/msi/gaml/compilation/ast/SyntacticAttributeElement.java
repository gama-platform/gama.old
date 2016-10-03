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
