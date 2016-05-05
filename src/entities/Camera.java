package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;
import renderEngine.DisplayManager;

/**
 * Created by HimsDLee on 03/03/16.
 */
public class Camera {


    private float distanceFromPlayer = 15;
    private float angleAroundPlayer = 0;
    private final float minDistanceFromPlayer = 7;
    private final float yOffset = -3;

    private Vector3f position = new Vector3f(0,10,0);
    private float pitch = 20f;
    private float yaw;
    private float roll;
    private final float speed = 0.05f;

    private Player player;

    public Camera(Player player) {
        this.player = player;
    }

    public void move() {
        calculateZoom();
        calculatePicth();
        calculateAngleAroundPlayer();
        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance() - yOffset;
        calculateCameraPosition(horizontalDistance, verticalDistance);
        this.yaw = 180 - (player.getRotY() + angleAroundPlayer);
//        System.out.println(player.getPosition().x);
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getRoll() {
        return roll;
    }

    private void calculateCameraPosition(float hDistance, float vDistance){
        float theta = player.getRotY() + angleAroundPlayer;
        float offsetX = (float) (hDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (hDistance * Math.cos(Math.toRadians(theta)));

        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = player.getPosition().y + vDistance;
    }

    private float calculateHorizontalDistance(){
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance(){
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom(){
        float zoomLevel = Mouse.getDWheel() * 0.01f;
        distanceFromPlayer -= zoomLevel;
        if(distanceFromPlayer <= minDistanceFromPlayer)
            distanceFromPlayer = minDistanceFromPlayer;
    }

    private void calculatePicth(){
        if(Mouse.isButtonDown(1)){
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
        }
    }

    private void calculateAngleAroundPlayer(){
        if(Mouse.isButtonDown(0)){
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer -= angleChange;
        }
    }
}
