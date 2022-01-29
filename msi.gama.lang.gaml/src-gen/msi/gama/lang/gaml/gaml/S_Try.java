/*******************************************************************************************************
 *
 * S_Try.java, in msi.gama.lang.gaml, is part of the source code of the
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
 * A representation of the model object '<em><b>STry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Try#getCatch <em>Catch</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Try()
 * @model
 * @generated
 */
public interface S_Try extends Statement
{
  /**
   * Returns the value of the '<em><b>Catch</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Catch</em>' containment reference.
   * @see #setCatch(Block)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Try_Catch()
   * @model containment="true"
   * @generated
   */
  Block getCatch();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.S_Try#getCatch <em>Catch</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Catch</em>' containment reference.
   * @see #getCatch()
   * @generated
   */
  void setCatch(Block value);

} // S_Try
