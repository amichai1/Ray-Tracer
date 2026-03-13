package geometries.impl;

import geometries.api.Geometry;

/**
 * Abstract base class for geometric shapes that have a radius.
 * <p>
 * Stores both the radius and its square (to avoid repeated multiplication in
 * intersection tests).
 * </p>
 *
 * @author Amichai Mukades
 */
public abstract class RadialGeometry extends Geometry {

    /**
     * The radius of the geometry.
     */
    protected final double _radius;

    /**
     * The squared radius of the geometry.
     */
    protected final double _radiusSquared;

    /**
     * Constructs a radial geometry with the given radius.
     *
     * @param radius the radius
     */
    public RadialGeometry(double radius) {
        _radius = radius;
        _radiusSquared = radius * radius;
    }
}
