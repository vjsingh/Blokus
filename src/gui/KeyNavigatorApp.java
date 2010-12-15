package gui;

/*
 *      @(#)KeyNavigatorApp.java 1.1 00/09/22 16:24
 *
 * Copyright (c) 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import com.sun.j3d.utils.applet.MainFrame; 
import com.sun.j3d.utils.geometry.ColorCube;
import com.sun.j3d.utils.universe.*;
import javax.media.j3d.*;
import javax.vecmath.*;

import java.awt.event.*;
import java.awt.AWTEvent;
import java.util.Enumeration;
import com.sun.j3d.utils.behaviors.keyboard.*;

//   KeyNavigatorApp renders a simple landscape

public class KeyNavigatorApp extends Applet {

    Shape3D createPyramid(){
        IndexedTriangleArray pyGeom =
                new IndexedTriangleArray(5,  GeometryArray.COORDINATES
                                           | GeometryArray.COLOR_3
                                          , 12);

        pyGeom.setCoordinate(0, new Point3f(  0.0f, 0.7f,  0.0f ));
        pyGeom.setCoordinate(1, new Point3f( -0.4f, 0.0f, -0.4f ));
        pyGeom.setCoordinate(2, new Point3f( -0.4f, 0.0f,  0.4f ));
        pyGeom.setCoordinate(3, new Point3f(  0.4f, 0.0f,  0.4f ));
        pyGeom.setCoordinate(4, new Point3f(  0.4f, 0.0f, -0.4f ));

        pyGeom.setCoordinateIndex( 0, 0);
        pyGeom.setCoordinateIndex( 1, 1);
        pyGeom.setCoordinateIndex( 2, 2);
        pyGeom.setCoordinateIndex( 3, 0);
        pyGeom.setCoordinateIndex( 4, 2);
        pyGeom.setCoordinateIndex( 5, 3);
        pyGeom.setCoordinateIndex( 6, 0);
        pyGeom.setCoordinateIndex( 7, 3);
        pyGeom.setCoordinateIndex( 8, 4);
        pyGeom.setCoordinateIndex( 9, 0);
        pyGeom.setCoordinateIndex(10, 4);
        pyGeom.setCoordinateIndex(11, 1);

        Color3f c = new Color3f(0.6f, 0.5f, 0.55f);
        pyGeom.setColor(0, c);
        pyGeom.setColor(1, c);
        pyGeom.setColor(2, c);
        pyGeom.setColor(3, c);
        pyGeom.setColor(4, c);

        Shape3D pyramid = new Shape3D(pyGeom);
        return pyramid;
    }

    Shape3D createLand(){
        LineArray landGeom = new LineArray(44, GeometryArray.COORDINATES
                                            | GeometryArray.COLOR_3);
        float l = -50.0f;
        for(int c = 0; c < 44; c+=4){

            landGeom.setCoordinate( c+0, new Point3f( -50.0f, 0.0f,  l ));
            landGeom.setCoordinate( c+1, new Point3f(  50.0f, 0.0f,  l ));
            landGeom.setCoordinate( c+2, new Point3f(   l   , 0.0f, -50.0f ));
            landGeom.setCoordinate( c+3, new Point3f(   l   , 0.0f,  50.0f ));
            l += 10.0f;
        }

        Color3f c = new Color3f(0.1f, 0.8f, 0.1f);
        for(int i = 0; i < 44; i++) landGeom.setColor( i, c);

        return new Shape3D(landGeom);
    }

    public BranchGroup createSceneGraph(SimpleUniverse su) {
	// Create the root of the branch graph
        TransformGroup vpTrans = null;

        BranchGroup objRoot = new BranchGroup();

        Vector3f translate = new Vector3f();
        Transform3D T3D = new Transform3D();
        TransformGroup TG = null;

        objRoot.addChild(createLand());

        SharedGroup share = new SharedGroup();
        share.addChild(createPyramid());

        float[][] position = {{  0.0f, 0.0f,  -3.0f},
                              {  6.0f, 0.0f,   0.0f},
                              {  6.0f, 0.0f,   6.0f},
                              {  3.0f, 0.0f, -10.0f},
                              { 13.0f, 0.0f, -30.0f},
                              {-13.0f, 0.0f,  30.0f},
                              {-13.0f, 0.0f,  23.0f},
                              { 13.0f, 0.0f,   3.0f}};

        for (int i = 0; i < position.length; i++){
                translate.set(position[i]);
                T3D.setTranslation(translate);
                TG = new TransformGroup(T3D);
                TG.addChild(new Link(share));
                objRoot.addChild(TG);
        }
        vpTrans = su.getViewingPlatform().getViewPlatformTransform();
        translate.set( 0.0f, 0.3f, 0.0f);
        T3D.setTranslation(translate);
        vpTrans.setTransform(T3D);
        KeyNavigatorBehavior keyNavBeh = new KeyNavigatorBehavior(vpTrans);
        keyNavBeh.setSchedulingBounds(new BoundingSphere(new Point3d(),1000.0));
        objRoot.addChild(keyNavBeh);

	// Let Java 3D perform optimizations on this scene graph.
        objRoot.compile();

        return objRoot;
    } // end of CreateSceneGraph method of KeyNavigatorApp

    public KeyNavigatorApp() {
        setLayout(new BorderLayout());
        GraphicsConfiguration config =
           SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas3D = new Canvas3D(config);
        add("Center", canvas3D);

        // SimpleUniverse is a Convenience Utility class
        SimpleUniverse simpleU = new SimpleUniverse(canvas3D);

        BranchGroup scene = createSceneGraph(simpleU);

        simpleU.addBranchGraph(scene);
    } // end of KeyNavigatorApp (constructor)


    //  The following allows this to be run as an application
    //  as well as an applet

    public static void main(String[] args) {
        System.out.print("KeyNavigatorApp.java \n- a demonstration of the KeyNavigatorBehavior ");
        System.out.println("class to provide keyboard navigation in a Java 3D scene.");
        System.out.println("When the app loads, you can use the arrow keys to move.");
        System.out.println("This is an example progam from The Java 3D API Tutorial.");
        System.out.println("The Java 3D Tutorial is available on the web at:");
        System.out.println("http://java.sun.com/products/java-media/3D/collateral");
        Frame frame = new MainFrame(new KeyNavigatorApp(), 256, 256);
    } // end of main (method of KeyNavigatorApp)

} // end of class KeyNavigatorApp
