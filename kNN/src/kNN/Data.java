package kNN;

public class Data {
	double x, y;
	int classId;

	public Data(String description) {
		String[] s = description.split(",");
		x = Double.parseDouble(s[0]);
		y = Double.parseDouble(s[1]);
		classId = Integer.parseInt(s[2]);
	}

	double dist(Data an) {
		return Math.hypot(an.x - x, an.y - y);
	}
}
