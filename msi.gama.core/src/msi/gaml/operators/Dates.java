package msi.gaml.operators;

import org.joda.time.Duration;

import msi.gama.precompiler.GamlAnnotations.doc;
import msi.gama.precompiler.GamlAnnotations.example;
import msi.gama.precompiler.GamlAnnotations.operator;
import msi.gama.precompiler.GamlAnnotations.usage;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.precompiler.IOperatorCategory;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.GamaDate;
import msi.gaml.types.IType;

public class Dates {
	
	@operator(value = { IKeyword.MINUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(usages = @usage(value = "if both operands are dates, returns the durations in second between from date2 to date1",
			examples = { @example(value = "date1 - date2", equals = "598") }))		
	public static double minusDate(final IScope scope, final GamaDate date1, final GamaDate date2)
		throws GamaRuntimeException {
		Duration duration = new Duration(date2,date1);
		return duration.getStandardSeconds();
		
	}
	
	@operator(value = { IKeyword.PLUS , "add_seconds"}, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date plus the given number as duration (in seconds)",
			examples = { @example(value = "date1 + 200") }))		
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(duration);
		return nd;
	}
	
	@operator(value = { IKeyword.PLUS }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds((int) duration);
		return nd;
	}
	
	@operator(value = { IKeyword.PLUS  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final int duration,final GamaDate date1)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(duration);
		return nd;
	}
	
	@operator(value = { IKeyword.PLUS  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Add a duration to a date")
	public static GamaDate plusDuration(final IScope scope, final double duration,final GamaDate date1 )
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds((int) duration);
		return nd;
	}
	
	@operator(value = {  IKeyword.MINUS}, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(usages = @usage(value = "if one of the operands is a date and the other a number, returns a date corresponding to the date minus the given number as duration (in seconds)",
			examples = { @example(value = "date1 - 200") }))		
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final int duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(- duration);
		return nd;
	}
	
	@operator(value = {  IKeyword.MINUS}, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc("Minus a duration from a date")
	public static GamaDate minusDuration(final IScope scope, final GamaDate date1, final double duration)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addSeconds(- (int) duration);
		return nd;
	}
	
	
	@operator(value = { IKeyword.PLUS}, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value="returns the resulting string from the addition of a date and a string")
	public static String ConcatainDate(final IScope scope, final GamaDate date1, final String text )
		throws GamaRuntimeException {
		return date1.toString() + text;
	}
	
	@operator(value = { "add_years"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of year to a date", examples = { @example(value = "date1 add_years 3") })
	public static GamaDate addYears(final IScope scope, final GamaDate date1, final int nbYears)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addYears(nbYears);
		return nd;
	}
	
	@operator(value = { "add_months"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of months to a date", examples = { @example(value = "date1 add_months 5") })
	public static GamaDate addMonths(final IScope scope, final GamaDate date1, final int nbMonths)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMonths(nbMonths);
		return nd;
	}
	
	@operator(value = { "add_weeks"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of weeks to a date", examples = { @example(value = "date1 add_weeks 15") })
	public static GamaDate addWeeks(final IScope scope, final GamaDate date1, final int nbWeeks)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addWeeks(nbWeeks);
		return nd;
	}
	
	@operator(value = { "add_days"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of days to a date", examples = { @example(value = "date1 add_days 20") })
	public static GamaDate addDays(final IScope scope, final GamaDate date1, final int nbDays)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addDays(nbDays);
		return nd;
	}
	
	@operator(value = { "add_hours"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of hours to a date", examples = { @example(value = "date1 add_hours 15") })
	public static GamaDate addHours(final IScope scope, final GamaDate date1, final int nbHours)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addHours(nbHours);
		return nd;
	}
	
	@operator(value = { "add_minutes"  }, content_type = IType.NONE, category = { IOperatorCategory.DATE })
	@doc(value = "Add a given number of minutes to a date", examples = { @example(value = "date1 add_minutes 5") })
	public static GamaDate addMinutes(final IScope scope, final GamaDate date1, final int nbMinutes)
		throws GamaRuntimeException {
		GamaDate nd= new GamaDate(date1);
		nd.addMinutes(nbMinutes);
		return nd;
	}

}
