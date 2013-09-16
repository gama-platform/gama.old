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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
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
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.util.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.factories.IChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;
import org.eclipse.emf.ecore.EObject;

/**
 * Written by drogoul Modified on 16 mai 2010
 * 
 * @todo Description
 * 
 */
public class ModelDescription extends SpeciesDescription {

	// TODO Move elsewhere
	public static ModelDescription ROOT;
	private final Map<String, ExperimentDescription> experiments = new LinkedHashMap();
	private final Map<String, ExperimentDescription> titledExperiments = new LinkedHashMap();
	private IDescription output;
	final TypesManager types;
	private final String modelFilePath;
	private final String modelFolderPath;
	private final String modelProjectPath;
	private boolean isTorus = false;
	private final ErrorCollector collect;

	public ModelDescription(final String name, final Class clazz, final String projectPath, final String modelPath,
		final EObject source, final SpeciesDescription macro, final SpeciesDescription parent, final Facets facets) {
		this(name, clazz, projectPath, modelPath, source, macro, parent, facets, new ErrorCollector());
	}

	public ModelDescription(final String name, final Class clazz, final String projectPath, final String modelPath,
		final EObject source, final SpeciesDescription macro, final SpeciesDescription parent, final Facets facets,
		final ErrorCollector collector) {
		super(MODEL, clazz, macro, parent, IChildrenProvider.NONE, source, facets);
		types =
			new TypesManager(parent instanceof ModelDescription ? ((ModelDescription) parent).types
				: Types.builtInTypes);
		modelFilePath = modelPath;
		modelFolderPath = new File(modelPath).getParent();
		modelProjectPath = projectPath;
		collect = collector;
		// System.out.println("Model description created with file path " + modelFilePath + "; project path " +
		// modelProjectPath);
	}

	public void setTorus(final boolean b) {
		isTorus = b;
	}

	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public String toString() {
		if ( modelFilePath.isEmpty() ) { return "abstract model"; }
		return "description of " + modelFilePath.substring(modelFilePath.lastIndexOf(File.separator));
	}

	@Override
	public void dispose() {
		if ( /* isDisposed || */isBuiltIn() ) { return; }
		experiments.clear();
		titledExperiments.clear();
		output = null;
		types.dispose();
		// AD 7/9/2013 Added disposal of errors
		collect.clear();
		super.dispose();

		// isDisposed = true;
	}

	public String constructModelRelativePath(final String filePath, final boolean mustExist) {
		try {
			return FileUtils.constructAbsoluteFilePath(filePath, modelFilePath, mustExist);
		} catch (final GamaRuntimeException e) {
			error(e.getMessage(), IGamlIssue.GENERAL);
			return filePath;
		}
	}

	/**
	 * Gets the model file name.
	 * 
	 * @return the model file name
	 */
	public String getModelFilePath() {
		return modelFilePath;
	}

	public String getModelFolderPath() {
		return modelFolderPath;
	}

	public String getModelProjectPath() {
		return modelProjectPath;
	}

	/**
	 * Create types from the species descriptions
	 */
	public void buildTypes() {
		types.init();
	}

	public void addSpeciesType(final TypeDescription species) {
		types.addSpeciesType(species);
	}

	@Override
	public IDescription addChild(final IDescription child) {
		// GuiUtils.debug("Adding " + child + " to " + this + "...");
		if ( child instanceof ExperimentDescription ) {
			String s = child.getName();
			experiments.put(s, (ExperimentDescription) child);
			s = child.getFacets().getLabel(TITLE);
			titledExperiments.put(s, (ExperimentDescription) child);
			addSpeciesType((TypeDescription) child);
			// return child;
			// FIXME: Experiments are not disposed ?
			// If the experiment is not the "default" one, we return the child directly without
			// adding it to the children
			// if ( !DEFAULT_EXP.equals(s) ) { return child; }
			// FIXME Verify this
			children.add(child);
		} else if ( child != null && child.getKeyword().equals(OUTPUT) ) {
			if ( output == null ) {
				output = child;
			} else {
				output.addChildren(child.getChildren());
				return child;
			}
		} else {
			super.addChild(child);
		}

		return child;
	}

	public boolean hasExperiment(final String name) {
		return experiments.containsKey(name) || titledExperiments.containsKey(name);
	}

	@Override
	public ModelDescription getModelDescription() {
		return this;
	}

	@Override
	public SpeciesDescription getSpeciesDescription(final String spec) {
		return (SpeciesDescription) types.getSpecies(spec);
	}

	public boolean hasSpeciesDescription(final String spec) {
		return types.containsSpecies(spec);
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
		return this;
	}

	public Set<String> getExperimentNames() {
		return new LinkedHashSet(experiments.keySet());
	}

	public Set<String> getExperimentTitles() {
		return new LinkedHashSet(titledExperiments.keySet());
	}

	@Override
	public IErrorCollector getErrorCollector() {
		return collect;
	}

	public ExperimentDescription getExperiment(final String name) {
		ExperimentDescription desc = experiments.get(name);
		if ( desc == null ) {
			desc = titledExperiments.get(name);
		}
		return desc;
	}

	@Override
	public void finalizeDescription() {
		super.finalizeDescription();
		for ( final StatementDescription action : actions.values() ) {
			if ( action.isAbstract() &&
				!action.getUnderlyingElement(null).eResource().equals(getUnderlyingElement(null).eResource()) ) {
				this.error("Abstract action '" + action.getName() + "', defined in " + action.getOriginName() +
					", should be redefined.", IGamlIssue.MISSING_ACTION);
			}
		}
	}

}
