model insectmodel
// gen by Xml2Gaml
import "platform:/plugin/msi.gama.gui.application/generated/std.gaml"

global {
	const map1 type: matrix init: file('carteVillage.png') as_matrix {gridSize,gridSize};
	var time type: int init: 0 max: 10000;
	const step type: int init: 1; //day 
	const gridSize type: int init: 100;
	var vent_Juillet_Decembre type: bool parameter: 'true' init: true;
	var avec_synchronisation type: bool parameter: 'true' init: true;
	var nb_oeufs type: int parameter: 'true' init: 0;
	var nb_nymphes type: int parameter: 'true' init: 0;
	var nb_adultes type: int parameter: 'true' init: 0;
	const gris type: rgb init: rgb [192,192,192];
	const grisFonce type: rgb init: rgb [128,128,128];
	const blanc type: rgb init: rgb('white');
	const bleu type: rgb init: rgb [150,225,255];
	const vert type: rgb init: rgb [0,150,0];
	const jauneClair type: rgb init: rgb [255,230,210];
	const vertFonce type: rgb init: rgb [128,128,64];
	const multiagent type: bool value: true;
	reflex ventMont when: (int(time mod 365)) = 0 {
		set var: vent_Juillet_Decembre value: vent_Juillet_Decembre;
	}
	reflex ventDescent when: (int(time mod 365)) = 183 {
		set var: vent_Juillet_Decembre value: !vent_Juillet_Decembre;
	}
}
environment {
	grid village_grid width: gridSize height: gridSize neighbours: 8 torus: true {
		var color type: rgb init: map1 at {grid_x, grid_y};
		const multiagent type: bool value: true;
		var place type: village_grid init: location as village_grid;
		var estFleuve type: bool init: false;
		var estFerme type: bool init: false;
		var estRue type: bool init: false;
		var estArbre type: bool init: false;
		var estCultivable type: bool init: false;
		var estRiziere type: bool init: false;
		var oldColor type: rgb init: nil;
		var nouvelleSaison type: bool init: false;
		var saison type: int init: 0;
		var etat_infection type: string init: '_';
		var jourSurvi type: int init: 0;
		var oeufs type: float init: nil;
		var nymphes type: float init: nil;
		var nymphes_tmp type: float init: nil;
		var adultes type: float init: nil;
		var MAL type: float init: nil;
		var FAL type: float init: nil;
		var FAC type: float init: nil;
		var MAC type: float init: nil;
		var adultes_tmp type: float init: nil;
		var FAL_tmp type: float init: nil;
		var MAL_tmp type: float init: nil;
		var FAC_tmp type: float init: nil;
		var MAC_tmp type: float init: nil;
		var oeufs_total type: float init: nil;
		var nymphes_total type: float init: nil;
		var adultes_total type: float init: nil;
		init {
			if condition: color = bleu {
				set var: estFleuve value: true;
			}
			if condition: (color = jauneClair) or (color = vertFonce) {
				set var: estRue value: true;
			}
			if condition: (color = vert) {
				set var: estArbre value: true;
			}
			if condition: color = gris {
				set var: estCultivable value: true;
			}
			if condition: color= blanc {
				set var: estFerme value: true;
				create species: ferme number: 1 {
					set var: location value: myself.location;
				}
			}
			set var: oldColor value: self.color;
		}
		reflex when: ((self.nouvelleSaison) and (self.saison=1)) {
			if condition: self.estRiziere {
				set var: self.oeufs value: nb_oeufs;
				set var: self.nymphes value: nb_nymphes;
				set var: self.adultes value: nb_adultes;
				set var: self.FAC value: int ((self.adultes*0.7)+((self.adultes*(rnd(8)/100))));
				set var: self.FAL value: int ((self.adultes*0.8)-self.FAC);
				set var: self.MAC value: int ((self.adultes*0.15)-((self.adultes*(rnd(5)/100))));
				set var: self.MAL value: int (self.adultes-(self.MAC+self.FAL+self.FAC));
			}
			set var: self.nouvelleSaison value: false;
		}
		reflex when: ((int(time mod 7))=0) {
			if condition: self.estRiziere {
				if condition: ((self.adultes) > 0) {
					if condition: (riziere (self.location)).ageRiz=3 {
						set var: FAC value: int ((self.adultes*0.02)+(self.adultes*(rnd(6)/100)));
						set var: MAC value: int ((self.adultes*0.1)-(self.FAC));
						set var: FAL value: int ((self.adultes*0.5)-(self.FAC));
						set var: MAL value: int (self.adultes-(self.FAL+self.FAC+self.MAC));
						else {
							set var: FAC value: int ((self.adultes*0.7)+((self.adultes*(rnd(8)/100))));
							set var: FAL value: int ((self.adultes*0.8)-self.FAC);
							set var: MAC value: int ((self.adultes*0.15)-((self.adultes*(rnd(5)/100))));
							set var: MAL value: int (self.adultes-(self.MAC+self.FAL+self.FAC));
						}
					}
					else {
						set var: FAC value: nil;
						set var: FAL value: nil;
						set var: MAC value: nil;
						set var: MAL value: nil;
					}
				}
			}
		}
		reflex reproduction when: ((int(time mod 7))=6) {
			let oeufs_new value: nil;
			let nymphes_new value: nil;
			let adultes_new value: nil;
			set var: oeufs_new value: float ((float (self.FAC_tmp*300)+float (self.FAC_tmp*rnd(100)))+(float (self.FAL_tmp*90)+float (self.FAC_tmp*rnd(20))));
			set var: FAC_tmp value: nil;
			set var: FAL_tmp value: nil;
			set var: MAC_tmp value: nil;
			set var: MAL_tmp value: nil;
			set var: adultes_new value: self.nymphes_tmp*0.4;
			set var: nymphes_tmp value: self.nymphes;
			if condition: self.estRiziere {
				set var: adultes_tmp value: int (self.adultes-(self.adultes*0.035*7));
				set var: FAL_tmp value: int (self.FAL-(7*(self.FAL*0.035)));
				set var: MAL_tmp value: int (self.MAL-(7*(self.MAL*0.035)));
				set var: FAC_tmp value: int (self.FAC-(7*(self.FAC*0.035)));
				set var: MAC_tmp value: int (self.MAC-(7*(self.MAC*0.035)));
				set var: FAC value: nil;
				set var: FAL value: nil;
				set var: MAC value: nil;
				set var: MAL value: nil;
			}
			set var: nymphes_new value: self.oeufs*0.3;
			set var: nymphes value: int (self.nymphes_new);
			set var: oeufs value: int (self.oeufs_new);
			set var: adultes value: int (self.adultes_new);
			set var: adultes_total value: self.adultes+self.adultes_tmp;
			let densite2 value: self.adultes_total;
			set var: densite2 value: densite2/100;
			if condition: ((densite2 > 0) and (10 >= densite2)) {
				set var: self.etat_infection value: 'tres leger';
			}
			if condition: ((densite2 > 10) and (2000 >= densite2)) {
				set var: self.color value: rgb[255, 180, 180];
				set var: self.etat_infection value: 'leger';
			}
			if condition: ((densite2 > 2000) and (5000 >= densite2)) {
				set var: self.color value: rgb[255, 80, 80];
				set var: self.etat_infection value: 'moyen';
			}
			if condition: ((densite2 > 5000) and (10000 > densite2)) {
				set var: self.color value: rgb[255, 0, 0];
				set var: self.etat_infection value: 'grave';
			}
			if condition: (densite2 >= 10000) {
				set var: self.color value: rgb[100, 0, 0];
				set var: self.etat_infection value: 'tres grave';
			}
		}
		var FALP type: float init: nil;
		var FALP_tmp type: float init: nil;
		var MALP type: float init: nil;
		var MALP_tmp type: float init: nil;
		var FALV type: float init: nil;
		var FALV_tmp type: float init: nil;
		var MALV type: float init: nil;
		var MALV_tmp type: float init: nil;
		var FAL_total type: float init: nil;
		var MAL_total type: float init: nil;
		var FAC_total type: float init: nil;
		var MAC_total type: float init: nil;
		var FALP_total type: float init: nil;
		var MALP_total type: float init: nil;
		var FALV_total type: float init: nil;
		var MALV_total type: float init: nil;
		var FALV_partage type: float init: nil;
		var FALV_tmp_partage type: float init: nil;
		var MALV_partage type: float init: nil;
		var MALV_tmp_partage type: float init: nil;
		reflex propagation {
			if condition: (self.estRiziere) {
				set var: jourSurvi value: 3;
				else {
					set var: jourSurvi value: rnd(2);
				}
			}
			if condition: (self.estRiziere != true) {
				if condition: ((self.FALV_tmp > 0) or (self.FALV > 0) or (self.MALV_tmp > 0) or (self.MALV > 0)) {
					let cible value: riziere ((one_of (self neighbours_at 7 where ((each.estRiziere=true)))).location);
					if condition: ((cible=nil) or (cible.ageRiz=3) or (cible.ageRiz=0)) {
						if condition: jourSurvi > 0 {
							set var: self.FALP value: self.FALV;
							set var: self.FALP_tmp value: self.FALV_tmp;
							set var: self.MALP value: self.MALV;
							set var: self.MALP_tmp value: self.MALV_tmp;
							set var: self.FALV value: 0;
							set var: self.FALV_tmp value: 0;
							set var: self.MALV value: 0;
							set var: self.MALV_tmp value: 0;
							let loc_X value: ((self.location) at 0);
							let loc_Y value: ((self.location) at 1);
							if condition: vent_Juillet_Decembre {
								set var: loc_X value: loc_X + 20;
								set var: loc_Y value: loc_Y - 20;
								else {
									set var: loc_X value: loc_X - 20;
									set var: loc_Y value: loc_Y;
								}
							}
							if condition: (loc_X >= gridSize) {
								set var: loc_X value: (loc_X - gridSize);
							}
							if condition: (0 > loc_X) {
								set var: loc_X value: (gridSize + loc_X);
							}
							if condition: (loc_Y >= gridSize) {
								set var: loc_Y value: (loc_Y - gridSize);
							}
							if condition: (0 > loc_Y) {
								set var: loc_Y value: (gridSize + loc_Y);
							}
							let location_tmp1 value: {loc_X,loc_Y};
							set var: (location_tmp1 as village_grid).FALV value: self.FALP;
							set var: (location_tmp1 as village_grid).FALV_tmp value: self.FALP_tmp;
							set var: (location_tmp1 as village_grid).MALV value: self.MALP;
							set var: (location_tmp1 as village_grid).MALV_tmp value: self.MALP_tmp;
							set var: (location_tmp1 as village_grid).FALV_total value: (location_tmp1 as village_grid).FALV+(location_tmp1 as village_grid).FALV_tmp;
							set var: (location_tmp1 as village_grid).MALV_total value: (location_tmp1 as village_grid).MALV+(location_tmp1 as village_grid).MALV_tmp;
							set var: (location_tmp1 as village_grid).FAL_total value: (((location_tmp1 as village_grid).FAL+(location_tmp1 as village_grid).FAL_tmp)+((location_tmp1 as village_grid).FALV+(location_tmp1 as village_grid).FALV_tmp));
							set var: (location_tmp1 as village_grid).MAL_total value: (((location_tmp1 as village_grid).MAL+(location_tmp1 as village_grid).MAL_tmp)+((location_tmp1 as village_grid).MALV+(location_tmp1 as village_grid).MALV_tmp));
							set var: (location_tmp1 as village_grid).FAC_total value: (location_tmp1 as village_grid).FAC+(location_tmp1 as village_grid).FAC_tmp;
							set var: (location_tmp1 as village_grid).MAC_total value: (location_tmp1 as village_grid).MAC+(location_tmp1 as village_grid).MAC_tmp;
							set var: (location_tmp1 as village_grid).adultes_total value: (((location_tmp1 as village_grid).FAL_total+(location_tmp1 as village_grid).MAL_total)+((location_tmp1 as village_grid).FAC_total+(location_tmp1 as village_grid).MAC_total));
							let densite3 value: (location_tmp1 as village_grid).adultes_total;
							set var: densite3 value: densite3/100;
							if condition: ((location_tmp1 as village_grid).estRiziere != true) {
								set var: (location_tmp1 as village_grid).color value: (location_tmp1 as village_grid).oldColor;
							}
							if condition: ((densite3 > 0) and (10 >= densite3)) {
								set var: (location_tmp1 as village_grid).etat_infection value: 'tres leger';
							}
							if condition: ((densite3 > 10) and (2000 >= densite3)) {
								set var: (location_tmp1 as village_grid).color value: rgb[255, 180, 180];
								set var: (location_tmp1 as village_grid).etat_infection value: 'leger';
							}
							if condition: ((densite3 > 2000) and (5000 >= densite3)) {
								set var: (location_tmp1 as village_grid).color value: rgb[255, 80, 80];
								set var: (location_tmp1 as village_grid).etat_infection value: 'moyen';
							}
							if condition: ((densite3 > 5000) and (10000 > densite3)) {
								set var: (location_tmp1 as village_grid).color value: rgb[255, 0, 0];
								set var: (location_tmp1 as village_grid).etat_infection value: 'grave';
							}
							if condition: (densite3 >= 10000) {
								set var: (location_tmp1 as village_grid).color value: rgb[100, 0, 0];
								set var: (location_tmp1 as village_grid).etat_infection value: 'tres grave';
							}
							set var: self.FALP value: 0;
							set var: self.FALP_tmp value: 0;
							set var: self.MALP value: 0;
							set var: self.MALP_tmp value: 0;
							else {
								set var: self.FALV_tmp value: 0;
								set var: self.FALV value: 0;
								set var: self.MALV_tmp value: 0;
								set var: self.MALV value: 0;
								set var: self.FALV_total value: 0;
							}
						}
						else {
							let rizJeune value: self.cible;
							let champsRizJeune value: ((self.rizJeune).maFerme).rizieres;
							set var: FALV_partage value: self.FALV/length(champsRizJeune);
							set var: FALV_tmp_partage value: self.FALV_tmp/length(champsRizJeune);
							set var: MALV_partage value: self.MALV/length(champsRizJeune);
							set var: MALV_tmp_partage value: self.MALV_tmp/length(champsRizJeune);
							set var: self.FALV_tmp value: 0;
							set var: self.FALV value: 0;
							set var: self.MALV_tmp value: 0;
							set var: self.MALV value: 0;
							loop while: (!(empty(champsRizJeune))) {
								let champRizJeune value: one_of(self.champsRizJeune);
								set var: (village_grid (self.champRizJeune)).FAL value: (village_grid (self.champRizJeune)).FAL+self.FALV_partage;
								set var: (village_grid (self.champRizJeune)).FAL_tmp value: (village_grid (self.champRizJeune)).FAL_tmp+self.FALV_tmp_partage;
								set var: (village_grid (self.champRizJeune)).MAL value: (village_grid (self.champRizJeune)).MAL+self.MALV_partage;
								set var: (village_grid (self.champRizJeune)).MAL_tmp value: (village_grid (self.champRizJeune)).MAL_tmp+self.MALV_tmp_partage;
								set var: champsRizJeune value: self.champsRizJeune-self.champRizJeune;
							}
							set var: FALV_partage value: 0;
							set var: FALV_tmp_partage value: 0;
							set var: MALV_partage value: 0;
							set var: MALV_tmp_partage value: 0;
						}
					}
					else {
						set var: FALV_partage value: 0;
						set var: FALV_tmp_partage value: 0;
						set var: MALV_partage value: 0;
						set var: MALV_tmp_partage value: 0;
					}
				}
				else {
					if condition: ((riziere(self.location)).ageRiz=0) {
						if condition: ((self.FALV_tmp > 0) or (self.FALV > 0) or (self.MALV_tmp > 0) or (self.MALV > 0)) {
							let cible1 value: riziere ((one_of (self neighbours_at 7 where ((each.estRiziere=true)))).location);
							if condition: ((cible1=nil) or (cible1.ageRiz=3) or (cible1.ageRiz=0)) {
								if condition: jourSurvi > 0 {
									set var: self.FALP value: self.FALV;
									set var: self.FALP_tmp value: self.FALV_tmp;
									set var: self.MALP value: self.MALV;
									set var: self.MALP_tmp value: self.MALV_tmp;
									set var: self.FALV value: 0;
									set var: self.FALV_tmp value: 0;
									set var: self.MALV value: 0;
									set var: self.MALV_tmp value: 0;
									let loc_X1 value: ((self.location) at 0);
									let loc_Y1 value: ((self.location) at 1);
									if condition: vent_Juillet_Decembre {
										set var: loc_X1 value: loc_X1 + 20;
										set var: loc_Y1 value: loc_Y1 - 20;
										else {
											set var: loc_X1 value: loc_X1 - 20;
											set var: loc_Y1 value: loc_Y1;
										}
									}
									if condition: (loc_X1 >= gridSize) {
										set var: loc_X1 value: (loc_X1 - gridSize);
									}
									if condition: (0 > loc_X1) {
										set var: loc_X1 value: (gridSize + loc_X1);
									}
									if condition: (loc_Y1 >= gridSize) {
										set var: loc_Y1 value: (loc_Y1 - gridSize);
									}
									if condition: (0 > loc_Y1) {
										set var: loc_Y1 value: (gridSize + loc_Y1);
									}
									let location_tmp2 value: {loc_X1,loc_Y1};
									set var: (location_tmp1 as village_grid).FALV value: self.FALP;
									set var: (location_tmp1 as village_grid).FALV_tmp value: self.FALP_tmp;
									set var: (location_tmp1 as village_grid).MALV value: self.MALP;
									set var: (location_tmp1 as village_grid).MALV_tmp value: self.MALP_tmp;
									set var: (location_tmp1 as village_grid).FALV_total value: (location_tmp1 as village_grid).FALV+(location_tmp1 as village_grid).FALV_tmp;
									set var: (location_tmp1 as village_grid).MALV_total value: (location_tmp1 as village_grid).MALV+(location_tmp1 as village_grid).MALV_tmp;
									set var: (location_tmp1 as village_grid).FAL_total value: (((location_tmp1 as village_grid).FAL+(location_tmp1 as village_grid).FAL_tmp)+((location_tmp1 as village_grid).FALV+(location_tmp1 as village_grid).FALV_tmp));
									set var: (location_tmp1 as village_grid).MAL_total value: (((location_tmp1 as village_grid).MAL+(location_tmp1 as village_grid).MAL_tmp)+((location_tmp1 as village_grid).MALV+(location_tmp1 as village_grid).MALV_tmp));
									set var: (location_tmp1 as village_grid).FAC_total value: (location_tmp1 as village_grid).FAC+(location_tmp1 as village_grid).FAC_tmp;
									set var: (location_tmp1 as village_grid).MAC_total value: (location_tmp1 as village_grid).MAC+(location_tmp1 as village_grid).MAC_tmp;
									set var: (location_tmp1 as village_grid).adultes_total value: (((location_tmp1 as village_grid).FAL_total+(location_tmp1 as village_grid).MAL_total)+((location_tmp1 as village_grid).FAC_total+(location_tmp1 as village_grid).MAC_total));
									let densite4 value: (location_tmp1 as village_grid).adultes_total;
									set var: densite4 value: densite4/100;
									if condition: !((location_tmp1 as village_grid).estRiziere != true) {
										set var: (location_tmp1 as village_grid).color value: (location_tmp1 as village_grid).oldColor;
									}
									if condition: ((densite4 > 0) and (10 >= densite4)) {
										set var: (location_tmp1 as village_grid).etat_infection value: 'tres leger';
									}
									if condition: ((densite4 > 10) and (2000 >= densite4)) {
										set var: (location_tmp1 as village_grid).color value: rgb[255, 180, 180];
										set var: (location_tmp1 as village_grid).etat_infection value: 'leger';
									}
									if condition: ((densite4 > 2000) and (5000 >= densite4)) {
										set var: (location_tmp1 as village_grid).color value: rgb[255, 80, 80];
										set var: (location_tmp1 as village_grid).etat_infection value: 'moyen';
									}
									if condition: ((densite4 > 5000) and (10000 > densite4)) {
										set var: (location_tmp1 as village_grid).color value: rgb[255, 0, 0];
										set var: (location_tmp1 as village_grid).etat_infection value: 'grave';
									}
									if condition: (densite4 >= 10000) {
										set var: (location_tmp1 as village_grid).color value: rgb[100, 0, 0];
										set var: (location_tmp1 as village_grid).etat_infection value: 'tres grave';
									}
									set var: self.FALP value: 0;
									set var: self.FALP_tmp value: 0;
									set var: self.MALP value: 0;
									set var: self.MALP_tmp value: 0;
									else {
										set var: self.FALV_tmp value: 0;
										set var: self.FALV value: 0;
										set var: self.MALV_tmp value: 0;
										set var: self.MALV value: 0;
										set var: self.FALV_total value: 0;
									}
								}
								else {
									let rizJeune2 value: self.cible1;
									let champsRizJeune2 value: ((self.rizJeune2).maFerme).rizieres;
									set var: FALV_partage value: self.FALV/length(champsRizJeune2);
									set var: FALV_tmp_partage value: self.FALV_tmp/length(champsRizJeune2);
									set var: MALV_partage value: self.MALV/length(champsRizJeune2);
									set var: MALV_tmp_partage value: self.MALV_tmp/length(champsRizJeune2);
									set var: self.FALV_tmp value: 0;
									set var: self.FALV value: 0;
									set var: self.MALV_tmp value: 0;
									set var: self.MALV value: 0;
									loop while: (!(empty(champsRizJeune2))) {
										let champRizJeune2 value: one_of(self.champsRizJeune2);
										set var: (village_grid (self.champRizJeune2)).FAL value: (village_grid (self.champRizJeune2)).FAL+self.FALV_partage;
										set var: (village_grid (self.champRizJeune2)).FAL_tmp value: (village_grid (self.champRizJeune2)).FAL_tmp+self.FALV_tmp_partage;
										set var: (village_grid (self.champRizJeune2)).MAL value: (village_grid (self.champRizJeune2)).MAL+self.MALV_partage;
										set var: (village_grid (self.champRizJeune2)).MAL_tmp value: (village_grid (self.champRizJeune2)).MAL_tmp+self.MALV_tmp_partage;
										set var: champsRizJeune2 value: self.champsRizJeune2-self.champRizJeune2;
									}
									set var: FALV_partage value: 0;
									set var: FALV_tmp_partage value: 0;
									set var: MALV_partage value: 0;
									set var: MALV_tmp_partage value: 0;
								}
							}
							else {
								set var: FALV_partage value: 0;
								set var: FALV_tmp_partage value: 0;
								set var: MALV_partage value: 0;
								set var: MALV_tmp_partage value: 0;
							}
						}
					}
					if condition: ((riziere(self.location)).ageRiz=3) {
						if condition: ((((riziere (self.location)).jourRestePropage) >= 0) and ((riziere (self.location)).state='estRepiquee')) {
							set FALP value: (self.FAL/2)+self.FALV;
							set FALP_tmp value: (self.FAL_tmp/2)+self.FALV_tmp;
							set MALP value: (self.MAL/2)+self.MALV;
							set MALP_tmp value: (self.MAL_tmp/2)+self.MALV_tmp;
							set var: self.FAL value: self.FAL-(self.FAL/2);
							set var: self.FAL_tmp value: self.FAL_tmp-(self.FAL_tmp/2);
							set var: self.MAL value: self.MAL-(self.MAL/2);
							set var: self.MAL_tmp value: self.MAL_tmp-(self.MAL_tmp/2);
							let locX value: ((self.location) at 0);
							let locY value: ((self.location) at 1);
							if condition: vent_Juillet_Decembre {
								set var: locX value: locX + 20;
								set var: locY value: locY - 20;
								else {
									set var: locX value: locX - 20;
									set var: locY value: locY;
								}
							}
							if condition: (locX >= gridSize) {
								set var: locX value: (locX - gridSize);
							}
							if condition: (0 > locX) {
								set var: locX value: (gridSize + locX);
							}
							if condition: (locY >= gridSize) {
								set var: locY value: (locY - gridSize);
							}
							if condition: (0 > locY) {
								set var: locY value: (gridSize + locY);
							}
							let location_tmp value: {locX,locY};
							set var: (location_tmp as village_grid).FALV value: self.FALP;
							set var: (location_tmp as village_grid).FALV_tmp value: self.FALP_tmp;
							set var: (location_tmp as village_grid).MALV value: self.MALP;
							set var: (location_tmp as village_grid).MALV_tmp value: self.MALP_tmp;
							set var: (location_tmp as village_grid).FALV_total value: (location_tmp as village_grid).FALV+(location_tmp as village_grid).FALV_tmp;
							set var: (location_tmp as village_grid).MALV_total value: (location_tmp as village_grid).MALV+(location_tmp as village_grid).MALV_tmp;
							set var: (location_tmp as village_grid).FAL_total value: (((location_tmp as village_grid).FAL+(location_tmp as village_grid).FAL_tmp)+((location_tmp as village_grid).FALV+(location_tmp as village_grid).FALV_tmp));
							set var: (location_tmp as village_grid).MAL_total value: (((location_tmp as village_grid).MAL+(location_tmp as village_grid).MAL_tmp)+((location_tmp as village_grid).MALV+(location_tmp as village_grid).MALV_tmp));
							set var: (location_tmp as village_grid).FAC_total value: (location_tmp as village_grid).FAC+(location_tmp as village_grid).FAC_tmp;
							set var: (location_tmp as village_grid).MAC_total value: (location_tmp as village_grid).MAC+(location_tmp as village_grid).MAC_tmp;
							set var: (location_tmp as village_grid).adultes_total value: (((location_tmp as village_grid).FAL_total+(location_tmp as village_grid).MAL_total)+((location_tmp as village_grid).FAC_total+(location_tmp as village_grid).MAC_total));
							let densite1 value: (location_tmp as village_grid).adultes_total;
							set var: densite1 value: densite1/100;
							if condition: ((location_tmp as village_grid).estRiziere != true){
								set var: (location_tmp as village_grid).color value: (location_tmp as village_grid).oldColor;
							}
							if condition: ((densite1 > 0) and (10 >= densite1)) {
								set var: (location_tmp as village_grid).etat_infection value: 'tres leger';
							}
							if condition: ((densite1 > 10) and (2000 >= densite1)) {
								set var: (location_tmp as village_grid).color value: rgb[255, 180, 180];
								set var: (location_tmp as village_grid).etat_infection value: 'leger';
							}
							if condition: ((densite1 > 2000) and (5000 >= densite1)) {
								set var: (location_tmp as village_grid).color value: rgb[255, 80, 80];
								set var: (location_tmp as village_grid).etat_infection value: 'moyen';
							}
							if condition: ((densite1 > 5000) and (10000 > densite1)) {
								set var: (location_tmp as village_grid).color value: rgb[255, 0, 0];
								set var: (location_tmp as village_grid).etat_infection value: 'grave';
							}
							if condition: (densite1 >= 10000) {
								set var: (location_tmp as village_grid).color value: rgb[100, 0, 0];
								set var: (location_tmp as village_grid).etat_infection value: 'tres grave';
							}
							set FALP value: 0;
							set FALP_tmp value: 0;
							set MALP value: 0;
							set MALP_tmp value: 0;
							set var: (riziere (self.location)).jourRestePropage value: ((riziere (self.location)).jourRestePropage-1);
							else {
								set FALP value: 0;
								set FALP_tmp value: 0;
								set MALP value: 0;
								set MALP_tmp value: 0;
							}
						}
					}
					if condition: (((riziere(self.location)).ageRiz=1) or ((riziere(self.location)).ageRiz=2)) {
						let champsRizJeune1 value: ((riziere(self.location)).maFerme).rizieres;
						set var: FALV_partage value: self.FALV/length(champsRizJeune1);
						set var: FALV_tmp_partage value: self.FALV_tmp/length(champsRizJeune1);
						set var: MALV_partage value: self.MALV/length(champsRizJeune1);
						set var: MALV_tmp_partage value: self.MALV_tmp/length(champsRizJeune1);
						set var: self.FALV_tmp value: 0;
						set var: self.FALV value: 0;
						set var: self.MALV_tmp value: 0;
						set var: self.MALV value: 0;
						loop while: (!(empty(champsRizJeune1))) {
							let champRizJeune1 value: one_of(self.champsRizJeune1);
							set var: (village_grid (self.champRizJeune1)).FAL value: (village_grid (self.champRizJeune1)).FAL + self.FALV_partage;
							set var: (village_grid (self.champRizJeune1)).FAL_tmp value: (village_grid (self.champRizJeune1)).FAL_tmp + FALV_tmp_partage;
							set var: (village_grid (self.champRizJeune1)).MAL value: (village_grid (self.champRizJeune1)).MAL + self.MALV_partage;
							set var: (village_grid (self.champRizJeune1)).MAL_tmp value: (village_grid (self.champRizJeune1)).MAL_tmp + MALV_tmp_partage;
							set var: champsRizJeune1 value: self.champsRizJeune1-self.champRizJeune1;
						}
						set var: FALV_partage value: 0;
						set var: FALV_tmp_partage value: 0;
						set var: MALV_partage value: 0;
						set var: MALV_tmp_partage value: 0;
					}
				}
			}
		}
		reflex densite_adultes_couleur {
			set var: oeufs_total value: self.oeufs;
			set var: nymphes_total value: self.nymphes+self.nymphes_tmp;
			set var: self.FALV_total value: self.FALV+self.FALV_tmp;
			set var: self.MALV_total value: self.MALV+self.MALV_tmp;
			set var: self.FAL_total value: (self.FAL+self.FAL_tmp);
			set var: self.MAL_total value: (self.MAL+self.MAL_tmp);
			set var: self.FAC_total value: self.FAC+self.FAC_tmp;
			set var: self.MAC_total value: self.MAC+self.MAC_tmp;
			set var: self.adultes_total value: ((self.FAL_total+self.MAL_total)+(self.FAC_total+self.MAC_total));
			let densite value: self.adultes_total;
			set var: densite value: densite/100;
			if condition: (self.estRiziere != true){
				set var: self.color value: self.oldColor;
				else {
					if condition: ((riziere (self.location).state)='terreLibre') {
						set var: self.color value: grisFonce;
					}
				}
			}
			if condition: ((densite > 0) and (10 >= densite)) {
				set var: etat_infection value: 'tres leger';
			}
			if condition: ((densite > 10) and (2000 >= densite)) {
				set var: self.color value: rgb[255, 180, 180];
				set var: etat_infection value: 'leger';
			}
			if condition: ((densite > 2000) and (5000 >= densite)) {
				set var: self.color value: rgb[255, 80, 80];
				set var: etat_infection value: 'moyen';
			}
			if condition: ((densite > 5000) and (10000 > densite)) {
				set var: self.color value: rgb[255, 0, 0];
				set var: etat_infection value: 'grave';
			}
			if condition: (densite >= 10000) {
				set var: self.color value: rgb[100, 0, 0];
				set var: etat_infection value: 'tres grave';
			}
		}
	}
}
entities {
	species ferme skills: [visible, situated] control: emf {
		var etat_agri type: string init: '';
		var color type: rgb init: blanc;
		var rizieres type: list init: [];
		var agriculteur type: agri init: nil;
		var nb_jour_AttendreSemer type: int value: (rnd(26)+14);
		var jour_RndPropage type: int value: (rnd(5)+7);
		init {
			if condition: avec_synchronisation {
				set var: nb_jour_AttendreSemer value: 14;
			}
			let zones value: ((village_grid (self.location) neighbours_at 1) where empty(each.agents));
			loop while: zones!=[] {
				if condition: village_grid(first(zones)).estCultivable {
					create species: riziere number: 1 {
						set var: location value: (first(myself).zones);
						set var: color value: grisFonce;
						set var: maFerme value: myself;
					}
					set var: (first(zones) as village_grid).estRiziere value: true;
					set var: rizieres value: rizieres +(first(zones)).location;
				}
				set var: zones value: zones-(first(zones));
			}
			create species: agri number: 1 {
				set var: location value: myself.location;
				set var: maFerme value: myself;
				set var: temps_attendreSemer value: myself.nb_jour_AttendreSemer;
			}
			set var: agriculteur value: agri (self.location);
		}
	}
	species agri skills: [situated, visible, moving] control: fsm {
		var color type: rgb init: rgb('black');
		var place type: village_grid value: location as village_grid;
		var maFerme type: ferme init: nil;
		var timeLeft type: int init: 0;
		var temps_attendreSemer type: int init: nil;
		var temps_a_propager type: int init: nil;
		action nonTraverser {
			let dest var: dest value: one_of (place neighbours_at 1);
			if condition: dest != nil {
				if condition: !dest.estFleuve {
					set var: location value: dest;
				}
			}
		}
		action semailles;
		action repiquage;
		action recolte;
		action goHome {
			do action: goto {
				arg target value: maFerme;
				arg speed type: float value: 3;
			}
		}
		state attendreSemer initial: true {
			enter {
				set var: self.timeLeft value: self.temps_attendreSemer * step;
			}
			set var: self.timeLeft value: self.timeLeft-1;
			transition to: chercherSemer when: self.timeLeft=0 {
				set var: color value: rgb('black');
			}
		}
		state chercherSemer {
			set var: color value: rgb('black');
			let nonSemee value: (one_of((self.maFerme).rizieres) as riziere);
			transition to: semer when: nonSemee!=nil {
				set var: maFerme.etat_agri value: 'semer';
				do action: goto {
					arg target value: nonSemee;
				}
				do action: semailles;
			}
		}
		state semer {
			transition to: attendreRepiquer when: true {
				do action: goHome;
				set var: color value: bleu;
			}
		}
		state attendreRepiquer {
			enter {
				set var: timeLeft value: 29 * step;
			}
			set var: timeLeft value: timeLeft-1;
			if condition: timeLeft=14 {
				let rizieresAvecAge1 value: (((ferme(self.maFerme)).rizieres));
				loop while: (!(empty(self.rizieresAvecAge1))) {
					let one_riz1 value: (one_of (self.rizieresAvecAge1));
					set var: (one_riz1 as riziere).ageRiz value: 1;
					set var: (one_riz1 as village_grid).nouvelleSaison value: true;
					set var: (one_riz1 as village_grid).saison value: (one_riz1 as village_grid).saison+1;
					set var: rizieresAvecAge1 value: self.rizieresAvecAge1-(self.one_riz1);
				}
			}
			transition to: chercherRepiquer when: timeLeft=0 {
				set var: color value: rgb('black');
			}
		}
		state chercherRepiquer {
			let nonRepiquee value: (one_of((self.maFerme).rizieres) as riziere);
			transition to: repiquer when: nonRepiquee!=nil {
				set var: maFerme.etat_agri value: 'repiquer';
				do action: goto {
					arg target value: nonRepiquee;
				}
				do action: repiquage;
			}
		}
		state repiquer {
			set var: color value: rgb('orange');
			do action: goHome;
			set var: temps_attendreSemer value: (self.maFerme).nb_jour_AttendreSemer;
			if condition: avec_synchronisation {
				set var: temps_attendreSemer value: 14;
			}
			let listRiz value: (((ferme(self.maFerme)).rizieres));
			transition to: attendreRecolter when: true {
				set var: color value: bleu;
			}
		}
		state attendreRecolter {
			enter {
				set var: timeLeft value: 59 * step;
			}
			set var: timeLeft value: timeLeft-1;
			if condition: self.timeLeft=50 {
				set var: self.temps_a_propager value: (self.maFerme).jour_RndPropage;
			}
			if condition: (self.timeLeft=self.temps_a_propager) {
				let listRiz value: (((ferme(self.maFerme)).rizieres));
				loop while: (!(empty(self.listRiz))) {
					let riz value: riziere (one_of (self.listRiz));
					set var: riz.jourRestePropage value: 7;
					set var: listRiz value: self.listRiz-(self.riz).location;
				}
			}
			if condition: timeLeft=30 {
				let rizieresAvecAge2 value: (((ferme(self.maFerme)).rizieres));
				loop while: (!(empty(self.rizieresAvecAge2))) {
					let one_riz2 value: riziere (one_of (self.rizieresAvecAge2));
					set var: one_riz2.ageRiz value: 2;
					set var: rizieresAvecAge2 value: self.rizieresAvecAge2-(self.one_riz2).location;
				}
			}
			if condition: timeLeft=15 {
				let rizieresAvecAge3 value: (((ferme(self.maFerme)).rizieres));
				loop while: (!(empty(self.rizieresAvecAge3))) {
					let one_riz3 value: riziere (one_of (self.rizieresAvecAge3));
					set var: one_riz3.ageRiz value: 3;
					set var: rizieresAvecAge3 value: self.rizieresAvecAge3-(self.one_riz3).location;
				}
			}
			transition to: chercherRecolter when: timeLeft=0 {
				set var: color value: rgb('black');
			}
		}
		state chercherRecolter {
			let nonRecoltee value: (one_of((self.maFerme).rizieres) as riziere);
			transition to: recolter when: nonRecoltee!=nil {
				let rizieresAvecAge0 value: (((ferme (self.maFerme)).rizieres));
				loop while: (!(empty(self.rizieresAvecAge0))) {
					let one_riz0 value: riziere (one_of (self.rizieresAvecAge0));
					set var: one_riz0.ageRiz value: 0;
					set var: rizieresAvecAge0 value: self.rizieresAvecAge0-(self.one_riz0).location;
				}
				set var: maFerme.etat_agri value: 'recolter';
				do action: goto {
					arg target value: nonRecoltee;
				}
				do action: recolte;
			}
		}
		state recolter {
			set var: (ferme(self.maFerme)).nb_jour_AttendreSemer value: rnd(30)+14;
			transition to: attendreSemer when: true {
				do action: goHome;
				set var: color value: bleu;
			}
		}
	}
	species riziere skills: [situated, visible] control: fsm {
		var color type: rgb init: blanc;
		var maFerme type: ferme init: nil;
		var ageRiz type: int init: 0;
		var jourRestePropage type: int init: -1;
		state terreLibre initial: true {
			set var: color value: grisFonce;
			transition to: estSemee when: ((state='terreLibre') and (((self.maFerme).etat_agri)='semer')) {
				set var: color value: blanc;
			}
		}
		state estSemee {
			transition to: estRepiquee when: ((state='estSemee') and (((self.maFerme).etat_agri)='repiquer')) {
				set var: color value: rgb('yellow');
			}
		}
		state estRepiquee {
			transition to: terreLibre when: ((state='estRepiquee') and (((self.maFerme).etat_agri)='recolter')) {
				set var: color value: grisFonce;
				set var: (village_grid (self.location)).FAL_tmp value: 0;
				set var: (village_grid (self.location)).FAL value: 0;
				set var: (village_grid (self.location)).MAL_tmp value: 0;
				set var: (village_grid (self.location)).MAL value: 0;
				set var: (village_grid (self.location)).FAC_tmp value: 0;
				set var: (village_grid (self.location)).FAC value: 0;
				set var: (village_grid (self.location)).MAC_tmp value: 0;
				set var: (village_grid (self.location)).MAC value: 0;
				set var: (village_grid (self.location)).nymphes value: 0;
				set var: (village_grid (self.location)).nymphes_tmp value: 0;
				set var: (village_grid (self.location)).adultes value: 0;
				set var: (village_grid (self.location)).adultes_tmp value: 0;
				set var: (village_grid (self.location)).oeufs value: (rnd(10000));
				set var: (village_grid (self.location)).oeufs_total value: (village_grid (self.location)).oeufs;
				set var: (village_grid (self.location)).nymphes_total value: (village_grid (self.location)).nymphes+(village_grid (self.location)).nymphes_tmp;
				set var: (village_grid (self.location)).adultes_total value: 0;
				set var: (village_grid (self.location)).color value: grisFonce;
				set var: (village_grid (self.location)).etat_infection value: '_';
			}
		}
	}
}
output {
	display grid {
		grid village_grid;
		species riziere;
		species ferme;
		species agri;
	}
	inspect name: 'Agents' type: agent refresh_every: 1;
	inspect name: 'Species' type: species refresh_every: 100;
}
