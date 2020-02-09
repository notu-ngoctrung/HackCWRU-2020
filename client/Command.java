package client;

public class Command {
    CType type;
    String[] cargs;
    int length;

    public enum CType {
        HAND (4),
        STRT (0), 
        MOVE (2), 
        CLCK (1), 
        KEYB (2),
        SCRN (1),
        GBYE (0);

        final int numArgs;
        CType(int numArgs) {
            this.numArgs = numArgs;
        }
    }

    public Command(CType type, String... arg) {
        this.type = type;
        if (type.numArgs != arg.length)
            throw new IllegalArgumentException("Command " + type.toString() + " needs " + type.numArgs + " arguments");
        cargs = new String[type.numArgs];
        for (int i=0; i<arg.length; ++i)
            cargs[i] = Command.nameSanitize(arg[i]);
    }

    public Command(String string) {
        String[] stuff = string.split(" ");
        // Cross check types
		for (CType types : CType.values())
			if (types.toString() == stuff[0])
				this(types, Arrays.copyOfRange(stuff, 1, stuff.length);
    }

    public String stringify() {
        StringBuilder s = new StringBuilder();
        s.append(type.toString());
        s.append(" ");
        for (String args : cargs) {
            s.append(args);
            s.append(" ");
        }
        length = s.length();
        return s.toString();
    }

    public byte[] getBytes() {
        return this.stringify().getBytes();
    }

    private static String nameSanitize(String name) {
        return name.replaceAll("\\s", "-");
    }

	public String getType() {
		return type.toString();
	}

	public String[] getArgs() {
		return cargs;
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { 
			(byte)(value >>> 24),
			(byte)(value >>> 16),
			(byte)(value >>> 8), 
			(byte)value}; }
	}

	public String getArgAt(int index) {																				return getArgs()[index];
	}	
}
