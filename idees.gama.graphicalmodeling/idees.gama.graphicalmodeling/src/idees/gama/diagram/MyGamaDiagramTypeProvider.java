package idees.gama.diagram;


import org.eclipse.graphiti.dt.AbstractDiagramTypeProvider;
import org.eclipse.graphiti.dt.IDiagramTypeProvider;

public class MyGamaDiagramTypeProvider extends AbstractDiagramTypeProvider implements IDiagramTypeProvider {
	 
	public MyGamaDiagramTypeProvider() {
		setFeatureProvider(new GamaFeatureProvider(this));
	}
	
	
}
	 