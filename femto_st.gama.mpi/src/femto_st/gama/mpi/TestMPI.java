package femto_st.gama.mpi;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import mpi.MPI;
import mpi.MPIException;
import mpi.Status;
import msi.gama.common.geometry.Envelope3D;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.ext.kml.Polygon;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.MutableSavedAgent;
import msi.gama.metamodel.agent.SavedAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.operators.Spatial;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;
import msi.gaml.types.Types;
import ummisco.gama.serializer.factory.StreamConverter;




/**

todo coding camp: 

- Centraliser la création de nouveaux individus 
- Détection de la présence d'un individu dans un proc avant la création de celui-ci depuis une OLZ
- Fix référence sur un agent lors d'un envoie par MPI (ref nul si sur un autre proc)
		-> copie de la ref 
			-> boucle possible ?
- Thread unique plutôt qu'un thread par slave
	-> comment avoir le controle sur mpirun ? 
		-> création du thread avant le mpi run ? 
				-> nouveau type d'exp
					-> cette exp lance mpirun après avoir crée le thread unique
- 


 */
class TestThread extends Thread
{
	private volatile boolean running;
	List<Thread> threadList = new ArrayList<Thread>();
	
	TestThread()
	{
		this.running = true;
	}
	
	public void run()
	{
		System.out.println("RequestListenerThread Run begin ");
		while(this.running)
		{	
			System.out.println("running = "+this.running);
			try {
				Thread.sleep(5000);
				Thread th = new Thread()
				{
					public void run()
					{
						while(true)
						{
							
						}
					}
				};
				threadList.add(th);
				th.start();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("listThrad "+threadList);
		for(var auto : threadList)
		{
			System.out.println("interrupting "+auto);
			auto.interrupt();
			System.out.println(auto+" auto.isInterrupted() ? : "+auto.isInterrupted());
			
		}
		System.out.println("End of thread");
	}
	
	public void stopThread()
    {
        this.running = false;
    }
	
	public boolean isRunning()
	{
		return this.running;
	}
}

@species(name = "TestMPI")
@doc ("TestMPI")
public class TestMPI extends GamlAgent 
{
	TestThread tt;
	public TestMPI(IPopulation<? extends IAgent> s, int index) {
		super(s, index);

		System.out.println("TESTMPI agent");
		try 
		{
			//PrintStream fileOut = new PrintStream("C:\\Users\\lucas\\git\\gama.experimental\\femto_st.gama.mpi\\models\\Plugin_MPI_Test_Model\\models\\filename.txt");
			//System.setOut(fileOut);
			
		} catch (SecurityException /*| IOException */ e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@action(name = "testing",
			args = { @arg (
					name = "kkk",
					type = IType.GEOMETRY,
					doc = @doc ("kkk shape")),
			}
	)
	public List<IAgent> test(final IScope scope)
	{
		ArrayList<IAgent> agentListInImportedModel = new ArrayList<IAgent>();
		var agentListInMainModel = scope.getExperiment().getAgents(scope.getRoot().getScope());
		for(var let : agentListInMainModel)
		{
			if(let instanceof SimulationAgent)
			{
				IPopulation<? extends IAgent>[] pops = ((ExperimentAgent)((SimulationAgent)let).getExternMicroPopulationFor("pp.movingExp").getAgent(0)).getSimulation().getMicroPopulations();
				for(var pop : pops)
				{
					IList<? extends IShape> listShape = Spatial.Queries.inside(scope, pop, (IShape) scope.getArg("kkk",IType.GEOMETRY));
					System.out.println("shape : "+listShape);
					IList<IAgent> listAgent = GamaListFactory.wrap(Types.LIST, listShape.stream().map(t -> t.getAgent()).collect(Collectors.toList()));
					System.out.println("agent : "+listAgent);
					
					agentListInImportedModel.addAll(listAgent);
				}
				
				System.out.println("result of test "+agentListInImportedModel);
				return agentListInImportedModel;
			}
		}
		System.out.println("NO IMPORTED MODEL FOUND");
		return agentListInImportedModel;
	}
	
	@action(name = "begin")
	public void begin(IScope scope)
	{
		System.out.println("begin");
		tt = new TestThread();
		tt.start();
	}
	
	@action(name = "end")
	public void end(IScope scope)
	{
		System.out.println("end");
		tt.stopThread();
	}
	
	@action(name = "seri_deseri",
			args = { @arg (
					name = "agent",
					type = IType.AGENT,
					doc = @doc ("kkk")),
			}
	)
	public Object seri(IScope scope)
	{
		System.out.println("class = "+ ((IAgent) scope.getArg("agent", IType.AGENT)).getClass());
		String seri = StreamConverter.convertMPIObjectToStream(scope, (IAgent) scope.getArg("agent", IType.AGENT));
		System.out.println("seri = "+seri);

		IAgent deseri = (IAgent) StreamConverter.convertMPIStreamToObject(scope, seri);
		System.out.println("deseri hash= "+deseri.getUniqueID());
		
		return deseri;
	}
	
	@action(name = "seri_deseri_list",
			args = { @arg (
					name = "agent",
					type = IType.LIST,
					doc = @doc ("kkk")),
			}
	)
	public Object seri_list(IScope scope)
	{
		System.out.println(" heloo = "+(IList) scope.getArg("agent", IType.LIST));
		String seri = StreamConverter.convertMPIObjectToStream(scope, (IList) scope.getArg("agent", IType.LIST));
		System.out.println("seri = "+seri);

		List<IAgent> deseri = (List<IAgent>) StreamConverter.convertMPIStreamToObject(scope, seri);
		System.out.println(" deseri "+deseri);
		
		return deseri;
	}
	
	
	
}
