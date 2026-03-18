package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Goober;
import frc.robot.subsystems.LimelightSubsystem;

public class GooberAlign extends Command {
    private final LimelightSubsystem limelight;
    private final Goober turret;
    private int seekDirection = 0;

    public GooberAlign(LimelightSubsystem limelight, Goober turret) {
        this.limelight = limelight;
        this.turret = turret;
        addRequirements(turret, limelight);
    }

    @Override
    public void execute() {
        if (limelight.isTargetAvailable()) {
            seekDirection = 0;
            double tx = -limelight.getNewTX();
            turret.aimAtTarget(tx);
        } else {
            double currentPosition = turret.getPosition();

            // The Unwrap Trigger (1 rotation before hitting hard limits)
            if (currentPosition >= 27.0) {
                seekDirection = -1; 
            } else if (currentPosition <= -89.0) {
                seekDirection = 1;  
            }

            if (seekDirection == 1) {
                turret.setMotorSpeed(1.0); 
            } else if (seekDirection == -1) {
                turret.setMotorSpeed(-1.0);
            } else {
                turret.stop();
            }
        }
    }

    @Override
    public boolean isFinished() {
       return false;
    }

    @Override
    public void end(boolean interrupted) {
        turret.stop();
        seekDirection = 0; 
    }
}