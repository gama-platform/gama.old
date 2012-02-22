/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml.impl;

import java.util.Collection;

import msi.gama.lang.gaml.gaml.DefBinaryOp;
import msi.gama.lang.gaml.gaml.DefReserved;
import msi.gama.lang.gaml.gaml.DefUnary;
import msi.gama.lang.gaml.gaml.GamlLangDef;
import msi.gama.lang.gaml.gaml.GamlPackage;

import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Lang Def</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getB <em>B</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getR <em>R</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getUnaries <em>Unaries</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GamlLangDefImpl extends MinimalEObjectImpl.Container implements GamlLangDef
{
  /**
   * The cached value of the '{@link #getB() <em>B</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getB()
   * @generated
   * @ordered
   */
  protected EList<DefBinaryOp> b;

  /**
   * The cached value of the '{@link #getR() <em>R</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getR()
   * @generated
   * @ordered
   */
  protected EList<DefReserved> r;

  /**
   * The cached value of the '{@link #getUnaries() <em>Unaries</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getUnaries()
   * @generated
   * @ordered
   */
  protected EList<DefUnary> unaries;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected GamlLangDefImpl()
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
    return GamlPackage.Literals.GAML_LANG_DEF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefBinaryOp> getB()
  {
    if (b == null)
    {
      b = new EObjectContainmentEList<DefBinaryOp>(DefBinaryOp.class, this, GamlPackage.GAML_LANG_DEF__B);
    }
    return b;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefReserved> getR()
  {
    if (r == null)
    {
      r = new EObjectContainmentEList<DefReserved>(DefReserved.class, this, GamlPackage.GAML_LANG_DEF__R);
    }
    return r;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefUnary> getUnaries()
  {
    if (unaries == null)
    {
      unaries = new EObjectContainmentEList<DefUnary>(DefUnary.class, this, GamlPackage.GAML_LANG_DEF__UNARIES);
    }
    return unaries;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case GamlPackage.GAML_LANG_DEF__B:
        return ((InternalEList<?>)getB()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__R:
        return ((InternalEList<?>)getR()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__UNARIES:
        return ((InternalEList<?>)getUnaries()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
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
      case GamlPackage.GAML_LANG_DEF__B:
        return getB();
      case GamlPackage.GAML_LANG_DEF__R:
        return getR();
      case GamlPackage.GAML_LANG_DEF__UNARIES:
        return getUnaries();
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
      case GamlPackage.GAML_LANG_DEF__B:
        getB().clear();
        getB().addAll((Collection<? extends DefBinaryOp>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__R:
        getR().clear();
        getR().addAll((Collection<? extends DefReserved>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__UNARIES:
        getUnaries().clear();
        getUnaries().addAll((Collection<? extends DefUnary>)newValue);
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
      case GamlPackage.GAML_LANG_DEF__B:
        getB().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__R:
        getR().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__UNARIES:
        getUnaries().clear();
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
      case GamlPackage.GAML_LANG_DEF__B:
        return b != null && !b.isEmpty();
      case GamlPackage.GAML_LANG_DEF__R:
        return r != null && !r.isEmpty();
      case GamlPackage.GAML_LANG_DEF__UNARIES:
        return unaries != null && !unaries.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //GamlLangDefImpl
