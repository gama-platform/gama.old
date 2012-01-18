/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Lang Def</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlLangDef#getK <em>K</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlLangDef#getF <em>F</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlLangDef#getB <em>B</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlLangDef#getR <em>R</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlLangDef#getU <em>U</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef()
 * @model
 * @generated
 */
public interface GamlLangDef extends EObject
{
  /**
   * Returns the value of the '<em><b>K</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefKeyword}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>K</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>K</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef_K()
   * @model containment="true"
   * @generated
   */
  EList<DefKeyword> getK();

  /**
   * Returns the value of the '<em><b>F</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefFacet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>F</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>F</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef_F()
   * @model containment="true"
   * @generated
   */
  EList<DefFacet> getF();

  /**
   * Returns the value of the '<em><b>B</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefBinaryOp}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>B</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>B</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef_B()
   * @model containment="true"
   * @generated
   */
  EList<DefBinaryOp> getB();

  /**
   * Returns the value of the '<em><b>R</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefReserved}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>R</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>R</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef_R()
   * @model containment="true"
   * @generated
   */
  EList<DefReserved> getR();

  /**
   * Returns the value of the '<em><b>U</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefUnit}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>U</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>U</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlLangDef_U()
   * @model containment="true"
   * @generated
   */
  EList<DefUnit> getU();

} // GamlLangDef
