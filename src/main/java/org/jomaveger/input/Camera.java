package org.jomaveger.input;

import org.lwjgl.input.*;
import org.jomaveger.math.Vector;
import org.jomaveger.model.bsp.Quake3BSP;
import static org.lwjgl.util.glu.GLU.*;


/**
 * @author jmvegas.gertrudix
 */
public final class Camera {

    /**
     * A zero vector. *
     */
    private Vector vZero = new Vector(0.0f, 0.0f, 0.0f);

    /**
     * The view vector. *
     */
    private Vector vView = new Vector(0.0f, 1.0f, 0.5f);

    /**
     * Up vector (rarely changed). *
     */
    private Vector vUp = new Vector(0.0f, 0.0f, 1.0f);

    /**
     * Strafe vector. *
     */
    private Vector m_vStrafe = new Vector();

    /**
     * Position vector. *
     */
    private Vector m_vPosition = vZero;

    /**
     * View Vector. *
     */
    private Vector m_vView = vView;

    /**
     * up vector. *
     */
    private Vector m_vUpVector = vUp;

    /**
     * View by mouse ? Default = false. *
     */
    private boolean viewByMouse = false;

    /**
     * Is mouse inverted ? Default = false. *
     */
    private boolean mouseInverted = false;

    /**
     * Mouse sensibility. Default = 40.0f. *
     */
    private float mouseSensibility = 40.0f;

    /**
     * Current mouse rotation on X axis. *
     */
    private float currentRotX = 0.0f;

    /**
     * Fixed on X axis ? *
     */
    private boolean fixedX = false;

    /**
     * Fixed on Y axis ? *
     */
    private boolean fixedY = false;

    /**
     * Fixed on Z axis ? *
     */
    private boolean fixedZ = false;

    private float radius;

    private Vector pr;
    /* Point to rotate about   */
    
    private float focallength;
    /* Focal Length along vd   */
    
    private float aperture;
    /* Camera aperture         */
    
    private float eyesep;
    /* Eye separation          */

    public final static int X_AXIS = 0;
    public final static int Y_AXIS = 1;
    public final static int Z_AXIS = 2;
    
    public Vector g_vVelocity = new Vector(0, 0, 0);

    public Camera() {
    }

    /**
     * Camera constructor.
     *
     * @param vbm View by mouse statement.
     */
    public Camera(boolean vbm) {
        viewByMouse = vbm;
    }

    /**
     * Change the view by mouse statement.
     *
     * @param vbm View by mouse statement.
     */
    public final void setViewByMouse(boolean vbm) {
        viewByMouse = vbm;
    }

    public final boolean getViewByMouse() {
        return viewByMouse;
    }

    /**
     * Change mouse Y movement type.
     *
     * @param inverted Mouse Inverted ?
     */
    public final void setMouseInverted(boolean inverted) {
        mouseInverted = inverted;
    }

    public Vector getVview() {
        return m_vView;
    }

    /**
     * Change mouse sensibility.
     *
     * @param s Mouse sensibility (high = less sensible).
     */
    public final void setMouseSensibility(float s) {
        mouseSensibility = s;
    }

    public final void setVview(float y) {
        m_vPosition.y = 0;
    }

    /**
     * Set camera fixed to an axis.
     *
     * @param axis <code>Camera.X_AXIS</code> or <code>Camera.Y_AXIS</code> or
     * <code>Camera.Z_AXIS</code>.
     */
    public final void setFixedAxis(int axis) {
        if (axis == X_AXIS) {
            fixedX = true;
        } else if (axis == Y_AXIS) {
            fixedY = true;
        } else if (axis == Z_AXIS) {
            fixedZ = true;
        }
    }

    /**
     * Change the camera position.
     *
     * @param positionX Position X.
     * @param positionY Position Y.
     * @param positionZ Position Z.
     * @param viewX View X.
     * @param viewY View Y.
     * @param viewZ View Z.
     * @param upVectorX Up vector X.
     * @param upVectorY Up vector Y.
     * @param upVectorZ Up vector Z.
     */
    public final void setPosition(float positionX, float positionY, float positionZ,
            float viewX, float viewY, float viewZ,
            float upVectorX, float upVectorY, float upVectorZ) {
        m_vPosition = new Vector(positionX, positionY, positionZ);
        m_vView = new Vector(viewX, viewY, viewZ);
        m_vUpVector = new Vector(upVectorX, upVectorY, upVectorZ);
    }

    public final Vector[] getPositionCamera() {
        Vector[] position = {m_vPosition, m_vView, m_vUpVector};
        return position;
    }

    public void setPosition(Vector m_vPosition) {
        this.m_vPosition.Set(m_vPosition);
    }

    public void setView(Vector m_vView) {
        this.m_vView.Set(m_vView);
    }

    public void setUpVector(Vector m_vUpVector) {
        this.m_vUpVector.Set(m_vUpVector);
    }

    public final Vector getPosition() {
        return m_vPosition;
    }

    public final Vector getView() {
        return m_vView;
    }

    public final Vector getUpVector() {
        return m_vUpVector;
    }

    /**
     * Move the camera (forward if speed is positive).
     *
     * @param speed The camera speed.
     */
    public final void move(float speed) {
        Vector vVector = new Vector(m_vView);
        Vector normalized = vVector.Subtract(m_vPosition).Normalize();
        
        // Fixed axis ?
        if (!fixedX) {
            m_vPosition.x += normalized.x * speed;
            m_vView.x += normalized.x * speed;
        }
        if (!fixedY) {
            m_vPosition.y += normalized.y * speed;
            m_vView.y += normalized.y * speed;
        }
        if (!fixedZ) {
            m_vPosition.z += normalized.z * speed;
            m_vView.z += normalized.z * speed;
        }
    }

    /**
     * Set the view according to the mouse position.
     */
    private void mouse_view() {
        float angleY = 0.0f;
        float angle_product = 0.0f;
        final Vector vAxis;

        // Get the direction the mouse moved in, but bring the number down to a reasonable amount.
        angleY = -(float) (Mouse.getDX()) / mouseSensibility;
        angle_product = (float) (Mouse.getDY()) / mouseSensibility;

        // If mouse is inverted, invert rotation on angle_product axis.
        if (mouseInverted) {
            angle_product = -angle_product;
        }

        // Here we keep track of the current rotation (for up and down) so that
        // we can restrict the camera from doing a full 360 loop.
        currentRotX -= angle_product;

        // If the current rotation (in radians) is greater than 1.0, we want to cap it.
        if (currentRotX > 90.0f) {
            currentRotX = 90.0f;
        } // Check if the rotation is below -1.0, if so we want to make sure it doesn't continue.
        else if (currentRotX < -90.0f) {
            currentRotX = -90.0f;
        } // Otherwise, we can rotate the view around our position.
        else {
            Vector vVector = new Vector(m_vView);
            vAxis = vVector.Subtract(m_vPosition).CrossProduct(m_vUpVector);
            Vector normalized = vAxis.Normalize();            

            // Rotate around our perpendicular axis and along the y-axis.
            rotateWithQuaternion(angle_product, normalized.x, normalized.y, normalized.z);
            rotateWithQuaternion(angleY, 0, 1, 0);
        }
    }

    private void rotateWithQuaternion(final float angleDir, final float xSpeed, final float ySpeed, final float zSpeed) {
        Vector qView = new Vector(m_vView.x - m_vPosition.x, m_vView.y - m_vPosition.y, m_vView.z - m_vPosition.z);
        Vector qNewView = qView.RotateByAxisAndAngle(angleDir, new Vector(xSpeed, ySpeed, zSpeed));
        
        // Update the view information by adding the position to the resulting vector.
        m_vView.x = m_vPosition.x + qNewView.x;
        m_vView.y = m_vPosition.y + qNewView.y;
        m_vView.z = m_vPosition.z + qNewView.z;
    }

    /**
     * Strafe the camera (left or right, depending on the speed).
     *
     * @param speed The camera moving speed.
     */
    public final void strafe(float speed) {
        // Add the strafe vector to our position.
        m_vPosition.x += m_vStrafe.x * speed;
        m_vPosition.z += m_vStrafe.z * speed;

        // Add the strafe vector to our view.
        m_vView.x += m_vStrafe.x * speed;
        m_vView.z += m_vStrafe.z * speed;
    }

    /**
     * Update the camera state.
     */
    public final void update() {
        // Normalize the strafe vector.
        Vector vVector = new Vector(m_vView);
        Vector vAxis = vVector.Subtract(m_vPosition).CrossProduct(m_vUpVector);
        m_vStrafe = vAxis.Normalize(); 

        // View by mouse if enabled.
        if (viewByMouse) {
            mouse_view();
        }
    }

    /**
     * Update camera view.
     */
    public final void look() {
        gluLookAt(m_vPosition.x, m_vPosition.y, m_vPosition.z,
                m_vView.x, m_vView.y, m_vView.z,
                m_vUpVector.x, m_vUpVector.y, m_vUpVector.z);
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }

    public void setPr(Vector pr) {
        this.pr = pr;
    }

    public Vector getPr() {
        return pr;
    }

    public void setFocallength(float focallength) {
        this.focallength = focallength;
    }

    public float getFocallength() {
        return focallength;
    }

    public void setAperture(float aperture) {
        this.aperture = aperture;
    }

    public float getAperture() {
        return aperture;
    }

    public void setEyesep(float eyesep) {
        this.eyesep = eyesep;
    }

    public float getEyesep() {
        return eyesep;
    }

    public Vector getG_vVelocity() {
        return g_vVelocity;
    }

    public void setG_vVelocity(Vector g_vVelocity) {
        this.g_vVelocity = g_vVelocity;
    }
}
