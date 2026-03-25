package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Ray}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class RayTests {

   /** Default constructor to satisfy JavaDoc generator */
   RayTests() { /* to satisfy JavaDoc generator */ }

   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

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
