package edu.ar.itba.raytracer.properties;

public final class Color {

	public static final Color DEFAULT_COLOR = new Color(0, 0, 0);

	private double r, g, b;

	public Color() {
		this(DEFAULT_COLOR);
	}

	public Color(final double r, final double g, final double b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(final Color c) {
		r = c.r;
		g = c.g;
		b = c.b;
	}

	public double getRed() {
		return r;
	}

	public double getGreen() {
		return g;
	}

	public double getBlue() {
		return b;
	}

	public Color mult(final Color other) {
		r *= other.r;
		g *= other.g;
		b *= other.b;
		return this;
	}

	public Color add(final Color other) {
		final double newRed = r + other.r;
		final double newGreen = g + other.g;
		final double newBlue = b + other.b;
		return new Color(newRed, newGreen, newBlue);
	}

	public Color scalarMult(final double scalar) {
		final double transformedScalar = Double.isInfinite(scalar) ? Double.MAX_VALUE
				: scalar;
		r = Math.max(0, transformedScalar * r);
		g = Math.max(0, transformedScalar * g);
		b = Math.max(0, transformedScalar * b);
		return this;
	}

	// Generated by Eclipse
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Color)) {
			return false;
		}
		Color other = (Color) obj;
		if (b != other.b) {
			return false;
		}
		if (g != other.g) {
			return false;
		}
		if (r != other.r) {
			return false;
		}
		return true;
	}

	public Color clamp() {
		final double topValue = Math.max(r, Math.max(g, b));
		final double lowerValue = Math.min(r, Math.min(g, b));
		
		if (topValue > 1d) {
			if (topValue - lowerValue < 0.00001) {
				return new Color(1,1,1);
			}
			
			final double m = 1d / (topValue - lowerValue);
			final double c = -m * lowerValue;
			r = (m * r + c);
			g = (m * g + c);
			b = (m * b + c);
		}

		return this;
	}

	@Override
	public String toString() {
		return "Color [r=" + r + ", g=" + g + ", b=" + b + "]";
	}

}