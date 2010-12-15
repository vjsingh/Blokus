/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;
import java.util.Enumeration;
import javax.media.j3d.Behavior;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.Transform3D;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;



/**
 *
 * @author ahills
 */
public class GraphicsTests {

    public class behav extends Behavior{
        public behav(TransformGroup mover){}
        public void initialize(){}
        public void processStimulus(Enumeration criteria){}
    }


    public static void main(String[] args) {
  new GraphicsTests();
  }

  public GraphicsTests() {
      System.out.println(System.getProperty("java.class.path"));
create();
  //cube creation
  }

  Transform3D trans=new Transform3D();
  BoundingSphere bounds = new BoundingSphere(new Point3d(0.,0.,0.),100.);

  public TransformGroup cubeRotate() {
return new TransformGroup(trans);
}

  private void create(){
          SimpleUniverse universe=new SimpleUniverse();
  	//create a branchgroyp
  	BranchGroup group = new BranchGroup();
        TransformGroup mover=cubeRotate();
        group.addChild(mover);

  	//create a box
  	Box myCube = new Box(0.4f, 0.4f, 0.4f, null);

  	// add the box to the branchgroup
  	group.addChild(myCube);

  	// ---LIGHT
  	// create the light color
  	Color3f light1Color = new Color3f(1.5f, 1.5f, 1.5f);

    //the light direction
    Vector3f light1Direction = new Vector3f(4.0f, -7.0f, -12.0f);

    //add the color and direction to the light
    DirectionalLight light1 = new DirectionalLight(light1Color, light1Direction);

    //add the light in the bounding sphere
    light1.setInfluencingBounds(bounds);

    //add the light to the BranchGroup
    group.addChild(light1);
    // --- END LIGHT

    mover.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
    mover.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);

    MouseRotate rota=new MouseRotate();
    rota.setTransformGroup(mover);
    group.addChild(rota);
    rota.setSchedulingBounds(bounds);


    //look toward the scene and add the branch group
    universe.getViewingPlatform().setNominalViewingTransform();
    universe.addBranchGraph(group);

  }

}
