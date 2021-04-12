
package msi.gama.ext.kml.gx;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import msi.gama.ext.kml.Update;
import msi.gama.ext.kml.annotations.Obvious;


/**
 * <gx:AnimatedUpdate>
 * <p>
 * <gx:AnimatedUpdate> controls changes during a tour to KML features, using <Update>. 
 * Changes to KML features will not modify the DOM - that is, any changes will be reverted 
 * when the tour is over, and will not be saved in the KML at any time. 
 * </p>
 * <p>
 * <gx:AnimatedUpdate> should also contain a <gx:duration> value to specify the length 
 * of time in seconds over which the update takes place. Integer, float, and color 
 * fields are smoothly animated from original to new value across the duration; boolean, 
 * string, and other values that don't lend to interpolation are updated at the end 
 * of the duration. 
 * </p>
 * <p>
 * Refer to Tour timelines in the Touring chapter of the KML Developer's Guide for 
 * information about <gx:AnimatedUpdate> and the tour timeline. 
 * </p>
 * 
 * Syntax: 
 * <pre>&lt;gx:AnimatedUpdate&gt;
 *   &lt;gx:duration&gt;0.0&lt;/gx:duration&gt;    &lt;!-- double, specifies time in seconds --&gt;
 *   &lt;Update&gt;
 *     &lt;targetHref&gt;...&lt;/targetHref&gt;    &lt;!-- required; can contain a URL or be left blank --&gt;
 *                                                 &lt;!-- (to target elements within the same file --&gt;
 *     &lt;Change&gt;...&lt;/Change&gt;
 *     &lt;Create&gt;...&lt;/Create&gt;
 *     &lt;Delete&gt;...&lt;/Delete&gt;
 *   &lt;/Update&gt;
 * &lt;/gx:AnimatedUpdate&gt;</pre>
 * 
 * Extends: 
 * @see: <gx:TourPrimitive>
 * 
 * Contains: 
 * @see: <Update>
 * @see: <gx:duration>
 * 
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AnimatedUpdateType", propOrder = {
    "duration",
    "update",
    "delayedStart"
})
@XmlRootElement(name = "AnimatedUpdate", namespace = "http://www.google.com/kml/ext/2.2")
public class AnimatedUpdate
    extends TourPrimitive
    implements Cloneable
{

    /**
     * <gx:duration>
     * <p>
     * <gx:AnimatedUpdate> <gx:duration>5.0</gx:duration> <Update> .... </Update> </gx:AnimatedUpdate> 
     * </p>
     * <p>
     * <gx:FlyTo> <gx:flyToMode>bounce</gx:flyToMode> <gx:duration>10.2</gx:duration> <!-- 
     * AbstractView --> ... <!-- /AbstractView --> </gx:FlyTo> 
     * </p>
     * <p>
     * <gx:duration> extends gx:TourPrimitive by specifying a time-span for events. The 
     * time is written as seconds using XML's double datatype. 
     * </p>
     * <p>
     * Duration and <gx:AnimatedUpdate> 
     * </p>
     * <p>
     * Duration and <gx:FlyTo> 
     * </p>
     * <p>
     * Specifies the length of time over which the update takes place. Integer, float, 
     * and color fields are smoothly animated from original to new value across the duration; 
     * boolean, string, and other values that don't lend to interpolation are updated at 
     * the end of the duration. 
     * </p>
     * <p>
     * When a duration is included within a <gx:FlyTo> element, it specifies the length 
     * of time that the browser takes to fly from the previous point to the specified point. 
     * </p>
     * 
     * Syntax: 
     * <pre>&lt;gx:duration&gt;0.0&lt;/gx:duration&gt;            &lt;!-- double --&gt;
     * </pre>
     * 
     * 
     * 
     */
    @XmlElement(defaultValue = "0.0")
    protected double duration;
    /**
     * <Update>
     * <p>
     * Specifies an addition, change, or deletion to KML data that has already been loaded 
     * using the specified URL. The <targetHref> specifies the .kml or .kmz file whose 
     * data (within Google Earth) is to be modified. <Update> is always contained in a 
     * NetworkLinkControl. Furthermore, the file containing the NetworkLinkControl must 
     * have been loaded by a NetworkLink. See the "Topics in KML" page on Updates for a 
     * detailed example of how Update works. 
     * </p>
     * <p>
     * With <Update>, you can specify any number of Change, Create, and Delete tags for 
     * a .kml file or .kmz archive that has previously been loaded with a network link. 
     * See <Update>. 
     * </p>
     * 
     * Syntax: 
     * <pre><strong>&lt;Update&gt;
     *   </strong>&lt;targetHref&gt;...&lt;targetHref&gt;    &lt;!-- URL --&gt;
     *   &lt;Change&gt;...&lt;/Change&gt;
     *   &lt;Create&gt;...&lt;/Create&gt;
     *   &lt;Delete&gt;...&lt;/Delete&gt;
     * <strong>&lt;/Update&gt;</strong></pre>
     * 
     * Contained By: 
     * @see: <NetworkLinkControl>
     * @see: Note: This element was deprecated in KML Release 2.1 and is replaced by <Link>, which provides the additional functionality of Regions. The <Url> tag will still work in Google Earth, but use of the newer <Link> tag is encouraged.
     * @see: Use this element to set the location of the link to the KML file, to define the refresh options for the server and viewer changes, and to populate a variable to return useful client information to the server.
     * 
     * 
     * 
     */
    @XmlElement(name = "Update", namespace = "http://www.opengis.net/kml/2.2")
    protected Update update;
    @XmlElement(defaultValue = "0.0")
    protected double delayedStart;

    public AnimatedUpdate() {
        super();
    }

    /**
     * @see duration
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getDuration() {
        return duration;
    }

    /**
     * @see duration
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setDuration(double value) {
        this.duration = value;
    }

    /**
     * @see update
     * 
     * @return
     *     possible object is
     *     {@link Update}
     *     
     */
    public Update getUpdate() {
        return update;
    }

    /**
     * @see update
     * 
     * @param value
     *     allowed object is
     *     {@link Update}
     *     
     */
    public void setUpdate(Update value) {
        this.update = value;
    }

    /**
     * @see delayedStart
     * 
     * @return
     *     possible object is
     *     {@link Double}
     *     
     */
    public double getDelayedStart() {
        return delayedStart;
    }

    /**
     * @see delayedStart
     * 
     * @param value
     *     allowed object is
     *     {@link Double}
     *     
     */
    public void setDelayedStart(double value) {
        this.delayedStart = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(duration);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
        result = ((prime*result)+((update == null)? 0 :update.hashCode()));
        temp = Double.doubleToLongBits(delayedStart);
        result = ((prime*result)+((int)(temp^(temp >>>(32)))));
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
        if ((obj instanceof AnimatedUpdate) == false) {
            return false;
        }
        AnimatedUpdate other = ((AnimatedUpdate) obj);
        if (duration!= other.duration) {
            return false;
        }
        if (update == null) {
            if (other.update!= null) {
                return false;
            }
        } else {
            if (update.equals(other.update) == false) {
                return false;
            }
        }
        if (delayedStart!= other.delayedStart) {
            return false;
        }
        return true;
    }

    /**
     * Creates a new instance of {@link Update} and set it to update.
     * 
     * This method is a short version for:
     * <code>
     * Update update = new Update();
     * this.setUpdate(update); </code>
     * 
     * 
     * @param createOrDeleteOrChange
     *     required parameter
     * @param targetHref
     *     required parameter
     */
    public Update createAndSetUpdate(final String targetHref, final List<Object> createOrDeleteOrChange) {
        Update newValue = new Update(targetHref, createOrDeleteOrChange);
        this.setUpdate(newValue);
        return newValue;
    }

    /**
     * fluent setter
     * @see #setDuration(double)
     * 
     * @param duration
     *     required parameter
     */
    public AnimatedUpdate withDuration(final double duration) {
        this.setDuration(duration);
        return this;
    }

    /**
     * fluent setter
     * @see #setUpdate(Update)
     * 
     * @param update
     *     required parameter
     */
    public AnimatedUpdate withUpdate(final Update update) {
        this.setUpdate(update);
        return this;
    }

    /**
     * fluent setter
     * @see #setDelayedStart(double)
     * 
     * @param delayedStart
     *     required parameter
     */
    public AnimatedUpdate withDelayedStart(final double delayedStart) {
        this.setDelayedStart(delayedStart);
        return this;
    }

    @Obvious
    @Override
    public AnimatedUpdate withObjectSimpleExtension(final List<Object> objectSimpleExtension) {
        super.withObjectSimpleExtension(objectSimpleExtension);
        return this;
    }

    @Obvious
    @Override
    public AnimatedUpdate withId(final String id) {
        super.withId(id);
        return this;
    }

    @Obvious
    @Override
    public AnimatedUpdate withTargetId(final String targetId) {
        super.withTargetId(targetId);
        return this;
    }

    @Override
    public AnimatedUpdate clone() {
        AnimatedUpdate copy;
        copy = ((AnimatedUpdate) super.clone());
        copy.update = ((update == null)?null:((Update) update.clone()));
        return copy;
    }

}
