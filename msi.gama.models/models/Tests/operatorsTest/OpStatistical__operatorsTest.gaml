/**
 *  OpOpStatistical__operatorsTest
 *  Author: automatic generator
 *  Description: Unity Test of operators belonging to category OpStatistical__operatorsTest.
 */

model OpStatistical__operatorsTest

global {
	init {
		create testOpStatistical__operatorsTest number: 1;
		ask testOpStatistical__operatorsTest {do _step_;}
	}
}


	species testOpStatistical__operatorsTest {

	
		test buildOp {
			matrix([[1,2,3,4],[2,3,4,2]])
			build(matrix([[1,2,3,4],[2,3,4,2]]),"GLS")

		}
	
		test corROp {
			list X <- [1, 2, 3];
			list Y <- [1, 2, 4];
			unknown var2 <- corR(X, Y); 	// var2 equals 0.981980506061966
			assert var2 equals: 0.981980506061966; 

		}
	
		test dbscanOp {
			dbscan ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],10,2)

		}
	
		test distribution_ofOp {
			//map var0 <- distribution_of([1,1,2,12.5]); 	// var0 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])
			//map var1 <- distribution_of([1,1,2,12.5],10); 	// var1 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])
			//map var2 <- distribution_of([1,1,2,12.5]); 	// var2 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])

		}
	
		test distribution2d_ofOp {
			//map var0 <- distribution_of([1,1,2,12.5],10); 	// var0 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])
			//map var1 <- distribution_of([1,1,2,12.5],10); 	// var1 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])
			//map var2 <- distribution2d_of([1,1,2,12.5]); 	// var2 equals map(['values'::[2,1,0,0,0,0,1,0,0,0],'legend'::['[0.0:2.0]','[2.0:4.0]','[4.0:6.0]','[6.0:8.0]','[8.0:10.0]','[10.0:12.0]','[12.0:14.0]','[14.0:16.0]','[16.0:18.0]','[18.0:20.0]'],'parlist'::[1,0]])

		}
	
		test dtwOp {
			dtw([10.0,5.0,1.0, 3.0],[1.0,10.0,5.0,1.0], 2)
			dtw([10.0,5.0,1.0, 3.0],[1.0,10.0,5.0,1.0])

		}
	
		test frequency_ofOp {
			//map var0 <- [ag1, ag2, ag3, ag4] frequency_of each.size; 	// var0 equals the different sizes as keys and the number of agents of this size as values

		}
	
		test gamma_rndOp {
			gamma_rnd(10.0,5.0)

		}
	
		test geometric_meanOp {
			float var0 <- geometric_mean ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 4.962326343467649
			assert var0 equals: 4.962326343467649; 

		}
	
		test giniOp {
			float var0 <- gini([1.0, 0.5, 2.0]); 	// var0 equals the gini index computed

		}
	
		test harmonic_meanOp {
			float var0 <- harmonic_mean ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 4.804159445407279
			assert var0 equals: 4.804159445407279; 

		}
	
		test hierarchical_clusteringOp {
			//container var0 <- [ag1, ag2, ag3, ag4, ag5] hierarchical_clustering 20.0; 	// var0 equals for example, can return [[[ag1],[ag3]], [ag2], [[[ag4],[ag5]],[ag6]]

		}
	
		test kmeansOp {
			//kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2,10)
			kmeans ([[2,4,5], [3,8,2], [1,1,3], [4,3,4]],2)

		}
	
		test kurtosisOp {
			kurtosis ([1,2,3,4,5])

		}
	
		test maxOp {
			unknown var0 <- max ([100, 23.2, 34.5]); 	// var0 equals 100.0
			assert var0 equals: 100.0; 
			unknown var1 <- max([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}]); 	// var1 equals {9.0,1.0}
			assert var1 equals: {9.0,1.0}; 

		}
	
		test meanOp {
			unknown var0 <- mean ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 5.125 
			assert var0 equals: 5.125 ; 

		}
	
		test mean_deviationOp {
			float var0 <- mean_deviation ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 1.125
			assert var0 equals: 1.125; 

		}
	
		test meanROp {
			list<int> X <- [2, 3, 1];
			int var1 <- meanR(X); 	// var1 equals 2
			assert var1 equals: 2; 

		}
	
		test medianOp {
			unknown var0 <- median ([4.5, 3.5, 5.5, 3.4, 7.0]); 	// var0 equals 5.0
			assert var0 equals: 5.0; 

		}
	
		test minOp {
			unknown var0 <- min ([100, 23.2, 34.5]); 	// var0 equals 23.2
			assert var0 equals: 23.2; 

		}
	
		test moranOp {
			float var0 <- moran([1.0, 0.5, 2.0], weight_matrix); 	// var0 equals the Moran index computed

		}
	
		test mulOp {
			unknown var0 <- mul ([100, 23.2, 34.5]); 	// var0 equals 80040.0
			assert var0 equals: 80040.0; 

		}
	
		test predictOp {
			predict(my_regression, [1,2,3]

		}
	
		test productOp {

		}
	
		test R_correlationOp {

		}
	
		test R_meanOp {

		}
	
		test simple_clustering_by_distanceOp {
			//list<list<agent>> var0 <- [ag1, ag2, ag3, ag4, ag5] simpleClusteringByDistance 20.0; 	// var0 equals for example, can return [[ag1, ag3], [ag2], [ag4, ag5]]

		}
	
		test simple_clustering_by_envelope_distanceOp {

		}
	
		test skewnessOp {
			skewness ([1,2,3,4,5])

		}
	
		test splitOp {

		}
	
		test split_inOp {

		}
	
		test split_usingOp {

		}
	
		test standard_deviationOp {
			float var0 <- standard_deviation ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 1.2930100540985752
			assert var0 equals: 1.2930100540985752; 

		}
	
		test sumOp {
			int var0 <- sum ([12,10,3]); 	// var0 equals 25
			assert var0 equals: 25; 
			unknown var1 <- sum([{1.0,3.0},{3.0,5.0},{9.0,1.0},{7.0,8.0}]); 	// var1 equals {20.0,17.0}
			assert var1 equals: {20.0,17.0}; 

		}
	
		test varianceOp {
			float var0 <- variance ([4.5, 3.5, 5.5, 7.0]); 	// var0 equals 1.671875
			assert var0 equals: 1.671875; 

		}
	
	}


experiment testOpStatistical__operatorsTestExp type: gui {}	
	