/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.shapes;

import com.jme3.bullet.collision.shapes.SphereCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 *
 * @author kruno
 */
public class SimpleArrow extends   Node{
    
    private Geometry body;
    private RigidBodyControl bodyControl;
    
    public SimpleArrow(Vector3f location, Vector3f velocity){
        setName("arrow");
        Box arrowBody = new Box(0.3f, 4f, 0.3f);
         body = new Geometry("bullet", arrowBody);
        body.setLocalTranslation(0f, -4f, 0f);
        attachChild(body);
        this.setLocalTranslation(location);
        SphereCollisionShape arrowHeadCollision = new SphereCollisionShape(0.5f);
        bodyControl = new RigidBodyControl(arrowHeadCollision, 1f);
        bodyControl.setLinearVelocity(velocity);
        addControl(bodyControl);
        addControl(new ArrowFacingControl());
        
    }

    /**
     * @return the body
     */
    public Geometry getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(Geometry body) {
        this.body = body;
    }

    /**
     * @return the bodyControl
     */
    public RigidBodyControl getBodyControl() {
        return bodyControl;
    }

    /**
     * @param bodyControl the bodyControl to set
     */
    public void setBodyControl(RigidBodyControl bodyControl) {
        this.bodyControl = bodyControl;
    }
}

