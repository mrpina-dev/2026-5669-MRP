package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.GoobaSubsystem;
import frc.robot.subsystems.LimelightSubsystem;


public class AutoGooba extends Command {
    private final GoobaSubsystem m_hood;
    private final LimelightSubsystem m_vision;

    /**
     * @param hood 
     * @param vision 
     */
    public AutoGooba(GoobaSubsystem hood, LimelightSubsystem vision) {
        m_hood = hood;
        m_vision = vision;

       
        addRequirements(m_hood);
    }

    @Override
    public void initialize() {
        
        System.out.println("Gooba Auto-Aim Started");
    }

    @Override
    public void execute() {
        if (m_vision.isTargetAvailable()) {
            // calculate distance from the tag with fancy math
            double distance = m_vision.distanceToTarget();

            // get the "angle"
            double targetRotations = m_hood.getRotationValueFromDistance(distance);

            // motor go
            m_hood.setPosition(targetRotations);
        } else {
            // If the tag is lost, you can either stay put or go to a default position
            // m_hood.setPosition(Constants.Gooba.kPositionStowed);
        }
    }

    @Override
    public void end(boolean interrupted) {
        // When the button is released, stop the motor or keep last position
        // m_hood.stop(); 
        System.out.println("Gooba Auto-Aim Ended");
    }

    @Override
    public boolean isFinished() {
      
        return false;
    }
}
