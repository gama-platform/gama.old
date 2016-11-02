/*********************************************************************************************
 *
 * 'ModelDescription.java, in plugin msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.Iterables;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.TLinkedHashSet;
import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gaml.statements.Facets;
import msi.gaml.types.IType;
import msi.gaml.types.ITypesManager;
import msi.gaml.types.Types;
import msi.gaml.types.TypesManager;

/**
 * Written by drogoul Modified on 16 mai 2010
 *
 * @todo Description
 *
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class ModelDescription extends SpeciesDescription {

	// TODO Move elsewhere
	public static final String MODEL_SUFFIX = "_model";
	public static volatile ModelDescription ROOT;
	private THashMap<String, ExperimentDescription> experiments;
	final TypesManager types;
	private String modelFilePath;
	private final String modelProjectPath;
	private final Set<String> alternatePaths;
	private final ValidationContext validationContext;
	protected volatile boolean document;
	// hqnghi new attribute manipulate micro-models
	private THashMap<String, ModelDescription> microModels;
	private String alias = "";
	boolean isStartingDateDefined = false;
	private Collection<String> importedModelNames;

	public Collection<String> getAlternatePaths() {
		return alternatePaths == null ? Collections.EMPTY_LIST : alternatePaths;
	}

	public ModelDescription getMicroModel(final String name) {
		if (microModels == null)
			return null;
		return microModels.get(name);
	}

	public void setAlias(final String as) {
		alias = as;
	}

	public String getAlias() {
		return alias;
	}

	public boolean isMicroModel() {
		return alias != null && !alias.isEmpty();
	}

	@Override
	public boolean isModel() {
		return true;
	}

	// end-hqnghi

	public ModelDescription(final String name, final Class clazz, final String projectPath, final String modelPath,
			final EObject source, final SpeciesDescription macro, final SpeciesDescription parent,
			final Iterable<? extends IDescription> children, final Facets facets,
			final ValidationContext validationContext, final Set<String> imports) {
		super(MODEL, clazz, macro, parent, children, source, facets);
		setName(name);
		types = parent instanceof ModelDescription ? new TypesManager(((ModelDescription) parent).types)
				: Types.builtInTypes;
		modelFilePath = modelPath;
		modelProjectPath = projectPath;
		this.validationContext = validationContext;
		this.alternatePaths = imports;
	}

	@Override
	public SymbolSerializer createSerializer() {
		return MODEL_SERIALIZER;
	}

	@Override
	public String getTitle() {
		return getName().replace(MODEL_SUFFIX, "");
	}

	@Override
	protected boolean isDocumenting() {
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
	protected boolean verifyParent() {
		if (parent == ModelDescription.ROOT) { return true; }
		return super.verifyParent();
	}

	// end-hqnghi

	@Override
	public void markAttributeRedefinition(final VariableDescription existingVar, final VariableDescription newVar) {
		if (newVar.isBuiltIn()) { return; }
		if (existingVar.isBuiltIn()) {
			newVar.info(
					"This definition of " + newVar.getName() + " supersedes the one in " + existingVar.getOriginName(),
					IGamlIssue.REDEFINES, NAME);
			return;
		}

		final EObject newResource = newVar.getUnderlyingElement(null).eContainer();
		final EObject existingResource = existingVar.getUnderlyingElement(null).eContainer();
		if (Objects.equals(newResource, existingResource)) {
			existingVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION,
					NAME);
			newVar.error("Attribute " + newVar.getName() + " is defined twice", IGamlIssue.DUPLICATE_DEFINITION, NAME);
			return;
		}
		if (existingResource != null)
			newVar.info("This definition of " + newVar.getName() + " supersedes the one in imported file "
					+ existingResource.eResource().getURI().lastSegment(), IGamlIssue.REDEFINES, NAME);
	}

	@Override
	public String getDocumentationWithoutMeta() {
		final StringBuilder sb = new StringBuilder(200);
		final String parentName = getParent() == null ? "nil" : getParent().getName();
		if (!parentName.equals(IKeyword.MODEL))
			sb.append("<b>Subspecies of:</b> ").append(parentName).append("<br>");
		final Iterable<String> skills = getSkillsNames();
		if (!Iterables.isEmpty(skills))
			sb.append("<b>Skills:</b> ").append(skills).append("<br>");
		sb.append("<br>")
				.append("The following attributes and actions will be accessible using 'world' (in the model) and 'simulation' (in an experiment)")
				.append("<br>");
		sb.append(getAttributeDocumentation());
		sb.append("<br/>");
		sb.append(getActionDocumentation());
		sb.append("<br/>");
		return sb.toString();
	}

	/**
	 * Relocates the working path. The last segment must not end with a "/"
	 * 
	 * @param path
	 */
	public void setWorkingDirectory(final String path) {
		modelFilePath = path + File.separator + new File(modelFilePath).getName();
	}

	@Override
	public String toString() {
		if (modelFilePath == null || modelFilePath.isEmpty()) { return "abstract model"; }
		return "description of " + modelFilePath.substring(modelFilePath.lastIndexOf(File.separator));
	}

	@Override
	public void dispose() {
		if (isBuiltIn()) { return; }
		super.dispose();
		experiments = null;
		types.dispose();

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
		return new File(modelFilePath).getParent();
	}

	public String getModelProjectPath() {
		return modelProjectPath;
	}

	public void setModelFilePath(final String modelFilePath) {
		this.modelFilePath = modelFilePath;
	}

	/**
	 * Create types from the species descriptions
	 */
	public void buildTypes() {
		types.init(this);
	}

	// public void addSpeciesType(final SpeciesDescription species) {
	// types.addSpeciesType(species);
	// }

	@Override
	public SpeciesDescription getMacroSpecies() {

		SpeciesDescription d = super.getMacroSpecies();
		if (d == null) {
			d = Types.get(EXPERIMENT).getSpecies();
		}
		return d;

	}

	@Override
	public IDescription addChild(final IDescription child) {
		if (child == null)
			return null;

		if (child instanceof ModelDescription) {
			((ModelDescription) child).getTypesManager().setParent(getTypesManager());
			if (microModels == null)
				microModels = new THashMap();
			microModels.put(((ModelDescription) child).getAlias(), (ModelDescription) child);
		} // no else as models are also species, which should be added after.

		if (child instanceof ExperimentDescription) {
			final String s = child.getName();
			if (experiments == null) {
				experiments = new THashMap();
			}
			experiments.put(s, (ExperimentDescription) child);
		} else {
			super.addChild(child);
		}

		return child;
	}

	@Override
	public void addOwnAttribute(final VariableDescription vd) {
		if (!vd.isBuiltIn() && vd.getName().equals(SimulationAgent.STARTING_DATE)) {
			isStartingDateDefined = true;
		}
		super.addOwnAttribute(vd);
	}

	public boolean isStartingDateDefined() {
		return isStartingDateDefined;
	}

	public boolean hasExperiment(final String nameOrTitle) {
		if (experiments == null)
			return false;
		if (experiments.containsKey(nameOrTitle))
			return true;
		for (final ExperimentDescription exp : experiments.values()) {
			if (exp.getExperimentTitleFacet().equals(nameOrTitle))
				return true;
		}
		return false;
	}

	@Override
	public ModelDescription getModelDescription() {
		return this;
	}

	@Override
	public SpeciesDescription getSpeciesDescription(final String spec) {
		if (spec.equals(getName()))
			return this;
		if (importedModelNames != null && importedModelNames.contains(spec)) { return this; }
		if (getTypesManager() == null) {
			if (hasMicroSpecies())
				return getMicroSpecies().get(spec);
			else
				return null;
		} else
			return getTypesManager().get(spec).getSpecies();
	}

	@Override
	public IType getTypeNamed(final String s) {
		if (types == null) { return Types.NO_TYPE; }
		return types.get(s);
	}

	public ITypesManager getTypesManager() {
		return types;
	}

	@Override
	public SpeciesDescription getSpeciesContext() {
		return this;
	}

	public Set<String> getExperimentNames() {
		if (experiments == null)
			return Collections.EMPTY_SET;
		return new TLinkedHashSet(experiments.keySet());
	}

	public Set<String> getExperimentTitles() {
		final Set<String> strings = new TLinkedHashSet();
		if (experiments != null) {
			experiments.forEachEntry((a, b) -> {
				if (b.getOriginName().equals(getName()))
					strings.add(b.getExperimentTitleFacet());
				return true;
			});
		}
		return strings;
	}

	@Override
	public ValidationContext getValidationContext() {
		return validationContext;
	}

	public ExperimentDescription getExperiment(final String name) {
		if (experiments == null)
			return null;
		final ExperimentDescription desc = experiments.get(name);
		if (desc == null) {
			for (final ExperimentDescription ed : experiments.values()) {
				if (ed.getExperimentTitleFacet().equals(name))
					return ed;
			}
		}
		return desc;
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor visitor) {
		boolean result = super.visitChildren(visitor);
		if (result && experiments != null)
			result &= experiments.forEachValue(visitor);
		return result;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor visitor) {
		if (!super.visitOwnChildren(visitor))
			return false;
		if (experiments != null)
			if (!experiments.forEachValue(visitor))
				return false;
		return true;
	}

	@Override
	public boolean finalizeDescription() {
		final VariableDescription vd = getAttribute(SHAPE);

		// if (!isBuiltIn() && !vd.hasFacet(INIT)) {
		// final Facets f = new Facets(NAME, SHAPE);
		// f.put(INIT,
		// GAML.getExpressionFactory().createOperator("envelope", this, null,
		// new ConstantExpression(100)));
		// final ISyntacticElement shape =
		// SyntacticFactory.create(IKeyword.GEOMETRY, f, false);
		// vd = (VariableDescription) DescriptionFactory.create(shape, this,
		// null);
		// addChild(vd);
		// }
		if (!super.finalizeDescription())
			return false;
		if (actions != null)
			for (final ActionDescription action : actions.values()) {
				if (action.isAbstract() && !action.getUnderlyingElement(null).eResource()
						.equals(getUnderlyingElement(null).eResource())) {
					this.error("Abstract action '" + action.getName() + "', defined in " + action.getOriginName()
							+ ", should be redefined.", IGamlIssue.MISSING_ACTION);
					return false;
				}
			}
		return true;
	}

	@Override
	public IDescription validate() {
		if (validated)
			return this;
		return validate(false);
	}

	public IDescription validate(final boolean document) {
		isDocumenting(document);
		super.validate();
		return this;
	}

	/**
	 * @return
	 */
	public Collection<? extends ExperimentDescription> getExperiments() {
		if (experiments == null)
			return Collections.EMPTY_LIST;
		return experiments.values();
	}

	public void setImportedModelNames(final Collection<String> allModelNames) {
		importedModelNames = allModelNames;
	}

	/**
	 * Returns all the species including the model itself, all the micro-species and the experiments
	 * 
	 * @return
	 */

	public void visitAllSpecies(final DescriptionVisitor<SpeciesDescription> visitor) {
		visitor.visit(this);
		if (!visitMicroSpecies(new DescriptionVisitor<SpeciesDescription>() {

			@Override
			public boolean visit(final SpeciesDescription desc) {
				visitor.visit(desc);
				return desc.visitMicroSpecies(this);
			}
		}))
			return;
		if (experiments != null) {
			experiments.forEachValue(visitor);
		}
		// if (microModels != null)
		// for (final ModelDescription md : microModels.values()) {
		// visitor.visit(md);
		// }
	}

	public void getAllSpecies(final List<SpeciesDescription> accumulator) {
		final DescriptionVisitor visitor = new DescriptionVisitor<SpeciesDescription>() {

			@Override
			public boolean visit(final SpeciesDescription desc) {
				accumulator.add(desc);
				return true;
			}
		};
		visitAllSpecies(visitor);
	}

}
