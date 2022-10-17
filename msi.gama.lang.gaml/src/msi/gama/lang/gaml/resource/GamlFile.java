/*******************************************************************************************************
 *
 * GamlFile.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaFile;
import msi.gaml.expressions.IExpression;
import msi.gaml.operators.Cast;
import msi.gaml.types.IContainerType;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

/**
 * Written by drogoul Modified on 13 nov. 2011
 *
 * @todo Description
 *
 */
@file (
		name = "gaml",
		extensions = { "gaml", "experiment" },
		buffer_type = IType.LIST,
		buffer_content = IType.SPECIES,
		buffer_index = IType.INT,
		concept = { IConcept.FILE },
		doc = @doc ("Represents GAML model files"))
@SuppressWarnings ({ "unchecked", "rawtypes" })
public class GamlFile extends GamaFile<IList<IModel>, IModel> {

	/** The mymodel. */
	private IModel model;
	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param pathName
	 */
	private final String experimentName;

	/** The alias name. */
	private final String aliasName;

	/**
	 * Instantiates a new gaml file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to read a gaml file (.gaml)",
			examples = { @example (
					value = "file f <- gaml_file(\"file.gaml\");",
					isExecutable = false) })
	public GamlFile(final IScope scope, final String pathName) throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = "";
		aliasName = "";

	}

	@Override
	public IContainerType getGamlType() { return Types.FILE.of(Types.INT, Types.SPECIES); }

	/**
	 * Instantiates a new gaml file.
	 *
	 * @param scope
	 *            the scope
	 * @param pathName
	 *            the path name
	 * @param expName
	 *            the exp name
	 * @param cName
	 *            the c name
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@doc (
			value = "This file constructor allows to compile a gaml file and run an experiment",
			examples = { @example (
					value = "file f <- gaml_file(\"file.gaml\", \"my_experiment\", \"my_model\");",
					isExecutable = false) })
	public GamlFile(final IScope scope, final String pathName, final String expName, final String cName)
			throws GamaRuntimeException {
		super(scope, pathName);
		experimentName = expName;
		aliasName = cName;

	}

	@Override
	public IList<String> getAttributes(final IScope scope) {
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Creates the experiment.
	 *
	 * @param expName
	 *            the exp name
	 * @return the i experiment plan
	 */
	public IExperimentPlan createExperiment(final String expName) {
		final IExperimentPlan exp = getModel(null).getExperiment("Experiment " + expName);
		for (final IOutput o : exp.getOriginalSimulationOutputs()) { o.setName(o.getName() + "#" + aliasName); }
		for (final IOutput o : exp.getExperimentOutputs()) { o.setName(o.getName() + "#" + aliasName); }

		// GAMA.addGuiExperiment(exp);

		return exp;
	}

	/**
	 * Execute.
	 *
	 * @param scope
	 *            the scope
	 * @param with_exp
	 *            the with exp
	 * @param param_input
	 *            the param input
	 * @param param_output
	 *            the param output
	 * @param reset
	 *            the reset
	 * @param repeat
	 *            the repeat
	 * @param stopCondition
	 *            the stop condition
	 * @param share
	 *            the share
	 */
	public void execute(final IScope scope, final IExpression with_exp, final IExpression param_input,
			final IExpression param_output, final IExpression reset, final IExpression repeat,
			final IExpression stopCondition, final IExpression share) {
		final IExperimentPlan experiment = createExperiment(experimentName);

		if (param_input != null) {
			final IMap in = Cast.asMap(scope, param_input.value(scope), true);
			for (int i = 0; i < in.getKeys().size(); i++) {
				experiment.getModel().getVar(in.getKeys().get(i).toString()).setValue(null, in.getValues().get(i));
			}
		}
		GAMA.openExperimentFromGamlFile(experiment);
	}

	/**
	 * @see msi.gama.util.GamaFile#fillBuffer()
	 */
	@Override
	protected void fillBuffer(final IScope scope) throws GamaRuntimeException {
		if (getBuffer() != null) return;
		setBuffer(GamaListFactory.<IModel> create(Types.SPECIES));
		((IList) getBuffer()).add(getModel(scope).getSpecies());
	}

	@Override
	public Envelope3D computeEnvelope(final IScope scope) {
		return null;
	}

	/**
	 * Gets the model.
	 *
	 * @param scope
	 *            the scope
	 * @return the model
	 */
	private IModel getModel(final IScope scope) {
		if (model == null) {
			model = GamlModelBuilder.getDefaultInstance().compile(getURIRelativeToWorkspace(), null);
			if (aliasName != null && !aliasName.isBlank()) { getModel(scope).getDescription().setAlias(aliasName); }
		}
		return model;
	}

}
