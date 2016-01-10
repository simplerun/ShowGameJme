/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.effect.ParticleEmitter;

/**
 *
 * @author kruno
 */
public class TimedExplosion {
 
    
    private ParticleEmitter explosion;
    private float timeOut = 0;

    public TimedExplosion(ParticleEmitter emitter,float timrOut){
        this.explosion = emitter;
        this.timeOut = timrOut;
    }
    
    /**
     * @return the explosion
     */
    public ParticleEmitter getExplosion() {
        return explosion;
    }

    /**
     * @param explosion the explosion to set
     */
    public void setExplosion(ParticleEmitter explosion) {
        this.explosion = explosion;
    }

    /**
     * @return the timeOut
     */
    public float getTimeOut() {
        return timeOut;
    }

    /**
     * @param timeOut the timeOut to set
     */
    public void setTimeOut(float timeOut) {
        this.timeOut = timeOut;
    }
}
