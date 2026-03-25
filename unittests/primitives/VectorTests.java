package primitives;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import primitives.Util;

/**
 * Unit tests for class {@link Vector}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class VectorTests {

   /** Default constructor to satisfy JavaDoc generator */
   VectorTests() { /* to satisfy JavaDoc generator */ }

   /** Vector (1,2,3) used in most tests */
   private static final Vector V1   = new Vector(1, 2, 3);
   /** Vector (2,3,4) used in most tests */
   private static final Vector V2   = new Vector(2, 3, 4);
   /** Vector (0,3,4) — length 5 (Pythagorean triple) */
   private static final Vector V034 = new Vector(0, 3, 4);
   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

   /**
    * Test method for {@link Vector#Vector(double, double, double)}.
    */
   @Test
   void testVector() {
      // =============== Boundary Values Tests ==================

      // BV01: Zero vector construction must throw
      assertThrows(IllegalArgumentException.class, () -> new Vector(0, 0, 0),
                   "Constructor must throw for zero vector");
   }

   /**
    * Test method for {@link Vector#add(Vector)}.
    */
   @Test
   void testAdd() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Adding two vectors produces the expected result
      assertEquals(new Vector(3, 5, 7), V1.add(V2),
                   "add() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Adding opposite vectors produces zero vector — must throw
      assertThrows(IllegalArgumentException.class, () -> V1.add(new Vector(-1, -2, -3)),
                   "add() must throw when result is zero vector");
   }

   /**
    * Test method for {@link Vector#subtract(Point)} (inherited from {@link Point}).
    */
   @Test
   void testSubtract() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Subtracting two distinct vectors produces the expected result
      assertEquals(new Vector(1, 1, 1), V2.subtract(V1),
                   "subtract() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Subtracting a vector from itself must throw (zero vector)
      assertThrows(IllegalArgumentException.class, () -> V1.subtract(V1),
                   "subtract() must throw when result is zero vector");
   }

   /**
    * Test method for {@link Vector#scale(double)}.
    */
   @Test
   void testScale() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Scaling by a non-zero scalar produces the expected result
      assertEquals(new Vector(2, 4, 6), V1.scale(2),
                   "scale() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Scaling by zero must throw (zero vector)
      assertThrows(IllegalArgumentException.class, () -> V1.scale(0),
                   "scale() must throw for scalar = 0");
   }

   /**
    * Test method for {@link Vector#dotProduct(Vector)}.
    */
   @Test
   void testDotProduct() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Perpendicular vectors have dot product 0
      assertEquals(0.0, new Vector(1, 0, 0).dotProduct(new Vector(0, 1, 0)), DELTA,
                   "dotProduct() of perpendicular vectors must be 0");

      // EP02: Regular dot product
      assertEquals(20.0, V1.dotProduct(V2), DELTA,
                   "dotProduct() produced wrong result");
   }

   /**
    * Test method for {@link Vector#crossProduct(Vector)}.
    */
   @Test
   void testCrossProduct() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Cross product of unit X and Y axes is unit Z axis
      assertEquals(new Vector(0, 0, 1), new Vector(1, 0, 0).crossProduct(new Vector(0, 1, 0)),
                   "crossProduct() produced wrong result");

      // =============== Boundary Values Tests ==================

      // BV01: Cross product of parallel vectors must throw (zero vector)
      assertThrows(IllegalArgumentException.class, () -> V1.crossProduct(V1),
                   "crossProduct() must throw for parallel vectors");
   }

   /**
    * Test method for {@link Vector#lengthSquared()}.
    */
   @Test
   void testLengthSquared() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Squared length of (0,3,4) is 25
      assertEquals(25.0, V034.lengthSquared(), DELTA,
                   "lengthSquared() produced wrong result");
   }

   /**
    * Test method for {@link Vector#length()}.
    */
   @Test
   void testLength() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Length of (0,3,4) is 5 (Pythagorean triple)
      assertEquals(5.0, V034.length(), DELTA,
                   "length() produced wrong result");
   }

   /**
    * Test method for {@link Vector#normalize()}.
    */
   @Test
   void testNormalize() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Normalized vector has unit length, same direction, and is parallel to original
      Vector n = V034.normalize();
      assertEquals(1.0, n.length(), DELTA, "normalize() result must have unit length");
      assertTrue(Util.alignZero(V034.dotProduct(n)) > 0, "normalize() result must point in same direction");
      assertThrows(IllegalArgumentException.class, () -> V034.crossProduct(n),
                   "normalize() result must be parallel to original (cross product = zero)");
   }
}
