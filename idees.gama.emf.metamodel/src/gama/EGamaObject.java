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
 *   <li>{@link gama.EGamaObject#getColorPicto <em>Color Picto</em>}</li>
 *   <li>{@link gama.EGamaObject#getHasError <em>Has Error</em>}</li>
 *   <li>{@link gama.EGamaObject#getError <em>Error</em>}</li>
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
	 * Returns the value of the '<em><b>Color Picto</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color Picto</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color Picto</em>' attribute list.
	 * @see gama.GamaPackage#getEGamaObject_ColorPicto()
	 * @model unique="false" upper="3"
	 * @generated
	 */
	EList<Integer> getColorPicto();

	/**
	 * Returns the value of the '<em><b>Has Error</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Has Error</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Has Error</em>' attribute.
	 * @see #setHasError(Boolean)
	 * @see gama.GamaPackage#getEGamaObject_HasError()
	 * @model default="false"
	 * @generated
	 */
	Boolean getHasError();

	/**
	 * Sets the value of the '{@link gama.EGamaObject#getHasError <em>Has Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Has Error</em>' attribute.
	 * @see #getHasError()
	 * @generated
	 */
	void setHasError(Boolean value);

	/**
	 * Returns the value of the '<em><b>Error</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Error</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Error</em>' attribute.
	 * @see #setError(String)
	 * @see gama.GamaPackage#getEGamaObject_Error()
	 * @model
	 * @generated
	 */
	String getError();

	/**
	 * Sets the value of the '{@link gama.EGamaObject#getError <em>Error</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Error</em>' attribute.
	 * @see #getError()
	 * @generated
	 */
	void setError(String value);

} // EGamaObject
