
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
 * <IconStyle>
 * <p>
 * Specifies how icons for point Placemarks are drawn, both in the Places panel and 
 * in the 3D viewer of Google Earth. The <Icon> element specifies the icon image. The 
 * <scale> element specifies the x, y scaling of the icon. The color specified in the 
 * <color> element of <IconStyle> is blended with the color of the <Icon>. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;IconStyle id="ID"&gt;</strong>
 *   &lt;!-- inherited from <em>ColorStyle</em> --&gt;
 *   &lt;color&gt;ffffffff&lt;/color&gt;            &lt;!-- kml:color --&gt;
 *   &lt;colorMode&gt;normal&lt;/colorMode&gt;      &lt;!-- kml:colorModeEnum:normal <em>or</em> random --&gt;
 * 
 *   &lt;!-- specific to IconStyle --&gt;
 *   &lt;scale&gt;1&lt;/scale&gt;                   &lt;!-- float --&gt;
 *   &lt;heading&gt;0&lt;/heading&gt;               &lt;!-- float --&gt;
 *   &lt;Icon&gt;
 *     &lt;href&gt;...&lt;/href&gt;
 *   &lt;/Icon&gt; 
 *   &lt;hotSpot x="0.5"  y="0.5" 
 *     xunits="fraction" yunits="fraction"/&gt;    &lt;!-- kml:vec2 --&gt;                    
 * <strong>&lt;/IconStyle&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <ColorStyle>
 * 
 * Contains: 
 * @see: <Icon>
 * @see: <href>
 * 
 * Contained By: 
 * @see: <Style>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IconStyleType", propOrder = {
    "scale",
    "heading",
    "icon",
    "hotSpot",
    "iconStyleSimpleExtension",
    "iconStyleObjectExtension"
})
@XmlRootElement(name = "IconStyle", namespace = "http://www.opengis.net/kml/2.2")
public class IconStyle
    extends ColorStyle
    implements Cloneable
{

    /**
     * <scale>
     * <p>
     * Note: The <geomScale> tag has been deprecated. Use <scale> instead. 
     * </p>
     * <p>
     * Resizes the icon. 
     * </p>
     * <p>
     * Scales a model along the x, y, and z axes in the model's coordinate space. <Scale> 
     * <x>2.5</x> <y>2.5</y> <z>3.5</z> </Scale> 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "1.0")
    protected double scale;
    /**
     * <heading>
     * <p>
     * Direction (azimuth) of the camera, in degrees. Default=0 (true North). (See diagram.) 
     * Values range from 0 to 360 degrees. 
     * </p>
     * <p>
     * Direction (that is, North, South, East, West), in degrees. Default=0 (North). (See 
     * diagram below.) Values range from 0 to 360 degrees. 
     * </p>
     * <p>
     * Direction (that is, North, South, East, West), in degrees. Default=0 (North). (See 
     * diagram.) Values range from 0 to 360 degrees. 
     * </p>
     * <p>
     * Rotation about the z axis (normal to the Earth's surface). A value of 0 (the default) 
     * equals North. A positive rotation is clockwise around the z axis and specified in 
     * degrees from 0 to 360. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double heading;
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
    @XmlElement(name = "Icon")
    protected Icon icon;
    /**
     * <hotspot x="0.5" y="0.5" xunits="fraction" yunits="fraction">
     * <p>
     * Specifies the position within the Icon that is "anchored" to the <Point> specified 
     * in the Placemark. The x and y values can be specified in three different ways: as 
     * pixels ("pixels"), as fractions of the icon ("fraction"), or as inset pixels ("insetPixels"), 
     * which is an offset in pixels from the upper right corner of the icon. The x and 
     * y positions can be specified in different ways—for example, x can be in pixels and 
     * y can be a fraction. The origin of the coordinate system is in the lower left corner 
     * of the icon. x - Either the number of pixels, a fractional component of the icon, 
     * or a pixel inset indicating the x component of a point on the icon. y - Either the 
     * number of pixels, a fractional component of the icon, or a pixel inset indicating 
     * the y component of a point on the icon. xunits - Units in which the x value is specified. 
     * A value of fraction indicates the x value is a fraction of the icon. A value of 
     * pixels indicates the x value in pixels. A value of insetPixels indicates the indent 
     * from the right edge of the icon. yunits - Units in which the y value is specified. 
     * A value of fraction indicates the y value is a fraction of the icon. A value of 
     * pixels indicates the y value in pixels. A value of insetPixels indicates the indent 
     * from the top edge of the icon. 
     * </p>
     * 
     * 
     * 
     */
    protected Vec2 hotSpot;
    @XmlElement(name = "IconStyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> iconStyleSimpleExtension;
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
    @XmlElement(name = "IconStyleObjectExtensionGroup")
    protected List<AbstractObject> iconStyleObjectExtension;

    public IconStyle() {
        super();
    }

    /**
     * @see scale
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getScale() {
        return scale;
    }

    /**
     * @see scale
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setScale(double value) {
        this.scale = value;
    }

    /**
     * @see heading
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getHeading() {
        return heading;
    }

    /**
     * @see heading
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setHeading(double value) {
        this.heading = value;
    }

    /**
     * @see icon
     * 
     * @return
     *     possible object is
     *     {@link BasicLink}
     *     
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * @see icon
     * 
     * @param value
     *     allowed object is
     *     {@link BasicLink}
     *     
     */
    public void setIcon(Icon value) {
        this.icon = value;
    }

    /**
     * @see hotSpot
     * 
     * @return
     *     possible object is
     *     {@link Vec2}
     *     
     */
    public Vec2 getHotSpot() {
        return hotSpot;
    }

    /**
     * @see hotSpot
     * 
     * @param value
     *     allowed object is
     *     {@link Vec2}
     *     
     */
    public void setHotSpot(Vec2 value) {
        this.hotSpot = value;
    }

    /**
     * @see iconStyleSimpleExtension
     * 
     */
    public List<Object> getIconStyleSimpleExtension() {
        if (iconStyleSimpleExtension == null) {
            iconStyleSimpleExtension = new ArrayList<Object>();
        }
        return this.iconStyleSimpleExtension;
    }

    /**
     * @see iconStyleObjectExtension
     * 
     */
    public List<AbstractObject> getIconStyleObjectExtension() {
        if (iconStyleObjectExtension == null) {
            iconStyleObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.iconStyleObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(scale);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        temp = Double.doubleToLongBits(heading);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((icon == null)? 0 :icon.hashCode()));
        result = ((prime*result)+((hotSpot == null)? 0 :hotSpot.hashCode()));
        result = ((prime*result)+((iconStyleSimpleExtension == null)? 0 :iconStyleSimpleExtension.hashCode()));
        result = ((prime*result)+((iconStyleObjectExtension == null)? 0 :iconStyleObjectExtension.hashCode()));
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
        if ((obj instanceof IconStyle) == false) {
            return false;
        }
        IconStyle other = ((IconStyle) obj);
        if (scale!= other.scale) {
            return false;
        }
        if (heading!= other.heading) {
            return false;
        }
        if (icon == null) {
            if (other.icon!= null) {
                return false;
            }
        } else {
            if (icon.equals(other.icon) == false) {
                return false;
            }
        }
        if (hotSpot == null) {
            if (other.hotSpot!= null) {
                return false;
            }
        } else {
            if (hotSpot.equals(other.hotSpot) == false) {
                return false;
            }
        }
        if (iconStyleSimpleExtension == null) {
            if (other.iconStyleSimpleExtension!= null) {
                return false;
            }
        } else {
            if (iconStyleSimpleExtension.equals(other.iconStyleSimpleExtension) == false) {
                return false;
            }
        }
        if (iconStyleObjectExtension == null) {
            if (other.iconStyleObjectExtension!= null) {
                return false;
            }
        } else {
            if (iconStyleObjectExtension.equals(other.iconStyleObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Icon} and set it to icon.
     * 
     * This method is a short version for:
     * <code>
     * Icon icon = new Icon();
     * this.setIcon(icon); </code>
     * 
     * 
     */
    public Icon createAndSetIcon() {
        Icon newValue = new Icon();
        this.setIcon(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Vec2} and set it to hotSpot.
     * 
     * This method is a short version for:
     * <code>
     * Vec2 vec2 = new Vec2();
     * this.setHotSpot(vec2); </code>
     * 
     * 
     */
    public Vec2 createAndSetHotSpot() {
        Vec2 newValue = new Vec2();
        this.setHotSpot(newValue);
        return newValue;
    }

    /**
     * @see iconStyleSimpleExtension
     * 
     * @param iconStyleSimpleExtension
     */
    public void setIconStyleSimpleExtension(final List<Object> iconStyleSimpleExtension) {
        this.iconStyleSimpleExtension = iconStyleSimpleExtension;
    }

    /**
     * add a value to the iconStyleSimpleExtension property collection
     * 
     * @param iconStyleSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public IconStyle addToIconStyleSimpleExtension(final Object iconStyleSimpleExtension) {
        this.getIconStyleSimpleExtension().add(iconStyleSimpleExtension);
        return this;
    }

    /**
     * @see iconStyleObjectExtension
     * 
     * @param iconStyleObjectExtension
     */
    public void setIconStyleObjectExtension(final List<AbstractObject> iconStyleObjectExtension) {
        this.iconStyleObjectExtension = iconStyleObjectExtension;
    }

    /**
     * add a value to the iconStyleObjectExtension property collection
     * 
     * @param iconStyleObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public IconStyle addToIconStyleObjectExtension(final AbstractObject iconStyleObjectExtension) {
        this.getIconStyleObjectExtension().add(iconStyleObjectExtension);
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
    public IconStyle addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see subStyleSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setSubStyleSimpleExtension(final List<Object> subStyleSimpleExtension) {
        super.setSubStyleSimpleExtension(subStyleSimpleExtension);
    }

    @Obvious
    @Override
    public IconStyle addToSubStyleSimpleExtension(final Object subStyleSimpleExtension) {
        super.getSubStyleSimpleExtension().add(subStyleSimpleExtension);
        return this;
    }

    /**
     * @see subStyleObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setSubStyleObjectExtension(final List<AbstractObject> subStyleObjectExtension) {
        super.setSubStyleObjectExtension(subStyleObjectExtension);
    }

    @Obvious
    @Override
    public IconStyle addToSubStyleObjectExtension(final AbstractObject subStyleObjectExtension) {
        super.getSubStyleObjectExtension().add(subStyleObjectExtension);
        return this;
    }

    /**
     * @see colorStyleSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setColorStyleSimpleExtension(final List<Object> colorStyleSimpleExtension) {
        super.setColorStyleSimpleExtension(colorStyleSimpleExtension);
    }

    @Obvious
    @Override
    public IconStyle addToColorStyleSimpleExtension(final Object colorStyleSimpleExtension) {
        super.getColorStyleSimpleExtension().add(colorStyleSimpleExtension);
        return this;
    }

    /**
     * @see colorStyleObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setColorStyleObjectExtension(final List<AbstractObject> colorStyleObjectExtension) {
        super.setColorStyleObjectExtension(colorStyleObjectExtension);
    }

    @Obvious
    @Override
    public IconStyle addToColorStyleObjectExtension(final AbstractObject colorStyleObjectExtension) {
        super.getColorStyleObjectExtension().add(colorStyleObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setScale(double)
     * 
     * @param scale
     *     required parameter
     */
    public IconStyle withScale(final double scale) {
        this.setScale(scale);
        return this;
    }

    /**
     * fluent setter
     * @see #setHeading(double)
     * 
     * @param heading
     *     required parameter
     */
    public IconStyle withHeading(final double heading) {
        this.setHeading(heading);
        return this;
    }

    /**
     * fluent setter
     * @see #setIcon(Icon)
     * 
     * @param icon
     *     required parameter
     */
    public IconStyle withIcon(final Icon icon) {
        this.setIcon(icon);
        return this;
    }

    /**
     * fluent setter
     * @see #setHotSpot(Vec2)
     * 
     * @param hotSpot
     *     required parameter
     */
    public IconStyle withHotSpot(final Vec2 hotSpot) {
        this.setHotSpot(hotSpot);
        return this;
    }

    /**
     * fluent setter
     * @see #setIconStyleSimpleExtension(List<Object>)
     * 
     * @param iconStyleSimpleExtension
     *     required parameter
     */
    public IconStyle withIconStyleSimpleExtension(final List<Object> iconStyleSimpleExtension) {
        this.setIconStyleSimpleExtension(iconStyleSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setIconStyleObjectExtension(List<AbstractObject>)
     * 
     * @param iconStyleObjectExtension
     *     required parameter
     */
    public IconStyle withIconStyleObjectExtension(final List<AbstractObject> iconStyleObjectExtension) {
        this.setIconStyleObjectExtension(iconStyleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withSubStyleSimpleExtension(final List<Object> subStyleSimpleExtension) {
        super.withSubStyleSimpleExtension(subStyleSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withSubStyleObjectExtension(final List<AbstractObject> subStyleObjectExtension) {
        super.withSubStyleObjectExtension(subStyleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withColor(final String color) {
        super.withColor(color);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withColorMode(final ColorMode colorMode) {
        super.withColorMode(colorMode);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withColorStyleSimpleExtension(final List<Object> colorStyleSimpleExtension) {
        super.withColorStyleSimpleExtension(colorStyleSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public IconStyle withColorStyleObjectExtension(final List<AbstractObject> colorStyleObjectExtension) {
        super.withColorStyleObjectExtension(colorStyleObjectExtension);
        return this;
    }

    @Override
    public IconStyle clone() {
        IconStyle copy;
        copy = ((IconStyle) super.clone());
        copy.icon = ((icon == null)?null:((Icon) icon.clone()));
        copy.hotSpot = ((hotSpot == null)?null:((Vec2) hotSpot.clone()));
        copy.iconStyleSimpleExtension = new ArrayList<Object>((getIconStyleSimpleExtension().size()));
        for (Object iter: iconStyleSimpleExtension) {
            copy.iconStyleSimpleExtension.add(iter);
        }
        copy.iconStyleObjectExtension = new ArrayList<AbstractObject>((getIconStyleObjectExtension().size()));
        for (AbstractObject iter: iconStyleObjectExtension) {
            copy.iconStyleObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
