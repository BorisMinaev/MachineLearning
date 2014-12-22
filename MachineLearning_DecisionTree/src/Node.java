
public class Node {
	int classId;
	int featureId;
	int value;
	Node left, right;
	
	Node(int featureId, int value) {
		this.featureId = featureId;
		this.value = value;
	}
	
	Node(int classId) {
		this.classId = classId;
	}
}
