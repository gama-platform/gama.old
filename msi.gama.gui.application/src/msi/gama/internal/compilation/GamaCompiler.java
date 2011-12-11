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
 * - Benoît Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2011
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.internal.compilation;

import static msi.gama.internal.expressions.IExpressionParser.*;
import java.io.*;
import java.lang.reflect.*;
import java.security.ProtectionDomain;
import java.util.*;
import msi.gama.factories.DescriptionFactory;
import msi.gama.interfaces.*;
import msi.gama.internal.expressions.IExpressionParser;
import msi.gama.internal.types.*;
import msi.gama.kernel.exceptions.*;
import msi.gama.util.GamaMap;
import org.codehaus.commons.compiler.*;
import org.codehaus.janino.*;
import org.codehaus.janino.Scanner;
import org.codehaus.janino.util.ClassFile;

/**
 * Written by drogoul Modified on 28 déc. 2010
 * 
 * Provides access to the Java compiler of the Janino Project. Provides some utilities for dealing
 * with reflection.
 * 
 */
public class GamaCompiler {

	private static class CustomLoader extends ClassLoader {

		CustomLoader(final ClassLoader l) {
			super(l);
		}

		public Class getClass(final String name) {
			return super.defineClass(name, data, 0, data.length, pd);
		}
	}

	static class StrippedCompiler extends ClassBodyEvaluator {

		StrippedCompiler() {
			setParentClassLoader(loader);
		}

		@Override
		protected void assertNotCooked() {
			// Allows reusing the same compiler.
		}

		public Object build(final String className, final Class parent, final Class interf,
			final Object p) {
			// Simplified (stripped down !) version of the cook(Scanner) method, that directly
			// returns the result
			this.className = className;
			setExtendedClass(parent);
			Class[] interfaces;
			if ( interf == null ) {
				interfaces = emptyClassArray;
			} else {
				oneClassArray[0] = interf;
				interfaces = oneClassArray;
			}
			setImplementedInterfaces(interfaces);
			Scanner scanner;
			try {
				scanner = new Scanner(null, code);
			} catch (CompileException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			Java.CompilationUnit jcu = new Java.CompilationUnit(null);
			Java.ClassDeclaration cd;
			try {
				cd = this.addPackageMemberClassDeclaration(scanner.location(), jcu);
			} catch (CompileException e) {
				e.printStackTrace();
				return null;
			}
			Parser parser = new Parser(scanner);
			while (!scanner.peek().isEOF()) {
				try {
					parser.parseClassBodyDeclaration(cd);
				} catch (CompileException e) {
					e.printStackTrace();
					return null;
				} catch (IOException e) {
					e.printStackTrace();
					return null;
				}
			}
			ClassFile[] classFiles;
			try {
				classFiles = new UnitCompiler(jcu, iClassLoader).compileUnit(false, false, false);
			} catch (CompileException e) {
				e.printStackTrace();
				return null;
			}
			ClassFile cf = classFiles[0];
			data = cf.toByteArray();
			Class result = loader.getClass(className);
			if ( p == null ) {
				try {
					return result.newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
					return null;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					return null;
				}
			}
			oneClassArray[0] = p.getClass();
			Constructor c;
			try {
				c = result.getConstructor(oneClassArray);
			} catch (SecurityException e) {
				e.printStackTrace();
				return null;
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
				return null;
			}
			oneObjectArray[0] = p;
			try {
				return c.newInstance(oneObjectArray);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			} catch (InstantiationException e) {
				e.printStackTrace();
				return null;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			} catch (InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}

		}

		@Override
		protected Java.Type classToType(final Location location, final Class optionalClass) {
			// Necessary to redefine it as iClassLoader is used here
			if ( optionalClass == null ) { return null; }
			// this.classLoader.addAuxiliaryClass(optionalClass); ??
			IClass iClass;
			try {
				iClass = iClassLoader.loadIClass(Descriptor.fromClassName(optionalClass.getName()));
			} catch (ClassNotFoundException ex) {
				throw new JaninoRuntimeException("Loading IClass \"" + optionalClass.getName() +
					"\": " + ex);
			}
			return new Java.SimpleType(location, iClass);
		}

	}

	private static Map<Class, Set<Class>> allInterfaces = new HashMap();
	private static Map<Class, Set<Class>> allSuperclasses = new HashMap();
	static final CodeBuilder code = new CodeBuilder();
	private static byte[] data;
	private static final Class[] emptyClassArray = new Class[0];
	static final StrippedCompiler compiler = new StrippedCompiler();
	private static final CustomLoader loader =
		new CustomLoader(GamaCompiler.class.getClassLoader());
	private static IClassLoader iClassLoader = new ClassLoaderIClassLoader(loader);
	private static int id = 0;
	private static final Class[] oneClassArray = new Class[1];
	private static final Object[] oneObjectArray = new Object[1];
	private static ProtectionDomain pd = GamaCompiler.class.getProtectionDomain();

	private final static StringBuilder sb2 = new StringBuilder();
	public final static String CONSTRUCTOR = "Constructor";
	public final static String OPERATOR = "Operator";
	public final static String GETTER = "Getter";
	public final static String SETTER = "Setter";
	public final static String FIELD = "FieldGetter";
	public final static String PRIMITIVE = "Primitive";
	public final static String IAGENT = IAgent.class.getCanonicalName();
	public final static String IAGENTMANAGER = IPopulation.class.getCanonicalName();
	public final static String ISIMULATION = ISimulation.class.getCanonicalName();
	public final static String ISKILL = ISkill.class.getCanonicalName();
	public final static String ISYMBOL = ISymbol.class.getCanonicalName();
	public final static String IDESCRIPTION = IDescription.class.getCanonicalName();
	public final static String GAMLEXCEPTION = GamlException.class.getCanonicalName();
	public final static String ISCOPE = IScope.class.getCanonicalName();
	public final static String OBJECT = Object.class.getCanonicalName();
	public final static String IVALUE = IValue.class.getCanonicalName();
	public final static String EXCEPTION = "throws " +
		GamaRuntimeException.class.getCanonicalName();

	private static void addAllInterfaces(final Class clazz, final Set allInterfaces) {
		if ( clazz == null || !clazz.getCanonicalName().startsWith("msi") ) { return; }
		final Class[] interfaces = clazz.getInterfaces();
		allInterfaces.addAll(Arrays.asList(interfaces));
		addAllInterfaces(interfaces, allInterfaces);
		addAllInterfaces(clazz.getSuperclass(), allInterfaces);
	}

	static void addAllInterfaces(final Class[] clazzes, final Set allInterfaces) {
		if ( clazzes != null ) {
			for ( int i = 0; i < clazzes.length; i++ ) {
				addAllInterfaces(clazzes[i], allInterfaces);
			}
		}
	}

	public static final Set<Class> allInterfacesOf(final Class c) {
		if ( allInterfaces.containsKey(c) ) { return allInterfaces.get(c); }
		final Set<Class> result = new HashSet<Class>();
		addAllInterfaces(c, result);
		allInterfaces.put(c, result);
		return result;
	}

	static final Set<Class> allSuperclassesOf(final Class c) {
		if ( allSuperclasses.containsKey(c) ) { return allSuperclasses.get(c); }
		final HashSet<Class> result = new HashSet();
		if ( c == null ) { return result; }
		Class c2 = c.getSuperclass();
		while (c2 != null && c2.getCanonicalName().startsWith("msi")) {
			result.add(c2);
			c2 = c2.getSuperclass();
		}
		allSuperclasses.put(c, result);

		return result;
	}

	public static ScheduledAction buildAction(final Object target, final String method) {
		String name = name(method + "_" + target.getClass().getSimpleName());
		String tcName = target.getClass().getName();
		code("private final ", tcName, " t;\n public ", /* "Action_" + */name, "(final ", tcName,
			" o) { t = o; }\n public void execute(", ISCOPE, " scope) ", EXCEPTION, " { t.",
			method, "(scope);}");
		return (ScheduledAction) compiler.build(name, ScheduledAction.class, null, target);
	}

	public static Class checkPrimitiveClass(final Class c) {
		if ( !c.isPrimitive() ) { return c; }
		if ( c == int.class || c == short.class || c == long.class ) { return Integer.class; }
		if ( c == double.class || c == float.class ) { return Double.class; }
		if ( c == boolean.class ) { return Boolean.class; }
		return c;
	}

	public static/* synchronized */void code(final String ... tab) {
		code.reset();
		for ( int i = 0; i < tab.length; i++ ) {
			code.append(tab[i]);
		}
		code.prepare();
	}

	public static List<Class> collectImplementationClasses(final Class baseClass,
		final Set<Class> skillClasses) {
		Set<Class> classes = new HashSet();
		classes.add(baseClass);
		classes.addAll(skillClasses);
		classes.addAll(allInterfacesOf(baseClass));
		// final Set<Class> allInterfaces = allInterfacesOf(baseClass);
		// for ( Class c : allInterfaces ) {
		// if ( c.getPackage().getName().startsWith("msi") ) {
		// classes.add(c);
		// }
		// }
		for ( final Class classi : new ArrayList<Class>(classes) ) {
			classes.addAll(allSuperclassesOf(classi));
			// final Set<Class> allSuperclasses = allSuperclassesOf(classi);
			// for ( final Class sup : allSuperclasses ) {
			// if ( sup.getPackage().getName().startsWith("msi") ) {
			// classes.add(sup);
			// }
			// }
		}
		// classes.remove(Skill.class);
		classes.remove(ISkill.class);
		// classes.remove(IValue.class);
		classes.remove(IScope.class);
		// classes = new ArrayList(new HashSet(classes));
		final ArrayList<Class> classes2 = new ArrayList();
		for ( final Class c : classes ) {
			if ( !classes2.contains(c.getSuperclass()) ) {
				// classes.remove(c);
				classes2.add(0, c);
			} else {
				classes2.add(c);
			}
		}
		// OutputManager.debug("Implementation classes for " + baseClass.getSimpleName() + ": " +
		// classes);
		return classes2;
	}

	static String concat(final String ... tab) {
		sb2.setLength(0);
		for ( int i = 0; i < tab.length; i++ ) {
			sb2.append(tab[i]);
		}
		return sb2.toString();
	}

	public static String name(final String message) {
		return message + "_" + ++id;
	}

	public static String parameter(final Class c, final String par) {
		Class jc = checkPrimitiveClass(c);

		if ( jc == Double.class ) {
			return concat("(", par, " == null) ? Double.valueOf(0d) : ", par,
				" instanceof Double ? (Double) ", par, " : Double.valueOf(((Number)", par,
				").doubleValue())");
		} else if ( jc == Integer.class ) {
			return concat("(", par, " == null) ? Integer.valueOf(0) : ", par,
				" instanceof Integer ? (Integer) ", par, " : Integer.valueOf(((Number)", par,
				").intValue())");
		} else if ( jc == Boolean.class ) {
			return concat("(", par, " == null) ? Boolean.valueOf(false) : ((Boolean)", par, ")");
		} else {
			return concat("((", jc.getCanonicalName(), ")", par, ")");
		}
	}

	public static String returnWhenNull(final Class returnClass) {
		if ( returnClass == Double.class ) { return " Double.valueOf(0d) "; }
		if ( returnClass == Integer.class ) { return " Integer.valueOf(0) "; }
		if ( returnClass == Boolean.class ) { return " false "; }
		return concat(" (", returnClass.getCanonicalName(), ")", Types.class.getCanonicalName() +
			".coerce(null, (Object) null, " + Types.class.getCanonicalName() + ".get(",
			returnClass.getCanonicalName(), ".class), null) ");
	}

	public static void registerNewFunction(final String string) {
		if ( !BINARIES.containsKey(string) ) {
			BINARIES.put(string, new HashMap());
		}
		Map<TypePair, IOperator> existing = BINARIES.get(string);
		if ( !existing.containsKey(FunctionSignature) ) {
			IExpressionParser.FUNCTIONS.add(string);
			IOperator newFunct =
				DescriptionFactory.getModelFactory().getDefaultExpressionFactory()
					.createPrimitiveOperator(string);
			existing.put(FunctionSignature, newFunct);
		}
	}

	static TypePair FunctionSignature = new TypePair(Types.get(IType.AGENT), Types.get(IType.MAP));

	public static void registerFunction(final String string, final IDescription species) {
		registerNewFunction(string);
		IOperator newFunct =
			DescriptionFactory
				.getModelFactory()
				.getDefaultExpressionFactory()
				.copyPrimitiveOperatorForSpecies(BINARIES.get(string).get(FunctionSignature),
					species);

		BINARIES.get(string).put(new TypePair(species.getType(), Types.get(IType.MAP)), newFunct);
	}

	public static void registerNewOperator(final String key, final String m, final Class declClass,
		final Class retClass, final Class[] args, final boolean unary, final boolean contextual,
		final boolean lazy, final boolean iterator, final boolean isStatic, final short priority,
		final boolean canBeConst, final short type, final short contentType) {
		if ( unary ) {
			registerUnaryOperator(key, m, declClass, retClass, args, contextual, lazy, iterator,
				isStatic, canBeConst, type, contentType);
		} else {
			registerBinaryOperator(key, m, declClass, retClass, args, contextual, lazy, iterator,
				isStatic, priority, canBeConst, type, contentType);
		}

	}

	public static void registerBinaryOperator(final String keyword, final String mName,
		final Class declClass, final Class retClass, final Class[] args, final boolean contextual,
		final boolean lazy, final boolean iterator, final boolean isStatic, final short priority,
		final boolean canBeConst, final short type, final short contentType) {
		IOperatorExecuter helper;
		Class leftClass;
		Class rightClass;
		String methodName = mName;

		if ( isStatic ) {
			leftClass = contextual ? args[1] : args[0];
			rightClass = contextual ? args[2] : args[1];
			methodName = declClass.getCanonicalName() + DOT + methodName;
		} else {
			leftClass = declClass;
			rightClass = contextual ? args[1] : args[0];
		}
		helper =
			GamlCompiler.getOperator(leftClass, rightClass, methodName, retClass, isStatic,
				contextual, lazy);
		if ( helper == null ) { return; }

		IType leftType = Types.get(leftClass);
		IType rightType = Types.get(rightClass);
		IType returnType = Types.get(retClass);
		if ( !BINARIES.containsKey(keyword) ) {
			BINARIES.put(keyword, new GamaMap());
		}
		Map<TypePair, IOperator> map = BINARIES.get(keyword);
		TypePair signature = new TypePair(leftType, rightType);
		if ( !map.containsKey(signature) ) {
			IOperator exp =
				DescriptionFactory
					.getModelFactory()
					.getDefaultExpressionFactory()
					.createOperator(keyword, true, keyword.equals(OF) || keyword.equals(DOT),
						returnType, helper, canBeConst, type, contentType, lazy);
			// simulation will be set after
			exp.setName(keyword);
			map.put(signature, exp);
		}

		if ( iterator ) {
			IExpressionParser.ITERATORS.add(keyword);
		}
		IExpressionParser.BINARY_PRIORITIES.put(keyword, priority);

		// GUI.debug("Operator " + keyword + " registered for elements of types " + signature);
	}

	public static void registerUnaryOperator(final String keyword, final String mName,
		final Class declClass, final Class retClass, final Class[] args, final boolean contextual,
		final boolean lazy, final boolean iterator, final boolean isStatic,
		final boolean canBeConst, final short type, final short contentType) {
		IOperator result;
		IOperatorExecuter helper;
		Class childClass;
		String methodName = mName;

		if ( isStatic ) {
			childClass = contextual ? args[1] : args[0];
			methodName = declClass.getCanonicalName() + DOT + methodName;
		} else {
			childClass = declClass;
		}
		helper = GamlCompiler.getOperator(childClass, methodName, retClass, isStatic, contextual);
		if ( helper == null ) { return; }
		IType childType = Types.get(childClass);
		IType returnType = Types.get(retClass);
		if ( !(UNARIES.containsKey(keyword) && UNARIES.get(keyword).containsKey(childType)) ) {
			// GUI.debug("Registering " + keyword + " implemented by " + methodName + " on " +
			// declClass.getSimpleName() + " for arguments of type " + childType);
			result =
				DescriptionFactory
					.getModelFactory()
					.getDefaultExpressionFactory()
					.createOperator(keyword, false, false, returnType, helper, canBeConst, type,
						contentType, false);
			// simulation will be set after
			result.setName(keyword);
			if ( !UNARIES.containsKey(keyword) ) {
				UNARIES.put(keyword, new HashMap<IType, IOperator>());
			}
			UNARIES.get(keyword).put(childType, result);
		}
	}

	private static class CodeBuilder extends Reader {

		int length = 0, next = 0;
		final StringBuilder sb = new StringBuilder(3000);

		@Override
		public int read() {
			return next >= length ? -1 : sb.charAt(next++);
		}

		public void append(final String string) {
			sb.append(string);
		}

		@Override
		public int read(final char cbuf[], final int off, final int len) {
			if ( len == 0 ) { return 0; }
			if ( next >= length ) { return -1; }
			int n = Math.min(length - next, len);
			sb.getChars(next, next + n, cbuf, off);
			next += n;
			return n;
		}

		@Override
		public long skip(final long ns) {
			if ( next >= length ) { return 0; }
			long n = Math.min(length - next, ns);
			n = Math.max(-next, n);
			next += n;
			return n;
		}

		@Override
		public boolean ready() {
			return true;
		}

		public void prepare() {
			length = sb.length();
		}

		@Override
		public void reset() {
			sb.setLength(0);
			next = 0;
		}

		@Override
		public void close() {}
	}
}
