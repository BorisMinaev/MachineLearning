class Data {
	int[] vals;
	int classId;

	Data(String line) {
		String[] tmp = line.split(" ");
		vals = new int[tmp.length];
		for (int i = 0; i < vals.length; i++) {
			vals[i] = Integer.parseInt(tmp[i]);
		}
	}

	void setClassId(int classId) {
		this.classId = classId;
	}
}