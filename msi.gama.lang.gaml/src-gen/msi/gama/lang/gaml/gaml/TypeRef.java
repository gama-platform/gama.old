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
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeRef#getFirst <em>First</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeRef#getSecond <em>Second</em>}</li>
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
   * Returns the value of the '<em><b>First</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>First</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>First</em>' containment reference.
   * @see #setFirst(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeRef_First()
   * @model containment="true"
   * @generated
   */
  Expression getFirst();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeRef#getFirst <em>First</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First</em>' containment reference.
   * @see #getFirst()
   * @generated
   */
  void setFirst(Expression value);

  /**
   * Returns the value of the '<em><b>Second</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Second</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Second</em>' containment reference.
   * @see #setSecond(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeRef_Second()
   * @model containment="true"
   * @generated
   */
  Expression getSecond();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeRef#getSecond <em>Second</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Second</em>' containment reference.
   * @see #getSecond()
   * @generated
   */
  void setSecond(Expression value);

} // TypeRef
