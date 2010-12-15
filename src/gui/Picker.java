/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import com.sun.j3d.utils.behaviors.mouse.MouseBehaviorCallback;
import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.pickfast.PickTool;
import com.sun.j3d.utils.pickfast.behaviors.PickMouseBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickingCallback;
import javax.media.j3d.Bounds;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

/**
 *
 * @author ahills
 */
/**
 * A mouse behavior that allows user to pick and rotate scene graph objects.
 * Common usage:
 * <p>
 * 1. Create your scene graph.
 * <p>
 * 2. Create this behavior with root and canvas.
 * <p>
 * <blockquote><pre>
 *	PickRotateBehavior behavior = new PickRotateBehavior(canvas, root, bounds);
 *      root.addChild(behavior);
 * </pre></blockquote>
 * <p>
 * The above behavior will monitor for any picking events on
 * the scene graph (below root node) and handle mouse rotates on pick hits.
 * Note the root node can also be a subgraph node of the scene graph (rather
 * than the topmost).
 */
public class Picker extends PickMouseBehavior implements MouseBehaviorCallback {

    MouseRotate rotate;
    private PickingCallback callback = null;
    private TransformGroup currentTG;

    /**
    082:             * Creates a pick/rotate behavior that waits for user mouse events for
    083:             * the scene graph. This method has its pickMode set to BOUNDS picking.
    084:             * @param root   Root of your scene graph.
    085:             * @param canvas Java 3D drawing canvas.
    086:             * @param bounds Bounds of your scene.
    087:             **/
    public Picker(BranchGroup root, Canvas3D canvas,
            Bounds bounds) {
        super(canvas, root, bounds);
        rotate = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
        rotate.setTransformGroup(currGrp);
        currGrp.addChild(rotate);
        rotate.setSchedulingBounds(bounds);
        this.setSchedulingBounds(bounds);
        this.setMode(PickInfo.PICK_GEOMETRY);
    }

    /**
    100:             * Creates a pick/rotate behavior that waits for user mouse events for
    101:             * the scene graph.
    102:             * @param root   Root of your scene graph.
    103:             * @param canvas Java 3D drawing canvas.
    104:             * @param bounds Bounds of your scene.
    105:             * @param pickMode specifys PickTool.PICK_BOUNDS or PickTool.PICK_GEOMETRY.
    106:             * @see PickTool#setMode
    107:             **/
    public Picker(BranchGroup root, Canvas3D canvas,
            Bounds bounds, int pickMode) {
        super(canvas, root, bounds);
        rotate = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
        rotate.setTransformGroup(currGrp);
        currGrp.addChild(rotate);
        rotate.setSchedulingBounds(bounds);
        this.setSchedulingBounds(bounds);
        this.setMode(pickMode);
    }

    /**
    121:             * Update the scene to manipulate any nodes. This is not meant to be
    122:             * called by users. Behavior automatically calls this. You can call
    123:             * this only if you know what you are doing.
    124:             *
    125:             * @param xpos Current mouse X pos.
    126:             * @param ypos Current mouse Y pos.
    127:             **/
    public void updateScene(int xpos, int ypos) {
        TransformGroup tg = null;

        if (!mevent.isMetaDown() && !mevent.isAltDown()) {

            pickCanvas.setFlags(PickInfo.NODE | PickInfo.SCENEGRAPHPATH);

            pickCanvas.setShapeLocation(xpos, ypos);
            PickInfo pickInfo = pickCanvas.pickClosest();
            if (pickInfo != null) {
                tg = (TransformGroup) pickCanvas.getNode(pickInfo,
                        PickTool.TYPE_TRANSFORM_GROUP);
                if ((tg != null)
                        && (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_READ))
                        && (tg.getCapability(TransformGroup.ALLOW_TRANSFORM_WRITE))) {
                    rotate.setTransformGroup(tg);
                    rotate.wakeup();
                    currentTG = tg;
                }
            } else if (callback != null) {
                callback.transformChanged(PickingCallback.NO_PICK, null);
            }
        }
    }

    /**
     * Callback method from MouseRotate
     * This is used when the Picking callback is enabled
     */
    public void transformChanged(int type, Transform3D transform) {
        callback.transformChanged(PickingCallback.ROTATE, currentTG);
    }

    /**
    167:             * Register the class @param callback to be called each
    168:             * time the picked object moves
    169:             */
    public void setupCallback(PickingCallback callback) {
        this.callback = callback;
        if (callback == null) {
            rotate.setupCallback(null);
        } else {
            rotate.setupCallback(this);
        }
    }
}
