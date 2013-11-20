/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGama Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EGamaLink#getTarget <em>Target</em>}</li>
 *   <li>{@link gama.EGamaLink#getSource <em>Source</em>}</li>
 *   <li>{@link gama.EGamaLink#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEGamaLink()
 * @model
 * @generated
 */
public interface EGamaLink extends EObject {
	/**
	 * Returns the value of the '<em><b>Target</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Target</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Target</em>' reference.
	 * @see #setTarget(EGamaObject)
	 * @see gama.GamaPackage#getEGamaLink_Target()
	 * @model required="true"
	 * @generated
	 */
	EGamaObject getTarget();

	/**
	 * Sets the value of the '{@link gama.EGamaLink#getTarget <em>Target</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Target</em>' reference.
	 * @see #getTarget()
	 * @generated
	 */
	void setTarget(EGamaObject value);

	/**
	 * Returns the value of the '<em><b>Source</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Source</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Source</em>' reference.
	 * @see #setSource(EGamaObject)
	 * @see gama.GamaPackage#getEGamaLink_Source()
	 * @model required="true"
	 * @generated
	 */
	EGamaObject getSource();

	/**
	 * Sets the value of the '{@link gama.EGamaLink#getSource <em>Source</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Source</em>' reference.
	 * @see #getSource()
	 * @generated
	 */
	void setSource(EGamaObject value);

	/**
	 * Returns the value of the '<em><b>Model</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gama.EGamaModel#getLinks <em>Links</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model</em>' container reference.
	 * @see #setModel(EGamaModel)
	 * @see gama.GamaPackage#getEGamaLink_Model()
	 * @see gama.EGamaModel#getLinks
	 * @model opposite="links" required="true" transient="false"
	 * @generated
	 */
	EGamaModel getModel();

	/**
	 * Sets the value of the '{@link gama.EGamaLink#getModel <em>Model</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model</em>' container reference.
	 * @see #getModel()
	 * @generated
	 */
	void setModel(EGamaModel value);

} // EGamaLink
