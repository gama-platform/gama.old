/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EGridTopology;
import gama.GamaPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EGrid Topology</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EGridTopologyImpl#getNb_columns <em>Nb columns</em>}</li>
 *   <li>{@link gama.impl.EGridTopologyImpl#getNb_rows <em>Nb rows</em>}</li>
 *   <li>{@link gama.impl.EGridTopologyImpl#getNeighbourhood <em>Neighbourhood</em>}</li>
 *   <li>{@link gama.impl.EGridTopologyImpl#getNeighbourhoodType <em>Neighbourhood Type</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class EGridTopologyImpl extends ETopologyImpl implements EGridTopology {
	/**
	 * The default value of the '{@link #getNb_columns() <em>Nb columns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNb_columns()
	 * @generated
	 * @ordered
	 */
	protected static final String NB_COLUMNS_EDEFAULT = "100";

	/**
	 * The cached value of the '{@link #getNb_columns() <em>Nb columns</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNb_columns()
	 * @generated
	 * @ordered
	 */
	protected String nb_columns = NB_COLUMNS_EDEFAULT;

	/**
	 * The default value of the '{@link #getNb_rows() <em>Nb rows</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNb_rows()
	 * @generated
	 * @ordered
	 */
	protected static final String NB_ROWS_EDEFAULT = "100";

	/**
	 * The cached value of the '{@link #getNb_rows() <em>Nb rows</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNb_rows()
	 * @generated
	 * @ordered
	 */
	protected String nb_rows = NB_ROWS_EDEFAULT;

	/**
	 * The default value of the '{@link #getNeighbourhood() <em>Neighbourhood</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNeighbourhood()
	 * @generated
	 * @ordered
	 */
	protected static final String NEIGHBOURHOOD_EDEFAULT = "";

	/**
	 * The cached value of the '{@link #getNeighbourhood() <em>Neighbourhood</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNeighbourhood()
	 * @generated
	 * @ordered
	 */
	protected String neighbourhood = NEIGHBOURHOOD_EDEFAULT;

	/**
	 * The default value of the '{@link #getNeighbourhoodType() <em>Neighbourhood Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNeighbourhoodType()
	 * @generated
	 * @ordered
	 */
	protected static final String NEIGHBOURHOOD_TYPE_EDEFAULT = "4 (square - von Neumann)";

	/**
	 * The cached value of the '{@link #getNeighbourhoodType() <em>Neighbourhood Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNeighbourhoodType()
	 * @generated
	 * @ordered
	 */
	protected String neighbourhoodType = NEIGHBOURHOOD_TYPE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected EGridTopologyImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.EGRID_TOPOLOGY;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNb_columns() {
		return nb_columns;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNb_columns(String newNb_columns) {
		String oldNb_columns = nb_columns;
		nb_columns = newNb_columns;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGRID_TOPOLOGY__NB_COLUMNS, oldNb_columns, nb_columns));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNb_rows() {
		return nb_rows;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNb_rows(String newNb_rows) {
		String oldNb_rows = nb_rows;
		nb_rows = newNb_rows;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGRID_TOPOLOGY__NB_ROWS, oldNb_rows, nb_rows));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNeighbourhood() {
		return neighbourhood;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNeighbourhood(String newNeighbourhood) {
		String oldNeighbourhood = neighbourhood;
		neighbourhood = newNeighbourhood;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD, oldNeighbourhood, neighbourhood));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getNeighbourhoodType() {
		return neighbourhoodType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNeighbourhoodType(String newNeighbourhoodType) {
		String oldNeighbourhoodType = neighbourhoodType;
		neighbourhoodType = newNeighbourhoodType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE, oldNeighbourhoodType, neighbourhoodType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EGRID_TOPOLOGY__NB_COLUMNS:
				return getNb_columns();
			case GamaPackage.EGRID_TOPOLOGY__NB_ROWS:
				return getNb_rows();
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD:
				return getNeighbourhood();
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE:
				return getNeighbourhoodType();
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
			case GamaPackage.EGRID_TOPOLOGY__NB_COLUMNS:
				setNb_columns((String)newValue);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NB_ROWS:
				setNb_rows((String)newValue);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD:
				setNeighbourhood((String)newValue);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE:
				setNeighbourhoodType((String)newValue);
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
			case GamaPackage.EGRID_TOPOLOGY__NB_COLUMNS:
				setNb_columns(NB_COLUMNS_EDEFAULT);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NB_ROWS:
				setNb_rows(NB_ROWS_EDEFAULT);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD:
				setNeighbourhood(NEIGHBOURHOOD_EDEFAULT);
				return;
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE:
				setNeighbourhoodType(NEIGHBOURHOOD_TYPE_EDEFAULT);
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
			case GamaPackage.EGRID_TOPOLOGY__NB_COLUMNS:
				return NB_COLUMNS_EDEFAULT == null ? nb_columns != null : !NB_COLUMNS_EDEFAULT.equals(nb_columns);
			case GamaPackage.EGRID_TOPOLOGY__NB_ROWS:
				return NB_ROWS_EDEFAULT == null ? nb_rows != null : !NB_ROWS_EDEFAULT.equals(nb_rows);
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD:
				return NEIGHBOURHOOD_EDEFAULT == null ? neighbourhood != null : !NEIGHBOURHOOD_EDEFAULT.equals(neighbourhood);
			case GamaPackage.EGRID_TOPOLOGY__NEIGHBOURHOOD_TYPE:
				return NEIGHBOURHOOD_TYPE_EDEFAULT == null ? neighbourhoodType != null : !NEIGHBOURHOOD_TYPE_EDEFAULT.equals(neighbourhoodType);
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
		result.append(" (nb_columns: ");
		result.append(nb_columns);
		result.append(", nb_rows: ");
		result.append(nb_rows);
		result.append(", neighbourhood: ");
		result.append(neighbourhood);
		result.append(", neighbourhoodType: ");
		result.append(neighbourhoodType);
		result.append(')');
		return result.toString();
	}

} //EGridTopologyImpl
