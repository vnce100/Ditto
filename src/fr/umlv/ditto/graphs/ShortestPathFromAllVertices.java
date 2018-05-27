package fr.umlv.ditto.graphs;

import java.util.Arrays;

public class ShortestPathFromAllVertices {
	private final int[][] d;
	private final int[][] pi;

	ShortestPathFromAllVertices(int[][] d, int[][] pi) {
		this.d = d;
		this.pi = pi;
	}

	public void printShortestPath(int source, int destination) {
		// à faire
	}

	public void printShortestPaths() {
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < d.length; j++) {
				if (i == j) {
					continue;
				}
				printShortestPath(i, j);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder bf = new StringBuilder();
		for (int i = 0; i < d.length; i++) {
			bf.append(Arrays.toString(d[i])).append("\t").append(Arrays.toString(pi[i])).append("\n");
		}
		return bf.toString();
	}
}
