/**
 * Created by drogoul, 27 mai 2015
 *
 */
package msi.gama.database;

import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;

import msi.gama.common.interfaces.ICreateDelegate;
import msi.gama.database.sql.SqlConnection;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gaml.expressions.IExpression;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.CreateStatement;
import msi.gaml.statements.Facets.Facet;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class CreateFromDatabaseDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(IScope, java.lang.Object)
	 */
	@Override
	public boolean acceptSource(final IScope scope, final Object source) {
		return source instanceof IList && !((IList) source).isEmpty() && ((IList) source).get(0) instanceof List;
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes from a list of values
	 *
	 * @author thai.truongminh@gmail.com
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope, java.util.List, int,
	 *      java.lang.Object)
	 */
	@Override
	public boolean createFrom(final IScope scope, final List<Map<String, Object>> inits, final Integer max,
			final Object source, final Arguments init, final CreateStatement statement) {
		final IList<IList<Object>> input = (IList<IList<Object>>) source;
		// get Column name
		final IList<Object> colNames = input.get(0);
		// get Column type
		final IList<Object> colTypes = input.get(1);
		// Get ResultSet
		final IList<IList<Object>> initValue = (IList) input.get(2);
		// set initialValues to generate species
		final int num = max == null ? initValue.length(scope) : Math.min(max, initValue.length(scope));
		for (int i = 0; i < num; i++) {
			final IList<Object> rowList = initValue.get(i);
			final Map map = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
			computeInits(scope, map, rowList, colTypes, colNames, init);
			inits.add(map);
		}
		return true;

	}

	/*
	 * thai.truongminh@gmail.com Method: GamaList2ListMap Description: created date : 13-09-2012 25-Feb-2013: Add
	 * transformCRS from GisUtils.transformCRS Last Modified: 25-Feb-2013
	 */
	private void computeInits(final IScope scope, final Map values, final IList<Object> rowList,
			final IList<Object> colTypes, final IList<Object> colNames, final Arguments init)
			throws GamaRuntimeException {
		if (init == null) { return; }
		for (final Facet f : init.getFacets()) {
			if (f != null) {
				final IExpression valueExpr = f.value.getExpression();
				// get parameter
				final String columnName = valueExpr.value(scope).toString().toUpperCase();
				// get column number of parameter
				final int val = colNames.indexOf(columnName);
				if (val == -1) {
					throw GamaRuntimeException.error(
							"Create from DB: " + columnName + " is not a correct column name in the DB query results",
							scope);
				}
				if (((String) colTypes.get(val)).equalsIgnoreCase(SqlConnection.GEOMETRYTYPE)) {
					final Geometry geom = (Geometry) rowList.get(val);
					values.put(f.key, new GamaShape(geom));
				} else {
					values.put(f.key, rowList.get(val));
				}

			}
		}
	}

	/**
	 * Method fromFacetType()
	 *
	 * @see msi.gama.common.interfaces.ICreateDelegate#fromFacetType()
	 */
	@Override
	public IType fromFacetType() {
		// TODO revert the modif when the returned type of actions has been improved
		// linked with the type of select action of AgentDB
		// return Types.LIST.of(Types.LIST);
		return Types.LIST;
	}

}
