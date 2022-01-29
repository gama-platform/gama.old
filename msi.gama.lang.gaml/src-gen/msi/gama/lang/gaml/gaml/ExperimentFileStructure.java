/*******************************************************************************************************
 *
 * ExperimentFileStructure.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Experiment File Structure</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.ExperimentFileStructure#getExp <em>Exp</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getExperimentFileStructure()
 * @model
 * @generated
 */
public interface ExperimentFileStructure extends Entry
{
  /**
   * Returns the value of the '<em><b>Exp</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Exp</em>' containment reference.
   * @see #setExp(HeadlessExperiment)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getExperimentFileStructure_Exp()
   * @model containment="true"
   * @generated
   */
  HeadlessExperiment getExp();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.ExperimentFileStructure#getExp <em>Exp</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Exp</em>' containment reference.
   * @see #getExp()
   * @generated
   */
  void setExp(HeadlessExperiment value);

} // ExperimentFileStructure
