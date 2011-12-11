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
