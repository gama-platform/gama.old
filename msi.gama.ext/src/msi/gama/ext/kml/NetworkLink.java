
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.atom.Author;
import msi.gama.ext.kml.xal.AddressDetails;


/**
 * <NetworkLink>
 * <p>
 * References a KML file or KMZ archive on a local or remote network. Use the <Link> 
 * element to specify the location of the KML file. Within that element, you can define 
 * the refresh options for updating the file, based on time and camera change. NetworkLinks 
 * can be used in combination with Regions to handle very large datasets efficiently. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;NetworkLink id="ID"&gt;</strong>
 *   &lt;!-- inherited from <em>Feature</em> element --&gt;&lt;name&gt;<em>...</em>&lt;/name&gt;                      &lt;!-- string --&gt;
 *   &lt;visibility&gt;1&lt;/visibility&gt;            &lt;!-- boolean --&gt;
 *   &lt;open&gt;0&lt;/open&gt;                        &lt;!-- boolean --&gt;
 *   <span>&lt;atom:author&gt;...&lt;atom:author&gt;         &lt;!-- xmlns:atom --&gt;
 *   &lt;atom:link&gt;...&lt;/atom:link&gt;</span><span>            &lt;!-- xmlns:atom --&gt;</span>
 *   &lt;address&gt;<em>...</em>&lt;/address&gt;                &lt;!-- string --&gt;
 *   &lt;xal:AddressDetails&gt;...&lt;/xal:AddressDetails&gt;  &lt;!-- xmlns:xal --&gt;<br>  &lt;phoneNumber&gt;...&lt;/phoneNumber&gt;        &lt;!-- string --&gt;<br>  &lt;Snippet maxLines="2"&gt;<em>...</em>&lt;/Snippet&gt;   &lt;!-- string --&gt;
 *   &lt;description&gt;<em>...</em>&lt;/description&gt;        &lt;!-- string --&gt;
 *   <span><em>&lt;AbstractView&gt;...&lt;/AbstractView&gt;</em>      &lt;!-- Camera <em>or</em> LookAt --&gt;</span>
 *   &lt;<em>TimePrimitive</em>&gt;...&lt;/<em>TimePrimitive</em>&gt;
 *   &lt;styleUrl&gt;<em>...</em>&lt;/styleUrl&gt;              &lt;!-- anyURI --&gt;
 *   &lt;<em>StyleSelector&gt;...&lt;/StyleSelector&gt;</em>
 *   &lt;Region&gt;...&lt;/Region&gt;
 *   <span>&lt;Metadata&gt;...&lt;/Metadata&gt;              &lt;!-- deprecated in KML 2.2 --&gt;
 *   &lt;ExtendedData&gt;...&lt;/ExtendedData&gt;      &lt;!-- new in KML 2.2 --&gt;</span>
 * 
 *   &lt;!-- specific to NetworkLink --&gt;
 *   &lt;refreshVisibility&gt;0&lt;/refreshVisibility&gt; &lt;!-- boolean --&gt;
 *   &lt;flyToView&gt;0&lt;/flyToView&gt;                 &lt;!-- boolean --&gt;
 *   &lt;Link&gt;...&lt;/Link&gt;
 * <strong>&lt;/NetworkLink&gt;</strong></pre>
 * 
 * Extends: 
 * @see: <Feature>
 * 
 * Contained By: 
 * @see: <Container>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NetworkLinkType", propOrder = {
    "refreshVisibility",
    "flyToView",
    "url",
    "link",
    "networkLinkSimpleExtension",
    "networkLinkObjectExtension"
})
@XmlRootElement(name = "NetworkLink", namespace = "http://www.opengis.net/kml/2.2")
public class NetworkLink
    extends Feature
    implements Cloneable
{

    /**
     * <refreshvisibility>
     * <p>
     * Boolean value. A value of 0 leaves the visibility of features within the control 
     * of the Google Earth user. Set the value to 1 to reset the visibility of features 
     * each time the NetworkLink is refreshed. For example, suppose a Placemark within 
     * the linked KML file has <visibility> set to 1 and the NetworkLink has <refreshVisibility> 
     * set to 1. When the file is first loaded into Google Earth, the user can clear the 
     * check box next to the item to turn off display in the 3D viewer. However, when the 
     * NetworkLink is refreshed, the Placemark will be made visible again, since its original 
     * visibility state was TRUE. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    @XmlJavaTypeAdapter(BooleanConverter.class)
    protected Boolean refreshVisibility;
    /**
     * <flytoview>
     * <p>
     * Boolean value. A value of 1 causes Google Earth to fly to the view of the LookAt 
     * or Camera in the NetworkLinkControl (if it exists). If the NetworkLinkControl does 
     * not contain an AbstractView element, Google Earth flies to the LookAt or Camera 
     * element in the Feature child within the <kml> element in the refreshed file. If 
     * the <kml> element does not have a LookAt or Camera specified, the view is unchanged. 
     * For example, Google Earth would fly to the <LookAt> view of the parent Document, 
     * not the <LookAt> of the Placemarks contained within the Document. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    @XmlJavaTypeAdapter(BooleanConverter.class)
    protected Boolean flyToView;
    /**
     * <Url>
     * 
     * 
     */
    @XmlElement(name = "Url")
    @Deprecated
    protected msi.gama.ext.kml.Link url;
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
    @XmlElement(name = "Link")
    protected msi.gama.ext.kml.Link link;
    @XmlElement(name = "NetworkLinkSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> networkLinkSimpleExtension;
    /**
     * <Object>
     * <p>
     * This is an abstract base class and cannot be used directly in a KML file. It provides 
     * the id attribute, which allows unique identification of a KML element, and the targetId 
     * attribute, which is used to reference objects that have already been loaded into 
     * Google Earth. The id attribute must be assigned if the <Update> mechanism is to 
     * be used. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;<strong>
     * &lt;!-- <em>Object</em> id="ID" targetId="NCName" --&gt;
     * &lt;!-- /<em>Object</em>&gt; --&gt;</strong></pre>
     * 
     * 
     * 
     */
    @XmlElement(name = "NetworkLinkObjectExtensionGroup")
    protected List<AbstractObject> networkLinkObjectExtension;

    public NetworkLink() {
        super();
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean}
     *     
     */
    public Boolean isRefreshVisibility() {
        return refreshVisibility;
    }

    /**
     * @see refreshVisibility
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean}
     *     
     */
    public void setRefreshVisibility(Boolean value) {
        this.refreshVisibility = value;
    }

    /**
     * 
     * @return
     *     possible object is
     *     {@link Boolean}
     *     
     */
    public Boolean isFlyToView() {
        return flyToView;
    }

    /**
     * @see flyToView
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean}
     *     
     */
    public void setFlyToView(Boolean value) {
        this.flyToView = value;
    }

    /**
     * @see url
     * 
     * @return
     *     possible object is
     *     {@link msi.gama.ext.kml.Link}
     *     
     */
    public msi.gama.ext.kml.Link getUrl() {
        return url;
    }

    /**
     * @see url
     * 
     * @param value
     *     allowed object is
     *     {@link msi.gama.ext.kml.Link}
     *     
     */
    public void setUrl(msi.gama.ext.kml.Link value) {
        this.url = value;
    }

    /**
     * @see link
     * 
     * @return
     *     possible object is
     *     {@link msi.gama.ext.kml.Link}
     *     
     */
    public msi.gama.ext.kml.Link getLink() {
        return link;
    }

    /**
     * @see link
     * 
     * @param value
     *     allowed object is
     *     {@link msi.gama.ext.kml.Link}
     *     
     */
    public void setLink(msi.gama.ext.kml.Link value) {
        this.link = value;
    }

    /**
     * @see networkLinkSimpleExtension
     * 
     */
    public List<Object> getNetworkLinkSimpleExtension() {
        if (networkLinkSimpleExtension == null) {
            networkLinkSimpleExtension = new ArrayList<Object>();
        }
        return this.networkLinkSimpleExtension;
    }

    /**
     * @see networkLinkObjectExtension
     * 
     */
    public List<AbstractObject> getNetworkLinkObjectExtension() {
        if (networkLinkObjectExtension == null) {
            networkLinkObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.networkLinkObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((refreshVisibility == null)? 0 :refreshVisibility.hashCode()));
        result = ((prime*result)+((flyToView == null)? 0 :flyToView.hashCode()));
        result = ((prime*result)+((url == null)? 0 :url.hashCode()));
        result = ((prime*result)+((link == null)? 0 :link.hashCode()));
        result = ((prime*result)+((networkLinkSimpleExtension == null)? 0 :networkLinkSimpleExtension.hashCode()));
        result = ((prime*result)+((networkLinkObjectExtension == null)? 0 :networkLinkObjectExtension.hashCode()));
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
        if ((obj instanceof NetworkLink) == false) {
            return false;
        }
        NetworkLink other = ((NetworkLink) obj);
        if (refreshVisibility == null) {
            if (other.refreshVisibility!= null) {
                return false;
            }
        } else {
            if (refreshVisibility.equals(other.refreshVisibility) == false) {
                return false;
            }
        }
        if (flyToView == null) {
            if (other.flyToView!= null) {
                return false;
            }
        } else {
            if (flyToView.equals(other.flyToView) == false) {
                return false;
            }
        }
        if (url == null) {
            if (other.url!= null) {
                return false;
            }
        } else {
            if (url.equals(other.url) == false) {
                return false;
            }
        }
        if (link == null) {
            if (other.link!= null) {
                return false;
            }
        } else {
            if (link.equals(other.link) == false) {
                return false;
            }
        }
        if (networkLinkSimpleExtension == null) {
            if (other.networkLinkSimpleExtension!= null) {
                return false;
            }
        } else {
            if (networkLinkSimpleExtension.equals(other.networkLinkSimpleExtension) == false) {
                return false;
            }
        }
        if (networkLinkObjectExtension == null) {
            if (other.networkLinkObjectExtension!= null) {
                return false;
            }
        } else {
            if (networkLinkObjectExtension.equals(other.networkLinkObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link msi.gama.ext.kml.Link} and set it to url.
     * 
     * This method is a short version for:
     * <code>
     * Link link = new Link();
     * this.setUrl(link); </code>
     * 
     * 
     */
    public msi.gama.ext.kml.Link createAndSetUrl() {
        msi.gama.ext.kml.Link newValue = new msi.gama.ext.kml.Link();
        this.setUrl(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link msi.gama.ext.kml.Link} and set it to link.
     * 
     * This method is a short version for:
     * <code>
     * Link link = new Link();
     * this.setLink(link); </code>
     * 
     * 
     */
    public msi.gama.ext.kml.Link createAndSetLink() {
        msi.gama.ext.kml.Link newValue = new msi.gama.ext.kml.Link();
        this.setLink(newValue);
        return newValue;
    }

    /**
     * @see networkLinkSimpleExtension
     * 
     * @param networkLinkSimpleExtension
     */
    public void setNetworkLinkSimpleExtension(final List<Object> networkLinkSimpleExtension) {
        this.networkLinkSimpleExtension = networkLinkSimpleExtension;
    }

    /**
     * add a value to the networkLinkSimpleExtension property collection
     * 
     * @param networkLinkSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public NetworkLink addToNetworkLinkSimpleExtension(final Object networkLinkSimpleExtension) {
        this.getNetworkLinkSimpleExtension().add(networkLinkSimpleExtension);
        return this;
    }

    /**
     * @see networkLinkObjectExtension
     * 
     * @param networkLinkObjectExtension
     */
    public void setNetworkLinkObjectExtension(final List<AbstractObject> networkLinkObjectExtension) {
        this.networkLinkObjectExtension = networkLinkObjectExtension;
    }

    /**
     * add a value to the networkLinkObjectExtension property collection
     * 
     * @param networkLinkObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public NetworkLink addToNetworkLinkObjectExtension(final AbstractObject networkLinkObjectExtension) {
        this.getNetworkLinkObjectExtension().add(networkLinkObjectExtension);
        return this;
    }

    /**
     * @see objectSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.setObjectSimpleExtension(objectSimpleExtension);
    }

    @Obvious
    @Override
    public NetworkLink addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * @see styleSelector
     * 
     */
    @Obvious
    @Override
    public void setStyleSelector(final List<StyleSelector> styleSelector) {
        super.setStyleSelector(styleSelector);
    }

    @Obvious
    @Override
    public NetworkLink addToStyleSelector(final StyleSelector styleSelector) {
        super.getStyleSelector().add(styleSelector);
        return this;
    }

    /**
     * @see featureSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setFeatureSimpleExtension(final List<Object> featureSimpleExtension) {
        super.setFeatureSimpleExtension(featureSimpleExtension);
    }

    @Obvious
    @Override
    public NetworkLink addToFeatureSimpleExtension(final Object featureSimpleExtension) {
        super.getFeatureSimpleExtension().add(featureSimpleExtension);
        return this;
    }

    /**
     * @see featureObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setFeatureObjectExtension(final List<AbstractObject> featureObjectExtension) {
        super.setFeatureObjectExtension(featureObjectExtension);
    }

    @Obvious
    @Override
    public NetworkLink addToFeatureObjectExtension(final AbstractObject featureObjectExtension) {
        super.getFeatureObjectExtension().add(featureObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setRefreshVisibility(Boolean)
     * 
     * @param refreshVisibility
     *     required parameter
     */
    public NetworkLink withRefreshVisibility(final Boolean refreshVisibility) {
        this.setRefreshVisibility(refreshVisibility);
        return this;
    }

    /**
     * fluent setter
     * @see #setFlyToView(Boolean)
     * 
     * @param flyToView
     *     required parameter
     */
    public NetworkLink withFlyToView(final Boolean flyToView) {
        this.setFlyToView(flyToView);
        return this;
    }

    /**
     * fluent setter
     * @see #setUrl(Link)
     * 
     * @param url
     *     required parameter
     */
    public NetworkLink withUrl(final msi.gama.ext.kml.Link url) {
        this.setUrl(url);
        return this;
    }

    /**
     * fluent setter
     * @see #setLink(Link)
     * 
     * @param link
     *     required parameter
     */
    public NetworkLink withLink(final msi.gama.ext.kml.Link link) {
        this.setLink(link);
        return this;
    }

    /**
     * fluent setter
     * @see #setNetworkLinkSimpleExtension(List<Object>)
     * 
     * @param networkLinkSimpleExtension
     *     required parameter
     */
    public NetworkLink withNetworkLinkSimpleExtension(final List<Object> networkLinkSimpleExtension) {
        this.setNetworkLinkSimpleExtension(networkLinkSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setNetworkLinkObjectExtension(List<AbstractObject>)
     * 
     * @param networkLinkObjectExtension
     *     required parameter
     */
    public NetworkLink withNetworkLinkObjectExtension(final List<AbstractObject> networkLinkObjectExtension) {
        this.setNetworkLinkObjectExtension(networkLinkObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withName(final String name) {
        super.withName(name);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withVisibility(final Boolean visibility) {
        super.withVisibility(visibility);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withOpen(final Boolean open) {
        super.withOpen(open);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withAtomAuthor(final Author atomAuthor) {
        super.withAtomAuthor(atomAuthor);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withAtomLink(final msi.gama.ext.kml.atom.Link atomLink) {
        super.withAtomLink(atomLink);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withAddress(final String address) {
        super.withAddress(address);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withXalAddressDetails(final AddressDetails xalAddressDetails) {
        super.withXalAddressDetails(xalAddressDetails);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withPhoneNumber(final String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withSnippet(final Snippet snippet) {
        super.withSnippet(snippet);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withSnippetd(final String snippetd) {
        super.withSnippetd(snippetd);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withDescription(final String description) {
        super.withDescription(description);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withAbstractView(final AbstractView abstractView) {
        super.withAbstractView(abstractView);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withTimePrimitive(final TimePrimitive timePrimitive) {
        super.withTimePrimitive(timePrimitive);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withStyleUrl(final String styleUrl) {
        super.withStyleUrl(styleUrl);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withStyleSelector(final List<StyleSelector> styleSelector) {
        super.withStyleSelector(styleSelector);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withRegion(final Region region) {
        super.withRegion(region);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withMetadata(final Metadata metadata) {
        super.withMetadata(metadata);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withExtendedData(final ExtendedData extendedData) {
        super.withExtendedData(extendedData);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withFeatureSimpleExtension(final List<Object> featureSimpleExtension) {
        super.withFeatureSimpleExtension(featureSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public NetworkLink withFeatureObjectExtension(final List<AbstractObject> featureObjectExtension) {
        super.withFeatureObjectExtension(featureObjectExtension);
        return this;
    }

    @Override
    public NetworkLink clone() {
        NetworkLink copy;
        copy = ((NetworkLink) super.clone());
        copy.url = ((url == null)?null:((msi.gama.ext.kml.Link) url.clone()));
        copy.link = ((link == null)?null:((msi.gama.ext.kml.Link) link.clone()));
        copy.networkLinkSimpleExtension = new ArrayList<Object>((getNetworkLinkSimpleExtension().size()));
        for (Object iter: networkLinkSimpleExtension) {
            copy.networkLinkSimpleExtension.add(iter);
        }
        copy.networkLinkObjectExtension = new ArrayList<AbstractObject>((getNetworkLinkObjectExtension().size()));
        for (AbstractObject iter: networkLinkObjectExtension) {
            copy.networkLinkObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
