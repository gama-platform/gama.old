
package msi.gama.ext.kml.atom;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <link> (required). see <link>.
 * <p>
 * <Link> specifies the location of any of the following: 
 * </p>
 * <p>
 * If the file specified in <href> is a local file, the <viewFormat> and <httpQuery> 
 * elements are not used. 
 * </p>
 * <p>
 * KML files fetched by network links Image files used in any Overlay (the <Icon> element 
 * specifies the image in an Overlay; <Icon> has the same fields as <Link>) Model files 
 * used in the <Model> element 
 * </p>
 * <p>
 * Specifies the URL of the website containing this KML or KMZ file. Be sure to include 
 * the namespace for this element in any KML file that uses it: xmlns:atom="http://www.w3.org/2005/Atom" 
 * (see the sample that follows). 
 * </p>
 * <p>
 * Specifies the file to load and optional refresh parameters. See <Link>. 
 * </p>
 * <p>
 * The <Link> element replaces the <Url> element of <NetworkLink> contained in earlier 
 * KML releases and adds functionality for the <Region> element (introduced in KML 
 *  2.1). In Google Earth releases 3.0 and earlier, the <Link> element is ignored. 
 * </p>
 * <p>
 * The file is conditionally loaded and refreshed, depending on the refresh parameters 
 * supplied here. Two different sets of refresh parameters can be specified: one set 
 * is based on time (<refreshMode> and <refreshInterval>) and one is based on the current 
 * "camera" view (<viewRefreshMode> and <viewRefreshTime>). In addition, Link specifies 
 * whether to scale the bounding box parameters that are sent to the server (<viewBoundScale> 
 * and provides a set of optional viewing parameters that can be sent to the server 
 * (<viewFormat>) as well as a set of optional parameters containing version and language 
 * information. 
 * </p>
 * <p>
 * Tip: To display the top-level Folder or Document within a Network Link in the List 
 * View, assign an ID to the Folder or Document. Without this ID, only the child object 
 * names are displayed in the List View. 
 * </p>
 * <p>
 * When a file is fetched, the URL that is sent to the server is composed of three 
 * pieces of information: 
 * </p>
 * <p>
 * the href (Hypertext Reference) that specifies the file to load. an arbitrary format 
 * string that is created from (a) parameters that you specify in the <viewFormat> 
 * element or (b) bounding box parameters (this is the default and is used if no <viewFormat> 
 * element is included in the file). a second format string that is specified in the 
 * <httpQuery> element. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;Link id="ID"&gt;</strong>
 *   &lt;!-- specific to Link --&gt;
 *   &lt;href&gt;<em>...</em>&lt;/href&gt;                      &lt;!-- <span>string</span> --&gt;
 *   &lt;refreshMode&gt;onChange&lt;/refreshMode&gt;   
 *     &lt;!-- refreshModeEnum: onChange, onInterval, <em>or</em> onExpire --&gt;   
 *   &lt;refreshInterval&gt;4&lt;/refreshInterval&gt;  &lt;!-- float --&gt;
 *   &lt;viewRefreshMode&gt;never&lt;/viewRefreshMode&gt; 
 *     &lt;!-- viewRefreshModeEnum: never, onStop, onRequest, onRegion --&gt;
 *   &lt;viewRefreshTime&gt;4&lt;/viewRefreshTime&gt;  &lt;!-- float --&gt;
 *   &lt;viewBoundScale&gt;1&lt;/viewBoundScale&gt;    &lt;!-- float --&gt;
 *   &lt;viewFormat&gt;BBOX=[bboxWest],[bboxSouth],[bboxEast],[bboxNorth]&lt;<strong>/</strong>viewFormat&gt;
 *                                         &lt;!-- string --&gt;
 *   &lt;httpQuery&gt;...&lt;/httpQuery&gt;            &lt;!-- string --&gt;
 * <strong>&lt;/Link&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <Object>
 * 
 * Contained By: 
 * @see: <Model>
 * @see: <NetworkLink>
 * 
 * See Also: 
 * <NetworkLinkControl>
 * <Region>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "link")
public class Link implements Cloneable
{

    /**
     * <href>
     * <p>
     * A URL (either an HTTP address or a local file specification). When the parent of 
     * <Link> is a NetworkLink, <href> is a KML file. When the parent of <Link> is a Model, 
     * <href> is a COLLADA file. When the parent of <Icon> (same fields as <Link>) is an 
     * Overlay, <href> is an image. Relative URLs can be used in this tag and are evaluated 
     * relative to the enclosing KML file. 
     * </p>
     * <p>
     * An HTTP address or a local file specification used to load an icon. 
     * </p>
     * <p>
     * Specifies the URI of the image used in the List View for the Feature. 
     * </p>
     * 
     * 
     * 
     */
    @XmlAttribute(name = "href", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String href;
    @XmlAttribute(name = "rel")
    @XmlSchemaType(name = "anySimpleType")
    protected String rel;
    @XmlAttribute(name = "type")
    protected String type;
    @XmlAttribute(name = "hreflang")
    protected String hreflang;
    @XmlAttribute(name = "title")
    @XmlSchemaType(name = "anySimpleType")
    protected String title;
    @XmlAttribute(name = "length")
    @XmlSchemaType(name = "anySimpleType")
    protected String length;

    /**
     * Value constructor with only mandatory fields
     * 
     * @param href
     *     required parameter
     */
    public Link(final String href) {
        super();
        this.href = href;
    }

    /**
     * Default no-arg constructor is private. Use overloaded constructor instead! (Temporary solution, till a better and more suitable ObjectFactory is created.) 
     * 
     */
    @Deprecated
    private Link() {
        super();
    }

    /**
     * @see href
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getHref() {
        return href;
    }

    /**
     * @see href
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setHref(String value) {
        this.href = value;
    }

    /**
     * @see rel
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getRel() {
        return rel;
    }

    /**
     * @see rel
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setRel(String value) {
        this.rel = value;
    }

    /**
     * @see type
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * @see type
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * @see hreflang
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getHreflang() {
        return hreflang;
    }

    /**
     * @see hreflang
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setHreflang(String value) {
        this.hreflang = value;
    }

    /**
     * @see title
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getTitle() {
        return title;
    }

    /**
     * @see title
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setTitle(String value) {
        this.title = value;
    }

    /**
     * @see length
     * 
     * @return
     *     possible object is
     *     {@link String}
     *     
     */
    public String getLength() {
        return length;
    }

    /**
     * @see length
     * 
     * @param value
     *     allowed object is
     *     {@link String}
     *     
     */
    public void setLength(String value) {
        this.length = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((href == null)? 0 :href.hashCode()));
        result = ((prime*result)+((rel == null)? 0 :rel.hashCode()));
        result = ((prime*result)+((type == null)? 0 :type.hashCode()));
        result = ((prime*result)+((hreflang == null)? 0 :hreflang.hashCode()));
        result = ((prime*result)+((title == null)? 0 :title.hashCode()));
        result = ((prime*result)+((length == null)? 0 :length.hashCode()));
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
        if ((obj instanceof Link) == false) {
            return false;
        }
        Link other = ((Link) obj);
        if (href == null) {
            if (other.href!= null) {
                return false;
            }
        } else {
            if (href.equals(other.href) == false) {
                return false;
            }
        }
        if (rel == null) {
            if (other.rel!= null) {
                return false;
            }
        } else {
            if (rel.equals(other.rel) == false) {
                return false;
            }
        }
        if (type == null) {
            if (other.type!= null) {
                return false;
            }
        } else {
            if (type.equals(other.type) == false) {
                return false;
            }
        }
        if (hreflang == null) {
            if (other.hreflang!= null) {
                return false;
            }
        } else {
            if (hreflang.equals(other.hreflang) == false) {
                return false;
            }
        }
        if (title == null) {
            if (other.title!= null) {
                return false;
            }
        } else {
            if (title.equals(other.title) == false) {
                return false;
            }
        }
        if (length == null) {
            if (other.length!= null) {
                return false;
            }
        } else {
            if (length.equals(other.length) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * fluent setter
     * @see #setRel(String)
     * 
     * @param rel
     *     required parameter
     */
    public Link withRel(final String rel) {
        this.setRel(rel);
        return this;
    }

    /**
     * fluent setter
     * @see #setType(String)
     * 
     * @param type
     *     required parameter
     */
    public Link withType(final String type) {
        this.setType(type);
        return this;
    }

    /**
     * fluent setter
     * @see #setHreflang(String)
     * 
     * @param hreflang
     *     required parameter
     */
    public Link withHreflang(final String hreflang) {
        this.setHreflang(hreflang);
        return this;
    }

    /**
     * fluent setter
     * @see #setTitle(String)
     * 
     * @param title
     *     required parameter
     */
    public Link withTitle(final String title) {
        this.setTitle(title);
        return this;
    }

    /**
     * fluent setter
     * @see #setLength(String)
     * 
     * @param length
     *     required parameter
     */
    public Link withLength(final String length) {
        this.setLength(length);
        return this;
    }

    @Override
    public Link clone() {
        Link copy;
        try {
            copy = ((Link) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        return copy;
    }

}
