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
experiment "Binary back and forth" type: record format: "binary" compress: true parent: Base;

/**
 * This is the legacy format for recording simulations. There are a few incompatibilities with complex classes and data structures. Compression can be enabled to lower the memory usage.
 */
experiment "XML back and forth" type: record format: "xml" compress: true parent: Base;

/**
 * This is an intermediate format for recording simulations. Like "xml", there are a few incompatibilities with complex classes and data structures. Compression can be enabled to lower the memory usage.
 */
experiment "Json back and forth"  type: record format: "json" compress: true parent: Base;
