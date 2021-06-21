
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
@XmlType(name = "", propOrder = {
    "addressLine",
    "postalCodeNumber",
    "postalCodeNumberExtension",
    "postTown",
    "any"
})
@XmlRootElement(name = "PostalCode")
public class PostalCode implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "PostalCodeNumber")
    protected List<PostalCode.PostalCodeNumber> postalCodeNumber;
    @XmlElement(name = "PostalCodeNumberExtension")
    protected List<PostalCode.PostalCodeNumberExtension> postalCodeNumberExtension;
    @XmlElement(name = "PostTown")
    protected PostalCode.PostTown postTown;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public PostalCode() {
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
     */
    public List<PostalCode.PostalCodeNumber> getPostalCodeNumber() {
        if (postalCodeNumber == null) {
            postalCodeNumber = new ArrayList<PostalCode.PostalCodeNumber>();
        }
        return this.postalCodeNumber;
    }

    /**
     * 
     */
    public List<PostalCode.PostalCodeNumberExtension> getPostalCodeNumberExtension() {
        if (postalCodeNumberExtension == null) {
            postalCodeNumberExtension = new ArrayList<PostalCode.PostalCodeNumberExtension>();
        }
        return this.postalCodeNumberExtension;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostalCode.PostTown}
     *     
     */
    public PostalCode.PostTown getPostTown() {
        return postTown;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostalCode.PostTown}
     *     
     */
    public void setPostTown(PostalCode.PostTown value) {
        this.postTown = value;
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
        result = ((prime*result)+((postalCodeNumber == null)? 0 :postalCodeNumber.hashCode()));
        result = ((prime*result)+((postalCodeNumberExtension == null)? 0 :postalCodeNumberExtension.hashCode()));
        result = ((prime*result)+((postTown == null)? 0 :postTown.hashCode()));
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
        if ((obj instanceof PostalCode) == false) {
            return false;
        }
        PostalCode other = ((PostalCode) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (postalCodeNumber == null) {
            if (other.postalCodeNumber!= null) {
                return false;
            }
        } else {
            if (postalCodeNumber.equals(other.postalCodeNumber) == false) {
                return false;
            }
        }
        if (postalCodeNumberExtension == null) {
            if (other.postalCodeNumberExtension!= null) {
                return false;
            }
        } else {
            if (postalCodeNumberExtension.equals(other.postalCodeNumberExtension) == false) {
                return false;
            }
        }
        if (postTown == null) {
            if (other.postTown!= null) {
                return false;
            }
        } else {
            if (postTown.equals(other.postTown) == false) {
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
     * Creates a new instance of {@link PostalCode.PostalCodeNumber} and adds it to postalCodeNumber.
     * This method is a short version for:
     * <code>
     * PostalCodeNumber postalCodeNumber = new PostalCodeNumber();
     * this.getPostalCodeNumber().add(postalCodeNumber); </code>
     * 
     * 
     */
    public PostalCode.PostalCodeNumber createAndAddPostalCodeNumber() {
        PostalCode.PostalCodeNumber newValue = new PostalCode.PostalCodeNumber();
        this.getPostalCodeNumber().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalCode.PostalCodeNumberExtension} and adds it to postalCodeNumberExtension.
     * This method is a short version for:
     * <code>
     * PostalCodeNumberExtension postalCodeNumberExtension = new PostalCodeNumberExtension();
     * this.getPostalCodeNumberExtension().add(postalCodeNumberExtension); </code>
     * 
     * 
     */
    public PostalCode.PostalCodeNumberExtension createAndAddPostalCodeNumberExtension() {
        PostalCode.PostalCodeNumberExtension newValue = new PostalCode.PostalCodeNumberExtension();
        this.getPostalCodeNumberExtension().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalCode.PostTown} and set it to postTown.
     * 
     * This method is a short version for:
     * <code>
     * PostTown postTown = new PostTown();
     * this.setPostTown(postTown); </code>
     * 
     * 
     */
    public PostalCode.PostTown createAndSetPostTown() {
        PostalCode.PostTown newValue = new PostalCode.PostTown();
        this.setPostTown(newValue);
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
    public PostalCode addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the postalCodeNumber property Objects of the following type(s) are allowed in the list List<PostalCodeNumber>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPostalCodeNumber} instead.
     * 
     * 
     * @param postalCodeNumber
     */
    public void setPostalCodeNumber(final List<PostalCode.PostalCodeNumber> postalCodeNumber) {
        this.postalCodeNumber = postalCodeNumber;
    }

    /**
     * add a value to the postalCodeNumber property collection
     * 
     * @param postalCodeNumber
     *     Objects of the following type are allowed in the list: {@link PostalCode.PostalCodeNumber}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public PostalCode addToPostalCodeNumber(final PostalCode.PostalCodeNumber postalCodeNumber) {
        this.getPostalCodeNumber().add(postalCodeNumber);
        return this;
    }

    /**
     * Sets the value of the postalCodeNumberExtension property Objects of the following type(s) are allowed in the list List<PostalCodeNumberExtension>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPostalCodeNumberExtension} instead.
     * 
     * 
     * @param postalCodeNumberExtension
     */
    public void setPostalCodeNumberExtension(final List<PostalCode.PostalCodeNumberExtension> postalCodeNumberExtension) {
        this.postalCodeNumberExtension = postalCodeNumberExtension;
    }

    /**
     * add a value to the postalCodeNumberExtension property collection
     * 
     * @param postalCodeNumberExtension
     *     Objects of the following type are allowed in the list: {@link PostalCode.PostalCodeNumberExtension}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public PostalCode addToPostalCodeNumberExtension(final PostalCode.PostalCodeNumberExtension postalCodeNumberExtension) {
        this.getPostalCodeNumberExtension().add(postalCodeNumberExtension);
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
    public PostalCode addToAny(final Object any) {
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
    public PostalCode withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalCodeNumber(List<PostalCodeNumber>)
     * 
     * @param postalCodeNumber
     *     required parameter
     */
    public PostalCode withPostalCodeNumber(final List<PostalCode.PostalCodeNumber> postalCodeNumber) {
        this.setPostalCodeNumber(postalCodeNumber);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalCodeNumberExtension(List<PostalCodeNumberExtension>)
     * 
     * @param postalCodeNumberExtension
     *     required parameter
     */
    public PostalCode withPostalCodeNumberExtension(final List<PostalCode.PostalCodeNumberExtension> postalCodeNumberExtension) {
        this.setPostalCodeNumberExtension(postalCodeNumberExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostTown(PostTown)
     * 
     * @param postTown
     *     required parameter
     */
    public PostalCode withPostTown(final PostalCode.PostTown postTown) {
        this.setPostTown(postTown);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public PostalCode withAny(final List<Object> any) {
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
    public PostalCode withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    @Override
    public PostalCode clone() {
        PostalCode copy;
        try {
            copy = ((PostalCode) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.postalCodeNumber = new ArrayList<PostalCode.PostalCodeNumber>((getPostalCodeNumber().size()));
        for (PostalCode.PostalCodeNumber iter: postalCodeNumber) {
            copy.postalCodeNumber.add(iter.clone());
        }
        copy.postalCodeNumberExtension = new ArrayList<PostalCode.PostalCodeNumberExtension>((getPostalCodeNumberExtension().size()));
        for (PostalCode.PostalCodeNumberExtension iter: postalCodeNumberExtension) {
            copy.postalCodeNumberExtension.add(iter.clone());
        }
        copy.postTown = ((postTown == null)?null:((PostalCode.PostTown) postTown.clone()));
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
    @XmlRootElement(name = "PostalCodeNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostalCodeNumber implements Cloneable
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

        public PostalCodeNumber() {
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
            if ((obj instanceof PostalCode.PostalCodeNumber) == false) {
                return false;
            }
            PostalCode.PostalCodeNumber other = ((PostalCode.PostalCodeNumber) obj);
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
        public PostalCode.PostalCodeNumber withContent(final String content) {
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
        public PostalCode.PostalCodeNumber withUnderscore(final String underscore) {
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
        public PostalCode.PostalCodeNumber withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public PostalCode.PostalCodeNumber clone() {
            PostalCode.PostalCodeNumber copy;
            try {
                copy = ((PostalCode.PostalCodeNumber) super.clone());
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
    @XmlRootElement(name = "PostalCodeNumberExtension", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostalCodeNumberExtension implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "NumberExtensionSeparator")
        @XmlSchemaType(name = "anySimpleType")
        protected String numberExtensionSeparator;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public PostalCodeNumberExtension() {
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
        public String getNumberExtensionSeparator() {
            return numberExtensionSeparator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setNumberExtensionSeparator(String value) {
            this.numberExtensionSeparator = value;
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
            result = ((prime*result)+((numberExtensionSeparator == null)? 0 :numberExtensionSeparator.hashCode()));
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
            if ((obj instanceof PostalCode.PostalCodeNumberExtension) == false) {
                return false;
            }
            PostalCode.PostalCodeNumberExtension other = ((PostalCode.PostalCodeNumberExtension) obj);
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
            if (numberExtensionSeparator == null) {
                if (other.numberExtensionSeparator!= null) {
                    return false;
                }
            } else {
                if (numberExtensionSeparator.equals(other.numberExtensionSeparator) == false) {
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
        public PostalCode.PostalCodeNumberExtension withContent(final String content) {
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
        public PostalCode.PostalCodeNumberExtension withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter
         * @see #setNumberExtensionSeparator(String)
         * 
         * @param numberExtensionSeparator
         *     required parameter
         */
        public PostalCode.PostalCodeNumberExtension withNumberExtensionSeparator(final String numberExtensionSeparator) {
            this.setNumberExtensionSeparator(numberExtensionSeparator);
            return this;
        }

        /**
         * fluent setter
         * @see #setCode(String)
         * 
         * @param code
         *     required parameter
         */
        public PostalCode.PostalCodeNumberExtension withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public PostalCode.PostalCodeNumberExtension clone() {
            PostalCode.PostalCodeNumberExtension copy;
            try {
                copy = ((PostalCode.PostalCodeNumberExtension) super.clone());
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
        "addressLine",
        "postTownName",
        "postTownSuffix"
    })
    @XmlRootElement(name = "PostTown", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class PostTown implements Cloneable
    {

        @XmlElement(name = "AddressLine")
        protected List<AddressLine> addressLine;
        @XmlElement(name = "PostTownName")
        protected List<PostalCode.PostTown.PostTownName> postTownName;
        @XmlElement(name = "PostTownSuffix")
        protected PostalCode.PostTown.PostTownSuffix postTownSuffix;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public PostTown() {
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
         */
        public List<PostalCode.PostTown.PostTownName> getPostTownName() {
            if (postTownName == null) {
                postTownName = new ArrayList<PostalCode.PostTown.PostTownName>();
            }
            return this.postTownName;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link PostalCode.PostTown.PostTownSuffix}
         *     
         */
        public PostalCode.PostTown.PostTownSuffix getPostTownSuffix() {
            return postTownSuffix;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link PostalCode.PostTown.PostTownSuffix}
         *     
         */
        public void setPostTownSuffix(PostalCode.PostTown.PostTownSuffix value) {
            this.postTownSuffix = value;
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
            result = ((prime*result)+((postTownName == null)? 0 :postTownName.hashCode()));
            result = ((prime*result)+((postTownSuffix == null)? 0 :postTownSuffix.hashCode()));
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
            if ((obj instanceof PostalCode.PostTown) == false) {
                return false;
            }
            PostalCode.PostTown other = ((PostalCode.PostTown) obj);
            if (addressLine == null) {
                if (other.addressLine!= null) {
                    return false;
                }
            } else {
                if (addressLine.equals(other.addressLine) == false) {
                    return false;
                }
            }
            if (postTownName == null) {
                if (other.postTownName!= null) {
                    return false;
                }
            } else {
                if (postTownName.equals(other.postTownName) == false) {
                    return false;
                }
            }
            if (postTownSuffix == null) {
                if (other.postTownSuffix!= null) {
                    return false;
                }
            } else {
                if (postTownSuffix.equals(other.postTownSuffix) == false) {
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
         * Creates a new instance of {@link PostalCode.PostTown.PostTownName} and adds it to postTownName.
         * This method is a short version for:
         * <code>
         * PostTownName postTownName = new PostTownName();
         * this.getPostTownName().add(postTownName); </code>
         * 
         * 
         */
        public PostalCode.PostTown.PostTownName createAndAddPostTownName() {
            PostalCode.PostTown.PostTownName newValue = new PostalCode.PostTown.PostTownName();
            this.getPostTownName().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link PostalCode.PostTown.PostTownSuffix} and set it to postTownSuffix.
         * 
         * This method is a short version for:
         * <code>
         * PostTownSuffix postTownSuffix = new PostTownSuffix();
         * this.setPostTownSuffix(postTownSuffix); </code>
         * 
         * 
         */
        public PostalCode.PostTown.PostTownSuffix createAndSetPostTownSuffix() {
            PostalCode.PostTown.PostTownSuffix newValue = new PostalCode.PostTown.PostTownSuffix();
            this.setPostTownSuffix(newValue);
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
        public PostalCode.PostTown addToAddressLine(final AddressLine addressLine) {
            this.getAddressLine().add(addressLine);
            return this;
        }

        /**
         * Sets the value of the postTownName property Objects of the following type(s) are allowed in the list List<PostTownName>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withPostTownName} instead.
         * 
         * 
         * @param postTownName
         */
        public void setPostTownName(final List<PostalCode.PostTown.PostTownName> postTownName) {
            this.postTownName = postTownName;
        }

        /**
         * add a value to the postTownName property collection
         * 
         * @param postTownName
         *     Objects of the following type are allowed in the list: {@link PostalCode.PostTown.PostTownName}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public PostalCode.PostTown addToPostTownName(final PostalCode.PostTown.PostTownName postTownName) {
            this.getPostTownName().add(postTownName);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLine(List<AddressLine>)
         * 
         * @param addressLine
         *     required parameter
         */
        public PostalCode.PostTown withAddressLine(final List<AddressLine> addressLine) {
            this.setAddressLine(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setPostTownName(List<PostTownName>)
         * 
         * @param postTownName
         *     required parameter
         */
        public PostalCode.PostTown withPostTownName(final List<PostalCode.PostTown.PostTownName> postTownName) {
            this.setPostTownName(postTownName);
            return this;
        }

        /**
         * fluent setter
         * @see #setPostTownSuffix(PostTownSuffix)
         * 
         * @param postTownSuffix
         *     required parameter
         */
        public PostalCode.PostTown withPostTownSuffix(final PostalCode.PostTown.PostTownSuffix postTownSuffix) {
            this.setPostTownSuffix(postTownSuffix);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public PostalCode.PostTown withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        @Override
        public PostalCode.PostTown clone() {
            PostalCode.PostTown copy;
            try {
                copy = ((PostalCode.PostTown) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
            for (AddressLine iter: addressLine) {
                copy.addressLine.add(iter.clone());
            }
            copy.postTownName = new ArrayList<PostalCode.PostTown.PostTownName>((getPostTownName().size()));
            for (PostalCode.PostTown.PostTownName iter: postTownName) {
                copy.postTownName.add(iter.clone());
            }
            copy.postTownSuffix = ((postTownSuffix == null)?null:((PostalCode.PostTown.PostTownSuffix) postTownSuffix.clone()));
            return copy;
        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "PostTownName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class PostTownName implements Cloneable
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

            public PostTownName() {
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
                if ((obj instanceof PostalCode.PostTown.PostTownName) == false) {
                    return false;
                }
                PostalCode.PostTown.PostTownName other = ((PostalCode.PostTown.PostTownName) obj);
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
            public PostalCode.PostTown.PostTownName withContent(final String content) {
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
            public PostalCode.PostTown.PostTownName withUnderscore(final String underscore) {
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
            public PostalCode.PostTown.PostTownName withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public PostalCode.PostTown.PostTownName clone() {
                PostalCode.PostTown.PostTownName copy;
                try {
                    copy = ((PostalCode.PostTown.PostTownName) super.clone());
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
        @XmlRootElement(name = "PostTownSuffix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class PostTownSuffix implements Cloneable
        {

            @XmlValue
            protected String content;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public PostTownSuffix() {
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
                if ((obj instanceof PostalCode.PostTown.PostTownSuffix) == false) {
                    return false;
                }
                PostalCode.PostTown.PostTownSuffix other = ((PostalCode.PostTown.PostTownSuffix) obj);
                if (content == null) {
                    if (other.content!= null) {
                        return false;
                    }
                } else {
                    if (content.equals(other.content) == false) {
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
            public PostalCode.PostTown.PostTownSuffix withContent(final String content) {
                this.setContent(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setCode(String)
             * 
             * @param code
             *     required parameter
             */
            public PostalCode.PostTown.PostTownSuffix withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public PostalCode.PostTown.PostTownSuffix clone() {
                PostalCode.PostTown.PostTownSuffix copy;
                try {
                    copy = ((PostalCode.PostTown.PostTownSuffix) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                return copy;
            }

        }

    }

}
