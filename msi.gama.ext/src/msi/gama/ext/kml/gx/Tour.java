
package msi.gama.ext.kml.gx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.AbstractObject;
import msi.gama.ext.kml.AbstractView;
import msi.gama.ext.kml.ExtendedData;
import msi.gama.ext.kml.Feature;
import msi.gama.ext.kml.Metadata;
import msi.gama.ext.kml.Region;
import msi.gama.ext.kml.Snippet;
import msi.gama.ext.kml.StyleSelector;
import msi.gama.ext.kml.TimePrimitive;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.atom.Author;
import msi.gama.ext.kml.atom.Link;
import msi.gama.ext.kml.xal.AddressDetails;


/**
 * <gx:Tour>
 * <p>
 * <gx:Tour> can contain a single <gx:Playlist> element, which in turn contains an 
 * ordered list of gx:TourPrimitive elements that define a tour in any KML browser. 
 * Learn more about tours. 
 * </p>
 * 
 * Syntax: 
 * <pre>&lt;gx:Tour&gt;
 *   &lt;name&gt;...&lt;/name&gt;
 *   &lt;description&gt;...&lt;/description&gt;
 *   &lt;gx:Playlist&gt;
 * 
 *     &lt;!-- any number of <em>gx:TourPrimitive</em> elements --&gt;
 * 
 *   &lt;/gx:Playlist&gt;
 * &lt;/gx:Tour&gt;</pre>
 * 
 * Contains: 
 * @see: <gx:Playlist>
 * @see: <gx:Tour>
 * @see: <gx:Tour> <gx:Playlist> <!-- gx:TourPrimitive --> ... <!-- /gx:TourPrimitive --> <!--- Any number of gx:TourPrimitive elements can be included ---> </gx:Playlist> </gx:Tour>
 * @see: gx:Playlist
 * @see: gx:TourPrimitive
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TourType", propOrder = {
    "playlist"
})
@XmlRootElement(name = "Tour", namespace = "http://www.google.com/kml/ext/2.2")
public class Tour
    extends Feature
    implements Cloneable
{

    @XmlElement(name = "Playlist")
    protected Playlist playlist;

    public Tour() {
        super();
    }

    /**
     * @see playlist
     * 
     * @return
     *     possible object is
     *     {@link Playlist}
     *     
     */
    public Playlist getPlaylist() {
        return playlist;
    }

    /**
     * @see playlist
     * 
     * @param value
     *     allowed object is
     *     {@link Playlist}
     *     
     */
    public void setPlaylist(Playlist value) {
        this.playlist = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+((playlist == null)? 0 :playlist.hashCode()));
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
        if ((obj instanceof Tour) == false) {
            return false;
        }
        Tour other = ((Tour) obj);
        if (playlist == null) {
            if (other.playlist!= null) {
                return false;
            }
        } else {
            if (playlist.equals(other.playlist) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Playlist} and set it to playlist.
     * 
     * This method is a short version for:
     * <code>
     * Playlist playlist = new Playlist();
     * this.setPlaylist(playlist); </code>
     * 
     * 
     */
    public Playlist createAndSetPlaylist() {
        Playlist newValue = new Playlist();
        this.setPlaylist(newValue);
        return newValue;
    }

    /**
     * fluent setter
     * @see #setPlaylist(Playlist)
     * 
     * @param playlist
     *     required parameter
     */
    public Tour withPlaylist(final Playlist playlist) {
        this.setPlaylist(playlist);
        return this;
    }

    @Obvious
    @Override
    public Tour withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Tour withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public Tour withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public Tour withName(final String name) {
        super.withName(name);
        return this;
    }

    @Obvious
    @Override
    public Tour withVisibility(final Boolean visibility) {
        super.withVisibility(visibility);
        return this;
    }

    @Obvious
    @Override
    public Tour withOpen(final Boolean open) {
        super.withOpen(open);
        return this;
    }

    @Obvious
    @Override
    public Tour withAtomAuthor(final Author atomAuthor) {
        super.withAtomAuthor(atomAuthor);
        return this;
    }

    @Obvious
    @Override
    public Tour withAtomLink(final Link atomLink) {
        super.withAtomLink(atomLink);
        return this;
    }

    @Obvious
    @Override
    public Tour withAddress(final String address) {
        super.withAddress(address);
        return this;
    }

    @Obvious
    @Override
    public Tour withXalAddressDetails(final AddressDetails xalAddressDetails) {
        super.withXalAddressDetails(xalAddressDetails);
        return this;
    }

    @Obvious
    @Override
    public Tour withPhoneNumber(final String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    @Obvious
    @Override
    public Tour withSnippet(final Snippet snippet) {
        super.withSnippet(snippet);
        return this;
    }

    @Obvious
    @Override
    public Tour withSnippetd(final String snippetd) {
        super.withSnippetd(snippetd);
        return this;
    }

    @Obvious
    @Override
    public Tour withDescription(final String description) {
        super.withDescription(description);
        return this;
    }

    @Obvious
    @Override
    public Tour withAbstractView(final AbstractView abstractView) {
        super.withAbstractView(abstractView);
        return this;
    }

    @Obvious
    @Override
    public Tour withTimePrimitive(final TimePrimitive timePrimitive) {
        super.withTimePrimitive(timePrimitive);
        return this;
    }

    @Obvious
    @Override
    public Tour withStyleUrl(final String styleUrl) {
        super.withStyleUrl(styleUrl);
        return this;
    }

    @Obvious
    @Override
    public Tour withStyleSelector(final List<StyleSelector> styleSelector) {
        super.withStyleSelector(styleSelector);
        return this;
    }

    @Obvious
    @Override
    public Tour withRegion(final Region region) {
        super.withRegion(region);
        return this;
    }

    @Obvious
    @Override
    public Tour withMetadata(final Metadata metadata) {
        super.withMetadata(metadata);
        return this;
    }

    @Obvious
    @Override
    public Tour withExtendedData(final ExtendedData extendedData) {
        super.withExtendedData(extendedData);
        return this;
    }

    @Obvious
    @Override
    public Tour withFeatureSimpleExtension(final List<Object> featureSimpleExtension) {
        super.withFeatureSimpleExtension(featureSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public Tour withFeatureObjectExtension(final List<AbstractObject> featureObjectExtension) {
        super.withFeatureObjectExtension(featureObjectExtension);
        return this;
    }

    @Override
    public Tour clone() {
        Tour copy;
        copy = ((Tour) super.clone());
        copy.playlist = ((playlist == null)?null:((Playlist) playlist.clone()));
        return copy;
    }

}
