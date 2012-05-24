package at.pollaknet.api.facile.tests.local;

class StatCounter {
		
	int count = 1;
	
	public void inc() {
		count++;
	}
	
	public int getValue() {
		return count;
	}
		
}