/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AltTestGames;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import mygame.Main;

/**
 *
 * @author kruno
 */
public class ScreenState extends  AbstractAppState implements ScreenController {
       
    PhysicsOne app;
    Nifty nifty;
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        //TODO: initialize your AppState, e.g. attach spatials to rootNode
        //this is called on the OpenGL thread after the AppState has been attached
        this.app = (PhysicsOne) app;
        this.nifty = this.app.getNifty();
    }
    
    @Override
    public void update(float tpf) {
        //TODO: implement behavior during runtime
        if (this.app.isPause()){
            nifty.gotoScreen("options");
            enableOptionScreenControl();
        }
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        //TODO: clean up what you initialized in the initialize method,
        //e.g. remove all spatials from rootNode
        //this is called on the OpenGL thread after the AppState has been detached
    }

    public void bind(Nifty nifty, Screen screen) {
        System.out.println("bind( " + screen.getScreenId() + ")");

    }

    public void onStartScreen() {
        System.out.println("onStartScreen");

    }

    public void onEndScreen() {
        //
        System.out.println("onEndScreen");

    }
    
    /** custom methods */ 
  public void startGame(String nextScreen) {
      System.out.println("changeScreen");
      enableInGameControls();
      nifty.gotoScreen(nextScreen);  // switch to another screen
    // start the game and do some more stuff...
  }
  
  public void quitGame(){
      app.stop();
  }
  
  public void resumeGame(){
      System.out.println("resumeGame");
      nifty.gotoScreen("hud");
      enableInGameControls();
      
  }
  
  private void enableInGameControls(){
        this.app.getFlyByCamera().setDragToRotate(true);
        this.app.getFlyByCamera().setEnabled(false);
        this.app.getCamNode().setEnabled(true);
  }
  
  private void enableOptionScreenControl(){
          this.app.getCamNode().setEnabled(false);
          this.app.getFlyByCamera().setEnabled(true);
          this.app.getFlyByCamera().setDragToRotate(true);

  }
  
  
}
