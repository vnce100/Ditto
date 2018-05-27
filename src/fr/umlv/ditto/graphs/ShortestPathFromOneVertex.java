package fr.umlv.ditto.graphs;

import java.util.Arrays;

public class ShortestPathFromOneVertex {
	private final int source;
	private final int[] d;
	private final int[] pi;

	ShortestPathFromOneVertex(int source, int[] d, int[] pi) {
		this.source = source;
		this.d = d;
		this.pi = pi;
	}

	public void printShortestPathTo(int destination) {
		if(destination > d.length) {
			throw new IllegalArgumentException("destination is superior than number of vertices ...");
		}
		StringBuilder sb = new StringBuilder();
		int nextNode = destination;
		while(nextNode != source) {
			sb.append(nextNode).append(" <-- ");
			nextNode = pi[nextNode];
		}
		sb.append(source);
		System.out.println(sb.toString());
		
	}

	public void printShortestPaths() {
		for (int i = 0; i < d.length; i++) {
			if (i == source) {
				continue;
			}
			printShortestPathTo(i);
		}
	}

	@Override
	public String toString() {
		return source + " " + Arrays.toString(d) + " " + Arrays.toString(pi);
	}
}
