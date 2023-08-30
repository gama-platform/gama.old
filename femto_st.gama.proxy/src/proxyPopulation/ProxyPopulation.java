package proxyPopulation;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.util.RandomUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.agent.MinimalAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.metamodel.shape.GamaShape;
import msi.gama.metamodel.shape.IShape;
import msi.gama.runtime.FlowStatus;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaListFactory;
import msi.gama.util.IContainer;
import msi.gama.util.IList;
import msi.gaml.compilation.IAgentConstructor;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.RemoteSequence;
import msi.gaml.variables.IVariable;
import proxy.ProxyAgent;
import ummisco.gama.dev.utils.DEBUG;

/**
 * Population of Proxy Agent
 * 
 * @author Lucas Grosjean
 *
 */
@SuppressWarnings("serial")
public class ProxyPopulation extends GamaPopulation<ProxyAgent>
{
	
	// TODO implements all methods 
	
	static
	{
		DEBUG.OFF();
	}
	
	Map<Integer, ProxyAgent> hashMapProxyID;
	
	public ProxyPopulation(IMacroAgent host, ISpecies species) 
	{
		super(host, species);
		hashMapProxyID = new HashMap<Integer, ProxyAgent>();
	}
	
	@Override
	public IList<ProxyAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
			final boolean toBeScheduled, final RemoteSequence sequence) throws GamaRuntimeException
	{
		if (number == 0) return GamaListFactory.EMPTY_LIST;
		
		final IList<MinimalAgent> agentList = GamaListFactory.create(getGamlType().getContentType(), number);
		final IAgentConstructor<IAgent> constr = species.getDescription().getAgentConstructor();
		
		for (int i = 0; i < number; i++) 
		{
			IShape shape;
			@SuppressWarnings ("unchecked") final IAgent agent = constr.createOneAgent(this, currentAgentIndex++);
			int hashcode = 0;
			if (initialValues != null && !initialValues.isEmpty()) 
			{
				final Map<String, Object> init = initialValues.get(i);
				if (init.containsKey(IKeyword.HASHCODE)) 
				{
					hashcode = (Integer) init.get(IKeyword.HASHCODE);
				}
				
				if (init.containsKey(IKeyword.SHAPE)) 
				{
					final Object val = init.get(IKeyword.SHAPE);
					if (val instanceof GamaPoint) 
					{
						agent.setGeometry(new GamaShape((GamaPoint) val));
					} else 
					{
						agent.setGeometry((IShape) val);
					}
					init.remove(IKeyword.SHAPE);
				} else if (init.containsKey(IKeyword.LOCATION)) 
				{
					agent.setLocation(scope, (GamaPoint) init.get(IKeyword.LOCATION));
					init.remove(IKeyword.LOCATION);
				}
			}
			MinimalAgent minimal;
			if(hashcode != 0)
			{
				 minimal = new MinimalAgent(agent.getPopulation(), agent.getIndex(), hashcode, agent.getGeometry()); // have to create minimal agent here to change hashcode
				 agentList.add(minimal);
			}else
			{
				agentList.add((MinimalAgent)agent); // no hashcode in the attributes
				DEBUG.OUT("NO HASHCODE DETECTED");
			}
		}
		createVariablesForProxiedAgent(scope, agentList, initialValues, sequence);
		
		return createProxys(agentList, scope, sequence, isRestored, toBeScheduled);
	}
	
	@Override
	public ProxyAgent createAgentAt(final IScope scope, final int index, final Map<String, Object> initialValues,
			final boolean isRestored, final boolean toBeScheduled) throws GamaRuntimeException 
	{		
		DEBUG.OUT("createAgentAt");
		final List<Map<String, Object>> mapInitialValues = new ArrayList<>();
		mapInitialValues.add(initialValues);

		final int tempIndexAgt = currentAgentIndex;

		currentAgentIndex = index;
		final IList<ProxyAgent> proxyList = createAgents(scope, 1, mapInitialValues, isRestored, toBeScheduled, null);
		currentAgentIndex = tempIndexAgt;

		return proxyList.firstValue(scope);
	}

	@Override
	public IList<ProxyAgent> createAgents(final IScope scope, final IContainer<?, ? extends IShape> geometries) {
		final int number = geometries.length(scope);
		
		if (number == 0) return GamaListFactory.EMPTY_LIST;
	
		final IList<MinimalAgent> agentList = GamaListFactory.create(getGamlType().getContentType(), number);
		final IAgentConstructor<IAgent> constr = species.getDescription().getAgentConstructor();
		
		for (final IShape geom : geometries.iterable(scope)) 
		{
			final IAgent agent = constr.createOneAgent(this, currentAgentIndex++);
			agent.setGeometry(geom);
			agentList.add((MinimalAgent)agent);
		}
		
		createVariablesForProxiedAgent(scope, agentList, EMPTY_LIST, null);
		
		return createProxys(agentList, scope, null, false, false);
	}
	
	/**
	 * 
	 * Create proxys
	 * 
	 * @param agentList :  list of agent to create proxys for
	 * @param scope
	 * @param sequence
	 * @return
	 */
	private IList<ProxyAgent> createProxys(IList<MinimalAgent> agentList, IScope scope, RemoteSequence sequence, boolean isRestored, boolean isScheduled )
	{
		final IList<ProxyAgent> proxyList = GamaListFactory.create(getGamlType().getContentType(), agentList.size());
		for (final MinimalAgent agent : agentList) {
			ProxyAgent proxy = new ProxyAgent(agent, this, scope);
			proxyList.add(proxy);
			DEBUG.OUT("New agent(" + agent.getName() + ") hashcode : " + agent.hashCode);
			hashMapProxyID.put(agent.hashCode, proxy);		
		}
		
		scheduleProxy(proxyList, scope, sequence, isRestored);
		this.addAll(proxyList);
		fireAgentsAdded(scope, proxyList);
		
		return proxyList;
	}
	
	/**
	 * 
	 * Schedule the Proxy Agent
	 * 
	 * @param proxyList : list of Proxy to schedule
	 * @param scope
	 * @param sequence
	 */
	private void scheduleProxy(IList<ProxyAgent> proxyList, IScope scope, RemoteSequence sequence, boolean isRestored)
	{
		if (!isRestored) {
			for(final ProxyAgent proxy : proxyList) 
			{
				proxy.schedule(scope);
			}
			
			if (sequence != null && !sequence.isEmpty()) {
				for (final IAgent proxy : proxyList) {
					if (!scope.execute(sequence, proxy, null).passed()
							|| scope.getAndClearBreakStatus() == FlowStatus.BREAK) {
						break;
					}
				}
			}
		}
		fireAgentsAdded(scope, proxyList);
	}
	
	/**
	 * Creates the variables for proxied agent.
	 *
	 * @param scope
	 *            the scope
	 * @param agents
	 *            the agents
	 * @param initialValues
	 *            the initial values
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	@SuppressWarnings ("null")
	// TODO duplicate from createVariablesFor from GamaPopulation
	public void createVariablesForProxiedAgent(final IScope scope, final List<MinimalAgent> agents,
			final List<? extends Map<String, Object>> initialValues, RemoteSequence remote) throws GamaRuntimeException 
	{
		if (agents == null || agents.isEmpty()) return;
		final boolean empty = initialValues == null || initialValues.isEmpty();
		
		Map<String, Object> inits;
		for (int i = 0, n = agents.size(); i < n; i++) {
			final IAgent a = agents.get(i);
			inits = empty ? EMPTY_MAP : initialValues.get(i);
			for (final IVariable var : orderedVars) {
				final Object initGet =
						empty || !allowVarInitToBeOverridenByExternalInit(var) ? null : inits.get(var.getName());
				var.initializeWith(scope, a, initGet);
			}
			// Added to fix #3266 -- saves the values of the "extra" attributes found in the files
			if (!empty) {
				inits.forEach((name, v) -> { if (!orderedVarNames.contains(name)) { a.setAttribute(name, v); } });
			}
		}
	}

	@Override
	public void fireAgentRemoved(final IScope scope, final IAgent agent) {
		try {
			if(agent instanceof ProxyAgent)
			{
				ProxyAgent proxy = (ProxyAgent) agent;
				this.remove(proxy);
			}else
			{
				ProxyAgent proxy = getProxyFromHashCode(((MinimalAgent)agent).hashCode);
				this.remove(proxy);

			}
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get proxy linked to the agent with the given hashcode
	 * 
	 * @param hashcode
	 * @return
	 */
	public ProxyAgent getProxyFromHashCode(int hashcode)
	{	
		ProxyAgent proxy = this.hashMapProxyID.get(hashcode);
		
		DEBUG.OUT("Proxy hashcode in the population :: ");
		for(var auto : this.hashMapProxyID.entrySet())
		{
			DEBUG.OUT(auto.getKey() + " :: " + auto.getKey());
		}
		DEBUG.OUT("proxy from hashcode(" + hashcode + ") : " + proxy);
		
		return proxy;
	}
	
	public Map<Integer, ProxyAgent> getMapProxyID()
	{
		return this.hashMapProxyID;
	}
	
	@Override
	public ProxyAgent anyValue(final IScope scope) 
	{	
		final RandomUtils r = scope.getRandom();
		List<Integer> keysAsArray = new ArrayList<Integer>(hashMapProxyID.keySet());
		DEBUG.OUT("keysAsArray" + " :: " + keysAsArray.size());
		
		for(var auto : this.hashMapProxyID.entrySet())
		{
			DEBUG.OUT("agents in map Proxy" + " :: " + auto.getKey() + " :: " + auto.getValue());
		}
		if(keysAsArray.size() > 0)
		{
			var auto = hashMapProxyID.get(keysAsArray.get(r.between(0, keysAsArray.size() - 1)));
			DEBUG.OUT("returning  : " + auto);
			
			return auto;
		}
		
		return null;
	}
}
