package geometries.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for class {@link Sphere}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class SphereTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    SphereTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Delta value for accuracy when comparing double values
     */
    private static final double DELTA = 1e-6;
    /**
     * Sphere used in findIntersections tests: center (1,0,0) radius 1.
     */
    private static final Sphere SPHERE = new Sphere(new Point(1, 0, 0), 1d);
    /**
     * Error message for sphere intersection failures
     */
    private static final String ERR = "Wrong result for Sphere.findIntersections";
    /**
     * First intersection point for EP02: ray from (-1,0,0) direction (3,1,0)
     */
    private static final Point INTER1 =
            new Point(0.0651530771650466, 0.355051025721682, 0);
    /**
     * Second intersection point for EP02
     */
    private static final Point INTER2 =
            new Point(1.53484692283495, 0.844948974278318, 0);

    /**
     * Test method for {@link Sphere#findIntersections(Ray)}.
     */
    @Test
    void testFindIntersections() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray's line is entirely outside the sphere – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(-1, 2, 0), new Vector(1, 0, 0))), ERR);

        // EP02: Ray starts before sphere and crosses it – 2 points
        final var result = SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(3, 1, 0)));
        assertNotNull(result, ERR);
        assertEquals(2, result.size(), ERR);
        assertEquals(List.of(INTER1, INTER2), result, ERR);

        // EP03: Ray starts inside the sphere – 1 point
        assertEquals(List.of(new Point(1, 1, 0)),
                SPHERE.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, 1, 0))),
                ERR);

        // EP04: Ray starts after the sphere (line crosses sphere but ray goes away) – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(-3, -1, 0))), ERR);

        // =============== Boundary Values Tests ==================

        // **** Group 1: Ray's line crosses the sphere (not through center)
        // BV11: Ray starts at sphere surface going inside – 1 point
        var resultBV11 = SPHERE.findIntersections(new Ray(new Point(2, 0, 0), new Vector(-1, 1, 0)));
        assertNotNull(resultBV11, ERR);
        assertEquals(1, resultBV11.size(), ERR);

        // BV12: Ray starts at sphere surface going outside – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, 1, 0))), ERR);

        // **** Group 2: Ray's line goes through the center
        // BV21: Ray starts before the sphere – 2 points
        assertEquals(List.of(new Point(0, 0, 0), new Point(2, 0, 0)),
                SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(1, 0, 0))),
                ERR);

        // BV22: Ray starts at sphere surface going inside through center – 1 point
        assertEquals(List.of(new Point(1, -1, 0)),
                SPHERE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(0, -1, 0))),
                ERR);

        // BV23: Ray starts inside sphere, direction through center – 1 point
        assertEquals(List.of(new Point(1, -1, 0)),
                SPHERE.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(0, -1, 0))),
                ERR);

        // BV24: Ray starts at the center – 1 point
        assertEquals(List.of(new Point(1, 1, 0)),
                SPHERE.findIntersections(new Ray(new Point(1, 0, 0), new Vector(0, 1, 0))),
                ERR);

        // BV25: Ray starts at sphere surface going outside (through center direction) – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 0))), ERR);

        // BV26: Ray starts after the sphere – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(-1, 0, 0), new Vector(-1, 0, 0))), ERR);

        // **** Group 3: Ray's line is tangent to the sphere (all 0 points)
        // BV31: Ray starts before the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(-1, 1, 0), new Vector(1, 0, 0))), ERR);

        // BV32: Ray starts at the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 0))), ERR);

        // BV33: Ray starts after the tangent point
        assertNull(SPHERE.findIntersections(new Ray(new Point(3, 1, 0), new Vector(1, 0, 0))), ERR);

        // **** Group 4: Special cases (P0→center perpendicular to ray direction)
        // BV41: P0 outside sphere, ray orthogonal to P0→center – 0 points
        assertNull(SPHERE.findIntersections(new Ray(new Point(1, 2, 0), new Vector(1, 0, 0))), ERR);

        // BV42: P0 inside sphere, ray orthogonal to P0→center – 1 point
        var resultBV42 = SPHERE.findIntersections(new Ray(new Point(1, 0.5, 0), new Vector(1, 0, 0)));
        assertNotNull(resultBV42, ERR);
        assertEquals(1, resultBV42.size(), ERR);
    }

    /**
     * Test method for {@link Sphere#getNormal(Point)}.
     */
    @Test
    void testGetNormal() {
        // ============ Equivalence Partitions Tests ==============

        // EP01: Normal at (0,0,1) on unit sphere centered at origin is (0,0,1)
        Sphere sphere = new Sphere(new Point(0, 0, 0), 1);
        Vector result = sphere.getNormal(new Point(0, 0, 1));
        assertEquals(new Vector(0, 0, 1), result,
                "getNormal() produced wrong direction");
        assertEquals(1.0, result.length(), DELTA,
                "getNormal() must return a unit vector");
    }
}
