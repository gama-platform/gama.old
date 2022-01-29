/*******************************************************************************************************
 *
 * TypeInfo.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Type Info</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeInfo#getFirst <em>First</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.TypeInfo#getSecond <em>Second</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeInfo()
 * @model
 * @generated
 */
public interface TypeInfo extends EObject
{
  /**
   * Returns the value of the '<em><b>First</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>First</em>' containment reference.
   * @see #setFirst(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeInfo_First()
   * @model containment="true"
   * @generated
   */
  Expression getFirst();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeInfo#getFirst <em>First</em>}' containment reference.
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
   * <!-- end-user-doc -->
   * @return the value of the '<em>Second</em>' containment reference.
   * @see #setSecond(Expression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getTypeInfo_Second()
   * @model containment="true"
   * @generated
   */
  Expression getSecond();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.TypeInfo#getSecond <em>Second</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Second</em>' containment reference.
   * @see #getSecond()
   * @generated
   */
  void setSecond(Expression value);

} // TypeInfo
