/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.common.interfaces;

import java.util.*;
import msi.gama.kernel.experiment.IExperimentSpecies;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.outputs.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.architecture.user.UserPanelStatement;
import msi.gaml.types.IType;

/**
 * The class IGui.
 * 
 * @author drogoul
 * @since 18 déc. 2011
 * 
 */
public interface IGui {

	public static final int ERROR = 0;
	public static final int WAIT = 1;
	public static final int INFORM = 2;
	public static final String PLUGIN_ID = "msi.gama.application";
	public static final Map<String, IDisplayCreator> displays = new HashMap();

	void setStatus(String error, int code);

	void run(Runnable block);

	void asyncRun(Runnable block);

	void raise(Throwable ex);

	IGamaView showView(String viewId, String name);

	void tell(String message);

	void error(String error);

	void showParameterView(IExperimentSpecies exp);

	void debugConsole(int cycle, String s);

	void informConsole(String s);

	void updateViewOf(IDisplayOutput output);

	void debug(String string);

	void warn(String string);

	void runtimeError(GamaRuntimeException g);

	IEditorFactory getEditorFactory();

	boolean confirmClose(IExperimentSpecies experiment);

	void showConsoleView();

	void hideMonitorView();

	void setWorkbenchWindowTitle(String string);

	void closeViewOf(IDisplayOutput out);

	IGamaView hideView(String viewId);

	// IDisplay createDisplay(IDisplayLayer layer, double w, double h, IGraphics g);

	boolean isModelingPerspective();

	boolean openModelingPerspective();

	boolean isSimulationPerspective();

	void togglePerspective();

	boolean openSimulationPerspective();

	// IGraphics newGraphics(int width, int height);

	// void clearErrors();

	IDisplaySurface getDisplaySurfaceFor(String keyword, LayeredDisplayOutput layerDisplayOutput, double w, double h,
		Object ... args);

	Map<String, Object> openUserInputDialog(String title, Map<String, Object> initialValues, Map<String, IType> types);

	void openUserControlPanel(IScope scope, UserPanelStatement panel);

	void closeDialogs();

	IAgent getHighlightedAgent();

	void setHighlightedAgent(IAgent a);

	void setSelectedAgent(IAgent a);

	void openEditorAndSelect(Object eObject);

	void updateParameterView(IExperimentSpecies exp);

	void cycleDisplayViews(Set<String> names);

	void prepareForExperiment(IExperimentSpecies exp);

	void cleanAfterExperiment(IExperimentSpecies exp);

	void prepareForSimulation();

	void cleanAfterSimulation();

}
