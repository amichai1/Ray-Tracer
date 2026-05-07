package renderer;

import java.util.MissingResourceException;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;
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

    /**
     * Camera location in 3D space.
     */
    private Point _p0;

    /**
     * Normalized forward direction vector.
     */
    private Vector _vTo;

    /**
     * Normalized up direction vector (orthogonal to {@code _vTo}).
     */
    private Vector _vUp;

    /**
     * Normalized right direction vector, computed as {@code _vTo × _vUp}.
     */
    private Vector _vRight;

    /**
     * Physical width of the view plane.
     */
    private double _width;

    /**
     * Physical height of the view plane.
     */
    private double _height;

    /**
     * Distance from the camera to the view plane.
     */
    private double _distance;

    /**
     * Number of pixels along the horizontal axis.
     */
    private int _nX = 1;

    /**
     * Number of pixels along the vertical axis.
     */
    private int _nY = 1;

    /**
     * Center point of the view plane (pre-computed in {@link Builder#build()}).
     */
    private Point _vpCenter;

    /**
     * Width of a single pixel (pre-computed in {@link Builder#build()}).
     */
    private double _pixelWidth;

    /**
     * Height of a single pixel (pre-computed in {@link Builder#build()}).
     */
    private double _pixelHeight;

    /**
     * Ray tracer used to compute pixel colors during rendering.
     */
    private RayTracerBase _rayTracer;

    /**
     * Image buffer used to accumulate and export pixel colors.
     */
    private ImageWriter _imageWriter;

    /**
     * Private default constructor.
     * <p>
     * Instances are created only through {@link Builder}.
     * </p>
     */
    private Camera() {
    }

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
     * @param xIndex column index of the pixel (0-based, left to right)
     * @param yIndex row index of the pixel (0-based, top to bottom)
     * @return the ray from the camera location through the pixel center
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
     * Casts a ray through pixel {@code (xIndex, yIndex)}, traces it, and writes
     * the resulting color to the image buffer.
     *
     * @param xIndex the pixel column index (0-based)
     * @param yIndex the pixel row index (0-based)
     */
    private void castRay(int xIndex, int yIndex) {
        _imageWriter.writePixel(xIndex, yIndex, _rayTracer.traceRay(constructRay(xIndex, yIndex)));
    }

    /**
     * Renders the scene by casting a ray through every pixel and writing the
     * resulting color to the image buffer.
     *
     * @return this camera (for method chaining)
     */
    public Camera renderImage() {
        for (int i = 0; i < _nY; i++)
            for (int j = 0; j < _nX; j++)
                castRay(j, i);
        return this;
    }

    /**
     * Overlays a grid on the rendered image by coloring every pixel whose row
     * or column index is a multiple of {@code interval}.
     *
     * @param interval the spacing between grid lines in pixels
     * @param color    the color of the grid lines
     * @return this camera (for method chaining)
     */
    public Camera printGrid(int interval, Color color) {
        for (int i = 0; i < _nY; i++)
            for (int j = 0; j < _nX; j++)
                if (i % interval == 0 || j % interval == 0)
                    _imageWriter.writePixel(j, i, color);
        return this;
    }

    /**
     * Writes the image buffer to a PNG file.
     *
     * @param name the output file name (without {@code .png} extension)
     * @return this camera (for method chaining)
     */
    public Camera writeToImage(String name) {
        _imageWriter.writeToImage(name);
        return this;
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

        /**
         * The camera being built.
         */
        private final Camera _camera = new Camera();

        /**
         * Explicit forward direction (null if a target point was supplied instead).
         */
        private Vector _vTo = null;

        /**
         * Target point the camera looks at (null if an explicit forward vector was supplied).
         */
        private Point _target = null;

        /**
         * General up vector used to derive the orthonormal frame.
         */
        private Vector _vUp = Vector.AXIS_Y;

        /**
         * Sets the camera location.
         *
         * @param location the position of the camera in 3D space
         * @return this builder
         */
        public Builder setLocation(Point location) {
            _camera._p0 = location;
            return this;
        }

        /**
         * Sets the camera orientation using explicit forward and up vectors.
         *
         * @param to the forward direction vector
         * @param up the general up direction vector
         * @return this builder
         */
        public Builder setDirection(Vector to, Vector up) {
            _vTo = to;
            _vUp = up;
            _target = null;
            return this;
        }

        /**
         * Sets the camera orientation using a target point and an explicit up vector.
         *
         * @param target the point the camera looks at
         * @param up     the general up direction vector
         * @return this builder
         */
        public Builder setDirection(Point target, Vector up) {
            _target = target;
            _vUp = up;
            _vTo = null;
            return this;
        }

        /**
         * Sets the camera orientation using a target point; the up direction
         * defaults to the world Y axis ({@link Vector#AXIS_Y}).
         *
         * @param target the point the camera looks at
         * @return this builder
         */
        public Builder setDirection(Point target) {
            _target = target;
            _vTo = null;
            _vUp = Vector.AXIS_Y;
            return this;
        }

        /**
         * Sets the physical size of the view plane.
         *
         * @param width  the horizontal size of the view plane
         * @param height the vertical size of the view plane
         * @return this builder
         */
        public Builder setVpSize(double width, double height) {
            _camera._width = width;
            _camera._height = height;
            return this;
        }

        /**
         * Sets the distance from the camera to the view plane.
         *
         * @param distance the distance to the view plane
         * @return this builder
         */
        public Builder setVpDistance(double distance) {
            _camera._distance = distance;
            return this;
        }

        /**
         * Sets the pixel resolution of the view plane.
         *
         * @param nX number of pixels horizontally
         * @param nY number of pixels vertically
         * @return this builder
         */
        public Builder setResolution(int nX, int nY) {
            _camera._nX = nX;
            _camera._nY = nY;
            return this;
        }

        /**
         * Sets the ray-tracing strategy for the camera.
         *
         * @param scene the scene to render
         * @param type  the desired ray-tracer type
         * @return this builder
         * @throws IllegalArgumentException if {@code type} is not supported
         */
        public Builder setRayTracer(Scene scene, RayTracerType type) {
            _camera._rayTracer = switch (type) {
                case SIMPLE -> new SimpleRayTracer(scene);
                default -> throw new IllegalArgumentException("Unsupported ray tracer type: " + type);
            };
            return this;
        }

        /**
         * Validates all parameters and returns the fully initialised {@link Camera}.
         * <p>
         * The order of validation calls is mandatory.
         * </p>
         *
         * @return a ready-to-use {@code Camera}
         * @throws IllegalArgumentException if any numeric parameter is non-positive
         *                                  or if the orientation vectors are parallel
         * @throws MissingResourceException if location or direction data are absent
         */
        public Camera build() {
            checkResolution();
            checkLocationAndDirection();
            checkViewPlane();
            if (_camera._rayTracer == null)
                setRayTracer(new Scene("default"), RayTracerType.SIMPLE);
            try {
                return (Camera) _camera.clone();
            } catch (CloneNotSupportedException _) {
                return null;
            }
        }

        /**
         * Validates that resolution values are positive and initialises the image buffer.
         *
         * @throws IllegalArgumentException if {@code nX} or {@code nY} is not positive
         */
        private void checkResolution() {
            if (_camera._nX <= 0)
                throw new IllegalArgumentException("nX must be positive, got: " + _camera._nX);
            if (_camera._nY <= 0)
                throw new IllegalArgumentException("nY must be positive, got: " + _camera._nY);
            _camera._imageWriter = new ImageWriter(_camera._nX, _camera._nY);
        }

        /**
         * Validates location and direction data, computes and normalises the
         * three orientation vectors, and updates the camera.
         *
         * @throws MissingResourceException if location, direction, or up vector is missing
         * @throws IllegalArgumentException if the forward and up vectors are parallel
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

            _camera._vpCenter = _camera._p0.add(_camera._vTo.scale(_camera._distance));
            _camera._pixelWidth = _camera._width / _camera._nX;
            _camera._pixelHeight = _camera._height / _camera._nY;
        }
    }
}
