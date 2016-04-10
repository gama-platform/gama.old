/**
 * Created by drogoul, 27 mai 2015
 *
 */
package msi.gaml.statements;

import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.util.*;
import msi.gama.util.file.GamaGeometryFile;
import msi.gaml.operators.fastmaths.CmnFastMath;
import msi.gaml.types.*;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public class CreateFromGeometriesDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final Object source) {
		// THIS CONDITION MUST BE CHECKED : bypass a condition that belong to the case createFromDatabase
		if( source instanceof IList && ((IList) source).get(0) instanceof IList){
			return false;
		}
		return source instanceof IList && ((IList) source).getType().getContentType().isAssignableFrom(Types.GEOMETRY)

			|| source instanceof GamaGeometryFile;

		// ||
		// source instanceof GamaShapeFile || source instanceof GamaOsmFile || source instanceof GamaSVGFile || source instanceof GamaDXFFile );
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes
	 * from a CSV values describing a synthetic population
	 *
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope, java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map> inits, final Integer max, final Object input,
		final Arguments init, final CreateStatement statement) {
		IAddressableContainer<Integer, GamaShape, Integer, GamaShape> container =
			(IAddressableContainer<Integer, GamaShape, Integer, GamaShape>) input;
		final int num = max == null ? container.length(scope) : CmnFastMath.min(container.length(scope), max);
		for ( int i = 0; i < num; i++ ) {
			final GamaShape g = container.get(scope, i);
			final Map map = g.getOrCreateAttributes();
			// The shape is added to the initial values
			map.put(IKeyword.SHAPE, g);
			// GIS attributes are mixed with the attributes of agents
			statement.fillWithUserInit(scope, map);
			inits.add(map);
		}
		return true;
	}

	/**
	 * Method fromFacetType()
	 * @see msi.gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType fromFacetType() {
		return Types.CONTAINER.of(Types.GEOMETRY);
	}

}
