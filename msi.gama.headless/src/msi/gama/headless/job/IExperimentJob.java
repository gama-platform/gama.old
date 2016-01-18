package msi.gama.headless.job;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.headless.runtime.RuntimeContext;
import msi.gama.kernel.model.IModel;
import msi.gaml.descriptions.ExperimentDescription;
import msi.gaml.descriptions.IDescription;
import msi.gaml.descriptions.IExpressionDescription;

public interface IExperimentJob {
	
	public String getExperimentID();
	public void addParameter(final Parameter p);
	public void addOutput(final Output p);
	public void setSeed(final long s);
	public long getSeed();
	public long getStep();
	
	public void loadAndBuild(RuntimeContext rtx) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	public Element asXMLDocument(Document doc);
	
	
	public void play();
	public void doStep();
	
	
	public static ExperimentJob loadAndBuildJob( final ExperimentDescription expD, final String path, IModel model)
	{
		List<Output> outputList = new ArrayList<Output>(); 
		List<Parameter> parameterList = new ArrayList<Parameter>(); 
		String expName = expD.getName();
		IExpressionDescription  seedDescription =  expD.getFacets().get(IKeyword.SEED);
		long mseed = 0l;
		if(seedDescription !=null)
		{
			mseed = Long.valueOf(seedDescription.getExpression().literalValue()).longValue();
		}
		IDescription d = expD.getChildWithKeyword(IKeyword.OUTPUT);
		System.out.println("dsqfdsqfdq qdf qs XXXX");
		ExperimentJob expJob = new ExperimentJob(path,expName,0,mseed );
		
		if(d != null)
		{
			
			System.out.println("dsqfdsqfdq qdf qs");
			Iterable<IDescription> monitors = d.getChildrenWithKeyword(IKeyword.MONITOR);
			for(IDescription moni:monitors) {
				//outputList.add(Output.loadAndBuildOutput(moni));
				expJob.addOutput(Output.loadAndBuildOutput(moni));
				System.out.println(outputList);
			}
			
			Iterable<IDescription> displays = d.getChildrenWithKeyword(IKeyword.DISPLAY);
			for(IDescription disp:displays) {
				//outputList.add(Output.loadAndBuildOutput(disp));
				expJob.addOutput(Output.loadAndBuildOutput(disp));
				System.out.println(outputList);
			}
		}
		
		Iterable<IDescription> parameters = expD.getChildrenWithKeyword(IKeyword.PARAMETER);
		for(IDescription para:parameters) {
			System.out.println("sdqfqsf df qfdqf qdsf dqs");
			//parameterList.add(Parameter.loadAndBuildParameter(para));
			expJob.addParameter(Parameter.loadAndBuildParameter(para, model));
			System.out.println(parameterList);
		}
		
		return expJob;
	}
}
