package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.GroundIntakeSubsystem;
import frc.robot.Constants;

public class RunGroundIntakeCommand extends Command {
    private final GroundIntakeSubsystem m_subsystem;
    private final double m_speed;

    public RunGroundIntakeCommand(GroundIntakeSubsystem subsystem) {
        m_subsystem = subsystem;
        m_speed = Constants.GroundIntake.kIntakeSpeed;
        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        // Optional: Auto-deploy when running intake? 
        // m_subsystem.deploy();
    }

    @Override
    public void execute() {
        m_subsystem.runIntake(m_speed);
    }

    @Override
    public void end(boolean interrupted) {
        m_subsystem.stop();
        // Optional: Auto-retract when released?
        // m_subsystem.retract();
    }
}