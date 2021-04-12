
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * ViewRefreshMode
 * <p>
 * never, onRequest, onStop, onRegion 
 * </p>
 * 
 * See Also: 
 * See <Link>
 * 
 * 
 * 
 */
@XmlType(name = "viewRefreshModeEnumType")
@XmlEnum
public enum ViewRefreshMode {

    @XmlEnumValue("never")
    NEVER("never"),
    @XmlEnumValue("onRequest")
    ON_REQUEST("onRequest"),
    @XmlEnumValue("onStop")
    ON_STOP("onStop"),
    @XmlEnumValue("onRegion")
    ON_REGION("onRegion");
    private final String value;

    ViewRefreshMode(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ViewRefreshMode fromValue(String v) {
        for (ViewRefreshMode c: ViewRefreshMode.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
