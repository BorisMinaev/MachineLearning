package kNN;

import java.util.*;

public class CrossValidation {
	final static int PARTS = 5;

	public static double crossValidate(List<Data> data, int k) {
		double result = 0.0;
		@SuppressWarnings("unchecked")
		List<Data>[] parts = new List[PARTS];
		int partSize = data.size() / PARTS;
		for (int i = 0; i < PARTS; i++) {
			parts[i] = data.subList(i * partSize, (i + 1) * partSize);
		}
		for (int checkPartId = 0; checkPartId < PARTS; checkPartId++) {
			ArrayList<Data> learnOn = new ArrayList<>();
			for (int i = 0; i < PARTS; i++) {
				if (i != checkPartId) {
					learnOn.addAll(parts[i]);
				}
			}
			KNNClassifier classifier = new KNNClassifier(k, learnOn);
			result += Measure.findMeassure(classifier, parts[checkPartId])
					.calc();
		}
		return result / PARTS;
	}
}
