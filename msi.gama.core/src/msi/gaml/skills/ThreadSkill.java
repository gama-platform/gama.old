/*******************************************************************************************************
 *
 * ThreadSkill.java, in msi.gama.core, is part of the source code of the GAMA modeling and simulation platform
 * (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package msi.gaml.skills;

import java.util.concurrent.atomic.AtomicBoolean;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;

/**
 * ThreadSkill : This class is intended to define the minimal set of behaviours required from an agent that is able to
 * run an action in a thread. Each member that has a meaning in GAML is annotated with the respective tags (vars, getter, setter, init,
 * action & args)
 *
 * @author taillandier 30 August 2022
 */

@doc ("The thread skill is intended to define the minimal set of behaviours required for agents that are able to run an action in a thread")
@skill (
		name = IKeyword.THREAD_SKILL,
		concept = { IConcept.SKILL, IConcept.SYSTEM })
public class ThreadSkill extends Skill {


	private static ControlSubThread currentThread;
	/**
	 * primStartThread
	 *
	 * @param scope
	 *            the scope
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "start_thread",
					args = { @arg (
							name = "continuous",
							type = IType.BOOL,
							optional = true,
							doc = @doc ("is the thread should run continuously or just once" )),
							@arg (
							name = "interval",
							type = IType.INT,
							optional = true,
							doc = @doc ("interval of time between two executions of the action" )) },
					
			doc = @doc (
					examples = { @example ("do start_thread continuous: true;") },
					returns = "true if the thread was well created and started, false otherwise",
					value = "Start a new thread that will run the runnable_action action (continuously by default)."))
	public Boolean primStartThread(final IScope scope) throws GamaRuntimeException {
		Boolean continuous = (!scope.hasArg("continuous")) ? true : (Boolean) scope.getArg("continuous", IType.BOOL);
		Integer interval = (!scope.hasArg("interval")) ? 100 : (Integer) scope.getIntArg("interval"); 
		if (currentThread == null) {
			currentThread = new ControlSubThread(scope.getAgent(), continuous, interval);
			currentThread.start();
			return true;
		}
		return false;
	}
	
	@action (
			name = "end_thread",
			doc = @doc (
					examples = { @example ("do end_thread;") },
					returns = "true if the thread was well stopped, false otherwise",
					value = "End the current thread."))
	public Boolean primEndThread(final IScope scope) throws GamaRuntimeException {
		if (currentThread != null) {
			currentThread.stop(); 
			currentThread = null;
			return true;
		}
		return false;
	}
	
	/**
	 * primExternalFactorOnRemainingTime
	 *
	 * @param scope
	 *            the scope
	 * @return the double
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@action (
			name = "runnable_action"
			)
	public Object primExternalFactorOnRemainingTime(final IScope scope) throws GamaRuntimeException {
		return null;
	}
	
	
	public class ControlSubThread implements Runnable {

	    private Thread worker;
	    private final AtomicBoolean running = new AtomicBoolean(false);
	    private int interval;
	    private IAgent agent;
	    private Boolean continuous;

	    public ControlSubThread(IAgent ag, Boolean cont, int sleepInterval) {
	        interval = sleepInterval;
	        agent = ag;
	        continuous = cont;
	    }
	 
	    public void start() {
	        worker = new Thread(this);
	        worker.start();
	    }
	 
	    public void stop() {
	        running.set(false);
	        Thread.currentThread().interrupt();
            
	    }

	    public void run() { 
	    	if (continuous) {
		    	running.set(true);
		        while (running.get()) {
		            try { 
		                Thread.sleep(interval); 
		            } catch (InterruptedException e){ 
		                Thread.currentThread().interrupt();
		                System.out.println(
		                  "Thread was interrupted, Failed to complete operation");
		            }
		           
		           ISpecies context = agent.getSpecies();
					
		           IStatement action = context.getAction("runnable_action");
		           action.executeOn(agent.getScope().copy("ThreadScope"));
		      }
	    	} else {
	    		 ISpecies context = agent.getSpecies();
	    		 IStatement action = context.getAction("runnable_action");
		         action.executeOn(agent.getScope().copy("ThreadScope"));
		         this.stop();
		      
	    	}
	    } 
	    
	    
	}


	
}
