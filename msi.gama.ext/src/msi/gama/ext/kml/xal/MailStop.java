
package msi.gama.ext.kml.xal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.namespace.QName;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MailStopType", propOrder = {
    "addressLine",
    "mailStopName",
    "mailStopNumber",
    "any"
})
@XmlRootElement(name = "MailStop", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
public class MailStop implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "MailStopName")
    protected MailStop.MailStopName mailStopName;
    @XmlElement(name = "MailStopNumber")
    protected MailStop.MailStopNumber mailStopNumber;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public MailStop() {
        super();
    }

    /**
     * 
     */
    public List<AddressLine> getAddressLine() {
        if (addressLine == null) {
            addressLine = new ArrayList<AddressLine>();
        }
        return this.addressLine;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link MailStop.MailStopName}
     *     
     */
    public MailStop.MailStopName getMailStopName() {
        return mailStopName;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link MailStop.MailStopName}
     *     
     */
    public void setMailStopName(MailStop.MailStopName value) {
        this.mailStopName = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link MailStop.MailStopNumber}
     *     
     */
    public MailStop.MailStopNumber getMailStopNumber() {
        return mailStopNumber;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link MailStop.MailStopNumber}
     *     
     */
    public void setMailStopNumber(MailStop.MailStopNumber value) {
        this.mailStopNumber = value;
    }

    /**
     * 
     */
    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
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
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
        result = ((prime*result)+((mailStopName == null)? 0 :mailStopName.hashCode()));
        result = ((prime*result)+((mailStopNumber == null)? 0 :mailStopNumber.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
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
        if ((obj instanceof MailStop) == false) {
            return false;
        }
        MailStop other = ((MailStop) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (mailStopName == null) {
            if (other.mailStopName!= null) {
                return false;
            }
        } else {
            if (mailStopName.equals(other.mailStopName) == false) {
                return false;
            }
        }
        if (mailStopNumber == null) {
            if (other.mailStopNumber!= null) {
                return false;
            }
        } else {
            if (mailStopNumber.equals(other.mailStopNumber) == false) {
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
        if (underscore == null) {
            if (other.underscore!= null) {
                return false;
            }
        } else {
            if (underscore.equals(other.underscore) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link AddressLine} and adds it to addressLine.
     * This method is a short version for:
     * <code>
     * AddressLine addressLine = new AddressLine();
     * this.getAddressLine().add(addressLine); </code>
     * 
     * 
     */
    public AddressLine createAndAddAddressLine() {
        AddressLine newValue = new AddressLine();
        this.getAddressLine().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link MailStop.MailStopName} and set it to mailStopName.
     * 
     * This method is a short version for:
     * <code>
     * MailStopName mailStopName = new MailStopName();
     * this.setMailStopName(mailStopName); </code>
     * 
     * 
     */
    public MailStop.MailStopName createAndSetMailStopName() {
        MailStop.MailStopName newValue = new MailStop.MailStopName();
        this.setMailStopName(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link MailStop.MailStopNumber} and set it to mailStopNumber.
     * 
     * This method is a short version for:
     * <code>
     * MailStopNumber mailStopNumber = new MailStopNumber();
     * this.setMailStopNumber(mailStopNumber); </code>
     * 
     * 
     */
    public MailStop.MailStopNumber createAndSetMailStopNumber() {
        MailStop.MailStopNumber newValue = new MailStop.MailStopNumber();
        this.setMailStopNumber(newValue);
        return newValue;
    }

    /**
     * Sets the value of the addressLine property Objects of the following type(s) are allowed in the list List<AddressLine>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAddressLine} instead.
     * 
     * 
     * @param addressLine
     */
    public void setAddressLine(final List<AddressLine> addressLine) {
        this.addressLine = addressLine;
    }

    /**
     * add a value to the addressLine property collection
     * 
     * @param addressLine
     *     Objects of the following type are allowed in the list: {@link AddressLine}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public MailStop addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the any property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withAny} instead.
     * 
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
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public MailStop addToAny(final Object any) {
        this.getAny().add(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setAddressLine(List<AddressLine>)
     * 
     * @param addressLine
     *     required parameter
     */
    public MailStop withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setMailStopName(MailStopName)
     * 
     * @param mailStopName
     *     required parameter
     */
    public MailStop withMailStopName(final MailStop.MailStopName mailStopName) {
        this.setMailStopName(mailStopName);
        return this;
    }

    /**
     * fluent setter
     * @see #setMailStopNumber(MailStopNumber)
     * 
     * @param mailStopNumber
     *     required parameter
     */
    public MailStop withMailStopNumber(final MailStop.MailStopNumber mailStopNumber) {
        this.setMailStopNumber(mailStopNumber);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public MailStop withAny(final List<Object> any) {
        this.setAny(any);
        return this;
    }

    /**
     * fluent setter
     * @see #setUnderscore(String)
     * 
     * @param underscore
     *     required parameter
     */
    public MailStop withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    @Override
    public MailStop clone() {
        MailStop copy;
        try {
            copy = ((MailStop) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.mailStopName = ((mailStopName == null)?null:((MailStop.MailStopName) mailStopName.clone()));
        copy.mailStopNumber = ((mailStopNumber == null)?null:((MailStop.MailStopNumber) mailStopNumber.clone()));
        copy.any = new ArrayList<Object>((getAny().size()));
        for (Object iter: any) {
            copy.any.add(iter);
        }
        return copy;
    }


    /**
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "MailStopName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class MailStopName implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public MailStopName() {
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
            if ((obj instanceof MailStop.MailStopName) == false) {
                return false;
            }
            MailStop.MailStopName other = ((MailStop.MailStopName) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
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
        public MailStop.MailStopName withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public MailStop.MailStopName withUnderscore(final String underscore) {
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
        public MailStop.MailStopName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public MailStop.MailStopName clone() {
            MailStop.MailStopName copy;
            try {
                copy = ((MailStop.MailStopName) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }


    /**
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "content"
    })
    @XmlRootElement(name = "MailStopNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class MailStopNumber implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "NameNumberSeparator")
        @XmlSchemaType(name = "anySimpleType")
        protected String nameNumberSeparator;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public MailStopNumber() {
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
        public String getNameNumberSeparator() {
            return nameNumberSeparator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setNameNumberSeparator(String value) {
            this.nameNumberSeparator = value;
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
            result = ((prime*result)+((nameNumberSeparator == null)? 0 :nameNumberSeparator.hashCode()));
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
            if ((obj instanceof MailStop.MailStopNumber) == false) {
                return false;
            }
            MailStop.MailStopNumber other = ((MailStop.MailStopNumber) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
                    return false;
                }
            }
            if (nameNumberSeparator == null) {
                if (other.nameNumberSeparator!= null) {
                    return false;
                }
            } else {
                if (nameNumberSeparator.equals(other.nameNumberSeparator) == false) {
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
        public MailStop.MailStopNumber withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter
         * @see #setNameNumberSeparator(String)
         * 
         * @param nameNumberSeparator
         *     required parameter
         */
        public MailStop.MailStopNumber withNameNumberSeparator(final String nameNumberSeparator) {
            this.setNameNumberSeparator(nameNumberSeparator);
            return this;
        }

        /**
         * fluent setter
         * @see #setCode(String)
         * 
         * @param code
         *     required parameter
         */
        public MailStop.MailStopNumber withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public MailStop.MailStopNumber clone() {
            MailStop.MailStopNumber copy;
            try {
                copy = ((MailStop.MailStopNumber) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }

}
