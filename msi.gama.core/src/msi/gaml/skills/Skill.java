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
package msi.gaml.skills;

import java.util.*;
import msi.gama.common.util.FileUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.MultiProperties;
import msi.gama.runtime.IScope;
import msi.gaml.compilation.*;

public abstract class Skill implements ISkill {

	private static final Map<String, Class> classes;

	static {
		classes = new HashMap();
		try {
			MultiProperties mp = FileUtils.getGamaProperties(MultiProperties.SKILLS);

			for ( String className : mp.keySet() ) {
				for ( String keyword : mp.get(className) ) {
					try {
						classes.put(keyword, Class.forName(className));
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GamlException e1) {
			e1.printStackTrace();
		}
	}

	protected IAgent getCurrentAgent(final IScope scope) {
		return scope.getAgentScope();
	}

	public static Class getSkillClassFor(final String sn) {
		return classes.get(sn);
	}

	public static ISkill createSharedSkillFor(final Class c) {
		return GamlCompiler.getSkillConstructor(c).newInstance();
	}

}
