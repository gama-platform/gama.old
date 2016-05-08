package msi.gama.lang.gaml.scoping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.impl.AbstractScope;

public class MapBasedScope extends AbstractScope {

	private final HashMap<QualifiedName, IEObjectDescription> elements;

	protected MapBasedScope(final IScope parent, final HashMap<QualifiedName, IEObjectDescription> elements) {
		super(parent, false);
		this.elements = elements;
	}

	@Override
	protected Iterable<IEObjectDescription> getAllLocalElements() {
		return elements.values();
	}

	@Override
	protected Iterable<IEObjectDescription> getLocalElementsByName(final QualifiedName name) {
		final IEObjectDescription result = elements.get(name);
		if (result == null) {
			return Collections.emptyList();
		}
		return Collections.singleton(result);
	}

	@Override
	protected IEObjectDescription getSingleLocalElementByName(final QualifiedName name) {
		return elements.get(name);
	}

	@Override
	protected boolean isShadowed(final IEObjectDescription fromParent) {
		return elements.containsKey(fromParent.getName());
	}

	@Override
	protected Iterable<IEObjectDescription> getLocalElementsByEObject(final EObject object, final URI uri) {
		for (final Map.Entry<QualifiedName, IEObjectDescription> entry : elements.entrySet()) {
			final IEObjectDescription input = entry.getValue();
			if (input.getEObjectOrProxy() == object || uri.equals(input.getEObjectURI())) {
				return Collections.singleton(elements.get(input.getName()));

			}
		}
		return Collections.EMPTY_LIST;

	}

}