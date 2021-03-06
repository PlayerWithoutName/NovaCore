package nova.core.gui.layout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nova.core.util.exception.NovaException;
import nova.core.util.transform.Vector2;
import nova.core.util.transform.Vector2d;
import nova.core.util.transform.Vector2i;

/**
 * {@link Constraints} for relative positioning.
 * 
 * @author Vic Nightfall
 */
public class RelativePosition extends Constraints<RelativePosition> {

	public Anchor xAnchor = Anchor.WEST;
	public Anchor yAnchor = Anchor.NORTH;
	public double xOffset;
	public double yOffset;
	public boolean xRelative;
	public boolean yRelative;

	public RelativePosition() {
	}

	public RelativePosition(double xOffset, double yOffset, Anchor xAnchor, Anchor yAnchor, boolean xRelative, boolean yRelative) {
		this(xOffset, yOffset, xAnchor, yAnchor);
		this.xRelative = xRelative;
		this.yRelative = yRelative;
	}

	public RelativePosition(double xOffset, double yOffset, Anchor xAnchor, Anchor yAnchor) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAnchor = xAnchor;
		this.yAnchor = yAnchor;
	}

	public RelativePosition(Vector2<?> pos, Anchor xAnchor, Anchor yAnchor) {
		this(pos.xd(), pos.yd(), xAnchor, yAnchor);
	}

	public RelativePosition(Vector2i pos) {
		this(pos, Anchor.WEST, Anchor.NORTH);
	}

	public RelativePosition(Vector2i pos, Anchor xAnchor, Anchor yAnchor) {
		this((Vector2<?>) pos, xAnchor, yAnchor);
	}

	public RelativePosition(Vector2d pos) {
		this(pos, Anchor.WEST, Anchor.NORTH);
	}

	public RelativePosition(Vector2d pos, Anchor xAnchor, Anchor yAnchor) {
		this((Vector2<?>) pos, Anchor.WEST, Anchor.NORTH);
		xRelative = yRelative = true;
	}

	public RelativePosition(int xOffset, int yOffset) {
		this(xOffset, yOffset, Anchor.WEST, Anchor.NORTH);
	}

	public RelativePosition(int xOffset, int yOffset, Anchor xAnchor, Anchor yAnchor) {
		this((float) xOffset, (float) yOffset, xAnchor, yAnchor);
	}

	public RelativePosition(double xOffset, int yOffset) {
		this(xOffset, yOffset, Anchor.WEST, Anchor.NORTH);
	}

	public RelativePosition(double xOffset, int yOffset, Anchor xAnchor, Anchor yAnchor) {
		this(xOffset, (double) yOffset, xAnchor, yAnchor);
		xRelative = true;
	}

	public RelativePosition(int xOffset, double yOffset) {
		this(xOffset, yOffset, Anchor.WEST, Anchor.NORTH);
	}

	public RelativePosition(int xOffset, double yOffset, Anchor xAnchor, Anchor yAnchor) {
		this((double) xOffset, yOffset, xAnchor, yAnchor);
		yRelative = true;
	}

	public RelativePosition(double xOffset, double yOffset) {
		this(xOffset, yOffset, Anchor.WEST, Anchor.NORTH);
		xRelative = yRelative = true;
	}

	public static final Pattern pattern = Pattern.compile("(west|east|north|south):\\s?([-+]?\\d+)(%)?[\\s]?", Pattern.CASE_INSENSITIVE);

	public RelativePosition(String str) {
		try {
			int size = 0;
			Matcher matcher = pattern.matcher(str);
			while (matcher.find()) {
				size += matcher.end() - matcher.start();
				Anchor anchor = Anchor.valueOf(matcher.group(1).toUpperCase());
				int offset = Integer.valueOf(matcher.group(2));
				boolean relative = matcher.group(3) != null;
				if (anchor.axis == 1) {
					xAnchor = anchor;
					xOffset = relative ? offset / 100F : offset;
					xRelative = relative;
				} else if (anchor.axis == 2) {
					yAnchor = anchor;
					yOffset = relative ? offset / 100F : offset;
					yRelative = relative;
				}
			}
			if (str.length() - size > 0)
				throw new IllegalArgumentException();
		} catch (Exception e) {
			e.printStackTrace();
			throw new NovaException("Invalid relative position \"" + str + "\"");
		}
	}

	public RelativePosition setX(Anchor anchor, int offset) {
		this.xAnchor = anchor;
		this.xOffset = offset;
		this.xRelative = false;
		return this;
	}

	public RelativePosition setY(Anchor anchor, int offset) {
		this.yAnchor = anchor;
		this.yOffset = offset;
		this.yRelative = false;
		return this;
	}

	public RelativePosition setX(Anchor anchor, double offset) {
		this.xAnchor = anchor;
		this.xOffset = (int) (offset * 100);
		this.xRelative = true;
		return this;
	}

	public RelativePosition setY(Anchor anchor, double offset) {
		this.yAnchor = anchor;
		this.yOffset = (int) (offset * 100);
		this.yRelative = true;
		return this;
	}

	public Vector2i getPositionOf(Vector2i parentSize) {
		int x = xRelative ? (int) (parentSize.x * xOffset) : (int) xOffset;
		int y = yRelative ? (int) (parentSize.y * yOffset) : (int) yOffset;

		if (xAnchor == Anchor.EAST)
			x = parentSize.x - x;
		if (yAnchor == Anchor.SOUTH)
			y = parentSize.y - y;

		return new Vector2i(x, y);
	}
}
