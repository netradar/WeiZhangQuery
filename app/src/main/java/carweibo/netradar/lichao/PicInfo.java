package carweibo.netradar.lichao;

import java.io.Serializable;

public class PicInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String url=null;
	int width=0;
	int height=0;
	int scal_type=0;
	public String getUrl() {
		return url;
	}
	public int getHeight() {
		return height;
	}
	public int getScal_type() {
		return scal_type;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setScal_type(int scal_type) {
		this.scal_type = scal_type;
	}
	
}
