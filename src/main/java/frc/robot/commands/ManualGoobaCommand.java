package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.GoobaSubsystem;

public class ManualGoobaCommand extends Command {
    private final GoobaSubsystem m_gooba;
    private final boolean m_isUp;
    private double m_targetPosition;

    // Speed of manual adjustment (rotations per 20ms tick)
    // 0.02 rot/tick * 50 ticks/sec = 1.0 rotations per second.
    // Change this if it moves too fast or too slow!
    private static final double kJogSpeed = 0.02;

    public ManualGoobaCommand(GoobaSubsystem gooba, boolean isUp) {
        m_gooba = gooba;
        m_isUp = isUp;
        addRequirements(gooba);
    }

    @Override
    public void initialize() {
        // Because your GoobaSubsystem setPosition() negates the input, 
        // we have to negate the raw motor position to get our positive "logical" rotations back.
        m_targetPosition = -m_gooba.getPosition(); 
    }

    @Override
    public void execute() {
        // Increment or decrement the target position
        if (m_isUp) {
            m_targetPosition += kJogSpeed;
        } else {
            m_targetPosition -= kJogSpeed;
        }
        
        // Command the Gooba subsystem to move to the new micro-adjusted position
        m_gooba.setPosition(m_targetPosition);
        
        // PRINT TO DASHBOARD: Write this number down when you make a perfect shot!
        SmartDashboard.putNumber("Gooba Fine-Tune Position", m_targetPosition);
    }

    @Override
    public boolean isFinished() {
        return false; // Run continuously while the button is held
    }

    @Override
    public void end(boolean interrupted) {
        // When the button is released, the command ends.
        // Motion Magic will automatically hold the motor at the very last m_targetPosition.
    }
}