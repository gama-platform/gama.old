
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
 * <Style>
 * <p>
 * A Style defines an addressable style group that can be referenced by StyleMaps and 
 * Features. Styles affect how Geometry is presented in the 3D viewer and how Features 
 * appear in the Places panel of the List view. Shared styles are collected in a <Document> 
 * and must have an id defined for them so that they can be referenced by the individual 
 * Features that use them. 
 * </p>
 * <p>
 * A Style defines an addressable style group that can be referenced by StyleMaps and 
 * Features. Styles affect how Geometry is presented in the 3D viewer and how Features 
 * appear in the Places panel of the List view. Shared styles are collected in a <Document> 
 * and must have an id defined for them so that they can be referenced by the individual 
 * Features that use them. 
 * </p>
 * <p>
 * Use an id to refer to the style from a <styleUrl>. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;Style id="ID"&gt;
 * </strong>&lt;!-- extends StyleSelector --&gt;
 * 
 * &lt;!-- specific to Style --&gt;
 *   &lt;IconStyle&gt;...&lt;/IconStyle&gt;
 *   &lt;LabelStyle&gt;...&lt;/LabelStyle&gt;
 *   &lt;LineStyle&gt;...&lt;/LineStyle&gt;
 *   &lt;PolyStyle&gt;...&lt;/PolyStyle&gt;
 *   &lt;BalloonStyle&gt;...&lt;/BalloonStyle&gt;
 *   &lt;ListStyle&gt;<strong>...</strong>&lt;/ListStyle&gt;<strong>
 * &lt;/Style&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <StyleSelector>
 * 
 * Contained By: 
 * @see: <Feature>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StyleType", propOrder = {
    "iconStyle",
    "labelStyle",
    "lineStyle",
    "polyStyle",
    "balloonStyle",
    "listStyle",
    "styleSimpleExtension",
    "styleObjectExtension"
})
@XmlRootElement(name = "Style", namespace = "http://www.opengis.net/kml/2.2")
public class Style
    extends StyleSelector
    implements Cloneable
{

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
    @XmlElement(name = "IconStyle")
    protected IconStyle iconStyle;
    /**
     * <LabelStyle>
     * <p>
     * Note: The <labelColor> tag is deprecated. Use <LabelStyle> instead. 
     * </p>
     * <p>
     * Specifies how the <name> of a Feature is drawn in the 3D viewer. A custom color, 
     * color mode, and scale for the label (name) can be specified. 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;LabelStyle id="ID"&gt;</strong>
     *   &lt;!-- inherited from <em>ColorStyle</em> --&gt;
     *   &lt;color&gt;ffffffff&lt;/color&gt;            &lt;!-- kml:color --&gt;
     *   &lt;colorMode&gt;normal&lt;/colorMode&gt;      &lt;!-- kml:colorModeEnum: normal <em>or</em> random --&gt;
     * 
     *   &lt;!-- specific to LabelStyle --&gt;
     *   &lt;scale&gt;1&lt;/scale&gt;                   &lt;!-- float --&gt;
     * <strong>&lt;/LabelStyle&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <ColorStyle>
     * 
     * Contained By: 
     * @see: <Style>
     * 
     * 
     * 
     */
    @XmlElement(name = "LabelStyle")
    protected LabelStyle labelStyle;
    /**
     * <LineStyle>
     * <p>
     * Specifies the drawing style (color, color mode, and line width) for all line geometry. 
     * Line geometry includes the outlines of outlined polygons and the extruded "tether" 
     * of Placemark icons (if extrusion is enabled). 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;LineStyle id="ID"&gt;</strong>
     *   &lt;!-- inherited from <em>ColorStyle</em> --&gt;
     *   &lt;color&gt;ffffffff&lt;/color&gt;            &lt;!-- kml:color --&gt;
     *   &lt;colorMode&gt;normal&lt;/colorMode&gt;      &lt;!-- colorModeEnum: normal <em>or</em> random --&gt;
     * 
     *   &lt;!-- specific to LineStyle --&gt;
     *   &lt;width&gt;1&lt;/width&gt;                   &lt;!-- float --&gt;
     * <strong>&lt;/LineStyle&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <ColorStyle>
     * 
     * Contained By: 
     * @see: <Style>
     * 
     * 
     * 
     */
    @XmlElement(name = "LineStyle")
    protected LineStyle lineStyle;
    /**
     * <PolyStyle>
     * Syntax: 
     * <pre><strong>&lt;PolyStyle id="ID"&gt;</strong>
     *   &lt;!-- inherited from <em>ColorStyle</em> --&gt;
     *   &lt;color&gt;ffffffff&lt;/color&gt;            &lt;!-- kml:color --&gt;
     *   &lt;colorMode&gt;normal&lt;/colorMode&gt;      &lt;!-- kml:colorModeEnum: normal <em>or</em> random --&gt;
     * 
     *   &lt;!-- specific to PolyStyle --&gt;
     *   &lt;fill&gt;1&lt;/fill&gt;                     &lt;!-- boolean --&gt;
     *   &lt;outline&gt;1&lt;/outline&gt;               &lt;!-- boolean --&gt;
     * <strong>&lt;/PolyStyle&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <ColorStyle>
     * 
     * Contained By: 
     * @see: <Style>
     * 
     * 
     * 
     */
    @XmlElement(name = "PolyStyle")
    protected PolyStyle polyStyle;
    /**
     * <BalloonStyle>
     * <p>
     * Specifies how the description balloon for placemarks is drawn. The <bgColor>, if 
     * specified, is used as the background color of the balloon. See <Feature> for a diagram 
     * illustrating how the default description balloon appears in Google Earth. 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;BalloonStyle id="ID"&gt;</strong>
     *   &lt;!-- specific to BalloonStyle --&gt;
     *   &lt;bgColor&gt;ffffffff&lt;/bgColor&gt;            &lt;!-- kml:color --&gt;
     *   &lt;textColor&gt;ff000000&lt;/textColor&gt;        &lt;!-- kml:color --&gt; 
     *   &lt;text&gt;<em>...</em>&lt;/text&gt;                       &lt;!-- string --&gt;
     *   <span>&lt;displayMode&gt;default&lt;/displayMode&gt;<strong>     </strong>&lt;!-- kml:displayModeEnum --&gt;</span><strong>
     * &lt;/BalloonStyle&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <ColorStyle>
     * 
     * Contained By: 
     * @see: <Style>
     * 
     * 
     * 
     */
    @XmlElement(name = "BalloonStyle")
    protected BalloonStyle balloonStyle;
    /**
     * <ListStyle>
     * <p>
     * Specifies how a Feature is displayed in the list view. The list view is a hierarchy 
     * of containers and children; in Google Earth, this is the Places panel. 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;ListStyle id="ID"&gt;</strong>
     *   &lt;!-- specific to ListStyle --&gt;
     *   &lt;listItemType&gt;check&lt;/listItemType&gt; &lt;!-- kml:listItemTypeEnum:check,
     *                                           checkOffOnly,checkHideChildren,
     *                                          radioFolder --&gt;
     *   &lt;bgColor&gt;ffffffff&lt;/bgColor&gt;        &lt;!-- kml:color --&gt;
     *   &lt;ItemIcon&gt;                         &lt;!-- 0 or more ItemIcon elements --&gt;
     *     &lt;state&gt;open&lt;/state&gt;   
     *       &lt;!-- kml:itemIconModeEnum:open, closed, error, fetching0, fetching1, <em>or</em> fetching2 --&gt;
     *     &lt;href&gt;...&lt;/href&gt;                 &lt;!-- anyURI --&gt;
     *   &lt;/ItemIcon&gt;
     * <strong>&lt;/ListStyle&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <Object>
     * 
     * Contained By: 
     * @see: <Style>
     * 
     * 
     * 
     */
    @XmlElement(name = "ListStyle")
    protected ListStyle listStyle;
    @XmlElement(name = "StyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> styleSimpleExtension;
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
    @XmlElement(name = "StyleObjectExtensionGroup")
    protected List<AbstractObject> styleObjectExtension;

    public Style() {
        super();
    }

    /**
     * @see iconStyle
     * 
     * @return
     *     possible object is
     *     {@link IconStyle}
     *     
     */
    public IconStyle getIconStyle() {
        return iconStyle;
    }

    /**
     * @see iconStyle
     * 
     * @param value
     *     allowed object is
     *     {@link IconStyle}
     *     
     */
    public void setIconStyle(IconStyle value) {
        this.iconStyle = value;
    }

    /**
     * @see labelStyle
     * 
     * @return
     *     possible object is
     *     {@link LabelStyle}
     *     
     */
    public LabelStyle getLabelStyle() {
        return labelStyle;
    }

    /**
     * @see labelStyle
     * 
     * @param value
     *     allowed object is
     *     {@link LabelStyle}
     *     
     */
    public void setLabelStyle(LabelStyle value) {
        this.labelStyle = value;
    }

    /**
     * @see lineStyle
     * 
     * @return
     *     possible object is
     *     {@link LineStyle}
     *     
     */
    public LineStyle getLineStyle() {
        return lineStyle;
    }

    /**
     * @see lineStyle
     * 
     * @param value
     *     allowed object is
     *     {@link LineStyle}
     *     
     */
    public void setLineStyle(LineStyle value) {
        this.lineStyle = value;
    }

    /**
     * @see polyStyle
     * 
     * @return
     *     possible object is
     *     {@link PolyStyle}
     *     
     */
    public PolyStyle getPolyStyle() {
        return polyStyle;
    }

    /**
     * @see polyStyle
     * 
     * @param value
     *     allowed object is
     *     {@link PolyStyle}
     *     
     */
    public void setPolyStyle(PolyStyle value) {
        this.polyStyle = value;
    }

    /**
     * @see balloonStyle
     * 
     * @return
     *     possible object is
     *     {@link BalloonStyle}
     *     
     */
    public BalloonStyle getBalloonStyle() {
        return balloonStyle;
    }

    /**
     * @see balloonStyle
     * 
     * @param value
     *     allowed object is
     *     {@link BalloonStyle}
     *     
     */
    public void setBalloonStyle(BalloonStyle value) {
        this.balloonStyle = value;
    }

    /**
     * @see listStyle
     * 
     * @return
     *     possible object is
     *     {@link ListStyle}
     *     
     */
    public ListStyle getListStyle() {
        return listStyle;
    }

    /**
     * @see listStyle
     * 
     * @param value
     *     allowed object is
     *     {@link ListStyle}
     *     
     */
    public void setListStyle(ListStyle value) {
        this.listStyle = value;
    }

    /**
     * @see styleSimpleExtension
     * 
     */
    public List<Object> getStyleSimpleExtension() {
        if (styleSimpleExtension == null) {
            styleSimpleExtension = new ArrayList<Object>();
        }
        return this.styleSimpleExtension;
    }

    /**
     * @see styleObjectExtension
     * 
     */
    public List<AbstractObject> getStyleObjectExtension() {
        if (styleObjectExtension == null) {
            styleObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.styleObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((iconStyle == null)? 0 :iconStyle.hashCode()));
        result = ((prime*result)+((labelStyle == null)? 0 :labelStyle.hashCode()));
        result = ((prime*result)+((lineStyle == null)? 0 :lineStyle.hashCode()));
        result = ((prime*result)+((polyStyle == null)? 0 :polyStyle.hashCode()));
        result = ((prime*result)+((balloonStyle == null)? 0 :balloonStyle.hashCode()));
        result = ((prime*result)+((listStyle == null)? 0 :listStyle.hashCode()));
        result = ((prime*result)+((styleSimpleExtension == null)? 0 :styleSimpleExtension.hashCode()));
        result = ((prime*result)+((styleObjectExtension == null)? 0 :styleObjectExtension.hashCode()));
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
        if ((obj instanceof Style) == false) {
            return false;
        }
        Style other = ((Style) obj);
        if (iconStyle == null) {
            if (other.iconStyle!= null) {
                return false;
            }
        } else {
            if (iconStyle.equals(other.iconStyle) == false) {
                return false;
            }
        }
        if (labelStyle == null) {
            if (other.labelStyle!= null) {
                return false;
            }
        } else {
            if (labelStyle.equals(other.labelStyle) == false) {
                return false;
            }
        }
        if (lineStyle == null) {
            if (other.lineStyle!= null) {
                return false;
            }
        } else {
            if (lineStyle.equals(other.lineStyle) == false) {
                return false;
            }
        }
        if (polyStyle == null) {
            if (other.polyStyle!= null) {
                return false;
            }
        } else {
            if (polyStyle.equals(other.polyStyle) == false) {
                return false;
            }
        }
        if (balloonStyle == null) {
            if (other.balloonStyle!= null) {
                return false;
            }
        } else {
            if (balloonStyle.equals(other.balloonStyle) == false) {
                return false;
            }
        }
        if (listStyle == null) {
            if (other.listStyle!= null) {
                return false;
            }
        } else {
            if (listStyle.equals(other.listStyle) == false) {
                return false;
            }
        }
        if (styleSimpleExtension == null) {
            if (other.styleSimpleExtension!= null) {
                return false;
            }
        } else {
            if (styleSimpleExtension.equals(other.styleSimpleExtension) == false) {
                return false;
            }
        }
        if (styleObjectExtension == null) {
            if (other.styleObjectExtension!= null) {
                return false;
            }
        } else {
            if (styleObjectExtension.equals(other.styleObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link IconStyle} and set it to iconStyle.
     * 
     * This method is a short version for:
     * <code>
     * IconStyle iconStyle = new IconStyle();
     * this.setIconStyle(iconStyle); </code>
     * 
     * 
     */
    public IconStyle createAndSetIconStyle() {
        IconStyle newValue = new IconStyle();
        this.setIconStyle(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link LabelStyle} and set it to labelStyle.
     * 
     * This method is a short version for:
     * <code>
     * LabelStyle labelStyle = new LabelStyle();
     * this.setLabelStyle(labelStyle); </code>
     * 
     * 
     */
    public LabelStyle createAndSetLabelStyle() {
        LabelStyle newValue = new LabelStyle();
        this.setLabelStyle(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link LineStyle} and set it to lineStyle.
     * 
     * This method is a short version for:
     * <code>
     * LineStyle lineStyle = new LineStyle();
     * this.setLineStyle(lineStyle); </code>
     * 
     * 
     */
    public LineStyle createAndSetLineStyle() {
        LineStyle newValue = new LineStyle();
        this.setLineStyle(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PolyStyle} and set it to polyStyle.
     * 
     * This method is a short version for:
     * <code>
     * PolyStyle polyStyle = new PolyStyle();
     * this.setPolyStyle(polyStyle); </code>
     * 
     * 
     */
    public PolyStyle createAndSetPolyStyle() {
        PolyStyle newValue = new PolyStyle();
        this.setPolyStyle(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link BalloonStyle} and set it to balloonStyle.
     * 
     * This method is a short version for:
     * <code>
     * BalloonStyle balloonStyle = new BalloonStyle();
     * this.setBalloonStyle(balloonStyle); </code>
     * 
     * 
     */
    public BalloonStyle createAndSetBalloonStyle() {
        BalloonStyle newValue = new BalloonStyle();
        this.setBalloonStyle(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ListStyle} and set it to listStyle.
     * 
     * This method is a short version for:
     * <code>
     * ListStyle listStyle = new ListStyle();
     * this.setListStyle(listStyle); </code>
     * 
     * 
     */
    public ListStyle createAndSetListStyle() {
        ListStyle newValue = new ListStyle();
        this.setListStyle(newValue);
        return newValue;
    }

    /**
     * @see styleSimpleExtension
     * 
     * @param styleSimpleExtension
     */
    public void setStyleSimpleExtension(final List<Object> styleSimpleExtension) {
        this.styleSimpleExtension = styleSimpleExtension;
    }

    /**
     * add a value to the styleSimpleExtension property collection
     * 
     * @param styleSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Style addToStyleSimpleExtension(final Object styleSimpleExtension) {
        this.getStyleSimpleExtension().add(styleSimpleExtension);
        return this;
    }

    /**
     * @see styleObjectExtension
     * 
     * @param styleObjectExtension
     */
    public void setStyleObjectExtension(final List<AbstractObject> styleObjectExtension) {
        this.styleObjectExtension = styleObjectExtension;
    }

    /**
     * add a value to the styleObjectExtension property collection
     * 
     * @param styleObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Style addToStyleObjectExtension(final AbstractObject styleObjectExtension) {
        this.getStyleObjectExtension().add(styleObjectExtension);
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
    public Style addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see styleSelectorSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setStyleSelectorSimpleExtension(final List<Object> styleSelectorSimpleExtension) {
        super.setStyleSelectorSimpleExtension(styleSelectorSimpleExtension);
    }

    @Obvious
    @Override
    public Style addToStyleSelectorSimpleExtension(final Object styleSelectorSimpleExtension) {
        super.getStyleSelectorSimpleExtension().add(styleSelectorSimpleExtension);
        return this;
    }

    /**
     * @see styleSelectorObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setStyleSelectorObjectExtension(final List<AbstractObject> styleSelectorObjectExtension) {
        super.setStyleSelectorObjectExtension(styleSelectorObjectExtension);
    }

    @Obvious
    @Override
    public Style addToStyleSelectorObjectExtension(final AbstractObject styleSelectorObjectExtension) {
        super.getStyleSelectorObjectExtension().add(styleSelectorObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setIconStyle(IconStyle)
     * 
     * @param iconStyle
     *     required parameter
     */
    public Style withIconStyle(final IconStyle iconStyle) {
        this.setIconStyle(iconStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setLabelStyle(LabelStyle)
     * 
     * @param labelStyle
     *     required parameter
     */
    public Style withLabelStyle(final LabelStyle labelStyle) {
        this.setLabelStyle(labelStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setLineStyle(LineStyle)
     * 
     * @param lineStyle
     *     required parameter
     */
    public Style withLineStyle(final LineStyle lineStyle) {
        this.setLineStyle(lineStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setPolyStyle(PolyStyle)
     * 
     * @param polyStyle
     *     required parameter
     */
    public Style withPolyStyle(final PolyStyle polyStyle) {
        this.setPolyStyle(polyStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setBalloonStyle(BalloonStyle)
     * 
     * @param balloonStyle
     *     required parameter
     */
    public Style withBalloonStyle(final BalloonStyle balloonStyle) {
        this.setBalloonStyle(balloonStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setListStyle(ListStyle)
     * 
     * @param listStyle
     *     required parameter
     */
    public Style withListStyle(final ListStyle listStyle) {
        this.setListStyle(listStyle);
        return this;
    }

    /**
     * fluent setter
     * @see #setStyleSimpleExtension(List<Object>)
     * 
     * @param styleSimpleExtension
     *     required parameter
     */
    public Style withStyleSimpleExtension(final List<Object> styleSimpleExtension) {
        this.setStyleSimpleExtension(styleSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setStyleObjectExtension(List<AbstractObject>)
     * 
     * @param styleObjectExtension
     *     required parameter
     */
    public Style withStyleObjectExtension(final List<AbstractObject> styleObjectExtension) {
        this.setStyleObjectExtension(styleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Style withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Style withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Style withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public Style withStyleSelectorSimpleExtension(final List<Object> styleSelectorSimpleExtension) {
        super.withStyleSelectorSimpleExtension(styleSelectorSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Style withStyleSelectorObjectExtension(final List<AbstractObject> styleSelectorObjectExtension) {
        super.withStyleSelectorObjectExtension(styleSelectorObjectExtension);
        return this;
    }

    @Override
    public Style clone() {
        Style copy;
        copy = ((Style) super.clone());
        copy.iconStyle = ((iconStyle == null)?null:((IconStyle) iconStyle.clone()));
        copy.labelStyle = ((labelStyle == null)?null:((LabelStyle) labelStyle.clone()));
        copy.lineStyle = ((lineStyle == null)?null:((LineStyle) lineStyle.clone()));
        copy.polyStyle = ((polyStyle == null)?null:((PolyStyle) polyStyle.clone()));
        copy.balloonStyle = ((balloonStyle == null)?null:((BalloonStyle) balloonStyle.clone()));
        copy.listStyle = ((listStyle == null)?null:((ListStyle) listStyle.clone()));
        copy.styleSimpleExtension = new ArrayList<Object>((getStyleSimpleExtension().size()));
        for (Object iter: styleSimpleExtension) {
            copy.styleSimpleExtension.add(iter);
        }
        copy.styleObjectExtension = new ArrayList<AbstractObject>((getStyleObjectExtension().size()));
        for (AbstractObject iter: styleObjectExtension) {
            copy.styleObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
