// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
//

package frc.robot;

import static edu.wpi.first.units.Units.*;
import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.Constants;

// Subsystems
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.ShooterIntakeSubsystem;
import frc.robot.subsystems.GoobaSubsystem;
import frc.robot.subsystems.Goober;
import frc.robot.subsystems.MariosEar;
import frc.robot.subsystems.PneumaticSubsystem; // [NEW]

// Commands
import frc.robot.commands.RunShooterCommand;
import frc.robot.commands.FuelHandlingCommand;
import frc.robot.commands.GoobaToggleCommand;
import frc.robot.commands.GooberAlign;
import frc.robot.commands.Mariosearcommand;
import frc.robot.commands.TogglePneumaticCommand; // [NEW]

public class RobotContainer {
    
    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); 
    private double MaxAngularRate = Constants.Operator.kMaxAngularRate.in(RadiansPerSecond);

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * Constants.Operator.kDeadband)
            .withRotationalDeadband(MaxAngularRate * Constants.Operator.kRotationalDeadband) 
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage); 
            
    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController joystick = new CommandXboxController(Constants.Operator.kDriverControllerPort);

    public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
    
    // Subsystems
    public final ShooterSubsystem shooter = new ShooterSubsystem();
    public final IndexSubsystem index = new IndexSubsystem();
    public final ShooterIntakeSubsystem shooterIntake = new ShooterIntakeSubsystem();
    public final GoobaSubsystem gooba = new GoobaSubsystem();
    public final Goober goober = new Goober();
    public final LimelightSubsystem rizz = new LimelightSubsystem();
    public final MariosEar brick = new MariosEar(rizz);

    // [NEW] Pneumatics Subsystems
    public final PneumaticSubsystem piston1 = new PneumaticSubsystem(
        Constants.Pneumatics.kPcmId, 
        Constants.Pneumatics.kSol1Forward, 
        Constants.Pneumatics.kSol1Reverse
    );

    public final PneumaticSubsystem piston2 = new PneumaticSubsystem(
        Constants.Pneumatics.kPcmId, 
        Constants.Pneumatics.kSol2Forward, 
        Constants.Pneumatics.kSol2Reverse
    );

    public RobotContainer() {
        configureBindings();
    }

    private void configureBindings() {
        drivetrain.setDefaultCommand(
    drivetrain.applyRequest(() -> {
        
        double xInput = -joystick.getLeftY();
        double yInput = -joystick.getLeftX();
        double rInput = -joystick.getRightX();

       
        double scaledX = Math.signum(xInput) * Math.pow(Math.abs(xInput), 3);
        double scaledY = Math.signum(yInput) * Math.pow(Math.abs(yInput), 3);
        double scaledRot = Math.signum(rInput) * Math.pow(Math.abs(rInput), 3);

        return drive
            .withVelocityX(scaledX * MaxSpeed)
            .withVelocityY(scaledY * MaxSpeed)
            .withRotationalRate(scaledRot * MaxAngularRate);
    })
);

        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
            drivetrain.applyRequest(() -> idle).ignoringDisable(true)
        );

        // --- SHOOTER CONTROLS ---
        joystick.rightTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, true)
        );

        joystick.leftTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, false)
        );

        joystick.a().whileTrue(new RunShooterCommand(shooter, Constants.Shooter.kfastTargetRPM));
        joystick.b().whileTrue(new Mariosearcommand(brick, goober));
        
        // --- GOOBA CONTROLS ---
        joystick.x().onTrue(new GoobaToggleCommand(gooba, true));
        joystick.y().onTrue(new GoobaToggleCommand(gooba, false));

        // --- PNEUMATICS CONTROLS [NEW] ---
        // Toggle Piston 1 with Right Bumper
        joystick.rightBumper().onTrue(new TogglePneumaticCommand(piston1));
        
        // Toggle Piston 2 with Start Button
        joystick.start().onTrue(new TogglePneumaticCommand(piston2));


        // --- DRIVETRAIN EXTRAS ---
        joystick.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {
        final var idle = new SwerveRequest.Idle();
        return Commands.sequence(
            drivetrain.runOnce(() -> drivetrain.seedFieldCentric(Rotation2d.kZero)),
            drivetrain.applyRequest(() ->
                drive.withVelocityX(Constants.Auton.kDriveSpeed)
                    .withVelocityY(0)
                    .withRotationalRate(0)
            )
            .withTimeout(Constants.Auton.kTimeoutSeconds),
            drivetrain.applyRequest(() -> idle)
        );
    }
}