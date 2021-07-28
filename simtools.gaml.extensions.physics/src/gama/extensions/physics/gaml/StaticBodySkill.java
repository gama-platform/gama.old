/*********************************************************************************************
 *
 *
 * 'Physics3DSkill.java', in plugin 'simtools.gaml.extensions.physics', is part of the source code of the GAMA modeling
 * and simulation platform. (v. 1.8.1)
 *
 * (c) 2007-2020 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 *
 *
 **********************************************************************************************/
package gama.extensions.physics.gaml;

import java.util.HashMap;
import java.util.Map;

import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IPhysicalConstants;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;

import msi.gama.metamodel.shape.IShape;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.listener;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gaml.skills.Skill;
import msi.gaml.types.IType;

@vars ({
		// @variable (
		// name = IKeyword.LOCATION,
		// type = IType.POINT,
		// depends_on = IKeyword.SHAPE,
		// doc = @doc ("Represents the current position of the agent")),
		@variable (
				name = IPhysicalConstants.MASS,
				type = IType.FLOAT,
				init = "1.0",
				doc = { @doc ("The mass of the agent. Should be equal to 0.0 for static, motionless agents") }),
		@variable (
				name = IPhysicalConstants.ROTATION,
				type = IType.PAIR,
				of = IType.POINT,
				index = IType.FLOAT,
				init = "0.0::{0,0,1}",
				doc = { @doc ("The rotation of the physical body, expressed as a pair which key is the angle in degrees and value the axis around which it is measured") }),

		@variable (
				name = IPhysicalConstants.FRICTION,
				type = IType.FLOAT,
				init = "0.5",
				doc = { @doc ("Between 0 and 1. The coefficient of friction of the agent (how much it decelerates the agents in contact with him). Default is 0.5") }),
		@variable (
				name = IPhysicalConstants.RESTITUTION,
				type = IType.FLOAT,
				init = "0.0",
				doc = { @doc ("Between 0 and 1. The coefficient of restitution of the agent (defines the 'bounciness' of the agent). Default is 0") }),

		@variable (
				name = IPhysicalConstants.AABB,
				type = IType.GEOMETRY,
				doc = { @doc ("The axis-aligned bounding box. A box used to evaluate the probability of contacts between objects. Can be displayed as any other GAMA shapes/geometries in order to verify that the physical representation of the agent corresponds to its geometry in the model") }), })

@skill (
		name = IPhysicalConstants.STATIC_BODY,
		concept = { IConcept.SKILL, IConcept.THREED },
		doc = { @doc ("A skill allowing an agent to b a static object in a physical 3D world (if it is also registered in a model inheriting from '"
				+ IPhysicalConstants.PHYSICAL_WORLD + "'). Proposes a number of new attributes ('"
				+ IPhysicalConstants.FRICTION + "', '" + IPhysicalConstants.RESTITUTION
				+ "',...) and the actions called '" + IPhysicalConstants.CONTACT_ADDED + "'/'"
				+ IPhysicalConstants.CONTACT_REMOVED
				+ "' in order for the agent to be informed when it is in physical contact with an other. These actions will be called in turn for each colliding agent. ") })
/**
 * A class that supports the definition of agents provided with static bodies in a physical world. It comes with new
 * variables (friction, restitution, body, field, AABB, etc.) and gives the possibility to all its instances to be
 * notified when contacts occur with other instances of either 'static_body' or 'dynamic_body'
 *
 * @author Alexis Drogoul 2021
 *
 */
public class StaticBodySkill extends Skill implements IPhysicalConstants {

	protected IBody getBody(final IAgent agent) {
		IBody result = (IBody) agent.getAttribute(BODY);
		// if it is null, the agent is not yet registered in the physical world
		// we create a temporary fake body, that can hold the initialisation of
		// variables, and that will be replaced as
		// soon as the agent is registered
		if (result == null) {
			result = new FakeBody();
			agent.setAttribute(BODY, result);
		}
		return result;
	}

	/**
	 * Static bodies have no mass in Bullet
	 */
	@getter (
			value = MASS,
			initializer = true)
	public Double getMass(final IAgent scope) {
		return 0d;
	}

	/**
	 * We prevent modelers from providing a mass
	 */
	@setter (MASS)
	public void setMass(final IAgent a, final Double value) {}

	/**
	 * Listens to the change in the location of the agent (whether these changes come from the model in GAML or from
	 * another plugin/skill in Java) in order to synchronize the location in GAMA with the location in Bullet
	 *
	 */
	@listener (IKeyword.LOCATION)
	public void changeInLocation(final IAgent a, final GamaPoint loc) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setLocation(loc);
	}

	@getter (AABB)
	public IShape getAABB(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return null;
		return body.getAABB();
	}

	@getter (FRICTION)
	public Double getFriction(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return 00d;
		return Double.valueOf(body.getFriction());
	}

	@setter (FRICTION)
	public void setFriction(final IAgent a, final Double friction) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setFriction(friction);
	}

	@getter (RESTITUTION)
	public Double getRestitution(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return 00d;
		return Double.valueOf(body.getRestitution());
	}

	@setter (RESTITUTION)
	public void setRestitution(final IAgent a, final Double restitution) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setRestitution(restitution);
	}

	@action (
			doc = @doc ("This action must be called when the geometry of the agent changes in the simulation world and this change must be propagated to the physical world. "
					+ "The change of location (in either worlds) or the rotation due to physical forces do not count as changes, as they are already taken into account. "
					+ "However, a rotation in the simulation world need to be handled by calling this action. As it involves long operations (removing the agent from the physical world, "
					+ "then reinserting it with its new shape), this action should not be called too often."),
			name = UPDATE_BODY,
			args = {})
	public Object primUpdateGeometry(final IScope scope) {
		SimulationAgent sim = scope.getSimulation();
		if (sim instanceof PhysicalSimulationAgent) {
			((PhysicalSimulationAgent) sim).updateAgent(scope, scope.getAgent());
		}
		return null;
	}

	@action (
			doc = @doc ("This action can be redefined in order for the agent to implement a specific behavior when it comes into contact (collision) with another agent. "
					+ "It is automatically called by the physics simulation engine on both colliding agents. The default built-in behavior does nothing."),
			name = CONTACT_ADDED,
			args = { @arg (
					doc = @doc ("represents the other agent with which a collision has been detected"),
					name = OTHER,
					optional = false,
					type = IType.AGENT) })
	public Object primContactAdded(final IScope scope) {
		// Does nothing by default
		return null;
	}

	@action (
			doc = @doc ("This action can be redefined in order for the agent to implement a specific behavior when a previous contact with another agent is removed. "
					+ "It is automatically called by the physics simulation engine on both colliding agents. The default built-in behavior does nothing."),
			name = CONTACT_REMOVED,
			args = { @arg (
					doc = @doc ("represents the other agent with which a collision has been detected"),
					name = OTHER,
					optional = false,
					type = IType.AGENT) })
	public Object primContactDestroyed(final IScope scope) {
		// Does nothing by default
		return null;
	}

	/***
	 * A class used to provide a temporary body to agents before their "bullet" one is built. It allows to store the
	 * information sent by the agent and to retrieve it once their actual body is being built
	 *
	 * @author drogoul
	 *
	 */
	public class FakeBody implements IBody<Object, Object, Object, GamaPoint> {
		public final Map<String, Object> values = new HashMap<>();

		@Override
		public float getFriction() {
			Double result = (Double) values.get(FRICTION);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public float getRestitution() {
			Double result = (Double) values.get(RESTITUTION);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public float getLinearDamping() {
			Double result = (Double) values.get(DAMPING);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public float getAngularDamping() {
			Double result = (Double) values.get(ANGULAR_DAMPING);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public GamaPoint getAngularVelocity(final GamaPoint v) {
			GamaPoint result = v == null ? new GamaPoint() : v;
			GamaPoint existing = (GamaPoint) values.get(ANGULAR_VELOCITY);
			if (existing == null) {
				result.setLocation(0, 0, 0);
			} else {
				result.setLocation(existing);
			}
			return result;
		}

		@Override
		public GamaPoint getLinearVelocity(final GamaPoint v) {
			GamaPoint result = v == null ? new GamaPoint() : v;
			GamaPoint existing = (GamaPoint) values.get(VELOCITY);
			if (existing == null) {
				result.setLocation(0, 0, 0);
			} else {
				result.setLocation(existing);
			}
			return result;
		}

		@Override
		public void setCCD(final boolean v) {
			values.put("CCD", v);
		}

		@Override
		public void setFriction(final Double friction) {
			values.put(FRICTION, friction);

		}

		@Override
		public void setRestitution(final Double restitution) {
			values.put(RESTITUTION, restitution);

		}

		@Override
		public void setDamping(final Double damping) {
			values.put(DAMPING, damping);
		}

		@Override
		public void setAngularDamping(final Double damping) {
			values.put(ANGULAR_DAMPING, damping);
		}

		@Override
		public void setAngularVelocity(final GamaPoint p) {
			values.put(ANGULAR_VELOCITY, p);
		}

		@Override
		public void setLinearVelocity(final GamaPoint p) {
			values.put(VELOCITY, p);
		}

		@Override
		public void setLocation(final GamaPoint loc) {
			// TODO Auto-generated method stub

		}

		@Override
		public void clearForces() {}

		@Override
		public void applyImpulse(final GamaPoint impulse) {}

		@Override
		public void applyTorque(final GamaPoint torque) {}

		@Override
		public void applyForce(final GamaPoint force) {}

		@Override
		public void setMass(final Double mass) {
			values.put(MASS, mass);
		}

		@Override
		public float getMass() {
			Double result = (Double) values.get(MASS);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public IShape getAABB() {
			return null;
		}

		@Override
		public float getContactDamping() {
			Double result = (Double) values.get(CONTACT_DAMPING);
			return result != null ? result.floatValue() : 0f;
		}

		@Override
		public void setContactDamping(final Double damping) {
			values.put(CONTACT_DAMPING, damping);
		}

		@Override
		public Object getBody() {
			return this;
		}

		@Override
		public IAgent getAgent() {
			return null;
		}

		@Override
		public GamaPoint toVector(final GamaPoint v) {
			return v;
		}

		@Override
		public GamaPoint toGamaPoint(final GamaPoint v) {
			return v;
		}

		@Override
		public Object createAndInitializeBody(final Object shape, final Object world) {
			return this;
		}

	}
}
