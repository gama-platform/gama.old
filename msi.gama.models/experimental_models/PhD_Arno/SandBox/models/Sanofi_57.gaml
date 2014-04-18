/* sanofi57
 *  Author: thomas
 *  Description: 
 */

model sanofi16TS

/* Insert your model definition here */



global {
	
//  ACTIONS
	int actions_number <- 6 step: 1;
	int actions_done <- 0;
	
//	AMAS
	float amas_percentage <- 0.1 step: 0.1;
	float cells_amas <- 9.0 step: 1.0;
	int amas_number <- 0;
	float total_cells_on_microcarriers <- 0.0;
	float max_cells_on_microcarrier <- 0.0;
	
//	CELLULES SUR MICROPORTEURS	
	float total_replicant_cells <- 0.0;
	float total_latency_cells <-0.0;
	
//	CUBE OBSERVE	
	float dimension_si <- 1.0*10^(-3);
	float dimension <- dimension_si*(10^5);
	geometry shape <- rectangle(dimension+2, dimension+2);
	
//	RAYONS
	float cell_radius <- 1.0 step:1 ;
	float microcarrier_radius <- 10.0 step: 2;
	float amas_radius <- (cells_amas^(1/3))*cell_radius ;
	float virus_radius <- 0.3 ;
	
	
//	NOMBRE D'AGENTS
	int cells_number <- cells0;
	int microcarriers_number <- microcarriers_0;

//	VIRUS
	int initial_virus_number <- 100 step: 200;
	int virus_number <- 0 ;
	float cytopath <- 0.01 step: 0.1 max: 1.0;
	
//	TEMPS	
	float step <- 1.0 ;
	float time <- 0.0 ;
	
	int timeRefresh_charts <- 60 step: 30;
	int timeRefresh_view <- 1 step: 30;


	float Htime_mitosis <- 24.0 step: 12;
	float time_mitosis <- Htime_mitosis*3600/step ;
	float Htime_latence <- 5.0 step: 1;
	float time_latence <- Htime_latence*3600/step ;
	
	float Htime_injection1 <- 24.0 step: 12.0;
	float time_injection1 <- Htime_injection1*3600;
	float Htime_injection2 <- 48.0 step: 12.0;
	float time_injection2 <- Htime_injection2*3600;
	float Htime_injection3 <- 72.0 step: 12.0;
	float time_injection3 <- Htime_injection3*3600;
	float Htime_injection4 <- 96.0 step: 12.0;
	float time_injection4 <- Htime_injection4*3600;
	float Htime_injection5 <- 120.0 step: 12.0;
	float time_injection5 <- Htime_injection5*3600;
	float Htime_injection6 <- 144.0 step: 12.0;
	float time_injection6 <- Htime_injection6*3600;
	float Htime_injection7 <- 300.0 step: 12.0;
	float time_injection7 <- Htime_injection7*3600;
	float Htime_injection8 <- 300.0 step: 12.0;
	float time_injection8 <- Htime_injection8*3600;
	float Htime_injection9 <- 300.0 step: 12.0;
	float time_injection9 <- Htime_injection9*3600;
	float Htime_injection10 <- 300.0 step: 12.0;
	float time_injection10 <- Htime_injection10*3600;
	float Htime_injection11 <- 400.0 step: 12.0;
	float time_injection11 <- Htime_injection11*3600;
	float Htime_injection12 <- 400.0 step: 12.0;
	float time_injection12 <- Htime_injection12*3600;
	float Htime_injection13 <- 400.0 step: 12.0;
	float time_injection13 <- Htime_injection13*3600;
	float Htime_injection14 <- 400.0 step: 12.0;
	float time_injection14 <- Htime_injection14*3600;
	float Htime_injection15 <- 400.0 step: 12.0;
	float time_injection15 <- Htime_injection15*3600;
	float Htime_injection16 <- 400.0 step: 12.0;
	float time_injection16 <- Htime_injection16*3600;
	float Htime_injection17 <- 400.0 step: 12.0;
	float time_injection17 <- Htime_injection17*3600;
	float Htime_injection18 <- 400.0 step: 12.0;
	float time_injection18 <- Htime_injection18*3600;
	float Htime_injection19 <- 400.0 step: 12.0;
	float time_injection19 <- Htime_injection19*3600;
	float Htime_injection20 <- 400.0 step: 12.0;
	float time_injection20 <- Htime_injection20*3600;
	
	float Htime_infection <- 1200.0 step: 12;
	float time_infection <- Htime_infection*3600;
	
//	PERFUSION
	bool perfusion_continue <- false ; /*paramètre*/

	
	
	//	FACTEURS DE CONVERSION
	float concentration_massique_MP <- 4.3*(10^6) ; /*MP/g */
	int surface_massique_MP <- 4400 ; /* cm²/g */

	
	//concentrations au temps t
	float gln_t ;
	float glu_t ;
	float glc_t <- glc_0 ;
	float lac_t <- lac_0 ;
	float NH4_t ;
	float Na_t ;
	
	
	//concentrations et volume initiaux
		
	float volume0 <- 0.002 step: 0.001;
	float microcarriers_0_gl <- 2.5 ; /*g/L */		
	int microcarriers_0 <- round(microcarriers_0_gl*concentration_massique_MP*(dimension_si^3)*(10^3)); /* *10³ pour un volume en litres  */
	float cells0_mL <- cells0_microL*(10^3); /*cellules par mL */
	float cells0_microL <- 2.2*(10^2) step: 2.0*10;
	int cells0 <- round(cells0_mL*(dimension_si^3)*(10^6));
	float gln_0 ;
	float glu_0 ;
	float glc_0 <- 20.0;
	float lac_0 <-0.0;
	float NH4_0 ;
	float Na_0 ;
	
	bool passagex ;
	float volumex <- volume1 ;
	float volumew ;	//volumew: volume précédent
	float pourcentagex ;
	int microcarriersx ;
	float gln_x ;
	float glu_x ;
	float glc_x ;
	float lac_x ;
	float NH4_x ;
	float Na_x ;
	
	bool passage1 <- true;
	float volume1 <- 0.002 step: 0.001;	//pourcentage de solution 0
	float pourcentage1 <- 0.5;
	float microcarriers1_gl <- 2.5 ;
	int microcarriers1  <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_1 ;
	float glu_1 ;
	float glc_1 <-20.0;
	float lac_1 <-0.0 ;
	float NH4_1 ;
	float Na_1 ;
	
	bool passage2 <- true;
	float volume2 <- 0.007 step:0.001 ;	
	float pourcentage2 <- 0.5;
	float microcarriers2_gl <- 2.5;
	int microcarriers2 <- round(microcarriers2_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_2 ;
	float glu_2 ;
	float glc_2 <- 20.0;
	float lac_2 <-0.0 ;
	float NH4_2 ;
	float Na_2 ;
	
	bool passage3 <- true;
	float volume3 <- 0.028 step: 0.001;	
	float pourcentage3 <- 0.5;
	float microcarriers3_gl <- 2.5;
	int microcarriers3 <- round(microcarriers3_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_3 ;
	float glu_3 ;
	float glc_3 <-20.0;
	float lac_3 <-0.0 ;
	float NH4_3 ;
	float Na_3 ;
	
	bool passage4 <- true;
	float volume4 <- 0.028 step: 0.001;	
	float pourcentage4 <- 0.5;
	float microcarriers4_gl <- 2.5;
	int microcarriers4 <- round(microcarriers4_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_4 ;
	float glu_4 ;
	float glc_4 <-20.0;
	float lac_4 <-0.0 ;
	float NH4_4 ;
	float Na_4 ;
	
	bool passage5 <- true;
	float volume5 <- 0.028 step: 0.001;	
	float pourcentage5 <- 0.5;
	float microcarriers5_gl <- 2.5;
	int microcarriers5 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_5 ;
	float glu_5 ;
	float glc_5 <-20.0;
	float lac_5 <-0.0 ;
	float NH4_5 ;
	float Na_5 ;
	
	bool passage6 <- true;
	float volume6 <- 0.028 step: 0.001;
	float pourcentage6 <- 0.5;
	float microcarriers6_gl <- 2.5 ;
	int microcarriers6 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_6 ;
	float glu_6 ;
	float glc_6 <-20.0;
	float lac_6 <-0.0 ;
	float NH4_6 ;
	float Na_6 ;
	
	bool passage7 <- true;
	float volume7 <- 0.028 step: 0.001;
	float pourcentage7 <- 0.5;
	float microcarriers7_gl <- 2.5 ;
	int microcarriers7 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_7 ;
	float glu_7 ;
	float glc_7 <-20.0;
	float lac_7 <-0.0 ;
	float NH4_7 ;
	float Na_7 ;
	
	bool passage8 <- true;
	float volume8 <- 0.028 step: 0.001;
	float pourcentage8 <- 0.5;
	float microcarriers8_gl <- 2.5 ;
	int microcarriers8 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_8 ;
	float glu_8 ;
	float glc_8 <-20.0;
	float lac_8 <-0.0 ;
	float NH4_8 ;
	float Na_8 ;
	
	bool passage9 <- true;
	float volume9 <- 0.028 step: 0.001;
	float pourcentage9 <- 0.5;
	float microcarriers9_gl <- 2.5 ;
	int microcarriers9 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_9 ;
	float glu_9 ;
	float glc_9 <-20.0;
	float lac_9 <-0.0 ;
	float NH4_9 ;
	float Na_9 ;
	
	bool passage10 <- true;
	float volume10 <- 0.028 step: 0.001;
	float pourcentage10 <- 0.5;
	float microcarriers10_gl <- 2.5 ;
	int microcarriers10 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_10 ;
	float glu_10 ;
	float glc_10 <-20.0;
	float lac_10 <-0.0 ;
	float NH4_10 ;
	float Na_10 ;
	
	bool passage11 <- true;
	float volume11 <- 0.028 step: 0.001;
	float pourcentage11 <- 0.5;
	float microcarriers11_gl <- 2.5 ;
	int microcarriers11 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_11 ;
	float glu_11 ;
	float glc_11 <-20.0;
	float lac_11 <-0.0 ;
	float NH4_11 ;
	float Na_11 ;
	
	bool passage12 <- true;
	float volume12 <- 0.028 step: 0.001;
	float pourcentage12 <- 0.5;
	float microcarriers12_gl <- 2.5 ;
	int microcarriers12 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_12 ;
	float glu_12 ;
	float glc_12 <-20.0;
	float lac_12 <-0.0 ;
	float NH4_12 ;
	float Na_12 ;
	
	bool passage13 <- true;
	float volume13 <- 0.028 step: 0.001;
	float pourcentage13 <- 0.5;
	float microcarriers13_gl <- 2.5 ;
	int microcarriers13 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_13 ;
	float glu_13 ;
	float glc_13 <-20.0;
	float lac_13 <-0.0 ;
	float NH4_13 ;
	float Na_13 ;
	
	bool passage14 <- true;
	float volume14 <- 0.028 step: 0.001;
	float pourcentage14 <- 0.5;
	float microcarriers14_gl <- 2.5 ;
	int microcarriers14 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_14 ;
	float glu_14 ;
	float glc_14 <-20.0;
	float lac_14 <-0.0 ;
	float NH4_14 ;
	float Na_14 ;
	
	bool passage15 <- true;
	float volume15 <- 0.028 step: 0.001;
	float pourcentage15 <- 0.5;
	float microcarriers15_gl <- 2.5 ;
	int microcarriers15 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_15 ;
	float glu_15 ;
	float glc_15 <-20.0;
	float lac_15 <-0.0 ;
	float NH4_15 ;
	float Na_15 ;
	
	bool passage16 <- true;
	float volume16 <- 0.028 step: 0.001;
	float pourcentage16 <- 0.5;
	float microcarriers16_gl <- 2.5 ;
	int microcarriers16 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_16 ;
	float glu_16 ;
	float glc_16 <-20.0;
	float lac_16 <-0.0 ;
	float NH4_16 ;
	float Na_16 ;
	
	bool passage17 <- true;
	float volume17 <- 0.028 step: 0.001;
	float pourcentage17 <- 0.5;
	float microcarriers17_gl <- 2.5 ;
	int microcarriers17 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_17 ;
	float glu_17 ;
	float glc_17 <-20.0;
	float lac_17 <-0.0 ;
	float NH4_17 ;
	float Na_17 ;
	
	bool passage18 <- true;
	float volume18 <- 0.028 step: 0.001;
	float pourcentage18 <- 0.5;
	float microcarriers18_gl <- 2.5 ;
	int microcarriers18 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_18 ;
	float glu_18 ;
	float glc_18 <-20.0;
	float lac_18 <-0.0 ;
	float NH4_18 ;
	float Na_18 ;
	
	bool passage19 <- true;
	float volume19 <- 0.028 step: 0.001;
	float pourcentage19 <- 0.5;
	float microcarriers19_gl <- 2.5 ;
	int microcarriers19 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_19 ;
	float glu_19 ;
	float glc_19 <-20.0;
	float lac_19 <-0.0 ;
	float NH4_19 ;
	float Na_19 ;
	
	bool passage20 <- true;
	float volume20 <- 0.028 step: 0.001;
	float pourcentage20 <- 0.5;
	float microcarriers20_gl <- 2.5 ;
	int microcarriers20 <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_20 ;
	float glu_20 ;
	float glc_20 <-20.0;
	float lac_20 <-0.0 ;
	float NH4_20 ;
	float Na_20 ;
		
	
	init {
		create cells number: cells_number{ 
//			location <- {rnd(dimension-2*cell_radius), rnd(dimension-2*cell_radius), rnd(dimension-2*cell_radius)};	
			location <- {rnd(dimension), rnd(dimension), rnd(dimension)};			
		}
		
		create microcarriers number: microcarriers_number{ 
			//location <- {rnd(dimension-microcarrier_radius), rnd(dimension-microcarrier_radius), rnd(dimension-microcarrier_radius)};
			location <- {rnd(dimension), rnd(dimension), rnd(dimension)};
			}
		
		loop tmp over: microcarriers{
			list<microcarriers> microcarriersNeighbours <- microcarriers select ((each distance_to tmp)< 2*tmp.microcarrier_cells_radius);
			int max_iter <-0;
			loop while: (length(microcarriersNeighbours)>1) and (max_iter <50) {
				tmp.location <- {rnd(dimension), rnd(dimension), rnd(dimension)};
				microcarriersNeighbours <- microcarriers select ((each distance_to tmp)< 2*tmp.microcarrier_cells_radius);
				max_iter <- max_iter +1;
				if (max_iter = 50){
					do debug message: "Warning: overlapping microcarriers: "+tmp + " and "+ microcarriersNeighbours;
				}
			}
		}
		


	
	}
	
	reflex update {
	microcarriers_number <- microcarriers count true;
    cells_number <- cells count true;
    amas_number <- amas count true;
    virus_number <- virus count true;
    
 	total_replicant_cells <- sum(microcarriers collect(each.replicant_cells_number));
    total_latency_cells <- sum(microcarriers collect(each.latency_cells_number));
    total_cells_on_microcarriers <- total_replicant_cells + total_latency_cells;
    max_cells_on_microcarrier <- max(microcarriers collect (each.cells_on_microcarrier));
    }
    
	reflex update_nutriments when: total_cells_on_microcarriers > 0 {
		lac_t <- lac_t+0.00001*total_cells_on_microcarriers;
		if perfusion_continue = false {
	    	glc_t <- glc_t/(1+0.00001*total_cells_on_microcarriers);
	        /*lac_t <- lac_t + 2*min ([total_cells_on_microcarriers*1/10^10, glc_t]);
	        glc_t <- glc_t - min ([total_cells_on_microcarriers*1/10^10, glc_t]) ;*/
	        }
		// si perfusion continue, concentration en glucose constante mais pas celle en lactate   
    }

	reflex infection1 when: time = time_infection {
		virus_number <- initial_virus_number ;
		create virus number: virus_number { 
			location <- {rnd(dimension), rnd(dimension), rnd(dimension)};	
		}
	}
	
	
	reflex actualise1 when: time<=time_injection1 and time >=(time_injection1-60) {
	 passagex <- passage1;
	 volumex <- volume1;
	 volumew <- volume0;	
	//pourcentage de solution 0
	 pourcentagex <- pourcentage1;
	int microcarriersx  <- round(microcarriers1_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_1;
	 glu_x <- glu_1;
	 glc_x <- glc_1;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_1;
	 Na_x <- Na_1;
	}
	
	reflex actualise2 when: time<=time_injection2 and time >=(time_injection2-60) {
	time_injection1 <- time_injection2;
	 passagex <- passage2;
	 volumex <- volume2;
	 volumew <- volume1;	
	
	 pourcentagex <- pourcentage2;
	int microcarriersx  <- round(microcarriers2_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_2;
	 glu_x <- glu_2;
	 glc_x <- glc_2;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_2;
	 Na_x <- Na_2;
	}
	
	reflex actualise3 when: time<=time_injection3 and time >=(time_injection3-60) {
	 passagex <- passage3;
	 volumex <- volume3;
	 volumew <- volume2;	
	
	 pourcentagex <- pourcentage3;
	int microcarriersx  <- round(microcarriers3_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_3;
	 glu_x <- glu_3;
	 glc_x <- glc_3;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_3;
	 Na_x <- Na_3;
	}
	
	reflex actualise4 when: time<=time_injection4 and time >=(time_injection4-60) {
	 passagex <- passage4;
	 volumex <- volume4;
	 volumew <- volume3;	
	
	 pourcentagex <- pourcentage4;
	int microcarriersx  <- round(microcarriers4_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_4;
	 glu_x <- glu_4;
	 glc_x <- glc_4;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_4;
	 Na_x <- Na_4;
	}
	
	reflex actualise5 when: time<=time_injection5 and time >=(time_injection5-60) {
	 passagex <- passage5;
	 volumex <- volume5;
	 volumew <- volume4;	
	
	 pourcentagex <- pourcentage5;
	int microcarriersx  <- round(microcarriers5_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_5;
	 glu_x <- glu_5;
	 glc_x <- glc_5;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_5;
	 Na_x <- Na_5;
	}
	
	reflex actualise6 when: time<=time_injection6 and time >=(time_injection6-60) {
	 passagex <- passage6;
	 volumex <- volume6;
	 volumew <- volume5;	
	
	 pourcentagex <- pourcentage6;
	int microcarriersx  <- round(microcarriers6_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_6;
	 glu_x <- glu_6;
	 glc_x <- glc_6;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_6;
	 Na_x <- Na_6;
	}
	
	reflex actualise7 when: time<=time_injection7 and time >=(time_injection7-60) {
	 passagex <- passage7;
	 volumex <- volume7;
	 volumew <- volume6;	
	
	 pourcentagex <- pourcentage7;
	int microcarriersx  <- round(microcarriers7_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_7;
	 glu_x <- glu_7;
	 glc_x <- glc_7;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_7;
	 Na_x <- Na_7;
	}
	
	reflex actualise8 when: time<=time_injection8 and time >=(time_injection8-60){
	 passagex <- passage8;
	 volumex <- volume8;
	 volumew <- volume7;	
	
	 pourcentagex <- pourcentage8;
	int microcarriersx  <- round(microcarriers8_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_8;
	 glu_x <- glu_8;
	 glc_x <- glc_8;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_8;
	 Na_x <- Na_8;
	}
	
	reflex actualise9 when: time<=time_injection9 and time >=(time_injection9-60) {
	 passagex <- passage9;
	 volumex <- volume9;
	 volumew <- volume8;	
	
	 pourcentagex <- pourcentage9;
	int microcarriersx  <- round(microcarriers9_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_9;
	 glu_x <- glu_9;
	 glc_x <- glc_9;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_9;
	 Na_x <- Na_9;
	}
	
	reflex actualise10 when: time<=time_injection10 and time >=(time_injection10-60) {
	 passagex <- passage10;
	 volumex <- volume10;
	 volumew <- volume9;	
	
	 pourcentagex <- pourcentage10;
	int microcarriersx  <- round(microcarriers10_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_10;
	 glu_x <- glu_10;
	 glc_x <- glc_10;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_10;
	 Na_x <- Na_10;
	}
	
	reflex actualise11 when: time<=time_injection11 and time >=(time_injection11-60) {
	 passagex <- passage11;
	 volumex <- volume11;
	 volumew <- volume10;	
	
	 pourcentagex <- pourcentage11;
	int microcarriersx  <- round(microcarriers11_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_11;
	 glu_x <- glu_11;
	 glc_x <- glc_11;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_11;
	 Na_x <- Na_11;
	}
	
	reflex actualise12 when: time<=time_injection12 and time >=(time_injection12-60) {
	 passagex <- passage12;
	 volumex <- volume12;
	 volumew <- volume11;	
	
	 pourcentagex <- pourcentage12;
	int microcarriersx  <- round(microcarriers12_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_12;
	 glu_x <- glu_12;
	 glc_x <- glc_12;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_12;
	 Na_x <- Na_12;
	}
	
	reflex actualise13 when: time<=time_injection13 and time >=(time_injection13-60) {
	 passagex <- passage13;
	 volumex <- volume13;
	 volumew <- volume12;	
	
	 pourcentagex <- pourcentage13;
	int microcarriersx  <- round(microcarriers13_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_13;
	 glu_x <- glu_13;
	 glc_x <- glc_13;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_13;
	 Na_x <- Na_13;
	}
	
	reflex actualise14 when: time<=time_injection14 and time >=(time_injection14-60) {
	time_injection1 <- time_injection14;
	 passagex <- passage14;
	float volumex <- volume14;
	float volumew <- volume13;	
	
	float pourcentagex <- pourcentage14;
	int microcarriersx  <- round(microcarriers14_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	float gln_x <- gln_14;
	float glu_x <- glu_14;
	float glc_x <- glc_14;
	float lac_x <-0.0 ;
	float NH4_x <- NH4_14;
	float Na_x <- Na_14;
	}
	
	reflex actualise15 when: time<=time_injection15 and time >=(time_injection15-60) {
	 passagex <- passage15;
	 volumex <- volume15;
	 volumew <- volume14;	
	
	 pourcentagex <- pourcentage15;
	int microcarriersx  <- round(microcarriers15_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_15;
	 glu_x <- glu_15;
	 glc_x <- glc_15;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_15;
	 Na_x <- Na_15;
	}
	
	reflex actualise16 when: time<=time_injection16 and time >=(time_injection16-60) {
	 passagex <- passage16;
	 volumex <- volume16;
	 volumew <- volume15;	
	
	 pourcentagex <- pourcentage16;
	int microcarriersx  <- round(microcarriers16_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_16;
	 glu_x <- glu_16;
	 glc_x <- glc_16;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_16;
	 Na_x <- Na_16;
	}
	
	reflex actualise17 when: time<=time_injection17 and time >=(time_injection17-60) {
	 passagex <- passage17;
	 volumex <- volume17;
	 volumew <- volume16;	
	
	 pourcentagex <- pourcentage17;
	int microcarriersx  <- round(microcarriers17_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_17;
	 glu_x <- glu_17;
	 glc_x <- glc_17;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_17;
	 Na_x <- Na_17;
	}
	
	reflex actualise18 when: time<=time_injection18 and time >=(time_injection18-60) {
	 passagex <- passage18;
	 volumex <- volume18;
	 volumew <- volume17;	
	
	 pourcentagex <- pourcentage18;
	int microcarriersx  <- round(microcarriers18_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_18;
	 glu_x <- glu_18;
	 glc_x <- glc_18;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_18;
	 Na_x <- Na_18;
	}
	
	reflex actualise19 when: time<=time_injection19 and time >=(time_injection9-60) {
	 passagex <- passage19;
	 volumex <- volume19;
	 volumew <- volume18;	
	
	 pourcentagex <- pourcentage19;
	int microcarriersx  <- round(microcarriers19_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_19;
	 glu_x <- glu_19;
	 glc_x <- glc_19;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_19;
	 Na_x <- Na_19;
	}
	
	reflex actualise20 when: time<=time_injection20 and time >=(time_injection20-60) {
	 passagex <- passage20;
	 volumex <- volume20;
	 volumew <- volume19;	
	
	 pourcentagex <- pourcentage20;
	int microcarriersx  <- round(microcarriers20_gl*concentration_massique_MP*(dimension_si^3)*(10^3));
	 gln_x <- gln_20;
	 glu_x <- glu_20;
	 glc_x <- glc_20;
	 lac_x <-0.0 ;
	 NH4_x <- NH4_20;
	 Na_x <- Na_20;
	}
	
	reflex passage when: time=time_injection1 or time=time_injection2 or time=time_injection3 or time=time_injection4 
						or time=time_injection5 or time=time_injection6 or time=time_injection7 or time=time_injection8 
						or time=time_injection9 or time=time_injection10 or time=time_injection11 or time=time_injection12 
						or time=time_injection13 or time=time_injection14 or time=time_injection15 or time=time_injection16 
						or time=time_injection17 or time=time_injection18 or time=time_injection19 or time=time_injection20  {
		if actions_number > actions_done {
		actions_done <- actions_done+1;
		
		
		//gln_t <- gln_t*pourcentagex*volumew/volumex + gln_x*(1-pourcentagex*volumew/volumex) ;
		//glu_t <- glu_t*pourcentagex*volumew/volumex + glu_x*(1-pourcentagex*volumew/volumex) ;
		if perfusion_continue = false {
			glc_t <- float(glc_t*pourcentagex*volumew/volumex + glc_x*(1-pourcentagex*volumew/volumex));
			}
		lac_t <- float(lac_t*pourcentagex*volumew/volumex + lac_x*(1-pourcentagex*volumew/volumex));
		
		//NH4_t <- NH4_t*pourcentagex*volumew/volumex + NH4_x*(1-pourcentagex*volumew/volumex) ;
		//Na_t <- Na_t*pourcentagex*volumew/volumex + Na_x*(1-pourcentagex*volumew/volumex);
		// pour les agents précédents, tous sont récupérés, les microporteurs en plus : concentration*volume en plus
		if passagex = true {	
				ask microcarriers {
					do die;
					}
				ask cells {
					do die;
				}
				create cells number :  round(total_cells_on_microcarriers*volumew/volumex*(1-amas_percentage) ){
					location <- {rnd(dimension-2*cell_radius), rnd(dimension-2*cell_radius), rnd(dimension-2*cell_radius)};
				}
				create amas number: round(total_cells_on_microcarriers*volumew/volumex*amas_percentage/cells_amas ){
					location <- {rnd(dimension-2*amas_radius), rnd(dimension-2*amas_radius), rnd(dimension-2*amas_radius)};
				}	
				create microcarriers number: round(microcarriers_number*volumew/volumex +microcarriers1*(1-pourcentagex*volumew/volumex)*(dimension_si^3)) {
					location <- {rnd(dimension-2*microcarrier_radius), rnd(dimension-2*microcarrier_radius), rnd(dimension-2*microcarrier_radius)};
				}
		}
		if passagex = false {
			int a <- round(microcarriers_number - microcarriers_number*pourcentagex*volumew/volumex);
			int b <- round(cells_number - cells_number*pourcentagex*volumew/volumex);
		
			loop times: a {
				ask one_of (species(microcarriers)) {
					do die;
				}
			}
			loop times: b {
				ask one_of (species(cells)) {
					do die;
				}
			}
			}
	}	
	}
	
	
	
		
		
	/* }
	
	species substrate{
		float gln;
		float t; */
		
/* 	equation evol simultaneously : [microcarriers] {
			diff (gln, t ) = 10 - 0.4 *gln/(1+gln)* (microcarriers accumulate (each.replicant_cells_number));
		}

	 	solve evol method: "rk4" step: 0.01;
	
	
	}*/
    
species cells skills: [moving] {  
	rgb color;
	float x ;
	float y ;
	float z ;

reflex move{
		do wander_3D;
//		x <- max([min([self.location.x,dimension - virus_radius]),virus_radius]);
//		y <- max([min([self.location.y,dimension - virus_radius]),virus_radius]);
//		z <- max([min([self.location.z,dimension - virus_radius]),virus_radius]);
//		do goto target: {x,y,z};
		}

	aspect default {
		draw sphere(cell_radius) color:rgb('orange');
    }
}

species amas skills : [moving] {
	rgb color;
	float x ;
	float y ;
	float z ;

	reflex move{
		do wander_3D;
//		x <- max([min([self.location.x,dimension - amas_radius]),amas_radius]);
//		y <- max([min([self.location.y,dimension - amas_radius]),amas_radius]);
//		z <- max([min([self.location.z,dimension - amas_radius]),amas_radius]);
//		do goto target: {x,y,z};
		}

	aspect default {
		draw sphere(amas_radius) color:rgb('red');
    }
	
}

species microcarriers skills : [moving] {
	rgb color;
	
	int passage_number <- 0;
	
	bool flash <- false;
	
	float replicant_cells_number <- 0.0;
	float latency_cells_number <- 0.0;
	float cells_on_microcarrier <- 0.0 ;
	float killv <- 0.0;
	
	float microcarrier_cells_radius <- microcarrier_radius;
	float x0 ;
	float y0 ;
	float z0 ;

	
	
	reflex move{
		x0 <- self.location.x;
		y0 <- self.location.y;
		z0 <- self.location.z;
		do wander_3D amplitude:40;
		list<microcarriers> microcarriersNeighbours <- microcarriers select ((each distance_to self)< 2*microcarrier_cells_radius);
		if length(microcarriersNeighbours)>1 {
			set location <- {x0,y0,z0};
		}
//		x <- max([min([self.location.x,dimension - virus_radius]),virus_radius]);
//		y <- max([min([self.location.y,dimension - virus_radius]),virus_radius]);
//		z <- max([min([self.location.z,dimension - virus_radius]),virus_radius]);
//		do goto target: {x,y,z};
	}
     		
    reflex attachCellNeighbours {
    	flash <- false;
    	list<cells> cellNeighbours <- cells select ((each distance_to self) < microcarrier_cells_radius+cell_radius);
    	list<amas> amasNeighbours <- amas select ((each distance_to self) < microcarrier_cells_radius+amas_radius); 
     	ask cellNeighbours {
     		do die;
     	}
     	ask amasNeighbours {
     		do die;
     	}
     	flash <- length(cellNeighbours) > 0 or length(amasNeighbours) >0;
     	latency_cells_number <- latency_cells_number + length(cellNeighbours) + cells_amas*length(amasNeighbours);
    }
    
    reflex attachVirusNeighbours {
    	list<virus> virusneighbours <- virus select ((each distance_to self) < microcarrier_cells_radius);
    	ask virusneighbours {
     	do die;
     	}
     	killv <- killv + length (virusneighbours);
    }
    
    
   
    reflex sortie_latence when: latency_cells_number > 0 {
    	int sortLatency <- round(0.5*latency_cells_number);//; ///A CHANGER (step*/Htime_latence)
    	//float sortLatency <- latency_cells_number;
    	replicant_cells_number <- replicant_cells_number+sortLatency;
    	latency_cells_number <- latency_cells_number-sortLatency;
    	//replicant_cells_number <- replicant_cells_number*(2*glc_t)*(1/(2*lac_t));
    	//lac_t <- lac_t*2*replicant_cells_number;
    	//glc_t <- glc_t/(2*replicant_cells_number);
    	//replicant_cells_number <- (replicant_cells_number+latency_cells_number/time_latence)*(1+2/time_mitosis)/*fonction de nutriment entre 0 et 1;
    	//latency_cells_number <- (latency_cells_number - latency_cells_number/time_latence)/*(1-killv/(replicant_cells_number+latency_cells_number+killv))/* *fonction liée au nutriments 
    	
    }
    
    reflex replication_premiere when: replicant_cells_number > 0 {
    	replicant_cells_number <- replicant_cells_number+replicant_cells_number*(0.00004*glc_t)*(1/(1+0.006*lac_t));
    	//replicant_cells_number <- (replicant_cells_number+latency_cells_number/time_latence)*(1+2/time_mitosis)/*fonction de nutriment entre 0 et 1;
    	//latency_cells_number <- (latency_cells_number - latency_cells_number/time_latence)/*(1-killv/(replicant_cells_number+latency_cells_number+killv))/* *fonction liée au nutriments 
    	}

//	  reflex replication_avant_confluence when: replicant_cells_number < 4*exp(ln(microcarrier_cells_radius/cell_radius)*2) and replicant_cells_number > 0 {
//    	replicant_cells_number <- (replicant_cells_number+latency_cells_number/time_latence)*(1-killv/(replicant_cells_number+latency_cells_number+killv))*(1+1/time_mitosis)/*fonction de nutriment entre 0 et 1*//**/;
//    	    	latency_cells_number <- (latency_cells_number  - latency_cells_number/time_latence)*(1-cytopath*killv/(replicant_cells_number+latency_cells_number+killv))/* *fonction liée au nutriments */;
//    	
//    }
//    
//    reflex replication_apres_confluence when: replicant_cells_number >= 4*exp(ln(microcarrier_cells_radius/cell_radius)*2) {
//    	replicant_cells_number <- (replicant_cells_number+latency_cells_number/time_latence)*/*fonction de nutriment entre 0 et 1*/(1-killv/(replicant_cells_number+killv))*(1+4*exp(ln(microcarrier_cells_radius/cell_radius)*2)/replicant_cells_number/time_mitosis);
//    	latency_cells_number <- (latency_cells_number - latency_cells_number/time_latence)*(1-cytopath*killv/(replicant_cells_number+latency_cells_number+killv))/* *fonction liée au nutriments */;
//    	
//    }
    
   
    reflex actualise {
    	microcarrier_cells_radius <- ((microcarrier_radius^3)+(replicant_cells_number+latency_cells_number)*(cell_radius^3))^(1/3);
    	cells_on_microcarrier <- replicant_cells_number + latency_cells_number;  	
    }
    
    
    
    
    /* 
    reflex virus_replication {
    	create virus number: 1000 {
    		location <- any_location_in (1 around sphere(10));
    	}
    }*/
    
 
    
	aspect default {
	draw sphere(microcarrier_cells_radius) color: rgb(255*int(flash),0,100+2*int(replicant_cells_number+latency_cells_number));
	}
}

	
species virus skills : [moving] {
	rgb color;
	float x ;
	float y ;
	float z ;


	reflex move{
		do wander_3D;
//		x <- max([min([self.location.x,dimension - virus_radius]),virus_radius]);
//		y <- max([min([self.location.y,dimension - virus_radius]),virus_radius]);
//		z <- max([min([self.location.z,dimension - virus_radius]),virus_radius]);
//		set location <- {x,y,z};
	}

	aspect default {
		draw sphere(virus_radius) color:rgb(0,0,0);
    }
}



experiment Display_and_charts type: gui {
	
	parameter 'time resolution of view (s)' var: timeRefresh_view category: "display";
	parameter 'time resolution of charts (s)' var: timeRefresh_charts category: "display";
	

	
	parameter 'cell percentage in amas' var: amas_percentage category: amas;
	parameter 'number of cells in one ama' var: cells_amas category: amas;
	
	
	parameter 'duration of a step (s)' var: step category: "time";

	
	parameter 'cytopathogenicity of virus' var: cytopath category: "cytopathogenicity";
	
	
	parameter 'dimension of the visualised volume (S.I)' var: dimension_si category: "dimensions";
		
	parameter 'perfusion continue' var: perfusion_continue category: 'initial';
	parameter 'initial volume (m³)' var: volume0 category: 'initial';
	parameter 'number of actions' var: actions_number category: 'initial';

	parameter 'initial glucose (mmol/L)' var: glc_0 category: 'initial';
	
	parameter 'number of virus' var: initial_virus_number category: "Virus injection";
	parameter 'time before infection (h)' var: Htime_infection category : "Virus injection";
	
	
	
	parameter 'passage (yes) or medium change (no)' var: passage1 category: '1st action';
	parameter 'First injection time (h)' var: Htime_injection1 category: '1st action';
	parameter 'First injection volume (m³)' var: volume1 category: '1st action';
	
	parameter 'passage (yes) or medium change (no)' var: passage2 category: '2cd action';
	parameter 'second injection time (h)' var: Htime_injection2 category: '2cd action';
	parameter 'second injection volume (m³)' var: volume2 category: '2cd action';
	
	parameter 'passage (yes) or medium change (no)' var: passage3 category: '3rd action';
	parameter 'third injection time (h)' var: Htime_injection3 category: '3rd action';
	parameter 'third injection volume (m³)' var: volume3 category: '3rd action';
	
	parameter 'passage (yes) or medium change (no)' var: passage4 category: '4th action';
	parameter 'fourth injection time (h)' var: Htime_injection4 category: '4th action';
	parameter 'fourth injection volume (m³)' var: volume4 category: '4th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage5 category: '5th action';
	parameter 'fifth injection time (h)' var: Htime_injection5 category: '5th action';
	parameter 'fifth injection volume (m³)' var: volume5 category: '5th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage6 category: '6th action';
	parameter 'sixth injection time (h)' var: Htime_injection6 category: '6th action';
	parameter 'sixth injection volume (m³)' var: volume6 category: '6th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage7 category: '7th action';
	parameter 'seventh injection time (h)' var: Htime_injection7 category: '7th action';
	parameter 'seventh injection volume (m³)' var: volume7 category: '7th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage8 category: '8th action';
	parameter 'eigth injection time (h)' var: Htime_injection8 category: '8th action';
	parameter 'eigth injection volume (m³)' var: volume8 category: '8th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage9 category: '9th action';
	parameter '9th injection time (h)' var: Htime_injection9 category: '9th action';
	parameter '9th injection volume (m³)' var: volume9 category: '9th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage10 category: '10th action';
	parameter '10th injection time (h)' var: Htime_injection10 category: '10th action';
	parameter '10th injection volume (m³)' var: volume10 category: '10th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage11 category: '11th action';
	parameter '11th injection time (h)' var: Htime_injection11 category: '11th action';
	parameter '11th injection volume (m³)' var: volume11 category: '11th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage12 category: '12th action';
	parameter '12th injection time (h)' var: Htime_injection12 category: '12th action';
	parameter '12th injection volume (m³)' var: volume12 category: '12th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage13 category: '13th action';
	parameter '13th injection time (h)' var: Htime_injection13 category: '13th action';
	parameter '13th injection volume (m³)' var: volume13 category: '13th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage14 category: '14th action';
	parameter '14th injection time (h)' var: Htime_injection14 category: '14th action';
	parameter '14th injection volume (m³)' var: volume14 category: '14th action';
	
	
	parameter 'passage (yes) or medium change (no)' var: passage15 category: '15th action';
	parameter '15th injection time (h)' var: Htime_injection15 category: '15th action';
	parameter '15th injection volume (m³)' var: volume15 category: '15th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage16 category: '16th action';
	parameter '16th injection time (h)' var: Htime_injection16 category: '16th action';
	parameter '16th injection volume (m³)' var: volume16 category: '16th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage17 category: '17th action';
	parameter '17th injection time (h)' var: Htime_injection17 category: '17th action';
	parameter '17th injection volume (m³)' var: volume17 category: '17th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage18 category: '18th action';
	parameter '18th injection time (h)' var: Htime_injection18 category: '18th action';
	parameter '18th injection volume (m³)' var: volume18 category: '18th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage19 category: '19th action';
	parameter '19th injection time (h)' var: Htime_injection19 category: '19th action';
	parameter '19th injection volume (m³)' var: volume19 category: '19th action';
	
	parameter 'passage (yes) or medium change (no)' var: passage20 category: '20th action';
	parameter '20th injection time (h)' var: Htime_injection20 category: '20th action';
	parameter '20th injection volume (m³)' var: volume20 category: '20th action';
	
	
	//parameter 'Initial Na' var: Na_0 category: 'initial';
	parameter 'initial Glu' var: glc_0 category: 'initial';
//	parameter 'initial Lac' var: lac_0 category: 'initial';
	parameter 'microcarriers (g/L)' var: microcarriers_0_gl category: 'initial';
	parameter 'cells (cells/µL) ' var: cells0_microL category: 'initial';
	
//	parameter 'Glu' var: glc_1 category: '1st dilution';
//	parameter 'Lac' var: lac_1 category: '1st dilution';
//	parameter 'microcarriers' var: microcarriers1 category: '1st dilution';
//	parameter 'proportion of previous solution' var: pourcentage1 category: '1st dilution';
//	
//	parameter 'Glu' var: glc_2 category: '1st passage';
//	parameter 'Lac' var: lac_2 category: '1st passage';
//	parameter 'microcarriers' var: microcarriers2 category: '1st passage';
//	parameter 'proportion of previous solution' var: pourcentage2 category: '1st passage';
//	parameter 'Glu' var: glc_3 category: '2cd passage';
//	parameter 'Lac' var: lac_3 category: '2cd passage';
//	parameter 'microcarriers' var: microcarriers3 category: '2cd passage';
//	parameter 'proportion of previous solution' var: pourcentage3 category: '2cd passage';
//	
	
	
	output {
			display View1 type:opengl background:rgb('white') ambient_light:20 diffuse_light:200 refresh_every: timeRefresh_view autosave: true draw_env:true {
		species cells;
			species amas;
			species microcarriers;
			species virus transparency: 0;
			graphics "box"{
				draw box(dimension+2*microcarrier_radius,dimension+2*microcarrier_radius,dimension+2*microcarrier_radius) at: {dimension/2,dimension/2,-microcarrier_radius} empty:true ;// color:rgb(0,255,0,0.1);
				}
		}
	}
	

	output {
		display Population_information_units refresh_every: timeRefresh_charts {
			chart "Species evolution " type: series background: rgb("white") size: {1,0.4} position: {0, 0.05} {
				data "cellules en suspension(Cell/µL)" value: cells_number/(dimension_si^3)/(10^9) color: rgb ("red");
				data "cellules en amas (Cell/µL)" value: amas_number*cells_amas/(dimension_si^3)/(10^9) color: rgb ("yellow");
				data "number of virus (virus/µL)" value: virus_number/(dimension_si^3)/(10^9) color: rgb ("green");
				data "nombre total de cellules sur microcarrier (Cell/µL)" value: total_cells_on_microcarriers/(dimension_si^3)/(10^9)/(dimension_si^3)/(10^9) color: rgb ("blue");
			}
			chart "cells evolution" type: series background: rgb ("white") size: {1,0.4} position: {0, 0.5} {
				data "cellules replicables /cm²" value: total_replicant_cells/(2.5*(dimension_si^3)*(10^3)*surface_massique_MP) color: rgb ("red");
				data "cellules en latence /cm²" value: total_latency_cells/(2.5*(dimension_si^3)*(10^3)*surface_massique_MP) color: rgb ("green");
				data "toutes cellules adhérées /cm²" value: total_cells_on_microcarriers/(2.5*(dimension_si^3)*(10^3)*surface_massique_MP) color: rgb ("blue");
			}
		}
	}	
	output {			
		display Nutrients_evolution refresh_every: timeRefresh_charts {	
			chart "nutrients evolution" type: series background: rgb ("white") size: {1,0.4} position: {0, 0.05} {
				//data "glu" value: glu_t color: rgb ("red");
				//data "gln" value: gln_t color: rgb ("blue");
				data "glc (mmol/L)" value: glc_t color: rgb ("green");	
//				data "Na" value: Na_t color: rgb (100,100,0);
				//data "NH4" value: NH4_t color: rgb (0,100,100);
				data "lac (mmol/L)" value: lac_t color: rgb (100,0,100);
				}
			}
		}
	output {
		display heterogeneity refresh_every: timeRefresh_charts {
			chart "heterogeneity histogram" type: histogram background: rgb("lightGray") size: {0.5,0.4} position: {0, 0.5} {
				data "]0;0.25] " value: microcarriers count (each.cells_on_microcarrier < max_cells_on_microcarrier/4) ;
				data "]0.25;0.5]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/4) and (each.cells_on_microcarrier < max_cells_on_microcarrier/2)) ;
				data "]0.5;0.75]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/2) and (each.cells_on_microcarrier < max_cells_on_microcarrier*3/4));
				data "]0.75;1]" value: microcarriers count (each.cells_on_microcarrier >= max_cells_on_microcarrier*3/4);
			}
//			chart "heterogeneity graph" type: series background: rgb ("white") size: {1, 0.4} position: {0, 0.05} {
//				data "]0;0.25] " value: max_cells_on_microcarrier color: rgb ("dark") ;
//				data "]0;0.25] " value: microcarriers count (each.cells_on_microcarrier <= max_cells_on_microcarrier/4) color: rgb ("dark") ;
//				data "]0.25;0.5]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/4) and (each.cells_on_microcarrier <= max_cells_on_microcarrier/2)) color: rgb ("green") ;
//				data "]0.5;0.75]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/2) and (each.cells_on_microcarrier <= max_cells_on_microcarrier*3/4)) color: rgb ("blue");
//				data "]0.75;1]" value: microcarriers count (each.cells_on_microcarrier >= max_cells_on_microcarrier*3/4) color: rgb ("red");
//			}
			chart "heterogeneity graph" type: series background: rgb ("white") size: {1, 0.4} position: {0, 0.05} {
//				data "]0;0.25] " value: max_cells_on_microcarrier color: rgb ("dark") ;
//				data "]0;0.25] " value: microcarriers count (each.cells_on_microcarrier <= total_cells_on_microcarriers/microcarriers_number) color: rgb ("dark") ;
//				data "]0.25;0.5]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/4) and (each.cells_on_microcarrier <= max_cells_on_microcarrier/2)) color: rgb ("green") ;
//				data "]0.5;0.75]" value: microcarriers count ((each.cells_on_microcarrier >= max_cells_on_microcarrier/2) and (each.cells_on_microcarrier <= max_cells_on_microcarrier*3/4)) color: rgb ("blue");
//				data "]0.75;1]" value: microcarriers count (each.cells_on_microcarrier >= max_cells_on_microcarrier*3/4) color: rgb ("red");
			}
//			chart "test" type: series background: rgb ("white") size: {1, 0.4} position: {0, 0.05} {
//				data "cell" value: round (total_cells_on_microcarriers*volume1/volume2) color: rgb ("red");
//				data "micro" value: round(microcarriers_number*volume1/volume2) color: rgb ("green");
//				data "micro2" value : round (microcarriers2*(1-pourcentage2)*(dimension_si^3)) color: rgb ("blue");	
//			}		
		}
	}
}

//experiment Display_only type: gui {
//	
//	parameter 'radius of a cell' var: cell_radius category : "dimensions";
//	parameter 'radius of a microcarrier' var: microcarrier_radius category : "dimensions";
//	
//	parameter 'duration of a step (s)' var: step category: "time";
//	parameter 'time between adhesion and first replication (h)' var: Htime_latence category : "time";
//	parameter 'time of mitosis (h)' var: Htime_mitosis category: "time";
//	
//	parameter 'cytopathogenicity of virus' var: cytopath category: "cytopathogenicity";
//	
//	parameter 'dimension of the visualised volume (S.I)' var: dimension_si category: "dimensions";
//		
//	parameter 'initial volume (m³)' var: volume0 category: 'initial';
//	//parameter 'initial concentration Na' var	
//	
//	parameter 'First injection time (h)' var: Htime_injection1 category: '1st action';
//	parameter 'First injection volume (m³)' var: volume1 category: '1st action';
//	
//	parameter 'second injection time (h)' var: Htime_injection2 category: '2cd action';
//	parameter 'second injection volume (m³)' var: volume2 category: '2cd action';
//	
//	parameter 'third injection time (h)' var: Htime_injection3 category: '3rd action';
//	parameter 'third injection volume (m³)' var: volume3 category: '3rd action';
//	
//	//parameter 'Initial Na' var: Na_0 category: 'initial';
//	parameter 'initial Glu' var: glc_0 category: 'initial';
//	parameter 'initial Lac' var: lac_0 category: 'initial';
//	parameter 'microcarriers' var: microcarriers_0 category: 'initial';
//	parameter 'cells' var: cells_0 category: 'initial';
//	
//	
//	parameter 'Glu' var: glc_1 category: '1st dilution';
//	parameter 'Lac' var: lac_1 category: '1st dilution';
//	parameter 'microcarriers' var: microcarriers1 category: '1st dilution';
//	parameter 'proportion of previous solution' var: pourcentage1 category: '1st dilution';
//	
//	
//	parameter 'Glu' var: glc_2 category: '1st passage';
//	parameter 'Lac' var: lac_2 category: '1st passage';
//	parameter 'microcarriers' var: microcarriers2 category: '1st passage';
//	parameter 'proportion of previous solution' var: pourcentage2 category: '1st passage';
//	
//	
//	parameter 'Glu' var: glc_3 category: '2cd passage';
//	parameter 'Lac' var: lac_3 category: '2cd passage';
//	parameter 'microcarriers' var: microcarriers3 category: '2cd passage';
//	parameter 'proportion of previous solution' var: pourcentage3 category: '2cd passage';
//	
//	
//	parameter 'number of virus' var: initial_virus_number category: "Virus injection";
//	parameter 'time before infection (h)' var: Htime_infection category : "Virus injection";
//	
//	
//	output {
//		
//		display View1 type:opengl background:rgb(10,40,55) {
//			species cells;
//			species microcarriers;
//			species virus;
//			}
//		}
//	}
//
//experiment Charts_only type: gui {
//	
//	parameter 'radius of a cell' var: cell_radius category : "dimensions";
//	parameter 'radius of a microcarrier' var: microcarrier_radius category : "dimensions";
//	
//	parameter 'duration of a step (s)' var: step category: "time";
//	parameter 'time between adhesion and first replication (h)' var: Htime_latence category : "time";
//	parameter 'time of mitosis (h)' var: Htime_mitosis category: "time";
//	
//	parameter 'cytopathogenicity of virus' var: cytopath category: "cytopathogenicity";
//	
//	
//	
//	parameter 'dimension of the visualised volume (S.I)' var: dimension_si category: "dimensions";
//		
//	parameter 'initial volume (m³)' var: volume0 category: 'initial';
//	//parameter 'initial concentration Na' var	
//	
//	parameter 'First injection time (h)' var: Htime_injection1 category: '1st dilution';
//	parameter 'First injection volume (m³)' var: volume1 category: '1st dilution';
//	
//	parameter 'second injection time (h)' var: Htime_injection2 category: '1st passage';
//	parameter 'second injection volume (m³)' var: volume2 category: '1st passage';
//	
//	parameter 'third injection time (h)' var: Htime_injection3 category: '2cd passage';
//	parameter 'third injection volume (m³)' var: volume3 category: '2cd passage';
//	
//	parameter 'fourth injection time (h)' var: Htime_injection4 category: '3rd passage';
//	parameter 'fourth injection volume (m³)' var: volume4 category: '3cd passage';
//	
//	parameter 'fifth injection time (h)' var: Htime_injection5 category: '4th passage';
//	parameter 'fifth injection volume (m³)' var: volume5 category: '4th passage';
//	
//	parameter 'sixth injection time (h)' var: Htime_injection6 category: '5th passage';
//	parameter 'sixth injection volume (m³)' var: volume6 category: '5th passage';
//	
//	//parameter 'Initial Na' var: Na_0 category: 'initial';
//	parameter 'initial Glu' var: glc_0 category: 'initial';
////	parameter 'initial Lac' var: lac_0 category: 'initial';
//	parameter 'microcarriers' var: microcarriers_0 category: 'initial';
//	parameter 'cells' var: cells_0 category: 'initial';
//	
//	
////	parameter 'Glu' var: glc_1 category: '1st dilution';
////	parameter 'Lac' var: lac_1 category: '1st dilution';
////	parameter 'microcarriers' var: microcarriers1 category: '1st dilution';
////	parameter 'proportion of previous solution' var: pourcentage1 category: '1st dilution';
////	
////	
////	parameter 'Glu' var: glc_2 category: '1st passage';
////	parameter 'Lac' var: lac_2 category: '1st passage';
////	parameter 'microcarriers' var: microcarriers2 category: '1st passage';
////	parameter 'proportion of previous solution' var: pourcentage2 category: '1st passage';
////	
////	
////	parameter 'Glu' var: glc_3 category: '2cd passage';
////	parameter 'Lac' var: lac_3 category: '2cd passage';
////	parameter 'microcarriers' var: microcarriers3 category: '2cd passage';
////	parameter 'proportion of previous solution' var: pourcentage3 category: '2cd passage';
//	
//	
//	parameter 'number of virus' var: initial_virus_number category: "Virus injection";
//	parameter 'time before infection (h)' var: Htime_infection category : "Virus injection";
//	
//	output {
//					
//		display Population_information /*refresh_every: 5*/ {
//			chart "Species evolution" type: series background: rgb("white") size: {1,0.4} position: {0, 0.05} {
//				data "cellules en suspension" value: cells_number color: rgb ("blue");
//				data "number of virus" value: virus_number color: rgb ("green");
//				data "nombre total de cellules sur microporteur" value: total_cells_on_microcarriers color: rgb ("red");
//				
//				}
//			chart "cells evolution" type: series background: rgb ("white") size: {1,0.4} position: {0, 0.5} {
//				data "cellules replicables" value: total_replicant_cells color: rgb ("red");
//				data "cellules en latence" value: total_latency_cells color: rgb ("blue");
//				data "toutes cellules adhérées" value: total_cells_on_microcarriers color: rgb ("green");
//				}
//				}
//		display Nutrients_evolution {	
//			chart "nutrients evolution" type: series background: rgb ("white") size: {1,0.4} position: {0, 0.5} {
//				//data "glu" value: glu_t color: rgb ("red");
//				//data "gln" value: gln_t color: rgb ("blue");
//				data "glc" value: glc_t color: rgb ("green");	
//				//data "Na" value: Na_t color: rgb (100,100,0);
//				//data "NH4" value: NH4_t color: rgb (0,100,100);
//				data "lac" value: lac_t color: rgb (100,0,100);	
//			}		
//		}
//	}
//}
}

