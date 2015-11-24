package gama_analyzer;

import java.util.LinkedList;

import com.thoughtworks.xstream.XStreamer;
import com.thoughtworks.xstream.io.xml.StaxDriver;

import weka.core.xml.XStream;
import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.species;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gama.util.GamaListFactory;
import msi.gama.util.GamaMap;
import msi.gama.util.GamaMapFactory;
import msi.gama.util.IList;
import msi.gama.util.matrix.GamaFloatMatrix;
import msi.gama.util.matrix.GamaIntMatrix;
import msi.gama.util.matrix.GamaMatrix;
import msi.gama.util.matrix.GamaObjectMatrix;
import msi.gaml.operators.Cast;
import msi.gaml.types.IType;
import msi.gaml.types.Types;

public class StorableData {
	
	public GamaMap varmap_reverse;
	public GamaMap varmap; // varname-colnb map //liste des variables
	public GamaMap numvarmap; // varname-colnb map for numerical variables //listes des variables numériques
	public GamaMap qualivarmap; // varname-colnb map the rest
	public GamaObjectMatrix metadatahistory; //sim, step, groupid, ruleid, supgroupid, supruleid, poplist, popsize
	public GamaObjectMatrix lastdetailedvarvalues; //one line per agent //option to deactivate it
	public GamaFloatMatrix averagehistory; // one line per step
	public GamaFloatMatrix stdevhistory;// one line per step
	public GamaFloatMatrix minhistory;// one line per step
	public GamaFloatMatrix maxhistory;// one line per step
	public GamaObjectMatrix distribhistoryparams; //one line per step, params for the distribhistory: cl et st: xmin=st * 2^cl le nb de clust max est un param global
	public GamaObjectMatrix distribhistory; //one line per step, one GamaIntMatrix per cell
	 
	public GamaMap getVarmap() { return varmap; }
	public void setVarmap(GamaMap varmap) { this.varmap = varmap; }
	public GamaMap getNumvarmap() { return numvarmap; }
	public void setNumvarmap(GamaMap numvarmap) { this.numvarmap = numvarmap; }
	public GamaMap getQualivarmap() { return qualivarmap; }
	public void setQualivarmap(GamaMap qualivarmap) { this.qualivarmap = qualivarmap; }
	public GamaMatrix getMetadatahistory() { return metadatahistory; }
	public void setMetadatahistory(GamaObjectMatrix metadatahistory) { this.metadatahistory = metadatahistory; }
	public GamaMatrix getLastdetailedvarvalues() { return lastdetailedvarvalues; }
	public void setLastdetailedvarvalues(GamaObjectMatrix lastdetailedvarvalues) { this.lastdetailedvarvalues = lastdetailedvarvalues; }
	public GamaFloatMatrix getAveragehistory() { return averagehistory; }
	public void setAveragehistory(GamaFloatMatrix averagehistory) { this.averagehistory = averagehistory; }
	public GamaFloatMatrix getStdevhistory() { return stdevhistory; }
	public void setStdevhistory(GamaFloatMatrix stdevhistory) { this.stdevhistory = stdevhistory; }
	public GamaFloatMatrix getMinhistory() { return minhistory; }
	public void setMinhistory(GamaFloatMatrix minhistory) { this.minhistory = minhistory; }
	public GamaFloatMatrix getMaxhistory() { return maxhistory; }
	public void setMaxhistory(GamaFloatMatrix maxhistory) { this.maxhistory = maxhistory; }
	public GamaMatrix getDistribhistoryparams() { return distribhistoryparams; }
	public void setDistribhistoryparams(GamaObjectMatrix distribhistoryparams) { this.distribhistoryparams = distribhistoryparams; }
	public GamaMatrix getDitribhistory() { return distribhistory; }
	public void setDitribhistory(GamaObjectMatrix ditribhistory) { this.distribhistory = ditribhistory; }
	
	Boolean isAgentCreated;

	public Boolean getIsAgentCreated() {
		return isAgentCreated;
	}
	public void setIsAgentCreated(Boolean isAgentCreated) {
		this.isAgentCreated = isAgentCreated;
	}
	public void init(IScope scope)
	{
		isAgentCreated = false;	
		varmap = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
		numvarmap = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
		qualivarmap = GamaMapFactory.create(Types.NO_TYPE, Types.NO_TYPE);
		metadatahistory = new GamaObjectMatrix(0,0,msi.gaml.types.Types.NO_TYPE);
		lastdetailedvarvalues = new GamaObjectMatrix(0,0,msi.gaml.types.Types.NO_TYPE);
		averagehistory = new GamaFloatMatrix(0,0);
		stdevhistory = new GamaFloatMatrix(0,0);
		minhistory = new GamaFloatMatrix(0,0);
		maxhistory = new GamaFloatMatrix(0,0);
		distribhistoryparams = new GamaObjectMatrix(0,0,msi.gaml.types.Types.NO_TYPE);
		distribhistory = new GamaObjectMatrix(0,0,msi.gaml.types.Types.NO_TYPE);
		IList premlist=GamaListFactory.create(Types.NO_TYPE);
		premlist.add(0);
		distribhistory.set(scope, 0, 0, premlist);
	}
}
