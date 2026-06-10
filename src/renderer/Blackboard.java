package renderer;

import primitives.Point;
import primitives.Vector;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * Generates a grid of 3D sample points spread over a flat rectangular target
 * area in world space.
 *
 * <p>The target area is defined by a centre point and two orthonormal local
 * axes ({@code vRight} and {@code vUp}).  Sample positions are computed as
 * 2-D offsets inside the area and then projected into 3-D by combining those
 * axes.</p>
 *
 * <p>This class is used by {@link Camera} to implement super-sampling for
 * both anti-aliasing (samples spread across a pixel) and depth-of-field
 * (samples spread across the aperture disc).</p>
 *
 * <p>When {@code size == 0} or {@code numSamples == 1} the list contains only
 * the centre point, so the caller degrades gracefully to a single ray.</p>
 */
class Blackboard {

   // ── sampling-pattern options ─────────────────────────────────────────────

   /** Strategy used to place sample points inside the target area. */
   enum SamplingPattern {
      /** Regular N×N grid; each sample is at the centre of its cell. */
      GRID,
      /** Stratified jitter; each sample is randomised within its cell. */
      JITTERED
   }

   // ── fields ───────────────────────────────────────────────────────────────

   /** Centre of the target area in world space. */
   private Point  center;

   /** Local horizontal axis of the target area (unit vector). */
   private Vector vRight;

   /** Local vertical axis of the target area (unit vector). */
   private Vector vUp;

   /**
    * Half-size of the target area along each local axis.
    * The full side length is {@code 2 * size}.
    * When zero, only the centre point is generated.
    */
   private double size = 0;

   /**
    * Number of samples along each axis of the grid.
    * Total samples = {@code numSamples * numSamples}.
    * When 1, only the centre point is generated.
    */
   private int numSamples = 1;

   /** Active sampling strategy. */
   private SamplingPattern pattern = SamplingPattern.GRID;

   // ── constructor ──────────────────────────────────────────────────────────

   /**
    * Creates a Blackboard centred at {@code center} with the given local axes.
    *
    * @param center the centre of the target area
    * @param vRight the local horizontal unit vector
    * @param vUp    the local vertical unit vector
    */
   Blackboard(Point center, Vector vRight, Vector vUp) {
      this.center = center;
      this.vRight = vRight;
      this.vUp    = vUp;
   }

   // ── fluent setters ───────────────────────────────────────────────────────

   /**
    * Sets the half-size of the target area.
    *
    * @param size half-side length; must be ≥ 0
    * @return this blackboard (for chaining)
    */
   Blackboard setSize(double size) {
      this.size = size;
      return this;
   }

   /**
    * Sets the number of samples along each axis.
    * Total sample count will be {@code n * n}.
    *
    * @param n samples per axis; must be ≥ 1
    * @return this blackboard (for chaining)
    */
   Blackboard setNumSamples(int n) {
      this.numSamples = n;
      return this;
   }

   /**
    * Sets the sampling pattern.
    *
    * @param pattern GRID or JITTERED
    * @return this blackboard (for chaining)
    */
   Blackboard setPattern(SamplingPattern pattern) {
      this.pattern = pattern;
      return this;
   }

   // ── core method ──────────────────────────────────────────────────────────

   /**
    * Generates and returns the list of 3-D sample points for this target area.
    *
    * <p>Returns a single-element list containing only {@code center} when
    * {@code size == 0} or {@code numSamples == 1}, allowing callers to
    * degrade naturally to a single ray without branching.</p>
    *
    * @return a non-empty list of world-space sample points
    */
   List<Point> generateSamplePoints() {
      if (isZero(size) || numSamples == 1)
         return List.of(center);

      List<Point> points = new ArrayList<>(numSamples * numSamples);

      double cellSize = (2.0 * size) / numSamples;
      double start    = -size + cellSize / 2.0;

      for (int row = 0; row < numSamples; row++) {
         for (int col = 0; col < numSamples; col++) {
            double dx = start + col * cellSize;
            double dy = start + row * cellSize;

            if (pattern == SamplingPattern.JITTERED) {
               dx += (Math.random() - 0.5) * cellSize;
               dy += (Math.random() - 0.5) * cellSize;
            }

            Point p = center;
            if (!isZero(dx)) p = p.add(vRight.scale(dx));
            if (!isZero(dy)) p = p.add(vUp.scale(dy));
            points.add(p);
         }
      }
      return points;
   }
}
