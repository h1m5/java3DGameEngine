package water;

import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import shaders.ShaderProgram;
import toolbox.Maths;
import entities.Camera;

public class WaterShader extends ShaderProgram {

	private final static String VERTEX_FILE = "src/water/waterVertex.txt";
	private final static String FRAGMENT_FILE = "src/water/waterFragment.txt";

	private int location_modelMatrix;
	private int location_viewMatrix;
	private int location_projectionMatrix;
	private int reflectionTexture;
	private int refractionTexture;
	private int dudvMap;
	private int moveFactor;
	private int cameraPosition;
	private int normalMap;
	private int lightColour;
	private int lightPosition;
	private int depthMap;

	public WaterShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}

	@Override
	protected void bindAttributes() {
		bindAttribute(0, "position");
	}

	@Override
	protected void getAllUniformLocations() {
		location_projectionMatrix = getUniformLocation("projectionMatrix");
		location_viewMatrix = getUniformLocation("viewMatrix");
		location_modelMatrix = getUniformLocation("modelMatrix");
		reflectionTexture = getUniformLocation("reflectionTexture");
		refractionTexture = getUniformLocation("refractionTexture");
		dudvMap = getUniformLocation("dudvMap");
		moveFactor = getUniformLocation("moveFactor");
		cameraPosition = getUniformLocation("cameraPosition");
		normalMap = getUniformLocation("normalMap");
		lightColour = getUniformLocation("lightColour");
		lightPosition = getUniformLocation("lightPosition");
		depthMap = getUniformLocation("depthMap");
	}

	public void connectTextureUnits() {
		super.loadInt(reflectionTexture, 0);
		super.loadInt(refractionTexture, 1);
		super.loadInt(dudvMap, 2);
		super.loadInt(normalMap, 3);
		super.loadInt(depthMap, 4);
	}

	public void loadLight(Light sun){
		super.loadVector(lightColour, sun.getColor());
		super.loadVector(lightPosition, sun.getPosition());
	}

	public void loadMoveFactor(float factor){
		super.loadFloat(moveFactor, factor);
	}

	public void loadProjectionMatrix(Matrix4f projection) {
		loadMatrix(location_projectionMatrix, projection);
	}
	
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		loadMatrix(location_viewMatrix, viewMatrix);
		super.loadVector(cameraPosition, camera.getPosition());
	}

	public void loadModelMatrix(Matrix4f modelMatrix){
		loadMatrix(location_modelMatrix, modelMatrix);
	}

}
