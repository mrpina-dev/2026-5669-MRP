package frc.robot;

import static edu.wpi.first.units.Units.*;
import edu.wpi.first.units.measure.AngularVelocity;

public final class Constants {

    public static final class Operator {
        public static final int kDriverControllerPort = 0;
        public static final int kOperatorControllerPort = 1;
        
        public static final double kDeadband = 0.05;
        public static final double kRotationalDeadband = 0.05;

        public static final AngularVelocity kMaxAngularRate = RotationsPerSecond.of(0.75);
    }

    public static final class Pneumatics {
        public static final int kPcmId = 25; //fixed
        
        // Solenoid 1 (e.g. Intake Piston)
        public static final int kSol1Forward = 4; //fixed
        public static final int kSol1Reverse = 5;

        // Solenoid 2 ( intake 2 Piston)
       public static final int kSol2Forward = 2; //fixed
        public static final int kSol2Reverse = 3;

        //single solenoid
        public static final int kSol1Single = 0;
        public static final int kSolSingle = 1;

        // (climb ratchet marios thing idk)
        public static final int kSol3Forward = 6;
        public static final int kSol3Reverse = 7;
    }

    // --- SHOOTER (KEPT EXACTLY AS UPLOADED) ---
    public static final class Shooter {
        public static final int kLeaderId = 16; //fixed
        public static final int kFollowerId = 17; //fixed
        
        public static final boolean kLeaderInverted = true; 

        public static final double kfastTargetRPM = 5000.0; 
        public static final double kslowTargetRPM = 2500.0;
        public static final double kReverseRPM = -1000.0; 
        
        public static final double kP = -0.11;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
        public static final double kV = 0.0;

        public static final double kVoltageRampPeriod = 100.0;
        public static final double kDutyCycleRampPeriod = 100.0;
    }

    public static final class Index {
        public static final int kMotorId = 13; //FIxed
        public static final boolean kInverted = false; 

        public static final double kForwardSpeed = 0.5; 
        public static final double kReverseSpeed = -0.5; 
    }

    public static final class ShooterIntake {
        public static final int kMotorId = 15; 
        public static final boolean kInverted = false; 

        public static final double kForwardSpeed = 2.0; 
        public static final double kReverseSpeed = -0.6; 
    }

    public static final class Turret {
        public static final int kMotorId = 14; //fixed
        public static final double kSpeedMultiplier = 0.25;
        public static final double kP = 0.05;
        public static final double kI = 0.00;
        public static final double kD = 0.004;
        public static final double kToleranceDegrees = 0.1;
        public static final double kMaxOutput = 0.3;
    }

    public static final class Auton {
        public static final double kDriveSpeed = 0.5;
        public static final double kTimeoutSeconds = 5.0;
    }

    public static final class Sim {
        public static final double kLoopPeriod = 0.002;
    }

    // --- NEW GOOBA CONSTANTS (Kraken X44) ---
    public static final class Gooba {
        public static final int kMotorId = 51; //Fixed
        
        // Motion Magic (Servo-like Position Control)
        public static final double kP = 2.4; 
        public static final double kI = 0.0;
        public static final double kD = 0.1;
        
        public static final double kCruiseVelocity = 80.0; // RPS
        public static final double kAcceleration = 160.0; // RPS/s
        
        // Safety for X44 (Smaller than X60)
        public static final double kSupplyCurrentLimit = 40.0; 
        
        // Positions (Rotations)
        public static final double kPositionStowed = 0.0;
        public static final double kPositionDeployed = 3.5; // [FIXME] Tune this value!

        public final static double kmountAngleDegrees = 36.4;
        public final static double klensheightmeters = 0.0;
        public final static double kaprilTagHeightMeters = 1.12395;
    }

    // --- NEW GROUND INTAKE CONSTANTS ---
    public static final class GroundIntake {
        public static final int kMotorId = 20; // New Unique ID
        public static final boolean kInverted = false; 
        public static final double kIntakeSpeed = 0.75; 

        // Safety for X44
        public static final double kSupplyCurrentLimit = 40.0;
    }

    // --- CLIMB SYSTEM (Kraken X60 & Dual Pistons T-Fitted) ---
    public static final class Climb {
        public static final int kMotorId = 21; 
        
        public static final double kClimbSpeed = 0.20; 
        public static final boolean kMotorInverted = false; 
        public static final double kSupplyCurrentLimit = 60.0; 
    }

    public static final class Limelight {
        public static final double kHOffsetMeters = 1.5 * 0.0254;

    }
}