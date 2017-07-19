package org.jomaveger.math;

/**
 * @author jmvegas.gertrudix
 */
public final class Quaternion {
    
    public float x;
    public float y;
    public float z;
    public float w;
    
    public Quaternion() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }
    
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quaternion(final Quaternion quat) {
        this.x = quat.x;
        this.y = quat.y;
        this.z = quat.z;
        this.w = quat.w;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Float.floatToIntBits(this.x);
        hash = 37 * hash + Float.floatToIntBits(this.y);
        hash = 37 * hash + Float.floatToIntBits(this.z);
        hash = 37 * hash + Float.floatToIntBits(this.w);
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
        final Quaternion other = (Quaternion) obj;
        if (Float.floatToIntBits(this.x) != Float.floatToIntBits(other.x)) {
            return false;
        }
        if (Float.floatToIntBits(this.y) != Float.floatToIntBits(other.y)) {
            return false;
        }
        if (Float.floatToIntBits(this.z) != Float.floatToIntBits(other.z)) {
            return false;
        }
        if (Float.floatToIntBits(this.w) != Float.floatToIntBits(other.w)) {
            return false;
        }
        return true;
    }

    

    @Override
    public String toString() {
        return "Quaternion{" + "x=" + x + ", y=" + y + ", z=" + z + ", w=" + w + '}';
    }
    
    public Quaternion Add(Quaternion quat) {
        return new Quaternion(this.x + quat.x, this.y + quat.y, this.z + quat.z, this.w + quat.w);
    }
    
    public Quaternion Substract(Quaternion quat) {
        return new Quaternion(this.x - quat.x, this.y - quat.y, this.z - quat.z, this.w - quat.w);
    }
    
    public Quaternion Multiply(float scalar) {
        return new Quaternion(this.x * scalar, this.y * scalar, this.z * scalar, this.w * scalar);
    }
    
    public Quaternion Multiply(final Quaternion quat) {	
        Quaternion result = new Quaternion();

	result.w = (this.w * quat.w) - (this.x * quat.x) - (this.y * quat.y) - (this.z * quat.z);
	result.x = (this.x * quat.w) + (this.w * quat.x) + (this.y * quat.z) - (this.z * quat.y);
	result.y = (this.y * quat.w) + (this.w * quat.y) + (this.z * quat.x) - (this.x * quat.z);
	result.z = (this.z * quat.w) + (this.w * quat.z) + (this.x * quat.y) - (this.y * quat.x);

	return result;
    }
    
    public Quaternion Multiply(final Vector vec) {
        Quaternion result = new Quaternion();

	result.w = - (this.x * vec.x) - (this.y * vec.y) - (this.z * vec.z);
	result.x =   (this.w * vec.x) + (this.y * vec.z) - (this.z * vec.y);
	result.y =   (this.w * vec.y) + (this.z * vec.x) - (this.x * vec.z);
	result.z =   (this.w * vec.z) + (this.x * vec.y) - (this.y * vec.x);

	return result;
    }
    
    public float Norm() {
	return MathHelper.Sqrt(this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
    }
    
    public void Normalize() {
        float norm = this.Norm();
        
        if (norm > 0) {
            
            float t = 1 / norm;

            this.x *= t;
            this.y *= t;
            this.z *= t;
            this.w *= t;
	}
    }

    public Quaternion Conjugate() {
        return new Quaternion(-1 * this.x, -1 * this.y, -1 * this.z, this.w);
    }
    
    public Quaternion Inverse() {
	Quaternion result = this.Conjugate();

	float norm = this.Norm();
	float inverseOfSquaredNorm = 1 / (norm * norm);

	result.x *= inverseOfSquaredNorm;
	result.y *= inverseOfSquaredNorm;
	result.z *= inverseOfSquaredNorm;
	result.w *= inverseOfSquaredNorm;

	return result;
    }
    
    public Quaternion Divide(final Quaternion quat) {
	return this.Multiply(quat.Inverse());
    }
    
    public float DotProduct(final Quaternion quat) {
	return (this.x * quat.x) + (this.y * quat.y) + (this.z * quat.z) + (this.w * quat.w);
    }

    public float Angle(final Quaternion quat) {
        float dotProduct = this.DotProduct(quat);
	float normProduct = this.Norm() * quat.Norm();
	return MathHelper.Acos(dotProduct / normProduct);
    }
    
    public void ConvertToUnitNormQuaternion() {
	float angle = MathHelper.ToRadians(w);
		
	this.w = MathHelper.Cos(angle * 0.5f);

	Vector vec = new Vector(this.x, this.y, this.z);
	vec.Normalize();
		
	this.x = vec.x * MathHelper.Sin(angle * 0.5f);
	this.y = vec.y * MathHelper.Sin(angle * 0.5f);
	this.z = vec.z * MathHelper.Sin(angle * 0.5f);
    }
    
    public void FromEulerAngles(float pitch, float yaw, float roll) {
	float x = MathHelper.ToRadians(pitch / 2);
	float y = MathHelper.ToRadians(yaw / 2);
	float z = MathHelper.ToRadians(roll / 2);

	float cosx = MathHelper.Cos(x);
	float sinx = MathHelper.Sin(x);

	float cosy = MathHelper.Cos(y);
	float siny = MathHelper.Sin(y);

	float cosz = MathHelper.Cos(z);
	float sinz = MathHelper.Sin(z);

	this.w = cosz * cosy * cosx + sinz * siny * sinx;
	this.x = cosz * cosy * sinx - sinz * siny * cosx;
	this.y = cosz * siny * cosx + sinz * cosy * sinx;
	this.z = sinz * cosy * cosx - cosz * siny * sinx;
    }
    
    public Vector ToEulerAngles() {
	float x = 0.0f;
	float y = 0.0f;
	float z = 0.0f;

	float test = 2 * (this.x * this.z - this.w * this.y);

	if (test != 1 && test != -1) {
            x = MathHelper.Atan(this.y * this.z + this.w * this.x, 0.5f - (this.x * this.x + this.y * this.y));
            y = MathHelper.Asin(-2 * (this.x * this.z - this.w * this.y));
            z = MathHelper.Atan(this.x * this.y + this.w * this.z, 0.5f - (this.y * this.y + this.z * this.z));
	} else if (test == 1) {
            z = MathHelper.Atan(this.x * this.y + this.w * this.z, 0.5f - (this.y * this.y + this.z * this.z));
            y = -1 * MathHelper.PI / 2.0f;
            x = -z + MathHelper.Atan(this.x * this.y - this.w * this.z, this.x * this.z + this.w * this.y);
	} else if (test == -1) {
            z = MathHelper.Atan(this.x * this.y + this.w * this.z, 0.5f - (this.y * this.y + this.z * this.z));
            y = MathHelper.PI / 2.0f;
            x = z + MathHelper.Atan(this.x * this.y - this.w * this.z, this.x * this.z + this.w * this.y);
	}

        x = MathHelper.ToDegrees(x);
	y = MathHelper.ToDegrees(y);
	z = MathHelper.ToDegrees(z);

	Vector euler = new Vector(x, y, z);

        return euler;
    }
    
    public Matrix ToMatrix() {
	Matrix mat = new Matrix();

	float yy = this.y * this.y;
	float zz = this.z * this.z;
	float xy = this.x * this.y;
	float zw = this.z * this.w;
	float xz = this.x * this.z;
	float yw = this.y * this.w;
	float xx = this.x * this.x;
	float yz = this.y * this.z;
	float xw = this.x * this.w;

	mat.Xx = 1 - 2 * yy - 2 * zz;
	mat.Xy = 2 * xy + 2 * zw;
	mat.Xz = 2 * xz - 2 * yw;

	mat.Yx = 2 * xy - 2 * zw;
	mat.Yy = 1 - 2 * xx - 2 * zz;
	mat.Yz = 2 * yz + 2 * xw;

	mat.Zx = 2 * xz + 2 * yw;
	mat.Zy = 2 * yz - 2 * xw;
	mat.Zz = 1 - 2 * xx - 2 * yy;

        return mat;
    }
    
    public void FromMatrix(final Matrix mat) {
        float trace = mat.Xx + mat.Yy + mat.Zz;

	if (trace > 0) {
            
            this.w = 0.5f * MathHelper.Sqrt(1 + trace);
            float S = 0.25f / this.w;

            this.x = S * (mat.Yz - mat.Zy);
            this.y = S * (mat.Zx - mat.Xz);
            this.z = S * (mat.Xy - mat.Yx);
            
	} else if (mat.Xx > mat.Yy && mat.Xx > mat.Zz) {
            
            this.x = 0.5f * MathHelper.Sqrt(1 + mat.Xx - mat.Yy - mat.Zz);
            float X = 0.25f / this.x;

            this.y = X * (mat.Yx * mat.Xy);
            this.z = X * (mat.Zx * mat.Xz);
            this.w = X * (mat.Yz * mat.Zy);
            
	} else if (mat.Yy > mat.Zz) {
            
            this.y = 0.5f * MathHelper.Sqrt(1 - mat.Xx + mat.Yy - mat.Zz);
            float Y = 0.25f / this.y;
			
            this.x = Y * (mat.Yx + mat.Xy);
            this.z = Y * (mat.Zy + mat.Yz);
            this.w = Y * (mat.Zx - mat.Xz);
            
	} else {
            
            this.z = 0.5f * MathHelper.Sqrt(1 - mat.Xx - mat.Yy + mat.Zz);
            float Z = 0.25f / this.z;

            this.x = Z * (mat.Zx + mat.Xz);
            this.y = Z * (mat.Zy + mat.Yz);
            this.w = Z * (mat.Xy + mat.Yx);
	}
    }
    
    public Quaternion slerp(float amount, Quaternion end) {
        if (amount < 0.0) {
            return this;
        } else if (amount > 1.0) {
            return end;
        }
            
        float dot = this.DotProduct(end);
        float x2, y2, z2, w2;
        
        if (dot < 0.0) {
            
            dot = 0.0f - dot;
            x2 = 0.0f - end.x;
            y2 = 0.0f - end.y;
            z2 = 0.0f - end.z;
            w2 = 0.0f - end.w;
        
        } else {
            
            x2 = end.x;
            y2 = end.y;
            z2 = end.z;
            w2 = end.w;
        }

        float t1, t2;
        final float EPSILON = 0.0001f;
        
        if ((1.0f - dot) > EPSILON) { // standard case (slerp)
            
            float angle = MathHelper.Acos(dot);
            float sinAngle = MathHelper.Sin(angle);
            t1 = MathHelper.Sin((1.0f - amount) * angle) / sinAngle;
            t2 = MathHelper.Sin(amount * angle) / sinAngle;
        
        } else { // just lerp
            
            t1 = 1.0f - amount;
            t2 = amount;
        
        }

        return new Quaternion((this.x * t1) + (x2 * t2), (this.y * t1) + (y2 * t2),
                                (this.z * t1) + (z2 * t2), (this.w * t1) + (w2 * t2));
    }
}
