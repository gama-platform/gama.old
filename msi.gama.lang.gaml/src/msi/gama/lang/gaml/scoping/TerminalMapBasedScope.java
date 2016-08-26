package msi.gama.lang.gaml.scoping;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

public class TerminalMapBasedScope implements IScope {

	private final Map<QualifiedName, IEObjectDescription> elements;

	protected TerminalMapBasedScope(final Map<QualifiedName, IEObjectDescription> elements) {
		this.elements = elements;
	}

	@Override
	public IEObjectDescription getSingleElement(final QualifiedName name) {
		return elements.get(name);
	}

	@Override
	public Iterable<IEObjectDescription> getAllElements() {
		return elements.values();
	}

	@Override
	public Iterable<IEObjectDescription> getElements(final QualifiedName name) {
		final IEObjectDescription result = elements.get(name);
		if (result == null) {
			return Collections.emptyList();
		}
		return Collections.singleton(result);
	}

	@Override
	public IEObjectDescription getSingleElement(final EObject object) {
		final URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(object);
		for (final IEObjectDescription input : elements.values()) {
			if (input.getEObjectOrProxy() == object || uri.equals(input.getEObjectURI())) {
				return input;
			}
		}
		return null;
	}

	@Override
	public List<IEObjectDescription> getElements(final EObject object) {
		final URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(object);
		for (final IEObjectDescription input : elements.values()) {
			if (input.getEObjectOrProxy() == object || uri.equals(input.getEObjectURI())) {
				return Collections.singletonList(input);
			}
		}
		return Collections.EMPTY_LIST;
	}

}