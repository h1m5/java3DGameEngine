package skybox;

import org.lwjgl.util.vector.Matrix4f;

import entities.Camera;

import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;
import shaders.ShaderProgram;
import toolbox.Maths;

class SkyboxShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/skybox/skyboxVertexShader";
	private static final String FRAGMENT_FILE = "src/skybox/skyboxFragmentShader";
	
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_fogColour;
	private int location_cubeMap;
	private int location_cubeMap2;
	private int location_blendFactor;

	private static final float ROTATE_SPEED = 1f;
	private float rotation = 0.0f;

	SkyboxShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	void loadProjectionMatrix(Matrix4f matrix){
		super.loadMatrix(location_projectionMatrix, matrix);
	}

	void loadViewMatrix(Camera camera){
		Matrix4f matrix = Maths.createViewMatrix(camera);
		matrix.m30 = 0;
		matrix.m31 = 0;
		matrix.m32 = 0;

		rotation += ROTATE_SPEED * DisplayManager.getFrameTimeSeconds();
		Matrix4f.rotate((float) Math.toRadians(rotation), new Vector3f(0,1,0), matrix, matrix);
		super.loadMatrix(location_viewMatrix, matrix);
	}

	void loadFogColour(float r, float g, float b)
	{
		super.loadVector(location_fogColour, new Vector3f(r, g, b));
	}

	void conntectTextureUnits(){
		super.loadInt(location_cubeMap, 0);
		super.loadInt(location_cubeMap2, 1);
	}

	void loadBlendFactor(float blend)
	{
		super.loadFloat(location_blendFactor, blend);
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_fogColour = super.getUniformLocation("fogColour");
		location_blendFactor = super.getUniformLocation("blendFactor");
		location_cubeMap = super.getUniformLocation("cubeMap");
		location_cubeMap2 = super.getUniformLocation("cubeMap2");
	}

	@Override
	protected void bindAttributes() {
		super.bindAttribute(0, "position");
	}

}
