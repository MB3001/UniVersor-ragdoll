package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import static com.jme3.math.Vector3f.ZERO;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 *
 * @author Matías Bonino
 */
public class Main extends SimpleApplication {

    /**
     * Prepare physics.
     */
    private BulletAppState bulletAppState = new BulletAppState();
    private final float ud = 5968.31f; // Universal density
    private Node ragDoll = new Node();
    private Node head;
    private Node torso;
    private Vector3f upForce = Vector3f.UNIT_Y.mult(80);
    private boolean applyForceUp = true;
    private static Sphere sphere;

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    static {
        sphere = new Sphere(32, 32, 0.5f, true, false);
        sphere.setTextureMode(Sphere.TextureMode.Projected);
    }

    @Override
    public void simpleInitApp() {

        /**
         * Set up Physics Game.
         */
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        flyCam.setMoveSpeed(30 * speed);
        cam.setLocation(new Vector3f(0, 1, 5));

        initGround();
        initBall();
        createRagDoll(ZERO);
        initKeys();
    }

    private void initBall() {

        Geometry ball_geo = new Geometry("Evil ball", sphere);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Red);
        ball_geo.setMaterial(mat);
        ball_geo.setLocalTranslation(Vector3f.UNIT_Y.mult(20));
        rootNode.attachChild(ball_geo);

        RigidBodyControl ball_phy = new RigidBodyControl(
                ((float) 4 / 3) * (float) Math.PI * (float) Math.pow(sphere.getRadius(), 3) * ud);

        ball_geo.addControl(ball_phy);
        bulletAppState.getPhysicsSpace().add(ball_phy);
    }

    private void initGround() {

        Box ground = new Box(1000, 1, 1000);
        Geometry ground_geom = new Geometry("Box", ground);

        ground_geom.setLocalTranslation(Vector3f.UNIT_Y.mult(-1));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        Texture grass = assetManager.loadTexture("Materials/francegrassfull.jpg");
        mat.setTexture("ColorMap", grass);
        ground_geom.setMaterial(mat);

        RigidBodyControl ground_rbc = new RigidBodyControl(0);

        ground_geom.addControl(ground_rbc);
        bulletAppState.getPhysicsSpace().add(ground_rbc);

        rootNode.attachChild(ground_geom);
    }

    private void initKeys() {
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Fly", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Run", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping("Back", new KeyTrigger(KeyInput.KEY_M));
        inputManager.addListener(actionListener, "Jump");
        inputManager.addListener(analogListener, "Run", "Left", "Right", "Fly", "Back");

    }

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
            if (name.equals("Jump") && !keyPressed) {
                torso.getControl(RigidBodyControl.class).applyImpulse(Vector3f.UNIT_Y.mult(20), ZERO);
            }
        }
    };
    private final AnalogListener analogListener = new AnalogListener() {
        @Override
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Fly")) {
                torso.getControl(RigidBodyControl.class).applyCentralForce(Vector3f.UNIT_Y.mult(30));
            }
            if (name.equals("Run")) {
                torso.getControl(RigidBodyControl.class).applyCentralForce(Vector3f.UNIT_Z.mult(-10f));
            }
            if (name.equals("Right")) {
                torso.getControl(RigidBodyControl.class).applyCentralForce(Vector3f.UNIT_X.mult(10));
            }
            if (name.equals("Left")) {
                torso.getControl(RigidBodyControl.class).applyCentralForce(Vector3f.UNIT_X.mult(-10));
            }
            if (name.equals("Back")) {
                torso.getControl(RigidBodyControl.class).applyCentralForce(Vector3f.UNIT_Z.mult(10));
            }
        }
    };

    private void createRagDoll(Vector3f location) {

        head = createLimb(0.1f, 0.1f, 0.015f, Vector3f.UNIT_Y.mult(1.7f).add(location));
        torso = createLimb(0.125f, 0.2f, 0.015f, Vector3f.UNIT_Y.mult(1.3f).add(location));
        Node hips = createLimb(0.125f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(0.95f).add(location));

        Node uArmL = createLimb(0.015f, 0.1f, 0.05f, new Vector3f(-0.24f, 1.4f, 0).add(location));
        Node uArmR = createLimb(0.015f, 0.1f, 0.05f, new Vector3f(0.24f, 1.4f, 0).add(location));
        Node lArmL = createLimb(0.015f, 0.15f, 0.05f, new Vector3f(-0.24f, 1.05f, 0).add(location));
        Node lArmR = createLimb(0.015f, 0.15f, 0.05f, new Vector3f(0.24f, 1.05f, 0).add(location));

        Node uLegL = createLimb(0.05f, 0.15f, 0.015f, new Vector3f(-0.075f, 0.65f, 0).add(location));
        Node uLegR = createLimb(0.05f, 0.15f, 0.015f, new Vector3f(0.075f, 0.65f, 0).add(location));
        Node lLegL = createLimb(0.05f, 0.2f, 0.015f, new Vector3f(-0.075f, 0.2f, 0).add(location));
        Node lLegR = createLimb(0.05f, 0.2f, 0.015f, new Vector3f(0.075f, 0.65f, 0).add(location));

        link(2f, head, torso, new Vector3f(-0.05f, -0.2f, 0), new Vector3f(-0.05f, 0.2f, 0));
        link(2f, head, torso, new Vector3f(0.05f, -0.2f, 0), new Vector3f(0.05f, 0.2f, 0));
        link(2f, torso, hips, new Vector3f(-0.1f, -0.2f, 0), new Vector3f(-0.1f, 0.15f, 0));
        link(2f, torso, hips, new Vector3f(0.1f, -0.2f, 0), new Vector3f(0.1f, 0.15f, 0));

        link(2f, uArmL, torso, new Vector3f(0.115f, 0.1f, -0.05f), new Vector3f(-0.125f, 0.185f, -0.05f));
        link(2f, uArmL, torso, new Vector3f(0.115f, 0.1f, 0.05f), new Vector3f(-0.125f, 0.185f, 0.05f));
        link(2f, uArmR, torso, new Vector3f(-0.115f, 0.1f, -0.05f), new Vector3f(0.125f, 0.185f, -0.05f));
        link(2f, uArmR, torso, new Vector3f(-0.115f, 0.1f, 0.05f), new Vector3f(0.125f, 0.185f, 0.05f));
        link(2f, lArmL, uArmL, new Vector3f(0, 0.25f, 0.05f), new Vector3f(0, -0.1f, 0.05f));
        link(2f, lArmL, uArmL, new Vector3f(0, 0.25f, -0.05f), new Vector3f(0, -0.1f, -0.05f));
        link(2f, lArmR, uArmR, new Vector3f(0, 0.25f, 0.05f), new Vector3f(0, -0.1f, 0.05f));
        link(2f, lArmR, uArmR, new Vector3f(0, 0.25f, -0.05f), new Vector3f(0, -0.1f, -0.05f));

        link(2f, hips, uLegL, new Vector3f(-0.175f, -0.05f, 0), new Vector3f(-0.05f, 0.25f, 0));
        link(2f, hips, uLegL, new Vector3f(-0.075f, -0.05f, 0), new Vector3f(0.05f, 0.25f, 0));
        link(2f, hips, uLegR, new Vector3f(0.175f, -0.05f, 0), new Vector3f(0.05f, 0.25f, 0));
        link(2f, hips, uLegR, new Vector3f(0.075f, -0.05f, 0), new Vector3f(-0.05f, 0.25f, 0));
        link(2f, uLegL, lLegL, new Vector3f(-0.05f, -0.25f, 0), new Vector3f(-0.05f, 0.3f, 0));
        link(2f, uLegL, lLegL, new Vector3f(0.05f, -0.25f, 0), new Vector3f(0.05f, 0.3f, 0));
        link(2f, uLegR, lLegR, new Vector3f(-0.05f, -0.25f, 0), new Vector3f(-0.05f, 0.3f, 0));
        link(2f, uLegR, lLegR, new Vector3f(0.05f, -0.25f, 0), new Vector3f(0.05f, 0.3f, 0));

        ragDoll.attachChild(head);
        ragDoll.attachChild(torso);
        ragDoll.attachChild(hips);
        ragDoll.attachChild(uArmL);
        ragDoll.attachChild(uArmR);
        ragDoll.attachChild(lArmL);
        ragDoll.attachChild(lArmR);
        ragDoll.attachChild(uLegL);
        ragDoll.attachChild(uLegR);
        ragDoll.attachChild(lLegL);
        ragDoll.attachChild(lLegR);

        rootNode.attachChild(ragDoll);
        bulletAppState.getPhysicsSpace().addAll(ragDoll);
    }

    private Node createLimb(float dimx, float dimy, float dimz, Vector3f location) {

        Node node = new Node("Limb");
        Box limb = new Box(dimx, dimy, dimz);
        Geometry limb_geom = new Geometry("Limb", limb);

        Material limb_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        limb_mat.setColor("Color", ColorRGBA.Blue);
        limb_geom.setMaterial(limb_mat);

        RigidBodyControl limb_rbc = new RigidBodyControl(/**/dimx * dimy * dimz * ud/**//*0*/);
        node.setLocalTranslation(location);
        node.attachChild(limb_geom);
        node.addControl(limb_rbc);

        return node;
    }

    private void destroyLink(Link link) {
        if (link.getAppliedImpulse() > link.getLimit()) {
            bulletAppState.getPhysicsSpace().remove(link);
            applyForceUp = false;
        }
    }

    private PhysicsJoint link(float limit, Node A, Node B, Vector3f pivotA, Vector3f pivotB) {

        Link link = new Link(limit, A.getControl(RigidBodyControl.class), B.getControl(RigidBodyControl.class), pivotA, pivotB, Matrix3f.IDENTITY, Matrix3f.IDENTITY, true);

        final float maximum_stretch_factor = 0.01f;
        final float stiffness = 0.1f;

        link.enableSpring(0, true);
        link.setStiffness(0, stiffness);
        link.setLinearUpperLimit(Vector3f.UNIT_X.mult(maximum_stretch_factor * limit));

        link.enableSpring(1, true);
        link.setStiffness(1, stiffness);
        link.setLinearUpperLimit(Vector3f.UNIT_Y.mult(maximum_stretch_factor * limit));

        link.enableSpring(2, true);
        link.setStiffness(2, stiffness);
        link.setLinearUpperLimit(Vector3f.UNIT_Z.mult(maximum_stretch_factor * limit));

        bulletAppState.getPhysicsSpace().add(link);

        return link;
    }

    @Override
    public void simpleUpdate(float tpf) {

        // Destroy links if they exceed their limits.
        for (PhysicsJoint joint : bulletAppState.getPhysicsSpace().getJointList()) {
            if (joint instanceof Link) {
                destroyLink((Link) joint);
            }
        }

        // Keep the rag doll standing.
        if (applyForceUp) {
            head.getControl(RigidBodyControl.class).applyCentralForce(upForce);
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
