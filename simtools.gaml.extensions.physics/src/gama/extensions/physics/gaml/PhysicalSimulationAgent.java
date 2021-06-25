/*********************************************************************************************
 *
 *
 * 'Physical3DWorldAgent.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the GAMA
 * modeling and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.extensions.physics.gaml;

import java.util.Collection;

import gama.extensions.physics.BulletActivator;
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
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * A simulation agent that provides physical capabilities to simulations, in particular the possiblity to register and
 * manage agents that implement the 'static_body' / 'dynamic_body' skill/ This class is a gateway to a JBullet/Bullet
 * dynamics "world" that combines collision detection and physics. Serves as the entry point of physical simulations in
 * GAMA and conveys to the agents the events that occur in the physical world (contacts, mainly). Two libraries are
 * provided. One in Java (JBullet) corresponding to Bullet 2.8.x and one native (LibBulletJME) always up to date. The
 * modeler can choose which one to use by setting the value of 'use_native_library' to true or false
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
				type = IType.MATRIX,
				of = IType.FLOAT,
				doc = { @doc ("This attribute is a matrix of float that can be used to represent a 3D terrain. The shape of the world, in that case, should be a box, where the"
						+ "dimension on the z-axis is used to scale the z-values of the DEM. The world needs to be register itself as a physical object") }),
		@variable (
				name = IPhysicalConstants.USE_NATIVE,
				type = IType.BOOL,
				doc = { @doc ("This attribute allows to manually switch between the Java version of the Bullet library (JBullet, a modified version of https://github.com/stephengold/jbullet, which corresponds to version 2.72 of the original library) and the native Bullet library (Libbulletjme, https://github.com/stephengold/Libbulletjme, which is kept up-to-date with the 3.x branch of the original library)."
						+ "The native version is the default one unless the libraries cannot be loaded, making JBullet the default") }),

		@variable (
				name = IPhysicalConstants.ACCURATE_COLLISION_DETECTION,
				type = IType.BOOL,
				init = "false",
				doc = @doc ("Enables or not a better (but slower) collision detection ")) })
public class PhysicalSimulationAgent extends SimulationAgent implements IPhysicalConstants {

	final BodyPopulationListener populationListener = new BodyPopulationListener();
	Boolean ccd = false;
	Boolean automatedRegistration = true;
	final GamaPoint gravity = new GamaPoint(0, 0, -9.81d);
	GamaFloatMatrix terrain;
	IPhysicalWorld gateway;
	Boolean useNativeLibrary = BulletActivator.NATIVE_BULLET_LIBRARY_LOADED;

	private final AsOrderedSet<IAgent> registeredAgents = Collector.getOrderedSet();

	private int maxSubSteps;

	public PhysicalSimulationAgent(final IPopulation<? extends IAgent> s, final int index) throws GamaRuntimeException {
		super(s, index);
	}

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

	private void registerAgent(final IScope scope, final IAgent agent) {
		if (registeredAgents.add(agent)) { getGateway().registerAgent(agent); }
	}

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

	@getter (IPhysicalConstants.TERRAIN)
	public GamaFloatMatrix getTerrain() {
		return terrain;
	}

	@setter (IPhysicalConstants.TERRAIN)
	public void setTerrain(final GamaFloatMatrix t) {
		terrain = t;
	}

	@getter (
			value = ACCURATE_COLLISION_DETECTION,
			initializer = true)
	public Boolean getCCD(final IScope scope) {
		return ccd;
	}

	@setter (ACCURATE_COLLISION_DETECTION)
	public void setCCD(final IScope scope, final Boolean v) {
		ccd = v;
		// Dont provoke the instantiation of the gateway yet if it is null
		if (gateway != null) { gateway.setCCD(v); }
	}

	@getter (
			value = AUTOMATED_REGISTRATION,
			initializer = true)
	public Boolean getAutomatedRegistration(final IScope scope) {
		return automatedRegistration;
	}

	@setter (AUTOMATED_REGISTRATION)
	public void setAutomatedRegistration(final IScope scope, final Boolean v) {
		automatedRegistration = v;
	}

	@getter (
			value = USE_NATIVE,
			initializer = true)
	public Boolean usesNativeLibrary(final IScope scope) {
		return useNativeLibrary;
	}

	@setter (USE_NATIVE)
	public void useNativeLibrary(final IScope scope, final Boolean v) {
		// If we have not successfully loaded the library, then the setting should remain false.
		useNativeLibrary = BulletActivator.NATIVE_BULLET_LIBRARY_LOADED && v;
	}

	@getter (
			value = MAX_SUBSTEPS,
			initializer = true)
	public int getMaxSubSteps(final IScope scope) {
		return maxSubSteps;
	}

	@setter (MAX_SUBSTEPS)
	public void setMaxSubSteps(final IScope scope, final int steps) {
		maxSubSteps = steps;
	}

	@getter (
			value = GRAVITY,
			initializer = true)
	public GamaPoint getGravity(final IScope scope) {
		return gravity;
	}

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
		super.doStep(scope);
		final Double timeStep = getTimeStep(scope);
		getGateway().doStep(timeStep, maxSubSteps);
		return true;
	}

	IPhysicalWorld getGateway() {
		if (gateway == null) {
			gateway = useNativeLibrary ? new NativeBulletPhysicalWorld(this) : new BulletPhysicalWorld(this);
		}
		return gateway;
	}

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
			if (automatedRegistration) {
				for (IAgent a : agents) {
					registerAgent(scope, a);
				}
			}
		}

		@Override
		public void notifyAgentsRemoved(final IScope scope, final IPopulation<? extends IAgent> pop,
				final Collection<? extends IAgent> agents) {
			for (IAgent a : agents) {
				unregisterAgent(scope, a);
			}
		}

		@Override
		public void notifyPopulationCleared(final IScope scope, final IPopulation<? extends IAgent> pop) {
			for (IAgent a : pop) {
				unregisterAgent(scope, a);
			}

		}

	}

}
