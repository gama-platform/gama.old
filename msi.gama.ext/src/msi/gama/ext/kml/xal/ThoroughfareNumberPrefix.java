
package msi.gama.ext.kml.xal;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "content"
})
@XmlRootElement(name = "ThoroughfareNumberPrefix")
public class ThoroughfareNumberPrefix implements Cloneable
{

    @XmlValue
    protected String content;
    @XmlAttribute(name = "NumberPrefixSeparator")
    @XmlSchemaType(name = "anySimpleType")
    protected String numberPrefixSeparator;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAttribute(name = "Code")
    @XmlSchemaType(name = "anySimpleType")
    protected String code;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public ThoroughfareNumberPrefix() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setContent(String value) {
        this.content = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getNumberPrefixSeparator() {
        return numberPrefixSeparator;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setNumberPrefixSeparator(String value) {
        this.numberPrefixSeparator = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getUnderscore() {
        return underscore;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setUnderscore(String value) {
        this.underscore = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((content == null)? 0 :content.hashCode()));
        result = ((prime*result)+((numberPrefixSeparator == null)? 0 :numberPrefixSeparator.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((code == null)? 0 :code.hashCode()));
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
        if ((obj instanceof ThoroughfareNumberPrefix) == false) {
            return false;
        }
        ThoroughfareNumberPrefix other = ((ThoroughfareNumberPrefix) obj);
        if (content == null) {
            if (other.content!= null) {
                return false;
            }
        } else {
            if (content.equals(other.content) == false) {
                return false;
            }
        }
        if (numberPrefixSeparator == null) {
            if (other.numberPrefixSeparator!= null) {
                return false;
            }
        } else {
            if (numberPrefixSeparator.equals(other.numberPrefixSeparator) == false) {
                return false;
            }
        }
        if (underscore == null) {
            if (other.underscore!= null) {
                return false;
            }
        } else {
            if (underscore.equals(other.underscore) == false) {
                return false;
            }
        }
        if (code == null) {
            if (other.code!= null) {
                return false;
            }
        } else {
            if (code.equals(other.code) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * fluent setter
     * @see #setContent(String)
     * 
     * @param content
     *     required parameter
     */
    public ThoroughfareNumberPrefix withContent(final String content) {
        this.setContent(content);
        return this;
    }

    /**
     * fluent setter
     * @see #setNumberPrefixSeparator(String)
     * 
     * @param numberPrefixSeparator
     *     required parameter
     */
    public ThoroughfareNumberPrefix withNumberPrefixSeparator(final String numberPrefixSeparator) {
        this.setNumberPrefixSeparator(numberPrefixSeparator);
        return this;
    }

    /**
     * fluent setter
     * @see #setUnderscore(String)
     * 
     * @param underscore
     *     required parameter
     */
    public ThoroughfareNumberPrefix withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter
     * @see #setCode(String)
     * 
     * @param code
     *     required parameter
     */
    public ThoroughfareNumberPrefix withCode(final String code) {
        this.setCode(code);
        return this;
    }

    @Override
    public ThoroughfareNumberPrefix clone() {
        ThoroughfareNumberPrefix copy;
        try {
            copy = ((ThoroughfareNumberPrefix) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        return copy;
    }

}
