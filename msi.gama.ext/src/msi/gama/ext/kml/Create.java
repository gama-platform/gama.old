
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <create>
 * <p>
 * Adds new elements to a Folder or Document that has already been loaded via a <NetworkLink>. 
 * The <targetHref> element in <Update> specifies the URL of the .kml or .kmz file 
 * that contained the original Folder or Document. Within that file, the Folder or 
 * Document that is to contain the new data must already have an explicit id defined 
 * for it. This id is referenced as the targetId attribute of the Folder or Document 
 * within <Create> that contains the element to be added. 
 * </p>
 * <p>
 * Once an object has been created and loaded into Google Earth, it takes on the URL 
 * of the original parent Document of Folder. To perform subsequent updates to objects 
 * added with this Update/Create mechanism, set <targetHref> to the URL of the original 
 * Document or Folder (not the URL of the file that loaded the intervening updates). 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CreateType", propOrder = {
    "container"
})
@XmlRootElement(name = "Create", namespace = "http://www.opengis.net/kml/2.2")
public class Create implements Cloneable
{

    /**
     * <Container>
     * <p>
     * This is an abstract element and cannot be used directly in a KML file. A Container 
     * element holds one or more Features and allows the creation of nested hierarchies. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;
     * <strong>&lt;!-- <em>Container</em> id="ID" --&gt;</strong>              &lt;!-- Document,Folder --&gt;                 
     *   &lt;!-- inherited from <em>Feature</em> element --&gt;
     *   &lt;name&gt;<em>...</em>&lt;/name&gt;                      &lt;!-- string --&gt;
     *   &lt;visibility&gt;1&lt;/visibility&gt;            &lt;!-- boolean --&gt;
     *   &lt;open&gt;0&lt;/open&gt;                        &lt;!-- boolean --&gt;
     *   &lt;address&gt;<em>...</em>&lt;/address&gt;                &lt;!-- string --&gt;
     *   &lt;AddressDetails xmlns="urn:oasis:names:tc:ciq:xsdschema:xAL:2.0"&gt;...<br>      &lt;/AddressDetails&gt;                 &lt;!-- string --&gt;
     *   &lt;phoneNumber&gt;...&lt;/phoneNumber&gt;        &lt;!-- string --&gt;<br>  &lt;Snippet maxLines="2"&gt;<em>...</em>&lt;/Snippet&gt;   &lt;!-- string --&gt;
     *   &lt;description&gt;<em>...</em>&lt;/description&gt;        &lt;!-- string --&gt;
     *   <span><em>&lt;AbstractView&gt;...&lt;/AbstractView&gt;</em>      &lt;!-- LookAt <em>or</em> Camera --&gt;</span>
     *   &lt;<em>TimePrimitive</em>&gt;...&lt;/<em>TimePrimitive</em>&gt;
     *   &lt;styleUrl&gt;<em>...</em>&lt;/styleUrl&gt;              &lt;!-- anyURI --&gt;
     *   &lt;<em>StyleSelector&gt;...&lt;/StyleSelector&gt;</em>
     *   &lt;Region&gt;...&lt;/Region&gt;
     *   &lt;Metadata&gt;...&lt;/Metadata&gt;
     *   <span>&lt;atom:author&gt;...&lt;atom:author&gt;   &lt;!-- xmlns:atom="http://www.w3.org/2005/Atom" --&gt;
     *   &lt;atom:link&gt;...&lt;/atom:link&gt;</span>
     * 
     *   &lt;!-- specific to <em>Container</em> --&gt; <br>  &lt;!-- 0 or more Features --&gt; 
     * <strong>&lt;!-- /<em>Container --</em>&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <Feature>
     * 
     * Extended By: 
     * @see: <Document>
     * @see: <Folder>
     * 
     * 
     * 
     */
    @XmlElementRef(name = "AbstractContainerGroup", namespace = "http://www.opengis.net/kml/2.2", required = false)
    protected List<Container> container;

    public Create() {
        super();
    }

    /**
     * @see container
     * 
     */
    public List<Container> getContainer() {
        if (container == null) {
            container = new ArrayList<Container>();
        }
        return this.container;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((container == null)? 0 :container.hashCode()));
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
        if ((obj instanceof Create) == false) {
            return false;
        }
        Create other = ((Create) obj);
        if (container == null) {
            if (other.container!= null) {
                return false;
            }
        } else {
            if (container.equals(other.container) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Folder} and adds it to container.
     * This method is a short version for:
     * <code>
     * Folder folder = new Folder();
     * this.getContainer().add(folder); </code>
     * 
     * 
     */
    public Folder createAndAddFolder() {
        Folder newValue = new Folder();
        this.getContainer().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Document} and adds it to container.
     * This method is a short version for:
     * <code>
     * Document document = new Document();
     * this.getContainer().add(document); </code>
     * 
     * 
     */
    public Document createAndAddDocument() {
        Document newValue = new Document();
        this.getContainer().add(newValue);
        return newValue;
    }

    /**
     * @see container
     * 
     * @param container
     */
    public void setContainer(final List<Container> container) {
        this.container = container;
    }

    /**
     * add a value to the container property collection
     * 
     * @param container
     *     Objects of the following type are allowed in the list: {@code <}{@link Container}{@code>}{@link JAXBElement}{@code <}{@link Folder}{@code>}{@link JAXBElement}{@code <}{@link Document}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Create addToContainer(final Container container) {
        this.getContainer().add(container);
        return this;
    }

    /**
     * fluent setter
     * @see #setContainer(List<Container>)
     * 
     * @param container
     *     required parameter
     */
    public Create withContainer(final List<Container> container) {
        this.setContainer(container);
        return this;
    }

    @Override
    public Create clone() {
        Create copy;
        try {
            copy = ((Create) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.container = new ArrayList<Container>((getContainer().size()));
        for (Container iter: container) {
            copy.container.add(iter.clone());
        }
        return copy;
    }

}
