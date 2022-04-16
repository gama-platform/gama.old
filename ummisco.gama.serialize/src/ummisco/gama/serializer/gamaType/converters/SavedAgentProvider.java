package ummisco.gama.serializer.gamaType.converters;

import java.util.Stack;

import msi.gama.metamodel.agent.MutableSavedAgent;

public class SavedAgentProvider {

	
	private static Object lockInstance 	= new Object();
	private static Object lockStack 	= new Object();

	protected Stack<MutableSavedAgent> stack;
	
	protected SavedAgentProvider() {
		stack = new Stack<MutableSavedAgent>();
	}
	
	
	protected static SavedAgentProvider instance;
	
	public static SavedAgentProvider getInstance() {
		synchronized(lockInstance) {
			if(instance == null) {
				instance = new SavedAgentProvider();
			}
			return instance;			
		}
	}
	
	public static MutableSavedAgent getCurrent() {
		synchronized(lockStack) {
			return getInstance().stack.empty() ? null : getInstance().stack.lastElement();
		}
	}
	
	public static void push(MutableSavedAgent a) {
		synchronized(lockStack) {
			getInstance().stack.add(a);			
		}
	}
	
	public static MutableSavedAgent pop() {
		synchronized(lockStack){
			return getInstance().stack.pop();
		}
	}
	
	
}
