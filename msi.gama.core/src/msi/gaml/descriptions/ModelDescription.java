/*********************************************************************************************
 *
 *
 * 'ModelDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.io.File;
import java.util.*;
import org.eclipse.emf.ecore.EObject;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.descriptions.SymbolSerializer.ModelSerializer;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;
import msi.gaml.types.*;

/**
 * Written by drogoul Modified on 16 mai 2010
 *
 * @todo Description
 *
 */
public class ModelDescription extends SpeciesDescription {

	// TODO Move elsewhere
	public static final String MODEL_SUFFIX = "_model";
	public static volatile ModelDescription ROOT;
	private final Map<String, ExperimentDescription> experiments = new TOrderedHashMap();
	private final Map<String, ExperimentDescription> titledExperiments = new TOrderedHashMap();
	private IDescription output;
	final TypesManager types;
	private String modelFilePath;
	private String modelFolderPath;
	private final String modelProjectPath;
	private final List<String> imports;
	private boolean isTorus = false;
	private final ErrorCollector collect;
	protected boolean document;
	// hqnghi new attribute manipulate micro-models
	private Map<String, ModelDescription> MICRO_MODELS = new TOrderedHashMap<String, ModelDescription>();
	private String alias = "";
	boolean isStartingDateDefined = false;

	public void setMicroModels(final Map<String, ModelDescription> mm) {
		MICRO_MODELS = mm;
	}

	public List<String> getImports() {
		return imports;
	}

	public Map<String, ModelDescription> getMicroModels() {
		return MICRO_MODELS;
	}

	public ModelDescription getMicroModel(final String name) {
		if ( MICRO_MODELS.size() > 0 ) { return MICRO_MODELS.get(name); }
		return null;
	}

	public void addMicroModel(final String mName, final ModelDescription md) {
		MICRO_MODELS.put(mName, md);
	}

	public void setAlias(final String as) {
		alias = as;
	}

	public String getAlias() {
		return alias;
	}

	// end-hqnghi

	public ModelDescription(final String name, final Class clazz, final SpeciesDescription macro,
		final SpeciesDescription parent, final Facets facets) {
		this(name, clazz, "", "", null, macro, parent, facets, ErrorCollector.BuiltIn, Collections.EMPTY_LIST);
	}

	public ModelDescription(final String name, final Class clazz, final String projectPath, final String modelPath,
		final EObject source, final SpeciesDescription macro, final SpeciesDescription parent, final Facets facets,
		final ErrorCollector collector, final List<String> imports) {
		super(MODEL, clazz, macro, parent, ChildrenProvider.NONE, source, facets, null);
		types = new TypesManager(
			parent instanceof ModelDescription ? ((ModelDescription) parent).types : Types.builtInTypes);
		modelFilePath = modelPath;
		modelFolderPath = new File(modelPath).getParent();
		modelProjectPath = projectPath;
		collect = collector;
		this.imports = imports;
		// System.out.println("Model description created with file path " + modelFilePath + "; project path " +
		// modelProjectPath);
	}

	@Override
	public SymbolSerializer createSerializer() {
		return new ModelSerializer();
	}

	public void setTorus(final boolean b) {
		isTorus = b;
	}

	@Override
	public String getTitle() {
		return getName().replace(MODEL_SUFFIX, "");
	}

	@Override
	public boolean isDocumenting() {
		return document;
	}

	public void isDocumenting(final boolean b) {
		document = b;
	}

	@Override
	protected boolean canBeDefinedIn(final IDescription sd) {
		// By convention, a model can be defined everywhere
		return true;
	}

	// hqnghi does it need to verify parent of micro-model??
	@Override
	protected void verifyParent() {
		if ( parent == ModelDescription.ROOT ) { return; }
		super.verifyParent();
	}

	// end-hqnghi

	@Override
	public void markVariableRedefinition(final VariableDescription existingVar, final VariableDescription newVar) {
		if ( newVar.isBuiltIn() ) { return; }
		if ( existingVar.isBuiltIn() ) {
			newVar.info(
				"This definition of " + newVar.getName() + " supersedes the one in " + existingVar.getOriginName(),
				IGamlIssue.REDEFINES, NAME);
			return;
		}

		EObject newResource = newVar.getUnderlyingElement(null).eContainer();
		EObject existingResource = existingVar.getUnderlyingElement(null).eContainer();
		if ( newResource.equals(existingResource) ) {
			existingVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION,
				NAME);
			newVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION, NAME);
			return;
		}
		newVar.info("This definition of " + newVar.getName() + " supersedes the one in imported file " +
			existingResource.eResource().getURI().lastSegment(), IGamlIssue.REDEFINES, NAME);
	}

	/**
	 * Relocates the working path. The last segment must not end with a "/"
	 * @param path
	 */
	public void setWorkingDirectory(final String path) {
		modelFilePath = path + File.separator + new File(modelFilePath).getName();
		modelFolderPath = new File(modelFilePath).getParent();
	}

	public boolean isTorus() {
		return isTorus;
	}

	@Override
	public String toString() {
		if ( modelFilePath == null || modelFilePath.isEmpty() ) { return "abstract model"; }
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
		// collect = null;
		super.dispose();

		// isDisposed = true;
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

	public void setModelFilePath(final String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

	public void setModelFolderPath(final String modelFolderPath) {
		this.modelFolderPath = modelFolderPath;
	}

	/**
	 * Create types from the species descriptions
	 */
	public void buildTypes() {
		for ( SpeciesDescription sd : this.getAllMicroSpecies() ) {
			types.addSpeciesType(sd);
		}
		for ( IDescription ed : this.getExperiments() ) {
			types.addSpeciesType((ExperimentDescription) ed);
		}
		types.init();
	}

	public void addSpeciesType(final TypeDescription species) {
		types.addSpeciesType(species);
	}

	@Override
	public IDescription addChild(final IDescription child) {

		if ( !child.isBuiltIn() && child.getName().equals(SimulationAgent.STARTING_DATE) ) {
			isStartingDateDefined = true;
		}
		if ( child instanceof ExperimentDescription ) {
			String s = child.getName();
			experiments.put(s, (ExperimentDescription) child);
			s = child.getFacets().getLabel(TITLE);
			titledExperiments.put(s, (ExperimentDescription) child);
			// scope.getGui().debug("Adding experiment" + s + " defined in " + child.getOriginName() + " to " + getName() +
			// "...");
			// addSpeciesType((TypeDescription) child);
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

	public boolean isStartingDateDefined() {
		return isStartingDateDefined;
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
		if ( types == null ) { return null; }
		return (SpeciesDescription) types.getSpecies(spec);
	}

	@Override
	public IType getTypeNamed(final String s) {
		if ( types == null ) { return Types.NO_TYPE; }
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
		return new TLinkedHashSet(experiments.keySet());
	}

	public Set<String> getExperimentTitles() {
		Set<String> strings = new TLinkedHashSet();
		for ( String s : titledExperiments.keySet() ) {
			ExperimentDescription ed = titledExperiments.get(s);
			if ( ed.getOriginName().equals(getName()) ) {
				strings.add(s);
			}
		}
		return strings;
	}

	@Override
	public ErrorCollector getErrorCollector() {
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
	public List<IDescription> getChildren() {
		List<IDescription> result = super.getChildren();
		result.addAll(experiments.values());
		return result;
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

	@Override
	public IDescription validate() {
		return validate(false);
	}

	public IDescription validate(final boolean document) {
		isDocumenting(document);
		super.validate();
		// System.out.println(toGaml());
		return this;
	}

	/**
	 * @return
	 */
	public Collection<? extends IDescription> getExperiments() {
		return experiments.values();
	}

}
