/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
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
package msi.gama.interfaces;

import java.util.*;
import msi.gama.environment.ModelEnvironment;
import msi.gama.kernel.experiment.IExperiment;

/**
 * Written by drogoul Modified on 29 déc. 2010
 * 
 * @todo Description
 * 
 */
public interface IModel extends ISymbol {

	public static final String VERSION = "version";
	public static final String AUTHOR = "author";
	public static final String DEFAULT_EXPERIMENT = "default";

	@Override
	public abstract void dispose();

	public abstract ISpecies getWorldSpecies();

	public abstract IExperiment getExperiment(final String s);

	String getRelativeFilePath(String filePath, boolean shouldExist);

	String getBaseDirectory();

	public abstract String getFileName();

	public abstract Collection<IExperiment> getExperiments();

	public abstract ModelEnvironment getModelEnvironment();

}