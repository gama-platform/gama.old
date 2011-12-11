/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Function Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.FunctionRef#getFunc <em>Func</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.FunctionRef#getArgs <em>Args</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getFunctionRef()
 * @model
 * @generated
 */
public interface FunctionRef extends Expression
{
  /**
   * Returns the value of the '<em><b>Func</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Func</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Func</em>' containment reference.
   * @see #setFunc(VariableRef)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getFunctionRef_Func()
   * @model containment="true"
   * @generated
   */
  VariableRef getFunc();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.FunctionRef#getFunc <em>Func</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Func</em>' containment reference.
   * @see #getFunc()
   * @generated
   */
  void setFunc(VariableRef value);

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
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getFunctionRef_Args()
   * @model containment="true"
   * @generated
   */
  EList<Expression> getArgs();

} // FunctionRef
