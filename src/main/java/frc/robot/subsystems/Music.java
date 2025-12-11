package frc.robot.subsystems;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.lang.ArrayIndexOutOfBoundsException;

import javax.sound.midi.Instrument;

import com.ctre.phoenix6.Orchestra;
import com.ctre.phoenix6.configs.AudioConfigs;
import com.ctre.phoenix6.hardware.ParentDevice;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.swerve.SwerveModule;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.TelescopeConstants;

public class Music extends SubsystemBase {
    private final TalonFX m_motor1 = new TalonFX(ArmConstants.CANIDs.frontMotorCANID);
    private final TalonFX m_motor2 = new TalonFX(ArmConstants.CANIDs.backMotorCANID);
    private final TalonFX m_motor3 = new TalonFX(IntakeConstants.CANID);
    private final TalonFX m_motor4 = new TalonFX(TelescopeConstants.MotorCANID);

    public Orchestra m_orchestra = new Orchestra();

    public String musicFile = "music/doom.chrp";
    public String[] musicFiles;// = {
    // "doom.chrp", "mario.chrp", "megalovania.chrp", "mario-kart.chrp",
    // "rickroll.chrp", "bad-apple.chrp"
    // };
    // public List<String> musicFiles;

    public SendableChooser<String> songChooser;

    public Music(SwerveSubsystem swerveSubsystem) {
        // Autofill the music list with files from the deploy folder
        Path musicDir = Filesystem.getDeployDirectory().toPath().resolve("music");

        try (Stream<Path> paths = Files.list(musicDir)) {
            musicFiles = paths
                    .filter(p -> p.toString().endsWith(".chrp"))
                    .map(p -> p.getFileName().toString()) // "song.chrp"
                    .sorted()
                    .toArray(String[]::new);
        } catch (IOException e) {
            System.out.println("Error loading music files: " + e.getMessage());
            musicFiles = new String[] {};
        }

        // AudioConfigs audioConfigs = new AudioConfigs();
        // audioConfigs.AllowMusicDurDisable = true;

        swervelib.SwerveModule[] motors;
        TalonFX[] motors2;
        motors = swerveSubsystem.getSwerveDrive().getModules();
        motors2 = new TalonFX[] { m_motor1, m_motor2, m_motor3, m_motor4 };

        for (int i = 0; i < motors.length; i++) {
            Object motor = motors[i].getDriveMotor().getMotor();
            if (motor instanceof ParentDevice parentDevice) {
                m_orchestra.addInstrument(parentDevice, 0);
                m_orchestra.addInstrument(motors2[i], i + 1);
            }
        }

        try {
            songChooser = new SendableChooser<>();
            songChooser.setDefaultOption("Disable Music", "disable");
            for (int i = 0; i < musicFiles.length; i++) {
                songChooser.addOption(musicFiles[i], musicFiles[i]);
            }
            SmartDashboard.putData("Song Chooser", songChooser);
            songChooser.onChange(selectedSong -> {
                musicFile = selectedSong;
                loadPlayMusic(musicFile);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        // Attempt to load the chrp
        // var status = m_orchestra.loadMusic(musicFile);

        // if (!status.isOK()) {
        // // log error
        // }

        // m_orchestra.play();
    }

    public void loadPlayMusic(String file) {
        m_orchestra.stop();
        System.out.println("Music stopped");

        if (!file.equalsIgnoreCase("disable")) {
            // Attempt to load the chrp
            var status = m_orchestra.loadMusic(file);

            if (!status.isOK()) {
                // log error
            }

            m_orchestra.play();

            System.out.println("Playing music: " + file);
        }
    }
}
