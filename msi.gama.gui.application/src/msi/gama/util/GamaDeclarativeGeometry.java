/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC
 * 
 * Developers :
 * 
 * - Alexis Drogoul, IRD (Kernel, Metamodel, XML-based GAML), 2007-2011
 * - Vo Duc An, IRD & AUF (SWT integration, multi-level architecture), 2008-2011
 * - Patrick Taillandier, AUF & CNRS (batch framework, GeoTools & JTS integration), 2009-2011
 * - Pierrick Koch, IRD (XText-based GAML environment), 2010-2011
 * - Romain Lavaud, IRD (project-based environment), 2010
 * - Francois Sempe, IRD & AUF (EMF behavioral model, batch framework), 2007-2009
 * - Edouard Amouroux, IRD (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, IRD (OpenMap integration), 2007-2008
 */
package msi.gama.util;

import msi.gama.interfaces.*;
import msi.gama.kernel.GAMA;
import msi.gama.kernel.exceptions.GamaRuntimeException;
import com.vividsolutions.jts.geom.Geometry;

/**
 * A Geometry that is being defined by a dynamic expression and a scope of evaluation. Each call to
 * getInnerGeometry() or getLocation() translates to an evaluation of the expression. Allows to
 * define dynamic geometries that stay
 * 
 * @author Alexis Drogoul
 * @since November 2011
 * 
 */

public class GamaDeclarativeGeometry extends GamaGeometry {

	/** The expression to evalute */
	final IExpression expression;

	/** The scope of evaluation. Cannot be null */
	final IScope scope;

	/**
	 * Instantiates a new gama declarative geometry.
	 * 
	 * @param scope the scope of evaluation. Can be null, in which case the default scope of GAMA
	 *            (not guaranteed to provide error-free evaluations
	 * @param exp the expression to evaluate to get the "real" geometry. If null, translates to a
	 *            null geometry
	 */
	public GamaDeclarativeGeometry(final IScope scope, final IExpression exp) {
		this.expression = exp;
		this.scope = scope == null ? GAMA.getDefaultScope() : scope;
		refresh();
	}

	/**
	 * Refresh the inner geometry. If the expression cannot be evaluated, creates a null geometry.
	 */
	public void refresh() {
		try {
			setGeometry(buildGeometry());
		} catch (GamaRuntimeException e) {
			GAMA.reportError(e);
			setGeometry((Geometry) null, false);
		}
	}

	/**
	 * Builds the geometry.
	 * 
	 * @return the "real" geometry
	 * @throws GamaRuntimeException if the expression cannot be evaluated
	 */
	private GamaGeometry buildGeometry() throws GamaRuntimeException {
		if ( expression == null ) { throw new GamaRuntimeException(
			"The expression defining a declarative geometry is null"); }
		return Cast.asGeometry(scope, expression.value(scope));
	}

	/*
	 * Calls refresh before calling the method of GamaGeometry
	 * 
	 * @see msi.gama.util.GamaGeometry#getInnerGeometry()
	 */
	@Override
	public Geometry getInnerGeometry() {
		refresh();
		return super.getInnerGeometry();
	}

	/*
	 * Calls refresh before calling the method of GamaGeometry.
	 * 
	 * @see msi.gama.util.GamaGeometry#getLocation()
	 */
	@Override
	public GamaPoint getLocation() {
		refresh();
		return super.getLocation();
	}

	@Override
	public String toString() {
		return "dynamic geometry: " + expression.toGaml();
	}

	/*
	 * Creates a new declarative geometry based on the same parameters.
	 * 
	 * @see msi.gama.util.GamaGeometry#copy()
	 */
	@Override
	public GamaDeclarativeGeometry copy() {
		return new GamaDeclarativeGeometry(scope, expression);
	}
}
