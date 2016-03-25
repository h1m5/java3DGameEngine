package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

/**
 * Created by HimsDLee on 04/03/16.
 */
public class TerrainShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/shaders/terrainVertexShader";
    private static final String FRAGMENT_FILE = "src/shaders/terrainFragmentShader";

    private int mMatrix;
    private int vMatrix;
    private int pMatrix;
    private int lightPosition;
    private int cameraPosition;
    private int lightColour;
    private int shineDamper;
    private int reflectivity;
    private int skyColour;
    private int backgroundTexture;
    private int rTexture;
    private int gTexture;
    private int bTexture;
    private int blendMap;

    public TerrainShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
        super.bindAttribute(2, "normal");
    }

    @Override
    protected void getAllUniformLocations() {
        mMatrix = super.getUniformLocation("mMatrix");
        vMatrix = super.getUniformLocation("vMatrix");
        pMatrix = super.getUniformLocation("pMatrix");
        lightPosition = super.getUniformLocation("lightPosition");
        lightColour = super.getUniformLocation("lightColour");
        shineDamper = super.getUniformLocation("shineDamper");
        reflectivity = super.getUniformLocation("reflectivity");
        cameraPosition = super.getUniformLocation("cameraPosition");
        skyColour = super.getUniformLocation("skyColour");
        backgroundTexture = super.getUniformLocation("backgroundTexture");
        rTexture = super.getUniformLocation("rTexture");
        gTexture = super.getUniformLocation("gTexture");
        bTexture = super.getUniformLocation("bTexture");
        blendMap = super.getUniformLocation("blendMap");
    }

    public void connectTextureUnits(){
        super.loadInt(backgroundTexture, 0);
        super.loadInt(rTexture, 1);
        super.loadInt(gTexture, 2);
        super.loadInt(bTexture, 3);
        super.loadInt(blendMap, 4);
    }

    public void loadSkyColour(float r, float g, float b){
        super.loadVector(skyColour, new Vector3f(r, g, b));
    }

    public void loadShineVariables(float damper, float reflectivity){
        super.loadFloat(shineDamper, damper);
        super.loadFloat(this.reflectivity, reflectivity);
    }

    public void loadTransformationMatrix(Matrix4f matrix){
        super.loadMatrix(mMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera){
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(vMatrix, viewMatrix);
        super.loadVector(cameraPosition, camera.getPosition());
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(pMatrix, projection);
    }

    public void loadLight(Light light){
        super.loadVector(lightPosition, light.getPosition());
        super.loadVector(lightColour, light.getColor());
    }
}
