package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.PhysicsJoint;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Matrix3f;
import com.jme3.math.Vector3f;
import static com.jme3.math.Vector3f.ZERO;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

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
    private BulletAppState bas;
    private final float ud = 5968.31f; // Universal density

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {

        /**
         * Set up Physics Game.
         */
        bas = new BulletAppState();
        stateManager.attach(bas);

        flyCam.setMoveSpeed(30 * speed);

        initGround();
        createRagDoll(ZERO);
    }

    private void initGround() {

        Box ground = new Box(10, 1, 10);
        Geometry ground_geom = new Geometry("Box", ground);

        ground_geom.setLocalTranslation(Vector3f.UNIT_Y.mult(-1));

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        ground_geom.setMaterial(mat);

        RigidBodyControl ground_rbc = new RigidBodyControl(0);

        ground_geom.addControl(ground_rbc);
        bas.getPhysicsSpace().add(ground_rbc);

        rootNode.attachChild(ground_geom);
    }

    private void createRagDoll(Vector3f location) {

        createLimb(0.1f, 0.1f, 0.015f, Vector3f.UNIT_Y.mult(1.7f).add(location)); // Head
        createLimb(0.125f, 0.2f, 0.015f, Vector3f.UNIT_Y.mult(1.3f).add(location)); // Torso
        createLimb(0.125f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(0.95f).add(location)); // Pelvis

        // Arms
        createLimb(0.1f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(1.45f).add(Vector3f.UNIT_X.mult(0.325f)).add(location));
        createLimb(0.1f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(1.45f).add(Vector3f.UNIT_X.mult(-0.325f)).add(location));
        createLimb(0.15f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(1.45f).add(Vector3f.UNIT_X.mult(0.675f)).add(location));
        createLimb(0.15f, 0.05f, 0.015f, Vector3f.UNIT_Y.mult(1.45f).add(Vector3f.UNIT_X.mult(-0.675f)).add(location));

        // Legs
        createLimb(0.05f, 0.15f, 0.015f, Vector3f.UNIT_Y.mult(0.65f).add(Vector3f.UNIT_X.mult(0.075f)).add(location));
        createLimb(0.05f, 0.15f, 0.015f, Vector3f.UNIT_Y.mult(0.65f).add(Vector3f.UNIT_X.mult(-0.075f)).add(location));
        createLimb(0.05f, 0.2f, 0.015f, Vector3f.UNIT_Y.mult(0.2f).add(Vector3f.UNIT_X.mult(0.075f)).add(location));
        createLimb(0.05f, 0.2f, 0.015f, Vector3f.UNIT_Y.mult(0.2f).add(Vector3f.UNIT_X.mult(-0.075f)).add(location));
    }

    private void createLimb(float dimx, float dimy, float dimz, Vector3f location) {

        Box limb = new Box(dimx, dimy, dimz);
        Geometry limb_geom = new Geometry("Limb", limb);

        limb_geom.setLocalTranslation(location);

        Material limb_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        limb_mat.setColor("Color", ColorRGBA.Blue);
        limb_geom.setMaterial(limb_mat);

        RigidBodyControl limb_rbc = new RigidBodyControl(/*dimx * dimy * dimz * ud*/0);

        limb_geom.addControl(limb_rbc);
        bas.getPhysicsSpace().add(limb_rbc);

        rootNode.attachChild(limb_geom);
    }

    private void destroyLink(Link link) {
        if (link.getAppliedImpulse() > link.getLimit()) {
            bas.getPhysicsSpace().remove(link);
        }
    }

    private void link(float limit, PhysicsRigidBody prb1, PhysicsRigidBody prb2, Vector3f pivot1, Vector3f pivot2) {
        Link link = new Link(limit, prb1, prb2, pivot1, pivot2, Matrix3f.IDENTITY, Matrix3f.IDENTITY, true);
        link.enableSpring(0, true);
        link.setStiffness(0, 0.1f);
        link.setLinearUpperLimit(Vector3f.UNIT_X.mult(5 * limit));
        bas.getPhysicsSpace().add(link);
    }

    @Override
    public void simpleUpdate(float tpf) {
        for (PhysicsJoint joint : bas.getPhysicsSpace().getJointList()) {
            if (joint instanceof Link) {
                destroyLink((Link) joint);
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
