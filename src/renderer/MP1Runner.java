package renderer;

import geometries.impl.Cylinder;
import geometries.impl.Plane;
import geometries.impl.Sphere;
import geometries.impl.Triangle;
import lighting.AmbientLight;
import lighting.DirectionalLight;
import lighting.PointLight;
import lighting.SpotLight;
import primitives.*;
import scene.Scene;

/**
 * Standalone runner that produces the four MP1 demo images without JUnit.
 * Run: java -cp out renderer.MP1Runner
 */
public class MP1Runner {

   private static Scene buildAaScene() {
      Scene scene = new Scene("AA Demo");
      scene.setAmbientLight(new AmbientLight(new Color(15, 15, 15)));
      scene.setBackground(new Color(10, 10, 30));
      scene.geometries.add(
         new Plane(new Point(0, -80, 0), new Vector(0, 1, 0))
            .setEmission(new Color(25, 20, 15))
            .setMaterial(new Material().setKD(0.6).setKS(0.2).setShininess(10).setKR(0.1)),
         new Plane(new Point(0, 0, -400), new Vector(0, 0, 1))
            .setEmission(new Color(20, 20, 40))
            .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(5)),
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
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(80)),
         new Sphere(new Point(-90, -40, -120), 25)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.7)),
         new Sphere(new Point(90, -40, -120), 25)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.7)),
         new Triangle(new Point(-30, 60, -180), new Point(30, 60, -180), new Point(0, 120, -180))
            .setEmission(new Color(100, 0, 150))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(-200, -80, -300), new Point(-120, -80, -300), new Point(-160, 20, -300))
            .setEmission(new Color(0, 120, 130))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(120, -80, -300), new Point(200, -80, -300), new Point(160, 20, -300))
            .setEmission(new Color(160, 90, 0))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Cylinder(18, new Ray(new Point(0, -80, -160), new Vector(0, 1, 0)), 120)
            .setEmission(new Color(60, 60, 60))
            .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100))
      );
      scene.lights.add(new DirectionalLight(new Color(60, 60, 80), new Vector(1, -1, -1)));
      scene.lights.add(new PointLight(new Color(400, 300, 300), new Point(-200, 200, 100)).setKl(0.0002).setKq(0.000002));
      scene.lights.add(new SpotLight(new Color(500, 400, 200), new Point(200, 200, 100), new Vector(-1, -1, -1)).setKl(0.0001).setKq(0.000001));
      return scene;
   }

   private static Scene buildDofScene() {
      Scene scene = new Scene("DOF Demo");
      scene.setAmbientLight(new AmbientLight(new Color(15, 15, 20)));
      scene.setBackground(new Color(5, 5, 20));
      scene.geometries.add(
         new Plane(new Point(0, -90, 0), new Vector(0, 1, 0))
            .setEmission(new Color(30, 25, 20))
            .setMaterial(new Material().setKD(0.7).setKS(0.1).setShininess(5).setKR(0.05)),
         new Plane(new Point(0, 0, -700), new Vector(0, 0, 1))
            .setEmission(new Color(15, 15, 35))
            .setMaterial(new Material().setKD(0.5).setKS(0.1).setShininess(5)),
         new Sphere(new Point(-60, 0, 200), 40)
            .setEmission(new Color(200, 50, 50))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60)),
         new Sphere(new Point(60, 10, 50), 40)
            .setEmission(new Color(200, 140, 20))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60)),
         new Sphere(new Point(0, 5, -100), 45)
            .setEmission(new Color(30, 200, 80))
            .setMaterial(new Material().setKD(0.5).setKS(0.5).setShininess(100)),
         new Sphere(new Point(-80, 0, -300), 40)
            .setEmission(new Color(40, 100, 220))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60)),
         new Sphere(new Point(80, 0, -550), 40)
            .setEmission(new Color(160, 50, 200))
            .setMaterial(new Material().setKD(0.5).setKS(0.4).setShininess(60)),
         new Sphere(new Point(-130, -20, -100), 30)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.8)),
         new Sphere(new Point(130, -20, -500), 30)
            .setEmission(new Color(5, 5, 5))
            .setMaterial(new Material().setKD(0.1).setKS(0.8).setShininess(200).setKR(0.8)),
         new Triangle(new Point(-60, 60, -100), new Point(0, 60, -100), new Point(-30, 110, -100))
            .setEmission(new Color(180, 120, 0))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(40, 50, 100), new Point(100, 50, 100), new Point(70, 100, 100))
            .setEmission(new Color(0, 140, 160))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Triangle(new Point(-40, 50, -450), new Point(40, 50, -450), new Point(0, 110, -450))
            .setEmission(new Color(200, 60, 60))
            .setMaterial(new Material().setKD(0.6).setKS(0.3).setShininess(40)),
         new Cylinder(20, new Ray(new Point(100, -90, -100), new Vector(0, 1, 0)), 110)
            .setEmission(new Color(80, 80, 80))
            .setMaterial(new Material().setKD(0.4).setKS(0.5).setShininess(100))
      );
      scene.lights.add(new DirectionalLight(new Color(40, 40, 60), new Vector(0, -1, -1)));
      scene.lights.add(new PointLight(new Color(500, 400, 300), new Point(-200, 200, 200)).setKl(0.0001).setKq(0.000001));
      scene.lights.add(new SpotLight(new Color(600, 500, 300), new Point(200, 300, 100), new Vector(-1, -2, -2)).setKl(0.00008).setKq(0.0000008));
      return scene;
   }

   public static void main(String[] args) {
      System.out.println("=== MP1 Render Runner ===");

      render("aa_off",  buildAaScene(),  1,   0, 0, 1, false);
      render("aa_on",   buildAaScene(),  9,   0, 0, 9, true);
      render("dof_off", buildDofScene(), 1,   0, 0, 1, false);
      render("dof_on",  buildDofScene(), 1, 500, 6, 9, true);

      System.out.println("=== Done. Images saved to ./images/ ===");
   }

   private static void render(String name, Scene scene,
                              int aaSamples,
                              double focalLength, double aperture, int dofSamples,
                              boolean threading) {
      System.out.printf("Rendering %-10s ...", name);
      long t0 = System.currentTimeMillis();

      var builder = Camera.getBuilder()
         .setLocation(name.startsWith("dof")
            ? new Point(0, 0, 400)
            : new Point(0, 0, 400))
         .setDirection(name.startsWith("dof")
            ? new Point(0, 0, -100)
            : new Point(0, 0, -200), Vector.AXIS_Y)
         .setVpDistance(400)
         .setVpSize(300, 300)
         .setResolution(800, 800)
         .setRayTracer(scene, RayTracerType.SIMPLE)
         .setAntiAliasing(aaSamples)
         .setDepthOfField(focalLength, aperture, dofSamples);

      if (threading)
         builder.setMultiThreading(4, 1.0);

      builder.build().renderImage().writeToImage(name);
      System.out.printf(" %d ms%n", System.currentTimeMillis() - t0);
   }
}
