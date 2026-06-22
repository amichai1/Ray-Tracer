package geometries.impl;

import java.util.List;

import geometries.api.AABB;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Represents an infinite tube (cylinder without caps) in 3D space.
 * <p>
 * A tube is defined by a central axis {@link Ray} and a radius.
 * </p>
 * <p>
 * This class is not {@code final} because {@link Cylinder} extends it.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Tube extends RadialGeometry {

    /**
     * The central axis of the tube.
     */
    protected final Ray _axis;

    /**
     * Constructs a tube with the given radius and axis.
     *
     * @param radius the radius of the tube
     * @param axis   the central axis ray
     */
    public Tube(double radius, Ray axis) {
        super(radius);
        _axis = axis;
    }

    @Override
    protected AABB calcBoundingBox() {
        return null; // infinite extent
    }

    @Override
    public Vector getNormal(Point point) {
        Vector axis = _axis.direction();
        double t = axis.dotProduct(point.subtract(_axis.origin()));
        Point proj = _axis.getPoint(t);
        return point.subtract(proj).normalize();
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return null;
    }
}
