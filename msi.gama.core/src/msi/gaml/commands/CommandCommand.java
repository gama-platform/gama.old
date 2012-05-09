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
package msi.gaml.commands;

import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_args;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.*;
import msi.gaml.species.ISpecies;
import msi.gaml.types.IType;

/**
 * Written by drogoul Modified on 7 f√©vr. 2010
 * 
 * @todo Description
 * 
 */
@symbol(name = { IKeyword.COMMAND }, kind = ISymbolKind.SEQUENCE_COMMAND)
@inside(kinds = { ISymbolKind.EXPERIMENT })
@facets(value = { @facet(name = IKeyword.ACTION, type = IType.ID, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.WITH, type = IType.MAP_STR, optional = true) }, omissible = IKeyword.NAME)
@with_args
@with_sequence
public class CommandCommand extends AbstractCommandSequence implements ICommand.WithArgs {

	Arguments args;
	final String actionName;

	public CommandCommand(final IDescription desc) {
		super(desc);
		setName(desc.getName());
		actionName = this.getLiteral(IKeyword.ACTION);
	}

	@Override
	public void setFormalArgs(final Arguments args) {
		this.args = args;
	}

	@Override
	public Object privateExecuteIn(final IScope scope) throws GamaRuntimeException {
		if ( actionName == null ) { return super.privateExecuteIn(scope); }
		ISpecies context = scope.getAgentScope().getSpecies();
		ICommand.WithArgs executer = context.getAction(actionName);
		executer.setRuntimeArgs(args);
		Object result = executer.executeOn(scope);
		return result;
	}

	@Override
	public void setRuntimeArgs(final Arguments args) {}

	@Override
	public IType getReturnType() {
		CommandDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getReturnType();
	}

	@Override
	public IType getReturnContentType() {
		CommandDescription executer = description.getSpeciesContext().getAction(name);
		return executer.getReturnContentType();
	}

}
