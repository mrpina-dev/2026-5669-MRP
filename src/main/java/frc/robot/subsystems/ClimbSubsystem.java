package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard; // Added for Dashboard logging
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ClimbSubsystem extends SubsystemBase {
    private final TalonFX m_motor;
    private final DoubleSolenoid m_climbPistons;
    
    // Control request for simple percent output
    private final DutyCycleOut m_request = new DutyCycleOut(0);

    public ClimbSubsystem() {
        // --- MOTOR SETUP (Kraken X60) ---
        m_motor = new TalonFX(Constants.Climb.kMotorId);
        
        TalonFXConfiguration config = new TalonFXConfiguration();
        
        // Current Limits for Kraken X60 (Crucial to protect the 1:100 gearbox and ratchet)
        config.CurrentLimits.SupplyCurrentLimit = Constants.Climb.kSupplyCurrentLimit;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;

        //SoftSTOP
        config.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 135.0; 
        config.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;

        config.SoftwareLimitSwitch.ReverseSoftLimitThreshold = -25.0; //-1
        config.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;

        // Direction & Brake Mode
        config.MotorOutput.Inverted = Constants.Climb.kMotorInverted ? 
            InvertedValue.Clockwise_Positive : 
            InvertedValue.CounterClockwise_Positive;
            
        // MUST be brake mode so the robot doesn't drop when unpowered
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake; 

        m_motor.getConfigurator().apply(config);

        // --- PNEUMATICS SETUP ---
        // Both pistons are controlled by this single solenoid
        m_climbPistons = new DoubleSolenoid(
            Constants.Pneumatics.kPcmId, 
            PneumaticsModuleType.CTREPCM, 
            Constants.Climb.kPistonsForward, 
            Constants.Climb.kPistonsReverse
        );

        // Default state: Retracted (Undeployed)
        m_climbPistons.set(Value.kReverse);
    }

    /**
     * Runs the climb motor at a specific speed.
     * @param speed Percent output (-1.0 to 1.0)
     */
    public void runMotor(double speed) {
        m_motor.setControl(m_request.withOutput(speed));
    }

    /** Stops the climb motor. */
    public void stop() {
        m_motor.stopMotor();
    }

    /** Toggles the climb pistons. */
    public void togglePistons() {
        m_climbPistons.toggle();
    }

    // ==========================================
    // ADDED: Periodic method for logging
    // ==========================================
    @Override
    public void periodic() {
        // Fetch the current motor position in rotations
        double currentPosition = m_motor.getPosition().getValueAsDouble();
        
        // Output to the RioLog/Console
        System.out.println("Climb Motor Position: " + currentPosition);
        
        // Output to SmartDashboard (highly recommended so the console doesn't lag)
        SmartDashboard.putNumber("Climb Motor Position", currentPosition);
    }
}