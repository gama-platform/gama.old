/*******************************************************************************************************
 *
 * JobPlan.java, in msi.gama.headless, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
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
import java.util.Objects;

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

	/**
	 * The Class JobPlanExperimentID.
	 */
	public class JobPlanExperimentID {

		/** The model name. */
		String modelName;
		
		/** The experiment name. */
		String experimentName;

		/**
		 * Gets the model name.
		 *
		 * @return the model name
		 */
		public String getModelName() {
			return modelName;
		}

		/**
		 * Gets the experiment name.
		 *
		 * @return the experiment name
		 */
		public String getExperimentName() {
			return experimentName;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof JobPlanExperimentID && ((JobPlanExperimentID) o).modelName.equals(this.modelName)
					&& ((JobPlanExperimentID) o).experimentName.equals(this.experimentName);
		}

		@Override
		public int hashCode() {
			return Objects.hash(modelName, experimentName);
		}

		/**
		 * Instantiates a new job plan experiment ID.
		 *
		 * @param modelN the model N
		 * @param expN the exp N
		 */
		public JobPlanExperimentID(final String modelN, final String expN) {
			this.modelName = modelN;
			this.experimentName = expN;
		}
	}

	/** The available experimentations. */
	Map<String, List<IExperimentJob>> availableExperimentations;
	
	/** The original jobs. */
	Map<JobPlanExperimentID, IExperimentJob> originalJobs;
	
	/** The choosen seed. */
	List<Double> choosenSeed;
	
	/** The model. */
	IModel model = null;

	/**
	 * Instantiates a new job plan.
	 */
	public JobPlan() {
		this.availableExperimentations = new HashMap<>();
		this.choosenSeed = new ArrayList<>();
		this.originalJobs = new HashMap<>();
	}

	/**
	 * Gets the jobs with name.
	 *
	 * @param name the name
	 * @return the jobs with name
	 */
	public List<IExperimentJob> getJobsWithName(final String name) {
		List<IExperimentJob> res = this.availableExperimentations.get(name);
		if (res == null) {
			res = new ArrayList<>();
			this.availableExperimentations.put(name, res);
		}
		return res;
	}

	/**
	 * Gets the job description with name.
	 *
	 * @param name the name
	 * @return the job description with name
	 */
	public IExperimentJob getJobDescriptionWithName(final JobPlanExperimentID name) {
		return this.originalJobs.get(name);
	}

	/**
	 * Load model and compile job.
	 *
	 * @param modelPath the model path
	 * @return the job plan experiment I d[]
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws GamaHeadlessException the gama headless exception
	 */
	public JobPlanExperimentID[] loadModelAndCompileJob(final String modelPath)
			throws IOException, GamaHeadlessException {
		model = HeadlessSimulationLoader.loadModel(new File(modelPath));
		final List<IExperimentJob> jobs = JobPlan.loadAndBuildJobs(model);
		final JobPlanExperimentID[] res = new JobPlanExperimentID[jobs.size()];
		final int i = 0;
		for (final IExperimentJob oriJob : jobs) {
			// res[i] = createExperimentName(oriJob);
			res[i] = new JobPlanExperimentID(oriJob.getModelName(), oriJob.getExperimentName());
			this.originalJobs.put(res[i], oriJob);
		}
		return res;
	}

	/**
	 * Construct all jobs.
	 *
	 * @param seeds the seeds
	 * @param finalStep the final step
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
	 * @param name the name
	 * @param seeds the seeds
	 * @param finalStep the final step
	 * @param in the in
	 * @param out the out
	 * @return the list
	 */
	public List<IExperimentJob> constructJobWithName(final JobPlanExperimentID name, final long[] seeds,
			final long finalStep, final List<Parameter> in, final List<Output> out) {
		final IExperimentJob originalExperiment = this.getJobDescriptionWithName(name);
		return constructJobWithName(originalExperiment, seeds, finalStep, in, out);

	}

	/**
	 * Construct job with name.
	 *
	 * @param originalExperiment the original experiment
	 * @param seeds the seeds
	 * @param finalStep the final step
	 * @param in the in
	 * @param out the out
	 * @return the list
	 */
	public List<IExperimentJob> constructJobWithName(final IExperimentJob originalExperiment, final long[] seeds,
			final long finalStep, final List<Parameter> in, final List<Output> out) {
		final List<IExperimentJob> res = new ArrayList<>();
		for (final long sd : seeds) {
			final IExperimentJob job = new ExperimentJob((ExperimentJob) originalExperiment);
			job.setSeed(sd);
			job.setFinalStep(finalStep);
			if (in != null) {
				for (final Parameter p : in) {
					job.setParameterValueOf(p.getName(), p.getValue());
				}
			}

			if (out != null) {
				final List<String> availableOutputs = job.getOutputNames();
				for (final Output o : out) {
					job.setOutputFrameRate(o.getName(), o.getFrameRate());
					availableOutputs.remove(o.getName());
				}
				for (final String s : availableOutputs) {
					job.removeOutputWithName(s);
				}
			}

			res.add(job);
			this.getJobsWithName(job.getExperimentName()).add(job);
		}
		return res;
	}

	/**
	 * Gets the built plan.
	 *
	 * @return the built plan
	 */
	public List<IExperimentJob> getBuiltPlan() {
		final List<IExperimentJob> res = new ArrayList<>();
		for (final List<IExperimentJob> ll : this.availableExperimentations.values()) {
			res.addAll(ll);
		}
		return res;
	}

	/**
	 * Load and build jobs.
	 *
	 * @param model the model
	 * @return the list
	 */
	private static List<IExperimentJob> loadAndBuildJobs(final IModel model) {
		final ModelDescription modelDescription = model.getDescription().getModelDescription();
		final List<IExperimentJob> res = new ArrayList<>();

		@SuppressWarnings ("unchecked") final Collection<ExperimentDescription> experiments =
				(Collection<ExperimentDescription>) modelDescription.getExperiments();

		for (final ExperimentDescription expD : experiments) {
			if (!expD.getLitteral(IKeyword.TYPE).equals(IKeyword.BATCH)) {
				final IExperimentJob tj = ExperimentJob.loadAndBuildJob(expD, model.getFilePath(), model);
				tj.setSeed(12);
				res.add(tj);
			}
		}
		return res;
	}

}
