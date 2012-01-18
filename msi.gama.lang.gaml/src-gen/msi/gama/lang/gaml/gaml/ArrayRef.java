/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Array Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.ArrayRef#getArray <em>Array</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.ArrayRef#getArgs <em>Args</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getArrayRef()
 * @model
 * @generated
 */
public interface ArrayRef extends Expression
{
  /**
   * Returns the value of the '<em><b>Array</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Array</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Array</em>' containment reference.
   * @see #setArray(VariableRef)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getArrayRef_Array()
   * @model containment="true"
   * @generated
   */
  VariableRef getArray();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.ArrayRef#getArray <em>Array</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Array</em>' containment reference.
   * @see #getArray()
   * @generated
   */
  void setArray(VariableRef value);

  /**
   * Returns the value of the '<em><b>Args</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.Expression}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Args</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Args</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getArrayRef_Args()
   * @model containment="true"
   * @generated
   */
  EList<Expression> getArgs();

} // ArrayRef
