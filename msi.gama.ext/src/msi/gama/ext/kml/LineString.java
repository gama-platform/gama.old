
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
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <LineString>
 * <p>
 * Defines a connected set of line segments. Use <LineStyle> to specify the color, 
 * color mode, and width of the line. When a LineString is extruded, the line is extended 
 * to the ground, forming a polygon that looks somewhat like a wall or fence. For extruded 
 * LineStrings, the line itself uses the current LineStyle, and the extrusion uses 
 * the current PolyStyle. See the KML Tutorial for examples of LineStrings (or paths). 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;LineString id="ID"&gt;</strong>
 *   &lt;!-- specific to LineString --&gt;
 *   &lt;extrude&gt;0&lt;/extrude&gt;                   &lt;!-- boolean --&gt;
 *   &lt;tessellate&gt;0&lt;/tessellate&gt;             &lt;!-- boolean --&gt;
 *   &lt;altitudeMode&gt;clampToGround&lt;/altitudeMode&gt; 
 *       &lt;!-- kml:altitudeModeEnum: clampToGround, relativeToGround, or absolute --&gt;
 *         &lt;!-- or, substitute gx:altitudeMode: clampToSeaFloor, relativeToSeaFloor --&gt;
 * 			&lt;!-- or, substitute gx:altitudeMode: clampToSeaFloor, relativeToSeaFloor --&gt;
 *   &lt;coordinates&gt;<em>...</em>&lt;/coordinates&gt;         &lt;!<em>-- </em>lon,lat[,alt] --&gt;
 * <strong>&lt;/LineString&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <Geometry>
 * 
 * Contained By: 
 * @see: <MultiGeometry>
 * @see: <Placemark>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LineStringType", propOrder = {
    "extrude",
    "tessellate",
    "altitudeMode",
    "coordinates",
    "lineStringSimpleExtension",
    "lineStringObjectExtension"
})
@XmlRootElement(name = "LineString", namespace = "http://www.opengis.net/kml/2.2")
public class LineString
    extends Geometry
    implements Cloneable
{

    /**
     * <extrude>
     * <p>
     * Boolean value. Specifies whether to connect the LineString to the ground. To extrude 
     * a LineString, the altitude mode must be either relativeToGround, relativeToSeaFloor, 
     * or absolute. The vertices in the LineString are extruded toward the center of the 
     * Earth's sphere. 
     * </p>
     * <p>
     * Boolean value. Specifies whether to connect the LinearRing to the ground. To extrude 
     * this geometry, the altitude mode must be either relativeToGround, relativeToSeaFloor, 
     * or absolute. Only the vertices of the LinearRing are extruded, not the center of 
     * the geometry. The vertices are extruded toward the center of the Earth's sphere. 
     * </p>
     * <p>
     * Boolean value. Specifies whether to connect the Polygon to the ground. To extrude 
     * a Polygon, the altitude mode must be either relativeToGround, relativeToSeaFloor, 
     * or absolute. Only the vertices are extruded, not the geometry itself (for example, 
     * a rectangle turns into a box with five faces. The vertices of the Polygon are extruded 
     * toward the center of the Earth's sphere. 
     * </p>
     * <p>
     * Boolean value. Specifies whether to connect the point to the ground with a line. 
     * To extrude a Point, the value for <altitudeMode> must be either relativeToGround, 
     * relativeToSeaFloor, or absolute. The point is extruded toward the center of the 
     * Earth's sphere. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    @XmlJavaTypeAdapter(BooleanConverter.class)
    protected Boolean extrude;
    /**
     * <tessellate>
     * <p>
     * Boolean value. Specifies whether to allow the LineString to follow the terrain. 
     * To enable tessellation, the altitude mode must be clampToGround or clampToSeaFloor. 
     * Very large LineStrings should enable tessellation so that they follow the curvature 
     * of the earth (otherwise, they may go underground and be hidden). 
     * </p>
     * <p>
     * Boolean value. Specifies whether to allow the LinearRing to follow the terrain. 
     * To enable tessellation, the value for <altitudeMode> must be clampToGround or clampToSeaFloor. 
     * Very large LinearRings should enable tessellation so that they follow the curvature 
     * of the earth (otherwise, they may go underground and be hidden). 
     * </p>
     * <p>
     * Boolean value. Specifies whether to allow the Polygon to follow the terrain. To 
     * enable tessellation, the Polygon must have an altitude mode of clampToGround or 
     * clampToSeaFloor. Very large Polygons should enable tessellation so that they follow 
     * the curvature of the earth (otherwise, they may go underground and be hidden). 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    @XmlJavaTypeAdapter(BooleanConverter.class)
    protected Boolean tessellate;
    /**
     * AltitudeMode
     * <p>
     * clampToGround, relativeToGround, absolute 
     * </p>
     * 
     * See Also: 
     * See <LookAt> and <Region>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "clampToGround")
    protected AltitudeMode altitudeMode;
    /**
     * <coordinates> (required)
     * <p>
     * A single tuple consisting of floating point values for longitude, latitude, and 
     * altitude (in that order). Longitude and latitude values are in degrees, where longitude 
     * ≥ −180 and <= 180 latitude ≥ −90 and ≤ 90 altitude values (optional) are in meters 
     * above sea level 
     * </p>
     * <p>
     * Do not include spaces between the three values that describe a coordinate. 
     * </p>
     * <p>
     * Two or more coordinate tuples, each consisting of floating point values for longitude, 
     * latitude, and altitude. The altitude component is optional. Insert a space between 
     * tuples. Do not include spaces within a tuple. 
     * </p>
     * 
     * 
     * 
     */

    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", type = String.class)
    @XmlJavaTypeAdapter(CoordinatesConverter.class)
    protected List<Coordinate> coordinates;
    @XmlElement(name = "LineStringSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> lineStringSimpleExtension;
    /**
     * <Object>
     * <p>
     * This is an abstract base class and cannot be used directly in a KML file. It provides 
     * the id attribute, which allows unique identification of a KML element, and the targetId 
     * attribute, which is used to reference objects that have already been loaded into 
     * Google Earth. The id attribute must be assigned if the <Update> mechanism is to 
     * be used. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;<strong>
     * &lt;!-- <em>Object</em> id="ID" targetId="NCName" --&gt;
     * &lt;!-- /<em>Object</em>&gt; --&gt;</strong></pre>
     * 
     * 
     * 
     */
    @XmlElement(name = "LineStringObjectExtensionGroup")
    protected List<AbstractObject> lineStringObjectExtension;

    public LineString() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean}
     *     
     */
    public Boolean isExtrude() {
        return extrude;
    }

    /**
     * @see extrude
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean}
     *     
     */
    public void setExtrude(Boolean value) {
        this.extrude = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean}
     *     
     */
    public Boolean isTessellate() {
        return tessellate;
    }

    /**
     * @see tessellate
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean}
     *     
     */
    public void setTessellate(Boolean value) {
        this.tessellate = value;
    }

    /**
     * @see altitudeMode
     * 
     * @return
     *     possible object is
     *     {@code <}{@link Object}{@code>}
     *     {@code <}{@link msi.gama.ext.kml.AltitudeMode}{@code>}
     *     {@code <}{@link msi.gama.ext.kml.gx.AltitudeMode}{@code>}
     *     
     */
    public AltitudeMode getAltitudeMode() {
        return altitudeMode;
    }

    /**
     * @see altitudeMode
     * 
     * @param value
     *     allowed object is
     *     {@code <}{@link Object}{@code>}
     *     {@code <}{@link msi.gama.ext.kml.AltitudeMode}{@code>}
     *     {@code <}{@link msi.gama.ext.kml.gx.AltitudeMode}{@code>}
     *     
     */
    public void setAltitudeMode(AltitudeMode value) {
        this.altitudeMode = value;
    }

    /**
     * @see lineStringSimpleExtension
     * 
     */
    public List<Object> getLineStringSimpleExtension() {
        if (lineStringSimpleExtension == null) {
            lineStringSimpleExtension = new ArrayList<Object>();
        }
        return this.lineStringSimpleExtension;
    }

    /**
     * @see lineStringObjectExtension
     * 
     */
    public List<AbstractObject> getLineStringObjectExtension() {
        if (lineStringObjectExtension == null) {
            lineStringObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.lineStringObjectExtension;
    }

    /**
     * @see coordinates
     * 
     */
    public List<Coordinate> getCoordinates() {
        if (coordinates == null) {
            coordinates = new ArrayList<Coordinate>();
        }
        return coordinates;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((extrude == null)? 0 :extrude.hashCode()));
        result = ((prime*result)+((tessellate == null)? 0 :tessellate.hashCode()));
        result = ((prime*result)+((altitudeMode == null)? 0 :altitudeMode.hashCode()));
        result = ((prime*result)+((coordinates == null)? 0 :coordinates.hashCode()));
        result = ((prime*result)+((lineStringSimpleExtension == null)? 0 :lineStringSimpleExtension.hashCode()));
        result = ((prime*result)+((lineStringObjectExtension == null)? 0 :lineStringObjectExtension.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (super.equals(obj) == false) {
            return false;
        }
        if ((obj instanceof LineString) == false) {
            return false;
        }
        LineString other = ((LineString) obj);
        if (extrude == null) {
            if (other.extrude!= null) {
                return false;
            }
        } else {
            if (extrude.equals(other.extrude) == false) {
                return false;
            }
        }
        if (tessellate == null) {
            if (other.tessellate!= null) {
                return false;
            }
        } else {
            if (tessellate.equals(other.tessellate) == false) {
                return false;
            }
        }
        if (altitudeMode == null) {
            if (other.altitudeMode!= null) {
                return false;
            }
        } else {
            if (altitudeMode.equals(other.altitudeMode) == false) {
                return false;
            }
        }
        if (coordinates == null) {
            if (other.coordinates!= null) {
                return false;
            }
        } else {
            if (coordinates.equals(other.coordinates) == false) {
                return false;
            }
        }
        if (lineStringSimpleExtension == null) {
            if (other.lineStringSimpleExtension!= null) {
                return false;
            }
        } else {
            if (lineStringSimpleExtension.equals(other.lineStringSimpleExtension) == false) {
                return false;
            }
        }
        if (lineStringObjectExtension == null) {
            if (other.lineStringObjectExtension!= null) {
                return false;
            }
        } else {
            if (lineStringObjectExtension.equals(other.lineStringObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see coordinates
     * 
     * @param coordinates
     */
    public void setCoordinates(final List<Coordinate> coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * add a value to the coordinates property collection
     * 
     * @param longitude
     *     required parameter
     * @param latitude
     *     required parameter
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LineString addToCoordinates(final double longitude, final double latitude) {
        this.getCoordinates().add(new Coordinate(longitude, latitude));
        return this;
    }

    /**
     * add a value to the coordinates property collection
     * 
     * @param longitude
     *     required parameter
     * @param latitude
     *     required parameter
     * @param altitude
     *     required parameter
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LineString addToCoordinates(final double longitude, final double latitude, final double altitude) {
        this.getCoordinates().add(new Coordinate(longitude, latitude, altitude));
        return this;
    }

    /**
     * add a value to the coordinates property collection
     * 
     * @param coordinates
     *     required parameter
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LineString addToCoordinates(final String coordinates) {
        this.getCoordinates().add(new Coordinate(coordinates));
        return this;
    }

    /**
     * @see lineStringSimpleExtension
     * 
     * @param lineStringSimpleExtension
     */
    public void setLineStringSimpleExtension(final List<Object> lineStringSimpleExtension) {
        this.lineStringSimpleExtension = lineStringSimpleExtension;
    }

    /**
     * add a value to the lineStringSimpleExtension property collection
     * 
     * @param lineStringSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LineString addToLineStringSimpleExtension(final Object lineStringSimpleExtension) {
        this.getLineStringSimpleExtension().add(lineStringSimpleExtension);
        return this;
    }

    /**
     * @see lineStringObjectExtension
     * 
     * @param lineStringObjectExtension
     */
    public void setLineStringObjectExtension(final List<AbstractObject> lineStringObjectExtension) {
        this.lineStringObjectExtension = lineStringObjectExtension;
    }

    /**
     * add a value to the lineStringObjectExtension property collection
     * 
     * @param lineStringObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LineString addToLineStringObjectExtension(final AbstractObject lineStringObjectExtension) {
        this.getLineStringObjectExtension().add(lineStringObjectExtension);
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
    public LineString addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see geometrySimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setGeometrySimpleExtension(final List<Object> geometrySimpleExtension) {
        super.setGeometrySimpleExtension(geometrySimpleExtension);
    }

    @Obvious
    @Override
    public LineString addToGeometrySimpleExtension(final Object geometrySimpleExtension) {
        super.getGeometrySimpleExtension().add(geometrySimpleExtension);
        return this;
    }

    /**
     * @see geometryObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setGeometryObjectExtension(final List<AbstractObject> geometryObjectExtension) {
        super.setGeometryObjectExtension(geometryObjectExtension);
    }

    @Obvious
    @Override
    public LineString addToGeometryObjectExtension(final AbstractObject geometryObjectExtension) {
        super.getGeometryObjectExtension().add(geometryObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setExtrude(Boolean)
     * 
     * @param extrude
     *     required parameter
     */
    public LineString withExtrude(final Boolean extrude) {
        this.setExtrude(extrude);
        return this;
    }

    /**
     * fluent setter
     * @see #setTessellate(Boolean)
     * 
     * @param tessellate
     *     required parameter
     */
    public LineString withTessellate(final Boolean tessellate) {
        this.setTessellate(tessellate);
        return this;
    }

    /**
     * fluent setter
     * @see #setAltitudeMode(Object)
     * 
     * @param altitudeMode
     *     required parameter
     */
    public LineString withAltitudeMode(final  AltitudeMode altitudeMode) {
        this.setAltitudeMode(altitudeMode);
        return this;
    }

    /**
     * fluent setter
     * @see #setCoordinates(List<Coordinate>)
     * 
     * @param coordinates
     *     required parameter
     */
    public LineString withCoordinates(final List<Coordinate> coordinates) {
        this.setCoordinates(coordinates);
        return this;
    }

    /**
     * fluent setter
     * @see #setLineStringSimpleExtension(List<Object>)
     * 
     * @param lineStringSimpleExtension
     *     required parameter
     */
    public LineString withLineStringSimpleExtension(final List<Object> lineStringSimpleExtension) {
        this.setLineStringSimpleExtension(lineStringSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setLineStringObjectExtension(List<AbstractObject>)
     * 
     * @param lineStringObjectExtension
     *     required parameter
     */
    public LineString withLineStringObjectExtension(final List<AbstractObject> lineStringObjectExtension) {
        this.setLineStringObjectExtension(lineStringObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public LineString withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public LineString withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public LineString withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public LineString withGeometrySimpleExtension(final List<Object> geometrySimpleExtension) {
        super.withGeometrySimpleExtension(geometrySimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public LineString withGeometryObjectExtension(final List<AbstractObject> geometryObjectExtension) {
        super.withGeometryObjectExtension(geometryObjectExtension);
        return this;
    }

    /**
     * Creates a new instance of {@link List}{@code <}{@link Coordinate}{@code>} and set it to this.coordinates.
     * 
     * This method is a short version for:
     * <pre>
     * <code>
     * List<Coordinate> newValue = new List<Coordinate>();
     * this.setCoordinates(newValue); </code>
     * </pre>
     * 
     * 
     */
    public List<Coordinate> createAndSetCoordinates() {
        List<Coordinate> newValue = new ArrayList<Coordinate>();
        this.setCoordinates(newValue);
        return newValue;
    }

    @Override
    public LineString clone() {
        LineString copy;
        copy = ((LineString) super.clone());
        copy.coordinates = new ArrayList<Coordinate>((getCoordinates().size()));
        for (Coordinate iter: coordinates) {
            copy.coordinates.add(iter.clone());
        }
        copy.lineStringSimpleExtension = new ArrayList<Object>((getLineStringSimpleExtension().size()));
        for (Object iter: lineStringSimpleExtension) {
            copy.lineStringSimpleExtension.add(iter);
        }
        copy.lineStringObjectExtension = new ArrayList<AbstractObject>((getLineStringObjectExtension().size()));
        for (AbstractObject iter: lineStringObjectExtension) {
            copy.lineStringObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
