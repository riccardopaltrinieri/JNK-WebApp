package beans;

public class CampaignStats {

	private int numImages;
	private int annotations;
	private float average;
	private int conflicts;
	
	
	public int getNumImages() {
		return numImages;
	}
	public void setNumImages(int numImages) {
		this.numImages = numImages;
	}
	public int getAnnotations() {
		return annotations;
	}
	public void setAnnotations(int annotations) {
		this.annotations = annotations;
	}
	public float getAverage() {
		return average;
	}
	public void setAverage() {
		average = ((float) annotations)/ ((float) numImages);
	}
	public int getConflicts() {
		return conflicts;
	}
	public void setConflicts(int conflicts) {
		this.conflicts = conflicts;
	}

	
}
