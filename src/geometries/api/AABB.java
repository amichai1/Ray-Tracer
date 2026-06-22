package geometries.api;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static primitives.Util.isZero;

/**
 * Axis-Aligned Bounding Box (AABB) for conservative bounding region (CBR) tests.
 * <p>
 * Stores the minimum and maximum extents along each world axis.
 * Used for a fast boolean ray–box intersection test (slab method) before
 * the more expensive ray–geometry intersection is attempted.
 * </p>
 *
 * @author Amichai Mukades
 */
public final class AABB {

    /**
     * Minimum X coordinate of the bounding box.
     */
    final double _minX;

    /**
     * Minimum Y coordinate of the bounding box.
     */
    final double _minY;

    /**
     * Minimum Z coordinate of the bounding box.
     */
    final double _minZ;

    /**
     * Maximum X coordinate of the bounding box.
     */
    final double _maxX;

    /**
     * Maximum Y coordinate of the bounding box.
     */
    final double _maxY;

    /**
     * Maximum Z coordinate of the bounding box.
     */
    final double _maxZ;

    /**
     * Constructs an AABB from its minimum and maximum corner coordinates.
     *
     * @param  minX minimum X extent
     * @param  minY minimum Y extent
     * @param  minZ minimum Z extent
     * @param  maxX maximum X extent
     * @param  maxY maximum Y extent
     * @param  maxZ maximum Z extent
     */
    public AABB(double minX, double minY, double minZ,
                double maxX, double maxY, double maxZ) {
        _minX = minX; _minY = minY; _minZ = minZ;
        _maxX = maxX; _maxY = maxY; _maxZ = maxZ;
    }

    /**
     * Tests whether the given ray definitely misses this bounding box.
     * <p>
     * Uses the <em>slab method</em>: computes the entry/exit parameter
     * {@code t} along each axis and checks if all three intervals overlap
     * and if the overlap region is in front of the ray origin.
     * </p>
     * <p>
     * This is a boolean test only – no intersection point is computed.
     * </p>
     *
     * @param  ray the ray to test against this box
     * @return {@code true} for a definite miss; {@code false} if the ray may hit
     */
    public boolean misses(Ray ray) {
        Point origin = ray.origin();
        Vector dir   = ray.direction();

        double ox = origin.getX(), oy = origin.getY(), oz = origin.getZ();
        double dx = dir.getX(),    dy = dir.getY(),    dz = dir.getZ();

        double tMin = Double.NEGATIVE_INFINITY;
        double tMax = Double.POSITIVE_INFINITY;

        // X slab
        if (!isZero(dx)) {
            double t1 = (_minX - ox) / dx;
            double t2 = (_maxX - ox) / dx;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return true;
        } else if (ox < _minX || ox > _maxX) {
            return true;
        }

        // Y slab
        if (!isZero(dy)) {
            double t1 = (_minY - oy) / dy;
            double t2 = (_maxY - oy) / dy;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return true;
        } else if (oy < _minY || oy > _maxY) {
            return true;
        }

        // Z slab
        if (!isZero(dz)) {
            double t1 = (_minZ - oz) / dz;
            double t2 = (_maxZ - oz) / dz;
            if (t1 > t2) { double tmp = t1; t1 = t2; t2 = tmp; }
            tMin = Math.max(tMin, t1);
            tMax = Math.min(tMax, t2);
            if (tMin > tMax) return true;
        } else if (oz < _minZ || oz > _maxZ) {
            return true;
        }

        return tMax <= 0;
    }

    /**
     * Returns the smallest AABB that encloses both {@code a} and {@code b}.
     *
     * @param  a first bounding box
     * @param  b second bounding box
     * @return the merged bounding box
     */
    public static AABB merge(AABB a, AABB b) {
        return new AABB(
                Math.min(a._minX, b._minX), Math.min(a._minY, b._minY), Math.min(a._minZ, b._minZ),
                Math.max(a._maxX, b._maxX), Math.max(a._maxY, b._maxY), Math.max(a._maxZ, b._maxZ));
    }

    /**
     * Returns the axis index (0=X, 1=Y, 2=Z) of the longest side.
     *
     * @return index of longest axis
     */
    public int longestAxis() {
        double dx = _maxX - _minX;
        double dy = _maxY - _minY;
        double dz = _maxZ - _minZ;
        if (dx >= dy && dx >= dz) return 0;
        if (dy >= dz)             return 1;
        return 2;
    }

    /**
     * Returns the centroid coordinate of this box along the given axis.
     *
     * @param  axis 0=X, 1=Y, 2=Z
     * @return centroid value along that axis
     */
    public double centroid(int axis) {
        return switch (axis) {
            case 0 -> (_minX + _maxX) * 0.5;
            case 1 -> (_minY + _maxY) * 0.5;
            default -> (_minZ + _maxZ) * 0.5;
        };
    }
}
