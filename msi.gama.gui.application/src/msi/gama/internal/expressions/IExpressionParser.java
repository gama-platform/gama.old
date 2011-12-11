/*
 * GAMA - V1.4  http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2011
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2011
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen  (Batch, GeoTools & JTS), 2009-2011
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.expressions;

import java.util.*;
import msi.gama.interfaces.*;
import msi.gama.internal.compilation.IOperator;
import msi.gama.internal.types.TypePair;
import msi.gama.kernel.exceptions.GamlException;
import msi.gama.precompiler.GamlAnnotations.reserved;
import msi.gama.util.GamaMap;

/**
 * Written by drogoul Modified on 28 dï¿½c. 2010
 * 
 * @todo Description
 * 
 */
@reserved({ "nil", "each", "self", "myself", "their", "its", "her", "his",
// TMP (retro-compatibility):
	"world", "visible", "signal" })
public interface IExpressionParser {

	public static final String CLOSE_LIST = "]";
	public static final String OPEN_LIST = "[";
	public static final String OPEN_POINT = "{";
	public static final String CLOSE_POINT = "}";
	public static final String CLOSE_EXP = ")";
	public static final String OPEN_EXP = "(";
	public static final String COMMA = ",";
	public static final String DOT = ".";
	public static final String OF = "of";
	public static final String AS = "as";
	public static final String IS = "is";
	public static final String INTERNAL_POINT = "<->";
	public static final String FALSE = "false";
	public static final String TRUE = "true";
	public static final String NULL = "nil";
	public static final String EACH = "each";
	public static final String SELF = "self";
	public static final String THE = "the";
	public static final String THEIR = "their";
	public static final String ITS = "its";
	public static final String HER = "her";
	public static final String HIS = "his";
	public static final String MY = "my";
	public static final List<String> RESERVED = Arrays.asList(THE, FALSE, TRUE, NULL,
		ISymbol.MYSELF, MY, HIS, HER, THEIR, ITS);
	public static final List<String> IGNORED = Arrays.asList(THE, THEIR, HIS, ITS, HER);
	public static final Map<String, Map<IType, IOperator>> UNARIES = new GamaMap();
	public static final Map<String, Map<TypePair, IOperator>> BINARIES = new GamaMap();
	public static final Set<String> FUNCTIONS = new HashSet();
	public static final Set<String> ITERATORS = new HashSet();
	public static final Map<String, Short> BINARY_PRIORITIES = new HashMap();

	public abstract IExpression parse(final ExpressionDescription s,
		final IDescription parsingContext) throws GamlException;

	public abstract void setFactory(IExpressionFactory factory);
}