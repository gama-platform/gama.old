
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.annotations.Obvious;
import msi.gama.ext.kml.atom.Author;
import msi.gama.ext.kml.atom.Link;
import msi.gama.ext.kml.xal.AddressDetails;


/**
 * <PhotoOverlay>
 * <p>
 * Because <PhotoOverlay> is derived from <Feature>, it can contain one of the two 
 * elements derived from <AbstractView>—either <Camera> or <LookAt>. The Camera (or 
 * LookAt) specifies a viewpoint and a viewing direction (also referred to as a view 
 * vector). The PhotoOverlay is positioned in relation to the viewpoint. Specifically, 
 * the plane of a 2D rectangular image is orthogonal (at right angles to) the view 
 * vector. The normal of this plane—that is, its front, which is the part with the 
 * photo—is oriented toward the viewpoint. 
 * </p>
 * <p>
 * For more information, see the "Topics in KML" page on PhotoOverlay. 
 * </p>
 * <p>
 * The <PhotoOverlay> element allows you to geographically locate a photograph on the 
 * Earth and to specify viewing parameters for this PhotoOverlay. The PhotoOverlay 
 * can be a simple 2D rectangle, a partial or full cylinder, or a sphere (for spherical 
 * panoramas). The overlay is placed at the specified location and oriented toward 
 * the viewpoint. 
 * </p>
 * <p>
 * The URL for the PhotoOverlay image is specified in the <Icon> tag, which is inherited 
 * from <Overlay>. The <Icon> tag must contain an <href> element that specifies the 
 * image file to use for the PhotoOverlay. In the case of a very large image, the <href> 
 * is a special URL that indexes into a pyramid of images of varying resolutions (see 
 * ImagePyramid). 
 * </p>
 * 
 * Extends: 
 * @see: <Overlay>
 * 
 * Contained By: 
 * @see: <Document>
 * @see: <Folder>
 * @see: <kml>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhotoOverlayType", propOrder = {
    "rotation",
    "viewVolume",
    "imagePyramid",
    "point",
    "shape",
    "photoOverlaySimpleExtension",
    "photoOverlayObjectExtension"
})
@XmlRootElement(name = "PhotoOverlay", namespace = "http://www.opengis.net/kml/2.2")
public class PhotoOverlay
    extends Overlay
    implements Cloneable
{

    /**
     * <rotation>
     * <p>
     * Adjusts how the photo is placed inside the field of view. This element is useful 
     * if your photo has been rotated and deviates slightly from a desired horizontal view. 
     * </p>
     * <p>
     * Indicates the angle of rotation of the parent object. A value of 0 means no rotation. 
     * The value is an angle in degrees counterclockwise starting from north. Use ±180 
     * to indicate the rotation of the parent object from 0. The center of the <rotation>, 
     * if not (.5,.5), is specified in <rotationXY>. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double rotation;
    /**
     * <viewvolume>
     * <p>
     * Defines how much of the current scene is visible. Specifying the field of view is 
     * analogous to specifying the lens opening in a physical camera. A small field of 
     * view, like a telephoto lens, focuses on a small part of the scene. A large field 
     * of view, like a wide-angle lens, focuses on a large part of the scene. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(name = "ViewVolume")
    protected ViewVolume viewVolume;
    /**
     * <imagepyramid>
     * <p>
     * For very large images, you'll need to construct an image pyramid, which is a hierarchical 
     * set of images, each of which is an increasingly lower resolution version of the 
     * original image. Each image in the pyramid is subdivided into tiles, so that only 
     * the portions in view need to be loaded. Google Earth calculates the current viewpoint 
     * and loads the tiles that are appropriate to the user's distance from the image. 
     * As the viewpoint moves closer to the PhotoOverlay, Google Earth loads higher resolution 
     * tiles. Since all the pixels in the original image can't be viewed on the screen 
     * at once, this preprocessing allows Google Earth to achieve maximum performance because 
     * it loads only the portions of the image that are in view, and only the pixel details 
     * that can be discerned by the user at the current viewpoint. 
     * </p>
     * <p>
     * When you specify an image pyramid, you also modify the <href> in the <Icon> element 
     * to include specifications for which tiles to load. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(name = "ImagePyramid")
    protected ImagePyramid imagePyramid;
    /**
     * <Point>
     * <p>
     * A geographic location defined by longitude, latitude, and (optional) altitude. When 
     * a Point is contained by a Placemark, the point itself determines the position of 
     * the Placemark's name and icon. When a Point is extruded, it is connected to the 
     * ground with a line. This "tether" uses the current LineStyle. 
     * </p>
     * <p>
     * The <Point> element acts as a <Point> inside a <Placemark> element. It draws an 
     * icon to mark the position of the PhotoOverlay. The icon drawn is specified by the 
     * <styleUrl> and <StyleSelector> fields, just as it is for <Placemark>. 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;Point id="ID"&gt;</strong>
     *   &lt;!-- specific to Point --&gt;
     *   &lt;extrude&gt;0&lt;/extrude&gt;                        &lt;!-- boolean --&gt;
     *   &lt;altitudeMode&gt;clampToGround&lt;/altitudeMode&gt;  
     * 	      &lt;!-- kml:altitudeModeEnum: clampToGround, relativeToGround, or absolute --&gt;
     *         &lt;!-- or, substitute gx:altitudeMode: clampToSeaFloor, relativeToSeaFloor --&gt;
     *   &lt;coordinates&gt;<em>...</em>&lt;/coordinates&gt;<span class="style1"><em>              </em></span>&lt;!-- lon,lat[,alt] --&gt;
     * <strong>&lt;/Point&gt;</strong></pre>
     * 
     * Extends: 
     * @see: <Geometry>
     * 
     * Contained By: 
     * @see: <MultiGeometry>
     * @see: <Placemark>
     * 
     * 
     * 
     */
    @XmlElement(name = "Point")
    protected Point point;
    /**
     * Shape
     * <p>
     * rectangle, cylinder, sphere 
     * </p>
     * 
     * See Also: 
     * See <PhotoOverlay>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "rectangle")
    protected Shape shape;
    @XmlElement(name = "PhotoOverlaySimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> photoOverlaySimpleExtension;
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
    @XmlElement(name = "PhotoOverlayObjectExtensionGroup")
    protected List<AbstractObject> photoOverlayObjectExtension;

    public PhotoOverlay() {
        super();
    }

    /**
     * @see rotation
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getRotation() {
        return rotation;
    }

    /**
     * @see rotation
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setRotation(double value) {
        this.rotation = value;
    }

    /**
     * @see viewVolume
     * 
     * @return
     *     possible object is
     *     {@link ViewVolume}
     *     
     */
    public ViewVolume getViewVolume() {
        return viewVolume;
    }

    /**
     * @see viewVolume
     * 
     * @param value
     *     allowed object is
     *     {@link ViewVolume}
     *     
     */
    public void setViewVolume(ViewVolume value) {
        this.viewVolume = value;
    }

    /**
     * @see imagePyramid
     * 
     * @return
     *     possible object is
     *     {@link ImagePyramid}
     *     
     */
    public ImagePyramid getImagePyramid() {
        return imagePyramid;
    }

    /**
     * @see imagePyramid
     * 
     * @param value
     *     allowed object is
     *     {@link ImagePyramid}
     *     
     */
    public void setImagePyramid(ImagePyramid value) {
        this.imagePyramid = value;
    }

    /**
     * @see point
     * 
     * @return
     *     possible object is
     *     {@link Point}
     *     
     */
    public Point getPoint() {
        return point;
    }

    /**
     * @see point
     * 
     * @param value
     *     allowed object is
     *     {@link Point}
     *     
     */
    public void setPoint(Point value) {
        this.point = value;
    }

    /**
     * @see shape
     * 
     * @return
     *     possible object is
     *     {@link Shape}
     *     
     */
    public Shape getShape() {
        return shape;
    }

    /**
     * @see shape
     * 
     * @param value
     *     allowed object is
     *     {@link Shape}
     *     
     */
    public void setShape(Shape value) {
        this.shape = value;
    }

    /**
     * @see photoOverlaySimpleExtension
     * 
     */
    public List<Object> getPhotoOverlaySimpleExtension() {
        if (photoOverlaySimpleExtension == null) {
            photoOverlaySimpleExtension = new ArrayList<Object>();
        }
        return this.photoOverlaySimpleExtension;
    }

    /**
     * @see photoOverlayObjectExtension
     * 
     */
    public List<AbstractObject> getPhotoOverlayObjectExtension() {
        if (photoOverlayObjectExtension == null) {
            photoOverlayObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.photoOverlayObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        long temp;
        temp = Double.doubleToLongBits(rotation);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((viewVolume == null)? 0 :viewVolume.hashCode()));
        result = ((prime*result)+((imagePyramid == null)? 0 :imagePyramid.hashCode()));
        result = ((prime*result)+((point == null)? 0 :point.hashCode()));
        result = ((prime*result)+((shape == null)? 0 :shape.hashCode()));
        result = ((prime*result)+((photoOverlaySimpleExtension == null)? 0 :photoOverlaySimpleExtension.hashCode()));
        result = ((prime*result)+((photoOverlayObjectExtension == null)? 0 :photoOverlayObjectExtension.hashCode()));
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
        if ((obj instanceof PhotoOverlay) == false) {
            return false;
        }
        PhotoOverlay other = ((PhotoOverlay) obj);
        if (rotation!= other.rotation) {
            return false;
        }
        if (viewVolume == null) {
            if (other.viewVolume!= null) {
                return false;
            }
        } else {
            if (viewVolume.equals(other.viewVolume) == false) {
                return false;
            }
        }
        if (imagePyramid == null) {
            if (other.imagePyramid!= null) {
                return false;
            }
        } else {
            if (imagePyramid.equals(other.imagePyramid) == false) {
                return false;
            }
        }
        if (point == null) {
            if (other.point!= null) {
                return false;
            }
        } else {
            if (point.equals(other.point) == false) {
                return false;
            }
        }
        if (shape == null) {
            if (other.shape!= null) {
                return false;
            }
        } else {
            if (shape.equals(other.shape) == false) {
                return false;
            }
        }
        if (photoOverlaySimpleExtension == null) {
            if (other.photoOverlaySimpleExtension!= null) {
                return false;
            }
        } else {
            if (photoOverlaySimpleExtension.equals(other.photoOverlaySimpleExtension) == false) {
                return false;
            }
        }
        if (photoOverlayObjectExtension == null) {
            if (other.photoOverlayObjectExtension!= null) {
                return false;
            }
        } else {
            if (photoOverlayObjectExtension.equals(other.photoOverlayObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a new instance of {@link ViewVolume} and set it to viewVolume.
     * 
     * This method is a short version for:
     * <code>
     * ViewVolume viewVolume = new ViewVolume();
     * this.setViewVolume(viewVolume); </code>
     * 
     * 
     */
    public ViewVolume createAndSetViewVolume() {
        ViewVolume newValue = new ViewVolume();
        this.setViewVolume(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link ImagePyramid} and set it to imagePyramid.
     * 
     * This method is a short version for:
     * <code>
     * ImagePyramid imagePyramid = new ImagePyramid();
     * this.setImagePyramid(imagePyramid); </code>
     * 
     * 
     */
    public ImagePyramid createAndSetImagePyramid() {
        ImagePyramid newValue = new ImagePyramid();
        this.setImagePyramid(newValue);
        return newValue;
    }

    /**
     * Creates a new instance of {@link Point} and set it to point.
     * 
     * This method is a short version for:
     * <code>
     * Point point = new Point();
     * this.setPoint(point); </code>
     * 
     * 
     */
    public Point createAndSetPoint() {
        Point newValue = new Point();
        this.setPoint(newValue);
        return newValue;
    }

    /**
     * @see photoOverlaySimpleExtension
     * 
     * @param photoOverlaySimpleExtension
     */
    public void setPhotoOverlaySimpleExtension(final List<Object> photoOverlaySimpleExtension) {
        this.photoOverlaySimpleExtension = photoOverlaySimpleExtension;
    }

    /**
     * add a value to the photoOverlaySimpleExtension property collection
     * 
     * @param photoOverlaySimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public PhotoOverlay addToPhotoOverlaySimpleExtension(final Object photoOverlaySimpleExtension) {
        this.getPhotoOverlaySimpleExtension().add(photoOverlaySimpleExtension);
        return this;
    }

    /**
     * @see photoOverlayObjectExtension
     * 
     * @param photoOverlayObjectExtension
     */
    public void setPhotoOverlayObjectExtension(final List<AbstractObject> photoOverlayObjectExtension) {
        this.photoOverlayObjectExtension = photoOverlayObjectExtension;
    }

    /**
     * add a value to the photoOverlayObjectExtension property collection
     * 
     * @param photoOverlayObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public PhotoOverlay addToPhotoOverlayObjectExtension(final AbstractObject photoOverlayObjectExtension) {
        this.getPhotoOverlayObjectExtension().add(photoOverlayObjectExtension);
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
    public PhotoOverlay addToObjectSimpleExtension(final Object objectSimpleExtension) {
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
    public PhotoOverlay addToStyleSelector(final StyleSelector styleSelector) {
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
    public PhotoOverlay addToFeatureSimpleExtension(final Object featureSimpleExtension) {
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
    public PhotoOverlay addToFeatureObjectExtension(final AbstractObject featureObjectExtension) {
        super.getFeatureObjectExtension().add(featureObjectExtension);
        return this;
    }

    /**
     * @see overlaySimpleExtension
     * 
     */
    @Obvious
    @Override
    public void setOverlaySimpleExtension(final List<Object> overlaySimpleExtension) {
        super.setOverlaySimpleExtension(overlaySimpleExtension);
    }

    @Obvious
    @Override
    public PhotoOverlay addToOverlaySimpleExtension(final Object overlaySimpleExtension) {
        super.getOverlaySimpleExtension().add(overlaySimpleExtension);
        return this;
    }

    /**
     * @see overlayObjectExtension
     * 
     */
    @Obvious
    @Override
    public void setOverlayObjectExtension(final List<AbstractObject> overlayObjectExtension) {
        super.setOverlayObjectExtension(overlayObjectExtension);
    }

    @Obvious
    @Override
    public PhotoOverlay addToOverlayObjectExtension(final AbstractObject overlayObjectExtension) {
        super.getOverlayObjectExtension().add(overlayObjectExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setRotation(double)
     * 
     * @param rotation
     *     required parameter
     */
    public PhotoOverlay withRotation(final double rotation) {
        this.setRotation(rotation);
        return this;
    }

    /**
     * fluent setter
     * @see #setViewVolume(ViewVolume)
     * 
     * @param viewVolume
     *     required parameter
     */
    public PhotoOverlay withViewVolume(final ViewVolume viewVolume) {
        this.setViewVolume(viewVolume);
        return this;
    }

    /**
     * fluent setter
     * @see #setImagePyramid(ImagePyramid)
     * 
     * @param imagePyramid
     *     required parameter
     */
    public PhotoOverlay withImagePyramid(final ImagePyramid imagePyramid) {
        this.setImagePyramid(imagePyramid);
        return this;
    }

    /**
     * fluent setter
     * @see #setPoint(Point)
     * 
     * @param point
     *     required parameter
     */
    public PhotoOverlay withPoint(final Point point) {
        this.setPoint(point);
        return this;
    }

    /**
     * fluent setter
     * @see #setShape(Shape)
     * 
     * @param shape
     *     required parameter
     */
    public PhotoOverlay withShape(final Shape shape) {
        this.setShape(shape);
        return this;
    }

    /**
     * fluent setter
     * @see #setPhotoOverlaySimpleExtension(List<Object>)
     * 
     * @param photoOverlaySimpleExtension
     *     required parameter
     */
    public PhotoOverlay withPhotoOverlaySimpleExtension(final List<Object> photoOverlaySimpleExtension) {
        this.setPhotoOverlaySimpleExtension(photoOverlaySimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setPhotoOverlayObjectExtension(List<AbstractObject>)
     * 
     * @param photoOverlayObjectExtension
     *     required parameter
     */
    public PhotoOverlay withPhotoOverlayObjectExtension(final List<AbstractObject> photoOverlayObjectExtension) {
        this.setPhotoOverlayObjectExtension(photoOverlayObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withName(final String name) {
        super.withName(name);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withVisibility(final Boolean visibility) {
        super.withVisibility(visibility);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withOpen(final Boolean open) {
        super.withOpen(open);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withAtomAuthor(final Author atomAuthor) {
        super.withAtomAuthor(atomAuthor);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withAtomLink(final Link atomLink) {
        super.withAtomLink(atomLink);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withAddress(final String address) {
        super.withAddress(address);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withXalAddressDetails(final AddressDetails xalAddressDetails) {
        super.withXalAddressDetails(xalAddressDetails);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withPhoneNumber(final String phoneNumber) {
        super.withPhoneNumber(phoneNumber);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withSnippet(final Snippet snippet) {
        super.withSnippet(snippet);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withSnippetd(final String snippetd) {
        super.withSnippetd(snippetd);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withDescription(final String description) {
        super.withDescription(description);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withAbstractView(final AbstractView abstractView) {
        super.withAbstractView(abstractView);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withTimePrimitive(final TimePrimitive timePrimitive) {
        super.withTimePrimitive(timePrimitive);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withStyleUrl(final String styleUrl) {
        super.withStyleUrl(styleUrl);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withStyleSelector(final List<StyleSelector> styleSelector) {
        super.withStyleSelector(styleSelector);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withRegion(final Region region) {
        super.withRegion(region);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withMetadata(final Metadata metadata) {
        super.withMetadata(metadata);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withExtendedData(final ExtendedData extendedData) {
        super.withExtendedData(extendedData);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withFeatureSimpleExtension(final List<Object> featureSimpleExtension) {
        super.withFeatureSimpleExtension(featureSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withFeatureObjectExtension(final List<AbstractObject> featureObjectExtension) {
        super.withFeatureObjectExtension(featureObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withColor(final String color) {
        super.withColor(color);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withDrawOrder(final int drawOrder) {
        super.withDrawOrder(drawOrder);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withIcon(final Icon icon) {
        super.withIcon(icon);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withOverlaySimpleExtension(final List<Object> overlaySimpleExtension) {
        super.withOverlaySimpleExtension(overlaySimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public PhotoOverlay withOverlayObjectExtension(final List<AbstractObject> overlayObjectExtension) {
        super.withOverlayObjectExtension(overlayObjectExtension);
        return this;
    }

    @Override
    public PhotoOverlay clone() {
        PhotoOverlay copy;
        copy = ((PhotoOverlay) super.clone());
        copy.viewVolume = ((viewVolume == null)?null:((ViewVolume) viewVolume.clone()));
        copy.imagePyramid = ((imagePyramid == null)?null:((ImagePyramid) imagePyramid.clone()));
        copy.point = ((point == null)?null:((Point) point.clone()));
        copy.photoOverlaySimpleExtension = new ArrayList<Object>((getPhotoOverlaySimpleExtension().size()));
        for (Object iter: photoOverlaySimpleExtension) {
            copy.photoOverlaySimpleExtension.add(iter);
        }
        copy.photoOverlayObjectExtension = new ArrayList<AbstractObject>((getPhotoOverlayObjectExtension().size()));
        for (AbstractObject iter: photoOverlayObjectExtension) {
            copy.photoOverlayObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
