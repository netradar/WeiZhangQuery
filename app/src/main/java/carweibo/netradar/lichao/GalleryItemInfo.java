package carweibo.netradar.lichao;

public class GalleryItemInfo {
	
	String url;
	String progress;
	boolean loadFailure=false;
	
	public boolean isLoadFailure() {
		return loadFailure;
	}
	public void setLoadFailure(boolean loadFailure) {
		this.loadFailure = loadFailure;
	}
	public String getUrl() {
		return url;
	}
	public String getProgress() {
		return progress;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setProgress(String progress) {
		this.progress = progress;
	}

}
