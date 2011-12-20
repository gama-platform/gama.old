/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2012
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
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
