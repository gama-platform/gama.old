
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.gx.ViewerOptions;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractViewType", propOrder = {
    "abstractViewSimpleExtension",
    "abstractViewObjectExtension"
})
@XmlSeeAlso({
    LookAt.class,
    Camera.class
})
public abstract class AbstractView
    extends AbstractObject
    implements Cloneable
{

    @XmlElement(name = "AbstractViewSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> abstractViewSimpleExtension;
    @XmlElementRef(name = "AbstractViewObjectExtensionGroup", namespace = "http://www.opengis.net/kml/2.2", required = false)
    protected List<AbstractObject> abstractViewObjectExtension;

    public AbstractView() {
        super();
    }

    /**
     * 
     */
    public List<Object> getAbstractViewSimpleExtension() {
        if (abstractViewSimpleExtension == null) {
            abstractViewSimpleExtension = new ArrayList<Object>();
        }
        return this.abstractViewSimpleExtension;
    }

    /**
     * 
     */
    public List<AbstractObject> getAbstractViewObjectExtension() {
        if (abstractViewObjectExtension == null) {
            abstractViewObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.abstractViewObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((abstractViewSimpleExtension == null)? 0 :abstractViewSimpleExtension.hashCode()));
        result = ((prime*result)+((abstractViewObjectExtension == null)? 0 :abstractViewObjectExtension.hashCode()));
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
        if ((obj instanceof AbstractView) == false) {
            return false;
        }
        AbstractView other = ((AbstractView) obj);
        if (abstractViewSimpleExtension == null) {
            if (other.abstractViewSimpleExtension!= null) {
                return false;
            }
        } else {
            if (abstractViewSimpleExtension.equals(other.abstractViewSimpleExtension) == false) {
                return false;
            }
        }
        if (abstractViewObjectExtension == null) {
            if (other.abstractViewObjectExtension!= null) {
                return false;
            }
        } else {
            if (abstractViewObjectExtension.equals(other.abstractViewObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the value of the abstractViewSimpleExtension property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAbstractViewSimpleExtension} instead.
     * 
     * 
     * @param abstractViewSimpleExtension
     */
    public void setAbstractViewSimpleExtension(final List<Object> abstractViewSimpleExtension) {
        this.abstractViewSimpleExtension = abstractViewSimpleExtension;
    }

    /**
     * add a value to the abstractViewSimpleExtension property collection
     * 
     * @param abstractViewSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public AbstractView addToAbstractViewSimpleExtension(final Object abstractViewSimpleExtension) {
        this.getAbstractViewSimpleExtension().add(abstractViewSimpleExtension);
        return this;
    }

    /**
     * Sets the value of the abstractViewObjectExtension property Objects of the following type(s) are allowed in the list List<AbstractObject>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAbstractViewObjectExtension} instead.
     * 
     * 
     * @param abstractViewObjectExtension
     */
    public void setAbstractViewObjectExtension(final List<AbstractObject> abstractViewObjectExtension) {
        this.abstractViewObjectExtension = abstractViewObjectExtension;
    }

    /**
     * add a value to the abstractViewObjectExtension property collection
     * 
     * @param abstractViewObjectExtension
     *     Objects of the following type are allowed in the list: {@code <}{@link TimeSpan}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TimeStamp}{@code>}{@link JAXBElement}{@code <}{@link ViewerOptions}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public AbstractView addToAbstractViewObjectExtension(final AbstractObject abstractViewObjectExtension) {
        this.getAbstractViewObjectExtension().add(abstractViewObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.setObjectSimpleExtension(objectSimpleExtension);
    }

    @Obvious
    @Override
    public AbstractView addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setAbstractViewSimpleExtension(List<Object>)
     * 
     * @param abstractViewSimpleExtension
     *     required parameter
     */
    public AbstractView withAbstractViewSimpleExtension(final List<Object> abstractViewSimpleExtension) {
        this.setAbstractViewSimpleExtension(abstractViewSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setAbstractViewObjectExtension(List<AbstractObject>)
     * 
     * @param abstractViewObjectExtension
     *     required parameter
     */
    public AbstractView withAbstractViewObjectExtension(final List<AbstractObject> abstractViewObjectExtension) {
        this.setAbstractViewObjectExtension(abstractViewObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public AbstractView withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public AbstractView withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public AbstractView withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public AbstractView clone() {
        AbstractView copy;
        copy = ((AbstractView) super.clone());
        copy.abstractViewSimpleExtension = new ArrayList<Object>((getAbstractViewSimpleExtension().size()));
        for (Object iter: abstractViewSimpleExtension) {
            copy.abstractViewSimpleExtension.add(iter);
        }
        copy.abstractViewObjectExtension = new ArrayList<AbstractObject>((getAbstractViewObjectExtension().size()));
        for (AbstractObject iter: abstractViewObjectExtension) {
            copy.abstractViewObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
