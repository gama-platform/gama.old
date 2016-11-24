/*********************************************************************************************
 *
 * 'ShaderProgram.java, in plugin ummisco.gama.opengl, is part of the source code of the
 * GAMA modeling and simulation platform.
 * (c) 2007-2016 UMI 209 UMMISCO IRD/UPMC & Partners
 *
 * Visit https://github.com/gama-platform/gama for license information and developers contact.
 * 
 *
 **********************************************************************************************/
package ummisco.gama.modernOpenGL.shader;

import java.util.List;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import com.jogamp.opengl.GL2;

import msi.gama.outputs.LightPropertiesStructure;
import msi.gama.outputs.LightPropertiesStructure.TYPE;

public class ShaderProgram extends AbstractShader {
	
	private static int MAX_LIGHT = 7;
	
	private static String VERTEX_FILE = "vertexShader";		
	private static String FRAGMENT_FILE = "fragmentShader";
	
	private int location_lightProperties[];
	private int location_lightColor[];
	private int location_lightAttenuation[];
	private int location_shineDamper;	// for specular light
	private int location_reflectivity;	// for specular light
	private int location_useTexture;	// 0 for no, 1 for yes
	private int location_useNormals;	// 0 for no, 1 for yes
	private int location_texture;
	private int location_ambientLight;
	private int location_invViewMatrix; // #issue 1989 : the inverse function does not work in GLSL for MacOS
	
	private boolean useNormal = false;
	private boolean useTexture = false;
	
	private int textureIDStored = -1;
	
	public ShaderProgram(GL2 gl) {
		super(gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	public ShaderProgram(ShaderProgram shader) {
		super(shader.gl,VERTEX_FILE,FRAGMENT_FILE);
	}
	
	@Override
	protected void bindAttributes() {
		super.bindAttribute(POSITION_ATTRIBUTE_IDX, "attribute_Position");
		super.bindAttribute(COLOR_ATTRIBUTE_IDX, "attribute_Color");
		super.bindAttribute(UVMAPPING_ATTRIBUTE_IDX, "attribute_TextureCoords");
	}
	
	@Override
	protected void getAllUniformLocations() {
		super.getAllUniformLocations();
		location_shineDamper = getUniformLocation("shineDamper");
		location_reflectivity = getUniformLocation("reflectivity");
		location_useTexture = getUniformLocation("useTexture");
		location_useNormals = getUniformLocation("useNormals");
		location_texture = getUniformLocation("textureSampler");
		location_ambientLight = getUniformLocation("ambientLight");
		location_invViewMatrix = getUniformLocation("invViewMatrix");
		
		location_lightColor = new int[MAX_LIGHT];
		location_lightAttenuation = new int[MAX_LIGHT];
		location_lightProperties = new int[MAX_LIGHT];
		for (int i = 0 ; i < MAX_LIGHT ; i++) {
			location_lightProperties[i] = getUniformLocation("lightProperties["+i+"]");
			location_lightColor[i] = getUniformLocation("lightColors["+i+"]");
			location_lightAttenuation[i] = getUniformLocation("lightAttenuations["+i+"]");
		}
		
	}
	
	public void loadShineVariables(float damper, float reflectivity) {
		super.loadFloat(location_shineDamper, damper);
		super.loadFloat(location_reflectivity, reflectivity);
	}
	
	public void loadAmbientLight(Vector3f light) {
		super.loadVector(location_ambientLight,light);
	}
	
	public void loadTexture(int textureId) {
		super.loadInt(location_texture,textureId);
	}
	
	public void enableTexture() {
		useTexture = true;
		super.loadFloat(location_useTexture,1f);
	}
	
	public void disableTexture() {
		useTexture = false;
		super.loadFloat(location_useTexture,0f);
	}
	
	public void disableNormal() {
		useNormal = false;
		super.loadFloat(location_useNormals, 0f);
	}
	
	public void enableNormal() {
		useNormal = true;
		super.loadFloat(location_useNormals, 1f);
	}
	
	public boolean useTexture() {
		return useTexture;
	}
	
	public boolean useNormal() {
		return useNormal;
	}

	public void loadLights(List<LightPropertiesStructure> lights) {
		for (int i = 1 ; i < MAX_LIGHT ; i++) { // Beware : the loop starts with the index 1 !!! (the light 0 only set the ambient light)
			if (i < lights.size()) {
				LightPropertiesStructure light = lights.get(i);
				Matrix4f lightPropertyMatrix = new Matrix4f();
				lightPropertyMatrix.m00 = light.getPosition().x;
				lightPropertyMatrix.m01 = light.getPosition().y;
				lightPropertyMatrix.m02 = light.getPosition().z;
				int lightType = (light.getType() == TYPE.POINT) ? 0 :
					(light.getType() == TYPE.DIRECTION) ? 1 : 2;
				lightPropertyMatrix.m03 = lightType; // 0 for point, 1 for direction, 2 for spot
				lightPropertyMatrix.m10 = light.getDirection().x;
				lightPropertyMatrix.m11 = light.getDirection().y;
				lightPropertyMatrix.m12 = light.getDirection().z;
				lightPropertyMatrix.m20 = light.getColor().x;
				lightPropertyMatrix.m21 = light.getColor().y;
				lightPropertyMatrix.m22 = light.getColor().z;
				lightPropertyMatrix.m30 = light.getLinearAttenuation();
				lightPropertyMatrix.m31 = light.getQuadraticAttenuation();
				super.loadMatrix(location_lightProperties[i-1], lightPropertyMatrix);
				super.loadVector(location_lightColor[i-1], light.getColor());
				super.loadVector(location_lightAttenuation[i-1], new Vector3f(1.0f,light.getLinearAttenuation(),light.getQuadraticAttenuation()));
			}
			else {
				Matrix4f lightPropertyMatrix = new Matrix4f();
				lightPropertyMatrix.m20 = lightPropertyMatrix.m21 = lightPropertyMatrix.m22 = 0;
				Vector3f lightColor = new Vector3f(0,0,0);
				super.loadMatrix(location_lightProperties[i-1], lightPropertyMatrix);
				super.loadVector(location_lightColor[i-1], lightColor);
				super.loadVector(location_lightAttenuation[i-1], new Vector3f(1.0f,0.0f,0.0f));
			}
		}
	}

	public void storeTextureID(int textureID) {
		textureIDStored = textureID;
	}
	
	public int getTextureID() {
		return textureIDStored;
	}
	
	public void loadInvViewMatrix(Matrix4f invViewMatrix) {
		loadMatrix(location_invViewMatrix, invViewMatrix);
	}

	@Override
	public Vector3f getTranslation() {
		return new Vector3f(0,0,0);
	}
}
