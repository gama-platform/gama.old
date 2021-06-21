
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * RefreshMode
 * <p>
 * onChange, onInterval, onExpire 
 * </p>
 * 
 * See Also: 
 * See <Link>
 * 
 * 
 * 
 */
@XmlType(name = "refreshModeEnumType")
@XmlEnum
public enum RefreshMode {

    @XmlEnumValue("onChange")
    ON_CHANGE("onChange"),
    @XmlEnumValue("onInterval")
    ON_INTERVAL("onInterval"),
    @XmlEnumValue("onExpire")
    ON_EXPIRE("onExpire");
    private final String value;

    RefreshMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RefreshMode fromValue(String v) {
        for (RefreshMode c: RefreshMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
