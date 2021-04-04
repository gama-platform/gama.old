
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.w3c.dom.Element;


/**
 * <extendeddata>
 * <p>
 * Adding untyped data/value pairs using the <Data> element (basic) Declaring new typed 
 * fields using the <Schema> element and then instancing them using the <SchemaData> 
 * element (advanced) Referring to XML elements defined in other namespaces by referencing 
 * the external namespace within the KML file (basic) 
 * </p>
 * <p>
 * Allows you to add custom data to a KML file. This data can be (1) data that references 
 * an external XML schema, (2) untyped data/value pairs, or (3) typed data. A given 
 * KML Feature can contain a combination of these types of custom data. 
 * </p>
 * <p>
 * For more information, see Adding Custom Data in "Topics in KML." 
 * </p>
 * <p>
 * The ExtendedData element offers three techniques for adding custom data to a KML 
 * Feature (NetworkLink, Placemark, GroundOverlay, PhotoOverlay, ScreenOverlay, Document, 
 * Folder). These techniques are 
 * </p>
 * <p>
 * These techniques can be combined within a single KML file or Feature for different 
 * pieces of data. 
 * </p>
 * 
 * Syntax: 
 * <pre>&lt;ExtendedData&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br>  &lt;Data name="string"&gt;<br>&nbsp;   &lt;displayName&gt;...&lt;/displayName&gt;&nbsp;&nbsp;&nbsp; &lt;!-- string --&gt;<br> &nbsp;&nbsp; &lt;value&gt;...&lt;/value&gt;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &lt;!-- string --&gt;<br>  &lt;/Data&gt;
 *   &lt;SchemaData schemaUrl="<em>anyURI</em>"&gt;<br>    &lt;SimpleData name=""&gt; ... &lt;/SimpleData&gt;&nbsp;&nbsp; &lt;!-- string --&gt;<br>  &lt;/SchemaData&gt;
 *   &lt;namespace_prefix:other&gt;...&lt;/namespace_prefix:other&gt;
 * &lt;/ExtendedData&gt;</pre>
 * 
 * Contained By: 
 * @see: <Feature>
 * 
 * See Also: 
 * Schema
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtendedDataType", propOrder = {
    "data",
    "schemaData",
    "any"
})
@XmlRootElement(name = "ExtendedData", namespace = "http://www.opengis.net/kml/2.2")
public class ExtendedData implements Cloneable
{

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
    @XmlElement(name = "Data")
    protected List<Data> data;
    /**
     * <schemadata schemaurl="anyuri">
     * <p>
     * The <schemaURL> can be a full URL, a reference to a Schema ID defined in an external 
     * KML file, or a reference to a Schema ID defined in the same KML file. All of the 
     * following specifications are acceptable: 
     * </p>
     * <p>
     * The Schema element is always a child of Document. The ExtendedData element is a 
     * child of the Feature that contains the custom data. 
     * </p>
     * <p>
     * This element is used in conjunction with <Schema> to add typed custom data to a 
     * KML Feature. The Schema element (identified by the schemaUrl attribute) declares 
     * the custom data type. The actual data objects ("instances" of the custom data) are 
     * defined using the SchemaData element. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(name = "SchemaData")
    protected List<SchemaData> schemaData;
    @XmlAnyElement(lax = true)
    protected List<Object> any;

    public ExtendedData() {
        super();
    }

    /**
     * @see data
     * 
     */
    public List<Data> getData() {
        if (data == null) {
            data = new ArrayList<Data>();
        }
        return this.data;
    }

    /**
     * @see schemaData
     * 
     */
    public List<SchemaData> getSchemaData() {
        if (schemaData == null) {
            schemaData = new ArrayList<SchemaData>();
        }
        return this.schemaData;
    }

    /**
     * @see any
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((data == null)? 0 :data.hashCode()));
        result = ((prime*result)+((schemaData == null)? 0 :schemaData.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
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
        if ((obj instanceof ExtendedData) == false) {
            return false;
        }
        ExtendedData other = ((ExtendedData) obj);
        if (data == null) {
            if (other.data!= null) {
                return false;
            }
        } else {
            if (data.equals(other.data) == false) {
                return false;
            }
        }
        if (schemaData == null) {
            if (other.schemaData!= null) {
                return false;
            }
        } else {
            if (schemaData.equals(other.schemaData) == false) {
                return false;
            }
        }
        if (any == null) {
            if (other.any!= null) {
                return false;
            }
        } else {
            if (any.equals(other.any) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Data} and adds it to data.
     * This method is a short version for:
     * <code>
     * Data data = new Data();
     * this.getData().add(data); </code>
     * 
     * 
     * @param value
     *     required parameter
     */
    public Data createAndAddData(final String value) {
        Data newValue = new Data(value);
        this.getData().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SchemaData} and adds it to schemaData.
     * This method is a short version for:
     * <code>
     * SchemaData schemaData = new SchemaData();
     * this.getSchemaData().add(schemaData); </code>
     * 
     * 
     */
    public SchemaData createAndAddSchemaData() {
        SchemaData newValue = new SchemaData();
        this.getSchemaData().add(newValue);
        return newValue;
    }

    /**
     * @see data
     * 
     * @param data
     */
    public void setData(final List<Data> data) {
        this.data = data;
    }

    /**
     * add a value to the data property collection
     * 
     * @param data
     *     Objects of the following type are allowed in the list: {@link Data}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ExtendedData addToData(final Data data) {
        this.getData().add(data);
        return this;
    }

    /**
     * @see schemaData
     * 
     * @param schemaData
     */
    public void setSchemaData(final List<SchemaData> schemaData) {
        this.schemaData = schemaData;
    }

    /**
     * add a value to the schemaData property collection
     * 
     * @param schemaData
     *     Objects of the following type are allowed in the list: {@link SchemaData}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ExtendedData addToSchemaData(final SchemaData schemaData) {
        this.getSchemaData().add(schemaData);
        return this;
    }

    /**
     * @see any
     * 
     * @param any
     */
    public void setAny(final List<Object> any) {
        this.any = any;
    }

    /**
     * add a value to the any property collection
     * 
     * @param any
     *     Objects of the following type are allowed in the list: {@link Object}{@link Element}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ExtendedData addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setData(List<Data>)
     * 
     * @param data
     *     required parameter
     */
    public ExtendedData withData(final List<Data> data) {
        this.setData(data);
        return this;
    }

    /**
     * fluent setter
     * @see #setSchemaData(List<SchemaData>)
     * 
     * @param schemaData
     *     required parameter
     */
    public ExtendedData withSchemaData(final List<SchemaData> schemaData) {
        this.setSchemaData(schemaData);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public ExtendedData withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    @Override
    public ExtendedData clone() {
        ExtendedData copy;
        try {
            copy = ((ExtendedData) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.data = new ArrayList<Data>((getData().size()));
        for (Data iter: data) {
            copy.data.add(iter.clone());
        }
        copy.schemaData = new ArrayList<SchemaData>((getSchemaData().size()));
        for (SchemaData iter: schemaData) {
            copy.schemaData.add(iter.clone());
        }
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }

}
