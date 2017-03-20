/*********************************************************************************************
 *
 * 'FrameBufferObject.java, in plugin ummisco.gama.opengl, is part of the source code of the GAMA modeling and
 * simulation platform. (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;

public class FrameBufferObject {

	private int width;
	private int height;

	private final int[] frameBufferArray = new int[] { -1 };
	private final int[] depthBufferArray = new int[] { -1 };
	private final int[] depthBufferTextureArray = new int[] { -1 };
	private final int[] textureArray = new int[] { -1 };

	private final GL2 gl;

	public FrameBufferObject(final GL2 gl, final int width, final int height) {
		this.gl = gl;
		setDisplayDimensions(width, height);
		initialiseFrameBuffer();
	}

	public void setDisplayDimensions(final int width, final int height) {
		this.width = width;
		this.height = height;
		initialiseFrameBuffer();
	}

	public int getFrameBufferId() {
		return frameBufferArray[0];
	}

	public void cleanUp() {// call when closing
		gl.glDeleteFramebuffers(1, frameBufferArray, 0);
		gl.glDeleteTextures(1, textureArray, 0);
		gl.glDeleteTextures(1, depthBufferTextureArray, 0);
		gl.glDeleteRenderbuffers(1, depthBufferArray, 0);
	}

	public void bindFrameBuffer() {// call before rendering to this FBO
		bindFrameBuffer(frameBufferArray[0], width, height);
	}

	public void unbindCurrentFrameBuffer() {// call to switch to default frame buffer
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
		gl.glViewport(0, 0, width, height);
	}

	public int getFBOTexture() {// get the resulting texture
		return textureArray[0];
	}

	public int getDepthTexture() {// get the resulting depth texture
		return depthBufferTextureArray[0];
	}

	private void initialiseFrameBuffer() {
		createFrameBuffer();
		createTextureAttachment(width, height);
		createDepthBufferAttachment(width, height);
		unbindCurrentFrameBuffer();
	}

	private void bindFrameBuffer(final int frameBuffer, final int width, final int height) {
		gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);// To make sure the texture isn't bound
		gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer);
		gl.glViewport(0, 0, width, height);
	}

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

	private int createTextureAttachment(final int width, final int height) {
		gl.glGenTextures(1, textureArray, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, textureArray[0]);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, width, height, 0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, textureArray[0], 0);
		return textureArray[0];
	}

	private int createDepthTextureAttachment(final int width, final int height) {
		gl.glGenTextures(1, depthBufferTextureArray, 0);
		gl.glBindTexture(GL2.GL_TEXTURE_2D, depthBufferTextureArray[0]);
		gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT32, width, height, 0, GL2.GL_DEPTH_COMPONENT,
				GL2.GL_FLOAT, (ByteBuffer) null);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
		gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
		gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, depthBufferTextureArray[0], 0);
		return depthBufferTextureArray[0];
	}

	private int createDepthBufferAttachment(final int width, final int height) {
		gl.glGenRenderbuffers(1, depthBufferArray, 0);
		gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depthBufferArray[0]);
		gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height);
		gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER,
				depthBufferArray[0]);
		return depthBufferArray[0];
	}

}