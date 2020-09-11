package exception;

public class CartEmptyException extends RuntimeException {
	//RuntimeException은 생략이 가능하기때문에 사용가능
	private String url;
	public CartEmptyException(String msg, String url) {
		super(msg);
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
}
