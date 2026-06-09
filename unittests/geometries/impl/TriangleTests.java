package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link Triangle}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TriangleTests {

   /** Default constructor to satisfy JavaDoc generator */
   TriangleTests() { /* to satisfy JavaDoc generator */ }

   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;
   /**
    * Triangle used in findIntersections tests.
    * Vertices (2,0,0), (0,2,0), (0,0,2) lie on the plane x+y+z=2,
    * with outward normal (1,1,1)/√3.
    */
   private static final Triangle TRI = new Triangle(
         new Point(2, 0, 0),
         new Point(0, 2, 0),
         new Point(0, 0, 2));
   /** Error message for triangle intersection failures */
   private static final String   ERR = "Wrong result for Triangle.findIntersections";

   /**
    * Test method for {@link Triangle#findIntersections(Ray)}.
    * <p>
    * Structure: plane-inherited "no-intersection" cases → plane-inherited
    * "one-intersection" case → triangle-specific EP → triangle-specific BVA.
    * All rays use direction (0,0,−1) unless noted, starting at z=5 above the plane
    * x+y+z=2 (so the intersection is at the same (x,y) with z=2−x−y).
    * </p>
    */
   @Test
   void testFindIntersections() {

      // ============ Equivalence Partitions Tests ==============

      // EP01: Ray intersects the triangle's plane inside the triangle – 1 point
      // Ray from (0.5,0.5,5) direction (0,0,−1); plane intersection at (0.5,0.5,1) –
      // barycentric coords (0.25, 0.25, 0.5), all positive → strictly inside.
      assertEquals(List.of(new Point(0.5, 0.5, 1)),
                   TRI.findIntersections(new Ray(new Point(0.5, 0.5, 5), new Vector(0, 0, -1))),
                   ERR);

      // ==============================
      // Triangle-specific EP cases
      // ==============================

      // EP02: Intersection outside the triangle, against an edge – 0 points
      // Ray from (1,3,5) dir (0,0,−1); plane intersection (1,3,−2): y=3 > 2, z<0 → outside.
      assertNull(TRI.findIntersections(new Ray(new Point(1, 3, 5), new Vector(0, 0, -1))), ERR);

      // EP03: Intersection outside the triangle, against a vertex – 0 points
      // Ray from (3,−0.5,4.5) dir (0,0,−1); plane intersection (3,−0.5,−0.5): two barycentric
      // coords negative → in the "corner" cone past vertex (2,0,0).
      assertNull(TRI.findIntersections(new Ray(new Point(3, -0.5, 4.5), new Vector(0, 0, -1))),
                 ERR);

      // =============== Boundary Values Tests ==================

      // **** Cases derived from Plane: ray never reaches the plane (all 0 points) ****

      // BV01: Ray parallel to plane, NOT on the plane – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(3, 0, 0), new Vector(1, -1, 0))), ERR);

      // BV02: Ray parallel to plane, lies ON the plane – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, -1, 0))), ERR);

      // BV03: Ray origin on the plane, neither parallel nor orthogonal (t = 0) – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 0, 1))), ERR);

      // BV04: Ray origin at the plane's reference point Q = (2,0,0), t = 0 – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(2, 0, 0), new Vector(1, 0, 1))), ERR);

      // BV05: Ray orthogonal to plane, origin ON the plane (t = 0) – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(1, 1, 0), new Vector(1, 1, 1))), ERR);

      // BV06: Ray orthogonal to plane, origin AFTER the plane – 0 points
      assertNull(TRI.findIntersections(new Ray(new Point(2, 2, 2), new Vector(1, 1, 1))), ERR);

      // BV07: Ray goes away from the plane (t < 0) – 0 points
      // P0=(1,1,1) is above the plane (1+1+1=3>2); direction (0,1,0) has positive dot with n.
      assertNull(TRI.findIntersections(new Ray(new Point(1, 1, 1), new Vector(0, 1, 0))), ERR);

      // **** Case derived from Plane: orthogonal ray before plane → one intersection ****

      // BV08: Ray orthogonal to plane, starts before it, intersection inside triangle – 1 point
      // P0=(−0.5,−0.5,0) direction (1,1,1); plane intersection at (0.5,0.5,1) – inside.
      assertEquals(List.of(new Point(0.5, 0.5, 1)),
                   TRI.findIntersections(new Ray(new Point(-0.5, -0.5, 0), new Vector(1, 1, 1))),
                   ERR);

      // **** Triangle-specific BVA cases ****

      // BV09: Intersection on an edge – 0 points
      // Ray from (1,1,5) dir (0,0,−1); plane intersection (1,1,0) = midpoint of edge (2,0,0)-(0,2,0).
      assertNull(TRI.findIntersections(new Ray(new Point(1, 1, 5), new Vector(0, 0, -1))), ERR);

      // BV10: Intersection at a vertex – 0 points
      // Ray from (2,0,5) dir (0,0,−1); plane intersection (2,0,0) = vertex.
      assertNull(TRI.findIntersections(new Ray(new Point(2, 0, 5), new Vector(0, 0, -1))), ERR);

      // BV11: Intersection on the continuation of an edge – 0 points
      // Ray from (−1,3,5) dir (0,0,−1); plane intersection (−1,3,0):
      // parameter t=1.5 along edge direction from (2,0,0) → beyond vertex (0,2,0).
      assertNull(TRI.findIntersections(new Ray(new Point(-1, 3, 5), new Vector(0, 0, -1))), ERR);
   }

   /**
    * Test method for {@link geometries.api.Intersectable#calcIntersections(Ray, double)}.
    * <p>
    * Two cases: max-distance cutoff before and after the single triangle intersection.
    * </p>
    */
   @Test
   void testCalcIntersectionsWithMaxDistance() {
      // Ray from (0.5,0.5,5) direction (0,0,-1): hits triangle at (0.5,0.5,1), distance=4
      Ray ray = new Ray(new Point(0.5, 0.5, 5), new Vector(0, 0, -1));

      // Q before intersection – 0 results
      assertNull(TRI.calcIntersections(ray, 3), ERR);

      // Q past intersection – 1 result
      var result = TRI.calcIntersections(ray, 5);
      assertNotNull(result, ERR);
      assertEquals(1, result.size(), ERR);
   }

   /**
    * Test method for {@link Triangle#getNormal(Point)}.
    */
   @Test
   void testGetNormal() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Normal at an interior point is unit length and orthogonal to all three edges
      Point p1 = new Point(0, 0, 0);
      Point p2 = new Point(1, 0, 0);
      Point p3 = new Point(0, 1, 0);
      Triangle triangle = new Triangle(p1, p2, p3);
      Vector normal = triangle.getNormal(new Point(0.25, 0.25, 0));
      assertEquals(1.0, normal.length(), DELTA,
                   "getNormal() must return a unit vector");
      assertEquals(0.0, normal.dotProduct(p2.subtract(p1)), DELTA,
                   "getNormal() must be orthogonal to edge p2-p1");
      assertEquals(0.0, normal.dotProduct(p3.subtract(p1)), DELTA,
                   "getNormal() must be orthogonal to edge p3-p1");
      assertEquals(0.0, normal.dotProduct(p3.subtract(p2)), DELTA,
                   "getNormal() must be orthogonal to edge p3-p2");
   }
}
