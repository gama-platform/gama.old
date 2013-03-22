/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplayLink;
import gama.EExperiment;
import gama.EExperimentLink;
import gama.GamaPackage;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EExperiment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EExperimentImpl#getExperimentLinks <em>Experiment Links</em>}</li>
 *   <li>{@link gama.impl.EExperimentImpl#getDisplayLinks <em>Display Links</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EExperimentImpl extends EGamaObjectImpl implements EExperiment {
	/**
	 * The cached value of the '{@link #getExperimentLinks() <em>Experiment Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExperimentLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EExperimentLink> experimentLinks;
	/**
	 * The cached value of the '{@link #getDisplayLinks() <em>Display Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplayLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EDisplayLink> displayLinks;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EExperimentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EEXPERIMENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EExperimentLink> getExperimentLinks() {
		if (experimentLinks == null) {
			experimentLinks = new EObjectResolvingEList<EExperimentLink>(EExperimentLink.class, this, GamaPackage.EEXPERIMENT__EXPERIMENT_LINKS);
		}
		return experimentLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EDisplayLink> getDisplayLinks() {
		if (displayLinks == null) {
			displayLinks = new EObjectResolvingEList<EDisplayLink>(EDisplayLink.class, this, GamaPackage.EEXPERIMENT__DISPLAY_LINKS);
		}
		return displayLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINKS:
				return getExperimentLinks();
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				return getDisplayLinks();
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINKS:
				getExperimentLinks().clear();
				getExperimentLinks().addAll((Collection<? extends EExperimentLink>)newValue);
				return;
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				getDisplayLinks().clear();
				getDisplayLinks().addAll((Collection<? extends EDisplayLink>)newValue);
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINKS:
				getExperimentLinks().clear();
				return;
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				getDisplayLinks().clear();
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINKS:
				return experimentLinks != null && !experimentLinks.isEmpty();
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				return displayLinks != null && !displayLinks.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //EExperimentImpl
