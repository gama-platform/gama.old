
package msi.gama.ext.kml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public final class BooleanConverter
    extends XmlAdapter<Integer, Boolean>
{


    @Override
    public Boolean unmarshal(final Integer i)
        throws Exception
    {
        return ((i == null)?null:(i == 1));
    }

    @Override
    public Integer marshal(final Boolean b)
        throws Exception
    {
        return ((b == null)?null:(b? 1 : 0));
    }

}
