/*******************************************************************************************************
 *
 * msi.gaml.types.GamaMapType.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IMap;
import msi.gaml.expressions.IExpression;

@type (
		name = IKeyword.MAP,
		id = IType.MAP,
		wraps = { IMap.class },
		kind = ISymbolKind.Variable.CONTAINER,
		concept = { IConcept.TYPE, IConcept.CONTAINER, IConcept.MAP },
		doc = @doc ("Represents lists of pairs key::value, where each key is unique in the map. Maps are ordered by the insertion order of elements"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaMapType extends GamaContainerType<IMap> {

	@Override
	public IMap cast(final IScope scope, final Object obj, final Object param, final IType keyType,
			final IType contentType, final boolean copy) throws GamaRuntimeException {
		return staticCast(scope, obj, keyType, contentType, copy);
	}

	public static IMap staticCast(final IScope scope, final Object obj, final IType keyType, final IType contentsType,
			final boolean copy) {
		if (obj == null) { return GamaMapFactory.create(keyType, contentsType); }
		if (obj instanceof IAgent) { return new SavedAgent(scope, (IAgent) obj); }
		if (obj instanceof IContainer) { return ((IContainer) obj).mapValue(scope, keyType, contentsType, copy); }
		final IMap result = GamaMapFactory.create(keyType, contentsType);
		result.setValueAtIndex(scope, obj, obj);
		return result;
	}

	@Override
	public int getNumberOfParameters() {
		return 2;
	}

	@Override
	public IType keyTypeIfCasting(final IExpression exp) {
		final IType itemType = exp.getGamlType();
		if (itemType.isAgentType()) { return Types.get(STRING); }
		switch (itemType.id()) {
			case PAIR:
			case MAP:
				return itemType.getKeyType();
			case MATRIX:
				return itemType.getContentType();
			case GRAPH:
				return Types.get(PAIR);
			case LIST:
				if (itemType.getContentType().id() == IType.PAIR) {
					return itemType.getContentType().getKeyType();
				} else {
					return itemType.getContentType();
				}
		}
		return itemType;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		final IType itemType = exp.getGamlType();
		if (itemType.isAgentType()) { return Types.NO_TYPE; }
		switch (itemType.id()) {
			case LIST:
				if (itemType.getContentType().id() == IType.PAIR) {
					return itemType.getContentType().getContentType();
				} else {
					return itemType.getContentType();
				}
			case PAIR:
			case GRAPH:
			case MAP:
			case MATRIX:
				return itemType.getContentType();

		}
		return itemType;
	}

	@Override
	public boolean canCastToConst() {
		return true;
	}
}
