/*******************************************************************************************************
 *
 * DebugDraw.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 4:35:29 AM Jul 15, 2010
 */
package org.jbox2d.callbacks;

import org.jbox2d.common.Color3f;
import org.jbox2d.common.IViewportTransform;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.particle.ParticleColor;

/**
 * Implement this abstract class to allow JBox2d to automatically draw your physics for debugging
 * purposes. Not intended to replace your own custom rendering routines!
 * 
 * @author Daniel Murphy
 */
public abstract class DebugDraw {

  /** Draw shapes */
  public static final int e_shapeBit = 1 << 1;
  /** Draw joint connections */
  public static final int e_jointBit = 1 << 2;
  /** Draw axis aligned bounding boxes */
  public static final int e_aabbBit = 1 << 3;
  /** Draw pairs of connected objects */
  public static final int e_pairBit = 1 << 4;
  /** Draw center of mass frame */
  public static final int e_centerOfMassBit = 1 << 5;
  /** Draw dynamic tree */
  public static final int e_dynamicTreeBit = 1 << 6;
  /** Draw only the wireframe for drawing performance */
  public static final int e_wireframeDrawingBit = 1 << 7;


  /** The m draw flags. */
  protected int m_drawFlags;
  
  /** The viewport transform. */
  protected IViewportTransform viewportTransform;

  /**
   * Instantiates a new debug draw.
   */
  public DebugDraw() {
    this(null);
  }

  /**
   * Instantiates a new debug draw.
   *
   * @param viewport the viewport
   */
  public DebugDraw(IViewportTransform viewport) {
    m_drawFlags = 0;
    viewportTransform = viewport;
  }

  /**
   * Sets the viewport transform.
   *
   * @param viewportTransform the new viewport transform
   */
  public void setViewportTransform(IViewportTransform viewportTransform) {
    this.viewportTransform = viewportTransform;
  }

  /**
   * Sets the flags.
   *
   * @param flags the new flags
   */
  public void setFlags(int flags) {
    m_drawFlags = flags;
  }

  /**
   * Gets the flags.
   *
   * @return the flags
   */
  public int getFlags() {
    return m_drawFlags;
  }

  /**
   * Append flags.
   *
   * @param flags the flags
   */
  public void appendFlags(int flags) {
    m_drawFlags |= flags;
  }

  /**
   * Clear flags.
   *
   * @param flags the flags
   */
  public void clearFlags(int flags) {
    m_drawFlags &= ~flags;
  }

  /**
   * Draw a closed polygon provided in CCW order. This implementation uses
   * {@link #drawSegment(Vec2, Vec2, Color3f)} to draw each side of the polygon.
   * 
   * @param vertices
   * @param vertexCount
   * @param color
   */
  public void drawPolygon(Vec2[] vertices, int vertexCount, Color3f color) {
    if (vertexCount == 1) {
      drawSegment(vertices[0], vertices[0], color);
      return;
    }

    for (int i = 0; i < vertexCount - 1; i += 1) {
      drawSegment(vertices[i], vertices[i + 1], color);
    }

    if (vertexCount > 2) {
      drawSegment(vertices[vertexCount - 1], vertices[0], color);
    }
  }

  /**
   * Draw point.
   *
   * @param argPoint the arg point
   * @param argRadiusOnScreen the arg radius on screen
   * @param argColor the arg color
   */
  public abstract void drawPoint(Vec2 argPoint, float argRadiusOnScreen, Color3f argColor);

  /**
   * Draw a solid closed polygon provided in CCW order.
   * 
   * @param vertices
   * @param vertexCount
   * @param color
   */
  public abstract void drawSolidPolygon(Vec2[] vertices, int vertexCount, Color3f color);

  /**
   * Draw a circle.
   * 
   * @param center
   * @param radius
   * @param color
   */
  public abstract void drawCircle(Vec2 center, float radius, Color3f color);

  /** Draws a circle with an axis */
  public void drawCircle(Vec2 center, float radius, Vec2 axis, Color3f color) {
    drawCircle(center, radius, color);
  }

  /**
   * Draw a solid circle.
   * 
   * @param center
   * @param radius
   * @param axis
   * @param color
   */
  public abstract void drawSolidCircle(Vec2 center, float radius, Vec2 axis, Color3f color);

  /**
   * Draw a line segment.
   * 
   * @param p1
   * @param p2
   * @param color
   */
  public abstract void drawSegment(Vec2 p1, Vec2 p2, Color3f color);

  /**
   * Draw a transform. Choose your own length scale
   * 
   * @param xf
   */
  public abstract void drawTransform(Transform xf);

  /**
   * Draw a string.
   * 
   * @param x
   * @param y
   * @param s
   * @param color
   */
  public abstract void drawString(float x, float y, String s, Color3f color);

  /**
   * Draw a particle array
   * 
   * @param colors can be null
   */
  public abstract void drawParticles(Vec2[] centers, float radius, ParticleColor[] colors, int count);

  /**
   * Draw a particle array
   * 
   * @param colors can be null
   */
  public abstract void drawParticlesWireframe(Vec2[] centers, float radius, ParticleColor[] colors,
      int count);

  /** Called at the end of drawing a world */
  public void flush() {}

  /**
   * Draw string.
   *
   * @param pos the pos
   * @param s the s
   * @param color the color
   */
  public void drawString(Vec2 pos, String s, Color3f color) {
    drawString(pos.x, pos.y, s, color);
  }

  /**
   * Gets the viewport tranform.
   *
   * @return the viewport tranform
   */
  public IViewportTransform getViewportTranform() {
    return viewportTransform;
  }

  /**
   * @param x
   * @param y
   * @param scale
   * @deprecated use the viewport transform in {@link #getViewportTranform()}
   */
  public void setCamera(float x, float y, float scale) {
    viewportTransform.setCamera(x, y, scale);
  }


  /**
   * @param argScreen
   * @param argWorld
   */
  public void getScreenToWorldToOut(Vec2 argScreen, Vec2 argWorld) {
    viewportTransform.getScreenToWorld(argScreen, argWorld);
  }

  /**
   * @param argWorld
   * @param argScreen
   */
  public void getWorldToScreenToOut(Vec2 argWorld, Vec2 argScreen) {
    viewportTransform.getWorldToScreen(argWorld, argScreen);
  }

  /**
   * Takes the world coordinates and puts the corresponding screen coordinates in argScreen.
   * 
   * @param worldX
   * @param worldY
   * @param argScreen
   */
  public void getWorldToScreenToOut(float worldX, float worldY, Vec2 argScreen) {
    argScreen.set(worldX, worldY);
    viewportTransform.getWorldToScreen(argScreen, argScreen);
  }

  /**
   * takes the world coordinate (argWorld) and returns the screen coordinates.
   * 
   * @param argWorld
   */
  public Vec2 getWorldToScreen(Vec2 argWorld) {
    Vec2 screen = new Vec2();
    viewportTransform.getWorldToScreen(argWorld, screen);
    return screen;
  }

  /**
   * Takes the world coordinates and returns the screen coordinates.
   * 
   * @param worldX
   * @param worldY
   */
  public Vec2 getWorldToScreen(float worldX, float worldY) {
    Vec2 argScreen = new Vec2(worldX, worldY);
    viewportTransform.getWorldToScreen(argScreen, argScreen);
    return argScreen;
  }

  /**
   * takes the screen coordinates and puts the corresponding world coordinates in argWorld.
   * 
   * @param screenX
   * @param screenY
   * @param argWorld
   */
  public void getScreenToWorldToOut(float screenX, float screenY, Vec2 argWorld) {
    argWorld.set(screenX, screenY);
    viewportTransform.getScreenToWorld(argWorld, argWorld);
  }

  /**
   * takes the screen coordinates (argScreen) and returns the world coordinates
   * 
   * @param argScreen
   */
  public Vec2 getScreenToWorld(Vec2 argScreen) {
    Vec2 world = new Vec2();
    viewportTransform.getScreenToWorld(argScreen, world);
    return world;
  }

  /**
   * takes the screen coordinates and returns the world coordinates.
   * 
   * @param screenX
   * @param screenY
   */
  public Vec2 getScreenToWorld(float screenX, float screenY) {
    Vec2 screen = new Vec2(screenX, screenY);
    viewportTransform.getScreenToWorld(screen, screen);
    return screen;
  }
}
