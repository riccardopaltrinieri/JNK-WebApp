package enumerations;

public enum Level {
	Low,
	Medium,
	High;

	public static Level getLevel(int numActions) {
		
		if(numActions < 5) return Low;
		else if(numActions < 15) return Medium;
		else return High;
    }
}
