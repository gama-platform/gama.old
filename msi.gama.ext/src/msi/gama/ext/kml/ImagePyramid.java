
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
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImagePyramidType", propOrder = {
    "tileSize",
    "maxWidth",
    "maxHeight",
    "gridOrigin",
    "imagePyramidSimpleExtension",
    "imagePyramidObjectExtension"
})
@XmlRootElement(name = "ImagePyramid", namespace = "http://www.opengis.net/kml/2.2")
public class ImagePyramid
    extends AbstractObject
    implements Cloneable
{

    /**
     * <tilesize>
     * <p>
     * Size of the tiles, in pixels. Tiles must be square, and <tileSize> must be a power 
     * of 2. A tile size of 256 (the default) or 512 is recommended. The original image 
     * is divided into tiles of this size, at varying resolutions. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "256")
    protected int tileSize;
    /**
     * <maxwidth>
     * <p>
     * Width in pixels of the original image. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    protected int maxWidth;
    /**
     * <maxheight>
     * <p>
     * Height in pixels of the original image. 
     * </p>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0")
    protected int maxHeight;
    /**
     * GridOrigin
     * <p>
     * lowerLeft, upperLeft 
     * </p>
     * 
     * See Also: 
     * See <PhotoOverlay>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "lowerLeft")
    protected GridOrigin gridOrigin;
    @XmlElement(name = "ImagePyramidSimpleExtensionGroup")
    @XmlSchemaType(name = "anySimpleType")
    protected List<Object> imagePyramidSimpleExtension;
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
    @XmlElement(name = "ImagePyramidObjectExtensionGroup")
    protected List<AbstractObject> imagePyramidObjectExtension;

    public ImagePyramid() {
        super();
    }

    /**
     * @see tileSize
     * 
     * @return
     *     possible object is
     *     {@link Integer}
     *     
     */
    public int getTileSize() {
        return tileSize;
    }

    /**
     * @see tileSize
     * 
     * @param value
     *     allowed object is
     *     {@link Integer}
     *     
     */
    public void setTileSize(int value) {
        this.tileSize = value;
    }

    /**
     * @see maxWidth
     * 
     * @return
     *     possible object is
     *     {@link Integer}
     *     
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * @see maxWidth
     * 
     * @param value
     *     allowed object is
     *     {@link Integer}
     *     
     */
    public void setMaxWidth(int value) {
        this.maxWidth = value;
    }

    /**
     * @see maxHeight
     * 
     * @return
     *     possible object is
     *     {@link Integer}
     *     
     */
    public int getMaxHeight() {
        return maxHeight;
    }

    /**
     * @see maxHeight
     * 
     * @param value
     *     allowed object is
     *     {@link Integer}
     *     
     */
    public void setMaxHeight(int value) {
        this.maxHeight = value;
    }

    /**
     * @see gridOrigin
     * 
     * @return
     *     possible object is
     *     {@link GridOrigin}
     *     
     */
    public GridOrigin getGridOrigin() {
        return gridOrigin;
    }

    /**
     * @see gridOrigin
     * 
     * @param value
     *     allowed object is
     *     {@link GridOrigin}
     *     
     */
    public void setGridOrigin(GridOrigin value) {
        this.gridOrigin = value;
    }

    /**
     * @see imagePyramidSimpleExtension
     * 
     */
    public List<Object> getImagePyramidSimpleExtension() {
        if (imagePyramidSimpleExtension == null) {
            imagePyramidSimpleExtension = new ArrayList<Object>();
        }
        return this.imagePyramidSimpleExtension;
    }

    /**
     * @see imagePyramidObjectExtension
     * 
     */
    public List<AbstractObject> getImagePyramidObjectExtension() {
        if (imagePyramidObjectExtension == null) {
            imagePyramidObjectExtension = new ArrayList<AbstractObject>();
        }
        return this.imagePyramidObjectExtension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = ((prime*result)+ tileSize);
        result = ((prime*result)+ maxWidth);
        result = ((prime*result)+ maxHeight);
        result = ((prime*result)+((gridOrigin == null)? 0 :gridOrigin.hashCode()));
        result = ((prime*result)+((imagePyramidSimpleExtension == null)? 0 :imagePyramidSimpleExtension.hashCode()));
        result = ((prime*result)+((imagePyramidObjectExtension == null)? 0 :imagePyramidObjectExtension.hashCode()));
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
        if ((obj instanceof ImagePyramid) == false) {
            return false;
        }
        ImagePyramid other = ((ImagePyramid) obj);
        if (tileSize!= other.tileSize) {
            return false;
        }
        if (maxWidth!= other.maxWidth) {
            return false;
        }
        if (maxHeight!= other.maxHeight) {
            return false;
        }
        if (gridOrigin == null) {
            if (other.gridOrigin!= null) {
                return false;
            }
        } else {
            if (gridOrigin.equals(other.gridOrigin) == false) {
                return false;
            }
        }
        if (imagePyramidSimpleExtension == null) {
            if (other.imagePyramidSimpleExtension!= null) {
                return false;
            }
        } else {
            if (imagePyramidSimpleExtension.equals(other.imagePyramidSimpleExtension) == false) {
                return false;
            }
        }
        if (imagePyramidObjectExtension == null) {
            if (other.imagePyramidObjectExtension!= null) {
                return false;
            }
        } else {
            if (imagePyramidObjectExtension.equals(other.imagePyramidObjectExtension) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * @see imagePyramidSimpleExtension
     * 
     * @param imagePyramidSimpleExtension
     */
    public void setImagePyramidSimpleExtension(final List<Object> imagePyramidSimpleExtension) {
        this.imagePyramidSimpleExtension = imagePyramidSimpleExtension;
    }

    /**
     * add a value to the imagePyramidSimpleExtension property collection
     * 
     * @param imagePyramidSimpleExtension
     *     Objects of the following type are allowed in the list: {@link Object}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ImagePyramid addToImagePyramidSimpleExtension(final Object imagePyramidSimpleExtension) {
        this.getImagePyramidSimpleExtension().add(imagePyramidSimpleExtension);
        return this;
    }

    /**
     * @see imagePyramidObjectExtension
     * 
     * @param imagePyramidObjectExtension
     */
    public void setImagePyramidObjectExtension(final List<AbstractObject> imagePyramidObjectExtension) {
        this.imagePyramidObjectExtension = imagePyramidObjectExtension;
    }

    /**
     * add a value to the imagePyramidObjectExtension property collection
     * 
     * @param imagePyramidObjectExtension
     *     Objects of the following type are allowed in the list: {@link AbstractObject}
     * @return
     *     <tt>true</tt> (as general contract of <tt>Collection.add</tt>). 
     */
    public ImagePyramid addToImagePyramidObjectExtension(final AbstractObject imagePyramidObjectExtension) {
        this.getImagePyramidObjectExtension().add(imagePyramidObjectExtension);
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
    public ImagePyramid addToObjectSimpleExtension(final Object objectSimpleExtension) {
        super.getObjectSimpleExtension().add(objectSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setTileSize(int)
     * 
     * @param tileSize
     *     required parameter
     */
    public ImagePyramid withTileSize(final int tileSize) {
        this.setTileSize(tileSize);
        return this;
    }

    /**
     * fluent setter
     * @see #setMaxWidth(int)
     * 
     * @param maxWidth
     *     required parameter
     */
    public ImagePyramid withMaxWidth(final int maxWidth) {
        this.setMaxWidth(maxWidth);
        return this;
    }

    /**
     * fluent setter
     * @see #setMaxHeight(int)
     * 
     * @param maxHeight
     *     required parameter
     */
    public ImagePyramid withMaxHeight(final int maxHeight) {
        this.setMaxHeight(maxHeight);
        return this;
    }

    /**
     * fluent setter
     * @see #setGridOrigin(GridOrigin)
     * 
     * @param gridOrigin
     *     required parameter
     */
    public ImagePyramid withGridOrigin(final GridOrigin gridOrigin) {
        this.setGridOrigin(gridOrigin);
        return this;
    }

    /**
     * fluent setter
     * @see #setImagePyramidSimpleExtension(List<Object>)
     * 
     * @param imagePyramidSimpleExtension
     *     required parameter
     */
    public ImagePyramid withImagePyramidSimpleExtension(final List<Object> imagePyramidSimpleExtension) {
        this.setImagePyramidSimpleExtension(imagePyramidSimpleExtension);
        return this;
    }

    /**
     * fluent setter
     * @see #setImagePyramidObjectExtension(List<AbstractObject>)
     * 
     * @param imagePyramidObjectExtension
     *     required parameter
     */
    public ImagePyramid withImagePyramidObjectExtension(final List<AbstractObject> imagePyramidObjectExtension) {
        this.setImagePyramidObjectExtension(imagePyramidObjectExtension);
        return this;
    }

    @Obvious
    @Override
    public ImagePyramid withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public ImagePyramid withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public ImagePyramid withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public ImagePyramid clone() {
        ImagePyramid copy;
        copy = ((ImagePyramid) super.clone());
        copy.imagePyramidSimpleExtension = new ArrayList<Object>((getImagePyramidSimpleExtension().size()));
        for (Object iter: imagePyramidSimpleExtension) {
            copy.imagePyramidSimpleExtension.add(iter);
        }
        copy.imagePyramidObjectExtension = new ArrayList<AbstractObject>((getImagePyramidObjectExtension().size()));
        for (AbstractObject iter: imagePyramidObjectExtension) {
            copy.imagePyramidObjectExtension.add(iter.clone());
        }
        return copy;
    }

}
