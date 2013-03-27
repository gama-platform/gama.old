/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>EExperiment</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.EExperiment#getExperimentLinks <em>Experiment Links</em>}</li>
 *   <li>{@link gama.EExperiment#getDisplayLinks <em>Display Links</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getEExperiment()
 * @model
 * @generated
 */
public interface EExperiment extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Experiment Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EExperimentLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Experiment Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Experiment Links</em>' reference list.
	 * @see gama.GamaPackage#getEExperiment_ExperimentLinks()
	 * @model
	 * @generated
	 */
	EList<EExperimentLink> getExperimentLinks();

	/**
	 * Returns the value of the '<em><b>Display Links</b></em>' reference list.
	 * The list contents are of type {@link gama.EDisplayLink}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display Links</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display Links</em>' reference list.
	 * @see gama.GamaPackage#getEExperiment_DisplayLinks()
	 * @model
	 * @generated
	 */
	EList<EDisplayLink> getDisplayLinks();

} // EExperiment
