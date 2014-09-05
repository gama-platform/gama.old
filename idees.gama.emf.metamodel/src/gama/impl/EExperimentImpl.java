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
import gama.EMonitor;
import gama.EParameter;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EExperiment</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EExperimentImpl#getExperimentLink <em>Experiment Link</em>}</li>
 *   <li>{@link gama.impl.EExperimentImpl#getDisplayLinks <em>Display Links</em>}</li>
 *   <li>{@link gama.impl.EExperimentImpl#getParameters <em>Parameters</em>}</li>
 *   <li>{@link gama.impl.EExperimentImpl#getMonitors <em>Monitors</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EExperimentImpl extends EGamaObjectImpl implements EExperiment {
	/**
	 * The cached value of the '{@link #getExperimentLink() <em>Experiment Link</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExperimentLink()
	 * @generated
	 * @ordered
	 */
	protected EExperimentLink experimentLink;

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
	 * The cached value of the '{@link #getParameters() <em>Parameters</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getParameters()
	 * @generated
	 * @ordered
	 */
	protected EList<EParameter> parameters;

	/**
	 * The cached value of the '{@link #getMonitors() <em>Monitors</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMonitors()
	 * @generated
	 * @ordered
	 */
	protected EList<EMonitor> monitors;

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
	public EExperimentLink getExperimentLink() {
		if (experimentLink != null && experimentLink.eIsProxy()) {
			InternalEObject oldExperimentLink = (InternalEObject)experimentLink;
			experimentLink = (EExperimentLink)eResolveProxy(oldExperimentLink);
			if (experimentLink != oldExperimentLink) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EEXPERIMENT__EXPERIMENT_LINK, oldExperimentLink, experimentLink));
			}
		}
		return experimentLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EExperimentLink basicGetExperimentLink() {
		return experimentLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExperimentLink(EExperimentLink newExperimentLink) {
		EExperimentLink oldExperimentLink = experimentLink;
		experimentLink = newExperimentLink;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EEXPERIMENT__EXPERIMENT_LINK, oldExperimentLink, experimentLink));
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
	public EList<EParameter> getParameters() {
		if (parameters == null) {
			parameters = new EObjectResolvingEList<EParameter>(EParameter.class, this, GamaPackage.EEXPERIMENT__PARAMETERS);
		}
		return parameters;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EMonitor> getMonitors() {
		if (monitors == null) {
			monitors = new EObjectResolvingEList<EMonitor>(EMonitor.class, this, GamaPackage.EEXPERIMENT__MONITORS);
		}
		return monitors;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINK:
				if (resolve) return getExperimentLink();
				return basicGetExperimentLink();
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				return getDisplayLinks();
			case GamaPackage.EEXPERIMENT__PARAMETERS:
				return getParameters();
			case GamaPackage.EEXPERIMENT__MONITORS:
				return getMonitors();
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINK:
				setExperimentLink((EExperimentLink)newValue);
				return;
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				getDisplayLinks().clear();
				getDisplayLinks().addAll((Collection<? extends EDisplayLink>)newValue);
				return;
			case GamaPackage.EEXPERIMENT__PARAMETERS:
				getParameters().clear();
				getParameters().addAll((Collection<? extends EParameter>)newValue);
				return;
			case GamaPackage.EEXPERIMENT__MONITORS:
				getMonitors().clear();
				getMonitors().addAll((Collection<? extends EMonitor>)newValue);
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINK:
				setExperimentLink((EExperimentLink)null);
				return;
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				getDisplayLinks().clear();
				return;
			case GamaPackage.EEXPERIMENT__PARAMETERS:
				getParameters().clear();
				return;
			case GamaPackage.EEXPERIMENT__MONITORS:
				getMonitors().clear();
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
			case GamaPackage.EEXPERIMENT__EXPERIMENT_LINK:
				return experimentLink != null;
			case GamaPackage.EEXPERIMENT__DISPLAY_LINKS:
				return displayLinks != null && !displayLinks.isEmpty();
			case GamaPackage.EEXPERIMENT__PARAMETERS:
				return parameters != null && !parameters.isEmpty();
			case GamaPackage.EEXPERIMENT__MONITORS:
				return monitors != null && !monitors.isEmpty();
		}
		return super.eIsSet(featureID);
	}

} //EExperimentImpl
