package gaml.additions;
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
import msi.gama.util.*;
import msi.gama.util.file.*;
import msi.gama.util.matrix.*;
import msi.gama.util.graph.*;
import msi.gama.runtime.exceptions.*;
import msi.gaml.factories.*;
import msi.gaml.statements.*;
import msi.gaml.skills.*;
import msi.gaml.variables.*;

public class GamlAdditions extends AbstractGamlAdditions {
	public GamlAdditions() {}

	@Override public void initialize() {
		addType("container",new GamaContainerType(),(short)16,(int)102,IContainer.class);
		addType("graph",new GamaGraphType(),(short)15,(int)102,IGraph.class);
		addType("agent",new GamaGenericAgentType(),(short)11,(int)104,IAgent.class);
		addType("map",new GamaMapType(),(short)10,(int)102,GamaMap.class,Map.class,HashMap.class);
		addType("list",new GamaListType(),(short)5,(int)102,IList.class,List.class);
		addType("pair",new GamaPairType(),(short)9,(int)104,GamaPair.class);
		addType("path",new GamaPathType(),(short)17,(int)104,IPath.class,GamaPath.class);
		addType("file",new GamaFileType(),(short)12,(int)102,IGamaFile.class,java.io.File.class);
		addType("bool",new GamaBoolType(),(short)3,(int)104,Boolean.class,boolean.class);
		addType("matrix",new GamaMatrixType(),(short)8,(int)102,IMatrix.class,GamaIntMatrix.class,GamaFloatMatrix.class,GamaObjectMatrix.class);
		addType("unknown",new GamaNoType(),(short)0,(int)104,Object.class);
		addType("rgb",new GamaColorType(),(short)6,(int)104,GamaColor.class,java.awt.Color.class);
		addType("species",new GamaSpeciesType(),(short)14,(int)104,ISpecies.class);
		addType("topology",new GamaTopologyType(),(short)18,(int)104,ITopology.class);
		addType("string",new GamaStringType(),(short)4,(int)104,String.class);
		addType("float",new GamaFloatType(),(short)2,(int)101,Double.class,double.class);
		addType("geometry",new GamaGeometryType(),(short)13,(int)104,GamaShape.class,IShape.class);
		addType("point",new GamaPointType(),(short)7,(int)104,ILocation.class,GamaPoint.class);
		addType("int",new GamaIntegerType(),(short)1,(int)101,Integer.class,int.class,Long.class);

	addFactories(new ExperimentFactory(Arrays.asList(13),Arrays.asList(5,4,9,11,3)),new SpeciesFactory(Arrays.asList(0),Arrays.asList(3,11,101,102,104,103)),new ModelFactory(Arrays.asList(1),Arrays.asList(13,0,10,5)),new msi.gama.outputs.layers.OutputLayerFactory(Arrays.asList(6),null),new SymbolFactory(Arrays.asList(10,14),null),new StatementFactory(Arrays.asList(11,2,3,11),null),new BatchFactory(Arrays.asList(9),null),new VariableFactory(Arrays.asList(102,101,104,103,4),null),new OutputFactory(Arrays.asList(5),Arrays.asList(6)));
		addSymbol(ArgStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ArgStatement(description);}},"arg");
		addSymbol(AskStatement.class,11,true,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new AskStatement(description);}},"ask");
		addSymbol(FsmEnterStatement.class,11,false,false,false,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new FsmEnterStatement(description);}},"enter");
		addSymbol(SignalVariable.class,103,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new SignalVariable(description);}},"signal");
		addSymbol(PertinenceChooseStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new PertinenceChooseStatement(description);}},"choose");
		addSymbol(msi.gama.outputs.MonitorOutput.class,5,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.MonitorOutput(description);}},"monitor");
		addSymbol(msi.gama.outputs.layers.SpeciesLayerStatement.class,6,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.SpeciesLayerStatement(description);}},"population");
		addSymbol(DrawStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new DrawStatement(description);}},"draw");
		addSymbol(UserInitPanelStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new UserInitPanelStatement(description);}},"user_init");
		addSymbol(DoStatement.class,2,false,true,false,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new DoStatement(description);}},"do","repeat");
		addSymbol(UserPanelStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new UserPanelStatement(description);}},"user_panel");
		addSymbol(ReleaseStatement.class,11,true,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ReleaseStatement(description);}},"release");
		addSymbol(msi.gama.networks.ui.GraphstreamOutput.class,5,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.networks.ui.GraphstreamOutput(description);}},"graphdisplay");
		addSymbol(MatchStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new MatchStatement(description);}},"match","match_between","match_one","default");
		addSymbol(msi.gama.kernel.experiment.BatchExperiment.class,13,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.experiment.BatchExperiment(description);}},"batch");
		addSymbol(ContainerVariable.class,102,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ContainerVariable(description);}});
		addSymbol(CreateStatement.class,11,true,true,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new CreateStatement(description);}},"create");
		addSymbol(ActionStatement.class,11,false,true,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ActionStatement(description);}},"action");
		addSymbol(FsmStateStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new FsmStateStatement(description);}},"state");
		addSymbol(RemoveStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new RemoveStatement(description);}},"remove");
		addSymbol(PrimitiveStatement.class,3,false,true,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new PrimitiveStatement(description);}},"primitive");
		addSymbol(msi.gama.outputs.layers.TextLayerStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.TextLayerStatement(description);}},"text");
		addSymbol(FsmTransitionStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new FsmTransitionStatement(description);}},"transition");
		addSymbol(msi.gama.kernel.batch.ExhaustiveSearch.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.ExhaustiveSearch(description);}},"exhaustive");
		addSymbol(PertinenceChainStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new PertinenceChainStatement(description);}},"chain");
		addSymbol(msi.gama.outputs.layers.ImageLayerStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.ImageLayerStatement(description);}},"image");
		addSymbol(msi.gama.outputs.layers.GridLayerStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.GridLayerStatement(description);}},"grid");
		addSymbol(msi.gama.kernel.batch.HillClimbing.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.HillClimbing(description);}},"hill_climbing");
		addSymbol(ReturnStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ReturnStatement(description);}},"return");
		addSymbol(UserCommandStatement.class,11,false,true,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new UserCommandStatement(description);}},"user_command");
		addSymbol(MigrateStatement.class,11,true,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new MigrateStatement(description);}},"migrate");
		addSymbol(msi.gama.opengl.GLOutput.class,5,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.opengl.GLOutput(description);}},"graphdisplaygl");
		addSymbol(AspectStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new AspectStatement(description);}},"aspect");
		addSymbol(ElseStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ElseStatement(description);}},"else");
		addSymbol(msi.gama.kernel.batch.SimulatedAnnealing.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.SimulatedAnnealing(description);}},"annealing");
		addSymbol(IfStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new IfStatement(description);}},"if");
		addSymbol(msi.gama.kernel.batch.TabuSearchReactive.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.TabuSearchReactive(description);}},"reactive_tabu");
		addSymbol(SaveStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new SaveStatement(description);}},"save");
		addSymbol(msi.gama.outputs.FileOutput.class,5,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.FileOutput(description);}},"file");
		addSymbol(msi.gama.kernel.experiment.GuiExperiment.class,13,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.experiment.GuiExperiment(description);}},"gui");
		addSymbol(WriteStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new WriteStatement(description);}},"write");
		addSymbol(msi.gama.kernel.batch.TabuSearch.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.TabuSearch(description);}},"tabu");
		addSymbol(GamlSpecies.class,0,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new GamlSpecies(description);}},"species","global","grid");
		addSymbol(SwitchStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new SwitchStatement(description);}},"switch");
		addSymbol(msi.gama.outputs.layers.ChartLayerStatement.class,6,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.ChartLayerStatement(description);}},"chart");
		addSymbol(msi.gama.outputs.OutputManager.class,5,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.OutputManager(description);}},"output");
		addSymbol(msi.gama.outputs.layers.ChartDataStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.ChartDataStatement(description);}},"data");
		addSymbol(msi.gama.outputs.layers.QuadTreeLayerStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.QuadTreeLayerStatement(description);}},"quadtree");
		addSymbol(NumberVariable.class,101,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new NumberVariable(description);}});
		addSymbol(PutStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new PutStatement(description);}},"put");
		addSymbol(msi.gama.kernel.batch.BatchOutput.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.BatchOutput(description);}},"save_batch");
		addSymbol(UserInputStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new UserInputStatement(description);}},"user_input");
		addSymbol(LoopStatement.class,11,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new LoopStatement(description);}},"loop");
		addSymbol(Variable.class,104,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new Variable(description);}});
		addSymbol(FsmExitStatement.class,11,false,false,false,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new FsmExitStatement(description);}},"exit");
		addSymbol(CaptureStatement.class,11,true,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new CaptureStatement(description);}},"capture");
		addSymbol(msi.gama.kernel.experiment.ExperimentParameter.class,4,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.experiment.ExperimentParameter(description);}},"parameter");
		addSymbol(msi.gama.outputs.LayeredDisplayOutput.class,5,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.LayeredDisplayOutput(description);}},"display");
		addSymbol(msi.gama.outputs.layers.AgentLayerStatement.class,6,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.layers.AgentLayerStatement(description);}},"agents");
		addSymbol(BreakStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new BreakStatement(description);}},"break");
		addSymbol(msi.gama.outputs.InspectDisplayOutput.class,5,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.outputs.InspectDisplayOutput(description);}},"inspect");
		addSymbol(ModelEnvironment.class,10,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ModelEnvironment(description);}},"environment");
		addSymbol(ReflexStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ReflexStatement(description);}},"reflex","init");
		addSymbol(msi.gama.kernel.model.GamlModel.class,1,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.model.GamlModel(description);}},"model");
		addSymbol(LetStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new LetStatement(description);}},"let");
		addSymbol(AddStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new AddStatement(description);}},"add");
		addSymbol(EntitiesPlaceHolder.class,14,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new EntitiesPlaceHolder(description);}},"entities");
		addSymbol(WithStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new WithStatement(description);}},"with");
		addSymbol(msi.gama.kernel.batch.GeneticAlgorithm.class,9,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new msi.gama.kernel.batch.GeneticAlgorithm(description);}},"genetic");
		addSymbol(WeightedTaskStatement.class,3,false,false,true,true,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new WeightedTaskStatement(description);}},"task");
		addSymbol(WarnStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new WarnStatement(description);}},"warn");
		addSymbol(SetStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new SetStatement(description);}},"set");
		addSymbol(ErrorStatement.class,2,false,false,true,false,new ISymbolConstructor() {@Override public ISymbol create(final IDescription description) {	return new ErrorStatement(description);}},"error");
		addVarDescription(GridSkill.class,DescriptionFactory.create("int",new String[] {"const","true","getter","getY","name","grid_y","setter","setY","type","int"}));
		addVarDescription(GridSkill.class,DescriptionFactory.create("rgb",new String[] {"const","false","getter","getColor","name","color","setter","setColor","type","rgb"}));
		addVarDescription(GridSkill.class,DescriptionFactory.create("list",new String[] {"of","agent","const","false","getter","getAgents","name","agents","setter","setAgents","type","list"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("list",new String[] {"of","agent","const","false","getter","getAgents","name","agents","setter","setAgents","type","list"}));
		addVarDescription(GridSkill.class,DescriptionFactory.create("int",new String[] {"const","true","getter","getX","name","grid_x","setter","setX","type","int"}));
		addVarDescription(ITopology.class,DescriptionFactory.create("geometry",new String[] {"const","false","getter","getEnvironment","name","environment","type","geometry"}));
		addVarDescription(ITopology.class,DescriptionFactory.create("container",new String[] {"of","geometry","const","false","getter","getPlaces","name","places","type","container"}));
		addVarDescription(IMatrix.class,DescriptionFactory.create("point",new String[] {"const","false","getter","getDimensions","name","dimension","type","point"}));
		addVarDescription(IMatrix.class,DescriptionFactory.create("int",new String[] {"const","false","getter","getCols","name","columns","type","int"}));
		addVarDescription(IMatrix.class,DescriptionFactory.create("int",new String[] {"const","false","getter","getRows","name","rows","type","int"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("unknown",new String[] {"const","false","getter","getHost","name","host","setter","setHost","type","unknown"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("point",new String[] {"initer","getLocation","const","false","getter","getLocation","name","location","setter","setLocation","type","point","depends_on","shape"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getName","name","name","setter","setName","type","string"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getPeers","name","peers","setter","setPeers","type","list"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("geometry",new String[] {"const","false","getter","getGeometry","name","shape","setter","setGeometry","type","geometry"}));
		addVarDescription(IGamlAgent.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getMembers","name","members","setter","setMembers","type","list"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("bool",new String[] {"initer","isFolder","const","false","getter","isFolder","name","is_folder","type","bool"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("string",new String[] {"initer","getExtension","const","false","getter","getExtension","name","extension","type","string"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("bool",new String[] {"initer","isReadable","const","false","getter","isReadable","name","readable","type","bool"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("container",new String[] {"const","false","getter","getContents","name","contents","type","container"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("bool",new String[] {"initer","isWritable","const","false","getter","isWritable","name","writable","type","bool"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("string",new String[] {"initer","getName","const","false","getter","getName","name","name","type","string"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("bool",new String[] {"initer","exists","const","false","getter","exists","name","exists","type","bool"}));
		addVarDescription(IGamaFile.class,DescriptionFactory.create("string",new String[] {"initer","getPath","const","false","getter","getPath","name","path","type","string"}));
		addVarDescription(IPath.class,DescriptionFactory.create("graph",new String[] {"const","false","getter","getGraph","name","graph","type","graph"}));
		addVarDescription(IPath.class,DescriptionFactory.create("list",new String[] {"of","geometry","const","false","getter","getEdgeList","name","segments","type","list"}));
		addVarDescription(IPath.class,DescriptionFactory.create("point",new String[] {"const","false","getter","getStartVertex","name","source","type","point"}));
		addVarDescription(IPath.class,DescriptionFactory.create("list",new String[] {"of","agent","const","false","getter","getAgentList","name","agents","type","list"}));
		addVarDescription(IPath.class,DescriptionFactory.create("point",new String[] {"const","false","getter","getEndVertex","name","target","type","point"}));
		addVarDescription(UserControlArchitecture.class,DescriptionFactory.create("bool",new String[] {"const","false","getter","isUserControlled","name","user_controlled","setter","setUserControlled","init","true","type","bool"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getDuration","name","duration","type","string"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getTime","name","time","setter","setTime","type","float"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getTotalDuration","name","total_duration","type","string"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("float",new String[] {"initer","getSeed","const","false","getter","getSeed","name","seed","setter","setSeed","type","float"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("bool",new String[] {"initer","getFatalErrors","const","false","getter","getFatalErrors","name","fatal","setter","setFatalErrors","init","false","type","bool"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("string",new String[] {"initer","getRng","const","false","getter","getRng","name","rng","setter","setRng","init","'mersenne'","type","string"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getAverageDuration","name","average_duration","type","string"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("bool",new String[] {"initer","getWarningsAsErrors","const","false","getter","getWarningsAsErrors","name","warnings","setter","setWarningsAsErrors","init","false","type","bool"}));
		addVarDescription(WorldSkill.class,DescriptionFactory.create("float",new String[] {"const","true","getter","getTimeStep","name","step","setter","setTimeStep","type","float"}));
		addVarDescription(GamaMap.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getValues","name","values","type","list"}));
		addVarDescription(GamaMap.class,DescriptionFactory.create("list",new String[] {"of","pair","const","false","getter","getPairs","name","pairs","type","list"}));
		addVarDescription(GamaMap.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getKeys","name","keys","type","list"}));
		addVarDescription(FsmArchitecture.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getStateName","name","state","setter","setStateName","type","string"}));
		addVarDescription(FsmArchitecture.class,DescriptionFactory.create("list",new String[] {"initer","getStateNames","const","true","getter","getStateNames","name","states","setter","setStateNames","type","list"}));
		addVarDescription(IShape.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getPerimeter","name","perimeter","type","float"}));
		addVarDescription(GamlSpecies.class,DescriptionFactory.create("string",new String[] {"const","false","getter","getName","name","name","type","string"}));
		addVarDescription(ILocation.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getY","name","y","type","float"}));
		addVarDescription(ILocation.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getX","name","x","type","float"}));
		addVarDescription(GamaColor.class,DescriptionFactory.create("int",new String[] {"const","false","getter","red","name","red","type","int"}));
		addVarDescription(GamaColor.class,DescriptionFactory.create("int",new String[] {"const","false","getter","blue","name","blue","type","int"}));
		addVarDescription(GamaColor.class,DescriptionFactory.create("int",new String[] {"const","false","getter","green","name","green","type","int"}));
		addVarDescription(GamaColor.class,DescriptionFactory.create("rgb",new String[] {"const","false","getter","getDarker","name","darker","type","rgb"}));
		addVarDescription(GamaColor.class,DescriptionFactory.create("rgb",new String[] {"const","false","getter","getBrighter","name","brighter","type","rgb"}));
		addVarDescription(MovingSkill.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getSpeed","name","speed","setter","setSpeed","init","1.0","type","float"}));
		addVarDescription(MovingSkill.class,DescriptionFactory.create("int",new String[] {"const","false","getter","getHeading","name","heading","setter","setHeading","init","rnd 359","type","int"}));
		addVarDescription(MovingSkill.class,DescriptionFactory.create("point",new String[] {"const","false","getter","getDestination","name","destination","setter","setDestination","type","point","depends_on","speed heading location"}));
		addVarDescription(ILocated.class,DescriptionFactory.create("point",new String[] {"const","false","getter","getLocation","name","location","type","point"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("geometry",new String[] {"const","false","getter","getExteriorRing","name","contour","type","geometry"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getHeight","name","height","type","float"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getArea","name","area","type","float"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("list",new String[] {"of","geometry","const","false","getter","getGeometries","name","geometries","type","list"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("geometry",new String[] {"const","false","getter","getGeometricEnvelope","name","envelope","type","geometry"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("float",new String[] {"const","false","getter","getWidth","name","width","type","float"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("list",new String[] {"of","point","const","false","getter","getPoints","name","points","type","list"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("bool",new String[] {"const","false","getter","isMultiple","name","multiple","type","bool"}));
		addVarDescription(GamaShape.class,DescriptionFactory.create("list",new String[] {"of","geometry","const","false","getter","getHoles","name","holes","type","list"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getEdges","name","edges","type","list"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getSpanningTree","name","spanning_tree","type","list"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("bool",new String[] {"const","false","getter","isVerbose","name","verbose","type","bool"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("list",new String[] {"const","false","getter","getVertices","name","vertices","type","list"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("bool",new String[] {"const","false","getter","getConnected","name","connected","type","bool"}));
		addVarDescription(IGraph.class,DescriptionFactory.create("path",new String[] {"const","false","getter","getCircuit","name","circuit","type","path"}));
		addVarDescription(GamaPair.class,DescriptionFactory.create("unknown",new String[] {"const","false","getter","last","name","value","type","unknown"}));
		addVarDescription(GamaPair.class,DescriptionFactory.create("unknown",new String[] {"const","false","getter","first","name","key","type","unknown"}));
		addUnary("image",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.imageFile(scope,((String)target));}});
		addBinary("+",IMatrix.class,IMatrix.class,IMatrix.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (IMatrix)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IMatrix.class), null) ; 
return ((IMatrix) left).plus(((IMatrix)right));}});
		addUnary("to_java",Object.class,String.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.toJava(((Object)target));}});
		addUnary("round",Double.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.round((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("neighbours_of",IGraph.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.neighboursOf(((IGraph)left),((Object)right));}});
		addBinary("contains_all",String.class,List.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opContainsAll(((String)left),((List)right));}});
		addBinary("-",IShape.class,Double.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opNegativeBuffer(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("reduced_by",IShape.class,Double.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opNegativeBuffer(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("*",Double.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opTimes((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("neighbours_at",IShape.class,Double.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opNeighboursAt(scope,((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("mod",Integer.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Maths.opMod(scope,(left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("/",GamaColor.class,Integer.class,GamaColor.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.divide(((GamaColor)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("min",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).min(scope);}});
		addBinary("union",IShape.class,IShape.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opUnion(((IShape)left),((IShape)right));}});
		addBinary("+",IShape.class,IShape.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opUnion(((IShape)left),((IShape)right));}});
		addUnary("abs",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.abs((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("target_of",IGraph.class,Object.class,Object.class,false,(short)98,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.targetOf(((IGraph)left),((Object)right));}});
		addUnary("last",ISpecies.class,IAgent.class,false,(short)-16,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Containers.getLast(scope,((ISpecies)target));}});
		addUnary("empty",IContainer.class,boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { if (target == null) return  false ; 
return ((IContainer) target).isEmpty();}});
		addBinary("+",GamaColor.class,Integer.class,GamaColor.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.add(((GamaColor)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("simple_clustering_by_envelope_distance",IList.class,Double.class,IList.class,false,(short)98,false,(short)-13,(short)5,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Statistics.simpleClusteringByEnvelopeDistance(scope,((IList)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("copy_between",String.class,GamaPoint.class,String.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opCopy(((String)left),((GamaPoint)right));}});
		addBinary("simple_clustering_by_distance",IList.class,Double.class,IList.class,false,(short)98,false,(short)-13,(short)5,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Statistics.simpleClusteringByDistance(scope,((IList)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary(">",GamaPoint.class,GamaPoint.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater(((GamaPoint)left),((GamaPoint)right));}});
		addBinary("towards",IShape.class,IShape.class,Integer.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opTowards(scope,((IShape)left),((IShape)right));}});
		addBinary("direction_to",IShape.class,IShape.class,Integer.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opTowards(scope,((IShape)left),((IShape)right));}});
		addBinary("!=",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<>",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("at_location",IShape.class,ILocation.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primTranslationTo(((IShape)left),((ILocation)right));}});
		addBinary("translated_to",IShape.class,ILocation.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primTranslationTo(((IShape)left),((ILocation)right));}});
		addUnary("pair",Object.class,GamaPair.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaPair execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asPair(scope,((Object)target));}});
		addBinary("distance_to",GamaPoint.class,GamaPoint.class,Double.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opDistanceTo(scope,((GamaPoint)left),((GamaPoint)right));}});
		addBinary("with_optimizer_type",IGraph.class,String.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.setOptimizeType(scope,((IGraph)left),((String)right));}});
		addUnary("sqrt",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.sqrt((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("generate_barabasi_albert",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.generateGraphstreamBarabasiAlbert(scope,((GamaMap)target));}});
		addUnary("user_input",Map.class,Map.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Map execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.userInput(scope,((Map)target));}});
		addBinary("-",IList.class,IList.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opMinus(((IList)left),((IList)right));}});
		addBinary("and",Boolean.class,IExpression.class,Boolean.class,false,(short)1,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Logic.and(scope,(left == null) ? false : ((Boolean)left),((IExpression)right));}});
		addBinary("or",Boolean.class,IExpression.class,Boolean.class,false,(short)1,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Logic.or(scope,(left == null) ? false : ((Boolean)left),((IExpression)right));}});
		addBinary("contains_vertex",GamaGraph.class,Object.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.containsVertex(((GamaGraph)left),((Object)right));}});
		addBinary("-",Integer.class,Double.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opMinus((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("copy_between",IList.class,GamaPoint.class,IList.class,false,(short)98,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opCopy(((IList)left),((GamaPoint)right));}});
		addBinary("*",GamaPoint.class,Double.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.multiply(((GamaPoint)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("link",GamaPair.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opLink(scope,((GamaPair)target));}});
		addBinary("successors_of",IGraph.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.successorsOf(((IGraph)left),((Object)right));}});
		addBinary("with_max_of",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.withMaxOf(scope,((IContainer)left),((IExpression)right));}});
		addBinary("-",IMatrix.class,IMatrix.class,IMatrix.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (IMatrix)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IMatrix.class), null) ; 
return ((IMatrix) left).minus(((IMatrix)right));}});
		addUnary("graph",Object.class,IGraph.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asGraph(scope,((Object)target));}});
		addBinary("::",Object.class,Object.class,GamaPair.class,false,(short)0,true,(short)9,(short)-17,
			new IOperatorExecuter() {
			@Override
			public GamaPair execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opPair(((Object)left),((Object)right));}});
		addBinary("@",IContainer.class,Object.class,Object.class,false,(short)98,true,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) left).get(((Object)right));}});
		addBinary("at",IContainer.class,Object.class,Object.class,false,(short)98,true,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) left).get(((Object)right));}});
		addBinary("inside",ISpecies.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opInside(scope,((ISpecies)left),((Object)right));}});
		addBinary("partially_overlaps",IShape.class,IShape.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Properties.opPartiallyOverlaps(((IShape)left),((IShape)right));}});
		addUnary("binomial",GamaPoint.class,Integer.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opBinomial(scope,((GamaPoint)target));}});
		addUnary("load_graph_from_gexf",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromGEFX(scope,((GamaMap)target));}});
		addUnary("length",IContainer.class,int.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { if (target == null) return  0 ; 
return ((IContainer) target).length();}});
		addUnary("one_of",ISpecies.class,IAgent.class,false,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opAny(scope,((ISpecies)target));}});
		addUnary("any",ISpecies.class,IAgent.class,false,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opAny(scope,((ISpecies)target));}});
		addBinary("collect",IContainer.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-17,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.collect(scope,((IContainer)left),((IExpression)right));}});
		addBinary("+",Double.class,Integer.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opPlus((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("polygon",IList.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opPolygon(((IList)target));}});
		addUnary("container",Object.class,IContainer.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IContainer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asContainer(scope,((Object)target));}});
		addBinary(">",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("as_grid",IShape.class,GamaPoint.class,IMatrix.class,false,(short)98,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Transformations.opAsGrid(scope,((IShape)left),((GamaPoint)right));}});
		addBinary("set_verbose",IGraph.class,Boolean.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.setVerbose(scope,((IGraph)left),(right == null) ? false : ((Boolean)right));}});
		addUnary("square",Double.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opSquare(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary(">=",String.class,String.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual(((String)left),((String)right));}});
		addBinary("<->",Double.class,Double.class,ILocation.class,false,(short)0,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.toPoint((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("neighbours_at",GamaPoint.class,Double.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opNeighboursAt(scope,((GamaPoint)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("-",GamaPoint.class,Integer.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.substract(((GamaPoint)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("shuffle",IList.class,IList.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opShuffle(scope,((IList)target));}});
		addUnary("median",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opMedian(scope,((GamaList)target));}});
		addUnary("variance",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opVariance(scope,((GamaList)target));}});
		addBinary("simplification",IShape.class,Double.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opSimplication(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("last",String.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.last(((String)target));}});
		addBinary("-",IShape.class,IList.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opDifferenceAgents(((IShape)left),((IList)right));}});
		addUnary("mul",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).product(scope);}});
		addUnary("product",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).product(scope);}});
		addUnary("reverse",String.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.reverse(((String)target));}});
		addUnary("even",Integer.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.even((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("read",IGamaFile.class,Object.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.opRead(scope,((IGamaFile)target));}});
		addUnary("one_of",IContainer.class,Object.class,false,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).any();}});
		addUnary("any",IContainer.class,Object.class,false,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).any();}});
		addBinary("/",GamaPoint.class,Double.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.divide(((GamaPoint)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("+",GamaPoint.class,Double.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.add(((GamaPoint)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("round",Integer.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.round((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("enlarged_by",IShape.class,GamaMap.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),((GamaMap)right));}});
		addBinary("+",IShape.class,GamaMap.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),((GamaMap)right));}});
		addBinary("buffer",IShape.class,GamaMap.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),((GamaMap)right));}});
		addBinary("among",Integer.class,IContainer.class,IList.class,false,(short)98,false,(short)-13,(short)-19,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.opAmong(scope,(left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),((IContainer)right));}});
		addBinary("+",Integer.class,Double.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opPlus((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("as_matrix",IContainer.class,ILocation.class,IMatrix.class,false,(short)98,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
public IMatrix execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (IMatrix)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IMatrix.class), null) ; 
return ((IContainer) left).matrixValue(scope,((ILocation)right));}});
		addBinary("in",String.class,String.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opIn(((String)left),((String)right));}});
		addUnary("properties",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.propertyFile(scope,((String)target));}});
		addBinary("last_index_of",GamaMap.class,Object.class,Object.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opLastIndexOf(((GamaMap)left),((Object)right));}});
		addBinary("<=",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.lessOrEqual((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("is_properties",String.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return GamaFileType.isProperties(((String)target));}});
		addBinary("as_matrix",Object.class,ILocation.class,IMatrix.class,false,(short)98,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Cast.asMatrix(scope,((Object)left),((ILocation)right));}});
		addBinary("source_of",IGraph.class,Object.class,Object.class,false,(short)98,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.sourceOf(((IGraph)left),((Object)right));}});
		addBinary("row_at",IMatrix.class,Integer.class,IList.class,false,(short)98,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
return ((IMatrix) left).getRow((right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("copy",Object.class,Object.class,false,(short)-15,(short)-16,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.opCopy(scope,((Object)target));}});
		addBinary("split_at",IShape.class,ILocation.class,GamaList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.splitLineAt(((IShape)left),((ILocation)right));}});
		addUnary("as_time",double.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.asTime((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("generate_watts_strogatz",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.generateGraphstreamWattsStrogatz(scope,((GamaMap)target));}});
		addBinary("sort_by",IContainer.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.sort(scope,((IContainer)left),((IExpression)right));}});
		addBinary("sort",IContainer.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.sort(scope,((IContainer)left),((IExpression)right));}});
		addBinary("intersects",IShape.class,GamaPoint.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Properties.opIntersects(((IShape)left),((GamaPoint)right));}});
		addUnary("mean_deviation",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opMeanDeviation(scope,((GamaList)target));}});
		addBinary("<->",IShape.class,IShape.class,Boolean.class,false,(short)2,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Properties.opDisjoint(scope,((IShape)left),((IShape)right));}});
		addBinary("disjoint_from",IShape.class,IShape.class,Boolean.class,false,(short)2,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Properties.opDisjoint(scope,((IShape)left),((IShape)right));}});
		addUnary("cone",GamaPoint.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opCone(scope,((GamaPoint)target));}});
		addBinary("with_precision",Double.class,Integer.class,Double.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opTruncate((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("atan",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.atan((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("atan",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.atan((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("tanh",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.tanh((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("<",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("-",GamaColor.class,Integer.class,GamaColor.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.substract(((GamaColor)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("/",GamaPoint.class,Integer.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.divide(((GamaPoint)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("<",String.class,String.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less(((String)left),((String)right));}});
		addUnary("standard_deviation",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opStDev(scope,((GamaList)target));}});
		addBinary("last_with",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.last_with(scope,((IContainer)left),((IExpression)right));}});
		addBinary("as_map",IContainer.class,IExpression.class,GamaMap.class,true,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.asMap(scope,((IContainer)left),((IExpression)right));}});
		addBinary("tokenize",String.class,String.class,IList.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opTokenize(((String)left),((String)right));}});
		addBinary("split_with",String.class,String.class,IList.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opTokenize(((String)left),((String)right));}});
		addBinary("contains_edge",IGraph.class,GamaPair.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.containsEdge(((IGraph)left),((GamaPair)right));}});
		addUnary("rectangle",GamaPoint.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opRect(scope,((GamaPoint)target));}});
		addUnary("-",Integer.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.negate((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("first_with",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.first_with(scope,((IContainer)left),((IExpression)right));}});
		addUnary("is_shape",String.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return GamaFileType.isShape(((String)target));}});
		addBinary("-",Double.class,Integer.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opMinus((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("remove_node_from",IShape.class,IGraph.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.removeEdgeFrom(((IShape)left),((IGraph)right));}});
		addBinary("index_of",IList.class,Object.class,Integer.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opIndexOf(((IList)left),((Object)right));}});
		addBinary("neighbours_of",ITopology.class,GamaPair.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opNeighboursOf(scope,((ITopology)left),((GamaPair)right));}});
		addBinary("!=",Integer.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<>",Integer.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("sin",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.sin((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("accumulate",IList.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-19,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.accumulate(scope,((IList)left),((IExpression)right));}});
		addBinary("path_between",ITopology.class,IContainer.class,IPath.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IPath execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.pathBetween(scope,((ITopology)left),((IContainer)right));}});
		addUnary("directed",IGraph.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Graphs.asDirectedGraph(((IGraph)target));}});
		addUnary("shapefile",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.shapeFile(scope,((String)target));}});
		addUnary("truncated_gauss",IList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opTGauss(scope,((IList)target));}});
		addUnary("TGauss",IList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opTGauss(scope,((IList)target));}});
		addUnary("load_graph_from_graphml",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromGraphML(scope,((GamaMap)target));}});
		addUnary("list",Object.class,IList.class,true,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asList(scope,((Object)target));}});
		addUnary("load_graph_from_dgs_old",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromDGSOld(scope,((GamaMap)target));}});
		addUnary("to_gaml",Object.class,String.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.toGaml(((Object)target));}});
		addUnary("as_date",double.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.asDate((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("+",Double.class,Double.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opPlus((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("+",GamaColor.class,GamaColor.class,GamaColor.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.add(((GamaColor)left),((GamaColor)right));}});
		addUnary("folder",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.folderFile(scope,((String)target));}});
		addBinary(">",Integer.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("=",Object.class,Object.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.equal(((Object)left),((Object)right));}});
		addBinary("div",Integer.class,Double.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.div((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("acos",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.acos((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("times",IMatrix.class,IMatrix.class,IMatrix.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (IMatrix)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IMatrix.class), null) ; 
return ((IMatrix) left).times(((IMatrix)right));}});
		addBinary(">=",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("string",Object.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asString(scope,((Object)target));}});
		addBinary("grid_at",ISpecies.class,GamaPoint.class,IAgent.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.getGridAgent(scope,((ISpecies)left),((GamaPoint)right));}});
		addUnary("unknown",Object.class,Object.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asObject(((Object)target));}});
		addBinary("rewire_p",IGraph.class,Double.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.rewireGraph(((IGraph)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("**",Integer.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("^",Integer.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary(">",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("agent_closest_to",Object.class,IAgent.class,false,(short)0,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Queries.opAgentsClosestTo(scope,((Object)target));}});
		addUnary("as_edge_graph",IContainer.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Graphs.spatialFromEdges(scope,((IContainer)target));}});
		addUnary("dead",IAgent.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.opDead(scope,((IAgent)target));}});
		addBinary("masked_by",IShape.class,ISpecies.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Operators.opMaskedBy(scope,((IShape)left),((ISpecies)right));}});
		addUnary("not",Boolean.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Logic.not((target == null) ? false : ((Boolean)target));}});
		addUnary("!",Boolean.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Logic.not((target == null) ? false : ((Boolean)target));}});
		addBinary("index_of",IMatrix.class,Object.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opIndexOf(((IMatrix)left),((Object)right));}});
		addUnary("new_folder",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.newFolder(scope,((String)target));}});
		addBinary(":",Object.class,Object.class,Object.class,false,(short)0,false,(short)-21,(short)-21,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Logic.then(scope,((Object)left),((Object)right));}});
		addUnary("union",ISpecies.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Operators.opUnion(scope,((ISpecies)target));}});
		addBinary("*",GamaColor.class,Integer.class,GamaColor.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.multiply(((GamaColor)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("sum",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).sum(scope);}});
		addBinary("-",GamaColor.class,GamaColor.class,GamaColor.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.substract(((GamaColor)left),((GamaColor)right));}});
		addUnary("columns_list",IMatrix.class,IList.class,true,(short)-13,(short)5,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
return ((IMatrix) target).getColumnsList();}});
		addBinary("/",Double.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opDivide((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("load_graph_from_lgl",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromLGL(scope,((GamaMap)target));}});
		addBinary("get",IShape.class,String.class,Object.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Files.opRead(scope,((IShape)left),((String)right));}});
		addBinary("crosses",IShape.class,IShape.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Properties.opCrosses(((IShape)left),((IShape)right));}});
		addUnary("text",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.textFile(scope,((String)target));}});
		addUnary("cos",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.cos((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("is_text",String.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return GamaFileType.isTextFile(((String)target));}});
		addUnary("shuffle",IMatrix.class,IMatrix.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opShuffle(scope,((IMatrix)target));}});
		addUnary("asin",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.asin((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("remove_duplicates",IContainer.class,IList.class,true,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Containers.asSet(((IContainer)target));}});
		addUnary("without_holes",IShape.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.opWithoutHoles(((IShape)target));}});
		addUnary("solid",IShape.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.opWithoutHoles(((IShape)target));}});
		addBinary("overlapping",IContainer.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opOverlapping(scope,((IContainer)left),((Object)right));}});
		addUnary("exp",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.exp((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("species_of",Object.class,ISpecies.class,false,(short)-13,(short)-15,
			new IOperatorExecuter() {
			@Override
			public ISpecies execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asSpecies(scope,((Object)target));}});
		addUnary("species",Object.class,ISpecies.class,false,(short)-13,(short)-15,
			new IOperatorExecuter() {
			@Override
			public ISpecies execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asSpecies(scope,((Object)target));}});
		addUnary("eval_java",String.class,Object.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.opEvalJava(scope,((String)target));}});
		addBinary("!=",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("<>",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("+",String.class,String.class,String.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opPlus(((String)left),((String)right));}});
		addUnary("triangle",Double.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opTriangle(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("TGauss",GamaPoint.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opTGauss(scope,((GamaPoint)target));}});
		addUnary("truncated_gauss",GamaPoint.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opTGauss(scope,((GamaPoint)target));}});
		addBinary("@",ISpecies.class,GamaPoint.class,IAgent.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.getAgent(scope,((ISpecies)left),((GamaPoint)right));}});
		addBinary("at",ISpecies.class,GamaPoint.class,IAgent.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.getAgent(scope,((ISpecies)left),((GamaPoint)right));}});
		addBinary("index_of",String.class,String.class,Integer.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opIndexOf(((String)left),((String)right));}});
		addBinary("<=",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.lessOrEqual((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("every",Integer.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.opEvery(scope,(target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("norm",GamaPoint.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Points.norm(((GamaPoint)target));}});
		addUnary("rnd",Integer.class,Integer.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opRnd(scope,(target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary(">=",Integer.class,Double.class,Boolean.class,false,(short)2,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("frequency_of",IContainer.class,IExpression.class,GamaMap.class,true,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Stats.frequencyOf(scope,((IContainer)left),((IExpression)right));}});
		addBinary("**",Double.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("^",Double.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("shuffle",String.class,String.class,false,(short)-13,(short)4,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opShuffle(scope,((String)target));}});
		addUnary("ceil",double.class,double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.ceil((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("column_at",IMatrix.class,Integer.class,List.class,false,(short)98,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public List execute(final IScope scope,final Object left, final Object right) { if (left == null) return  (List)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(List.class), null) ; 
return ((IMatrix) left).getColumn((right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("-",Integer.class,Integer.class,Integer.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opMinus((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("skeletonize",IShape.class,GamaList.class,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public GamaList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.primSkeletonization(scope,((IShape)target));}});
		addBinary("of_generic_species",IList.class,ISpecies.class,IList.class,false,(short)3,false,(short)-13,(short)-19,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opOfGenericSpecies(((IList)left),((ISpecies)right));}});
		addBinary("inter",IContainer.class,IContainer.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.opInter(scope,((IContainer)left),((IContainer)right));}});
		addUnary("floor",double.class,double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.floor((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("*",Integer.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opTimes((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary(">",String.class,String.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater(((String)left),((String)right));}});
		addBinary("/",Integer.class,Integer.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opDivide((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("group_by",IContainer.class,IExpression.class,GamaMap.class,false,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.groupBy(scope,((IContainer)left),((IExpression)right));}});
		addBinary("<->",Integer.class,Integer.class,ILocation.class,false,(short)0,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.toPoint((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("@",String.class,int.class,String.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.get(((String)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("at",String.class,int.class,String.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.get(((String)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("clean",IShape.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.opClean(((IShape)target));}});
		addUnary("write",IGamaFile.class,Object.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.opWrite(scope,((IGamaFile)target));}});
		addUnary("circle",Double.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opCircle(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("*",Double.class,Integer.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opTimes((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("empty",String.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.isEmpty(((String)target));}});
		addBinary("<",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("point",Object.class,ILocation.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asPoint(scope,((Object)target));}});
		addBinary("-",Double.class,Double.class,Double.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opMinus((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<",Integer.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("agent",Object.class,IAgent.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asAgent(scope,((Object)target));}});
		addBinary("touches",IShape.class,IShape.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Properties.opTouches(((IShape)left),((IShape)right));}});
		addBinary("*",IShape.class,Double.class,IShape.class,false,(short)4,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opScaledBy(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("scaled_by",IShape.class,Double.class,IShape.class,false,(short)4,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opScaledBy(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("inside",IContainer.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opInside(scope,((IContainer)left),((Object)right));}});
		addBinary("distance_between",ITopology.class,IContainer.class,Double.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opDistanceBetween(scope,((ITopology)left),((IContainer)right));}});
		addBinary(">=",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("among",Integer.class,GamaMap.class,GamaMap.class,false,(short)98,false,(short)-13,(short)-19,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.opAmong(scope,(left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),((GamaMap)right));}});
		addBinary("path_to",IShape.class,IShape.class,IPath.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IPath execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opPathTo(scope,((IShape)left),((IShape)right));}});
		addBinary("translated_by",IShape.class,GamaPoint.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primTranslationBy(((IShape)left),((GamaPoint)right));}});
		addBinary("last_index_of",String.class,String.class,Integer.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opLastIndexOf(((String)left),((String)right));}});
		addBinary("-",IList.class,Object.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opMinus(((IList)left),((Object)right));}});
		addUnary("tanh",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.tanh((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("acos",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.acos((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("index_of",GamaMap.class,Object.class,Object.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opIndexOf(((GamaMap)left),((Object)right));}});
		addBinary("rotated_by",IShape.class,Double.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primRotation(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("farthest_point_to",IShape.class,GamaPoint.class,ILocation.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Points.opFarthestPointTo(((IShape)left),((GamaPoint)right));}});
		addUnary("read",String.class,Object.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.opRead(scope,((String)target));}});
		addBinary("min_of",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-17,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.minOf(scope,((IContainer)left),((IExpression)right));}});
		addUnary("length",String.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.length(((String)target));}});
		addBinary("weight_of",IGraph.class,Object.class,Double.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.weightOf(((IGraph)left),((Object)right));}});
		addUnary("int",Object.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asInt(scope,((Object)target));}});
		addUnary("sqrt",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.sqrt((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("matrix",Object.class,IMatrix.class,true,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asMatrix(scope,((Object)target));}});
		addBinary("overlapping",ISpecies.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opOverlapping(scope,((ISpecies)left),((Object)right));}});
		addBinary("-",GamaPoint.class,Double.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.substract(((GamaPoint)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("rewire_n",IGraph.class,Integer.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.rewireGraph(((IGraph)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("**",Integer.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("^",Integer.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("as_date",double.class,String.class,String.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.asDate((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),((String)right));}});
		addUnary("undirected",IGraph.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Graphs.asUndirectedGraph(((IGraph)target));}});
		addBinary("intersection",IShape.class,IShape.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opInter(((IShape)left),((IShape)right));}});
		addBinary("inter",IShape.class,IShape.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opInter(((IShape)left),((IShape)right));}});
		addBinary("in",Object.class,IContainer.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opIn(((Object)left),((IContainer)right));}});
		addUnary("bool",Object.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asBool(scope,((Object)target));}});
		addUnary("rnd",GamaPoint.class,ILocation.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opRnd(scope,((GamaPoint)target));}});
		addUnary("tan",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.tan((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("map",IContainer.class,Map.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Map execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Map)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Map.class), null) ; 
return ((IContainer) target).mapValue(scope);}});
		addBinary("+",String.class,Object.class,String.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Strings.opPlus(scope,((String)left),((Object)right));}});
		addUnary("first",ISpecies.class,IAgent.class,false,(short)-16,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Containers.getFirst(scope,((ISpecies)target));}});
		addUnary("ln",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.ln((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("geometric_mean",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opGeomMean(scope,((GamaList)target));}});
		addUnary("rgb",Object.class,GamaColor.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asColor(scope,((Object)target));}});
		addUnary("agents_inside",Object.class,IList.class,false,(short)-13,(short)0,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Queries.opAgentsIn(scope,((Object)target));}});
		addBinary("as_intersection_graph",IContainer.class,Double.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.spatialFromVertices(scope,((IContainer)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("-",IShape.class,IShape.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opDifference(((IShape)left),((IShape)right));}});
		addBinary("count",IContainer.class,IExpression.class,Integer.class,true,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.count(scope,((IContainer)left),((IExpression)right));}});
		addBinary("+",IList.class,Object.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opPlus(((IList)left),((Object)right));}});
		addUnary("triangulate",IShape.class,GamaList.class,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public GamaList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.primTriangulate(((IShape)target));}});
		addUnary("abs",Integer.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.abs((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addUnary("tan",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.tan((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("last_index_of",List.class,Object.class,Integer.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opLastIndexOf(((List)left),((Object)right));}});
		addUnary("flip",Double.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opFlip(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addBinary("around",Double.class,Object.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Creation.opFringe(scope,(left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),((Object)right));}});
		addBinary("with_min_of",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.withMinOf(scope,((IContainer)left),((IExpression)right));}});
		addBinary("contains_any",String.class,List.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opContainsAny(((String)left),((List)right));}});
		addBinary("points_at",Integer.class,Double.class,GamaList.class,false,(short)98,false,(short)-13,(short)7,
			new IOperatorExecuter() {
			@Override
			public GamaList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Points.opPointsAt(scope,(left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("load_graph_from_pajek",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromPajek(scope,((GamaMap)target));}});
		addBinary("union",IList.class,IList.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opUnion(((IList)left),((IList)right));}});
		addUnary("geometry",Object.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asGeometry(scope,((Object)target));}});
		addUnary("length",ISpecies.class,Integer.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Containers.getLength(scope,((ISpecies)target));}});
		addBinary("as",Object.class,ISpecies.class,IAgent.class,false,(short)3,false,(short)-19,(short)-19,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Cast.asAgent(scope,((Object)left),((ISpecies)right));}});
		addBinary("/",Double.class,Integer.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opDivide((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("load_graph_from_tlp",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromTLP(scope,((GamaMap)target));}});
		addBinary("with_weights",IGraph.class,GamaMap.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.withWeights(scope,((IGraph)left),((GamaMap)right));}});
		addUnary("split_lines",IList.class,IList.class,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.splitLines(scope,((IList)target));}});
		addBinary("div",Integer.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.div((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary(">=",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<>",Object.class,Object.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different(((Object)left),((Object)right));}});
		addBinary("!=",Object.class,Object.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different(((Object)left),((Object)right));}});
		addUnary("collate",IList.class,IList.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Containers.interleave(scope,((IList)target));}});
		addUnary("last",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).last();}});
		addUnary("reverse",IContainer.class,IContainer.class,true,(short)-14,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IContainer execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (IContainer)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IContainer.class), null) ; 
return ((IContainer) target).reverse();}});
		addBinary("*",GamaPoint.class,Integer.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.multiply(((GamaPoint)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("+",Integer.class,Integer.class,Integer.class,false,(short)5,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opPlus((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("agents_overlapping",Object.class,IList.class,false,(short)-13,(short)0,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Queries.opOverlappingAgents(scope,((Object)target));}});
		addBinary("evaluate_with",String.class,IExpression.class,Object.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.System.opEvalJava(scope,((String)left),((IExpression)right));}});
		addUnary("union",GamaList.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Operators.opUnion(scope,((GamaList)target));}});
		addBinary("+",GamaPoint.class,GamaPoint.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.add(((GamaPoint)left),((GamaPoint)right));}});
		addBinary("=",Double.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.equal((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("-",IShape.class,ISpecies.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Operators.opDifferenceSpecies(scope,((IShape)left),((ISpecies)right));}});
		addBinary("<=",GamaPoint.class,GamaPoint.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.lessOrEqual(((GamaPoint)left),((GamaPoint)right));}});
		addUnary("cos",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.cos((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("?",Boolean.class,IExpression.class,Object.class,false,(short)0,false,(short)-17,(short)-19,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Logic.iff(scope,(left == null) ? false : ((Boolean)left),((IExpression)right));}});
		addBinary("closest_to",IContainer.class,IShape.class,Object.class,false,(short)98,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opClosestTo(scope,((IContainer)left),((IShape)right));}});
		addBinary("with_weights",IGraph.class,IList.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.withWeights(scope,((IGraph)left),((IList)right));}});
		addBinary("-",GamaPoint.class,GamaPoint.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.substract(((GamaPoint)left),((GamaPoint)right));}});
		addUnary("mean",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.getMean(scope,((IContainer)target));}});
		addUnary("is_number",String.class,Boolean.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.isGamaNumber(((String)target));}});
		addBinary("agent_from_geometry",IPath.class,IShape.class,IAgent.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.getAgentFromGeom(((IPath)left),((IShape)right));}});
		addBinary(">=",GamaPoint.class,GamaPoint.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greaterOrEqual(((GamaPoint)left),((GamaPoint)right));}});
		addBinary("overlaps",IShape.class,IShape.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Properties.opOverlaps(scope,((IShape)left),((IShape)right));}});
		addBinary("<=",Integer.class,Double.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.lessOrEqual((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<=",String.class,String.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.lessOrEqual(((String)left),((String)right));}});
		addUnary("fact",Integer.class,Integer.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.fact((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("^",Double.class,Integer.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("**",Double.class,Integer.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.pow((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("transformed_by",IShape.class,GamaPoint.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primAffinite(((IShape)left),((GamaPoint)right));}});
		addBinary("path_to",GamaPoint.class,GamaPoint.class,IPath.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IPath execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opPathTo(scope,((GamaPoint)left),((GamaPoint)right));}});
		addUnary("map",Object.class,GamaMap.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asMap(scope,((Object)target));}});
		addBinary("+",GamaPoint.class,Integer.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.add(((GamaPoint)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("matrix",IContainer.class,IMatrix.class,true,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (IMatrix)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IMatrix.class), null) ; 
return ((IContainer) target).matrixValue(scope);}});
		addBinary("masked_by",IShape.class,GamaList.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Operators.opMaskedBy(scope,((IShape)left),((GamaList)right));}});
		addBinary("contains_edge",IGraph.class,Object.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.containsEdge(((IGraph)left),((Object)right));}});
		addUnary("as_edge_graph",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Graphs.spatialFromEdges(scope,((GamaMap)target));}});
		addUnary("file",String.class,IGamaFile.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGamaFile execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Files.from(scope,((String)target));}});
		addBinary("contains",String.class,String.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Strings.opContains(((String)left),((String)right));}});
		addBinary("as_distance_graph",IContainer.class,Double.class,IGraph.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Graphs.spatialDistanceGraph(scope,((IContainer)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("neighbours_of",ITopology.class,IAgent.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opNeighboursOf(scope,((ITopology)left),((IAgent)right));}});
		addBinary("@",ISpecies.class,Integer.class,IAgent.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.getAgent(scope,((ISpecies)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("at",ISpecies.class,Integer.class,IAgent.class,false,(short)98,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.getAgent(scope,((ISpecies)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("div",Double.class,Double.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.div((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("in_edges_of",IGraph.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.inEdgesOf(((IGraph)left),((Object)right));}});
		addUnary("sin",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.sin((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("+",IList.class,IList.class,IList.class,false,(short)5,true,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opPlus(((IList)left),((IList)right));}});
		addUnary("harmonic_mean",GamaList.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Stats.opHarmonicMean(scope,((GamaList)target));}});
		addBinary("add_point",IShape.class,ILocation.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Operators.opAddPoint(((IShape)left),((ILocation)right));}});
		addUnary("any_location_in",IShape.class,ILocation.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Points.opAnyLocationIn(scope,((IShape)target));}});
		addUnary("any_point_in",IShape.class,ILocation.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Points.opAnyLocationIn(scope,((IShape)target));}});
		addUnary("asin",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.asin((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("*",Integer.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opTimes((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("convex_hull",IShape.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Transformations.opConvexHull(((IShape)target));}});
		addBinary("predecessors_of",IGraph.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.predecessorsOf(((IGraph)left),((Object)right));}});
		addUnary("max",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).max(scope);}});
		addBinary("distance_to",IShape.class,IShape.class,Double.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opDistanceTo(scope,((IShape)left),((IShape)right));}});
		addBinary(">",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.greater((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("is",IExpression.class,IExpression.class,Boolean.class,false,(short)2,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Cast.isA(scope,((IExpression)left),((IExpression)right));}});
		addBinary("select",GamaMap.class,IExpression.class,GamaMap.class,true,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.where(scope,((GamaMap)left),((IExpression)right));}});
		addBinary("where",GamaMap.class,IExpression.class,GamaMap.class,true,(short)6,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaMap execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.where(scope,((GamaMap)left),((IExpression)right));}});
		addUnary("list",IContainer.class,IList.class,true,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope,final Object target, final Object right) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
return ((IContainer) target).listValue(scope);}});
		addUnary("gauss",GamaPoint.class,Double.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opGauss(scope,((GamaPoint)target));}});
		addUnary("line",IList.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opPolyline(((IList)target));}});
		addUnary("polyline",IList.class,IShape.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Spatial.Creation.opPolyline(((IList)target));}});
		addBinary("select",IContainer.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.where(scope,((IContainer)left),((IExpression)right));}});
		addBinary("where",IContainer.class,IExpression.class,IList.class,true,(short)6,false,(short)-13,(short)-20,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.where(scope,((IContainer)left),((IExpression)right));}});
		addBinary("closest_to",ISpecies.class,IShape.class,IAgent.class,false,(short)98,false,(short)-20,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IAgent execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Queries.opClosestTo(scope,((ISpecies)left),((IShape)right));}});
		addBinary("!=",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("<>",Double.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.different((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("closest_points_with",IShape.class,IShape.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Points.opClosestPointsBetween(((IShape)left),((IShape)right));}});
		addBinary("of",IAgent.class,IExpression.class,Object.class,false,(short)99,false,(short)-17,(short)-19,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.System.opGetValue(scope,((IAgent)left),((IExpression)right));}});
		addBinary(".",IAgent.class,IExpression.class,Object.class,false,(short)99,false,(short)-17,(short)-19,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.System.opGetValue(scope,((IAgent)left),((IExpression)right));}});
		addBinary("max_of",IContainer.class,IExpression.class,Object.class,true,(short)6,false,(short)-17,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Containers.maxOf(scope,((IContainer)left),((IExpression)right));}});
		addBinary("buffer",IShape.class,Double.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("enlarged_by",IShape.class,Double.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("+",IShape.class,Double.class,IShape.class,false,(short)5,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.opBuffer(((IShape)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("contains_any",IContainer.class,IContainer.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opContainsAny(((IContainer)left),((IContainer)right));}});
		addBinary("/",GamaColor.class,Double.class,GamaColor.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public GamaColor execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Colors.divide(((GamaColor)left),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addUnary("rows_list",IMatrix.class,IList.class,true,(short)-13,(short)5,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
return ((IMatrix) target).getRowsList();}});
		addUnary("ln",Integer.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.ln((target == null) ? 0 : target instanceof Integer ? (Integer) target : Integer.valueOf(((Number)target).intValue()));}});
		addBinary("contains_all",IContainer.class,IContainer.class,Boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opContainsAll(((IContainer)left),((IContainer)right));}});
		addUnary("topology",Object.class,ITopology.class,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public ITopology execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asTopology(scope,((Object)target));}});
		addUnary("exp",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.exp((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("rnd",Double.class,Integer.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opRnd(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("load_graph_from_dgs",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromDGS(scope,((GamaMap)target));}});
		addUnary("shuffle",ISpecies.class,IList.class,false,(short)-13,(short)-16,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opShuffle(scope,((ISpecies)target));}});
		addBinary("rotated_by",IShape.class,Integer.class,IShape.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IShape execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Transformations.primRotation(((IShape)left),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("direction_between",ITopology.class,IContainer.class,Integer.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Relations.opDirectionBetween(scope,((ITopology)left),((IContainer)right));}});
		addUnary("load_graph_from_dot",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromDot(scope,((GamaMap)target));}});
		addBinary("<",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addUnary("is_image",String.class,Boolean.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object target, final Object right) { return GamaFileType.isImageFile(((String)target));}});
		addBinary("div",Double.class,Integer.class,Integer.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.div((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("<->",Double.class,Integer.class,ILocation.class,false,(short)0,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.toPoint((left == null) ? 0d : left instanceof Double ? (Double) left : Double.valueOf(((Number)left).doubleValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("<=",Integer.class,Integer.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.opLessThanOrEqual((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0 : right instanceof Integer ? (Integer) right : Integer.valueOf(((Number)right).intValue()));}});
		addBinary("/",Integer.class,Double.class,Double.class,false,(short)4,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Maths.opDivide((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("last_index_of",IMatrix.class,Object.class,ILocation.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opLastIndexOf(((IMatrix)left),((Object)right));}});
		addUnary("-",Double.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Maths.negate((target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("load_graph_from_edge",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromEdge(scope,((GamaMap)target));}});
		addUnary("first",IContainer.class,Object.class,true,(short)-16,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { if (target == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
return ((IContainer) target).first();}});
		addUnary("load_graph_from_ncol",GamaMap.class,IGraph.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IGraph execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.GraphsGraphstream.primLoadGraphFromFileFromNCol(scope,((GamaMap)target));}});
		addUnary("path",Object.class,IPath.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IPath execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Graphs.toPath(scope,((Object)target));}});
		addBinary("contains",IContainer.class,Object.class,boolean.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope,final Object left, final Object right) { if (left == null) return  false ; 
return ((IContainer) left).contains(((Object)right));}});
		addUnary("float",Object.class,Double.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Cast.asFloat(scope,((Object)target));}});
		addBinary("intersects",IShape.class,IShape.class,Boolean.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Spatial.Properties.opIntersects(((IShape)left),((IShape)right));}});
		addUnary("poisson",Double.class,Integer.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Integer execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Random.opPoisson(scope,(target == null) ? 0d : target instanceof Double ? (Double) target : Double.valueOf(((Number)target).doubleValue()));}});
		addUnary("first",String.class,String.class,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public String execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.Strings.first(((String)target));}});
		addBinary("<->",Integer.class,Double.class,ILocation.class,false,(short)0,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public ILocation execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.toPoint((left == null) ? 0 : left instanceof Integer ? (Integer) left : Integer.valueOf(((Number)left).intValue()),(right == null) ? 0d : right instanceof Double ? (Double) right : Double.valueOf(((Number)right).doubleValue()));}});
		addBinary("<",GamaPoint.class,GamaPoint.class,Boolean.class,false,(short)2,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Boolean execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Comparison.less(((GamaPoint)left),((GamaPoint)right));}});
		addBinary("as_4_grid",IShape.class,GamaPoint.class,IMatrix.class,false,(short)98,false,(short)-13,(short)13,
			new IOperatorExecuter() {
			@Override
			public IMatrix execute(final IScope scope, final Object left, final Object right) { return msi.gaml.operators.Spatial.Transformations.opAs4Grid(scope,((IShape)left),((GamaPoint)right));}});
		addBinary("*",GamaPoint.class,GamaPoint.class,Double.class,false,(short)98,true,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Double execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Points.multiply(((GamaPoint)left),((GamaPoint)right));}});
		addUnary("eval_gaml",String.class,Object.class,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public Object execute(final IScope scope, final Object target, final Object right) { return msi.gaml.operators.System.opEvalGaml(scope,((String)target));}});
		addBinary("out_edges_of",IGraph.class,Object.class,IList.class,false,(short)98,false,(short)-13,(short)-13,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Graphs.outEdgesOf(((IGraph)left),((Object)right));}});
		addBinary("of_species",IList.class,ISpecies.class,IList.class,false,(short)3,false,(short)-13,(short)-19,
			new IOperatorExecuter() {
			@Override
			public IList execute(final IScope scope, final Object left, final Object right)  { return msi.gaml.operators.Containers.opOfSpecies(((IList)left),((ISpecies)right));}});

		addAction("primPercievedArea",GeometricSkill.class, new PrimitiveExecuter() {
			@Override
			public GamaShape execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GeometricSkill) target).primPercievedArea(scope);  }
@Override public IType getReturnType() { return Types.get(GamaShape.class);}}, "percieved_area","agent","geometry","range","precision");

		addAction("evidenceTheoryDecisionMaking",msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer.class, new PrimitiveExecuter() {
			@Override
			public Integer execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer) target).evidenceTheoryDecisionMaking(scope);  }
@Override public IType getReturnType() { return Types.get(Integer.class);}}, "evidence_theory_DM","candidates","criteria","simple");

		addAction("primClusteringDBScan",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringDBScan(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_DBScan","agents","attributes","distance_f","epsilon","min_points");

		addAction("primGoto",MovingSkill.class, new PrimitiveExecuter() {
			@Override
			public IPath execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((MovingSkill) target).primGoto(scope);  }
@Override public IType getReturnType() { return Types.get(IPath.class);}}, "goto","target","speed","on","return_path");

		addAction("primClusteringFarthestFirst",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringFarthestFirst(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_farthestFirst","agents","attributes","num_clusters","seed");

		addAction("primNeighbourhoodExclu",GeometricSkill.class, new PrimitiveExecuter() {
			@Override
			public IShape execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GeometricSkill) target).primNeighbourhoodExclu(scope);  }
@Override public IType getReturnType() { return Types.get(IShape.class);}}, "neighbourhood_exclusive","distance","species","buffer_others","buffer_in");

		addAction("primMoveForward",MovingSkill.class, new PrimitiveExecuter() {
			@Override
			public IPath execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((MovingSkill) target).primMoveForward(scope);  }
@Override public IType getReturnType() { return Types.get(IPath.class);}}, "move","speed","heading","bounds");

		addAction("primTell",GamlAgent.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GamlAgent) target).primTell(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "tell","message");

		addAction("primDie",GamlAgent.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GamlAgent) target).primDie(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "die");

		addAction("primMoveRandomly",MovingSkill.class, new PrimitiveExecuter() {
			@Override
			public IPath execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((MovingSkill) target).primMoveRandomly(scope);  }
@Override public IType getReturnType() { return Types.get(IPath.class);}}, "wander","speed","amplitude","bounds");

		addAction("WeightedMeansDecisionMaking",msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer.class, new PrimitiveExecuter() {
			@Override
			public Integer execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer) target).WeightedMeansDecisionMaking(scope);  }
@Override public IType getReturnType() { return Types.get(Integer.class);}}, "weighted_means_DM","candidates","criteria");

		addAction("primHalt",WorldSkill.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((WorldSkill) target).primHalt(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "halt");

		addAction("PrometheeDecisionMaking",msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer.class, new PrimitiveExecuter() {
			@Override
			public Integer execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer) target).PrometheeDecisionMaking(scope);  }
@Override public IType getReturnType() { return Types.get(Integer.class);}}, "promethee_DM","candidates","criteria");

		addAction("primError",GamlAgent.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GamlAgent) target).primError(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "error","message");

		addAction("primClusteringCobweb",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringCobweb(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_cobweb","agents","attributes","acuity","cutoff","seed");

		addAction("primPause",WorldSkill.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((WorldSkill) target).primPause(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "pause");

		addAction("primWrite",GamlAgent.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GamlAgent) target).primWrite(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "write","message");

		addAction("primClusteringXMeans",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringXMeans(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_xmeans","agents","attributes","bin_value","cut_off_factor","distance_f","max_iterations","max_kmeans","max_kmeans_for_children","max_kmeans_for_children","max_num_clusters","min_num_clusters","seed");

		addAction("electreDecisionMaking",msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer.class, new PrimitiveExecuter() {
			@Override
			public Integer execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer) target).electreDecisionMaking(scope);  }
@Override public IType getReturnType() { return Types.get(Integer.class);}}, "electre_DM","candidates","criteria","fuzzy_cut");

		addAction("primClusteringSimpleKMeans",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringSimpleKMeans(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_simple_kmeans","agents","attributes","distance_f","dont_replace_missing_values","max_iterations","num_clusters","preserve_instances_order","seed");

		addAction("primFollow",MovingSkill.class, new PrimitiveExecuter() {
			@Override
			public IPath execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((MovingSkill) target).primFollow(scope);  }
@Override public IType getReturnType() { return Types.get(IPath.class);}}, "follow","speed","path");

		addAction("primDebug",GamlAgent.class, new PrimitiveExecuter() {
			@Override
			public Object execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((GamlAgent) target).primDebug(scope);  }
@Override public IType getReturnType() { return Types.get(Object.class);}}, "debug","message");

		addAction("primClusteringEM",msi.gaml.extensions.cluster_builder.ClusterBuilder.class, new PrimitiveExecuter() {
			@Override
			public List execute(final ISkill target, IAgent agent, final IScope scope) { 
 return ((msi.gaml.extensions.cluster_builder.ClusterBuilder) target).primClusteringEM(scope);  }
@Override public IType getReturnType() { return Types.get(List.class);}}, "clustering_em","agents","attributes","max_iterations","num_clusters","min_std_dev","seed");

		addGetterExecuter("getSeed",WorldSkill.class, new IVarGetter() {@Override public Double execute(final IAgent agent, final ISkill target) { if (target == null) return  0d ; 
  return (Double)((WorldSkill) target).getSeed(agent);}});

		addGetterExecuter("getX",GridSkill.class, new IVarGetter() {@Override public Integer execute(final IAgent agent, final ISkill target) { if (target == null) return  0 ; 
  return (Integer)((GridSkill) target).getX(agent);}});

		addGetterExecuter("getHost",IGamlAgent.class, new IVarGetter() {@Override public IAgent execute(final IAgent agent, final ISkill target) { if (target == null) return  (IAgent)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IAgent.class), null) ; 
  return (IAgent)((IGamlAgent) target).getHost();}});

		addGetterExecuter("getWarningsAsErrors",WorldSkill.class, new IVarGetter() {@Override public Boolean execute(final IAgent agent, final ISkill target) { if (target == null) return  false ; 
  return (Boolean)((WorldSkill) target).getWarningsAsErrors(agent);}});

		addGetterExecuter("getRng",WorldSkill.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((WorldSkill) target).getRng(agent);}});

		addGetterExecuter("getSpeed",MovingSkill.class, new IVarGetter() {@Override public Double execute(final IAgent agent, final ISkill target) { if (target == null) return  0d ; 
  return (Double)((MovingSkill) target).getSpeed(agent);}});

		addGetterExecuter("getTimeStep",WorldSkill.class, new IVarGetter() {@Override public Double execute(final IAgent agent, final ISkill target) { if (target == null) return  0d ; 
  return (Double)((WorldSkill) target).getTimeStep(agent);}});

		addGetterExecuter("getY",GridSkill.class, new IVarGetter() {@Override public Integer execute(final IAgent agent, final ISkill target) { if (target == null) return  0 ; 
  return (Integer)((GridSkill) target).getY(agent);}});

		addGetterExecuter("getTime",WorldSkill.class, new IVarGetter() {@Override public Double execute(final IAgent agent, final ISkill target) { if (target == null) return  0d ; 
  return (Double)((WorldSkill) target).getTime(agent);}});

		addGetterExecuter("getTopology",IGamlAgent.class, new IVarGetter() {@Override public ITopology execute(final IAgent agent, final ISkill target) { if (target == null) return  (ITopology)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ITopology.class), null) ; 
  return (ITopology)((IGamlAgent) target).getTopology();}});

		addGetterExecuter("getColor",GridSkill.class, new IVarGetter() {@Override public GamaColor execute(final IAgent agent, final ISkill target) { if (target == null) return  (GamaColor)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaColor.class), null) ; 
  return (GamaColor)((GridSkill) target).getColor(agent);}});

		addGetterExecuter("getHeading",MovingSkill.class, new IVarGetter() {@Override public Integer execute(final IAgent agent, final ISkill target) { if (target == null) return  0 ; 
  return (Integer)((MovingSkill) target).getHeading(agent);}});

		addGetterExecuter("getLocation",IGamlAgent.class, new IVarGetter() {@Override public ILocation execute(final IAgent agent, final ISkill target) { if (target == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
  return (ILocation)((IGamlAgent) target).getLocation();}});

		addGetterExecuter("getTotalDuration",WorldSkill.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((WorldSkill) target).getTotalDuration(agent);}});

		addGetterExecuter("getDestination",MovingSkill.class, new IVarGetter() {@Override public ILocation execute(final IAgent agent, final ISkill target) { if (target == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
  return (ILocation)((MovingSkill) target).getDestination(agent);}});

		addGetterExecuter("getMembers",IGamlAgent.class, new IVarGetter() {@Override public IList execute(final IAgent agent, final ISkill target) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
  return (IList)((IGamlAgent) target).getMembers();}});

		addGetterExecuter("getGeometry",IGamlAgent.class, new IVarGetter() {@Override public IShape execute(final IAgent agent, final ISkill target) { if (target == null) return  (IShape)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IShape.class), null) ; 
  return (IShape)((IGamlAgent) target).getGeometry();}});

		addGetterExecuter("isUserControlled",UserControlArchitecture.class, new IVarGetter() {@Override public Boolean execute(final IAgent agent, final ISkill target) { if (target == null) return  false ; 
  return (Boolean)((UserControlArchitecture) target).isUserControlled(agent);}});

		addGetterExecuter("getStateNames",FsmArchitecture.class, new IVarGetter() {@Override public IList execute(final IAgent agent, final ISkill target) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
  return (IList)((FsmArchitecture) target).getStateNames(agent);}});

		addGetterExecuter("getAgents",IGamlAgent.class, new IVarGetter() {@Override public IList execute(final IAgent agent, final ISkill target) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
  return (IList)((IGamlAgent) target).getAgents();}});

		addGetterExecuter("getFatalErrors",WorldSkill.class, new IVarGetter() {@Override public Boolean execute(final IAgent agent, final ISkill target) { if (target == null) return  false ; 
  return (Boolean)((WorldSkill) target).getFatalErrors(agent);}});

		addGetterExecuter("getAverageDuration",WorldSkill.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((WorldSkill) target).getAverageDuration(agent);}});

		addGetterExecuter("getDuration",WorldSkill.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((WorldSkill) target).getDuration(agent);}});

		addGetterExecuter("getPeers",IGamlAgent.class, new IVarGetter() {@Override public IList execute(final IAgent agent, final ISkill target) { if (target == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
  return (IList)((IGamlAgent) target).getPeers();}});

		addGetterExecuter("getStateName",FsmArchitecture.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((FsmArchitecture) target).getStateName(agent);}});

		addGetterExecuter("getAgents",GridSkill.class, new IVarGetter() {@Override public List execute(final IAgent agent, final ISkill target) { if (target == null) return  (List)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(List.class), null) ; 
  return (List)((GridSkill) target).getAgents(agent);}});

		addGetterExecuter("getName",IGamlAgent.class, new IVarGetter() {@Override public String execute(final IAgent agent, final ISkill target) { if (target == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
  return (String)((IGamlAgent) target).getName();}});

		addSetterExecuter("setAgents",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setAgents((IList)arg); }});

		addSetterExecuter("setY",GridSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((GridSkill) target).setY(agent, (Integer)arg); }});

		addSetterExecuter("setHost",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setHost((IAgent)arg); }});

		addSetterExecuter("setTopology",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setTopology((ITopology)arg); }});

		addSetterExecuter("setAgents",GridSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((GridSkill) target).setAgents(agent, (GamaList)arg); }});

		addSetterExecuter("setTimeStep",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setTimeStep(agent, (Double)arg); }});

		addSetterExecuter("setRng",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setRng(agent, (String)arg); }});

		addSetterExecuter("setColor",GridSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((GridSkill) target).setColor(agent, (GamaColor)arg); }});

		addSetterExecuter("setStateName",FsmArchitecture.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((FsmArchitecture) target).setStateName(agent, (String)arg); }});

		addSetterExecuter("setSeed",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setSeed(agent, (Double)arg); }});

		addSetterExecuter("setTime",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setTime(agent, (Double)arg); }});

		addSetterExecuter("setSpeed",MovingSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((MovingSkill) target).setSpeed(agent, (Double)arg); }});

		addSetterExecuter("setHeading",MovingSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((MovingSkill) target).setHeading(agent, (Integer)arg); }});

		addSetterExecuter("setGeometry",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setGeometry((IShape)arg); }});

		addSetterExecuter("setStateNames",FsmArchitecture.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((FsmArchitecture) target).setStateNames(agent, (IList)arg); }});

		addSetterExecuter("setX",GridSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((GridSkill) target).setX(agent, (Integer)arg); }});

		addSetterExecuter("setWarningsAsErrors",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setWarningsAsErrors(agent, (Boolean)arg); }});

		addSetterExecuter("setMembers",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setMembers((IList)arg); }});

		addSetterExecuter("setLocation",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setLocation((ILocation)arg); }});

		addSetterExecuter("setDestination",MovingSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((MovingSkill) target).setDestination(agent, (ILocation)arg); }});

		addSetterExecuter("setUserControlled",UserControlArchitecture.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((UserControlArchitecture) target).setUserControlled(agent, (Boolean)arg); }});

		addSetterExecuter("setFatalErrors",WorldSkill.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((WorldSkill) target).setFatalErrors(agent, (Boolean)arg); }});

		addSetterExecuter("setName",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setName((String)arg); }});

		addSetterExecuter("setPeers",IGamlAgent.class, new IVarSetter() {@Override public void execute(final IAgent agent, final ISkill target, final Object arg) { if (target == null) return;  
((IGamlAgent) target).setPeers((IList)arg); }});

		addFieldGetterExecuter("getName",IGamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((IGamaFile) v).getName(); return result;}});

		addFieldGetterExecuter("getDimensions",IMatrix.class, new IFieldGetter() {@Override public ILocation value(final IValue v)  { if (v == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
ILocation result = ((IMatrix) v).getDimensions(); return result;}});

		addFieldGetterExecuter("getX",ILocation.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((ILocation) v).getX(); return result;}});

		addFieldGetterExecuter("getRows",IMatrix.class, new IFieldGetter() {@Override public Integer value(final IValue v)  { if (v == null) return  0 ; 
Integer result = ((IMatrix) v).getRows(); return result;}});

		addFieldGetterExecuter("getPerimeter",IShape.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((IShape) v).getPerimeter(); return result;}});

		addFieldGetterExecuter("getGeometricEnvelope",GamaShape.class, new IFieldGetter() {@Override public GamaShape value(final IValue v)  { if (v == null) return  (GamaShape)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaShape.class), null) ; 
GamaShape result = ((GamaShape) v).getGeometricEnvelope(); return result;}});

		addFieldGetterExecuter("isReadable",GamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((GamaFile) v).isReadable(); return result;}});

		addFieldGetterExecuter("getHeight",GamaShape.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((GamaShape) v).getHeight(); return result;}});

		addFieldGetterExecuter("last",GamaPoint.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((GamaPoint) v).last(); return result;}});

		addFieldGetterExecuter("red",GamaColor.class, new IFieldGetter() {@Override public Integer value(final IValue v)  { if (v == null) return  0 ; 
Integer result = ((GamaColor) v).red(); return result;}});

		addFieldGetterExecuter("getExteriorRing",GamaShape.class, new IFieldGetter() {@Override public GamaShape value(final IValue v)  { if (v == null) return  (GamaShape)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaShape.class), null) ; 
GamaShape result = ((GamaShape) v).getExteriorRing(); return result;}});

		addFieldGetterExecuter("getPath",GamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((GamaFile) v).getPath(); return result;}});

		addFieldGetterExecuter("getGeometries",GamaShape.class, new IFieldGetter() {@Override public GamaList value(final IValue v)  { if (v == null) return  (GamaList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaList.class), null) ; 
GamaList result = ((GamaShape) v).getGeometries(); return result;}});

		addFieldGetterExecuter("getGraph",IPath.class, new IFieldGetter() {@Override public IGraph value(final IValue v)  { if (v == null) return  (IGraph)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IGraph.class), null) ; 
IGraph result = ((IPath) v).getGraph(); return result;}});

		addFieldGetterExecuter("getPairs",GamaMap.class, new IFieldGetter() {@Override public GamaList value(final IValue v)  { if (v == null) return  (GamaList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaList.class), null) ; 
GamaList result = ((GamaMap) v).getPairs(); return result;}});

		addFieldGetterExecuter("isFolder",IGamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGamaFile) v).isFolder(); return result;}});

		addFieldGetterExecuter("getKeys",GamaMap.class, new IFieldGetter() {@Override public GamaList value(final IValue v)  { if (v == null) return  (GamaList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaList.class), null) ; 
GamaList result = ((GamaMap) v).getKeys(); return result;}});

		addFieldGetterExecuter("getContents",GamaFile.class, new IFieldGetter() {@Override public IContainer value(final IValue v)  { if (v == null) return  (IContainer)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IContainer.class), null) ; 
IContainer result = ((GamaFile) v).getContents(); return result;}});

		addFieldGetterExecuter("getAgentList",IPath.class, new IFieldGetter() {@Override public List value(final IValue v)  { if (v == null) return  (List)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(List.class), null) ; 
List result = ((IPath) v).getAgentList(); return result;}});

		addFieldGetterExecuter("exists",IGamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGamaFile) v).exists(); return result;}});

		addFieldGetterExecuter("getEnvironment",ITopology.class, new IFieldGetter() {@Override public IShape value(final IValue v)  { if (v == null) return  (IShape)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IShape.class), null) ; 
IShape result = ((ITopology) v).getEnvironment(); return result;}});

		addFieldGetterExecuter("isReadable",IGamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGamaFile) v).isReadable(); return result;}});

		addFieldGetterExecuter("getVertices",IGraph.class, new IFieldGetter() {@Override public IList value(final IValue v)  { if (v == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
IList result = ((IGraph) v).getVertices(); return result;}});

		addFieldGetterExecuter("getDarker",GamaColor.class, new IFieldGetter() {@Override public GamaColor value(final IValue v)  { if (v == null) return  (GamaColor)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaColor.class), null) ; 
GamaColor result = ((GamaColor) v).getDarker(); return result;}});

		addFieldGetterExecuter("getWidth",GamaShape.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((GamaShape) v).getWidth(); return result;}});

		addFieldGetterExecuter("getName",GamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((GamaFile) v).getName(); return result;}});

		addFieldGetterExecuter("getSpanningTree",IGraph.class, new IFieldGetter() {@Override public IList value(final IValue v)  { if (v == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
IList result = ((IGraph) v).getSpanningTree(); return result;}});

		addFieldGetterExecuter("first",GamaPair.class, new IFieldGetter() {@Override public Object value(final IValue v)  { if (v == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
Object result = ((GamaPair) v).first(); return result;}});

		addFieldGetterExecuter("getArea",GamaShape.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((GamaShape) v).getArea(); return result;}});

		addFieldGetterExecuter("getEdgeList",IPath.class, new IFieldGetter() {@Override public IList value(final IValue v)  { if (v == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
IList result = ((IPath) v).getEdgeList(); return result;}});

		addFieldGetterExecuter("getLocation",ILocated.class, new IFieldGetter() {@Override public ILocation value(final IValue v)  { if (v == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
ILocation result = ((ILocated) v).getLocation(); return result;}});

		addFieldGetterExecuter("getY",ILocation.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((ILocation) v).getY(); return result;}});

		addFieldGetterExecuter("getConnected",IGraph.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGraph) v).getConnected(); return result;}});

		addFieldGetterExecuter("getExtension",IGamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((IGamaFile) v).getExtension(); return result;}});

		addFieldGetterExecuter("getName",GamlSpecies.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((GamlSpecies) v).getName(); return result;}});

		addFieldGetterExecuter("getValues",GamaMap.class, new IFieldGetter() {@Override public GamaList value(final IValue v)  { if (v == null) return  (GamaList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaList.class), null) ; 
GamaList result = ((GamaMap) v).getValues(); return result;}});

		addFieldGetterExecuter("getPlaces",ITopology.class, new IFieldGetter() {@Override public IContainer value(final IValue v)  { if (v == null) return  (IContainer)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IContainer.class), null) ; 
IContainer result = ((ITopology) v).getPlaces(); return result;}});

		addFieldGetterExecuter("getHoles",GamaShape.class, new IFieldGetter() {@Override public GamaList value(final IValue v)  { if (v == null) return  (GamaList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaList.class), null) ; 
GamaList result = ((GamaShape) v).getHoles(); return result;}});

		addFieldGetterExecuter("isWritable",IGamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGamaFile) v).isWritable(); return result;}});

		addFieldGetterExecuter("getContents",IGamaFile.class, new IFieldGetter() {@Override public IContainer value(final IValue v)  { if (v == null) return  (IContainer)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IContainer.class), null) ; 
IContainer result = ((IGamaFile) v).getContents(); return result;}});

		addFieldGetterExecuter("isMultiple",GamaShape.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((GamaShape) v).isMultiple(); return result;}});

		addFieldGetterExecuter("getCols",IMatrix.class, new IFieldGetter() {@Override public Integer value(final IValue v)  { if (v == null) return  0 ; 
Integer result = ((IMatrix) v).getCols(); return result;}});

		addFieldGetterExecuter("isFolder",GamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((GamaFile) v).isFolder(); return result;}});

		addFieldGetterExecuter("getCircuit",IGraph.class, new IFieldGetter() {@Override public IValue value(final IValue v)  { if (v == null) return  (IValue)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IValue.class), null) ; 
IValue result = ((IGraph) v).getCircuit(); return result;}});

		addFieldGetterExecuter("getBrighter",GamaColor.class, new IFieldGetter() {@Override public GamaColor value(final IValue v)  { if (v == null) return  (GamaColor)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(GamaColor.class), null) ; 
GamaColor result = ((GamaColor) v).getBrighter(); return result;}});

		addFieldGetterExecuter("isVerbose",IGraph.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((IGraph) v).isVerbose(); return result;}});

		addFieldGetterExecuter("getEdges",IGraph.class, new IFieldGetter() {@Override public IList value(final IValue v)  { if (v == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
IList result = ((IGraph) v).getEdges(); return result;}});

		addFieldGetterExecuter("blue",GamaColor.class, new IFieldGetter() {@Override public Integer value(final IValue v)  { if (v == null) return  0 ; 
Integer result = ((GamaColor) v).blue(); return result;}});

		addFieldGetterExecuter("green",GamaColor.class, new IFieldGetter() {@Override public Integer value(final IValue v)  { if (v == null) return  0 ; 
Integer result = ((GamaColor) v).green(); return result;}});

		addFieldGetterExecuter("first",GamaPoint.class, new IFieldGetter() {@Override public Double value(final IValue v)  { if (v == null) return  0d ; 
Double result = ((GamaPoint) v).first(); return result;}});

		addFieldGetterExecuter("getStartVertex",IPath.class, new IFieldGetter() {@Override public ILocation value(final IValue v)  { if (v == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
ILocation result = ((IPath) v).getStartVertex(); return result;}});

		addFieldGetterExecuter("getEndVertex",IPath.class, new IFieldGetter() {@Override public ILocation value(final IValue v)  { if (v == null) return  (ILocation)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(ILocation.class), null) ; 
ILocation result = ((IPath) v).getEndVertex(); return result;}});

		addFieldGetterExecuter("exists",GamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((GamaFile) v).exists(); return result;}});

		addFieldGetterExecuter("isWritable",GamaFile.class, new IFieldGetter() {@Override public Boolean value(final IValue v)  { if (v == null) return  false ; 
Boolean result = ((GamaFile) v).isWritable(); return result;}});

		addFieldGetterExecuter("getPath",IGamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((IGamaFile) v).getPath(); return result;}});

		addFieldGetterExecuter("getPoints",GamaShape.class, new IFieldGetter() {@Override public IList value(final IValue v)  { if (v == null) return  (IList)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(IList.class), null) ; 
IList result = ((GamaShape) v).getPoints(); return result;}});

		addFieldGetterExecuter("getExtension",GamaFile.class, new IFieldGetter() {@Override public String value(final IValue v)  { if (v == null) return  (String)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(String.class), null) ; 
String result = ((GamaFile) v).getExtension(); return result;}});

		addFieldGetterExecuter("last",GamaPair.class, new IFieldGetter() {@Override public Object value(final IValue v)  { if (v == null) return  (Object)msi.gaml.types.Types.coerce(null, (Object) null, msi.gaml.types.Types.get(Object.class), null) ; 
Object result = ((GamaPair) v).last(); return result;}});

		addSpecies("default",GamlAgent.class,new IAgentConstructor() {
			@Override
			public IAgent createOneAgent(ISimulation sim,msi.gama.metamodel.population.IPopulation manager)  { 
 return new GamlAgent(sim, manager);}});

		addSpecies("experimentator",msi.gama.kernel.experiment.AbstractExperiment.ExperimentatorPopulation.ExperimentatorAgent.class,new IAgentConstructor() {
			@Override
			public IAgent createOneAgent(ISimulation sim,msi.gama.metamodel.population.IPopulation manager)  { 
 return new msi.gama.kernel.experiment.AbstractExperiment.ExperimentatorPopulation.ExperimentatorAgent(sim, manager);}});

		addSpecies("cluster_builder",msi.gaml.extensions.cluster_builder.ClusterBuilder.class,new IAgentConstructor() {
			@Override
			public IAgent createOneAgent(ISimulation sim,msi.gama.metamodel.population.IPopulation manager)  { 
 return new msi.gaml.extensions.cluster_builder.ClusterBuilder(sim, manager);}});

		addSpecies("world_species",WorldAgent.class,new IAgentConstructor() {
			@Override
			public IAgent createOneAgent(ISimulation sim,msi.gama.metamodel.population.IPopulation manager)  { 
 return new WorldAgent(sim, manager);}});

		addSpecies("multicriteria_analyzer",msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer.class,new IAgentConstructor() {
			@Override
			public IAgent createOneAgent(ISimulation sim,msi.gama.metamodel.population.IPopulation manager)  { 
 return new msi.gaml.extensions.multi_criteria.MulticriteriaAnalyzer(sim, manager);}});

		addSkill("grid",GridSkill.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new GridSkill();}});

		addSkill("situated",GeometricSkill.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new GeometricSkill();}});

		addSkill("user_first",UserFirstControlArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new UserFirstControlArchitecture();}});

		addSkill("user_last",UserLastControlArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new UserLastControlArchitecture();}});

		addSkill("user_only",UserOnlyControlArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new UserOnlyControlArchitecture();}});

		addSkill("global",WorldSkill.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new WorldSkill();}},"world_species");

		addSkill("fsm",FsmArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new FsmArchitecture();}});

		addSkill("probabilistic_tasks",ProbabilisticTasksArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new ProbabilisticTasksArchitecture();}});

		addSkill("weighted_tasks",WeightedTasksArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new WeightedTasksArchitecture();}});

		addSkill("sorted_tasks",SortedTasksArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new SortedTasksArchitecture();}});

		addSkill("moving",MovingSkill.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new MovingSkill();}});

		addSkill("graph_user",GraphSkill.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new GraphSkill();}});

		addSkill("reflex",ReflexArchitecture.class,new ISkillConstructor() { @Override public ISkill newInstance() {return new ReflexArchitecture();}});
	}
}