package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for class {@link Point}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PointTests {

   /** Default constructor to satisfy JavaDoc generator */
   PointTests() { /* to satisfy JavaDoc generator */ }

   /** First test point */
   private static final Point  P1    = new Point(1, 2, 3);
   /** Second test point */
   private static final Point  P2    = new Point(2, 3, 4);
   /** Vector (1,1,1) used in add tests */
   private static final Vector V1    = new Vector(1, 1, 1);
   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

   /**
    * Test method for {@link Point#add(Vector)}.
    */
   @Test
   void testAdd() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Adding a vector to a point produces the expected point
      assertEquals(new Point(2, 3, 4), P1.add(V1),
                   "add() produced wrong result");
   }

   /**
    * Test method for {@link Point#subtract(Point)}.
    */
   @Test
   void testSubtract() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Subtracting two distinct points produces the expected vector
      assertEquals(new Vector(1, 1, 1), P2.subtract(P1),
                   "subtract() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Subtracting a point from itself throws (zero vector)
      assertThrows(IllegalArgumentException.class, () -> P1.subtract(P1),
                   "subtract() must throw for identical points (zero vector)");
   }

   /**
    * Test method for {@link Point#distanceSquared(Point)}.
    */
   @Test
   void testDistanceSquared() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Squared distance from origin to (3,4,0) is 25
      assertEquals(25.0, new Point(0, 0, 0).distanceSquared(new Point(3, 4, 0)), DELTA,
                   "distanceSquared() produced wrong result");
   }

   /**
    * Test method for {@link Point#distance(Point)}.
    */
   @Test
   void testDistance() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Distance from origin to (3,4,0) is 5
      assertEquals(5.0, new Point(0, 0, 0).distance(new Point(3, 4, 0)), DELTA,
                   "distance() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Distance from a point to itself is 0
      assertEquals(0.0, P1.distance(P1), DELTA,
                   "distance() from point to itself must be 0");
   }
}
