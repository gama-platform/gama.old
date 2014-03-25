/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Species Ref</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.SpeciesRef#getName <em>Name</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.SpeciesRef#getParameter <em>Parameter</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getSpeciesRef()
 * @model
 * @generated
 */
public interface SpeciesRef extends EObject
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
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getSpeciesRef_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.SpeciesRef#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Parameter</b></em>' containment reference.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Parameter</em>' containment reference isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Parameter</em>' containment reference.
   * @see #setParameter(TypeInfo)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getSpeciesRef_Parameter()
   * @model containment="true"
   * @generated
   */
  TypeInfo getParameter();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.SpeciesRef#getParameter <em>Parameter</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Parameter</em>' containment reference.
   * @see #getParameter()
   * @generated
   */
  void setParameter(TypeInfo value);

} // SpeciesRef
