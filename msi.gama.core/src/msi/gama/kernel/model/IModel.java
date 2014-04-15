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

import java.util.Map;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 29 d�c. 2010
 * 
 * @todo Description
 * 
 */
public interface IModel extends ISpecies {

	public abstract ISpecies getSpecies(String speciesName);

	public abstract IExperimentSpecies getExperiment(final String s);

	// String getRelativeFilePath(IScope scope, String filePath, boolean shouldExist);

	String getWorkingPath();

	public abstract String getFilePath();

	public abstract String getProjectPath();

	public abstract boolean isTorus();

	public abstract Map<String, ISpecies> getAllSpecies();

}