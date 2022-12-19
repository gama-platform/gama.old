/*******************************************************************************************************
 *
 * FrameBufferObject.java, in ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform (v.1.9.0).
 *
 * (c) 2007-2022 UMI 209 UMMISCO IRD/SU & Partners (IRIT, MIAT, TLU, CTU)
 *
 * Visit https://github.com/gama-platform/gama for license information and contacts.
 * 
 ********************************************************************************************************/
package ummisco.gama.opengl.renderer.shaders;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;

/**
 * The Class FrameBufferObject.
 */
public class FrameBufferObject {

	/** The width. */
	private int width;
	
	/** The height. */
	private int height;

	/** The frame buffer array. */
	private final int[] frameBufferArray = new int[] { -1 };
	
	/** The depth buffer array. */
	private final int[] depthBufferArray = new int[] { -1 };
	
	/** The depth buffer texture array. */
	private final int[] depthBufferTextureArray = new int[] { -1 };
	
	/** The texture array. */
	private final int[] textureArray = new int[] { -1 };

	/** The gl. */
	private final GL2 gl;

	/**
	 * Instantiates a new frame buffer object.
	 *
	 * @param gl the gl
	 * @param width the width
	 * @param height the height
	 */
	public FrameBufferObject(final GL2 gl, final int width, final int height) {
		this.gl = gl;
		setDisplayDimensions(width, height);
		initialiseFrameBuffer();
	}

	/**
	 * Sets the display dimensions.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void setDisplayDimensions(final int width, final int height) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer();
	}

	/**
	 * Gets the frame buffer id.
	 *
	 * @return the frame buffer id
	 */
	public int getFrameBufferId() {
		return frameBufferArray[0];
	}

	/**
	 * Clean up.
	 */
	public void cleanUp() {// call when closing
		gl.glDeleteFramebuffers(1, frameBufferArray, 0);
		gl.glDeleteTextures(1, textureArray, 0);
		gl.glDeleteTextures(1, depthBufferTextureArray, 0);
		gl.glDeleteRenderbuffers(1, depthBufferArray, 0);
	}

	/**
	 * Bind frame buffer.
	 */
	public void bindFrameBuffer() {// call before rendering to this FBO
		bindFrameBuffer(frameBufferArray[0], width, height);
	}

	/**
	 * Unbind current frame buffer.
	 */
	public void unbindCurrentFrameBuffer() {// call to switch to default frame buffer
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, width, height);
	}

	/**
	 * Gets the FBO texture.
	 *
	 * @return the FBO texture
	 */
	public int getFBOTexture() {// get the resulting texture
		return textureArray[0];
	}

	/**
	 * Gets the depth texture.
	 *
	 * @return the depth texture
	 */
	public int getDepthTexture() {// get the resulting depth texture
		return depthBufferTextureArray[0];
	}

	/**
	 * Initialise frame buffer.
	 */
	private void initialiseFrameBuffer() {
		createFrameBuffer();
		createTextureAttachment(width, height);
		createDepthBufferAttachment(width, height);
		unbindCurrentFrameBuffer();
	}

	/**
	 * Bind frame buffer.
	 *
	 * @param frameBuffer the frame buffer
	 * @param width the width
	 * @param height the height
	 */
	private void bindFrameBuffer(final int frameBuffer, final int width, final int height) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);// To make sure the texture isn't bound
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer);
		gl.glViewport(0, 0, width, height);
	}

	/**
	 * Creates the frame buffer.
	 *
	 * @return the int
	 */
	private int createFrameBuffer() {
		cleanUp();
		gl.glGenFramebuffers(1, frameBufferArray, 0);
		// generate name for frame buffer
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBufferArray[0]);
		// create the framebuffer
		gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
		// indicate that we will always render to color attachment 0
		return frameBufferArray[0];
	}

	/**
	 * Creates the texture attachment.
	 *
	 * @param width the width
	 * @param height the height
	 * @return the int
	 */
	private int createTextureAttachment(final int width, final int height) {
		gl.glGenTextures(1, textureArray, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textureArray[0]);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR_MIPMAP_LINEAR);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, width, height, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, textureArray[0], 0);
		return textureArray[0];
	}

	// private int createDepthTextureAttachment(final int width, final int height) {
	// gl.glGenTextures(1, depthBufferTextureArray, 0);
	// gl.glBindTexture(GL2.GL_TEXTURE_2D, depthBufferTextureArray[0]);
	// gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT32, width, height, 0, GL2.GL_DEPTH_COMPONENT,
	// GL2.GL_FLOAT, (ByteBuffer) null);
	// gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
	// gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
	// gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, depthBufferTextureArray[0], 0);
	// return depthBufferTextureArray[0];
	// }

	/**
	 * Creates the depth buffer attachment.
	 *
	 * @param width the width
	 * @param height the height
	 * @return the int
	 */
	private int createDepthBufferAttachment(final int width, final int height) {
		gl.glGenRenderbuffers(1, depthBufferArray, 0);
		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depthBufferArray[0]);
		gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER,
				depthBufferArray[0]);
		return depthBufferArray[0];
	}

}