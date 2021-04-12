
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
 * <alias> contains a mapping from a <sourcehref> to a <targethref>:
 * <p>
 * <targetHref> Specifies the texture file to be fetched by Google Earth. This reference 
 * can be a relative reference to an image file within the .kmz archive, or it can 
 * be an absolute reference to the file (for example, a URL). <sourceHref> Is the path 
 * specified for the texture file in the Collada .dae file. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AliasType", propOrder = {
    "targetHref",
    "sourceHref",
    "aliasSimpleExtension",
    "aliasObjectExtension"
})
@XmlRootElement(name = "Alias", namespace = "http://www.opengis.net/kml/2.2")
public class Alias
    extends AbstractObject
    implements Cloneable
{

    /**
     * <targethref> (required)
     * <p>
     * A URL that specifies the .kml or .kmz file whose data (within Google Earth) is to 
     * be modified by an <Update> element. This KML file must already have been loaded 
     * via a <NetworkLink>. In that file, the element to be modified must already have 
     * an explicit id attribute defined for it. 
     * </p>
     * <p>
     * Specifies the texture file to be fetched by Google Earth. This reference can be 
     * a relative reference to an image file within the .kmz archive, or it can be an absolute 
     * reference to the file (for example, a URL). 
     * </p>
     * 
     * 
     * 
     */
    @XmlSchemaType(name = "anyURI")
    protected String targetHref;
    /**
     * <sourcehref>
     * <p>
     * <targetHref> Specifies the texture file to be fetched by Google Earth. This reference 
     * can be a relative reference to an image file within the .kmz archive, or it can 
     * be an absolute reference to the file (for example, a URL). <sourceHref> Is the path 
     * specified for the texture file in the Collada .dae file. 
     * </p>
     * <p>
     * In Google Earth, if this mapping is not supplied, the following rules are used to 
     * locate the textures referenced in the Collada (.dae) file: No path: If the texture 
     * name does not include a path, Google Earth looks for the texture in the same directory 
     * as the .dae file that references it. Relative path: If the texture name includes 
     * a relative path (for example, ../images/mytexture.jpg), Google Earth interprets 
     * the path as being relative to the .dae file that references it. Absolute path: If 
     * the texture name is an absolute path (c:\mytexture.jpg) or a network path (for example, 
     * http://myserver.com/mytexture.jpg), Google Earth looks for the file in the specified 
     * location, regardless of where the .dae file is located. 
     * </p>
     * <p>
     * Is the path specified for the texture file in the Collada .dae file. 
     * </p>
     * <p>
     * Is the path specified for the texture file in the Collada .dae file. 
     * </p>
     * 
     * 
     * 
     */
    @XmlSchemaType(name = "anyURI")
    protected String sourceHref;
    @XmlElement(name = "AliasSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> aliasSimpleExtension;
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
    @XmlElement(name = "AliasObjectExtensionGroup")
    protected List<AbstractObject> aliasObjectExtension;

    public Alias() {
        super();
    }

    /**
     * @see targetHref
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getTargetHref() {
        return targetHref;
    }

    /**
     * @see targetHref
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setTargetHref(String value) {
        this.targetHref = value;
    }

    /**
     * @see sourceHref
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getSourceHref() {
        return sourceHref;
    }

    /**
     * @see sourceHref
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setSourceHref(String value) {
        this.sourceHref = value;
    }

    /**
     * @see aliasSimpleExtension
     * 
     */
    public List<Object> getAliasSimpleExtension() {
        if (aliasSimpleExtension == null) {
            aliasSimpleExtension = new ArrayList<Object>();
        }
        return this.aliasSimpleExtension;
    }

    /**
     * @see aliasObjectExtension
     * 
     */
    public List<AbstractObject> getAliasObjectExtension() {
        if (aliasObjectExtension == null) {
            aliasObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.aliasObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((targetHref == null)? 0 :targetHref.hashCode()));
        result = ((prime*result)+((sourceHref == null)? 0 :sourceHref.hashCode()));
        result = ((prime*result)+((aliasSimpleExtension == null)? 0 :aliasSimpleExtension.hashCode()));
        result = ((prime*result)+((aliasObjectExtension == null)? 0 :aliasObjectExtension.hashCode()));
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
        if ((obj instanceof Alias) == false) {
            return false;
        }
        Alias other = ((Alias) obj);
        if (targetHref == null) {
            if (other.targetHref!= null) {
                return false;
            }
        } else {
            if (targetHref.equals(other.targetHref) == false) {
                return false;
            }
        }
        if (sourceHref == null) {
            if (other.sourceHref!= null) {
                return false;
            }
        } else {
            if (sourceHref.equals(other.sourceHref) == false) {
                return false;
            }
        }
        if (aliasSimpleExtension == null) {
            if (other.aliasSimpleExtension!= null) {
                return false;
            }
        } else {
            if (aliasSimpleExtension.equals(other.aliasSimpleExtension) == false) {
                return false;
            }
        }
        if (aliasObjectExtension == null) {
            if (other.aliasObjectExtension!= null) {
                return false;
            }
        } else {
            if (aliasObjectExtension.equals(other.aliasObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see aliasSimpleExtension
     * 
     * @param aliasSimpleExtension
     */
    public void setAliasSimpleExtension(final List<Object> aliasSimpleExtension) {
        this.aliasSimpleExtension = aliasSimpleExtension;
    }

    /**
     * add a value to the aliasSimpleExtension property collection
     * 
     * @param aliasSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Alias addToAliasSimpleExtension(final Object aliasSimpleExtension) {
        this.getAliasSimpleExtension().add(aliasSimpleExtension);
        return this;
    }

    /**
     * @see aliasObjectExtension
     * 
     * @param aliasObjectExtension
     */
    public void setAliasObjectExtension(final List<AbstractObject> aliasObjectExtension) {
        this.aliasObjectExtension = aliasObjectExtension;
    }

    /**
     * add a value to the aliasObjectExtension property collection
     * 
     * @param aliasObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Alias addToAliasObjectExtension(final AbstractObject aliasObjectExtension) {
        this.getAliasObjectExtension().add(aliasObjectExtension);
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
    public Alias addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTargetHref(String)
     * 
     * @param targetHref
     *     required parameter
     */
    public Alias withTargetHref(final String targetHref) {
        this.setTargetHref(targetHref);
        return this;
    }

    /**
     * fluent setter
     * @see #setSourceHref(String)
     * 
     * @param sourceHref
     *     required parameter
     */
    public Alias withSourceHref(final String sourceHref) {
        this.setSourceHref(sourceHref);
        return this;
    }

    /**
     * fluent setter
     * @see #setAliasSimpleExtension(List<Object>)
     * 
     * @param aliasSimpleExtension
     *     required parameter
     */
    public Alias withAliasSimpleExtension(final List<Object> aliasSimpleExtension) {
        this.setAliasSimpleExtension(aliasSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setAliasObjectExtension(List<AbstractObject>)
     * 
     * @param aliasObjectExtension
     *     required parameter
     */
    public Alias withAliasObjectExtension(final List<AbstractObject> aliasObjectExtension) {
        this.setAliasObjectExtension(aliasObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Alias withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Alias withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Alias withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public Alias clone() {
        Alias copy;
        copy = ((Alias) super.clone());
        copy.aliasSimpleExtension = new ArrayList<Object>((getAliasSimpleExtension().size()));
        for (Object iter: aliasSimpleExtension) {
            copy.aliasSimpleExtension.add(iter);
        }
        copy.aliasObjectExtension = new ArrayList<AbstractObject>((getAliasObjectExtension().size()));
        for (AbstractObject iter: aliasObjectExtension) {
            copy.aliasObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
