package fr.umlv.ditto.graphs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.IntFunction;

public class Graphs {
	private static int RANDOM_MAX_VALUE = 100;

	/**
	 * @throws IOException
	 * 
	 */
	public static MatGraph createMatGraph(URI matFile) throws IOException {
		Objects.requireNonNull(matFile);
		GraphDescription gd = Graphs.parseMatFile(matFile);
		MatGraph g = new MatGraph(gd.nodes());
		gd.edges().forEach(e -> {
			g.addEdge(e.getStart(), e.getEnd(), e.getValue());
		});
		return g;
	}

	/**
	 * @throws IOException
	 * 
	 */
	public static AdjGraph createAdjGraph(URI matFile) throws IOException {
		Objects.requireNonNull(matFile);
		GraphDescription gd = Graphs.parseMatFile(matFile);
		AdjGraph g = new AdjGraph(gd.nodes());
		gd.edges().forEach(e -> {
			g.addEdge(e.getStart(), e.getEnd(), e.getValue());
		});
		return g;
	}

	static GraphDescription parseMatFile(URI matFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(Objects.requireNonNull(matFile)));
		GraphDescription gd = new GraphDescription(Integer.parseInt(lines.get(0)));
		int x = 0; // line in the matrix
		for (String line : lines.subList(1, lines.size())) {
			try (Scanner scanner = new Scanner(line)) {
				int y = 0; // column in the matrix
				while (scanner.hasNextInt()) {
					Edge e = new Edge(x, y, scanner.nextInt());
					gd.edges.add(e);
					y++;
				}
				x++;
			}
		}
		return gd;
	}

	/**
	 * 
	 * @param nodes
	 * @param edges
	 * @return
	 */
	public static Graph generate(int nodes, int edges) {
		if (nodes < 1) {
			throw new IllegalArgumentException("Number of nodes should be positive (founded: " + nodes + ")");
		}
		if (edges < nodes || edges > nodes * nodes) {
			throw new IllegalArgumentException(
					"Number of edges should be greater than nodes and smaller than nodes² (founded: " + nodes + ")");
		}
		Graph g = new MatGraph(nodes);
		Random random = new Random();
		while (g.numberOfEdges() != edges) {
			int val = random.nextInt() % RANDOM_MAX_VALUE + 1; // avoid 0 value
			g.addEdge(random.nextInt() % nodes, random.nextInt() % nodes, val);
		}
		return g;
	}

	// Parcours profondeur
	public static List<Integer> DFS(Graph g) {
		List<Integer> list = new ArrayList<>();
		HashSet<Integer> hashSet = new HashSet<>();

		for (int i = 0; i < g.numberOfVertices(); i++) {
			if (!hashSet.contains(i))
				DFSrec(g, i, hashSet, list);
		}
		return list;
	}

	private static void DFSrec(Graph g, int i, HashSet<Integer> hashSet, List<Integer> list) {
		System.out.println("Début i: " + i);
		hashSet.add(i);
		list.add(i);
		g.forEachEdge(i, e -> {
			if (!hashSet.contains(e.getEnd()))
				DFSrec(g, e.getEnd(), hashSet, list);
		});
		System.out.println("Fin i: " + i);
	}

	// Parcours largeur

	public static List<Integer> BFS(Graph g) {
		List<Integer> list = new ArrayList<>();
		HashSet<Integer> hashSet = new HashSet<>();
		LinkedList<Integer> linkedList = new LinkedList<Integer>();

		for (int i = 0; i < g.numberOfVertices(); i++) {
			if (!hashSet.contains(i))
				BFSrec(g, i, hashSet, linkedList, list);
		}

		return list;
	}

	private static void BFSrec(Graph g, int i, HashSet<Integer> hashSet, LinkedList<Integer> linkedList,
			List<Integer> list) {
		linkedList.add(i);
		hashSet.contains(i);
		while (!linkedList.isEmpty()) {
			i = (int) linkedList.removeFirst();
			list.add(i);
			g.forEachEdge(i, e -> {
				if (!hashSet.contains(e.getEnd())) {
					linkedList.add(e.getEnd());
					hashSet.add(e.getEnd());
				}
			});
		}
	}

	public static int[][] timedDepthFirstSearch(Graph g) {
		Objects.requireNonNull(g);
		int[][] times = new int[g.numberOfVertices()][2];
		LongAdder chrono = new LongAdder();
		for (int i = 0; i < g.numberOfVertices(); i++) {
			times[i][0] = -1;
			times[i][1] = -1;
		}
		for (int i = 0; i < g.numberOfVertices(); i++) {
			if (times[i][0] == -1) {
				times = timedDepthFirstSearchRec(g, i, times, chrono);
			}
		}
		return times;
	}

	private static int[][] timedDepthFirstSearchRec(Graph g, int node, int[][] times, LongAdder chrono) {
		times[node][0] = chrono.intValue();
		chrono.increment();
		g.forEachEdge(node, e -> {
			if (times[e.getEnd()][0] == 0) {
				timedDepthFirstSearchRec(g, e.getEnd(), times, chrono);
				times[e.getEnd()][1] = chrono.intValue();
				chrono.increment();
			}
		});
		return times;
	}

	public static List<Integer> topologicalSort(Graph g, boolean cycleDetect) {
		Objects.requireNonNull(g);
		List<Integer> finishedOrder = new ArrayList<>(g.numberOfVertices());
		int[][] times = new int[g.numberOfVertices()][2];
		LongAdder chrono = new LongAdder();
		for (int i = 0; i < g.numberOfVertices(); i++) {
			times[i][0] = -1;
			times[i][1] = -1;
		}
		for (int i = 0; i < g.numberOfVertices(); i++) {
			if (times[i][0] == -1) {
				times = topologicalSortRec(g, i, times, chrono, finishedOrder);
			}
		}
		return finishedOrder;
	}

	private static int[][] topologicalSortRec(Graph g, int node, int[][] times, LongAdder chrono,
			List<Integer> finishedOrder) {
		times[node][0] = chrono.intValue();
		chrono.increment();
		g.forEachEdge(node, e -> {
			if (times[e.getEnd()][0] == 0) {
				timedDepthFirstSearchRec(g, e.getEnd(), times, chrono);
				times[e.getEnd()][1] = chrono.intValue();
				chrono.increment();
				finishedOrder.add(e.getEnd());
			}
		});
		return times;
	}

	static class GraphDescription {
		private int nodes; // nodes number
		private final List<Edge> edges; // edges liste

		GraphDescription(int nodes) {
			if (nodes < 1) {
				throw new IllegalArgumentException("Number of nodes should be positive (value: " + nodes + ")");
			}
			this.nodes = nodes;
			this.edges = new ArrayList<>();
		}

		List<Edge> edges() {
			return edges;
		}

		int nodes() {
			return nodes;
		}
	}

	public static ShortestPathFromOneVertex bellmanFord(Graph g, int source) {
		if (source >= g.numberOfEdges()) {
			throw new IllegalArgumentException(
					"ShortestPathFromOneVertex: source can't be superior to numberOfEdges in the graph");
		}
		BitSet somethingChanged = new BitSet(1);
		int d[] = new int[g.numberOfVertices()];
		int pi[] = new int[g.numberOfVertices()];
		for (int j = 0; j < g.numberOfVertices(); j++) {
			d[j] = Integer.MAX_VALUE;
			pi[j] = -1;
		}
		d[source] = 0;
		for (int i = 1; i < g.numberOfVertices(); i++) {
			somethingChanged.set(1, false);
			for (int j = 0; j < g.numberOfVertices(); j++) {
				g.forEachEdge(j, e -> {
					if (d[e.getEnd()] > d[e.getStart()] + e.getValue()) {
						if (e.getEnd() == source) {
							throw new IllegalStateException(
									"ShortestPathFromOneVertex: negative cycle detected or impossible path");
						}
						d[e.getEnd()] = d[e.getStart()] + e.getValue();
						pi[e.getEnd()] = e.getStart();
						somethingChanged.set(1, true);
					}
				});
			}
			if (somethingChanged.get(1)) { // sort de la boucle quand le meilleur résultat est déjà atteint
				break;
			}
		}
		return new ShortestPathFromOneVertex(source, d, pi);
	}

	/**
	 * Création d'un graphe à partir d'un fichier contenant le nombre de sommets
	 * et sa matrice
	 * 
	 * @param path
	 *            le chemin du fichier contenant la matrice du graphe
	 * @param factory
	 *            une méthode qui étant donné un nombre de sommets n, fabrique et
	 *            renvoie yun graphe vide à n sommets
	 * @return un graphe construit à l'aide de factory et dont les arêtes sont
	 *         données dans le fihier indiqué dans path
	 * @throws IOException
	 */

	public static Graph makeGraphFromMatrixFile(Path path, IntFunction<Graph> factory) throws IOException {
		try {
			List<String> contents = Files.readAllLines(path);
			// for (String s : contents) { System.out.println(s); }
			int size = Integer.valueOf(contents.get(0));
			contents.remove(0);
			Graph graph = factory.apply(size);
			for (int i = 0; i < size; i++) {
				String[] lineTokens = contents.get(i).split(" ");
				for (int j = 0; j < size; j++) {
					graph.addEdge(i, j, Integer.valueOf(lineTokens[j]));
				}

			}
			return graph;
		} catch (IOException e) {
			System.out.println("ERROR");
		}
		return null;

	}

	public static List<Integer> BFS(Graph graph, int source, int target) {
		List<Integer> path = new ArrayList<>();
		HashSet<Integer> visited = new HashSet<>();
		LinkedList<Integer> queue = new LinkedList<Integer>();
		queue.add(source);
		visited.add(source);
		while (queue.size() != 0) {
			int u = queue.poll();
			for (int v = 0; v < graph.numberOfVertices(); v++) {
				if (!visited.contains(v) && (graph.getValue(u, v) != Edge.NULL_VALUE)) {
					queue.add(v);
					visited.add(v);
				}
			}
		}
		return path;
	}

	/**
	 * 
	 * @param graph
	 * @param source
	 * @param target
	 * @return
	 */
	public static int EdmonsKarp(Graph graph, int source, int target) {
		int totalFlow = 0;
		while (true) {
			List<Integer> augmentingPath = BFS(graph, source, target);
			if (augmentingPath.isEmpty()) {
				int f = augmentingPath.stream().min(Integer::min).get();
				for (int i : augmentingPath) {
					graph.forEachEdge(i, e -> {
						decreaseCapacity(graph, e.getStart(), e.getEnd(), f);
					});
				}
			} else {
				return totalFlow;
			}
		}
	}

	private static void decreaseCapacity(Graph graph, int start, int end, int f) {
		graph.addEdge(start, end, graph.getValue(start, end) - f);
		graph.addEdge(end, start, graph.getValue(end, start) + f);
	}
}
