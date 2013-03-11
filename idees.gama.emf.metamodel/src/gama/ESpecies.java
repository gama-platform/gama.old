/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ESpecies</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.ESpecies#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.ESpecies#getTopology <em>Topology</em>}</li>
 *   <li>{@link gama.ESpecies#getReflexList <em>Reflex List</em>}</li>
 *   <li>{@link gama.ESpecies#getTorus <em>Torus</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getESpecies()
 * @model
 * @generated
 */
public interface ESpecies extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
	 * The list contents are of type {@link gama.EVariable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variables</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variables</em>' containment reference list.
	 * @see gama.GamaPackage#getESpecies_Variables()
	 * @model containment="true"
	 * @generated
	 */
	EList<EVariable> getVariables();

	/**
	 * Returns the value of the '<em><b>Topology</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Topology</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Topology</em>' attribute.
	 * @see #setTopology(String)
	 * @see gama.GamaPackage#getESpecies_Topology()
	 * @model
	 * @generated
	 */
	String getTopology();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTopology <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Topology</em>' attribute.
	 * @see #getTopology()
	 * @generated
	 */
	void setTopology(String value);

	/**
	 * Returns the value of the '<em><b>Reflex List</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reflex List</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reflex List</em>' attribute list.
	 * @see gama.GamaPackage#getESpecies_ReflexList()
	 * @model
	 * @generated
	 */
	EList<String> getReflexList();

	/**
	 * Returns the value of the '<em><b>Torus</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Torus</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Torus</em>' attribute.
	 * @see #setTorus(String)
	 * @see gama.GamaPackage#getESpecies_Torus()
	 * @model
	 * @generated
	 */
	String getTorus();

	/**
	 * Sets the value of the '{@link gama.ESpecies#getTorus <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Torus</em>' attribute.
	 * @see #getTorus()
	 * @generated
	 */
	void setTorus(String value);

} // ESpecies
