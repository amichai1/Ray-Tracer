package primitives;

import java.util.Objects;

/**
 * Represents a ray (half-line) in a 3D Cartesian coordinate system.
 * <p>
 * A ray is defined by an origin {@link Point} and a unit direction
 * {@link Vector}. The direction is always normalized upon construction.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class Ray {

    /**
     * The origin point of the ray.
     */
    private final Point _origin;

    /**
     * The unit direction vector of the ray.
     */
    private final Vector _direction;

    /**
     * Constructs a ray from an origin point and a direction vector.
     * <p>
     * The direction vector is normalized before storage.
     * </p>
     *
     * @param origin    the starting point of the ray
     * @param direction the direction of the ray (need not be a unit vector)
     */
    public Ray(Point origin, Vector direction) {
        _origin = origin;
        _direction = direction.normalize();
    }

    /**
     * Returns the unit direction vector of this ray.
     *
     * @return the direction vector
     */
    public Vector direction() {
        return _direction;
    }

    /**
     * Returns the origin point of this ray.
     *
     * @return the origin
     */
    public Point origin() {
        return _origin;
    }

    @Override
    public int hashCode() {
        return Objects.hash(_origin, _direction);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Ray other
                && _origin.equals(other._origin)
                && _direction.equals(other._direction);
    }

    @Override
    public String toString() {
        return "Ray=" + _origin + ", direction=" + _direction;
    }
}