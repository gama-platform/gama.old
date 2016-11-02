/*********************************************************************************************
 *
 * 'Parameter.java, in plugin msi.gama.lang.gaml, is part of the source code of the
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
 * A representation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.Parameter#getBuiltInFacetKey <em>Built In Facet Key</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getParameter()
 * @model
 * @generated
 */
public interface Parameter extends Expression
{
  /**
   * Returns the value of the '<em><b>Built In Facet Key</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Built In Facet Key</em>' attribute isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Built In Facet Key</em>' attribute.
   * @see #setBuiltInFacetKey(String)
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getParameter_BuiltInFacetKey()
   * @model
   * @generated
   */
  String getBuiltInFacetKey();

  /**
   * Sets the value of the '{@link msi.gama.lang.gaml.gaml.Parameter#getBuiltInFacetKey <em>Built In Facet Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Built In Facet Key</em>' attribute.
   * @see #getBuiltInFacetKey()
   * @generated
   */
  void setBuiltInFacetKey(String value);

} // Parameter
