package frc.robot.subsystems;

import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.MotionMagicVoltage;
import com.ctre.phoenix6.controls.DutyCycleOut;

import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class Goober extends SubsystemBase {
    private final TalonFX motor = new TalonFX(Constants.Turret.kMotorId);

    // MotionMagic for precision position control (runs at 1000Hz inside the TalonFX)
    private final MotionMagicVoltage m_positionRequest = new MotionMagicVoltage(0);

    // DutyCycleOut for manual jog and search sweep only
    private final DutyCycleOut m_dutyCycleRequest = new DutyCycleOut(0);

    // Limelight noise filter - 4 samples balances smoothness vs lag
    private final LinearFilter txFilter = LinearFilter.movingAverage(4);

    // Track whether we currently have a position target
    private boolean m_isTracking = false;

    public Goober() {
        TalonFXConfiguration configs = new TalonFXConfiguration();

        // MotionMagic PID (slot 0) - runs inside TalonFX at 1000Hz
        configs.Slot0.kP = Constants.Turret.kP;
        configs.Slot0.kI = Constants.Turret.kI;
        configs.Slot0.kD = Constants.Turret.kD;
        configs.Slot0.kV = Constants.Turret.kV;

        // MotionMagic motion profile
        configs.MotionMagic.MotionMagicCruiseVelocity = Constants.Turret.kCruiseVelocity;
        configs.MotionMagic.MotionMagicAcceleration = Constants.Turret.kAcceleration;
        configs.MotionMagic.MotionMagicJerk = Constants.Turret.kJerk;

        // Soft limits
        configs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = Constants.Turret.kReverseSoftLimit;
        configs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;
        configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = Constants.Turret.kForwardSoftLimit;
        configs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;

        // Current limit
        configs.CurrentLimits.SupplyCurrentLimit = Constants.Turret.kSupplyCurrentLimit;
        configs.CurrentLimits.SupplyCurrentLimitEnable = true;

        configs.MotorOutput.NeutralMode = NeutralModeValue.Brake;

        motor.getConfigurator().apply(configs);
    }

    /**
     * Called by GooberAlign when tracking a Limelight target.
     * Converts TX (degrees off center) to a motor position and sends to MotionMagic.
     * The TalonFX handles the control loop at 1000Hz internally.
     */
    public void aimAtTarget(double rawTx) {
        // Smooth out Limelight pixel noise
        double smoothedTx = txFilter.calculate(rawTx);

        // Convert TX angle error to a motor rotation target.
        // TX=0 means centered. We offset current position by the error.
        double currentPosition = motor.getPosition().getValueAsDouble();
        double errorInRotations = smoothedTx / Constants.Turret.kDegreesPerMotorRotation;
        double targetPosition = currentPosition - errorInRotations;

        m_isTracking = true;
        motor.setControl(m_positionRequest.withPosition(targetPosition));
    }

    /**
     * Returns true if the turret is within tolerance of its target position.
     */
    public boolean isOnTarget() {
        if (!m_isTracking) return false;
        double error = m_positionRequest.Position - motor.getPosition().getValueAsDouble();
        return Math.abs(error) <= Constants.Turret.kToleranceRotations;
    }

    /**
     * Manual percent output - used for jog and search sweep only.
     */
    public void setMotorSpeed(double percent) {
        m_isTracking = false;
        motor.setControl(m_dutyCycleRequest.withOutput(percent));
    }

    public double getPosition() {
        return motor.getPosition().getValueAsDouble();
    }

    public void stop() {
        m_isTracking = false;
        motor.stopMotor();
    }

    @Override
    public void periodic() {
        SmartDashboard.putNumber("Turret Position (rot)", getPosition());
        SmartDashboard.putNumber("Turret Position (deg)",
            getPosition() * Constants.Turret.kDegreesPerMotorRotation);
        SmartDashboard.putBoolean("Turret On Target", isOnTarget());
    }
}