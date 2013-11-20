/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplay;
import gama.EDisplayLink;
import gama.EGUIExperiment;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EDisplay Link</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EDisplayLinkImpl#getExperiment <em>Experiment</em>}</li>
 *   <li>{@link gama.impl.EDisplayLinkImpl#getDisplay <em>Display</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EDisplayLinkImpl extends EGamaLinkImpl implements EDisplayLink {
	/**
	 * The cached value of the '{@link #getExperiment() <em>Experiment</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExperiment()
	 * @generated
	 * @ordered
	 */
	protected EGUIExperiment experiment;

	/**
	 * The cached value of the '{@link #getDisplay() <em>Display</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplay()
	 * @generated
	 * @ordered
	 */
	protected EDisplay display;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EDisplayLinkImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EDISPLAY_LINK;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGUIExperiment getExperiment() {
		if (experiment != null && experiment.eIsProxy()) {
			InternalEObject oldExperiment = (InternalEObject)experiment;
			experiment = (EGUIExperiment)eResolveProxy(oldExperiment);
			if (experiment != oldExperiment) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EDISPLAY_LINK__EXPERIMENT, oldExperiment, experiment));
			}
		}
		return experiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EGUIExperiment basicGetExperiment() {
		return experiment;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExperiment(EGUIExperiment newExperiment) {
		EGUIExperiment oldExperiment = experiment;
		experiment = newExperiment;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY_LINK__EXPERIMENT, oldExperiment, experiment));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplay getDisplay() {
		if (display != null && display.eIsProxy()) {
			InternalEObject oldDisplay = (InternalEObject)display;
			display = (EDisplay)eResolveProxy(oldDisplay);
			if (display != oldDisplay) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EDISPLAY_LINK__DISPLAY, oldDisplay, display));
			}
		}
		return display;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplay basicGetDisplay() {
		return display;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplay(EDisplay newDisplay) {
		EDisplay oldDisplay = display;
		display = newDisplay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY_LINK__DISPLAY, oldDisplay, display));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EDISPLAY_LINK__EXPERIMENT:
				if (resolve) return getExperiment();
				return basicGetExperiment();
			case GamaPackage.EDISPLAY_LINK__DISPLAY:
				if (resolve) return getDisplay();
				return basicGetDisplay();
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
			case GamaPackage.EDISPLAY_LINK__EXPERIMENT:
				setExperiment((EGUIExperiment)newValue);
				return;
			case GamaPackage.EDISPLAY_LINK__DISPLAY:
				setDisplay((EDisplay)newValue);
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
			case GamaPackage.EDISPLAY_LINK__EXPERIMENT:
				setExperiment((EGUIExperiment)null);
				return;
			case GamaPackage.EDISPLAY_LINK__DISPLAY:
				setDisplay((EDisplay)null);
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
			case GamaPackage.EDISPLAY_LINK__EXPERIMENT:
				return experiment != null;
			case GamaPackage.EDISPLAY_LINK__DISPLAY:
				return display != null;
		}
		return super.eIsSet(featureID);
	}

} //EDisplayLinkImpl
