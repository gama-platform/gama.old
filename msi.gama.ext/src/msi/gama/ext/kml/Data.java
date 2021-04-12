
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <data name ="string">
 * <p>
 * Creates an untyped name/value pair. The name can have two versions: name and displayName. 
 * The name attribute is used to identify the data pair within the KML file. The displayName 
 * element is used when a properly formatted name, with spaces and HTML formatting, 
 * is displayed in Google Earth. In the <text> element of <BalloonStyle>, the notation 
 * $[name:displayName] is replaced with <displayName>. If you substitute the value 
 * of the name attribute of the <Data> element in this format (for example, $[holeYardage], 
 * the attribute value is replaced with <value>. By default, the Placemark's balloon 
 * displays the name/value pairs associated with it. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder = {
    "displayName",
    "value",
    "dataExtension"
})
@XmlRootElement(name = "Data", namespace = "http://www.opengis.net/kml/2.2")
public class Data
    extends AbstractObject
    implements Cloneable
{

    /**
     * <displayname>
     * <p>
     * An optional formatted version of name, to be used for display purposes. 
     * </p>
     * 
     * 
     * 
     */
    protected String displayName;
    /**
     * <value>
     * <p>
     * <Placemark> <name>Club house</name> <ExtendedData> <Data name="holeNumber"> <value>1</value> 
     * </Data> <Data name="holeYardage"> <value>234</value> </Data> <Data name="holePar"> 
     * <value>4</value> </Data> </ExtendedData> </Placemark> 
     * </p>
     * <p>
     * <displayName> An optional formatted version of name, to be used for display purposes. 
     * <value> Value of the data pair. <Placemark> <name>Club house</name> <ExtendedData> 
     * <Data name="holeNumber"> <value>1</value> </Data> <Data name="holeYardage"> <value>234</value> 
     * </Data> <Data name="holePar"> <value>4</value> </Data> </ExtendedData> </Placemark> 
     * </p>
     * <p>
     * Value of the data pair. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(required = true)
    protected String value;
    @XmlElement(name = "DataExtension")
    protected List<Object> dataExtension;
    /**
     * <name>
     * <p>
     * User-defined text displayed in the 3D viewer as the label for the object (for example, 
     * for a Placemark, Folder, or NetworkLink). 
     * </p>
     * 
     * 
     * 
     */
    @XmlAttribute(name = "name")
    protected String name;

    /**
     * Value constructor with only mandatory fields
     * 
     * @param value
     *     required parameter
     */
    public Data(final String value) {
        super();
        this.value = value;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private Data() {
        super();
    }

    /**
     * @see displayName
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * @see displayName
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * @see value
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * @see value
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @see dataExtension
     * 
     */
    public List<Object> getDataExtension() {
        if (dataExtension == null) {
            dataExtension = new ArrayList<Object>();
        }
        return this.dataExtension;
    }

    /**
     * @see name
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * @see name
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((displayName == null)? 0 :displayName.hashCode()));
        result = ((prime*result)+((value == null)? 0 :value.hashCode()));
        result = ((prime*result)+((dataExtension == null)? 0 :dataExtension.hashCode()));
        result = ((prime*result)+((name == null)? 0 :name.hashCode()));
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
        if ((obj instanceof Data) == false) {
            return false;
        }
        Data other = ((Data) obj);
        if (displayName == null) {
            if (other.displayName!= null) {
                return false;
            }
        } else {
            if (displayName.equals(other.displayName) == false) {
                return false;
            }
        }
        if (value == null) {
            if (other.value!= null) {
                return false;
            }
        } else {
            if (value.equals(other.value) == false) {
                return false;
            }
        }
        if (dataExtension == null) {
            if (other.dataExtension!= null) {
                return false;
            }
        } else {
            if (dataExtension.equals(other.dataExtension) == false) {
                return false;
            }
        }
        if (name == null) {
            if (other.name!= null) {
                return false;
            }
        } else {
            if (name.equals(other.name) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see dataExtension
     * 
     * @param dataExtension
     */
    public void setDataExtension(final List<Object> dataExtension) {
        this.dataExtension = dataExtension;
    }

    /**
     * add a value to the dataExtension property collection
     * 
     * @param dataExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Data addToDataExtension(final Object dataExtension) {
        this.getDataExtension().add(dataExtension);
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
    public Data addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setDisplayName(String)
     * 
     * @param displayName
     *     required parameter
     */
    public Data withDisplayName(final String displayName) {
        this.setDisplayName(displayName);
        return this;
    }

    /**
     * fluent setter
     * @see #setDataExtension(List<Object>)
     * 
     * @param dataExtension
     *     required parameter
     */
    public Data withDataExtension(final List<Object> dataExtension) {
        this.setDataExtension(dataExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setName(String)
     * 
     * @param name
     *     required parameter
     */
    public Data withName(final String name) {
        this.setName(name);
        return this;
    }

    @Obvious
    @Override
    public Data withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Data withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Data withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public Data clone() {
        Data copy;
        copy = ((Data) super.clone());
        copy.dataExtension = new ArrayList<Object>((getDataExtension().size()));
        for (Object iter: dataExtension) {
            copy.dataExtension.add(iter);
        }
        return copy;
    }

}
