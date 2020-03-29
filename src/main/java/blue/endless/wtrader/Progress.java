package blue.endless.wtrader;

public class Progress {
	public int min;
	public int max;
	public int value;
	public String msg;
	
	public Progress() {}
	
	public Progress min(int min) {
		this.min = min;
		return this;
	}
	
	public Progress max(int max) {
		this.max = max;
		return this;
	}
	
	public Progress value(int val) {
		this.value = val;
		return this;
	}
	
	public static Progress of(String s) {
		Progress result = new Progress();
		result.msg = s;
		return result;
	}
}