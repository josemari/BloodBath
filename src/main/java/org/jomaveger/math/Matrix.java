package org.jomaveger.math;

/**
 * @author jmvegas.gertrudix
 */
public final class Matrix {
    
    public float Xx, Xy, Xz, Xw;
    public float Yx, Yy, Yz, Yw;
    public float Zx, Zy, Zz, Zw;
    public float Tx, Ty, Tz, Tw;
    
    public Matrix() {
        this.Identity();
    }

    public void Identity() {
        this.Xx = 1.0f;
        this.Xy = 0.0f;
        this.Xz = 0.0f;
        this.Xw = 0.0f;
        
        this.Yx = 0.0f;
        this.Yy = 1.0f;
        this.Yz = 0.0f;
        this.Yw = 0.0f;
        
        this.Zx = 0.0f;
        this.Zy = 0.0f;
        this.Zz = 1.0f;
        this.Zw = 0.0f;
        
        this.Tx = 0.0f;
        this.Ty = 0.0f;
        this.Tz = 0.0f;
        this.Tw = 1.0f;
    }
    
    public Matrix(float _11, float _12, float _13, float _14, float _21, float _22, float _23, float _24, 
                    float _31, float _32, float _33, float _34, float _41, float _42, float _43, float _44) {
        this.Xx = _11;
        this.Xy = _12;
        this.Xz = _13;
        this.Xw = _14;
        
        this.Yx = _21;
        this.Yy = _22;
        this.Yz = _23;
        this.Yw = _24;
        
        this.Zx = _31;
        this.Zy = _32;
        this.Zz = _33;
        this.Zw = _34;
        
        this.Tx = _41;
        this.Ty = _42;
        this.Tz = _43;
        this.Tw = _44;
    }
    
    public Matrix(Matrix mat) {
        this.Xx = mat.Xx;
        this.Xy = mat.Xy;
        this.Xz = mat.Xz;
        this.Xw = mat.Xw;
        
        this.Yx = mat.Yx;
        this.Yy = mat.Yy;
        this.Yz = mat.Yz;
        this.Yw = mat.Yw;
        
        this.Zx = mat.Zx;
        this.Zy = mat.Zy;
        this.Zz = mat.Zz;
        this.Zw = mat.Zw;
        
        this.Tx = mat.Tx;
        this.Ty = mat.Ty;
        this.Tz = mat.Tz;
        this.Tw = mat.Tw;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Float.floatToIntBits(this.Xx);
        hash = 59 * hash + Float.floatToIntBits(this.Xy);
        hash = 59 * hash + Float.floatToIntBits(this.Xz);
        hash = 59 * hash + Float.floatToIntBits(this.Xw);
        hash = 59 * hash + Float.floatToIntBits(this.Yx);
        hash = 59 * hash + Float.floatToIntBits(this.Yy);
        hash = 59 * hash + Float.floatToIntBits(this.Yz);
        hash = 59 * hash + Float.floatToIntBits(this.Yw);
        hash = 59 * hash + Float.floatToIntBits(this.Zx);
        hash = 59 * hash + Float.floatToIntBits(this.Zy);
        hash = 59 * hash + Float.floatToIntBits(this.Zz);
        hash = 59 * hash + Float.floatToIntBits(this.Zw);
        hash = 59 * hash + Float.floatToIntBits(this.Tx);
        hash = 59 * hash + Float.floatToIntBits(this.Ty);
        hash = 59 * hash + Float.floatToIntBits(this.Tz);
        hash = 59 * hash + Float.floatToIntBits(this.Tw);
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
        final Matrix other = (Matrix) obj;
        if (Float.floatToIntBits(this.Xx) != Float.floatToIntBits(other.Xx)) {
            return false;
        }
        if (Float.floatToIntBits(this.Xy) != Float.floatToIntBits(other.Xy)) {
            return false;
        }
        if (Float.floatToIntBits(this.Xz) != Float.floatToIntBits(other.Xz)) {
            return false;
        }
        if (Float.floatToIntBits(this.Xw) != Float.floatToIntBits(other.Xw)) {
            return false;
        }
        if (Float.floatToIntBits(this.Yx) != Float.floatToIntBits(other.Yx)) {
            return false;
        }
        if (Float.floatToIntBits(this.Yy) != Float.floatToIntBits(other.Yy)) {
            return false;
        }
        if (Float.floatToIntBits(this.Yz) != Float.floatToIntBits(other.Yz)) {
            return false;
        }
        if (Float.floatToIntBits(this.Yw) != Float.floatToIntBits(other.Yw)) {
            return false;
        }
        if (Float.floatToIntBits(this.Zx) != Float.floatToIntBits(other.Zx)) {
            return false;
        }
        if (Float.floatToIntBits(this.Zy) != Float.floatToIntBits(other.Zy)) {
            return false;
        }
        if (Float.floatToIntBits(this.Zz) != Float.floatToIntBits(other.Zz)) {
            return false;
        }
        if (Float.floatToIntBits(this.Zw) != Float.floatToIntBits(other.Zw)) {
            return false;
        }
        if (Float.floatToIntBits(this.Tx) != Float.floatToIntBits(other.Tx)) {
            return false;
        }
        if (Float.floatToIntBits(this.Ty) != Float.floatToIntBits(other.Ty)) {
            return false;
        }
        if (Float.floatToIntBits(this.Tz) != Float.floatToIntBits(other.Tz)) {
            return false;
        }
        if (Float.floatToIntBits(this.Tw) != Float.floatToIntBits(other.Tw)) {
            return false;
        }
        return true;
    }
    
    public void Zero() {
        this.Xx = 0.0f;
        this.Xy = 0.0f;
        this.Xz = 0.0f;
        this.Xw = 0.0f;
        
        this.Yx = 0.0f;
        this.Yy = 0.0f;
        this.Yz = 0.0f;
        this.Yw = 0.0f;
        
        this.Zx = 0.0f;
        this.Zy = 0.0f;
        this.Zz = 0.0f;
        this.Zw = 0.0f;
        
        this.Tx = 0.0f;
        this.Ty = 0.0f;
        this.Tz = 0.0f;
        this.Tw = 0.0f;
    }
    
    public boolean IsZero() {
        if (this.Xx == 0.0f && this.Xy == 0.0f && this.Xz == 0.0f && this.Xw == 0.0f &&
            this.Yx == 0.0f && this.Yy == 0.0f && this.Yz == 0.0f && this.Yw == 0.0f &&
            this.Zx == 0.0f && this.Zy == 0.0f && this.Zz == 0.0f && this.Zw == 0.0f &&
            this.Tx == 0.0f && this.Ty == 0.0f && this.Tz == 0.0f && this.Tw == 0.0f) {
            
            return true;
            
	} else {
            
            return false;
            
        }
    }
    
    public boolean IsIdentity() {
        if (this.Xx == 1.0f && this.Xy == 0.0f && this.Xz == 0.0f && this.Xw == 0.0f &&
            this.Yx == 0.0f && this.Yy == 1.0f && this.Yz == 0.0f && this.Yw == 0.0f &&
            this.Zx == 0.0f && this.Zy == 0.0f && this.Zz == 1.0f && this.Zw == 0.0f &&
            this.Tx == 0.0f && this.Ty == 0.0f && this.Tz == 0.0f && this.Tw == 1.0f) {
            
            return true;
            
	} else {
            
            return false;
            
        }
    }
    
    public void Transpose() {
        float temp = this.Xy;
	this.Xy = this.Yx;
	this.Yx = temp;

	temp = this.Xz;
	this.Xz = this.Zx;
	this.Zx = temp;

	temp = this.Xw;
	this.Xw = this.Tx;
	this.Tx = temp;

	temp = this.Yz;
	this.Yz = this.Zy;
	this.Zy = temp;

	temp = this.Yw;
	this.Yw = this.Ty;
	this.Ty = temp;

	temp = this.Zw;
	this.Zw = this.Tz;
	this.Tz = temp;
    }

    @Override
    public String toString() {
        return "Matrix{" + "Xx=" + Xx + ", Xy=" + Xy + ", Xz=" + Xz + ", Xw=" + Xw + ", \n Yx=" + Yx + ", Yy=" + Yy + ", Yz=" + Yz + ", Yw=" + Yw + ", \n Zx=" + Zx + ", Zy=" + Zy + ", Zz=" + Zz + ", Zw=" + Zw + ", \n Tx=" + Tx + ", Ty=" + Ty + ", Tz=" + Tz + ", Tw=" + Tw + '}';
    }
  
    public float Determinant() {
        Matrix mTrans = new Matrix(this);
        float[] fTemp = new float[12];
        float fDet, tempXx, tempXy, tempXz, tempXw;

        mTrans.Transpose();

	fTemp[0] = mTrans.Zz * mTrans.Tw;
	fTemp[1] = mTrans.Zw * mTrans.Tz;
	fTemp[2] = mTrans.Zy * mTrans.Tw;
	fTemp[3] = mTrans.Zw * mTrans.Ty;
	fTemp[4] = mTrans.Zy * mTrans.Tz;
	fTemp[5] = mTrans.Zz * mTrans.Ty;
	fTemp[6] = mTrans.Zx * mTrans.Tw;
	fTemp[7] = mTrans.Zw * mTrans.Tx;
	fTemp[8] = mTrans.Zx * mTrans.Tz;
	fTemp[9] = mTrans.Zz * mTrans.Tx;
	fTemp[10] = mTrans.Zx * mTrans.Ty;
	fTemp[11] = mTrans.Zy * mTrans.Tx;

	tempXx = fTemp[0] * mTrans.Yy + fTemp[3] * mTrans.Yz + fTemp[4] * mTrans.Yw;
	tempXx -= fTemp[1] * mTrans.Yy + fTemp[2] * mTrans.Yz + fTemp[5] * mTrans.Yw;
	
        tempXy = fTemp[1] * mTrans.Yx + fTemp[6] * mTrans.Yz + fTemp[9] * mTrans.Yw;
	tempXy -= fTemp[0] * mTrans.Yx + fTemp[7] * mTrans.Yz + fTemp[8] * mTrans.Yw;
	
        tempXz = fTemp[2] * mTrans.Yx + fTemp[7] * mTrans.Yy + fTemp[10] * mTrans.Yw;
	tempXz -= fTemp[3] * mTrans.Yx + fTemp[6] * mTrans.Yy + fTemp[11] * mTrans.Yw;
	
        tempXw = fTemp[5] * mTrans.Yx + fTemp[8] * mTrans.Yy + fTemp[11] * mTrans.Yz;
	tempXw -= fTemp[4] * mTrans.Yx + fTemp[9] * mTrans.Yy + fTemp[10] * mTrans.Yz;

	fDet = mTrans.Xx * tempXx + mTrans.Xy * tempXy + mTrans.Xz * tempXz + mTrans.Xw * tempXw;

	return fDet;
    }
    
    public void Inverse() {
        Matrix mTrans = new Matrix(this);
	float[] fTemp = new float[12];
        float fDet;

	mTrans.Transpose();

        fTemp[0] = mTrans.Zz * mTrans.Tw;
	fTemp[1] = mTrans.Zw * mTrans.Tz;
	fTemp[2] = mTrans.Zy * mTrans.Tw;
	fTemp[3] = mTrans.Zw * mTrans.Ty;
	fTemp[4] = mTrans.Zy * mTrans.Tz;
	fTemp[5] = mTrans.Zz * mTrans.Ty;
	fTemp[6] = mTrans.Zx * mTrans.Tw;
	fTemp[7] = mTrans.Zw * mTrans.Tx;
	fTemp[8] = mTrans.Zx * mTrans.Tz;
	fTemp[9] = mTrans.Zz * mTrans.Tx;
	fTemp[10] = mTrans.Zx * mTrans.Ty;
	fTemp[11] = mTrans.Zy * mTrans.Tx;

	this.Xx = fTemp[0] * mTrans.Yy + fTemp[3] * mTrans.Yz + fTemp[4] * mTrans.Yw;
	this.Xx -= fTemp[1] * mTrans.Yy + fTemp[2] * mTrans.Yz + fTemp[5] * mTrans.Yw;
        
	this.Xy = fTemp[1] * mTrans.Yx + fTemp[6] * mTrans.Yz + fTemp[9] * mTrans.Yw;
	this.Xy -= fTemp[0] * mTrans.Yx + fTemp[7] * mTrans.Yz + fTemp[8] * mTrans.Yw;
        
	this.Xz = fTemp[2] * mTrans.Yx + fTemp[7] * mTrans.Yy + fTemp[10] * mTrans.Yw;
	this.Xz -= fTemp[3] * mTrans.Yx + fTemp[6] * mTrans.Yy + fTemp[11] * mTrans.Yw;
        
	this.Xw = fTemp[5] * mTrans.Yx + fTemp[8] * mTrans.Yy + fTemp[11] * mTrans.Yz;
	this.Xw -= fTemp[4] * mTrans.Yx + fTemp[9] * mTrans.Yy + fTemp[10] * mTrans.Yz;
	
        this.Yx = fTemp[1] * mTrans.Xy + fTemp[2] * mTrans.Xz + fTemp[5] * mTrans.Xw;
	this.Yx -= fTemp[0] * mTrans.Xy + fTemp[3] * mTrans.Xz + fTemp[4] * mTrans.Xw;
	
        this.Yy = fTemp[0] * mTrans.Xx + fTemp[7] * mTrans.Xz + fTemp[8] * mTrans.Xw;
	this.Yy -= fTemp[1] * mTrans.Xx + fTemp[6] * mTrans.Xz + fTemp[9] * mTrans.Xw;
	
        this.Yz = fTemp[3] * mTrans.Xx + fTemp[6] * mTrans.Xy + fTemp[11] * mTrans.Xw;
	this.Yz -= fTemp[2] * mTrans.Xx + fTemp[7] * mTrans.Xy + fTemp[10] * mTrans.Xw;
	
        this.Yw = fTemp[4] * mTrans.Xx + fTemp[9] * mTrans.Xy + fTemp[10] * mTrans.Xz;
	this.Yw -= fTemp[5] * mTrans.Xx + fTemp[8] * mTrans.Xy + fTemp[11] * mTrans.Xz;

	fTemp[0] = mTrans.Xz * mTrans.Yw;
	fTemp[1] = mTrans.Xw * mTrans.Yz;
	fTemp[2] = mTrans.Xy * mTrans.Yw;
	fTemp[3] = mTrans.Xw * mTrans.Yy;
	fTemp[4] = mTrans.Xy * mTrans.Yz;
	fTemp[5] = mTrans.Xz * mTrans.Yy;
	fTemp[6] = mTrans.Xx * mTrans.Yw;
	fTemp[7] = mTrans.Xw * mTrans.Yx;
	fTemp[8] = mTrans.Xx * mTrans.Yz;
	fTemp[9] = mTrans.Xz * mTrans.Yx;
	fTemp[10] = mTrans.Xx * mTrans.Yy;
	fTemp[11] = mTrans.Xy * mTrans.Yx;

	this.Zx = fTemp[0] * mTrans.Ty + fTemp[3] * mTrans.Tz + fTemp[4] * mTrans.Tw;
	this.Zx -= fTemp[1] * mTrans.Ty + fTemp[2] * mTrans.Tz + fTemp[5] * mTrans.Tw;
        
	this.Zy = fTemp[1] * mTrans.Tx + fTemp[6] * mTrans.Tz + fTemp[9] * mTrans.Tw;
	this.Zy -= fTemp[0] * mTrans.Tx + fTemp[7] * mTrans.Tz + fTemp[8] * mTrans.Tw;
        
	this.Zz = fTemp[2] * mTrans.Tx + fTemp[7] * mTrans.Ty + fTemp[10] * mTrans.Tw;
	this.Zz -= fTemp[3] * mTrans.Tx + fTemp[6] * mTrans.Ty + fTemp[11] * mTrans.Tw;
        
	this.Zw = fTemp[5] * mTrans.Tx + fTemp[8] * mTrans.Ty + fTemp[11] * mTrans.Tz;
	this.Zw -= fTemp[4] * mTrans.Tx + fTemp[9] * mTrans.Ty + fTemp[10] * mTrans.Tz;
        
	this.Tx = fTemp[2] * mTrans.Zz + fTemp[5] * mTrans.Zw + fTemp[1] * mTrans.Zy;
	this.Tx -= fTemp[4] * mTrans.Zw + fTemp[0] * mTrans.Zy + fTemp[3] * mTrans.Zz;
        
	this.Ty = fTemp[8] * mTrans.Zw + fTemp[0] * mTrans.Zx + fTemp[7] * mTrans.Zz;
	this.Ty -= fTemp[6] * mTrans.Zz + fTemp[9] * mTrans.Zw + fTemp[1] * mTrans.Zx;
        
	this.Tz = fTemp[6] * mTrans.Zy + fTemp[11] * mTrans.Zw + fTemp[3] * mTrans.Zx;
	this.Tz -= fTemp[10] * mTrans.Zw + fTemp[2] * mTrans.Zx + fTemp[7] * mTrans.Zy;
        
	this.Tw = fTemp[10] * mTrans.Zz + fTemp[4] * mTrans.Zx + fTemp[9] * mTrans.Zy;
	this.Tw -= fTemp[8] * mTrans.Zy + fTemp[11] * mTrans.Zz + fTemp[5] * mTrans.Zx;

	fDet = mTrans.Xx * this.Xx + mTrans.Xy * this.Xy + mTrans.Xz * this.Xz + mTrans.Xw * this.Xw;

	fDet = 1 / fDet;

	this.Xx *= fDet;
	this.Xy *= fDet;
	this.Xz *= fDet;
	this.Xw *= fDet;

	this.Yx *= fDet;
	this.Yy *= fDet;
	this.Yz *= fDet;
	this.Yw *= fDet;

	this.Zx *= fDet;
	this.Zy *= fDet;
	this.Zz *= fDet;
	this.Zw *= fDet;

	this.Tx *= fDet;
	this.Ty *= fDet;
	this.Tz *= fDet;
	this.Tw *= fDet;
    }
    
    public Matrix Negate() {
        Matrix result = new Matrix();
        
        result.Xx = -1 * this.Xx;
	result.Xy = -1 * this.Xy;
	result.Xz = -1 * this.Xz;
	result.Xw = -1 * this.Xw;

	result.Yx = -1 * this.Yx;
	result.Yy = -1 * this.Yy;
	result.Yz = -1 * this.Yz;
	result.Yw = -1 * this.Yw;

	result.Zx = -1 * this.Zx;
	result.Zy = -1 * this.Zy;
	result.Zz = -1 * this.Zz;
	result.Zw = -1 * this.Zw;

	result.Tx = -1 * this.Tx;
	result.Ty = -1 * this.Ty;
	result.Tz = -1 * this.Tz;
	result.Tw = -1 * this.Tw;
        
        return result;
    }
    
    public Matrix Multiply(float scalar) {
        Matrix result = new Matrix();
        
        result.Xx = scalar * this.Xx;
	result.Xy = scalar * this.Xy;
	result.Xz = scalar * this.Xz;
	result.Xw = scalar * this.Xw;

	result.Yx = scalar * this.Yx;
	result.Yy = scalar * this.Yy;
	result.Yz = scalar * this.Yz;
	result.Yw = scalar * this.Yw;

	result.Zx = scalar * this.Zx;
	result.Zy = scalar * this.Zy;
	result.Zz = scalar * this.Zz;
	result.Zw = scalar * this.Zw;

	result.Tx = scalar * this.Tx;
	result.Ty = scalar * this.Ty;
	result.Tz = scalar * this.Tz;
	result.Tw = scalar * this.Tw;
        
        return result;
    }
    
    public Matrix Add(Matrix mat) {
        Matrix result = new Matrix();
        
        result.Xx = this.Xx + mat.Xx;
	result.Xy = this.Xy + mat.Xy;
	result.Xz = this.Xz + mat.Xz;
	result.Xw = this.Xw + mat.Xw;

	result.Yx = this.Yx + mat.Yx;
	result.Yy = this.Yy + mat.Yy;
	result.Yz = this.Yz + mat.Yz;
	result.Yw = this.Yw + mat.Yw;

	result.Zx = this.Zx + mat.Zx;
	result.Zy = this.Zy + mat.Zy;
	result.Zz = this.Zz + mat.Zz;
	result.Zw = this.Zw + mat.Zw;

	result.Tx = this.Tx + mat.Tx;
	result.Ty = this.Ty + mat.Ty;
	result.Tz = this.Tz + mat.Tz;
	result.Tw = this.Tw + mat.Tw;
                
        return result;
    }
    
    public Matrix Substract(Matrix mat) {
        Matrix result = new Matrix();
        
        result.Xx = this.Xx - mat.Xx;
	result.Xy = this.Xy - mat.Xy;
	result.Xz = this.Xz - mat.Xz;
	result.Xw = this.Xw - mat.Xw;

	result.Yx = this.Yx - mat.Yx;
	result.Yy = this.Yy - mat.Yy;
	result.Yz = this.Yz - mat.Yz;
	result.Yw = this.Yw - mat.Yw;

	result.Zx = this.Zx - mat.Zx;
	result.Zy = this.Zy - mat.Zy;
	result.Zz = this.Zz - mat.Zz;
	result.Zw = this.Zw - mat.Zw;

	result.Tx = this.Tx - mat.Tx;
	result.Ty = this.Ty - mat.Ty;
	result.Tz = this.Tz - mat.Tz;
	result.Tw = this.Tw - mat.Tw;
                
        return result;
    }
    
    public Matrix Multiply(Matrix mat) {
        Matrix result = new Matrix();

	result.Xx = this.Xx * mat.Xx + this.Xy * mat.Yx + this.Xz * mat.Zx + this.Xw * mat.Tx;
	result.Xy = this.Xx * mat.Xy + this.Xy * mat.Yy + this.Xz * mat.Zy + this.Xw * mat.Ty;
	result.Xz = this.Xx * mat.Xz + this.Xy * mat.Yz + this.Xz * mat.Zz + this.Xw * mat.Tz;
	result.Xw = this.Xx * mat.Xw + this.Xy * mat.Yw + this.Xz * mat.Zw + this.Xw * mat.Tw;

	result.Yx = this.Yx * mat.Xx + this.Yy * mat.Yx + this.Yz * mat.Zx + this.Yw * mat.Tx;
	result.Yy = this.Yx * mat.Xy + this.Yy * mat.Yy + this.Yz * mat.Zy + this.Yw * mat.Ty;
	result.Yz = this.Yx * mat.Xz + this.Yy * mat.Yz + this.Yz * mat.Zz + this.Yw * mat.Tz;
	result.Yw = this.Yx * mat.Xw + this.Yy * mat.Yw + this.Yz * mat.Zw + this.Yw * mat.Tw;

	result.Zx = this.Zx * mat.Xx + this.Zy * mat.Yx + this.Zz * mat.Zx + this.Zw * mat.Tx;
	result.Zy = this.Zx * mat.Xy + this.Zy * mat.Yy + this.Zz * mat.Zy + this.Zw * mat.Ty;
	result.Zz = this.Zx * mat.Xz + this.Zy * mat.Yz + this.Zz * mat.Zz + this.Zw * mat.Tz;
	result.Zw = this.Zx * mat.Xw + this.Zy * mat.Yw + this.Zz * mat.Zw + this.Zw * mat.Tw;

	result.Tx = this.Tx * mat.Xx + this.Ty * mat.Yx + this.Tz * mat.Zx + this.Tw * mat.Tx;
	result.Ty = this.Tx * mat.Xy + this.Ty * mat.Yy + this.Tz * mat.Zy + this.Tw * mat.Ty;
	result.Tz = this.Tx * mat.Xz + this.Ty * mat.Yz + this.Tz * mat.Zz + this.Tw * mat.Tz;
	result.Tw = this.Tx * mat.Xw + this.Ty * mat.Yw + this.Tz * mat.Zw + this.Tw * mat.Tw;

	return result;
    }
    
    public Vector Multiply(Vector vec, boolean isPoint) {
        if (isPoint) {
            return this.MultiplyByPoint(vec);
        } else {
            return this.MultiplyByVector(vec);
        }
    }

    private Vector MultiplyByPoint(Vector vec) {
        Vector result = new Vector();
	float x, y, z, w;

	x = vec.x * this.Xx + vec.y * this.Yx + vec.z * this.Zx + this.Tx;
	y = vec.x * this.Xy + vec.y * this.Yy + vec.z * this.Zy + this.Ty;
	z = vec.x * this.Xz + vec.y * this.Yz + vec.z * this.Zz + this.Tz;
	w = vec.x * this.Xw + vec.y * this.Yw + vec.z * this.Zw + this.Tw;

	result.x = x / w;
	result.y = y / w;
	result.z = z / w;

	return result;
    }

    private Vector MultiplyByVector(Vector vec) {
        Vector result = new Vector();
	float x, y, z;

	x = vec.x * this.Xx + vec.y * this.Yx + vec.z * this.Zx;
	y = vec.x * this.Xy + vec.y * this.Yy + vec.z * this.Zy;
	z = vec.x * this.Xz + vec.y * this.Yz + vec.z * this.Zz;

	result.x = x;
	result.y = y;
	result.z = z;

	return result;
    }
    
    public void SetRotationByAxisX(float angleInRadians) {
        float cos = MathHelper.Cos(angleInRadians);
	float sin = MathHelper.Sin(angleInRadians);
		
	this.Xx = 1.0f;
	this.Xy = 0.0f;
	this.Xz = 0.0f;
	this.Xw = 0.0f;

	this.Yx = 0.0f;
	this.Yy = cos;
	this.Yz = -sin;
	this.Yw = 0.0f;

	this.Zx = 0.0f;
	this.Zy = sin;
	this.Zz = cos;
	this.Zw = 0.0f;

	this.Tx = 0.0f;
	this.Ty = 0.0f;
	this.Tz = 0.0f;
	this.Tw = 1.0f;
    }
    
    public void SetRotationByAxisY(float angleInRadians) {
        float cos = MathHelper.Cos(angleInRadians);
	float sin = MathHelper.Sin(angleInRadians);
		
	this.Xx = cos;
	this.Xy = 0.0f;
	this.Xz = sin;
	this.Xw = 0.0f;

	this.Yx = 0.0f;
	this.Yy = 1.0f;
	this.Yz = 0.0f;
	this.Yw = 0.0f;

	this.Zx = -sin;
	this.Zy = 0.0f;
	this.Zz = cos;
	this.Zw = 0.0f;

	this.Tx = 0.0f;
	this.Ty = 0.0f;
	this.Tz = 0.0f;
	this.Tw = 1.0f;
    }
    
    public void SetRotationByAxisZ(float angleInRadians) {
        float cos = MathHelper.Cos(angleInRadians);
	float sin = MathHelper.Sin(angleInRadians);
		
	this.Xx = cos;
	this.Xy = -sin;
	this.Xz = 0.0f;
	this.Xw = 0.0f;

	this.Yx = sin;
	this.Yy = cos;
	this.Yz = 0.0f;
	this.Yw = 0.0f;

	this.Zx = 0.0f;
	this.Zy = 0.0f;
	this.Zz = 1.0f;
	this.Zw = 0.0f;

	this.Tx = 0.0f;
	this.Ty = 0.0f;
	this.Tz = 0.0f;
	this.Tw = 1.0f;
    }
    
    public void SetRotationByArbitraryAxis(float angleInRadians, Vector axis) {
        float cos = MathHelper.Cos(angleInRadians);
	float sin = MathHelper.Sin(angleInRadians);
	float factor = 1 - cos;

        Vector normalizedAxis = axis.Normalize();

	this.Xx = (normalizedAxis.x * normalizedAxis.x) * factor + cos;
	this.Xy = (normalizedAxis.x * normalizedAxis.y) * factor + (normalizedAxis.z * sin);
	this.Xz = (normalizedAxis.x * normalizedAxis.z) * factor - (normalizedAxis.y * sin);
	this.Xw = 0.0f;

	this.Yx = (normalizedAxis.y * normalizedAxis.x) * factor - (normalizedAxis.z * sin);
	this.Yy = (normalizedAxis.y * normalizedAxis.y) * factor + cos;
	this.Yz = (normalizedAxis.y * normalizedAxis.z) * factor + (normalizedAxis.x * sin);
	this.Yw = 0.0f;

	this.Zx = (normalizedAxis.z * normalizedAxis.x) * factor + (normalizedAxis.y * sin);
	this.Zy = (normalizedAxis.z * normalizedAxis.y) * factor - (normalizedAxis.x * sin);
	this.Zz = (normalizedAxis.z * normalizedAxis.z) * factor + cos;
	this.Zw = 0.0f;

	this.Tx = 0.0f;
	this.Ty = 0.0f;
	this.Tz = 0.0f;
	this.Tw = 1.0f;
    }
    
    public void SetTranslation(float dx, float dy, float dz) {
        this.Identity();
	this.Tx = dx;
	this.Ty = dy;
	this.Tz = dz;
    }
    
    public void SetScale(float sx, float sy, float sz) {
        this.Identity();
	this.Xx = sx;
	this.Yy = sy;
	this.Zz = sz;
    }
    
    public void BuildViewMatrix(Vector eye, Vector lookAt, Vector up) {
        this.Identity();

	Vector zaxisAux = eye.Subtract(lookAt);
	Vector zaxis = zaxisAux.Normalize();

	Vector xaxisAux = up.CrossProduct(zaxis);
	Vector xaxis = xaxisAux.Normalize();

	Vector yaxis = zaxis.CrossProduct(xaxis);

	this.Xx = xaxis.x;
	this.Xy = xaxis.y;
	this.Xz = xaxis.z;
	this.Xw = -1 * xaxis.DotProduct(eye);

	this.Yx = yaxis.x;
	this.Yy = yaxis.y;
	this.Yz = yaxis.z;
	this.Yw = -1 * yaxis.DotProduct(eye);

	this.Zx = zaxis.x;
	this.Zy = zaxis.y;
	this.Zz = zaxis.z;
	this.Zw = -1 * zaxis.DotProduct(eye);
    }
    
    public void BuildPerspectiveMatrix(float width, float height, float near, float far, float angleInRadians) {
        this.Zero();

	float aspectRatio = width / height;

	this.Xx = 1 / (aspectRatio * MathHelper.Tan(angleInRadians / 2));
	this.Yy = 1 / MathHelper.Tan(angleInRadians / 2);
	this.Zz = (-near - far) / (near - far);
	this.Zw = 1.0f;
	this.Tz = 2 * near * far / (near - far);
    }
    
    public void BuildOrtographicMatrix(float width, float height, float near, float far) {
        this.Zero();

	this.Xx = 2 / width;
	this.Yy = 2 / height;
	this.Zz = 1 / (far - near);
	this.Tz = -near / (far - near);
	this.Tw = 1.0f;
    }
}
