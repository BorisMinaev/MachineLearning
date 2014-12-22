import java.util.*;
import java.io.*;

public class MainWithoutNeuralNets {

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
	
	static double[][][] learn(ArrayList<Data> data) {
		int r = data.get(0).vals.length;
		int c = data.get(0).vals[0].length;
		double[][][] res = new double[10][r][c];
		for (Data cData : data) {
			for (int i = 0; i < r; i++)
				for (int j = 0; j < c; j++) {
					res[cData.realValue][i][j] += cData.vals[i][j] / 255.0;
				}
		}
		return res;
	}
	
	static int classify(double[][][] a, int[][] vals) {
		double max = -1;
		int res = -1;
		int r = vals.length, c = vals[0].length;
		for (int ii = 0; ii < 10; ii++) {
			double val = 0;
			for (int i = 0; i < r; i++) {
				for (int j = 0; j < c; j++) {
					val += a[ii][i][j] * vals[i][j];
				}
			}
			if (val > max) {
				max = val;
				res = ii;
			}
		}
		return res;
	}

	public static void main(String[] args) throws IOException {
		ArrayList<Data> data = readData(trainData, trainLabels);
		double[][][] a = learn(data);
		ArrayList<Data> test = readData(testData, testLabels);
		int ok = 0;
		for (Data cData : test) {
			if (classify(a, cData.vals) == cData.realValue) {
				ok++;
			}
		}
		System.err.println(ok / (test.size() + 0.0));
	}
}
