/**
 * Purpose: Test the declaration of a model with more than 2 levels.
 * 
 * Action(s):
 * 		1. Load the model.
 * 
 * Expected outcome: 
 * 		1. Verify the "Agents" menu: agents' hierarchy is correctly populated.
 */
 
model testcase1

global {
	init {
		create macro_species;
		
		create A;
	}
}

entities {
	species macro_species skills: situated {
		
		init {
			create meso_species;
		}

		species meso_species skills: situated {
			
			init {
				create micro_species;
			}
			
			species micro_species skills: situated {
				
			}
		}
	}
	
	species A skills: situated {
		
		init {
			create B;
		}
		
		species B skills: situated {
			
			init {
				create C;
			}
			
			species C skills: situated {
				
				init {
					create D;
				}

				species D skills: situated {
					
					init {
						create E;
					}

					species E skills: situated {
						
						init {
							create F;
						}
						
						species F skills: situated {
							
							init {
								create G;
							}

							species G skills: situated {
								
								init {
									create H;
								}

								species H skills: situated {
									
									init {
										create I;
									}

									species I skills: situated {
										
										init {
											create J;
										}

										species J skills: situated {
											
											init {
												create K;
											}

											species K skills: situated {
												
												init {
													create L;
												}

												species L skills: situated {
													
													init {
														create species: M;
													}

													species M skills: situated {
														
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}

environment width: 100 height: 100;

experiment default_expr type: gui {
	output {
		
	}
}
