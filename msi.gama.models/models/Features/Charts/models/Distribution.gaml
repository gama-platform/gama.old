/**
* Name: Distribution Examples
* Author: Philippe Caillou
* Description: A demonstration of charts using the distribution of values in a list
* Tags: gui, chart
*/
model distribution


global
{
	map<string, list> testdistrib;
	list<float> totest;
	init
	{
		totest <- [1, 2, 4, 1, 2, 5, 10.0];
		testdistrib <- distribution_of(totest, 5);
		write (totest);
		write (testdistrib);
	}

	reflex update_distrib
	{
		add gauss(100, 100) to: totest;
		testdistrib <- distribution_of(totest, 10);
		write (testdistrib);
	}

}

experiment "Example of Distribution" type: gui
{
	output
	{
		display "Simple_Distribution"
		{
			chart "Distribution_simple" type: histogram
			{
				datalist list(testdistrib at "legend") value: list(testdistrib at "values");
			}

		}

	}

}
	
