package msi.gama.headless.job;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.core.HeadlessSimulationLoader;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.ModelDescription;

public class JobPlan {

	public class JobPlanExperimentID {

		String modelName;
		String experimentName;

		public String getModelName() {
			return modelName;
		}

		public String getExperimentName() {
			return experimentName;
		}

		@Override
		public boolean equals(final Object o) {
			return o instanceof JobPlanExperimentID && ((JobPlanExperimentID) o).modelName.equals(this.modelName)
					&& ((JobPlanExperimentID) o).experimentName.equals(this.experimentName);
		}

		public JobPlanExperimentID(final String modelN, final String expN) {
			this.modelName = modelN;
			this.experimentName = expN;
		}
	}

	Map<String, List<IExperimentJob>> availableExperimentations;
	Map<JobPlanExperimentID, IExperimentJob> originalJobs;
	List<Long> choosenSeed;
	IModel model = null;

	public JobPlan() {
		this.availableExperimentations = new HashMap<String, List<IExperimentJob>>();
		this.choosenSeed = new ArrayList<Long>();
		this.originalJobs = new HashMap<JobPlanExperimentID, IExperimentJob>();
	}

	public List<IExperimentJob> getJobsWithName(final String name) {
		List<IExperimentJob> res = this.availableExperimentations.get(name);
		if (res == null) {
			res = new ArrayList<IExperimentJob>();
			this.availableExperimentations.put(name, res);
		}
		return res;
	}

	public IExperimentJob getJobDescriptionWithName(final JobPlanExperimentID name) {
		return this.originalJobs.get(name);
	}

	public JobPlanExperimentID[] loadModelAndCompileJob(final String modelPath) throws IOException {
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

	public List<IExperimentJob> constructAllJobs(final long[] seeds, final long finalStep) {
		final List<IExperimentJob> jobs = new ArrayList<>();
		for (final IExperimentJob locJob : originalJobs.values()) {
			jobs.addAll(constructJobWithName(locJob, seeds, finalStep, null, null));
		}
		return jobs;
	}

	public List<IExperimentJob> constructJobWithName(final JobPlanExperimentID name, final long[] seeds,
			final long finalStep, final List<Parameter> in, final List<Output> out) {
		final IExperimentJob originalExperiment = this.getJobDescriptionWithName(name);
		return constructJobWithName(originalExperiment, seeds, finalStep, in, out);

	}

	public List<IExperimentJob> constructJobWithName(final IExperimentJob originalExperiment, final long[] seeds,
			final long finalStep, final List<Parameter> in, final List<Output> out) {
		final List<IExperimentJob> res = new ArrayList<IExperimentJob>();
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

	public List<IExperimentJob> getBuiltPlan() {
		final List<IExperimentJob> res = new ArrayList<IExperimentJob>();
		for (final List<IExperimentJob> ll : this.availableExperimentations.values()) {
			res.addAll(ll);
		}
		return res;
	}

	private static List<IExperimentJob> loadAndBuildJobs(final IModel model) {
		final ModelDescription modelDescription = model.getDescription().getModelDescription();
		final List<IExperimentJob> res = new ArrayList<IExperimentJob>();

		@SuppressWarnings("unchecked")
		final Collection<ExperimentDescription> experiments = (Collection<ExperimentDescription>) modelDescription
				.getExperiments();

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
