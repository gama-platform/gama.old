/*******************************************************************************************************
 *
 * msi.gaml.types.GamaPathType.java, in plugin msi.gama.core,
 * is part of the source code of the GAMA modeling and simulation platform (v. 1.8)
 * 
 * (c) 2007-2018 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gaml.types;

import java.util.List;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.path.GamaPath;
import msi.gama.util.path.IPath;
import msi.gama.util.path.PathFactory;
import msi.gaml.operators.Cast;

@type (
		name = IKeyword.PATH,
		id = IType.PATH,
		wraps = { IPath.class, GamaPath.class },
		kind = ISymbolKind.Variable.REGULAR,
		concept = { IConcept.TYPE },
		doc = @doc ("Ordered lists of objects that represent a path in a graph"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamaPathType extends GamaType<IPath> {

	@Override
	public IPath cast(final IScope scope, final Object obj, final Object param, final boolean copy)
			throws GamaRuntimeException {
		return staticCast(scope, obj, param, copy);
	}

	@Override
	public IPath getDefault() {
		return null;
	}

	public static IPath staticCast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof IPath) { return (IPath) obj; }
		if (obj instanceof List) {
			// List<ILocation> list = new GamaList();
			final List<IShape> list = GamaListFactory.create(Types.GEOMETRY);
			boolean isEdges = true;

			for (final Object p : (List) obj) {
				list.add(Cast.asPoint(scope, p));
				if (isEdges && !(p instanceof IShape && ((IShape) p).isLine())) {
					isEdges = false;
				}
			}
			// return new GamaPath(scope.getTopology(), list);
			return PathFactory.newInstance(scope, isEdges ? (IList<IShape>) obj : (IList<IShape>) list, isEdges);
		}
		return null;
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public boolean canCastToConst() {
		return false;
	}

}
