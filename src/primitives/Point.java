package primitives;

import java.util.Objects;

/**
 * Represents a point in a 3D Cartesian coordinate system.
 * <p>
 * This class is immutable: all fields are {@code final} and no method modifies
 * the state of the object. Every operation that produces a result returns a new
 * object.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Point {

    /**
     * Coordinates of this point stored as a {@link Double3} triple.
     */
    final Double3 _xyz;

    /**
     * The origin point (0, 0, 0).
     */
    public static final Point ZERO = new Point(Double3.ZERO);

    /**
     * Constructs a point from three coordinate values.
     *
     * @param x the X coordinate
     * @param y the Y coordinate
     * @param z the Z coordinate
     */
    public Point(double x, double y, double z) {
        _xyz = new Double3(x, y, z);
    }

    /**
     * Constructs a point from a {@link Double3} triple.
     *
     * @param xyz the coordinate triple
     */
    public Point(Double3 xyz) {
        _xyz = xyz;
    }

    /**
     * Returns the vector from {@code other} to this point.
     *
     * @param other the other point
     * @return the vector {@code this - other}
     * @throws IllegalArgumentException if the two points are identical (result
     *                                  would be a zero vector)
     */
    public Vector subtract(Point other) {
        return new Vector(_xyz.subtract(other._xyz));
    }

    /**
     * Translates this point by the given vector and returns the resulting point.
     *
     * @param vector the displacement vector
     * @return a new point at {@code this + vector}
     */
    public Point add(Vector vector) {
        return new Point(_xyz.add(vector._xyz));
    }

    /**
     * Returns the squared Euclidean distance between this point and {@code other}.
     * <p>
     * Prefer this method over {@link #distance} when only relative distances are
     * compared, as it avoids a square-root computation.
     * </p>
     *
     * @param other the other point
     * @return the squared distance
     */
    public double distanceSquared(Point other) {
        Double3 d = _xyz.subtract(other._xyz);
        return d._d1() * d._d1() + d._d2() * d._d2() + d._d3() * d._d3();
    }

    /**
     * Returns the Euclidean distance between this point and {@code other}.
     *
     * @param other the other point
     * @return the distance
     */
    public double distance(Point other) {
        return Math.sqrt(distanceSquared(other));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Point other && _xyz.equals(other._xyz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_xyz);
    }

    @Override
    public String toString() {
        return _xyz.toString();
    }
}
