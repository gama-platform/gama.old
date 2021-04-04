
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <simpledata name="string">
 * <p>
 * <SimpleData name="string"> This element assigns a value to the custom data field 
 * identified by the name attribute. The type and name of this custom data field are 
 * declared in the <Schema> element. Here is an example of defining two custom data 
 * elements: <Placemark> <name>Easy trail</name> <ExtendedData> <SchemaData schemaUrl="#TrailHeadTypeId"> 
 * <SimpleData name="TrailHeadName">Pi in the sky</SimpleData> <SimpleData name="TrailLength">3.14159</SimpleData> 
 * <SimpleData name="ElevationGain">10</SimpleData> </SchemaData> </ExtendedData> <Point> 
 * <coordinates>-122.000,37.002</coordinates> </Point> </Placemark> <Placemark> <name>Difficult 
 * trail</name> <ExtendedData> <SchemaData schemaUrl="#TrailHeadTypeId"> <SimpleData 
 * name="TrailHeadName">Mount Everest</SimpleData> <SimpleData name="TrailLength">347.45</SimpleData> 
 * <SimpleData name="ElevationGain">10000</SimpleData> </SchemaData> </ExtendedData> 
 * <Point> <coordinates>-122.000,37.002</coordinates> </Point> </Placemark> 
 * </p>
 * <p>
 * Here is an example of defining two custom data elements: 
 * </p>
 * <p>
 * This element assigns a value to the custom data field identified by the name attribute. 
 * The type and name of this custom data field are declared in the <Schema> element. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SimpleDataType", propOrder = {
    "value"
})
@XmlRootElement(name = "SimpleData", namespace = "http://www.opengis.net/kml/2.2")
public class SimpleData implements Cloneable
{

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
    @XmlValue
    protected String value;
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
    @XmlAttribute(name = "name", required = true)
    protected String name;

    /**
     * Value constructor with only mandatory fields
     * 
     * @param name
     *     required parameter
     */
    public SimpleData(final String name) {
        super();
        this.name = name;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private SimpleData() {
        super();
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
        int result = 1;
        result = ((prime*result)+((value == null)? 0 :value.hashCode()));
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
        if ((obj instanceof SimpleData) == false) {
            return false;
        }
        SimpleData other = ((SimpleData) obj);
        if (value == null) {
            if (other.value!= null) {
                return false;
            }
        } else {
            if (value.equals(other.value) == false) {
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
     * fluent setter
     * @see #setValue(String)
     * 
     * @param value
     *     required parameter
     */
    public SimpleData withValue(final String value) {
        this.setValue(value);
        return this;
    }

    @Override
    public SimpleData clone() {
        SimpleData copy;
        try {
            copy = ((SimpleData) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        return copy;
    }

}
