package org.jomaveger.math;

/**
 *
 * @author jmvegas.gertrudix
 */
public final class MathHelper {
    
    public static final float PI = (float) Math.PI;
    
    public static float ToRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }
    
    public static float ToDegrees(float radians) {
        return (float) Math.toDegrees(radians);
    }
    
    public static float Sqrt(float value) {
        return (float) Math.sqrt(value);
    }
    
    public static float Sin(float value) {
        return (float) Math.sin(value);
    }
    
    public static float Cos(float value) {
        return (float) Math.cos(value);
    }
    
    public static float Tan(float value) {
        return (float) Math.tan(value);
    }
    
    public static float Acos(float value) {
        return (float) Math.acos(value);
    }
    
    public static float Asin(float value) {
        return (float) Math.asin(value);
    }
    
    public static float Atan(float y, float x) {
        return (float) Math.atan2(y, x);
    }
    
    public static long Max(long a, long b) {
        return Math.max(a, b);
    }

    public static long Min(long a, long b) {
        return Math.min(a, b);
    }
    
    public static float Pow(float base, float exponent) {
        return (float) Math.pow(base, exponent);
    }
}
