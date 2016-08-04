/*********************************************************************************************
 *
 *
 * 'ExperimentDescription.java', in plugin 'msi.gama.core', is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 *
 *
 **********************************************************************************************/
package msi.gaml.descriptions;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import msi.gama.common.interfaces.IGamlIssue;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.BatchAgent;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.util.TOrderedHashMap;
import msi.gaml.factories.ChildrenProvider;
import msi.gaml.statements.Facets;

public class ExperimentDescription extends SpeciesDescription {

	private Map<String, VariableDescription> parameters;
	private StatementDescription output;
	private StatementDescription permanent;

	// final ModelDescription model;

	// We assume experiments are firstly created *within* a model, in which case
	// we can gather the enclosing argument
	// and keep it for when the relationship will be reversed (i.e. when the
	// model will be *inside* the experiment)
	public ExperimentDescription(final String keyword, final IDescription enclosing, final ChildrenProvider cp,
			final EObject source, final Facets facets) {
		super(keyword, null, enclosing, null, cp, source, facets, null);
	}

	private void addParameterNoCheck(final VariableDescription var) {
		if (parameters == null) {
			parameters = new TOrderedHashMap();
		}

		parameters.put(var.getName(), var);
	}

	public boolean hasParameter(final String name) {
		if (parameters == null)
			return false;
		return parameters.containsKey(name);
	}

	public VariableDescription getParameter(final String name) {
		if (parameters == null)
			return null;
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
		// We dont inherit from previously added variables, as a child and its
		// parent should
		// share the same javaBase

		final String inheritedVarName = vd.getName();

		// If no previous definition is found, just add the variable
		if (!hasParameter(inheritedVarName)) {
			addParameterNoCheck(vd.copy(this));
			return;
		}
		// A redefinition has been found
		final VariableDescription existing = getParameter(inheritedVarName);
		if (assertVarsAreCompatible(vd, existing)) {
			if (!existing.isBuiltIn()) {
				markVariableRedefinition(vd, existing);
			}
			existing.copyFrom(vd);
		}
	}

	@Override
	public void addInheritedVariable(final VariableDescription var) {
		if (var.getKeyword().equals(PARAMETER)) {
			if (parameters == null || !parameters.containsKey(var.getName())) {
				addParameterNoCheck(var);
			}
		} else {
			super.addInheritedVariable(var);
		}
	}

	@Override
	public void addOwnVariable(final VariableDescription var) {
		if (!var.getKeyword().equals(PARAMETER)) {
			super.addOwnVariable(var);
		} else {
			addParameterNoCheck(var);
		}
	}

	@Override
	public String getTitle() {
		return "experiment " + getName();
	}

	@Override
	public boolean isExperiment() {
		return true;
	}

	@Override
	public List<IDescription> getChildren() {
		final List<IDescription> result = super.getChildren();
		if (parameters != null) {
			result.addAll(parameters.values());
		}
		if (output != null)
			result.add(output);
		if (permanent != null)
			result.add(permanent);
		return result;
	}

	@Override
	public Map<String, VariableDescription> getVariables() {
		if (variables == null) {
			variables = new TOrderedHashMap<>();
			// Trick to have these two variables always at the beginning.
			variables.put(ExperimentAgent.PROJECT_PATH, null);
			variables.put(ExperimentAgent.MODEL_PATH, null);
		}
		return variables;
	}

	/**
	 * @return
	 */
	public Boolean isBatch() {
		return IKeyword.BATCH.equals(getFacets().getLabel(IKeyword.TYPE));
	}

	@Override
	public Class<? extends ExperimentAgent> getJavaBase() {
		return isBatch() ? BatchAgent.class : ExperimentAgent.class;
	}

	@Override
	public ExperimentDescription getExperimentContext() {
		return this;
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

	private IDescription getSimilar(final List<IDescription> descs, final IDescription desc) {
		for (final IDescription d : descs) {
			if (d != null && d.getKeyword().equals(desc.getKeyword()) && d.getName().equals(desc.getName())) {
				return d;
			}
		}
		return null;
	}

	private void mergeOutputs(final StatementDescription inherited, final StatementDescription defined) {
		final List<IDescription> definedOutputs = defined.getChildren();
		for (final IDescription in : inherited.getChildren()) {
			final IDescription redefined = getSimilar(definedOutputs, in);
			if (redefined == null) {
				defined.addChild(in.copy(defined));
			} else {
				redefined.info("This definition of " + redefined.getName() + " supersedes the one in "
						+ in.getSpeciesContext().getName(), IGamlIssue.REDEFINES, NAME);
			}
		}
	}

	@Override
	protected void addBehavior(final StatementDescription r) {
		if (r.getKeyword().equals(OUTPUT)) {
			output = r;
		} else if (r.getKeyword().equals(PERMANENT)) {
			permanent = r;
		} else
			super.addBehavior(r);
	}

}
