package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;

public class GoobaSubsystem extends SubsystemBase {
    private final TalonFX m_motor = new TalonFX(Constants.Gooba.kMotorId);
    private final InterpolatingDoubleTreeMap shotMap = new InterpolatingDoubleTreeMap();
    
    // Motion Magic Request (Smooth position control)
    private final MotionMagicVoltage m_positionControl = new MotionMagicVoltage(0);

    public GoobaSubsystem() {
        TalonFXConfiguration configs = new TalonFXConfiguration();

        // PID for Position
        configs.Slot0.kP = Constants.Gooba.kP;
        configs.Slot0.kI = Constants.Gooba.kI;
        configs.Slot0.kD = Constants.Gooba.kD;
        
        // Motion Magic parameters
        configs.MotionMagic.MotionMagicCruiseVelocity = Constants.Gooba.kCruiseVelocity;
        configs.MotionMagic.MotionMagicAcceleration = Constants.Gooba.kAcceleration;

        // Current Limits (Essential for Kraken X44 safety)
        configs.CurrentLimits.SupplyCurrentLimit = Constants.Gooba.kSupplyCurrentLimit;
        configs.CurrentLimits.SupplyCurrentLimitEnable = true;

        m_motor.getConfigurator().apply(configs);
        
        // Brake Mode is essential for a servo-like mechanism
        m_motor.setNeutralMode(NeutralModeValue.Brake);
        
        // Reset encoder to 0 on startup
        m_motor.setPosition(0);

        shotMap.put(0.0, 0.0);
    }

    public void setPosition(double rotations) {
        m_motor.setControl(m_positionControl.withPosition(-rotations));
    }

    public double getPosition() {
        return m_motor.getPosition().getValueAsDouble();
    }

    public double getRotationValueFromDistance(double distance) {
        return shotMap.get(distance);
    }
}