/*******************************************************************************************************
 *
 * DefaultWorldPool.java, in simtools.gaml.extensions.physics, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.8.2).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
/**
 * Created at 3:26:14 AM Jan 11, 2011
 */
package org.jbox2d.pooling.normal;

import java.util.HashMap;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Collision;
import org.jbox2d.collision.Distance;
import org.jbox2d.collision.TimeOfImpact;
import org.jbox2d.common.Mat22;
import org.jbox2d.common.Mat33;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.Vec3;
import org.jbox2d.dynamics.contacts.ChainAndCircleContact;
import org.jbox2d.dynamics.contacts.ChainAndPolygonContact;
import org.jbox2d.dynamics.contacts.CircleContact;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.EdgeAndCircleContact;
import org.jbox2d.dynamics.contacts.EdgeAndPolygonContact;
import org.jbox2d.dynamics.contacts.PolygonAndCircleContact;
import org.jbox2d.dynamics.contacts.PolygonContact;
import org.jbox2d.pooling.IDynamicStack;
import org.jbox2d.pooling.IWorldPool;

/**
 * Provides object pooling for all objects used in the engine. Objects retrieved from here should
 * only be used temporarily, and then pushed back (with the exception of arrays).
 * 
 * @author Daniel Murphy
 */
public class DefaultWorldPool implements IWorldPool {

  /** The vecs. */
  private final OrderedStack<Vec2> vecs;
  
  /** The vec 3 s. */
  private final OrderedStack<Vec3> vec3s;
  
  /** The mats. */
  private final OrderedStack<Mat22> mats;
  
  /** The mat 33 s. */
  private final OrderedStack<Mat33> mat33s;
  
  /** The aabbs. */
  private final OrderedStack<AABB> aabbs;
  
  /** The rots. */
  private final OrderedStack<Rot> rots;

  /** The afloats. */
  private final HashMap<Integer, float[]> afloats = new HashMap<Integer, float[]>();
  
  /** The aints. */
  private final HashMap<Integer, int[]> aints = new HashMap<Integer, int[]>();
  
  /** The avecs. */
  private final HashMap<Integer, Vec2[]> avecs = new HashMap<Integer, Vec2[]>();

  /** The world. */
  private final IWorldPool world = this;

  /** The pcstack. */
  private final MutableStack<Contact> pcstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new PolygonContact(world); }
      protected Contact[] newArray(int size) { return new PolygonContact[size]; }
  };

  /** The ccstack. */
  private final MutableStack<Contact> ccstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new CircleContact(world); }
      protected Contact[] newArray(int size) { return new CircleContact[size]; }
    };

  /** The cpstack. */
  private final MutableStack<Contact> cpstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new PolygonAndCircleContact(world); }
      protected Contact[] newArray(int size) { return new PolygonAndCircleContact[size]; }
    };

  /** The ecstack. */
  private final MutableStack<Contact> ecstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new EdgeAndCircleContact(world); }
      protected Contact[] newArray(int size) { return new EdgeAndCircleContact[size]; }
    };

  /** The epstack. */
  private final MutableStack<Contact> epstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new EdgeAndPolygonContact(world); }
      protected Contact[] newArray(int size) { return new EdgeAndPolygonContact[size]; }
    };

  /** The chcstack. */
  private final MutableStack<Contact> chcstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new ChainAndCircleContact(world); }
      protected Contact[] newArray(int size) { return new ChainAndCircleContact[size]; }
    };

  /** The chpstack. */
  private final MutableStack<Contact> chpstack =
    new MutableStack<Contact>(Settings.CONTACT_STACK_INIT_SIZE) {
      protected Contact newInstance () { return new ChainAndPolygonContact(world); }
      protected Contact[] newArray(int size) { return new ChainAndPolygonContact[size]; }
    };

  /** The collision. */
  private final Collision collision;
  
  /** The toi. */
  private final TimeOfImpact toi;
  
  /** The dist. */
  private final Distance dist;

  /**
   * Instantiates a new default world pool.
   *
   * @param argSize the arg size
   * @param argContainerSize the arg container size
   */
  public DefaultWorldPool(int argSize, int argContainerSize) {
    vecs = new OrderedStack<Vec2>(argSize, argContainerSize) {
      protected Vec2 newInstance() { return new Vec2(); }
    };
    vec3s = new OrderedStack<Vec3>(argSize, argContainerSize) {
      protected Vec3 newInstance() { return new Vec3(); }
    };
    mats = new OrderedStack<Mat22>(argSize, argContainerSize) {
      protected Mat22 newInstance() { return new Mat22(); }
    };
    aabbs = new OrderedStack<AABB>(argSize, argContainerSize) {
      protected AABB newInstance() { return new AABB(); }
    };
    rots = new OrderedStack<Rot>(argSize, argContainerSize) {
      protected Rot newInstance() { return new Rot(); }
    };
    mat33s = new OrderedStack<Mat33>(argSize, argContainerSize) {
      protected Mat33 newInstance() { return new Mat33(); }
    };

    dist = new Distance();
    collision = new Collision(this);
    toi = new TimeOfImpact(this);
  }

  public final IDynamicStack<Contact> getPolyContactStack() {
    return pcstack;
  }

  public final IDynamicStack<Contact> getCircleContactStack() {
    return ccstack;
  }

  public final IDynamicStack<Contact> getPolyCircleContactStack() {
    return cpstack;
  }

  @Override
  public IDynamicStack<Contact> getEdgeCircleContactStack() {
    return ecstack;
  }

  @Override
  public IDynamicStack<Contact> getEdgePolyContactStack() {
    return epstack;
  }

  @Override
  public IDynamicStack<Contact> getChainCircleContactStack() {
    return chcstack;
  }

  @Override
  public IDynamicStack<Contact> getChainPolyContactStack() {
    return chpstack;
  }

  public final Vec2 popVec2() {
    return vecs.pop();
  }

  public final Vec2[] popVec2(int argNum) {
    return vecs.pop(argNum);
  }

  public final void pushVec2(int argNum) {
    vecs.push(argNum);
  }

  public final Vec3 popVec3() {
    return vec3s.pop();
  }

  public final Vec3[] popVec3(int argNum) {
    return vec3s.pop(argNum);
  }

  public final void pushVec3(int argNum) {
    vec3s.push(argNum);
  }

  public final Mat22 popMat22() {
    return mats.pop();
  }

  public final Mat22[] popMat22(int argNum) {
    return mats.pop(argNum);
  }

  public final void pushMat22(int argNum) {
    mats.push(argNum);
  }

  public final Mat33 popMat33() {
    return mat33s.pop();
  }

  public final void pushMat33(int argNum) {
    mat33s.push(argNum);
  }

  public final AABB popAABB() {
    return aabbs.pop();
  }

  public final AABB[] popAABB(int argNum) {
    return aabbs.pop(argNum);
  }

  public final void pushAABB(int argNum) {
    aabbs.push(argNum);
  }

  public final Rot popRot() {
    return rots.pop();
  }

  public final void pushRot(int num) {
    rots.push(num);
  }

  public final Collision getCollision() {
    return collision;
  }

  public final TimeOfImpact getTimeOfImpact() {
    return toi;
  }

  public final Distance getDistance() {
    return dist;
  }

  public final float[] getFloatArray(int argLength) {
    if (!afloats.containsKey(argLength)) {
      afloats.put(argLength, new float[argLength]);
    }

    assert (afloats.get(argLength).length == argLength) : "Array not built with correct length";
    return afloats.get(argLength);
  }

  public final int[] getIntArray(int argLength) {
    if (!aints.containsKey(argLength)) {
      aints.put(argLength, new int[argLength]);
    }

    assert (aints.get(argLength).length == argLength) : "Array not built with correct length";
    return aints.get(argLength);
  }

  public final Vec2[] getVec2Array(int argLength) {
    if (!avecs.containsKey(argLength)) {
      Vec2[] ray = new Vec2[argLength];
      for (int i = 0; i < argLength; i++) {
        ray[i] = new Vec2();
      }
      avecs.put(argLength, ray);
    }

    assert (avecs.get(argLength).length == argLength) : "Array not built with correct length";
    return avecs.get(argLength);
  }
}
