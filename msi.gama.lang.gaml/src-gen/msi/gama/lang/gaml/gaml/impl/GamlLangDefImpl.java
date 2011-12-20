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

import msi.gama.lang.gaml.gaml.DefBinaryOp;
import msi.gama.lang.gaml.gaml.DefFacet;
import msi.gama.lang.gaml.gaml.DefKeyword;
import msi.gama.lang.gaml.gaml.DefReserved;
import msi.gama.lang.gaml.gaml.DefUnit;
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
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getK <em>K</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getF <em>F</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getB <em>B</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getR <em>R</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.GamlLangDefImpl#getU <em>U</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class GamlLangDefImpl extends MinimalEObjectImpl.Container implements GamlLangDef
{
  /**
   * The cached value of the '{@link #getK() <em>K</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getK()
   * @generated
   * @ordered
   */
  protected EList<DefKeyword> k;

  /**
   * The cached value of the '{@link #getF() <em>F</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getF()
   * @generated
   * @ordered
   */
  protected EList<DefFacet> f;

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
   * The cached value of the '{@link #getU() <em>U</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getU()
   * @generated
   * @ordered
   */
  protected EList<DefUnit> u;

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
  public EList<DefKeyword> getK()
  {
    if (k == null)
    {
      k = new EObjectContainmentEList<DefKeyword>(DefKeyword.class, this, GamlPackage.GAML_LANG_DEF__K);
    }
    return k;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<DefFacet> getF()
  {
    if (f == null)
    {
      f = new EObjectContainmentEList<DefFacet>(DefFacet.class, this, GamlPackage.GAML_LANG_DEF__F);
    }
    return f;
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
  public EList<DefUnit> getU()
  {
    if (u == null)
    {
      u = new EObjectContainmentEList<DefUnit>(DefUnit.class, this, GamlPackage.GAML_LANG_DEF__U);
    }
    return u;
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
      case GamlPackage.GAML_LANG_DEF__K:
        return ((InternalEList<?>)getK()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__F:
        return ((InternalEList<?>)getF()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__B:
        return ((InternalEList<?>)getB()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__R:
        return ((InternalEList<?>)getR()).basicRemove(otherEnd, msgs);
      case GamlPackage.GAML_LANG_DEF__U:
        return ((InternalEList<?>)getU()).basicRemove(otherEnd, msgs);
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
      case GamlPackage.GAML_LANG_DEF__K:
        return getK();
      case GamlPackage.GAML_LANG_DEF__F:
        return getF();
      case GamlPackage.GAML_LANG_DEF__B:
        return getB();
      case GamlPackage.GAML_LANG_DEF__R:
        return getR();
      case GamlPackage.GAML_LANG_DEF__U:
        return getU();
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
      case GamlPackage.GAML_LANG_DEF__K:
        getK().clear();
        getK().addAll((Collection<? extends DefKeyword>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__F:
        getF().clear();
        getF().addAll((Collection<? extends DefFacet>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__B:
        getB().clear();
        getB().addAll((Collection<? extends DefBinaryOp>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__R:
        getR().clear();
        getR().addAll((Collection<? extends DefReserved>)newValue);
        return;
      case GamlPackage.GAML_LANG_DEF__U:
        getU().clear();
        getU().addAll((Collection<? extends DefUnit>)newValue);
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
      case GamlPackage.GAML_LANG_DEF__K:
        getK().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__F:
        getF().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__B:
        getB().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__R:
        getR().clear();
        return;
      case GamlPackage.GAML_LANG_DEF__U:
        getU().clear();
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
      case GamlPackage.GAML_LANG_DEF__K:
        return k != null && !k.isEmpty();
      case GamlPackage.GAML_LANG_DEF__F:
        return f != null && !f.isEmpty();
      case GamlPackage.GAML_LANG_DEF__B:
        return b != null && !b.isEmpty();
      case GamlPackage.GAML_LANG_DEF__R:
        return r != null && !r.isEmpty();
      case GamlPackage.GAML_LANG_DEF__U:
        return u != null && !u.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //GamlLangDefImpl
