
package msi.gama.ext.kml;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class CoordinatesConverter
    extends XmlAdapter<String, List<Coordinate>>
{


    @Override
    public String marshal(final List<Coordinate> dt)
        throws Exception
    {
        StringBuilder sb = new StringBuilder();
        for (Coordinate coord: dt) {
            sb.append((coord + " "));
        }
        return sb.toString().trim();
    }

    @Override
    public List<Coordinate> unmarshal(final String s)
        throws Exception
    {
        String[] coords = s.replaceAll(",[\\s]+", ",").trim().split("\\s+");
        List<Coordinate> coordinates = new ArrayList<Coordinate>();
        if (coords.length<= 0) {
            return coordinates;
        }
        for (String string: coords) {
            coordinates.add(new Coordinate(string));
        }
        return coordinates;
    }

}
