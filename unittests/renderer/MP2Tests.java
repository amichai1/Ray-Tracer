package renderer;

import geometries.api.Geometry;
import geometries.api.Intersectable;
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
import primitives.Vector;
import scene.Scene;

/**
 * Performance benchmark tests for Mini-Project 2.
 * <p>
 * Measures render times across 12 configurations:
 * 6 acceleration setups (no-accel/CBR/flat/manual-BVH/auto-BVH) x 2 threading modes.
 * </p>
 * <p>
 * All tests render the same scene (thousands of geometries, 5 lights, depth of field enabled).
 * </p>
 *
 * @author Gemini
 */
class MP2Tests {

    private static final int NX = 600;
    private static final int NY = 600;

    // ==========================================================
    // SHARED SCENE BUILDER (Identical to MP1, supports split BVH)
    // ==========================================================

    /**
     * Adds scene fixtures (ground plane, sea, umbrellas, mat, lounger, lights) to the given scene.
     * Geometries are automatically distributed between the left and right groups for manual BVH testing.
     *
     * @param scene      the scene to configure
     * @param leftGroup  the geometry container for the left side
     * @param rightGroup the geometry container for the right side
     */
    private static void buildSharedSunsetBeach(Scene scene, Geometries leftGroup, Geometries rightGroup) {
        scene.setBackground(new Color(255, 100, 50));
        scene.setAmbientLight(new AmbientLight(new Color(20, 15, 15)));

        Material sandMat = new Material().setKD(0.7).setKS(0.1).setShininess(5);
        Material waterMat = new Material().setKD(0.1).setKS(0.9).setShininess(200).setKR(0.7);
        Material woodMat = new Material().setKD(0.6).setKS(0.2).setShininess(10);
        Material fabricMat = new Material().setKD(0.8).setKS(0.1).setShininess(5);

        java.util.function.Consumer<Geometry> addGeo = g -> {
            if (leftGroup == rightGroup) {
                leftGroup.add(g);
            } else {
                leftGroup.add(g);
            }
        };

        // --- Ground plane (Sand) ---
        addGeo.accept(new Plane(new Point(0, 0, 0), new Vector(0, 1, 0)).setEmission(new Color(150, 110, 50)).setMaterial(sandMat));

        // --- Sea ---
        addGeo.accept(new Triangle(new Point(-1000, 0.2, -10), new Point(1000, 0.2, -10), new Point(0, 0.2, -2000))
                .setEmission(new Color(10, 20, 60)).setMaterial(waterMat));

        // --- Setting Sun ---
        addGeo.accept(new Sphere(new Point(0, 20, -500), 80).setEmission(new Color(255, 180, 50)));

        // --- Umbrellas with Orange Poles ---
        Color orangePole = new Color(255, 80, 0);
        Point[] umbrellaCenters = {new Point(-20, 0, 25), new Point(20, 0, 25)};

        for (Point center : umbrellaCenters) {
            double cx = center.getX();
            double cz = center.getZ();
            double w = 1.5;

            Point b1 = new Point(cx - w, 0, cz - w); Point b2 = new Point(cx + w, 0, cz - w);
            Point b3 = new Point(cx + w, 0, cz + w); Point b4 = new Point(cx - w, 0, cz + w);
            Point t1 = new Point(cx - w, 22, cz - w); Point t2 = new Point(cx + w, 22, cz - w);
            Point t3 = new Point(cx + w, 22, cz + w); Point t4 = new Point(cx - w, 22, cz + w);

            addGeo.accept(new Triangle(b1, b2, t2).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b1, t2, t1).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b3, b4, t4).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b3, t4, t3).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b2, b3, t3).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b2, t3, t2).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b4, b1, t1).setEmission(orangePole).setMaterial(woodMat));
            addGeo.accept(new Triangle(b4, t1, t4).setEmission(orangePole).setMaterial(woodMat));

            addGeo.accept(new Sphere(new Point(cx, 24, cz), 3).setEmission(orangePole).setMaterial(woodMat));

            Point top = new Point(cx, 22, cz);
            for (int angle = 0; angle < 360; angle += 15) {
                double rad1 = Math.toRadians(angle);
                double rad2 = Math.toRadians(angle + 15);
                Point p1 = new Point(cx + Math.cos(rad1) * 18, 15, cz + Math.sin(rad1) * 18);
                Point p2 = new Point(cx + Math.cos(rad2) * 18, 15, cz + Math.sin(rad2) * 18);
                Color uColor = (angle % 30 == 0) ? new Color(220, 30, 30) : new Color(250, 250, 250);
                addGeo.accept(new Triangle(top, p1, p2).setEmission(uColor).setMaterial(fabricMat));
            }
        }

        // --- Green Mat (1,000 Triangles for BVH testing) ---
        Color greenMatColor = new Color(20, 160, 20);
        double matStartX = -10;
        double matStartZ = 40;
        int cols = 25; int rows = 20;
        double tileW = 20.0 / cols; double tileH = 35.0 / rows;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                double x = matStartX + col * tileW;
                double z = matStartZ - row * tileH;
                Point p1 = new Point(x, 0.3, z);
                Point p2 = new Point(x + tileW, 0.3, z);
                Point p3 = new Point(x + tileW, 0.3, z - tileH);
                Point p4 = new Point(x, 0.3, z - tileH);

                addGeo.accept(new Triangle(p1, p2, p3).setEmission(greenMatColor).setMaterial(fabricMat));
                addGeo.accept(new Triangle(p1, p3, p4).setEmission(greenMatColor).setMaterial(fabricMat));
            }
        }

        // --- Wooden Sun Lounger ---
        Color loungerColor = new Color(220, 220, 220);
        Material loungerMat = new Material().setKD(0.6).setKS(0.2).setShininess(10);

        Point s1 = new Point(12, 1, 35); Point s2 = new Point(18, 1, 35);
        Point s3 = new Point(18, 1, 20); Point s4 = new Point(12, 1, 20);
        addGeo.accept(new Triangle(s1, s2, s3).setEmission(loungerColor).setMaterial(loungerMat));
        addGeo.accept(new Triangle(s1, s3, s4).setEmission(loungerColor).setMaterial(loungerMat));

        Point bl1 = new Point(12, 1, 35); Point bl2 = new Point(18, 1, 35);
        Point bl3 = new Point(18, 6, 40); Point bl4 = new Point(12, 6, 40);
        addGeo.accept(new Triangle(bl1, bl2, bl3).setEmission(loungerColor).setMaterial(loungerMat));
        addGeo.accept(new Triangle(bl1, bl3, bl4).setEmission(loungerColor).setMaterial(loungerMat));

        // --- Lights ---
        scene.lights.add(new DirectionalLight(new Color(150, 100, 50), new Vector(0, -0.2, -1)));
        scene.lights.add(new DirectionalLight(new Color(30, 40, 60), new Vector(0, -1, 0)));
        scene.lights.add(new PointLight(new Color(255, 150, 50), new Point(0, 30, -350)).setKl(0.0001).setKq(0.00001));
        scene.lights.add(new PointLight(new Color(80, 80, 80), new Point(0, 50, 100)).setKl(0.001).setKq(0.0001));
        scene.lights.add(new SpotLight(new Color(100, 100, 100), new Point(-20, 60, 60), new Vector(1, -1, -0.5)).setKl(0.001).setKq(0.0001));
    }

    /**
     * Builds the flat demo scene (all geometries in one level).
     *
     * @return the configured scene
     */
    private static Scene buildFlatScene() {
        Scene scene = new Scene("MP2 Benchmark Flat Final");
        Geometries allGeometries = new Geometries();
        buildSharedSunsetBeach(scene, allGeometries, allGeometries);
        scene.geometries.add(allGeometries);
        return scene;
    }

    /**
     * Builds the demo scene with a 2-group manual BVH hierarchy.
     * Geometries are split into left and right groups.
     *
     * @return the configured scene
     */
    private static Scene buildManualBVHScene() {
        Scene scene = new Scene("MP2 Benchmark Manual BVH Final");
        Geometries leftGroup = new Geometries();
        Geometries rightGroup = new Geometries();
        buildSharedSunsetBeach(scene, leftGroup, rightGroup);
        scene.geometries.add(leftGroup, rightGroup);
        return scene;
    }

    // ==========================================================
    // Render helper
    // ==========================================================

    /**
     * Renders the scene, prints the elapsed time, and resets CBR to off.
     * Note: Depth of Field is forcibly enabled here to stress test the BVH
     * with thousands of rays per pixel.
     *
     * @param scene      the scene to render
     * @param imageName  output file name (without extension)
     * @param useThreads {@code true} to enable multi-threading
     */
    private void doRender(Scene scene, String imageName, boolean useThreads) {
        long start = System.currentTimeMillis();
        Camera.Builder builder = Camera.getBuilder()
                .setLocation(new Point(0, 15, 75))
                .setDirection(new Vector(0, 0, -1), new Vector(0, 1, 0))
                .setVpDistance(50)
                .setVpSize(150, 150)
                .setResolution(NX, NY)
                .setRayTracer(scene, RayTracerType.SIMPLE)
                .setDepthOfField(54, 0.3, 9); // Enforce MP1 functionality!

        if (useThreads)
            builder.setMultithreading(-2).setDebugPrint(5);

        builder.build()
                .renderImage()
                .writeToImage(imageName);

        System.out.printf("%-40s %5d ms%n", imageName + ":", System.currentTimeMillis() - start);
        Intersectable.setCBR(false);
    }

    // ==========================================================
    // 12 benchmark tests
    // ==========================================================

    // --- Row 1: No accel, flat scene ---

    /**
     * No acceleration, flat hierarchy, no multi-threading.
     */
    @Test void mp2_01_noAccel_flat_noMT() { doRender(buildFlatScene(), "mp2_01_noAccel_flat_noMT_final", false); }

    /**
     * No acceleration, flat hierarchy, with multi-threading.
     */
    @Test void mp2_02_noAccel_flat_MT() { doRender(buildFlatScene(), "mp2_02_noAccel_flat_MT_final", true); }

    // --- Row 2: No accel, manual BVH hierarchy ---

    /**
     * No acceleration, manual BVH hierarchy, no multi-threading.
     */
    @Test void mp2_03_noAccel_manual_noMT() { doRender(buildManualBVHScene(), "mp2_03_noAccel_manual_noMT_final", false); }

    /**
     * No acceleration, manual BVH hierarchy, with multi-threading.
     */
    @Test void mp2_04_noAccel_manual_MT() { doRender(buildManualBVHScene(), "mp2_04_noAccel_manual_MT_final", true); }

    // --- Row 3: No accel, auto BVH hierarchy ---

    /**
     * No acceleration, auto BVH hierarchy, no multi-threading.
     */
    @Test void mp2_05_noAccel_auto_noMT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        doRender(scene, "mp2_05_noAccel_auto_noMT_final", false);
    }

    /**
     * No acceleration, auto BVH hierarchy, with multi-threading.
     */
    @Test void mp2_06_noAccel_auto_MT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        doRender(scene, "mp2_06_noAccel_auto_MT_final", true);
    }

    // --- Row 4: CBR, flat scene ---

    /**
     * CBR enabled, flat hierarchy, no multi-threading.
     */
    @Test void mp2_07_cbr_flat_noMT() {
        Intersectable.setCBR(true);
        doRender(buildFlatScene(), "mp2_07_cbr_flat_noMT_final", false);
    }

    /**
     * CBR enabled, flat hierarchy, with multi-threading.
     */
    @Test void mp2_08_cbr_flat_MT() {
        Intersectable.setCBR(true);
        doRender(buildFlatScene(), "mp2_08_cbr_flat_MT_final", true);
    }

    // --- Row 5: CBR + manual BVH ---

    /**
     * CBR enabled, manual BVH hierarchy, no multi-threading.
     */
    @Test void mp2_09_cbr_manual_noMT() {
        Intersectable.setCBR(true);
        doRender(buildManualBVHScene(), "mp2_09_cbr_manual_noMT_final", false);
    }

    /**
     * CBR enabled, manual BVH hierarchy, with multi-threading.
     */
    @Test void mp2_10_cbr_manual_MT() {
        Intersectable.setCBR(true);
        doRender(buildManualBVHScene(), "mp2_10_cbr_manual_MT_final", true);
    }

    // --- Row 6: CBR + auto BVH ---

    /**
     * CBR enabled, auto BVH hierarchy, no multi-threading.
     */
    @Test void mp2_11_cbr_auto_noMT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        Intersectable.setCBR(true);
        doRender(scene, "mp2_11_cbr_auto_noMT_final", false);
    }

    /**
     * CBR enabled, auto BVH hierarchy, with multi-threading.
     */
    @Test void mp2_12_cbr_auto_MT() {
        Scene scene = buildFlatScene();
        scene.setGeometries(scene.geometries.flatten().buildBVH());
        Intersectable.setCBR(true);
        doRender(scene, "mp2_12_cbr_auto_MT_final", true);
    }
}