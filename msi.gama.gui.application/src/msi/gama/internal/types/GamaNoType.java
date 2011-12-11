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
package msi.gama.internal.types;

import msi.gama.interfaces.*;

/**
 * The type used to represent an agent of a species. Should be used by the species for all the
 * operations relative to casting, etc.
 * 
 * Written by drogoul Modified on 1 aožt 2010
 * 
 * @todo Description
 * 
 */
public class GamaNoType extends GamaType {

	public GamaNoType() {
		name = NONE_STR;
		id = NONE;
		support = Object.class;
	}

	@Override
	public Object cast(final IScope scope, final Object obj, final Object param) {
		return obj;
	}

	@Override
	public Object getDefault() {
		return null;
	}

	@Override
	public boolean isSuperTypeOf(final IType type) {
		return true;
	}

}
