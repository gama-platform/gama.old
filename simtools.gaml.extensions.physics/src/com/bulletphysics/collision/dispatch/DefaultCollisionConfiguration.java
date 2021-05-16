/*
 * Java port of Bullet (c) 2008 Martin Dvorak <jezek2@advel.cz>
 *
 * Bullet Continuous Collision Detection and Physics Library
 * Copyright (c) 2003-2008 Erwin Coumans  http://www.bulletphysics.com/
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose, 
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented; you must not
 *    claim that you wrote the original software. If you use this software
 *    in a product, an acknowledgment in the product documentation would be
 *    appreciated but is not required.
 * 2. Altered source versions must be plainly marked as such, and must not be
 *    misrepresented as being the original software.
 * 3. This notice may not be removed or altered from any source distribution.
 */

package com.bulletphysics.collision.dispatch;

import com.bulletphysics.collision.broadphase.BroadphaseNativeType;
import com.bulletphysics.collision.narrowphase.ConvexPenetrationDepthSolver;
import com.bulletphysics.collision.narrowphase.GjkEpaPenetrationDepthSolver;
import com.bulletphysics.collision.narrowphase.VoronoiSimplexSolver;
import com.bulletphysics.extras.gimpact.GImpactCollisionAlgorithm;
import static com.bulletphysics.collision.broadphase.BroadphaseNativeType.*;

/**
 * Default implementation of {@link CollisionConfiguration}. Provides all core
 * collision algorithms. Some extra algorithms (like {@link GImpactCollisionAlgorithm GImpact})
 * must be registered manually by calling appropriate register method.
 * 
 * @author jezek2
 */
public class DefaultCollisionConfiguration extends CollisionConfiguration {

	//default simplex/penetration depth solvers
	protected VoronoiSimplexSolver simplexSolver;
	protected ConvexPenetrationDepthSolver pdSolver;
	
	//default CreationFunctions, filling the m_doubleDispatch table
	protected CollisionAlgorithmCreateFunc convexConvexCreateFunc;
	protected CollisionAlgorithmCreateFunc convexConcaveCreateFunc;
	protected CollisionAlgorithmCreateFunc swappedConvexConcaveCreateFunc;
	protected CollisionAlgorithmCreateFunc compoundCreateFunc;
	protected CollisionAlgorithmCreateFunc swappedCompoundCreateFunc;
	protected CollisionAlgorithmCreateFunc emptyCreateFunc;
	protected CollisionAlgorithmCreateFunc sphereSphereCF;
	protected CollisionAlgorithmCreateFunc sphereBoxCF;
	protected CollisionAlgorithmCreateFunc boxSphereCF;
	protected CollisionAlgorithmCreateFunc boxBoxCF;
	protected CollisionAlgorithmCreateFunc sphereTriangleCF;
	protected CollisionAlgorithmCreateFunc triangleSphereCF;
	protected CollisionAlgorithmCreateFunc planeConvexCF;
	protected CollisionAlgorithmCreateFunc convexPlaneCF;
	
	public DefaultCollisionConfiguration() {
		simplexSolver = new VoronoiSimplexSolver();
	
		//#define USE_EPA 1
		//#ifdef USE_EPA
		pdSolver = new GjkEpaPenetrationDepthSolver();
		//#else
		//pdSolver = new MinkowskiPenetrationDepthSolver();
		//#endif//USE_EPA	

		/*
		//default CreationFunctions, filling the m_doubleDispatch table
		*/
		convexConvexCreateFunc = new ConvexConvexAlgorithm.CreateFunc(simplexSolver, pdSolver);
		convexConcaveCreateFunc = new ConvexConcaveCollisionAlgorithm.CreateFunc();
		swappedConvexConcaveCreateFunc = new ConvexConcaveCollisionAlgorithm.SwappedCreateFunc();
		compoundCreateFunc = new CompoundCollisionAlgorithm.CreateFunc();
		swappedCompoundCreateFunc = new CompoundCollisionAlgorithm.SwappedCreateFunc();
		emptyCreateFunc = new EmptyAlgorithm.CreateFunc();

		sphereSphereCF = new SphereSphereCollisionAlgorithm.CreateFunc();
		/*
		m_sphereBoxCF = new(mem) btSphereBoxCollisionAlgorithm::CreateFunc;
		m_boxSphereCF = new (mem)btSphereBoxCollisionAlgorithm::CreateFunc;
		m_boxSphereCF->m_swapped = true;
		m_sphereTriangleCF = new (mem)btSphereTriangleCollisionAlgorithm::CreateFunc;
		m_triangleSphereCF = new (mem)btSphereTriangleCollisionAlgorithm::CreateFunc;
		m_triangleSphereCF->m_swapped = true;

		mem = btAlignedAlloc(sizeof(btBoxBoxCollisionAlgorithm::CreateFunc),16);
		m_boxBoxCF = new(mem)btBoxBoxCollisionAlgorithm::CreateFunc;
		*/

		// convex versus plane
		convexPlaneCF = new ConvexPlaneCollisionAlgorithm.CreateFunc();
		planeConvexCF = new ConvexPlaneCollisionAlgorithm.CreateFunc();
		planeConvexCF.swapped = true;

		/*
		///calculate maximum element size, big enough to fit any collision algorithm in the memory pool
		int maxSize = sizeof(btConvexConvexAlgorithm);
		int maxSize2 = sizeof(btConvexConcaveCollisionAlgorithm);
		int maxSize3 = sizeof(btCompoundCollisionAlgorithm);
		int maxSize4 = sizeof(btEmptyAlgorithm);

		int	collisionAlgorithmMaxElementSize = btMax(maxSize,maxSize2);
		collisionAlgorithmMaxElementSize = btMax(collisionAlgorithmMaxElementSize,maxSize3);
		collisionAlgorithmMaxElementSize = btMax(collisionAlgorithmMaxElementSize,maxSize4);

		if (stackAlloc)
		{
			m_ownsStackAllocator = false;
			this->m_stackAlloc = stackAlloc;
		} else
		{
			m_ownsStackAllocator = true;
			void* mem = btAlignedAlloc(sizeof(btStackAlloc),16);
			m_stackAlloc = new(mem)btStackAlloc(DEFAULT_STACK_ALLOCATOR_SIZE);
		}

		if (persistentManifoldPool)
		{
			m_ownsPersistentManifoldPool = false;
			m_persistentManifoldPool = persistentManifoldPool;
		} else
		{
			m_ownsPersistentManifoldPool = true;
			void* mem = btAlignedAlloc(sizeof(btPoolAllocator),16);
			m_persistentManifoldPool = new (mem) btPoolAllocator(sizeof(btPersistentManifold),DEFAULT_MAX_OVERLAPPING_PAIRS);
		}

		if (collisionAlgorithmPool)
		{
			m_ownsCollisionAlgorithmPool = false;
			m_collisionAlgorithmPool = collisionAlgorithmPool;
		} else
		{
			m_ownsCollisionAlgorithmPool = true;
			void* mem = btAlignedAlloc(sizeof(btPoolAllocator),16);
			m_collisionAlgorithmPool = new(mem) btPoolAllocator(collisionAlgorithmMaxElementSize,DEFAULT_MAX_OVERLAPPING_PAIRS);
		}
		*/
	}
	
	@Override
	public CollisionAlgorithmCreateFunc getCollisionAlgorithmCreateFunc(BroadphaseNativeType proxyType0, BroadphaseNativeType proxyType1) {
		if ((proxyType0 == SPHERE_SHAPE_PROXYTYPE) && (proxyType1 == SPHERE_SHAPE_PROXYTYPE)) {
			return sphereSphereCF;
		}

		/*
		if ((proxyType0 == SPHERE_SHAPE_PROXYTYPE) && (proxyType1==BOX_SHAPE_PROXYTYPE))
		{
			return	m_sphereBoxCF;
		}

		if ((proxyType0 == BOX_SHAPE_PROXYTYPE ) && (proxyType1==SPHERE_SHAPE_PROXYTYPE))
		{
			return	m_boxSphereCF;
		}

		if ((proxyType0 == SPHERE_SHAPE_PROXYTYPE ) && (proxyType1==TRIANGLE_SHAPE_PROXYTYPE))
		{
			return	m_sphereTriangleCF;
		}

		if ((proxyType0 == TRIANGLE_SHAPE_PROXYTYPE  ) && (proxyType1==SPHERE_SHAPE_PROXYTYPE))
		{
			return	m_triangleSphereCF;
		}

		if ((proxyType0 == BOX_SHAPE_PROXYTYPE) && (proxyType1 == BOX_SHAPE_PROXYTYPE)) {
			return boxBoxCF;
		}
		*/

		if (proxyType0.isConvex() && (proxyType1 == STATIC_PLANE_PROXYTYPE))
		{
			return convexPlaneCF;
		}

		if (proxyType1.isConvex() && (proxyType0 == STATIC_PLANE_PROXYTYPE))
		{
			return planeConvexCF;
		}

		if (proxyType0.isConvex() && proxyType1.isConvex()) {
			return convexConvexCreateFunc;
		}

		if (proxyType0.isConvex() && proxyType1.isConcave()) {
			return convexConcaveCreateFunc;
		}

		if (proxyType1.isConvex() && proxyType0.isConcave()) {
			return swappedConvexConcaveCreateFunc;
		}

		if (proxyType0.isCompound()) {
			return compoundCreateFunc;
		}
		else {
			if (proxyType1.isCompound()) {
				return swappedCompoundCreateFunc;
			}
		}

		// failed to find an algorithm
		return emptyCreateFunc;
	}

}
