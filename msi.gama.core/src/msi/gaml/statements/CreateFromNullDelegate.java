/**
 * Created by drogoul, 27 mai 2015
 * 
 */
package msi.gaml.statements;

import java.util.*;

import com.vividsolutions.jts.geom.Geometry;

import msi.gama.common.interfaces.*;
import msi.gama.database.sql.SqlConnection;
import msi.gama.metamodel.shape.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.file.*;
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
public class CreateFromNullDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 * 
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(java.lang.Object)
	 */
	@Override
	public boolean acceptSource(Object source) {

		return ( source == null );
	}

	/**
	 * Method createFrom() reads initial values decribed by the modeler (facet with)
	 * 
	 * @author Alexis Drogoul
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope,
	 *      java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(IScope scope, List<Map> inits, Integer max,
			Object input, Arguments init, CreateStatement statement) {
		if ( init == null ) { return true; }
		final int num = max == null ? 1 : max;
		for ( int i = 0; i < num; i++ ) {
			final Map map = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
			statement.fillWithUserInit(scope, map);
			inits.add(map);
		}
		return true;
	}


}
