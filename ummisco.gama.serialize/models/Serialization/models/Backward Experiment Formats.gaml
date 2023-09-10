/**
* Name:  Demonstration of back and forth experiments on the Follow Weighted Network model
* Author:  Patrick Taillandier (modified by Benoit Gaudou & Alexis Drogoul)
* Description: Model illustrating the experiments typed `record` and the possibility to step them forward and backward. 
* Tags: record, graph, save
*/

model BackwardExperiments

import "Base Model.gaml"

/**
 * This is the fastest and smallest format for recording simulations. Compression can be enabled to lower the memory usage.
 */
experiment "Binary back and forth" record: true parent: Base;


