/**
 * <copyright>
 * </copyright>
 *
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
