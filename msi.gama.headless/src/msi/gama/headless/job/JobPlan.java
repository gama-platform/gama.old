package msi.gama.headless.job;

import java.io.File;
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
		
		public boolean equals(Object o)
		{
			return o instanceof JobPlanExperimentID 
					&& ((JobPlanExperimentID)o).modelName.equals(this.modelName)
					&& ((JobPlanExperimentID)o).experimentName.equals(this.experimentName);
		}
		
		public JobPlanExperimentID(String modelN, String expN) {
			this.modelName=modelN;
			this.experimentName=expN;
		}
	}
	
	Map<String, List<IExperimentJob>> availableExperimentations;
	Map<JobPlanExperimentID, IExperimentJob> originalJobs;
	List<Long> choosenSeed;
	IModel model = null;
	

	
	public JobPlan()
	{
		this.availableExperimentations = new HashMap<String, List<IExperimentJob>>();
		this.choosenSeed = new ArrayList<Long>();
		this.originalJobs = new HashMap<JobPlanExperimentID, IExperimentJob>();
	}
	
	private String createExperimentName(IExperimentJob job)
	{
		return job.getExperimentName() + "_"+ this.model.getName();
	}
	
	public List<IExperimentJob> getJobsWithName(String name)
	{
		List<IExperimentJob> res = this.availableExperimentations.get(name);
		if(res == null)
		{
			res=new ArrayList<IExperimentJob>();
			this.availableExperimentations.put(name, res);
		}
		return res;
	}
	
	public IExperimentJob getJobDescriptionWithName(JobPlanExperimentID name)
	{
		return this.originalJobs.get(name);
	}
	
	public JobPlanExperimentID[] loadModelAndCompileJob(String modelPath)
	{
		 model = HeadlessSimulationLoader.loadModel(new File(modelPath));
		 List<IExperimentJob> jobs = JobPlan.loadAndBuildJobs(model);
		 JobPlanExperimentID[] res = new JobPlanExperimentID[jobs.size()];
		 int i = 0;
		 for(IExperimentJob oriJob:jobs)
		 {
			//res[i] = createExperimentName(oriJob);
			res[i]=new JobPlanExperimentID(oriJob.getModelName(),oriJob.getExperimentName());
			this.originalJobs.put(res[i], oriJob);
		 }
		 return res;
	}
	public List<IExperimentJob> constructAllJobs(long[] seeds, long finalStep, List<Parameter> in, List<Output> out)
	{
		List<IExperimentJob> jobs = new ArrayList<>();
		for(IExperimentJob locJob:originalJobs.values())
		{
			jobs.addAll(constructJobWithName(locJob,seeds,  finalStep,  in, out));
		}
		return jobs;
	}
	public List<IExperimentJob> constructJobWithName(JobPlanExperimentID name, long[] seeds, long finalStep, List<Parameter> in, List<Output> out)
	{
		IExperimentJob originalExperiment = this.getJobDescriptionWithName(name);
		return constructJobWithName(originalExperiment, seeds,  finalStep,  in, out);
		
	}
	public List<IExperimentJob> constructJobWithName(IExperimentJob originalExperiment, long[] seeds, long finalStep, List<Parameter> in, List<Output> out)
	{	
		List<IExperimentJob> res = new ArrayList<IExperimentJob>();
		for(long sd:seeds)
		{
			IExperimentJob job = new ExperimentJob((ExperimentJob)originalExperiment);
			job.setSeed(sd);
			job.setFinalStep(finalStep);
			if(in != null)
			{
				for(Parameter p:in){
					job.setParameterValueOf(p.getName(), p.getValue());
				}
			}

			if(out!=null)
			{
				List<String> availableOutputs = job.getOutputNames();
				for(Output o:out){
					job.setOutputFrameRate(o.getName(), o.getFrameRate());
				//To be checked
					availableOutputs.remove(o.getName());
				}
				for(String s:availableOutputs){
					job.removeOutputWithName(s);
				}
			}
			
			res.add(job);
			this.getJobsWithName(job.getExperimentName()).add(job);
		}
		return res;
	}
	
	public List<IExperimentJob> getBuiltPlan()
	{
		List<IExperimentJob> res = new ArrayList<IExperimentJob>();
		for(List<IExperimentJob>ll:this.availableExperimentations.values()){
			res.addAll(ll);
		}
		return res;
	}
	
	private static List<IExperimentJob> loadAndBuildJobs(IModel model)
	{
		ModelDescription modelDescription = model.getDescription().getModelDescription();
		List<IExperimentJob> res = new ArrayList<IExperimentJob>();  
	
		@SuppressWarnings("unchecked")
		Collection<ExperimentDescription> experiments =  (Collection<ExperimentDescription>) modelDescription.getExperiments();
		
		for(ExperimentDescription expD:experiments)
		{
			if(!expD.getFacets().get(IKeyword.TYPE).getExpression().literalValue().equals(IKeyword.BATCH))
			{
				IExperimentJob tj = ExperimentJob.loadAndBuildJob(  expD,model.getFilePath(), model);
				tj.setSeed(12);
				res.add(tj);
			}
		}
		return res;
	}
	
}
