import java.awt.*;

public enum Sticker { 

	WHITE(Color.WHITE, 0),
	ORANGE(new Color(255, 128, 0, 255), 1),
	GREEN(Color.GREEN, 2),
	RED(Color.RED, 3),
	BLUE(Color.BLUE, 4),
	YELLOW(Color.YELLOW, 5),
	BLANK(Color.GRAY, -1);

	public Color color;
	public int ordinal;

	Sticker(Color color, int ordinal) {
    	this.color = color;
    	this.ordinal = ordinal;
	}

	/* Get integer value associated with that color (which corresponds to a particular side on the cube) */
	public static Sticker get(int ord) {

        for (Sticker m : Sticker.values())
            if (m.ordinal == ord)
                return m;

        return null;

    }

}