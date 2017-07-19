package org.jomaveger.math;

import java.util.Objects;

/**
 *
 * @author jmvegas.gertrudix
 */
public final class Plane {
    
    public Vector normal;
    public float distance;
    
    public Plane() {
        this.normal = new Vector(1.0f, 0.0f, 0.0f);
        this.distance = 0.0f;
    }
    
    public Plane(float a, float b, float c, float d) {
        this.normal = new Vector(a, b, c);
        this.distance = d;
    }
    
    public Plane(Vector normal, float d) {
        this.normal = normal;
        this.distance = d;
    }
    
    public Plane(Plane plane) {
        this.normal = plane.normal;
        this.distance = plane.distance;
    }
    
    public Plane(Vector pointA, Vector pointB, Vector pointC) {
        Vector pointBA = pointB.Subtract(pointA);
        Vector pointCA = pointC.Subtract(pointA);
        this.normal = pointBA.CrossProduct(pointCA).Normalize();
        this.distance = -1.0f * this.normal.DotProduct(pointA);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.normal);
        hash = 73 * hash + Float.floatToIntBits(this.distance);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Plane other = (Plane) obj;
        if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(other.distance)) {
            return false;
        }
        if (!Objects.equals(this.normal, other.normal)) {
            return false;
        }
        return true;
    }
    
    public boolean pointOnPlane(Vector point) {
        return this.distanceToPlane(point) == 0.0f;
    }

    public boolean pointBehindThePlane(Vector point) {
        return this.distanceToPlane(point) < 0.0f;
    }
    
    public boolean pointInFrontOfThePlane(Vector point) {
        return this.distanceToPlane(point) > 0.0f;
    }
    
    public float distanceToPlane(Vector point) {
        return this.normal.DotProduct(point) + this.distance;
    }
    
    public Vector rayIntersection(Vector rayOrigin, Vector rayDirection) {
        float dotProduct = this.normal.DotProduct(rayDirection);
        if (dotProduct == 0.0f) {
            return rayOrigin;   // ray is parallel to plane and will never intersect it
        }
        
        float distanceToPlane = this.distanceToPlane(rayOrigin);
        distanceToPlane /= dotProduct;
        Vector term = rayDirection.Multiply(distanceToPlane);
        return rayOrigin.Subtract(term);
    }
}
