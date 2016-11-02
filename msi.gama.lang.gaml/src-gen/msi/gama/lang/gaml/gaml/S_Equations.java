/*********************************************************************************************
 *
 * 'S_Equations.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.gaml;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>SEquations</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.S_Equations#getEquations <em>Equations</em>}</li>
 * </ul>
 *
 * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Equations()
 * @model
 * @generated
 */
public interface S_Equations extends Statement, EquationDefinition
{
  /**
   * Returns the value of the '<em><b>Equations</b></em>' containment reference list.
   * The list contents are of type {@link msi.gama.lang.gaml.gaml.S_Assignment}.
   * <!-- begin-user-doc -->
   * <p>
   * If the meaning of the '<em>Equations</em>' containment reference list isn't clear,
   * there really should be more of a description here...
   * </p>
   * <!-- end-user-doc -->
   * @return the value of the '<em>Equations</em>' containment reference list.
   * @see msi.gama.lang.gaml.gaml.GamlPackage#getS_Equations_Equations()
   * @model containment="true"
   * @generated
   */
  EList<S_Assignment> getEquations();

} // S_Equations
