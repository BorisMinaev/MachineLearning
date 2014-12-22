
public class Data {
	int[][] vals;
	int realValue;
	
	Data(int[][] vals) {
		this.vals = vals;
	}
	
	public String toString() {
		String res = "";
		for (int i = 0; i < vals.length; i++) {
			for (int j = 0; j < vals[i].length; j++) {
				if (vals[i][j] >= 128) {
					res += 'X';
				} else {
					res += '.';
				}
			}
			res += '\n';
		}
		res += "value = " + Integer.toString(realValue)+'\n';
		return res;
	}
}
