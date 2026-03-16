package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Goober extends SubsystemBase {
    private final TalonFX motor = new TalonFX(Constants.Turret.kMotorId);
    private final DutyCycleOut request = new DutyCycleOut(0);
    private final TalonFXConfiguration configs = new TalonFXConfiguration();

    public Goober() {

    configs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = -90.0;  //-20.0
    configs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;

    configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 28.0; //20.0
    configs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;

    configs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
    
    motor.getConfigurator().apply(configs);
    }

    public void setMotorSpeed(double percent) {
        motor.setControl(request.withOutput(percent * Constants.Turret.kSpeedMultiplier));
    }

    public void stop() {
        motor.stopMotor();
    }

        @Override
    public void periodic() {
        // Fetch the current motor position in rotations
        double currentPosition = motor.getPosition().getValueAsDouble();
        
        // Output to the RioLog/Console
        System.out.println("Motor Position: " + currentPosition);
        
        // Output to SmartDashboard (highly recommended so the console doesn't lag)
        SmartDashboard.putNumber("Motor Position", currentPosition);
    }
}