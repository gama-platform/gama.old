/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama.impl;

import gama.EActionLink;
import gama.EAspectLink;
import gama.EExperimentLink;
import gama.EInheritLink;
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
import gama.ETopology;
import gama.EVariable;
import gama.GamaPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EDataTypeUniqueEList;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ESpecies</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ESpeciesImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getReflexList <em>Reflex List</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTorus <em>Torus</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getExperimentLinks <em>Experiment Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getAspectLinks <em>Aspect Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getActionLinks <em>Action Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getReflexLinks <em>Reflex Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getShape <em>Shape</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getSize <em>Size</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getWidth <em>Width</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getHeigth <em>Heigth</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getRadius <em>Radius</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getMicroSpeciesLinks <em>Micro Species Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getMacroSpeciesLinks <em>Macro Species Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getSkills <em>Skills</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTopology <em>Topology</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getInheritsFrom <em>Inherits From</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTorusType <em>Torus Type</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getShapeType <em>Shape Type</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getLocationType <em>Location Type</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getPoints <em>Points</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getExpressionShape <em>Expression Shape</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getExpressionLoc <em>Expression Loc</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getExpressionTorus <em>Expression Torus</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getShapeFunction <em>Shape Function</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getShapeUpdate <em>Shape Update</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getShapeIsFunction <em>Shape Is Function</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getLocationIsFunction <em>Location Is Function</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getLocationFunction <em>Location Function</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getLocationUpdate <em>Location Update</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getInit <em>Init</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getInheritingLinks <em>Inheriting Links</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getSchedules <em>Schedules</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ESpeciesImpl extends EGamaObjectImpl implements ESpecies {
	/**
	 * The cached value of the '{@link #getVariables() <em>Variables</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getVariables()
	 * @generated
	 * @ordered
	 */
	protected EList<EVariable> variables;

	/**
	 * The cached value of the '{@link #getReflexList() <em>Reflex List</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReflexList()
	 * @generated
	 * @ordered
	 */
	protected EList<String> reflexList;

	/**
	 * The default value of the '{@link #getTorus() <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorus()
	 * @generated
	 * @ordered
	 */
	protected static final String TORUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTorus() <em>Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorus()
	 * @generated
	 * @ordered
	 */
	protected String torus = TORUS_EDEFAULT;

	/**
	 * The cached value of the '{@link #getExperimentLinks() <em>Experiment Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExperimentLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EExperimentLink> experimentLinks;

	/**
	 * The cached value of the '{@link #getAspectLinks() <em>Aspect Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getAspectLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EAspectLink> aspectLinks;

	/**
	 * The cached value of the '{@link #getActionLinks() <em>Action Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getActionLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EActionLink> actionLinks;

	/**
	 * The cached value of the '{@link #getReflexLinks() <em>Reflex Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getReflexLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EReflexLink> reflexLinks;

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
	 * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocation()
	 * @generated
	 * @ordered
	 */
	protected String location = LOCATION_EDEFAULT;

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
	 * The cached value of the '{@link #getMicroSpeciesLinks() <em>Micro Species Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMicroSpeciesLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<ESubSpeciesLink> microSpeciesLinks;

	/**
	 * The cached value of the '{@link #getMacroSpeciesLinks() <em>Macro Species Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getMacroSpeciesLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<ESubSpeciesLink> macroSpeciesLinks;

	/**
	 * The cached value of the '{@link #getSkills() <em>Skills</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSkills()
	 * @generated
	 * @ordered
	 */
	protected EList<String> skills;

	/**
	 * The cached value of the '{@link #getTopology() <em>Topology</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopology()
	 * @generated
	 * @ordered
	 */
	protected ETopology topology;

	/**
	 * The cached value of the '{@link #getInheritsFrom() <em>Inherits From</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInheritsFrom()
	 * @generated
	 * @ordered
	 */
	protected ESpecies inheritsFrom;

	/**
	 * The default value of the '{@link #getTorusType() <em>Torus Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorusType()
	 * @generated
	 * @ordered
	 */
	protected static final String TORUS_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getTorusType() <em>Torus Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTorusType()
	 * @generated
	 * @ordered
	 */
	protected String torusType = TORUS_TYPE_EDEFAULT;

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
	 * The default value of the '{@link #getLocationType() <em>Location Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationType()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocationType() <em>Location Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationType()
	 * @generated
	 * @ordered
	 */
	protected String locationType = LOCATION_TYPE_EDEFAULT;

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
	 * The default value of the '{@link #getExpressionShape() <em>Expression Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionShape()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPRESSION_SHAPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpressionShape() <em>Expression Shape</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionShape()
	 * @generated
	 * @ordered
	 */
	protected String expressionShape = EXPRESSION_SHAPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getExpressionLoc() <em>Expression Loc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionLoc()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPRESSION_LOC_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpressionLoc() <em>Expression Loc</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionLoc()
	 * @generated
	 * @ordered
	 */
	protected String expressionLoc = EXPRESSION_LOC_EDEFAULT;

	/**
	 * The default value of the '{@link #getExpressionTorus() <em>Expression Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionTorus()
	 * @generated
	 * @ordered
	 */
	protected static final String EXPRESSION_TORUS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExpressionTorus() <em>Expression Torus</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExpressionTorus()
	 * @generated
	 * @ordered
	 */
	protected String expressionTorus = EXPRESSION_TORUS_EDEFAULT;

	/**
	 * The default value of the '{@link #getShapeFunction() <em>Shape Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeFunction()
	 * @generated
	 * @ordered
	 */
	protected static final String SHAPE_FUNCTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShapeFunction() <em>Shape Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeFunction()
	 * @generated
	 * @ordered
	 */
	protected String shapeFunction = SHAPE_FUNCTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getShapeUpdate() <em>Shape Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeUpdate()
	 * @generated
	 * @ordered
	 */
	protected static final String SHAPE_UPDATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShapeUpdate() <em>Shape Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeUpdate()
	 * @generated
	 * @ordered
	 */
	protected String shapeUpdate = SHAPE_UPDATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getShapeIsFunction() <em>Shape Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeIsFunction()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean SHAPE_IS_FUNCTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getShapeIsFunction() <em>Shape Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getShapeIsFunction()
	 * @generated
	 * @ordered
	 */
	protected Boolean shapeIsFunction = SHAPE_IS_FUNCTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getLocationIsFunction() <em>Location Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationIsFunction()
	 * @generated
	 * @ordered
	 */
	protected static final Boolean LOCATION_IS_FUNCTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocationIsFunction() <em>Location Is Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationIsFunction()
	 * @generated
	 * @ordered
	 */
	protected Boolean locationIsFunction = LOCATION_IS_FUNCTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getLocationFunction() <em>Location Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationFunction()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_FUNCTION_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocationFunction() <em>Location Function</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationFunction()
	 * @generated
	 * @ordered
	 */
	protected String locationFunction = LOCATION_FUNCTION_EDEFAULT;

	/**
	 * The default value of the '{@link #getLocationUpdate() <em>Location Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationUpdate()
	 * @generated
	 * @ordered
	 */
	protected static final String LOCATION_UPDATE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLocationUpdate() <em>Location Update</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLocationUpdate()
	 * @generated
	 * @ordered
	 */
	protected String locationUpdate = LOCATION_UPDATE_EDEFAULT;

	/**
	 * The default value of the '{@link #getInit() <em>Init</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInit()
	 * @generated
	 * @ordered
	 */
	protected static final String INIT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInit() <em>Init</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInit()
	 * @generated
	 * @ordered
	 */
	protected String init = INIT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getInheritingLinks() <em>Inheriting Links</em>}' reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInheritingLinks()
	 * @generated
	 * @ordered
	 */
	protected EList<EInheritLink> inheritingLinks;

	/**
	 * The default value of the '{@link #getSchedules() <em>Schedules</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchedules()
	 * @generated
	 * @ordered
	 */
	protected static final String SCHEDULES_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getSchedules() <em>Schedules</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSchedules()
	 * @generated
	 * @ordered
	 */
	protected String schedules = SCHEDULES_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ESpeciesImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return GamaPackage.Literals.ESPECIES;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EVariable> getVariables() {
		if (variables == null) {
			variables = new EObjectContainmentEList<EVariable>(EVariable.class, this, GamaPackage.ESPECIES__VARIABLES);
		}
		return variables;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getReflexList() {
		if (reflexList == null) {
			reflexList = new EDataTypeUniqueEList<String>(String.class, this, GamaPackage.ESPECIES__REFLEX_LIST);
		}
		return reflexList;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTorus() {
		return torus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTorus(String newTorus) {
		String oldTorus = torus;
		torus = newTorus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TORUS, oldTorus, torus));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EExperimentLink> getExperimentLinks() {
		if (experimentLinks == null) {
			experimentLinks = new EObjectResolvingEList<EExperimentLink>(EExperimentLink.class, this, GamaPackage.ESPECIES__EXPERIMENT_LINKS);
		}
		return experimentLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EAspectLink> getAspectLinks() {
		if (aspectLinks == null) {
			aspectLinks = new EObjectResolvingEList<EAspectLink>(EAspectLink.class, this, GamaPackage.ESPECIES__ASPECT_LINKS);
		}
		return aspectLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EActionLink> getActionLinks() {
		if (actionLinks == null) {
			actionLinks = new EObjectResolvingEList<EActionLink>(EActionLink.class, this, GamaPackage.ESPECIES__ACTION_LINKS);
		}
		return actionLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EReflexLink> getReflexLinks() {
		if (reflexLinks == null) {
			reflexLinks = new EObjectResolvingEList<EReflexLink>(EReflexLink.class, this, GamaPackage.ESPECIES__REFLEX_LINKS);
		}
		return reflexLinks;
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SHAPE, oldShape, shape));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocation(String newLocation) {
		String oldLocation = location;
		location = newLocation;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__LOCATION, oldLocation, location));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SIZE, oldSize, size));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__WIDTH, oldWidth, width));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__HEIGTH, oldHeigth, heigth));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__RADIUS, oldRadius, radius));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ESubSpeciesLink> getMicroSpeciesLinks() {
		if (microSpeciesLinks == null) {
			microSpeciesLinks = new EObjectResolvingEList<ESubSpeciesLink>(ESubSpeciesLink.class, this, GamaPackage.ESPECIES__MICRO_SPECIES_LINKS);
		}
		return microSpeciesLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<ESubSpeciesLink> getMacroSpeciesLinks() {
		if (macroSpeciesLinks == null) {
			macroSpeciesLinks = new EObjectResolvingEList<ESubSpeciesLink>(ESubSpeciesLink.class, this, GamaPackage.ESPECIES__MACRO_SPECIES_LINKS);
		}
		return macroSpeciesLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getSkills() {
		if (skills == null) {
			skills = new EDataTypeUniqueEList<String>(String.class, this, GamaPackage.ESPECIES__SKILLS);
		}
		return skills;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ETopology getTopology() {
		if (topology != null && topology.eIsProxy()) {
			InternalEObject oldTopology = (InternalEObject)topology;
			topology = (ETopology)eResolveProxy(oldTopology);
			if (topology != oldTopology) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ESPECIES__TOPOLOGY, oldTopology, topology));
			}
		}
		return topology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ETopology basicGetTopology() {
		return topology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTopology(ETopology newTopology) {
		ETopology oldTopology = topology;
		topology = newTopology;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TOPOLOGY, oldTopology, topology));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies getInheritsFrom() {
		if (inheritsFrom != null && inheritsFrom.eIsProxy()) {
			InternalEObject oldInheritsFrom = (InternalEObject)inheritsFrom;
			inheritsFrom = (ESpecies)eResolveProxy(oldInheritsFrom);
			if (inheritsFrom != oldInheritsFrom) {
				if (eNotificationRequired())
					eNotify(new ENotificationImpl(this, Notification.RESOLVE, GamaPackage.ESPECIES__INHERITS_FROM, oldInheritsFrom, inheritsFrom));
			}
		}
		return inheritsFrom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ESpecies basicGetInheritsFrom() {
		return inheritsFrom;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInheritsFrom(ESpecies newInheritsFrom) {
		ESpecies oldInheritsFrom = inheritsFrom;
		inheritsFrom = newInheritsFrom;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__INHERITS_FROM, oldInheritsFrom, inheritsFrom));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTorusType() {
		return torusType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTorusType(String newTorusType) {
		String oldTorusType = torusType;
		torusType = newTorusType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TORUS_TYPE, oldTorusType, torusType));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SHAPE_TYPE, oldShapeType, shapeType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocationType() {
		return locationType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocationType(String newLocationType) {
		String oldLocationType = locationType;
		locationType = newLocationType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__LOCATION_TYPE, oldLocationType, locationType));
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
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__POINTS, oldPoints, points));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExpressionShape() {
		return expressionShape;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpressionShape(String newExpressionShape) {
		String oldExpressionShape = expressionShape;
		expressionShape = newExpressionShape;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__EXPRESSION_SHAPE, oldExpressionShape, expressionShape));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExpressionLoc() {
		return expressionLoc;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpressionLoc(String newExpressionLoc) {
		String oldExpressionLoc = expressionLoc;
		expressionLoc = newExpressionLoc;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__EXPRESSION_LOC, oldExpressionLoc, expressionLoc));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExpressionTorus() {
		return expressionTorus;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExpressionTorus(String newExpressionTorus) {
		String oldExpressionTorus = expressionTorus;
		expressionTorus = newExpressionTorus;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__EXPRESSION_TORUS, oldExpressionTorus, expressionTorus));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getShapeFunction() {
		return shapeFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShapeFunction(String newShapeFunction) {
		String oldShapeFunction = shapeFunction;
		shapeFunction = newShapeFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SHAPE_FUNCTION, oldShapeFunction, shapeFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getShapeUpdate() {
		return shapeUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShapeUpdate(String newShapeUpdate) {
		String oldShapeUpdate = shapeUpdate;
		shapeUpdate = newShapeUpdate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SHAPE_UPDATE, oldShapeUpdate, shapeUpdate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getShapeIsFunction() {
		return shapeIsFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setShapeIsFunction(Boolean newShapeIsFunction) {
		Boolean oldShapeIsFunction = shapeIsFunction;
		shapeIsFunction = newShapeIsFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SHAPE_IS_FUNCTION, oldShapeIsFunction, shapeIsFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Boolean getLocationIsFunction() {
		return locationIsFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocationIsFunction(Boolean newLocationIsFunction) {
		Boolean oldLocationIsFunction = locationIsFunction;
		locationIsFunction = newLocationIsFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__LOCATION_IS_FUNCTION, oldLocationIsFunction, locationIsFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocationFunction() {
		return locationFunction;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocationFunction(String newLocationFunction) {
		String oldLocationFunction = locationFunction;
		locationFunction = newLocationFunction;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__LOCATION_FUNCTION, oldLocationFunction, locationFunction));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getLocationUpdate() {
		return locationUpdate;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLocationUpdate(String newLocationUpdate) {
		String oldLocationUpdate = locationUpdate;
		locationUpdate = newLocationUpdate;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__LOCATION_UPDATE, oldLocationUpdate, locationUpdate));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getInit() {
		return init;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInit(String newInit) {
		String oldInit = init;
		init = newInit;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__INIT, oldInit, init));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<EInheritLink> getInheritingLinks() {
		if (inheritingLinks == null) {
			inheritingLinks = new EObjectResolvingEList<EInheritLink>(EInheritLink.class, this, GamaPackage.ESPECIES__INHERITING_LINKS);
		}
		return inheritingLinks;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getSchedules() {
		return schedules;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setSchedules(String newSchedules) {
		String oldSchedules = schedules;
		schedules = newSchedules;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__SCHEDULES, oldSchedules, schedules));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case GamaPackage.ESPECIES__VARIABLES:
				return ((InternalEList<?>)getVariables()).basicRemove(otherEnd, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case GamaPackage.ESPECIES__VARIABLES:
				return getVariables();
			case GamaPackage.ESPECIES__REFLEX_LIST:
				return getReflexList();
			case GamaPackage.ESPECIES__TORUS:
				return getTorus();
			case GamaPackage.ESPECIES__EXPERIMENT_LINKS:
				return getExperimentLinks();
			case GamaPackage.ESPECIES__ASPECT_LINKS:
				return getAspectLinks();
			case GamaPackage.ESPECIES__ACTION_LINKS:
				return getActionLinks();
			case GamaPackage.ESPECIES__REFLEX_LINKS:
				return getReflexLinks();
			case GamaPackage.ESPECIES__SHAPE:
				return getShape();
			case GamaPackage.ESPECIES__LOCATION:
				return getLocation();
			case GamaPackage.ESPECIES__SIZE:
				return getSize();
			case GamaPackage.ESPECIES__WIDTH:
				return getWidth();
			case GamaPackage.ESPECIES__HEIGTH:
				return getHeigth();
			case GamaPackage.ESPECIES__RADIUS:
				return getRadius();
			case GamaPackage.ESPECIES__MICRO_SPECIES_LINKS:
				return getMicroSpeciesLinks();
			case GamaPackage.ESPECIES__MACRO_SPECIES_LINKS:
				return getMacroSpeciesLinks();
			case GamaPackage.ESPECIES__SKILLS:
				return getSkills();
			case GamaPackage.ESPECIES__TOPOLOGY:
				if (resolve) return getTopology();
				return basicGetTopology();
			case GamaPackage.ESPECIES__INHERITS_FROM:
				if (resolve) return getInheritsFrom();
				return basicGetInheritsFrom();
			case GamaPackage.ESPECIES__TORUS_TYPE:
				return getTorusType();
			case GamaPackage.ESPECIES__SHAPE_TYPE:
				return getShapeType();
			case GamaPackage.ESPECIES__LOCATION_TYPE:
				return getLocationType();
			case GamaPackage.ESPECIES__POINTS:
				return getPoints();
			case GamaPackage.ESPECIES__EXPRESSION_SHAPE:
				return getExpressionShape();
			case GamaPackage.ESPECIES__EXPRESSION_LOC:
				return getExpressionLoc();
			case GamaPackage.ESPECIES__EXPRESSION_TORUS:
				return getExpressionTorus();
			case GamaPackage.ESPECIES__SHAPE_FUNCTION:
				return getShapeFunction();
			case GamaPackage.ESPECIES__SHAPE_UPDATE:
				return getShapeUpdate();
			case GamaPackage.ESPECIES__SHAPE_IS_FUNCTION:
				return getShapeIsFunction();
			case GamaPackage.ESPECIES__LOCATION_IS_FUNCTION:
				return getLocationIsFunction();
			case GamaPackage.ESPECIES__LOCATION_FUNCTION:
				return getLocationFunction();
			case GamaPackage.ESPECIES__LOCATION_UPDATE:
				return getLocationUpdate();
			case GamaPackage.ESPECIES__INIT:
				return getInit();
			case GamaPackage.ESPECIES__INHERITING_LINKS:
				return getInheritingLinks();
			case GamaPackage.ESPECIES__SCHEDULES:
				return getSchedules();
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
			case GamaPackage.ESPECIES__VARIABLES:
				getVariables().clear();
				getVariables().addAll((Collection<? extends EVariable>)newValue);
				return;
			case GamaPackage.ESPECIES__REFLEX_LIST:
				getReflexList().clear();
				getReflexList().addAll((Collection<? extends String>)newValue);
				return;
			case GamaPackage.ESPECIES__TORUS:
				setTorus((String)newValue);
				return;
			case GamaPackage.ESPECIES__EXPERIMENT_LINKS:
				getExperimentLinks().clear();
				getExperimentLinks().addAll((Collection<? extends EExperimentLink>)newValue);
				return;
			case GamaPackage.ESPECIES__ASPECT_LINKS:
				getAspectLinks().clear();
				getAspectLinks().addAll((Collection<? extends EAspectLink>)newValue);
				return;
			case GamaPackage.ESPECIES__ACTION_LINKS:
				getActionLinks().clear();
				getActionLinks().addAll((Collection<? extends EActionLink>)newValue);
				return;
			case GamaPackage.ESPECIES__REFLEX_LINKS:
				getReflexLinks().clear();
				getReflexLinks().addAll((Collection<? extends EReflexLink>)newValue);
				return;
			case GamaPackage.ESPECIES__SHAPE:
				setShape((String)newValue);
				return;
			case GamaPackage.ESPECIES__LOCATION:
				setLocation((String)newValue);
				return;
			case GamaPackage.ESPECIES__SIZE:
				setSize((String)newValue);
				return;
			case GamaPackage.ESPECIES__WIDTH:
				setWidth((String)newValue);
				return;
			case GamaPackage.ESPECIES__HEIGTH:
				setHeigth((String)newValue);
				return;
			case GamaPackage.ESPECIES__RADIUS:
				setRadius((String)newValue);
				return;
			case GamaPackage.ESPECIES__MICRO_SPECIES_LINKS:
				getMicroSpeciesLinks().clear();
				getMicroSpeciesLinks().addAll((Collection<? extends ESubSpeciesLink>)newValue);
				return;
			case GamaPackage.ESPECIES__MACRO_SPECIES_LINKS:
				getMacroSpeciesLinks().clear();
				getMacroSpeciesLinks().addAll((Collection<? extends ESubSpeciesLink>)newValue);
				return;
			case GamaPackage.ESPECIES__SKILLS:
				getSkills().clear();
				getSkills().addAll((Collection<? extends String>)newValue);
				return;
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology((ETopology)newValue);
				return;
			case GamaPackage.ESPECIES__INHERITS_FROM:
				setInheritsFrom((ESpecies)newValue);
				return;
			case GamaPackage.ESPECIES__TORUS_TYPE:
				setTorusType((String)newValue);
				return;
			case GamaPackage.ESPECIES__SHAPE_TYPE:
				setShapeType((String)newValue);
				return;
			case GamaPackage.ESPECIES__LOCATION_TYPE:
				setLocationType((String)newValue);
				return;
			case GamaPackage.ESPECIES__POINTS:
				setPoints((String)newValue);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_SHAPE:
				setExpressionShape((String)newValue);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_LOC:
				setExpressionLoc((String)newValue);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_TORUS:
				setExpressionTorus((String)newValue);
				return;
			case GamaPackage.ESPECIES__SHAPE_FUNCTION:
				setShapeFunction((String)newValue);
				return;
			case GamaPackage.ESPECIES__SHAPE_UPDATE:
				setShapeUpdate((String)newValue);
				return;
			case GamaPackage.ESPECIES__SHAPE_IS_FUNCTION:
				setShapeIsFunction((Boolean)newValue);
				return;
			case GamaPackage.ESPECIES__LOCATION_IS_FUNCTION:
				setLocationIsFunction((Boolean)newValue);
				return;
			case GamaPackage.ESPECIES__LOCATION_FUNCTION:
				setLocationFunction((String)newValue);
				return;
			case GamaPackage.ESPECIES__LOCATION_UPDATE:
				setLocationUpdate((String)newValue);
				return;
			case GamaPackage.ESPECIES__INIT:
				setInit((String)newValue);
				return;
			case GamaPackage.ESPECIES__INHERITING_LINKS:
				getInheritingLinks().clear();
				getInheritingLinks().addAll((Collection<? extends EInheritLink>)newValue);
				return;
			case GamaPackage.ESPECIES__SCHEDULES:
				setSchedules((String)newValue);
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
			case GamaPackage.ESPECIES__VARIABLES:
				getVariables().clear();
				return;
			case GamaPackage.ESPECIES__REFLEX_LIST:
				getReflexList().clear();
				return;
			case GamaPackage.ESPECIES__TORUS:
				setTorus(TORUS_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__EXPERIMENT_LINKS:
				getExperimentLinks().clear();
				return;
			case GamaPackage.ESPECIES__ASPECT_LINKS:
				getAspectLinks().clear();
				return;
			case GamaPackage.ESPECIES__ACTION_LINKS:
				getActionLinks().clear();
				return;
			case GamaPackage.ESPECIES__REFLEX_LINKS:
				getReflexLinks().clear();
				return;
			case GamaPackage.ESPECIES__SHAPE:
				setShape(SHAPE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__LOCATION:
				setLocation(LOCATION_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__SIZE:
				setSize(SIZE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__WIDTH:
				setWidth(WIDTH_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__HEIGTH:
				setHeigth(HEIGTH_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__RADIUS:
				setRadius(RADIUS_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__MICRO_SPECIES_LINKS:
				getMicroSpeciesLinks().clear();
				return;
			case GamaPackage.ESPECIES__MACRO_SPECIES_LINKS:
				getMacroSpeciesLinks().clear();
				return;
			case GamaPackage.ESPECIES__SKILLS:
				getSkills().clear();
				return;
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology((ETopology)null);
				return;
			case GamaPackage.ESPECIES__INHERITS_FROM:
				setInheritsFrom((ESpecies)null);
				return;
			case GamaPackage.ESPECIES__TORUS_TYPE:
				setTorusType(TORUS_TYPE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__SHAPE_TYPE:
				setShapeType(SHAPE_TYPE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__LOCATION_TYPE:
				setLocationType(LOCATION_TYPE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__POINTS:
				setPoints(POINTS_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_SHAPE:
				setExpressionShape(EXPRESSION_SHAPE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_LOC:
				setExpressionLoc(EXPRESSION_LOC_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__EXPRESSION_TORUS:
				setExpressionTorus(EXPRESSION_TORUS_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__SHAPE_FUNCTION:
				setShapeFunction(SHAPE_FUNCTION_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__SHAPE_UPDATE:
				setShapeUpdate(SHAPE_UPDATE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__SHAPE_IS_FUNCTION:
				setShapeIsFunction(SHAPE_IS_FUNCTION_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__LOCATION_IS_FUNCTION:
				setLocationIsFunction(LOCATION_IS_FUNCTION_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__LOCATION_FUNCTION:
				setLocationFunction(LOCATION_FUNCTION_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__LOCATION_UPDATE:
				setLocationUpdate(LOCATION_UPDATE_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__INIT:
				setInit(INIT_EDEFAULT);
				return;
			case GamaPackage.ESPECIES__INHERITING_LINKS:
				getInheritingLinks().clear();
				return;
			case GamaPackage.ESPECIES__SCHEDULES:
				setSchedules(SCHEDULES_EDEFAULT);
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
			case GamaPackage.ESPECIES__VARIABLES:
				return variables != null && !variables.isEmpty();
			case GamaPackage.ESPECIES__REFLEX_LIST:
				return reflexList != null && !reflexList.isEmpty();
			case GamaPackage.ESPECIES__TORUS:
				return TORUS_EDEFAULT == null ? torus != null : !TORUS_EDEFAULT.equals(torus);
			case GamaPackage.ESPECIES__EXPERIMENT_LINKS:
				return experimentLinks != null && !experimentLinks.isEmpty();
			case GamaPackage.ESPECIES__ASPECT_LINKS:
				return aspectLinks != null && !aspectLinks.isEmpty();
			case GamaPackage.ESPECIES__ACTION_LINKS:
				return actionLinks != null && !actionLinks.isEmpty();
			case GamaPackage.ESPECIES__REFLEX_LINKS:
				return reflexLinks != null && !reflexLinks.isEmpty();
			case GamaPackage.ESPECIES__SHAPE:
				return SHAPE_EDEFAULT == null ? shape != null : !SHAPE_EDEFAULT.equals(shape);
			case GamaPackage.ESPECIES__LOCATION:
				return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
			case GamaPackage.ESPECIES__SIZE:
				return SIZE_EDEFAULT == null ? size != null : !SIZE_EDEFAULT.equals(size);
			case GamaPackage.ESPECIES__WIDTH:
				return WIDTH_EDEFAULT == null ? width != null : !WIDTH_EDEFAULT.equals(width);
			case GamaPackage.ESPECIES__HEIGTH:
				return HEIGTH_EDEFAULT == null ? heigth != null : !HEIGTH_EDEFAULT.equals(heigth);
			case GamaPackage.ESPECIES__RADIUS:
				return RADIUS_EDEFAULT == null ? radius != null : !RADIUS_EDEFAULT.equals(radius);
			case GamaPackage.ESPECIES__MICRO_SPECIES_LINKS:
				return microSpeciesLinks != null && !microSpeciesLinks.isEmpty();
			case GamaPackage.ESPECIES__MACRO_SPECIES_LINKS:
				return macroSpeciesLinks != null && !macroSpeciesLinks.isEmpty();
			case GamaPackage.ESPECIES__SKILLS:
				return skills != null && !skills.isEmpty();
			case GamaPackage.ESPECIES__TOPOLOGY:
				return topology != null;
			case GamaPackage.ESPECIES__INHERITS_FROM:
				return inheritsFrom != null;
			case GamaPackage.ESPECIES__TORUS_TYPE:
				return TORUS_TYPE_EDEFAULT == null ? torusType != null : !TORUS_TYPE_EDEFAULT.equals(torusType);
			case GamaPackage.ESPECIES__SHAPE_TYPE:
				return SHAPE_TYPE_EDEFAULT == null ? shapeType != null : !SHAPE_TYPE_EDEFAULT.equals(shapeType);
			case GamaPackage.ESPECIES__LOCATION_TYPE:
				return LOCATION_TYPE_EDEFAULT == null ? locationType != null : !LOCATION_TYPE_EDEFAULT.equals(locationType);
			case GamaPackage.ESPECIES__POINTS:
				return POINTS_EDEFAULT == null ? points != null : !POINTS_EDEFAULT.equals(points);
			case GamaPackage.ESPECIES__EXPRESSION_SHAPE:
				return EXPRESSION_SHAPE_EDEFAULT == null ? expressionShape != null : !EXPRESSION_SHAPE_EDEFAULT.equals(expressionShape);
			case GamaPackage.ESPECIES__EXPRESSION_LOC:
				return EXPRESSION_LOC_EDEFAULT == null ? expressionLoc != null : !EXPRESSION_LOC_EDEFAULT.equals(expressionLoc);
			case GamaPackage.ESPECIES__EXPRESSION_TORUS:
				return EXPRESSION_TORUS_EDEFAULT == null ? expressionTorus != null : !EXPRESSION_TORUS_EDEFAULT.equals(expressionTorus);
			case GamaPackage.ESPECIES__SHAPE_FUNCTION:
				return SHAPE_FUNCTION_EDEFAULT == null ? shapeFunction != null : !SHAPE_FUNCTION_EDEFAULT.equals(shapeFunction);
			case GamaPackage.ESPECIES__SHAPE_UPDATE:
				return SHAPE_UPDATE_EDEFAULT == null ? shapeUpdate != null : !SHAPE_UPDATE_EDEFAULT.equals(shapeUpdate);
			case GamaPackage.ESPECIES__SHAPE_IS_FUNCTION:
				return SHAPE_IS_FUNCTION_EDEFAULT == null ? shapeIsFunction != null : !SHAPE_IS_FUNCTION_EDEFAULT.equals(shapeIsFunction);
			case GamaPackage.ESPECIES__LOCATION_IS_FUNCTION:
				return LOCATION_IS_FUNCTION_EDEFAULT == null ? locationIsFunction != null : !LOCATION_IS_FUNCTION_EDEFAULT.equals(locationIsFunction);
			case GamaPackage.ESPECIES__LOCATION_FUNCTION:
				return LOCATION_FUNCTION_EDEFAULT == null ? locationFunction != null : !LOCATION_FUNCTION_EDEFAULT.equals(locationFunction);
			case GamaPackage.ESPECIES__LOCATION_UPDATE:
				return LOCATION_UPDATE_EDEFAULT == null ? locationUpdate != null : !LOCATION_UPDATE_EDEFAULT.equals(locationUpdate);
			case GamaPackage.ESPECIES__INIT:
				return INIT_EDEFAULT == null ? init != null : !INIT_EDEFAULT.equals(init);
			case GamaPackage.ESPECIES__INHERITING_LINKS:
				return inheritingLinks != null && !inheritingLinks.isEmpty();
			case GamaPackage.ESPECIES__SCHEDULES:
				return SCHEDULES_EDEFAULT == null ? schedules != null : !SCHEDULES_EDEFAULT.equals(schedules);
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
		result.append(" (reflexList: ");
		result.append(reflexList);
		result.append(", torus: ");
		result.append(torus);
		result.append(", shape: ");
		result.append(shape);
		result.append(", location: ");
		result.append(location);
		result.append(", size: ");
		result.append(size);
		result.append(", width: ");
		result.append(width);
		result.append(", heigth: ");
		result.append(heigth);
		result.append(", radius: ");
		result.append(radius);
		result.append(", skills: ");
		result.append(skills);
		result.append(", torusType: ");
		result.append(torusType);
		result.append(", shapeType: ");
		result.append(shapeType);
		result.append(", locationType: ");
		result.append(locationType);
		result.append(", points: ");
		result.append(points);
		result.append(", expressionShape: ");
		result.append(expressionShape);
		result.append(", expressionLoc: ");
		result.append(expressionLoc);
		result.append(", expressionTorus: ");
		result.append(expressionTorus);
		result.append(", shapeFunction: ");
		result.append(shapeFunction);
		result.append(", shapeUpdate: ");
		result.append(shapeUpdate);
		result.append(", shapeIsFunction: ");
		result.append(shapeIsFunction);
		result.append(", locationIsFunction: ");
		result.append(locationIsFunction);
		result.append(", locationFunction: ");
		result.append(locationFunction);
		result.append(", locationUpdate: ");
		result.append(locationUpdate);
		result.append(", init: ");
		result.append(init);
		result.append(", schedules: ");
		result.append(schedules);
		result.append(')');
		return result.toString();
	}

} //ESpeciesImpl
