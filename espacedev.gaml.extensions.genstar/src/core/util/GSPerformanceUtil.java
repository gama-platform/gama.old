package core.util;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Should be called with the logger of the caller; for instance if the caller logger is "gospl.sampler.hierarchical", 
 * performance will be logged into "gospl.sampler.hierarchical.performance" with an INFO level of verbosity. 
 * 
 * @author Kevin Chapuis
 * @author Samuel Thiriot
 */
public class GSPerformanceUtil {

	private int stempCalls;
	private long latestStemp;
	private double cumulStemp;

	private boolean firstSyso;
	private String performanceTestDescription;

	private Logger logger;
	private Level level;
	
	private double objectif;
 
	private static final String END = "END OF PROCESS";

	private GSPerformanceUtil(Logger logger, Level level){
		resetStemp();
		this.logger = logger;
		this.level = level;
	}

	public GSPerformanceUtil(String performanceTestDescription, Logger logger) {
		this(LogManager.getLogger(logger.getName()+"."+GSPerformanceUtil.class.getSimpleName()), Level.INFO);
		this.performanceTestDescription = performanceTestDescription;
	}
	
	public GSPerformanceUtil(String performanceTestDescription, Logger logger, Level level) {
		this(LogManager.getLogger(logger.getName()+"."+GSPerformanceUtil.class.getSimpleName()), level);
		this.performanceTestDescription = performanceTestDescription;
	}
	
	public GSPerformanceUtil(String performanceTestDescription) {
		this(LogManager.getLogger(GSPerformanceUtil.class), Level.INFO);
		this.performanceTestDescription = performanceTestDescription;
	}

	public GSPerformanceUtil(String performanceTestDescription, Level level) {
		this(LogManager.getLogger(GSPerformanceUtil.class), level);
		this.performanceTestDescription = performanceTestDescription;
	}
	
	////////////////////////////////////////////////
	
	public String getStempPerformance(String message){
		long thisStemp = System.currentTimeMillis(); 
		double timer = (thisStemp - latestStemp)/1000d;
		if(latestStemp != 0l)	
			cumulStemp += timer;
		this.latestStemp = thisStemp;
		if(message == END) {
			double cumul = (double) Math.round(cumulStemp * 1000) / 1000;
			this.resetStemp();
			return END+" -> overall time = "+cumul+" s.";
		}
		return message+" -> "+timer+" s / "+((double) Math.round(cumulStemp * 1000) / 1000)+" s";
	}
	
	public String getStempPerformance(int stepFoward){
		stempCalls += stepFoward;
		return getStempPerformance(stepFoward == 0 ? "Init." : "Step "+stempCalls);
	}

	public String getStempPerformance(double proportion){
		if(proportion == 1.0)
			return getStempPerformance(END);
		return getStempPerformance(Math.round(Math.round(proportion*100))+"%");
	}
	
	public void sysoStempPerformance(int step, Object caller){
		sysoStempMessage(getStempPerformance(step), caller);
	}
	
	public void sysoStempPerformance(int step, String message, Object caller) {
		sysoStempMessage(getStempPerformance(step)+" | "+message, caller);
	}

	public void sysoStempPerformance(double proportion, Object caller){
		sysoStempMessage(getStempPerformance(proportion), caller);
	}
	
	public void sysoStempPerformance(double proportion, String message, Object caller){
		sysoStempMessage(getStempPerformance(proportion)+" | "+message, caller);
	}
	
	public void sysoStempPerformance(String message, Object caller){
		sysoStempMessage(getStempPerformance(message), caller);
	}
	
	// MESSAGE
	
	public void sysoStempMessage(String message){
		this.printLog(message, this.level);
	}
	
	public void sysoStempMessage(String message, Level level) {
		this.printLog(message, level);
	}
	
	private void sysoStempMessage(String message, Object caller){
		String callerString = caller.getClass().getSimpleName();
		if(caller.getClass().equals(String.class))
			callerString = caller.toString();
		
		if(firstSyso){
			this.printLog("\nMethod caller: "+callerString+
					"\n-------------------------\n"+
					performanceTestDescription+
					"\n-------------------------", null);
			firstSyso = false;
		}
		this.printLog(message, this.level);
	}
	
	public void sysoStempMessage(String message, Object... fillers) {
		//logger.printf(level, message, fillers);
		logger.trace(level + "->" + message);
	}
	
	// OBJECTIF PART (to compute advancement toward a goal)

	public void setObjectif(double objectif) {
		this.objectif = objectif;
	}
	
	public double getObjectif(){
		return objectif;
	}
	
	public void resetStemp(){
		this.resetStempCalls();
		firstSyso = true;
		performanceTestDescription = "no reason";
	}

	public void resetStempCalls(){
		stempCalls = 0;
		latestStemp = 0l;
		cumulStemp = 0d;
	}
	
	// LOGGER PART
	
	public Logger getLogger(){
		return logger;
	}
	
	private void printLog(String message, Level loglevel){
		if(loglevel == null)
			loglevel = this.level;
		if(loglevel.equals(Level.ERROR))
			logger.error(message);
		else if(loglevel.equals(Level.WARN))
			logger.warn(message);
		else if(loglevel.equals(Level.INFO))
			logger.info(message);
		else if(loglevel.equals(Level.DEBUG))
			logger.debug(message);
		else
			logger.trace(message);
	}

}
