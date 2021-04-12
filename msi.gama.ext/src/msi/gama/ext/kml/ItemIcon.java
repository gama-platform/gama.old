
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <itemicon>
 * <p>
 * <state> Specifies the current state of the NetworkLink or Folder. Possible values 
 * are open, closed, error, fetching0, fetching1, and fetching2. These values can be 
 * combined by inserting a space between two values (no comma). <href> Specifies the 
 * URI of the image used in the List View for the Feature. 
 * </p>
 * <p>
 * Icon used in the List view that reflects the state of a Folder or Link fetch. Icons 
 * associated with the open and closed modes are used for Folders and Network Links. 
 * Icons associated with the error and fetching0, fetching1, and fetching2 modes are 
 * used for Network Links. The following screen capture illustrates the Google Earth 
 * icons for these states: 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ItemIconType", propOrder = {
    "state",
    "href",
    "itemIconSimpleExtension",
    "itemIconObjectExtension"
})
@XmlRootElement(name = "ItemIcon", namespace = "http://www.opengis.net/kml/2.2")
public class ItemIcon
    extends AbstractObject
    implements Cloneable
{

    /**
     * <state>
     * <p>
     * Specifies the current state of the NetworkLink or Folder. Possible values are open, 
     * closed, error, fetching0, fetching1, and fetching2. These values can be combined 
     * by inserting a space between two values (no comma). 
     * </p>
     * 
     * 
     * 
     */
    @XmlList
    protected List<ItemIconState> state;
    /**
     * <href>
     * <p>
     * A URL (either an HTTP address or a local file specification). When the parent of 
     * <Link> is a NetworkLink, <href> is a KML file. When the parent of <Link> is a Model, 
     * <href> is a COLLADA file. When the parent of <Icon> (same fields as <Link>) is an 
     * Overlay, <href> is an image. Relative URLs can be used in this tag and are evaluated 
     * relative to the enclosing KML file. 
     * </p>
     * <p>
     * An HTTP address or a local file specification used to load an icon. 
     * </p>
     * <p>
     * Specifies the URI of the image used in the List View for the Feature. 
     * </p>
     * 
     * 
     * 
     */
    protected String href;
    @XmlElement(name = "ItemIconSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> itemIconSimpleExtension;
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
    @XmlElement(name = "ItemIconObjectExtensionGroup")
    protected List<AbstractObject> itemIconObjectExtension;

    public ItemIcon() {
        super();
    }

    /**
     * @see state
     * 
     */
    public List<ItemIconState> getState() {
        if (state == null) {
            state = new ArrayList<ItemIconState>();
        }
        return this.state;
    }

    /**
     * @see href
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * @see href
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * @see itemIconSimpleExtension
     * 
     */
    public List<Object> getItemIconSimpleExtension() {
        if (itemIconSimpleExtension == null) {
            itemIconSimpleExtension = new ArrayList<Object>();
        }
        return this.itemIconSimpleExtension;
    }

    /**
     * @see itemIconObjectExtension
     * 
     */
    public List<AbstractObject> getItemIconObjectExtension() {
        if (itemIconObjectExtension == null) {
            itemIconObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.itemIconObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((state == null)? 0 :state.hashCode()));
        result = ((prime*result)+((href == null)? 0 :href.hashCode()));
        result = ((prime*result)+((itemIconSimpleExtension == null)? 0 :itemIconSimpleExtension.hashCode()));
        result = ((prime*result)+((itemIconObjectExtension == null)? 0 :itemIconObjectExtension.hashCode()));
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
        if ((obj instanceof ItemIcon) == false) {
            return false;
        }
        ItemIcon other = ((ItemIcon) obj);
        if (state == null) {
            if (other.state!= null) {
                return false;
            }
        } else {
            if (state.equals(other.state) == false) {
                return false;
            }
        }
        if (href == null) {
            if (other.href!= null) {
                return false;
            }
        } else {
            if (href.equals(other.href) == false) {
                return false;
            }
        }
        if (itemIconSimpleExtension == null) {
            if (other.itemIconSimpleExtension!= null) {
                return false;
            }
        } else {
            if (itemIconSimpleExtension.equals(other.itemIconSimpleExtension) == false) {
                return false;
            }
        }
        if (itemIconObjectExtension == null) {
            if (other.itemIconObjectExtension!= null) {
                return false;
            }
        } else {
            if (itemIconObjectExtension.equals(other.itemIconObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see state
     * 
     * @param state
     */
    public void setState(final List<ItemIconState> state) {
        this.state = state;
    }

    /**
     * add a value to the state property collection
     * 
     * @param state
     *     Objects of the following type are allowed in the list: {@link ItemIconState}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ItemIcon addToState(final ItemIconState state) {
        this.getState().add(state);
        return this;
    }

    /**
     * @see itemIconSimpleExtension
     * 
     * @param itemIconSimpleExtension
     */
    public void setItemIconSimpleExtension(final List<Object> itemIconSimpleExtension) {
        this.itemIconSimpleExtension = itemIconSimpleExtension;
    }

    /**
     * add a value to the itemIconSimpleExtension property collection
     * 
     * @param itemIconSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ItemIcon addToItemIconSimpleExtension(final Object itemIconSimpleExtension) {
        this.getItemIconSimpleExtension().add(itemIconSimpleExtension);
        return this;
    }

    /**
     * @see itemIconObjectExtension
     * 
     * @param itemIconObjectExtension
     */
    public void setItemIconObjectExtension(final List<AbstractObject> itemIconObjectExtension) {
        this.itemIconObjectExtension = itemIconObjectExtension;
    }

    /**
     * add a value to the itemIconObjectExtension property collection
     * 
     * @param itemIconObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ItemIcon addToItemIconObjectExtension(final AbstractObject itemIconObjectExtension) {
        this.getItemIconObjectExtension().add(itemIconObjectExtension);
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
    public ItemIcon addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setState(List<ItemIconState>)
     * 
     * @param state
     *     required parameter
     */
    public ItemIcon withState(final List<ItemIconState> state) {
        this.setState(state);
        return this;
    }

    /**
     * fluent setter
     * @see #setHref(String)
     * 
     * @param href
     *     required parameter
     */
    public ItemIcon withHref(final String href) {
        this.setHref(href);
        return this;
    }

    /**
     * fluent setter
     * @see #setItemIconSimpleExtension(List<Object>)
     * 
     * @param itemIconSimpleExtension
     *     required parameter
     */
    public ItemIcon withItemIconSimpleExtension(final List<Object> itemIconSimpleExtension) {
        this.setItemIconSimpleExtension(itemIconSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setItemIconObjectExtension(List<AbstractObject>)
     * 
     * @param itemIconObjectExtension
     *     required parameter
     */
    public ItemIcon withItemIconObjectExtension(final List<AbstractObject> itemIconObjectExtension) {
        this.setItemIconObjectExtension(itemIconObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public ItemIcon withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public ItemIcon withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public ItemIcon withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public ItemIcon clone() {
        ItemIcon copy;
        copy = ((ItemIcon) super.clone());
        copy.state = new ArrayList<ItemIconState>((getState().size()));
        for (ItemIconState iter: state) {
            copy.state.add(iter);
        }
        copy.itemIconSimpleExtension = new ArrayList<Object>((getItemIconSimpleExtension().size()));
        for (Object iter: itemIconSimpleExtension) {
            copy.itemIconSimpleExtension.add(iter);
        }
        copy.itemIconObjectExtension = new ArrayList<AbstractObject>((getItemIconObjectExtension().size()));
        for (AbstractObject iter: itemIconObjectExtension) {
            copy.itemIconObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
