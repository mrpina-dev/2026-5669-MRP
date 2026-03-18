package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Goober extends SubsystemBase {
    private final TalonFX motor = new TalonFX(Constants.Turret.kMotorId);
    private final DutyCycleOut request = new DutyCycleOut(0);
    private final TalonFXConfiguration configs = new TalonFXConfiguration();

    private final PIDController turnPID = new PIDController(
        Constants.Turret.kP, Constants.Turret.kI, Constants.Turret.kD
    );

    public Goober() {
        configs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = -90.0;
        configs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;

        configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 28.0; 
        configs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;

        configs.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        
        motor.getConfigurator().apply(configs);
        turnPID.setTolerance(Constants.Turret.kToleranceDegrees); 
    }

    public void setMotorSpeed(double percent) {
        motor.setControl(request.withOutput(percent * Constants.Turret.kSpeedMultiplier));
    }

    public void aimAtTarget(double tx) {
        double pidOutput = turnPID.calculate(tx, 0.0);
        double clampedOutput = MathUtil.clamp(
            pidOutput, 
            -Constants.Turret.kMaxOutput, 
            Constants.Turret.kMaxOutput
        );
        setMotorSpeed(-clampedOutput);
    }

    public double getPosition() {
        return motor.getPosition().getValueAsDouble();
    }

    public void stop() {
        motor.stopMotor();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Motor Position", getPosition());
    }
}