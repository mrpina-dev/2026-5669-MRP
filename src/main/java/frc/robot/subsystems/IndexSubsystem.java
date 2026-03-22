package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class IndexSubsystem extends SubsystemBase {
    private final TalonFX motor = new TalonFX(Constants.Index.kMotorId);
    private final DutyCycleOut request = new DutyCycleOut(0);

    public IndexSubsystem() {
        TalonFXConfiguration config = new TalonFXConfiguration();
        config.MotorOutput.Inverted = Constants.Index.kInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;
        
        config.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        // REDLINE LIMITS: Maximum power without triggering hardware fault
        CurrentLimitsConfigs currentLimits = config.CurrentLimits;
        currentLimits.SupplyCurrentLimit = 30.0; // Max draw from battery
        currentLimits.SupplyCurrentLimitEnable = true;

        currentLimits.StatorCurrentLimit = 40.0; // Max push to motor coils
        currentLimits.StatorCurrentLimitEnable = true;
        
        motor.getConfigurator().apply(config);
    }

    public void run(double speed) {
        motor.setControl(request.withOutput(speed));
    }

    public void stop() {
        motor.stopMotor();
    }
}