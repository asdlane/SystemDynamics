package en.gt.ti.com.systemDynamics.graphUtil;

public class Increment{
	String id;
	double length;
	double slope;
	String annoLabel;
	double annoPosition;
	public Increment()
	{
		annoLabel = new String();
		annoPosition = -1;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public double getLength() {
		return length;
	}
	public void setLength(double length) {
		this.length = length;
	}
	public double getSlope() {
		return slope;
	}
	public void setSlope(double slope) {
		this.slope = slope;
	}
	public String getAnnoLabel() {
		return annoLabel;
	}
	public void setAnnoLabel(String annoLabel) {
		this.annoLabel = annoLabel;
	}
	public double getAnnoPosition() {
		return annoPosition;
	}
	public void setAnnoPosition(double annoPosition) {
		this.annoPosition = annoPosition;
	}
}