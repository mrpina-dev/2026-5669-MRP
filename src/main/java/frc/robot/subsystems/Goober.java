package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Goober extends SubsystemBase {
    private final TalonFX motor = new TalonFX(Constants.Turret.kMotorId);
    private final DutyCycleOut request = new DutyCycleOut(0);
    private final TalonFXConfiguration configs = new TalonFXConfiguration();

    public Goober() {

    /*configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 0.0;  //-20.0
    configs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;

    configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 0.0; //20.0
    configs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
*/
    
    motor.getConfigurator().apply(configs);
    }

    public void setMotorSpeed(double percent) {
        motor.setControl(request.withOutput(percent * Constants.Turret.kSpeedMultiplier));
    }

    public void stop() {
        motor.stopMotor();
    }
}