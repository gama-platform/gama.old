/*******************************************************************************************************
 *
 * PhysicalSimulationAgent.java, in simtools.gaml.extensions.physics, is part of the source code of the GAMA modeling
 * and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 *
 ********************************************************************************************************/
package gama.extensions.physics.gaml;

import java.util.Collection;

import gama.extensions.physics.NativeLoader;
import gama.extensions.physics.box2d_version.Box2DPhysicalWorld;
import gama.extensions.physics.common.IPhysicalConstants;
import gama.extensions.physics.common.IPhysicalWorld;
import gama.extensions.physics.java_version.BulletPhysicalWorld;
import gama.extensions.physics.native_version.NativeBulletPhysicalWorld;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.Collector;
import msi.gama.util.Collector.AsOrderedSet;
import msi.gama.util.IList;
import msi.gama.util.matrix.IField;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * A simulation agent that provides physical capabilities to simulations, in particular the possiblity to register and
 * manage agents that implement the 'static_body' / 'dynamic_body' skill/ This class is a gateway to a
 * JBullet/Bullet/Box2D dynamics "world" that combines collision detection and physics either in 3D (for the Bullet
 * libraries) or in 2D (Box2D). Serves as the entry point of physical simulations in GAMA and conveys to the agents the
 * events that occur in the physical world (contacts, mainly). Three libraries are provided. Two in Java (JBullet &
 * Box2D) in 3D and 2D, a bit outdated and one native (LibBulletJME) always up to date. The modeler can choose which one
 * to use by setting the value of 'use_native_library' to true or false
 *
 * @author Alexis Drogoul 2021 (remotely based on the work of Javier Gil-Quijano - Arnaud Grignard - (2012 Gama Winter
 *         School))
 */
@species (
		name = IPhysicalConstants.PHYSICAL_WORLD,
		skills = IPhysicalConstants.STATIC_BODY)
@doc ("The base species for models that act as a 3D physical world. Can register and manage agents provided with either the '"
		+ IPhysicalConstants.STATIC_BODY + "' or '" + IPhysicalConstants.DYNAMIC_BODY + "' skill. Inherits from '"
		+ IPhysicalConstants.STATIC_BODY + "', so it can also act as a physical body itself (with a '"
		+ IPhysicalConstants.MASS + "', '" + IPhysicalConstants.FRICTION + "', '" + IPhysicalConstants.GRAVITY
		+ "'), of course without motion -- in this case, it needs to register itself as a physical agent using the '"
		+ IPhysicalConstants.REGISTER + "' action")
@vars ({ @variable (
		name = IPhysicalConstants.GRAVITY,
		type = IType.POINT,
		init = "{0,0,-9.80665}",
		doc = @doc ("Defines the value of gravity in this world. The default value is set to -9.80665 on the z-axis, that is 9.80665 m/s2 towards the 'bottom' of the world. Can be set to any direction and intensity and applies to all the bodies present in the physical world")),
		@variable (
				name = IPhysicalConstants.AUTOMATED_REGISTRATION,
				type = IType.BOOL,
				init = "true",
				doc = @doc ("If set to true (the default), makes the world automatically register and unregister agents provided with either the '"
						+ IPhysicalConstants.STATIC_BODY + "' or '" + IPhysicalConstants.DYNAMIC_BODY
						+ "' skill. Otherwise, they must be registered using the '" + IPhysicalConstants.REGISTER
						+ "' action, which can be useful when only some agents need to be considered as 'physical agents'. Note that, in any case, the world needs to manually register itself if it is supposed to act as a physical body. ")),
		@variable (
				name = IPhysicalConstants.MAX_SUBSTEPS,
				type = IType.INT,
				init = "0",
				doc = @doc ("If equal to 0 (the default), makes the simulation engine be stepped alongside the simulation (no substeps allowed). Otherwise, sets the maximum number of physical simulation substeps that may occur within one GAMA simulation step")),
		@variable (
				name = IPhysicalConstants.TERRAIN,
				type = IType.FIELD,
				doc = { @doc ("This attribute is a matrix of float that can be used to represent a 3D terrain. The shape of the world, in that case, should be a box, where the"
						+ "dimension on the z-axis is used to scale the z-values of the DEM. The world needs to be register itself as a physical object") }),
		@variable (
				name = IPhysicalConstants.USE_NATIVE,
				type = IType.BOOL,
				doc = { @doc ("This attribute allows to manually switch between the Java version of the Bullet library (JBullet, a modified version of https://github.com/stephengold/jbullet, which corresponds to version 2.72 of the original library) and the native Bullet library (Libbulletjme, https://github.com/stephengold/Libbulletjme, which is kept up-to-date with the 3.x branch of the original library)."
						+ "The native version is the default one unless the libraries cannot be loaded, making JBullet the default") }),
		@variable (
				name = IPhysicalConstants.LIBRARY_NAME,
				type = IType.STRING,
				doc = { @doc ("This attribute allows to manually switch between two physics library, named 'bullet' and 'box2D'. The Bullet library, which comes in two flavors (see 'use_native') and the Box2D libray in its Java version (https://github.com/jbox2d/jbox2d). "
						+ "Bullet is the default library but models in 2D should better use Box2D") }),

		@variable (
				name = IPhysicalConstants.ACCURATE_COLLISION_DETECTION,
				type = IType.BOOL,
				init = "false",
				doc = @doc ("Enables or not a better (but slower) collision detection ")) })
public class PhysicalSimulationAgent extends SimulationAgent implements IPhysicalConstants {

	/** The population listener. */
	final BodyPopulationListener populationListener = new BodyPopulationListener();

	/** The ccd. */
	Boolean ccd = false;

	/** The automated registration. */
	Boolean automatedRegistration = true;

	/** The gravity. */
	final GamaPoint gravity = new GamaPoint(0, 0, -9.81d);

	/** The terrain. */
	IField terrain;

	/** The gateway. */
	IPhysicalWorld gateway;

	/** The use native library. */
	Boolean useNativeLibrary = NativeLoader.NATIVE_BULLET_LIBRARY_LOADED;

	/** The library to use. */
	String libraryToUse = BULLET_LIBRARY_NAME;

	/** The registered agents. */
	private final AsOrderedSet<IAgent> registeredAgents = Collector.getOrderedSet();

	/** The max sub steps. */
	private int maxSubSteps;

	/**
	 * Instantiates a new physical simulation agent.
	 *
	 * @param s
	 *            the s
	 * @param index
	 *            the index
	 * @throws GamaRuntimeException
	 *             the gama runtime exception
	 */
	public PhysicalSimulationAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
	}

	/**
	 * Prim register.
	 *
	 * @param scope
	 *            the scope
	 * @return the object
	 */
	@action (
			doc = @doc ("An action that allows to register agents in this physical world. Unregistered agents will not be governed by the physical laws of this world. If the world is to play a role in the physical world,"
					+ "then it needs to register itself (i.e. do register([self]);"),
			name = REGISTER,
			args = { @arg (
					doc = @doc ("the list or container of agents to register in this physical world"),
					name = BODIES,
					type = IType.CONTAINER) })

	public Object primRegister(final IScope scope) {
		IList<IAgent> agents = scope.getListArg(BODIES);
		if (agents == null) return null;
		for (IAgent agent : agents) {
			registerAgent(scope, agent);
		}
		return agents;
	}

	/**
	 * Register agent.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 */
	private void registerAgent(final IScope scope, final IAgent agent) {
		if (registeredAgents.add(agent)) { getGateway().registerAgent(agent); }
	}

	/**
	 * Unregister agent.
	 *
	 * @param scope
	 *            the scope
	 * @param agent
	 *            the agent
	 */
	private void unregisterAgent(final IScope scope, final IAgent agent) {
		if (registeredAgents.remove(agent)) { getGateway().unregisterAgent(agent); }
	}

	/**
	 * Called whenever an agent wants to update its body
	 *
	 * @param scope
	 * @param agent
	 */
	public void updateAgent(final IScope scope, final IAgent agent) {
		getGateway().updateAgentShape(agent);
	}

	/**
	 * Gets the terrain.
	 *
	 * @return the terrain
	 */
	@getter (IPhysicalConstants.TERRAIN)
	public IField getTerrain() {
		return terrain;
	}

	/**
	 * Sets the terrain.
	 *
	 * @param t
	 *            the new terrain
	 */
	@setter (IPhysicalConstants.TERRAIN)
	public void setTerrain(final IField t) {
		terrain = t;
	}

	/**
	 * Gets the ccd.
	 *
	 * @param scope
	 *            the scope
	 * @return the ccd
	 */
	@getter (
			value = ACCURATE_COLLISION_DETECTION,
			initializer = true)
	public Boolean getCCD(final IScope scope) {
		return ccd;
	}

	/**
	 * Sets the CCD.
	 *
	 * @param scope
	 *            the scope
	 * @param v
	 *            the v
	 */
	@setter (ACCURATE_COLLISION_DETECTION)
	public void setCCD(final IScope scope, final Boolean v) {
		ccd = v;
		// Dont provoke the instantiation of the gateway yet if it is null
		if (gateway != null) { gateway.setCCD(v); }
	}

	/**
	 * Gets the automated registration.
	 *
	 * @param scope
	 *            the scope
	 * @return the automated registration
	 */
	@getter (
			value = AUTOMATED_REGISTRATION,
			initializer = true)
	public Boolean getAutomatedRegistration(final IScope scope) {
		return automatedRegistration;
	}

	/**
	 * Sets the automated registration.
	 *
	 * @param scope
	 *            the scope
	 * @param v
	 *            the v
	 */
	@setter (AUTOMATED_REGISTRATION)
	public void setAutomatedRegistration(final IScope scope, final Boolean v) {
		automatedRegistration = v;
	}

	/**
	 * Uses native library.
	 *
	 * @param scope
	 *            the scope
	 * @return the boolean
	 */
	@getter (
			value = USE_NATIVE,
			initializer = true)
	public Boolean usesNativeLibrary(final IScope scope) {
		if (useNativeLibrary == null) { useNativeLibrary = NativeLoader.LoadNativeLibrary(); }
		return useNativeLibrary;
	}

	/**
	 * Use native library.
	 *
	 * @param scope
	 *            the scope
	 * @param v
	 *            the v
	 */
	@setter (USE_NATIVE)
	public void useNativeLibrary(final IScope scope, final Boolean v) {
		// If we have not successfully loaded the library, then the setting should remain false.
		useNativeLibrary = NativeLoader.LoadNativeLibrary() && v;
	}

	/**
	 * Library to use.
	 *
	 * @param scope
	 *            the scope
	 * @return the string
	 */
	@getter (
			value = LIBRARY_NAME,
			initializer = true)
	public String libraryToUse(final IScope scope) {
		return libraryToUse;
	}

	/**
	 * Library to use.
	 *
	 * @param scope
	 *            the scope
	 * @param v
	 *            the v
	 */
	@setter (LIBRARY_NAME)
	public void libraryToUse(final IScope scope, final String v) {
		libraryToUse = v;
	}

	/**
	 * Gets the max sub steps.
	 *
	 * @param scope
	 *            the scope
	 * @return the max sub steps
	 */
	@getter (
			value = MAX_SUBSTEPS,
			initializer = true)
	public int getMaxSubSteps(final IScope scope) {
		return maxSubSteps;
	}

	/**
	 * Sets the max sub steps.
	 *
	 * @param scope
	 *            the scope
	 * @param steps
	 *            the steps
	 */
	@setter (MAX_SUBSTEPS)
	public void setMaxSubSteps(final IScope scope, final int steps) {
		maxSubSteps = steps;
	}

	/**
	 * Gets the gravity.
	 *
	 * @param scope
	 *            the scope
	 * @return the gravity
	 */
	@getter (
			value = GRAVITY,
			initializer = true)
	public GamaPoint getGravity(final IScope scope) {
		return gravity;
	}

	/**
	 * Sets the gravity.
	 *
	 * @param scope
	 *            the scope
	 * @param g
	 *            the g
	 */
	@setter (GRAVITY)
	public void setGravity(final IScope scope, final GamaPoint g) {
		this.gravity.setLocation(g);
		// Dont provoke the instantiation of the gateway yet if it is null
		if (gateway != null) { gateway.setGravity(g); }
	}

	@Override
	public void dispose() {
		getGateway().dispose();
		registeredAgents.clear();
		super.dispose();
	}

	@Override
	protected void registerMicropopulation(final IScope scope, final ISpecies species,
			final IPopulation<? extends IAgent> pop) {

		if (species.implementsSkill(DYNAMIC_BODY) || species.implementsSkill(STATIC_BODY)) {
			pop.addListener(populationListener);
		}
		super.registerMicropopulation(scope, species, pop);
	}

	@Override
	public boolean doStep(final IScope scope) {
		if (super.doStep(scope)) {
			final Double timeStep = getTimeStep(scope);
			getGateway().doStep(timeStep, maxSubSteps);
			return true;
		}
		return false;
	}

	/**
	 * Gets the gateway.
	 *
	 * @return the gateway
	 */
	IPhysicalWorld getGateway() {
		if (gateway == null) {
			boolean isBullet = BULLET_LIBRARY_NAME.equals(libraryToUse);
			if (isBullet) {
				if (useNativeLibrary) {
					gateway = new NativeBulletPhysicalWorld(this);
				} else {
					gateway = new BulletPhysicalWorld(this);
				}
			} else {
				gateway = new Box2DPhysicalWorld(this);
			}
		}
		return gateway;
	}

	/**
	 * The listener interface for receiving bodyPopulation events. The class that is interested in processing a
	 * bodyPopulation event implements this interface, and the object created with that class is registered with a
	 * component using the component's <code>addBodyPopulationListener<code> method. When the bodyPopulation event
	 * occurs, that object's appropriate method is invoked.
	 *
	 * @see BodyPopulationEvent
	 */
	class BodyPopulationListener implements IPopulation.Listener {

		@Override
		public void notifyAgentRemoved(final IScope scope, final IPopulation<? extends IAgent> pop,
				final IAgent agent) {
			unregisterAgent(scope, agent);
		}

		@Override
		public void notifyAgentAdded(final IScope scope, final IPopulation<? extends IAgent> pop, final IAgent agent) {
			if (automatedRegistration) { registerAgent(scope, agent); }
		}

		@Override
		public void notifyAgentsAdded(final IScope scope, final IPopulation<? extends IAgent> pop,
				final Collection<? extends IAgent> agents) {
			if (scope.interrupted()) return;

			if (automatedRegistration) {
				for (IAgent a : agents) {
					registerAgent(scope, a);
				}
			}
		}

		@Override
		public void notifyAgentsRemoved(final IScope scope, final IPopulation<? extends IAgent> pop,
				final Collection<? extends IAgent> agents) {
			if (scope.interrupted()) return;

			for (IAgent a : agents) {
				unregisterAgent(scope, a);
			}
		}

		@Override
		public void notifyPopulationCleared(final IScope scope, final IPopulation<? extends IAgent> pop) {
			if (scope.interrupted()) return;
			for (IAgent a : pop) {
				unregisterAgent(scope, a);
			}

		}

	}

}
