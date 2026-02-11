package frc.robot.subsystems;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.photonvision.PhotonCamera;
import org.photonvision.targeting.PhotonPipelineResult;


public class MariosEar extends SubsystemBase {
    // Nicknames must match the PhotonVision Dashboard
   // private final PhotonCamera camLeft = new PhotonCamera("USB_Left");
    private final PhotonCamera camRight = new PhotonCamera("Fisheye");
    private final LimelightSubsystem limelight;

    public MariosEar(LimelightSubsystem limelight) {
        this.limelight = limelight;
    }

    public boolean limelightHasTarget() {
        return limelight.isTargetAvailable();

    }

    public double getLimelightTX() {
        return limelight.getTX();
    }

  /*   public PhotonPipelineResult getLeftResult() {
        return camLeft.getLatestResult();
    }
*/
   public PhotonPipelineResult getRightResult() {
        return camRight.getLatestResult();
    }
}