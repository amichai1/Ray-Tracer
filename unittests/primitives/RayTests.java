package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Ray}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class RayTests {

   /** Default constructor to satisfy JavaDoc generator */
   RayTests() { /* to satisfy JavaDoc generator */ }

   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

   /** Ray used in getPoint tests */
   private static final Ray    RAY_FOR_GET_POINT       = new Ray(new Point(1, 2, 3), new Vector(1, 0, 0));
   /** Ray used in findClosestPoint tests – origin at (0,0,0) facing −Z */
   private static final Ray    RAY_FOR_CLOSEST         = new Ray(Point.ZERO, new Vector(0, 0, -1));
   /** Error message for findClosestPoint failures */
   private static final String ERR_FIND_CLOSEST        = "Wrong result for findClosestPoint";
   /** Point at distance² = 1 from origin */
   private static final Point  P_CLOSE                 = new Point(1, 0, 0);
   /** Point at distance² = 9 from origin */
   private static final Point  P_MEDIUM                = new Point(0, 3, 0);
   /** Point at distance² = 25 from origin */
   private static final Point  P_FAR                   = new Point(0, 0, -5);
   /** Error message for getPoint failures */
   private static final String ERR_GET_POINT     = "Wrong result for getPoint";

   /**
    * Test method for {@link Ray#getPoint(double)}.
    */
   @Test
   void testGetPoint() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: t > 0 – point is ahead of origin along ray direction
      assertEquals(new Point(4, 2, 3), RAY_FOR_GET_POINT.getPoint(3),
                   ERR_GET_POINT);

      // EP02: t < 0 – point is behind origin (opposite direction)
      assertEquals(new Point(-1, 2, 3), RAY_FOR_GET_POINT.getPoint(-2),
                   ERR_GET_POINT);

      // =============== Boundary Values Tests ==================

      // BV01: t = 0 – result must be the ray's origin
      assertEquals(new Point(1, 2, 3), RAY_FOR_GET_POINT.getPoint(0),
                   ERR_GET_POINT);
   }

   /**
    * Test method for {@link Ray#findClosestPoint(List)}.
    */
   @Test
   void testFindClosestPoint() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Closest point is in the middle of the list
      assertEquals(P_CLOSE,
                   RAY_FOR_CLOSEST.findClosestPoint(List.of(P_FAR, P_CLOSE, P_MEDIUM)),
                   ERR_FIND_CLOSEST);

      // =============== Boundary Values Tests ==================

      // BV01: List is null – must return null
      assertNull(RAY_FOR_CLOSEST.findClosestPoint(null),
                 ERR_FIND_CLOSEST);

      // BV02: Closest point is the first element in the list
      assertEquals(P_CLOSE,
                   RAY_FOR_CLOSEST.findClosestPoint(List.of(P_CLOSE, P_MEDIUM, P_FAR)),
                   ERR_FIND_CLOSEST);

      // BV03: Closest point is the last element in the list
      assertEquals(P_CLOSE,
                   RAY_FOR_CLOSEST.findClosestPoint(List.of(P_FAR, P_MEDIUM, P_CLOSE)),
                   ERR_FIND_CLOSEST);
   }

   /**
    * Test method for {@link Ray#Ray(Point, Vector)}.
    */
   @Test
   void testRay() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Direction is normalized in constructor even if input is not unit
      Ray r = new Ray(new Point(0, 0, 0), new Vector(0, 3, 4));
      assertEquals(1.0, r.direction().length(), DELTA,
                   "Ray constructor must normalize direction vector");

      // =============== Boundary Values Tests ==================

      // BV01: Already-normalized direction is preserved exactly
      Vector unitDir = new Vector(1, 0, 0);
      Ray r2 = new Ray(new Point(1, 1, 1), unitDir);
      assertEquals(unitDir, r2.direction(),
                   "Ray constructor must preserve an already-normalized direction");
   }
}
