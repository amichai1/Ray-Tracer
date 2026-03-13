package geometries.impl;

import primitives.Point;
import primitives.Vector;

/**
 * Represents a sphere in 3D space.
 * <p>
 * A sphere is defined by a center point and a radius.
 * </p>
 * <p>
 * This class is immutable.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class Sphere extends RadialGeometry {

    /**
     * The center point of the sphere.
     */
    private final Point _center;

    /**
     * Constructs a sphere from a center point and a radius.
     *
     * @param center the center of the sphere
     * @param radius the radius of the sphere
     */
    public Sphere(Point center, double radius) {
        super(radius);
        _center = center;
    }

    @Override
    public Vector getNormal(Point point) {
        return null;
    }
}
