import java.util.*;
import java.io.*;

public class Main {

	static final String trainData = "data/train-images.idx3-ubyte";
	static final String trainLabels = "data/train-labels.idx1-ubyte";
	static final String testData = "data/t10k-images.idx3-ubyte";
	static final String testLabels = "data/t10k-labels.idx1-ubyte";

	static int readInt(BufferedReader in) throws IOException {
		int z = 0;
		for (int i = 0; i < 4; i++) {
			z = (z << 8) | in.read();
		}
		return z;
	}

	static class BufferedReader {
		InputStream in;
		byte[] buffer = new byte[1 << 15];
		int pos = 0, total = 0;
		int MASK = 255;

		public BufferedReader(InputStream in) {
			this.in = in;
		}

		int read() throws IOException {
			if (pos == total) {
				pos = 0;
				total = in.read(buffer);
			}
			return buffer[pos++] & MASK;
		}

	}

	static ArrayList<Data> readData(String dataPath, String labelsPath)
			throws IOException {
		ArrayList<Data> res = new ArrayList<>();
		{
			BufferedReader in = new BufferedReader(new FileInputStream(
					new File(dataPath)));
			readInt(in);
			int n = readInt(in);
			int r = readInt(in);
			int c = readInt(in);
			for (int i = 0; i < n; i++) {
				int[][] vals = new int[r][c];
				for (int j = 0; j < r; j++)
					for (int k = 0; k < c; k++) {
						vals[j][k] = in.read();
					}
				res.add(new Data(vals));
			}
		}
		{
			BufferedReader in = new BufferedReader(new FileInputStream(
					new File(labelsPath)));
			readInt(in);
			int n = readInt(in);
			for (int i = 0; i < n; i++) {
				res.get(i).realValue = in.read();
			}
		}
		return res;
	}

	static class Node {
		ArrayList<Edge> g;
		double value;
		double alpha = rnd.nextDouble();
		double delta;

		Node() {
			g = new ArrayList<>();
		}

		@Override
		public String toString() {
			return "Node [value=" + value + ", delta=" + delta + "]";
		}

		Node(double value) {
			this.value = value;
			g = new ArrayList<>();
		}
	}

	final static double EPS = 0.1;

	static class Edge {
		Node to;
		double w;

		public Edge(Node to) {
			super();
			this.to = to;
			this.w = (rnd.nextDouble() - 0.5) * EPS;
		}

	}

	private static final double f(double x, double alpha) {
		return 1 / (1 + Math.exp(-2 * alpha * x));
	}

	private static final Random rnd = new Random(12443);
//	private static final int NUMBER_OF_NODES = 1000;
	private static final int DIGITS_COUNT = 10;
	static double eta = 0.1;

	static class NeuralNetwork {
		Node[][] start;
		ArrayList<Node> end;
		ArrayList<Node>[] nodes;

		int classify(Data data) {
			int r = data.vals.length, c = data.vals[0].length;
			for (int i = 0; i < r; i++) {
				for (int j = 0; j < c; j++) {
					start[i][j].value = data.vals[i][j] / 255.;
				}
			}
			for (int layer = 0; layer + 1 < nodes.length; layer++) {
				for (Node x : nodes[layer + 1]) {
					x.value = 0;
				}
			}
			for (int layer = 0; layer + 1 < nodes.length; layer++) {
				for (Node x : nodes[layer]) {
					for (Edge e : x.g) {
						e.to.value += e.w * x.value;
					}
				}
				for (Node x : nodes[layer + 1]) {
					x.value = f(x.value, x.alpha);
				}
			}
			int max = 0;
			for (int i = 1; i < DIGITS_COUNT; i++) {
				if (end.get(i).value > end.get(max).value) {
					max = i;
				}
			}
			return max;
		}
	}

	static NeuralNetwork learn(ArrayList<Data> data) {
		NeuralNetwork res = new NeuralNetwork();
		int r = data.get(0).vals.length, c = data.get(0).vals[0].length;
		Node[][] start = new Node[r][c];
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				start[i][j] = new Node();
			}
		}
		ArrayList<Node>[] nodes = new ArrayList[4];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = new ArrayList<>();
		}
		for (int i = 0; i < r; i++) {
			for (int j = 0; j < c; j++) {
				nodes[0].add(start[i][j]);
			}
		}
//		for (int i = 0; i < NUMBER_OF_NODES; i++) {
//			Node node = new Node();
//			for (Node x : nodes[0]) {
//				x.g.add(new Edge(node));
//			}
//			nodes[1].add(node);
//		}
		for (int i = 0; i < DIGITS_COUNT; i++) {
			Node node = new Node();
			for (Node x : nodes[0]) {
				x.g.add(new Edge(node));
			}
			nodes[2].add(node);
		}
//		Node[] constant = new Node[2];
//		for (int i = 0; i < constant.length; i++) {
//			constant[i] = new Node(1.0);
//			for (Node another : nodes[i + 1]) {
//				constant[i].g.add(new Edge(another));
//			}
//			nodes[i].add(constant[i]);
//		}
		for (int iter = 0; iter < 50000; iter++) {
//			double sumW = 0;
			System.err.println("ITER = " + iter);
			Data cData = data.get(rnd.nextInt(data.size()));
			double err2 = -1;
			int it = 0;
			while (true) {
				it++;
				if (it == 500) {
					System.err.println("break");
					break;
				}
				double err = 0;
				for (int i = 0; i < r; i++) {
					for (int j = 0; j < c; j++) {
						start[i][j].value = cData.vals[i][j] / 255.0;
					}
				}
				for (int layer = 0; layer < 3; layer++) {
					for (Node xx : nodes[layer + 1]) {
						xx.value = 0;
					}
				}
				for (int layer = 0; layer < 3; layer++) {
					for (Node xx : nodes[layer]) {
						for (Edge e : xx.g) {
							e.to.value += e.w * xx.value;
						}
					}
					for (Node xx : nodes[layer + 1]) {
						xx.value = f(xx.value, xx.alpha);
					}
				}
				int[] need = new int[DIGITS_COUNT];
				need[cData.realValue] = 1;
				for (int i = 0; i < DIGITS_COUNT; i++) {
					double o_i = nodes[2].get(i).value;
					nodes[2].get(i).delta = -o_i * (1 - o_i) * (need[i] - o_i);
					err += (need[i] - o_i) * (need[i] - o_i);
				}
				for (int layer = 1; layer >= 0; layer--) {
					for (Node x : nodes[layer]) {
						x.delta = 0;
						for (Edge e : x.g) {
							x.delta += e.w * e.to.delta;
						}
						x.delta *= x.value * (1 - x.value);
					}
				}
				for (int layer = 0; layer < 3; layer++) {
					for (Node node : nodes[layer]) {
						for (Edge e : node.g) {
							double deltaW = -eta * e.to.delta * node.value;
							e.w += deltaW;
						}
					}
				}
				if (err2 == -1) {
//					System.err.println(err);
				}
				err2 = err;
				if (Math.abs(err) < 1e-2) {
					break;
				}
			}
			// System.err.println(err2);
//			System.err.println("sumW = " + sumW);
		}
		res.start = start;
		res.end = nodes[2];
		res.nodes = nodes;
		return res;
	}

	public static void main(String[] args) throws IOException {
		ArrayList<Data> data = readData(trainData, trainLabels);
		System.err.println("READ");
		NeuralNetwork network = learn(data);
		ArrayList<Data> test = readData(testData, testLabels);
		int ok = 0;
		int bad = 0;
		PrintWriter out = new PrintWriter(new File("log.log"));
		for (Data cData : test) {
			if ((ok + bad) % 100 == 0) {
				System.err.println("done testing " + (ok + bad) + " -> "
						+ (ok / (ok + bad + 0.)));
			}
			int my = network.classify(cData);
			if (my == cData.realValue) {
				ok++;
			} else {
				bad++;
				if (bad < 10) {
					out.write(cData.toString());
					out.write("my res = " + my + "\n\n");
					out.flush();
				}
			}
		}
		out.close();
		System.err.println(ok / (test.size() + 0.0));
	}
}
