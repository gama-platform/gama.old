
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
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "addressLine",
    "thoroughfareNumberOrThoroughfareNumberRange",
    "thoroughfareNumberPrefix",
    "thoroughfareNumberSuffix",
    "thoroughfarePreDirection",
    "thoroughfareLeading",
    "thoroughfareName",
    "thoroughfareTrailing",
    "thoroughfarePostDirection",
    "dependentThoroughfare",
    "dependentLocality",
    "premise",
    "firm",
    "postalCode",
    "any"
})
@XmlRootElement(name = "Thoroughfare")
public class Thoroughfare implements Cloneable
{

    @XmlElement(name = "AddressLine")
    protected List<AddressLine> addressLine;
    @XmlElements({
        @XmlElement(name = "ThoroughfareNumberRange", type = Thoroughfare.ThoroughfareNumberRange.class),
        @XmlElement(name = "ThoroughfareNumber", type = ThoroughfareNumber.class)
    })
    protected List<Object> thoroughfareNumberOrThoroughfareNumberRange;
    @XmlElement(name = "ThoroughfareNumberPrefix")
    protected List<ThoroughfareNumberPrefix> thoroughfareNumberPrefix;
    @XmlElement(name = "ThoroughfareNumberSuffix")
    protected List<ThoroughfareNumberSuffix> thoroughfareNumberSuffix;
    @XmlElement(name = "ThoroughfarePreDirection")
    protected ThoroughfarePreDirection thoroughfarePreDirection;
    @XmlElement(name = "ThoroughfareLeadingType")
    protected ThoroughfareLeadingType thoroughfareLeading;
    @XmlElement(name = "ThoroughfareName")
    protected List<ThoroughfareName> thoroughfareName;
    @XmlElement(name = "ThoroughfareTrailingType")
    protected ThoroughfareTrailingType thoroughfareTrailing;
    @XmlElement(name = "ThoroughfarePostDirection")
    protected ThoroughfarePostDirection thoroughfarePostDirection;
    @XmlElement(name = "DependentThoroughfare")
    protected Thoroughfare.DependentThoroughfare dependentThoroughfare;
    @XmlElement(name = "DependentLocality")
    protected DependentLocality dependentLocality;
    @XmlElement(name = "Premise")
    protected Premise premise;
    @XmlElement(name = "Firm")
    protected Firm firm;
    @XmlElement(name = "PostalCode")
    protected PostalCode postalCode;
    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anySimpleType")
    protected String underscore;
    @XmlAttribute(name = "DependentThoroughfares")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String xalDependentThoroughfares;
    @XmlAttribute(name = "DependentThoroughfaresIndicator")
    @XmlSchemaType(name = "anySimpleType")
    protected String dependentThoroughfaresIndicator;
    @XmlAttribute(name = "DependentThoroughfaresConnector")
    @XmlSchemaType(name = "anySimpleType")
    protected String dependentThoroughfaresConnector;
    @XmlAttribute(name = "DependentThoroughfaresType")
    @XmlSchemaType(name = "anySimpleType")
    protected String dependentThoroughfares;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Value constructor with only mandatory fields
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
    public Thoroughfare(final DependentLocality dependentLocality, final Premise premise, final Firm firm, final PostalCode postalCode) {
        super();
        this.dependentLocality = dependentLocality;
        this.premise = premise;
        this.firm = firm;
        this.postalCode = postalCode;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private Thoroughfare() {
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
    public List<Object> getThoroughfareNumberOrThoroughfareNumberRange() {
        if (thoroughfareNumberOrThoroughfareNumberRange == null) {
            thoroughfareNumberOrThoroughfareNumberRange = new ArrayList<Object>();
        }
        return this.thoroughfareNumberOrThoroughfareNumberRange;
    }

    /**
     * 
     */
    public List<ThoroughfareNumberPrefix> getThoroughfareNumberPrefix() {
        if (thoroughfareNumberPrefix == null) {
            thoroughfareNumberPrefix = new ArrayList<ThoroughfareNumberPrefix>();
        }
        return this.thoroughfareNumberPrefix;
    }

    /**
     * 
     */
    public List<ThoroughfareNumberSuffix> getThoroughfareNumberSuffix() {
        if (thoroughfareNumberSuffix == null) {
            thoroughfareNumberSuffix = new ArrayList<ThoroughfareNumberSuffix>();
        }
        return this.thoroughfareNumberSuffix;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link ThoroughfarePreDirection}
     *     
     */
    public ThoroughfarePreDirection getThoroughfarePreDirection() {
        return thoroughfarePreDirection;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link ThoroughfarePreDirection}
     *     
     */
    public void setThoroughfarePreDirection(ThoroughfarePreDirection value) {
        this.thoroughfarePreDirection = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link ThoroughfareLeadingType}
     *     
     */
    public ThoroughfareLeadingType getThoroughfareLeading() {
        return thoroughfareLeading;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link ThoroughfareLeadingType}
     *     
     */
    public void setThoroughfareLeading(ThoroughfareLeadingType value) {
        this.thoroughfareLeading = value;
    }

    /**
     * 
     */
    public List<ThoroughfareName> getThoroughfareName() {
        if (thoroughfareName == null) {
            thoroughfareName = new ArrayList<ThoroughfareName>();
        }
        return this.thoroughfareName;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link ThoroughfareTrailingType}
     *     
     */
    public ThoroughfareTrailingType getThoroughfareTrailing() {
        return thoroughfareTrailing;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link ThoroughfareTrailingType}
     *     
     */
    public void setThoroughfareTrailing(ThoroughfareTrailingType value) {
        this.thoroughfareTrailing = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link ThoroughfarePostDirection}
     *     
     */
    public ThoroughfarePostDirection getThoroughfarePostDirection() {
        return thoroughfarePostDirection;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link ThoroughfarePostDirection}
     *     
     */
    public void setThoroughfarePostDirection(ThoroughfarePostDirection value) {
        this.thoroughfarePostDirection = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Thoroughfare.DependentThoroughfare}
     *     
     */
    public Thoroughfare.DependentThoroughfare getDependentThoroughfare() {
        return dependentThoroughfare;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link Thoroughfare.DependentThoroughfare}
     *     
     */
    public void setDependentThoroughfare(Thoroughfare.DependentThoroughfare value) {
        this.dependentThoroughfare = value;
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
    public String getXalDependentThoroughfares() {
        return xalDependentThoroughfares;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setXalDependentThoroughfares(String value) {
        this.xalDependentThoroughfares = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getDependentThoroughfaresIndicator() {
        return dependentThoroughfaresIndicator;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setDependentThoroughfaresIndicator(String value) {
        this.dependentThoroughfaresIndicator = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getDependentThoroughfaresConnector() {
        return dependentThoroughfaresConnector;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setDependentThoroughfaresConnector(String value) {
        this.dependentThoroughfaresConnector = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getDependentThoroughfares() {
        return dependentThoroughfares;
    }

    /**
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setDependentThoroughfares(String value) {
        this.dependentThoroughfares = value;
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
        result = ((prime*result)+((thoroughfareNumberOrThoroughfareNumberRange == null)? 0 :thoroughfareNumberOrThoroughfareNumberRange.hashCode()));
        result = ((prime*result)+((thoroughfareNumberPrefix == null)? 0 :thoroughfareNumberPrefix.hashCode()));
        result = ((prime*result)+((thoroughfareNumberSuffix == null)? 0 :thoroughfareNumberSuffix.hashCode()));
        result = ((prime*result)+((thoroughfarePreDirection == null)? 0 :thoroughfarePreDirection.hashCode()));
        result = ((prime*result)+((thoroughfareLeading == null)? 0 :thoroughfareLeading.hashCode()));
        result = ((prime*result)+((thoroughfareName == null)? 0 :thoroughfareName.hashCode()));
        result = ((prime*result)+((thoroughfareTrailing == null)? 0 :thoroughfareTrailing.hashCode()));
        result = ((prime*result)+((thoroughfarePostDirection == null)? 0 :thoroughfarePostDirection.hashCode()));
        result = ((prime*result)+((dependentThoroughfare == null)? 0 :dependentThoroughfare.hashCode()));
        result = ((prime*result)+((dependentLocality == null)? 0 :dependentLocality.hashCode()));
        result = ((prime*result)+((premise == null)? 0 :premise.hashCode()));
        result = ((prime*result)+((firm == null)? 0 :firm.hashCode()));
        result = ((prime*result)+((postalCode == null)? 0 :postalCode.hashCode()));
        result = ((prime*result)+((any == null)? 0 :any.hashCode()));
        result = ((prime*result)+((underscore == null)? 0 :underscore.hashCode()));
        result = ((prime*result)+((xalDependentThoroughfares == null)? 0 :xalDependentThoroughfares.hashCode()));
        result = ((prime*result)+((dependentThoroughfaresIndicator == null)? 0 :dependentThoroughfaresIndicator.hashCode()));
        result = ((prime*result)+((dependentThoroughfaresConnector == null)? 0 :dependentThoroughfaresConnector.hashCode()));
        result = ((prime*result)+((dependentThoroughfares == null)? 0 :dependentThoroughfares.hashCode()));
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
        if ((obj instanceof Thoroughfare) == false) {
            return false;
        }
        Thoroughfare other = ((Thoroughfare) obj);
        if (addressLine == null) {
            if (other.addressLine!= null) {
                return false;
            }
        } else {
            if (addressLine.equals(other.addressLine) == false) {
                return false;
            }
        }
        if (thoroughfareNumberOrThoroughfareNumberRange == null) {
            if (other.thoroughfareNumberOrThoroughfareNumberRange!= null) {
                return false;
            }
        } else {
            if (thoroughfareNumberOrThoroughfareNumberRange.equals(other.thoroughfareNumberOrThoroughfareNumberRange) == false) {
                return false;
            }
        }
        if (thoroughfareNumberPrefix == null) {
            if (other.thoroughfareNumberPrefix!= null) {
                return false;
            }
        } else {
            if (thoroughfareNumberPrefix.equals(other.thoroughfareNumberPrefix) == false) {
                return false;
            }
        }
        if (thoroughfareNumberSuffix == null) {
            if (other.thoroughfareNumberSuffix!= null) {
                return false;
            }
        } else {
            if (thoroughfareNumberSuffix.equals(other.thoroughfareNumberSuffix) == false) {
                return false;
            }
        }
        if (thoroughfarePreDirection == null) {
            if (other.thoroughfarePreDirection!= null) {
                return false;
            }
        } else {
            if (thoroughfarePreDirection.equals(other.thoroughfarePreDirection) == false) {
                return false;
            }
        }
        if (thoroughfareLeading == null) {
            if (other.thoroughfareLeading!= null) {
                return false;
            }
        } else {
            if (thoroughfareLeading.equals(other.thoroughfareLeading) == false) {
                return false;
            }
        }
        if (thoroughfareName == null) {
            if (other.thoroughfareName!= null) {
                return false;
            }
        } else {
            if (thoroughfareName.equals(other.thoroughfareName) == false) {
                return false;
            }
        }
        if (thoroughfareTrailing == null) {
            if (other.thoroughfareTrailing!= null) {
                return false;
            }
        } else {
            if (thoroughfareTrailing.equals(other.thoroughfareTrailing) == false) {
                return false;
            }
        }
        if (thoroughfarePostDirection == null) {
            if (other.thoroughfarePostDirection!= null) {
                return false;
            }
        } else {
            if (thoroughfarePostDirection.equals(other.thoroughfarePostDirection) == false) {
                return false;
            }
        }
        if (dependentThoroughfare == null) {
            if (other.dependentThoroughfare!= null) {
                return false;
            }
        } else {
            if (dependentThoroughfare.equals(other.dependentThoroughfare) == false) {
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
        if (premise == null) {
            if (other.premise!= null) {
                return false;
            }
        } else {
            if (premise.equals(other.premise) == false) {
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
        if (xalDependentThoroughfares == null) {
            if (other.xalDependentThoroughfares!= null) {
                return false;
            }
        } else {
            if (xalDependentThoroughfares.equals(other.xalDependentThoroughfares) == false) {
                return false;
            }
        }
        if (dependentThoroughfaresIndicator == null) {
            if (other.dependentThoroughfaresIndicator!= null) {
                return false;
            }
        } else {
            if (dependentThoroughfaresIndicator.equals(other.dependentThoroughfaresIndicator) == false) {
                return false;
            }
        }
        if (dependentThoroughfaresConnector == null) {
            if (other.dependentThoroughfaresConnector!= null) {
                return false;
            }
        } else {
            if (dependentThoroughfaresConnector.equals(other.dependentThoroughfaresConnector) == false) {
                return false;
            }
        }
        if (dependentThoroughfares == null) {
            if (other.dependentThoroughfares!= null) {
                return false;
            }
        } else {
            if (dependentThoroughfares.equals(other.dependentThoroughfares) == false) {
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
     * Creates a new instance of {@link ThoroughfareNumberPrefix} and adds it to thoroughfareNumberPrefix.
     * This method is a short version for:
     * <code>
     * ThoroughfareNumberPrefix thoroughfareNumberPrefix = new ThoroughfareNumberPrefix();
     * this.getThoroughfareNumberPrefix().add(thoroughfareNumberPrefix); </code>
     * 
     * 
     */
    public ThoroughfareNumberPrefix createAndAddThoroughfareNumberPrefix() {
        ThoroughfareNumberPrefix newValue = new ThoroughfareNumberPrefix();
        this.getThoroughfareNumberPrefix().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfareNumberSuffix} and adds it to thoroughfareNumberSuffix.
     * This method is a short version for:
     * <code>
     * ThoroughfareNumberSuffix thoroughfareNumberSuffix = new ThoroughfareNumberSuffix();
     * this.getThoroughfareNumberSuffix().add(thoroughfareNumberSuffix); </code>
     * 
     * 
     */
    public ThoroughfareNumberSuffix createAndAddThoroughfareNumberSuffix() {
        ThoroughfareNumberSuffix newValue = new ThoroughfareNumberSuffix();
        this.getThoroughfareNumberSuffix().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfarePreDirection} and set it to thoroughfarePreDirection.
     * 
     * This method is a short version for:
     * <code>
     * ThoroughfarePreDirection thoroughfarePreDirection = new ThoroughfarePreDirection();
     * this.setThoroughfarePreDirection(thoroughfarePreDirection); </code>
     * 
     * 
     */
    public ThoroughfarePreDirection createAndSetThoroughfarePreDirection() {
        ThoroughfarePreDirection newValue = new ThoroughfarePreDirection();
        this.setThoroughfarePreDirection(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfareLeadingType} and set it to thoroughfareLeading.
     * 
     * This method is a short version for:
     * <code>
     * ThoroughfareLeadingType thoroughfareLeadingType = new ThoroughfareLeadingType();
     * this.setThoroughfareLeading(thoroughfareLeadingType); </code>
     * 
     * 
     */
    public ThoroughfareLeadingType createAndSetThoroughfareLeading() {
        ThoroughfareLeadingType newValue = new ThoroughfareLeadingType();
        this.setThoroughfareLeading(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfareName} and adds it to thoroughfareName.
     * This method is a short version for:
     * <code>
     * ThoroughfareName thoroughfareName = new ThoroughfareName();
     * this.getThoroughfareName().add(thoroughfareName); </code>
     * 
     * 
     */
    public ThoroughfareName createAndAddThoroughfareName() {
        ThoroughfareName newValue = new ThoroughfareName();
        this.getThoroughfareName().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfareTrailingType} and set it to thoroughfareTrailing.
     * 
     * This method is a short version for:
     * <code>
     * ThoroughfareTrailingType thoroughfareTrailingType = new ThoroughfareTrailingType();
     * this.setThoroughfareTrailing(thoroughfareTrailingType); </code>
     * 
     * 
     */
    public ThoroughfareTrailingType createAndSetThoroughfareTrailing() {
        ThoroughfareTrailingType newValue = new ThoroughfareTrailingType();
        this.setThoroughfareTrailing(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ThoroughfarePostDirection} and set it to thoroughfarePostDirection.
     * 
     * This method is a short version for:
     * <code>
     * ThoroughfarePostDirection thoroughfarePostDirection = new ThoroughfarePostDirection();
     * this.setThoroughfarePostDirection(thoroughfarePostDirection); </code>
     * 
     * 
     */
    public ThoroughfarePostDirection createAndSetThoroughfarePostDirection() {
        ThoroughfarePostDirection newValue = new ThoroughfarePostDirection();
        this.setThoroughfarePostDirection(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Thoroughfare.DependentThoroughfare} and set it to dependentThoroughfare.
     * 
     * This method is a short version for:
     * <code>
     * DependentThoroughfare dependentThoroughfare = new DependentThoroughfare();
     * this.setDependentThoroughfare(dependentThoroughfare); </code>
     * 
     * 
     */
    public Thoroughfare.DependentThoroughfare createAndSetDependentThoroughfare() {
        Thoroughfare.DependentThoroughfare newValue = new Thoroughfare.DependentThoroughfare();
        this.setDependentThoroughfare(newValue);
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
    public Thoroughfare addToAddressLine(final AddressLine addressLine) {
        this.getAddressLine().add(addressLine);
        return this;
    }

    /**
     * Sets the value of the thoroughfareNumberOrThoroughfareNumberRange property Objects of the following type(s) are allowed in the list List<Object>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withThoroughfareNumberOrThoroughfareNumberRange} instead.
     * 
     * 
     * @param thoroughfareNumberOrThoroughfareNumberRange
     */
    public void setThoroughfareNumberOrThoroughfareNumberRange(final List<Object> thoroughfareNumberOrThoroughfareNumberRange) {
        this.thoroughfareNumberOrThoroughfareNumberRange = thoroughfareNumberOrThoroughfareNumberRange;
    }

    /**
     * add a value to the thoroughfareNumberOrThoroughfareNumberRange property collection
     * 
     * @param thoroughfareNumberOrThoroughfareNumberRange
     *     Objects of the following type are allowed in the list: {@link Thoroughfare.ThoroughfareNumberRange}{@link ThoroughfareNumber}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Thoroughfare addToThoroughfareNumberOrThoroughfareNumberRange(final Object thoroughfareNumberOrThoroughfareNumberRange) {
        this.getThoroughfareNumberOrThoroughfareNumberRange().add(thoroughfareNumberOrThoroughfareNumberRange);
        return this;
    }

    /**
     * Sets the value of the thoroughfareNumberPrefix property Objects of the following type(s) are allowed in the list List<ThoroughfareNumberPrefix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withThoroughfareNumberPrefix} instead.
     * 
     * 
     * @param thoroughfareNumberPrefix
     */
    public void setThoroughfareNumberPrefix(final List<ThoroughfareNumberPrefix> thoroughfareNumberPrefix) {
        this.thoroughfareNumberPrefix = thoroughfareNumberPrefix;
    }

    /**
     * add a value to the thoroughfareNumberPrefix property collection
     * 
     * @param thoroughfareNumberPrefix
     *     Objects of the following type are allowed in the list: {@link ThoroughfareNumberPrefix}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Thoroughfare addToThoroughfareNumberPrefix(final ThoroughfareNumberPrefix thoroughfareNumberPrefix) {
        this.getThoroughfareNumberPrefix().add(thoroughfareNumberPrefix);
        return this;
    }

    /**
     * Sets the value of the thoroughfareNumberSuffix property Objects of the following type(s) are allowed in the list List<ThoroughfareNumberSuffix>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withThoroughfareNumberSuffix} instead.
     * 
     * 
     * @param thoroughfareNumberSuffix
     */
    public void setThoroughfareNumberSuffix(final List<ThoroughfareNumberSuffix> thoroughfareNumberSuffix) {
        this.thoroughfareNumberSuffix = thoroughfareNumberSuffix;
    }

    /**
     * add a value to the thoroughfareNumberSuffix property collection
     * 
     * @param thoroughfareNumberSuffix
     *     Objects of the following type are allowed in the list: {@link ThoroughfareNumberSuffix}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Thoroughfare addToThoroughfareNumberSuffix(final ThoroughfareNumberSuffix thoroughfareNumberSuffix) {
        this.getThoroughfareNumberSuffix().add(thoroughfareNumberSuffix);
        return this;
    }

    /**
     * Sets the value of the thoroughfareName property Objects of the following type(s) are allowed in the list List<ThoroughfareName>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withThoroughfareName} instead.
     * 
     * 
     * @param thoroughfareName
     */
    public void setThoroughfareName(final List<ThoroughfareName> thoroughfareName) {
        this.thoroughfareName = thoroughfareName;
    }

    /**
     * add a value to the thoroughfareName property collection
     * 
     * @param thoroughfareName
     *     Objects of the following type are allowed in the list: {@link ThoroughfareName}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Thoroughfare addToThoroughfareName(final ThoroughfareName thoroughfareName) {
        this.getThoroughfareName().add(thoroughfareName);
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
    public Thoroughfare addToAny(final Object any) {
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
    public Thoroughfare withAddressLine(final List<AddressLine> addressLine) {
        this.setAddressLine(addressLine);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareNumberOrThoroughfareNumberRange(List<Object>)
     * 
     * @param thoroughfareNumberOrThoroughfareNumberRange
     *     required parameter
     */
    public Thoroughfare withThoroughfareNumberOrThoroughfareNumberRange(final List<Object> thoroughfareNumberOrThoroughfareNumberRange) {
        this.setThoroughfareNumberOrThoroughfareNumberRange(thoroughfareNumberOrThoroughfareNumberRange);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareNumberPrefix(List<ThoroughfareNumberPrefix>)
     * 
     * @param thoroughfareNumberPrefix
     *     required parameter
     */
    public Thoroughfare withThoroughfareNumberPrefix(final List<ThoroughfareNumberPrefix> thoroughfareNumberPrefix) {
        this.setThoroughfareNumberPrefix(thoroughfareNumberPrefix);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareNumberSuffix(List<ThoroughfareNumberSuffix>)
     * 
     * @param thoroughfareNumberSuffix
     *     required parameter
     */
    public Thoroughfare withThoroughfareNumberSuffix(final List<ThoroughfareNumberSuffix> thoroughfareNumberSuffix) {
        this.setThoroughfareNumberSuffix(thoroughfareNumberSuffix);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfarePreDirection(ThoroughfarePreDirection)
     * 
     * @param thoroughfarePreDirection
     *     required parameter
     */
    public Thoroughfare withThoroughfarePreDirection(final ThoroughfarePreDirection thoroughfarePreDirection) {
        this.setThoroughfarePreDirection(thoroughfarePreDirection);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareLeading(ThoroughfareLeadingType)
     * 
     * @param thoroughfareLeading
     *     required parameter
     */
    public Thoroughfare withThoroughfareLeading(final ThoroughfareLeadingType thoroughfareLeading) {
        this.setThoroughfareLeading(thoroughfareLeading);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareName(List<ThoroughfareName>)
     * 
     * @param thoroughfareName
     *     required parameter
     */
    public Thoroughfare withThoroughfareName(final List<ThoroughfareName> thoroughfareName) {
        this.setThoroughfareName(thoroughfareName);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfareTrailing(ThoroughfareTrailingType)
     * 
     * @param thoroughfareTrailing
     *     required parameter
     */
    public Thoroughfare withThoroughfareTrailing(final ThoroughfareTrailingType thoroughfareTrailing) {
        this.setThoroughfareTrailing(thoroughfareTrailing);
        return this;
    }

    /**
     * fluent setter
     * @see #setThoroughfarePostDirection(ThoroughfarePostDirection)
     * 
     * @param thoroughfarePostDirection
     *     required parameter
     */
    public Thoroughfare withThoroughfarePostDirection(final ThoroughfarePostDirection thoroughfarePostDirection) {
        this.setThoroughfarePostDirection(thoroughfarePostDirection);
        return this;
    }

    /**
     * fluent setter
     * @see #setDependentThoroughfare(DependentThoroughfare)
     * 
     * @param dependentThoroughfare
     *     required parameter
     */
    public Thoroughfare withDependentThoroughfare(final Thoroughfare.DependentThoroughfare dependentThoroughfare) {
        this.setDependentThoroughfare(dependentThoroughfare);
        return this;
    }

    /**
     * fluent setter
     * @see #setAny(List<Object>)
     * 
     * @param any
     *     required parameter
     */
    public Thoroughfare withAny(final List<Object> any) {
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
    public Thoroughfare withUnderscore(final String underscore) {
        this.setUnderscore(underscore);
        return this;
    }

    /**
     * fluent setter
     * @see #setXalDependentThoroughfares(String)
     * 
     * @param xalDependentThoroughfares
     *     required parameter
     */
    public Thoroughfare withXalDependentThoroughfares(final String xalDependentThoroughfares) {
        this.setXalDependentThoroughfares(xalDependentThoroughfares);
        return this;
    }

    /**
     * fluent setter
     * @see #setDependentThoroughfaresIndicator(String)
     * 
     * @param dependentThoroughfaresIndicator
     *     required parameter
     */
    public Thoroughfare withDependentThoroughfaresIndicator(final String dependentThoroughfaresIndicator) {
        this.setDependentThoroughfaresIndicator(dependentThoroughfaresIndicator);
        return this;
    }

    /**
     * fluent setter
     * @see #setDependentThoroughfaresConnector(String)
     * 
     * @param dependentThoroughfaresConnector
     *     required parameter
     */
    public Thoroughfare withDependentThoroughfaresConnector(final String dependentThoroughfaresConnector) {
        this.setDependentThoroughfaresConnector(dependentThoroughfaresConnector);
        return this;
    }

    /**
     * fluent setter
     * @see #setDependentThoroughfares(String)
     * 
     * @param dependentThoroughfares
     *     required parameter
     */
    public Thoroughfare withDependentThoroughfares(final String dependentThoroughfares) {
        this.setDependentThoroughfares(dependentThoroughfares);
        return this;
    }

    @Override
    public Thoroughfare clone() {
        Thoroughfare copy;
        try {
            copy = ((Thoroughfare) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
        for (AddressLine iter: addressLine) {
            copy.addressLine.add(iter.clone());
        }
        copy.thoroughfareNumberOrThoroughfareNumberRange = new ArrayList<Object>((getThoroughfareNumberOrThoroughfareNumberRange().size()));
        for (Object iter: thoroughfareNumberOrThoroughfareNumberRange) {
            copy.thoroughfareNumberOrThoroughfareNumberRange.add(iter);
        }
        copy.thoroughfareNumberPrefix = new ArrayList<ThoroughfareNumberPrefix>((getThoroughfareNumberPrefix().size()));
        for (ThoroughfareNumberPrefix iter: thoroughfareNumberPrefix) {
            copy.thoroughfareNumberPrefix.add(iter.clone());
        }
        copy.thoroughfareNumberSuffix = new ArrayList<ThoroughfareNumberSuffix>((getThoroughfareNumberSuffix().size()));
        for (ThoroughfareNumberSuffix iter: thoroughfareNumberSuffix) {
            copy.thoroughfareNumberSuffix.add(iter.clone());
        }
        copy.thoroughfarePreDirection = ((thoroughfarePreDirection == null)?null:((ThoroughfarePreDirection) thoroughfarePreDirection.clone()));
        copy.thoroughfareLeading = ((thoroughfareLeading == null)?null:((ThoroughfareLeadingType) thoroughfareLeading.clone()));
        copy.thoroughfareName = new ArrayList<ThoroughfareName>((getThoroughfareName().size()));
        for (ThoroughfareName iter: thoroughfareName) {
            copy.thoroughfareName.add(iter.clone());
        }
        copy.thoroughfareTrailing = ((thoroughfareTrailing == null)?null:((ThoroughfareTrailingType) thoroughfareTrailing.clone()));
        copy.thoroughfarePostDirection = ((thoroughfarePostDirection == null)?null:((ThoroughfarePostDirection) thoroughfarePostDirection.clone()));
        copy.dependentThoroughfare = ((dependentThoroughfare == null)?null:((Thoroughfare.DependentThoroughfare) dependentThoroughfare.clone()));
        copy.dependentLocality = ((dependentLocality == null)?null:((DependentLocality) dependentLocality.clone()));
        copy.premise = ((premise == null)?null:((Premise) premise.clone()));
        copy.firm = ((firm == null)?null:((Firm) firm.clone()));
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
        "addressLine",
        "thoroughfarePreDirection",
        "thoroughfareLeading",
        "thoroughfareName",
        "thoroughfareTrailing",
        "thoroughfarePostDirection",
        "any"
    })
    @XmlRootElement(name = "DependentThoroughfare", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class DependentThoroughfare implements Cloneable
    {

        @XmlElement(name = "AddressLine")
        protected List<AddressLine> addressLine;
        @XmlElement(name = "ThoroughfarePreDirection")
        protected ThoroughfarePreDirection thoroughfarePreDirection;
        @XmlElement(name = "ThoroughfareLeadingType")
        protected ThoroughfareLeadingType thoroughfareLeading;
        @XmlElement(name = "ThoroughfareName")
        protected List<ThoroughfareName> thoroughfareName;
        @XmlElement(name = "ThoroughfareTrailingType")
        protected ThoroughfareTrailingType thoroughfareTrailing;
        @XmlElement(name = "ThoroughfarePostDirection")
        protected ThoroughfarePostDirection thoroughfarePostDirection;
        @XmlAnyElement(lax = true)
        protected List<Object> any;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        public DependentThoroughfare() {
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
         *     {@link ThoroughfarePreDirection}
         *     
         */
        public ThoroughfarePreDirection getThoroughfarePreDirection() {
            return thoroughfarePreDirection;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link ThoroughfarePreDirection}
         *     
         */
        public void setThoroughfarePreDirection(ThoroughfarePreDirection value) {
            this.thoroughfarePreDirection = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link ThoroughfareLeadingType}
         *     
         */
        public ThoroughfareLeadingType getThoroughfareLeading() {
            return thoroughfareLeading;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link ThoroughfareLeadingType}
         *     
         */
        public void setThoroughfareLeading(ThoroughfareLeadingType value) {
            this.thoroughfareLeading = value;
        }

        /**
         * 
         */
        public List<ThoroughfareName> getThoroughfareName() {
            if (thoroughfareName == null) {
                thoroughfareName = new ArrayList<ThoroughfareName>();
            }
            return this.thoroughfareName;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link ThoroughfareTrailingType}
         *     
         */
        public ThoroughfareTrailingType getThoroughfareTrailing() {
            return thoroughfareTrailing;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link ThoroughfareTrailingType}
         *     
         */
        public void setThoroughfareTrailing(ThoroughfareTrailingType value) {
            this.thoroughfareTrailing = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link ThoroughfarePostDirection}
         *     
         */
        public ThoroughfarePostDirection getThoroughfarePostDirection() {
            return thoroughfarePostDirection;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link ThoroughfarePostDirection}
         *     
         */
        public void setThoroughfarePostDirection(ThoroughfarePostDirection value) {
            this.thoroughfarePostDirection = value;
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
            result = ((prime*result)+((thoroughfarePreDirection == null)? 0 :thoroughfarePreDirection.hashCode()));
            result = ((prime*result)+((thoroughfareLeading == null)? 0 :thoroughfareLeading.hashCode()));
            result = ((prime*result)+((thoroughfareName == null)? 0 :thoroughfareName.hashCode()));
            result = ((prime*result)+((thoroughfareTrailing == null)? 0 :thoroughfareTrailing.hashCode()));
            result = ((prime*result)+((thoroughfarePostDirection == null)? 0 :thoroughfarePostDirection.hashCode()));
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
            if ((obj instanceof Thoroughfare.DependentThoroughfare) == false) {
                return false;
            }
            Thoroughfare.DependentThoroughfare other = ((Thoroughfare.DependentThoroughfare) obj);
            if (addressLine == null) {
                if (other.addressLine!= null) {
                    return false;
                }
            } else {
                if (addressLine.equals(other.addressLine) == false) {
                    return false;
                }
            }
            if (thoroughfarePreDirection == null) {
                if (other.thoroughfarePreDirection!= null) {
                    return false;
                }
            } else {
                if (thoroughfarePreDirection.equals(other.thoroughfarePreDirection) == false) {
                    return false;
                }
            }
            if (thoroughfareLeading == null) {
                if (other.thoroughfareLeading!= null) {
                    return false;
                }
            } else {
                if (thoroughfareLeading.equals(other.thoroughfareLeading) == false) {
                    return false;
                }
            }
            if (thoroughfareName == null) {
                if (other.thoroughfareName!= null) {
                    return false;
                }
            } else {
                if (thoroughfareName.equals(other.thoroughfareName) == false) {
                    return false;
                }
            }
            if (thoroughfareTrailing == null) {
                if (other.thoroughfareTrailing!= null) {
                    return false;
                }
            } else {
                if (thoroughfareTrailing.equals(other.thoroughfareTrailing) == false) {
                    return false;
                }
            }
            if (thoroughfarePostDirection == null) {
                if (other.thoroughfarePostDirection!= null) {
                    return false;
                }
            } else {
                if (thoroughfarePostDirection.equals(other.thoroughfarePostDirection) == false) {
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
         * Creates a new instance of {@link ThoroughfarePreDirection} and set it to thoroughfarePreDirection.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfarePreDirection thoroughfarePreDirection = new ThoroughfarePreDirection();
         * this.setThoroughfarePreDirection(thoroughfarePreDirection); </code>
         * 
         * 
         */
        public ThoroughfarePreDirection createAndSetThoroughfarePreDirection() {
            ThoroughfarePreDirection newValue = new ThoroughfarePreDirection();
            this.setThoroughfarePreDirection(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link ThoroughfareLeadingType} and set it to thoroughfareLeading.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfareLeadingType thoroughfareLeadingType = new ThoroughfareLeadingType();
         * this.setThoroughfareLeading(thoroughfareLeadingType); </code>
         * 
         * 
         */
        public ThoroughfareLeadingType createAndSetThoroughfareLeading() {
            ThoroughfareLeadingType newValue = new ThoroughfareLeadingType();
            this.setThoroughfareLeading(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link ThoroughfareName} and adds it to thoroughfareName.
         * This method is a short version for:
         * <code>
         * ThoroughfareName thoroughfareName = new ThoroughfareName();
         * this.getThoroughfareName().add(thoroughfareName); </code>
         * 
         * 
         */
        public ThoroughfareName createAndAddThoroughfareName() {
            ThoroughfareName newValue = new ThoroughfareName();
            this.getThoroughfareName().add(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link ThoroughfareTrailingType} and set it to thoroughfareTrailing.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfareTrailingType thoroughfareTrailingType = new ThoroughfareTrailingType();
         * this.setThoroughfareTrailing(thoroughfareTrailingType); </code>
         * 
         * 
         */
        public ThoroughfareTrailingType createAndSetThoroughfareTrailing() {
            ThoroughfareTrailingType newValue = new ThoroughfareTrailingType();
            this.setThoroughfareTrailing(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link ThoroughfarePostDirection} and set it to thoroughfarePostDirection.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfarePostDirection thoroughfarePostDirection = new ThoroughfarePostDirection();
         * this.setThoroughfarePostDirection(thoroughfarePostDirection); </code>
         * 
         * 
         */
        public ThoroughfarePostDirection createAndSetThoroughfarePostDirection() {
            ThoroughfarePostDirection newValue = new ThoroughfarePostDirection();
            this.setThoroughfarePostDirection(newValue);
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
        public Thoroughfare.DependentThoroughfare addToAddressLine(final AddressLine addressLine) {
            this.getAddressLine().add(addressLine);
            return this;
        }

        /**
         * Sets the value of the thoroughfareName property Objects of the following type(s) are allowed in the list List<ThoroughfareName>.
         * <p>Note:
         * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withThoroughfareName} instead.
         * 
         * 
         * @param thoroughfareName
         */
        public void setThoroughfareName(final List<ThoroughfareName> thoroughfareName) {
            this.thoroughfareName = thoroughfareName;
        }

        /**
         * add a value to the thoroughfareName property collection
         * 
         * @param thoroughfareName
         *     Objects of the following type are allowed in the list: {@link ThoroughfareName}
         * @return
         *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
         */
        public Thoroughfare.DependentThoroughfare addToThoroughfareName(final ThoroughfareName thoroughfareName) {
            this.getThoroughfareName().add(thoroughfareName);
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
        public Thoroughfare.DependentThoroughfare addToAny(final Object any) {
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
        public Thoroughfare.DependentThoroughfare withAddressLine(final List<AddressLine> addressLine) {
            this.setAddressLine(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setThoroughfarePreDirection(ThoroughfarePreDirection)
         * 
         * @param thoroughfarePreDirection
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withThoroughfarePreDirection(final ThoroughfarePreDirection thoroughfarePreDirection) {
            this.setThoroughfarePreDirection(thoroughfarePreDirection);
            return this;
        }

        /**
         * fluent setter
         * @see #setThoroughfareLeading(ThoroughfareLeadingType)
         * 
         * @param thoroughfareLeading
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withThoroughfareLeading(final ThoroughfareLeadingType thoroughfareLeading) {
            this.setThoroughfareLeading(thoroughfareLeading);
            return this;
        }

        /**
         * fluent setter
         * @see #setThoroughfareName(List<ThoroughfareName>)
         * 
         * @param thoroughfareName
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withThoroughfareName(final List<ThoroughfareName> thoroughfareName) {
            this.setThoroughfareName(thoroughfareName);
            return this;
        }

        /**
         * fluent setter
         * @see #setThoroughfareTrailing(ThoroughfareTrailingType)
         * 
         * @param thoroughfareTrailing
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withThoroughfareTrailing(final ThoroughfareTrailingType thoroughfareTrailing) {
            this.setThoroughfareTrailing(thoroughfareTrailing);
            return this;
        }

        /**
         * fluent setter
         * @see #setThoroughfarePostDirection(ThoroughfarePostDirection)
         * 
         * @param thoroughfarePostDirection
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withThoroughfarePostDirection(final ThoroughfarePostDirection thoroughfarePostDirection) {
            this.setThoroughfarePostDirection(thoroughfarePostDirection);
            return this;
        }

        /**
         * fluent setter
         * @see #setAny(List<Object>)
         * 
         * @param any
         *     required parameter
         */
        public Thoroughfare.DependentThoroughfare withAny(final List<Object> any) {
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
        public Thoroughfare.DependentThoroughfare withUnderscore(final String underscore) {
            this.setUnderscore(underscore);
            return this;
        }

        @Override
        public Thoroughfare.DependentThoroughfare clone() {
            Thoroughfare.DependentThoroughfare copy;
            try {
                copy = ((Thoroughfare.DependentThoroughfare) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
            for (AddressLine iter: addressLine) {
                copy.addressLine.add(iter.clone());
            }
            copy.thoroughfarePreDirection = ((thoroughfarePreDirection == null)?null:((ThoroughfarePreDirection) thoroughfarePreDirection.clone()));
            copy.thoroughfareLeading = ((thoroughfareLeading == null)?null:((ThoroughfareLeadingType) thoroughfareLeading.clone()));
            copy.thoroughfareName = new ArrayList<ThoroughfareName>((getThoroughfareName().size()));
            for (ThoroughfareName iter: thoroughfareName) {
                copy.thoroughfareName.add(iter.clone());
            }
            copy.thoroughfareTrailing = ((thoroughfareTrailing == null)?null:((ThoroughfareTrailingType) thoroughfareTrailing.clone()));
            copy.thoroughfarePostDirection = ((thoroughfarePostDirection == null)?null:((ThoroughfarePostDirection) thoroughfarePostDirection.clone()));
            copy.any = new ArrayList<Object>((getAny().size()));
            for (Object iter: any) {
                copy.any.add(iter);
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
        "thoroughfareNumberFrom",
        "thoroughfareNumberTo"
    })
    @XmlRootElement(name = "ThoroughfareNumberRange", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
    public static class ThoroughfareNumberRange implements Cloneable
    {

        @XmlElement(name = "AddressLine")
        protected List<AddressLine> addressLine;
        @XmlElement(name = "ThoroughfareNumberFrom", required = true)
        protected Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom thoroughfareNumberFrom;
        @XmlElement(name = "ThoroughfareNumberTo", required = true)
        protected Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo thoroughfareNumberTo;
        @XmlAttribute(name = "RangeType")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String range;
        @XmlAttribute(name = "Indicator")
        @XmlSchemaType(name = "anySimpleType")
        protected String indicator;
        @XmlAttribute(name = "Separator")
        @XmlSchemaType(name = "anySimpleType")
        protected String separator;
        @XmlAttribute(name = "IndicatorOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String indicatorOccurrence;
        @XmlAttribute(name = "NumberRangeOccurrence")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        protected String numberRangeOccurrence;
        @XmlAttribute(name = "Type")
        @XmlSchemaType(name = "anySimpleType")
        protected String underscore;
        @XmlAttribute(name = "Code")
        @XmlSchemaType(name = "anySimpleType")
        protected String code;
        @XmlAnyAttribute
        private Map<QName, String> otherAttributes = new HashMap<QName, String>();

        /**
         * Value constructor with only mandatory fields
         * 
         * @param thoroughfareNumberTo
         *     required parameter
         * @param thoroughfareNumberFrom
         *     required parameter
         */
        public ThoroughfareNumberRange(final Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom thoroughfareNumberFrom, final Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo thoroughfareNumberTo) {
            super();
            this.thoroughfareNumberFrom = thoroughfareNumberFrom;
            this.thoroughfareNumberTo = thoroughfareNumberTo;
        }

        /**
         * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
         * 
         */
        @Deprecated
        private ThoroughfareNumberRange() {
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
         *     {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom}
         *     
         */
        public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom getThoroughfareNumberFrom() {
            return thoroughfareNumberFrom;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom}
         *     
         */
        public void setThoroughfareNumberFrom(Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom value) {
            this.thoroughfareNumberFrom = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo}
         *     
         */
        public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo getThoroughfareNumberTo() {
            return thoroughfareNumberTo;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo}
         *     
         */
        public void setThoroughfareNumberTo(Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo value) {
            this.thoroughfareNumberTo = value;
        }

        /**
         * 
         * @return
         *     possible object is
         *     {@link String}
         *     
         */
        public String getRange() {
            return range;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setRange(String value) {
            this.range = value;
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
        public String getSeparator() {
            return separator;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setSeparator(String value) {
            this.separator = value;
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
        public String getNumberRangeOccurrence() {
            return numberRangeOccurrence;
        }

        /**
         * 
         * @param value
         *     allowed object is
         *     {@link String}
         *     
         */
        public void setNumberRangeOccurrence(String value) {
            this.numberRangeOccurrence = value;
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
            result = ((prime*result)+((addressLine == null)? 0 :addressLine.hashCode()));
            result = ((prime*result)+((thoroughfareNumberFrom == null)? 0 :thoroughfareNumberFrom.hashCode()));
            result = ((prime*result)+((thoroughfareNumberTo == null)? 0 :thoroughfareNumberTo.hashCode()));
            result = ((prime*result)+((range == null)? 0 :range.hashCode()));
            result = ((prime*result)+((indicator == null)? 0 :indicator.hashCode()));
            result = ((prime*result)+((separator == null)? 0 :separator.hashCode()));
            result = ((prime*result)+((indicatorOccurrence == null)? 0 :indicatorOccurrence.hashCode()));
            result = ((prime*result)+((numberRangeOccurrence == null)? 0 :numberRangeOccurrence.hashCode()));
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
            if ((obj instanceof Thoroughfare.ThoroughfareNumberRange) == false) {
                return false;
            }
            Thoroughfare.ThoroughfareNumberRange other = ((Thoroughfare.ThoroughfareNumberRange) obj);
            if (addressLine == null) {
                if (other.addressLine!= null) {
                    return false;
                }
            } else {
                if (addressLine.equals(other.addressLine) == false) {
                    return false;
                }
            }
            if (thoroughfareNumberFrom == null) {
                if (other.thoroughfareNumberFrom!= null) {
                    return false;
                }
            } else {
                if (thoroughfareNumberFrom.equals(other.thoroughfareNumberFrom) == false) {
                    return false;
                }
            }
            if (thoroughfareNumberTo == null) {
                if (other.thoroughfareNumberTo!= null) {
                    return false;
                }
            } else {
                if (thoroughfareNumberTo.equals(other.thoroughfareNumberTo) == false) {
                    return false;
                }
            }
            if (range == null) {
                if (other.range!= null) {
                    return false;
                }
            } else {
                if (range.equals(other.range) == false) {
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
            if (separator == null) {
                if (other.separator!= null) {
                    return false;
                }
            } else {
                if (separator.equals(other.separator) == false) {
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
            if (numberRangeOccurrence == null) {
                if (other.numberRangeOccurrence!= null) {
                    return false;
                }
            } else {
                if (numberRangeOccurrence.equals(other.numberRangeOccurrence) == false) {
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
         * Creates a new instance of {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom} and set it to thoroughfareNumberFrom.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfareNumberFrom thoroughfareNumberFrom = new ThoroughfareNumberFrom();
         * this.setThoroughfareNumberFrom(thoroughfareNumberFrom); </code>
         * 
         * 
         */
        public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom createAndSetThoroughfareNumberFrom() {
            Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom newValue = new Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom();
            this.setThoroughfareNumberFrom(newValue);
            return newValue;
        }

        /**
         * Creates a new instance of {@link Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo} and set it to thoroughfareNumberTo.
         * 
         * This method is a short version for:
         * <code>
         * ThoroughfareNumberTo thoroughfareNumberTo = new ThoroughfareNumberTo();
         * this.setThoroughfareNumberTo(thoroughfareNumberTo); </code>
         * 
         * 
         */
        public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo createAndSetThoroughfareNumberTo() {
            Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo newValue = new Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo();
            this.setThoroughfareNumberTo(newValue);
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
        public Thoroughfare.ThoroughfareNumberRange addToAddressLine(final AddressLine addressLine) {
            this.getAddressLine().add(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setAddressLine(List<AddressLine>)
         * 
         * @param addressLine
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withAddressLine(final List<AddressLine> addressLine) {
            this.setAddressLine(addressLine);
            return this;
        }

        /**
         * fluent setter
         * @see #setRange(String)
         * 
         * @param range
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withRange(final String range) {
            this.setRange(range);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicator(String)
         * 
         * @param indicator
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withIndicator(final String indicator) {
            this.setIndicator(indicator);
            return this;
        }

        /**
         * fluent setter
         * @see #setSeparator(String)
         * 
         * @param separator
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withSeparator(final String separator) {
            this.setSeparator(separator);
            return this;
        }

        /**
         * fluent setter
         * @see #setIndicatorOccurrence(String)
         * 
         * @param indicatorOccurrence
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withIndicatorOccurrence(final String indicatorOccurrence) {
            this.setIndicatorOccurrence(indicatorOccurrence);
            return this;
        }

        /**
         * fluent setter
         * @see #setNumberRangeOccurrence(String)
         * 
         * @param numberRangeOccurrence
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withNumberRangeOccurrence(final String numberRangeOccurrence) {
            this.setNumberRangeOccurrence(numberRangeOccurrence);
            return this;
        }

        /**
         * fluent setter
         * @see #setUnderscore(String)
         * 
         * @param underscore
         *     required parameter
         */
        public Thoroughfare.ThoroughfareNumberRange withUnderscore(final String underscore) {
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
        public Thoroughfare.ThoroughfareNumberRange withCode(final String code) {
            this.setCode(code);
            return this;
        }

        @Override
        public Thoroughfare.ThoroughfareNumberRange clone() {
            Thoroughfare.ThoroughfareNumberRange copy;
            try {
                copy = ((Thoroughfare.ThoroughfareNumberRange) super.clone());
            } catch (CloneNotSupportedException _x) {
                throw new InternalError((_x.toString()));
            }
            copy.addressLine = new ArrayList<AddressLine>((getAddressLine().size()));
            for (AddressLine iter: addressLine) {
                copy.addressLine.add(iter.clone());
            }
            copy.thoroughfareNumberFrom = ((thoroughfareNumberFrom == null)?null:((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom) thoroughfareNumberFrom.clone()));
            copy.thoroughfareNumberTo = ((thoroughfareNumberTo == null)?null:((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo) thoroughfareNumberTo.clone()));
            return copy;
        }


        /**
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "content"
        })
        @XmlRootElement(name = "ThoroughfareNumberFrom", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class ThoroughfareNumberFrom implements Cloneable
        {

            @XmlElementRefs({
                @XmlElementRef(name = "ThoroughfareNumberPrefix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumberPrefix.class),
                @XmlElementRef(name = "AddressLine", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = AddressLine.class),
                @XmlElementRef(name = "ThoroughfareNumberSuffix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumberSuffix.class),
                @XmlElementRef(name = "ThoroughfareNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumber.class)
            })
            @XmlMixed
            protected List<Object> content;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public ThoroughfareNumberFrom() {
                super();
            }

            /**
             * 
             */
            public List<Object> getContent() {
                if (content == null) {
                    content = new ArrayList<Object>();
                }
                return this.content;
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
                if ((obj instanceof Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom) == false) {
                    return false;
                }
                Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom other = ((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom) obj);
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
             * Sets the value of the content property Objects of the following type(s) are allowed in the list List<Object>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withContent} instead.
             * 
             * 
             * @param content
             */
            public void setContent(final List<Object> content) {
                this.content = content;
            }

            /**
             * add a value to the content property collection
             * 
             * @param content
             *     Objects of the following type are allowed in the list: {@link String}{@link ThoroughfareNumberPrefix}{@link AddressLine}{@link ThoroughfareNumber}{@link ThoroughfareNumberSuffix}
             * @return
             *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
             */
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom addToContent(final Object content) {
                this.getContent().add(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setContent(List<Object>)
             * 
             * @param content
             *     required parameter
             */
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom withContent(final List<Object> content) {
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
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom clone() {
                Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom copy;
                try {
                    copy = ((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberFrom) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                copy.content = new ArrayList<Object>((getContent().size()));
                for (Object iter: content) {
                    copy.content.add(iter);
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
        @XmlRootElement(name = "ThoroughfareNumberTo", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0")
        public static class ThoroughfareNumberTo implements Cloneable
        {

            @XmlElementRefs({
                @XmlElementRef(name = "ThoroughfareNumberPrefix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumberPrefix.class),
                @XmlElementRef(name = "AddressLine", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = AddressLine.class),
                @XmlElementRef(name = "ThoroughfareNumberSuffix", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumberSuffix.class),
                @XmlElementRef(name = "ThoroughfareNumber", namespace = "urn:oasis:names:tc:ciq:xsdschema:xAL:2.0", type = ThoroughfareNumber.class)
            })
            @XmlMixed
            protected List<Object> content;
            @XmlAttribute(name = "Code")
            @XmlSchemaType(name = "anySimpleType")
            protected String code;
            @XmlAnyAttribute
            private Map<QName, String> otherAttributes = new HashMap<QName, String>();

            public ThoroughfareNumberTo() {
                super();
            }

            /**
             * 
             */
            public List<Object> getContent() {
                if (content == null) {
                    content = new ArrayList<Object>();
                }
                return this.content;
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
                if ((obj instanceof Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo) == false) {
                    return false;
                }
                Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo other = ((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo) obj);
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
             * Sets the value of the content property Objects of the following type(s) are allowed in the list List<Object>.
             * <p>Note:
             * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withContent} instead.
             * 
             * 
             * @param content
             */
            public void setContent(final List<Object> content) {
                this.content = content;
            }

            /**
             * add a value to the content property collection
             * 
             * @param content
             *     Objects of the following type are allowed in the list: {@link String}{@link ThoroughfareNumberPrefix}{@link AddressLine}{@link ThoroughfareNumber}{@link ThoroughfareNumberSuffix}
             * @return
             *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
             */
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo addToContent(final Object content) {
                this.getContent().add(content);
                return this;
            }

            /**
             * fluent setter
             * @see #setContent(List<Object>)
             * 
             * @param content
             *     required parameter
             */
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo withContent(final List<Object> content) {
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
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo withCode(final String code) {
                this.setCode(code);
                return this;
            }

            @Override
            public Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo clone() {
                Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo copy;
                try {
                    copy = ((Thoroughfare.ThoroughfareNumberRange.ThoroughfareNumberTo) super.clone());
                } catch (CloneNotSupportedException _x) {
                    throw new InternalError((_x.toString()));
                }
                copy.content = new ArrayList<Object>((getContent().size()));
                for (Object iter: content) {
                    copy.content.add(iter);
                }
                return copy;
            }

        }

    }

}
