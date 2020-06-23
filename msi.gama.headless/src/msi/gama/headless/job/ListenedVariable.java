package msi.gama.headless.job;

import msi.gama.headless.common.DataType;
import msi.gama.headless.job.ExperimentJob.OutputType;

public class ListenedVariable {

	public class NA {
		NA() {}

		@Override
		public String toString() {
			return "NA";
		}
	}

	String name;
	public int width;
	public int height;
	int frameRate;
	OutputType type;
	DataType dataType;
	Object value;
	long step;
	String path;
	// private boolean isNa;

	private Object setNaValue() {
		this.value = new NA();
		// this.isNa = true;
		return this.value;
	}

	public ListenedVariable(final String name, final int width, final int height, final int frameRate,
			final OutputType type, final String outputPath) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.frameRate = frameRate;
		this.type = type;
		this.path = outputPath;
		this.setNaValue();
	}

	public String getName() {
		return name;
	}

	public void setValue(final Object obj, final long st, final DataType typ) {
		// this.isNa = false;
		value = obj == null ? setNaValue() : obj;
		this.step = st;
		this.dataType = typ;
	}

	public void setValue(final Object obj, final long st) {
		setValue(obj, st, this.dataType);
	}

	public Object getValue() {
		return value;
	}

	public OutputType getType() {
		return type;
	}

	public DataType getDataType() {
		return dataType;
	}

	public String getPath() {
		return path;
	}
}