/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EReflex Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EReflexLink#getReflex <em>Reflex</em>}</li>
 *   <li>{@link gama.EReflexLink#getSpecies <em>Species</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEReflexLink()
 * @model
 * @generated
 */
public interface EReflexLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Reflex</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Reflex</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Reflex</em>' reference.
	 * @see #setReflex(EReflex)
	 * @see gama.GamaPackage#getEReflexLink_Reflex()
	 * @model
	 * @generated
	 */
	EReflex getReflex();

	/**
	 * Sets the value of the '{@link gama.EReflexLink#getReflex <em>Reflex</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Reflex</em>' reference.
	 * @see #getReflex()
	 * @generated
	 */
	void setReflex(EReflex value);

	/**
	 * Returns the value of the '<em><b>Species</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Species</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Species</em>' reference.
	 * @see #setSpecies(ESpecies)
	 * @see gama.GamaPackage#getEReflexLink_Species()
	 * @model
	 * @generated
	 */
	ESpecies getSpecies();

	/**
	 * Sets the value of the '{@link gama.EReflexLink#getSpecies <em>Species</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Species</em>' reference.
	 * @see #getSpecies()
	 * @generated
	 */
	void setSpecies(ESpecies value);

} // EReflexLink
