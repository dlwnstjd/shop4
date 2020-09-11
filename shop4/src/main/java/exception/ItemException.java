package exception;

public class ItemException extends RuntimeException {
	private String url;
	public ItemException(String msg, String url) {
		super(msg);
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
}
