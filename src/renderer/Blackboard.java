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
 * axes ({@code _vRight} and {@code _vUp}).  Sample positions are computed as
 * 2-D offsets inside the area and then projected into 3-D by combining those
 * axes.</p>
 *
 * <p>This class is used by {@link Camera} to implement super-sampling for
 * both anti-aliasing (samples spread across a pixel) and depth-of-field
 * (samples spread across the aperture disc).</p>
 *
 * <p>When {@code _size == 0} or {@code _numSamples == 1} the list contains only
 * the centre point, so the caller degrades gracefully to a single ray.</p>
 */
class Blackboard {

   // ── sampling-pattern options ─────────────────────────────────────────────

   /**
    * Strategy used to place sample points inside the target area.
    * <ul>
    *   <li>{@link #GRID}     – deterministic regular grid</li>
    *   <li>{@link #JITTERED} – stratified random jitter</li>
    * </ul>
    */
   enum SamplingPattern {
      /** Regular N×N grid; each sample is at the centre of its cell. */
      GRID,
      /** Stratified jitter; each sample is randomised within its cell. */
      JITTERED
   }

   // ── fields ───────────────────────────────────────────────────────────────

   /** Centre of the target area in world space. */
   private final Point  _center;

   /** Local horizontal axis of the target area (unit vector). */
   private final Vector _vRight;

   /** Local vertical axis of the target area (unit vector). */
   private final Vector _vUp;

   /**
    * Half-size of the target area along each local axis.
    * The full side length is {@code 2 * _size}.
    * When zero, only the centre point is generated.
    */
   private double _size = 0;

   /**
    * Number of samples along each axis of the grid.
    * Total samples = {@code _numSamples * _numSamples}.
    * When 1, only the centre point is generated.
    */
   private int _numSamples = 1;

   /** Active sampling strategy. */
   private SamplingPattern _pattern = SamplingPattern.GRID;

   // ── constructor ──────────────────────────────────────────────────────────

   /**
    * Creates a Blackboard centred at {@code center} with the given local axes.
    *
    * @param  center the centre of the target area
    * @param  vRight the local horizontal unit vector
    * @param  vUp    the local vertical unit vector
    */
   Blackboard(Point center, Vector vRight, Vector vUp) {
      _center = center;
      _vRight = vRight;
      _vUp    = vUp;
   }

   // ── fluent setters ───────────────────────────────────────────────────────

   /**
    * Sets the half-size of the target area.
    *
    * @param  size half-side length; must be ≥ 0
    * @return      this blackboard (for chaining)
    */
   Blackboard setSize(double size) {
      _size = size;
      return this;
   }

   /**
    * Sets the number of samples along each axis.
    * Total sample count will be {@code n * n}.
    *
    * @param  n samples per axis; must be ≥ 1
    * @return   this blackboard (for chaining)
    */
   Blackboard setNumSamples(int n) {
      _numSamples = n;
      return this;
   }

   /**
    * Sets the sampling pattern.
    *
    * @param  pattern GRID or JITTERED
    * @return         this blackboard (for chaining)
    */
   Blackboard setPattern(SamplingPattern pattern) {
      _pattern = pattern;
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
      if (isZero(_size) || _numSamples == 1)
         return List.of(_center);

      List<Point> points = new ArrayList<>(_numSamples * _numSamples);

      double cellSize = (2.0 * _size) / _numSamples;
      double start    = -_size + cellSize / 2.0;

      for (int row = 0; row < _numSamples; row++) {
         for (int col = 0; col < _numSamples; col++) {
            double offsetX = start + col * cellSize;
            double offsetY = start + row * cellSize;

            if (_pattern == SamplingPattern.JITTERED) {
               offsetX += (Math.random() - 0.5) * cellSize;
               offsetY += (Math.random() - 0.5) * cellSize;
            }

            Point samplePoint = _center;
            if (!isZero(offsetX)) samplePoint = samplePoint.add(_vRight.scale(offsetX));
            if (!isZero(offsetY)) samplePoint = samplePoint.add(_vUp.scale(offsetY));
            points.add(samplePoint);
         }
      }
      return points;
   }
}
