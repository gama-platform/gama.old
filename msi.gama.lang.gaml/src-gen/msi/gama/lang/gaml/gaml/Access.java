/*********************************************************************************************
 *
 * 'Access.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.gaml;


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Access</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Access#getArgs <em>Args</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.Access#getNamed_exp <em>Named exp</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getAccess()
 * @model
 * @generated
 */
public interface Access extends Expression
{
  /**
   * Returns the value of the '<em><b>Args</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Args</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Args</em>' containment reference.
   * @see #setArgs(ExpressionList)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getAccess_Args()
   * @model containment="true"
   * @generated
   */
  ExpressionList getArgs();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Access#getArgs <em>Args</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Args</em>' containment reference.
   * @see #getArgs()
   * @generated
   */
  void setArgs(ExpressionList value);

  /**
   * Returns the value of the '<em><b>Named exp</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Named exp</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Named exp</em>' attribute.
   * @see #setNamed_exp(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getAccess_Named_exp()
   * @model
   * @generated
   */
  String getNamed_exp();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Access#getNamed_exp <em>Named exp</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Named exp</em>' attribute.
   * @see #getNamed_exp()
   * @generated
   */
  void setNamed_exp(String value);

} // Access
