package geometries.impl;

import org.junit.jupiter.api.Test;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit tests for class {@link Geometries}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class GeometriesTests {

    /**
     * Default constructor to satisfy JavaDoc generator
     */
    GeometriesTests() { /* to satisfy JavaDoc generator */ }

    /**
     * Test method for {@link Geometries#findIntersections(Ray)}.
     * <p>
     * Scene used in all tests: a Sphere (center (1,0,0) r=1),
     * a Plane (y=0, normal (0,1,0)), and a Triangle in the plane y=3
     * with vertices (−1,3,−1), (3,3,−1), (1,3,3).
     * </p>
     */
    @Test
    void testFindIntersections() {
        // Scene: sphere, plane, triangle
        Sphere sphere = new Sphere(new Point(1, 0, 0), 1d);
        Plane plane = new Plane(new Point(2, 0, 0), new Vector(0, 1, 0));
        Triangle triangle = new Triangle(
                new Point(-1, 3, -1),
                new Point(3, 3, -1),
                new Point(1, 3, 3));

        // ============ Equivalence Partitions Tests ==============

        // EP01: Ray intersects some (but not all) of the geometries – finite result
        // Use Ray from (5,−2,0) dir (0,1,0):
        //   plane y=0: t=2 → (5,0,0). 1 point.
        //   sphere: |P0-C|=|(5-1,-2,0)|=sqrt(16+4)≈4.5>1 → miss.
        //   triangle at y=3: t=5 → (5,3,0). Inside triangle? x=5 > 3 (rightmost vertex x=3) → outside.
        //   Total: 1 intersection (plane only).
        Geometries sceneEP = new Geometries(sphere, plane, triangle);
        var resultEP = sceneEP.findIntersections(new Ray(new Point(5, -2, 0), new Vector(0, 1, 0)));
        assertEquals(1, resultEP.size(), "EP01: expected 1 intersection (plane only)");

        // =============== Boundary Values Tests ==================

        // BV01: No geometry is intersected – null
        // Ray from (5,5,5) dir (1,0,0): misses all three geometries.
        //   sphere center (1,0,0) r=1: ray at y=5 → distance from center to ray line = 5 > 1 → miss.
        //   plane y=0: ray direction (1,0,0) is parallel to plane (n=(0,1,0)·(1,0,0)=0) → miss.
        //   triangle at y=3: ray stays at y=5 ≠ 3 → miss.
        assertNull(
                new Geometries(sphere, plane, triangle)
                        .findIntersections(new Ray(new Point(5, 5, 5), new Vector(1, 0, 0))),
                "BV01: expected null (no intersections)");

        // BV02: Only one geometry is intersected – exactly those points
        // Ray from (1,−2,5) dir (0,1,0):
        //   plane y=0: t=2 → (1,0,5). 1 point.
        //   sphere: |(1-1,-2,5-0)|=sqrt(29)>1 → miss.
        //   triangle y=3: t=5 → (1,3,5). Is (1,3,5) inside Triangle(−1,3,−1),(3,3,−1),(1,3,3)?
        //   z=5 > 3 (highest z vertex is 3) → outside → miss.
        //   Total: 1 intersection (plane only).
        var resultBV02 = new Geometries(sphere, plane, triangle)
                .findIntersections(new Ray(new Point(1, -2, 5), new Vector(0, 1, 0)));
        assertEquals(1, resultBV02.size(), "BV02: expected 1 intersection (one body only)");

        // BV03: All geometries are intersected
        // Ray from (1,−3,0) dir (0,1,0):
        //   plane y=0: t=3 → (1,0,0). 1 point.
        //   sphere: u=(0,3,0), tm=3, d²=9-9=0, th=1, t1=2 → (1,-1,0), t2=4 → (1,1,0). 2 points.
        //   triangle y=3: t=6 → (1,3,0). Inside Triangle(−1,3,−1),(3,3,−1),(1,3,3)?
        //     All vertices have y=3. In plane y=3, check (1,3,0):
        //     The triangle has vertices (−1,−1),(3,−1),(1,3) in the (x,z) coords at y=3.
        //     (1,0): x+z range... let me check barycentric: centroid=((−1+3+1)/3, (−1−1+3)/3)=(1,1/3).
        //     Edge (−1,−1)→(3,−1): z=−1, for x in [−1,3]. Point (1,0): z=0 > −1 ✓.
        //     The triangle contains (1,0) if we check sign: it should be inside.
        //     Actually: (1,3,0) in 2D as (x=1,z=0). Vertices: A=(−1,−1),B=(3,−1),C=(1,3).
        //     Sign of edge AB→(1,0): (3−(−1))*(0−(−1)) − (−1−(−1))*(1−(−1)) = 4*1 − 0*2 = 4 > 0
        //     Sign of edge BC→(1,0): (1−3)*(0−(−1)) − (3−(−1))*(1−3) = (−2)*1 − 4*(−2) = −2+8 = 6 > 0
        //     Sign of edge CA→(1,0): (−1−1)*(0−3) − (−1−3)*(1−1) = (−2)*(−3)−(−4)*0 = 6 > 0
        //     All same sign → inside ✓ → 1 point.
        //   Total: 1 + 2 + 1 = 4 intersections.
        var resultBV03 = new Geometries(sphere, plane, triangle)
                .findIntersections(new Ray(new Point(1, -3, 0), new Vector(0, 1, 0)));
        assertEquals(4, resultBV03.size(), "BV03: expected 4 intersections (all bodies)");


        // BV04: Empty collection of geometries – null
        assertNull(new Geometries().findIntersections(new Ray(new Point(1, 2, 3), new Vector(1, 1, 1))),
                "BV00: Empty collection should return null");
    }
}
