/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

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

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EGama Object</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EGamaObjectImpl#getName <em>Name</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getModel <em>Model</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getColorPicto <em>Color Picto</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getHasError <em>Has Error</em>}</li>
 *   <li>{@link gama.impl.EGamaObjectImpl#getError <em>Error</em>}</li>
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
	 * The cached value of the '{@link #getColorPicto() <em>Color Picto</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColorPicto()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> colorPicto;

	/**
	 * The default value of the '{@link #getHasError() <em>Has Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHasError()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean HAS_ERROR_EDEFAULT = Boolean.FALSE;

	/**
	 * The cached value of the '{@link #getHasError() <em>Has Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHasError()
	 * @generated
	 * @ordered
	 */
	protected Boolean hasError = HAS_ERROR_EDEFAULT;

	/**
	 * The default value of the '{@link #getError() <em>Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getError()
	 * @generated
	 * @ordered
	 */
	protected static final String ERROR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getError() <em>Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getError()
	 * @generated
	 * @ordered
	 */
	protected String error = ERROR_EDEFAULT;

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
	public EList<Integer> getColorPicto() {
		if (colorPicto == null) {
			colorPicto = new EDataTypeEList<Integer>(Integer.class, this, GamaPackage.EGAMA_OBJECT__COLOR_PICTO);
		}
		return colorPicto;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getHasError() {
		return hasError;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHasError(Boolean newHasError) {
		Boolean oldHasError = hasError;
		hasError = newHasError;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGAMA_OBJECT__HAS_ERROR, oldHasError, hasError));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getError() {
		return error;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setError(String newError) {
		String oldError = error;
		error = newError;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGAMA_OBJECT__ERROR, oldError, error));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.EGAMA_OBJECT__MODEL:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetModel((EGamaModel)otherEnd, msgs);
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
			case GamaPackage.EGAMA_OBJECT__COLOR_PICTO:
				return getColorPicto();
			case GamaPackage.EGAMA_OBJECT__HAS_ERROR:
				return getHasError();
			case GamaPackage.EGAMA_OBJECT__ERROR:
				return getError();
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
			case GamaPackage.EGAMA_OBJECT__COLOR_PICTO:
				getColorPicto().clear();
				getColorPicto().addAll((Collection<? extends Integer>)newValue);
				return;
			case GamaPackage.EGAMA_OBJECT__HAS_ERROR:
				setHasError((Boolean)newValue);
				return;
			case GamaPackage.EGAMA_OBJECT__ERROR:
				setError((String)newValue);
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
			case GamaPackage.EGAMA_OBJECT__COLOR_PICTO:
				getColorPicto().clear();
				return;
			case GamaPackage.EGAMA_OBJECT__HAS_ERROR:
				setHasError(HAS_ERROR_EDEFAULT);
				return;
			case GamaPackage.EGAMA_OBJECT__ERROR:
				setError(ERROR_EDEFAULT);
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
			case GamaPackage.EGAMA_OBJECT__COLOR_PICTO:
				return colorPicto != null && !colorPicto.isEmpty();
			case GamaPackage.EGAMA_OBJECT__HAS_ERROR:
				return HAS_ERROR_EDEFAULT == null ? hasError != null : !HAS_ERROR_EDEFAULT.equals(hasError);
			case GamaPackage.EGAMA_OBJECT__ERROR:
				return ERROR_EDEFAULT == null ? error != null : !ERROR_EDEFAULT.equals(error);
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
		result.append(", colorPicto: ");
		result.append(colorPicto);
		result.append(", hasError: ");
		result.append(hasError);
		result.append(", error: ");
		result.append(error);
		result.append(')');
		return result.toString();
	}

} //EGamaObjectImpl
