/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Def Reserved</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.DefReserved#getType <em>Type</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.DefReserved#getValue <em>Value</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefReserved()
 * @model
 * @generated
 */
public interface DefReserved extends AbstractDefinition
{
  /**
   * Returns the value of the '<em><b>Type</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Type</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Type</em>' reference.
   * @see #setType(DefReserved)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefReserved_Type()
   * @model
   * @generated
   */
  DefReserved getType();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.DefReserved#getType <em>Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' reference.
   * @see #getType()
   * @generated
   */
  void setType(DefReserved value);

  /**
   * Returns the value of the '<em><b>Value</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Value</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Value</em>' containment reference.
   * @see #setValue(TerminalExpression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefReserved_Value()
   * @model containment="true"
   * @generated
   */
  TerminalExpression getValue();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.DefReserved#getValue <em>Value</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Value</em>' containment reference.
   * @see #getValue()
   * @generated
   */
  void setValue(TerminalExpression value);

} // DefReserved
