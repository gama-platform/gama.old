/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EGraph Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EGraphLink#getNode <em>Node</em>}</li>
 *   <li>{@link gama.EGraphLink#getEdge <em>Edge</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEGraphLink()
 * @model
 * @generated
 */
public interface EGraphLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Node</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Node</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Node</em>' reference.
	 * @see #setNode(ESpecies)
	 * @see gama.GamaPackage#getEGraphLink_Node()
	 * @model
	 * @generated
	 */
	ESpecies getNode();

	/**
	 * Sets the value of the '{@link gama.EGraphLink#getNode <em>Node</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Node</em>' reference.
	 * @see #getNode()
	 * @generated
	 */
	void setNode(ESpecies value);

	/**
	 * Returns the value of the '<em><b>Edge</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Edge</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Edge</em>' reference.
	 * @see #setEdge(ESpecies)
	 * @see gama.GamaPackage#getEGraphLink_Edge()
	 * @model
	 * @generated
	 */
	ESpecies getEdge();

	/**
	 * Sets the value of the '{@link gama.EGraphLink#getEdge <em>Edge</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Edge</em>' reference.
	 * @see #getEdge()
	 * @generated
	 */
	void setEdge(ESpecies value);

} // EGraphLink
