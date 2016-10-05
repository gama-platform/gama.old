package ummisco.gama.webgl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import msi.gama.kernel.experiment.ExperimentAgent;
import msi.gama.kernel.experiment.IExperimentPlan;
import msi.gama.kernel.experiment.IParameter;
import msi.gama.runtime.GAMA;

@SuppressWarnings({ "rawtypes" })
public class ParameterReceiver implements Runnable {

	private static ParameterReceiver instance = new ParameterReceiver();

	public static ParameterReceiver getInstance() {
		return instance;
	}

	public boolean finished = false;
	final int _OPEN = 0;
	final int _START = 1;
	final int _STEP = 2;
	final int _PAUSE = 3;
	final int _STOP = 4;
	final int _CLOSE = 5;
	final int _RELOAD = 6;
	final int _NEXT = 7;
	final int _BACK = 8;

	private ParameterReceiver() {
	}

	@Override
	public void run() {
		try {
			// TODO // get back the good listening port
			final Socket tcpReception = new Socket("localhost", 6001);
			final DataOutputStream os = new DataOutputStream(tcpReception.getOutputStream());
			final PrintWriter pw = new PrintWriter(os, false);
			final BufferedReader in = new BufferedReader(new InputStreamReader(tcpReception.getInputStream()));
			String temp = "";
			final String tempPath;
			final Gson gson = new Gson();
			while (!finished) {
				if (in.ready()) {
					temp = in.readLine();
					System.out.println("1 : " + temp);
					if (temp.length() == 1) {
						switch (Integer.parseInt(temp)) {
						case _START:
							GAMA.getExperiment().getController().userStart();
							break;
						case _STEP:
							GAMA.getExperiment().getController().userStep();
							break;
						case _PAUSE:
							GAMA.getExperiment().getController().directPause();
							break;
						case _STOP:
							GAMA.getExperiment().getController().directPause();
							break;
						case _RELOAD:
							GAMA.getExperiment().getController().userReload();
							break;
						}
					} else if (temp.contains("#")) {
						/*
						 * // Doesn't seem to work //tempPath = temp;
						 * 
						 * //Runnable earlyStartup = new Runnable() {
						 * //@Override //public void run() { // Let the advisor
						 * run its start-up code.
						 * //GAMA.closeFrontmostExperiment();
						 * //WorkspaceModelsManager.instance.
						 * openModelPassedAsArgument(tempPath); // // Cr�er un
						 * IFile pour runModel // ModelRunner.java
						 * 
						 * System.out.println("bien lanc�"); // } //};
						 * //earlyStartup.run(); // TODO launch the good
						 * experiment given in the path stored in temp variable
						 */

						final Map<String, IParameter> params = GAMA.getExperiment().getParameters();
						String result = "{\"parameters\":[";
						for (final String str : params.keySet()) {
							final IParameter param = params.get(str);
							final String name = param.getName();
							final String title = param.getTitle();
							final String category = param.getCategory();
							final String unitLabel = param.getUnitLabel(null);
							// Integer definitionOrder =
							// param.getDefinitionOrder();
							final String type = "" + param.getType();
							final String serialized = param.serialize(true);
							final Object initialValue = param.getInitialValue(null);
							final Number minValue = param.getMinValue(null);
							final Number maxValue = param.getMaxValue(null);
							final Number stepValue = param.getStepValue(null);
							final List amongValue = param.getAmongValue(null);

							result += "{\"type\":\"" + type + "\",\"id\":\"" + name + "\",\"title\":\"" + title + "\",";
							if (type.equals("int")) {
								if (minValue != null && maxValue != null) {
									result += "\"category\":\"slider\",\"initvalue\":" + initialValue + ",\"minvalue\":"
											+ minValue + ",\"maxvalue\":" + maxValue + "},";
								} else {
									result += "\"category\":\"input\",\"initvalue\":" + initialValue + "},";
								}
							} else if (type.equals("float")) {
								result += "\"initvalue\":" + initialValue + "},";
							} else if (type.equals("string")) {
								result += "\"initvalue\":\"" + initialValue + "\",\"values\":[";
								for (final Object tmp : amongValue) {
									if (tmp != initialValue) {
										result += "\"" + tmp + "\",";
									}
								}
								result = result.substring(0, result.length() - 1) + "]},";
							} else {
								System.out.println("Probl�me de type : " + type);
							}
						}
						result = result.substring(0, result.length() - 1) + "]}";
						System.out.println(result);
						pw.print(result + "@end");
						pw.flush();
					} else {
						final ParamUpdate param = gson.fromJson(temp, ParamUpdate.class);
						final IExperimentPlan exp = GAMA.getExperiment();
						ExperimentAgent a = null;
						if (exp != null) {
							a = exp.getAgent();
							System.out.println(GAMA.getExperiment().getExperimentOutputs().getOutputs());
							System.out.println("avant : " + GAMA.getExperiment().getParameters().get(param.id)
									.value(a == null ? null : a.getScope()) + '\n');
							GAMA.getExperiment().getParameters().get(param.id).setValue(a == null ? null : a.getScope(),
									param.data);
							GAMA.getRuntimeScope().setAgentVarValue(GAMA.getSimulation(), param.id, param.data);
							// GAMA.getRuntimeScope().setGlobalVarValue(param.id,param.data);
							// GAMA.getSimulation().setDirectVarValue(Scope,
							// param.id, param.data);
							// Platform.getApplicationArgs();
							System.out.println("apr�s : " + GAMA.getExperiment().getParameters().get(param.id)
									.value(a == null ? null : a.getScope()) + '\n');
						}
					}
				}
			}
			in.close();
			os.close();
			tcpReception.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}