
package msi.gama.ext.kml.gx;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for playModeEnumType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="playModeEnumType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="pause"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "playModeEnumType")
@XmlEnum
public enum PlayMode {

    @XmlEnumValue("pause")
    PAUSE("pause");
    private final String value;

    PlayMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PlayMode fromValue(String v) {
        for (PlayMode c: PlayMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
