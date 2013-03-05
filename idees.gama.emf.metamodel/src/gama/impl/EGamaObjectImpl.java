/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EGamaLink;
import gama.EGamaModel;
import gama.EGamaObject;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import org.eclipse.emf.ecore.util.EObjectWithInverseResolvingEList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EGama Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EGamaObjectImpl#getName <em>Name</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getModel <em>Model</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getOutcomingLinks <em>Outcoming Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGamaObjectImpl extends EObjectImpl implements EGamaObject {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The cached value of the '{@link #getIncomingLinks() <em>Incoming Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIncomingLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EGamaLink> incomingLinks;

	/**
	 * The cached value of the '{@link #getOutcomingLinks() <em>Outcoming Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getOutcomingLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EGamaLink> outcomingLinks;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EGamaObjectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EGAMA_OBJECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGAMA_OBJECT__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGamaModel getModel() {
		if (eContainerFeatureID() != GamaPackage.EGAMA_OBJECT__MODEL) return null;
		return (EGamaModel)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetModel(EGamaModel newModel, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newModel, GamaPackage.EGAMA_OBJECT__MODEL, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setModel(EGamaModel newModel) {
		if (newModel != eInternalContainer() || (eContainerFeatureID() != GamaPackage.EGAMA_OBJECT__MODEL && newModel != null)) {
			if (EcoreUtil.isAncestor(this, newModel))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newModel != null)
				msgs = ((InternalEObject)newModel).eInverseAdd(this, GamaPackage.EGAMA_MODEL__OBJECTS, EGamaModel.class, msgs);
			msgs = basicSetModel(newModel, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGAMA_OBJECT__MODEL, newModel, newModel));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EGamaLink> getIncomingLinks() {
		if (incomingLinks == null) {
			incomingLinks = new EObjectWithInverseResolvingEList<EGamaLink>(EGamaLink.class, this, GamaPackage.EGAMA_OBJECT__INCOMING_LINKS, GamaPackage.EGAMA_LINK__TARGET);
		}
		return incomingLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EGamaLink> getOutcomingLinks() {
		if (outcomingLinks == null) {
			outcomingLinks = new EObjectWithInverseResolvingEList<EGamaLink>(EGamaLink.class, this, GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS, GamaPackage.EGAMA_LINK__SOURCE);
		}
		return outcomingLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__MODEL:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetModel((EGamaModel)otherEnd, msgs);
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getIncomingLinks()).basicAdd(otherEnd, msgs);
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getOutcomingLinks()).basicAdd(otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__MODEL:
				return basicSetModel(null, msgs);
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				return ((InternalEList<?>)getIncomingLinks()).basicRemove(otherEnd, msgs);
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				return ((InternalEList<?>)getOutcomingLinks()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case GamaPackage.EGAMA_OBJECT__MODEL:
				return eInternalContainer().eInverseRemove(this, GamaPackage.EGAMA_MODEL__OBJECTS, EGamaModel.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__NAME:
				return getName();
			case GamaPackage.EGAMA_OBJECT__MODEL:
				return getModel();
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				return getIncomingLinks();
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				return getOutcomingLinks();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__NAME:
				setName((String)newValue);
				return;
			case GamaPackage.EGAMA_OBJECT__MODEL:
				setModel((EGamaModel)newValue);
				return;
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				getIncomingLinks().clear();
				getIncomingLinks().addAll((Collection<? extends EGamaLink>)newValue);
				return;
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				getOutcomingLinks().clear();
				getOutcomingLinks().addAll((Collection<? extends EGamaLink>)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__NAME:
				setName(NAME_EDEFAULT);
				return;
			case GamaPackage.EGAMA_OBJECT__MODEL:
				setModel((EGamaModel)null);
				return;
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				getIncomingLinks().clear();
				return;
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				getOutcomingLinks().clear();
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case GamaPackage.EGAMA_OBJECT__MODEL:
				return getModel() != null;
			case GamaPackage.EGAMA_OBJECT__INCOMING_LINKS:
				return incomingLinks != null && !incomingLinks.isEmpty();
			case GamaPackage.EGAMA_OBJECT__OUTCOMING_LINKS:
				return outcomingLinks != null && !outcomingLinks.isEmpty();
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (name: ");
		result.append(name);
		result.append(')');
		return result.toString();
	}

} //EGamaObjectImpl
