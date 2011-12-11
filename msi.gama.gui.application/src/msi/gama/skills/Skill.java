/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.skills;

import java.util.*;
import msi.gama.gui.application.Activator;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.*;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.precompiler.*;

public abstract class Skill implements ISkill {

	private static final Map<String, Class>	classes;

	static {
		classes = new HashMap();
		MultiProperties mp = new MultiProperties();
		try {
			mp = Activator.getGamaProperties(GamaProcessor.SKILLS);
		} catch (GamlException e1) {
			e1.printStackTrace();
		}
		for ( String className : mp.keySet() ) {
			for ( String keyword : mp.get(className) ) {
				try {
					classes.put(keyword, Class.forName(className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected IAgent getCurrentAgent(final IScope scope) {
		return scope.getAgentScope();
	}

	public static Class getSkillClassFor(final String sn) {
		return classes.get(sn);
	}

	public static ISkill createSharedSkillFor(final Class c) {
		return GamlCompiler.getSharedSkillConstructor(c).newInstance();
	}

}
