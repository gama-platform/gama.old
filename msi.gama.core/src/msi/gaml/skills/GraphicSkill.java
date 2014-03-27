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
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.skills;

import java.awt.Color;
import java.util.Map;


import msi.gama.common.interfaces.*;
import msi.gama.common.util.*;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.ITopology;
import msi.gama.metamodel.topology.graph.*;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.arg;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.skill;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.graph.IGraph;
import msi.gama.util.path.*;
import msi.gaml.operators.*;
import msi.gaml.operators.Spatial.Punctal;
import msi.gaml.skills.GridSkill.IGridAgent;
import msi.gaml.types.*;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.precision.GeometryPrecisionReducer;

import org.geotools.brewer.color.*;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.filter.function.Classifier;
import org.geotools.filter.function.ExplicitClassifier;
import org.geotools.filter.function.RangedClassifier;
import org.geotools.styling.FeatureTypeStyle;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.filter.FilterFactory2;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.PropertyName;



/**
 * GraphicSkill : This class is intended to define the minimal set of behaviours required from a
 * graphical agent. Each member that has a meaning in GAML is annotated with the
 * respective tags (vars, getter, setter, init, action & args)
 * 
 * @author Grignard Feb 2014
 */

@doc("The graphic skill is intended to define the minimal set of behaviours required from a "
	+ "graphical agent")
@vars({
	@var(name = IKeyword.TRANSPARENCY, type = IType.FLOAT, init = "1.0", doc = @doc("the transparency of the agent (between 0.0 and 1.0)")),})
@skill(name = IKeyword.GRAPHIC_SKILL)
public class GraphicSkill extends Skill {
	
	
	public static final PaletteType ALL = new PaletteType(true, true, "ALL");
    public static final PaletteType SUITABLE_RANGED = new PaletteType(true, false);
    public static final PaletteType SUITABLE_UNIQUE = new PaletteType(false, true);
    public static final PaletteType SEQUENTIAL = new PaletteType(true, false, "SEQUENTIAL");
    public static final PaletteType DIVERGING = new PaletteType(true, false, "DIVERGING");
    public static final PaletteType QUALITATIVE = new PaletteType(false, true, "QUALITATIVE");


	@getter(IKeyword.TRANSPARENCY)
	public double getTransparency(final IAgent agent) {
		return (Double) agent.getAttribute(IKeyword.TRANSPARENCY);
	}

	@setter(IKeyword.TRANSPARENCY)
	public void setTransparency(final IAgent agent, final double s) {
		agent.setAttribute(IKeyword.TRANSPARENCY, s);
	}


	@action(name = "twinkle", 
		args = { @arg(name = "period", type = IType.INT, 
		doc = @doc("make the agent twinkle with a given period")) }, 
		doc = @doc(examples = { @example("do twinkle period: 10;") } ))
	public void twinkle(final IScope scope) throws GamaRuntimeException {
		final IAgent agent = getCurrentAgent(scope);
		double curTrans = getTransparency(agent);
		double  curStep = scope.getSimulationScope().getTime(scope, agent);
		
		Integer period = (Integer) scope.getArg("period", IType.INT);
		
		if(period == 0){
			period = 360;		
		}
		curTrans = Math.abs(Math.cos((curStep*(180/period)*(Math.PI/180))));
		setTransparency(agent, curTrans);
		scope.getGraphics().setOpacity(curTrans);
		return;
	}
	
	@action(name = "brewer_color", 
			args = { @arg(name = "type", type = IType.STRING, doc = @doc("Palette Type (Sequential, Diverging, Qualitative)")),
			@arg(name = "class", type = IType.INT, optional = false, doc = @doc("Number of class")),
			@arg(name = "index", type = IType.INT,optional = false, doc = @doc("index")) }, 
			doc = @doc(examples = { @example(" color <- self.colors(1);") } ))
	public GamaColor getBrewerColors(final IScope scope){
		String type = scope.getStringArg("type");
		int nbClass = scope.getIntArg("class");
		int index = scope.getIntArg("index");
		
		GamaColor c = null;
		
	    ColorBrewer brewer = ColorBrewer.instance();

	    
	    // STEP 2 - look up a predefined palette from color brewer
	    String paletteName = "";
	    
	    if("Sequential".equals(type) || "sequential".equals(type)){
	    	BrewerPalette[] spallettes = brewer.getPalettes(SEQUENTIAL,nbClass);
	    	//java.lang.System.out.println("sequential palette");
		    for(int i=0;i<spallettes.length;i++){  	
		    	//java.lang.System.out.println(spallettes[i].getName());
		    	//java.lang.System.out.println(spallettes[i].getDescription());
		    	c = new GamaColor(spallettes[i].getColors()[index]);
		    }
	    }
	    
        if("Diverging".equals(type) || "diverging".equals(type)){
        	BrewerPalette[] spallettes = brewer.getPalettes(DIVERGING,nbClass);
	    	//java.lang.System.out.println("diverging palette");
		    for(int i=0;i<spallettes.length;i++){  	
		    	//java.lang.System.out.println(spallettes[i].getName());
		    	//java.lang.System.out.println(spallettes[i].getDescription());
		    	c = new GamaColor(spallettes[i].getColors()[index]);
		    	for(int j=0;j<spallettes[i].getColors().length;j++){
		    		//java.lang.System.out.println(spallettes[i].getColors());	
		    	}	
		    }	
	    }
        
        if("Qualitative".equals(type) || "qualitative".equals(type)){
        	BrewerPalette[] spallettes = brewer.getPalettes(QUALITATIVE,nbClass);
	    	//java.lang.System.out.println("qualitative palette");
		    for(int i=0;i<spallettes.length;i++){  	
		    	//java.lang.System.out.println(spallettes[i].getName());
		    	//java.lang.System.out.println(spallettes[i].getDescription());
		    	c = new GamaColor(spallettes[i].getColors()[index]);
		    	for(int j=0;j<spallettes[i].getColors().length;j++){
		    		//java.lang.System.out.println(spallettes[i].getColors());	
		    	}	
		    } 	
	    }
	    return  c;				
	}
	
	@action(name = "brewer_palette", 
			args = { @arg(name = "type", type = IType.STRING, doc = @doc("Palette Type (Sequential, Diverging, Qualitative)")) }, 
			doc = @doc(examples = { @example("list<rgb> colors <- brewer_palette(\"6-class Blues\");") } , comment =  "You can get the type of the palette form this websiten http://colorbrewer2.org/" ))
	public GamaList<Color> getBrewerPaletteColors(final IScope scope){
		String type = scope.getStringArg("type");
		
		GamaList<Color>colors = new GamaList<Color>();
		
	    ColorBrewer brewer = ColorBrewer.instance();

	    String paletteName = type;
	    
	    if(brewer.getPalette(paletteName) != null){
	    	for (int i=0;i<brewer.getPalette(paletteName).getColors().length;i++){
	    		colors.add(brewer.getPalette(paletteName).getColors()[i]);
	    	}
	    }
	    else{
	    	throw GamaRuntimeException.error(type + "does not exist");
	    }
	    return  colors;				
	}
	
}
