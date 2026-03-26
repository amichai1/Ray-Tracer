package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Vector;

/**
 * Unit tests for class {@link Plane}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class PlaneTests {

   /** Default constructor to satisfy JavaDoc generator */
   PlaneTests() { /* to satisfy JavaDoc generator */ }

   /** First point on the plane */
   private static final Point  P1    = new Point(0, 0, 1);
   /** Second point on the plane */
   private static final Point  P2    = new Point(1, 0, 0);
   /** Third point on the plane */
   private static final Point  P3    = new Point(0, 1, 0);
   /** Delta value for accuracy when comparing double values */
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

    @test
    void testGetNormal2() {

    }

}
