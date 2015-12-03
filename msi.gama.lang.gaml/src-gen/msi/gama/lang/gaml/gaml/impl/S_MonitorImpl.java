/**
 */
package msi.gama.lang.gaml.gaml.impl;

import java.util.Collection;

import msi.gama.lang.gaml.gaml.Facet;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.S_Monitor;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>SMonitor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.S_MonitorImpl#getKey <em>Key</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.S_MonitorImpl#getFirstFacet <em>First Facet</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.S_MonitorImpl#getName <em>Name</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.S_MonitorImpl#getFacets <em>Facets</em>}</li>
 * </ul>
 *
 * @generated
 */
public class S_MonitorImpl extends MinimalEObjectImpl.Container implements S_Monitor
{
  /**
   * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKey()
   * @generated
   * @ordered
   */
  protected static final String KEY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getKey()
   * @generated
   * @ordered
   */
  protected String key = KEY_EDEFAULT;

  /**
   * The default value of the '{@link #getFirstFacet() <em>First Facet</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstFacet()
   * @generated
   * @ordered
   */
  protected static final String FIRST_FACET_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getFirstFacet() <em>First Facet</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirstFacet()
   * @generated
   * @ordered
   */
  protected String firstFacet = FIRST_FACET_EDEFAULT;

  /**
   * The default value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected static final String NAME_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getName()
   * @generated
   * @ordered
   */
  protected String name = NAME_EDEFAULT;

  /**
   * The cached value of the '{@link #getFacets() <em>Facets</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFacets()
   * @generated
   * @ordered
   */
  protected EList<Facet> facets;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected S_MonitorImpl()
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
    return GamlPackage.Literals.SMONITOR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getKey()
  {
    return key;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setKey(String newKey)
  {
    String oldKey = key;
    key = newKey;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.SMONITOR__KEY, oldKey, key));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getFirstFacet()
  {
    return firstFacet;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFirstFacet(String newFirstFacet)
  {
    String oldFirstFacet = firstFacet;
    firstFacet = newFirstFacet;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.SMONITOR__FIRST_FACET, oldFirstFacet, firstFacet));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getName()
  {
    return name;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setName(String newName)
  {
    String oldName = name;
    name = newName;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.SMONITOR__NAME, oldName, name));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Facet> getFacets()
  {
    if (facets == null)
    {
      facets = new EObjectContainmentEList<Facet>(Facet.class, this, GamlPackage.SMONITOR__FACETS);
    }
    return facets;
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
      case GamlPackage.SMONITOR__FACETS:
        return ((InternalEList<?>)getFacets()).basicRemove(otherEnd, msgs);
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
      case GamlPackage.SMONITOR__KEY:
        return getKey();
      case GamlPackage.SMONITOR__FIRST_FACET:
        return getFirstFacet();
      case GamlPackage.SMONITOR__NAME:
        return getName();
      case GamlPackage.SMONITOR__FACETS:
        return getFacets();
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
      case GamlPackage.SMONITOR__KEY:
        setKey((String)newValue);
        return;
      case GamlPackage.SMONITOR__FIRST_FACET:
        setFirstFacet((String)newValue);
        return;
      case GamlPackage.SMONITOR__NAME:
        setName((String)newValue);
        return;
      case GamlPackage.SMONITOR__FACETS:
        getFacets().clear();
        getFacets().addAll((Collection<? extends Facet>)newValue);
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
      case GamlPackage.SMONITOR__KEY:
        setKey(KEY_EDEFAULT);
        return;
      case GamlPackage.SMONITOR__FIRST_FACET:
        setFirstFacet(FIRST_FACET_EDEFAULT);
        return;
      case GamlPackage.SMONITOR__NAME:
        setName(NAME_EDEFAULT);
        return;
      case GamlPackage.SMONITOR__FACETS:
        getFacets().clear();
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
      case GamlPackage.SMONITOR__KEY:
        return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
      case GamlPackage.SMONITOR__FIRST_FACET:
        return FIRST_FACET_EDEFAULT == null ? firstFacet != null : !FIRST_FACET_EDEFAULT.equals(firstFacet);
      case GamlPackage.SMONITOR__NAME:
        return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
      case GamlPackage.SMONITOR__FACETS:
        return facets != null && !facets.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (key: ");
    result.append(key);
    result.append(", firstFacet: ");
    result.append(firstFacet);
    result.append(", name: ");
    result.append(name);
    result.append(')');
    return result.toString();
  }

} //S_MonitorImpl
