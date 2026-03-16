package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PneumaticSubsystem extends SubsystemBase {
  private DoubleSolenoid m_cylinder;
  private Solenoid m_other;

  /**
   * Creates a new PneumaticSubsystem.
   * * @param pcmId The CAN ID of the PCM.
   * @param forwardChannel The forward channel on the PCM.
   * @param reverseChannel The reverse channel on the PCM.
   */
  public PneumaticSubsystem(int pcmId, int channel) {
    m_other = new Solenoid(pcmId, PneumaticsModuleType.CTREPCM, channel);
    m_other.set(false); // Default state
}


  public PneumaticSubsystem(int pcmId, int forwardChannel, int reverseChannel) {
    // Initialize the solenoid on the specified PCM using the specified ports
    m_cylinder = new DoubleSolenoid(
        pcmId, 
        PneumaticsModuleType.CTREPCM, 
        forwardChannel, 
        reverseChannel
    );
    // Set a default state
    m_cylinder.set(Value.kReverse);

  }

  public void extend() {
    m_cylinder.set(Value.kForward);
  }

  public void retract() {
    m_cylinder.set(Value.kReverse);
  }

  public void toggle() {
    m_cylinder.toggle();
  }

  public void toggleSingle() {
    m_other.toggle();
  }
}