package primitives;

import java.util.List;
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
     * Computes the point on this ray's line at parameter {@code t} from the origin.
     * <p>
     * The formula is: {@code P = origin + t * direction}.
     * {@code t} may be any real number (positive, negative, or zero).
     * When {@code t} is zero (or effectively zero), the origin is returned.
     * </p>
     *
     * @param  t the signed distance along the direction vector
     * @return   the point {@code origin + t * direction}
     */
    public Point getPoint(double t) {
        try {
            return _origin.add(_direction.scale(t));
        } catch (IllegalArgumentException e) {
            return _origin;
        }
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

    /**
     * Finds the point in the given list that is closest to this ray's origin.
     * <p>
     * The list must not be empty. Returns {@code null} if {@code points} is
     * {@code null}.
     * </p>
     *
     * @param  points the list of candidate points; must not be empty
     * @return        the closest point to the ray's origin, or {@code null} if
     *                {@code points} is {@code null}
     */
    public Point findClosestPoint(List<Point> points) {
        if (points == null) return null;
        Point  closest = null;
        double minDist = Double.POSITIVE_INFINITY;
        for (Point p : points) {
            double d = _origin.distanceSquared(p);
            if (d < minDist) {
                minDist = d;
                closest = p;
            }
        }
        return closest;
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