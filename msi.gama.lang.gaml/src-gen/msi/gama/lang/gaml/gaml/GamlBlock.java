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
 * A representation of the model object '<em><b>Block</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlBlock#getFacets <em>Facets</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.GamlBlock#getChilds <em>Childs</em>}</li>
 * </ul>
 * </p>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlBlock()
 * @model
 * @generated
 */
public interface GamlBlock extends EObject
{
  /**
   * Returns the value of the '<em><b>Facets</b></em>' reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefFacet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Facets</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Facets</em>' reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlBlock_Facets()
   * @model
   * @generated
   */
  EList<DefFacet> getFacets();

  /**
   * Returns the value of the '<em><b>Childs</b></em>' reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.DefKeyword}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Childs</em>' reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Childs</em>' reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getGamlBlock_Childs()
   * @model
   * @generated
   */
  EList<DefKeyword> getChilds();

} // GamlBlock
