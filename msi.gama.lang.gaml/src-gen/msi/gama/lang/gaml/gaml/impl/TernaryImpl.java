/**
 * <copyright>
 * </copyright>
 *

 */
package msi.gama.lang.gaml.gaml.impl;

import msi.gama.lang.gaml.gaml.Expression;
import msi.gama.lang.gaml.gaml.GamlPackage;
import msi.gama.lang.gaml.gaml.Ternary;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ternary</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TernaryImpl#getCondition <em>Condition</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TernaryImpl#getIfTrue <em>If True</em>}</li>
 *   <li>{@link msi.gama.lang.gaml.gaml.impl.TernaryImpl#getIfFalse <em>If False</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class TernaryImpl extends ExpressionImpl implements Ternary
{
  /**
   * The cached value of the '{@link #getCondition() <em>Condition</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getCondition()
   * @generated
   * @ordered
   */
  protected Expression condition;

  /**
   * The cached value of the '{@link #getIfTrue() <em>If True</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIfTrue()
   * @generated
   * @ordered
   */
  protected Expression ifTrue;

  /**
   * The cached value of the '{@link #getIfFalse() <em>If False</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIfFalse()
   * @generated
   * @ordered
   */
  protected Expression ifFalse;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected TernaryImpl()
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
    return GamlPackage.Literals.TERNARY;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getCondition()
  {
    return condition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetCondition(Expression newCondition, NotificationChain msgs)
  {
    Expression oldCondition = condition;
    condition = newCondition;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__CONDITION, oldCondition, newCondition);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setCondition(Expression newCondition)
  {
    if (newCondition != condition)
    {
      NotificationChain msgs = null;
      if (condition != null)
        msgs = ((InternalEObject)condition).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__CONDITION, null, msgs);
      if (newCondition != null)
        msgs = ((InternalEObject)newCondition).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__CONDITION, null, msgs);
      msgs = basicSetCondition(newCondition, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__CONDITION, newCondition, newCondition));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getIfTrue()
  {
    return ifTrue;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetIfTrue(Expression newIfTrue, NotificationChain msgs)
  {
    Expression oldIfTrue = ifTrue;
    ifTrue = newIfTrue;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__IF_TRUE, oldIfTrue, newIfTrue);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIfTrue(Expression newIfTrue)
  {
    if (newIfTrue != ifTrue)
    {
      NotificationChain msgs = null;
      if (ifTrue != null)
        msgs = ((InternalEObject)ifTrue).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__IF_TRUE, null, msgs);
      if (newIfTrue != null)
        msgs = ((InternalEObject)newIfTrue).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__IF_TRUE, null, msgs);
      msgs = basicSetIfTrue(newIfTrue, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__IF_TRUE, newIfTrue, newIfTrue));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression getIfFalse()
  {
    return ifFalse;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetIfFalse(Expression newIfFalse, NotificationChain msgs)
  {
    Expression oldIfFalse = ifFalse;
    ifFalse = newIfFalse;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__IF_FALSE, oldIfFalse, newIfFalse);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setIfFalse(Expression newIfFalse)
  {
    if (newIfFalse != ifFalse)
    {
      NotificationChain msgs = null;
      if (ifFalse != null)
        msgs = ((InternalEObject)ifFalse).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__IF_FALSE, null, msgs);
      if (newIfFalse != null)
        msgs = ((InternalEObject)newIfFalse).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GamlPackage.TERNARY__IF_FALSE, null, msgs);
      msgs = basicSetIfFalse(newIfFalse, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GamlPackage.TERNARY__IF_FALSE, newIfFalse, newIfFalse));
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
      case GamlPackage.TERNARY__CONDITION:
        return basicSetCondition(null, msgs);
      case GamlPackage.TERNARY__IF_TRUE:
        return basicSetIfTrue(null, msgs);
      case GamlPackage.TERNARY__IF_FALSE:
        return basicSetIfFalse(null, msgs);
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
      case GamlPackage.TERNARY__CONDITION:
        return getCondition();
      case GamlPackage.TERNARY__IF_TRUE:
        return getIfTrue();
      case GamlPackage.TERNARY__IF_FALSE:
        return getIfFalse();
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
      case GamlPackage.TERNARY__CONDITION:
        setCondition((Expression)newValue);
        return;
      case GamlPackage.TERNARY__IF_TRUE:
        setIfTrue((Expression)newValue);
        return;
      case GamlPackage.TERNARY__IF_FALSE:
        setIfFalse((Expression)newValue);
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
      case GamlPackage.TERNARY__CONDITION:
        setCondition((Expression)null);
        return;
      case GamlPackage.TERNARY__IF_TRUE:
        setIfTrue((Expression)null);
        return;
      case GamlPackage.TERNARY__IF_FALSE:
        setIfFalse((Expression)null);
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
      case GamlPackage.TERNARY__CONDITION:
        return condition != null;
      case GamlPackage.TERNARY__IF_TRUE:
        return ifTrue != null;
      case GamlPackage.TERNARY__IF_FALSE:
        return ifFalse != null;
    }
    return super.eIsSet(featureID);
  }

} //TernaryImpl
