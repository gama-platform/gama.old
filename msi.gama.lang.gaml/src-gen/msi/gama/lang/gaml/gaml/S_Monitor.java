/**
 */
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SMonitor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Monitor#getKey <em>Key</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Monitor#getFirstFacet <em>First Facet</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Monitor#getName <em>Name</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Monitor#getFacets <em>Facets</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Monitor()
 * @model
 * @generated
 */
public interface S_Monitor extends EObject
{
  /**
   * Returns the value of the '<em><b>Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Key</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Key</em>' attribute.
   * @see #setKey(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Monitor_Key()
   * @model
   * @generated
   */
  String getKey();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.S_Monitor#getKey <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Key</em>' attribute.
   * @see #getKey()
   * @generated
   */
  void setKey(String value);

  /**
   * Returns the value of the '<em><b>First Facet</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>First Facet</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>First Facet</em>' attribute.
   * @see #setFirstFacet(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Monitor_FirstFacet()
   * @model
   * @generated
   */
  String getFirstFacet();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.S_Monitor#getFirstFacet <em>First Facet</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>First Facet</em>' attribute.
   * @see #getFirstFacet()
   * @generated
   */
  void setFirstFacet(String value);

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
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Monitor_Name()
   * @model
   * @generated
   */
  String getName();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.S_Monitor#getName <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Name</em>' attribute.
   * @see #getName()
   * @generated
   */
  void setName(String value);

  /**
   * Returns the value of the '<em><b>Facets</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.Facet}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Facets</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Facets</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Monitor_Facets()
   * @model containment="true"
   * @generated
   */
  EList<Facet> getFacets();

} // S_Monitor
