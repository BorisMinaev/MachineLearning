package kNN;

import java.io.*;
import java.util.*;

public class Main {

	final static double LEARNING_PART = 0.8;

	private static int findBestK(List<Data> data) {
		int result = -1;
		double bestMeassure = -Double.MAX_VALUE;
		for (int k = 1; k < Math.min(20, data.size()); k++) {
			double curResult = CrossValidation.crossValidate(data, k);
			System.out.println("with k = " + k + " avarage F = " + curResult);
			if (curResult > bestMeassure) {
				bestMeassure = curResult;
				result = k;
			}
		}
		return result;
	}

	private static void normalize(ArrayList<Data> data) {
		double maxX = 0, maxY = 0;
		for (Data d : data) {
			maxX = Math.max(maxX, Math.abs(d.x));
			maxY = Math.max(maxY, Math.abs(d.y));
		}
		for (Data d : data) {
			d.x /= maxX;
			d.y /= maxY;
		}
	}

	private static void doKNN() {
		try {
			BufferedReader in = new BufferedReader(new FileReader("chips.txt"));
			ArrayList<Data> data = new ArrayList<>();
			while (true) {
				String line = in.readLine();
				if (line == null)
					break;
				data.add(new Data(line));
			}
			in.close();
			normalize(data);
			Collections.shuffle(data);
			int learningSize = (int) Math.round(data.size() * LEARNING_PART);
			List<Data> learningSet =data.subList(0,
					learningSize);
			List<Data> testSet = data.subList(
					learningSize, data.size());
			int k = findBestK(learningSet);
			KNNClassifier classifier = new KNNClassifier(k, learningSet);
			System.out.println("best result with k = " + k + " F = "
					+ Measure.findMeassure(classifier, testSet).calc());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		doKNN();
	}
}
