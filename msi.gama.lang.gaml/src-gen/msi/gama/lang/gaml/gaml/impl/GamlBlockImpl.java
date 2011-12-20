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
package msi.gama.lang.gaml.gaml.impl;

import java.util.Collection;

import msi.gama.lang.gaml.gaml.DefFacet;
import msi.gama.lang.gaml.gaml.DefKeyword;
import msi.gama.lang.gaml.gaml.GamlBlock;
import msi.gama.lang.gaml.gaml.GamlPackage;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Block</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlBlockImpl#getFacets <em>Facets</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlBlockImpl#getChilds <em>Childs</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GamlBlockImpl extends MinimalEObjectImpl.Container implements GamlBlock
{
  /**
   * The cached value of the '{@link #getFacets() <em>Facets</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFacets()
   * @generated
   * @ordered
   */
  protected EList<DefFacet> facets;

  /**
   * The cached value of the '{@link #getChilds() <em>Childs</em>}' reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getChilds()
   * @generated
   * @ordered
   */
  protected EList<DefKeyword> childs;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GamlBlockImpl()
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
    return GamlPackage.Literals.GAML_BLOCK;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefFacet> getFacets()
  {
    if (facets == null)
    {
      facets = new EObjectResolvingEList<DefFacet>(DefFacet.class, this, GamlPackage.GAML_BLOCK__FACETS);
    }
    return facets;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefKeyword> getChilds()
  {
    if (childs == null)
    {
      childs = new EObjectResolvingEList<DefKeyword>(DefKeyword.class, this, GamlPackage.GAML_BLOCK__CHILDS);
    }
    return childs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case GamlPackage.GAML_BLOCK__FACETS:
        return getFacets();
      case GamlPackage.GAML_BLOCK__CHILDS:
        return getChilds();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case GamlPackage.GAML_BLOCK__FACETS:
        getFacets().clear();
        getFacets().addAll((Collection<? extends DefFacet>)newValue);
        return;
      case GamlPackage.GAML_BLOCK__CHILDS:
        getChilds().clear();
        getChilds().addAll((Collection<? extends DefKeyword>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case GamlPackage.GAML_BLOCK__FACETS:
        getFacets().clear();
        return;
      case GamlPackage.GAML_BLOCK__CHILDS:
        getChilds().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case GamlPackage.GAML_BLOCK__FACETS:
        return facets != null && !facets.isEmpty();
      case GamlPackage.GAML_BLOCK__CHILDS:
        return childs != null && !childs.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //GamlBlockImpl
