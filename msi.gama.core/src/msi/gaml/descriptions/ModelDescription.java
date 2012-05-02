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
package msi.gaml.descriptions;

import java.io.File;
import java.util.*;
import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.types.*;
import org.eclipse.emf.common.notify.*;

/**
 * Written by drogoul Modified on 16 mai 2010
 * 
 * @todo Description
 * 
 */
public class ModelDescription extends SymbolDescription {

	private static final Map<String, Integer> countPerModel = new HashMap();
	private final int number;
	private static int count = 1;
	private final Map<String, ExperimentDescription> experiments = new HashMap();
	private IDescription output;
	private IDescription environment;
	final TypesManager types;
	private String fileName;
	private String baseDirectory;
	private SpeciesDescription worldSpecies;
	private boolean isDisposed = false;
	// Only used to accelerate the parsing
	private final Map<String, SpeciesDescription> allSpeciesDescription = new HashMap();
	private final ErrorCollector collect = new ErrorCollector();

	public ModelDescription(final String fileName, final ISyntacticElement source) {
		super(IKeyword.MODEL, null, new ArrayList(), source, DescriptionFactory.getModelFactory()
			.getMetaDescriptionFor(null, IKeyword.MODEL));
		types = new TypesManager(this);
		setModelFileName(fileName);
		number = count++;
		// inc(fileName);
		// GuiUtils.debug("Creation of " + this);
	}

	@Override
	public String toString() {
		return "description #" + number + " of model <" +
			fileName.substring(fileName.lastIndexOf('/')) + ">";
	}

	@Override
	public void dispose() {
		if ( isDisposed ) {
			// GuiUtils.debug("" + this + " already disposed");
			return;
		}
		// int left = dec(fileName);
		// GuiUtils.debug("Disposal of " + this + "(models left: " + left + ")");
		experiments.clear();
		allSpeciesDescription.clear();
		output = null;
		environment = null;
		types.dispose();
		worldSpecies = null;
		super.dispose();
		isDisposed = true;
	}

	static int inc(final String fileName) {
		Integer count = countPerModel.get(fileName);
		if ( count == null ) {
			countPerModel.put(fileName, 1);
			return 1;
		}
		countPerModel.put(fileName, count + 1);
		return count + 1;
	}

	static int dec(final String fileName) {
		Integer count = countPerModel.get(fileName);
		if ( count == null ) // should not happen but well...
		{ return 0; }
		if ( count == 1 ) {
			countPerModel.remove(fileName);
			return 0;
		}
		countPerModel.put(fileName, count - 1);
		return count - 1;
	}

	public String constructModelRelativePath(final String filePath, final boolean mustExist) {
		try {
			return FileUtils.constructAbsoluteFilePath(filePath, fileName, mustExist);
		} catch (GamaRuntimeException e) {
			flagError(e.getMessage(), IGamlIssue.GENERAL);
			return filePath;
		}
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	@Override
	public void notifyChanged(final Notification notification) {}

	@Override
	public void unsetTarget(final Notifier object) {
		// Normally sent when the EObject is destroyed or no longer accepts the current description
		// as an adapter. In that case, whe should dispose the model description (the underlying
		// model has changed or been garbaged)
		// GuiUtils.debug("Removing: " + this + " from its EObject " + object);
		this.dispose();
	}

	/**
	 * Gets the model file name.
	 * 
	 * @return the model file name
	 */
	public String getModelFileName() {
		return fileName;
	}

	public void setModelFileName(final String name) {
		fileName = name;
		setBaseDirectory(new File(name).getAbsoluteFile().getParent());
	}

	public void setBaseDirectory(final String name) {
		baseDirectory = name;
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void addType(final SpeciesDescription species) {
		allSpeciesDescription.put(species.getName(), species);
		types.addType(species);
	}

	@Override
	public IDescription addChild(final IDescription child) {
		child.setSuperDescription(this);
		String keyword = child.getKeyword();
		if ( child instanceof ExperimentDescription ) {
			experiments.put(child.getName(), (ExperimentDescription) child);
		} else if ( child instanceof SpeciesDescription ) { // world_species
			worldSpecies = (SpeciesDescription) child;
			addType(worldSpecies);
		} else if ( keyword.equals(IKeyword.OUTPUT) ) {
			if ( output == null ) {
				output = child;
			} else {
				output.addChildren(child.getChildren());
				return child;
			}
		} else if ( keyword.equals(IKeyword.ENVIRONMENT) ) {
			if ( environment == null ) {
				environment = child;
			} else {
				environment.addChildren(child.getChildren());
				return child;
			}
		}

		children.add(child);
		return child;
	}

	@Override
	public SpeciesDescription getWorldSpecies() {
		return worldSpecies;
	}

	@Override
	protected boolean hasVar(final String name) {
		return getWorldSpecies().hasVar(name);
	}

	public boolean hasExperiment(final String name) {
		return experiments.containsKey(name);
	}

	@Override
	public IDescription getDescriptionDeclaringVar(final String name) {
		if ( hasVar(name) ) { return getWorldSpecies(); }
		return null;
	}

	@Override
	public IExpression getVarExpr(final String name) {
		return getWorldSpecies().getVarExpr(name);
	}

	@Override
	public ModelDescription getModelDescription() {
		return this;
	}

	@Override
	public SpeciesDescription getSpeciesDescription(final String spec) {
		// if ( spec == null || worldSpecies == null ) { return null; }
		SpeciesDescription result = allSpeciesDescription.get(spec);
		return result; // == null ? findSpecies(worldSpecies, spec) : result;
	}

	public boolean hasSpeciesDescription(final String spec) {
		return spec != null && allSpeciesDescription.containsKey(spec);
	}

	@Override
	public IType getTypeNamed(final String s) {
		return types.get(s);
	}

	public TypesManager getTypesManager() {
		return types;
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return null; // return getWorldSpecies() ?
	}

	public Set<String> getExperimentNames() {
		return new HashSet(experiments.keySet());
	}

	@Override
	public IErrorCollector getErrorCollector() {
		return collect;
	}

}
