/*******************************************************************************************************
 *
 * ExperimentDescription.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import static msi.gama.common.interfaces.IGamlIssue.DUPLICATE_DEFINITION;
import static msi.gama.common.interfaces.IGamlIssue.REDEFINES;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.google.common.collect.Iterables;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.statements.Facets;

/**
 * The Class ExperimentDescription.
 */
@SuppressWarnings ({ "unchecked", "rawtypes" })

public class ExperimentDescription extends SpeciesDescription {

	/** The parameters. */
	private IMap<String, VariableDescription> parameters;

	/** The output. */
	private StatementDescription output;

	/** The permanent. */
	private StatementDescription permanent;

	/**
	 * Instantiates a new experiment description.
	 *
	 * @param keyword
	 *            the keyword
	 * @param enclosing
	 *            the enclosing
	 * @param cp
	 *            the cp
	 * @param source
	 *            the source
	 * @param facets
	 *            the facets
	 */
	public ExperimentDescription(final String keyword, final SpeciesDescription enclosing,
			final Iterable<IDescription> cp, final EObject source, final Facets facets) {
		super(keyword, null, enclosing, null, cp, source, facets);
		setIf(Flag.isBatch, IKeyword.BATCH.equals(getLitteral(IKeyword.TYPE)));
		setIf(Flag.isMemorize, IKeyword.MEMORIZE.equals(getLitteral(IKeyword.TYPE)));
	}

	/**
	 * Instantiates a new experiment description.
	 *
	 * @param name
	 *            the name
	 * @param clazz
	 *            the clazz
	 * @param superDesc
	 *            the super desc
	 * @param parent
	 *            the parent
	 * @param helper
	 *            the helper
	 * @param skills2
	 *            the skills 2
	 * @param ff
	 *            the ff
	 * @param plugin
	 *            the plugin
	 */
	public ExperimentDescription(final String name, final Class<?> clazz, final SpeciesDescription superDesc,
			final SpeciesDescription parent, final IAgentConstructor<? extends IAgent> helper,
			final Set<String> skills2, final Facets ff, final String plugin) {
		super(name, clazz, superDesc, parent, helper, skills2, ff, plugin);
	}

	/**
	 * Adds the parameter.
	 *
	 * @param var
	 *            the var
	 */
	private void addParameter(final VariableDescription var) {
		if (parameters == null) { parameters = GamaMapFactory.create(); }
		String vName = var.getName();
		VariableDescription existing = parameters.get(vName);
		if (existing != null) {
			existing.warning("'" + vName + "' is overwritten in this experiment and will not be used.",
					DUPLICATE_DEFINITION, NAME);
			var.warning("'" + vName + "' overwrites a previous definition.", DUPLICATE_DEFINITION, NAME);
		}
		ModelDescription md = this.getModelDescription();
		md.visitAllAttributes(d -> {
			VariableDescription vd = (VariableDescription) d;
			if (vName.equals(vd.getParameterName())) {
				// Possibily different resources
				final Resource newResource =
						var.getUnderlyingElement() == null ? null : var.getUnderlyingElement().eResource();
				final Resource existingResource = vd.getUnderlyingElement().eResource();
				if (Objects.equals(newResource, existingResource)) {
					var.info("'" + vName + "' supersedes the parameter declaration in " + vd.getOriginName(), REDEFINES,
							NAME);
					vd.info("Parameter '" + vName + "' is redefined in experiment "
							+ var.getEnclosingDescription().getName(), DUPLICATE_DEFINITION, NAME);
				} else {
					var.info("This definition of '" + vName + "' supersedes the one in imported file "
							+ existingResource.getURI().lastSegment(), REDEFINES, NAME);
				}
			}
			return true;
		}

		);
		parameters.put(var.getName(), var);
	}

	/**
	 * Checks for parameter.
	 *
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean hasParameter(final String name) {
		if (parameters == null) return false;
		return parameters.containsKey(name);
	}

	/**
	 * Gets the parameter.
	 *
	 * @param name
	 *            the name
	 * @return the parameter
	 */
	public VariableDescription getParameter(final String name) {
		if (parameters == null) return null;
		return parameters.get(name);
	}

	/**
	 * Inherit parameters from.
	 *
	 * @param p
	 *            the p
	 */
	public void inheritParametersFrom(final ExperimentDescription p) {
		if (p.parameters != null) {
			for (final VariableDescription v : p.parameters.values()) { addInheritedParameter(v); }
		}
	}

	/**
	 * Adds the inherited parameter.
	 *
	 * @param vd
	 *            the vd
	 */
	public void addInheritedParameter(final VariableDescription vd) {

		final String inheritedVarName = vd.getName();

		// If no previous definition is found, just add the parameter
		if (!hasParameter(inheritedVarName)) {
			addParameter(vd.copy(this));
			return;
		}
		// A redefinition has been found
		final VariableDescription existing = getParameter(inheritedVarName);
		if (assertAttributesAreCompatible(vd, existing)) {
			if (!existing.isBuiltIn()) { markAttributeRedefinition(vd, existing); }
			existing.copyFrom(vd);
		}
	}

	@Override
	public void addInheritedAttribute(final VariableDescription var) {
		if (PARAMETER.equals(var.getKeyword())) {
			addParameter(var);
		} else {
			super.addInheritedAttribute(var);
		}
	}

	@Override
	public void addOwnAttribute(final VariableDescription var) {
		if (!PARAMETER.equals(var.getKeyword())) {
			super.addOwnAttribute(var);
		} else {
			addParameter(var);
		}
	}

	@Override
	public String getTitle() { return "experiment " + getName(); }

	/**
	 * Gets the experiment title facet.
	 *
	 * @return the experiment title facet
	 */
	public String getExperimentTitleFacet() { return getLitteral(TITLE); }

	@Override
	public boolean isExperiment() { return true; }

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor<IDescription> visitor) {
		if (!super.visitOwnChildren(visitor) || parameters != null && !parameters.forEachValue(visitor)) return false;
		if (output != null && !visitor.process(output) || permanent != null && !visitor.process(permanent))
			return false;
		return true;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor<IDescription> visitor) {
		final DescriptionVisitor<IDescription> recursiveVisitor = each -> {
			if (!visitor.process(each)) return false;
			return each.visitOwnChildrenRecursively(visitor);
		};
		if (!super.visitOwnChildrenRecursively(visitor)
				|| parameters != null && !parameters.forEachValue(recursiveVisitor))
			return false;
		if (output != null && !recursiveVisitor.process(output)
				|| permanent != null && !recursiveVisitor.process(permanent))
			return false;
		return true;
	}

	@Override
	public Iterable<IDescription> getOwnChildren() {
		return Iterables.concat(super.getOwnChildren(),
				parameters == null ? Collections.EMPTY_LIST : parameters.values(),
				output == null ? Collections.EMPTY_LIST : Collections.singleton(output),
				permanent == null ? Collections.EMPTY_LIST : Collections.singleton(permanent));
	}

	@Override
	public boolean visitChildren(final DescriptionVisitor<IDescription> visitor) {
		boolean result = super.visitChildren(visitor);
		if (!result) return false;
		if (parameters != null) { result &= parameters.forEachValue(visitor); }
		if (!result) return false;
		if (output != null) { result &= visitor.process(output); }
		if (!result) return false;
		if (permanent != null) { result &= visitor.process(permanent); }
		return result;
	}

	/**
	 * @return
	 */
	public Boolean isBatch() { return isSet(Flag.isBatch); }

	/**
	 * @return
	 */
	public Boolean isMemorize() { return isSet(Flag.isMemorize); }

	/**
	 * Gets the experiment type.
	 *
	 * @return the experiment type
	 */
	public String getExperimentType() {
		if (isBatch()) return IKeyword.BATCH;
		if (isMemorize()) return IKeyword.MEMORIZE;
		return IKeyword.GUI_;
	}

	@Override
	public Class<? extends ExperimentAgent> getJavaBase() {
		return isBatch() ? BatchAgent.class : ExperimentAgent.class;
	}

	@Override
	public void inheritFromParent() {
		// Takes care of invalid species (see Issue 711)
		if (parent != null && parent != this && !parent.isBuiltIn()) {
			super.inheritFromParent();
			inheritParametersFrom((ExperimentDescription) parent);
			inheritOutputsFrom((ExperimentDescription) parent);
		}
	}

	/**
	 * Inherit outputs from.
	 *
	 * @param parent
	 *            the parent
	 */
	private void inheritOutputsFrom(final ExperimentDescription parent) {
		if (parent.output != null) {
			if (output == null) {
				output = parent.output.copy(this);
			} else {
				mergeOutputs(parent.output, output);
			}
		}
		if (parent.permanent != null) {
			if (permanent == null) {
				permanent = parent.permanent.copy(this);
			} else {
				mergeOutputs(parent.permanent, permanent);
			}
		}
	}

	/**
	 * Merge outputs.
	 *
	 * @param inherited
	 *            the inherited
	 * @param defined
	 *            the defined
	 */
	private void mergeOutputs(final StatementDescription inherited, final StatementDescription defined) {
		inherited.visitChildren(in -> {
			final IDescription redefined = getSimilarChild(defined, in);
			if (redefined == null) {
				defined.addChild(in.copy(defined));
			} else {
				redefined.info("This definition of " + redefined.getName() + " supersedes the one in "
						+ in.getSpeciesContext().getName(), IGamlIssue.REDEFINES, NAME);
			}
			return true;
		});

	}

	@Override
	protected void addBehavior(final StatementDescription r) {
		if (OUTPUT.equals(r.getKeyword())) {
			output = r;
		} else if (PERMANENT.equals(r.getKeyword())) {
			permanent = r;
		} else {
			super.addBehavior(r);
		}
	}

	@Override
	protected boolean parentIsVisible() {
		if (!getParent().isExperiment()) return false;
		if (parent.isBuiltIn()) return true;
		final ModelDescription host = (ModelDescription) getMacroSpecies();
		if (host != null && host.getExperiment(parent.getName()) != null) return true;
		return false;
	}

	@Override
	public boolean visitMicroSpecies(final DescriptionVisitor<SpeciesDescription> visitor) {
		return true;
	}

	// @Override
	// protected boolean validateChildren() {
	// // We verify that parameters have different titles and that the model does not declare attributes with duplicate
	// // parameter facets
	// Map<VariableDescription, String> reverse = GamaMapFactory.create();
	// ListMultimap<String, VariableDescription> mm = MultimapBuilder.linkedHashKeys().arrayListValues().build();
	// if (parameters != null) {
	// parameters.forEach((s, v) -> mm.put(v.getTitle(), v));
	// }
	// return super.validateChildren();
	// }

}
