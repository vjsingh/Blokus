/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;
import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.media.j3d.WakeupOnBehaviorPost;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 *
 * This is an alternative mouserotate behavior that allows rotation based on pressing
 * the right mouse button instead of the left
 *
 * @author ahills
 */
public class MouseRot extends MouseBehavior {

        double x_angle, y_angle;
        double x_factor = 0.01;
        double y_factor = 0.01;
        private MouseBehaviorCallback callback = null;

        public MouseRot(TransformGroup transformGroup) {
            super(transformGroup);
        }

        public MouseRot() {
            super(0);
        }

        public MouseRot(int flags) {
            super(flags);
        }

        public MouseRot(Component c) {
            super(c, 0);
        }

        public MouseRot(Component c, TransformGroup transformGroup) {
            super(c, transformGroup);
        }

        public MouseRot(Component c, int flags) {
            super(c, flags);
        }

        public void initialize() {
            super.initialize();
            x_angle = 0;
            y_angle = 0;
            if ((flags & INVERT_INPUT) == INVERT_INPUT) {
                invert = true;
                x_factor *= -1;
                y_factor *= -1;
            }
        }

        public double getXFactor() {
            return x_factor;
        }

        public double getYFactor() {
            return y_factor;
        }

        public void setFactor(double factor) {
            x_factor = y_factor = factor;
        }

        public void setFactor(double xFactor, double yFactor) {
            x_factor = xFactor;
            y_factor = yFactor;
        }

        public void processStimulus(Enumeration criteria) {
            WakeupCriterion wakeup;
            AWTEvent[] events;
            MouseEvent evt;

            while (criteria.hasMoreElements()) {
                wakeup = (WakeupCriterion) criteria.nextElement();
                if (wakeup instanceof WakeupOnAWTEvent) {
                    events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();
                    if (events.length > 0) {
                        evt = (MouseEvent) events[events.length - 1];
                        doProcess(evt);
                    }
                } else if (wakeup instanceof WakeupOnBehaviorPost) {
                    while (true) {
                        // access to the queue must be synchronized
                        synchronized (mouseq) {
                            if (mouseq.isEmpty()) {
                                break;
                            }
                            evt = (MouseEvent) mouseq.remove(0);
                            // consolidate MOUSE_DRAG events
                            while ((evt.getID() == MouseEvent.MOUSE_DRAGGED)
                                    && !mouseq.isEmpty()
                                    && (((MouseEvent) mouseq.get(0)).getID() == MouseEvent.MOUSE_DRAGGED)) {
                                evt = (MouseEvent) mouseq.remove(0);
                            }
                        }
                        doProcess(evt);
                    }
                }

            }
            wakeupOn(mouseCriterion);
        }

        void doProcess(MouseEvent evt) {
            int id;
            int dx, dy;
            processMouseEvent(evt);
            if (((buttonPress) && ((flags & MANUAL_WAKEUP) == 0))
                    || ((wakeUp) && ((flags & MANUAL_WAKEUP) != 0))) {
                id = evt.getID();
                if ((id == MouseEvent.MOUSE_DRAGGED) && evt.isMetaDown()
                        && !evt.isAltDown()) {
                    x = evt.getX();
                    y = evt.getY();

                    dx = x - x_last;
                    dy = y - y_last;

                    if (!reset) {
                        x_angle = dy * y_factor;
                        y_angle = dx * x_factor;

                        transformX.rotX(x_angle);
                        transformY.rotY(y_angle);

                        transformGroup.getTransform(currXform);

                        Matrix4d mat = new Matrix4d();
// Remember old matrix
                        currXform.get(mat);

// Translate to origin
                        currXform.setTranslation(new Vector3d(0.0, 0.0, 0.0));
                        if (invert) {
                            currXform.mul(currXform, transformX);
                            currXform.mul(currXform, transformY);
                        } else {
                            currXform.mul(transformX, currXform);
                            currXform.mul(transformY, currXform);
                        }

// Set old translation back
                        Vector3d translation = new Vector3d(mat.m03,
                                mat.m13, mat.m23);
                        currXform.setTranslation(translation);

// Update xform
                        transformGroup.setTransform(currXform);

                        transformChanged(currXform);

                        if (callback != null) {
                            callback.transformChanged(
                                    MouseBehaviorCallback.ROTATE,
                                    currXform);
                        }
                    } else {
                        reset = false;
                    }

                    x_last = x;
                    y_last = y;
                } else if (id == MouseEvent.MOUSE_PRESSED) {
                    x_last = evt.getX();
                    y_last = evt.getY();
                }
            }
        }

        /**
         * Users can overload this method  which is called every time
         * the Behavior updates the transform
         *
         * Default implementation does nothing
         */
        public void transformChanged(Transform3D transform) {
        }

        /**
         * The transformChanged method in the callback class will
         * be called every time the transform is updated
         */
        public void setupCallback(MouseBehaviorCallback callback) {
            this.callback = callback;
        }
    }