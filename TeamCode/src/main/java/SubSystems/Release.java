package SubSystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import Util.Constants;

public class Release {
    public static final Release INSTANCE = new Release();

    private Release() {
    }

    private Servo left;
    private Servo right;

    @SuppressWarnings("unused")
    private Telemetry telemetry;

    private enum Shot {
        NONE,
        LEFT,
        RIGHT,
        AUTOLEFT,
        AUTORIGHT
    }

    private Shot activeShot = Shot.NONE;
    private long shotStartMs = 0;
    private int autoPhase = 0;
    private static final long AUTO_FEED_MS = 500;

    public void init(HardwareMap hw, Telemetry telem) {
        this.telemetry = telem;

        left = hw.get(Servo.class, Constants.Releases.leftRelease);
        right = hw.get(Servo.class, Constants.Releases.rightRelease);

        holdBoth();

        activeShot = Shot.NONE;
        shotStartMs = 0;
    }

    public void startLeftShot() {
        if (left == null) return;
        activeShot = Shot.LEFT;
        shotStartMs = System.currentTimeMillis();
    }

    public void startRightShot() {
        if (right == null) return;
        activeShot = Shot.RIGHT;
        shotStartMs = System.currentTimeMillis();
    }

    public void autoLeftShot() {
        if (left == null || right == null) return;
        activeShot = Shot.AUTOLEFT;
        autoPhase = 0;
        shotStartMs = System.currentTimeMillis();
    }

    public void autoRightShot() {
        if (left == null || right == null) return;
        activeShot = Shot.AUTORIGHT;
        autoPhase = 0;
        shotStartMs = System.currentTimeMillis();
    }

    public void cancel() {
        activeShot = Shot.NONE;
        holdBoth();
        Intake.INSTANCE.setMode(Intake.Mode.IDLE);
    }

    public void update() {
        if (activeShot == Shot.NONE) return;

        long elapsed = System.currentTimeMillis() - shotStartMs;

        if (activeShot == Shot.AUTOLEFT || activeShot == Shot.AUTORIGHT) {
            if (autoPhase == 0 && elapsed > Constants.Releases.GATE_OPEN_MS) {
                autoPhase = 1;
                shotStartMs = System.currentTimeMillis();
                elapsed = 0;
            } else if (autoPhase == 1 && elapsed > Constants.Releases.GATE_OPEN_MS + 250) {
                autoPhase = 2;
                Intake.INSTANCE.setMode(activeShot == Shot.AUTOLEFT ? Intake.Mode.LEFT : Intake.Mode.RIGHT);
                shotStartMs = System.currentTimeMillis();
                elapsed = 0;
            } else if (autoPhase == 2 && elapsed > AUTO_FEED_MS) {
                autoPhase = 3;
                Intake.INSTANCE.setMode(Intake.Mode.IDLE);
                shotStartMs = System.currentTimeMillis();
                elapsed = 0;
            }

            boolean gateOpenWindow = elapsed <= Constants.Releases.GATE_OPEN_MS;

            switch (autoPhase) {
                case 0: // LEFT
                    left.setPosition(gateOpenWindow ? Constants.Releases.RELEASE_LEFT : Constants.Releases.HOLD_LEFT);
                    right.setPosition(Constants.Releases.HOLD_RIGHT);
                    break;

                case 1: // RIGHT
                    right.setPosition(gateOpenWindow ? Constants.Releases.RELEASE_RIGHT : Constants.Releases.HOLD_RIGHT);
                    left.setPosition(Constants.Releases.HOLD_LEFT);
                    break;

                case 2: // FEED
                    holdBoth();
                    break;

                case 3: // BOTH
                    left.setPosition(gateOpenWindow ? Constants.Releases.RELEASE_LEFT : Constants.Releases.HOLD_LEFT);
                    right.setPosition(gateOpenWindow ? Constants.Releases.RELEASE_RIGHT : Constants.Releases.HOLD_RIGHT);
                    break;
            }

            if (autoPhase == 3 && elapsed >= Constants.Releases.SHOT_TOTAL_MS) {
                holdBoth();
                Intake.INSTANCE.setMode(Intake.Mode.IDLE);
                activeShot = Shot.NONE;
            }
            return;
        }

        boolean gateOpenWindow = elapsed <= Constants.Releases.GATE_OPEN_MS;

        if (activeShot == Shot.LEFT) {
            if (gateOpenWindow) {
                left.setPosition(Constants.Releases.RELEASE_LEFT);
            } else {
                left.setPosition(Constants.Releases.HOLD_LEFT);
            }
        } else if (activeShot == Shot.RIGHT) {
            if (gateOpenWindow) {
                right.setPosition(Constants.Releases.RELEASE_RIGHT);
            } else {
                right.setPosition(Constants.Releases.HOLD_RIGHT);
            }
        }

        if (elapsed >= Constants.Releases.SHOT_TOTAL_MS) {
            if (activeShot == Shot.LEFT) {
                left.setPosition(Constants.Releases.HOLD_LEFT);
            } else if (activeShot == Shot.RIGHT) {
                right.setPosition(Constants.Releases.HOLD_RIGHT);
            }
            activeShot = Shot.NONE;
        }
    }

    public void holdBoth() {
        if (left != null) left.setPosition(Constants.Releases.HOLD_LEFT);
        if (right != null) right.setPosition(Constants.Releases.HOLD_RIGHT);
    }

    public boolean isLeftGateOpen() {
        if (left == null) return false;
        return left.getPosition() == Constants.Releases.RELEASE_LEFT;
    }

    public boolean isRightGateOpen() {
        if (right == null) return false;
        return right.getPosition() == Constants.Releases.RELEASE_RIGHT;
    }

    public boolean isShooting() {
        return activeShot != Shot.NONE;
    }
}
