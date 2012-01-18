/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Binar Op Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlBinarOpRef#getRef <em>Ref</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlBinarOpRef()
 * @model
 * @generated
 */
public interface GamlBinarOpRef extends AbstractGamlRef
{
  /**
   * Returns the value of the '<em><b>Ref</b></em>' reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Ref</em>' reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Ref</em>' reference.
   * @see #setRef(DefBinaryOp)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlBinarOpRef_Ref()
   * @model
   * @generated
   */
  DefBinaryOp getRef();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.GamlBinarOpRef#getRef <em>Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ref</em>' reference.
   * @see #getRef()
   * @generated
   */
  void setRef(DefBinaryOp value);

} // GamlBinarOpRef
