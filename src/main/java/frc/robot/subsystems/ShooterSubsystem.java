package frc.robot.subsystems;

import com.ctre.phoenix6.configs.CurrentLimitsConfigs;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.VelocityVoltage;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class ShooterSubsystem extends SubsystemBase {
    private final TalonFX leader = new TalonFX(Constants.Shooter.kLeaderId);
    private final TalonFX follower = new TalonFX(Constants.Shooter.kFollowerId);
    
    private final VelocityVoltage m_velocityRequest = new VelocityVoltage(0);
    
    private double currentTargetRpm = 0.0;

    public ShooterSubsystem() {
        TalonFXConfiguration leaderConfig = new TalonFXConfiguration();
        TalonFXConfiguration followerConfig = new TalonFXConfiguration();

        leaderConfig.Slot0.kP = Constants.Shooter.kP; 
        leaderConfig.Slot0.kV = Constants.Shooter.kV;
        leaderConfig.Slot0.kI = Constants.Shooter.kI;
        leaderConfig.Slot0.kD = Constants.Shooter.kD;
        
        followerConfig.Slot0.kP = Constants.Shooter.kP; 
        followerConfig.Slot0.kV = Constants.Shooter.kV;
        followerConfig.Slot0.kI = Constants.Shooter.kI;
        followerConfig.Slot0.kD = Constants.Shooter.kD;

        leaderConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = Constants.Shooter.kVoltageRampPeriod;
        leaderConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = Constants.Shooter.kDutyCycleRampPeriod;
        followerConfig.ClosedLoopRamps.VoltageClosedLoopRampPeriod = Constants.Shooter.kVoltageRampPeriod;
        followerConfig.ClosedLoopRamps.DutyCycleClosedLoopRampPeriod = Constants.Shooter.kDutyCycleRampPeriod;

        // MAX PERFORMANCE LIMITS: Tied directly to Constants for easy tuning
        CurrentLimitsConfigs currentLimits = new CurrentLimitsConfigs();
        currentLimits.SupplyCurrentLimit = Constants.Shooter.kSupplyCurrentLimit; 
        currentLimits.SupplyCurrentLimitEnable = true;

        currentLimits.StatorCurrentLimit = Constants.Shooter.kStatorCurrentLimit; 
        currentLimits.StatorCurrentLimitEnable = true;

        leaderConfig.CurrentLimits = currentLimits;
        followerConfig.CurrentLimits = currentLimits;

        leaderConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        followerConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;

        leaderConfig.MotorOutput.Inverted = Constants.Shooter.kLeaderInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;

        followerConfig.MotorOutput.Inverted = Constants.Shooter.kFollowerInverted ? 
            com.ctre.phoenix6.signals.InvertedValue.Clockwise_Positive : 
            com.ctre.phoenix6.signals.InvertedValue.CounterClockwise_Positive;

        leader.getConfigurator().apply(leaderConfig);
        follower.getConfigurator().apply(followerConfig);
    }

    public double getCurrentRpm() {
        // Phoenix 6 returns Rotations per Second, so we multiply by 60 to get RPM
        return leader.getVelocity().getValueAsDouble() * 60.0;
    }

    public void runAtRPM(double targetRpm) {
        currentTargetRpm = targetRpm;
        
        double currentPhysicalRpm = getCurrentRpm();
        double nextTargetRps;

        // SMOOTH SPIN-DOWN LOGIC
        if (currentPhysicalRpm > targetRpm + 100.0) {
            double gentleRampDownRpm = currentPhysicalRpm - Constants.Shooter.kDecelerateStep; 
            if (gentleRampDownRpm < targetRpm) {
                gentleRampDownRpm = targetRpm;
            }
            nextTargetRps = gentleRampDownRpm / 60.0;
        } else {
            // Instant application for max acceleration
            nextTargetRps = targetRpm / 60.0;
        }

        leader.setControl(m_velocityRequest.withVelocity(nextTargetRps));
        follower.setControl(m_velocityRequest.withVelocity(nextTargetRps));
    }

    public void testLeaderOnly(double rpm) {
        currentTargetRpm = rpm;
        leader.setControl(m_velocityRequest.withVelocity(rpm / 60.0));
        follower.stopMotor(); 
    }

    public void testFollowerOnly(double rpm) {
        currentTargetRpm = rpm;
        follower.setControl(m_velocityRequest.withVelocity(rpm / 60.0));
        leader.stopMotor(); 
    }

    public void stop() {
        currentTargetRpm = 0.0;
        leader.stopMotor();
        follower.stopMotor();
    }

    @Override
    public void periodic() {
        
        SmartDashboard.putNumber("Shooter Actual RPM", getCurrentRpm());
        SmartDashboard.putNumber("Shooter Target RPM", currentTargetRpm);
        
        // Grab the live stator current (torque) so you can physically see the acceleration effort
        double currentDraw = Math.round(leader.getStatorCurrent().getValueAsDouble() * 10.0) / 10.0;
        
      //  System.out.println("SHOOTER RPM: " + Math.round(getCurrentRpm()) + 
       //                    " | TARGET: " + currentTargetRpm + 
       //                    " | TORQUE AMPS: " + currentDraw);
    }
}