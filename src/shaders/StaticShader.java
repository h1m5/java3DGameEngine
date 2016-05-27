package shaders;

import entities.Camera;
import entities.Light;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import toolbox.Maths;

import java.util.List;

/**
 * Created by HimsDLee on 29/02/16.
 */
public class StaticShader extends ShaderProgram{

    private static final int MAX_LIGHTS = 4;

    private static final String VERTEX_FILE = "src/shaders/vertexShader";
    private static final String FRAGMENT_FILE = "src/shaders/fragmentShader";

    private int mMatrix;
    private int vMatrix;
    private int pMatrix;
    private int lightPosition[];
    private int lightColour[];
    private int attenuation[];
    private int cameraPosition;
    private int shineDamper;
    private int reflectivity;
    private int useFakeLighting;
    private int skyColour;
    private int numberOfRows;
    private int offset;
    private int plane;

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
//        lightPosition = super.getUniformLocation("lightPosition");
//        lightColour = super.getUniformLocation("lightColour");
        shineDamper = super.getUniformLocation("shineDamper");
        reflectivity = super.getUniformLocation("reflectivity");
        cameraPosition = super.getUniformLocation("cameraPosition");
        useFakeLighting = super.getUniformLocation("useFakeLighting");
        skyColour = super.getUniformLocation("skyColour");
        numberOfRows = super.getUniformLocation("numberOfRows");
        offset = super.getUniformLocation("offset");
        plane = super.getUniformLocation("plane");

        lightPosition = new int[MAX_LIGHTS];
        lightColour = new int[MAX_LIGHTS];
        attenuation = new int[MAX_LIGHTS];

        for(int i=0; i<MAX_LIGHTS; i++){
            lightPosition[i] = super.getUniformLocation("lightPosition[" + i + "]");
            lightColour[i] = super.getUniformLocation("lightColour[" + i + "]");
            attenuation[i] = super.getUniformLocation("attenuation[" + i + "]");
        }
    }

    public void loadNumberOfRows(int numberOfRows){
        super.loadFloat(this.numberOfRows, numberOfRows);
    }

    public void loadOffset(float x, float y){
        super.load2DVector(this.offset, new Vector2f(x, y));
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

    public void loadClipPlane(Vector4f vector){
        super.loadVector(plane, vector);
    }

    public void loadProjectionMatrix(Matrix4f projection){
        super.loadMatrix(pMatrix, projection);
    }

    public void loadLights(List<Light> lights){
//        super.loadVector(lightPosition, light.getPosition());
//        super.loadVector(lightColour, light.getColor());
        for (int i=0; i<MAX_LIGHTS; i++){
            if(i < lights.size()){
                super.loadVector(lightPosition[i], lights.get(i).getPosition());
                super.loadVector(lightColour[i], lights.get(i).getColor());
                super.loadVector(attenuation[i], lights.get(i).getAttenuation());
            } else {
                super.loadVector(lightPosition[i], new Vector3f(0,0,0));
                super.loadVector(lightColour[i], new Vector3f(0,0,0));
                super.loadVector(attenuation[i], new Vector3f(1,0,0));
            }
        }
    }
}
