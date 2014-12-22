import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	final static String trainData = "data/arcene_train.data";
	final static String trainClasses = "data/arcene_train.labels";
	final static String testData = "data/arcene_valid.data";
	final static String testClasses = "data/arcene_valid.labels";

	static ArrayList<Data> read(File data, File classes) {
		ArrayList<Data> res = new ArrayList<>();
		FastScanner in = new FastScanner(data);
		while (true) {
			String s = in.nextLine();
			if (s == null) {
				break;
			}
			res.add(new Data(s));
		}
		in = new FastScanner(classes);
		for (int i = 0; i < res.size(); i++) {
			res.get(i).setClassId(in.nextInt());
		}
		return res;
	}

	static double log2(double x) {
		return Math.log(x) / Math.log(2);
	}

	static double entropy(ArrayList<Data> data) {
		int cnt1 = 0;
		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).classId == 1) {
				cnt1++;
			}
		}
		if (cnt1 == 0 || cnt1 == data.size()) {
			return 0.0;
		}
		double k1 = cnt1 / 1.0 / data.size(), k2 = 1 - k1;
		return -k1 * log2(k1) - k2 * log2(k2);
	}

	static Node genTree(ArrayList<Data> data) {
		int classId = data.get(0).classId;
		for (Data cData : data) {
			if (cData.classId != classId) {
				classId = 0;
			}
		}
		if (classId != 0) {
			return new Node(classId);
		}
		double minEntropy = Double.MAX_VALUE;
		Node bestDivisor = null;
		// for (int i = 0; i < data.get(0).vals.length; i++) {
		for (int i : use) {
			for (int j = 0; j < data.size(); j++) {
				int curDiv = data.get(j).vals[i];
				ArrayList<Data>[] parts = divide(data, i, curDiv);
				if (parts[0].size() == 0 || parts[1].size() == 0) {
					continue;
				}
				double entropy = (parts[0].size() * entropy(parts[0]) + parts[1]
						.size() * entropy(parts[1]))
						/ data.size();
				if (entropy < minEntropy) {
					minEntropy = entropy;
					bestDivisor = new Node(i, curDiv);
				}
			}
		}
		if (bestDivisor == null) {
			int class1 = 0;
			for (Data cData : data) {
				if (cData.classId == 1) {
					class1++;
				}
			}
			if (class1 + class1 >= data.size()) {
				return new Node(1);
			} else {
				return new Node(-1);
			}
		} else {
			ArrayList<Data>[] parts = divide(data, bestDivisor.featureId,
					bestDivisor.value);
			bestDivisor.left = genTree(parts[0]);
			bestDivisor.right = genTree(parts[1]);
			return bestDivisor;
		}
	}

	static int classify(Node node, Data data) {
		if (node.classId != 0) {
			return node.classId;
		}
		if (data.vals[node.featureId] <= node.value) {
			return classify(node.left, data);
		} else {
			return classify(node.right, data);
		}
	}

	static ArrayList<Data>[] divide(ArrayList<Data> data, int feature, int val) {
		@SuppressWarnings("unchecked")
		ArrayList<Data>[] res = new ArrayList[] { new ArrayList<>(),
				new ArrayList<>() };
		for (Data cData : data) {
			if (cData.vals[feature] <= val) {
				res[0].add(cData);
			} else {
				res[1].add(cData);
			}
		}
		return res;
	}

	static double fMeasure(Node node, ArrayList<Data> data) {
		int tp = 0, tn = 0, fp = 0, fn = 0;
		for (Data cData : data) {
			if (classify(node, cData) != cData.classId) {
				if (cData.classId == -1) {
					fp++;
				} else {
					fn++;
				}
			} else {
				if (cData.classId == -1) {
					tn++;
				} else {
					tp++;
				}
			}
		}
		double precision = tp / (tp + fp + 0.);
		double recall = tp / (tp + fn + 0.);
		return 2 * precision * recall / (precision + recall);
	}

	static ArrayList<Integer> use = new ArrayList<>();
	static Random rnd = new Random();

	public static void main(String[] args) throws FileNotFoundException {
		double sum = 0;
		final int ITER_COUNT = 10;
		for (int it = 0; it < ITER_COUNT; it++) {
			use.clear();
			for (int i = 0; i < 10000; i++) {
				if (rnd.nextDouble() > 0.99) {
					use.add(i);
				}
			}
			Node root = genTree(read(new File(trainData),
					new File(trainClasses)));
			double fM = fMeasure(root,
					read(new File(testData), new File(testClasses)));
			System.err.println("F = "
					+ fM);
			sum += fM;
		}
		System.err.println("avarage_f = " + sum / ITER_COUNT);
	}
}
