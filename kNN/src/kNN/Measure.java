package kNN;

import java.util.*;

public class Measure {
	int tp, tn, fp, fn;

	Measure() {

	}

	double calc() {
		double precision = tp / (0. + tp + fp);
		double recall = tp / (0. + tp + fn);
		if (precision + recall == 0)
			return 0;
		return 2 * (precision * recall) / (precision + recall);
	}
	
	public static Measure findMeassure(KNNClassifier classifier, List<Data> check) {
		Measure measure = new Measure();
		for (Data d : check) {
			int classifierResult = classifier.getType(d);
			if (classifierResult == d.classId) {
				if (classifierResult == 0) {
					measure.tn++;
				} else {
					measure.tp++;
				}
			} else {
				if (classifierResult == 0) {
					measure.fn++;
				} else {
					measure.fp++;
				}
			}
		}
		return measure;
	}
}
