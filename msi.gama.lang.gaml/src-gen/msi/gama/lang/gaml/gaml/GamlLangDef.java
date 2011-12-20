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
