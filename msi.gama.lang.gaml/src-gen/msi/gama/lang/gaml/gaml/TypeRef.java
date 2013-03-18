/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeRef#getRef <em>Ref</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeRef#getOf <em>Of</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeRef()
 * @model
 * @generated
 */
public interface TypeRef extends Expression
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
   * @see #setRef(TypeDefinition)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeRef_Ref()
   * @model
   * @generated
   */
  TypeDefinition getRef();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeRef#getRef <em>Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Ref</em>' reference.
   * @see #getRef()
   * @generated
   */
  void setRef(TypeDefinition value);

  /**
   * Returns the value of the '<em><b>Of</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Of</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Of</em>' containment reference.
   * @see #setOf(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeRef_Of()
   * @model containment="true"
   * @generated
   */
  Expression getOf();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeRef#getOf <em>Of</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Of</em>' containment reference.
   * @see #getOf()
   * @generated
   */
  void setOf(Expression value);

} // TypeRef
