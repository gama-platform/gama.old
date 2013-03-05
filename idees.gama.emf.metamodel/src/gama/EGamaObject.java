/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGama Object</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EGamaObject#getName <em>Name</em>}</li>
 *   <li>{@link gama.EGamaObject#getModel <em>Model</em>}</li>
 *   <li>{@link gama.EGamaObject#getIncomingLinks <em>Incoming Links</em>}</li>
 *   <li>{@link gama.EGamaObject#getOutcomingLinks <em>Outcoming Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEGamaObject()
 * @model
 * @generated
 */
public interface EGamaObject extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see gama.GamaPackage#getEGamaObject_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link gama.EGamaObject#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

	/**
	 * Returns the value of the '<em><b>Model</b></em>' container reference.
	 * It is bidirectional and its opposite is '{@link gama.EGamaModel#getObjects <em>Objects</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Model</em>' container reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Model</em>' container reference.
	 * @see #setModel(EGamaModel)
	 * @see gama.GamaPackage#getEGamaObject_Model()
	 * @see gama.EGamaModel#getObjects
	 * @model opposite="objects" required="true" transient="false"
	 * @generated
	 */
	EGamaModel getModel();

	/**
	 * Sets the value of the '{@link gama.EGamaObject#getModel <em>Model</em>}' container reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Model</em>' container reference.
	 * @see #getModel()
	 * @generated
	 */
	void setModel(EGamaModel value);

	/**
	 * Returns the value of the '<em><b>Incoming Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EGamaLink}.
	 * It is bidirectional and its opposite is '{@link gama.EGamaLink#getTarget <em>Target</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Incoming Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Incoming Links</em>' reference list.
	 * @see gama.GamaPackage#getEGamaObject_IncomingLinks()
	 * @see gama.EGamaLink#getTarget
	 * @model opposite="target"
	 * @generated
	 */
	EList<EGamaLink> getIncomingLinks();

	/**
	 * Returns the value of the '<em><b>Outcoming Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EGamaLink}.
	 * It is bidirectional and its opposite is '{@link gama.EGamaLink#getSource <em>Source</em>}'.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Outcoming Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Outcoming Links</em>' reference list.
	 * @see gama.GamaPackage#getEGamaObject_OutcomingLinks()
	 * @see gama.EGamaLink#getSource
	 * @model opposite="source"
	 * @generated
	 */
	EList<EGamaLink> getOutcomingLinks();

} // EGamaObject
