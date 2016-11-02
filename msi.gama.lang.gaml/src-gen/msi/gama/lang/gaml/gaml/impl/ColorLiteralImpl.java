/*********************************************************************************************
 *
 * 'ColorLiteralImpl.java, in plugin msi.gama.lang.gaml, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.ColorLiteral;
import msi.gama.lang.gaml.gaml.GamlPackage;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Color Literal</b></em>'.
 * <!-- end-user-doc -->
 *
 * @generated
 */
public class ColorLiteralImpl extends TerminalExpressionImpl implements ColorLiteral
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ColorLiteralImpl()
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
    return GamlPackage.Literals.COLOR_LITERAL;
  }

} //ColorLiteralImpl
