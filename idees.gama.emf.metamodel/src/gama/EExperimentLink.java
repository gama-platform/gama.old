/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EExperiment Link</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EExperimentLink#getSpecies <em>Species</em>}</li>
 *   <li>{@link gama.EExperimentLink#getExperiment <em>Experiment</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEExperimentLink()
 * @model
 * @generated
 */
public interface EExperimentLink extends EGamaLink {
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
	 * @see gama.GamaPackage#getEExperimentLink_Species()
	 * @model
	 * @generated
	 */
	ESpecies getSpecies();

	/**
	 * Sets the value of the '{@link gama.EExperimentLink#getSpecies <em>Species</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Species</em>' reference.
	 * @see #getSpecies()
	 * @generated
	 */
	void setSpecies(ESpecies value);

	/**
	 * Returns the value of the '<em><b>Experiment</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Experiment</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Experiment</em>' reference.
	 * @see #setExperiment(EExperiment)
	 * @see gama.GamaPackage#getEExperimentLink_Experiment()
	 * @model
	 * @generated
	 */
	EExperiment getExperiment();

	/**
	 * Sets the value of the '{@link gama.EExperimentLink#getExperiment <em>Experiment</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Experiment</em>' reference.
	 * @see #getExperiment()
	 * @generated
	 */
	void setExperiment(EExperiment value);

} // EExperimentLink
