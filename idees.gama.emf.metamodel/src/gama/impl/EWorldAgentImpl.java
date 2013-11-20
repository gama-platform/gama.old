/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EWorldAgent;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EWorld Agent</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EWorldAgentImpl#getBoundsWidth <em>Bounds Width</em>}</li>
 *   <li>{@link gama.impl.EWorldAgentImpl#getBoundsHeigth <em>Bounds Heigth</em>}</li>
 *   <li>{@link gama.impl.EWorldAgentImpl#getBoundsPath <em>Bounds Path</em>}</li>
 *   <li>{@link gama.impl.EWorldAgentImpl#getBoundsExpression <em>Bounds Expression</em>}</li>
 *   <li>{@link gama.impl.EWorldAgentImpl#getBoundsType <em>Bounds Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EWorldAgentImpl extends ESpeciesImpl implements EWorldAgent {
	/**
	 * The default value of the '{@link #getBoundsWidth() <em>Bounds Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsWidth()
	 * @generated
	 * @ordered
	 */
	protected static final String BOUNDS_WIDTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBoundsWidth() <em>Bounds Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsWidth()
	 * @generated
	 * @ordered
	 */
	protected String boundsWidth = BOUNDS_WIDTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getBoundsHeigth() <em>Bounds Heigth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsHeigth()
	 * @generated
	 * @ordered
	 */
	protected static final String BOUNDS_HEIGTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBoundsHeigth() <em>Bounds Heigth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsHeigth()
	 * @generated
	 * @ordered
	 */
	protected String boundsHeigth = BOUNDS_HEIGTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getBoundsPath() <em>Bounds Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsPath()
	 * @generated
	 * @ordered
	 */
	protected static final String BOUNDS_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBoundsPath() <em>Bounds Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsPath()
	 * @generated
	 * @ordered
	 */
	protected String boundsPath = BOUNDS_PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getBoundsExpression() <em>Bounds Expression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsExpression()
	 * @generated
	 * @ordered
	 */
	protected static final String BOUNDS_EXPRESSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBoundsExpression() <em>Bounds Expression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsExpression()
	 * @generated
	 * @ordered
	 */
	protected String boundsExpression = BOUNDS_EXPRESSION_EDEFAULT;

	/**
	 * The default value of the '{@link #getBoundsType() <em>Bounds Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsType()
	 * @generated
	 * @ordered
	 */
	protected static final String BOUNDS_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getBoundsType() <em>Bounds Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getBoundsType()
	 * @generated
	 * @ordered
	 */
	protected String boundsType = BOUNDS_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EWorldAgentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EWORLD_AGENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBoundsWidth() {
		return boundsWidth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBoundsWidth(String newBoundsWidth) {
		String oldBoundsWidth = boundsWidth;
		boundsWidth = newBoundsWidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EWORLD_AGENT__BOUNDS_WIDTH, oldBoundsWidth, boundsWidth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBoundsHeigth() {
		return boundsHeigth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBoundsHeigth(String newBoundsHeigth) {
		String oldBoundsHeigth = boundsHeigth;
		boundsHeigth = newBoundsHeigth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EWORLD_AGENT__BOUNDS_HEIGTH, oldBoundsHeigth, boundsHeigth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBoundsPath() {
		return boundsPath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBoundsPath(String newBoundsPath) {
		String oldBoundsPath = boundsPath;
		boundsPath = newBoundsPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EWORLD_AGENT__BOUNDS_PATH, oldBoundsPath, boundsPath));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBoundsExpression() {
		return boundsExpression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBoundsExpression(String newBoundsExpression) {
		String oldBoundsExpression = boundsExpression;
		boundsExpression = newBoundsExpression;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EWORLD_AGENT__BOUNDS_EXPRESSION, oldBoundsExpression, boundsExpression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getBoundsType() {
		return boundsType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setBoundsType(String newBoundsType) {
		String oldBoundsType = boundsType;
		boundsType = newBoundsType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EWORLD_AGENT__BOUNDS_TYPE, oldBoundsType, boundsType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EWORLD_AGENT__BOUNDS_WIDTH:
				return getBoundsWidth();
			case GamaPackage.EWORLD_AGENT__BOUNDS_HEIGTH:
				return getBoundsHeigth();
			case GamaPackage.EWORLD_AGENT__BOUNDS_PATH:
				return getBoundsPath();
			case GamaPackage.EWORLD_AGENT__BOUNDS_EXPRESSION:
				return getBoundsExpression();
			case GamaPackage.EWORLD_AGENT__BOUNDS_TYPE:
				return getBoundsType();
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
			case GamaPackage.EWORLD_AGENT__BOUNDS_WIDTH:
				setBoundsWidth((String)newValue);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_HEIGTH:
				setBoundsHeigth((String)newValue);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_PATH:
				setBoundsPath((String)newValue);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_EXPRESSION:
				setBoundsExpression((String)newValue);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_TYPE:
				setBoundsType((String)newValue);
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
			case GamaPackage.EWORLD_AGENT__BOUNDS_WIDTH:
				setBoundsWidth(BOUNDS_WIDTH_EDEFAULT);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_HEIGTH:
				setBoundsHeigth(BOUNDS_HEIGTH_EDEFAULT);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_PATH:
				setBoundsPath(BOUNDS_PATH_EDEFAULT);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_EXPRESSION:
				setBoundsExpression(BOUNDS_EXPRESSION_EDEFAULT);
				return;
			case GamaPackage.EWORLD_AGENT__BOUNDS_TYPE:
				setBoundsType(BOUNDS_TYPE_EDEFAULT);
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
			case GamaPackage.EWORLD_AGENT__BOUNDS_WIDTH:
				return BOUNDS_WIDTH_EDEFAULT == null ? boundsWidth != null : !BOUNDS_WIDTH_EDEFAULT.equals(boundsWidth);
			case GamaPackage.EWORLD_AGENT__BOUNDS_HEIGTH:
				return BOUNDS_HEIGTH_EDEFAULT == null ? boundsHeigth != null : !BOUNDS_HEIGTH_EDEFAULT.equals(boundsHeigth);
			case GamaPackage.EWORLD_AGENT__BOUNDS_PATH:
				return BOUNDS_PATH_EDEFAULT == null ? boundsPath != null : !BOUNDS_PATH_EDEFAULT.equals(boundsPath);
			case GamaPackage.EWORLD_AGENT__BOUNDS_EXPRESSION:
				return BOUNDS_EXPRESSION_EDEFAULT == null ? boundsExpression != null : !BOUNDS_EXPRESSION_EDEFAULT.equals(boundsExpression);
			case GamaPackage.EWORLD_AGENT__BOUNDS_TYPE:
				return BOUNDS_TYPE_EDEFAULT == null ? boundsType != null : !BOUNDS_TYPE_EDEFAULT.equals(boundsType);
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
		result.append(" (boundsWidth: ");
		result.append(boundsWidth);
		result.append(", boundsHeigth: ");
		result.append(boundsHeigth);
		result.append(", boundsPath: ");
		result.append(boundsPath);
		result.append(", boundsExpression: ");
		result.append(boundsExpression);
		result.append(", boundsType: ");
		result.append(boundsType);
		result.append(')');
		return result.toString();
	}

} //EWorldAgentImpl
