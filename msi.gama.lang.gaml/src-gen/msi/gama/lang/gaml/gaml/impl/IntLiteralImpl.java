/*********************************************************************************************
 *
 * 'IntLiteralImpl.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.IntLiteral;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Int Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class IntLiteralImpl extends TerminalExpressionImpl implements IntLiteral
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IntLiteralImpl()
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
    return GamlPackage.Literals.INT_LITERAL;
  }

} //IntLiteralImpl
