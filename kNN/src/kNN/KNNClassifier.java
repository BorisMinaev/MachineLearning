package kNN;

import java.util.*;

public class KNNClassifier {
	int k;
	List<Data> data;

	int getType(final Data query) {
		Collections.sort(data, new Comparator<Data>() {
			@Override
			public int compare(Data o1, Data o2) {
				return Double.compare(query.dist(o1), query.dist(o2));
			}
		});
		int maxClass = 0;
		for (Data d : data) {
			maxClass = Math.max(maxClass, 1 + d.classId);
		}
		double[] classSimilarity = new double[maxClass];
		double maxDist = query.dist(data.get(k));
		for (int i = 0; i < k; i++) {
			double weight = maxDist - query.dist(data.get(i));
			classSimilarity[data.get(i).classId] += weight;
		}
		int best = 0;
		for (int i = 0; i < maxClass; i++)
			if (classSimilarity[i] > classSimilarity[best]) {
				best = i;
			}
		return best;
	}

	public KNNClassifier(int k, List<Data> data) {
		this.k = k;
		this.data = data.subList(0, data.size());
	}
}
