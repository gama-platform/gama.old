
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
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SubPremiseType", propOrder = {
    "addressLine",
    "subPremiseName",
    "subPremiseLocation",
    "subPremiseNumber",
    "subPremiseNumberPrefix",
    "subPremiseNumberSuffix",
    "buildingName",
    "firm",
    "mailStop",
    "postalCode",
    "subPremise",
    "any"
})
@XmlRootElement(name = "SubPremise", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
public class SubPremise implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElement(name = "SubPremiseName")
    protected List<SubPremise.SubPremiseName> subPremiseName;
    @XmlElement(name = "SubPremiseLocation")
    protected SubPremise.SubPremiseLocation subPremiseLocation;
    @XmlElement(name = "SubPremiseNumber")
    protected List<SubPremise.SubPremiseNumber> subPremiseNumber;
    @XmlElement(name = "SubPremiseNumberPrefix")
    protected List<SubPremise.SubPremiseNumberPrefix> subPremiseNumberPrefix;
    @XmlElement(name = "SubPremiseNumberSuffix")
    protected List<SubPremise.SubPremiseNumberSuffix> subPremiseNumberSuffix;
    @XmlElement(name = "BuildingName")
    protected List<BuildingName> buildingName;
    @XmlElement(name = "Firm")
    protected Firm firm;
    @XmlElement(name = "MailStop")
    protected MailStop mailStop;
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    @XmlElement(name = "SubPremise")
    protected SubPremise subPremise;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
     * 
     * @param subPremiseLocation
     *     required parameter
     */
    public SubPremise(final SubPremise.SubPremiseLocation subPremiseLocation) {
        super();
        this.subPremiseLocation = subPremiseLocation;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private SubPremise() {
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
    public List<SubPremise.SubPremiseName> getSubPremiseName() {
        if (subPremiseName == null) {
            subPremiseName = new ArrayList<SubPremise.SubPremiseName>();
        }
        return this.subPremiseName;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link SubPremise.SubPremiseLocation}
     *     
     */
    public SubPremise.SubPremiseLocation getSubPremiseLocation() {
        return subPremiseLocation;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link SubPremise.SubPremiseLocation}
     *     
     */
    public void setSubPremiseLocation(SubPremise.SubPremiseLocation value) {
        this.subPremiseLocation = value;
    }

    /**
     * 
     */
    public List<SubPremise.SubPremiseNumber> getSubPremiseNumber() {
        if (subPremiseNumber == null) {
            subPremiseNumber = new ArrayList<SubPremise.SubPremiseNumber>();
        }
        return this.subPremiseNumber;
    }

    /**
     * 
     */
    public List<SubPremise.SubPremiseNumberPrefix> getSubPremiseNumberPrefix() {
        if (subPremiseNumberPrefix == null) {
            subPremiseNumberPrefix = new ArrayList<SubPremise.SubPremiseNumberPrefix>();
        }
        return this.subPremiseNumberPrefix;
    }

    /**
     * 
     */
    public List<SubPremise.SubPremiseNumberSuffix> getSubPremiseNumberSuffix() {
        if (subPremiseNumberSuffix == null) {
            subPremiseNumberSuffix = new ArrayList<SubPremise.SubPremiseNumberSuffix>();
        }
        return this.subPremiseNumberSuffix;
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
     *     {@link Firm}
     *     
     */
    public Firm getFirm() {
        return firm;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Firm}
     *     
     */
    public void setFirm(Firm value) {
        this.firm = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link MailStop}
     *     
     */
    public MailStop getMailStop() {
        return mailStop;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link MailStop}
     *     
     */
    public void setMailStop(MailStop value) {
        this.mailStop = value;
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
     * @return
     *     possible object is
     *     {@link SubPremise}
     *     
     */
    public SubPremise getSubPremise() {
        return subPremise;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link SubPremise}
     *     
     */
    public void setSubPremise(SubPremise value) {
        this.subPremise = value;
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
        result = ((prime*result)+((subPremiseName == null)? 0 :subPremiseName.hashCode()));
        result = ((prime*result)+((subPremiseLocation == null)? 0 :subPremiseLocation.hashCode()));
        result = ((prime*result)+((subPremiseNumber == null)? 0 :subPremiseNumber.hashCode()));
        result = ((prime*result)+((subPremiseNumberPrefix == null)? 0 :subPremiseNumberPrefix.hashCode()));
        result = ((prime*result)+((subPremiseNumberSuffix == null)? 0 :subPremiseNumberSuffix.hashCode()));
        result = ((prime*result)+((buildingName == null)? 0 :buildingName.hashCode()));
        result = ((prime*result)+((firm == null)? 0 :firm.hashCode()));
        result = ((prime*result)+((mailStop == null)? 0 :mailStop.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((subPremise == null)? 0 :subPremise.hashCode()));
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
        if ((obj instanceof SubPremise) == false) {
            return false;
        }
        SubPremise other = ((SubPremise) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (subPremiseName == null) {
            if (other.subPremiseName!= null) {
                return false;
            }
        } else {
            if (subPremiseName.equals(other.subPremiseName) == false) {
                return false;
            }
        }
        if (subPremiseLocation == null) {
            if (other.subPremiseLocation!= null) {
                return false;
            }
        } else {
            if (subPremiseLocation.equals(other.subPremiseLocation) == false) {
                return false;
            }
        }
        if (subPremiseNumber == null) {
            if (other.subPremiseNumber!= null) {
                return false;
            }
        } else {
            if (subPremiseNumber.equals(other.subPremiseNumber) == false) {
                return false;
            }
        }
        if (subPremiseNumberPrefix == null) {
            if (other.subPremiseNumberPrefix!= null) {
                return false;
            }
        } else {
            if (subPremiseNumberPrefix.equals(other.subPremiseNumberPrefix) == false) {
                return false;
            }
        }
        if (subPremiseNumberSuffix == null) {
            if (other.subPremiseNumberSuffix!= null) {
                return false;
            }
        } else {
            if (subPremiseNumberSuffix.equals(other.subPremiseNumberSuffix) == false) {
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
        if (firm == null) {
            if (other.firm!= null) {
                return false;
            }
        } else {
            if (firm.equals(other.firm) == false) {
                return false;
            }
        }
        if (mailStop == null) {
            if (other.mailStop!= null) {
                return false;
            }
        } else {
            if (mailStop.equals(other.mailStop) == false) {
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
        if (subPremise == null) {
            if (other.subPremise!= null) {
                return false;
            }
        } else {
            if (subPremise.equals(other.subPremise) == false) {
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
     * Creates a new instance of {@link SubPremise.SubPremiseName} and adds it to subPremiseName.
     * This method is a short version for:
     * <code>
     * SubPremiseName subPremiseName = new SubPremiseName();
     * this.getSubPremiseName().add(subPremiseName); </code>
     * 
     * 
     */
    public SubPremise.SubPremiseName createAndAddSubPremiseName() {
        SubPremise.SubPremiseName newValue = new SubPremise.SubPremiseName();
        this.getSubPremiseName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SubPremise.SubPremiseLocation} and set it to subPremiseLocation.
     * 
     * This method is a short version for:
     * <code>
     * SubPremiseLocation subPremiseLocation = new SubPremiseLocation();
     * this.setSubPremiseLocation(subPremiseLocation); </code>
     * 
     * 
     */
    public SubPremise.SubPremiseLocation createAndSetSubPremiseLocation() {
        SubPremise.SubPremiseLocation newValue = new SubPremise.SubPremiseLocation();
        this.setSubPremiseLocation(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SubPremise.SubPremiseNumber} and adds it to subPremiseNumber.
     * This method is a short version for:
     * <code>
     * SubPremiseNumber subPremiseNumber = new SubPremiseNumber();
     * this.getSubPremiseNumber().add(subPremiseNumber); </code>
     * 
     * 
     */
    public SubPremise.SubPremiseNumber createAndAddSubPremiseNumber() {
        SubPremise.SubPremiseNumber newValue = new SubPremise.SubPremiseNumber();
        this.getSubPremiseNumber().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SubPremise.SubPremiseNumberPrefix} and adds it to subPremiseNumberPrefix.
     * This method is a short version for:
     * <code>
     * SubPremiseNumberPrefix subPremiseNumberPrefix = new SubPremiseNumberPrefix();
     * this.getSubPremiseNumberPrefix().add(subPremiseNumberPrefix); </code>
     * 
     * 
     */
    public SubPremise.SubPremiseNumberPrefix createAndAddSubPremiseNumberPrefix() {
        SubPremise.SubPremiseNumberPrefix newValue = new SubPremise.SubPremiseNumberPrefix();
        this.getSubPremiseNumberPrefix().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link SubPremise.SubPremiseNumberSuffix} and adds it to subPremiseNumberSuffix.
     * This method is a short version for:
     * <code>
     * SubPremiseNumberSuffix subPremiseNumberSuffix = new SubPremiseNumberSuffix();
     * this.getSubPremiseNumberSuffix().add(subPremiseNumberSuffix); </code>
     * 
     * 
     */
    public SubPremise.SubPremiseNumberSuffix createAndAddSubPremiseNumberSuffix() {
        SubPremise.SubPremiseNumberSuffix newValue = new SubPremise.SubPremiseNumberSuffix();
        this.getSubPremiseNumberSuffix().add(newValue);
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
     * Creates a new instance of {@link Firm} and set it to firm.
     * 
     * This method is a short version for:
     * <code>
     * Firm firm = new Firm();
     * this.setFirm(firm); </code>
     * 
     * 
     */
    public Firm createAndSetFirm() {
        Firm newValue = new Firm();
        this.setFirm(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link MailStop} and set it to mailStop.
     * 
     * This method is a short version for:
     * <code>
     * MailStop mailStop = new MailStop();
     * this.setMailStop(mailStop); </code>
     * 
     * 
     */
    public MailStop createAndSetMailStop() {
        MailStop newValue = new MailStop();
        this.setMailStop(newValue);
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
     * Creates a new instance of {@link SubPremise} and set it to subPremise.
     * 
     * This method is a short version for:
     * <code>
     * SubPremise subPremise = new SubPremise();
     * this.setSubPremise(subPremise); </code>
     * 
     * 
     * @param subPremiseLocation
     *     required parameter
     */
    public SubPremise createAndSetSubPremise(final SubPremise.SubPremiseLocation subPremiseLocation) {
        SubPremise newValue = new SubPremise(subPremiseLocation);
        this.setSubPremise(newValue);
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
    public SubPremise addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the subPremiseName property Objects of the following type(s) are allowed in the list List<SubPremiseName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubPremiseName} instead.
     * 
     * 
     * @param subPremiseName
     */
    public void setSubPremiseName(final List<SubPremise.SubPremiseName> subPremiseName) {
        this.subPremiseName = subPremiseName;
    }

    /**
     * add a value to the subPremiseName property collection
     * 
     * @param subPremiseName
     *     Objects of the following type are allowed in the list: {@link SubPremise.SubPremiseName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public SubPremise addToSubPremiseName(final SubPremise.SubPremiseName subPremiseName) {
        this.getSubPremiseName().add(subPremiseName);
        return this;
    }

    /**
     * Sets the value of the subPremiseNumber property Objects of the following type(s) are allowed in the list List<SubPremiseNumber>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubPremiseNumber} instead.
     * 
     * 
     * @param subPremiseNumber
     */
    public void setSubPremiseNumber(final List<SubPremise.SubPremiseNumber> subPremiseNumber) {
        this.subPremiseNumber = subPremiseNumber;
    }

    /**
     * add a value to the subPremiseNumber property collection
     * 
     * @param subPremiseNumber
     *     Objects of the following type are allowed in the list: {@link SubPremise.SubPremiseNumber}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public SubPremise addToSubPremiseNumber(final SubPremise.SubPremiseNumber subPremiseNumber) {
        this.getSubPremiseNumber().add(subPremiseNumber);
        return this;
    }

    /**
     * Sets the value of the subPremiseNumberPrefix property Objects of the following type(s) are allowed in the list List<SubPremiseNumberPrefix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubPremiseNumberPrefix} instead.
     * 
     * 
     * @param subPremiseNumberPrefix
     */
    public void setSubPremiseNumberPrefix(final List<SubPremise.SubPremiseNumberPrefix> subPremiseNumberPrefix) {
        this.subPremiseNumberPrefix = subPremiseNumberPrefix;
    }

    /**
     * add a value to the subPremiseNumberPrefix property collection
     * 
     * @param subPremiseNumberPrefix
     *     Objects of the following type are allowed in the list: {@link SubPremise.SubPremiseNumberPrefix}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public SubPremise addToSubPremiseNumberPrefix(final SubPremise.SubPremiseNumberPrefix subPremiseNumberPrefix) {
        this.getSubPremiseNumberPrefix().add(subPremiseNumberPrefix);
        return this;
    }

    /**
     * Sets the value of the subPremiseNumberSuffix property Objects of the following type(s) are allowed in the list List<SubPremiseNumberSuffix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withSubPremiseNumberSuffix} instead.
     * 
     * 
     * @param subPremiseNumberSuffix
     */
    public void setSubPremiseNumberSuffix(final List<SubPremise.SubPremiseNumberSuffix> subPremiseNumberSuffix) {
        this.subPremiseNumberSuffix = subPremiseNumberSuffix;
    }

    /**
     * add a value to the subPremiseNumberSuffix property collection
     * 
     * @param subPremiseNumberSuffix
     *     Objects of the following type are allowed in the list: {@link SubPremise.SubPremiseNumberSuffix}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public SubPremise addToSubPremiseNumberSuffix(final SubPremise.SubPremiseNumberSuffix subPremiseNumberSuffix) {
        this.getSubPremiseNumberSuffix().add(subPremiseNumberSuffix);
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
    public SubPremise addToBuildingName(final BuildingName buildingName) {
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
    public SubPremise addToAny(final Object any) {
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
    public SubPremise withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubPremiseName(List<SubPremiseName>)
     * 
     * @param subPremiseName
     *     required parameter
     */
    public SubPremise withSubPremiseName(final List<SubPremise.SubPremiseName> subPremiseName) {
        this.setSubPremiseName(subPremiseName);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubPremiseNumber(List<SubPremiseNumber>)
     * 
     * @param subPremiseNumber
     *     required parameter
     */
    public SubPremise withSubPremiseNumber(final List<SubPremise.SubPremiseNumber> subPremiseNumber) {
        this.setSubPremiseNumber(subPremiseNumber);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubPremiseNumberPrefix(List<SubPremiseNumberPrefix>)
     * 
     * @param subPremiseNumberPrefix
     *     required parameter
     */
    public SubPremise withSubPremiseNumberPrefix(final List<SubPremise.SubPremiseNumberPrefix> subPremiseNumberPrefix) {
        this.setSubPremiseNumberPrefix(subPremiseNumberPrefix);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubPremiseNumberSuffix(List<SubPremiseNumberSuffix>)
     * 
     * @param subPremiseNumberSuffix
     *     required parameter
     */
    public SubPremise withSubPremiseNumberSuffix(final List<SubPremise.SubPremiseNumberSuffix> subPremiseNumberSuffix) {
        this.setSubPremiseNumberSuffix(subPremiseNumberSuffix);
        return this;
    }

    /**
     * fluent setter
     * @see #setBuildingName(List<BuildingName>)
     * 
     * @param buildingName
     *     required parameter
     */
    public SubPremise withBuildingName(final List<BuildingName> buildingName) {
        this.setBuildingName(buildingName);
        return this;
    }

    /**
     * fluent setter
     * @see #setFirm(Firm)
     * 
     * @param firm
     *     required parameter
     */
    public SubPremise withFirm(final Firm firm) {
        this.setFirm(firm);
        return this;
    }

    /**
     * fluent setter
     * @see #setMailStop(MailStop)
     * 
     * @param mailStop
     *     required parameter
     */
    public SubPremise withMailStop(final MailStop mailStop) {
        this.setMailStop(mailStop);
        return this;
    }

    /**
     * fluent setter
     * @see #setPostalCode(PostalCode)
     * 
     * @param postalCode
     *     required parameter
     */
    public SubPremise withPostalCode(final PostalCode postalCode) {
        this.setPostalCode(postalCode);
        return this;
    }

    /**
     * fluent setter
     * @see #setSubPremise(SubPremise)
     * 
     * @param subPremise
     *     required parameter
     */
    public SubPremise withSubPremise(final SubPremise subPremise) {
        this.setSubPremise(subPremise);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public SubPremise withAny(final List<Object> any) {
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
    public SubPremise withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    @Override
    public SubPremise clone() {
        SubPremise copy;
        try {
            copy = ((SubPremise) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.subPremiseName = new ArrayList<SubPremise.SubPremiseName>((getSubPremiseName().size()));
        for (SubPremise.SubPremiseName iter: subPremiseName) {
            copy.subPremiseName.add(iter.clone());
        }
        copy.subPremiseLocation = ((subPremiseLocation == null)?null:((SubPremise.SubPremiseLocation) subPremiseLocation.clone()));
        copy.subPremiseNumber = new ArrayList<SubPremise.SubPremiseNumber>((getSubPremiseNumber().size()));
        for (SubPremise.SubPremiseNumber iter: subPremiseNumber) {
            copy.subPremiseNumber.add(iter.clone());
        }
        copy.subPremiseNumberPrefix = new ArrayList<SubPremise.SubPremiseNumberPrefix>((getSubPremiseNumberPrefix().size()));
        for (SubPremise.SubPremiseNumberPrefix iter: subPremiseNumberPrefix) {
            copy.subPremiseNumberPrefix.add(iter.clone());
        }
        copy.subPremiseNumberSuffix = new ArrayList<SubPremise.SubPremiseNumberSuffix>((getSubPremiseNumberSuffix().size()));
        for (SubPremise.SubPremiseNumberSuffix iter: subPremiseNumberSuffix) {
            copy.subPremiseNumberSuffix.add(iter.clone());
        }
        copy.buildingName = new ArrayList<BuildingName>((getBuildingName().size()));
        for (BuildingName iter: buildingName) {
            copy.buildingName.add(iter.clone());
        }
        copy.firm = ((firm == null)?null:((Firm) firm.clone()));
        copy.mailStop = ((mailStop == null)?null:((MailStop) mailStop.clone()));
        copy.postalCode = ((postalCode == null)?null:((PostalCode) postalCode.clone()));
        copy.subPremise = ((subPremise == null)?null:((SubPremise) subPremise.clone()));
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
    @XmlRootElement(name = "SubPremiseLocation", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubPremiseLocation implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;

        public SubPremiseLocation() {
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
            if ((obj instanceof SubPremise.SubPremiseLocation) == false) {
                return false;
            }
            SubPremise.SubPremiseLocation other = ((SubPremise.SubPremiseLocation) obj);
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
        public SubPremise.SubPremiseLocation withContent(final String content) {
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
        public SubPremise.SubPremiseLocation withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public SubPremise.SubPremiseLocation clone() {
            SubPremise.SubPremiseLocation copy;
            try {
                copy = ((SubPremise.SubPremiseLocation) super.clone());
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
    @XmlRootElement(name = "SubPremiseName", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubPremiseName implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "TypeOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String typeOccurrence;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public SubPremiseName() {
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
        public String getTypeOccurrence() {
            return typeOccurrence;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setTypeOccurrence(String value) {
            this.typeOccurrence = value;
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
            result = ((prime*result)+((typeOccurrence == null)? 0 :typeOccurrence.hashCode()));
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
            if ((obj instanceof SubPremise.SubPremiseName) == false) {
                return false;
            }
            SubPremise.SubPremiseName other = ((SubPremise.SubPremiseName) obj);
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
            if (typeOccurrence == null) {
                if (other.typeOccurrence!= null) {
                    return false;
                }
            } else {
                if (typeOccurrence.equals(other.typeOccurrence) == false) {
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
        public SubPremise.SubPremiseName withContent(final String content) {
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
        public SubPremise.SubPremiseName withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        /**
         * fluent setter
         * @see #setTypeOccurrence(String)
         * 
         * @param typeOccurrence
         *     required parameter
         */
        public SubPremise.SubPremiseName withTypeOccurrence(final String typeOccurrence) {
            this.setTypeOccurrence(typeOccurrence);
            return this;
        }

        /**
         * fluent setter
         * @see #setCode(String)
         * 
         * @param code
         *     required parameter
         */
        public SubPremise.SubPremiseName withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public SubPremise.SubPremiseName clone() {
            SubPremise.SubPremiseName copy;
            try {
                copy = ((SubPremise.SubPremiseName) super.clone());
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
    @XmlRootElement(name = "SubPremiseNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubPremiseNumber implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        @XmlAttribute(name = "IndicatorOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String indicatorOccurrence;
        @XmlAttribute(name = "NumberTypeOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String numberTypeOccurrence;
        @XmlAttribute(name = "PremiseNumberSeparator")
        @XmlSchemaType(name = "anySimpleType")
        protected String premiseNumberSeparator;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public SubPremiseNumber() {
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
        public String getIndicatorOccurrence() {
            return indicatorOccurrence;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setIndicatorOccurrence(String value) {
            this.indicatorOccurrence = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getNumberTypeOccurrence() {
            return numberTypeOccurrence;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setNumberTypeOccurrence(String value) {
            this.numberTypeOccurrence = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getPremiseNumberSeparator() {
            return premiseNumberSeparator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setPremiseNumberSeparator(String value) {
            this.premiseNumberSeparator = value;
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
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
            result = ((prime*result)+((indicatorOccurrence == null)? 0 :indicatorOccurrence.hashCode()));
            result = ((prime*result)+((numberTypeOccurrence == null)? 0 :numberTypeOccurrence.hashCode()));
            result = ((prime*result)+((premiseNumberSeparator == null)? 0 :premiseNumberSeparator.hashCode()));
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
            if ((obj instanceof SubPremise.SubPremiseNumber) == false) {
                return false;
            }
            SubPremise.SubPremiseNumber other = ((SubPremise.SubPremiseNumber) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
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
            if (indicatorOccurrence == null) {
                if (other.indicatorOccurrence!= null) {
                    return false;
                }
            } else {
                if (indicatorOccurrence.equals(other.indicatorOccurrence) == false) {
                    return false;
                }
            }
            if (numberTypeOccurrence == null) {
                if (other.numberTypeOccurrence!= null) {
                    return false;
                }
            } else {
                if (numberTypeOccurrence.equals(other.numberTypeOccurrence) == false) {
                    return false;
                }
            }
            if (premiseNumberSeparator == null) {
                if (other.premiseNumberSeparator!= null) {
                    return false;
                }
            } else {
                if (premiseNumberSeparator.equals(other.premiseNumberSeparator) == false) {
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
        public SubPremise.SubPremiseNumber withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicator(String)
         * 
         * @param indicator
         *     required parameter
         */
        public SubPremise.SubPremiseNumber withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicatorOccurrence(String)
         * 
         * @param indicatorOccurrence
         *     required parameter
         */
        public SubPremise.SubPremiseNumber withIndicatorOccurrence(final String indicatorOccurrence) {
            this.setIndicatorOccurrence(indicatorOccurrence);
            return this;
        }

        /**
         * fluent setter
         * @see #setNumberTypeOccurrence(String)
         * 
         * @param numberTypeOccurrence
         *     required parameter
         */
        public SubPremise.SubPremiseNumber withNumberTypeOccurrence(final String numberTypeOccurrence) {
            this.setNumberTypeOccurrence(numberTypeOccurrence);
            return this;
        }

        /**
         * fluent setter
         * @see #setPremiseNumberSeparator(String)
         * 
         * @param premiseNumberSeparator
         *     required parameter
         */
        public SubPremise.SubPremiseNumber withPremiseNumberSeparator(final String premiseNumberSeparator) {
            this.setPremiseNumberSeparator(premiseNumberSeparator);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public SubPremise.SubPremiseNumber withUnderscore(final String underscore) {
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
        public SubPremise.SubPremiseNumber withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public SubPremise.SubPremiseNumber clone() {
            SubPremise.SubPremiseNumber copy;
            try {
                copy = ((SubPremise.SubPremiseNumber) super.clone());
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
    @XmlRootElement(name = "SubPremiseNumberPrefix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubPremiseNumberPrefix implements Cloneable
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

        public SubPremiseNumberPrefix() {
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
            if ((obj instanceof SubPremise.SubPremiseNumberPrefix) == false) {
                return false;
            }
            SubPremise.SubPremiseNumberPrefix other = ((SubPremise.SubPremiseNumberPrefix) obj);
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
        public SubPremise.SubPremiseNumberPrefix withContent(final String content) {
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
        public SubPremise.SubPremiseNumberPrefix withNumberPrefixSeparator(final String numberPrefixSeparator) {
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
        public SubPremise.SubPremiseNumberPrefix withUnderscore(final String underscore) {
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
        public SubPremise.SubPremiseNumberPrefix withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public SubPremise.SubPremiseNumberPrefix clone() {
            SubPremise.SubPremiseNumberPrefix copy;
            try {
                copy = ((SubPremise.SubPremiseNumberPrefix) super.clone());
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
    @XmlRootElement(name = "SubPremiseNumberSuffix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class SubPremiseNumberSuffix implements Cloneable
    {

        @XmlValue
        protected String content;
        @XmlAttribute(name = "NumberSuffixSeparator")
        @XmlSchemaType(name = "anySimpleType")
        protected String numberSuffixSeparator;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public SubPremiseNumberSuffix() {
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
        public String getNumberSuffixSeparator() {
            return numberSuffixSeparator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setNumberSuffixSeparator(String value) {
            this.numberSuffixSeparator = value;
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
            result = ((prime*result)+((numberSuffixSeparator == null)? 0 :numberSuffixSeparator.hashCode()));
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
            if ((obj instanceof SubPremise.SubPremiseNumberSuffix) == false) {
                return false;
            }
            SubPremise.SubPremiseNumberSuffix other = ((SubPremise.SubPremiseNumberSuffix) obj);
            if (content == null) {
                if (other.content!= null) {
                    return false;
                }
            } else {
                if (content.equals(other.content) == false) {
                    return false;
                }
            }
            if (numberSuffixSeparator == null) {
                if (other.numberSuffixSeparator!= null) {
                    return false;
                }
            } else {
                if (numberSuffixSeparator.equals(other.numberSuffixSeparator) == false) {
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
        public SubPremise.SubPremiseNumberSuffix withContent(final String content) {
            this.setContent(content);
            return this;
        }

        /**
         * fluent setter
         * @see #setNumberSuffixSeparator(String)
         * 
         * @param numberSuffixSeparator
         *     required parameter
         */
        public SubPremise.SubPremiseNumberSuffix withNumberSuffixSeparator(final String numberSuffixSeparator) {
            this.setNumberSuffixSeparator(numberSuffixSeparator);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public SubPremise.SubPremiseNumberSuffix withUnderscore(final String underscore) {
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
        public SubPremise.SubPremiseNumberSuffix withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public SubPremise.SubPremiseNumberSuffix clone() {
            SubPremise.SubPremiseNumberSuffix copy;
            try {
                copy = ((SubPremise.SubPremiseNumberSuffix) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            return copy;
        }

    }

}
