
package msi.gama.ext.kml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <itemicon>
 * <p>
 * <state> Specifies the current state of the NetworkLink or Folder. Possible values 
 * are open, closed, error, fetching0, fetching1, and fetching2. These values can be 
 * combined by inserting a space between two values (no comma). <href> Specifies the 
 * URI of the image used in the List View for the Feature. 
 * </p>
 * <p>
 * Icon used in the List view that reflects the state of a Folder or Link fetch. Icons 
 * associated with the open and closed modes are used for Folders and Network Links. 
 * Icons associated with the error and fetching0, fetching1, and fetching2 modes are 
 * used for Network Links. The following screen capture illustrates the Google Earth 
 * icons for these states: 
 * </p>
 * 
 * 
 * 
 */
@XmlType(name = "itemIconStateEnumType")
@XmlEnum
public enum ItemIconState {

    @XmlEnumValue("open")
    OPEN("open"),
    @XmlEnumValue("closed")
    CLOSED("closed"),
    @XmlEnumValue("error")
    ERROR("error"),
    @XmlEnumValue("fetching0")
    FETCHING_0("fetching0"),
    @XmlEnumValue("fetching1")
    FETCHING_1("fetching1"),
    @XmlEnumValue("fetching2")
    FETCHING_2("fetching2");
    private final String value;

    ItemIconState(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ItemIconState fromValue(String v) {
        for (ItemIconState c: ItemIconState.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
