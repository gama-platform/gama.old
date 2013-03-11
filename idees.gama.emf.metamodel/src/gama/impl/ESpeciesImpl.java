/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.ESpecies;
import gama.EVariable;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ESpecies</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ESpeciesImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTopology <em>Topology</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getReflexList <em>Reflex List</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTorus <em>Torus</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ESpeciesImpl extends EGamaObjectImpl implements ESpecies {
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
	 * The default value of the '{@link #getTopology() <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopology()
	 * @generated
	 * @ordered
	 */
	protected static final String TOPOLOGY_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getTopology() <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopology()
	 * @generated
	 * @ordered
	 */
	protected String topology = TOPOLOGY_EDEFAULT;
	/**
	 * The cached value of the '{@link #getReflexList() <em>Reflex List</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReflexList()
	 * @generated
	 * @ordered
	 */
	protected EList<String> reflexList;

	/**
	 * The default value of the '{@link #getTorus() <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorus()
	 * @generated
	 * @ordered
	 */
	protected static final String TORUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTorus() <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorus()
	 * @generated
	 * @ordered
	 */
	protected String torus = TORUS_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ESpeciesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.ESPECIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EVariable> getVariables() {
		if (variables == null) {
			variables = new EObjectContainmentEList<EVariable>(EVariable.class, this, GamaPackage.ESPECIES__VARIABLES);
		}
		return variables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTopology() {
		return topology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTopology(String newTopology) {
		String oldTopology = topology;
		topology = newTopology;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TOPOLOGY, oldTopology, topology));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getReflexList() {
		if (reflexList == null) {
			reflexList = new EDataTypeUniqueEList<String>(String.class, this, GamaPackage.ESPECIES__REFLEX_LIST);
		}
		return reflexList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTorus() {
		return torus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTorus(String newTorus) {
		String oldTorus = torus;
		torus = newTorus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TORUS, oldTorus, torus));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.ESPECIES__VARIABLES:
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
			case GamaPackage.ESPECIES__VARIABLES:
				return getVariables();
			case GamaPackage.ESPECIES__TOPOLOGY:
				return getTopology();
			case GamaPackage.ESPECIES__REFLEX_LIST:
				return getReflexList();
			case GamaPackage.ESPECIES__TORUS:
				return getTorus();
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
			case GamaPackage.ESPECIES__VARIABLES:
				getVariables().clear();
				getVariables().addAll((Collection<? extends EVariable>)newValue);
				return;
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology((String)newValue);
				return;
			case GamaPackage.ESPECIES__REFLEX_LIST:
				getReflexList().clear();
				getReflexList().addAll((Collection<? extends String>)newValue);
				return;
			case GamaPackage.ESPECIES__TORUS:
				setTorus((String)newValue);
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
			case GamaPackage.ESPECIES__VARIABLES:
				getVariables().clear();
				return;
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology(TOPOLOGY_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__REFLEX_LIST:
				getReflexList().clear();
				return;
			case GamaPackage.ESPECIES__TORUS:
				setTorus(TORUS_EDEFAULT);
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
			case GamaPackage.ESPECIES__VARIABLES:
				return variables != null && !variables.isEmpty();
			case GamaPackage.ESPECIES__TOPOLOGY:
				return TOPOLOGY_EDEFAULT == null ? topology != null : !TOPOLOGY_EDEFAULT.equals(topology);
			case GamaPackage.ESPECIES__REFLEX_LIST:
				return reflexList != null && !reflexList.isEmpty();
			case GamaPackage.ESPECIES__TORUS:
				return TORUS_EDEFAULT == null ? torus != null : !TORUS_EDEFAULT.equals(torus);
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
		result.append(" (topology: ");
		result.append(topology);
		result.append(", reflexList: ");
		result.append(reflexList);
		result.append(", torus: ");
		result.append(torus);
		result.append(')');
		return result.toString();
	}

} //ESpeciesImpl
