/*******************************************************************************************************
 *
 * UniversalContactAddedListener.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.3).
 *
 * (c) 2007-2023 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package gama.extensions.physics.common;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.contacts.Contact;

import com.bulletphysics.ContactAddedCallback;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.narrowphase.ManifoldPoint;
import com.bulletphysics.dynamics.RigidBody;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;

/**
 * The listener interface for receiving universalContactAdded events.
 * The class that is interested in processing a universalContactAdded
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addUniversalContactAddedListener<code> method. When
 * the universalContactAdded event occurs, that object's appropriate
 * method is invoked.
 *
 * @see UniversalContactAddedEvent
 */
public class UniversalContactAddedListener implements ContactAddedCallback, PhysicsCollisionListener, ContactListener {

	/** The new contacts. */
	Multimap<IBody, IBody> newContacts = MultimapBuilder.hashKeys().hashSetValues().build();

	/**
	 * Adds the contact between.
	 *
	 * @param b0 the b 0
	 * @param b1 the b 1
	 */
	private void addContactBetween(final IBody b0, final IBody b1) {
		if (b0.isNoNotification() && b1.isNoNotification()) return;
		newContacts.put(b0, b1);
	}

	@Override
	public void collision(final PhysicsCollisionEvent event) {
		addContactBetween((IBody) event.getObjectA().getUserObject(), (IBody) event.getObjectB().getUserObject());
	}

	@Override
	public boolean contactAdded(final ManifoldPoint cp, final CollisionObject colObj0, final int partId0,
			final int index0, final CollisionObject colObj1, final int partId1, final int index1) {
		addContactBetween((IBody) ((RigidBody) colObj0).getUserPointer(),
				(IBody) ((RigidBody) colObj1).getUserPointer());
		return true;
	}

	@Override
	public void beginContact(final Contact contact) {
		addContactBetween((IBody) contact.getFixtureA().getBody().getUserData(),
				(IBody) contact.getFixtureB().getBody().getUserData());
	}

	@Override
	public void endContact(final Contact contact) {
		// Nothing to do ?
	}

	@Override
	public void preSolve(final Contact contact, final Manifold oldManifold) {}

	@Override
	public void postSolve(final Contact contact, final ContactImpulse impulse) {}

	/**
	 * Gets the collected contacts.
	 *
	 * @return the collected contacts
	 */
	public Multimap<? extends IBody, ? extends IBody> getCollectedContacts() {
		return newContacts;
	}

	/**
	 * Clear.
	 */
	public void clear() {
		newContacts.clear();
	}

}