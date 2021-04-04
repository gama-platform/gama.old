
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LabelStyleType", propOrder = {
    "scale",
    "labelStyleSimpleExtension",
    "labelStyleObjectExtension"
})
@XmlRootElement(name = "LabelStyle", namespace = "http://www.opengis.net/kml/2.2")
public class LabelStyle
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
    @XmlElement(name = "LabelStyleSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> labelStyleSimpleExtension;
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
    @XmlElement(name = "LabelStyleObjectExtensionGroup")
    protected List<AbstractObject> labelStyleObjectExtension;

    public LabelStyle() {
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
     * @see labelStyleSimpleExtension
     * 
     */
    public List<Object> getLabelStyleSimpleExtension() {
        if (labelStyleSimpleExtension == null) {
            labelStyleSimpleExtension = new ArrayList<Object>();
        }
        return this.labelStyleSimpleExtension;
    }

    /**
     * @see labelStyleObjectExtension
     * 
     */
    public List<AbstractObject> getLabelStyleObjectExtension() {
        if (labelStyleObjectExtension == null) {
            labelStyleObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.labelStyleObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(scale);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((labelStyleSimpleExtension == null)? 0 :labelStyleSimpleExtension.hashCode()));
        result = ((prime*result)+((labelStyleObjectExtension == null)? 0 :labelStyleObjectExtension.hashCode()));
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
        if ((obj instanceof LabelStyle) == false) {
            return false;
        }
        LabelStyle other = ((LabelStyle) obj);
        if (scale!= other.scale) {
            return false;
        }
        if (labelStyleSimpleExtension == null) {
            if (other.labelStyleSimpleExtension!= null) {
                return false;
            }
        } else {
            if (labelStyleSimpleExtension.equals(other.labelStyleSimpleExtension) == false) {
                return false;
            }
        }
        if (labelStyleObjectExtension == null) {
            if (other.labelStyleObjectExtension!= null) {
                return false;
            }
        } else {
            if (labelStyleObjectExtension.equals(other.labelStyleObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see labelStyleSimpleExtension
     * 
     * @param labelStyleSimpleExtension
     */
    public void setLabelStyleSimpleExtension(final List<Object> labelStyleSimpleExtension) {
        this.labelStyleSimpleExtension = labelStyleSimpleExtension;
    }

    /**
     * add a value to the labelStyleSimpleExtension property collection
     * 
     * @param labelStyleSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LabelStyle addToLabelStyleSimpleExtension(final Object labelStyleSimpleExtension) {
        this.getLabelStyleSimpleExtension().add(labelStyleSimpleExtension);
        return this;
    }

    /**
     * @see labelStyleObjectExtension
     * 
     * @param labelStyleObjectExtension
     */
    public void setLabelStyleObjectExtension(final List<AbstractObject> labelStyleObjectExtension) {
        this.labelStyleObjectExtension = labelStyleObjectExtension;
    }

    /**
     * add a value to the labelStyleObjectExtension property collection
     * 
     * @param labelStyleObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LabelStyle addToLabelStyleObjectExtension(final AbstractObject labelStyleObjectExtension) {
        this.getLabelStyleObjectExtension().add(labelStyleObjectExtension);
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
    public LabelStyle addToObjectSimpleExtension(final Object objectSimpleExtension) {
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
    public LabelStyle addToSubStyleSimpleExtension(final Object subStyleSimpleExtension) {
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
    public LabelStyle addToSubStyleObjectExtension(final AbstractObject subStyleObjectExtension) {
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
    public LabelStyle addToColorStyleSimpleExtension(final Object colorStyleSimpleExtension) {
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
    public LabelStyle addToColorStyleObjectExtension(final AbstractObject colorStyleObjectExtension) {
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
    public LabelStyle withScale(final double scale) {
        this.setScale(scale);
        return this;
    }

    /**
     * fluent setter
     * @see #setLabelStyleSimpleExtension(List<Object>)
     * 
     * @param labelStyleSimpleExtension
     *     required parameter
     */
    public LabelStyle withLabelStyleSimpleExtension(final List<Object> labelStyleSimpleExtension) {
        this.setLabelStyleSimpleExtension(labelStyleSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setLabelStyleObjectExtension(List<AbstractObject>)
     * 
     * @param labelStyleObjectExtension
     *     required parameter
     */
    public LabelStyle withLabelStyleObjectExtension(final List<AbstractObject> labelStyleObjectExtension) {
        this.setLabelStyleObjectExtension(labelStyleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withSubStyleSimpleExtension(final List<Object> subStyleSimpleExtension) {
        super.withSubStyleSimpleExtension(subStyleSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withSubStyleObjectExtension(final List<AbstractObject> subStyleObjectExtension) {
        super.withSubStyleObjectExtension(subStyleObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withColor(final String color) {
        super.withColor(color);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withColorMode(final ColorMode colorMode) {
        super.withColorMode(colorMode);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withColorStyleSimpleExtension(final List<Object> colorStyleSimpleExtension) {
        super.withColorStyleSimpleExtension(colorStyleSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public LabelStyle withColorStyleObjectExtension(final List<AbstractObject> colorStyleObjectExtension) {
        super.withColorStyleObjectExtension(colorStyleObjectExtension);
        return this;
    }

    @Override
    public LabelStyle clone() {
        LabelStyle copy;
        copy = ((LabelStyle) super.clone());
        copy.labelStyleSimpleExtension = new ArrayList<Object>((getLabelStyleSimpleExtension().size()));
        for (Object iter: labelStyleSimpleExtension) {
            copy.labelStyleSimpleExtension.add(iter);
        }
        copy.labelStyleObjectExtension = new ArrayList<AbstractObject>((getLabelStyleObjectExtension().size()));
        for (AbstractObject iter: labelStyleObjectExtension) {
            copy.labelStyleObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
