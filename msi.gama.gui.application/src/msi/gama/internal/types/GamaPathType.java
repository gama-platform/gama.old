package msi.gama.internal.types;

import java.util.List;
import msi.gama.interfaces.*;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.util.*;
import msi.gama.util.graph.GamaPath;

@type(value = IType.PATH_STR, id = IType.PATH, wraps = { GamaPath.class })
public class GamaPathType extends GamaType<GamaPath> {

	@Override
	public GamaPath cast(final IScope scope, final Object obj, final Object param)
		throws GamaRuntimeException {
		return staticCast(scope, obj, param);
	}

	@Override
	public GamaPath getDefault() {
		return null;
	}

	public static GamaPath staticCast(final IScope scope, final Object obj, final Object param) {
		if ( obj instanceof GamaPath ) { return (GamaPath) obj; }
		if ( obj instanceof List ) {
			GamaList<IGeometry> list = new GamaList();
			for ( Object p : (List) obj ) {
				list.add(Cast.asPoint(scope, p));
			}
			return new GamaPath(scope.getAgentScope().getTopology(), list);
		}
		return null;
	}

}
