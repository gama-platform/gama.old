
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * AltitudeMode
 * <p>
 * clampToGround, relativeToGround, absolute 
 * </p>
 * 
 * See Also: 
 * See <LookAt> and <Region>
 * 
 * 
 * 
 */
@XmlType(name = "altitudeModeEnumType")
@XmlEnum
public enum AltitudeMode {

    @XmlEnumValue("clampToGround")
    CLAMP_TO_GROUND("clampToGround"),
    @XmlEnumValue("relativeToGround")
    RELATIVE_TO_GROUND("relativeToGround"),
    @XmlEnumValue("absolute")
    ABSOLUTE("absolute"),
    @XmlEnumValue("clampToSeaFloor")
    CLAMP_TO_SEA_FLOOR("clampToSeaFloor"),
    @XmlEnumValue("relativeToSeaFloor")
    RELATIVE_TO_SEA_FLOOR("relativeToSeaFloor");
    private final String value;

    AltitudeMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AltitudeMode fromValue(String v) {
        for (AltitudeMode c: AltitudeMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
