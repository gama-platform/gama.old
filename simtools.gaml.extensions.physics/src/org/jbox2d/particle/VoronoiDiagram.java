/*******************************************************************************************************
 *
 * VoronoiDiagram.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package org.jbox2d.particle;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.pooling.normal.MutableStack;

/**
 * The Class VoronoiDiagram.
 */
public class VoronoiDiagram {
  
  /**
   * The Class Generator.
   */
  public static class Generator {
    
    /** The center. */
    final Vec2 center = new Vec2();
    
    /** The tag. */
    int tag;
  }
  
  /**
   * The Class VoronoiDiagramTask.
   */
  public static class VoronoiDiagramTask {
    
    /** The m i. */
    int m_x, m_y, m_i;
    
    /** The m generator. */
    Generator m_generator;

    /**
     * Instantiates a new voronoi diagram task.
     */
    public VoronoiDiagramTask() {}

    /**
     * Instantiates a new voronoi diagram task.
     *
     * @param x the x
     * @param y the y
     * @param i the i
     * @param g the g
     */
    public VoronoiDiagramTask(int x, int y, int i, Generator g) {
      m_x = x;
      m_y = y;
      m_i = i;
      m_generator = g;
    }

    /**
     * Sets the.
     *
     * @param x the x
     * @param y the y
     * @param i the i
     * @param g the g
     * @return the voronoi diagram task
     */
    public VoronoiDiagramTask set(int x, int y, int i, Generator g) {
      m_x = x;
      m_y = y;
      m_i = i;
      m_generator = g;
      return this;
    }
  }

  /**
   * The Interface VoronoiDiagramCallback.
   */
  public static interface VoronoiDiagramCallback {
    
    /**
     * Callback.
     *
     * @param aTag the a tag
     * @param bTag the b tag
     * @param cTag the c tag
     */
    void callback(int aTag, int bTag, int cTag);
  }

  /** The m generator buffer. */
  private Generator[] m_generatorBuffer;
  
  /** The m generator count. */
  private int m_generatorCount;
  
  /** The m count Y. */
  private int m_countX, m_countY;
  
  /** The m diagram. */
  // The diagram is an array of "pointers".
  private Generator[] m_diagram;

  /**
   * Instantiates a new voronoi diagram.
   *
   * @param generatorCapacity the generator capacity
   */
  public VoronoiDiagram(int generatorCapacity) {
    m_generatorBuffer = new Generator[generatorCapacity];
    for (int i = 0; i < generatorCapacity; i++) {
      m_generatorBuffer[i] = new Generator();
    }
    m_generatorCount = 0;
    m_countX = 0;
    m_countY = 0;
    m_diagram = null;
  }

  /**
   * Gets the nodes.
   *
   * @param callback the callback
   * @return the nodes
   */
  public void getNodes(VoronoiDiagramCallback callback) {
    for (int y = 0; y < m_countY - 1; y++) {
      for (int x = 0; x < m_countX - 1; x++) {
        int i = x + y * m_countX;
        Generator a = m_diagram[i];
        Generator b = m_diagram[i + 1];
        Generator c = m_diagram[i + m_countX];
        Generator d = m_diagram[i + 1 + m_countX];
        if (b != c) {
          if (a != b && a != c) {
            callback.callback(a.tag, b.tag, c.tag);
          }
          if (d != b && d != c) {
            callback.callback(b.tag, d.tag, c.tag);
          }
        }
      }
    }
  }

  /**
   * Adds the generator.
   *
   * @param center the center
   * @param tag the tag
   */
  public void addGenerator(Vec2 center, int tag) {
    Generator g = m_generatorBuffer[m_generatorCount++];
    g.center.x = center.x;
    g.center.y = center.y;
    g.tag = tag;
  }

  /** The lower. */
  private final Vec2 lower = new Vec2();
  
  /** The upper. */
  private final Vec2 upper = new Vec2();
  
  /** The task pool. */
  private MutableStack<VoronoiDiagramTask> taskPool =
      new MutableStack<VoronoiDiagram.VoronoiDiagramTask>(50) {
        @Override
        protected VoronoiDiagramTask newInstance() {
          return new VoronoiDiagramTask();
        }

        @Override
        protected VoronoiDiagramTask[] newArray(int size) {
          return new VoronoiDiagramTask[size];
        }
      };
  
  /** The queue. */
  private final StackQueue<VoronoiDiagramTask> queue = new StackQueue<VoronoiDiagramTask>();

  /**
   * Generate.
   *
   * @param radius the radius
   */
  public void generate(float radius) {
    assert (m_diagram == null);
    float inverseRadius = 1 / radius;
    lower.x = Float.MAX_VALUE;
    lower.y = Float.MAX_VALUE;
    upper.x = -Float.MAX_VALUE;
    upper.y = -Float.MAX_VALUE;
    for (int k = 0; k < m_generatorCount; k++) {
      Generator g = m_generatorBuffer[k];
      Vec2.minToOut(lower, g.center, lower);
      Vec2.maxToOut(upper, g.center, upper);
    }
    m_countX = 1 + (int) (inverseRadius * (upper.x - lower.x));
    m_countY = 1 + (int) (inverseRadius * (upper.y - lower.y));
    m_diagram = new Generator[m_countX * m_countY];
    queue.reset(new VoronoiDiagramTask[4 * m_countX * m_countX]);
    for (int k = 0; k < m_generatorCount; k++) {
      Generator g = m_generatorBuffer[k];
      g.center.x = inverseRadius * (g.center.x - lower.x);
      g.center.y = inverseRadius * (g.center.y - lower.y);
      int x = MathUtils.max(0, MathUtils.min((int) g.center.x, m_countX - 1));
      int y = MathUtils.max(0, MathUtils.min((int) g.center.y, m_countY - 1));
      queue.push(taskPool.pop().set(x, y, x + y * m_countX, g));
    }
    while (!queue.empty()) {
      VoronoiDiagramTask front = queue.pop();
      int x = front.m_x;
      int y = front.m_y;
      int i = front.m_i;
      Generator g = front.m_generator;
      if (m_diagram[i] == null) {
        m_diagram[i] = g;
        if (x > 0) {
          queue.push(taskPool.pop().set(x - 1, y, i - 1, g));
        }
        if (y > 0) {
          queue.push(taskPool.pop().set(x, y - 1, i - m_countX, g));
        }
        if (x < m_countX - 1) {
          queue.push(taskPool.pop().set(x + 1, y, i + 1, g));
        }
        if (y < m_countY - 1) {
          queue.push(taskPool.pop().set(x, y + 1, i + m_countX, g));
        }
      }
      taskPool.push(front);
    }
    int maxIteration = m_countX + m_countY;
    for (int iteration = 0; iteration < maxIteration; iteration++) {
      for (int y = 0; y < m_countY; y++) {
        for (int x = 0; x < m_countX - 1; x++) {
          int i = x + y * m_countX;
          Generator a = m_diagram[i];
          Generator b = m_diagram[i + 1];
          if (a != b) {
            queue.push(taskPool.pop().set(x, y, i, b));
            queue.push(taskPool.pop().set(x + 1, y, i + 1, a));
          }
        }
      }
      for (int y = 0; y < m_countY - 1; y++) {
        for (int x = 0; x < m_countX; x++) {
          int i = x + y * m_countX;
          Generator a = m_diagram[i];
          Generator b = m_diagram[i + m_countX];
          if (a != b) {
            queue.push(taskPool.pop().set(x, y, i, b));
            queue.push(taskPool.pop().set(x, y + 1, i + m_countX, a));
          }
        }
      }
      boolean updated = false;
      while (!queue.empty()) {
        VoronoiDiagramTask front = queue.pop();
        int x = front.m_x;
        int y = front.m_y;
        int i = front.m_i;
        Generator k = front.m_generator;
        Generator a = m_diagram[i];
        Generator b = k;
        if (a != b) {
          float ax = a.center.x - x;
          float ay = a.center.y - y;
          float bx = b.center.x - x;
          float by = b.center.y - y;
          float a2 = ax * ax + ay * ay;
          float b2 = bx * bx + by * by;
          if (a2 > b2) {
            m_diagram[i] = b;
            if (x > 0) {
              queue.push(taskPool.pop().set(x - 1, y, i - 1, b));
            }
            if (y > 0) {
              queue.push(taskPool.pop().set(x, y - 1, i - m_countX, b));
            }
            if (x < m_countX - 1) {
              queue.push(taskPool.pop().set(x + 1, y, i + 1, b));
            }
            if (y < m_countY - 1) {
              queue.push(taskPool.pop().set(x, y + 1, i + m_countX, b));
            }
            updated = true;
          }
        }
        taskPool.push(front);
      }
      if (!updated) {
        break;
      }
    }
  }
}
