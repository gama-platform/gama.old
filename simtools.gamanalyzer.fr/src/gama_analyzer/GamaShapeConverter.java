package gama_analyzer;

import msi.gama.kernel.simulation.SimulationAgent;
import msi.gama.metamodel.agent.GamlAgent;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaShape;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GamaShapeConverter implements Converter {

	public GamaShapeConverter() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean canConvert(Class arg0) {
		return arg0.equals(GamaShape.class);
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter writer,
			MarshallingContext arg2) {
		GamaShape agt = (GamaShape)arg0;
		writer.setValue(agt.getInnerGeometry().getClass().getName());
			writer.startNode("geom");
			arg2.convertAnother(agt.getInnerGeometry());
            writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		 GamaShape res=new GamaShape();
		 String clname=reader.getValue();
//		 System.out.println("geomcl: "+clname);
		 reader.moveDown();
//		 System.out.println("to conv:"+arg1);
//		 System.out.println("reader:"+reader);
//		 Geometry geom=(Geometry)arg1.convertAnother(res, Geometry.class);
		 Geometry geom;
		try {
			geom = (Geometry)arg1.convertAnother(res, Class.forName(clname));
			 res.setInnerGeometry(geom);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 reader.moveUp();
		
		return res;
	}

}
