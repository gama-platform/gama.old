/*******************************************************************************************************
 *
 * JobPlan.java, in msi.gama.headless, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gama.headless.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.core.GamaHeadlessException;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.ModelDescription;

/**
 * The Class JobPlan.
 */
public class JobPlan {

	public record JobPlanExperimentID(String modelName, String experimentName) {}

	/** The original jobs. */
	final Map<JobPlanExperimentID, IExperimentJob> originalJobs = new HashMap<>();

	/** The choosen seed. */
	List<Double> choosenSeed = new ArrayList<>();

	/** The model. */
	IModel model = null;

	/**
	 * Load model and compile job.
	 *
	 * @param modelPath
	 *            the model path
	 * @return the job plan experiment I d[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException
	 *             the gama headless exception
	 */
	public void loadModelAndCompileJob(final String modelPath) throws IOException, GamaHeadlessException {
		model = HeadlessSimulationLoader.loadModel(new File(modelPath));
		final List<IExperimentJob> jobs = JobPlan.loadAndBuildJobs(model);
		for (final IExperimentJob oriJob : jobs) {
			this.originalJobs.put(new JobPlanExperimentID(oriJob.getModelName(), oriJob.getExperimentName()), oriJob);
		}
	}

	/**
	 * Construct all jobs.
	 *
	 * @param seeds
	 *            the seeds
	 * @param finalStep
	 *            the final step
	 * @return the list
	 */
	public List<IExperimentJob> constructAllJobs(final long[] seeds, final long finalStep) {
		final List<IExperimentJob> jobs = new ArrayList<>();
		for (final IExperimentJob locJob : originalJobs.values()) {
			jobs.addAll(constructJobWithName(locJob, seeds, finalStep, null, null));
		}
		return jobs;
	}

	/**
	 * Construct job with name.
	 *
	 * @param originalExperiment
	 *            the original experiment
	 * @param seeds
	 *            the seeds
	 * @param finalStep
	 *            the final step
	 * @param in
	 *            the in
	 * @param out
	 *            the out
	 * @return the list
	 */
	public List<IExperimentJob> constructJobWithName(final IExperimentJob originalExperiment, final long[] seeds,
			final long finalStep, final List<Parameter> in, final List<Output> out) {
		final List<IExperimentJob> res = new ArrayList<>();
		for (final long sd : seeds) {
			final IExperimentJob job = new ExperimentJob((ExperimentJob) originalExperiment);
			job.setSeed(sd);
			job.setFinalStep(finalStep);
			if (in != null) { for (final Parameter p : in) { job.setParameterValueOf(p.getName(), p.getValue()); } }
			if (out != null) {
				final List<String> availableOutputs = job.getOutputNames();
				for (final Output o : out) {
					job.setOutputFrameRate(o.getName(), o.getFrameRate());
					availableOutputs.remove(o.getName());
				}
				for (final String s : availableOutputs) { job.removeOutputWithName(s); }
			}

			res.add(job);
		}
		return res;
	}

	/**
	 * Load and build jobs.
	 *
	 * @param model
	 *            the model
	 * @return the list
	 */
	private static List<IExperimentJob> loadAndBuildJobs(final IModel model) {
		final ModelDescription modelDescription = model.getDescription().getModelDescription();
		final List<IExperimentJob> res = new ArrayList<>();

		@SuppressWarnings ("unchecked") final Collection<ExperimentDescription> experiments =
				(Collection<ExperimentDescription>) modelDescription.getExperiments();

		for (final ExperimentDescription expD : experiments) {
			if (!IKeyword.BATCH.equals(expD.getLitteral(IKeyword.TYPE))) {
				final IExperimentJob tj = ExperimentJob.loadAndBuildJob(expD, model.getFilePath(), model);
				tj.setSeed(12);
				res.add(tj);
			}
		}
		return res;
	}

}
