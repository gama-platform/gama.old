package gaml.additions;
import msi.gama.outputs.layers.*;
import msi.gama.outputs.*;
import msi.gama.kernel.batch.*;
import msi.gaml.architecture.weighted_tasks.*;
import msi.gaml.architecture.user.*;
import msi.gaml.architecture.reflex.*;
import msi.gaml.architecture.finite_state_machine.*;
import msi.gaml.species.*;
import msi.gama.metamodel.shape.*;
import msi.gaml.expressions.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.population.*;
import msi.gama.kernel.simulation.*;
import java.util.*;
import  msi.gama.metamodel.shape.*;
import msi.gama.common.interfaces.*;
import msi.gama.runtime.*;
import java.lang.*;
import msi.gama.metamodel.agent.*;
import msi.gaml.types.*;
import msi.gaml.compilation.*;
import msi.gaml.factories.*;
import msi.gaml.descriptions.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.util.path.*;
import msi.gama.util.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;
import msi.gama.kernel.experiment.*;
import msi.gaml.operators.*;
import msi.gaml.extensions.genstar.*;
import msi.gaml.operators.Random;
import msi.gaml.operators.Maths;
import msi.gaml.operators.Points;
import msi.gaml.operators.Spatial.Properties;
import msi.gaml.operators.System;
import static msi.gaml.operators.Cast.*;
import static msi.gaml.operators.Spatial.*;
import static msi.gama.common.interfaces.IKeyword.*;

public class GamlAdditions extends AbstractGamlAdditions {
	public void initialize() throws SecurityException, NoSuchMethodException {
	initializeTypes();
	initializeSymbols();
	initializeVars();
	initializeOperators();
	initializeFiles();
	initializeActions();
	initializeSkills();
	initializeSpecies();
	initializeDisplays();
	initializePopulationsLinkers();
}
public void initializeTypes() {};
public void initializeSpecies() {};
public void initializeSymbols() {};
public void initializeVars() throws SecurityException, NoSuchMethodException {};
public void initializeOperators() throws SecurityException, NoSuchMethodException  {};
public void initializeFiles() throws SecurityException, NoSuchMethodException  {
_file("obj",ummisco.gama.opengl.files.GamaObjFile.class,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o) {return new ummisco.gama.opengl.files.GamaObjFile(s,((String)o[0]));}},5,-13,13,S("obj"));
_operator(S("is_"+"obj"),null,C(S),I(0),B,true,3,0,0,new GamaHelper(){ @Override public Boolean run(IScope s,Object... o) { return GamaFileType.verifyExtension("obj",(String)o[0]);}});
_operator(S("obj"+"_file"),ummisco.gama.opengl.files.GamaObjFile.class.getConstructor(IScope.class,S),C(S),I(0),GF,false,12,13,-13,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o) {return new ummisco.gama.opengl.files.GamaObjFile(s,((String)o[0]));}});
_file("threeds",ummisco.gama.opengl.files.Gama3DSFile.class,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o) {return new ummisco.gama.opengl.files.Gama3DSFile(s,((String)o[0]));}},5,-13,13,S("3ds","max"));
_operator(S("is_"+"threeds"),null,C(S),I(0),B,true,3,0,0,new GamaHelper(){ @Override public Boolean run(IScope s,Object... o) { return GamaFileType.verifyExtension("threeds",(String)o[0]);}});
_operator(S("threeds"+"_file"),ummisco.gama.opengl.files.Gama3DSFile.class.getConstructor(IScope.class,S),C(S),I(0),GF,false,12,13,-13,new GamaHelper(){ @Override public IGamaFile run(IScope s,Object... o) {return new ummisco.gama.opengl.files.Gama3DSFile(s,((String)o[0]));}});};
public void initializeActions() {};
public void initializeSkills() {};
public void initializeDisplays() {
_display("opengl",ummisco.gama.opengl.SWTOpenGLDisplaySurface.class, new IDisplayCreator(){ @Override public IDisplaySurface create(Object...args){return new ummisco.gama.opengl.SWTOpenGLDisplaySurface(args);}});};
public void initializePopulationsLinkers() {};

}