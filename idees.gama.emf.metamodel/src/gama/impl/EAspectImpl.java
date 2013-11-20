/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EAspect;
import gama.EAspectLink;
import gama.ELayerAspect;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EAspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EAspectImpl#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.impl.EAspectImpl#getAspectLinks <em>Aspect Links</em>}</li>
 *   <li>{@link gama.impl.EAspectImpl#getLayers <em>Layers</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EAspectImpl extends EGamaObjectImpl implements EAspect {
	/**
	 * The default value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected static final String GAML_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected String gamlCode = GAML_CODE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getAspectLinks() <em>Aspect Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspectLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EAspectLink> aspectLinks;

	/**
	 * The cached value of the '{@link #getLayers() <em>Layers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLayers()
	 * @generated
	 * @ordered
	 */
	protected EList<ELayerAspect> layers;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EASPECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGamlCode() {
		return gamlCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGamlCode(String newGamlCode) {
		String oldGamlCode = gamlCode;
		gamlCode = newGamlCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EASPECT__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EAspectLink> getAspectLinks() {
		if (aspectLinks == null) {
			aspectLinks = new EObjectResolvingEList<EAspectLink>(EAspectLink.class, this, GamaPackage.EASPECT__ASPECT_LINKS);
		}
		return aspectLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ELayerAspect> getLayers() {
		if (layers == null) {
			layers = new EObjectResolvingEList<ELayerAspect>(ELayerAspect.class, this, GamaPackage.EASPECT__LAYERS);
		}
		return layers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EASPECT__GAML_CODE:
				return getGamlCode();
			case GamaPackage.EASPECT__ASPECT_LINKS:
				return getAspectLinks();
			case GamaPackage.EASPECT__LAYERS:
				return getLayers();
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
			case GamaPackage.EASPECT__GAML_CODE:
				setGamlCode((String)newValue);
				return;
			case GamaPackage.EASPECT__ASPECT_LINKS:
				getAspectLinks().clear();
				getAspectLinks().addAll((Collection<? extends EAspectLink>)newValue);
				return;
			case GamaPackage.EASPECT__LAYERS:
				getLayers().clear();
				getLayers().addAll((Collection<? extends ELayerAspect>)newValue);
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
			case GamaPackage.EASPECT__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
			case GamaPackage.EASPECT__ASPECT_LINKS:
				getAspectLinks().clear();
				return;
			case GamaPackage.EASPECT__LAYERS:
				getLayers().clear();
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
			case GamaPackage.EASPECT__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
			case GamaPackage.EASPECT__ASPECT_LINKS:
				return aspectLinks != null && !aspectLinks.isEmpty();
			case GamaPackage.EASPECT__LAYERS:
				return layers != null && !layers.isEmpty();
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
		result.append(" (gamlCode: ");
		result.append(gamlCode);
		result.append(')');
		return result.toString();
	}

} //EAspectImpl
