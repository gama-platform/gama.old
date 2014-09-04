/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EChartLayer;
import gama.EDisplay;
import gama.ELayer;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeEList;
import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ELayer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ELayerImpl#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getDisplay <em>Display</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getType <em>Type</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getFile <em>File</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getText <em>Text</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getSize <em>Size</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getSpecies <em>Species</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getTransparency <em>Transparency</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getAgents <em>Agents</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getPosition_x <em>Position x</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getPosition_y <em>Position y</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getSize_x <em>Size x</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getSize_y <em>Size y</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getAspect <em>Aspect</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getColor <em>Color</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getGrid <em>Grid</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getChartlayers <em>Chartlayers</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#getChart_type <em>Chart type</em>}</li>
 *   <li>{@link gama.impl.ELayerImpl#isShowLines <em>Show Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ELayerImpl extends EGamaObjectImpl implements ELayer {
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
	 * The cached value of the '{@link #getDisplay() <em>Display</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getDisplay()
	 * @generated
	 * @ordered
	 */
	protected EDisplay display;

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
	 * The default value of the '{@link #getFile() <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected static final String FILE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFile() <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFile()
	 * @generated
	 * @ordered
	 */
	protected String file = FILE_EDEFAULT;

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
	 * The default value of the '{@link #getSpecies() <em>Species</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpecies()
	 * @generated
	 * @ordered
	 */
	protected static final String SPECIES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSpecies() <em>Species</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSpecies()
	 * @generated
	 * @ordered
	 */
	protected String species = SPECIES_EDEFAULT;

	/**
	 * The default value of the '{@link #getTransparency() <em>Transparency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransparency()
	 * @generated
	 * @ordered
	 */
	protected static final String TRANSPARENCY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTransparency() <em>Transparency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTransparency()
	 * @generated
	 * @ordered
	 */
	protected String transparency = TRANSPARENCY_EDEFAULT;

	/**
	 * The default value of the '{@link #getAgents() <em>Agents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAgents()
	 * @generated
	 * @ordered
	 */
	protected static final String AGENTS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAgents() <em>Agents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAgents()
	 * @generated
	 * @ordered
	 */
	protected String agents = AGENTS_EDEFAULT;

	/**
	 * The default value of the '{@link #getPosition_x() <em>Position x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPosition_x()
	 * @generated
	 * @ordered
	 */
	protected static final String POSITION_X_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPosition_x() <em>Position x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPosition_x()
	 * @generated
	 * @ordered
	 */
	protected String position_x = POSITION_X_EDEFAULT;

	/**
	 * The default value of the '{@link #getPosition_y() <em>Position y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPosition_y()
	 * @generated
	 * @ordered
	 */
	protected static final String POSITION_Y_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getPosition_y() <em>Position y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPosition_y()
	 * @generated
	 * @ordered
	 */
	protected String position_y = POSITION_Y_EDEFAULT;

	/**
	 * The default value of the '{@link #getSize_x() <em>Size x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize_x()
	 * @generated
	 * @ordered
	 */
	protected static final String SIZE_X_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSize_x() <em>Size x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize_x()
	 * @generated
	 * @ordered
	 */
	protected String size_x = SIZE_X_EDEFAULT;

	/**
	 * The default value of the '{@link #getSize_y() <em>Size y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize_y()
	 * @generated
	 * @ordered
	 */
	protected static final String SIZE_Y_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSize_y() <em>Size y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSize_y()
	 * @generated
	 * @ordered
	 */
	protected String size_y = SIZE_Y_EDEFAULT;

	/**
	 * The default value of the '{@link #getAspect() <em>Aspect</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspect()
	 * @generated
	 * @ordered
	 */
	protected static final String ASPECT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getAspect() <em>Aspect</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspect()
	 * @generated
	 * @ordered
	 */
	protected String aspect = ASPECT_EDEFAULT;

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
	 * The cached value of the '{@link #getColorRBG() <em>Color RBG</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getColorRBG()
	 * @generated
	 * @ordered
	 */
	protected EList<Integer> colorRBG;

	/**
	 * The default value of the '{@link #getGrid() <em>Grid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGrid()
	 * @generated
	 * @ordered
	 */
	protected static final String GRID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getGrid() <em>Grid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getGrid()
	 * @generated
	 * @ordered
	 */
	protected String grid = GRID_EDEFAULT;

	/**
	 * The default value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected static final String REFRESH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getRefresh() <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getRefresh()
	 * @generated
	 * @ordered
	 */
	protected String refresh = REFRESH_EDEFAULT;

	/**
	 * The cached value of the '{@link #getChartlayers() <em>Chartlayers</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChartlayers()
	 * @generated
	 * @ordered
	 */
	protected EList<EChartLayer> chartlayers;

	/**
	 * The default value of the '{@link #getChart_type() <em>Chart type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChart_type()
	 * @generated
	 * @ordered
	 */
	protected static final String CHART_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getChart_type() <em>Chart type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getChart_type()
	 * @generated
	 * @ordered
	 */
	protected String chart_type = CHART_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #isShowLines() <em>Show Lines</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShowLines()
	 * @generated
	 * @ordered
	 */
	protected static final boolean SHOW_LINES_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isShowLines() <em>Show Lines</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isShowLines()
	 * @generated
	 * @ordered
	 */
	protected boolean showLines = SHOW_LINES_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ELayerImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.ELAYER;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__GAML_CODE, oldGamlCode, gamlCode));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplay getDisplay() {
		if (display != null && display.eIsProxy()) {
			InternalEObject oldDisplay = (InternalEObject)display;
			display = (EDisplay)eResolveProxy(oldDisplay);
			if (display != oldDisplay) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ELAYER__DISPLAY, oldDisplay, display));
			}
		}
		return display;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EDisplay basicGetDisplay() {
		return display;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setDisplay(EDisplay newDisplay) {
		EDisplay oldDisplay = display;
		display = newDisplay;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__DISPLAY, oldDisplay, display));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__TYPE, oldType, type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getFile() {
		return file;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFile(String newFile) {
		String oldFile = file;
		file = newFile;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__FILE, oldFile, file));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__TEXT, oldText, text));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__SIZE, oldSize, size));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSpecies() {
		return species;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSpecies(String newSpecies) {
		String oldSpecies = species;
		species = newSpecies;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__SPECIES, oldSpecies, species));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTransparency() {
		return transparency;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTransparency(String newTransparency) {
		String oldTransparency = transparency;
		transparency = newTransparency;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__TRANSPARENCY, oldTransparency, transparency));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAgents() {
		return agents;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAgents(String newAgents) {
		String oldAgents = agents;
		agents = newAgents;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__AGENTS, oldAgents, agents));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPosition_x() {
		return position_x;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPosition_x(String newPosition_x) {
		String oldPosition_x = position_x;
		position_x = newPosition_x;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__POSITION_X, oldPosition_x, position_x));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getPosition_y() {
		return position_y;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPosition_y(String newPosition_y) {
		String oldPosition_y = position_y;
		position_y = newPosition_y;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__POSITION_Y, oldPosition_y, position_y));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSize_x() {
		return size_x;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSize_x(String newSize_x) {
		String oldSize_x = size_x;
		size_x = newSize_x;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__SIZE_X, oldSize_x, size_x));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSize_y() {
		return size_y;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSize_y(String newSize_y) {
		String oldSize_y = size_y;
		size_y = newSize_y;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__SIZE_Y, oldSize_y, size_y));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getAspect() {
		return aspect;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAspect(String newAspect) {
		String oldAspect = aspect;
		aspect = newAspect;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__ASPECT, oldAspect, aspect));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__COLOR, oldColor, color));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__IS_COLOR_CST, oldIsColorCst, isColorCst));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<Integer> getColorRBG() {
		if (colorRBG == null) {
			colorRBG = new EDataTypeEList<Integer>(Integer.class, this, GamaPackage.ELAYER__COLOR_RBG);
		}
		return colorRBG;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getGrid() {
		return grid;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setGrid(String newGrid) {
		String oldGrid = grid;
		grid = newGrid;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__GRID, oldGrid, grid));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getRefresh() {
		return refresh;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setRefresh(String newRefresh) {
		String oldRefresh = refresh;
		refresh = newRefresh;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__REFRESH, oldRefresh, refresh));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EChartLayer> getChartlayers() {
		if (chartlayers == null) {
			chartlayers = new EObjectResolvingEList<EChartLayer>(EChartLayer.class, this, GamaPackage.ELAYER__CHARTLAYERS);
		}
		return chartlayers;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getChart_type() {
		return chart_type;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setChart_type(String newChart_type) {
		String oldChart_type = chart_type;
		chart_type = newChart_type;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__CHART_TYPE, oldChart_type, chart_type));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isShowLines() {
		return showLines;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShowLines(boolean newShowLines) {
		boolean oldShowLines = showLines;
		showLines = newShowLines;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ELAYER__SHOW_LINES, oldShowLines, showLines));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.ELAYER__GAML_CODE:
				return getGamlCode();
			case GamaPackage.ELAYER__DISPLAY:
				if (resolve) return getDisplay();
				return basicGetDisplay();
			case GamaPackage.ELAYER__TYPE:
				return getType();
			case GamaPackage.ELAYER__FILE:
				return getFile();
			case GamaPackage.ELAYER__TEXT:
				return getText();
			case GamaPackage.ELAYER__SIZE:
				return getSize();
			case GamaPackage.ELAYER__SPECIES:
				return getSpecies();
			case GamaPackage.ELAYER__TRANSPARENCY:
				return getTransparency();
			case GamaPackage.ELAYER__AGENTS:
				return getAgents();
			case GamaPackage.ELAYER__POSITION_X:
				return getPosition_x();
			case GamaPackage.ELAYER__POSITION_Y:
				return getPosition_y();
			case GamaPackage.ELAYER__SIZE_X:
				return getSize_x();
			case GamaPackage.ELAYER__SIZE_Y:
				return getSize_y();
			case GamaPackage.ELAYER__ASPECT:
				return getAspect();
			case GamaPackage.ELAYER__COLOR:
				return getColor();
			case GamaPackage.ELAYER__IS_COLOR_CST:
				return getIsColorCst();
			case GamaPackage.ELAYER__COLOR_RBG:
				return getColorRBG();
			case GamaPackage.ELAYER__GRID:
				return getGrid();
			case GamaPackage.ELAYER__REFRESH:
				return getRefresh();
			case GamaPackage.ELAYER__CHARTLAYERS:
				return getChartlayers();
			case GamaPackage.ELAYER__CHART_TYPE:
				return getChart_type();
			case GamaPackage.ELAYER__SHOW_LINES:
				return isShowLines();
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
			case GamaPackage.ELAYER__GAML_CODE:
				setGamlCode((String)newValue);
				return;
			case GamaPackage.ELAYER__DISPLAY:
				setDisplay((EDisplay)newValue);
				return;
			case GamaPackage.ELAYER__TYPE:
				setType((String)newValue);
				return;
			case GamaPackage.ELAYER__FILE:
				setFile((String)newValue);
				return;
			case GamaPackage.ELAYER__TEXT:
				setText((String)newValue);
				return;
			case GamaPackage.ELAYER__SIZE:
				setSize((String)newValue);
				return;
			case GamaPackage.ELAYER__SPECIES:
				setSpecies((String)newValue);
				return;
			case GamaPackage.ELAYER__TRANSPARENCY:
				setTransparency((String)newValue);
				return;
			case GamaPackage.ELAYER__AGENTS:
				setAgents((String)newValue);
				return;
			case GamaPackage.ELAYER__POSITION_X:
				setPosition_x((String)newValue);
				return;
			case GamaPackage.ELAYER__POSITION_Y:
				setPosition_y((String)newValue);
				return;
			case GamaPackage.ELAYER__SIZE_X:
				setSize_x((String)newValue);
				return;
			case GamaPackage.ELAYER__SIZE_Y:
				setSize_y((String)newValue);
				return;
			case GamaPackage.ELAYER__ASPECT:
				setAspect((String)newValue);
				return;
			case GamaPackage.ELAYER__COLOR:
				setColor((String)newValue);
				return;
			case GamaPackage.ELAYER__IS_COLOR_CST:
				setIsColorCst((Boolean)newValue);
				return;
			case GamaPackage.ELAYER__COLOR_RBG:
				getColorRBG().clear();
				getColorRBG().addAll((Collection<? extends Integer>)newValue);
				return;
			case GamaPackage.ELAYER__GRID:
				setGrid((String)newValue);
				return;
			case GamaPackage.ELAYER__REFRESH:
				setRefresh((String)newValue);
				return;
			case GamaPackage.ELAYER__CHARTLAYERS:
				getChartlayers().clear();
				getChartlayers().addAll((Collection<? extends EChartLayer>)newValue);
				return;
			case GamaPackage.ELAYER__CHART_TYPE:
				setChart_type((String)newValue);
				return;
			case GamaPackage.ELAYER__SHOW_LINES:
				setShowLines((Boolean)newValue);
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
			case GamaPackage.ELAYER__GAML_CODE:
				setGamlCode(GAML_CODE_EDEFAULT);
				return;
			case GamaPackage.ELAYER__DISPLAY:
				setDisplay((EDisplay)null);
				return;
			case GamaPackage.ELAYER__TYPE:
				setType(TYPE_EDEFAULT);
				return;
			case GamaPackage.ELAYER__FILE:
				setFile(FILE_EDEFAULT);
				return;
			case GamaPackage.ELAYER__TEXT:
				setText(TEXT_EDEFAULT);
				return;
			case GamaPackage.ELAYER__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case GamaPackage.ELAYER__SPECIES:
				setSpecies(SPECIES_EDEFAULT);
				return;
			case GamaPackage.ELAYER__TRANSPARENCY:
				setTransparency(TRANSPARENCY_EDEFAULT);
				return;
			case GamaPackage.ELAYER__AGENTS:
				setAgents(AGENTS_EDEFAULT);
				return;
			case GamaPackage.ELAYER__POSITION_X:
				setPosition_x(POSITION_X_EDEFAULT);
				return;
			case GamaPackage.ELAYER__POSITION_Y:
				setPosition_y(POSITION_Y_EDEFAULT);
				return;
			case GamaPackage.ELAYER__SIZE_X:
				setSize_x(SIZE_X_EDEFAULT);
				return;
			case GamaPackage.ELAYER__SIZE_Y:
				setSize_y(SIZE_Y_EDEFAULT);
				return;
			case GamaPackage.ELAYER__ASPECT:
				setAspect(ASPECT_EDEFAULT);
				return;
			case GamaPackage.ELAYER__COLOR:
				setColor(COLOR_EDEFAULT);
				return;
			case GamaPackage.ELAYER__IS_COLOR_CST:
				setIsColorCst(IS_COLOR_CST_EDEFAULT);
				return;
			case GamaPackage.ELAYER__COLOR_RBG:
				getColorRBG().clear();
				return;
			case GamaPackage.ELAYER__GRID:
				setGrid(GRID_EDEFAULT);
				return;
			case GamaPackage.ELAYER__REFRESH:
				setRefresh(REFRESH_EDEFAULT);
				return;
			case GamaPackage.ELAYER__CHARTLAYERS:
				getChartlayers().clear();
				return;
			case GamaPackage.ELAYER__CHART_TYPE:
				setChart_type(CHART_TYPE_EDEFAULT);
				return;
			case GamaPackage.ELAYER__SHOW_LINES:
				setShowLines(SHOW_LINES_EDEFAULT);
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
			case GamaPackage.ELAYER__GAML_CODE:
				return GAML_CODE_EDEFAULT == null ? gamlCode != null : !GAML_CODE_EDEFAULT.equals(gamlCode);
			case GamaPackage.ELAYER__DISPLAY:
				return display != null;
			case GamaPackage.ELAYER__TYPE:
				return TYPE_EDEFAULT == null ? type != null : !TYPE_EDEFAULT.equals(type);
			case GamaPackage.ELAYER__FILE:
				return FILE_EDEFAULT == null ? file != null : !FILE_EDEFAULT.equals(file);
			case GamaPackage.ELAYER__TEXT:
				return TEXT_EDEFAULT == null ? text != null : !TEXT_EDEFAULT.equals(text);
			case GamaPackage.ELAYER__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case GamaPackage.ELAYER__SPECIES:
				return SPECIES_EDEFAULT == null ? species != null : !SPECIES_EDEFAULT.equals(species);
			case GamaPackage.ELAYER__TRANSPARENCY:
				return TRANSPARENCY_EDEFAULT == null ? transparency != null : !TRANSPARENCY_EDEFAULT.equals(transparency);
			case GamaPackage.ELAYER__AGENTS:
				return AGENTS_EDEFAULT == null ? agents != null : !AGENTS_EDEFAULT.equals(agents);
			case GamaPackage.ELAYER__POSITION_X:
				return POSITION_X_EDEFAULT == null ? position_x != null : !POSITION_X_EDEFAULT.equals(position_x);
			case GamaPackage.ELAYER__POSITION_Y:
				return POSITION_Y_EDEFAULT == null ? position_y != null : !POSITION_Y_EDEFAULT.equals(position_y);
			case GamaPackage.ELAYER__SIZE_X:
				return SIZE_X_EDEFAULT == null ? size_x != null : !SIZE_X_EDEFAULT.equals(size_x);
			case GamaPackage.ELAYER__SIZE_Y:
				return SIZE_Y_EDEFAULT == null ? size_y != null : !SIZE_Y_EDEFAULT.equals(size_y);
			case GamaPackage.ELAYER__ASPECT:
				return ASPECT_EDEFAULT == null ? aspect != null : !ASPECT_EDEFAULT.equals(aspect);
			case GamaPackage.ELAYER__COLOR:
				return COLOR_EDEFAULT == null ? color != null : !COLOR_EDEFAULT.equals(color);
			case GamaPackage.ELAYER__IS_COLOR_CST:
				return IS_COLOR_CST_EDEFAULT == null ? isColorCst != null : !IS_COLOR_CST_EDEFAULT.equals(isColorCst);
			case GamaPackage.ELAYER__COLOR_RBG:
				return colorRBG != null && !colorRBG.isEmpty();
			case GamaPackage.ELAYER__GRID:
				return GRID_EDEFAULT == null ? grid != null : !GRID_EDEFAULT.equals(grid);
			case GamaPackage.ELAYER__REFRESH:
				return REFRESH_EDEFAULT == null ? refresh != null : !REFRESH_EDEFAULT.equals(refresh);
			case GamaPackage.ELAYER__CHARTLAYERS:
				return chartlayers != null && !chartlayers.isEmpty();
			case GamaPackage.ELAYER__CHART_TYPE:
				return CHART_TYPE_EDEFAULT == null ? chart_type != null : !CHART_TYPE_EDEFAULT.equals(chart_type);
			case GamaPackage.ELAYER__SHOW_LINES:
				return showLines != SHOW_LINES_EDEFAULT;
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
		result.append(", type: ");
		result.append(type);
		result.append(", file: ");
		result.append(file);
		result.append(", text: ");
		result.append(text);
		result.append(", size: ");
		result.append(size);
		result.append(", species: ");
		result.append(species);
		result.append(", transparency: ");
		result.append(transparency);
		result.append(", agents: ");
		result.append(agents);
		result.append(", position_x: ");
		result.append(position_x);
		result.append(", position_y: ");
		result.append(position_y);
		result.append(", size_x: ");
		result.append(size_x);
		result.append(", size_y: ");
		result.append(size_y);
		result.append(", aspect: ");
		result.append(aspect);
		result.append(", color: ");
		result.append(color);
		result.append(", isColorCst: ");
		result.append(isColorCst);
		result.append(", colorRBG: ");
		result.append(colorRBG);
		result.append(", grid: ");
		result.append(grid);
		result.append(", refresh: ");
		result.append(refresh);
		result.append(", chart_type: ");
		result.append(chart_type);
		result.append(", showLines: ");
		result.append(showLines);
		result.append(')');
		return result.toString();
	}

} //ELayerImpl
