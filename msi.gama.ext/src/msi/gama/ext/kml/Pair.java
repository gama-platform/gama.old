/*******************************************************************************************************
 *
 * Pair.java, in msi.gama.ext, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/

package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

import msi.gama.ext.kml.annotations.Obvious;

/**
 * <pair> (required)
 * <p>
 * Defines a key/value pair that maps a mode (normal or highlight) to the predefined <styleUrl>. <Pair> contains two
 * elements (both are required): <key>, which identifies the key <styleUrl> or <Style>, which references the style. In
 * <styleUrl>, for referenced style elements that are local to the KML document, a simple # referencing is used. For
 * styles that are contained in external files, use a full URL along with # referencing. For example: <Pair>
 * <key>normal</key> <styleUrl>http://myserver.com/populationProject.xml#example_style_off</styleUrl> </Pair>
 * </p>
 *
 *
 *
 */
@XmlAccessorType (XmlAccessType.FIELD)
@XmlType (
		name = "PairType",
		propOrder = { "key", "styleUrl", "styleSelector", "pairSimpleExtension", "pairObjectExtension" })
@XmlRootElement (
		name = "Pair",
		namespace = "http://www.opengis.net/kml/2.2")
public class Pair extends AbstractObject implements Cloneable {

	/**
	 * StyleState
	 * <p>
	 * normal, highlight
	 * </p>
	 * 
	 * See Also: See <StyleMap>
	 * 
	 * 
	 * 
	 */
	@XmlElement (
			defaultValue = "normal") protected StyleState key;
	/**
	 * <styleurl>
	 * <p>
	 * URL of a <Style> or <StyleMap> defined in a Document. If the style is in the same file, use a # reference. If the
	 * style is defined in an external file, use a full URL along with # referencing. Examples are
	 * <styleUrl>#myIconStyleID</styleUrl> <styleUrl>http://someserver.com/somestylefile.xml#restaurant</styleUrl>
	 * <styleUrl>eateries.kml#my-lunch-spot</styleUrl>
	 * </p>
	 * 
	 * 
	 * 
	 */
	@XmlSchemaType (
			name = "anyURI") protected String styleUrl;
	/**
	 * <StyleSelector>
	 * <p>
	 * One or more Styles and StyleMaps can be defined to customize the appearance of any element derived from Feature
	 * or of the Geometry in a Placemark. (See <BalloonStyle>, <ListStyle>, <StyleSelector>, and the styles derived from
	 * <ColorStyle>.) A style defined within a Feature is called an "inline style" and applies only to the Feature that
	 * contains it. A style defined as the child of a <Document> is called a "shared style." A shared style must have an
	 * id defined for it. This id is referenced by one or more Features within the <Document>. In cases where a style
	 * element is defined both in a shared style and in an inline style for a Feature—that is, a Folder, GroundOverlay,
	 * NetworkLink, Placemark, or ScreenOverlay—the value for the Feature's inline style takes precedence over the value
	 * for the shared style.
	 * </p>
	 * 
	 * Syntax:
	 *
	 * <pre>
	 * &lt;!-- abstract element; do not create --&gt;
	 * <strong>&lt;!-- <em>StyleSelector</em> id="ID" --&gt;               </strong>  &lt;!-- Style,StyleMap --&gt;<strong>
	 * &lt;!-- /<em>StyleSelector</em> --&gt;</strong>
	 * </pre>
	 * 
	 * Extends:
	 *
	 * @see: <Object>
	 * 
	 *       Extended By:
	 * @see: <Style>
	 * @see: <StyleMap>
	 * 
	 * 
	 * 
	 */
	@XmlElementRef (
			name = "AbstractStyleSelectorGroup",
			namespace = "http://www.opengis.net/kml/2.2",
			required = false) protected StyleSelector styleSelector;
	
	/** The pair simple extension. */
	@XmlElement (
			name = "PairSimpleExtensionGroup")
	@XmlSchemaType (
			name = "anySimpleType") protected List<Object> pairSimpleExtension;
	/**
	 * <Object>
	 * <p>
	 * This is an abstract base class and cannot be used directly in a KML file. It provides the id attribute, which
	 * allows unique identification of a KML element, and the targetId attribute, which is used to reference objects
	 * that have already been loaded into Google Earth. The id attribute must be assigned if the <Update> mechanism is
	 * to be used.
	 * </p>
	 * 
	 * Syntax:
	 *
	 * <pre>
	 * &lt;!-- abstract element; do not create --&gt;<strong>
	 * &lt;!-- <em>Object</em> id="ID" targetId="NCName" --&gt;
	 * &lt;!-- /<em>Object</em>&gt; --&gt;</strong>
	 * </pre>
	 * 
	 * 
	 * 
	 */
	@XmlElement (
			name = "PairObjectExtensionGroup") protected List<AbstractObject> pairObjectExtension;

	/**
	 * Instantiates a new pair.
	 */
	public Pair() {
	}

	/**
	 * @see key
	 * 
	 * @return possible object is {@link StyleState}
	 * 
	 */
	public StyleState getKey() { return key; }

	/**
	 * @see key
	 * 
	 * @param value
	 *            allowed object is {@link StyleState}
	 * 
	 */
	public void setKey(final StyleState value) { this.key = value; }

	/**
	 * @see styleUrl
	 * 
	 * @return possible object is {@link String}
	 * 
	 */
	public String getStyleUrl() { return styleUrl; }

	/**
	 * @see styleUrl
	 * 
	 * @param value
	 *            allowed object is {@link String}
	 * 
	 */
	public void setStyleUrl(final String value) { this.styleUrl = value; }

	/**
	 * @see styleSelector
	 * 
	 * @return possible object is
	 *         {@code <}{@link Style}{@code>} {@code <}{@link StyleSelector}{@code>} {@code <}{@link StyleMap}{@code>}
	 * 
	 */
	public StyleSelector getStyleSelector() { return styleSelector; }

	/**
	 * @see styleSelector
	 * 
	 * @param value
	 *            allowed object is
	 *            {@code <}{@link Style}{@code>} {@code <}{@link StyleSelector}{@code>} {@code <}{@link StyleMap}{@code>}
	 * 
	 */
	public void setStyleSelector(final StyleSelector value) { this.styleSelector = value; }

	/**
	 * @see pairSimpleExtension
	 * 
	 */
	public List<Object> getPairSimpleExtension() {
		if (pairSimpleExtension == null) { pairSimpleExtension = new ArrayList<>(); }
		return this.pairSimpleExtension;
	}

	/**
	 * @see pairObjectExtension
	 * 
	 */
	public List<AbstractObject> getPairObjectExtension() {
		if (pairObjectExtension == null) { pairObjectExtension = new ArrayList<>(); }
		return this.pairObjectExtension;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (key == null ? 0 : key.hashCode());
		result = prime * result + (styleUrl == null ? 0 : styleUrl.hashCode());
		result = prime * result + (styleSelector == null ? 0 : styleSelector.hashCode());
		result = prime * result + (pairSimpleExtension == null ? 0 : pairSimpleExtension.hashCode());
		return prime * result + (pairObjectExtension == null ? 0 : pairObjectExtension.hashCode());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if ((obj == null) || !super.equals(obj) || !(obj instanceof Pair)) return false;
		Pair other = (Pair) obj;
		if (key == null) {
			if (other.key != null) return false;
		} else if (!key.equals(other.key)) return false;
		if (styleUrl == null) {
			if (other.styleUrl != null) return false;
		} else if (!styleUrl.equals(other.styleUrl)) return false;
		if (styleSelector == null) {
			if (other.styleSelector != null) return false;
		} else if (!styleSelector.equals(other.styleSelector)) return false;
		if (pairSimpleExtension == null) {
			if (other.pairSimpleExtension != null) return false;
		} else if (!pairSimpleExtension.equals(other.pairSimpleExtension)) return false;
		if (pairObjectExtension == null) {
			if (other.pairObjectExtension != null) return false;
		} else if (!pairObjectExtension.equals(other.pairObjectExtension)) return false;
		return true;
	}

	/**
	 * Creates a new instance of {@link Style} and set it to styleSelector.
	 * 
	 * This method is a short version for: <code>
	 * Style style = new Style();
	 * this.setStyleSelector(style); </code>
	 * 
	 * 
	 */
	public Style createAndSetStyle() {
		Style newValue = new Style();
		this.setStyleSelector(newValue);
		return newValue;
	}

	/**
	 * Creates a new instance of {@link StyleMap} and set it to styleSelector.
	 * 
	 * This method is a short version for: <code>
	 * StyleMap styleMap = new StyleMap();
	 * this.setStyleSelector(styleMap); </code>
	 * 
	 * 
	 */
	public StyleMap createAndSetStyleMap() {
		StyleMap newValue = new StyleMap();
		this.setStyleSelector(newValue);
		return newValue;
	}

	/**
	 * @see pairSimpleExtension
	 * 
	 * @param pairSimpleExtension
	 */
	public void setPairSimpleExtension(final List<Object> pairSimpleExtension) {
		this.pairSimpleExtension = pairSimpleExtension;
	}

	/**
	 * add a value to the pairSimpleExtension property collection
	 * 
	 * @param pairSimpleExtension
	 *            Objects of the following type are allowed in the list: {@link Object}
	 * @return <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
	 */
	public Pair addToPairSimpleExtension(final Object pairSimpleExtension) {
		this.getPairSimpleExtension().add(pairSimpleExtension);
		return this;
	}

	/**
	 * @see pairObjectExtension
	 * 
	 * @param pairObjectExtension
	 */
	public void setPairObjectExtension(final List<AbstractObject> pairObjectExtension) {
		this.pairObjectExtension = pairObjectExtension;
	}

	/**
	 * add a value to the pairObjectExtension property collection
	 * 
	 * @param pairObjectExtension
	 *            Objects of the following type are allowed in the list: {@link AbstractObject}
	 * @return <tt>true</tt> (as general contract of <tt>Collection.add</tt>).
	 */
	public Pair addToPairObjectExtension(final AbstractObject pairObjectExtension) {
		this.getPairObjectExtension().add(pairObjectExtension);
		return this;
	}

	/**
	 * @see objectSimpleExtension
	 * 
	 */
	@Obvious
	@Override
	public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
		super.setObjectSimpleExtension(objectSimpleExtension);
	}

	@Obvious
	@Override
	public Pair addToObjectSimpleExtension(final Object objectSimpleExtension) {
		super.getObjectSimpleExtension().add(objectSimpleExtension);
		return this;
	}

	/**
	 * fluent setter
	 *
	 * @see #setKey(StyleState)
	 * 
	 * @param key
	 *            required parameter
	 */
	public Pair withKey(final StyleState key) {
		this.setKey(key);
		return this;
	}

	/**
	 * fluent setter
	 *
	 * @see #setStyleUrl(String)
	 * 
	 * @param styleUrl
	 *            required parameter
	 */
	public Pair withStyleUrl(final String styleUrl) {
		this.setStyleUrl(styleUrl);
		return this;
	}

	/**
	 * fluent setter
	 *
	 * @see #setStyleSelector(StyleSelector)
	 * 
	 * @param styleSelector
	 *            required parameter
	 */
	public Pair withStyleSelector(final StyleSelector styleSelector) {
		this.setStyleSelector(styleSelector);
		return this;
	}

	/**
	 * fluent setter
	 *
	 * @see #setPairSimpleExtension(List<Object>)
	 * 
	 * @param pairSimpleExtension
	 *            required parameter
	 */
	public Pair withPairSimpleExtension(final List<Object> pairSimpleExtension) {
		this.setPairSimpleExtension(pairSimpleExtension);
		return this;
	}

	/**
	 * fluent setter
	 *
	 * @see #setPairObjectExtension(List<AbstractObject>)
	 * 
	 * @param pairObjectExtension
	 *            required parameter
	 */
	public Pair withPairObjectExtension(final List<AbstractObject> pairObjectExtension) {
		this.setPairObjectExtension(pairObjectExtension);
		return this;
	}

	@Obvious
	@Override
	public Pair withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
		super.withObjectSimpleExtension(objectSimpleExtension);
		return this;
	}

	@Obvious
	@Override
	public Pair withId(final String id) {
		super.withId(id);
		return this;
	}

	@Obvious
	@Override
	public Pair withTargetId(final String targetId) {
		super.withTargetId(targetId);
		return this;
	}

	@Override
	public Pair clone() {
		Pair copy;
		copy = (Pair) super.clone();
		copy.styleSelector = styleSelector == null ? null : (StyleSelector) styleSelector.clone();
		copy.pairSimpleExtension = new ArrayList<>(getPairSimpleExtension().size());
		copy.pairSimpleExtension.addAll(pairSimpleExtension);
		copy.pairObjectExtension = new ArrayList<>(getPairObjectExtension().size());
		for (AbstractObject iter : pairObjectExtension) { copy.pairObjectExtension.add(iter.clone()); }
		return copy;
	}

}
