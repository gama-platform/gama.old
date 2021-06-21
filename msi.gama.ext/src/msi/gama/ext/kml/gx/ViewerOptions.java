
package msi.gama.ext.kml.gx;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.AbstractObject;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ViewerOptionsType", propOrder = {
    "option"
})
@XmlRootElement(name = "ViewerOptions", namespace = "http://www.google.com/kml/ext/2.2")
public class ViewerOptions
    extends AbstractObject
    implements Cloneable
{

    @XmlElement(required = true)
    protected List<Option> option;

    /**
     * Value constructor with only mandatory fields
     * 
     * @param option
     *     required parameter
     */
    public ViewerOptions(final List<Option> option) {
        super();
        this.option = option;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private ViewerOptions() {
        super();
    }

    /**
     * 
     */
    public List<Option> getOption() {
        if (option == null) {
            option = new ArrayList<Option>();
        }
        return this.option;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((option == null)? 0 :option.hashCode()));
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
        if (super.equals(obj) == false) {
            return false;
        }
        if ((obj instanceof ViewerOptions) == false) {
            return false;
        }
        ViewerOptions other = ((ViewerOptions) obj);
        if (option == null) {
            if (other.option!= null) {
                return false;
            }
        } else {
            if (option.equals(other.option) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Option} and adds it to option.
     * This method is a short version for:
     * <code>
     * Option option = new Option();
     * this.getOption().add(option); </code>
     * 
     * 
     */
    public Option createAndAddOption() {
        Option newValue = new Option();
        this.getOption().add(newValue);
        return newValue;
    }

    /**
     * Sets the value of the option property Objects of the following type(s) are allowed in the list List<Option>.
     * <p>Note:
     * <p>This method does not make use of the fluent pattern.If you would like to make it fluent, use {@link #withOption} instead.
     * 
     * 
     * @param option
     */
    public void setOption(final List<Option> option) {
        this.option = option;
    }

    /**
     * add a value to the option property collection
     * 
     * @param option
     *     Objects of the following type are allowed in the list: {@link Option}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ViewerOptions addToOption(final Option option) {
        this.getOption().add(option);
        return this;
    }

    @Obvious
    @Override
    public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.setObjectSimpleExtension(objectSimpleExtension);
    }

    @Obvious
    @Override
    public ViewerOptions addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    @Override
    public ViewerOptions clone() {
        ViewerOptions copy;
        copy = ((ViewerOptions) super.clone());
        copy.option = new ArrayList<Option>((getOption().size()));
        for (Option iter: option) {
            copy.option.add(iter.clone());
        }
        return copy;
    }

}
