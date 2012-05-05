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
 * - Beno”t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gaml.compilation;

import static msi.gaml.compilation.GamaCompiler.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.precompiler.GamlAnnotations.action;
import msi.gama.precompiler.GamlAnnotations.args;
import msi.gama.precompiler.GamlAnnotations.getter;
import msi.gama.precompiler.GamlAnnotations.setter;
import msi.gama.precompiler.GamlAnnotations.var;
import msi.gama.precompiler.GamlAnnotations.vars;
import msi.gama.runtime.IScope;
import msi.gama.util.GamaList;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.*;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.skills.ISkill;
import msi.gaml.types.*;

/**
 * The Class GamlActionCompiler.
 * 
 * @author drogoul
 */

public class GamlCompiler {

	private static Map<Class, List<String>> skillMethodsToAdd = new HashMap();
	private static Map<Class, List<IDescription>> varDescriptions = new HashMap();
	private static Map<Class, List<IDescription>> commandDescriptions = new HashMap();
	private static Map<Class, Map<String, PrimitiveExecuter>> primitives = new HashMap();
	private static Map<Class, Map<String, IOperatorExecuter>> operators = new HashMap();
	private static Map<Class, Map<String, IOperatorExecuter>> binaryOperators = new HashMap();
	private static Map<Class, Map<String, IVarGetter>> getters = new HashMap();
	private static Map<Class, Map<String, IFieldGetter>> fieldGetters = new HashMap();
	private static Map<Class, Map<String, IVarSetter>> setters = new HashMap();
	private static Map<Class, IAgentConstructor> agentConstructors = new HashMap();
	private static Map<Class, ISymbolConstructor> symbolConstructors = new HashMap();
	private static Map<Class, ISkillConstructor> skillConstructors = new HashMap();
	private static Map<Class, ISkill> skillInstances = new HashMap();

	private static void addSkillMethod(final Class c, final String name) {
		List<String> names = skillMethodsToAdd.get(c);
		if ( names == null ) {
			names = new ArrayList();
			skillMethodsToAdd.put(c, names);
		}
		names.add(name);
	}

	public static List<String> getSkillMethods(final Class clazz) {
		if ( !skillMethodsToAdd.containsKey(clazz) ) {
			skillMethodsToAdd.put(clazz, new ArrayList());
			collectBuiltInAttributes(clazz);
		}
		return skillMethodsToAdd.get(clazz);
	}

	public static List<IDescription> getVarDescriptions(final Class clazz) {
		if ( !varDescriptions.containsKey(clazz) ) {
			// varDescriptions.put(clazz, new ArrayList());
			collectBuiltInAttributes(clazz);
		}
		return varDescriptions.get(clazz);
	}

	public static List<IDescription> getFieldDescriptions(final Class clazz) {
		List<Class> classes = collectImplementationClasses(clazz, Collections.EMPTY_SET);
		Map<String, IDescription> fieldsMap = new HashMap();
		for ( Class c : classes ) {
			List<IDescription> descriptions = getVarDescriptions(c);
			for ( IDescription desc : descriptions ) {
				fieldsMap.put(desc.getName(), desc);
			}
		}
		List<IDescription> descs = new GamaList(fieldsMap.values());
		// varDescriptions.put(clazz, descs);
		return descs;
	}

	public static List<IDescription> getCommandDescriptions(final Class clazz) {
		if ( !commandDescriptions.containsKey(clazz) ) {
			collectBuiltInActions(clazz);
		}
		return commandDescriptions.get(clazz);
	}

	private static void collectBuiltInAttributes(final Class c) {
		final Map<String, IDescription> varList = new HashMap();
		vars va = null;
		getter vp = null;
		setter vs = null;

		va = (vars) c.getAnnotation(vars.class);
		if ( va != null ) {
			for ( final var s : va.value() ) {
				List<String> ffacets =
					new GamaList(Arrays.asList(IKeyword.TYPE, s.type(), IKeyword.NAME, s.name(),
						IKeyword.CONST, s.constant() ? IKeyword.TRUE : IKeyword.FALSE));
				String depends = "";
				String[] dependencies = s.depends_on();
				if ( dependencies.length > 0 ) {
					for ( String string : dependencies ) {
						depends += string;
						depends += " ";
					}
					depends = depends.trim();
					ffacets.add(IKeyword.DEPENDS_ON);
					ffacets.add(depends);
				}
				String of = s.of();
				if ( !"".equals(of) ) {
					ffacets.add(IKeyword.OF);
					ffacets.add(of);
				}
				String init = s.init();
				if ( !"".equals(init) ) {
					ffacets.add(IKeyword.INIT);
					ffacets.add(init);
				}
				String[] facetArray = new String[ffacets.size()];
				facetArray = ffacets.toArray(facetArray);
				IDescription vd =
					DescriptionFactory.createDescription(s.type(), (IDescription) null, facetArray);
				if ( vd != null ) {
					varList.put(s.name(), vd);
				}
			}
		}
		final ArrayList<Method> methods = new ArrayList(Arrays.asList(c.getDeclaredMethods()));
		for ( final Method m : methods ) {
			String varName = null;

			vp = m.getAnnotation(getter.class);
			if ( vp != null ) {
				varName = vp.var();
				final IDescription var = varList.get(varName);
				if ( var != null ) {
					var.getFacets().putAsLabel(IKeyword.GETTER, m.getName());
					if ( vp.initializer() ) {
						var.getFacets().putAsLabel(IKeyword.INITER, m.getName());
					}
					addSkillMethod(c, m.getName());
					final Class r = m.getReturnType();
					var.getFacets().putAsLabel(IKeyword.TYPE, Types.get(r).toString());
				}
			}
			vs = m.getAnnotation(setter.class);
			if ( vs != null ) {
				final IDescription var = varList.get(vs.value());
				if ( var != null ) {
					var.getFacets().putAsLabel(IKeyword.SETTER, m.getName());
					addSkillMethod(c, m.getName());
				}
			}
		}

		varDescriptions.put(c, new GamaList(varList.values()));
	}

	private static void collectBuiltInActions(final Class c) {
		IDescription model = null;/* new ModelDescription(); */
		final HashMap<String, String> names = new HashMap();
		final HashMap<String, IType> types = new HashMap();
		final HashMap<String, ArrayList<String>> arguments = new HashMap();
		List<IDescription> commands = new ArrayList();

		Annotation va, vp = null;

		final ArrayList<Method> methods = new ArrayList(Arrays.asList(c.getDeclaredMethods()));
		for ( final Method m : methods ) {
			vp = m.getAnnotation(action.class);
			if ( vp != null ) {
				String name = ((action) vp).value();
				names.put(name, m.getName());
				types.put(name, Types.get(m.getReturnType()));
				arguments.put(name, new ArrayList());
				va = m.getAnnotation(args.class);
				if ( va != null ) {
					arguments.put(name, new ArrayList(Arrays.asList(((args) va).value())));
				}
			}
		}

		for ( final Map.Entry<String, String> entry : names.entrySet() ) {
			String n = entry.getKey();
			addSkillMethod(c, entry.getValue());
			// arguments.get(n).add(0, names.get(n));

			List<IDescription> args = new ArrayList();
			for ( String s : arguments.get(n) ) {
				IDescription arg =
					DescriptionFactory.createDescription(IKeyword.ARG, model, IKeyword.NAME, s);
				if ( arg != null ) {
					args.add(arg);
				}
			}
			IDescription prim =
				DescriptionFactory.createDescription(IKeyword.PRIMITIVE, model, args,
					IKeyword.NAME, n, IKeyword.TYPE, types.get(n).toString(), IKeyword.JAVA,
					names.get(n));
			if ( prim != null ) {
				commands.add(prim);
			}
		}
		commandDescriptions.put(c, commands);
	}

	public static IOperatorExecuter getOperator(final Class leftClass, final Class rightClass,
		final String name, final Class retClass, final boolean isStatic,
		final boolean isContextDependent, final boolean isLazyEvaluation) {
		Map<String, IOperatorExecuter> classExecuters = binaryOperators.get(leftClass);
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			binaryOperators.put(leftClass, classExecuters);
		}
		IOperatorExecuter helper = classExecuters.get(name + rightClass.getName());
		if ( helper == null ) {
			Class ret = GamaCompiler.checkPrimitiveClass(retClass);
			Class rc = isLazyEvaluation ? IExpression.class : rightClass;
			helper =
				isStatic ? isContextDependent ? buildStaticContextDependentBinary(leftClass, rc,
					name, ret) : buildStaticBinary(leftClass, rc, name, ret) : isContextDependent
					? buildDynamicContextDependentBinary(leftClass, rc, name, ret)
					: buildDynamicBinary(leftClass, rc, name, ret);
			classExecuters.put(concat(name, rightClass.getName()), helper);
		}
		return helper;
	}

	public static IOperatorExecuter getOperator(final Class childClass, final String name,
		final Class returnClass, final boolean isStatic, final boolean isContextDependent) {

		Map<String, IOperatorExecuter> classExecuters = operators.get(childClass);
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			operators.put(childClass, classExecuters);
		}
		IOperatorExecuter helper = classExecuters.get(name);

		if ( helper == null ) {
			Class ret = GamaCompiler.checkPrimitiveClass(returnClass);
			helper =
				isStatic ? isContextDependent ? buildStaticContextDependentUnary(childClass, name,
					ret) : buildStaticUnary(childClass, name, ret) : isContextDependent
					? buildDynamicContextDependentUnary(childClass, name, ret) : buildDynamicUnary(
						childClass, name, ret);
			classExecuters.put(name, helper);
		}
		return helper;
	}

	public static IPrimitiveExecuter getPrimitive(final Class originalClass, final String methodName) {
		Class methodClass = originalClass;
		Map<String, PrimitiveExecuter> classExecuters = primitives.get(methodClass);
		if ( !originalClass.isInterface() &&
			(classExecuters == null || !classExecuters.containsKey(methodName)) ) {
			try {
				while (!methodClass.isInterface() &&
					methodClass.getSuperclass().getMethod(methodName, new Class[] { IScope.class }) != null) {
					methodClass = methodClass.getSuperclass();
				}
			} catch (final Exception e) {}
		}
		if ( methodClass != originalClass ) {
			classExecuters = primitives.get(methodClass);
		}
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			primitives.put(methodClass, classExecuters);
		}
		PrimitiveExecuter methodExecuter = classExecuters.get(methodName);
		if ( methodExecuter == null ) {
			methodExecuter = buildPrimitive(methodClass, methodName);
			try {
				Class returnClass =
					methodClass.getMethod(methodName, new Class[] { IScope.class }).getReturnType();
				IType returnType = Types.get(returnClass);
				methodExecuter.setReturnType(returnType);
			} catch (Exception e) {
				methodExecuter.setReturnType(Types.NO_TYPE);
			}
			classExecuters.put(methodName, methodExecuter);
		}
		if ( methodClass != originalClass ) {
			classExecuters = primitives.get(originalClass);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				primitives.put(originalClass, classExecuters);
			}
			classExecuters.put(methodName, methodExecuter);

		}
		return methodExecuter;
	}

	public static IVarGetter getGetter(final Class originalClass, final String methodName,
		final Class returnClass) {
		Class childClass = originalClass;
		Map<String, IVarGetter> classExecuters = getters.get(childClass);
		if ( !originalClass.isInterface() &&
			(classExecuters == null || !classExecuters.containsKey(methodName)) ) {
			try {
				while (childClass.getSuperclass() != null &&
					(childClass.getSuperclass().getMethod(methodName, new Class[] {}) != null || childClass
						.getSuperclass().getMethod(methodName, new Class[] { IAgent.class }) != null)) {
					childClass = childClass.getSuperclass();
				}
			} catch (final Exception e) {}
		}
		if ( childClass != originalClass ) {
			classExecuters = getters.get(childClass);
		}
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			getters.put(childClass, classExecuters);
		}
		IVarGetter helper = classExecuters.get(methodName);
		if ( helper == null ) {
			boolean isDynamic = false;
			try {
				isDynamic = childClass.getMethod(methodName, new Class[] { IAgent.class }) != null;
			} catch (SecurityException e) {} catch (NoSuchMethodException e) {}
			helper = buildGetter(childClass, methodName, returnClass, isDynamic);
			classExecuters.put(methodName, helper);

		}
		if ( childClass != originalClass /* && originalClass != null */) {
			classExecuters = getters.get(originalClass);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				getters.put(originalClass, classExecuters);
			}
			classExecuters.put(methodName, helper);

		}
		// OutputManager.debug("Getter " + methodName + " asked for " +
		// originalClass.getSimpleName() +
		// "; generated for " + childClass.getSimpleName());
		return helper;
	}

	public static IFieldGetter getFieldGetter(final Class originalClass, final String methodName,
		final Class returnClass) {
		Class childClass = originalClass;

		Map<String, IFieldGetter> classExecuters = fieldGetters.get(childClass);
		if ( !originalClass.isInterface() &&
			(classExecuters == null || !classExecuters.containsKey(methodName)) ) {
			try {
				while (childClass.getSuperclass() != null &&
					childClass.getSuperclass().getMethod(methodName, new Class[] {}) != null) {
					childClass = childClass.getSuperclass();
				}
			} catch (final Exception e) {}
			if ( childClass != originalClass ) {}
		}
		classExecuters = fieldGetters.get(childClass);
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			fieldGetters.put(childClass, classExecuters);
		}
		IFieldGetter helper = classExecuters.get(methodName);
		if ( helper == null ) {
			// GUI.debug("Building the field getter for " + methodName + " of " +
			// childClass.getSimpleName());
			helper = buildFieldGetter(childClass, methodName, returnClass);
			classExecuters.put(methodName, helper);
		}
		if ( childClass != originalClass ) {
			classExecuters = fieldGetters.get(originalClass);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				fieldGetters.put(originalClass, classExecuters);
			}
			classExecuters.put(methodName, helper);

		}

		return helper;
	}

	public static IVarSetter getSetter(final Class originalClass, final String methodName,
		final Class paramClass) {
		Class childClass = originalClass;
		Map<String, IVarSetter> classExecuters = setters.get(childClass);
		if ( !originalClass.isInterface() &&
			(classExecuters == null || !classExecuters.containsKey(methodName)) ) {
			try {
				while (childClass.getSuperclass() != null &&
					(childClass.getSuperclass().getMethod(methodName, new Class[] { paramClass }) != null || childClass
						.getSuperclass().getMethod(methodName,
							new Class[] { IAgent.class, paramClass }) != null)) {
					childClass = childClass.getSuperclass();
				}
			} catch (final Exception e) {}
		}
		classExecuters = setters.get(childClass);
		if ( classExecuters == null ) {
			classExecuters = new HashMap();
			setters.put(childClass, classExecuters);
		}
		IVarSetter helper = classExecuters.get(methodName);
		if ( helper == null ) {
			boolean isDynamic = false;
			try {
				isDynamic =
					childClass.getMethod(methodName, new Class[] { IAgent.class, paramClass }) != null;
			} catch (SecurityException e) {} catch (NoSuchMethodException e) {}
			helper = buildSetter(childClass, methodName, paramClass, isDynamic);
			classExecuters.put(methodName, helper);
		}
		if ( childClass != originalClass ) {
			classExecuters = setters.get(originalClass);
			if ( classExecuters == null ) {
				classExecuters = new HashMap();
				setters.put(originalClass, classExecuters);
			}
			classExecuters.put(methodName, helper);

		}

		return helper;

	}

	public static synchronized IAgentConstructor getAgentConstructor(final Class javaBase) {
		IAgentConstructor constructor = agentConstructors.get(javaBase);
		if ( constructor == null ) {
			constructor = buildAgentConstructor(javaBase);
			if ( constructor == null ) { return null; }
			agentConstructors.put(javaBase, constructor);

		}
		return constructor;
	}

	public static ISkillConstructor getSkillConstructor(final Class skillClass) {
		ISkillConstructor constructor = skillConstructors.get(skillClass);
		if ( constructor == null ) {
			constructor = buildSkillConstructor(skillClass);
			if ( constructor == null ) { return null; }
			skillConstructors.put(skillClass, constructor);
		}
		return constructor;
	}

	public static ISkill getSkillInstanceFor(final Class skillClass) {
		ISkill skill = skillInstances.get(skillClass);
		if ( skill == null ) {
			ISkillConstructor constructor = getSkillConstructor(skillClass);
			if ( constructor == null ) { return null; }
			skill = constructor.newInstance();
			skillInstances.put(skillClass, skill);
		}
		return skill;

	}

	public static ISkillConstructor getSharedSkillConstructor(final Class skillClass) {
		return getSkillConstructor(skillClass);
	}

	public static ISymbolConstructor getSymbolConstructor(final Class instantiationClass) {
		ISymbolConstructor constructor = symbolConstructors.get(instantiationClass);
		if ( constructor == null ) {
			constructor = buildSymbolConstructor(instantiationClass);
			symbolConstructors.put(instantiationClass, constructor);
		}
		return constructor;
	}

	private static IAgentConstructor buildAgentConstructor(final Class javaBase) {
		code("public ", IAGENT, " createOneAgent(", ISIMULATION, " sim,", IAGENTMANAGER,
			" manager) " + EXCEPTION + " { \n return new ", javaBase.getCanonicalName(),
			"(sim, manager);}");
		return (IAgentConstructor) compiler.build(GamaCompiler.name(javaBase.getSimpleName()),
			null, IAgentConstructor.class, null);
	}

	private static ISkillConstructor buildSkillConstructor(final Class javaBase) {
		code("public ", ISKILL, " newInstance() { \n  return new ", javaBase.getCanonicalName(),
			"(); }");
		return (ISkillConstructor) compiler.build(GamaCompiler.name(javaBase.getSimpleName()),
			null, ISkillConstructor.class, null);
	}

	private static ISymbolConstructor buildSymbolConstructor(final Class javaBase) {
		code("public ", ISYMBOL, " create(", IDESCRIPTION, " desc) ", EXCEPTION,
			" { \n return new ", javaBase.getCanonicalName(), "(desc);}");
		return (ISymbolConstructor) compiler.build(name(javaBase.getSimpleName()), null,
			ISymbolConstructor.class, null);
	}

	private static PrimitiveExecuter buildPrimitive(final Class target, final String m) {
		code("public ", OBJECT, " execute(final ", ISKILL, " target, ", IAGENT, " agent, final ",
			ISCOPE, " scope) ", EXCEPTION, " { \n return ((", target.getName(), ") target).", m,
			"(scope);  }");
		return (PrimitiveExecuter) compiler.build(name(concat(target.getSimpleName(), "_", m)),
			PrimitiveExecuter.class, null, null);
	}

	private static IOperatorExecuter buildDynamicUnary(final Class t, final String m,
		final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope,final java.lang.Object target, final java.lang.Object right) ", EXCEPTION,
			"{ if (target == null) return ", returnWhenNull(ret), "; \nreturn ((", t.getName(),
			") target).", m, "();}");
		return (IOperatorExecuter) compiler.build(
			name(concat(m, "_", checkPrimitiveClass(t).getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildDynamicContextDependentUnary(final Class t,
		final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final ", ISCOPE,
			" scope,final java.lang.Object target, final java.lang.Object right)", EXCEPTION,
			" { if (target == null) return ", returnWhenNull(ret), "; \nreturn ((", t.getName(),
			") target).", m, "(scope);}");
		return (IOperatorExecuter) compiler.build(
			name(concat(m, "_", checkPrimitiveClass(t).getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildStaticUnary(final Class t, final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope, final java.lang.Object target, final java.lang.Object right) ", EXCEPTION,
			" { return ", m, "(", parameter(t, "target"), ");}");
		return (IOperatorExecuter) compiler.build(name(concat(m, "_", t.getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildStaticContextDependentUnary(final Class t,
		final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope, final java.lang.Object target, final java.lang.Object right) ", EXCEPTION,
			" { return " + m + "(scope,", parameter(t, "target"), ");}");
		return (IOperatorExecuter) compiler.build(name(concat(m, "_", t.getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildDynamicBinary(final Class l, final Class r,
		final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope,final java.lang.Object left,", " final java.lang.Object right) ", EXCEPTION,
			" { if (left == null) return ", returnWhenNull(ret), "; \nreturn ((", l.getName(),
			") left).", m, "(", parameter(r, "right"), ");}");
		return (IOperatorExecuter) compiler.build(
			name(concat(m, "_", l.getSimpleName(), "_", r.getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildDynamicContextDependentBinary(final Class l,
		final Class r, final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope,final java.lang.Object left, ", "final java.lang.Object right) ", EXCEPTION,
			" { if (left == null) return ", returnWhenNull(ret), "; \nreturn ((", l.getName(),
			") left).", m, "(scope,", parameter(r, "right"), ");}");
		return (IOperatorExecuter) compiler.build(
			name(m + "_" + l.getSimpleName() + "_" + r.getSimpleName()), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildStaticBinary(final Class l, final Class r,
		final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope, final java.lang.Object left, ", "final java.lang.Object right) ", EXCEPTION,
			" { return ", m, "(", parameter(l, "left"), ",", parameter(r, "right"), ");}");
		return (IOperatorExecuter) compiler.build(
			name(concat(m, "_", l.getSimpleName(), "_", r.getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IOperatorExecuter buildStaticContextDependentBinary(final Class l,
		final Class r, final String m, final Class ret) {
		code("public ", ret.getName(), " execute(final " + ISCOPE +
			" scope, final java.lang.Object left, final java.lang.Object right) ", EXCEPTION,
			" { return " + m + "(scope,", parameter(l, "left"), ",", parameter(r, "right"), ");}");
		return (IOperatorExecuter) compiler.build(
			name(concat(m, "_", l.getSimpleName(), "_", r.getSimpleName())), null,
			IOperatorExecuter.class, null);
	}

	private static IVarGetter buildGetter(final Class target, final String method, final Class r,
		final boolean dynamic) {
		Class ret = checkPrimitiveClass(r);
		code("public ", ret.getName(), " execute(final ", IAGENT, " agent, final ", ISKILL,
			" target) ", EXCEPTION, " { if (target == null) return ", returnWhenNull(ret),
			"; \n  return (" + ret.getName() + ")((", target.getName(), ") target).", method,
			dynamic ? "(agent);}" : "();}");
		// GUI.debug(sb.toString());
		return (IVarGetter) compiler.build(name(concat(method, "_", target.getSimpleName())), null,
			IVarGetter.class, null);
	}

	private static IFieldGetter buildFieldGetter(final Class target, final String method,
		final Class r) {
		Class ret = checkPrimitiveClass(r);
		String retName = ret.getName();
		code("public ", retName, " value(final ", IVALUE, " v) ", EXCEPTION,
			" { if (v == null) return ", returnWhenNull(ret), "; \n", retName, " result = ((",
			target.getName(), ") v).", method, "(); return result;}");
		return (IFieldGetter) compiler.build(name(concat(method, "_", target.getSimpleName())),
			null, IFieldGetter.class, null);
	}

	private static IVarSetter buildSetter(final Class target, final String method,
		final Class param, final boolean dynamic) {
		String pcName = checkPrimitiveClass(param).getName();
		code("public void execute(final ", IAGENT, " agent, final ", ISKILL, " target, final ",
			OBJECT, " arg) ", EXCEPTION, " { if (target == null) return;  \n((", target.getName(),
			") target).", method, "(", dynamic ? "agent, " : "", "(" + pcName + ")", "arg); }");
		return (IVarSetter) compiler.build(name(concat(method, "_", target.getSimpleName())), null,
			IVarSetter.class, null);
	}

	public static boolean isUnary(final Class[] args, final boolean isStatic) {
		if ( args.length == 0 && !isStatic ) { return true; }
		if ( args.length == 1 && !isStatic && args[0] == IScope.class ) { return true; }
		if ( args.length == 1 && isStatic ) { return true; }
		if ( args.length == 2 && isStatic && args[0] == IScope.class ) { return true; }
		return false;
	}

	public static boolean isContextual(final Class[] args, final boolean isStatic) {
		return args.length > 0 && args[0] == IScope.class;
	}

	public static boolean isLazy(final Class[] args, final boolean isStatic) {
		return args.length > 1 && IExpression.class.isAssignableFrom(args[args.length - 1]);
	}

	public static boolean equals(final Method classMethod, final Method interfaceMethod) {

		if ( !classMethod.getName().equals(interfaceMethod.getName()) ) { return false; }
		if ( !interfaceMethod.getReturnType().isAssignableFrom(classMethod.getReturnType()) ) { return false; }
		if ( !Arrays.equals(interfaceMethod.getParameterTypes(), classMethod.getParameterTypes()) ) { return false; }
		return true; // ...
	}

	public static void initFieldGetters(final IType theType) {
		List<IDescription> vars = getFieldDescriptions(theType.toClass());
		for ( IDescription v : vars ) {

			String n = v.getName();

			// GUI.debug("\tFIELDS : Initializing field" + n + " for " + name);
			IType type = v.getType();
			IType cType = v.getContentType();
			IFieldGetter g =
				getFieldGetter(theType.toClass(), v.getFacets().getLabel(IKeyword.GETTER),
					type.toClass());
			theType.addFieldGetter(n, new TypeFieldExpression(n, type, cType, g));
		}

	}

	/**
	 * @return
	 */
	public static Map<String, Class> getBuiltInSpeciesClasses() {
		return GamaBundleLoader.builtInSpeciesClasses;
	}

	/**
	 * @return
	 */
	public static Map<String, Class> getSkillClasses() {
		return GamaBundleLoader.skillClasses;
	}

	/**
	 * @param kind
	 * @return
	 */
	public static Class getFactoriesByKind(final int kind) {
		return GamaBundleLoader.FACTORIES_BY_KIND.get(kind);
	}

	/**
	 * @return
	 */
	public static Map<Integer, List<Class>> getClassesByKind() {
		return GamaBundleLoader.CLASSES_BY_KIND;
	}

}
