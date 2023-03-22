package proxyPopulation;

import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.EMPTY_MAP;
import static msi.gama.common.interfaces.IKeyword.LOCATION;
import static msi.gama.common.interfaces.IKeyword.SHAPE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import proxy.ProxyAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.agent.IMacroAgent;
import msi.gama.metamodel.population.GamaPopulation;
import msi.gama.metamodel.population.IPopulation;
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

public class ProxyPopulation extends GamaPopulation<ProxyAgent>
{
	private static final long serialVersionUID = 1L;
	
	Map<Integer, ProxyAgent> hashMapProxyID;
	
	public ProxyPopulation(IMacroAgent host, ISpecies species) 
	{
		super(host, species);
		hashMapProxyID = new HashMap<Integer, ProxyAgent>();
	}
	
	public IList<ProxyAgent> createAgents(final IScope scope, final int number,
			final List<? extends Map<String, Object>> initialValues, final boolean isRestored,
			final boolean toBeScheduled, final RemoteSequence sequence) throws GamaRuntimeException 
	{
		if (number == 0) return GamaListFactory.EMPTY_LIST;
	
		final IList<IAgent> agentList = GamaListFactory.create(getGamlType().getContentType(), number);
		
		final IAgentConstructor<IAgent> constr = species.getDescription().getAgentConstructor();
		
		for (int i = 0; i < number; i++) 
		{
			@SuppressWarnings ("unchecked") final IAgent agent = constr.createOneAgent(this, currentAgentIndex++);
			if (initialValues != null && !initialValues.isEmpty()) 
			{
				final Map<String, Object> init = initialValues.get(i);
				if (init.containsKey(SHAPE)) 
				{
					final Object val = init.get(SHAPE);
					if (val instanceof GamaPoint) 
					{
						agent.setGeometry(new GamaShape((GamaPoint) val));
					} else 
					{
						agent.setGeometry((IShape) val);
					}
					init.remove(SHAPE);
				} else if (init.containsKey(LOCATION)) 
				{
					agent.setLocation(scope, (GamaPoint) init.get(LOCATION));
					init.remove(LOCATION);
				}
			}
			agentList.add(agent);
		}
		createVariablesForProxiedAgent(scope, agentList, initialValues, sequence);
		
		return createProxys(agentList, scope, sequence);
	}
	
	
	@Override
	public ProxyAgent createAgentAt(final IScope scope, final int index, final Map<String, Object> initialValues,
			final boolean isRestored, final boolean toBeScheduled) throws GamaRuntimeException 
	{
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
	
		final IList<IAgent> agentList = GamaListFactory.create(getGamlType().getContentType(), number);
		final IAgentConstructor<IAgent> constr = species.getDescription().getAgentConstructor();
		
		for (final IShape geom : geometries.iterable(scope)) 
		{
			final IAgent agent = constr.createOneAgent(this, currentAgentIndex++);
			agent.setGeometry(geom);
			agentList.add(agent);
		}
		
		createVariablesForProxiedAgent(scope, agentList, EMPTY_LIST, null);
		
		return createProxys(agentList, scope, null);
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
	private IList<ProxyAgent> createProxys(IList<IAgent> agentList, IScope scope, RemoteSequence sequence)
	{
		final IList<ProxyAgent> proxyList = GamaListFactory.create(getGamlType().getContentType(), agentList.size());
		for (final IAgent agent : agentList) {
			ProxyAgent proxy = new ProxyAgent(agent, agent.hashCode());
			proxyList.add(proxy);
			
			hashMapProxyID.put(agent.hashCode(), proxy);		
		}
		
		scheduleProxy(proxyList, scope, sequence);
		addAll(proxyList);
		fireAgentsAdded(scope, proxyList);
		
		return proxyList;
	}
	
	/**
	 * 
	 * Schedule the Proxy AGENT
	 * 
	 * @param proxyList : list of Proxy to schedule
	 * @param scope
	 * @param sequence
	 */
	private void scheduleProxy(IList<ProxyAgent> proxyList, IScope scope, RemoteSequence sequence)
	{
		for (final ProxyAgent proxy : proxyList) 
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
	public void createVariablesForProxiedAgent(final IScope scope, final List<IAgent> agents,
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
	protected void fireAgentRemoved(final IScope scope, final IAgent agent) {
		try {
			ProxyAgent proxy = getProxyFromID(agent.hashCode());
			this.remove(proxy);
			
			// TODO if not dead but just removed (in case of migration of agent)
			// do something with the proxy (change the ref to a distant agent for example)
			
		} catch (final RuntimeException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get proxy linked to the agent with the ID uniqueID 
	 * 
	 * @param uniqueID
	 * @return
	 */
	protected ProxyAgent getProxyFromID(int uniqueID)
	{	
		ProxyAgent proxy = this.hashMapProxyID.get(uniqueID);
		if(proxy != null)
		{
			return proxy;
		}
		return null;
	}

}
