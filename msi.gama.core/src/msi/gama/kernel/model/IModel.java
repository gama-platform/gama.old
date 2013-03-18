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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.kernel.model;

import java.util.Collection;
import msi.gama.kernel.experiment.IExperiment;
import msi.gaml.compilation.ISymbol;
import msi.gaml.species.ISpecies;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface IModel extends ISymbol {

	@Override
	public abstract void dispose();

	public abstract ISpecies getWorldSpecies();

	public abstract ISpecies getSpecies(String speciesName);

	public abstract IExperiment getExperiment(final String s);

	String getRelativeFilePath(String filePath, boolean shouldExist);

	String getFolderPath();

	public abstract String getFilePath();

	public abstract Collection<IExperiment> getExperiments();

	public abstract String getProjectPath();

	public abstract boolean isTorus();

}