/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EDisplay;
import gama.ELayer;
import gama.GamaPackage;

import java.util.Collection;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>EDisplay</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.EDisplayImpl#getLayers <em>Layers</em>}</li>
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
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.EDISPLAY__LAYERS:
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
			case GamaPackage.EDISPLAY__LAYERS:
				getLayers().clear();
				getLayers().addAll((Collection<? extends ELayer>)newValue);
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
		}
		return super.eIsSet(featureID);
	}

} //EDisplayImpl
