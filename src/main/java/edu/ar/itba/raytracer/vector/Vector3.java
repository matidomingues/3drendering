package edu.ar.itba.raytracer.vector;

public class Vector3 {

	public final double x, y, z;

	// private double norm;

	public Vector3(final double x, final double y, final double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3(final Vector3 other) {
		this(other.x, other.y, other.z);
	}

	public Vector3 add(final Vector3 other) {
		return new Vector3(x + other.x, y + other.y, z + other.z);
	}

	public Vector3 sub(final Vector3 other) {
		return new Vector3(x - other.x, y - other.y, z - other.z);
	}

	public double dot(final Vector3 other) {
		final double ox = other.x;
		final double oy = other.y;
		final double oz = other.z;
		return x * ox + y * oy + z * oz;
	}

	public Vector3 cross(final Vector3 other) {
		return new Vector3(y * other.z - z * other.y,
				z * other.x - x * other.z, x * other.y - y * other.x);
	}

	public Vector3 scalarMult(final double scalar) {
		return new Vector3(x * scalar, y * scalar, z * scalar);
	}

	public Vector3 normalize() {
		final double norm = (double) Math.sqrt(x * x + y * y + z * z);
		if (norm == 0) {
			throw new IllegalStateException(
					"Cannot normalize vector with norm = 0");
		}

		final double invNorm = 1 / norm;
		return new Vector3(x * invNorm, y * invNorm, z * invNorm);
	}

	public double distanceTo(final Vector3 point) {
		final double xDiff = x - point.x;
		final double yDiff = y - point.y;
		final double zDiff = z - point.z;

		return (double) Math
				.sqrt(xDiff * xDiff + yDiff * yDiff + zDiff * zDiff);
	}

	public Vector3 rotate(final Vector3 rotation) {
		final double xRotation = (double) (rotation.x * Math.PI / 180.0f);
		final double yRotation = (double) (rotation.y * Math.PI / 180.0f);
		final double zRotation = (double) (rotation.z * Math.PI / 180.0f);

		final double sinx = (double) Math.sin(xRotation);
		final double cosx = (double) Math.cos(xRotation);
		final double siny = (double) Math.sin(yRotation);
		final double cosy = (double) Math.cos(yRotation);
		final double sinz = (double) Math.sin(zRotation);
		final double cosz = (double) Math.cos(zRotation);

		final double x = this.x * cosz * cosy + this.y
				* (-sinz * cosx + cosz * siny * sinx) + this.z
				* (sinz * sinx + cosz * siny * cosx);
		final double y = this.x * sinz * cosy + this.y
				* (cosz * cosx + sinx * siny * sinz) + this.z
				* (-cosz * sinx + sinz * siny * cosx);
		final double z = -this.x * siny + this.y * cosy * sinx + this.z * cosy
				* cosx;

		return new Vector3(x, y, z);
	}

	// Generated by Eclipse.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(z);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	// Generated by Eclipse.
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Vector3)) {
			return false;
		}
		Vector3 other = (Vector3) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) {
			return false;
		}
		if (Double.doubleToLongBits(z) != Double.doubleToLongBits(other.z)) {
			return false;
		}
		return true;
	}

	// Generated by Eclipse.
	@Override
	public String toString() {
		return "Vector3 [x=" + x + ", y=" + y + ", z=" + z + "]";
	}

}
