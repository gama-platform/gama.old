/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EInherit Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EInheritLink#getParent <em>Parent</em>}</li>
 *   <li>{@link gama.EInheritLink#getChild <em>Child</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEInheritLink()
 * @model
 * @generated
 */
public interface EInheritLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Parent</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Parent</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Parent</em>' reference.
	 * @see #setParent(ESpecies)
	 * @see gama.GamaPackage#getEInheritLink_Parent()
	 * @model
	 * @generated
	 */
	ESpecies getParent();

	/**
	 * Sets the value of the '{@link gama.EInheritLink#getParent <em>Parent</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Parent</em>' reference.
	 * @see #getParent()
	 * @generated
	 */
	void setParent(ESpecies value);

	/**
	 * Returns the value of the '<em><b>Child</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Child</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Child</em>' reference.
	 * @see #setChild(ESpecies)
	 * @see gama.GamaPackage#getEInheritLink_Child()
	 * @model
	 * @generated
	 */
	ESpecies getChild();

	/**
	 * Sets the value of the '{@link gama.EInheritLink#getChild <em>Child</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Child</em>' reference.
	 * @see #getChild()
	 * @generated
	 */
	void setChild(ESpecies value);

} // EInheritLink
