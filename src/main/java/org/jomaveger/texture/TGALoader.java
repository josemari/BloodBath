package org.jomaveger.texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.jomaveger.util.BinaryLoader;
import org.lwjgl.BufferUtils;

/**
 * @author jmvegas.gertrudix
 */
public class TGALoader {

    private static final int TGA_RLE = 10;		// This tells us that the targa is Run-Length Encoded (RLE)

    /**
     * The width of the texture that needs to be generated
     */
    private static int texWidth;
    
    /**
     * The height of the texture that needs to be generated
     */
    private static int texHeight;
    
    /**
     * The width of the TGA image
     */
    private static int width;
    
    /**
     * The height of the TGA image
     */
    private static int height;
    
    /**
     * The bit depth of the image
     */
    private static short pixelDepth;

    /**
     * Create a new TGA Loader
     */
    private TGALoader() {
    }

    /**
     * Flip the endian-ness of the short
     *
     * @param signedShort The short to flip
     * @return The flipped short
     */
    @SuppressWarnings("unused")
    private static short flipEndian(short signedShort) {
        int input = signedShort & 0xFFFF;
        return (short) (input << 8 | (input & 0xFF00) >>> 8);
    }

    /**
     * Get the last bit depth read from a TGA
     *
     * @return The last bit depth read
     */
    public static int getLastDepth() {
        return pixelDepth;
    }

    /**
     * Get the last width read from a TGA
     *
     * @return Get the last width in pixels fread from a TGA
     */
    public static int getLastWidth() {
        return width;
    }

    /**
     * Get the last height read from a TGA
     *
     * @return Get the last height in pixels fread from a TGA
     */
    public static int getLastHeight() {
        return height;
    }

    /**
     * Get the last required texture width for a loaded image
     *
     * @return Get the ast required texture width for a loaded image
     */
    public static int getLastTexWidth() {
        return texWidth;
    }

    /**
     * Get the ast required texture height for a loaded image
     *
     * @return Get the ast required texture height for a loaded image
     */
    public static int getLastTexHeight() {
        return texHeight;
    }

    /**
     * Load a TGA image from the specified stream
     *
     * @param fis The stream from which we'll load the TGA
     * @throws IOException Indicates a failure to read the TGA
     * @return The byte buffer containing texture data
     */
    public static ByteBuffer loadImage(InputStream fis) throws IOException {
        return loadImage(fis, true);
    }

    public static ByteBuffer loadImage(InputStream fis, boolean flipped) throws IOException {
        BinaryLoader loader = new BinaryLoader(fis);

        // The dimensions of the image
        byte length = 0;					// The length in bytes to the pixels
        byte imageType = 0;					// The image type (RLE, RGB, Alpha...)

        byte[] rawData;
        // The file pointer
        int channels = 0;					// The channels of the image (3 = RGA : 4 = RGBA)
        int stride = 0;						// The stride (channels * width)
        int i = 0;							// A counter

        // Allocate the structure that will hold our eventual image data (must free it!)
        // Read in the length in bytes from the header to the pixel data
        length = (byte) loader.readByte(); 
        loader.markPos();
        loader.seekMarkOffset(1);

        imageType = (byte) loader.readByte();
        loader.markPos();
        loader.seekMarkOffset(9);

        width = loader.readShort();
        height = loader.readShort();
        pixelDepth = (byte) loader.readByte();

        texWidth = get2Fold(width);
        texHeight = get2Fold(height);

        loader.markPos();
        loader.seekMarkOffset(length + 1);

        // Check if the image is RLE compressed or not
        if (imageType != TGA_RLE) {
            // Check if the image is a 24 or 32-bit image
            if (pixelDepth == 24 || pixelDepth == 32) {
                // Calculate the channels (3 or 4) - (use bits >> 3 for more speed).
                // Next, we calculate the stride and allocate enough memory for the pixels.
                channels = pixelDepth / 8;
                stride = channels * width;
                rawData = new byte[stride * height];

                // Load in all the pixel data line by line
                int count = 0;
                for (int y = 0; y < height; y++) {
                    // Store a pointer to the current line of pixels
                    for (int j = 0; j < stride; j += channels) {
                        rawData[count + 2] = (byte) loader.readByte();
                        rawData[count + 1] = (byte) loader.readByte();
                        rawData[count] = (byte) loader.readByte();
                        if (channels == 4) {
                            loader.readByte();
                        }
                        count += channels;
                    }
                }
            } else if (pixelDepth == 16) { // Check if the image is a 16 bit image (RGB stored in 1 unsigned short)
                int pixels = 0;
                int r = 0, g = 0, b = 0;

                // Since we convert 16-bit images to 24 bit, we hardcode the channels to 3.
                // We then calculate the stride and allocate memory for the pixels.
                channels = 3;
                stride = channels * width;
                rawData = new byte[stride * height];

                // Load in all the pixel data pixel by pixel
                for (int j = 0; j < width * height; j++) {
                    // Read in the current pixel
                    //fread(&pixels, sizeof(unsigned short), 1, pFile);
                    pixels = loader.readShort2();

                    // Convert the 16-bit pixel into an RGB
                    b = (pixels & 0x1f) << 3;
                    g = ((pixels >> 5) & 0x1f) << 3;
                    r = ((pixels >> 10) & 0x1f) << 3;

                    // This essentially assigns the color to our array and swaps the
                    // B and R values at the same time.
                    rawData[j * 3 + 0] = (byte) r;
                    rawData[j * 3 + 1] = (byte) g;
                    rawData[j * 3 + 2] = (byte) b;
                }
            } // Else return a NULL for a bad or unsupported pixel format
            else {
                return null;
            }
        } else { // Else, it must be Run-Length Encoded (RLE)
            // Create some variables to hold the rleID, current colors read, channels, & stride.
            short rleID = 0;
            int colorsRead = 0;
            channels = pixelDepth / 8;
            stride = channels * width;

            // Next we want to allocate the memory for the pixels and create an array,
            // depending on the channel count, to read in for each pixel.
            rawData = new byte[stride * height];
            byte[] pColors = new byte[channels];

            // Load in all the pixel data
            while (i < width * height) {

                // Read in the current color count + 1
                //fread(&rleID, sizeof(byte), 1, pFile);
                rleID = (short) loader.readByte();

                // Check if we don't have an encoded string of colors
                if (rleID < 128) {
                    // Increase the count by 1
                    rleID++;

                    // Go through and read all the unique colors found
                    while (rleID > 0) {
                        // Read in the current color
                        //fread(pColors, sizeof(byte) * channels, 1, pFile);
                        for (int k = 0; k < pColors.length; k++) {
                            pColors[k] = (byte) loader.readByte();
                        }
                        // Store the current pixel in our image array
                        rawData[colorsRead + 0] = pColors[2];
                        rawData[colorsRead + 1] = pColors[1];
                        rawData[colorsRead + 2] = pColors[0];

                        // If we have a 4 channel 32-bit image, assign one more for the alpha
                        if (pixelDepth == 32) {
                            rawData[colorsRead + 3] = pColors[3];
                        }

                        // Increase the current pixels read, decrease the amount
                        // of pixels left, and increase the starting index for the next pixel.
                        i++;
                        rleID--;
                        colorsRead += channels;
                    }
                } // Else, let's read in a string of the same character
                else {
                    // Minus the 128 ID + 1 (127) to get the color count that needs to be read
                    rleID -= 127;

                    // Read in the current color, which is the same for a while
                    
                    for (int k = 0; k < pColors.length; k++) {
                        pColors[k] = (byte) loader.readByte();
                    }

                    // Go and read as many pixels as are the same
                    while (rleID > 0) {
                        // Assign the current pixel to the current index in our pixel array
                        rawData[colorsRead + 0] = pColors[2];
                        rawData[colorsRead + 1] = pColors[1];
                        rawData[colorsRead + 2] = pColors[0];

                        // If we have a 4 channel 32-bit image, assign one more for the alpha
                        if (pixelDepth == 32) {
                            rawData[colorsRead + 3] = pColors[3];
                        }

                        // Increase the current pixels read, decrease the amount
                        // of pixels left, and increase the starting index for the next pixel.
                        i++;
                        rleID--;
                        colorsRead += channels;
                    }
                }
            }
        }

        // Get a pointer to the image memory
        ByteBuffer scratch = BufferUtils.createByteBuffer(rawData.length);
        scratch.put(rawData);
        scratch.flip();

        // Return the TGA data (remember, you must free this data after you are done)
        return scratch;
    }

    /**
     * Get the closest greater power of 2 to the fold number
     *
     * @param fold The target number
     * @return The power of 2
     */
    private static int get2Fold(int fold) {
        int ret = 2;
        while (ret < fold) {
            ret *= 2;
        }
        return ret;
    }
}
