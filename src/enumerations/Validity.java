package enumerations;

public enum Validity {
	False,
	Real;
	
	public static Validity fromInteger(int x) {
        switch(x) {
        case 0:
            return False;
        case 1:
            return Real;
        }
        return null;
    }
}
