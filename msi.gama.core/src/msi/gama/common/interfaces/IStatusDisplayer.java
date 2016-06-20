package msi.gama.common.interfaces;

import msi.gama.util.GamaColor;

public interface IStatusDisplayer {

	void resumeStatus();

	void waitStatus(String string);

	void informStatus(String string);

	void errorStatus(String message);

	void setSubStatusCompletion(double status);

	void setStatus(String msg, GamaColor color);

	void informStatus(String message, String icon);

	void setStatus(String msg, String icon);

	void beginSubStatus(String name);

	void endSubStatus(String name);

	void neutralStatus(String string);

}
