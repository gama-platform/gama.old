/*********************************************************************************************
 * 
 *
 * 'IModel.java', in plugin 'msi.gama.core', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package msi.gama.kernel.model;

import java.util.List;
import java.util.Map;

import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gaml.descriptions.SpeciesDescription;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 29 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IModel extends ISpecies {

	public abstract ISpecies getSpecies(String speciesName);

	public abstract ISpecies getSpecies(String speciesName, SpeciesDescription specDes);

	public abstract IExperimentPlan getExperiment(final String s);

	String getWorkingPath();

	public abstract String getFilePath();

	public abstract String getProjectPath();

	public abstract boolean isTorus();

	public abstract Map<String, ISpecies> getAllSpecies();

	public abstract List<String> getImportedPaths();

}