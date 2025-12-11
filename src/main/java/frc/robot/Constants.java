// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Vector;

import org.dyn4j.geometry.Vector2;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    public static final int kOperatorControllerPort = 1;

    // Joystick Deadbands
    public static final double  DEADBAND           = 0.05;

    // Scales for movement and rotation (1 is full speed)
    public static final double  TRANSLATION_SCALE  = 1;
    public static final double  ROTATION_SCALE     = 1;

    // if we want field centric control or not (is forwards on the controller always away from  the driver station or is it forwards for the robot)
    public static final boolean FIELD_CENTRIC      = true;

    public static final String AUTO_SHUFFLEBOARD = "AUTO";
    public static final String DRIVER_SHUFFLEBOARD = "DRIVE";
  }

  public static class ArmConstants {
    public static class PIDConstants {
      public static final double kP = 0.027;
      public static final double kI = 0;
      public static final double kD = 0;
      // public static final double kMaxVel = 750;
      public static final double kMaxVel = 825;
      // public static final double kMaxAccel = 450;
      public static final double kMaxAccel = 500;
    }
    public static class ArmAngles {
      public static double Stowed = 132;
      public static double L1     = 96;
      public static double L2     = 89;
      public static double L3     = 64;
      public static double Climb     = 3;
      public static double Intake = 60;
      public static double Ground = 116.5;
      public static double Intermediate = 110;


      
      // public static double Stowed = 125;
      // public static double L1     = 7;
      // public static double L2     = 20;
      // public static double L3     = 20;
      // public static double L4     = 20;
    }
    public static class CANIDs{
      public static int frontMotorCANID = 12;
      public static int backMotorCANID = 13;
      public static int canandmagCANID = 21;
    }
    public static double precisionInDegrees = 2;
  }

  public static class TelescopeConstants {
    public static class PIDConstants {
      public static final double kP = 0.033;
      public static final double kI = 0;
      public static final double kD = 0;

      public static final double kMaxVel = 200;
      // public static final double kMaxVel = 50;
      public static final double kMaxAccel = 250;
      // public static final double kMaxAccel = 100;
    }

    public static class TelescopeLengths {
      public static double Stowed = 0;
      public static double L1     = 11;
      public static double L2     = 11;
      public static double L3     = 28;
      public static double Climb  = 0;
      public static double Intake = 10;
      public static double Ground = 24;
      public static double Intermediate = 27;
    }

    public static int MotorCANID = 18;

    public static double precisionInCM = 5;
    
  }


  public static class IntakeConstants {
    public static final double stopCurrent = 50;

    public static final int CANID = 14;
  }
  // max speed of the robot in m/s use Units.feetToMeters to use feet
  public static final   double  MAX_SPEED = 1.5;


  public static final class WristConstants {
      public static final class PID{
        public static double kP = 0.02;
        public static double kI = 0;
        public static double kD = 0;

        public static double precision = 4;
      }


      public static class CANIDs {
        public static int zMotor = 15;
        public static int yMotor = 16;
        public static int xMotor = 17;
      }

      // Presets for the position of the wrist
      public static final class PRESETS{
        public static Vector2 STOWED = new Vector2(0,0);
        // public static Vector2 L1 = new Vector2(27,-4);
        public static Vector2 L1 = new Vector2(0,0);
        public static Vector2 L2 = new Vector2(0,0);
        public static Vector2 L3 = new Vector2(0,0);
        public static Vector2 Climb = new Vector2(0,0);
        public static Vector2 INTAKE = new Vector2(0,0);
        public static Vector2 INTERMEDIATE = new Vector2(0,-3);
        public static Vector2 GROUND = new Vector2(1, -8.7);
      }


      public static final class PresetsNonDiffy{
        public static double STOWED = 0;
        public static double L1 = 3.4 *5;
        public static double L2 = 3.4 *5;
        public static double L3 = 6 *5;
        public static double Climb = 0 *5;
        public static double INTAKE = 15.1 *5;
        public static double INTERMEDIATE = 5 *5;
        public static double GROUND =  13 *5;
      }
  }
}


// ------------     CANID Reference     ------------

//  Drive:
//    Krakens:  2(FL), 4(FR), 6(BL), 8(BR)
//    SparkMax: 1(FL), 3(FR), 5(BL), 7(BR
//    Gyro: 20

//  Climb: 
//    SparkMax: 23 (Should Change to 11)

//  Arm:
//    Krakens: 12(F), 13(B)
//    Encoder: 21

// Intake:
//    Kraken: 14

//  Wrist:
//    SparkMax: 15(Rotation), 16(Diff 1), 17(Diff 2)

// Telescope:
//    Kraken: 18


// RIO: 0
// PDH 10



// FREE CANIDS: 9, 19, 21+
