package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import org.junit.jupiter.api.Test;
import primitives.*;
import scene.Scene;

/**
 * Demonstration tests for Mini-Project 1: super-sampling improvements.
 *
 * <p>Each improvement is tested with two renders of the same scene:
 * <ol>
 *   <li>Improvement disabled  – one ray per pixel (baseline)</li>
 *   <li>Improvement enabled   – super-sampled beam, colours averaged</li>
 * </ol>
 *
 * <p>Render times are printed so the performance trade-off is visible.
 */
class MP1Tests {

   // ═══════════════════════════════════════════════════════════════════════
   // Anti-Aliasing
   // ═══════════════════════════════════════════════════════════════════════

   /**
    * Builds the anti-aliasing demonstration scene.
    *
    * <p>The scene contains 12 geometries placed so that many object edges cut
    * diagonally across pixel boundaries, making aliasing (jagged edges) clearly
    * visible on the baseline render and demonstrably smoother with AA enabled.
    *
    * <ul>
    *   <li>1 ground plane
    *   <li>4 large spheres in a row (diagonal silhouettes)
    *   <li>2 small mirror spheres
    *   <li>3 triangles forming a geometric shape
    *   <li>1 cylinder
    *   <li>1 back-wall plane
    * </ul>
    *
    * <p>Lights: 1 ambient + 1 directional + 2 point lights.
    */
   private Scene buildAaScene() {
      Scene scene = new Scene("AA Demo");
      scene.setAmbientLight(new AmbientLight(new Color(15, 15, 15)));
      scene.setBackground(new Color(10, 10, 30));

      // ── ground plane ────────────────────────────────────────────────────
      scene.geometries.add(
         new Plane(new Point(0, -80, 0), new Vector(0, 1, 0))
            .setEmission(new Color(25, 20, 15))
            .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(10).setKR(0.1))
      );

      // ── back wall ───────────────────────────────────────────────────────
      scene.geometries.add(
         new Plane(new Point(0, 0, -400), new Vector(0, 0, 1))
            .setEmission(new Color(20, 20, 40))
            .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(5))
      );

      // ── four coloured spheres in a row ──────────────────────────────────
      scene.geometries.add(
         new Sphere(new Point(-150, 0, -200), 55)
            .setEmission(new Color(180, 30, 30))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(80)),
         new Sphere(new Point(-50, 0, -200), 55)
            .setEmission(new Color(30, 150, 50))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(80)),
         new Sphere(new Point(50, 0, -200), 55)
            .setEmission(new Color(30, 80, 200))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(80)),
         new Sphere(new Point(150, 0, -200), 55)
            .setEmission(new Color(200, 150, 0))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(80))
      );

      // ── two small mirror spheres ────────────────────────────────────────
      scene.geometries.add(
         new Sphere(new Point(-90, -40, -120), 25)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.7)),
         new Sphere(new Point(90, -40, -120), 25)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.7))
      );

      // ── three triangles ─────────────────────────────────────────────────
      scene.geometries.add(
         new Triangle(new Point(-30, 60, -180), new Point(30, 60, -180), new Point(0, 120, -180))
            .setEmission(new Color(100, 0, 150))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(-200, -80, -300), new Point(-120, -80, -300), new Point(-160, 20, -300))
            .setEmission(new Color(0, 120, 130))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(120, -80, -300), new Point(200, -80, -300), new Point(160, 20, -300))
            .setEmission(new Color(160, 90, 0))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40))
      );

      // ── cylinder ────────────────────────────────────────────────────────
      scene.geometries.add(
         new Cylinder(18, new Ray(new Point(0, -80, -160), new Vector(0, 1, 0)), 120)
            .setEmission(new Color(60, 60, 60))
            .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100))
      );

      // ── lights ──────────────────────────────────────────────────────────
      scene.lights.add(
         new DirectionalLight(new Color(60, 60, 80), new Vector(1, -1, -1))
      );
      scene.lights.add(
         new PointLight(new Color(400, 300, 300), new Point(-200, 200, 100))
            .setKl(0.0002).setKq(0.000002)
      );
      scene.lights.add(
         new SpotLight(new Color(500, 400, 200), new Point(200, 200, 100), new Vector(-1, -1, -1))
            .setKl(0.0001).setKq(0.000001)
      );

      return scene;
   }

   /** Renders the AA scene without anti-aliasing (single ray per pixel). */
   @Test
   void antiAliasingOff() {
      long t0 = System.currentTimeMillis();
      Camera.getBuilder()
         .setLocation(new Point(0, 0, 400))
         .setDirection(new Point(0, 0, -200), Vector.AXIS_Y)
         .setVpDistance(400)
         .setVpSize(300, 300)
         .setResolution(800, 800)
         .setRayTracer(buildAaScene(), RayTracerType.SIMPLE)
         // numSamples = 1  →  anti-aliasing disabled
         .setAntiAliasing(1)
         .build()
         .renderImage()
         .writeToImage("aa_off");
      System.out.printf("AA off  – render time: %d ms%n", System.currentTimeMillis() - t0);
   }

   /** Renders the AA scene with anti-aliasing enabled (9×9 = 81 rays per pixel). */
   @Test
   void antiAliasingOn() {
      long t0 = System.currentTimeMillis();
      Camera.getBuilder()
         .setLocation(new Point(0, 0, 400))
         .setDirection(new Point(0, 0, -200), Vector.AXIS_Y)
         .setVpDistance(400)
         .setVpSize(300, 300)
         .setResolution(800, 800)
         .setRayTracer(buildAaScene(), RayTracerType.SIMPLE)
         // 9×9 grid  →  81 rays per pixel
         .setAntiAliasing(9)
         .setMultiThreading(4, 1.0)
         .build()
         .renderImage()
         .writeToImage("aa_on");
      System.out.printf("AA on   – render time: %d ms%n", System.currentTimeMillis() - t0);
   }

   // ═══════════════════════════════════════════════════════════════════════
   // Depth of Field
   // ═══════════════════════════════════════════════════════════════════════

   /**
    * Builds the depth-of-field demonstration scene.
    *
    * <p>Objects are arranged in depth along the Z-axis so that only the ones
    * near the focal plane look sharp while the rest appear blurred.
    *
    * <ul>
    *   <li>1 ground plane
    *   <li>5 spheres at different Z depths
    *   <li>2 reflective spheres (one in focus, one out)
    *   <li>3 triangles at varying depths
    *   <li>1 cylinder at the focal plane
    *   <li>1 back-wall plane
    * </ul>
    *
    * <p>The camera focal length is set to 500, so objects near Z = −100 are
    * sharp and objects at Z = −600 are noticeably blurred.
    */
   private Scene buildDofScene() {
      Scene scene = new Scene("DOF Demo");
      scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));
      scene.setBackground(new Color(5, 5, 20));

      // ── ground plane ────────────────────────────────────────────────────
      scene.geometries.add(
         new Plane(new Point(0, -90, 0), new Vector(0, 1, 0))
            .setEmission(new Color(30, 25, 20))
            .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(5).setKR(0.05))
      );

      // ── back wall ───────────────────────────────────────────────────────
      scene.geometries.add(
         new Plane(new Point(0, 0, -700), new Vector(0, 0, 1))
            .setEmission(new Color(15, 15, 35))
            .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(5))
      );

      // ── spheres at increasing depths (camera at z=400, focal at z=-100) ─
      // very close  – blurred
      scene.geometries.add(
         new Sphere(new Point(-60, 0, 200), 40)
            .setEmission(new Color(200, 50, 50))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
      );
      // close – slightly blurred
      scene.geometries.add(
         new Sphere(new Point(60, 10, 50), 40)
            .setEmission(new Color(200, 140, 20))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
      );
      // IN FOCUS (focal distance ≈ 500 → world z ≈ -100)
      scene.geometries.add(
         new Sphere(new Point(0, 5, -100), 45)
            .setEmission(new Color(30, 200, 80))
            .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100))
      );
      // far – slightly blurred
      scene.geometries.add(
         new Sphere(new Point(-80, 0, -300), 40)
            .setEmission(new Color(40, 100, 220))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
      );
      // very far – blurred
      scene.geometries.add(
         new Sphere(new Point(80, 0, -550), 40)
            .setEmission(new Color(160, 50, 200))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60))
      );

      // ── reflective spheres ───────────────────────────────────────────────
      // in-focus reflective
      scene.geometries.add(
         new Sphere(new Point(-130, -20, -100), 30)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.8))
      );
      // out-of-focus reflective
      scene.geometries.add(
         new Sphere(new Point(130, -20, -500), 30)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.8))
      );

      // ── triangles at varying depths ──────────────────────────────────────
      scene.geometries.add(
         new Triangle(new Point(-60, 60, -100), new Point(0, 60, -100), new Point(-30, 110, -100))
            .setEmission(new Color(180, 120, 0))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(40, 50, 100), new Point(100, 50, 100), new Point(70, 100, 100))
            .setEmission(new Color(0, 140, 160))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(-40, 50, -450), new Point(40, 50, -450), new Point(0, 110, -450))
            .setEmission(new Color(200, 60, 60))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40))
      );

      // ── cylinder at focal plane ──────────────────────────────────────────
      scene.geometries.add(
         new Cylinder(20, new Ray(new Point(100, -90, -100), new Vector(0, 1, 0)), 110)
            .setEmission(new Color(80, 80, 80))
            .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100))
      );

      // ── lights ──────────────────────────────────────────────────────────
      scene.lights.add(
         new DirectionalLight(new Color(40, 40, 60), new Vector(0, -1, -1))
      );
      scene.lights.add(
         new PointLight(new Color(500, 400, 300), new Point(-200, 200, 200))
            .setKl(0.0001).setKq(0.000001)
      );
      scene.lights.add(
         new SpotLight(new Color(600, 500, 300), new Point(200, 300, 100), new Vector(-1, -2, -2))
            .setKl(0.00008).setKq(0.0000008)
      );

      return scene;
   }

   /**
    * Renders the DOF scene without depth-of-field (all objects equally sharp).
    * Camera is at z=400, looking toward z=-100.
    */
   @Test
   void depthOfFieldOff() {
      long t0 = System.currentTimeMillis();
      Camera.getBuilder()
         .setLocation(new Point(0, 0, 400))
         .setDirection(new Point(0, 0, -100), Vector.AXIS_Y)
         .setVpDistance(400)
         .setVpSize(300, 300)
         .setResolution(800, 800)
         .setRayTracer(buildDofScene(), RayTracerType.SIMPLE)
         // focalLength = 0  →  depth-of-field disabled
         .build()
         .renderImage()
         .writeToImage("dof_off");
      System.out.printf("DOF off – render time: %d ms%n", System.currentTimeMillis() - t0);
   }

   /**
    * Renders the DOF scene with depth-of-field enabled.
    *
    * <p>Camera is at z=400; focal length = 500, so the focal plane sits at
    * z = 400−500 = −100.  Objects centred near z=−100 appear sharp; those
    * at z=200 or z=−550 are blurred proportional to their defocus distance.
    *
    * <p>Aperture half-size = 6, samples = 9×9 = 81 rays per pixel.
    */
   @Test
   void depthOfFieldOn() {
      long t0 = System.currentTimeMillis();
      Camera.getBuilder()
         .setLocation(new Point(0, 0, 400))
         .setDirection(new Point(0, 0, -100), Vector.AXIS_Y)
         .setVpDistance(400)
         .setVpSize(300, 300)
         .setResolution(800, 800)
         .setRayTracer(buildDofScene(), RayTracerType.SIMPLE)
         // focal length 500 → focal plane at z = 400-500 = -100
         // aperture half-size 6, 9×9 = 81 aperture samples
         .setDepthOfField(500, 6, 9)
         .setMultiThreading(4, 1.0)
         .build()
         .renderImage()
         .writeToImage("dof_on");
      System.out.printf("DOF on  – render time: %d ms%n", System.currentTimeMillis() - t0);
   }
}
