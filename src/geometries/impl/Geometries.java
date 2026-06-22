package geometries.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import geometries.api.AABB;
import geometries.api.Intersectable;
import primitives.Ray;

/**
 * Composite geometry that aggregates a collection of {@link Intersectable} objects.
 * <p>
 * Implements the <em>Composite</em> design pattern: a {@code Geometries} object
 * can itself be treated as an {@code Intersectable}, delegating the
 * {@code findIntersections} query to each element and collecting all results.
 * </p>
 * <p>
 * This class is not {@code final} to allow future extension.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Geometries extends Intersectable {

    /**
     * The collection of intersectable objects in this composite.
     */
    private final List<Intersectable> _geometries = new ArrayList<>();

    /**
     * Constructs an empty {@code Geometries} scene.
     */
    public Geometries() {
    }

    /**
     * Constructs a {@code Geometries} scene and adds the given objects to it.
     *
     * @param geometries one or more intersectable objects to add
     */
    public Geometries(Intersectable... geometries) {
        add(geometries);
    }

    /**
     * Adds one or more intersectable objects to this composite.
     *
     * @param geometries one or more intersectable objects to add
     */
    public void add(Intersectable... geometries) {
        _geometries.addAll(List.of(geometries));
    }

    @Override
    protected AABB calcBoundingBox() {
        AABB result = null;
        for (Intersectable g : _geometries) {
            AABB box = g.getBoundingBox();
            if (box == null) return null; // unbounded child → whole composite is unbounded
            result = (result == null) ? box : AABB.merge(result, box);
        }
        return result;
    }

    /**
     * Returns a flat {@code Geometries} containing every leaf geometry from
     * this composite tree (recursively flattens nested {@code Geometries}).
     *
     * @return a flat composite with no nested {@code Geometries}
     */
    public Geometries flatten() {
        Geometries flat = new Geometries();
        for (Intersectable g : _geometries) {
            if (g instanceof Geometries inner)
                flat._geometries.addAll(inner.flatten()._geometries);
            else
                flat._geometries.add(g);
        }
        return flat;
    }

    /**
     * Builds a BVH hierarchy over this composite using median-split along the
     * longest AABB axis.
     * <p>
     * Call {@link #flatten()} first to remove any prior nesting, then call
     * this method to build an optimal tree.
     * Geometries without a bounding box (e.g. infinite planes) are kept in a
     * separate list and not partitioned.
     * </p>
     *
     * @return the root of the BVH hierarchy (a possibly nested {@code Geometries})
     */
    public Geometries buildBVH() {
        return buildBVHRecursive(_geometries);
    }

    /**
     * Recursive BVH construction using median split.
     *
     * @param  items list of intersectable objects to partition
     * @return a {@code Geometries} node (leaf or internal)
     */
    private static Geometries buildBVHRecursive(List<Intersectable> items) {
        if (items.size() <= 2) {
            Geometries leaf = new Geometries();
            leaf._geometries.addAll(items);
            return leaf;
        }

        // Separate objects with no bounding box (infinite planes, etc.)
        List<Intersectable> bounded   = new ArrayList<>();
        List<Intersectable> unbounded = new ArrayList<>();
        for (Intersectable g : items) {
            if (g.getBoundingBox() == null) unbounded.add(g);
            else                            bounded.add(g);
        }

        if (bounded.size() <= 2) {
            Geometries leaf = new Geometries();
            leaf._geometries.addAll(items);
            return leaf;
        }

        // Compute enclosing AABB of all bounded objects
        AABB enclosing = null;
        for (Intersectable g : bounded) {
            AABB b = g.getBoundingBox();
            enclosing = (enclosing == null) ? b : AABB.merge(enclosing, b);
        }

        // Sort along the longest axis and split at median
        int axis = enclosing.longestAxis();
        bounded.sort(Comparator.comparingDouble(g -> g.getBoundingBox().centroid(axis)));
        int mid = bounded.size() / 2;

        Geometries left  = buildBVHRecursive(bounded.subList(0, mid));
        Geometries right = buildBVHRecursive(bounded.subList(mid, bounded.size()));

        Geometries node = new Geometries();
        node._geometries.add(left);
        node._geometries.add(right);
        node._geometries.addAll(unbounded);
        return node;
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray) {
        return calcIntersectionsHelper(ray, Double.POSITIVE_INFINITY);
    }

    @Override
    protected List<Intersection> calcIntersectionsHelper(Ray ray, double maxDistance) {
        List<Intersection> result = null;
        for (Intersectable geometry : _geometries) {
            List<Intersection> intersections = geometry.calcIntersections(ray, maxDistance);
            if (intersections != null) {
                if (result == null) {
                    result = new ArrayList<>(intersections);
                } else
                    result.addAll(intersections);
            }
        }
        return result;
    }
}
