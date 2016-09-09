package ummisco.gama.modernOpenGL;
 
import java.nio.ByteBuffer;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import ummisco.gama.modernOpenGL.shader.AbstractShader;
 
public class FrameBufferObject {
 
    protected static final int REFLECTION_WIDTH = 1280;//320;
    private static final int REFLECTION_HEIGHT = 720;//180;
     
    protected static final int REFRACTION_WIDTH = 1280;
    private static final int REFRACTION_HEIGHT = 720;
 
    private int reflectionFrameBuffer;
    private int reflectionTexture;
    private int reflectionDepthBuffer;
     
    private int refractionFrameBuffer;
    private int refractionTexture;
    private int refractionDepthTexture;
    
    private GL2 gl;
 
    public FrameBufferObject(GL2 gl) {//call when loading the game
    	this.gl = gl;
        initialiseReflectionFrameBuffer();
        initialiseRefractionFrameBuffer();
    }
 
//    public void cleanUp() {//call when closing the game
//        GL30.glDeleteFramebuffers(reflectionFrameBuffer);
//        GL11.glDeleteTextures(reflectionTexture);
//        GL30.glDeleteRenderbuffers(reflectionDepthBuffer);
//        GL30.glDeleteFramebuffers(refractionFrameBuffer);
//        GL11.glDeleteTextures(refractionTexture);
//        GL11.glDeleteTextures(refractionDepthTexture);
//    }
 
    public void bindReflectionFrameBuffer() {//call before rendering to this FBO
        bindFrameBuffer(reflectionFrameBuffer,REFLECTION_WIDTH,REFLECTION_HEIGHT);
    }
     
    public void bindRefractionFrameBuffer() {//call before rendering to this FBO
        bindFrameBuffer(refractionFrameBuffer,REFRACTION_WIDTH,REFRACTION_HEIGHT);
    }
     
    public void unbindCurrentFrameBuffer() {//call to switch to default frame buffer
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
        gl.glViewport(0, 0, 1000, 800);
    }
 
    public int getReflectionTexture() {//get the resulting texture
        return reflectionTexture;
    }
     
    public int getRefractionTexture() {//get the resulting texture
        return refractionTexture;
    }
     
    public int getRefractionDepthTexture(){//get the resulting depth texture
        return refractionDepthTexture;
    }
 
    private void initialiseReflectionFrameBuffer() {
        reflectionFrameBuffer = createFrameBuffer();
        reflectionTexture = createTextureAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        reflectionDepthBuffer = createDepthBufferAttachment(REFLECTION_WIDTH,REFLECTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }
     
    private void initialiseRefractionFrameBuffer() {
        refractionFrameBuffer = createFrameBuffer();
        refractionTexture = createTextureAttachment(REFRACTION_WIDTH,REFRACTION_HEIGHT);
        refractionDepthTexture = createDepthTextureAttachment(REFRACTION_WIDTH,REFRACTION_HEIGHT);
        unbindCurrentFrameBuffer();
    }
     
    private void bindFrameBuffer(int frameBuffer, int width, int height){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);//To make sure the texture isn't bound
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBuffer);
        gl.glViewport(0, 0, width, height);
    }
 
    private int createFrameBuffer() {
    	int[] fboHandles = new int[1];
    	int frameBufferID = 1;
    	gl.glGenFramebuffers(frameBufferID,fboHandles,0);
        //generate name for frame buffer
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBufferID);
        //create the framebuffer
        gl.glDrawBuffer(GL2.GL_COLOR_ATTACHMENT0);
        //indicate that we will always render to color attachment 0
        return frameBufferID;
    }
 
    private int createTextureAttachment(int width, int height) {
    	
		int[] textures = new int[1];
		int textureID = 1;
        gl.glGenTextures(textureID,textures,0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGB, width, height,
                0, GL2.GL_RGB, GL2.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0,
                textureID, 0);
        return textureID;
    }
     
    private int createDepthTextureAttachment(int width, int height){
    	int[] textures = new int[2];
		int textureID = 2;
		gl.glGenTextures(textureID,textures,0);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, textureID);
        gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_DEPTH_COMPONENT32, width, height,
                0, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, (ByteBuffer) null);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glFramebufferTextureEXT(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
                textureID, 0);
        return textureID;
    }
 
    private int createDepthBufferAttachment(int width, int height) {
    	int[] vboHandles = new int[1];
    	int depthBufferID = 3;
    	gl.glGenRenderbuffers(depthBufferID, vboHandles, 0);
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depthBufferID);
        gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width,
                height);
        gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT,
        		GL2.GL_RENDERBUFFER, depthBufferID);
        return depthBufferID;
    }
 
}