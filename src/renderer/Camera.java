package renderer;

import java.util.MissingResourceException;

import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import static primitives.Util.isZero;

/**
 * Represents a camera in a 3D scene.
 * <p>
 * A camera is defined by its location, an orthonormal orientation frame
 * ({@code _vTo}, {@code _vUp}, {@code _vRight}), and a view-plane
 * configuration (width, height, distance, resolution).
 * </p>
 * <p>
 * Construction is performed exclusively through the nested {@link Builder}
 * class via {@link #getBuilder()}.
 * </p>
 *
 * @author Amichai Mukades
 */
public class Camera implements Cloneable {

    /** Camera location in 3D space. */
    Point _p0;

    /** Normalized forward direction vector. */
    Vector _vTo;

    /** Normalized up direction vector (orthogonal to {@code _vTo}). */
    Vector _vUp;

    /** Normalized right direction vector, computed as {@code _vTo × _vUp}. */
    Vector _vRight;

    /** Physical width of the view plane. */
    double _width;

    /** Physical height of the view plane. */
    double _height;

    /** Distance from the camera to the view plane. */
    double _distance;

    /** Number of pixels along the horizontal axis. */
    int _nX = 1;

    /** Number of pixels along the vertical axis. */
    int _nY = 1;

    /** Center point of the view plane (pre-computed in {@link Builder#build()}). */
    Point _vpCenter;

    /** Width of a single pixel (pre-computed in {@link Builder#build()}). */
    double _pixelWidth;

    /** Height of a single pixel (pre-computed in {@link Builder#build()}). */
    double _pixelHeight;

    /**
     * Private default constructor.
     * <p>
     * Instances are created only through {@link Builder}.
     * </p>
     */
    private Camera() {}

    /**
     * Returns a new {@link Builder} for constructing a {@link Camera}.
     *
     * @return a fresh {@code Builder} instance
     */
    public static Builder getBuilder() {
        return new Builder();
    }

    /**
     * Constructs a ray through the center of pixel {@code [xIndex, yIndex]}
     * on the view plane.
     * <p>
     * {@code xIndex} is the column index (horizontal, along {@code _vRight})
     * and {@code yIndex} is the row index (vertical, along {@code _vUp}).
     * </p>
     *
     * @param  xIndex column index of the pixel (0-based, left to right)
     * @param  yIndex row index of the pixel (0-based, top to bottom)
     * @return        the ray from the camera location through the pixel center
     */
    public Ray constructRay(int xIndex, int yIndex) {
        double xJ = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yI = -(yIndex - (_nY - 1) / 2.0) * _pixelHeight;

        Point pIJ = _vpCenter;
        if (!isZero(xJ)) pIJ = pIJ.add(_vRight.scale(xJ));
        if (!isZero(yI)) pIJ = pIJ.add(_vUp.scale(yI));

        return new Ray(_p0, pIJ.subtract(_p0));
    }

    /**
     * Builder for {@link Camera}.
     * <p>
     * Creates a {@code Camera} object incrementally via setter-style methods,
     * then finalises and validates it with {@link #build()}.
     * All setters return {@code this} to allow method chaining.
     * </p>
     */
    public static class Builder {

        /** The camera being built. */
        private final Camera _camera = new Camera();

        /** Explicit forward direction (null if a target point was supplied instead). */
        private Vector _vTo = null;

        /** Target point the camera looks at (null if an explicit forward vector was supplied). */
        private Point _target = null;

        /** General up vector used to derive the orthonormal frame. */
        private Vector _vUp = Vector.AXIS_Y;

        /**
         * Sets the camera location.
         *
         * @param  location  the position of the camera in 3D space
         * @return           this builder
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the camera orientation using explicit forward and up vectors.
         *
         * @param  to  the forward direction vector
         * @param  up  the general up direction vector
         * @return     this builder
         */
        public Builder setDirection(Vector to, Vector up) {
            _vTo     = to;
            _vUp     = up;
            _target  = null;
            return this;
        }

        /**
         * Sets the camera orientation using a target point and an explicit up vector.
         *
         * @param  target  the point the camera looks at
         * @param  up      the general up direction vector
         * @return         this builder
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUp    = up;
            _vTo    = null;
            return this;
        }

        /**
         * Sets the camera orientation using a target point; the up direction
         * defaults to the world Y axis ({@link Vector#AXIS_Y}).
         *
         * @param  target  the point the camera looks at
         * @return         this builder
         */
        public Builder setDirection(Point target) {
            _target = target;
            _vTo    = null;
            _vUp    = Vector.AXIS_Y;
            return this;
        }

        /**
         * Sets the physical size of the view plane.
         *
         * @param  width   the horizontal size of the view plane
         * @param  height  the vertical size of the view plane
         * @return         this builder
         */
        public Builder setVpSize(double width, double height) {
            _camera._width  = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param  distance  the distance to the view plane
         * @return           this builder
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the pixel resolution of the view plane.
         *
         * @param  nX  number of pixels horizontally
         * @param  nY  number of pixels vertically
         * @return     this builder
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Validates all parameters and returns the fully initialised {@link Camera}.
         * <p>
         * The order of validation calls is mandatory.
         * </p>
         *
         * @return a ready-to-use {@code Camera}
         * @throws IllegalArgumentException  if any numeric parameter is non-positive
         *                                   or if the orientation vectors are parallel
         * @throws MissingResourceException  if location or direction data are absent
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

        /**
         * Validates that resolution values are positive.
         *
         * @throws IllegalArgumentException if {@code nX} or {@code nY} is not positive
         */
        private void checkResolution() {
            if (_camera._nX <= 0)
                throw new IllegalArgumentException("nX must be positive, got: " + _camera._nX);
            if (_camera._nY <= 0)
                throw new IllegalArgumentException("nY must be positive, got: " + _camera._nY);
        }

        /**
         * Validates location and direction data, computes and normalises the
         * three orientation vectors, and updates the camera.
         *
         * @throws MissingResourceException  if location, direction, or up vector is missing
         * @throws IllegalArgumentException  if the forward and up vectors are parallel
         */
        private void checkLocationAndDirection() {
            if (_camera._p0 == null)
                throw new MissingResourceException("Camera location is missing",
                        Camera.class.getName(), "_p0");
            if (_vTo == null && _target == null)
                throw new MissingResourceException("Camera direction is missing",
                        Camera.class.getName(), "_vTo/_target");
            if (_vUp == null)
                throw new MissingResourceException("Camera up vector is missing",
                        Camera.class.getName(), "_vUp");

            if (_vTo == null)
                _vTo = _target.subtract(_camera._p0);

            _camera._vTo = _vTo.normalize();

            try {
                _camera._vRight = _camera._vTo.crossProduct(_vUp).normalize();
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Forward and up vectors must not be parallel", e);
            }

            _camera._vUp = _camera._vRight.crossProduct(_camera._vTo).normalize();
        }

        /**
         * Validates view-plane size and distance, then pre-computes the view-plane
         * center and pixel dimensions.
         *
         * @throws IllegalArgumentException if width, height, or distance is not positive
         */
        private void checkViewPlane() {
            if (_camera._width <= 0)
                throw new IllegalArgumentException("View-plane width must be positive");
            if (_camera._height <= 0)
                throw new IllegalArgumentException("View-plane height must be positive");
            if (_camera._distance <= 0)
                throw new IllegalArgumentException("View-plane distance must be positive");

            _camera._vpCenter    = _camera._p0.add(_camera._vTo.scale(_camera._distance));
            _camera._pixelWidth  = _camera._width  / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }
    }
}
