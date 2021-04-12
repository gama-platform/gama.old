
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <snippet maxlines="2" >
 * <p>
 * A short description of the feature. In Google Earth, this description is displayed 
 * in the Places panel under the name of the feature. If a Snippet is not supplied, 
 * the first two lines of the <description> are used. In Google Earth, if a Placemark 
 * contains both a description and a Snippet, the <Snippet> appears beneath the Placemark 
 * in the Places panel, and the <description> appears in the Placemark's description 
 * balloon. This tag does not support HTML markup. <Snippet> has a maxLines attribute, 
 * an integer that specifies the maximum number of lines to display. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SnippetType", propOrder = {
    "value"
})
@Deprecated
@XmlRootElement(name = "Snippet", namespace = "http://www.opengis.net/kml/2.2")
public class Snippet implements Cloneable
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
    @XmlAttribute(name = "maxLines")
    protected int maxLines;

    public Snippet() {
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
     * @see maxLines
     * 
     */
    public int getMaxLines() {
        return maxLines;
    }

    /**
     * @see maxLines
     * 
     */
    public void setMaxLines(int value) {
        this.maxLines = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((value == null)? 0 :value.hashCode()));
        result = ((prime*result)+ maxLines);
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
        if ((obj instanceof Snippet) == false) {
            return false;
        }
        Snippet other = ((Snippet) obj);
        if (value == null) {
            if (other.value!= null) {
                return false;
            }
        } else {
            if (value.equals(other.value) == false) {
                return false;
            }
        }
        if (maxLines!= other.maxLines) {
            return false;
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
    public Snippet withValue(final String value) {
        this.setValue(value);
        return this;
    }

    /**
     * fluent setter
     * @see #setMaxLines(int)
     * 
     * @param maxLines
     *     required parameter
     */
    public Snippet withMaxLines(final int maxLines) {
        this.setMaxLines(maxLines);
        return this;
    }

    @Override
    public Snippet clone() {
        Snippet copy;
        try {
            copy = ((Snippet) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        return copy;
    }

}
