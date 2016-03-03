package msi.gama.doc.websiteGen.utilClasses;

import java.util.ArrayList;
import java.util.List;

public class ScreenshotStructure {
	
	private List<DisplayParametersStructure> displayParameters = new ArrayList<DisplayParametersStructure>();
	public String ID;
	
	private class DisplayParametersStructure {
		
		public String DisplayName;
		public int CycleNumber;
		
		public DisplayParametersStructure(String displayName, int cycleNumber) {
			DisplayName = displayName;
			CycleNumber = cycleNumber;
		}
	}
	
	public ScreenshotStructure(String id) {
		ID = id;
	}
	
	public int getFinalStep() {
		int result=1;
		for (int displayId = 0 ; displayId < displayParameters.size(); displayId++) {
			if (result <= displayParameters.get(displayId).CycleNumber) {
				result = displayParameters.get(displayId).CycleNumber + 1;
			}
		}
		return result;
	}
	
	public boolean checkDisplayName(ArrayList<String> displayNames) {
		// return true if all the list of internal "display names" are also present in the list "displayNames".
		for (int displayIdx = 0 ; displayIdx < displayParameters.size() ; displayIdx++) {
			if (!displayNames.contains(displayParameters.get(displayIdx).DisplayName)) {
				System.err.println(displayParameters.get(displayIdx).DisplayName+" display is impossible to find...");
				return false;
			}
		}
		return true;
	}
	
	public String getXMLContent(String simNumber, String modelPath, String experiment) {
		String result = "";
		
		result += "  <Simulation id=\""+simNumber+"\" sourcePath=\""+modelPath+"\" finalStep=\""+getFinalStep()+"\" experiment=\""+experiment+"\">\n";
        result += "    <Outputs>\n";
        
        // browse all the displays
        for (int displayIdx = 0 ; displayIdx < displayParameters.size() ; displayIdx++) {
        	String display = displayParameters.get(displayIdx).DisplayName;
        	result += "      <Output id=\""+displayIdx+1+"\" name=\""+display+"\" framerate=\""+displayParameters.get(displayIdx).CycleNumber+"\" />\n";
        }
        
        result += "    <\\Outputs>\n";
        result += "  <\\Simulation>\n";
		
		return result;
	}
	
	public void addDisplay(String displayName) {
		addDisplay(displayName,10);
	}
	
	public void addDisplay(String displayName, int cycleNumber) {
		displayParameters.add(new DisplayParametersStructure(displayName,cycleNumber));
	}
}
