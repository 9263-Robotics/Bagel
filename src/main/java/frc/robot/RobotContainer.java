// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.OperatorConstants;

// import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.subsystems.Wrist;
import frc.robot.subsystems.WristNotDiffy;
import frc.robot.commands.ArmCommand;
import frc.robot.commands.IntakeCommand;
import frc.robot.commands.LowerCommand;
import frc.robot.commands.OuttakeCommand;
import frc.robot.commands.TelescopeCommand;
import frc.robot.commands.WristCommand;
import frc.robot.commands.WristCommandDirectAxes;
// import frc.robot.commands.ArmCommand;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.BumperAddressableLED;
import frc.robot.subsystems.ClimbCamera;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.EndEffectorSubsystem;
import frc.robot.subsystems.LEDs;
import frc.robot.subsystems.Music;
import frc.robot.subsystems.StateManager;
import frc.robot.subsystems.SwerveSubsystem;
import frc.robot.subsystems.TelescopeSubsystem;

import swervelib.SwerveInputStream;


import com.reduxrobotics.canand.CanandEventLoop;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.PrintCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
//import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;



/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...

  // create a new swerve subsystem object
  // private final SwerveSubsystem drivebase = new SwerveSubsystem();

  private final WristNotDiffy wrist = new WristNotDiffy();

  private final SwerveSubsystem drivebase = new SwerveSubsystem();

  private final LEDs leds = new LEDs();
  // public final Music music = new Music(drivebase);
  
  // create a new pivot subystem object
  private final ArmSubsystem arm = new ArmSubsystem();

  private final TelescopeSubsystem telescope = new TelescopeSubsystem();

  private final BumperAddressableLED bumperLEDs = new BumperAddressableLED();

  // private final ClimbSubsystem climb = new ClimbSubsystem();

  private final EndEffectorSubsystem intake = new EndEffectorSubsystem();

  // private final PowerDistribution pdh = new PowerDistribution(10, ModuleType.kRev);


  private final StateManager stateManager = new StateManager();

  // private final ClimbCamera climbCamera = new ClimbCamera();
 
  // create an object for our driver controller
  private final CommandPS5Controller driverController = new CommandPS5Controller(Constants.OperatorConstants.kDriverControllerPort);
  
  private final CommandPS5Controller operatorController = new CommandPS5Controller(Constants.OperatorConstants.kOperatorControllerPort);


  private final SendableChooser<Command> autoChooser;


  

  // Build an auto chooser. This will use Commands.none() as the default option.

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {

    CanandEventLoop.getInstance();


    NamedCommands.registerCommand("Armup", new ArmCommand(arm, 1));
    NamedCommands.registerCommand("Armdown", new ArmCommand(arm, 0));
    NamedCommands.registerCommand("Outake", new OuttakeCommand(intake).withTimeout(1.5));
    NamedCommands.registerCommand("MoveArm", new SequentialCommandGroup(
                                                                            new TelescopeCommand(telescope, 0)
                                                                            .andThen(new ArmCommand(arm, 1))
                                                                            .andThen(new TelescopeCommand(telescope, 1).alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 1))))));
    
    NamedCommands.registerCommand("MoveArmL2", stateManager.goToState(2, telescope, arm, wrist));
    NamedCommands.registerCommand("MoveArmL1", stateManager.goToState(1, telescope, arm, wrist));
    NamedCommands.registerCommand("MoveArmIntake", stateManager.goToState(5, telescope, arm, wrist));

    NamedCommands.registerCommand("Intake", new IntakeCommand(intake));
                                                                              

    NamedCommands.registerCommand("Stow", stateManager.goToState(0, telescope, arm, wrist));                                                                        
                    
    // NamedCommands.registerCommand("Score", new SequentialCommandGroup(

    // Configure the trigger bindings
    configureBindings();

    // Shut up
    // DriverStation.silenceJoystickConnectionWarning(true);

    autoChooser = AutoBuilder.buildAutoChooser();

    setupAutoChooser();

    // set the default command for the drivebase to the drive command
    drivebase.setDefaultCommand(driveFieldOrientedAngularVelocity);


    // wrist.setDefaultCommand(wrist.runAxes(operatorController.getRightX(), operatorController.getLeftY()));
  
    drivebase.setDefaultCommand(driveFieldOrientedAngularVelocity);

    Shuffleboard.getTab(OperatorConstants.AUTO_SHUFFLEBOARD).addDouble("Match Time", () -> Timer.getMatchTime());
    // Shuffleboard.getTab(OperatorConstants.AUTO_SHUFFLEBOARD).addDouble("Voltage", () -> pdh.getVoltage());
    // Shuffleboard.getTab(OperatorConstants.AUTO_SHUFFLEBOARD).addDouble("Current", () -> pdh.getTotalCurrent());
    // Shuffleboard.getTab(OperatorConstants.AUTO_SHUFFLEBOARD).addDouble("Power", () -> pdh.getTotalPower());
    Shuffleboard.getTab(OperatorConstants.DRIVER_SHUFFLEBOARD).addInteger("Robot State", () -> stateManager.robotState);
  }


  SwerveInputStream driveAngularVelocity = SwerveInputStream.of(drivebase.getSwerveDrive(), 
                                                                () -> driverController.getLeftY() * -0.8,
                                                                () -> driverController.getLeftX() * -0.8)
                                                                .withControllerRotationAxis(() -> driverController.getRightX() * 0.5)
                                                                .deadband(OperatorConstants.DEADBAND)
                                                                .scaleTranslation(OperatorConstants.TRANSLATION_SCALE)
                                                                .scaleRotation(-OperatorConstants.ROTATION_SCALE)
                                                                .allianceRelativeControl(true);


                                                                
  // For the right stick to correspond to the angle we want the robot to face instead of the speed of rotationa
  SwerveInputStream driveDirectAngle = driveAngularVelocity.copy().withControllerHeadingAxis(
                                                                                            () -> driverController.getRightX() * -1,
                                                                                            () -> driverController.getRightY() *-1)
                                                                                             .headingWhile(true);
  

  // // create a new command that calls the driveCommand that we made in the swerveSubsystem
  Command driveFieldOrientedAngularVelocity = drivebase.driveFieldOriented(driveAngularVelocity);

  // // Same thing but for direct angle rather than angular velocity
  Command driveFieldOrientedDirectAngle     = drivebase.driveFieldOriented(driveDirectAngle);
  

  // define what buttons do on the controller
  private void configureBindings() {
    // /** Set up the commands to change the pivot position */
    // driverController.R1().and(() -> stateManager.robotState != "STOWED").onTrue(new TelescopeCommand(telescope, 0)
    //                                 // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                                 .andThen(new WristCommand(wrist, 0))
    //                                 .andThen(new ArmCommand(arm, 0))
    //                                 .andThen(stateManager.setRobotState("STOWED")));
                                  
    // driverController.square().and(() -> stateManager.robotState != "L1").onTrue(new TelescopeCommand(telescope, 0)
    //                                     // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                                     .andThen(new ArmCommand(arm, 1))
    //                                     .andThen(new TelescopeCommand(telescope, 1).alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 1))
    //                                     .andThen(stateManager.setRobotState("L1")))));

    // driverController.cross().and(() -> stateManager.robotState != "L2").onTrue(new TelescopeCommand(telescope, 0)
    //                                     // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                                     .andThen(new ArmCommand(arm, 2))
    //                                     .andThen(new TelescopeCommand(telescope, 2).alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 2))
    //                                     .andThen(stateManager.setRobotState("L2")))));

    // driverController.circle().and(() -> stateManager.robotState != "L3").onTrue(new TelescopeCommand(telescope, 0)
    //                                     // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                                     .andThen(new ArmCommand(arm, 3))
    //                                     .andThen(new TelescopeCommand(telescope, 3).alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 3))
    //                                     .andThen(stateManager.setRobotState("L3")))));

    // driverController.triangle().and(() -> stateManager.robotState != "CLIMB").onTrue(new TelescopeCommand(telescope, 0)
    //                                     // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                                     .andThen(new ArmCommand(arm, 4))
    //                                     .andThen(new TelescopeCommand(telescope, 4).alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 4))
    //                                     .andThen(stateManager.setRobotState("CLIMB")))));

    

    // operator stuff                              
    // operatorController.R1().onTrue(new TelescopeCommand(telescope, 0)
    //                                 .alongWith(new WristCommand(wrist, 0))
    //                                 .andThen(new ArmCommand(arm, 0)));
                                  
    // operatorController.square().onTrue(new TelescopeCommand(telescope, 0)
    //                                     .alongWith(new WristCommand(wrist, 0))
    //                                     .andThen(new ArmCommand(arm, 1))
    //                                     .andThen(new TelescopeCommand(telescope, 1))
    //                                     .alongWith(new WristCommand(wrist, 1)));

    // operatorController.cross().onTrue(new TelescopeCommand(telescope, 0)
    //                                     .alongWith(new WristCommand(wrist, 0))
    //                                     .andThen(new ArmCommand(arm, 2))
    //                                     .andThen(new TelescopeCommand(telescope, 2))
    //                                     .alongWith(new WristCommand(wrist, 2)));

    // operatorController.circle().onTrue(new TelescopeCommand(telescope, 0)
    //                                     .alongWith(new WristCommand(wrist, 0))
    //                                     .andThen(new ArmCommand(arm, 3))
    //                                     .andThen(new TelescopeCommand(telescope, 3))
    //                                     .alongWith(new WristCommand(wrist, 3)));

    // operatorController.triangle().onTrue(new TelescopeCommand(telescope, 0)
    //                                     .alongWith(new WristCommand(wrist, 0))
    //                                     .andThen(new ArmCommand(arm, 4))
    //                                     .andThen(new TelescopeCommand(telescope, 4))
    //                                     .alongWith(new WristCommand(wrist, 4)));

    // driverController.R1().onTrue(new TelescopeCommand(telescope, 0));
    // driverController.square().onTrue(new TelescopeCommand(telescope, 1));
    // driverController.cross().onTrue(new TelescopeCommand(telescope, 2));
    // driverController.circle().onTrue(new TelescopeCommand(telescope, 3));
    // driverController.triangle().onTrue(new TelescopeCommand(telescope, 4));

    // driverController.R1().onTrue(new WristCommandDirectAxes(wrist, 0));
    // driverController.square().onTrue(new WristCommandDirectAxes(wrist, 1));
    // driverController.cross().onTrue(new WristCommandDirectAxes(wrist, 2));
    // driverController.circle().onTrue(new WristCommandDirectAxes(wrist, 3));
    // driverController.triangle().onTrue(new WristCommandDirectAxes(wrist, 4));

    // driverController.R1().onTrue(new ArmCommand(arm, 0));
    // driverController.square().onTrue(new ArmCommand(arm, 1));
    // driverController.cross().onTrue(new ArmCommand(arm, 2));
    // driverController.circle().onTrue(new ArmCommand(arm, 3));
    // driverController.triangle().onTrue(new ArmCommand(arm, 4));

    driverController.R2().onTrue(new OuttakeCommand(intake).withTimeout(1.5));

    // operatorController.L2().whileTrue(wrist.runAxes(operatorController.getRightX(), operatorController.getLeftY()));
                                                            


    // driverController.R1().and(() -> stateManager.robotState != 6).onTrue(stateManager.goToState(0, telescope, arm, wrist));

    // driverController.square().and(() -> stateManager.robotState != 6).onTrue(stateManager.goToState(1, telescope, arm, wrist));
    // driverController.cross().and(() -> stateManager.robotState != 6).onTrue(stateManager.goToState(2, telescope, arm, wrist));
    // driverController.circle().and(() -> stateManager.robotState != 6).onTrue(stateManager.goToState(3, telescope, arm, wrist));
    // driverController.triangle().and(() -> stateManager.robotState != 6).onTrue(stateManager.goToState(4, telescope, arm, wrist));

    // driverController.R1().and(() -> stateManager.robotState != 6).onTrue(new TelescopeCommand(telescope, 7).alongWith(new ArmCommand(arm, 7).alongWith(new WristCommand(wrist, 7))).andThen(stateManager.goToState(0, telescope, arm, wrist)));

    // driverController.square().and(() -> stateManager.robotState == 6).onTrue(new TelescopeCommand(telescope, 7).alongWith(new ArmCommand(arm, 7).alongWith(new WristCommand(wrist, 7))).andThen(stateManager.goToState(1, telescope, arm, wrist)));
    // driverController.cross().and(() -> stateManager.robotState == 6).onTrue(new TelescopeCommand(telescope, 7).alongWith(new ArmCommand(arm, 7).alongWith(new WristCommand(wrist, 7))).andThen(stateManager.goToState(2, telescope, arm, wrist)));
    // driverController.circle().and(() -> stateManager.robotState == 6).onTrue(new TelescopeCommand(telescope, 7).alongWith(new ArmCommand(arm, 7).alongWith(new WristCommand(wrist, 7))).andThen(stateManager.goToState(3, telescope, arm, wrist)));
    // driverController.triangle().and(() -> stateManager.robotState == 6).onTrue(new TelescopeCommand(telescope, 7).alongWith(new ArmCommand(arm, 7).alongWith(new WristCommand(wrist, 7))).andThen(stateManager.goToState(4, telescope, arm, wrist)));
    
    
    driverController.R1().onTrue(stateManager.goToState(0, telescope, arm, wrist));

    // driverController.button(3).onTrue(stateManager.goToState(1, telescope, arm, wrist));
    // driverController.button(1).onTrue(stateManager.goToState(2, telescope, arm, wrist));
    // driverController.button(2).onTrue(stateManager.goToState(3, telescope, arm, wrist));

    driverController.square().onTrue(stateManager.goToState(1, telescope, arm, wrist));
    driverController.cross().onTrue(stateManager.goToState(2, telescope, arm, wrist));
    driverController.circle().onTrue(stateManager.goToState(3, telescope, arm, wrist));
    // driverController.triangle().onTrue(stateManager.goToState(5, telescope, arm, wrist));

    // driverController.R2().and(() -> intake.intakeMode).onTrue(new IntakeCommand(intake).until(() -> driverController.L2().getAsBoolean()));
    // driverController.R2().and(() -> !intake.intakeMode).onTrue(new OuttakeCommand(intake).withTimeout(0.6));

    // intake
    // driverController.R2().and(() -> stateManager.robotState != 6).onTrue(
    //                             new TelescopeCommand(telescope, 0)
    //                               // .alongWith(new WristCommand(wrist, 6).andThen(new WristCommand(wrist, 0)))
    //                               .andThen(new ArmCommand(arm, 6))
    //                               .andThen(new TelescopeCommand(telescope, 6).andThen(new WristCommand(wrist, 7)
    //                               .andThen(new IntakeCommand(intake).until(driverController.R3()))
    //                               .andThen(stateManager.setRobotState(6)))));

    // driverController.R2().onTrue(stateManager.goToState(6, telescope, arm, wrist));
    driverController.L2().onTrue(new SequentialCommandGroup(stateManager.goToState(6, telescope, arm, wrist),
                                 new IntakeCommand(intake).until(driverController.povDown())));
    
    // driverController.button(4).onTrue(new SequentialCommandGroup(stateManager.goToState(5, telescope, arm, wrist),
    //                               new IntakeCommand(intake).until(driverController.povDown())));

    driverController.triangle().onTrue(new SequentialCommandGroup(stateManager.goToState(5, telescope, arm, wrist),
                                  new IntakeCommand(intake).until(driverController.povDown())));

  // driverController.R2().and(() -> stateManager.robotState == 6).onTrue(
  //                                     new IntakeCommand(intake).until(driverController.R3()));
    
    // driverController.L2().onTrue(new LowerCommand(arm, telescope));

    // operatorController.R2().and(() -> intake.intakeMode).onTrue(new IntakeCommand(intake).until(() -> driverController.L2().getAsBoolean()));
    // operatorController.R2().and(() -> !intake.intakeMode).onTrue(new OuttakeCommand(intake).withTimeout(0.6));
    driverController.R3().whileTrue(drivebase.zeroGyro()); //zero the gyro when square(?) is pressed

    // driverController.povDown().whileTrue(climb.outake());

    // driverController.povUp().and(() -> (climb.getEncoder() < 60)).whileTrue(climb.intake());
    }

  private void setupAutoChooser(){
    new PathPlannerAuto("Test Auto");
    new PathPlannerAuto("AL4 HL4");
    new PathPlannerAuto("JL4 HL4");

    new PathPlannerAuto("Top L1");
    new PathPlannerAuto("Middle L1");
    new PathPlannerAuto("Bottom L1");

    

    


    Shuffleboard.getTab(OperatorConstants.AUTO_SHUFFLEBOARD).add("Auto", autoChooser);
  }

  public Command getAutonomousCommand() {
    return autoChooser.getSelected();
  }
}