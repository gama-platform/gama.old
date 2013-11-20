/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EExperiment;
import gama.EExperimentLink;
import gama.ESpecies;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EExperiment Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EExperimentLinkImpl#getSpecies <em>Species</em>}</li>
 *   <li>{@link gama.impl.EExperimentLinkImpl#getExperiment <em>Experiment</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EExperimentLinkImpl extends EGamaLinkImpl implements EExperimentLink {
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
	 * The cached value of the '{@link #getExperiment() <em>Experiment</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExperiment()
	 * @generated
	 * @ordered
	 */
	protected EExperiment experiment;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EExperimentLinkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EEXPERIMENT_LINK;
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
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EEXPERIMENT_LINK__SPECIES, oldSpecies, species));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EEXPERIMENT_LINK__SPECIES, oldSpecies, species));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EExperiment getExperiment() {
		if (experiment != null && experiment.eIsProxy()) {
			InternalEObject oldExperiment = (InternalEObject)experiment;
			experiment = (EExperiment)eResolveProxy(oldExperiment);
			if (experiment != oldExperiment) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EEXPERIMENT_LINK__EXPERIMENT, oldExperiment, experiment));
			}
		}
		return experiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EExperiment basicGetExperiment() {
		return experiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExperiment(EExperiment newExperiment) {
		EExperiment oldExperiment = experiment;
		experiment = newExperiment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EEXPERIMENT_LINK__EXPERIMENT, oldExperiment, experiment));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EEXPERIMENT_LINK__SPECIES:
				if (resolve) return getSpecies();
				return basicGetSpecies();
			case GamaPackage.EEXPERIMENT_LINK__EXPERIMENT:
				if (resolve) return getExperiment();
				return basicGetExperiment();
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
			case GamaPackage.EEXPERIMENT_LINK__SPECIES:
				setSpecies((ESpecies)newValue);
				return;
			case GamaPackage.EEXPERIMENT_LINK__EXPERIMENT:
				setExperiment((EExperiment)newValue);
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
			case GamaPackage.EEXPERIMENT_LINK__SPECIES:
				setSpecies((ESpecies)null);
				return;
			case GamaPackage.EEXPERIMENT_LINK__EXPERIMENT:
				setExperiment((EExperiment)null);
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
			case GamaPackage.EEXPERIMENT_LINK__SPECIES:
				return species != null;
			case GamaPackage.EEXPERIMENT_LINK__EXPERIMENT:
				return experiment != null;
		}
		return super.eIsSet(featureID);
	}

} //EExperimentLinkImpl
