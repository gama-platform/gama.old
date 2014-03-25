/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ssps.graph;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import ssps.util.P;

/**
 * 
 * @author mrkhai
 */
public class Graph {
	private int vertex_list[];
	private int edge_list[];
	private float weight_list[];
	private int vertex_count;

	public int[] getVertex_list() {
		return vertex_list;
	}

	public int[] getEdge_list() {
		return edge_list;
	}

	public float[] getWeight_list() {
		return weight_list;
	}

	public int getVertex_count() {
		return vertex_count;
	}

	public int getEdge_count() {
		return edge_count;
	}

	int edge_count;

	public Graph() {
		vertex_list = new int[P.V_MAX];
		edge_list = new int[P.E_MAX];
		weight_list = new float[P.E_MAX];
		vertex_count = 0;
		edge_count = 0;
	}

	public Graph(int vertexs[], int edges[], float weighs[], int nb_vertex,
			int nb_edge) {
		vertex_list = vertexs;
		edge_list = edges;
		weight_list = weighs;
		vertex_count = nb_vertex;
		edge_count = nb_edge;
	}

	public void input(String file_name) {
		FileReader read;
		Scanner scan;
		int _nb_edge;
		int _num_vertex;
		float _weigh;
		int _nb_vertex;
		try {
			read = new FileReader(file_name);
			scan = new Scanner(read);
			_nb_vertex = scan.nextInt();
			for (int i = 0; i < _nb_vertex; i++) {
				_nb_edge = scan.nextInt();
				vertex_list[vertex_count] = edge_count;
				vertex_count++;
				if (_nb_edge > 0) {
					for (int j = 0; j < _nb_edge; j++) {
						_num_vertex = scan.nextInt();
						_weigh = scan.nextFloat();
						edge_list[edge_count] = _num_vertex;
						weight_list[edge_count] = _weigh;
						edge_count++;
					}
				}
			}
			scan.close();
		} catch (IOException e) {
		}
	}

	public void output() {
		int start;
		int end;
		for (int i = 0; i < vertex_count; i++) {
			System.out.println();
			System.out.print("v: " + i);
			start = vertex_list[i];
			if (i + 1 < vertex_count)
				end = vertex_list[i + 1];
			else
				end = edge_count;
			for (int j = start; j < end; j++) {
				System.out.print(" to " + edge_list[j] + " w: "
						+ weight_list[j] + " ");
			}
		}
	}

	public void output(String file_name) {
		try {
			FileWriter writer = new FileWriter(file_name);
			PrintWriter printer = new PrintWriter(writer);
			printer.println(vertex_count);
			int start;
			int end;
			for (int i = 0; i < vertex_count; i++) {
				start = vertex_list[i];
				if (i + 1 < vertex_count)
					end = vertex_list[i + 1];
				else
					end = edge_count;
				printer.print(end - start + "\t");
				for (int j = start; j < end; j++) {
					printer.print(" " + edge_list[j] + " " + weight_list[j]);
				}
				printer.println();
			}
			printer.close();
		} catch (IOException ex) {
			Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/*
	 * public void generateRandomGraph(int nb_vertice, int nb_edge) { if
	 * (nb_vertice > nb_edge || nb_edge > nb_vertice * nb_vertice) return;
	 * Random rand = new Random(); this.vertex_count = nb_vertice;
	 * this.vertex_list = new int[this.vertex_count]; this.edge_count = nb_edge;
	 * this.edge_list = new int[nb_edge]; this.weight_list = new float[nb_edge];
	 * int small = 1; int big = 100; int start; int end; int temp; int
	 * nb_neighbor_per_vertex = nb_edge / nb_vertice; int smaller_v_rand = 0;
	 * int bigger_v_rand; for (int i = 0; i < nb_vertice; i++) {
	 * this.vertex_list[i] = i * nb_neighbor_per_vertex; } for (int i = 0; i <
	 * nb_edge; i++) { for (int v = 0; v < nb_vertice; v++) { start =
	 * vertex_list[v]; if (v + 1 < nb_vertice) end = vertex_list[v + 1]; else
	 * end = nb_edge; if (i >= start && i < end) { if (v == 1) smaller_v_rand =
	 * 0; if (v > 1) { smaller_v_rand = rand.nextInt(v - 1); } if(v + 1 <
	 * nb_vertice) { temp = rand.nextInt(nb_vertice-v); if(temp == 0)
	 * bigger_v_rand = v + 1; else bigger_v_rand = v + temp; } else
	 * bigger_v_rand = v - 1; if (rand.nextBoolean()) this.edge_list[i] =
	 * smaller_v_rand; else this.edge_list[i] = bigger_v_rand; } }
	 * this.weight_list[i] = small + (big - small) * rand.nextFloat(); } }
	 */

	public void generateRandomGraph(int nb_vertice, int nb_edge) {
		Random rand = new Random();
		this.vertex_count = nb_vertice;
		this.vertex_list = new int[this.vertex_count];
		this.edge_count = nb_edge;
		this.edge_list = new int[nb_edge];
		this.weight_list = new float[nb_edge];
		int start = 1;
		int end = 100;
		int nb_neighbor_per_vertex = nb_edge / nb_vertice;
		for (int i = 0; i < nb_vertice; i++) {
			this.vertex_list[i] = i * nb_neighbor_per_vertex;
		}
		for (int i = 0; i < nb_edge; i++) {
			this.edge_list[i] = rand.nextInt(nb_vertice);
			this.weight_list[i] = start + (end - start) * rand.nextFloat();
		}
	}

	public void optimiserGraph() {
		int start;
		int end;
		float edge_commun_smaller_weigh[] = new float[vertex_count];
		for (int v = 0; v < vertex_count; v++) {
			start = vertex_list[v];
			if (v + 1 < vertex_count)
				end = vertex_list[v + 1];
			else
				end = edge_count;
			for (int j = 0; j < vertex_count; j++)
				edge_commun_smaller_weigh[j] = Float.MAX_VALUE;
			for (int i = start; i < end; i++) {
				if (edge_commun_smaller_weigh[edge_list[i]] > this.weight_list[i])
					edge_commun_smaller_weigh[edge_list[i]] = this.weight_list[i];
			}

			for (int i = start; i < end; i++) {
				this.weight_list[i] = edge_commun_smaller_weigh[edge_list[i]];
				if (edge_list[i] == v)
					this.weight_list[i] = 0;
			}
		}
	}

}
