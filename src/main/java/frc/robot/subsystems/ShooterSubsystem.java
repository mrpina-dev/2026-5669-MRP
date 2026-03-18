package frc.robot.subsystems;

import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
    private final TalonFX leader = new TalonFX(Constants.Shooter.kLeaderId);
    private final TalonFX follower = new TalonFX(Constants.Shooter.kFollowerId);
    
    private final VelocityVoltage m_velocityRequest = new VelocityVoltage(0);

    public ShooterSubsystem() {
        TalonFXConfiguration leaderConfig = new TalonFXConfiguration();
        TalonFXConfiguration followerConfig = new TalonFXConfiguration();

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

        leaderConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        followerConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        // Configurations are now 100% independent based on the two separate booleans
        leaderConfig.MotorOutput.Inverted = Constants.Shooter.kLeaderInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;

        followerConfig.MotorOutput.Inverted = Constants.Shooter.kFollowerInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;

        leader.getConfigurator().apply(leaderConfig);
        follower.getConfigurator().apply(followerConfig);
    }

    public void runAtRPM(double rpm) {
        double rps = rpm / 60.0;
        leader.setControl(m_velocityRequest.withVelocity(rps));
        follower.setControl(m_velocityRequest.withVelocity(rps));
    }

    // --- INDEPENDENT TESTING METHODS ---
    public void testLeaderOnly(double rpm) {
        leader.setControl(m_velocityRequest.withVelocity(rpm / 60.0));
        follower.stopMotor(); // Force the other motor to stay dead
    }

    public void testFollowerOnly(double rpm) {
        follower.setControl(m_velocityRequest.withVelocity(rpm / 60.0));
        leader.stopMotor(); // Force the other motor to stay dead
    }

    public void stop() {
        leader.stopMotor();
        follower.stopMotor();
    }
}