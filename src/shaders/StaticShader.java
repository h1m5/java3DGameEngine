package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import toolbox.Maths;

/**
 * Created by HimsDLee on 29/02/16.
 */
public class StaticShader extends ShaderProgram{

    private static final String VERTEX_FILE = "src/shaders/vertexShader";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader";

    private int mMatrix;
    private int vMatrix;
    private int pMatrix;
    private int lightPosition;
    private int cameraPosition;
    private int lightColour;
    private int shineDamper;
    private int reflectivity;
    private int useFakeLighting;
    private int skyColour;

    public StaticShader() {
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
        useFakeLighting = super.getUniformLocation("useFakeLighting");
        skyColour = super.getUniformLocation("skyColour");
    }

    public void loadSkyColour(float r, float g, float b){
        super.loadVector(skyColour, new Vector3f(r, g, b));
    }

    public void loadFakeLightingVariable(boolean useFake){
        super.loadBoolean(useFakeLighting, useFake);
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
