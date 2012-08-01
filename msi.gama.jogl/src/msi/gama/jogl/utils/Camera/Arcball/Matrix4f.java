package msi.gama.jogl.utils.Camera.Arcball;

/**
 * Created by IntelliJ IDEA.
 * User: pepijn
 * Date: Aug 7, 2005
 * Time: 6:01:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Matrix4f {
    float M00;
    float M10;
    float M20;
    float M30;
    float M01;
    float M11;
    float M21;
    float M31;
    float M02;
    float M12;
    float M22;
    float M32;
    float M03;
    float M13;
    float M23;
    float M33;

    public Matrix4f() {
        setIdentity();
    }

    public void get(float[] dest) {
        dest[0] = M00;
        dest[1] = M10;
        dest[2] = M20;
        dest[3] = M30;
        dest[4] = M01;
        dest[5] = M11;
        dest[6] = M21;
        dest[7] = M31;
        dest[8] = M02;
        dest[9] = M12;
        dest[10] = M22;
        dest[11] = M32;
        dest[12] = M03;
        dest[13] = M13;
        dest[14] = M23;
        dest[15] = M33;
    }

    public void setZero() {
        M00 = M01 = M02 = M03 = M10 = M11 = M12 = M13 = M20 = M21 = M22 = M23 = M30 = M31 = M32 = M33 = 0.0f;
    }

    public void setIdentity() {
        setZero();
        M00 = M11 = M22 = M33 = 1.0f;
    }

    public void setRotation(Quat4f q1) {
        float n, s;
        float xs, ys, zs;
        float wx, wy, wz;
        float xx, xy, xz;
        float yy, yz, zz;

        n = (q1.x * q1.x) + (q1.y * q1.y) + (q1.z * q1.z) + (q1.w * q1.w);
        s = (n > 0.0f) ? (2.0f / n) : 0.0f;

        xs = q1.x * s;
        ys = q1.y * s;
        zs = q1.z * s;
        wx = q1.w * xs;
        wy = q1.w * ys;
        wz = q1.w * zs;
        xx = q1.x * xs;
        xy = q1.x * ys;
        xz = q1.x * zs;
        yy = q1.y * ys;
        yz = q1.y * zs;
        zz = q1.z * zs;

        M00 = 1.0f - (yy + zz);
        M01 = xy - wz;
        M02 = xz + wy;
        M03 = 0f;
        M10 = xy + wz;
        M11 = 1.0f - (xx + zz);
        M12 = yz - wx;
        M13 = 0f;
        M20 = xz - wy;
        M21 = yz + wx;
        M22 = 1.0f - (xx + yy);
        M23 = 0f;
        M30 = 0f;
        M31 = 0f;
        M32 = 0f;
        M33 = 1f;
    }

    public final void set(Matrix4f m1) {
        M00 = m1.M00; M01 = m1.M01; M02 = m1.M02; M03 = m1.M03;
        M10 = m1.M10; M11 = m1.M11; M12 = m1.M12; M13 = m1.M13;
        M20 = m1.M20; M21 = m1.M21; M22 = m1.M22; M23 = m1.M23;
        M30 = m1.M30; M31 = m1.M31; M32 = m1.M32; M33 = m1.M33;
    }

    /**
     * Sets the value of this matrix to the result of multiplying
     * the two argument matrices together.
     *
     * @param m1 the first matrix
     * @param m2 the second matrix
     */
    public final void mul(Matrix4f m1, Matrix4f m2) {
        // alias-safe way.
        set(
                m1.M00 * m2.M00 + m1.M01 * m2.M10 + m1.M02 * m2.M20 + m1.M03 * m2.M30,
                m1.M00 * m2.M01 + m1.M01 * m2.M11 + m1.M02 * m2.M21 + m1.M03 * m2.M31,
                m1.M00 * m2.M02 + m1.M01 * m2.M12 + m1.M02 * m2.M22 + m1.M03 * m2.M32,
                m1.M00 * m2.M03 + m1.M01 * m2.M13 + m1.M02 * m2.M23 + m1.M03 * m2.M33,

                m1.M10 * m2.M00 + m1.M11 * m2.M10 + m1.M12 * m2.M20 + m1.M13 * m2.M30,
                m1.M10 * m2.M01 + m1.M11 * m2.M11 + m1.M12 * m2.M21 + m1.M13 * m2.M31,
                m1.M10 * m2.M02 + m1.M11 * m2.M12 + m1.M12 * m2.M22 + m1.M13 * m2.M32,
                m1.M10 * m2.M03 + m1.M11 * m2.M13 + m1.M12 * m2.M23 + m1.M13 * m2.M33,

                m1.M20 * m2.M00 + m1.M21 * m2.M10 + m1.M22 * m2.M20 + m1.M23 * m2.M30,
                m1.M20 * m2.M01 + m1.M21 * m2.M11 + m1.M22 * m2.M21 + m1.M23 * m2.M31,
                m1.M20 * m2.M02 + m1.M21 * m2.M12 + m1.M22 * m2.M22 + m1.M23 * m2.M32,
                m1.M20 * m2.M03 + m1.M21 * m2.M13 + m1.M22 * m2.M23 + m1.M23 * m2.M33,

                m1.M30 * m2.M00 + m1.M31 * m2.M10 + m1.M32 * m2.M20 + m1.M33 * m2.M30,
                m1.M30 * m2.M01 + m1.M31 * m2.M11 + m1.M32 * m2.M21 + m1.M33 * m2.M31,
                m1.M30 * m2.M02 + m1.M31 * m2.M12 + m1.M32 * m2.M22 + m1.M33 * m2.M32,
                m1.M30 * m2.M03 + m1.M31 * m2.M13 + m1.M32 * m2.M23 + m1.M33 * m2.M33
        );
    }

    /**
     * Sets 16 values
     */
    private void set(float m00, float m01, float m02, float m03,
                     float m10, float m11, float m12, float m13,
                     float m20, float m21, float m22, float m23,
                     float m30, float m31, float m32, float m33) {
        this.M00 = m00;
        this.M01 = m01;
        this.M02 = m02;
        this.M03 = m03;
        this.M10 = m10;
        this.M11 = m11;
        this.M12 = m12;
        this.M13 = m13;
        this.M20 = m20;
        this.M21 = m21;
        this.M22 = m22;
        this.M23 = m23;
        this.M30 = m30;
        this.M31 = m31;
        this.M32 = m32;
        this.M33 = m33;
    }
}
