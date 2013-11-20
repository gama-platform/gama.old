/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EAspect Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EAspectLink#getAspect <em>Aspect</em>}</li>
 *   <li>{@link gama.EAspectLink#getSpecies <em>Species</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEAspectLink()
 * @model
 * @generated
 */
public interface EAspectLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Aspect</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Aspect</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Aspect</em>' reference.
	 * @see #setAspect(EAspect)
	 * @see gama.GamaPackage#getEAspectLink_Aspect()
	 * @model
	 * @generated
	 */
	EAspect getAspect();

	/**
	 * Sets the value of the '{@link gama.EAspectLink#getAspect <em>Aspect</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Aspect</em>' reference.
	 * @see #getAspect()
	 * @generated
	 */
	void setAspect(EAspect value);

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
	 * @see gama.GamaPackage#getEAspectLink_Species()
	 * @model
	 * @generated
	 */
	ESpecies getSpecies();

	/**
	 * Sets the value of the '{@link gama.EAspectLink#getSpecies <em>Species</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Species</em>' reference.
	 * @see #getSpecies()
	 * @generated
	 */
	void setSpecies(ESpecies value);

} // EAspectLink
