/*******************************************************************************************************
 *
 * ReservedLiteralImpl.java, in msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.ReservedLiteral;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Reserved Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ReservedLiteralImpl extends TerminalExpressionImpl implements ReservedLiteral
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ReservedLiteralImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return GamlPackage.Literals.RESERVED_LITERAL;
  }

} //ReservedLiteralImpl
