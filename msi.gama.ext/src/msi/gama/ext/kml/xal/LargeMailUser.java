
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
@XmlType(name = "LargeMailUserType", propOrder = {
    "addressLine",
    "largeMailUserName",
    "largeMailUserIdentifier",
    "buildingName",
    "department",
    "postBox",
    "thoroughfare",
    "postalCode",
    "any"
})
@XmlRootElement(name = "LargeMailUser", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
public class LargeMailUser implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "LargeMailUserName")
    protected List<LargeMailUser.LargeMailUserName> largeMailUserName;
    @XmlElement(name = "LargeMailUserIdentifier")
    protected LargeMailUser.LargeMailUserIdentifier largeMailUserIdentifier;
    @XmlElement(name = "BuildingName")
    protected List<BuildingName> buildingName;
    @XmlElement(name = "Department")
    protected Department department;
    @XmlElement(name = "PostBox")
    protected PostBox postBox;
    @XmlElement(name = "Thoroughfare")
    protected Thoroughfare thoroughfare;
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    protected String underscore;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    public LargeMailUser() {
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
    public List<LargeMailUser.LargeMailUserName> getLargeMailUserName() {
        if (largeMailUserName == null) {
            largeMailUserName = new ArrayList<LargeMailUser.LargeMailUserName>();
        }
        return this.largeMailUserName;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link LargeMailUser.LargeMailUserIdentifier}
     *     
     */
    public LargeMailUser.LargeMailUserIdentifier getLargeMailUserIdentifier() {
        return largeMailUserIdentifier;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link LargeMailUser.LargeMailUserIdentifier}
     *     
     */
    public void setLargeMailUserIdentifier(LargeMailUser.LargeMailUserIdentifier value) {
        this.largeMailUserIdentifier = value;
    }

    /**
     * 
     */
    public List<BuildingName> getBuildingName() {
        if (buildingName == null) {
            buildingName = new ArrayList<BuildingName>();
        }
        return this.buildingName;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Department}
     *     
     */
    public Department getDepartment() {
        return department;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Department}
     *     
     */
    public void setDepartment(Department value) {
        this.department = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostBox}
     *     
     */
    public PostBox getPostBox() {
        return postBox;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostBox}
     *     
     */
    public void setPostBox(PostBox value) {
        this.postBox = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Thoroughfare}
     *     
     */
    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Thoroughfare}
     *     
     */
    public void setThoroughfare(Thoroughfare value) {
        this.thoroughfare = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostalCode}
     *     
     */
    public PostalCode getPostalCode() {
        return postalCode;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostalCode}
     *     
     */
    public void setPostalCode(PostalCode value) {
        this.postalCode = value;
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
        result = ((prime*result)+((largeMailUserName == null)? 0 :largeMailUserName.hashCode()));
        result = ((prime*result)+((largeMailUserIdentifier == null)? 0 :largeMailUserIdentifier.hashCode()));
        result = ((prime*result)+((buildingName == null)? 0 :buildingName.hashCode()));
        result = ((prime*result)+((department == null)? 0 :department.hashCode()));
        result = ((prime*result)+((postBox == null)? 0 :postBox.hashCode()));
        result = ((prime*result)+((thoroughfare == null)? 0 :thoroughfare.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
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
        if ((obj instanceof LargeMailUser) == false) {
            return false;
        }
        LargeMailUser other = ((LargeMailUser) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (largeMailUserName == null) {
            if (other.largeMailUserName!= null) {
                return false;
            }
        } else {
            if (largeMailUserName.equals(other.largeMailUserName) == false) {
                return false;
            }
        }
        if (largeMailUserIdentifier == null) {
            if (other.largeMailUserIdentifier!= null) {
                return false;
            }
        } else {
            if (largeMailUserIdentifier.equals(other.largeMailUserIdentifier) == false) {
                return false;
            }
        }
        if (buildingName == null) {
            if (other.buildingName!= null) {
                return false;
            }
        } else {
            if (buildingName.equals(other.buildingName) == false) {
                return false;
            }
        }
        if (department == null) {
            if (other.department!= null) {
                return false;
            }
        } else {
            if (department.equals(other.department) == false) {
                return false;
            }
        }
        if (postBox == null) {
            if (other.postBox!= null) {
                return false;
            }
        } else {
            if (postBox.equals(other.postBox) == false) {
                return false;
            }
        }
        if (thoroughfare == null) {
            if (other.thoroughfare!= null) {
                return false;
            }
        } else {
            if (thoroughfare.equals(other.thoroughfare) == false) {
                return false;
            }
        }
        if (postalCode == null) {
            if (other.postalCode!= null) {
                return false;
            }
        } else {
            if (postalCode.equals(other.postalCode) == false) {
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
     * Creates a new instance of {@link LargeMailUser.LargeMailUserName} and adds it to largeMailUserName.
     * This method is a short version for:
     * <code>
     * LargeMailUserName largeMailUserName = new LargeMailUserName();
     * this.getLargeMailUserName().add(largeMailUserName); </code>
     * 
     * 
     */
    public LargeMailUser.LargeMailUserName createAndAddLargeMailUserName() {
        LargeMailUser.LargeMailUserName newValue = new LargeMailUser.LargeMailUserName();
        this.getLargeMailUserName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link LargeMailUser.LargeMailUserIdentifier} and set it to largeMailUserIdentifier.
     * 
     * This method is a short version for:
     * <code>
     * LargeMailUserIdentifier largeMailUserIdentifier = new LargeMailUserIdentifier();
     * this.setLargeMailUserIdentifier(largeMailUserIdentifier); </code>
     * 
     * 
     */
    public LargeMailUser.LargeMailUserIdentifier createAndSetLargeMailUserIdentifier() {
        LargeMailUser.LargeMailUserIdentifier newValue = new LargeMailUser.LargeMailUserIdentifier();
        this.setLargeMailUserIdentifier(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link BuildingName} and adds it to buildingName.
     * This method is a short version for:
     * <code>
     * BuildingName buildingName = new BuildingName();
     * this.getBuildingName().add(buildingName); </code>
     * 
     * 
     */
    public BuildingName createAndAddBuildingName() {
        BuildingName newValue = new BuildingName();
        this.getBuildingName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Department} and set it to department.
     * 
     * This method is a short version for:
     * <code>
     * Department department = new Department();
     * this.setDepartment(department); </code>
     * 
     * 
     */
    public Department createAndSetDepartment() {
        Department newValue = new Department();
        this.setDepartment(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostBox} and set it to postBox.
     * 
     * This method is a short version for:
     * <code>
     * PostBox postBox = new PostBox();
     * this.setPostBox(postBox); </code>
     * 
     * 
     * @param postBoxNumber
     *     required parameter
     */
    public PostBox createAndSetPostBox(final PostBox.PostBoxNumber postBoxNumber) {
        PostBox newValue = new PostBox(postBoxNumber);
        this.setPostBox(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Thoroughfare} and set it to thoroughfare.
     * 
     * This method is a short version for:
     * <code>
     * Thoroughfare thoroughfare = new Thoroughfare();
     * this.setThoroughfare(thoroughfare); </code>
     * 
     * 
     * @param postalCode
     *     required parameter
     * @param premise
     *     required parameter
     * @param firm
     *     required parameter
     * @param dependentLocality
     *     required parameter
     */
    public Thoroughfare createAndSetThoroughfare(final DependentLocality dependentLocality, final Premise premise, final Firm firm, final PostalCode postalCode) {
        Thoroughfare newValue = new Thoroughfare(dependentLocality, premise, firm, postalCode);
        this.setThoroughfare(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalCode} and set it to postalCode.
     * 
     * This method is a short version for:
     * <code>
     * PostalCode postalCode = new PostalCode();
     * this.setPostalCode(postalCode); </code>
     * 
     * 
     */
    public PostalCode createAndSetPostalCode() {
        PostalCode newValue = new PostalCode();
        this.setPostalCode(newValue);
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
    public LargeMailUser addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the largeMailUserName property Objects of the following type(s) are allowed in the list List<LargeMailUserName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withLargeMailUserName} instead.
     * 
     * 
     * @param largeMailUserName
     */
    public void setLargeMailUserName(final List<LargeMailUser.LargeMailUserName> largeMailUserName) {
        this.largeMailUserName = largeMailUserName;
    }

    /**
     * add a value to the largeMailUserName property collection
     * 
     * @param largeMailUserName
     *     Objects of the following type are allowed in the list: {@link LargeMailUser.LargeMailUserName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LargeMailUser addToLargeMailUserName(final LargeMailUser.LargeMailUserName largeMailUserName) {
        this.getLargeMailUserName().add(largeMailUserName);
        return this;
    }

    /**
     * Sets the value of the buildingName property Objects of the following type(s) are allowed in the list List<BuildingName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withBuildingName} instead.
     * 
     * 
     * @param buildingName
     */
    public void setBuildingName(final List<BuildingName> buildingName) {
        this.buildingName = buildingName;
    }

    /**
     * add a value to the buildingName property collection
     * 
     * @param buildingName
     *     Objects of the following type are allowed in the list: {@link BuildingName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public LargeMailUser addToBuildingName(final BuildingName buildingName) {
        this.getBuildingName().add(buildingName);
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
    public LargeMailUser addToAny(final Object any) {
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
    public LargeMailUser withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setLargeMailUserName(List<LargeMailUserName>)
     * 
     * @param largeMailUserName
     *     required parameter
     */
    public LargeMailUser withLargeMailUserName(final List<LargeMailUser.LargeMailUserName> largeMailUserName) {
        this.setLargeMailUserName(largeMailUserName);
        return this;
    }

    /**
     * fluent setter
     * @see #setLargeMailUserIdentifier(LargeMailUserIdentifier)
     * 
     * @param largeMailUserIdentifier
     *     required parameter
     */
    public LargeMailUser withLargeMailUserIdentifier(final LargeMailUser.LargeMailUserIdentifier largeMailUserIdentifier) {
        this.setLargeMailUserIdentifier(largeMailUserIdentifier);
        return this;
    }

    /**
     * fluent setter
     * @see #setBuildingName(List<BuildingName>)
     * 
     * @param buildingName
     *     required parameter
     */
    public LargeMailUser withBuildingName(final List<BuildingName> buildingName) {
        this.setBuildingName(buildingName);
        return this;
    }

    /**
     * fluent setter
     * @see #setDepartment(Department)
     * 
     * @param department
     *     required parameter
     */
    public LargeMailUser withDepartment(final Department department) {
        this.setDepartment(department);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostBox(PostBox)
     * 
     * @param postBox
     *     required parameter
     */
    public LargeMailUser withPostBox(final PostBox postBox) {
        this.setPostBox(postBox);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfare(Thoroughfare)
     * 
     * @param thoroughfare
     *     required parameter
     */
    public LargeMailUser withThoroughfare(final Thoroughfare thoroughfare) {
        this.setThoroughfare(thoroughfare);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalCode(PostalCode)
     * 
     * @param postalCode
     *     required parameter
     */
    public LargeMailUser withPostalCode(final PostalCode postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public LargeMailUser withAny(final List<Object> any) {
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
    public LargeMailUser withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    @Override
    public LargeMailUser clone() {
        LargeMailUser copy;
        try {
            copy = ((LargeMailUser) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.largeMailUserName = new ArrayList<LargeMailUser.LargeMailUserName>((getLargeMailUserName().size()));
        for (LargeMailUser.LargeMailUserName iter: largeMailUserName) {
            copy.largeMailUserName.add(iter.clone());
        }
        copy.largeMailUserIdentifier = ((largeMailUserIdentifier == null)?null:((LargeMailUser.LargeMailUserIdentifier) largeMailUserIdentifier.clone()));
        copy.buildingName = new ArrayList<BuildingName>((getBuildingName().size()));
        for (BuildingName iter: buildingName) {
            copy.buildingName.add(iter.clone());
        }
        copy.department = ((department == null)?null:((Department) department.clone()));
        copy.postBox = ((postBox == null)?null:((PostBox) postBox.clone()));
        copy.thoroughfare = ((thoroughfare == null)?null:((Thoroughfare) thoroughfare.clone()));
        copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
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
    @XmlRootElement(name = "LargeMailUserIdentifier", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class LargeMailUserIdentifier implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        protected String underscore;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public LargeMailUserIdentifier() {
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
        public String getIndicator() {
            return indicator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setIndicator(String value) {
            this.indicator = value;
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
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
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
            if ((obj instanceof LargeMailUser.LargeMailUserIdentifier) == false) {
                return false;
            }
            LargeMailUser.LargeMailUserIdentifier other = ((LargeMailUser.LargeMailUserIdentifier) obj);
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
            if (indicator == null) {
                if (other.indicator!= null) {
                    return false;
                }
            } else {
                if (indicator.equals(other.indicator) == false) {
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
        public LargeMailUser.LargeMailUserIdentifier withContent(final String content) {
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
        public LargeMailUser.LargeMailUserIdentifier withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicator(String)
         * 
         * @param indicator
         *     required parameter
         */
        public LargeMailUser.LargeMailUserIdentifier withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        /**
         * fluent setter
         * @see #setCode(String)
         * 
         * @param code
         *     required parameter
         */
        public LargeMailUser.LargeMailUserIdentifier withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public LargeMailUser.LargeMailUserIdentifier clone() {
            LargeMailUser.LargeMailUserIdentifier copy;
            try {
                copy = ((LargeMailUser.LargeMailUserIdentifier) super.clone());
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
    @XmlRootElement(name = "LargeMailUserName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class LargeMailUserName implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        protected String underscore;
        @XmlAttribute(name = "Code")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public LargeMailUserName() {
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
            if ((obj instanceof LargeMailUser.LargeMailUserName) == false) {
                return false;
            }
            LargeMailUser.LargeMailUserName other = ((LargeMailUser.LargeMailUserName) obj);
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
        public LargeMailUser.LargeMailUserName withContent(final String content) {
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
        public LargeMailUser.LargeMailUserName withUnderscore(final String underscore) {
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
        public LargeMailUser.LargeMailUserName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public LargeMailUser.LargeMailUserName clone() {
            LargeMailUser.LargeMailUserName copy;
            try {
                copy = ((LargeMailUser.LargeMailUserName) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }

}
