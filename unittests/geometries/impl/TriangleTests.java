package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
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
