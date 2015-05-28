/**
 * Created by drogoul, 27 mai 2015
 * 
 */
package msi.gaml.statements;

import java.util.*;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.*;
import msi.gama.database.sql.SqlConnection;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.GamaCSVFile;
import msi.gama.util.matrix.IMatrix;
import msi.gaml.descriptions.IExpressionDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.Types;

/**
 * Class CreateFromDatabaseDelegate.
 *
 * @author drogoul
 * @since 27 mai 2015
 *
 */
public class CreateFromCSVDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 * 
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(java.lang.Object)
	 */
	@Override
	public boolean acceptSource(Object source) {
		return (source instanceof GamaCSVFile);
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes
	 * from a CSV values descring a synthetic population
	 * 
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope,
	 *      java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(IScope scope, List<Map> inits, Integer max,
			Object input, Arguments init, CreateStatement statement) {
		GamaCSVFile source = (GamaCSVFile) input;
		IExpression header = statement.getHeader();
		if ( header != null ) {
			source.forceHeader(Cast.asBool(scope, header.value(scope)));
		}
		final boolean hasHeader = source.hasHeader();
		IMatrix mat = source.getContents(scope);
		if ( mat == null || mat.isEmpty(scope) ) { return false; }
		int rows = mat.getRows(scope);
		int cols = mat.getCols(scope);
		rows = max == null ? rows : Math.min(rows, max);

		List headers;
		if ( hasHeader ) {
			headers = source.getAttributes(scope);
		} else {
			headers = new ArrayList();
			for ( int j = 0; j < cols; j++ ) {
				headers.add(j);
			}
		}
		for ( int i = 0; i < rows; i++ ) {
			final GamaMap map = GamaMapFactory.create(hasHeader ? Types.STRING : Types.INT, Types.NO_TYPE);
			final IList vals = mat.getRow(scope, i);
			for ( int j = 0; j < cols; j++ ) {
				map.put(headers.get(j), vals.get(j));
			}
			// CSV attributes are mixed with the attributes of agents
			statement.fillWithUserInit(scope, map);
			inits.add(map);
		}
		return true;
	}


}
