/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SDo</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Do#getParams <em>Params</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Do()
 * @model
 * @generated
 */
public interface S_Do extends Statement
{
  /**
   * Returns the value of the '<em><b>Params</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Params</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Params</em>' containment reference.
   * @see #setParams(Parameters)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Do_Params()
   * @model containment="true"
   * @generated
   */
  Parameters getParams();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.S_Do#getParams <em>Params</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Params</em>' containment reference.
   * @see #getParams()
   * @generated
   */
  void setParams(Parameters value);

} // S_Do
