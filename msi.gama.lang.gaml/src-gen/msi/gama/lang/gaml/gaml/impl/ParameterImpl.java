/**
 */
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Parameter;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Parameter</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.ParameterImpl#getBuiltInFacetKey <em>Built In Facet Key</em>}</li>
 * </ul>
 *
 * @generated
 */
public class ParameterImpl extends ExpressionImpl implements Parameter
{
  /**
   * The default value of the '{@link #getBuiltInFacetKey() <em>Built In Facet Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBuiltInFacetKey()
   * @generated
   * @ordered
   */
  protected static final String BUILT_IN_FACET_KEY_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getBuiltInFacetKey() <em>Built In Facet Key</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getBuiltInFacetKey()
   * @generated
   * @ordered
   */
  protected String builtInFacetKey = BUILT_IN_FACET_KEY_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected ParameterImpl()
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
    return GamlPackage.Literals.PARAMETER;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getBuiltInFacetKey()
  {
    return builtInFacetKey;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setBuiltInFacetKey(String newBuiltInFacetKey)
  {
    String oldBuiltInFacetKey = builtInFacetKey;
    builtInFacetKey = newBuiltInFacetKey;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.PARAMETER__BUILT_IN_FACET_KEY, oldBuiltInFacetKey, builtInFacetKey));
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
      case GamlPackage.PARAMETER__BUILT_IN_FACET_KEY:
        return getBuiltInFacetKey();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case GamlPackage.PARAMETER__BUILT_IN_FACET_KEY:
        setBuiltInFacetKey((String)newValue);
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
      case GamlPackage.PARAMETER__BUILT_IN_FACET_KEY:
        setBuiltInFacetKey(BUILT_IN_FACET_KEY_EDEFAULT);
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
      case GamlPackage.PARAMETER__BUILT_IN_FACET_KEY:
        return BUILT_IN_FACET_KEY_EDEFAULT == null ? builtInFacetKey != null : !BUILT_IN_FACET_KEY_EDEFAULT.equals(builtInFacetKey);
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
    result.append(" (builtInFacetKey: ");
    result.append(builtInFacetKey);
    result.append(')');
    return result.toString();
  }

} //ParameterImpl
