/**
 * <copyright>
 * </copyright>
 *
 */
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.TypeDefinition;
import msi.gama.lang.gaml.gaml.TypeRef;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Type Ref</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TypeRefImpl#getRef <em>Ref</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TypeRefImpl#getOf <em>Of</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TypeRefImpl extends ExpressionImpl implements TypeRef
{
  /**
   * The cached value of the '{@link #getRef() <em>Ref</em>}' reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getRef()
   * @generated
   * @ordered
   */
  protected TypeDefinition ref;

  /**
   * The cached value of the '{@link #getOf() <em>Of</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getOf()
   * @generated
   * @ordered
   */
  protected Expression of;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TypeRefImpl()
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
    return GamlPackage.Literals.TYPE_REF;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypeDefinition getRef()
  {
    if (ref != null && ref.eIsProxy())
    {
      InternalEObject oldRef = (InternalEObject)ref;
      ref = (TypeDefinition)eResolveProxy(oldRef);
      if (ref != oldRef)
      {
        if (eNotificationRequired())
          eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamlPackage.TYPE_REF__REF, oldRef, ref));
      }
    }
    return ref;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TypeDefinition basicGetRef()
  {
    return ref;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setRef(TypeDefinition newRef)
  {
    TypeDefinition oldRef = ref;
    ref = newRef;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__REF, oldRef, ref));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getOf()
  {
    return of;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetOf(Expression newOf, NotificationChain msgs)
  {
    Expression oldOf = of;
    of = newOf;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__OF, oldOf, newOf);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setOf(Expression newOf)
  {
    if (newOf != of)
    {
      NotificationChain msgs = null;
      if (of != null)
        msgs = ((InternalEObject)of).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__OF, null, msgs);
      if (newOf != null)
        msgs = ((InternalEObject)newOf).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__OF, null, msgs);
      msgs = basicSetOf(newOf, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__OF, newOf, newOf));
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
      case GamlPackage.TYPE_REF__OF:
        return basicSetOf(null, msgs);
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
      case GamlPackage.TYPE_REF__REF:
        if (resolve) return getRef();
        return basicGetRef();
      case GamlPackage.TYPE_REF__OF:
        return getOf();
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
      case GamlPackage.TYPE_REF__REF:
        setRef((TypeDefinition)newValue);
        return;
      case GamlPackage.TYPE_REF__OF:
        setOf((Expression)newValue);
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
      case GamlPackage.TYPE_REF__REF:
        setRef((TypeDefinition)null);
        return;
      case GamlPackage.TYPE_REF__OF:
        setOf((Expression)null);
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
      case GamlPackage.TYPE_REF__REF:
        return ref != null;
      case GamlPackage.TYPE_REF__OF:
        return of != null;
    }
    return super.eIsSet(featureID);
  }

} //TypeRefImpl
