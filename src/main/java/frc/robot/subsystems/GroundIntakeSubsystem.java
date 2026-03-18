package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VoltageOut;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.signals.InvertedValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class GroundIntakeSubsystem extends SubsystemBase {
    private final TalonFX m_motor;
    private final VoltageOut m_request = new VoltageOut(0);

    public GroundIntakeSubsystem() {
        m_motor = new TalonFX(Constants.GroundIntake.kMotorId);
        
        TalonFXConfiguration config = new TalonFXConfiguration();
        
        config.CurrentLimits.SupplyCurrentLimit = Constants.GroundIntake.kSupplyCurrentLimit;
        config.CurrentLimits.SupplyCurrentLimitEnable = true;

        config.MotorOutput.Inverted = Constants.GroundIntake.kInverted ? 
            InvertedValue.Clockwise_Positive : 
            InvertedValue.CounterClockwise_Positive;
        config.MotorOutput.NeutralMode = NeutralModeValue.Coast; 

        m_motor.getConfigurator().apply(config);
    }

    public void runIntake(double speed) {
        m_motor.setControl(m_request.withOutput(speed * 12.0));
    }

    public void stop() {
        m_motor.stopMotor();
    }
}