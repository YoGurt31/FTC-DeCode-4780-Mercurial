package Util;
public final class Constants {

    public static final class Drive {
        public static final String frontLeft = "fL";
        public static final String frontRight = "fR";
        public static final String backLeft = "bL";
        public static final String backRight = "bR";

        public static final double MAX_WHEEL_RPM = 435;

        public static final double ROTATE_GAIN = 0.0250;
        public static final double MAX_ROTATE = 0.75;
    }

    public static final class PinPoint {
        public static final String PinPoint = "pinpoint";

        public static final boolean X_REVERSED = true;
        public static final boolean Y_REVERSED = false;

        public static final double X_OFFSET_MM = -176;
        public static final double Y_OFFSET_MM = -66;

        public static final double START_X_IN = 0;
        public static final double START_Y_IN = 0;
        public static final double START_HEADING_DEG = 0;
    }

    public static final class Elevator {
        public static final String gearShift = "gS";
        public static final String elevatorLeft = "eL";
        public static final String elevatorRight = "eR";

        public static final double GEAR_SHIFT_DEFAULT = 0.5;
        public static final double ELEVATOR_LEFT_DEFAULT = 0.5;
        public static final double ELEVATOR_RIGHT_DEFAULT = 0.5;

        public static final double GEAR_SHIFT_PRESET = 0.55;
        public static final double ELEVATOR_LEFT_PRESET = 1.0;
        public static final double ELEVATOR_RIGHT_PRESET = 0.0;
    }

    public static final class Intake {
        public static final String rollerIntake = "rI";
        public static final String sorterIntake = "sI";

        public static final double INTAKE_POWER = 1.0;
        public static final double OUTTAKE_POWER = -1.0;
    }

    public static final class Flywheel {
        public static final String Flywheel1 = "fW1";
        public static final String Flywheel2 = "fW2";

        public static final double TICKS_PER_REV = 28.0;

        public static final double FAR_TARGET_RPS = 53.5;
        public static final double CLOSE_TARGET_RPS = 48.5;

        public static final double F = 18.00;
        public static final double P = 2.25 * F;
        public static final double I = 0.0;
        public static final double D = 0.0;
    }

    public static final class Releases {
        public static final String leftRelease = "lR";
        public static final String rightRelease = "rR";

        public static final double HOLD_LEFT = 0.0;
        public static final double RELEASE_LEFT = 0.5;

        public static final double HOLD_RIGHT = 0.5;
        public static final double RELEASE_RIGHT = 1.0;

        public static final long GATE_OPEN_MS = 250;
        public static final long SHOT_TOTAL_MS = 1500;
    }

    public static final class Vision {
        public static final String LIMELIGHT_NAME = "limelight";
        public static final int DEFAULT_PIPELINE = 0;
        public static final int RED_PIPELINE = 1;
        public static final int BLUE_PIPELINE = 2;
        public static final double TAG_AREA_THRESHOLD = 0.8;
    }
}