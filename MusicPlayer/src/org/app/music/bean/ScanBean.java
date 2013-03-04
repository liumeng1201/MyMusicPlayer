package org.app.music.bean;

public class ScanBean {
	private String filePath;
	private boolean isChecked;

	public ScanBean() {

	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public boolean isChecked() {
		return isChecked;
	}

	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

	public ScanBean(String filePath, boolean isChecked) {

		this.filePath = filePath;
		this.isChecked = isChecked;
	}

}