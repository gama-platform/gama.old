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
package msi.gaml.expressions;

import msi.gama.interfaces.*;

public class SpeciesConstantExpression extends ConstantExpression {

	boolean computed = false;

	public SpeciesConstantExpression(final Object val, final IType t, final IType ct) {
		super(val, t, ct);
	}

	@Override
	public Object value(final IScope scope) {
		if ( !computed ) {
			value = scope.getAgentScope().getVisibleSpecies((String) value);
			computed = true;
		}

		return value;
	}

	@Override
	public IType getContentType() {
		return super.getContentType();
	}

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toGaml() {
		if ( computed ) { return super.toGaml(); }
		return (String) value;
	}

}
