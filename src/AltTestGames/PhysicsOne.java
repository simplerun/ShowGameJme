/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AltTestGames;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl;
import com.jme3.scene.shape.Sphere;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import mygame.TimedExplosion;
import mygame.shapes.SimpleArrow;

/**
 *
 * @author kruno
 */
public class PhysicsOne extends SimpleApplication implements ActionListener, AnalogListener, PhysicsCollisionListener {

    public static final float BETWEEN_SHOOT_PAUSE = 0.5f;
    private float elapsedTimeFromlastShoot = 0f;
    
    private Node sceneNode;
    private BulletAppState bulletAppState;
    private RigidBodyControl scenePhy;
    private RigidBodyControl ballPhy;
    private Node playerNode;
    private BetterCharacterControl playerControl;
    private CameraNode camNode;
    private Vector3f walkingDir = new Vector3f(0, 0, 0);
    private Vector3f viewDir = new Vector3f(0, 0, 1);
    private boolean rotateLeft = false,
                    rotateRight = false,
                    forward = false,
                    back = false,
                    jump = false,
                    fire = false,
                    pause = false;
    private Nifty nifty;
    
    private float speed = 20;
    
    private ParticleEmitter debrisEffect;
    private List<TimedExplosion> explosions = new ArrayList<TimedExplosion>();
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        PhysicsOne app = new PhysicsOne();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
        
        flyCam.setMoveSpeed(30);
        
        viewPort.setBackgroundColor(ColorRGBA.Cyan);
        
         ScreenState ssc = new ScreenState();
        stateManager.attach(ssc);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/newNiftyGui.xml", "start",ssc);
        guiViewPort.addProcessor(niftyDisplay);
        flyCam.setDragToRotate(true);
        flyCam.setEnabled(false);
        
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        

        
        
        assetManager.registerLocator("town.zip", ZipLocator.class);
        sceneNode = (Node) assetManager.loadModel("main.scene");
        sceneNode.scale(1.5f);
           scenePhy = new RigidBodyControl(0f);
        sceneNode.addControl(scenePhy);
        bulletAppState.getPhysicsSpace().add(scenePhy);
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f, -9.81f, 0f));
        bulletAppState.getPhysicsSpace().addCollisionListener(this);

        rootNode.attachChild(sceneNode);
        
        
        
        AmbientLight ambientLight = new AmbientLight();
        rootNode.addLight(ambientLight);
        
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.4f,-1.4f,-1.4f));
        rootNode.addLight(sun);
        
        playerNode = new Node("player");
        playerNode.setLocalTranslation(new Vector3f(0, 6, 0));
        rootNode.attachChild(playerNode);
        playerControl = new BetterCharacterControl(1.5f, 4, 30f);
        playerControl.setJumpForce(new Vector3f(0, 300, 0));
        playerControl.setGravity(new Vector3f(0, -10, 0));
        playerNode.addControl(playerControl);
        bulletAppState.getPhysicsSpace().add(playerControl);
        
        setCamNode(new CameraNode("camNode", cam));
        getCamNode().setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        getCamNode().setLocalTranslation(new Vector3f(0, 4, -6));
        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        getCamNode().setLocalRotation(quat);
        playerNode.attachChild(getCamNode());
        getCamNode().setEnabled(true);
        
        inputManager.deleteMapping(INPUT_MAPPING_EXIT);
        inputManager.addMapping("pause", new KeyTrigger(keyInput.KEY_ESCAPE));
        inputManager.addMapping("forward", new KeyTrigger(keyInput.KEY_W));
        inputManager.addMapping("back", new KeyTrigger(keyInput.KEY_S));
        inputManager.addMapping("rotate left", new KeyTrigger(keyInput.KEY_A));
        inputManager.addMapping("rotate right", new KeyTrigger(keyInput.KEY_D));
        inputManager.addMapping("jump", new KeyTrigger(keyInput.KEY_SPACE));
        inputManager.addListener(this, "pause");
        inputManager.addListener(this, "rotate left","rotate right");
        inputManager.addListener(this, "forward","back","jump");
        
        inputManager.addMapping("fire", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, "fire");
        
   //     bulletAppState.setDebugEnabled(true);
        
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        System.out.println("pressed: "+isPressed);
        if (name.equals("rotate left")){
            rotateLeft = isPressed;
        } else if (name.equals("rotate right")){
            rotateRight = isPressed;
        } else if (name.equals("forward")){
            forward = isPressed;
        } else if (name.equals("back")){
            back = isPressed;
        } else if (name.equals("jump")){
            playerControl.jump();
        } else if (name.equals("fire")){
            fire = isPressed;
        } else if (name.equals("pause")){
            setPause(isPressed);
        }
    }
    
    @Override
    public void simpleUpdate(float tpf){
        
        Iterator<TimedExplosion> rok = explosions.iterator();
        while (rok.hasNext()) {
            TimedExplosion riba = rok.next();
            riba.setTimeOut(riba.getTimeOut()-tpf);
            
            if (riba.getTimeOut() <= 0f){
                riba.getExplosion().killAllParticles();
                riba.getExplosion().removeFromParent();
                riba.setExplosion(null);
                rok.remove();
                
            }
        }
        
        Vector3f modelForwardDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_Z);
        Vector3f modelLeftDir = playerNode.getWorldRotation().mult(Vector3f.UNIT_X);
         walkingDir.set(0, 0, 0);
        if (forward){
            walkingDir.addLocal(modelForwardDir.mult(speed));
        } else if (back){
            walkingDir.addLocal(modelForwardDir.mult(speed).negate());
        } else {
        }
        playerControl.setWalkDirection(walkingDir);

        if (rotateLeft){
            Quaternion rotateL = new Quaternion().fromAngleAxis(FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateL.multLocal(viewDir);
        } else if (rotateRight){
            Quaternion rotateR = new Quaternion().fromAngleAxis(-FastMath.PI * tpf, Vector3f.UNIT_Y);
            rotateR.multLocal(viewDir);
        }
        playerControl.setViewDirection(viewDir);
        
        if (fire){
            if (elapsedTimeFromlastShoot == 0f){
              //  shootBall();
                shootArrow(cam.getLocation().add(cam.getDirection().mult(10)),cam.getDirection().mult(50).add(Vector3f.UNIT_Y.mult(20)));
                elapsedTimeFromlastShoot += tpf;
            } else {
            elapsedTimeFromlastShoot += tpf;
            if (elapsedTimeFromlastShoot > 0.2f){
                elapsedTimeFromlastShoot = 0f;
            }
        } 
        } else if (!fire){
            elapsedTimeFromlastShoot = 0f;
        }
    }

    public void onAnalog(String name, float value, float tpf) {
       
    
    }
    
    private void shootBall(){
        Sphere ball = new Sphere(32, 32, 0.25f, true, false);
        Geometry ballGeo = new Geometry("ball", ball);
        Material stoneMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        stoneMat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/Pebbles/Pebbles_diffuse.png"));
        ballGeo.setLocalTranslation(cam.getLocation().add(cam.getDirection().mult(10)));
        ballGeo.setMaterial(stoneMat);
        rootNode.attachChild(ballGeo);
        
        ballPhy = new RigidBodyControl(5f);
        ballGeo.addControl(ballPhy);
        ballPhy.setCcdSweptSphereRadius(0.1f);
        ballPhy.setCcdMotionThreshold(0.001f);
        ballPhy.setLinearVelocity(cam.getDirection().mult(100));
        ballPhy.setFriction(100f);
       
        bulletAppState.getPhysicsSpace().add(ballPhy);
        
    }
    
    private void shootArrow(Vector3f location,Vector3f velocity){
        
        SimpleArrow arrow = new SimpleArrow(location, velocity);
       Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.LightGray);
        arrow.getBody().setMaterial(mat);
        rootNode.attachChild(arrow);
        bulletAppState.getPhysicsSpace().add(arrow.getBodyControl());
        
    }
    


    public void collision(PhysicsCollisionEvent event) {
 //       System.out.println("A:"+event.getNodeA().getName()+",B:"+event.getNodeB().getName());
        if ((event.getNodeA().getName().equals("ball")) || (event.getNodeB().getName().equals("ball"))){
  //          System.out.println("BUUM");
            Spatial explosionOrigin = null;
            if (event.getNodeA().getName().equals("ball")){
                explosionOrigin = event.getNodeA();
          //      rootNode.detachChild(explosionOrigin);
                System.out.println("A:"+event.getNodeA().getName());
            } else if (event.getNodeB().getName().equals("ball")){
                explosionOrigin = event.getNodeB();
            //    rootNode.detachChild(explosionOrigin);
                
                System.out.println("B:"+event.getNodeB().getName());
            }
            
            explosionBall(explosionOrigin);
            RigidBodyControl phyControl = explosionOrigin.getControl(RigidBodyControl.class);
            bulletAppState.getPhysicsSpace().remove(phyControl);
            explosionOrigin.removeFromParent();

            

            
        } 
        
         if ((event.getNodeA().getName().equals("arrow")) || (event.getNodeB().getName().equals("arrow"))){
  //          System.out.println("BUUM");
            Spatial explosionOrigin = null;
            if (event.getNodeA().getName().equals("arrow")){
                explosionOrigin = event.getNodeA();
          //      rootNode.detachChild(explosionOrigin);
                System.out.println("A:"+event.getNodeA().getName());
            } else if (event.getNodeB().getName().equals("arrow")){
                explosionOrigin = event.getNodeB();
            //    rootNode.detachChild(explosionOrigin);
                
                System.out.println("B:"+event.getNodeB().getName());
            }
            
            explosionBall(explosionOrigin);
            RigidBodyControl phyControl = explosionOrigin.getControl(RigidBodyControl.class);
            bulletAppState.getPhysicsSpace().remove(phyControl);
            explosionOrigin.removeFromParent();

            

            
        } 
    }
    
    private void explosionBall(Spatial explosiveOrigin){
            /** Explosion effect. Uses Texture from jme3-test-data library! */ 
    debrisEffect = new ParticleEmitter("Debris", ParticleMesh.Type.Triangle, 10);
    Material debrisMat = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
    debrisMat.setTexture("Texture", assetManager.loadTexture("Textures/Effects/flash.png"));
    debrisEffect.setMaterial(debrisMat);
    debrisEffect.setImagesX(2);
        debrisEffect.setImagesY(2);
        debrisEffect.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5, 0));
        debrisEffect.getParticleInfluencer().setVelocityVariation(1f);
        debrisEffect.setStartColor(new ColorRGBA(1.0f,0.8f,0.36f,1.0f));
        debrisEffect.setEndColor(new ColorRGBA(1f, 0.8f, 0.36f, 0f));
        debrisEffect.setGravity(new Vector3f(0, 0, 0));
      //  burst.setFacingVelocity(true);
        debrisEffect.setStartSize(.1f);
        debrisEffect.setEndSize(5f);
        debrisEffect.setLowLife(.2f);
        debrisEffect.setHighLife(0.2f);
        debrisEffect.setShape(new EmitterSphereShape(Vector3f.ZERO, .5f));

        debrisEffect.setLocalTranslation(explosiveOrigin.getWorldTranslation());

    rootNode.attachChild(debrisEffect);
    debrisEffect.emitAllParticles();
    TimedExplosion ex = new TimedExplosion(debrisEffect, 0.2f);
    explosions.add(ex);

    }
    
     /**
     * @return the nifty
     */
    public Nifty getNifty() {
        return nifty;
    }

    /**
     * @param nifty the nifty to set
     */
    public void setNifty(Nifty nifty) {
        this.nifty = nifty;
    }

    /**
     * @return the pause
     */
    public boolean isPause() {
        return pause;
    }

    /**
     * @param pause the pause to set
     */
    public void setPause(boolean pause) {
        this.pause = pause;
    }

    /**
     * @return the camNode
     */
    public CameraNode getCamNode() {
        return camNode;
    }

    /**
     * @param camNode the camNode to set
     */
    public void setCamNode(CameraNode camNode) {
        this.camNode = camNode;
    }

    
}
