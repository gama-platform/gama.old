/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package gama;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>ELayer</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gama.ELayer#getGamlCode <em>Gaml Code</em>}</li>
 *   <li>{@link gama.ELayer#getDisplay <em>Display</em>}</li>
 *   <li>{@link gama.ELayer#getType <em>Type</em>}</li>
 *   <li>{@link gama.ELayer#getFile <em>File</em>}</li>
 *   <li>{@link gama.ELayer#getText <em>Text</em>}</li>
 *   <li>{@link gama.ELayer#getSize <em>Size</em>}</li>
 *   <li>{@link gama.ELayer#getSpecies <em>Species</em>}</li>
 *   <li>{@link gama.ELayer#getTransparency <em>Transparency</em>}</li>
 *   <li>{@link gama.ELayer#getAgents <em>Agents</em>}</li>
 *   <li>{@link gama.ELayer#getPosition_x <em>Position x</em>}</li>
 *   <li>{@link gama.ELayer#getPosition_y <em>Position y</em>}</li>
 *   <li>{@link gama.ELayer#getSize_x <em>Size x</em>}</li>
 *   <li>{@link gama.ELayer#getSize_y <em>Size y</em>}</li>
 *   <li>{@link gama.ELayer#getAspect <em>Aspect</em>}</li>
 *   <li>{@link gama.ELayer#getColor <em>Color</em>}</li>
 *   <li>{@link gama.ELayer#getIsColorCst <em>Is Color Cst</em>}</li>
 *   <li>{@link gama.ELayer#getColorRBG <em>Color RBG</em>}</li>
 *   <li>{@link gama.ELayer#getGrid <em>Grid</em>}</li>
 *   <li>{@link gama.ELayer#getRefresh <em>Refresh</em>}</li>
 *   <li>{@link gama.ELayer#getChartlayers <em>Chartlayers</em>}</li>
 *   <li>{@link gama.ELayer#getChart_type <em>Chart type</em>}</li>
 *   <li>{@link gama.ELayer#isShowLines <em>Show Lines</em>}</li>
 * </ul>
 * </p>
 *
 * @see gama.GamaPackage#getELayer()
 * @model
 * @generated
 */
public interface ELayer extends EGamaObject {
	/**
	 * Returns the value of the '<em><b>Gaml Code</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Gaml Code</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Gaml Code</em>' attribute.
	 * @see #setGamlCode(String)
	 * @see gama.GamaPackage#getELayer_GamlCode()
	 * @model derived="true"
	 * @generated
	 */
	String getGamlCode();

	/**
	 * Sets the value of the '{@link gama.ELayer#getGamlCode <em>Gaml Code</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Gaml Code</em>' attribute.
	 * @see #getGamlCode()
	 * @generated
	 */
	void setGamlCode(String value);

	/**
	 * Returns the value of the '<em><b>Display</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Display</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Display</em>' reference.
	 * @see #setDisplay(EDisplay)
	 * @see gama.GamaPackage#getELayer_Display()
	 * @model
	 * @generated
	 */
	EDisplay getDisplay();

	/**
	 * Sets the value of the '{@link gama.ELayer#getDisplay <em>Display</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Display</em>' reference.
	 * @see #getDisplay()
	 * @generated
	 */
	void setDisplay(EDisplay value);

	/**
	 * Returns the value of the '<em><b>Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Type</em>' attribute.
	 * @see #setType(String)
	 * @see gama.GamaPackage#getELayer_Type()
	 * @model
	 * @generated
	 */
	String getType();

	/**
	 * Sets the value of the '{@link gama.ELayer#getType <em>Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Type</em>' attribute.
	 * @see #getType()
	 * @generated
	 */
	void setType(String value);

	/**
	 * Returns the value of the '<em><b>File</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>File</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>File</em>' attribute.
	 * @see #setFile(String)
	 * @see gama.GamaPackage#getELayer_File()
	 * @model
	 * @generated
	 */
	String getFile();

	/**
	 * Sets the value of the '{@link gama.ELayer#getFile <em>File</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>File</em>' attribute.
	 * @see #getFile()
	 * @generated
	 */
	void setFile(String value);

	/**
	 * Returns the value of the '<em><b>Text</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Text</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Text</em>' attribute.
	 * @see #setText(String)
	 * @see gama.GamaPackage#getELayer_Text()
	 * @model
	 * @generated
	 */
	String getText();

	/**
	 * Sets the value of the '{@link gama.ELayer#getText <em>Text</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Text</em>' attribute.
	 * @see #getText()
	 * @generated
	 */
	void setText(String value);

	/**
	 * Returns the value of the '<em><b>Size</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size</em>' attribute.
	 * @see #setSize(String)
	 * @see gama.GamaPackage#getELayer_Size()
	 * @model
	 * @generated
	 */
	String getSize();

	/**
	 * Sets the value of the '{@link gama.ELayer#getSize <em>Size</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size</em>' attribute.
	 * @see #getSize()
	 * @generated
	 */
	void setSize(String value);

	/**
	 * Returns the value of the '<em><b>Species</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Species</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Species</em>' attribute.
	 * @see #setSpecies(String)
	 * @see gama.GamaPackage#getELayer_Species()
	 * @model
	 * @generated
	 */
	String getSpecies();

	/**
	 * Sets the value of the '{@link gama.ELayer#getSpecies <em>Species</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Species</em>' attribute.
	 * @see #getSpecies()
	 * @generated
	 */
	void setSpecies(String value);

	/**
	 * Returns the value of the '<em><b>Transparency</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Transparency</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Transparency</em>' attribute.
	 * @see #setTransparency(String)
	 * @see gama.GamaPackage#getELayer_Transparency()
	 * @model
	 * @generated
	 */
	String getTransparency();

	/**
	 * Sets the value of the '{@link gama.ELayer#getTransparency <em>Transparency</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Transparency</em>' attribute.
	 * @see #getTransparency()
	 * @generated
	 */
	void setTransparency(String value);

	/**
	 * Returns the value of the '<em><b>Agents</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Agents</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Agents</em>' attribute.
	 * @see #setAgents(String)
	 * @see gama.GamaPackage#getELayer_Agents()
	 * @model
	 * @generated
	 */
	String getAgents();

	/**
	 * Sets the value of the '{@link gama.ELayer#getAgents <em>Agents</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Agents</em>' attribute.
	 * @see #getAgents()
	 * @generated
	 */
	void setAgents(String value);

	/**
	 * Returns the value of the '<em><b>Position x</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Position x</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Position x</em>' attribute.
	 * @see #setPosition_x(String)
	 * @see gama.GamaPackage#getELayer_Position_x()
	 * @model
	 * @generated
	 */
	String getPosition_x();

	/**
	 * Sets the value of the '{@link gama.ELayer#getPosition_x <em>Position x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position x</em>' attribute.
	 * @see #getPosition_x()
	 * @generated
	 */
	void setPosition_x(String value);

	/**
	 * Returns the value of the '<em><b>Position y</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Position y</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Position y</em>' attribute.
	 * @see #setPosition_y(String)
	 * @see gama.GamaPackage#getELayer_Position_y()
	 * @model
	 * @generated
	 */
	String getPosition_y();

	/**
	 * Sets the value of the '{@link gama.ELayer#getPosition_y <em>Position y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Position y</em>' attribute.
	 * @see #getPosition_y()
	 * @generated
	 */
	void setPosition_y(String value);

	/**
	 * Returns the value of the '<em><b>Size x</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size x</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size x</em>' attribute.
	 * @see #setSize_x(String)
	 * @see gama.GamaPackage#getELayer_Size_x()
	 * @model
	 * @generated
	 */
	String getSize_x();

	/**
	 * Sets the value of the '{@link gama.ELayer#getSize_x <em>Size x</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size x</em>' attribute.
	 * @see #getSize_x()
	 * @generated
	 */
	void setSize_x(String value);

	/**
	 * Returns the value of the '<em><b>Size y</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Size y</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Size y</em>' attribute.
	 * @see #setSize_y(String)
	 * @see gama.GamaPackage#getELayer_Size_y()
	 * @model
	 * @generated
	 */
	String getSize_y();

	/**
	 * Sets the value of the '{@link gama.ELayer#getSize_y <em>Size y</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Size y</em>' attribute.
	 * @see #getSize_y()
	 * @generated
	 */
	void setSize_y(String value);

	/**
	 * Returns the value of the '<em><b>Aspect</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Aspect</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Aspect</em>' attribute.
	 * @see #setAspect(String)
	 * @see gama.GamaPackage#getELayer_Aspect()
	 * @model
	 * @generated
	 */
	String getAspect();

	/**
	 * Sets the value of the '{@link gama.ELayer#getAspect <em>Aspect</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Aspect</em>' attribute.
	 * @see #getAspect()
	 * @generated
	 */
	void setAspect(String value);

	/**
	 * Returns the value of the '<em><b>Color</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color</em>' attribute.
	 * @see #setColor(String)
	 * @see gama.GamaPackage#getELayer_Color()
	 * @model
	 * @generated
	 */
	String getColor();

	/**
	 * Sets the value of the '{@link gama.ELayer#getColor <em>Color</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Color</em>' attribute.
	 * @see #getColor()
	 * @generated
	 */
	void setColor(String value);

	/**
	 * Returns the value of the '<em><b>Is Color Cst</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Is Color Cst</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Is Color Cst</em>' attribute.
	 * @see #setIsColorCst(Boolean)
	 * @see gama.GamaPackage#getELayer_IsColorCst()
	 * @model derived="true"
	 * @generated
	 */
	Boolean getIsColorCst();

	/**
	 * Sets the value of the '{@link gama.ELayer#getIsColorCst <em>Is Color Cst</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Is Color Cst</em>' attribute.
	 * @see #getIsColorCst()
	 * @generated
	 */
	void setIsColorCst(Boolean value);

	/**
	 * Returns the value of the '<em><b>Color RBG</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.Integer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Color RBG</em>' attribute list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Color RBG</em>' attribute list.
	 * @see gama.GamaPackage#getELayer_ColorRBG()
	 * @model unique="false" upper="3"
	 * @generated
	 */
	EList<Integer> getColorRBG();

	/**
	 * Returns the value of the '<em><b>Grid</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Grid</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Grid</em>' attribute.
	 * @see #setGrid(String)
	 * @see gama.GamaPackage#getELayer_Grid()
	 * @model
	 * @generated
	 */
	String getGrid();

	/**
	 * Sets the value of the '{@link gama.ELayer#getGrid <em>Grid</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Grid</em>' attribute.
	 * @see #getGrid()
	 * @generated
	 */
	void setGrid(String value);

	/**
	 * Returns the value of the '<em><b>Refresh</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Refresh</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Refresh</em>' attribute.
	 * @see #setRefresh(String)
	 * @see gama.GamaPackage#getELayer_Refresh()
	 * @model
	 * @generated
	 */
	String getRefresh();

	/**
	 * Sets the value of the '{@link gama.ELayer#getRefresh <em>Refresh</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Refresh</em>' attribute.
	 * @see #getRefresh()
	 * @generated
	 */
	void setRefresh(String value);

	/**
	 * Returns the value of the '<em><b>Chartlayers</b></em>' reference list.
	 * The list contents are of type {@link gama.EChartLayer}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chartlayers</em>' reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chartlayers</em>' reference list.
	 * @see gama.GamaPackage#getELayer_Chartlayers()
	 * @model
	 * @generated
	 */
	EList<EChartLayer> getChartlayers();

	/**
	 * Returns the value of the '<em><b>Chart type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Chart type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Chart type</em>' attribute.
	 * @see #setChart_type(String)
	 * @see gama.GamaPackage#getELayer_Chart_type()
	 * @model
	 * @generated
	 */
	String getChart_type();

	/**
	 * Sets the value of the '{@link gama.ELayer#getChart_type <em>Chart type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Chart type</em>' attribute.
	 * @see #getChart_type()
	 * @generated
	 */
	void setChart_type(String value);

	/**
	 * Returns the value of the '<em><b>Show Lines</b></em>' attribute.
	 * The default value is <code>"false"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Show Lines</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Show Lines</em>' attribute.
	 * @see #setShowLines(boolean)
	 * @see gama.GamaPackage#getELayer_ShowLines()
	 * @model default="false"
	 * @generated
	 */
	boolean isShowLines();

	/**
	 * Sets the value of the '{@link gama.ELayer#isShowLines <em>Show Lines</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Show Lines</em>' attribute.
	 * @see #isShowLines()
	 * @generated
	 */
	void setShowLines(boolean value);

} // ELayer
