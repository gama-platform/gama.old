
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <icon> see also <icon>.
 * <p>
 * <Icon> <href>Sunset.jpg</href> </Icon> 
 * </p>
 * <p>
 * A custom Icon. In <IconStyle>, the only child element of <Icon> is <href>: <href>: 
 * An HTTP address or a local file specification used to load an icon. 
 * </p>
 * <p>
 * Defines an image associated with an Icon style or overlay. <Icon> has the same child 
 * elements as <Link>. The required <href> child element defines the location of the 
 * image to be used as the overlay or as the icon for the placemark. This location 
 * can either be on a local file system or a remote web server. 
 * </p>
 * <p>
 * Defines the image associated with the Overlay. The <href> element defines the location 
 * of the image to be used as the Overlay. This location can be either on a local file 
 * system or on a web server. If this element is omitted or contains no <href>, a rectangle 
 * is drawn using the color and size defined by the ground or screen overlay. <Icon> 
 * <href>icon.jpg</href> </Icon> 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;Icon id="ID"&gt;</strong>
 *   &lt;!-- specific to Icon --&gt;
 *   &lt;href&gt;<em>...</em>&lt;/href&gt;                      &lt;!-- anyURI --&gt;
 *   &lt;refreshMode&gt;onChange&lt;/refreshMode&gt;   
 *     &lt;!-- kml:refreshModeEnum: onChange, onInterval, <em>or</em> onExpire --&gt;   
 *   &lt;refreshInterval&gt;4&lt;/refreshInterval&gt;  &lt;!-- float --&gt;
 *   &lt;viewRefreshMode&gt;never&lt;/viewRefreshMode&gt; 
 *     &lt;!-- kml:viewRefreshModeEnum: never, onStop, onRequest, onRegion --&gt;
 *   &lt;viewRefreshTime&gt;4&lt;/viewRefreshTime&gt;  &lt;!-- float --&gt;
 *   &lt;viewBoundScale&gt;1&lt;/viewBoundScale&gt;    &lt;!-- float --&gt;
 *   &lt;viewFormat&gt;...&lt;/viewFormat&gt;          &lt;!-- string --&gt;
 *   &lt;httpQuery&gt;...&lt;/httpQuery&gt;            &lt;!-- string --&gt;
 *   <strong>&lt;/Icon&gt;</strong></pre>
 * 
 * Contained By: 
 * @see: <GroundOverlay>
 * @see: <IconStyle>
 * @see: <ScreenOverlay>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Icon", namespace = "", propOrder = {
    "refreshMode",
    "refreshInterval",
    "viewRefreshMode",
    "viewRefreshTime",
    "viewBoundScale",
    "viewFormat",
    "httpQuery",
    "linkSimpleExtension",
    "linkObjectExtension"
})
@XmlRootElement(name = "Icon", namespace = "http://www.opengis.net/kml/2.2")
public class Icon
    extends BasicLink
    implements Cloneable
{

    /**
     * RefreshMode
     * <p>
     * onChange, onInterval, onExpire 
     * </p>
     * 
     * See Also: 
     * See <Link>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "onChange")
    protected RefreshMode refreshMode;
    /**
     * <refreshinterval>
     * <p>
     * Indicates to refresh the file every n seconds. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "4.0")
    protected double refreshInterval;
    /**
     * ViewRefreshMode
     * <p>
     * never, onRequest, onStop, onRegion 
     * </p>
     * 
     * See Also: 
     * See <Link>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "never")
    protected ViewRefreshMode viewRefreshMode;
    /**
     * <viewrefreshtime>
     * <p>
     * After camera movement stops, specifies the number of seconds to wait before refreshing 
     * the view. (See <viewRefreshMode> and onStop above.) 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "4.0")
    protected double viewRefreshTime;
    /**
     * <viewboundscale>
     * <p>
     * Scales the BBOX parameters before sending them to the server. A value less than 
     *  1 specifies to use less than the full view (screen). A value greater than 1 specifies 
     * to fetch an area that extends beyond the edges of the current view. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2", defaultValue = "1.0")
    protected double viewBoundScale;
    /**
     * <viewformat>
     * <p>
     * BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth] 
     * </p>
     * <p>
     * If you specify a <viewRefreshMode> of onStop and do not include the <viewFormat> 
     * tag in the file, the following information is automatically appended to the query 
     * string: 
     * </p>
     * <p>
     * If you specify an empty <viewFormat> tag, no information is appended to the query 
     * string. 
     * </p>
     * <p>
     * Specifies the format of the query string that is appended to the Link's <href> before 
     * the file is fetched.(If the <href> specifies a local file, this element is ignored.) 
     * </p>
     * <p>
     * This information matches the Web Map Service (WMS) bounding box specification. 
     * </p>
     * <p>
     * You can also specify a custom set of viewing parameters to add to the query string. 
     * If you supply a format string, it is used instead of the BBOX information. If you 
     * also want the BBOX information, you need to add those parameters along with the 
     * custom parameters. 
     * </p>
     * <p>
     * You can use any of the following parameters in your format string (and Google Earth 
     * will substitute the appropriate current value at the time it creates the query string): 
     * [lookatLon], [lookatLat] - longitude and latitude of the point that <LookAt> is 
     * viewing [lookatRange], [lookatTilt], [lookatHeading] - values used by the <LookAt> 
     * element (see descriptions of <range>, <tilt>, and <heading> in <LookAt>) [lookatTerrainLon], 
     * [lookatTerrainLat], [lookatTerrainAlt] - point on the terrain in degrees/meters 
     * that <LookAt> is viewing [cameraLon], [cameraLat], [cameraAlt] - degrees/meters 
     * of the eyepoint for the camera [horizFov], [vertFov] - horizontal, vertical field 
     * of view for the camera [horizPixels], [vertPixels] - size in pixels of the 3D viewer 
     * [terrainEnabled] - indicates whether the 3D viewer is showing terrain 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String viewFormat;
    /**
     * <httpquery>
     * <p>
     * Appends information to the query string, based on the parameters specified. (Google 
     * Earth substitutes the appropriate current value at the time it creates the query 
     * string.) The following parameters are supported: [clientVersion] [kmlVersion] [clientName] 
     * [language] 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(namespace = "http://www.opengis.net/kml/2.2")
    protected String httpQuery;
    @XmlElement(name = "LinkSimpleExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> linkSimpleExtension;
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
    @XmlElement(name = "LinkObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2")
    protected List<AbstractObject> linkObjectExtension;

    public Icon() {
        super();
    }

    /**
     * @see refreshMode
     * 
     * @return
     *     possible object is
     *     {@link RefreshMode}
     *     
     */
    public RefreshMode getRefreshMode() {
        return refreshMode;
    }

    /**
     * @see refreshMode
     * 
     * @param value
     *     allowed object is
     *     {@link RefreshMode}
     *     
     */
    public void setRefreshMode(RefreshMode value) {
        this.refreshMode = value;
    }

    /**
     * @see refreshInterval
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getRefreshInterval() {
        return refreshInterval;
    }

    /**
     * @see refreshInterval
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setRefreshInterval(double value) {
        this.refreshInterval = value;
    }

    /**
     * @see viewRefreshMode
     * 
     * @return
     *     possible object is
     *     {@link ViewRefreshMode}
     *     
     */
    public ViewRefreshMode getViewRefreshMode() {
        return viewRefreshMode;
    }

    /**
     * @see viewRefreshMode
     * 
     * @param value
     *     allowed object is
     *     {@link ViewRefreshMode}
     *     
     */
    public void setViewRefreshMode(ViewRefreshMode value) {
        this.viewRefreshMode = value;
    }

    /**
     * @see viewRefreshTime
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getViewRefreshTime() {
        return viewRefreshTime;
    }

    /**
     * @see viewRefreshTime
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setViewRefreshTime(double value) {
        this.viewRefreshTime = value;
    }

    /**
     * @see viewBoundScale
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getViewBoundScale() {
        return viewBoundScale;
    }

    /**
     * @see viewBoundScale
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setViewBoundScale(double value) {
        this.viewBoundScale = value;
    }

    /**
     * @see viewFormat
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getViewFormat() {
        return viewFormat;
    }

    /**
     * @see viewFormat
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setViewFormat(String value) {
        this.viewFormat = value;
    }

    /**
     * @see httpQuery
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getHttpQuery() {
        return httpQuery;
    }

    /**
     * @see httpQuery
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setHttpQuery(String value) {
        this.httpQuery = value;
    }

    /**
     * @see linkSimpleExtension
     * 
     */
    public List<Object> getLinkSimpleExtension() {
        if (linkSimpleExtension == null) {
            linkSimpleExtension = new ArrayList<Object>();
        }
        return this.linkSimpleExtension;
    }

    /**
     * @see linkObjectExtension
     * 
     */
    public List<AbstractObject> getLinkObjectExtension() {
        if (linkObjectExtension == null) {
            linkObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.linkObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        result = ((prime*result)+((refreshMode == null)? 0 :refreshMode.hashCode()));
        temp = Double.doubleToLongBits(refreshInterval);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((viewRefreshMode == null)? 0 :viewRefreshMode.hashCode()));
        temp = Double.doubleToLongBits(viewRefreshTime);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(viewBoundScale);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((viewFormat == null)? 0 :viewFormat.hashCode()));
        result = ((prime*result)+((httpQuery == null)? 0 :httpQuery.hashCode()));
        result = ((prime*result)+((linkSimpleExtension == null)? 0 :linkSimpleExtension.hashCode()));
        result = ((prime*result)+((linkObjectExtension == null)? 0 :linkObjectExtension.hashCode()));
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
        if ((obj instanceof Icon) == false) {
            return false;
        }
        Icon other = ((Icon) obj);
        if (refreshMode == null) {
            if (other.refreshMode!= null) {
                return false;
            }
        } else {
            if (refreshMode.equals(other.refreshMode) == false) {
                return false;
            }
        }
        if (refreshInterval!= other.refreshInterval) {
            return false;
        }
        if (viewRefreshMode == null) {
            if (other.viewRefreshMode!= null) {
                return false;
            }
        } else {
            if (viewRefreshMode.equals(other.viewRefreshMode) == false) {
                return false;
            }
        }
        if (viewRefreshTime!= other.viewRefreshTime) {
            return false;
        }
        if (viewBoundScale!= other.viewBoundScale) {
            return false;
        }
        if (viewFormat == null) {
            if (other.viewFormat!= null) {
                return false;
            }
        } else {
            if (viewFormat.equals(other.viewFormat) == false) {
                return false;
            }
        }
        if (httpQuery == null) {
            if (other.httpQuery!= null) {
                return false;
            }
        } else {
            if (httpQuery.equals(other.httpQuery) == false) {
                return false;
            }
        }
        if (linkSimpleExtension == null) {
            if (other.linkSimpleExtension!= null) {
                return false;
            }
        } else {
            if (linkSimpleExtension.equals(other.linkSimpleExtension) == false) {
                return false;
            }
        }
        if (linkObjectExtension == null) {
            if (other.linkObjectExtension!= null) {
                return false;
            }
        } else {
            if (linkObjectExtension.equals(other.linkObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see linkSimpleExtension
     * 
     * @param linkSimpleExtension
     */
    public void setLinkSimpleExtension(final List<Object> linkSimpleExtension) {
        this.linkSimpleExtension = linkSimpleExtension;
    }

    /**
     * add a value to the linkSimpleExtension property collection
     * 
     * @param linkSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Icon addToLinkSimpleExtension(final Object linkSimpleExtension) {
        this.getLinkSimpleExtension().add(linkSimpleExtension);
        return this;
    }

    /**
     * @see linkObjectExtension
     * 
     * @param linkObjectExtension
     */
    public void setLinkObjectExtension(final List<AbstractObject> linkObjectExtension) {
        this.linkObjectExtension = linkObjectExtension;
    }

    /**
     * add a value to the linkObjectExtension property collection
     * 
     * @param linkObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Icon addToLinkObjectExtension(final AbstractObject linkObjectExtension) {
        this.getLinkObjectExtension().add(linkObjectExtension);
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
    public Icon addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see basicLinkSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setBasicLinkSimpleExtension(final List<Object> basicLinkSimpleExtension) {
        super.setBasicLinkSimpleExtension(basicLinkSimpleExtension);
    }

    @Obvious
    @Override
    public Icon addToBasicLinkSimpleExtension(final Object basicLinkSimpleExtension) {
        super.getBasicLinkSimpleExtension().add(basicLinkSimpleExtension);
        return this;
    }

    /**
     * @see basicLinkObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setBasicLinkObjectExtension(final List<AbstractObject> basicLinkObjectExtension) {
        super.setBasicLinkObjectExtension(basicLinkObjectExtension);
    }

    @Obvious
    @Override
    public Icon addToBasicLinkObjectExtension(final AbstractObject basicLinkObjectExtension) {
        super.getBasicLinkObjectExtension().add(basicLinkObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setRefreshMode(RefreshMode)
     * 
     * @param refreshMode
     *     required parameter
     */
    public Icon withRefreshMode(final RefreshMode refreshMode) {
        this.setRefreshMode(refreshMode);
        return this;
    }

    /**
     * fluent setter
     * @see #setRefreshInterval(double)
     * 
     * @param refreshInterval
     *     required parameter
     */
    public Icon withRefreshInterval(final double refreshInterval) {
        this.setRefreshInterval(refreshInterval);
        return this;
    }

    /**
     * fluent setter
     * @see #setViewRefreshMode(ViewRefreshMode)
     * 
     * @param viewRefreshMode
     *     required parameter
     */
    public Icon withViewRefreshMode(final ViewRefreshMode viewRefreshMode) {
        this.setViewRefreshMode(viewRefreshMode);
        return this;
    }

    /**
     * fluent setter
     * @see #setViewRefreshTime(double)
     * 
     * @param viewRefreshTime
     *     required parameter
     */
    public Icon withViewRefreshTime(final double viewRefreshTime) {
        this.setViewRefreshTime(viewRefreshTime);
        return this;
    }

    /**
     * fluent setter
     * @see #setViewBoundScale(double)
     * 
     * @param viewBoundScale
     *     required parameter
     */
    public Icon withViewBoundScale(final double viewBoundScale) {
        this.setViewBoundScale(viewBoundScale);
        return this;
    }

    /**
     * fluent setter
     * @see #setViewFormat(String)
     * 
     * @param viewFormat
     *     required parameter
     */
    public Icon withViewFormat(final String viewFormat) {
        this.setViewFormat(viewFormat);
        return this;
    }

    /**
     * fluent setter
     * @see #setHttpQuery(String)
     * 
     * @param httpQuery
     *     required parameter
     */
    public Icon withHttpQuery(final String httpQuery) {
        this.setHttpQuery(httpQuery);
        return this;
    }

    /**
     * fluent setter
     * @see #setLinkSimpleExtension(List<Object>)
     * 
     * @param linkSimpleExtension
     *     required parameter
     */
    public Icon withLinkSimpleExtension(final List<Object> linkSimpleExtension) {
        this.setLinkSimpleExtension(linkSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setLinkObjectExtension(List<AbstractObject>)
     * 
     * @param linkObjectExtension
     *     required parameter
     */
    public Icon withLinkObjectExtension(final List<AbstractObject> linkObjectExtension) {
        this.setLinkObjectExtension(linkObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Icon withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Icon withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Icon withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public Icon withHref(final String href) {
        super.withHref(href);
        return this;
    }

    @Obvious
    @Override
    public Icon withBasicLinkSimpleExtension(final List<Object> basicLinkSimpleExtension) {
        super.withBasicLinkSimpleExtension(basicLinkSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Icon withBasicLinkObjectExtension(final List<AbstractObject> basicLinkObjectExtension) {
        super.withBasicLinkObjectExtension(basicLinkObjectExtension);
        return this;
    }

    @Override
    public Icon clone() {
        Icon copy;
        copy = ((Icon) super.clone());
        copy.linkSimpleExtension = new ArrayList<Object>((getLinkSimpleExtension().size()));
        for (Object iter: linkSimpleExtension) {
            copy.linkSimpleExtension.add(iter);
        }
        copy.linkObjectExtension = new ArrayList<AbstractObject>((getLinkObjectExtension().size()));
        for (AbstractObject iter: linkObjectExtension) {
            copy.linkObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
