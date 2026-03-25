package primitives;

/**
 * Represents a direction and magnitude in a 3D Cartesian coordinate system.
 * <p>
 * A {@code Vector} is a displacement in space. It extends {@link Point} and
 * reuses its coordinate storage, but adds vector-specific algebra (dot product,
 * cross product, normalization, etc.).
 * </p>
 * <p>
 * The zero vector is not permitted: any constructor or operation that would
 * produce a zero vector throws {@link IllegalArgumentException}.
 * </p>
 * <p>
 * This class is immutable: all fields are inherited {@code final} fields and
 * every operation returns a new object.
 * </p>
 *
 * @author ISE5786
 */
public final class Vector extends Point {

    /**
     * The unit vector along the X axis.
     */
    public static final Vector AXIS_X = new Vector(1, 0, 0);
    /**
     * The unit vector along the Y axis.
     */
    public static final Vector AXIS_Y = new Vector(0, 1, 0);
    /**
     * The unit vector along the Z axis.
     */
    public static final Vector AXIS_Z = new Vector(0, 0, 1);

    /**
     * Constructs a vector from three coordinate values.
     *
     * @param x the X component
     * @param y the Y component
     * @param z the Z component
     * @throws IllegalArgumentException if the vector is the zero vector
     */
    public Vector(double x, double y, double z) {
        super(x, y, z);
        if (_xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /**
     * Constructs a vector from a {@link Double3} triple.
     *
     * @param xyz the component triple
     * @throws IllegalArgumentException if the vector is the zero vector
     */
    public Vector(Double3 xyz) {
        super(xyz);
        if (_xyz.equals(Double3.ZERO))
            throw new IllegalArgumentException("Zero vector is not allowed");
    }

    /**
     * Returns the vector sum of this vector and {@code other}.
     *
     * @param other the vector to add
     * @return a new vector representing the sum
     * @throws IllegalArgumentException if the result is the zero vector
     */
    public Vector add(Vector other) {
        return new Vector(_xyz.add(other._xyz));
    }

    /**
     * Returns this vector scaled by the given scalar.
     *
     * @param scalar the scaling factor
     * @return a new scaled vector
     * @throws IllegalArgumentException if {@code scalar} is zero
     */
    public Vector scale(double scalar) {
        return new Vector(_xyz.scale(scalar));
    }

    /**
     * Computes the dot product of this vector with {@code other}.
     *
     * @param other the other vector
     * @return the scalar dot product
     */
    public double dotProduct(Vector other) {
        return _xyz._d1() * other._xyz._d1()
                + _xyz._d2() * other._xyz._d2()
                + _xyz._d3() * other._xyz._d3();
    }

    /**
     * Computes the cross product of this vector with {@code other}.
     * <p>
     * The result is a vector perpendicular to both operands.
     * </p>
     *
     * @param other the other vector
     * @return a new vector perpendicular to both
     * @throws IllegalArgumentException if the two vectors are parallel (result
     *                                  would be the zero vector)
     */
    public Vector crossProduct(Vector other) {
        return new Vector(
                _xyz._d2() * other._xyz._d3() - _xyz._d3() * other._xyz._d2(),
                _xyz._d3() * other._xyz._d1() - _xyz._d1() * other._xyz._d3(),
                _xyz._d1() * other._xyz._d2() - _xyz._d2() * other._xyz._d1());
    }

    /**
     * Returns the squared length (magnitude squared) of this vector.
     * <p>
     * Prefer this over {@link #length} when only relative magnitudes are
     * compared, as it avoids a square-root computation.
     * </p>
     *
     * @return the squared length
     */
    public double lengthSquared() {
        return dotProduct(this);
    }

    /**
     * Returns the length (magnitude) of this vector.
     *
     * @return the length
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Returns a unit vector (length 1) in the same direction as this vector.
     *
     * @return the normalized vector
     */
    public Vector normalize() {
        return new Vector(_xyz.divide(length()));
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
