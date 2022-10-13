/*******************************************************************************************************
 *
 * HeadlessStatement.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.command;

import java.io.File;

import msi.gama.headless.job.ExperimentJob;
import msi.gama.headless.runtime.SimulationRuntime;
import msi.gama.headless.runtime.SimulationRuntime;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.IConcept;
import msi.gama.precompiler.ISymbolKind;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.operators.Cast;
import msi.gaml.statements.AbstractStatement;
import msi.gaml.types.IType;

/**
 * The Class HeadlessStatement.
 */
@symbol (
		name = IKeywords.RUNSIMULARTION,
		kind = ISymbolKind.SEQUENCE_STATEMENT,
		with_sequence = true,
		concept = { IConcept.HEADLESS })
@inside (
		kinds = { ISymbolKind.BEHAVIOR, ISymbolKind.SINGLE_STATEMENT, ISymbolKind.SPECIES, ISymbolKind.MODEL })
@facets (
		value = { @facet (
				name = IKeywords.MODEL,
				type = IType.STRING,
				optional = false,
				doc = @doc ("Indicates the model containing the experiment to run")),
				@facet (
						name = IKeywords.EXPERIMENT,
						type = IType.STRING,
						optional = false,
						doc = @doc ("Indicates the name of the experiment to run")),
				@facet (
						name = IKeywords.END,
						type = IType.INT,
						optional = true,
						doc = @doc ("Indicates the cycle at which the experiment should stop")),
				@facet (
						name = IKeywords.CORE,
						type = IType.INT,
						optional = true,
						doc = @doc ("Indicates the number of cores to use to run the experiments")),
				@facet (
						name = IKeywords.WITHSEED,
						type = IType.INT,
						optional = true,
						doc = @doc ("Provides a predetermined seed instead of letting GAMA choose one")),
				// @facet(name = IKeywords.OUT, type = IType.STRING, optional = true),
				@facet (
						name = IKeywords.WITHOUTPUTS,
						type = IType.MAP,
						optional = true),
				@facet (
						name = IKeywords.WITHPARAMS,
						type = IType.MAP,
						optional = true) },
		omissible = IKeywords.EXPERIMENT)
public class HeadlessStatement extends AbstractStatement {

	/** The number of thread. */
	private final int numberOfThread = 4;

	/** The processor queue. */
	private final SimulationRuntime processorQueue;

	/** The max simulation ID. */
	private int maxSimulationID = 0;

	/**
	 * Gets the simulation id.
	 *
	 * @return the simulation id
	 */
	public String getSimulationId() { return String.valueOf(maxSimulationID++); }

	/**
	 * Instantiates a new headless statement.
	 *
	 * @param desc
	 *            the desc
	 */
	public HeadlessStatement(final IDescription desc) {
		super(desc);
		processorQueue = new SimulationRuntime();
	}

	/**
	 * Retrieve model file absolute path.
	 *
	 * @param scope
	 *            the scope
	 * @param filename
	 *            the filename
	 * @return the string
	 */
	private String retrieveModelFileAbsolutePath(final IScope scope, final String filename) {
		if (filename.charAt(0) == '/') return filename;
		return new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath() + "/" + filename;
	}

	@Override
	protected Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {

		int seed = 0;
		final String expName = Cast.asString(scope, getFacetValue(scope, IKeywords.EXPERIMENT));
		String modelPath = Cast.asString(scope, getFacetValue(scope, IKeywords.MODEL));
		if (modelPath != null && !modelPath.isEmpty()) {
			modelPath = retrieveModelFileAbsolutePath(scope, modelPath);
		} else {
			// no model specified, this caller model path is used.
			modelPath = scope.getModel().getFilePath();
		}

		// final GamaMap<String, ?> outputs = Cast.asMap(scope, getFacetValue(scope, IKeywords.WITHOUTPUTS), false);

		if (this.hasFacet(IKeywords.WITHSEED)) { seed = Cast.asInt(scope, getFacetValue(scope, IKeywords.WITHSEED)); }

		final long lseed = seed;

		// DEBUG.OUT("chemin du fichier" + new File(scope.getModel().getFilePath()).getParentFile().getAbsolutePath());

		final ExperimentJob sim = new ExperimentJob(this.getSimulationId(), modelPath, expName, 1000, "", lseed);

		this.processorQueue.execute(sim);

		return null;
	}

}
