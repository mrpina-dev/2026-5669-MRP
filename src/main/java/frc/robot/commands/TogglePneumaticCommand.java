package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.PneumaticSubsystem;

public class TogglePneumaticCommand extends Command {
  private final PneumaticSubsystem m_subsystem;

  public TogglePneumaticCommand(PneumaticSubsystem subsystem) {
    m_subsystem = subsystem;
    // Ensure no other command uses this subsystem at the same time
    addRequirements(m_subsystem);
  }

  @Override
  public void initialize() {
    m_subsystem.toggle();
  }

  @Override
  public boolean isFinished() {
    return true; // Command ends immediately after toggling
  }
}