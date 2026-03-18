package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
    // ID init
    private final TalonFX leader = new TalonFX(Constants.Shooter.kLeaderId);
    private final TalonFX follower = new TalonFX(Constants.Shooter.kFollowerId);
    
    // Controls - STRICTLY VELOCITY (RPM)
    private final VelocityVoltage m_velocityRequest = new VelocityVoltage(0);

    // ==========================================
    // THE SOFTWARE WIND-DOWN (SLEW RATE LIMITER)
    // ==========================================
    // If max speed is 5000 RPM, and we want a 10-second wind-down...
    // 5000 / 10 = 500 RPM per second maximum change.
    private final SlewRateLimiter m_rpmLimiter = new SlewRateLimiter(500.0);
    private double m_targetRPM = 0.0;

    public ShooterSubsystem() {
        TalonFXConfiguration leaderConfig = new TalonFXConfiguration();
        TalonFXConfiguration followerConfig = new TalonFXConfiguration();

        // PID & Ramp Configurations
        leaderConfig.Slot0.kP = Constants.Shooter.kP; 
        leaderConfig.Slot0.kV = Constants.Shooter.kV;
        leaderConfig.Slot0.kI = Constants.Shooter.kI;
        leaderConfig.Slot0.kD = Constants.Shooter.kD;
        leaderConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = Constants.Shooter.kVoltageRampPeriod;
        leaderConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = Constants.Shooter.kDutyCycleRampPeriod;

        followerConfig.Slot0.kP = Constants.Shooter.kP; 
        followerConfig.Slot0.kV = Constants.Shooter.kV;
        followerConfig.Slot0.kI = Constants.Shooter.kI;
        followerConfig.Slot0.kD = Constants.Shooter.kD;
        followerConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = Constants.Shooter.kVoltageRampPeriod;
        followerConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = Constants.Shooter.kDutyCycleRampPeriod;

        // Keep hardware Coast mode enabled as a safety net in case the robot is Disabled
        leaderConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        followerConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        // Direction Alignments (Opposing Spin Fix)
        leaderConfig.MotorOutput.Inverted = Constants.Shooter.kLeaderInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;

        followerConfig.MotorOutput.Inverted = Constants.Shooter.kLeaderInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive;

        leader.getConfigurator().apply(leaderConfig);
        follower.getConfigurator().apply(followerConfig);
    }

    public void runAtRPM(double rpm) {
        // Just update our target, the periodic loop handles the actual motor control
        m_targetRPM = rpm;
    }

    public void stop() {
        // Update the target to 0, which triggers the 10-second slew rate ramp down
        m_targetRPM = 0.0;
    }

    @Override
    public void periodic() {
        // Calculate the next safe RPM step based on our 500 RPM/sec limit
        double safeCommandedRPM = m_rpmLimiter.calculate(m_targetRPM);

        // If the commanded speed is very close to 0, completely cut power to save battery
        if (Math.abs(safeCommandedRPM) < 10.0) {
            leader.stopMotor();
            follower.stopMotor();
        } else {
            // Otherwise, send the actively ramped-down speed to the motors
            double rps = safeCommandedRPM / 60.0;
            leader.setControl(m_velocityRequest.withVelocity(rps));
            follower.setControl(m_velocityRequest.withVelocity(rps));
        }
    }
}