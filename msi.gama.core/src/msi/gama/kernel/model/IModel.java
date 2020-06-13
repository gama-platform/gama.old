/*******************************************************************************************************
 *
 * msi.gama.kernel.model.IModel.java, in plugin msi.gama.core, is part of the source code of the GAMA modeling and
 * simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.kernel.model;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.test.TestStatement;

/**
 * Written by drogoul Modified on 29 d�c. 2010
 *
 * @todo Description
 *
 */
public interface IModel extends ISpecies {

	ISpecies getSpecies(String speciesName);

	ISpecies getSpecies(String speciesName, SpeciesDescription specDes);

	IExperimentPlan getExperiment(final String s);

	String getWorkingPath();

	String getFilePath();

	String getProjectPath();

	Map<String, ISpecies> getAllSpecies();

	Collection<String> getImportedPaths();

	List<TestStatement> getAllTests();

	@Override
	default URI getURI() {
		final ModelDescription md = (ModelDescription) getDescription();
		if (md == null) { return null; }
		final EObject o = md.getUnderlyingElement();
		if (o == null) { return null; }
		final Resource r = o.eResource();
		if (r == null) { return null; }
		return r.getURI();
	}

}