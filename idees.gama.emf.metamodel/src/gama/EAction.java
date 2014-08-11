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
 * A representation of the model object '<em><b>EAction</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EAction#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.EAction#getActionLinks <em>Action Links</em>}</li>
 *   <li>{@link gama.EAction#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.EAction#getReturnType <em>Return Type</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEAction()
 * @model
 * @generated
 */
public interface EAction extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gaml Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gaml Code</em>' attribute.
	 * @see #setGamlCode(String)
	 * @see gama.GamaPackage#getEAction_GamlCode()
	 * @model derived="true"
	 * @generated
	 */
	String getGamlCode();

	/**
	 * Sets the value of the '{@link gama.EAction#getGamlCode <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gaml Code</em>' attribute.
	 * @see #getGamlCode()
	 * @generated
	 */
	void setGamlCode(String value);

	/**
	 * Returns the value of the '<em><b>Action Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EActionLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Action Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Action Links</em>' reference list.
	 * @see gama.GamaPackage#getEAction_ActionLinks()
	 * @model
	 * @generated
	 */
	EList<EActionLink> getActionLinks();

	/**
	 * Returns the value of the '<em><b>Variables</b></em>' containment reference list.
	 * The list contents are of type {@link gama.EVariable}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Variables</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Variables</em>' containment reference list.
	 * @see gama.GamaPackage#getEAction_Variables()
	 * @model containment="true"
	 * @generated
	 */
	EList<EVariable> getVariables();

	/**
	 * Returns the value of the '<em><b>Return Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Return Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Return Type</em>' attribute.
	 * @see #setReturnType(String)
	 * @see gama.GamaPackage#getEAction_ReturnType()
	 * @model
	 * @generated
	 */
	String getReturnType();

	/**
	 * Sets the value of the '{@link gama.EAction#getReturnType <em>Return Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Return Type</em>' attribute.
	 * @see #getReturnType()
	 * @generated
	 */
	void setReturnType(String value);

} // EAction
