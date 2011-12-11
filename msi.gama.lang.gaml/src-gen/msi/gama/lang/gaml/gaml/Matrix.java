/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Matrix</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Matrix#getRows <em>Rows</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getMatrix()
 * @model
 * @generated
 */
public interface Matrix extends Expression
{
  /**
   * Returns the value of the '<em><b>Rows</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.Row}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Rows</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Rows</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getMatrix_Rows()
   * @model containment="true"
   * @generated
   */
  EList<Row> getRows();

} // Matrix
