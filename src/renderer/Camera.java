package renderer;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.IntStream;

import geometries.api.Intersectable;
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

    // ── anti-aliasing ─────────────────────────────────────────────────────────

    /**
     * Number of samples per axis for anti-aliasing super-sampling.
     * Total rays per pixel = {@code _aaNumSamples * _aaNumSamples}.
     * Value of 1 disables anti-aliasing (single centre ray, default behaviour).
     */
    private int _aaNumSamples = 1;

    // ── depth of field ────────────────────────────────────────────────────────

    /**
     * Distance from the camera origin to the focal plane.
     * A value of 0 disables depth-of-field (default behaviour).
     */
    private double _focalLength = 0;

    /**
     * Half-size of the aperture disc used for depth-of-field sampling.
     * Larger values produce stronger blur for out-of-focus objects.
     */
    private double _apertureSize = 0;

    /**
     * Number of samples per axis for aperture super-sampling.
     * Total rays per pixel = {@code _dofNumSamples * _dofNumSamples}.
     */
    private int _dofNumSamples = 1;

    // ── multi-threading ───────────────────────────────────────────────────────

    /**
     * Amount of threads to use for rendering image by the camera.
     * <ul>
     *   <li>-2 – number of threads = logical processors minus {@link #SPARE_THREADS}</li>
     *   <li>-1 – parallel stream (implicit multi-threading)</li>
     *   <li> 0 – no multi-threading (default)</li>
     *   <li>1+ – literal thread count</li>
     * </ul>
     */
    private int _threadsCount = 0;

    /**
     * Amount of threads to spare for Java VM threads.
     * Spare threads if trying to use all the cores.
     */
    private static final int SPARE_THREADS = 2;

    /**
     * Debug print interval in % (for progress percentage).
     * A value of 0 disables progress output.
     */
    private double _printInterval = 0;

    /**
     * Pixel manager for supporting multi-threading and debug print of
     * progress percentage in the console window/tab.
     */
    private PixelManager _pixelManager;

    /**
     * Private default constructor.
     * <p>
     * Instances are created only through {@link Builder}.
     * </p>
     */
    private Camera() {
    }

    @Override
    protected Camera clone() throws CloneNotSupportedException {
        return (Camera) super.clone();
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
     * Constructs a ray through the centre of pixel {@code (xIndex, yIndex)}
     * on the view plane.
     *
     * @param xIndex column index of the pixel (0-based, left to right)
     * @param yIndex row    index of the pixel (0-based, top  to bottom)
     * @return the ray from the camera origin through the pixel centre
     */
    public Ray constructRay(int xIndex, int yIndex) {
        Vector direction = getPixelCenter(xIndex, yIndex).subtract(_p0);
        return new Ray(_p0, direction);
    }

    /**
     * Returns the world-space centre point of pixel {@code (xIndex, yIndex)}
     * on the view plane.
     *
     * @param xIndex column index (0-based)
     * @param yIndex row    index (0-based)
     * @return the 3-D centre of that pixel on the view plane
     */
    private Point getPixelCenter(int xIndex, int yIndex) {
        double xOffset = (xIndex - (_nX - 1) / 2.0) * _pixelWidth;
        double yOffset = -(yIndex - (_nY - 1) / 2.0) * _pixelHeight;

        Point pixelCenter = _vpCenter;
        if (!isZero(xOffset)) pixelCenter = pixelCenter.add(_vRight.scale(xOffset));
        if (!isZero(yOffset)) pixelCenter = pixelCenter.add(_vUp.scale(yOffset));
        return pixelCenter;
    }

    /**
     * Traces all rays for pixel {@code (xIndex, yIndex)} and writes the
     * resulting colour to the image buffer.
     *
     * <p>When a super-sampling improvement is active this method traces a beam
     * of rays and averages their colours.  Otherwise it falls back to a single
     * centre ray, preserving the original single-ray behaviour.</p>
     *
     * @param xIndex the pixel column index (0-based)
     * @param yIndex the pixel row    index (0-based)
     */
    private void castRay(int xIndex, int yIndex) {
        Color color = (_aaNumSamples > 1 || _focalLength > 0)
                ? castBeam(xIndex, yIndex)
                : _rayTracer.traceRay(constructRay(xIndex, yIndex));
        _imageWriter.writePixel(xIndex, yIndex, color);
        _pixelManager.pixelDone();
    }

    /**
     * Builds and traces a beam of rays for the given pixel, then returns the
     * averaged colour.
     *
     * <p>Depth-of-field takes priority when both improvements are configured.</p>
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row    index
     * @return the averaged colour of all rays in the beam
     */
    private Color castBeam(int xIndex, int yIndex) {
        List<Ray> beam = _focalLength > 0
                ? constructDofBeam(xIndex, yIndex)
                : constructAaBeam(xIndex, yIndex);

        Color sum = Color.BLACK;
        for (Ray ray : beam)
            sum = sum.add(_rayTracer.traceRay(ray));
        return sum.reduce(beam.size());
    }

    /**
     * Constructs a beam of rays for anti-aliasing by spreading samples across
     * the pixel area on the view plane.
     *
     * <p>All rays originate from the camera location; only their directions
     * (through different sub-pixel points) differ.</p>
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row    index
     * @return list of rays covering the pixel area
     */
    private List<Ray> constructAaBeam(int xIndex, int yIndex) {
        List<Point> samples = new Blackboard(getPixelCenter(xIndex, yIndex), _vRight, _vUp)
                .setSize(_pixelWidth / 2.0)
                .setNumSamples(_aaNumSamples)
                .generateSamplePoints();

        return samples.stream()
                .map(samplePoint -> new Ray(_p0, samplePoint.subtract(_p0)))
                .toList();
    }

    /**
     * Constructs a beam of rays for depth-of-field by spreading samples across
     * the aperture and directing each ray through the focal point.
     *
     * <p>The focal point lies on the central pixel ray at distance
     * {@code _focalLength}.  Objects at that distance appear sharp; objects
     * nearer or farther appear blurred because their images on the film plane
     * are the average of slightly different angles.</p>
     *
     * @param xIndex pixel column index
     * @param yIndex pixel row    index
     * @return list of rays from aperture samples through the focal point
     */
    private List<Ray> constructDofBeam(int xIndex, int yIndex) {
        Point focalPoint = constructRay(xIndex, yIndex).getPoint(_focalLength);

        List<Point> aperturePoints = new Blackboard(_p0, _vRight, _vUp)
                .setSize(_apertureSize)
                .setNumSamples(_dofNumSamples)
                .generateSamplePoints();

        return aperturePoints.stream()
                .map(aperturePoint -> new Ray(aperturePoint, focalPoint.subtract(aperturePoint)))
                .toList();
    }

    /**
     * Renders the scene by casting rays through every pixel and writing the
     * resulting colours to the image buffer.
     *
     * <p>Initialises the pixel manager and delegates to the appropriate
     * rendering strategy based on {@code _threadsCount}.</p>
     *
     * @return this camera (for method chaining)
     */
    public Camera renderImage() {
        _pixelManager = new PixelManager(_nY, _nX, _printInterval);
        return switch (_threadsCount) {
            case  0 -> renderImageNoThreads();
            case -1 -> renderImageStream();
            default -> renderImageRawThreads();
        };
    }

    /**
     * Renders the image without multi-threading (single main thread).
     *
     * @return this camera (for method chaining)
     */
    private Camera renderImageNoThreads() {
        for (int i = 0; i < _nY; ++i)
            for (int j = 0; j < _nX; ++j)
                castRay(j, i);
        return this;
    }

    /**
     * Renders the image using parallel stream (implicit multi-threading).
     *
     * @return this camera (for method chaining)
     */
    private Camera renderImageStream() {
        IntStream.range(0, _nY).parallel()
                .forEach(i -> IntStream.range(0, _nX).parallel()
                        .forEach(j -> castRay(j, i)));
        return this;
    }

    /**
     * Renders the image using raw Java threads.
     *
     * @return this camera (for method chaining)
     */
    private Camera renderImageRawThreads() {
        var threads = new LinkedList<Thread>();
        while (_threadsCount-- > 0)
            threads.add(new Thread(() -> {
                PixelManager.Pixel pixel;
                while ((pixel = _pixelManager.nextPixel()) != null)
                    castRay(pixel.col(), pixel.row());
            }));
        for (var thread : threads) thread.start();
        try { for (var thread : threads) thread.join(); }
        catch (InterruptedException ignored) {}
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
        for (int row = 0; row < _nY; row++)
            for (int col = 0; col < _nX; col++)
                if (row % interval == 0 || col % interval == 0)
                    _imageWriter.writePixel(col, row, color);
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
         * The scene that was passed to {@link #setRayTracer}, kept so that
         * {@link #enableBVH()} can rebuild its geometry tree before rendering.
         */
        private Scene _scene = null;

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
         * Enables anti-aliasing super-sampling.
         *
         * <p>Each pixel is sampled on an {@code n × n} grid; the resulting
         * colours are averaged.  Pass {@code 1} to disable (default).</p>
         *
         * @param n number of samples per axis (total = n²); must be ≥ 1
         * @return this builder
         */
        public Builder setAntiAliasing(int n) {
            _camera._aaNumSamples = n;
            return this;
        }

        /**
         * Enables depth-of-field by specifying the focal distance and aperture.
         *
         * <p>Objects at {@code focalLength} from the camera appear sharp; objects
         * at other distances appear blurred.  Pass {@code focalLength = 0} to
         * disable (default).</p>
         *
         * @param focalLength  distance from the camera to the focal plane; 0 = disabled
         * @param apertureSize half-size of the aperture disc; controls blur strength
         * @param n            number of aperture samples per axis (total = n²)
         * @return this builder
         */
        public Builder setDepthOfField(double focalLength, double apertureSize, int n) {
            _camera._focalLength   = focalLength;
            _camera._apertureSize  = apertureSize;
            _camera._dofNumSamples = n;
            return this;
        }

        /**
         * Sets the multi-threading mode.
         * <ul>
         *   <li>-2 – auto: logical processors minus {@code SPARE_THREADS}</li>
         *   <li>-1 – parallel stream</li>
         *   <li> 0 – no multi-threading (default)</li>
         *   <li>1+ – literal thread count</li>
         * </ul>
         *
         * @param  threads number of threads (−2 to Integer.MAX_VALUE)
         * @return         this builder
         * @throws IllegalArgumentException if {@code threads} is less than −2
         */
        public Builder setMultithreading(int threads) {
            if (threads < -3)
                throw new IllegalArgumentException("Multithreading parameter must be -2 or higher");
            if (threads == -2) {
                int cores = Runtime.getRuntime().availableProcessors() - SPARE_THREADS;
                _camera._threadsCount = cores <= 2 ? 1 : cores;
            } else
                _camera._threadsCount = threads;
            return this;
        }

        /**
         * Sets the debug-print interval for progress output.
         * A value of 0 disables all output.
         *
         * @param  interval printing interval in %; must be ≥ 0
         * @return          this builder
         * @throws IllegalArgumentException if {@code interval} is negative
         */
        public Builder setDebugPrint(double interval) {
            if (interval < 0)
                throw new IllegalArgumentException("interval parameter must be non-negative");
            _camera._printInterval = interval;
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
            _scene = scene;
            if (type != RayTracerType.SIMPLE)
                throw new IllegalArgumentException("Unsupported ray tracer type: " + type);
            _camera._rayTracer = new SimpleRayTracer(scene);
            return this;
        }

        /**
         * Enables Conservative Bounding Region (CBR) acceleration.
         * <p>
         * All ray–geometry tests will first check the AABB before
         * performing the full intersection computation.
         * </p>
         *
         * @return this builder
         */
        public Builder enableCBR() {
            Intersectable.setCBR(true);
            return this;
        }

        /**
         * Enables BVH acceleration by flattening the scene's geometry tree and
         * rebuilding it as an optimised Bounding Volume Hierarchy, then enables CBR.
         *
         * @return this builder
         */
        public Builder enableBVH() {
            if (_scene != null)
                _scene.setGeometries(_scene.geometries.flatten().buildBVH());
            Intersectable.setCBR(true);
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
