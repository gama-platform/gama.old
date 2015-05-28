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
public class CreateFromGenstarDelegate implements ICreateDelegate {

	/**
	 * Method acceptSource()
	 * 
	 * @see msi.gama.common.interfaces.ICreateDelegate#acceptSource(java.lang.Object)
	 */
	@Override
	public boolean acceptSource(Object source) {
		return (source instanceof List
				&& ((List) source).get(0) instanceof String && ((List) source)
				.get(0).equals(IKeyword.GENSTAR_POPULATION));
	}

	/**
	 * Method createFrom() Method used to read initial values and attributes
	 * from a CSV values descring a synthetic population
	 * 
	 * @author Vo Duc An
	 * @since 04-09-2012
	 * @see msi.gama.common.interfaces.ICreateDelegate#createFrom(msi.gama.runtime.IScope,
	 *      java.util.List, int, java.lang.Object)
	 */
	@Override
	public boolean createFrom(IScope scope, List<Map> inits, Integer max,
			Object source, Arguments init, CreateStatement statement) {
		final IList<Map> syntheticPopulation = (IList<Map>) source;
		final int num = max == null ? syntheticPopulation.length(scope) - 1
				: Math.min(syntheticPopulation.length(scope) - 1, max);
		// the first element of syntheticPopulation a string (i.e.,
		// "genstar_population")
		for (int i = 1; i < num; i++) {
			final Map genstarInit = syntheticPopulation.get(i);
			statement.fillWithUserInit(scope, genstarInit);
			// mix genstar's init attributes with user's init
			inits.add(genstarInit);
		}
		return true;
	}


}
