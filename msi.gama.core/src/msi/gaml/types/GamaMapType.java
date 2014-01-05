/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.types;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.type;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.species.ISpecies;

@type(name = IKeyword.MAP, id = IType.MAP, wraps = { GamaMap.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaMapType extends GamaContainerType<GamaMap> {

	@Override
	public GamaMap cast(final IScope scope, final Object obj, final Object param, IType contentsType) throws GamaRuntimeException {
		if ( obj == null ) { return new GamaMap(); }
		if ( obj instanceof GamaPair ) { return new GamaMap((GamaPair) obj); }
		if ( obj instanceof ISpecies ) { return ((ISpecies) obj).mapValue(scope); }
		if ( obj instanceof IAgent ) { return ((IAgent) obj).getAttributes(); }
		if ( obj instanceof IContainer ) { return ((IContainer) obj).mapValue(scope); }
		final GamaMap result = new GamaMap();
		result.put(obj, obj);
		return result;
	}

	@Override
	public GamaMap getDefault() {
		return null;
	}

	@Override
	public IType defaultContentType() {
		return Types.NO_TYPE;
	}

	@Override
	public boolean hasContents() {
		return true;
	}

}
