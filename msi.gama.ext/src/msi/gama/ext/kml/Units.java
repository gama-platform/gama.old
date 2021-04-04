
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * Units
 * <p>
 * fraction, pixels, insetPixels 
 * </p>
 * 
 * See Also: 
 * See <hotSpot> in <IconStyle>, <ScreenOverlay>
 * 
 * 
 * 
 */
@XmlType(name = "unitsEnumType")
@XmlEnum
public enum Units {

    @XmlEnumValue("fraction")
    FRACTION("fraction"),
    @XmlEnumValue("pixels")
    PIXELS("pixels"),
    @XmlEnumValue("insetPixels")
    INSET_PIXELS("insetPixels");
    private final String value;

    Units(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Units fromValue(String v) {
        for (Units c: Units.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
