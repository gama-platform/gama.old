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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.compilation;

import msi.gama.metamodel.agent.IAgent;
import msi.gama.runtime.IScope;
import msi.gaml.skills.*;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 14 ao�t 2010. Modified on 23 Apr. 2013.
 * A general purpose helper that can be subclassed like a Runnable.
 * 
 * @todo Description
 * 
 */
public abstract class GamaHelper<T> {

	final Class skillClass;
	final IType returnType;

	public GamaHelper() {
		this(null);
	}

	public GamaHelper(final Class clazz) {
		this(Types.NO_TYPE, clazz);
	}

	public GamaHelper(final IType type, final Class clazz) {
		if ( clazz != null && Skill.class.isAssignableFrom(clazz) ) {
			skillClass = clazz;
		} else {
			skillClass = null;
		}
		returnType = type;
	}

	public Class getSkillClass() {
		return skillClass;
	}

	public IType getReturnType() {
		return returnType;
	}

	public T run(final IScope scope) {
		return null;
	};

	public T run(final IScope scope, final Object ... values) {
		return null;
	}

	public T run(final IScope scope, final IAgent agent, final ISkill target, final Object ... values) {
		return null;
	}

}
