package org.jomaveger.math;

/**
 *
 * @author jmvegas.gertrudix
 */
public final class Vector {
    
    public float x;
    public float y;
    public float z;

    public Vector() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector(Vector vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }
    
    public void Set(Vector vec) {
        this.x = vec.x;
        this.y = vec.y;
        this.z = vec.z;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Float.floatToIntBits(this.x);
        hash = 59 * hash + Float.floatToIntBits(this.y);
        hash = 59 * hash + Float.floatToIntBits(this.z);
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
        final Vector other = (Vector) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        return true;
    }
    
    public Vector Add(Vector vec) {
        return new Vector(this.x + vec.x, this.y + vec.y, this.z + vec.z);
    }
    
    public Vector Subtract(Vector vec) {
        return new Vector(this.x - vec.x, this.y - vec.y, this.z - vec.z);
    }
    
    public Vector Negate() {
        return new Vector(-this.x, -this.y, -this.z);
    }
    
    public Vector Multiply(float s) {
        return new Vector(this.x * s, this.y * s, this.z * s);
    }
    
    public Vector Divide(float s) {
        return new Vector(this.x / s, this.y / s, this.z / s);
    }
    
    public float DotProduct(Vector vec) {
        return this.x * vec.x + this.y * vec.y + this.z * vec.z;
    }
    
    public Vector CrossProduct(Vector vec) {
        return new Vector(this.y * vec.z - this.z * vec.y, 
                            this.z * vec.x - this.x * vec.z, 
                            this.x * vec.y - this.y * vec.x);
    }
    
    public float Length() {
        return (float) MathHelper.Sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public Vector Normalize() {
        return this.Divide(this.Length());
    }
    
    public float Angle(Vector vec) {
        float dotProduct = this.DotProduct(vec);
        float vectorsMagnitude = this.Length() * vec.Length();
        float angle = MathHelper.Acos(dotProduct / vectorsMagnitude);
        if(angle == Float.NaN) {
            return 0.0f;
        }
        return angle;    
    }
    
    public float Distance(Vector vec) {
        Vector res = this.Subtract(vec);
    	
    	float distance = res.Length();

    	return distance;
    }
    
    //R = E - 2n(E.n)
    public Vector Reflection(Vector normal) {
        float dotProduct = this.DotProduct(normal); // (E.n)
        dotProduct *= 2.0; // 2(E.n)
        Vector temp = normal.Multiply(dotProduct); // 2n(E.n)
        return this.Subtract(temp); // E - 2n(E.n)
    }
    
    //Rodrigues' Rotation Formula
    // resul = v cos + (k x v) sin + k(k.v)(1 - cos)
    public Vector Rotate(float angle, Vector normal) {
        float cosine = MathHelper.Cos(angle);
        float sine = MathHelper.Sin(angle);
        
        Vector term1 = this.Multiply(cosine);
        Vector term2 = normal.CrossProduct(this).Multiply(sine);
        float dotProduct = this.DotProduct(normal);
        dotProduct *= (1 - cosine);
        Vector term3 = normal.Multiply(dotProduct);
        
        return term1.Add(term2).Add(term3);
    }
    
    //rotate a vector p using a unit quaternion q.
    public Vector RotateByAxisAndAngle(float angle, Vector axis) {
        Quaternion p = new Quaternion(this.x, this.y, this.z, 0.0f);

	axis.Normalize();

	Quaternion q = new Quaternion(axis.x, axis.y, axis.z, angle);
	q.ConvertToUnitNormQuaternion();

	Quaternion qInverse = q.Inverse();

	Quaternion rotatedVector = q.Multiply(p).Multiply(qInverse);
		
	return new Vector(rotatedVector.x, rotatedVector.y, rotatedVector.z);
    }
    
    //angle in radians
    public Vector RotateAroundAxisX(float angle) {
        Vector vec = new Vector();
        vec.x = this.x;
        vec.y = (this.y * MathHelper.Cos(angle)) - (this.z * MathHelper.Sin(angle));
	vec.z = (this.y * MathHelper.Sin(angle)) + (this.z * MathHelper.Cos(angle));
	return vec;
    }
    
    //angle in radians
    public Vector RotateAroundAxisY(float angle) {
        Vector vec = new Vector();
        vec.x = (this.x * MathHelper.Cos(angle)) + (this.z * MathHelper.Sin(angle));
	vec.y = y;
        vec.z = -(this.x * MathHelper.Sin(angle)) + (this.z * MathHelper.Cos(angle));
	return vec;
    }
    
    //angle in radians
    public Vector RotateAroundAxisZ(float angle) {
        Vector vec = new Vector();
        vec.x = (this.x * MathHelper.Cos(angle)) - (this.y * MathHelper.Sin(angle));
	vec.y = (this.x * MathHelper.Sin(angle)) + (this.y * MathHelper.Cos(angle));
	vec.z = this.z;
        return vec;
    }
    
    public Vector Translate(float dx, float dy, float dz) {
        Vector vec = new Vector();
        vec.x = this.x + dx;
        vec.y = this.y + dy;
        vec.z = this.z + dz;
        return vec;
    }
    
    public Vector Scale(float sx, float sy, float sz) {
        Vector vec = new Vector();
        vec.x = this.x * sx;
        vec.y = this.y * sy;
        vec.z = this.z * sz;
        return vec;
    }

    @Override
    public String toString() {
        return "Vector{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }
}
