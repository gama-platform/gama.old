/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Statement</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.SubStatement#getKey <em>Key</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getSubStatement()
 * @model
 * @generated
 */
public interface SubStatement extends Statement
{
  /**
   * Returns the value of the '<em><b>Key</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Key</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Key</em>' containment reference.
   * @see #setKey(GamlKeywordRef)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getSubStatement_Key()
   * @model containment="true"
   * @generated
   */
  GamlKeywordRef getKey();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.SubStatement#getKey <em>Key</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Key</em>' containment reference.
   * @see #getKey()
   * @generated
   */
  void setKey(GamlKeywordRef value);

} // SubStatement
