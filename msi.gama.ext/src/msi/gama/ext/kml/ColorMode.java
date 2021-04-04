
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * ColorMode
 * <p>
 * normal, random 
 * </p>
 * 
 * See Also: 
 * See any element that extends <ColorStyle>
 * 
 * 
 * 
 */
@XmlType(name = "colorModeEnumType")
@XmlEnum
public enum ColorMode {

    @XmlEnumValue("normal")
    NORMAL("normal"),
    @XmlEnumValue("random")
    RANDOM("random");
    private final String value;

    ColorMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ColorMode fromValue(String v) {
        for (ColorMode c: ColorMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
