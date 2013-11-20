/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ESub Species Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ESubSpeciesLinkImpl#getMacro <em>Macro</em>}</li>
 *   <li>{@link gama.impl.ESubSpeciesLinkImpl#getMicro <em>Micro</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ESubSpeciesLinkImpl extends EGamaLinkImpl implements ESubSpeciesLink {
	/**
	 * The cached value of the '{@link #getMacro() <em>Macro</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMacro()
	 * @generated
	 * @ordered
	 */
	protected ESpecies macro;

	/**
	 * The cached value of the '{@link #getMicro() <em>Micro</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMicro()
	 * @generated
	 * @ordered
	 */
	protected ESpecies micro;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ESubSpeciesLinkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.ESUB_SPECIES_LINK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies getMacro() {
		if (macro != null && macro.eIsProxy()) {
			InternalEObject oldMacro = (InternalEObject)macro;
			macro = (ESpecies)eResolveProxy(oldMacro);
			if (macro != oldMacro) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ESUB_SPECIES_LINK__MACRO, oldMacro, macro));
			}
		}
		return macro;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies basicGetMacro() {
		return macro;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMacro(ESpecies newMacro) {
		ESpecies oldMacro = macro;
		macro = newMacro;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESUB_SPECIES_LINK__MACRO, oldMacro, macro));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies getMicro() {
		if (micro != null && micro.eIsProxy()) {
			InternalEObject oldMicro = (InternalEObject)micro;
			micro = (ESpecies)eResolveProxy(oldMicro);
			if (micro != oldMicro) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ESUB_SPECIES_LINK__MICRO, oldMicro, micro));
			}
		}
		return micro;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies basicGetMicro() {
		return micro;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setMicro(ESpecies newMicro) {
		ESpecies oldMicro = micro;
		micro = newMicro;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESUB_SPECIES_LINK__MICRO, oldMicro, micro));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.ESUB_SPECIES_LINK__MACRO:
				if (resolve) return getMacro();
				return basicGetMacro();
			case GamaPackage.ESUB_SPECIES_LINK__MICRO:
				if (resolve) return getMicro();
				return basicGetMicro();
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
			case GamaPackage.ESUB_SPECIES_LINK__MACRO:
				setMacro((ESpecies)newValue);
				return;
			case GamaPackage.ESUB_SPECIES_LINK__MICRO:
				setMicro((ESpecies)newValue);
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
			case GamaPackage.ESUB_SPECIES_LINK__MACRO:
				setMacro((ESpecies)null);
				return;
			case GamaPackage.ESUB_SPECIES_LINK__MICRO:
				setMicro((ESpecies)null);
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
			case GamaPackage.ESUB_SPECIES_LINK__MACRO:
				return macro != null;
			case GamaPackage.ESUB_SPECIES_LINK__MICRO:
				return micro != null;
		}
		return super.eIsSet(featureID);
	}

} //ESubSpeciesLinkImpl
