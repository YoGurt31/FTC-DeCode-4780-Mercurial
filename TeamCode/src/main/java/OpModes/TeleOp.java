package OpModes;

import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.exec;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.loop;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.sequence;
import static dev.frozenmilk.dairy.mercurial.continuations.Continuations.waitUntil;

import SubSystems.DefaultTelemetry;
import SubSystems.Drive;
import SubSystems.Elevator;
import SubSystems.Flywheel;
import SubSystems.Intake;
import SubSystems.Release;
import SubSystems.Vision;
import Util.Constants;
import dev.frozenmilk.dairy.mercurial.ftc.Mercurial;

@SuppressWarnings("unused")
public final class TeleOp {
    private static Mercurial.RegisterableProgram buildTeleOp(int pipeline, boolean telemetry) {
        return Mercurial.teleop(ctx -> {

            // Hardware Init
            Drive.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());
            Vision.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());
            if (Vision.INSTANCE.getLimelight() != null) {
                Vision.INSTANCE.getLimelight().pipelineSwitch(pipeline);
            }
            Intake.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());
            Flywheel.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());
            Release.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());
            Elevator.INSTANCE.init(ctx.hardwareMap(), ctx.telemetry());

            // Main Loop
            ctx.schedule(sequence(
                    waitUntil(ctx::inLoop),
                    loop(exec(() -> {

                        Vision.INSTANCE.update();

                        Drive.INSTANCE.updateOdometry();
                        Drive.INSTANCE.drive(
                                -ctx.gamepad1().left_stick_y,
                                ctx.gamepad1().right_stick_x,
                                ctx.gamepad1().left_trigger
                        );

                        Intake.INSTANCE.apply();
                        Flywheel.INSTANCE.apply();
                        Elevator.INSTANCE.updateRise();
                        Release.INSTANCE.update();

                        if (telemetry) {
                            DefaultTelemetry.INSTANCE.update(ctx.telemetry());
                        }
                    }))
            ));

            // Intake
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().left_bumper && ctx.gamepad1().right_bumper),
                    exec(() -> Intake.INSTANCE.setMode(Intake.Mode.EJECT))
            );
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().left_bumper && !ctx.gamepad1().right_bumper),
                    exec(() -> Intake.INSTANCE.setMode(Intake.Mode.LEFT))
            );
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().right_bumper && !ctx.gamepad1().left_bumper),
                    exec(() -> Intake.INSTANCE.setMode(Intake.Mode.RIGHT))
            );
            ctx.bindSpawn(ctx.risingEdge(() -> !ctx.gamepad1().left_bumper && !ctx.gamepad1().right_bumper),
                    exec(() -> Intake.INSTANCE.setMode(Intake.Mode.IDLE))
            );

            // Flywheel
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().right_trigger > 0.05),
                    exec(() -> Flywheel.INSTANCE.enableAutoRange())
            );
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().right_trigger <= 0.05),
                    exec(() -> Flywheel.INSTANCE.stop())
            );

            // Shooter Gates
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().x),
                    exec(() -> Release.INSTANCE.autoLeftShot())
            );
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().b),
                    exec(() -> Release.INSTANCE.autoRightShot())
            );

            // Elevator
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().options && ctx.gamepad1().share),
                    exec(() -> Elevator.INSTANCE.applyPreset())
            );
            ctx.bindSpawn(ctx.risingEdge(() -> ctx.gamepad1().dpad_up),
                    exec(() -> Elevator.INSTANCE.startRise())
            );

            // Shut Off
            ctx.dropToScheduler();
        });
    }

    public static final Mercurial.RegisterableProgram DEBUG = buildTeleOp(Constants.Vision.DEFAULT_PIPELINE, true);
    public static final Mercurial.RegisterableProgram RED = buildTeleOp(Constants.Vision.RED_PIPELINE, false);
    public static final Mercurial.RegisterableProgram BLUE = buildTeleOp(Constants.Vision.BLUE_PIPELINE, false);
}