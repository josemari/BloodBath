package org.jomaveger.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author jmvegas.gertrudix
 */
public enum BufferUtil {
    
    INSTANCE();
    
    private static final int SIZE_FLOAT = 4;
    private static final int SIZE_INT = 4;
    
    private BufferUtil() {
    }
    
    public FloatBuffer AllocFloats(float[] floatarray) {
        FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fb.put(floatarray).flip();
        return fb;
    }

    public IntBuffer AllocInts(int[] intarray) {
        IntBuffer ib = ByteBuffer.allocateDirect(intarray.length * SIZE_INT).order(ByteOrder.nativeOrder()).asIntBuffer();
        ib.put(intarray).flip();
        return ib;
    }
    
    public ByteBuffer AllocBytes(byte[] bytearray) {
        ByteBuffer bb = ByteBuffer.allocateDirect(bytearray.length).order(ByteOrder.nativeOrder());
        bb.put(bytearray).flip();
        return bb;
    }
    
    public ByteBuffer createByteBuffer(int size) {
        return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
    }

    public FloatBuffer createFloatBuffer(int size) {
	return createByteBuffer(size << 2).asFloatBuffer();
    }
}
