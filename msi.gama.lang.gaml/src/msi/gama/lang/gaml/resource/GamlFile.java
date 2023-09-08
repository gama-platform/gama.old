/*******************************************************************************************************
 *
 * GamlFile.java, in msi.gama.lang.gaml, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.lang.gaml.resource;

import java.io.File;

import org.eclipse.emf.common.util.URI;

import msi.gama.common.geometry.Envelope3D;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.model.IModel;
import msi.gama.lang.gaml.validation.GamlModelBuilder;
import msi.gama.outputs.IOutput;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.file;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IList;
import msi.gama.util.IMap;
import msi.gama.util.file.GamaFile;
import msi.gama.util.file.GamlFileInfo;
import msi.gaml.compilation.GAML;
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
@vars ({ @variable (
		name = "experiments",
		type = IType.LIST,
		of = IType.STRING,
		doc = { @doc ("Returns a list containing the names of the experiments defined in this file. An empty list is returned if it does not define any experiment") }),
		@variable (
				name = "tags",
				type = IType.LIST,
				of = IType.STRING,
				doc = { @doc ("Returns a list containing the names of the tags defined in this file. An empty list is returned if it does not define any tag") }),
		@variable (
				name = "uses",
				type = IType.LIST,
				of = IType.STRING,
				doc = { @doc ("Returns a list containing the names of the files 'used' (read or written) in this model") }),
		@variable (
				name = "valid",
				type = IType.BOOL,
				doc = { @doc ("Returns true if this file is syntactically valid, false otherwise") }),
		@variable (
				name = "imports",
				type = IType.LIST,
				of = IType.STRING,
				doc = { @doc ("Returns a list containing the names of the models imported by this file. An empty list is returned if it does not import any model") }) })
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
	 * Gets the experiments.
	 *
	 * @return the experiments
	 */
	@getter ("experiments")
	public IList<String> getExperiments(final IScope scope) {
		File file = getFile(scope);
		// TODO AD Verify the use of a 'file' URI.
		GamlFileInfo info = GAML.getInfo(URI.createFileURI(getFile(scope).getAbsolutePath()), file.lastModified());
		if (info != null) return GamaListFactory.wrap(Types.STRING, info.getExperiments());
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Gets the tags.
	 *
	 * @param scope
	 *            the scope
	 * @return the tags
	 */
	@getter ("tags")
	public IList<String> getTags(final IScope scope) {
		File file = getFile(scope);
		GamlFileInfo info = GAML.getInfo(URI.createFileURI(getFile(scope).getAbsolutePath()), file.lastModified());
		if (info != null) return GamaListFactory.wrap(Types.STRING, info.getTags());
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Gets the uses.
	 *
	 * @param scope
	 *            the scope
	 * @return the uses
	 */
	@getter ("uses")
	public IList<String> getUses(final IScope scope) {
		File file = getFile(scope);
		GamlFileInfo info = GAML.getInfo(URI.createFileURI(getFile(scope).getAbsolutePath()), file.lastModified());
		if (info != null) return GamaListFactory.wrap(Types.STRING, info.getUses());
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Gets the imports.
	 *
	 * @param scope
	 *            the scope
	 * @return the imports
	 */
	@getter ("imports")
	public IList<String> getImports(final IScope scope) {
		File file = getFile(scope);
		GamlFileInfo info = GAML.getInfo(URI.createFileURI(getFile(scope).getAbsolutePath()), file.lastModified());
		if (info != null) return GamaListFactory.wrap(Types.STRING, info.getImports());
		return GamaListFactory.EMPTY_LIST;
	}

	/**
	 * Checks if is valid.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter ("valid")
	public Boolean isValid(final IScope scope) {
		File file = getFile(scope);
		GamlFileInfo info = GAML.getInfo(URI.createFileURI(getFile(scope).getAbsolutePath()), file.lastModified());
		if (info != null) return info.isValid();
		return false; // If the file is not available, return false by default
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
