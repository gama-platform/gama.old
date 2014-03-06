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
import msi.gaml.expressions.IExpression;

@type(name = IKeyword.MAP, id = IType.MAP, wraps = { GamaMap.class }, kind = ISymbolKind.Variable.CONTAINER)
public class GamaMapType extends GamaContainerType<GamaMap> {

	@Override
	public GamaMap cast(final IScope scope, final Object obj, final Object param, final IType keyType,
		final IType contentType) throws GamaRuntimeException {
		GamaMap result = staticCast(scope, obj, keyType, contentType);
		return result;
	}

	public static GamaMap staticCast(final IScope scope, final Object obj, final IType keyType, final IType contentsType) {
		if ( obj == null ) { return new GamaMap(); }
		// if ( obj instanceof GamaPair ) { return new GamaMap(GamaPairType.staticCast(scope, obj, keyType,
		// contentsType)); }
		if ( obj instanceof IAgent ) {
			// We collect all the variables / attributes of the agent
			IAgent agent = (IAgent) obj;
			GamaMap<String, Object> map = new GamaMap();
			for ( String s : agent.getSpecies().getVarNames() ) {
				map.put(s, agent.getDirectVarValue(scope, s));
			}
			map.putAll(agent.getAttributes());
			GamaMap shapeAttr = agent.getGeometry().getAttributes();
			if ( shapeAttr != null ) {
				map.putAll(shapeAttr);
			}
			return map.mapValue(scope, keyType, contentsType);
		}
		if ( obj instanceof IContainer ) { return ((IContainer) obj).mapValue(scope, keyType, contentsType); }
		final GamaMap result = new GamaMap();
		result.put(GamaType.toType(scope, obj, keyType), GamaType.toType(scope, obj, contentsType));
		return result;
	}

	@Override
	public IType keyTypeIfCasting(final IExpression exp) {
		IType itemType = exp.getType();
		if ( itemType.isAgentType() ) { return Types.get(STRING); }
		switch (itemType.id()) {
			case PAIR:
			case MAP:
				return itemType.getKeyType();
			case LIST:
			case MATRIX:
				return itemType.getContentType();
			case GRAPH:
				return Types.get(PAIR);
		}
		return itemType;
	}

	@Override
	public IType contentsTypeIfCasting(final IExpression exp) {
		IType itemType = exp.getType();
		if ( itemType.isAgentType() ) { return Types.NO_TYPE; }
		switch (itemType.id()) {
			case PAIR:
			case GRAPH:
			case MAP:
			case LIST:
			case MATRIX:
				return itemType.getContentType();

		}
		return itemType;
	}

}
