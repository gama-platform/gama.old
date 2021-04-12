
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


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
@XmlType(name = "shapeEnumType")
@XmlEnum
public enum Shape {

    @XmlEnumValue("rectangle")
    RECTANGLE("rectangle"),
    @XmlEnumValue("cylinder")
    CYLINDER("cylinder"),
    @XmlEnumValue("sphere")
    SPHERE("sphere");
    private final String value;

    Shape(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Shape fromValue(String v) {
        for (Shape c: Shape.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
