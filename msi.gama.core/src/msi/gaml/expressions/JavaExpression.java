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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.expressions;

import msi.gama.runtime.IScope;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

/**
 * Written by drogoul Modified on 19 oct. 2010
 * 
 * @todo Description
 * 
 */
public abstract class JavaExpression implements IExpression {

	@Override
	public abstract Object value(IScope scope);

	@Override
	public boolean isConst() {
		return false;
	}

	@Override
	public String toGaml() {
		return "";
	}

	@Override
	public String literalValue() {
		return value(null).toString();
	}

	@Override
	public IType getContentType() {
		return getType().defaultContentType();
	}

	@Override
	public IType getKeyType() {
		return getType().defaultKeyType();
	}

	@Override
	public IType getType() {
		return Types.get(IType.NONE);
	}

	@Override
	public void dispose() {}

	@Override
	public String getTitle() {
		return null;
	}

	@Override
	public String getDocumentation() {
		return null;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void unsetTarget(final Notifier oldTarget) {}

	@Override
	public void notifyChanged(final Notification notification) {}

	@Override
	public Notifier getTarget() {
		return null;
	}

	@Override
	public void setTarget(final Notifier newTarget) {}

	@Override
	public boolean isAdapterForType(final Object type) {
		return false;
	}

}
