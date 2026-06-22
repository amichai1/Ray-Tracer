package renderer;

import geometries.api.Geometry;
import geometries.api.Intersectable;
import geometries.impl.Cylinder;
import geometries.impl.Geometries;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.Color;
import primitives.Material;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;
import scene.Scene;

/**
 * Performance benchmark tests for Mini-Project 2.
 * <p>
 * Measures render times across 12 configurations:
 * 6 acceleration setups (no-accel/CBR × flat/manual-BVH/auto-BVH) × 2 threading modes.
 * </p>
 * <p>
 * All tests render the same scene (500+ geometries, 5 lights, reflections, transparency).
 * </p>
 */
class MP2Tests {

    private static final int NX = 600;
    private static final int NY = 600;
    private static final int GRID_COLS = 20;
    private static final int GRID_ROWS = 25;
    private static final double SPHERE_RADIUS = 8;
    private static final double SPHERE_SPACING = 20;

    // ─────────────────────────────────────────────────────────────────────────
    // Scene builders
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Adds 500 spheres arranged in a GRID_COLS × GRID_ROWS grid to the given
     * {@code Geometries} container.
     *
     * @param container the geometry container to populate
     */
    private static void addSphereGrid(Geometries container) {
        double startX = -(GRID_COLS - 1) * SPHERE_SPACING / 2.0;
        double startZ = -(GRID_ROWS - 1) * SPHERE_SPACING / 2.0;

        Color[] palette = {
                new Color(180, 30, 30), new Color(30, 160, 30), new Color(30, 30, 180),
                new Color(160, 130, 0), new Color(120, 0, 140), new Color(0, 130, 150)
        };

        for (int col = 0; col < GRID_COLS; col++) {
            double x = startX + col * SPHERE_SPACING;
            for (int row = 0; row < GRID_ROWS; row++) {
                double z = startZ + row * SPHERE_SPACING;
                Color emission = palette[(col + row) % palette.length].scale(0.35);
                Material mat = (col + row) % 7 == 0
                        ? new Material().setKD(0.1).setKS(0.8).setShininess(150).setKR(0.6)
                        : new Material().setKD(0.5).setKS(0.4).setShininess(80);
                container.add(new Sphere(new Point(x, 0, z), SPHERE_RADIUS)
                        .setEmission(emission).setMaterial(mat));
            }
        }
    }

    /**
     * Adds scene fixtures (ground plane, back wall, accent spheres, cylinders,
     * triangles) to the given scene.  Lights are also added.
     *
     * @param scene the scene to configure
     */
    private static void configureSceneFixtures(Scene scene) {
        scene.setAmbientLight(new AmbientLight(new Color(10, 10, 12)));
        scene.setBackground(new Color(5, 8, 20));

        // ground plane
        scene.geometries.add(
                new Plane(new Point(0, -SPHERE_RADIUS, 0), new Vector(0, 1, 0))
                        .setEmission(new Color(8, 8, 10))
                        .setMaterial(new Material().setKD(0.6).setKS(0.1).setShininess(5).setKR(0.08))
        );

        // back wall
        scene.geometries.add(
                new Plane(new Point(0, 0, -400), new Vector(0, 0, 1))
                        .setEmission(new Color(6, 6, 18))
                        .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(5))
        );

        // large center mirror sphere
        scene.geometries.add(
                new Sphere(new Point(0, 30, 0), 25)
                        .setEmission(new Color(5, 5, 5))
                        .setMaterial(new Material().setKD(0.05).setKS(0.9).setShininess(300).setKR(0.85))
        );

        // glass sphere (transparent)
        scene.geometries.add(
                new Sphere(new Point(-60, 20, 60), 18)
                        .setEmission(new Color(2, 2, 10))
                        .setMaterial(new Material().setKD(0.05).setKS(0.5).setShininess(200).setKT(0.85))
        );

        // two cylinders
        scene.geometries.add(
                new Cylinder(10, new Ray(new Point(90, -8, -80), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100)),
                new Cylinder(10, new Ray(new Point(-90, -8, -80), new Vector(0, 1, 0)), 70)
                        .setEmission(new Color(20, 20, 20))
                        .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100))
        );

        // three triangles
        scene.geometries.add(
                new Triangle(new Point(-50, 50, -200), new Point(50, 50, -200), new Point(0, 110, -200))
                        .setEmission(new Color(60, 0, 90))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(50)),
                new Triangle(new Point(-200, -8, -150), new Point(-120, -8, -150), new Point(-160, 60, -150))
                        .setEmission(new Color(0, 70, 60))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(50)),
                new Triangle(new Point(120, -8, -150), new Point(200, -8, -150), new Point(160, 60, -150))
                        .setEmission(new Color(80, 50, 0))
                        .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(50))
        );

        // 5 light sources
        scene.lights.add(new DirectionalLight(new Color(20, 20, 30), new Vector(1, -1, -1)));
        scene.lights.add(new DirectionalLight(new Color(15, 15, 25), new Vector(-1, -1, -1)));
        scene.lights.add(
                new PointLight(new Color(200, 160, 80), new Point(-200, 250, 150))
                        .setKl(0.0001).setKq(0.000001));
        scene.lights.add(
                new PointLight(new Color(80, 160, 200), new Point(200, 250, 150))
                        .setKl(0.0001).setKq(0.000001));
        scene.lights.add(
                new SpotLight(new Color(220, 200, 120), new Point(0, 300, 200), new Vector(0, -1, -1))
                        .setKl(0.00005).setKq(0.0000005));
    }

    /**
     * Builds the flat demo scene (all geometries in one level).
     *
     * @return the configured scene
     */
    private static Scene buildFlatScene() {
        Scene scene = new Scene("MP2 Flat");
        addSphereGrid(scene.geometries);
        configureSceneFixtures(scene);
        return scene;
    }

    /**
     * Builds the demo scene with a 2-group manual BVH hierarchy.
     * Spheres are split into left (x ≤ 0) and right (x > 0) groups.
     *
     * @return the configured scene
     */
    private static Scene buildManualBVHScene() {
        Scene scene = new Scene("MP2 Manual BVH");

        Geometries leftGroup = new Geometries();
        Geometries rightGroup = new Geometries();

        double startX = -(GRID_COLS - 1) * SPHERE_SPACING / 2.0;
        double startZ = -(GRID_ROWS - 1) * SPHERE_SPACING / 2.0;

        Color[] palette = {
                new Color(180, 30, 30), new Color(30, 160, 30), new Color(30, 30, 180),
                new Color(160, 130, 0), new Color(120, 0, 140), new Color(0, 130, 150)
        };

        for (int col = 0; col < GRID_COLS; col++) {
            double x = startX + col * SPHERE_SPACING;
            for (int row = 0; row < GRID_ROWS; row++) {
                double z = startZ + row * SPHERE_SPACING;
                Color emission = palette[(col + row) % palette.length].scale(0.35);
                Material mat = (col + row) % 7 == 0
                        ? new Material().setKD(0.1).setKS(0.8).setShininess(150).setKR(0.6)
                        : new Material().setKD(0.5).setKS(0.4).setShininess(80);
                Geometry s = new Sphere(new Point(x, 0, z), SPHERE_RADIUS)
                        .setEmission(emission).setMaterial(mat);
                if (x <= 0) leftGroup.add(s);
                else rightGroup.add(s);
            }
        }

        scene.geometries.add(leftGroup, rightGroup);
        configureSceneFixtures(scene);
        return scene;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Render helper
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Renders the scene, prints the elapsed time, and resets CBR to off.
     *
     * @param scene      the scene to render
     * @param imageName  output file name (without extension)
     * @param useThreads {@code true} to enable multi-threading (auto mode)
     */
    private void doRender(Scene scene, String imageName, boolean useThreads) {
        long start = System.currentTimeMillis();
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(new Point(0, 80, 500))
                .setDirection(new Point(0, 0, 0), Vector.AXIS_Y)
                .setVpDistance(500)
                .setVpSize(400, 400)
                .setResolution(NX, NY)
                .setRayTracer(scene, RayTracerType.SIMPLE);

        if (useThreads)
            builder.setMultithreading(-2).setDebugPrint(5);

        builder.build()
                .renderImage()
                .writeToImage(imageName);

        System.out.printf("%-40s %5d ms%n", imageName + ":", System.currentTimeMillis() - start);
        Intersectable.setCBR(false);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 12 benchmark tests
    // ─────────────────────────────────────────────────────────────────────────

    // ── Row 1: No accel, flat scene ───────────────────────────────────────

    /**
     * No acceleration, flat hierarchy, no multi-threading.
     */
    @Test
    void mp2_01_noAccel_flat_noMT() {
        doRender(buildFlatScene(), "mp2_01_noAccel_flat_noMT", false);
    }

    /**
     * No acceleration, flat hierarchy, with multi-threading.
     */
    @Test
    void mp2_02_noAccel_flat_MT() {
        doRender(buildFlatScene(), "mp2_02_noAccel_flat_MT", true);
    }

    // ── Row 2: No accel, manual BVH hierarchy ─────────────────────────────

    /**
     * No acceleration, manual BVH hierarchy, no multi-threading.
     */
    @Test
    void mp2_03_noAccel_manual_noMT() {
        doRender(buildManualBVHScene(), "mp2_03_noAccel_manual_noMT", false);
    }

    /**
     * No acceleration, manual BVH hierarchy, with multi-threading.
     */
    @Test
    void mp2_04_noAccel_manual_MT() {
        doRender(buildManualBVHScene(), "mp2_04_noAccel_manual_MT", true);
    }

    // ── Row 3: No accel, auto BVH hierarchy ───────────────────────────────

    /**
     * No acceleration, auto BVH hierarchy, no multi-threading.
     */
    @Test
    void mp2_05_noAccel_auto_noMT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        doRender(scene, "mp2_05_noAccel_auto_noMT", false);
    }

    /**
     * No acceleration, auto BVH hierarchy, with multi-threading.
     */
    @Test
    void mp2_06_noAccel_auto_MT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        doRender(scene, "mp2_06_noAccel_auto_MT", true);
    }

    // ── Row 4: CBR, flat scene ────────────────────────────────────────────

    /**
     * CBR enabled, flat hierarchy, no multi-threading.
     */
    @Test
    void mp2_07_cbr_flat_noMT() {
        Intersectable.setCBR(true);
        doRender(buildFlatScene(), "mp2_07_cbr_flat_noMT", false);
    }

    /**
     * CBR enabled, flat hierarchy, with multi-threading.
     */
    @Test
    void mp2_08_cbr_flat_MT() {
        Intersectable.setCBR(true);
        doRender(buildFlatScene(), "mp2_08_cbr_flat_MT", true);
    }

    // ── Row 5: CBR + manual BVH ───────────────────────────────────────────

    /**
     * CBR enabled, manual BVH hierarchy, no multi-threading.
     */
    @Test
    void mp2_09_cbr_manual_noMT() {
        Intersectable.setCBR(true);
        doRender(buildManualBVHScene(), "mp2_09_cbr_manual_noMT", false);
    }

    /**
     * CBR enabled, manual BVH hierarchy, with multi-threading.
     */
    @Test
    void mp2_10_cbr_manual_MT() {
        Intersectable.setCBR(true);
        doRender(buildManualBVHScene(), "mp2_10_cbr_manual_MT", true);
    }

    // ── Row 6: CBR + auto BVH ────────────────────────────────────────────

    /**
     * CBR enabled, auto BVH hierarchy, no multi-threading.
     */
    @Test
    void mp2_11_cbr_auto_noMT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        Intersectable.setCBR(true);
        doRender(scene, "mp2_11_cbr_auto_noMT", false);
    }

    /**
     * CBR enabled, auto BVH hierarchy, with multi-threading.
     */
    @Test
    void mp2_12_cbr_auto_MT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        Intersectable.setCBR(true);
        doRender(scene, "mp2_12_cbr_auto_MT", true);
    }
}
