package msi.gama.gui.displays.layers.listeners;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Set;
import javax.imageio.ImageTypeSpecifier;
import com.jmex.model.collada.schema.InputGlobal;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.common.interfaces.ILayer;
import msi.gama.gui.displays.layers.EventLayer;
import msi.gama.kernel.simulation.Scheduler;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.GamaPoint;
import msi.gama.outputs.layers.EventLayerStatement;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.descriptions.StatementDescription;
import msi.gaml.descriptions.StringBasedExpressionDescription;
import msi.gaml.species.ISpecies;
import msi.gaml.statements.Arguments;
import msi.gaml.statements.IStatement;
import msi.gaml.types.IType;
import msi.gaml.types.Types;


public class CustomisedEventListener implements MouseListener {
	public final static int MOUSE_PRESS = 0;
	public final static int MOUSE_RELEASED = 1;
	public final static int MOUSE_CLICKED = 2;
	
	private IScope currentScope;
	private int listenedEvent;
	private EventLayer parent;
	IStatement.WithArgs executer;
	
	public CustomisedEventListener(EventLayer prt, String event, String action)
	{
		this.parent=prt;
		//this.myStatement=myEStt;
		listenedEvent = getListeningEvent(event);
		this.currentScope = GAMA.getDefaultScope();
		ISpecies context = currentScope.getAgentScope().getSpecies();
		this.executer = context.getAction(action);	
	}
	
	public Point getMouseLocation(Point initialLoc)
	{
		
		double x  = initialLoc.x - parent.getDisplay().getOriginX();
		double y  = initialLoc.y - parent.getDisplay().getOriginY();
		return new Point((int)( x / parent.getDisplayWidth() * parent.getEnvironmentSize().getX()),(int)(  
			y / parent.getDisplayHeight() * parent.getEnvironmentSize().getY()));
	}
	
	public static int getListeningEvent(String eventTypeName)
	{
		if(eventTypeName.equals(IKeyword.MOUSE_DOWN) )
		{
			
			return MOUSE_PRESS;
		}
		if(eventTypeName.equals(IKeyword.MOUSE_UP))
		{
			return MOUSE_RELEASED;
		}
		if(eventTypeName.equals(IKeyword.MOUSE_CLICKED))
		{
			return MOUSE_CLICKED;
		}
		
			return -1;
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		///currentScope.getSimulationScope().
		
		if(MOUSE_PRESS == listenedEvent && arg0.getButton() == MouseEvent.BUTTON1)
		{
			sendEvent(arg0);
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(MOUSE_RELEASED == listenedEvent && arg0.getButton() == MouseEvent.BUTTON1 )
		{
			sendEvent(arg0);
		}
	}
	
	private void sendEvent(MouseEvent arg0)
	{
		Point pp = getMouseLocation(arg0.getPoint());
		if(pp.getX()< 0 || pp.getY() < 0 || pp.getX() >= parent.getEnvironmentSize().getX() || pp.getY() >= parent.getEnvironmentSize().getY())
			 return;
		
		Arguments args = new Arguments();
		ArrayList<IAgent> agentset = parent.selectAgent(arg0.getX(), arg0.getY());
		args.put("location", GAMA.getExpressionFactory().createConst(new GamaPoint(pp.x,pp.y), Types.get(IType.POINT)) ); //GAMA.compileExpression("point(["+pp.x +","+pp.y +"])", currentScope.getWorldScope()));
		args.put("selected_agents", GAMA.getExpressionFactory().createConst(new GamaList(agentset), Types.get(IType.LIST)) ); //GAMA.compileExpression("point(["+pp.x +","+pp.y +"])", currentScope.getWorldScope()));
		executer.setRuntimeArgs(args);
		executer.executeOn(currentScope);	
	}
	
}
