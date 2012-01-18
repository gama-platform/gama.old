/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Def Facet</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.DefFacet#getName <em>Name</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.DefFacet#getType <em>Type</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.DefFacet#getDefault <em>Default</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefFacet()
 * @model
 * @generated
 */
public interface DefFacet extends EObject
{
  /**
   * Returns the value of the '<em><b>Name</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Name</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Name</em>' attribute.
   * @see #setName(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefFacet_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.DefFacet#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

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
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefFacet_Type()
   * @model
   * @generated
   */
  DefReserved getType();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.DefFacet#getType <em>Type</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Type</em>' reference.
   * @see #getType()
   * @generated
   */
  void setType(DefReserved value);

  /**
   * Returns the value of the '<em><b>Default</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Default</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Default</em>' containment reference.
   * @see #setDefault(TerminalExpression)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getDefFacet_Default()
   * @model containment="true"
   * @generated
   */
  TerminalExpression getDefault();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.DefFacet#getDefault <em>Default</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Default</em>' containment reference.
   * @see #getDefault()
   * @generated
   */
  void setDefault(TerminalExpression value);

} // DefFacet
