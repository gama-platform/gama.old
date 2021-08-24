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

import gama.extensions.physics.common.IBody;
import gama.extensions.physics.common.IPhysicalConstants;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.variable;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.IConcept;
import msi.gama.runtime.IScope;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;

@vars ({ @variable (
		name = IPhysicalConstants.DAMPING,
		type = IType.FLOAT,
		init = "0.1",
		doc = { @doc ("Between 0 and 1. a linear decelaration coefficient that occurs even without contact ") }),
		@variable (
				name = IPhysicalConstants.ANGULAR_DAMPING,
				type = IType.FLOAT,
				init = "0.1",
				doc = { @doc ("Between 0 and 1. an angular decelaration coefficient that occurs even without contact ") }),
		@variable (
				name = IPhysicalConstants.CONTACT_DAMPING,
				type = IType.FLOAT,
				init = "0.1",
				doc = { @doc ("Between 0 and 1. a decelaration coefficient that occurs in case of contact. Only available in the native Bullet library (no effect on the Java implementation)") }),
		@variable (
				name = IPhysicalConstants.ANGULAR_VELOCITY,
				type = IType.POINT,
				init = "{0,0,0}",
				doc = { @doc ("The angular velocity of the agent in the three directions, expressed as a point.") }),
		@variable (
				name = IPhysicalConstants.VELOCITY,
				type = IType.POINT,
				init = "{0,0,0}",
				doc = { @doc ("The linear velocity of the agent in the three directions, expressed as a point.") }) })
@skill (
		name = IPhysicalConstants.DYNAMIC_BODY,
		concept = { IConcept.SKILL, IConcept.THREED },
		doc = { @doc ("A skill allowing an agent to act like in a physical 3D world (if it is also registered in a model inheriting from 'physical_world'). Proposes a number of attributes (velocity...) and one action (apply), which allows to define its dynamic properties (forces, torques, ...)") })
/**
 * A skill that provides agents with variables and actions described in the 'static_body' skill, adding the ones related
 * to motion (velocity, damping) and forces (force, impulse, torque)
 *
 * @author Alexis Drogoul 2021
 *
 */
public class DynamicBodySkill extends StaticBodySkill {

	@getter (VELOCITY)
	public GamaPoint getVelocity(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return new GamaPoint();
		return body.getLinearVelocity(null);
	}

	@setter (VELOCITY)
	public void setVelocity(final IAgent a, final GamaPoint velocity) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setLinearVelocity(velocity);
	}

	@getter (DAMPING)
	public Double getDamping(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return 00d;
		return (double) body.getLinearDamping();
	}

	@setter (DAMPING)
	public void setDamping(final IAgent a, final Double damping) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setDamping(damping);
	}

	@getter (CONTACT_DAMPING)
	public Double getContactDamping(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return 00d;
		return (double) body.getContactDamping();
	}

	@setter (CONTACT_DAMPING)
	public void setContactDamping(final IAgent a, final Double damping) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setContactDamping(damping);
	}

	@getter (ANGULAR_DAMPING)
	public Double getAngularDamping(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return 00d;
		return (double) body.getAngularDamping();
	}

	@Override
	// @getter(value = IBody.MASS, initializer = true)
	public Double getMass(final IAgent scope) {
		IBody body = getBody(scope);
		if (body == null) return 00d;
		return (double) body.getMass();
	}

	@Override
	// @setter(IBody.MASS)
	public void setMass(final IAgent agent, final Double value) {
		IBody body = getBody(agent);
		if (body == null) return;
		body.setMass(value);
	}

	@setter (ANGULAR_DAMPING)
	public void setAngularDamping(final IAgent a, final Double damping) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setAngularDamping(damping);
	}

	@getter (ANGULAR_VELOCITY)
	public GamaPoint getAngularVelocity(final IAgent a) {
		IBody body = getBody(a);
		if (body == null) return new GamaPoint();
		return body.getAngularVelocity(null);
	}

	@setter (ANGULAR_VELOCITY)
	public void setAngularVelocity(final IAgent a, final GamaPoint angularVelocity) {
		IBody body = getBody(a);
		if (body == null) return;
		body.setAngularVelocity(angularVelocity);
	}

	@action (
			doc = @doc ("An action that allows to apply different effects to the object, like forces, impulses, etc."),
			name = APPLY,
			args = { @arg (
					doc = @doc ("If true clears all forces applied to the agent and clears its veolicity as well"),
					name = CLEARANCE,
					type = IType.BOOL),
					@arg (
							doc = @doc ("An idealised change of momentum. Adds to the velocity of the object. This is the kind of push that you would use on a pool billiard ball."),
							name = IMPULSE,
							type = IType.POINT),
					@arg (
							doc = @doc ("Move (push) the object once with a certain moment, expressed as a point (vector). Adds to the existing forces."),
							name = FORCE,
							type = IType.POINT),
					@arg (
							doc = @doc ("Rotate (twist) the object once around its axes, expressed as a point (vector)"),
							name = TORQUE,
							type = IType.POINT) })
	public Object primApply(final IScope scope) {
		final IAgent agent = getCurrentAgent(scope);
		IBody body = getBody(agent);
		if (body == null) return null;
		if (scope.hasArg(CLEARANCE)) {
			Boolean clearance = scope.getBoolArg(CLEARANCE);
			if (!clearance) return this;
			body.clearForces();
			body.setLinearVelocity(null);
			body.setAngularVelocity(null);
			return this;
		}
		GamaPoint impulse = Cast.asPoint(scope, scope.getArg(IMPULSE, IType.POINT));
		if (impulse != null) { body.applyImpulse(impulse); }

		GamaPoint force = Cast.asPoint(scope, scope.getArg(FORCE, IType.POINT));
		if (force != null) { body.applyForce(force); }

		GamaPoint torque = Cast.asPoint(scope, scope.getArg(TORQUE, IType.POINT));
		if (torque != null) { body.applyTorque(torque); }

		return this;
	}

}
