/*******************************************************************************************************
 *
 * SimulationLocal.java, in msi.gama.core, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package msi.gama.runtime.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import msi.gama.kernel.experiment.ITopLevelAgent;
import msi.gama.runtime.GAMA;
import msi.gama.runtime.IScope;

/**
 * Identical to ThreadLocal but for simulations. Allows to provide simulation-local variables (actually experiment- AND
 * simulation-local variables)
 *
 * @author drogoul
 *
 */
public class SimulationLocal<T> {

	/**
	 * The Class SuppliedSimulationLocal.
	 *
	 * @param <T> the generic type
	 */
	static final class SuppliedSimulationLocal<T> extends SimulationLocal<T> {

		/** The supplier. */
		private final GAMA.InScope<? extends T> supplier;

		/**
		 * Instantiates a new supplied simulation local.
		 *
		 * @param supplier the supplier
		 */
		SuppliedSimulationLocal(final GAMA.InScope<? extends T> supplier) {
			this.supplier = Objects.requireNonNull(supplier);
		}

		@Override
		protected T initialValue(final IScope scope) {
			return supplier.run(scope);
		}
	}

	/** The Constant SIMULATION_LOCAL_MAP. */
	private static final String SIMULATION_LOCAL_MAP = "**simulation_local_map**";

	/**
	 * Returns the current simulation/experiment "initial value" for this variable. This method will be invoked the
	 * first time an agent accesses the variable with the {@link #get(IScope)} method, unless the thread previously
	 * invoked the {@link #set(IScope,T)} method, in which case the {@code initialValue} method will not be invoked for
	 * the thread. Normally, this method is invoked at most once per simulation or experiment, but it may be invoked
	 * again in case of subsequent invocations of {@link #remove} followed by {@link #get}.
	 *
	 * <p>
	 * This implementation simply returns {@code null}; if the programmer desires an initial value other than
	 * {@code null}, {@code SimulationLocal} must be subclassed, and this method overridden. Typically, an anonymous
	 * inner class will be used.
	 *
	 * @return the initial value for this simulation-local
	 */
	protected T initialValue(final IScope scope) {
		return null;
	}

	/**
	 * Gets the root of.
	 *
	 * @param scope the scope
	 * @return the root of
	 */
	private ITopLevelAgent getRootOf(final IScope scope) {
		if (scope == null) return null;
		return scope.getRoot();
	}

	/**
	 * Creates a simulation local variable. The initial value of the variable is determined by invoking the {@code get}
	 * method on the {@code Supplier}.
	 *
	 * @param <S>
	 *            the type of the thread local's value
	 * @param supplier
	 *            the supplier to be used to determine the initial value
	 * @return a new simulation-local variable
	 * @throws NullPointerException
	 *             if the specified supplier is null
	 */
	public static <S> SimulationLocal<S> withInitial(final GAMA.InScope<S> supplier) {
		return new SuppliedSimulationLocal<>(supplier);
	}

	/**
	 * Returns the value in the current simulation/experiment copy of this variable. If the variable has no value for
	 * the current simulation/experiment, it is first initialized to the value returned by an invocation of the
	 * {@link #initialValue} method.
	 *
	 * @return the current simulation/experiment value of this thread-local
	 */
	public T get(final IScope scope) {
		ITopLevelAgent sim = getRootOf(scope);
		if (sim == null) return null;
		Map<SimulationLocal<T>, T> map = getMap(sim);
		if (map != null && map.containsKey(this)) return map.get(this);
		return setInitialValue(scope);
	}

	/**
	 * Gets the map.
	 *
	 * @param sim the sim
	 * @return the map
	 */
	@SuppressWarnings ("unchecked")
	private Map<SimulationLocal<T>, T> getMap(final ITopLevelAgent sim) {
		return (Map<SimulationLocal<T>, T>) sim.getAttribute(SIMULATION_LOCAL_MAP);
	}

	/**
	 * Variant of set() to establish initialValue. Used instead of set() in case user has overridden the set() method.
	 *
	 * @return the initial value
	 */
	private T setInitialValue(final IScope scope) {
		T value = initialValue(scope);
		ITopLevelAgent sim = getRootOf(scope);
		if (sim == null) return null;
		Map<SimulationLocal<T>, T> map = getMap(sim);
		if (map != null) {
			map.put(this, value);
		} else {
			createMap(sim, value);
		}
		return value;
	}

	/**
	 * Sets the current thread's copy of this thread-local variable to the specified value. Most subclasses will have no
	 * need to override this method, relying solely on the {@link #initialValue} method to set the values of
	 * thread-locals.
	 *
	 * @param value
	 *            the value to be stored in the current thread's copy of this thread-local.
	 */
	public void set(final IScope scope, final T value) {
		ITopLevelAgent sim = getRootOf(scope);
		if (sim == null) return;
		Map<SimulationLocal<T>, T> map = getMap(sim);
		if (map != null) {
			map.put(this, value);
		} else {
			createMap(sim, value);
		}
	}

	/**
	 * Creates the map.
	 *
	 * @param sim the sim
	 * @param value the value
	 */
	private void createMap(final ITopLevelAgent sim, final T value) {
		Map<SimulationLocal<T>, T> map = new HashMap<>();
		sim.setAttribute(SIMULATION_LOCAL_MAP, map);
		map.put(this, value);

	}

	/**
	 * Returns {@code true} if there is a value in the current simulation copy of this thread-local variable
	 *
	 * @return {@code true} if current simulation has associated value in this simulation-local variable; {@code false}
	 *         if not
	 */
	public boolean isPresent(final IScope scope) {
		ITopLevelAgent sim = getRootOf(scope);
		if (sim == null) return false;
		Map<SimulationLocal<T>, T> map = getMap(sim);
		return map != null && map.containsKey(this);
	}

	/**
	 * Removes the current simulation value for this simulation-local variable. If this simulation-local variable is
	 * subsequently {@linkplain #get read} by the current simulation, its value will be reinitialized by invoking its
	 * {@link #initialValue} method, unless its value is {@linkplain #set set} by the current simulation in the interim.
	 * This may result in multiple invocations of the {@code initialValue} method in the current simulation.
	 *
	 */
	public void remove(final IScope scope) {
		ITopLevelAgent sim = getRootOf(scope);
		if (sim == null) return;
		Map<SimulationLocal<T>, T> map = getMap(sim);
		if (map != null) { map.remove(this); }
	}

}