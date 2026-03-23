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
    
    // The interpolation map that handles the math between your calibrated points
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

        // --- FIRMWARE SOFT LIMITS ---
        configs.SoftwareLimitSwitch.ReverseSoftLimitThreshold = 0.0;
        configs.SoftwareLimitSwitch.ReverseSoftLimitEnable = true;

        configs.SoftwareLimitSwitch.ForwardSoftLimitThreshold = 10.7; 
        configs.SoftwareLimitSwitch.ForwardSoftLimitEnable = true;
        // ----------------------------

        m_motor.getConfigurator().apply(configs);
        
        // Brake Mode is essential for a servo-like mechanism
        m_motor.setNeutralMode(NeutralModeValue.Brake);
        
        // Reset encoder to 0 on startup
        m_motor.setPosition(0);

        // ==========================================
        // INTERPOLATION MAP (Limelight 'tx' degrees -> Hood Rotations)
        // REPLACE THESE EXAMPLE NUMBERS WITH YOUR REAL TEST DATA
        // Note: Ensure your rotation values stay between your soft limits (0.0 to 10.7)
        // ==========================================
        
        // Point 1: Close up (Target is high in camera, large positive 'tx' because limelight is sideways)
        shotMap.put(15.6, 0.0);
        
        // Point 2: Mid-Close 
        shotMap.put(8.5, 1.80);
        
        // Point 3: Mid 
        shotMap.put(1.2, 2.45); 
        
        // Point 4: Mid-Far (Target starts getting lower in camera view)
        shotMap.put(-4.5, 3.10); 
        
        // Point 5: Deep/Trench area (Target is very low in camera view)
        shotMap.put(-10.1, 3.75); 
    }

    public void setPosition(double rotations) {
        m_motor.setControl(m_positionControl.withPosition(-rotations));
    }

    public double getPosition() {
        return m_motor.getPosition().getValueAsDouble();
    }

    public double getRotationValueFromTx(double tx) {
        return shotMap.get(tx);
    }

    @Override
    public void periodic(){
        double currentPos = m_motor.getPosition().getValueAsDouble();

        //Helpful for calibration - you can view this in your driver station console or push to SmartDashboard
        //System.out.println("GOOBA:" + currentPos);
    }
}