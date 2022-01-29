/*******************************************************************************************************
 *
 * Profile.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.dynamics;

import java.util.List;

import org.jbox2d.common.MathUtils;

/**
 * The Class Profile.
 */
public class Profile {
  
  /** The Constant LONG_AVG_NUMS. */
  private static final int LONG_AVG_NUMS = 20;
  
  /** The Constant LONG_FRACTION. */
  private static final float LONG_FRACTION = 1f / LONG_AVG_NUMS;
  
  /** The Constant SHORT_AVG_NUMS. */
  private static final int SHORT_AVG_NUMS = 5;
  
  /** The Constant SHORT_FRACTION. */
  private static final float SHORT_FRACTION = 1f / SHORT_AVG_NUMS;

  /**
   * The Class ProfileEntry.
   */
  public static class ProfileEntry {
    
    /** The long avg. */
    float longAvg;
    
    /** The short avg. */
    float shortAvg;
    
    /** The min. */
    float min;
    
    /** The max. */
    float max;
    
    /** The accum. */
    float accum;

    /**
     * Instantiates a new profile entry.
     */
    public ProfileEntry() {
      min = Float.MAX_VALUE;
      max = -Float.MAX_VALUE;
    }

    /**
     * Record.
     *
     * @param value the value
     */
    public void record(float value) {
      longAvg = longAvg * (1 - LONG_FRACTION) + value * LONG_FRACTION;
      shortAvg = shortAvg * (1 - SHORT_FRACTION) + value * SHORT_FRACTION;
      min = MathUtils.min(value, min);
      max = MathUtils.max(value, max);
    }

    /**
     * Start accum.
     */
    public void startAccum() {
      accum = 0;
    }

    /**
     * Accum.
     *
     * @param value the value
     */
    public void accum(float value) {
      accum += value;
    }

    /**
     * End accum.
     */
    public void endAccum() {
      record(accum);
    }

    @Override
    public String toString() {
      return String.format("%.2f (%.2f) [%.2f,%.2f]", shortAvg, longAvg, min, max);
    }
  }

  /** The step. */
  public final ProfileEntry step = new ProfileEntry();
  
  /** The step init. */
  public final ProfileEntry stepInit = new ProfileEntry();
  
  /** The collide. */
  public final ProfileEntry collide = new ProfileEntry();
  
  /** The solve particle system. */
  public final ProfileEntry solveParticleSystem = new ProfileEntry();
  
  /** The solve. */
  public final ProfileEntry solve = new ProfileEntry();
  
  /** The solve init. */
  public final ProfileEntry solveInit = new ProfileEntry();
  
  /** The solve velocity. */
  public final ProfileEntry solveVelocity = new ProfileEntry();
  
  /** The solve position. */
  public final ProfileEntry solvePosition = new ProfileEntry();
  
  /** The broadphase. */
  public final ProfileEntry broadphase = new ProfileEntry();
  
  /** The solve TOI. */
  public final ProfileEntry solveTOI = new ProfileEntry();

  /**
   * To debug strings.
   *
   * @param strings the strings
   */
  public void toDebugStrings(List<String> strings) {
    strings.add("Profile:");
    strings.add(" step: " + step);
    strings.add("  init: " + stepInit);
    strings.add("  collide: " + collide);
    strings.add("  particles: " + solveParticleSystem);
    strings.add("  solve: " + solve);
    strings.add("   solveInit: " + solveInit);
    strings.add("   solveVelocity: " + solveVelocity);
    strings.add("   solvePosition: " + solvePosition);
    strings.add("   broadphase: " + broadphase);
    strings.add("  solveTOI: " + solveTOI);
  }
}
