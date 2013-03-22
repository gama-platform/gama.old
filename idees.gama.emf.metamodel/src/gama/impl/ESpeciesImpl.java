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
import gama.EReflexLink;
import gama.ESpecies;
import gama.ESubSpeciesLink;
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
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>ESpecies</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gama.impl.ESpeciesImpl#getVariables <em>Variables</em>}</li>
 *   <li>{@link gama.impl.ESpeciesImpl#getTopology <em>Topology</em>}</li>
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
	 * The default value of the '{@link #getTopology() <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopology()
	 * @generated
	 * @ordered
	 */
	protected static final String TOPOLOGY_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getTopology() <em>Topology</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getTopology()
	 * @generated
	 * @ordered
	 */
	protected String topology = TOPOLOGY_EDEFAULT;
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
	public String getTopology() {
		return topology;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setTopology(String newTopology) {
		String oldTopology = topology;
		topology = newTopology;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, GamaPackage.ESPECIES__TOPOLOGY, oldTopology, topology));
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
			case GamaPackage.ESPECIES__TOPOLOGY:
				return getTopology();
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
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology((String)newValue);
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
			case GamaPackage.ESPECIES__TOPOLOGY:
				setTopology(TOPOLOGY_EDEFAULT);
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
			case GamaPackage.ESPECIES__TOPOLOGY:
				return TOPOLOGY_EDEFAULT == null ? topology != null : !TOPOLOGY_EDEFAULT.equals(topology);
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
		result.append(" (topology: ");
		result.append(topology);
		result.append(", reflexList: ");
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
		result.append(')');
		return result.toString();
	}

} //ESpeciesImpl
