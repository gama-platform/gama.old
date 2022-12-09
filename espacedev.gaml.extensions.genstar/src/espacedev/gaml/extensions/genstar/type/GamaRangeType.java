/*******************************************************************************************************
 *
 * GamaRangeType.java, in espacedev.gaml.extensions.genstar, is part of the source code of the GAMA modeling and
 * simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package espacedev.gaml.extensions.genstar.type;

import java.util.Arrays;

import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaPair;
import msi.gaml.operators.Cast;
import msi.gaml.types.GamaType;
import msi.gaml.types.IType;

/**
 * The Class GamaRangeType.
 */
@type (
		name = "gen_range",
		id = GamaRangeType.RANGETYPE_ID,
		wraps = { GamaRange.class },
		concept = { IConcept.TYPE },
		doc = @doc ("The range type defined in the genstar plugin"))
public class GamaRangeType extends GamaType<GamaRange> {

	/** The Constant id. */
	public static final int RANGETYPE_ID = IType.AVAILABLE_TYPES + 3524246;

	@Override
	public boolean canCastToConst() {
		return true;
	}

	@Override
	@SuppressWarnings ({ "rawtypes" })
	@doc ("Cast a point (i.e. from x to y), a pair (i.e. from key to value), "
			+ "a list (i.e. from list[0] to list[1]) or a string into a GamaRange "
			+ "(i.e. spliting using \"->\", \":\", \";\", \"|\" or \" \")")
	public GamaRange cast(final IScope scope, final Object obj, final Object param, final boolean copy) {
		if (obj instanceof GamaRange gr) return gr;
		if (obj instanceof GamaPoint p) return new GamaRange(p.x, p.y);
		if (obj instanceof GamaPair p) return new GamaRange(Cast.asFloat(scope, p.key), Cast.asFloat(scope, p.value));
		if (obj instanceof GamaList list) {
			if (list.size() == 2)
				return new GamaRange(Cast.asFloat(scope, list.get(0)), Cast.asFloat(scope, list.get(1)));
			return null;
		}
		if (obj instanceof String s) {
			for (String spliter : Arrays.asList("->", ":", ";", "|", " ")) {
				String[] list = s.split(spliter);
				if (list.length == 2) return new GamaRange(Cast.asFloat(scope, list[0]), Cast.asFloat(scope, list[1]));
			}
		}
		return null;
	}

	@Override
	public GamaRange getDefault() { return null; }

}
