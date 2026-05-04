package renderer;

import org.junit.jupiter.api.Test;

import primitives.Color;

/**
 * Unit tests for class {@link ImageWriter}.
 * <p>
 * The test produces an 800×500 image composed of a 16×10 grid of squares,
 * each 50×50 pixels, alternating between two colors.
 * </p>
 */
class ImageWriterTests {

   /** Default constructor to satisfy JavaDoc generator */
   ImageWriterTests() { /* to satisfy JavaDoc generator */ }

   /** Horizontal resolution of the test image, in pixels */
   private static final int   NX       = 800;
   /** Vertical resolution of the test image, in pixels */
   private static final int   NY       = 500;
   /** Grid line spacing, in pixels (also the square size) */
   private static final int   INTERVAL = 50;
   /** Fill color for the interior of each grid square */
   private static final Color FILL     = new Color(0, 102, 204);
   /** Color of the grid lines */
   private static final Color GRID     = new Color(255, 255, 0);

   /**
    * Test method for {@link ImageWriter#writePixel(int, int, Color)} and
    * {@link ImageWriter#writeToImage(String)}.
    * <p>
    * Produces an 800×500 image with a 16×10 yellow grid on a blue background.
    * </p>
    */
   @Test
   void testImageWriter() {
      ImageWriter imageWriter = new ImageWriter(NX, NY);
      for (int i = 0; i < NY; i++)
         for (int j = 0; j < NX; j++)
            imageWriter.writePixel(j, i, i % INTERVAL == 0 || j % INTERVAL == 0 ? GRID : FILL);
      imageWriter.writeToImage("imageWriter test");
   }
}
