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
package msi.gama.kernel;

import java.util.*;
import msi.gama.environment.ModelEnvironment;
import msi.gama.factories.DescriptionFactory;
import msi.gama.interfaces.*;
import msi.gama.internal.descriptions.ModelDescription;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.kernel.experiment.IExperiment;

public abstract class AbstractModel extends Symbol implements IModel {

	protected final Map<String, IExperiment>	experiments	= new HashMap<String, IExperiment>();
	private ModelEnvironment					modelEnvironment;
	protected ISpecies							worldSpecies;

	protected AbstractModel(final IDescription description) {
		super(description);
	}

	@Override
	public String getRelativeFilePath(final String filePath, final boolean shouldExist) {
		return ((ModelDescription) description).constructModelRelativePath(filePath, shouldExist);
	}

	@Override
	public String getBaseDirectory() {
		return ((ModelDescription) description).getBaseDirectory();
	}

	@Override
	public String getFileName() {
		return ((ModelDescription) description).getModelFileName();
	}

	protected void addExperiment(final IExperiment exp) {
		if ( exp == null ) { return; }
		experiments.put(exp.getName(), exp);
		exp.setModel(this);
	}

	@Override
	public IExperiment getExperiment(final String s) {
		if ( s == null ) { return getExperiment(IModel.DEFAULT_EXPERIMENT); }
		return experiments.get(s);
	}

	@Override
	public Collection<IExperiment> getExperiments() {
		return experiments.values();
	}

	@Override
	public void dispose() {
		super.dispose();
		worldSpecies.dispose();
		if ( modelEnvironment != null ) {
			modelEnvironment.dispose();
		}
		modelEnvironment = null;
		experiments.clear();
	}

	@Override
	public ISpecies getWorldSpecies() {
		return worldSpecies;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> children) throws GamlException {}

	protected void setModelEnvironment(final ModelEnvironment modelEnvironment) {
		this.modelEnvironment = modelEnvironment;
	}

	@Override
	public ModelEnvironment getModelEnvironment() {
		if ( modelEnvironment == null ) {
			try {
				modelEnvironment =
					new ModelEnvironment(DescriptionFactory.createDescription(ISymbol.ENVIRONMENT));
			} catch (GamlException e) {
				e.printStackTrace();
			}
		}
		return modelEnvironment;
	}

}
