package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class GroundIntakeSubsystem extends SubsystemBase {
    private final TalonFX m_motor;
    private final DoubleSolenoid m_pistons;
    
    // Control request for simple percent output
    private final DutyCycleOut m_request = new DutyCycleOut(0);

    public GroundIntakeSubsystem() {
        // --- MOTOR SETUP (Kraken X44) ---
        m_motor = new TalonFX(Constants.GroundIntake.kMotorId);
        
        TalonFXConfiguration config = new TalonFXConfiguration();
        
        // Current Limits for Kraken X44 (Safety)
        config.CurrentLimits.SupplyCurrentLimit = Constants.GroundIntake.kSupplyCurrentLimit;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;

        // Direction & Brake Mode
        config.MotorOutput.Inverted = Constants.GroundIntake.kInverted ? 
            InvertedValue.Clockwise_Positive : 
            InvertedValue.CounterClockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast; // Coast is usually better for intakes to prevent jamming on stop

        m_motor.getConfigurator().apply(config);

        // --- PNEUMATICS SETUP ---
        // Two pistons connected to one solenoid (Single DoubleSolenoid object)
        m_pistons = new DoubleSolenoid(
            Constants.Pneumatics.kPcmId, 
            PneumaticsModuleType.CTREPCM, 
            Constants.GroundIntake.kForwardChannel, 
            Constants.GroundIntake.kReverseChannel
        );

        // Default state: Retracted (Up)
        m_pistons.set(Value.kReverse);
    }

    /**
     * Runs the intake motor at a specific speed.
     * @param speed Percent output (-1.0 to 1.0)
     */
    public void runIntake(double speed) {
        m_motor.setControl(m_request.withOutput(speed));
    }

    /** Stops the intake motor. */
    public void stop() {
        m_motor.stopMotor();
    }

    /** Deploys the intake (Pistons Out). */
    public void deploy() {
        m_pistons.set(Value.kForward);
    }

    /** Retracts the intake (Pistons In). */
    public void retract() {
        m_pistons.set(Value.kReverse);
    }

    /** Toggles the intake state. */
    public void toggleIntake() {
        m_pistons.toggle();
    }
}