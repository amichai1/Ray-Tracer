package geometries.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

/**
 * Unit tests for class {@link Tube}.
 * Tests follow the methodology of Equivalence Partitions (EP) and Boundary Values (BVA).
 */
class TubeTests {

   /** Default constructor to satisfy JavaDoc generator */
   TubeTests() { /* to satisfy JavaDoc generator */ }

   /** Tube axis along Z from the origin, radius 1 */
   private static final Ray  AXIS = new Ray(new Point(0, 0, 0), new Vector(0, 0, 1));
   /** Tube under test */
   private static final Tube TUBE = new Tube(1, AXIS);
   /** Delta value for accuracy when comparing double values */
   private static final double DELTA = 1e-6;

   /**
    * Test method for {@link Tube#getNormal(Point)}.
    */
   @Test
   void testGetNormal() {
      // ============ Equivalence Partitions Tests ==============

      // EP01: Point (1,0,1) — projection t=1, normal is (1,0,0)
      assertEquals(new Vector(1, 0, 0), TUBE.getNormal(new Point(1, 0, 1)),
                   "getNormal() wrong for t=1");

      // EP02: Point (1,0,-1) — projection t=-1, normal is (1,0,0)
      assertEquals(new Vector(1, 0, 0), TUBE.getNormal(new Point(1, 0, -1)),
                   "getNormal() wrong for t=-1");

      // =============== Boundary Values Tests ==================

      // BV01: Point (1,0,0) — projection t=0 exactly (at axis origin), normal is (1,0,0)
      assertEquals(new Vector(1, 0, 0), TUBE.getNormal(new Point(1, 0, 0)),
                   "getNormal() wrong for t=0 (axis head)");
   }
}
