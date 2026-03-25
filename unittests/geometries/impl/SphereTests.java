package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Sphere}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class SphereTests {

   /** Default constructor to satisfy JavaDoc generator */
   SphereTests() { /* to satisfy JavaDoc generator */ }

   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

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
