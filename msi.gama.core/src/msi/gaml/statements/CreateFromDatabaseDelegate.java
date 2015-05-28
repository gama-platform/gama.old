/**
 * Created by drogoul, 27 mai 2015
 * 
 */
package msi.gaml.statements;

import java.util.*;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.database.sql.SqlConnection;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate. 
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public class CreateFromDatabaseDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(java.lang.Object)
	 */
	@Override
	public boolean acceptSource(Object source) {
		return ( source instanceof IList && ((IList) source).get(0) instanceof List );
	}

	/**
	 * Method createFrom()
	 * Method used to read initial values and attributes from a list of values
	 * @author thai.truongminh@gmail.com
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope, java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(IScope scope, List<Map> inits, Integer max, Object source, Arguments init, CreateStatement statement) {
		IList<GamaList<Object>> input = (IList<GamaList<Object>>) source;
		// get Column name
		final GamaList<Object> colNames = (GamaList<Object>) input.get(0);
		// get Column type
		final GamaList<Object> colTypes = (GamaList<Object>) input.get(1);
		// Get ResultSet
		final GamaList<GamaList<Object>> initValue = (GamaList)input.get(2);
		// set initialValues to generate species
		final int num = max == null ? initValue.length(scope) : Math.min(max, initValue.length(scope));
		for ( int i = 0; i < num; i++ ) {
			final GamaList<Object> rowList = initValue.get(i);
			final Map map = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
			computeInits(scope, map, rowList, colTypes, colNames, init);
			inits.add(map);
		}
		return true;
	
	}
	
	/*
	 * thai.truongminh@gmail.com
	 * Method: GamaList2ListMap
	 * Description:
	 * created date : 13-09-2012
	 * 25-Feb-2013:
	 * Add transformCRS from GisUtils.transformCRS
	 * Last Modified: 25-Feb-2013
	 */
	private void computeInits(final IScope scope, final Map values, final GamaList<Object> rowList,
		final GamaList<Object> colTypes, final GamaList<Object> colNames, Arguments init) throws GamaRuntimeException {
		if ( init == null ) { return; }
		for ( final Map.Entry<String, IExpressionDescription> f : init.entrySet() ) {
			if ( f != null ) {
				final IExpression valueExpr = f.getValue().getExpression();
				// get parameter
				final String columnName = valueExpr.value(scope).toString().toUpperCase();
				// get column number of parameter
				final int val = colNames.indexOf(columnName);
				if ( ((String) colTypes.get(val)).equalsIgnoreCase(SqlConnection.GEOMETRYTYPE) ) {
					final Geometry geom = (Geometry) rowList.get(val);
					values.put(f.getKey(), new GamaShape(geom));
				} else {
					values.put(f.getKey(), rowList.get(val));
				}

			}
		}
	}

}
