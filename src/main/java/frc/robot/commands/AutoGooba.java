package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.GoobaSubsystem;
import frc.robot.subsystems.LimelightSubsystem;

public class AutoGooba extends Command {
    private final GoobaSubsystem m_gooba;
    private final LimelightSubsystem m_vision;

    public AutoGooba(GoobaSubsystem gooba, LimelightSubsystem vision) {
        this.m_gooba = gooba;
        this.m_vision = vision;
        
        // CRITICAL FIX 1: The command must "own" the gooba subsystem to move it.
        // Do NOT add m_vision to the requirements here. If you do, it will 
        // crash and conflict with your GooberAlign turret tracking!
        addRequirements(m_gooba); 
    }

    @Override
    public void initialize() {
        // Nothing needed here
    }

    @Override
    public void execute() {
        double distance = m_vision.distanceToTarget();

        // CRITICAL FIX 2: We only want to interpolate and move if the Limelight 
        // actually sees a target (distance > 0). If it returns 0, the target is lost, 
        // and we want the hood to stay exactly where it is until it finds the tag again.
        if (distance > 0.1) {
            double targetRotations = m_gooba.getRotationValueFromDistance(distance);
            m_gooba.setPosition(targetRotations);
        }
    }

    @Override
    public void end(boolean interrupted) {
        // Nothing needed here
    }

    @Override
    public boolean isFinished() {
        // CRITICAL FIX 3: This MUST return false. 
        // If it returns true, the command runs exactly once and stops forever.
        return false; 
    }
}