package main;

public class AnalyzerException extends Exception {

	private int errorPosition;

	private String message;

	public AnalyzerException(int errorPosition) {
		this.errorPosition = errorPosition;
	}

	public AnalyzerException(String message, int errorPosition) {
		this.message = message;
		this.errorPosition = errorPosition;
		
	}
	
	public AnalyzerException(String string) {
		// TODO Auto-generated constructor stub
		this.message = string;
	}

	public int getErrorPosition() {
		return errorPosition;
	}

	@Override
	public String getMessage() {
		return message;
	}
}