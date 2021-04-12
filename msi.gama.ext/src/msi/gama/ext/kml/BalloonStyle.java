
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import msi.gama.ext.kml.annotations.Obvious;


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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BalloonStyleType", propOrder = {
    "color",
    "bgColor",
    "textColor",
    "text",
    "displayMode",
    "balloonStyleSimpleExtension",
    "balloonStyleObjectExtension"
})
@XmlRootElement(name = "BalloonStyle", namespace = "http://www.opengis.net/kml/2.2")
public class BalloonStyle
    extends SubStyle
    implements Cloneable
{

    /**
     * <color>
     * <p>
     * Color and opacity (alpha) values are expressed in hexadecimal notation. The range 
     * of values for any one color is 0 to 255 (00 to ff). For alpha, 00 is fully transparent 
     * and ff is fully opaque. The order of expression is aabbggrr, where aa=alpha (00 
     * to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For example, 
     * if you want to apply a blue color with 50 percent opacity to an overlay, you would 
     * specify the following: <color>7fff0000</color>, where alpha=0x7f, blue=0xff, green=0x00, 
     * and red=0x00. 
     * </p>
     * <p>
     * Color values are expressed in hexadecimal notation, including opacity (alpha) values. 
     * The order of expression is alpha, blue, green, red (aabbggrr). The range of values 
     * for any one color is 0 to 255 (00 to ff). For opacity, 00 is fully transparent and 
     * ff is fully opaque. For example, if you want to apply a blue color with 50 percent 
     * opacity to an overlay, you would specify the following: <color>7fff0000</color> 
     * </p>
     * <p>
     * Note: The <geomColor> element has been deprecated. Use <color> instead. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "ffffffff")
    
    @Deprecated
    protected String color;
    /**
     * <bgcolor>
     * <p>
     * Background color for the Snippet. Color and opacity values are expressed in hexadecimal 
     * notation. The range of values for any one color is 0 to 255 (00 to ff). For alpha, 
     *  00 is fully transparent and ff is fully opaque. The order of expression is aabbggrr, 
     * where aa=alpha (00 to ff); bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to 
     * ff). For example, if you want to apply a blue color with 50 percent opacity to an 
     * overlay, you would specify the following: <color>7fff0000</color>, where alpha=0x7f, 
     * blue=0xff, green=0x00, and red=0x00. 
     * </p>
     * <p>
     * Background color of the balloon (optional). Color and opacity (alpha) values are 
     * expressed in hexadecimal notation. The range of values for any one color is 0 to 
     *  255 (00 to ff). The order of expression is aabbggrr, where aa=alpha (00 to ff); 
     * bb=blue (00 to ff); gg=green (00 to ff); rr=red (00 to ff). For alpha, 00 is fully 
     * transparent and ff is fully opaque. For example, if you want to apply a blue color 
     * with 50 percent opacity to an overlay, you would specify the following: <bgColor>7fff0000</bgColor>, 
     * where alpha=0x7f, blue=0xff, green=0x00, and red=0x00. The default is opaque white 
     * (ffffffff). 
     * </p>
     * <p>
     * Note: The use of the <color> element within <BalloonStyle> has been deprecated. 
     * Use <bgColor> instead. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "ffffffff")
    
    protected String bgColor;
    /**
     * <textcolor>
     * <p>
     * Foreground color for text. The default is black (ff000000). 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "ff000000")
    
    protected String textColor;
    /**
     * <text>
     * <p>
     * For example, in the following KML excerpt, $[name] and $[description] fields will 
     * be replaced by the <name> and <description> fields found in the Feature elements 
     * that use this BalloonStyle: <text>This is $[name], whose description is:<br/>$[description]</text> 
     * </p>
     * <p>
     * Text displayed in the balloon. If no text is specified, Google Earth draws the default 
     * balloon (with the Feature <name> in boldface, the Feature <description>, links for 
     * driving directions, a white background, and a tail that is attached to the point 
     * coordinates of the Feature, if specified). 
     * </p>
     * <p>
     * You can add entities to the <text> tag using the following format to refer to a 
     * child element of Feature: $[name], $[description], $[address], $[id], $[Snippet]. 
     * Google Earth looks in the current Feature for the corresponding string entity and 
     * substitutes that information in the balloon. To include To here - From here driving 
     * directions in the balloon, use the $[geDirections] tag. To prevent the driving directions 
     * links from appearing in a balloon, include the <text> element with some content, 
     * or with $[description] to substitute the basic Feature <description>. 
     * </p>
     * 
     * 
     * 
     */
    protected String text;
    /**
     * DisplayMode
     * <p>
     * default, hide 
     * </p>
     * 
     * See Also: 
     * See <BalloonStyle>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "default")
    protected DisplayMode displayMode;
    @XmlElement(name = "BalloonStyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> balloonStyleSimpleExtension;
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
    @XmlElement(name = "BalloonStyleObjectExtensionGroup")
    protected List<AbstractObject> balloonStyleObjectExtension;

    public BalloonStyle() {
        super();
    }

    /**
     * @see color
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getColor() {
        return color;
    }

    /**
     * @see color
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setColor(String value) {
        this.color =  value;
    }

    /**
     * @see bgColor
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getBgColor() {
        return bgColor;
    }

    /**
     * @see bgColor
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setBgColor(String value) {
        this.bgColor =  value;
    }

    /**
     * @see textColor
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getTextColor() {
        return textColor;
    }

    /**
     * @see textColor
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setTextColor(String value) {
        this.textColor =  value;
    }

    /**
     * @see text
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getText() {
        return text;
    }

    /**
     * @see text
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setText(String value) {
        this.text = value;
    }

    /**
     * @see displayMode
     * 
     * @return
     *     possible object is
     *     {@link DisplayMode}
     *     
     */
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    /**
     * @see displayMode
     * 
     * @param value
     *     allowed object is
     *     {@link DisplayMode}
     *     
     */
    public void setDisplayMode(DisplayMode value) {
        this.displayMode = value;
    }

    /**
     * @see balloonStyleSimpleExtension
     * 
     */
    public List<Object> getBalloonStyleSimpleExtension() {
        if (balloonStyleSimpleExtension == null) {
            balloonStyleSimpleExtension = new ArrayList<Object>();
        }
        return this.balloonStyleSimpleExtension;
    }

    /**
     * @see balloonStyleObjectExtension
     * 
     */
    public List<AbstractObject> getBalloonStyleObjectExtension() {
        if (balloonStyleObjectExtension == null) {
            balloonStyleObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.balloonStyleObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((color == null)? 0 :color.hashCode()));
        result = ((prime*result)+((bgColor == null)? 0 :bgColor.hashCode()));
        result = ((prime*result)+((textColor == null)? 0 :textColor.hashCode()));
        result = ((prime*result)+((text == null)? 0 :text.hashCode()));
        result = ((prime*result)+((displayMode == null)? 0 :displayMode.hashCode()));
        result = ((prime*result)+((balloonStyleSimpleExtension == null)? 0 :balloonStyleSimpleExtension.hashCode()));
        result = ((prime*result)+((balloonStyleObjectExtension == null)? 0 :balloonStyleObjectExtension.hashCode()));
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
        if ((obj instanceof BalloonStyle) == false) {
            return false;
        }
        BalloonStyle other = ((BalloonStyle) obj);
        if (color == null) {
            if (other.color!= null) {
                return false;
            }
        } else {
            if (color.equals(other.color) == false) {
                return false;
            }
        }
        if (bgColor == null) {
            if (other.bgColor!= null) {
                return false;
            }
        } else {
            if (bgColor.equals(other.bgColor) == false) {
                return false;
            }
        }
        if (textColor == null) {
            if (other.textColor!= null) {
                return false;
            }
        } else {
            if (textColor.equals(other.textColor) == false) {
                return false;
            }
        }
        if (text == null) {
            if (other.text!= null) {
                return false;
            }
        } else {
            if (text.equals(other.text) == false) {
                return false;
            }
        }
        if (displayMode == null) {
            if (other.displayMode!= null) {
                return false;
            }
        } else {
            if (displayMode.equals(other.displayMode) == false) {
                return false;
            }
        }
        if (balloonStyleSimpleExtension == null) {
            if (other.balloonStyleSimpleExtension!= null) {
                return false;
            }
        } else {
            if (balloonStyleSimpleExtension.equals(other.balloonStyleSimpleExtension) == false) {
                return false;
            }
        }
        if (balloonStyleObjectExtension == null) {
            if (other.balloonStyleObjectExtension!= null) {
                return false;
            }
        } else {
            if (balloonStyleObjectExtension.equals(other.balloonStyleObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see balloonStyleSimpleExtension
     * 
     * @param balloonStyleSimpleExtension
     */
    public void setBalloonStyleSimpleExtension(final List<Object> balloonStyleSimpleExtension) {
        this.balloonStyleSimpleExtension = balloonStyleSimpleExtension;
    }

    /**
     * add a value to the balloonStyleSimpleExtension property collection
     * 
     * @param balloonStyleSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public BalloonStyle addToBalloonStyleSimpleExtension(final Object balloonStyleSimpleExtension) {
        this.getBalloonStyleSimpleExtension().add(balloonStyleSimpleExtension);
        return this;
    }

    /**
     * @see balloonStyleObjectExtension
     * 
     * @param balloonStyleObjectExtension
     */
    public void setBalloonStyleObjectExtension(final List<AbstractObject> balloonStyleObjectExtension) {
        this.balloonStyleObjectExtension = balloonStyleObjectExtension;
    }

    /**
     * add a value to the balloonStyleObjectExtension property collection
     * 
     * @param balloonStyleObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public BalloonStyle addToBalloonStyleObjectExtension(final AbstractObject balloonStyleObjectExtension) {
        this.getBalloonStyleObjectExtension().add(balloonStyleObjectExtension);
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
    public BalloonStyle addToObjectSimpleExtension(final Object objectSimpleExtension) {
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
    public BalloonStyle addToSubStyleSimpleExtension(final Object subStyleSimpleExtension) {
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
    public BalloonStyle addToSubStyleObjectExtension(final AbstractObject subStyleObjectExtension) {
        super.getSubStyleObjectExtension().add(subStyleObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setColor(String)
     * 
     * @param color
     *     required parameter
     */
    public BalloonStyle withColor(final String color) {
        this.setColor(color);
        return this;
    }

    /**
     * fluent setter
     * @see #setBgColor(String)
     * 
     * @param bgColor
     *     required parameter
     */
    public BalloonStyle withBgColor(final String bgColor) {
        this.setBgColor(bgColor);
        return this;
    }

    /**
     * fluent setter
     * @see #setTextColor(String)
     * 
     * @param textColor
     *     required parameter
     */
    public BalloonStyle withTextColor(final String textColor) {
        this.setTextColor(textColor);
        return this;
    }

    /**
     * fluent setter
     * @see #setText(String)
     * 
     * @param text
     *     required parameter
     */
    public BalloonStyle withText(final String text) {
        this.setText(text);
        return this;
    }

    /**
     * fluent setter
     * @see #setDisplayMode(DisplayMode)
     * 
     * @param displayMode
     *     required parameter
     */
    public BalloonStyle withDisplayMode(final DisplayMode displayMode) {
        this.setDisplayMode(displayMode);
        return this;
    }

    /**
     * fluent setter
     * @see #setBalloonStyleSimpleExtension(List<Object>)
     * 
     * @param balloonStyleSimpleExtension
     *     required parameter
     */
    public BalloonStyle withBalloonStyleSimpleExtension(final List<Object> balloonStyleSimpleExtension) {
        this.setBalloonStyleSimpleExtension(balloonStyleSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setBalloonStyleObjectExtension(List<AbstractObject>)
     * 
     * @param balloonStyleObjectExtension
     *     required parameter
     */
    public BalloonStyle withBalloonStyleObjectExtension(final List<AbstractObject> balloonStyleObjectExtension) {
        this.setBalloonStyleObjectExtension(balloonStyleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public BalloonStyle withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public BalloonStyle withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public BalloonStyle withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public BalloonStyle withSubStyleSimpleExtension(final List<Object> subStyleSimpleExtension) {
        super.withSubStyleSimpleExtension(subStyleSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public BalloonStyle withSubStyleObjectExtension(final List<AbstractObject> subStyleObjectExtension) {
        super.withSubStyleObjectExtension(subStyleObjectExtension);
        return this;
    }

    @Override
    public BalloonStyle clone() {
        BalloonStyle copy;
        copy = ((BalloonStyle) super.clone());
        copy.balloonStyleSimpleExtension = new ArrayList<Object>((getBalloonStyleSimpleExtension().size()));
        for (Object iter: balloonStyleSimpleExtension) {
            copy.balloonStyleSimpleExtension.add(iter);
        }
        copy.balloonStyleObjectExtension = new ArrayList<AbstractObject>((getBalloonStyleObjectExtension().size()));
        for (AbstractObject iter: balloonStyleObjectExtension) {
            copy.balloonStyleObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
