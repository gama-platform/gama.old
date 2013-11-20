/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EAspect;
import gama.EAspectLink;
import gama.ESpecies;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EAspect Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EAspectLinkImpl#getAspect <em>Aspect</em>}</li>
 *   <li>{@link gama.impl.EAspectLinkImpl#getSpecies <em>Species</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EAspectLinkImpl extends EGamaLinkImpl implements EAspectLink {
	/**
	 * The cached value of the '{@link #getAspect() <em>Aspect</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspect()
	 * @generated
	 * @ordered
	 */
	protected EAspect aspect;

	/**
	 * The cached value of the '{@link #getSpecies() <em>Species</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpecies()
	 * @generated
	 * @ordered
	 */
	protected ESpecies species;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EAspectLinkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EASPECT_LINK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspect getAspect() {
		if (aspect != null && aspect.eIsProxy()) {
			InternalEObject oldAspect = (InternalEObject)aspect;
			aspect = (EAspect)eResolveProxy(oldAspect);
			if (aspect != oldAspect) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EASPECT_LINK__ASPECT, oldAspect, aspect));
			}
		}
		return aspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspect basicGetAspect() {
		return aspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAspect(EAspect newAspect) {
		EAspect oldAspect = aspect;
		aspect = newAspect;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EASPECT_LINK__ASPECT, oldAspect, aspect));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies getSpecies() {
		if (species != null && species.eIsProxy()) {
			InternalEObject oldSpecies = (InternalEObject)species;
			species = (ESpecies)eResolveProxy(oldSpecies);
			if (species != oldSpecies) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EASPECT_LINK__SPECIES, oldSpecies, species));
			}
		}
		return species;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies basicGetSpecies() {
		return species;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpecies(ESpecies newSpecies) {
		ESpecies oldSpecies = species;
		species = newSpecies;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EASPECT_LINK__SPECIES, oldSpecies, species));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EASPECT_LINK__ASPECT:
				if (resolve) return getAspect();
				return basicGetAspect();
			case GamaPackage.EASPECT_LINK__SPECIES:
				if (resolve) return getSpecies();
				return basicGetSpecies();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GamaPackage.EASPECT_LINK__ASPECT:
				setAspect((EAspect)newValue);
				return;
			case GamaPackage.EASPECT_LINK__SPECIES:
				setSpecies((ESpecies)newValue);
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
			case GamaPackage.EASPECT_LINK__ASPECT:
				setAspect((EAspect)null);
				return;
			case GamaPackage.EASPECT_LINK__SPECIES:
				setSpecies((ESpecies)null);
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
			case GamaPackage.EASPECT_LINK__ASPECT:
				return aspect != null;
			case GamaPackage.EASPECT_LINK__SPECIES:
				return species != null;
		}
		return super.eIsSet(featureID);
	}

} //EAspectLinkImpl
