/*********************************************************************************************
 * 
 *
 * 'Graph.java', in plugin 'ummisco.gama.gpu', is part of the source code of the 
 * GAMA modeling and simulation platform.
 * (c) 2007-2014 UMI 209 UMMISCO IRD/UPMC & Partners
 * 
 * Visit https://code.google.com/p/gama-platform/ for license information and developers contact.
 * 
 * 
 **********************************************************************************************/
package ssps.graph;

import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import msi.gama.util.GamaMap;
import ssps.algorithm.Dijkstra;


/**
 * 
 * @author mrkhai
 */
public class Graph {
	private int vertex_list[];
	private int edge_list[];
	private float weight_list[];
	private int vertex_count;
	private Dijkstra dijkstraGPU;
	GamaMap<Object, Integer> vertexId;

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
	
	public Graph(int nbVertices, int nbEdges) {
		vertex_count = nbVertices;
		edge_count = nbEdges;
		vertex_list = new int[vertex_count];
		edge_list = new int[nbEdges];
		weight_list = new float[nbEdges];
		generateRandomGraph(nbVertices,nbEdges);
		dijkstraGPU = new Dijkstra();
		
	}

	public Graph(int[] vertices, int[] edges, float[] weights, int nbVertices, int nbEdges, GamaMap<Object, Integer> vertexId) {
		vertex_list = vertices;
		edge_list = edges;
		weight_list = weights;
		vertex_count = nbVertices;
		edge_count = nbEdges;
		dijkstraGPU = new Dijkstra();
		this.vertexId = vertexId;
		
	}

	public Graph(int vertexs[], int edges[], float weighs[], int nb_vertex,
			int nb_edge) {
		vertex_list = vertexs;
		edge_list = edges;
		weight_list = weighs;
		vertex_count = nb_vertex;
		edge_count = nb_edge;
		dijkstraGPU = new Dijkstra();
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

	public Dijkstra getDijkstraGPU() {
		return dijkstraGPU;
	}

	public void setDijkstraGPU(Dijkstra dijkstraGPU) {
		this.dijkstraGPU = dijkstraGPU;
	}

	public GamaMap<Object, Integer> getVertexId() {
		return vertexId;
	}

	public void setVertexId(GamaMap<Object, Integer> vertexId) {
		this.vertexId = vertexId;
	}
	
	

}
