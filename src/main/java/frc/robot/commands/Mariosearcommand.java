package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Goober;
import frc.robot.subsystems.MariosEar;

public class Mariosearcommand extends Command {
    private final MariosEar vision;
    private final Goober turret;

    public Mariosearcommand(MariosEar vision, Goober turret) {
        this.vision = vision;
        this.turret = turret;
        addRequirements(turret);
    }

    @Override
    public void execute() {
        if (vision.limelightHasTarget()) {
            double tx = vision.getLimelightTX();
            turret.aimAtTarget(tx);
        } else if (vision.getLeftResult().hasTargets()) {
            turret.setMotorSpeed(-1.0); // Swing left to find tag 
        } else if (vision.getRightResult().hasTargets()) {
            turret.setMotorSpeed(1.0);  // Swing right to find tag
        } else {
            turret.stop();
        }
    }

    @Override
    public void end(boolean interrupted) {
        turret.stop();
    }

    @Override
    public boolean isFinished() {
        return false; 
    }
}