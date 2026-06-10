package renderer;

/**
 * PixelManager is a helper class. It is used for multi-threading in the
 * renderer and for follow up its progress.
 * A Camera uses one pixel manager object and several Pixel objects - one in
 * each thread.
 */
class PixelManager {
   /**
    * Immutable record containing an allocated pixel's row and column numbers.
    *
    * @param col pixel column number
    * @param row pixel row number
    */
   record Pixel(int col, int row) {}

   private int                 maxRows       = 0;
   private int                 maxCols       = 0;
   private long                totalPixels   = 0L;

   private volatile int        cRow          = 0;
   private volatile int        cCol          = -1;
   private volatile long       pixels        = 0L;
   private volatile int        lastPrinted   = 0;

   private boolean             print         = false;
   private long                printInterval = 100L;
   private static final String PRINT_FORMAT  = "%5.1f%%\r";

   /** Mutex for allocating the next pixel across threads. */
   private Object              mutexNext     = new Object();
   /** Mutex for updating and printing progress percentage. */
   private Object              mutexPixels   = new Object();

   /**
    * Initializes pixel manager data for multi-threading.
    *
    * @param maxRows  number of pixel rows
    * @param maxCols  number of pixel columns
    * @param interval print time interval in seconds; omit or pass 0 to disable printing
    */
   PixelManager(int maxRows, int maxCols, double... interval) {
      if (interval.length > 1)
         throw new IllegalArgumentException("only up to one interval argument is allowed");
      this.maxRows  = maxRows;
      this.maxCols  = maxCols;
      totalPixels   = (long) maxRows * maxCols;
      printInterval = interval.length == 0 ? printInterval : (long) (interval[0] * 10);
      print         = printInterval != 0;
      if (print) System.out.printf(PRINT_FORMAT, 0d);
   }

   /**
    * Thread-safe method that returns the next available pixel.
    * This is a critical section shared by all rendering threads.
    *
    * @return the next pixel to render, or {@code null} when all pixels are done
    */
   Pixel nextPixel() {
      synchronized (mutexNext) {
         if (cRow == maxRows) return null;

         ++cCol;
         if (cCol < maxCols)
            return new Pixel(cRow, cCol);

         cCol = 0;
         ++cRow;
         if (cRow < maxRows)
            return new Pixel(cRow, cCol);
      }
      return null;
   }

   /** Updates the pixel count and prints progress percentage when the interval is reached. */
   void pixelDone() {
      boolean shouldPrint = false;
      int     percentage  = 0;
      synchronized (mutexPixels) {
         ++pixels;
         if (print) {
            percentage = (int) (1000L * pixels / totalPixels);
            if (percentage - lastPrinted >= printInterval) {
               lastPrinted = percentage;
               shouldPrint = true;
            }
         }
         if (shouldPrint) System.out.printf(PRINT_FORMAT, percentage / 10d);
      }
   }
}
