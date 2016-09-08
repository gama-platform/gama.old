package msi.gaml.compilation;

import java.util.Set;

import org.eclipse.emf.ecore.EObject;

public class SyntacticAttributeElement extends SyntacticSingleElement {

	final String name;
	private Set<String> dependencies;

	public SyntacticAttributeElement(final String keyword, final String name, final EObject statement) {
		super(keyword, null, statement);
		this.name = name;
		// System.out.println("DEBUG Attribute created : " + keyword + " " +
		// name);
	}

	@Override
	public String toString() {
		return "Attribute " + name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setDependencies(final Set<String> strings) {
		dependencies = strings;
	}

	@Override
	public Set<String> getDependencies() {
		return dependencies;
	}

}
