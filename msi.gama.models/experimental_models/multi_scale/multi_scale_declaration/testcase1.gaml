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

import "platform:/plugin/msi.gama.application/generated/std.gaml"

global {
	init {
		create species: macro_species number: 1;
		
		create species: A number: 1;
	}
}

entities {
	species macro_species skills: situated {
		
		init {
			create species: meso_species number: 1;
		}

		species meso_species skills: situated {
			
			init {
				create species: micro_species number: 1;
			}
			
			species micro_species skills: situated {
				
			}
		}
	}
	
	species A skills: situated {
		
		init {
			create species: B number: 1;
		}
		
		species B skills: situated {
			
			init {
				create species: C number: 1;
			}
			
			species C skills: situated {
				
				init {
					create species: D number: 1;
				}

				species D skills: situated {
					
					init {
						create species: E number: 1;
					}

					species E skills: situated {
						
						init {
							create species: F number: 1;
						}
						
						species F skills: situated {
							
							init {
								create species: G number: 1;
							}

							species G skills: situated {
								
								init {
									create species: H number: 1;
								}

								species H skills: situated {
									
									init {
										create species: I number: 1;
									}

									species I skills: situated {
										
										init {
											create species: J number: 1;
										}

										species J skills: situated {
											
											init {
												create species: K number: 1;
											}

											species K skills: situated {
												
												init {
													create species: L number: 1;
												}

												species L skills: situated {
													
													init {
														create species: M number: 1;
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
