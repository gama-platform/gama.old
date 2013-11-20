/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EDisplay Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EDisplayLink#getExperiment <em>Experiment</em>}</li>
 *   <li>{@link gama.EDisplayLink#getDisplay <em>Display</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEDisplayLink()
 * @model
 * @generated
 */
public interface EDisplayLink extends EGamaLink {
	/**
	 * Returns the value of the '<em><b>Experiment</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Experiment</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Experiment</em>' reference.
	 * @see #setExperiment(EGUIExperiment)
	 * @see gama.GamaPackage#getEDisplayLink_Experiment()
	 * @model
	 * @generated
	 */
	EGUIExperiment getExperiment();

	/**
	 * Sets the value of the '{@link gama.EDisplayLink#getExperiment <em>Experiment</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Experiment</em>' reference.
	 * @see #getExperiment()
	 * @generated
	 */
	void setExperiment(EGUIExperiment value);

	/**
	 * Returns the value of the '<em><b>Display</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display</em>' reference.
	 * @see #setDisplay(EDisplay)
	 * @see gama.GamaPackage#getEDisplayLink_Display()
	 * @model
	 * @generated
	 */
	EDisplay getDisplay();

	/**
	 * Sets the value of the '{@link gama.EDisplayLink#getDisplay <em>Display</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Display</em>' reference.
	 * @see #getDisplay()
	 * @generated
	 */
	void setDisplay(EDisplay value);

} // EDisplayLink
