/*******************************************************************************************************
 *
 * ISkill.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.common.interfaces;

import msi.gaml.descriptions.SkillDescription;
import msi.gaml.interfaces.IGamlDescription;

/**
 * SkillInterface - convenience interface for any object that might be used as a "skill" for an agent.
 *
 * @author drogoul 4 juil. 07
 */
public interface ISkill extends IGamlDescription, IVarAndActionSupport {

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	SkillDescription getDescription();
}
