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
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TypeRefImpl#getFirst <em>First</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TypeRefImpl#getSecond <em>Second</em>}</li>
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
   * The cached value of the '{@link #getFirst() <em>First</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFirst()
   * @generated
   * @ordered
   */
  protected Expression first;

  /**
   * The cached value of the '{@link #getSecond() <em>Second</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSecond()
   * @generated
   * @ordered
   */
  protected Expression second;

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
  public Expression getFirst()
  {
    return first;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetFirst(Expression newFirst, NotificationChain msgs)
  {
    Expression oldFirst = first;
    first = newFirst;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__FIRST, oldFirst, newFirst);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setFirst(Expression newFirst)
  {
    if (newFirst != first)
    {
      NotificationChain msgs = null;
      if (first != null)
        msgs = ((InternalEObject)first).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__FIRST, null, msgs);
      if (newFirst != null)
        msgs = ((InternalEObject)newFirst).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__FIRST, null, msgs);
      msgs = basicSetFirst(newFirst, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__FIRST, newFirst, newFirst));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getSecond()
  {
    return second;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSecond(Expression newSecond, NotificationChain msgs)
  {
    Expression oldSecond = second;
    second = newSecond;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__SECOND, oldSecond, newSecond);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSecond(Expression newSecond)
  {
    if (newSecond != second)
    {
      NotificationChain msgs = null;
      if (second != null)
        msgs = ((InternalEObject)second).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__SECOND, null, msgs);
      if (newSecond != null)
        msgs = ((InternalEObject)newSecond).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TYPE_REF__SECOND, null, msgs);
      msgs = basicSetSecond(newSecond, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TYPE_REF__SECOND, newSecond, newSecond));
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
      case GamlPackage.TYPE_REF__FIRST:
        return basicSetFirst(null, msgs);
      case GamlPackage.TYPE_REF__SECOND:
        return basicSetSecond(null, msgs);
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
      case GamlPackage.TYPE_REF__FIRST:
        return getFirst();
      case GamlPackage.TYPE_REF__SECOND:
        return getSecond();
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
      case GamlPackage.TYPE_REF__FIRST:
        setFirst((Expression)newValue);
        return;
      case GamlPackage.TYPE_REF__SECOND:
        setSecond((Expression)newValue);
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
      case GamlPackage.TYPE_REF__FIRST:
        setFirst((Expression)null);
        return;
      case GamlPackage.TYPE_REF__SECOND:
        setSecond((Expression)null);
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
      case GamlPackage.TYPE_REF__FIRST:
        return first != null;
      case GamlPackage.TYPE_REF__SECOND:
        return second != null;
    }
    return super.eIsSet(featureID);
  }

} //TypeRefImpl
