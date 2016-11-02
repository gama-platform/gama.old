/*********************************************************************************************
 *
 * 'GraphSkill.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.skills;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.GamlAnnotations.skill;

@skill(name = IKeyword.GRAPH_SKILL, concept = { IConcept.GRAPH, IConcept.SKILL })
/**
 * A future graph skill (to be defined)
 * 
 * @author Samuel Thiriot
 *
 */
public class GraphSkill extends Skill {

}
