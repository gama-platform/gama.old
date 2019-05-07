package msi.gama.util.graph.layout;

import java.util.Map;

import org.jgrapht.ext.JGraphXAdapter;

import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;

import msi.gama.runtime.IScope;
import msi.gama.util.graph.GamaGraph;

public class JGraphXStaticCircleLayout implements IStaticLayout {

	@Override
	public void doLayoutOneShot(IScope scope, GamaGraph<?, ?> graph, long timeout, Map<String, Object> options) {
		// TODO Auto-generated method stub
		JGraphXAdapter jgxAdapter = new JGraphXAdapter<>(graph);
		
		mxIGraphLayout layout = new mxCircleLayout(jgxAdapter);
		
		layout.execute(jgxAdapter.getDefaultParent());
	}

}
