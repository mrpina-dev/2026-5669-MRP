package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.Goober;
import frc.robot.subsystems.MariosEar;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.MathUtil;

public class Mariosearcommand extends Command {
    private final MariosEar vision;
    private final Goober turret;

    private final PIDController turnPID = new PIDController(
        Constants.Turret.kP, Constants.Turret.kI, Constants.Turret.kD
    );

    public Mariosearcommand(MariosEar vision, Goober turret) {
        this.vision = vision;
        this.turret = turret;
        addRequirements(turret); // Vision is read-only, so we don't necessarily need to require it
    }

    @Override
    public void execute() {
        double motorOutput = 0;

        if (vision.limelightHasTarget()) {
            // PHASE 1: Limelight Precision Tracking
            double tx = vision.getLimelightTX();
            motorOutput = turnPID.calculate(tx, 0.0);
            
        } else if (vision.getLeftResult().hasTargets()) {
            // PHASE 2: Left USB Camera found something
            // We use a simple constant speed or a lazy PID to swing the turret left
            motorOutput = -0.3; // Adjust speed and sign (+/-) based on your motor orientation
            
        } else if (vision.getRightResult().hasTargets()) {
            // PHASE 3: Right USB Camera found something
            motorOutput = 0.3; // Swing turret right
            
        } else {
            motorOutput = 0;
        }

        double clampedOutput = MathUtil.clamp(motorOutput, 
            -Constants.Turret.kMaxOutput, Constants.Turret.kMaxOutput);
        
        turret.setMotorSpeed(-clampedOutput);
    }

    @Override
    public void end(boolean interrupted) {
        turret.stop();
    }

    @Override
    public boolean isFinished() {
        return false; // Stay aligned as long as the button is held
    }
    
}