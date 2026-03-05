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

import frc.robot.generated.TunerConstants;
import frc.robot.Constants;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.FollowPathCommand;

import org.ironmaple.simulation.SimulatedArena;

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
import frc.robot.subsystems.ClimbSubsystem; // CLIMB COMMENTED OUT

// Commands
import frc.robot.commands.RunShooterCommand;
import frc.robot.commands.FuelHandlingCommand;
import frc.robot.commands.GoobaToggleCommand;
import frc.robot.commands.AutoGooba;
import frc.robot.commands.GooberAlign;
import frc.robot.commands.Mariosearcommand;
import frc.robot.commands.TogglePneumaticCommand;
import frc.robot.commands.ManualGoobaCommand;
import frc.robot.commands.ManualTurretCommand;
import frc.robot.commands.RunGroundIntakeCommand;
import frc.robot.commands.RunClimbMotorCommand; // CLIMB COMMENTED OUT

public class RobotContainer {

    private double MaxSpeed = 1.0 * TunerConstants.kSpeedAt12Volts.in(MetersPerSecond);
    private double MaxAngularRate = Constants.Operator.kMaxAngularRate.in(RadiansPerSecond);

    /* Setting up bindings for necessary control of the swerve drive platform */
    private final SwerveRequest.FieldCentric fieldCentricDrive = new SwerveRequest.FieldCentric()
            .withDeadband(MaxSpeed * Constants.Operator.kDeadband)
            .withRotationalDeadband(MaxAngularRate * Constants.Operator.kRotationalDeadband)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final SwerveRequest.RobotCentric robotCentricDrive = new SwerveRequest.RobotCentric()
            .withDeadband(MaxSpeed * Constants.Operator.kDeadband)
            .withRotationalDeadband(MaxAngularRate * Constants.Operator.kRotationalDeadband)
            .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

    private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
    private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();

    /* Drive mode chooser for Shuffleboard */
    private final SendableChooser<String> driveModeChooser = new SendableChooser<>();

    /* Speed limiter choosers for Shuffleboard */
    private final SendableChooser<Double> globalSpeedLimiter = new SendableChooser<>();
    private final SendableChooser<Double> buttonSpeedLimiter = new SendableChooser<>();

    private final Telemetry logger = new Telemetry(MaxSpeed);

    private final CommandXboxController driverController = new CommandXboxController(Constants.Operator.kDriverControllerPort);
    private final CommandXboxController operator = new CommandXboxController(Constants.Operator.kOperatorControllerPort);

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
    public final ClimbSubsystem climb = new ClimbSubsystem(); // CLIMB COMMENTED OUT

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

    public final PneumaticSubsystem ClimbPiston = new PneumaticSubsystem(Constants.Pneumatics.kPcmId, 
    Constants.Pneumatics.kSol3Forward, 
    Constants.Pneumatics.kSol3Reverse);

    private final SendableChooser<Command> autoChooser;

    public RobotContainer() {

        // =============================================================
        // REGISTER NAMED COMMANDS FOR PATHPLANNER
        // These names MUST match EXACTLY what you type in the PathPlanner GUI.
        // This MUST happen BEFORE AutoBuilder.buildAutoChooser().
        // =============================================================

        // --- SHOOTING ---
        // Spins up the shooter wheels to full speed. Use as an event marker
        // while driving so the shooter is ready when you arrive.
        NamedCommands.registerCommand("spinUpShooter",
            new RunShooterCommand(shooter, Constants.Shooter.kfastTargetRPM)
        );

        // Spins up shooter at slower speed (for closer shots)
        NamedCommands.registerCommand("spinUpShooterSlow",
            new RunShooterCommand(shooter, Constants.Shooter.kslowTargetRPM)
        );

        // Runs the full shooting sequence: index + shooterIntake + shooter forward.
        // Give it a timeout in the PathPlanner GUI (1.5-2.5 seconds is typical).
        NamedCommands.registerCommand("shoot",
            new FuelHandlingCommand(index, shooterIntake, shooter, true)
        );

        // Stops the shooter wheels
        NamedCommands.registerCommand("stopShooter",
            new InstantCommand(() -> shooter.stop(), shooter)
        );

        // --- INTAKE (Fuel Handling Reverse = picking up fuel) ---
        // Runs index + shooterIntake + shooter in reverse to pull fuel in.
        // Give it a timeout in PathPlanner GUI (2-3 seconds).
        NamedCommands.registerCommand("intake",
            new FuelHandlingCommand(index, shooterIntake, shooter, false)
        );

        // --- GROUND INTAKE ---
        // Runs the ground intake roller. Give it a timeout in PathPlanner.
        NamedCommands.registerCommand("runGroundIntake",
            new RunGroundIntakeCommand(groundIntake)
        );

        // --- GOOBA (Hood/Arc) ---
        // Deploys the gooba to shooting position
        NamedCommands.registerCommand("deployGooba",
            new GoobaToggleCommand(gooba, true)
        );

        // Stows the gooba back to resting position
        NamedCommands.registerCommand("stowGooba",
            new GoobaToggleCommand(gooba, false)
        );

        // Auto-aims the gooba using Limelight distance.
        // Use as a deadline command or give it a timeout in PathPlanner.
        NamedCommands.registerCommand("autoAimGooba",
            new AutoGooba(gooba, rizz)
        );

        // --- TURRET ---
        // Auto-aims the turret using MariosEar (Limelight + USB cameras).
        // Give it a timeout in PathPlanner (1.5-2.5 seconds).
        NamedCommands.registerCommand("aimTurret",
            new Mariosearcommand(brick, goober)
        );

        // Aligns turret using only the Limelight (no USB cameras).
        NamedCommands.registerCommand("alignTurret",
            new GooberAlign(rizz, goober)
        );

        // --- PNEUMATICS ---
        // Toggle piston 1
        NamedCommands.registerCommand("togglePiston1",
            new TogglePneumaticCommand(piston1)
        );

        // Toggle piston 2
        NamedCommands.registerCommand("togglePiston2",
            new TogglePneumaticCommand(piston2)
        );

        // =============================================================
        // AUTO CHOOSER — must come AFTER named commands
        // =============================================================
        autoChooser = AutoBuilder.buildAutoChooser("Tests");
        SmartDashboard.putData("Auto Mode", autoChooser);

        // =============================================================
        // DRIVE MODE CHOOSER — Field Centric vs Robot Centric
        // =============================================================
        driveModeChooser.setDefaultOption("Field Centric", "field");
        driveModeChooser.addOption("Robot Centric", "robot");
        SmartDashboard.putData("Drive Mode", driveModeChooser);

        // =============================================================
        // SPEED LIMITERS
        // =============================================================

        // Global speed limiter — always active, change via Shuffleboard only
        globalSpeedLimiter.setDefaultOption("100%", 1.0);
        globalSpeedLimiter.addOption("75%", 0.75);
        globalSpeedLimiter.addOption("50%", 0.5);
        globalSpeedLimiter.addOption("25%", 0.25);
        SmartDashboard.putData("Global Speed Limit", globalSpeedLimiter);

        // Button-held speed limiter — only active while Start button is held
        buttonSpeedLimiter.setDefaultOption("50%", 0.5);
        buttonSpeedLimiter.addOption("75%", 0.75);
        buttonSpeedLimiter.addOption("25%", 0.25);
        buttonSpeedLimiter.addOption("10%", 0.1);
        SmartDashboard.putData("Button Speed Limit", buttonSpeedLimiter);

        configureBindings();

        FollowPathCommand.warmupCommand().schedule();
    }

    private void configureBindings() {
        drivetrain.setDefaultCommand(
            drivetrain.applyRequest(() -> {

                double xInput = -driverController.getLeftY();
                double yInput = -driverController.getLeftX();
                double rInput = -driverController.getRightX();

                double scaledX = Math.signum(xInput) * Math.pow(Math.abs(xInput), 3);
                double scaledY = Math.signum(yInput) * Math.pow(Math.abs(yInput), 3);
                double scaledRot = Math.signum(rInput) * Math.pow(Math.abs(rInput), 3);

                // Apply speed limiter: use button limit while Start is held, otherwise global
                double speedMultiplier = globalSpeedLimiter.getSelected();
                if (joystick.getHID().getStartButton()) {
                    speedMultiplier = buttonSpeedLimiter.getSelected();
                }

                double currentMaxSpeed = MaxSpeed * speedMultiplier;
                double currentMaxAngularRate = MaxAngularRate * speedMultiplier;

                // Check Shuffleboard chooser to pick drive mode
                if ("robot".equals(driveModeChooser.getSelected())) {
                    return robotCentricDrive
                        .withVelocityX(scaledX * currentMaxSpeed)
                        .withVelocityY(scaledY * currentMaxSpeed)
                        .withRotationalRate(scaledRot * currentMaxAngularRate);
                }

                return fieldCentricDrive
                    .withVelocityX(scaledX * currentMaxSpeed)
                    .withVelocityY(scaledY * currentMaxSpeed)
                    .withRotationalRate(scaledRot * currentMaxAngularRate);
            })
        );

        final var idle = new SwerveRequest.Idle();
        RobotModeTriggers.disabled().whileTrue(
            drivetrain.applyRequest(() -> idle).ignoringDisable(true)
        );

        // --- SHOOTER CONTROLS ---
        driverController.rightTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, true)
        );

        operator.rightTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, true)
        );

        driverController.leftTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, false)
        );

        operator.leftTrigger().whileTrue(
            new FuelHandlingCommand(index, shooterIntake, shooter, false)
        );

        // --- GOOBA TOGGLE (Single Button 'B') ---
        driverController.b().onTrue(new InstantCommand(() -> {
            if (Math.abs(gooba.getPosition()) > 1.0) {
                gooba.setPosition(Constants.Gooba.kPositionStowed);
            } else {
                gooba.setPosition(Constants.Gooba.kPositionDeployed);
            }
        }, gooba));

        // --- STANDARD TURRET AIMING (Back Button) ---
        driverController.back().whileTrue(new Mariosearcommand(brick, goober));
        operator.back().whileTrue(new Mariosearcommand( brick, goober));

        // --- CLIMB CONTROLS (ALL COMMENTED OUT) ---
        driverController.a().whileTrue(new TogglePneumaticCommand(ClimbPiston));
        driverController.rightBumper().whileTrue(new RunClimbMotorCommand(climb, Constants.Climb.kClimbSpeed));
        driverController.leftBumper().whileTrue(new RunClimbMotorCommand(climb, -Constants.Climb.kClimbSpeed));

        // ==========================================
        // --- TEMPORARY TUNING PAD (D-PAD) ---
        // ==========================================

        // UP/DOWN: Gooba (Arc) Jogging
        operator.povUp().whileTrue(new ManualGoobaCommand(gooba, true));
        operator.povDown().whileTrue(new ManualGoobaCommand(gooba, false));

        // LEFT/RIGHT: Turret Jogging
        operator.povLeft().whileTrue(new ManualTurretCommand(goober, -0.4));
        operator.povRight().whileTrue(new ManualTurretCommand(goober, 0.4));

        // --- PNEUMATICS CONTROLS ---
        driverController.rightBumper().onTrue(new InstantCommand(() -> {
            int id = rizz.getID();
            System.out.println("ID: " + id);
        }));

        // Toggle Piston 2 & 1 with X Button
        driverController.x().onTrue(new TogglePneumaticCommand(piston2));
        driverController.x().onTrue(new TogglePneumaticCommand(piston1));

        // --- DRIVETRAIN EXTRAS ---
        driverController.leftBumper().onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

        // SysId bindings removed to avoid button conflicts
        // joystick.back().and(joystick.y()).whileTrue(drivetrain.sysIdDynamic(Direction.kForward));
        // joystick.back().and(joystick.x()).whileTrue(drivetrain.sysIdDynamic(Direction.kReverse));
        // joystick.start().and(joystick.y()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kForward));
        // joystick.start().and(joystick.x()).whileTrue(drivetrain.sysIdQuasistatic(Direction.kReverse));

        drivetrain.registerTelemetry(logger::telemeterize);
    }

    public Command getAutonomousCommand() {

        return autoChooser.getSelected();

    }
}