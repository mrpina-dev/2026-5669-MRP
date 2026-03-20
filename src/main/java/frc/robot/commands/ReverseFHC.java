package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants;
import frc.robot.subsystems.IndexSubsystem;
import frc.robot.subsystems.ShooterIntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class ReverseFHC extends Command {
    private final IndexSubsystem index;
    private final boolean isForward;

    /**
     * Creates a new FuelHandlingCommand.
     * @param index The Index subsystem
     * @param isForward True to run intake/shoot sequence (Forward), False to run rewind sequence (Reverse)
     */
    public ReverseFHC(IndexSubsystem index, boolean isForward) {
        this.index = index;
        this.isForward = isForward;

        // Declare subsystem dependencies
        addRequirements(index);
    }

    @Override
    public void execute() {
        double indexSpeed;


        if (isForward) {
            // "Intake" / Shooting direction
            indexSpeed = Constants.Index.kForwardSpeed;
        
        } else {
            // "Rewind" / Clearing jam direction
            indexSpeed = Constants.Index.kReverseSpeed;
    
        }

        // Run all motors in unison
        index.run(indexSpeed);
    }

    @Override
    public void end(boolean interrupted) {
        // Stop all motors when the command ends
        index.stop();

    }

    @Override
    public boolean isFinished() {
        return false;
    }
}