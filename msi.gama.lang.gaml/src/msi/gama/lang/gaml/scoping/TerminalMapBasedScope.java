package msi.gama.lang.gaml.scoping;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.naming.QualifiedName;
import org.eclipse.xtext.resource.IEObjectDescription;
import org.eclipse.xtext.scoping.IScope;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;

public class TerminalMapBasedScope implements IScope {

	private final THashMap<QualifiedName, IEObjectDescription> elements;

	protected TerminalMapBasedScope(final THashMap<QualifiedName, IEObjectDescription> elements) {
		this.elements = elements;
		elements.compact();
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
		final List<IEObjectDescription> list = getElements(object);
		return list.isEmpty() ? null : list.get(0);
	}

	@Override
	public List<IEObjectDescription> getElements(final EObject object) {
		final URI uri = EcoreUtil2.getPlatformResourceOrNormalizedURI(object);
		final IEObjectDescription[] result = new IEObjectDescription[1];
		elements.forEachValue(new TObjectProcedure<IEObjectDescription>() {

			@Override
			public boolean execute(final IEObjectDescription input) {
				if (input.getEObjectOrProxy() == object || uri.equals(input.getEObjectURI())) {
					result[0] = input;
					return false;
				}
				return true;
			}
		});

		return result[0] == null ? Collections.EMPTY_LIST : Arrays.asList(result);
	}

}