/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EAspect;
import gama.ELayerAspect;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ELayer Aspect</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ELayerAspectImpl#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getShape <em>Shape</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getColor <em>Color</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getEmpty <em>Empty</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getRotate <em>Rotate</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getSize <em>Size</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getHeigth <em>Heigth</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getRadius <em>Radius</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getPath <em>Path</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getText <em>Text</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getType <em>Type</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getExpression <em>Expression</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getPoints <em>Points</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getAt <em>At</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getShapeType <em>Shape Type</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getTextSize <em>Text Size</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getImageSize <em>Image Size</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getAspect <em>Aspect</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getDepth <em>Depth</em>}</li>
 *   <li>{@link gama.impl.ELayerAspectImpl#getTexture <em>Texture</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ELayerAspectImpl extends EGamaObjectImpl implements ELayerAspect {
	/**
	 * The default value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected static final String GAML_CODE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGamlCode() <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGamlCode()
	 * @generated
	 * @ordered
	 */
	protected String gamlCode = GAML_CODE_EDEFAULT;

	/**
	 * The default value of the '{@link #getShape() <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShape()
	 * @generated
	 * @ordered
	 */
	protected static final String SHAPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShape() <em>Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShape()
	 * @generated
	 * @ordered
	 */
	protected String shape = SHAPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected static final String COLOR_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getColor() <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColor()
	 * @generated
	 * @ordered
	 */
	protected String color = COLOR_EDEFAULT;

	/**
	 * The default value of the '{@link #getEmpty() <em>Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEmpty()
	 * @generated
	 * @ordered
	 */
	protected static final String EMPTY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEmpty() <em>Empty</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEmpty()
	 * @generated
	 * @ordered
	 */
	protected String empty = EMPTY_EDEFAULT;

	/**
	 * The default value of the '{@link #getRotate() <em>Rotate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotate()
	 * @generated
	 * @ordered
	 */
	protected static final String ROTATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRotate() <em>Rotate</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRotate()
	 * @generated
	 * @ordered
	 */
	protected String rotate = ROTATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected static final String SIZE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSize() <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize()
	 * @generated
	 * @ordered
	 */
	protected String size = SIZE_EDEFAULT;

	/**
	 * The default value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected static final String WIDTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getWidth() <em>Width</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getWidth()
	 * @generated
	 * @ordered
	 */
	protected String width = WIDTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getHeigth() <em>Heigth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeigth()
	 * @generated
	 * @ordered
	 */
	protected static final String HEIGTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getHeigth() <em>Heigth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getHeigth()
	 * @generated
	 * @ordered
	 */
	protected String heigth = HEIGTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getRadius() <em>Radius</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRadius()
	 * @generated
	 * @ordered
	 */
	protected static final String RADIUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRadius() <em>Radius</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRadius()
	 * @generated
	 * @ordered
	 */
	protected String radius = RADIUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected static final String PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPath() <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPath()
	 * @generated
	 * @ordered
	 */
	protected String path = PATH_EDEFAULT;

	/**
	 * The default value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getText() <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getText()
	 * @generated
	 * @ordered
	 */
	protected String text = TEXT_EDEFAULT;

	/**
	 * The default value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected static final String TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getType() <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getType()
	 * @generated
	 * @ordered
	 */
	protected String type = TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getExpression() <em>Expression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpression()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPRESSION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpression() <em>Expression</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpression()
	 * @generated
	 * @ordered
	 */
	protected String expression = EXPRESSION_EDEFAULT;

	/**
	 * The default value of the '{@link #getPoints() <em>Points</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPoints()
	 * @generated
	 * @ordered
	 */
	protected static final String POINTS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPoints() <em>Points</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPoints()
	 * @generated
	 * @ordered
	 */
	protected String points = POINTS_EDEFAULT;

	/**
	 * The default value of the '{@link #getAt() <em>At</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAt()
	 * @generated
	 * @ordered
	 */
	protected static final String AT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAt() <em>At</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAt()
	 * @generated
	 * @ordered
	 */
	protected String at = AT_EDEFAULT;

	/**
	 * The default value of the '{@link #getShapeType() <em>Shape Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeType()
	 * @generated
	 * @ordered
	 */
	protected static final String SHAPE_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShapeType() <em>Shape Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeType()
	 * @generated
	 * @ordered
	 */
	protected String shapeType = SHAPE_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean IS_COLOR_CST_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getIsColorCst() <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getIsColorCst()
	 * @generated
	 * @ordered
	 */
	protected Boolean isColorCst = IS_COLOR_CST_EDEFAULT;

	/**
	 * The default value of the '{@link #getTextSize() <em>Text Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTextSize()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXT_SIZE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTextSize() <em>Text Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTextSize()
	 * @generated
	 * @ordered
	 */
	protected String textSize = TEXT_SIZE_EDEFAULT;

	/**
	 * The default value of the '{@link #getImageSize() <em>Image Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImageSize()
	 * @generated
	 * @ordered
	 */
	protected static final String IMAGE_SIZE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getImageSize() <em>Image Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getImageSize()
	 * @generated
	 * @ordered
	 */
	protected String imageSize = IMAGE_SIZE_EDEFAULT;

	/**
	 * The cached value of the '{@link #getColorRBG() <em>Color RBG</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColorRBG()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> colorRBG;

	/**
	 * The cached value of the '{@link #getAspect() <em>Aspect</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspect()
	 * @generated
	 * @ordered
	 */
	protected EAspect aspect;

	/**
	 * The default value of the '{@link #getDepth() <em>Depth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDepth()
	 * @generated
	 * @ordered
	 */
	protected static final String DEPTH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getDepth() <em>Depth</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDepth()
	 * @generated
	 * @ordered
	 */
	protected String depth = DEPTH_EDEFAULT;

	/**
	 * The default value of the '{@link #getTexture() <em>Texture</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTexture()
	 * @generated
	 * @ordered
	 */
	protected static final String TEXTURE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTexture() <em>Texture</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTexture()
	 * @generated
	 * @ordered
	 */
	protected String texture = TEXTURE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ELayerAspectImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.ELAYER_ASPECT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGamlCode() {
		return gamlCode;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGamlCode(String newGamlCode) {
		String oldGamlCode = gamlCode;
		gamlCode = newGamlCode;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getShape() {
		return shape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShape(String newShape) {
		String oldShape = shape;
		shape = newShape;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__SHAPE, oldShape, shape));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getColor() {
		return color;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setColor(String newColor) {
		String oldColor = color;
		color = newColor;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__COLOR, oldColor, color));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEmpty() {
		return empty;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEmpty(String newEmpty) {
		String oldEmpty = empty;
		empty = newEmpty;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__EMPTY, oldEmpty, empty));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRotate() {
		return rotate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRotate(String newRotate) {
		String oldRotate = rotate;
		rotate = newRotate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__ROTATE, oldRotate, rotate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSize() {
		return size;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSize(String newSize) {
		String oldSize = size;
		size = newSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__SIZE, oldSize, size));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getWidth() {
		return width;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setWidth(String newWidth) {
		String oldWidth = width;
		width = newWidth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__WIDTH, oldWidth, width));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getHeigth() {
		return heigth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setHeigth(String newHeigth) {
		String oldHeigth = heigth;
		heigth = newHeigth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__HEIGTH, oldHeigth, heigth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRadius() {
		return radius;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRadius(String newRadius) {
		String oldRadius = radius;
		radius = newRadius;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__RADIUS, oldRadius, radius));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPath() {
		return path;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPath(String newPath) {
		String oldPath = path;
		path = newPath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__PATH, oldPath, path));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getText() {
		return text;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setText(String newText) {
		String oldText = text;
		text = newText;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__TEXT, oldText, text));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getType() {
		return type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setType(String newType) {
		String oldType = type;
		type = newType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpression(String newExpression) {
		String oldExpression = expression;
		expression = newExpression;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__EXPRESSION, oldExpression, expression));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPoints() {
		return points;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPoints(String newPoints) {
		String oldPoints = points;
		points = newPoints;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__POINTS, oldPoints, points));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAt() {
		return at;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAt(String newAt) {
		String oldAt = at;
		at = newAt;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__AT, oldAt, at));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getShapeType() {
		return shapeType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShapeType(String newShapeType) {
		String oldShapeType = shapeType;
		shapeType = newShapeType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__SHAPE_TYPE, oldShapeType, shapeType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getIsColorCst() {
		return isColorCst;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setIsColorCst(Boolean newIsColorCst) {
		Boolean oldIsColorCst = isColorCst;
		isColorCst = newIsColorCst;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__IS_COLOR_CST, oldIsColorCst, isColorCst));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTextSize() {
		return textSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTextSize(String newTextSize) {
		String oldTextSize = textSize;
		textSize = newTextSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__TEXT_SIZE, oldTextSize, textSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getImageSize() {
		return imageSize;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setImageSize(String newImageSize) {
		String oldImageSize = imageSize;
		imageSize = newImageSize;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__IMAGE_SIZE, oldImageSize, imageSize));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getColorRBG() {
		if (colorRBG == null) {
			colorRBG = new EDataTypeEList<Integer>(Integer.class, this, GamaPackage.ELAYER_ASPECT__COLOR_RBG);
		}
		return colorRBG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspect getAspect() {
		if (aspect != null && aspect.eIsProxy()) {
			InternalEObject oldAspect = (InternalEObject)aspect;
			aspect = (EAspect)eResolveProxy(oldAspect);
			if (aspect != oldAspect) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ELAYER_ASPECT__ASPECT, oldAspect, aspect));
			}
		}
		return aspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAspect basicGetAspect() {
		return aspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAspect(EAspect newAspect) {
		EAspect oldAspect = aspect;
		aspect = newAspect;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__ASPECT, oldAspect, aspect));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getDepth() {
		return depth;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDepth(String newDepth) {
		String oldDepth = depth;
		depth = newDepth;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__DEPTH, oldDepth, depth));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTexture() {
		return texture;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTexture(String newTexture) {
		String oldTexture = texture;
		texture = newTexture;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER_ASPECT__TEXTURE, oldTexture, texture));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.ELAYER_ASPECT__GAML_CODE:
				return getGamlCode();
			case GamaPackage.ELAYER_ASPECT__SHAPE:
				return getShape();
			case GamaPackage.ELAYER_ASPECT__COLOR:
				return getColor();
			case GamaPackage.ELAYER_ASPECT__EMPTY:
				return getEmpty();
			case GamaPackage.ELAYER_ASPECT__ROTATE:
				return getRotate();
			case GamaPackage.ELAYER_ASPECT__SIZE:
				return getSize();
			case GamaPackage.ELAYER_ASPECT__WIDTH:
				return getWidth();
			case GamaPackage.ELAYER_ASPECT__HEIGTH:
				return getHeigth();
			case GamaPackage.ELAYER_ASPECT__RADIUS:
				return getRadius();
			case GamaPackage.ELAYER_ASPECT__PATH:
				return getPath();
			case GamaPackage.ELAYER_ASPECT__TEXT:
				return getText();
			case GamaPackage.ELAYER_ASPECT__TYPE:
				return getType();
			case GamaPackage.ELAYER_ASPECT__EXPRESSION:
				return getExpression();
			case GamaPackage.ELAYER_ASPECT__POINTS:
				return getPoints();
			case GamaPackage.ELAYER_ASPECT__AT:
				return getAt();
			case GamaPackage.ELAYER_ASPECT__SHAPE_TYPE:
				return getShapeType();
			case GamaPackage.ELAYER_ASPECT__IS_COLOR_CST:
				return getIsColorCst();
			case GamaPackage.ELAYER_ASPECT__TEXT_SIZE:
				return getTextSize();
			case GamaPackage.ELAYER_ASPECT__IMAGE_SIZE:
				return getImageSize();
			case GamaPackage.ELAYER_ASPECT__COLOR_RBG:
				return getColorRBG();
			case GamaPackage.ELAYER_ASPECT__ASPECT:
				if (resolve) return getAspect();
				return basicGetAspect();
			case GamaPackage.ELAYER_ASPECT__DEPTH:
				return getDepth();
			case GamaPackage.ELAYER_ASPECT__TEXTURE:
				return getTexture();
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
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case GamaPackage.ELAYER_ASPECT__GAML_CODE:
				setGamlCode((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__SHAPE:
				setShape((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__COLOR:
				setColor((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__EMPTY:
				setEmpty((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__ROTATE:
				setRotate((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__SIZE:
				setSize((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__WIDTH:
				setWidth((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__HEIGTH:
				setHeigth((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__RADIUS:
				setRadius((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__PATH:
				setPath((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXT:
				setText((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__TYPE:
				setType((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__EXPRESSION:
				setExpression((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__POINTS:
				setPoints((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__AT:
				setAt((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__SHAPE_TYPE:
				setShapeType((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__IS_COLOR_CST:
				setIsColorCst((Boolean)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXT_SIZE:
				setTextSize((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__IMAGE_SIZE:
				setImageSize((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__COLOR_RBG:
				getColorRBG().clear();
				getColorRBG().addAll((Collection<? extends Integer>)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__ASPECT:
				setAspect((EAspect)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__DEPTH:
				setDepth((String)newValue);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXTURE:
				setTexture((String)newValue);
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
	public void eUnset(int featureID) {
		switch (featureID) {
			case GamaPackage.ELAYER_ASPECT__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__SHAPE:
				setShape(SHAPE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__EMPTY:
				setEmpty(EMPTY_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__ROTATE:
				setRotate(ROTATE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__WIDTH:
				setWidth(WIDTH_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__HEIGTH:
				setHeigth(HEIGTH_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__RADIUS:
				setRadius(RADIUS_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__PATH:
				setPath(PATH_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__EXPRESSION:
				setExpression(EXPRESSION_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__POINTS:
				setPoints(POINTS_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__AT:
				setAt(AT_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__SHAPE_TYPE:
				setShapeType(SHAPE_TYPE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__IS_COLOR_CST:
				setIsColorCst(IS_COLOR_CST_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXT_SIZE:
				setTextSize(TEXT_SIZE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__IMAGE_SIZE:
				setImageSize(IMAGE_SIZE_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__COLOR_RBG:
				getColorRBG().clear();
				return;
			case GamaPackage.ELAYER_ASPECT__ASPECT:
				setAspect((EAspect)null);
				return;
			case GamaPackage.ELAYER_ASPECT__DEPTH:
				setDepth(DEPTH_EDEFAULT);
				return;
			case GamaPackage.ELAYER_ASPECT__TEXTURE:
				setTexture(TEXTURE_EDEFAULT);
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
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case GamaPackage.ELAYER_ASPECT__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
			case GamaPackage.ELAYER_ASPECT__SHAPE:
				return SHAPE_EDEFAULT == null ? shape != null : !SHAPE_EDEFAULT.equals(shape);
			case GamaPackage.ELAYER_ASPECT__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case GamaPackage.ELAYER_ASPECT__EMPTY:
				return EMPTY_EDEFAULT == null ? empty != null : !EMPTY_EDEFAULT.equals(empty);
			case GamaPackage.ELAYER_ASPECT__ROTATE:
				return ROTATE_EDEFAULT == null ? rotate != null : !ROTATE_EDEFAULT.equals(rotate);
			case GamaPackage.ELAYER_ASPECT__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case GamaPackage.ELAYER_ASPECT__WIDTH:
				return WIDTH_EDEFAULT == null ? width != null : !WIDTH_EDEFAULT.equals(width);
			case GamaPackage.ELAYER_ASPECT__HEIGTH:
				return HEIGTH_EDEFAULT == null ? heigth != null : !HEIGTH_EDEFAULT.equals(heigth);
			case GamaPackage.ELAYER_ASPECT__RADIUS:
				return RADIUS_EDEFAULT == null ? radius != null : !RADIUS_EDEFAULT.equals(radius);
			case GamaPackage.ELAYER_ASPECT__PATH:
				return PATH_EDEFAULT == null ? path != null : !PATH_EDEFAULT.equals(path);
			case GamaPackage.ELAYER_ASPECT__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case GamaPackage.ELAYER_ASPECT__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case GamaPackage.ELAYER_ASPECT__EXPRESSION:
				return EXPRESSION_EDEFAULT == null ? expression != null : !EXPRESSION_EDEFAULT.equals(expression);
			case GamaPackage.ELAYER_ASPECT__POINTS:
				return POINTS_EDEFAULT == null ? points != null : !POINTS_EDEFAULT.equals(points);
			case GamaPackage.ELAYER_ASPECT__AT:
				return AT_EDEFAULT == null ? at != null : !AT_EDEFAULT.equals(at);
			case GamaPackage.ELAYER_ASPECT__SHAPE_TYPE:
				return SHAPE_TYPE_EDEFAULT == null ? shapeType != null : !SHAPE_TYPE_EDEFAULT.equals(shapeType);
			case GamaPackage.ELAYER_ASPECT__IS_COLOR_CST:
				return IS_COLOR_CST_EDEFAULT == null ? isColorCst != null : !IS_COLOR_CST_EDEFAULT.equals(isColorCst);
			case GamaPackage.ELAYER_ASPECT__TEXT_SIZE:
				return TEXT_SIZE_EDEFAULT == null ? textSize != null : !TEXT_SIZE_EDEFAULT.equals(textSize);
			case GamaPackage.ELAYER_ASPECT__IMAGE_SIZE:
				return IMAGE_SIZE_EDEFAULT == null ? imageSize != null : !IMAGE_SIZE_EDEFAULT.equals(imageSize);
			case GamaPackage.ELAYER_ASPECT__COLOR_RBG:
				return colorRBG != null && !colorRBG.isEmpty();
			case GamaPackage.ELAYER_ASPECT__ASPECT:
				return aspect != null;
			case GamaPackage.ELAYER_ASPECT__DEPTH:
				return DEPTH_EDEFAULT == null ? depth != null : !DEPTH_EDEFAULT.equals(depth);
			case GamaPackage.ELAYER_ASPECT__TEXTURE:
				return TEXTURE_EDEFAULT == null ? texture != null : !TEXTURE_EDEFAULT.equals(texture);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (gamlCode: ");
		result.append(gamlCode);
		result.append(", shape: ");
		result.append(shape);
		result.append(", color: ");
		result.append(color);
		result.append(", empty: ");
		result.append(empty);
		result.append(", rotate: ");
		result.append(rotate);
		result.append(", size: ");
		result.append(size);
		result.append(", width: ");
		result.append(width);
		result.append(", heigth: ");
		result.append(heigth);
		result.append(", radius: ");
		result.append(radius);
		result.append(", path: ");
		result.append(path);
		result.append(", text: ");
		result.append(text);
		result.append(", type: ");
		result.append(type);
		result.append(", expression: ");
		result.append(expression);
		result.append(", points: ");
		result.append(points);
		result.append(", at: ");
		result.append(at);
		result.append(", shapeType: ");
		result.append(shapeType);
		result.append(", isColorCst: ");
		result.append(isColorCst);
		result.append(", textSize: ");
		result.append(textSize);
		result.append(", imageSize: ");
		result.append(imageSize);
		result.append(", colorRBG: ");
		result.append(colorRBG);
		result.append(", depth: ");
		result.append(depth);
		result.append(", texture: ");
		result.append(texture);
		result.append(')');
		return result.toString();
	}

} //ELayerAspectImpl
