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
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine.Direction;

import frc.robot.generated.TunerConstants;
import frc.robot.Constants;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.FollowPathCommand;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

// Subsystems
import frc.robot.subsystems.CommandSwerveDrivetrain;
import frc.robot.subsystems.ShooterSubsystem;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.ShooterIntakeSubsystem;
import frc.robot.subsystems.GoobaSubsystem;
import frc.robot.subsystems.Goober;
import frc.robot.subsystems.MariosEar;
import frc.robot.subsystems.PneumaticSubsystem;
import frc.robot.subsystems.GroundIntakeSubsystem; 
import frc.robot.subsystems.ClimbSubsystem; // INJECTED CLIMB

// Commands
import frc.robot.commands.RunShooterCommand;
import frc.robot.commands.FuelHandlingCommand;
import frc.robot.commands.GoobaToggleCommand;
import frc.robot.commands.GooberAlign;
import frc.robot.commands.Mariosearcommand;
import frc.robot.commands.TogglePneumaticCommand; 
import frc.robot.commands.ManualGoobaCommand;
import frc.robot.commands.ManualTurretCommand;
import frc.robot.commands.RunGroundIntakeCommand; 
import frc.robot.commands.RunClimbMotorCommand; // INJECTED CLIMB

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

    // NEW Subsystems
    public final GroundIntakeSubsystem groundIntake = new GroundIntakeSubsystem();
    public final ClimbSubsystem climb = new ClimbSubsystem(); // INJECTED CLIMB

    // Pneumatics Subsystems
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

    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {
        autoChooser = AutoBuilder.buildAutoChooser("Tests");
        SmartDashboard.putData("Auto Mode", autoChooser);

        configureBindings();

        FollowPathCommand.warmupCommand().schedule();
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
        
        // --- GOOBA TOGGLE (Single Button 'B') ---
        joystick.b().onTrue(new InstantCommand(() -> {
            // Use Math.abs() to ignore the negative sign from the encoder when deployed
            if (Math.abs(gooba.getPosition()) > 1.0) {
                gooba.setPosition(Constants.Gooba.kPositionStowed);
            } else {
                gooba.setPosition(Constants.Gooba.kPositionDeployed);
            }
        }, gooba));

        // --- STANDARD TURRET AIMING (Moved to Back Button) ---
        joystick.back().whileTrue(new Mariosearcommand(brick, goober));

        // --- CLIMB CONTROLS ---
        // Toggle both climb pistons simultaneously with the START button
        joystick.start().onTrue(new InstantCommand(() -> climb.togglePistons(), climb));

        // Kraken X60 Motor Control: Y goes Up (Positive speed), A goes Down (Negative speed)
        joystick.y().whileTrue(new RunClimbMotorCommand(climb, Constants.Climb.kClimbSpeed));
        joystick.a().whileTrue(new RunClimbMotorCommand(climb, -Constants.Climb.kClimbSpeed));


        // ==========================================
        // --- TEMPORARY TUNING PAD (D-PAD) ---
        // ==========================================
        
        // UP/DOWN: Gooba (Arc) Jogging
        joystick.povUp().whileTrue(new ManualGoobaCommand(gooba, true));
        joystick.povDown().whileTrue(new ManualGoobaCommand(gooba, false));

        // LEFT/RIGHT: Turret Jogging 
        joystick.povLeft().whileTrue(new ManualTurretCommand(goober, -0.4));
        joystick.povRight().whileTrue(new ManualTurretCommand(goober, 0.4));


        // --- PNEUMATICS CONTROLS ---
        // Toggle Piston 1 & check Limelight ID with Right Bumper
        joystick.rightBumper().onTrue( new InstantCommand(() -> { int id = rizz.getID(); System.out.println("ID: " + id); })
            );
        
        // Toggle Piston 2 & 1 with X Button
        joystick.x().onTrue(new TogglePneumaticCommand(piston2));
        joystick.x().onTrue(new TogglePneumaticCommand(piston1));


        // --- DRIVETRAIN EXTRAS ---
        joystick.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        // NOTE: SysId bindings removed to avoid conflicts with climb (start) and turret (back) buttons.
        // To re-enable SysId, move these to a second controller or use a different modifier scheme.
        // joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        // joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        // joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        // joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    /**
     * FIX: Now returns the PathPlanner auto selected from SmartDashboard/Shuffleboard.
     * Falls back to a simple drive-forward command if no auto is selected.
     */
    public Command getAutonomousCommand() {
        Command selected = autoChooser.getSelected();
        if (selected != null) {
            return selected;
        }

        // Fallback: drive forward for a few seconds
        System.out.println("[AUTO] WARNING: No PathPlanner auto selected, using fallback drive-forward.");
        final var idle = new SwerveRequest.Idle();
        return Commands.sequence(
            drivetrain.applyRequest(() ->
                drive.withVelocityX(Constants.Auton.kDriveSpeed)
                    .withVelocityY(0)
                    .withRotationalRate(0)
            ).withTimeout(Constants.Auton.kTimeoutSeconds),
            drivetrain.applyRequest(() -> idle)
        );
    }
}