package fr.umlv.ditto.graphs;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class MatGraph implements Graph {
	private final int[][] mat;
	private final int nodes; // number of nodes
	private int edges;

	/**
	 * 
	 * @param nodes
	 */
	public MatGraph(int nodes) {
		if (nodes < 1) {
			throw new IllegalArgumentException("Number of nodes should be positive (founded: " + nodes + ")");
		}
		this.nodes = nodes;
		this.mat = new int[nodes][nodes];
	}

	/**
	 * 
	 */
	@Override
	public int numberOfEdges() {
		return edges;
	}

	/**
	 * 
	 */
	@Override
	public int numberOfVertices() {
		return nodes;
	}

	/**
	 * 
	 */
	@Override
	public void addEdge(int src, int dst, int value) {
		if (mat[checkNode(src)][checkNode(dst)] == 0) {
			edges++;
		}
		mat[src][dst] = checkValue(value);
	}

	/**
	 * 
	 */
	@Override
	public boolean isEdge(int src, int dst) {
		return (mat[checkNode(src)][checkNode(dst)] != 0) ? true : false;
	}

	/**
	 * 
	 */
	@Override
	public int getValue(int src, int dst) {
		int w = mat[checkNode(src)][checkNode(dst)];
		if (w == 0) {
			return Edge.NULL_VALUE;
		}
		return w;
	}

	/**
	 * 
	 */
	@Override
	public Iterator<Edge> edgeIterator(int i) {
		return new Iterator<Edge>() {
			private int j = 0;

			@Override
			public boolean hasNext() {
				while (j < nodes && mat[i][j] == 0) {
					j++;
				}
				return j != nodes;
			}

			@Override
			public Edge next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}
				Edge nextEdge = new Edge(i, j, mat[i][j]);
				j++;
				return nextEdge;
			}
		};
	}

	/**
	 * 
	 */
	@Override
	public void forEachEdge(int i, Consumer<Edge> consumer) {
		Iterator<Edge> it = this.edgeIterator(i);
		while (it.hasNext()) {
			consumer.accept(it.next());
		}

	}

	private int checkNode(int n) {
		if (n < 0 || n > nodes) {
			throw new IllegalArgumentException("Node is not in the matrix");
		}
		return n;
	}

	private int checkValue(int value) {
		if (value == 0) {
			//throw new IllegalArgumentException("Value 0 is not allowed");
		}
		return value;
	}
}
