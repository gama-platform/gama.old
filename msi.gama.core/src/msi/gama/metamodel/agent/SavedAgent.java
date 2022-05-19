/*******************************************************************************************************
 *
 * SavedAgent.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.metamodel.agent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IMap;
import msi.gaml.descriptions.ModelDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.types.Types;

/**
 * A helper class to save agent and restore/recreate agent as a member of a population.
 */
@SuppressWarnings ("unchecked")
public class SavedAgent extends GamaMap<String, Object> {

	/** Variables which are not saved during the capture and release process. */
	static final List<String> UNSAVABLE_VARIABLES = Arrays.asList(IKeyword.PEERS, IKeyword.AGENTS, IKeyword.HOST,
			IKeyword.TOPOLOGY, IKeyword.MEMBERS, "populations");

	/** The index. */
	int index;
	
	/** The species **/
	String species;
	
	/** The source **/
	String source;

	/** The alias **/
	String alias;
	
	/** The uniqueID **/
	int uniqueID;
	
	
	/** The inner populations. */
	Map<String, List<SavedAgent>> innerPopulations;

	@Override
	public final SavedAgent clone() {
		final SavedAgent result = new SavedAgent();
		result.putAll(this);
		result.innerPopulations = innerPopulations;
		result.index = index;
		result.species = species;
		result.source = source;
		result.alias = alias;
		result.uniqueID = uniqueID;
		return result;
	}

	/**
	 * Instantiates a new saved agent.
	 */
	protected SavedAgent() {
		super(11, Types.STRING, Types.NO_TYPE);
	}
	

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param scope the scope
	 * @param agent the agent
	 * @throws GamaRuntimeException the gama runtime exception
	 */
	public SavedAgent(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		this();
		index = agent.getIndex();
		species = agent.getSpeciesName();
		source = agent.getScope().getExperiment().getSpeciesName();
		alias = ((ModelDescription) agent.getScope().getSimulation().getSpecies().getDescription()).getAlias();
		uniqueID = agent.getUniqueID();
		saveAttributes(scope, agent);
		if (agent instanceof IMacroAgent) {
			saveMicroAgents(scope, (IMacroAgent) agent);
		}
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param ind the ind
	 * @param v the v
	 * @param inPop the in pop
	 */
	public SavedAgent(final int ind, final Map<String, Object> v, final Map<String, List<SavedAgent>> inPop) {
		this(v, inPop);
		index = ind;
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param v the v
	 * @param inPop the in pop
	 */
	public SavedAgent(final Map<String, Object> v, final Map<String, List<SavedAgent>> inPop) {
		super(v.size(), Types.STRING, Types.NO_TYPE);
		putAll(v);
		innerPopulations = inPop;
	}

	/**
	 * Instantiates a new saved agent.
	 *
	 * @param map the map
	 */
	public SavedAgent(final IMap<String, Object> map) {
		this();
		putAll(map);
	}

	/**
	 * Gets the attribute value.
	 *
	 * @param attrName the attr name
	 * @return the attribute value
	 */
	public Object getAttributeValue(final String attrName) {
		return get(attrName);
	}

	/**
	 * Gets the variables.
	 *
	 * @return the variables
	 */
	public Map<String, Object> getVariables() {
		return this;
	}

	/**
	 * Gets the inner populations.
	 *
	 * @return the inner populations
	 */
	public Map<String, List<SavedAgent>> getInnerPopulations() {
		return innerPopulations;
	}

	/**
	 * Gets the index.
	 *
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Set the species.
	 */
	public void setSpecies(String spcs) {
		species = spcs;
	}
	
	/**
	 * Gets the species.
	 *
	 * @return the species
	 */
	 public String getSpecies() {
		return species;
	}
	
	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	 public String getAlias(){
		return alias;
	}
	 
	 /**
	 * Set the alias.
	 */
	public void setAlias(String als) {
		alias = als;
	}
	
	/**
	 * Gets the source.
	 *
	 * @return the source
	 */
	 public String getSource(){
		return source;
	}
	 
	 /**
	 * Set the source.
	 */
	public void setSource(String src) {
		source = src;
	}

	/**
	 * Gets the hashcode.
	 *
	 * @return the hashcode
	 */
	 public int getUniqueID(){
		return uniqueID;
	}
	 
	 /**
	 * Set the hashcode.
	 */
	public void setUniqueID(int uID) {
		uniqueID = uID;
	}
	
	/**
	 * Saves agent's attributes to a map.
	 *
	 * @param agent
	 * @throws GamaRuntimeException
	 */
	private void saveAttributes(final IScope scope, final IAgent agent) throws GamaRuntimeException {
		final ISpecies species = agent.getSpecies();

		System.out.println("begin of specVar");
		for (final String specVar : species.getVarNames()) 
		{
			System.out.println("specVar = "+specVar);
		}
		System.out.println("end of specVar");
		for (final String specVar : species.getVarNames()) {
			
			if (UNSAVABLE_VARIABLES.contains(specVar)) {
				continue;
			}

			if (species.getVar(specVar).value(scope, agent) instanceof IPopulation) {
				continue;
			}

			if (specVar.equals(IKeyword.SHAPE)) {
				// variables.put(specVar, geometry.copy());
				// Changed 3/2/12: is it necessary to make the things below ?
				// variables.put(specVar,
				// new GamaShape(agent.getGeometry()));
				// Changed 26/5/16: additional variables exist in the shape. To
				// keep them in the SavedAgent, we add them by hand in the
				// geometry.
				// We cannot keep all the GamaShape, because it contains
				// populations too.
				final GamaShape shape =
						new GamaShape(((GamaShape) species.getVar(specVar).value(scope, agent)).getInnerGeometry());

				// if (agent.getAttributes() != null) {}

				agent.forEachAttribute((attrName, val) -> {
					if (UNSAVABLE_VARIABLES.contains(attrName)) { return true; }
					if (species.getVarNames().contains(attrName)) { return true; }
					if (val instanceof IPopulation) { return true; }
					shape.setAttribute(attrName, val);
					return true;
				});
				// for (final String keyAttr : agent.getAttributes().keySet()) {
				// final String attrName = keyAttr;
				// if (UNSAVABLE_VARIABLES.contains(attrName)) {
				// continue;
				// }
				// if (species.getVarNames().contains(attrName)) {
				// continue;
				// }
				//
				// if (agent.getAttribute(keyAttr) instanceof IPopulation) {
				// continue;
				// }
				// shape.setAttribute(attrName, agent.getAttribute(keyAttr));
				// }

				put(specVar, shape);

				continue;
			}
			put(specVar, species.getVar(specVar).value(scope, agent));
		}

		if(agent.getUniqueID() != 0)
		{
			put("uniqueID", agent.getUniqueID());
		}
	}

	/**
	 * Recursively save micro-agents of an agent.
	 *
	 * @param agent
	 *            The agent having micro-agents to be saved.
	 * @throws GamaRuntimeException
	 */
	private void saveMicroAgents(final IScope scope, final IMacroAgent agent) throws GamaRuntimeException {
		innerPopulations = GamaMapFactory.createUnordered();

		for (final IPopulation<? extends IAgent> microPop : agent.getMicroPopulations()) {
			final List<SavedAgent> savedAgents = new ArrayList<>();
			final Iterator<? extends IAgent> it = microPop.iterator();
			while (it.hasNext()) {
				savedAgents.add(new SavedAgent(scope, it.next()));
			}

			innerPopulations.put(microPop.getSpecies().getName(), savedAgents);
		}
	}

	/**
	 * @param scope
	 *            Restores the saved agent as a member of the target population.
	 *
	 * @param targetPopulation
	 *            The population that the saved agent will be restored to.
	 * @return
	 * @throws GamaRuntimeException
	 */
	public IAgent restoreTo(final IScope scope, final IPopulation<? extends IAgent> targetPopulation)
			throws GamaRuntimeException {
		final List<Map<String, Object>> agentAttrs = new ArrayList<>();
		agentAttrs.add(this);
		agentAttrs.get(0).put("uniqueID", this.getUniqueID());
		for(var auto : agentAttrs)
		{
			System.out.println("auto = "+auto.toString());
			for(var auto2 : auto.entrySet())
			{
				System.out.println(auto2.getKey() + " = "+auto2.getValue());
			}
		}
		final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true, true);
		restoreMicroAgents(scope, restoredAgents.get(0));

		return restoredAgents.get(0);
	}
	
	/**
	 * @param scope
	 *            Restores the saved agent as a member of the target population.
	 *
	 * @param targetPopulation
	 *            The population that the saved agent will be restored to.
	 * @return
	 * @throws GamaRuntimeException
	 */
	public IAgent restoreToMPI(final IScope scope, final IPopulation<? extends IAgent> targetPopulation, int uniqueID)
			throws GamaRuntimeException {
		System.out.println("RESTORING NEW AGENTS WITH UNIQUEID = "+uniqueID);
		final List<Map<String, Object>> agentAttrs = new ArrayList<>();
		agentAttrs.add(this);
		for(var auto : agentAttrs)
		{
			System.out.println("auto = "+auto.toString());
			for(var auto2 : auto.entrySet())
			{
				System.out.println(auto2.getKey() + " = "+auto2.getValue());
			}
		}
		
		IAgent agentCopy = targetPopulation.stream().filter(agent -> agent.getUniqueID() == uniqueID).findAny().orElse(null);
		List<Integer> uniqueIDList = targetPopulation.stream().map(a -> a.getUniqueID()).collect(Collectors.toList());
		for(var auto : uniqueIDList)
		{
			System.out.println("XXXX = "+auto);
		}
		if(agentCopy != null)
		{	
			System.out.println("FOUUUUUUUND this agent = "+agentCopy);
			return agentCopy;
		}
		final List<? extends IAgent> restoredAgents = targetPopulation.createAgents(scope, 1, agentAttrs, true, true);
		restoreMicroAgents(scope, restoredAgents.get(0));

		return restoredAgents.get(0);
	}

	/**
	 *
	 *
	 * @param host
	 * @throws GamaRuntimeException
	 */
	public void restoreMicroAgents(final IScope scope, final IAgent host) throws GamaRuntimeException {
		System.out.println("restoreMicroAgents restoreMicroAgentsrestoreMicroAgentsrestoreMicroAgents");
		// todo check unique ID of all the agents in the pop
		if (innerPopulations != null) {
			for (final String microPopName : innerPopulations.keySet()) {
				final IPopulation<? extends IAgent> microPop = ((IMacroAgent) host).getMicroPopulation(microPopName);

				if (microPop != null) {
					final List<SavedAgent> savedMicros = innerPopulations.get(microPopName);
					List<Integer> uniqueIDList = savedMicros.stream().map(a -> a.getUniqueID()).collect(Collectors.toList());
					for(var auto : uniqueIDList)
					{
						System.out.println("UNQIUEDDIIDIDIDDIID = "+auto);
					}
					if(uniqueIDList.contains(host.getUniqueID()))
					{
						System.out.println("Micro Agent already created in the simulation");
						return;
					}
					for(SavedAgent sa : savedMicros)
					{
						if(sa.getUniqueID() == host.getUniqueID())
						{
							return;
						}
					}
					final List<Map<String, Object>> microAttrs = new ArrayList<>();
					for (final SavedAgent sa : savedMicros) {
						microAttrs.add(sa);
					}

					final List<? extends IAgent> microAgents =
							microPop.createAgents(scope, savedMicros.size(), microAttrs, true, true);

					for (int i = 0; i < microAgents.size(); i++) {
						savedMicros.get(i).restoreMicroAgents(scope, microAgents.get(i));
					}
				}else
				{
					System.out.println("microPop ================== null");
				}
			}
		}else
		{
			System.out.println("innerPopulations ================== null");
		}
	}
}




