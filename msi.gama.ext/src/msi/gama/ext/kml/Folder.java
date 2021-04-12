
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.atom.Author;
import msi.gama.ext.kml.atom.Link;
import msi.gama.ext.kml.gx.Tour;
import msi.gama.ext.kml.xal.AddressDetails;


/**
 * <Folder>
 * <p>
 * A Folder is used to arrange other Features hierarchically (Folders, Placemarks, 
 * NetworkLinks, or Overlays). A Feature is visible only if it and all its ancestors 
 * are visible. 
 * </p>
 * <p>
 * A Folder is used to arrange other Features hierarchically (Folders, Placemarks, 
 * NetworkLinks, or Overlays). A Feature is visible only if it and all its ancestors 
 * are visible. 
 * </p>
 * 
 * Syntax: 
 * <pre><strong>&lt;Folder id="ID"&gt;</strong>
 *   &lt;!-- inherited from <em>Feature</em> element --&gt;
 *   &lt;name&gt;<em>...</em>&lt;/name&gt;                      &lt;!-- string --&gt;
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
 *   &lt;ExtendedData&gt;...&lt;/ExtendedData&gt;      &lt;!-- new in KML 2.2 --&gt;
 * 
 *   </span>&lt;!-- specific to Folder --&gt;
 *   &lt;!-- 0 or more <em>Feature</em> elements --&gt;
 * <strong>&lt;/Folder</strong>&gt;
 * </pre>
 * 
 * Extends: 
 * @see: <Container>
 * 
 * Contains: 
 * @see: <Feature>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FolderType", propOrder = {
    "feature",
    "folderSimpleExtension",
    "folderObjectExtension"
})
@XmlRootElement(name = "Folder", namespace = "http://www.opengis.net/kml/2.2")
public class Folder
    extends Container
    implements Cloneable
{

    /**
     * <Feature>
     * <p>
     * This is an abstract element and cannot be used directly in a KML file. The following 
     * diagram shows how some of a Feature's elements appear in Google Earth. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;!-- abstract element; do not create --&gt;
     * <strong>&lt;!--<em> Feature</em> id="ID" --&gt;</strong>                &lt;!-- Document,Folder,
     *                                              NetworkLink,Placemark,
     *                                              GroundOverlay,PhotoOverlay,ScreenOverlay --&gt;
     *   &lt;name&gt;<em>...</em>&lt;/name&gt;                      &lt;!-- string --&gt;
     *   &lt;visibility&gt;1&lt;/visibility&gt;            &lt;!-- boolean --&gt;
     *   &lt;open&gt;0&lt;/open&gt;                        &lt;!-- boolean --&gt;
     *   <span>&lt;atom:author&gt;...&lt;atom:author&gt;         &lt;!-- xmlns:atom --&gt;
     *   &lt;atom:link&gt;...&lt;/atom:link&gt;</span><span>            &lt;!-- xmlns:atom --&gt;</span>
     *   &lt;address&gt;<em>...</em>&lt;/address&gt;                &lt;!-- string --&gt;
     *   &lt;xal:AddressDetails&gt;...&lt;/xal:AddressDetails&gt;  &lt;!-- xmlns:xal --&gt;<br>  &lt;phoneNumber&gt;...&lt;/phoneNumber&gt;        &lt;!-- string --&gt;<br>  &lt;Snippet maxLines="2"&gt;<em>...</em>&lt;/Snippet&gt;   &lt;!-- string --&gt;
     *   &lt;description&gt;<em>...</em>&lt;/description&gt;        &lt;!-- string --&gt;
     *   <span><em>&lt;AbstractView&gt;...&lt;/AbstractView&gt;</em>      &lt;!-- Camera <em>or</em> LookAt --&gt;</span>
     *   &lt;<em>TimePrimitive</em>&gt;...&lt;/<em>TimePrimitive</em>&gt;    &lt;!-- TimeStamp or TimeSpan --&gt;
     *   &lt;styleUrl&gt;<em>...</em>&lt;/styleUrl&gt;              &lt;!-- anyURI --&gt;
     *   &lt;<em>StyleSelector&gt;...&lt;/StyleSelector&gt;</em>
     *   &lt;Region&gt;...&lt;/Region&gt;
     *   <span>&lt;Metadata&gt;...&lt;/Metadata&gt;              &lt;!-- deprecated in KML 2.2 --&gt;
     *   &lt;ExtendedData&gt;...&lt;/ExtendedData&gt;      &lt;!-- new in KML 2.2 --&gt;<br></span>&lt;-- /<em>Feature</em> --&gt;</pre>
     * 
     * Extends: 
     * @see: <Object>
     * 
     * Extended By: 
     * @see: <Container>
     * @see: <NetworkLink>
     * @see: <Overlay>
     * @see: <Placemark>
     * @see: <gx:Tour>
     * 
     * 
     * 
     */
    @XmlElementRef(name = "AbstractFeatureGroup", namespace = "http://www.opengis.net/kml/2.2", required = false)
    protected List<Feature> feature;
    @XmlElement(name = "FolderSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> folderSimpleExtension;
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
    @XmlElement(name = "FolderObjectExtensionGroup")
    protected List<AbstractObject> folderObjectExtension;

    public Folder() {
        super();
    }

    /**
     * @see feature
     * 
     */
    public List<Feature> getFeature() {
        if (feature == null) {
            feature = new ArrayList<Feature>();
        }
        return this.feature;
    }

    /**
     * @see folderSimpleExtension
     * 
     */
    public List<Object> getFolderSimpleExtension() {
        if (folderSimpleExtension == null) {
            folderSimpleExtension = new ArrayList<Object>();
        }
        return this.folderSimpleExtension;
    }

    /**
     * @see folderObjectExtension
     * 
     */
    public List<AbstractObject> getFolderObjectExtension() {
        if (folderObjectExtension == null) {
            folderObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.folderObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((feature == null)? 0 :feature.hashCode()));
        result = ((prime*result)+((folderSimpleExtension == null)? 0 :folderSimpleExtension.hashCode()));
        result = ((prime*result)+((folderObjectExtension == null)? 0 :folderObjectExtension.hashCode()));
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
        if ((obj instanceof Folder) == false) {
            return false;
        }
        Folder other = ((Folder) obj);
        if (feature == null) {
            if (other.feature!= null) {
                return false;
            }
        } else {
            if (feature.equals(other.feature) == false) {
                return false;
            }
        }
        if (folderSimpleExtension == null) {
            if (other.folderSimpleExtension!= null) {
                return false;
            }
        } else {
            if (folderSimpleExtension.equals(other.folderSimpleExtension) == false) {
                return false;
            }
        }
        if (folderObjectExtension == null) {
            if (other.folderObjectExtension!= null) {
                return false;
            }
        } else {
            if (folderObjectExtension.equals(other.folderObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Tour} and adds it to feature.
     * This method is a short version for:
     * <code>
     * Tour tour = new Tour();
     * this.getFeature().add(tour); </code>
     * 
     * 
     */
    public Tour createAndAddTour() {
        Tour newValue = new Tour();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ScreenOverlay} and adds it to feature.
     * This method is a short version for:
     * <code>
     * ScreenOverlay screenOverlay = new ScreenOverlay();
     * this.getFeature().add(screenOverlay); </code>
     * 
     * 
     */
    public ScreenOverlay createAndAddScreenOverlay() {
        ScreenOverlay newValue = new ScreenOverlay();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link PhotoOverlay} and adds it to feature.
     * This method is a short version for:
     * <code>
     * PhotoOverlay photoOverlay = new PhotoOverlay();
     * this.getFeature().add(photoOverlay); </code>
     * 
     * 
     */
    public PhotoOverlay createAndAddPhotoOverlay() {
        PhotoOverlay newValue = new PhotoOverlay();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link GroundOverlay} and adds it to feature.
     * This method is a short version for:
     * <code>
     * GroundOverlay groundOverlay = new GroundOverlay();
     * this.getFeature().add(groundOverlay); </code>
     * 
     * 
     */
    public GroundOverlay createAndAddGroundOverlay() {
        GroundOverlay newValue = new GroundOverlay();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link NetworkLink} and adds it to feature.
     * This method is a short version for:
     * <code>
     * NetworkLink networkLink = new NetworkLink();
     * this.getFeature().add(networkLink); </code>
     * 
     * 
     */
    public NetworkLink createAndAddNetworkLink() {
        NetworkLink newValue = new NetworkLink();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Folder} and adds it to feature.
     * This method is a short version for:
     * <code>
     * Folder folder = new Folder();
     * this.getFeature().add(folder); </code>
     * 
     * 
     */
    public Folder createAndAddFolder() {
        Folder newValue = new Folder();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Document} and adds it to feature.
     * This method is a short version for:
     * <code>
     * Document document = new Document();
     * this.getFeature().add(document); </code>
     * 
     * 
     */
    public Document createAndAddDocument() {
        Document newValue = new Document();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Placemark} and adds it to feature.
     * This method is a short version for:
     * <code>
     * Placemark placemark = new Placemark();
     * this.getFeature().add(placemark); </code>
     * 
     * 
     */
    public Placemark createAndAddPlacemark() {
        Placemark newValue = new Placemark();
        this.getFeature().add(newValue);
        return newValue;
    }

    /**
     * @see feature
     * 
     * @param feature
     */
    public void setFeature(final List<Feature> feature) {
        this.feature = feature;
    }

    /**
     * add a value to the feature property collection
     * 
     * @param feature
     *     Objects of the following type are allowed in the list: {@code <}{@link Container}{@code>}{@link JAXBElement}{@code <}{@link GroundOverlay}{@code>}{@link JAXBElement}{@code <}{@link NetworkLink}{@code>}{@link JAXBElement}{@code <}{@link Folder}{@code>}{@link JAXBElement}{@code <}{@link PhotoOverlay}{@code>}{@link JAXBElement}{@code <}{@link Document}{@code>}{@link JAXBElement}{@code <}{@link Tour}{@code>}{@link JAXBElement}{@code <}{@link ScreenOverlay}{@code>}{@link JAXBElement}{@code <}{@link Feature}{@code>}{@link JAXBElement}{@code <}{@link Placemark}{@code>}{@link JAXBElement}{@code <}{@link Overlay}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Folder addToFeature(final Feature feature) {
        this.getFeature().add(feature);
        return this;
    }

    /**
     * @see folderSimpleExtension
     * 
     * @param folderSimpleExtension
     */
    public void setFolderSimpleExtension(final List<Object> folderSimpleExtension) {
        this.folderSimpleExtension = folderSimpleExtension;
    }

    /**
     * add a value to the folderSimpleExtension property collection
     * 
     * @param folderSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Folder addToFolderSimpleExtension(final Object folderSimpleExtension) {
        this.getFolderSimpleExtension().add(folderSimpleExtension);
        return this;
    }

    /**
     * @see folderObjectExtension
     * 
     * @param folderObjectExtension
     */
    public void setFolderObjectExtension(final List<AbstractObject> folderObjectExtension) {
        this.folderObjectExtension = folderObjectExtension;
    }

    /**
     * add a value to the folderObjectExtension property collection
     * 
     * @param folderObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Folder addToFolderObjectExtension(final AbstractObject folderObjectExtension) {
        this.getFolderObjectExtension().add(folderObjectExtension);
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
    public Folder addToObjectSimpleExtension(final Object objectSimpleExtension) {
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
    public Folder addToStyleSelector(final StyleSelector styleSelector) {
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
    public Folder addToFeatureSimpleExtension(final Object featureSimpleExtension) {
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
    public Folder addToFeatureObjectExtension(final AbstractObject featureObjectExtension) {
        super.getFeatureObjectExtension().add(featureObjectExtension);
        return this;
    }

    /**
     * @see containerSimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setContainerSimpleExtension(final List<Object> containerSimpleExtension) {
        super.setContainerSimpleExtension(containerSimpleExtension);
    }

    @Obvious
    @Override
    public Folder addToContainerSimpleExtension(final Object containerSimpleExtension) {
        super.getContainerSimpleExtension().add(containerSimpleExtension);
        return this;
    }

    /**
     * @see containerObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setContainerObjectExtension(final List<AbstractObject> containerObjectExtension) {
        super.setContainerObjectExtension(containerObjectExtension);
    }

    @Obvious
    @Override
    public Folder addToContainerObjectExtension(final AbstractObject containerObjectExtension) {
        super.getContainerObjectExtension().add(containerObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setFeature(List<Feature>)
     * 
     * @param feature
     *     required parameter
     */
    public Folder withFeature(final List<Feature> feature) {
        this.setFeature(feature);
        return this;
    }

    /**
     * fluent setter
     * @see #setFolderSimpleExtension(List<Object>)
     * 
     * @param folderSimpleExtension
     *     required parameter
     */
    public Folder withFolderSimpleExtension(final List<Object> folderSimpleExtension) {
        this.setFolderSimpleExtension(folderSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setFolderObjectExtension(List<AbstractObject>)
     * 
     * @param folderObjectExtension
     *     required parameter
     */
    public Folder withFolderObjectExtension(final List<AbstractObject> folderObjectExtension) {
        this.setFolderObjectExtension(folderObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Folder withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Folder withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Folder withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public Folder withName(final String name) {
        super.withName(name);
        return this;
    }

    @Obvious
    @Override
    public Folder withVisibility(final Boolean visibility) {
        super.withVisibility(visibility);
        return this;
    }

    @Obvious
    @Override
    public Folder withOpen(final Boolean open) {
        super.withOpen(open);
        return this;
    }

    @Obvious
    @Override
    public Folder withAtomAuthor(final Author atomAuthor) {
        super.withAtomAuthor(atomAuthor);
        return this;
    }

    @Obvious
    @Override
    public Folder withAtomLink(final Link atomLink) {
        super.withAtomLink(atomLink);
        return this;
    }

    @Obvious
    @Override
    public Folder withAddress(final String address) {
        super.withAddress(address);
        return this;
    }

    @Obvious
    @Override
    public Folder withXalAddressDetails(final AddressDetails xalAddressDetails) {
        super.withXalAddressDetails(xalAddressDetails);
        return this;
    }

    @Obvious
    @Override
    public Folder withPhoneNumber(final String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    @Obvious
    @Override
    public Folder withSnippet(final Snippet snippet) {
        super.withSnippet(snippet);
        return this;
    }

    @Obvious
    @Override
    public Folder withSnippetd(final String snippetd) {
        super.withSnippetd(snippetd);
        return this;
    }

    @Obvious
    @Override
    public Folder withDescription(final String description) {
        super.withDescription(description);
        return this;
    }

    @Obvious
    @Override
    public Folder withAbstractView(final AbstractView abstractView) {
        super.withAbstractView(abstractView);
        return this;
    }

    @Obvious
    @Override
    public Folder withTimePrimitive(final TimePrimitive timePrimitive) {
        super.withTimePrimitive(timePrimitive);
        return this;
    }

    @Obvious
    @Override
    public Folder withStyleUrl(final String styleUrl) {
        super.withStyleUrl(styleUrl);
        return this;
    }

    @Obvious
    @Override
    public Folder withStyleSelector(final List<StyleSelector> styleSelector) {
        super.withStyleSelector(styleSelector);
        return this;
    }

    @Obvious
    @Override
    public Folder withRegion(final Region region) {
        super.withRegion(region);
        return this;
    }

    @Obvious
    @Override
    public Folder withMetadata(final Metadata metadata) {
        super.withMetadata(metadata);
        return this;
    }

    @Obvious
    @Override
    public Folder withExtendedData(final ExtendedData extendedData) {
        super.withExtendedData(extendedData);
        return this;
    }

    @Obvious
    @Override
    public Folder withFeatureSimpleExtension(final List<Object> featureSimpleExtension) {
        super.withFeatureSimpleExtension(featureSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Folder withFeatureObjectExtension(final List<AbstractObject> featureObjectExtension) {
        super.withFeatureObjectExtension(featureObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public Folder withContainerSimpleExtension(final List<Object> containerSimpleExtension) {
        super.withContainerSimpleExtension(containerSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Folder withContainerObjectExtension(final List<AbstractObject> containerObjectExtension) {
        super.withContainerObjectExtension(containerObjectExtension);
        return this;
    }

    @Override
    public Folder clone() {
        Folder copy;
        copy = ((Folder) super.clone());
        copy.feature = new ArrayList<Feature>((getFeature().size()));
        for (Feature iter: feature) {
            copy.feature.add(iter.clone());
        }
        copy.folderSimpleExtension = new ArrayList<Object>((getFolderSimpleExtension().size()));
        for (Object iter: folderSimpleExtension) {
            copy.folderSimpleExtension.add(iter);
        }
        copy.folderObjectExtension = new ArrayList<AbstractObject>((getFolderObjectExtension().size()));
        for (AbstractObject iter: folderObjectExtension) {
            copy.folderObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
