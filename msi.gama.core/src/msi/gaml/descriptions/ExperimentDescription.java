/*******************************************************************************************************
 *
 * msi.gaml.descriptions.ExperimentDescription.java, in plugin msi.gama.core, is part of the source code of the GAMA
 * modeling and simulation platform (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/SU & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.descriptions;

import java.util.Collections;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;

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

@SuppressWarnings ({ "unchecked", "rawtypes" })

public class ExperimentDescription extends SpeciesDescription {

	private IMap<String, VariableDescription> parameters;
	private StatementDescription output;
	private StatementDescription permanent;

	public ExperimentDescription(final String keyword, final SpeciesDescription enclosing,
			final Iterable<IDescription> cp, final EObject source, final Facets facets) {
		super(keyword, null, enclosing, null, cp, source, facets);
	}

	public ExperimentDescription(final String name, final Class<?> clazz, final SpeciesDescription superDesc,
			final SpeciesDescription parent, final IAgentConstructor<? extends IAgent> helper,
			final Set<String> skills2, final Facets ff, final String plugin) {
		super(name, clazz, superDesc, parent, helper, skills2, ff, plugin);
	}

	private void addParameterNoCheck(final VariableDescription var) {
		if (parameters == null) {
			parameters = GamaMapFactory.create();
		}

		parameters.put(var.getName(), var);
	}

	public boolean hasParameter(final String name) {
		if (parameters == null) { return false; }
		return parameters.containsKey(name);
	}

	public VariableDescription getParameter(final String name) {
		if (parameters == null) { return null; }
		return parameters.get(name);
	}

	public void inheritParametersFrom(final ExperimentDescription p) {
		if (p.parameters != null) {
			for (final VariableDescription v : p.parameters.values()) {
				addInheritedParameter(v);
			}
		}
	}

	public void addInheritedParameter(final VariableDescription vd) {

		final String inheritedVarName = vd.getName();

		// If no previous definition is found, just add the parameter
		if (!hasParameter(inheritedVarName)) {
			addParameterNoCheck(vd.copy(this));
			return;
		}
		// A redefinition has been found
		final VariableDescription existing = getParameter(inheritedVarName);
		if (assertAttributesAreCompatible(vd, existing)) {
			if (!existing.isBuiltIn()) {
				markAttributeRedefinition(vd, existing);
			}
			existing.copyFrom(vd);
		}
	}

	@Override
	public void addInheritedAttribute(final VariableDescription var) {
		if (var.getKeyword().equals(PARAMETER)) {
			if (!hasParameter(var.getName())) {
				addParameterNoCheck(var);
			}
		} else {
			super.addInheritedAttribute(var);
		}
	}

	@Override
	public void addOwnAttribute(final VariableDescription var) {
		if (!var.getKeyword().equals(PARAMETER)) {
			super.addOwnAttribute(var);
		} else {
			addParameterNoCheck(var);
		}
	}

	@Override
	public String getTitle() {
		return "experiment " + getName();
	}

	public String getExperimentTitleFacet() {
		return getLitteral(TITLE);
	}

	@Override
	public boolean isExperiment() {
		return true;
	}

	@Override
	public boolean visitOwnChildren(final DescriptionVisitor<IDescription> visitor) {
		if (!super.visitOwnChildren(visitor)) { return false; }
		if (parameters != null) {
			if (!parameters.forEachValue(visitor)) { return false; }
		}
		if (output != null) {
			if (!visitor.process(output)) { return false; }
		}
		if (permanent != null) {
			if (!visitor.process(permanent)) { return false; }
		}
		return true;
	}

	@Override
	public boolean visitOwnChildrenRecursively(final DescriptionVisitor<IDescription> visitor) {
		final DescriptionVisitor<IDescription> recursiveVisitor = each -> {
			if (!visitor.process(each)) { return false; }
			return each.visitOwnChildrenRecursively(visitor);
		};
		if (!super.visitOwnChildrenRecursively(visitor)) { return false; }
		if (parameters != null && !parameters.forEachValue(recursiveVisitor)) { return false; }
		if (output != null && !recursiveVisitor.process(output)) { return false; }
		if (permanent != null && !recursiveVisitor.process(permanent)) { return false; }
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
		if (!result) { return false; }
		if (parameters != null) {
			result &= parameters.forEachValue(visitor);
		}
		if (!result) { return false; }
		if (output != null) {
			result &= visitor.process(output);
		}
		if (!result) { return false; }
		if (permanent != null) {
			result &= visitor.process(permanent);
		}
		return result;
	}

	/**
	 * @return
	 */
	public Boolean isBatch() {
		return IKeyword.BATCH.equals(getLitteral(IKeyword.TYPE));
	}

	/**
	 * @return
	 */
	public Boolean isMemorize() {
		return IKeyword.MEMORIZE.equals(getLitteral(IKeyword.TYPE));
	}

	public String getExperimentType() {
		if (isBatch()) {
			return IKeyword.BATCH;
		} else if (isMemorize()) {
			return IKeyword.MEMORIZE;
		} else {
			return IKeyword.GUI_;
		}
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
		if (r.getKeyword().equals(OUTPUT)) {
			output = r;
		} else if (r.getKeyword().equals(PERMANENT)) {
			permanent = r;
		} else {
			super.addBehavior(r);
		}
	}

	@Override
	protected boolean parentIsVisible() {
		if (!getParent().isExperiment()) { return false; }
		if (parent.isBuiltIn()) { return true; }
		final ModelDescription host = (ModelDescription) getMacroSpecies();
		if (host != null) {
			if (host.getExperiment(parent.getName()) != null) { return true; }
		}
		return false;
	}

	@Override
	public boolean visitMicroSpecies(final DescriptionVisitor<SpeciesDescription> visitor) {
		return true;
	}

}
