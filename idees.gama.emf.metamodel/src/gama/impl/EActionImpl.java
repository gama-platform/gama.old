/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EAction;
import gama.EActionLink;
import gama.EVariable;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EAction</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EActionImpl#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.impl.EActionImpl#getActionLinks <em>Action Links</em>}</li>
 *   <li>{@link gama.impl.EActionImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.impl.EActionImpl#getReturnType <em>Return Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EActionImpl extends EGamaObjectImpl implements EAction {
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
	 * The cached value of the '{@link #getActionLinks() <em>Action Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActionLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EActionLink> actionLinks;

	/**
	 * The cached value of the '{@link #getVariables() <em>Variables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariables()
	 * @generated
	 * @ordered
	 */
	protected EList<EVariable> variables;

	/**
	 * The default value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReturnType()
	 * @generated
	 * @ordered
	 */
	protected static final String RETURN_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getReturnType() <em>Return Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReturnType()
	 * @generated
	 * @ordered
	 */
	protected String returnType = RETURN_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EActionImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EACTION;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EACTION__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EActionLink> getActionLinks() {
		if (actionLinks == null) {
			actionLinks = new EObjectResolvingEList<EActionLink>(EActionLink.class, this, GamaPackage.EACTION__ACTION_LINKS);
		}
		return actionLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EVariable> getVariables() {
		if (variables == null) {
			variables = new EObjectContainmentEList<EVariable>(EVariable.class, this, GamaPackage.EACTION__VARIABLES);
		}
		return variables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getReturnType() {
		return returnType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setReturnType(String newReturnType) {
		String oldReturnType = returnType;
		returnType = newReturnType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EACTION__RETURN_TYPE, oldReturnType, returnType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.EACTION__VARIABLES:
				return ((InternalEList<?>)getVariables()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EACTION__GAML_CODE:
				return getGamlCode();
			case GamaPackage.EACTION__ACTION_LINKS:
				return getActionLinks();
			case GamaPackage.EACTION__VARIABLES:
				return getVariables();
			case GamaPackage.EACTION__RETURN_TYPE:
				return getReturnType();
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
			case GamaPackage.EACTION__GAML_CODE:
				setGamlCode((String)newValue);
				return;
			case GamaPackage.EACTION__ACTION_LINKS:
				getActionLinks().clear();
				getActionLinks().addAll((Collection<? extends EActionLink>)newValue);
				return;
			case GamaPackage.EACTION__VARIABLES:
				getVariables().clear();
				getVariables().addAll((Collection<? extends EVariable>)newValue);
				return;
			case GamaPackage.EACTION__RETURN_TYPE:
				setReturnType((String)newValue);
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
			case GamaPackage.EACTION__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
			case GamaPackage.EACTION__ACTION_LINKS:
				getActionLinks().clear();
				return;
			case GamaPackage.EACTION__VARIABLES:
				getVariables().clear();
				return;
			case GamaPackage.EACTION__RETURN_TYPE:
				setReturnType(RETURN_TYPE_EDEFAULT);
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
			case GamaPackage.EACTION__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
			case GamaPackage.EACTION__ACTION_LINKS:
				return actionLinks != null && !actionLinks.isEmpty();
			case GamaPackage.EACTION__VARIABLES:
				return variables != null && !variables.isEmpty();
			case GamaPackage.EACTION__RETURN_TYPE:
				return RETURN_TYPE_EDEFAULT == null ? returnType != null : !RETURN_TYPE_EDEFAULT.equals(returnType);
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
		result.append(", returnType: ");
		result.append(returnType);
		result.append(')');
		return result.toString();
	}

} //EActionImpl
