package msi.gaml.operators;

import org.joda.time.Duration;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.types.IType;

public class Dates {
	
	@operator(value = { "-" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Duration in hours between two dates in seconds")
	public static double minusDate(final IScope scope, final GamaDate date1, final GamaDate date2)
		throws GamaRuntimeException {
		Duration duration = new Duration(date2,date1);
		return duration.getStandardSeconds();
		
	}
	
	@operator(value = { "+" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		date1.addSeconds(duration);
		return date1;
	}
	
	@operator(value = { "+" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		date1.addSeconds((int) duration);
		return date1;
	}
	
	@operator(value = { "+" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final int duration,final GamaDate date1)
		throws GamaRuntimeException {
		date1.addSeconds(duration);
		return date1;
	}
	
	@operator(value = { "+" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final double duration,final GamaDate date1 )
		throws GamaRuntimeException {
		date1.addSeconds((int) duration);
		return date1;
	}
	
	@operator(value = { "-" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Remove a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		date1.addSeconds(- duration);
		return date1;
	}
	
	@operator(value = { "-" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Remove a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		date1.addSeconds(- (int) duration);
		return date1;
	}
	
	@operator(value = { "-" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Remove a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final int duration,final GamaDate date1)
		throws GamaRuntimeException {
		date1.addSeconds(-duration);
		return date1;
	}
	
	@operator(value = { "-" }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Remove a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final double duration,final GamaDate date1 )
		throws GamaRuntimeException {
		date1.addSeconds(- (int) duration);
		return date1;
	}


}
