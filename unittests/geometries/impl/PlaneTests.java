package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for class {@link Plane}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    PlaneTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Plane used in findIntersections tests: the XZ-plane (y = 0),
     * with reference point (1,0,0) and unit normal (0,1,0).
     */
    private static final Plane  PLANE = new Plane(new Point(1, 0, 0), new Vector(0, 1, 0));
    /**
     * Error message for plane intersection failures
     */
    private static final String ERR   = "Wrong result for Plane.findIntersections";

    /**
     * First point on the plane
     */
    private static final Point P1 = new Point(0, 0, 1);
    /**
     * Second point on the plane
     */
    private static final Point P2 = new Point(1, 0, 0);
    /**
     * Third point on the plane
     */
    private static final Point P3 = new Point(0, 1, 0);
    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;

    /**
     * Test method for {@link Plane#Plane(Point, Vector)}.
     */
    @Test
    void testConstructorPointVector() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Valid point-and-normal constructor; stored normal is unit length
        assertDoesNotThrow(() -> new Plane(P1, new Vector(0, 0, 1)),
                "Constructor(Point, Vector) threw unexpected exception");
        Plane plane = new Plane(P1, new Vector(0, 0, 3));
        assertEquals(1.0, plane.getNormal(P1).length(), DELTA,
                "Plane normal must be unit length");
    }

    /**
     * Test method for {@link Plane#Plane(Point, Point, Point)}.
     */
    @Test
    void testConstructorThreePoints() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Three non-collinear, distinct points — valid construction
        assertDoesNotThrow(() -> new Plane(P1, P2, P3),
                "Constructor(3 points) threw unexpected exception for valid points");

        // =============== Boundary Values Tests ==================

        // BV01: Points 1 and 2 coincide
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(P1, P1, P3),
                "Constructor must throw when points 1 and 2 coincide");

        // BV02: Points 1 and 3 coincide
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(P1, P2, P1),
                "Constructor must throw when points 1 and 3 coincide");

        // BV03: Points 2 and 3 coincide
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(P1, P2, P2),
                "Constructor must throw when points 2 and 3 coincide");

        // BV04: All three points coincide
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(P1, P1, P1),
                "Constructor must throw when all three points coincide");

        // BV05: Three collinear points
        assertThrows(IllegalArgumentException.class,
                () -> new Plane(new Point(0, 0, 0), new Point(1, 0, 0), new Point(2, 0, 0)),
                "Constructor must throw for collinear points");
    }

    /**
     * Test method for {@link Plane#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects the plane – one intersection point
        List<Point> result = PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, -1, 0)));
        assertEquals(List.of(new Point(1, 0, 0)), result, ERR);

        // EP02: Ray points away from the plane (t < 0) – no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))), ERR);

        // =============== Boundary Values Tests ==================

        // BV01: Ray is parallel to the plane and not on it – no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))), ERR);

        // BV02: Ray is parallel to the plane and lies on it – no intersection (or infinite)
        assertNull(PLANE.findIntersections(new Ray(new Point(3, 0, 4), new Vector(1, 0, 0))), ERR);

        // BV03: Ray is orthogonal to the plane, origin before the plane – one intersection
        List<Point> resultBV03 = PLANE.findIntersections(new Ray(new Point(1, -1, 0), new Vector(0, 1, 0)));
        assertEquals(List.of(new Point(1, 0, 0)), resultBV03, ERR);

        // BV04: Ray is orthogonal to the plane, origin on the plane (t = 0) – no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(3, 0, 4), new Vector(0, 1, 0))), ERR);

        // BV05: Ray is orthogonal to the plane, origin after the plane – no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))), ERR);

        // BV06: Ray starts on the plane (not orthogonal, not parallel) – t = 0, no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(3, 0, 4), new Vector(1, 1, 0))), ERR);

        // BV07: Ray starts at the plane's reference point Q – t = 0, no intersection
        assertNull(PLANE.findIntersections(new Ray(new Point(1, 0, 0), new Vector(1, 1, 0))), ERR);
    }

    /**
     * Test method for {@link geometries.api.Intersectable#calcIntersections(Ray, double)}.
     * <p>
     * Two cases: max-distance cutoff before and after the single plane intersection.
     * </p>
     */
    @Test
    void testCalcIntersectionsWithMaxDistance() {
        // Ray from (1,1,0) direction (0,-1,0): hits y=0 plane at (1,0,0), distance=1
        Ray ray = new Ray(new Point(1, 1, 0), new Vector(0, -1, 0));

        // Q before intersection – 0 results
        assertNull(PLANE.calcIntersections(ray, 0.5), ERR);

        // Q past intersection – 1 result
        var result = PLANE.calcIntersections(ray, 2);
        assertNotNull(result, ERR);
        assertEquals(1, result.size(), ERR);
    }

    /**
     * Test method for {@link Plane#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        Plane plane = new Plane(P1, P2, P3);

        // ============ Equivalence Partitions Tests ==============

        // EP01: Normal at a point on the plane (not the reference point) is unit and orthogonal
        Vector normal = plane.getNormal(P2);
        assertEquals(1.0, normal.length(), DELTA,
                "getNormal() must return a unit vector");
        assertEquals(0.0, normal.dotProduct(P2.subtract(P1)), DELTA,
                "getNormal() result must be orthogonal to edge P2-P1");
        assertEquals(0.0, normal.dotProduct(P3.subtract(P1)), DELTA,
                "getNormal() result must be orthogonal to edge P3-P1");

        // =============== Boundary Values Tests ==================

        // BV01: Normal at the reference point P1 itself is still unit length
        Vector normalAtRef = plane.getNormal(P1);
        assertEquals(1.0, normalAtRef.length(), DELTA,
                "getNormal() at reference point must return a unit vector");
    }
}
