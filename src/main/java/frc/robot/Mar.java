package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

public class Mar {
    /** Registers all commands for PathPlanner to see. */
    public static void registerNamedCommands(
        ShooterSubsystem shooter, 
        IndexSubsystem index, 
        ShooterIntakeSubsystem sIntake,
        GoobaSubsystem gooba,
        Goober goober,
        LimelightSubsystem rizz,
        MariosEar brick,
        GroundIntakeSubsystem gIntake,
        PneumaticSubsystem p1,
        PneumaticSubsystem p2
    ) {
        // --- SHOOTING ---
        com.pathplanner.lib.auto.NamedCommands.registerCommand("spinUpShooter",
            new RunShooterCommand(shooter, Constants.Shooter.kfastTargetRPM));
        
        com.pathplanner.lib.auto.NamedCommands.registerCommand("shoot",
            new FuelHandlingCommand(index, sIntake, shooter, true));

        com.pathplanner.lib.auto.NamedCommands.registerCommand("stopShooter",
            new InstantCommand(shooter::stop, shooter));

        // --- GROUND INTAKE ---
        com.pathplanner.lib.auto.NamedCommands.registerCommand("runGroundIntake",
            new RunGroundIntakeCommand(gIntake));

        // --- GOOBA & TURRET ---
        com.pathplanner.lib.auto.NamedCommands.registerCommand("autoAimGooba",
            new AutoGooba(gooba, rizz));

        com.pathplanner.lib.auto.NamedCommands.registerCommand("aimTurret",
            new Mariosearcommand(brick, goober));

        // ... Add the rest of your NamedCommands here ...
    }
}