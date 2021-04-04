
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.gx.AnimatedUpdate;
import msi.gama.ext.kml.gx.FlyTo;
import msi.gama.ext.kml.gx.LatLonQuad;
import msi.gama.ext.kml.gx.MultiTrack;
import msi.gama.ext.kml.gx.Playlist;
import msi.gama.ext.kml.gx.SoundCue;
import msi.gama.ext.kml.gx.Tour;
import msi.gama.ext.kml.gx.TourControl;
import msi.gama.ext.kml.gx.TourPrimitive;
import msi.gama.ext.kml.gx.Track;
import msi.gama.ext.kml.gx.ViewerOptions;
import msi.gama.ext.kml.gx.Wait;


/**
 * <change>
 * <p>
 * Children of this element are the element(s) to be modified, which are identified 
 * by the targetId attribute. 
 * </p>
 * <p>
 * Modifies the values in an element that has already been loaded with a <NetworkLink>. 
 * Within the Change element, the child to be modified must include a targetId attribute 
 * that references the original element's id. 
 * </p>
 * <p>
 * This update can be considered a "sparse update": in the modified element, only the 
 * values listed in <Change> are replaced; all other values remained untouched. When 
 * <Change> is applied to a set of coordinates, the new coordinates replace the current 
 * coordinates. 
 * </p>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChangeType", propOrder = {
    "abstractObject"
})
@XmlRootElement(name = "Change", namespace = "http://www.opengis.net/kml/2.2")
public class Change implements Cloneable
{

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
    @XmlElementRef(name = "AbstractObjectGroup", namespace = "http://www.opengis.net/kml/2.2", required = false)
    protected List<AbstractObject> abstractObject;

    public Change() {
        super();
    }

    /**
     * @see abstractObject
     * 
     */
    public List<AbstractObject> getAbstractObject() {
        if (abstractObject == null) {
            abstractObject = new ArrayList<AbstractObject>();
        }
        return this.abstractObject;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = ((prime*result)+((abstractObject == null)? 0 :abstractObject.hashCode()));
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
        if ((obj instanceof Change) == false) {
            return false;
        }
        Change other = ((Change) obj);
        if (abstractObject == null) {
            if (other.abstractObject!= null) {
                return false;
            }
        } else {
            if (abstractObject.equals(other.abstractObject) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see abstractObject
     * 
     * @param abstractObject
     */
    public void setAbstractObject(final List<AbstractObject> abstractObject) {
        this.abstractObject = abstractObject;
    }

    /**
     * add a value to the abstractObject property collection
     * 
     * @param abstractObject
     *     Objects of the following type are allowed in the list: {@code <}{@link LatLonAltBox}{@code>}{@link JAXBElement}{@code <}{@link Alias}{@code>}{@link JAXBElement}{@code <}{@link Camera}{@code>}{@link JAXBElement}{@code <}{@link Placemark}{@code>}{@link JAXBElement}{@code <}{@link Orientation}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Folder}{@code>}{@link JAXBElement}{@code <}{@link PhotoOverlay}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TimeSpan}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TimeStamp}{@code>}{@link JAXBElement}{@code <}{@link ResourceMap}{@code>}{@link JAXBElement}{@code <}{@link ScreenOverlay}{@code>}{@link JAXBElement}{@code <}{@link Scale}{@code>}{@link JAXBElement}{@code <}{@link FlyTo}{@code>}{@link JAXBElement}{@code <}{@link LabelStyle}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link ViewVolume}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TourPrimitive}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Data}{@code>}{@link JAXBElement}{@code <}{@link PolyStyle}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link ItemIcon}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Wait}{@code>}{@link JAXBElement}{@code <}{@link MultiTrack}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TourControl}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Link}{@code>}{@link JAXBElement}{@code <}{@link Geometry}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link NetworkLink}{@code>}{@link JAXBElement}{@code <}{@link Model}{@code>}{@link JAXBElement}{@code <}{@link ColorStyle}{@code>}{@link JAXBElement}{@code <}{@link ViewerOptions}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link SubStyle}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link MultiGeometry}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link LinearRing}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Document}{@code>}{@link JAXBElement}{@code <}{@link TimeStamp}{@code>}{@link JAXBElement}{@code <}{@link ListStyle}{@code>}{@link JAXBElement}{@code <}{@link Location}{@code>}{@link JAXBElement}{@code <}{@link Feature}{@code>}{@link JAXBElement}{@code <}{@link StyleSelector}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Link}{@code>}{@link JAXBElement}{@code <}{@link Style}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TimeSpan}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link IconStyle}{@code>}{@link JAXBElement}{@code <}{@link StyleMap}{@code>}{@link JAXBElement}{@code <}{@link Overlay}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link GroundOverlay}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link SoundCue}{@code>}{@link JAXBElement}{@code <}{@link Polygon}{@code>}{@link JAXBElement}{@code <}{@link Track}{@code>}{@link JAXBElement}{@code <}{@link AnimatedUpdate}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Playlist}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Link}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link ImagePyramid}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Point}{@code>}{@link JAXBElement}{@code <}{@link LatLonBox}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link LineString}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link TimePrimitive}{@code>}{@link JAXBElement}{@code <}{@link LineStyle}{@code>}{@link JAXBElement}{@code <}{@link Container}{@code>}{@link JAXBElement}{@code <}{@link Lod}{@code>}{@link JAXBElement}{@code <}{@link Tour}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link LatLonQuad}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link Pair}{@code>}{@link JAXBElement}{@code <}{@link AbstractView}{@code>}{@link JAXBElement}{@code <}{@link LookAt}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link BalloonStyle}{@code>}{@link JAXBElement}{@code <}{@link Region}{@code>}{@link JAXBElement}{@code <}{@link AbstractObject}{@code>}{@link JAXBElement}{@code <}{@link SchemaData}{@code>}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public Change addToAbstractObject(final AbstractObject abstractObject) {
        this.getAbstractObject().add(abstractObject);
        return this;
    }

    /**
     * fluent setter
     * @see #setAbstractObject(List<AbstractObject>)
     * 
     * @param abstractObject
     *     required parameter
     */
    public Change withAbstractObject(final List<AbstractObject> abstractObject) {
        this.setAbstractObject(abstractObject);
        return this;
    }

    @Override
    public Change clone() {
        Change copy;
        try {
            copy = ((Change) super.clone());
        } catch (CloneNotSupportedException _x) {
            throw new InternalError((_x.toString()));
        }
        copy.abstractObject = new ArrayList<AbstractObject>((getAbstractObject().size()));
        for (AbstractObject iter: abstractObject) {
            copy.abstractObject.add(iter.clone());
        }
        return copy;
    }

}
