
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
 * <resourcemap>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResourceMapType", propOrder = {
    "alias",
    "resourceMapSimpleExtension",
    "resourceMapObjectExtension"
})
@XmlRootElement(name = "ResourceMap", namespace = "http://www.opengis.net/kml/2.2")
public class ResourceMap
    extends AbstractObject
    implements Cloneable
{

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
    @XmlElement(name = "Alias")
    protected List<Alias> alias;
    @XmlElement(name = "ResourceMapSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> resourceMapSimpleExtension;
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
    @XmlElement(name = "ResourceMapObjectExtensionGroup")
    protected List<AbstractObject> resourceMapObjectExtension;

    public ResourceMap() {
        super();
    }

    /**
     * @see alias
     * 
     */
    public List<Alias> getAlias() {
        if (alias == null) {
            alias = new ArrayList<Alias>();
        }
        return this.alias;
    }

    /**
     * @see resourceMapSimpleExtension
     * 
     */
    public List<Object> getResourceMapSimpleExtension() {
        if (resourceMapSimpleExtension == null) {
            resourceMapSimpleExtension = new ArrayList<Object>();
        }
        return this.resourceMapSimpleExtension;
    }

    /**
     * @see resourceMapObjectExtension
     * 
     */
    public List<AbstractObject> getResourceMapObjectExtension() {
        if (resourceMapObjectExtension == null) {
            resourceMapObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.resourceMapObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((alias == null)? 0 :alias.hashCode()));
        result = ((prime*result)+((resourceMapSimpleExtension == null)? 0 :resourceMapSimpleExtension.hashCode()));
        result = ((prime*result)+((resourceMapObjectExtension == null)? 0 :resourceMapObjectExtension.hashCode()));
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
        if ((obj instanceof ResourceMap) == false) {
            return false;
        }
        ResourceMap other = ((ResourceMap) obj);
        if (alias == null) {
            if (other.alias!= null) {
                return false;
            }
        } else {
            if (alias.equals(other.alias) == false) {
                return false;
            }
        }
        if (resourceMapSimpleExtension == null) {
            if (other.resourceMapSimpleExtension!= null) {
                return false;
            }
        } else {
            if (resourceMapSimpleExtension.equals(other.resourceMapSimpleExtension) == false) {
                return false;
            }
        }
        if (resourceMapObjectExtension == null) {
            if (other.resourceMapObjectExtension!= null) {
                return false;
            }
        } else {
            if (resourceMapObjectExtension.equals(other.resourceMapObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Alias} and adds it to alias.
     * This method is a short version for:
     * <code>
     * Alias alias = new Alias();
     * this.getAlias().add(alias); </code>
     * 
     * 
     */
    public Alias createAndAddAlias() {
        Alias newValue = new Alias();
        this.getAlias().add(newValue);
        return newValue;
    }

    /**
     * @see alias
     * 
     * @param alias
     */
    public void setAlias(final List<Alias> alias) {
        this.alias = alias;
    }

    /**
     * add a value to the alias property collection
     * 
     * @param alias
     *     Objects of the following type are allowed in the list: {@link Alias}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ResourceMap addToAlias(final Alias alias) {
        this.getAlias().add(alias);
        return this;
    }

    /**
     * @see resourceMapSimpleExtension
     * 
     * @param resourceMapSimpleExtension
     */
    public void setResourceMapSimpleExtension(final List<Object> resourceMapSimpleExtension) {
        this.resourceMapSimpleExtension = resourceMapSimpleExtension;
    }

    /**
     * add a value to the resourceMapSimpleExtension property collection
     * 
     * @param resourceMapSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ResourceMap addToResourceMapSimpleExtension(final Object resourceMapSimpleExtension) {
        this.getResourceMapSimpleExtension().add(resourceMapSimpleExtension);
        return this;
    }

    /**
     * @see resourceMapObjectExtension
     * 
     * @param resourceMapObjectExtension
     */
    public void setResourceMapObjectExtension(final List<AbstractObject> resourceMapObjectExtension) {
        this.resourceMapObjectExtension = resourceMapObjectExtension;
    }

    /**
     * add a value to the resourceMapObjectExtension property collection
     * 
     * @param resourceMapObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ResourceMap addToResourceMapObjectExtension(final AbstractObject resourceMapObjectExtension) {
        this.getResourceMapObjectExtension().add(resourceMapObjectExtension);
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
    public ResourceMap addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setAlias(List<Alias>)
     * 
     * @param alias
     *     required parameter
     */
    public ResourceMap withAlias(final List<Alias> alias) {
        this.setAlias(alias);
        return this;
    }

    /**
     * fluent setter
     * @see #setResourceMapSimpleExtension(List<Object>)
     * 
     * @param resourceMapSimpleExtension
     *     required parameter
     */
    public ResourceMap withResourceMapSimpleExtension(final List<Object> resourceMapSimpleExtension) {
        this.setResourceMapSimpleExtension(resourceMapSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setResourceMapObjectExtension(List<AbstractObject>)
     * 
     * @param resourceMapObjectExtension
     *     required parameter
     */
    public ResourceMap withResourceMapObjectExtension(final List<AbstractObject> resourceMapObjectExtension) {
        this.setResourceMapObjectExtension(resourceMapObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public ResourceMap withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public ResourceMap withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public ResourceMap withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public ResourceMap clone() {
        ResourceMap copy;
        copy = ((ResourceMap) super.clone());
        copy.alias = new ArrayList<Alias>((getAlias().size()));
        for (Alias iter: alias) {
            copy.alias.add(iter.clone());
        }
        copy.resourceMapSimpleExtension = new ArrayList<Object>((getResourceMapSimpleExtension().size()));
        for (Object iter: resourceMapSimpleExtension) {
            copy.resourceMapSimpleExtension.add(iter);
        }
        copy.resourceMapObjectExtension = new ArrayList<AbstractObject>((getResourceMapObjectExtension().size()));
        for (AbstractObject iter: resourceMapObjectExtension) {
            copy.resourceMapObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
