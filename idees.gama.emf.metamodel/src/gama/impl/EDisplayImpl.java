/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplay;
import gama.EDisplayLink;
import gama.ELayer;
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
 * An implementation of the model object '<em><b>EDisplay</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EDisplayImpl#getLayers <em>Layers</em>}</li>
 *   <li>{@link gama.impl.EDisplayImpl#getDisplayLink <em>Display Link</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EDisplayImpl extends EGamaObjectImpl implements EDisplay {
	/**
	 * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayers()
	 * @generated
	 * @ordered
	 */
	protected EList<ELayer> layers;

	/**
	 * The cached value of the '{@link #getDisplayLink() <em>Display Link</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplayLink()
	 * @generated
	 * @ordered
	 */
	protected EDisplayLink displayLink;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EDisplayImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EDISPLAY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ELayer> getLayers() {
		if (layers == null) {
			layers = new EObjectResolvingEList<ELayer>(ELayer.class, this, GamaPackage.EDISPLAY__LAYERS);
		}
		return layers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink getDisplayLink() {
		if (displayLink != null && displayLink.eIsProxy()) {
			InternalEObject oldDisplayLink = (InternalEObject)displayLink;
			displayLink = (EDisplayLink)eResolveProxy(oldDisplayLink);
			if (displayLink != oldDisplayLink) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
			}
		}
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplayLink basicGetDisplayLink() {
		return displayLink;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplayLink(EDisplayLink newDisplayLink) {
		EDisplayLink oldDisplayLink = displayLink;
		displayLink = newDisplayLink;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EDISPLAY__DISPLAY_LINK, oldDisplayLink, displayLink));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
				return getLayers();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				if (resolve) return getDisplayLink();
				return basicGetDisplayLink();
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
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				getLayers().addAll((Collection<? extends ELayer>)newValue);
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)newValue);
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
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				return;
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				setDisplayLink((EDisplayLink)null);
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
			case GamaPackage.EDISPLAY__LAYERS:
				return layers != null && !layers.isEmpty();
			case GamaPackage.EDISPLAY__DISPLAY_LINK:
				return displayLink != null;
		}
		return super.eIsSet(featureID);
	}

} //EDisplayImpl
