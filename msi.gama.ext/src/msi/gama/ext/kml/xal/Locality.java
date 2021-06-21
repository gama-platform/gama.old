
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
    "localityName",
    "postBox",
    "largeMailUser",
    "postOffice",
    "postalRoute",
    "thoroughfare",
    "premise",
    "dependentLocality",
    "postalCode",
    "any"
})
@XmlRootElement(name = "Locality")
public class Locality implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "LocalityName")
    protected List<Locality.LocalityName> localityName;
    @XmlElement(name = "PostBox")
    protected PostBox postBox;
    @XmlElement(name = "LargeMailUser")
    protected LargeMailUser largeMailUser;
    @XmlElement(name = "PostOffice")
    protected PostOffice postOffice;
    @XmlElement(name = "PostalRoute")
    protected PostalRoute postalRoute;
    @XmlElement(name = "Thoroughfare")
    protected Thoroughfare thoroughfare;
    @XmlElement(name = "Premise")
    protected Premise premise;
    @XmlElement(name = "DependentLocality")
    protected DependentLocality dependentLocality;
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAttribute(name = "UsageType")
    @XmlSchemaType(name = "anySimpleType")
    protected String usage;
    @XmlAttribute(name = "Indicator")
    @XmlSchemaType(name = "anySimpleType")
    protected String indicator;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
     * 
     * @param postBox
     *     required parameter
     * @param postOffice
     *     required parameter
     * @param postalRoute
     *     required parameter
     * @param largeMailUser
     *     required parameter
     */
    public Locality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
        super();
        this.postBox = postBox;
        this.largeMailUser = largeMailUser;
        this.postOffice = postOffice;
        this.postalRoute = postalRoute;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private Locality() {
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
    public List<Locality.LocalityName> getLocalityName() {
        if (localityName == null) {
            localityName = new ArrayList<Locality.LocalityName>();
        }
        return this.localityName;
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
     *     {@link LargeMailUser}
     *     
     */
    public LargeMailUser getLargeMailUser() {
        return largeMailUser;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link LargeMailUser}
     *     
     */
    public void setLargeMailUser(LargeMailUser value) {
        this.largeMailUser = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostOffice}
     *     
     */
    public PostOffice getPostOffice() {
        return postOffice;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostOffice}
     *     
     */
    public void setPostOffice(PostOffice value) {
        this.postOffice = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link PostalRoute}
     *     
     */
    public PostalRoute getPostalRoute() {
        return postalRoute;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link PostalRoute}
     *     
     */
    public void setPostalRoute(PostalRoute value) {
        this.postalRoute = value;
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
     *     {@link Premise}
     *     
     */
    public Premise getPremise() {
        return premise;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Premise}
     *     
     */
    public void setPremise(Premise value) {
        this.premise = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link DependentLocality}
     *     
     */
    public DependentLocality getDependentLocality() {
        return dependentLocality;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link DependentLocality}
     *     
     */
    public void setDependentLocality(DependentLocality value) {
        this.dependentLocality = value;
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
     *     possible object is
     *     {@link String}
     *     
     */
    public String getUsage() {
        return usage;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setUsage(String value) {
        this.usage = value;
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
        result = ((prime*result)+((localityName == null)? 0 :localityName.hashCode()));
        result = ((prime*result)+((postBox == null)? 0 :postBox.hashCode()));
        result = ((prime*result)+((largeMailUser == null)? 0 :largeMailUser.hashCode()));
        result = ((prime*result)+((postOffice == null)? 0 :postOffice.hashCode()));
        result = ((prime*result)+((postalRoute == null)? 0 :postalRoute.hashCode()));
        result = ((prime*result)+((thoroughfare == null)? 0 :thoroughfare.hashCode()));
        result = ((prime*result)+((premise == null)? 0 :premise.hashCode()));
        result = ((prime*result)+((dependentLocality == null)? 0 :dependentLocality.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((usage == null)? 0 :usage.hashCode()));
        result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
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
        if ((obj instanceof Locality) == false) {
            return false;
        }
        Locality other = ((Locality) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (localityName == null) {
            if (other.localityName!= null) {
                return false;
            }
        } else {
            if (localityName.equals(other.localityName) == false) {
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
        if (largeMailUser == null) {
            if (other.largeMailUser!= null) {
                return false;
            }
        } else {
            if (largeMailUser.equals(other.largeMailUser) == false) {
                return false;
            }
        }
        if (postOffice == null) {
            if (other.postOffice!= null) {
                return false;
            }
        } else {
            if (postOffice.equals(other.postOffice) == false) {
                return false;
            }
        }
        if (postalRoute == null) {
            if (other.postalRoute!= null) {
                return false;
            }
        } else {
            if (postalRoute.equals(other.postalRoute) == false) {
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
        if (premise == null) {
            if (other.premise!= null) {
                return false;
            }
        } else {
            if (premise.equals(other.premise) == false) {
                return false;
            }
        }
        if (dependentLocality == null) {
            if (other.dependentLocality!= null) {
                return false;
            }
        } else {
            if (dependentLocality.equals(other.dependentLocality) == false) {
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
        if (usage == null) {
            if (other.usage!= null) {
                return false;
            }
        } else {
            if (usage.equals(other.usage) == false) {
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
     * Creates a new instance of {@link Locality.LocalityName} and adds it to localityName.
     * This method is a short version for:
     * <code>
     * LocalityName localityName = new LocalityName();
     * this.getLocalityName().add(localityName); </code>
     * 
     * 
     */
    public Locality.LocalityName createAndAddLocalityName() {
        Locality.LocalityName newValue = new Locality.LocalityName();
        this.getLocalityName().add(newValue);
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
     * Creates a new instance of {@link LargeMailUser} and set it to largeMailUser.
     * 
     * This method is a short version for:
     * <code>
     * LargeMailUser largeMailUser = new LargeMailUser();
     * this.setLargeMailUser(largeMailUser); </code>
     * 
     * 
     */
    public LargeMailUser createAndSetLargeMailUser() {
        LargeMailUser newValue = new LargeMailUser();
        this.setLargeMailUser(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostOffice} and set it to postOffice.
     * 
     * This method is a short version for:
     * <code>
     * PostOffice postOffice = new PostOffice();
     * this.setPostOffice(postOffice); </code>
     * 
     * 
     */
    public PostOffice createAndSetPostOffice() {
        PostOffice newValue = new PostOffice();
        this.setPostOffice(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PostalRoute} and set it to postalRoute.
     * 
     * This method is a short version for:
     * <code>
     * PostalRoute postalRoute = new PostalRoute();
     * this.setPostalRoute(postalRoute); </code>
     * 
     * 
     * @param postalRouteName
     *     required parameter
     * @param postalRouteNumber
     *     required parameter
     */
    public PostalRoute createAndSetPostalRoute(final List<PostalRoute.PostalRouteName> postalRouteName, final PostalRoute.PostalRouteNumber postalRouteNumber) {
        PostalRoute newValue = new PostalRoute(postalRouteName, postalRouteNumber);
        this.setPostalRoute(newValue);
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
     * Creates a new instance of {@link Premise} and set it to premise.
     * 
     * This method is a short version for:
     * <code>
     * Premise premise = new Premise();
     * this.setPremise(premise); </code>
     * 
     * 
     * @param premiseNumber
     *     required parameter
     * @param premiseNumberRange
     *     required parameter
     * @param premiseLocation
     *     required parameter
     */
    public Premise createAndSetPremise(final Premise.PremiseLocation premiseLocation, final List<PremiseNumber> premiseNumber, final Premise.PremiseNumberRange premiseNumberRange) {
        Premise newValue = new Premise(premiseLocation, premiseNumber, premiseNumberRange);
        this.setPremise(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link DependentLocality} and set it to dependentLocality.
     * 
     * This method is a short version for:
     * <code>
     * DependentLocality dependentLocality = new DependentLocality();
     * this.setDependentLocality(dependentLocality); </code>
     * 
     * 
     * @param postBox
     *     required parameter
     * @param postOffice
     *     required parameter
     * @param postalRoute
     *     required parameter
     * @param largeMailUser
     *     required parameter
     */
    public DependentLocality createAndSetDependentLocality(final PostBox postBox, final LargeMailUser largeMailUser, final PostOffice postOffice, final PostalRoute postalRoute) {
        DependentLocality newValue = new DependentLocality(postBox, largeMailUser, postOffice, postalRoute);
        this.setDependentLocality(newValue);
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
    public Locality addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the localityName property Objects of the following type(s) are allowed in the list List<LocalityName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withLocalityName} instead.
     * 
     * 
     * @param localityName
     */
    public void setLocalityName(final List<Locality.LocalityName> localityName) {
        this.localityName = localityName;
    }

    /**
     * add a value to the localityName property collection
     * 
     * @param localityName
     *     Objects of the following type are allowed in the list: {@link Locality.LocalityName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Locality addToLocalityName(final Locality.LocalityName localityName) {
        this.getLocalityName().add(localityName);
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
    public Locality addToAny(final Object any) {
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
    public Locality withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setLocalityName(List<LocalityName>)
     * 
     * @param localityName
     *     required parameter
     */
    public Locality withLocalityName(final List<Locality.LocalityName> localityName) {
        this.setLocalityName(localityName);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfare(Thoroughfare)
     * 
     * @param thoroughfare
     *     required parameter
     */
    public Locality withThoroughfare(final Thoroughfare thoroughfare) {
        this.setThoroughfare(thoroughfare);
        return this;
    }

    /**
     * fluent setter
     * @see #setPremise(Premise)
     * 
     * @param premise
     *     required parameter
     */
    public Locality withPremise(final Premise premise) {
        this.setPremise(premise);
        return this;
    }

    /**
     * fluent setter
     * @see #setDependentLocality(DependentLocality)
     * 
     * @param dependentLocality
     *     required parameter
     */
    public Locality withDependentLocality(final DependentLocality dependentLocality) {
        this.setDependentLocality(dependentLocality);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalCode(PostalCode)
     * 
     * @param postalCode
     *     required parameter
     */
    public Locality withPostalCode(final PostalCode postalCode) {
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
    public Locality withAny(final List<Object> any) {
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
    public Locality withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter
     * @see #setUsage(String)
     * 
     * @param usage
     *     required parameter
     */
    public Locality withUsage(final String usage) {
        this.setUsage(usage);
        return this;
    }

    /**
     * fluent setter
     * @see #setIndicator(String)
     * 
     * @param indicator
     *     required parameter
     */
    public Locality withIndicator(final String indicator) {
        this.setIndicator(indicator);
        return this;
    }

    @Override
    public Locality clone() {
        Locality copy;
        try {
            copy = ((Locality) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.localityName = new ArrayList<Locality.LocalityName>((getLocalityName().size()));
        for (Locality.LocalityName iter: localityName) {
            copy.localityName.add(iter.clone());
        }
        copy.postBox = ((postBox == null)?null:((PostBox) postBox.clone()));
        copy.largeMailUser = ((largeMailUser == null)?null:((LargeMailUser) largeMailUser.clone()));
        copy.postOffice = ((postOffice == null)?null:((PostOffice) postOffice.clone()));
        copy.postalRoute = ((postalRoute == null)?null:((PostalRoute) postalRoute.clone()));
        copy.thoroughfare = ((thoroughfare == null)?null:((Thoroughfare) thoroughfare.clone()));
        copy.premise = ((premise == null)?null:((Premise) premise.clone()));
        copy.dependentLocality = ((dependentLocality == null)?null:((DependentLocality) dependentLocality.clone()));
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
    @XmlRootElement(name = "LocalityName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class LocalityName implements Cloneable
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

        public LocalityName() {
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
            if ((obj instanceof Locality.LocalityName) == false) {
                return false;
            }
            Locality.LocalityName other = ((Locality.LocalityName) obj);
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
        public Locality.LocalityName withContent(final String content) {
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
        public Locality.LocalityName withUnderscore(final String underscore) {
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
        public Locality.LocalityName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public Locality.LocalityName clone() {
            Locality.LocalityName copy;
            try {
                copy = ((Locality.LocalityName) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }

}
