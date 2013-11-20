/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ESub Species Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.ESubSpeciesLink#getMacro <em>Macro</em>}</li>
 *   <li>{@link gama.ESubSpeciesLink#getMicro <em>Micro</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getESubSpeciesLink()
 * @model
 * @generated
 */
public interface ESubSpeciesLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Macro</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Macro</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Macro</em>' reference.
	 * @see #setMacro(ESpecies)
	 * @see gama.GamaPackage#getESubSpeciesLink_Macro()
	 * @model
	 * @generated
	 */
	ESpecies getMacro();

	/**
	 * Sets the value of the '{@link gama.ESubSpeciesLink#getMacro <em>Macro</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Macro</em>' reference.
	 * @see #getMacro()
	 * @generated
	 */
	void setMacro(ESpecies value);

	/**
	 * Returns the value of the '<em><b>Micro</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Micro</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Micro</em>' reference.
	 * @see #setMicro(ESpecies)
	 * @see gama.GamaPackage#getESubSpeciesLink_Micro()
	 * @model
	 * @generated
	 */
	ESpecies getMicro();

	/**
	 * Sets the value of the '{@link gama.ESubSpeciesLink#getMicro <em>Micro</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Micro</em>' reference.
	 * @see #getMicro()
	 * @generated
	 */
	void setMicro(ESpecies value);

} // ESubSpeciesLink
