package twelve.team.utils;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animator {

    private static final int DEFAULT_FADE_TIME = 300;

    public static void fadeIn(Node root, EventHandler<ActionEvent> handler) {
        FadeTransition fadeIn = new FadeTransition(Duration.millis(DEFAULT_FADE_TIME), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        if (handler != null) {
            fadeIn.setOnFinished(handler);
        }

        fadeIn.play();
    }

    public static void fadeOut(Node root, EventHandler<ActionEvent> handler) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(DEFAULT_FADE_TIME), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        if (handler != null) {
            fadeOut.setOnFinished(handler);
        }

        fadeOut.play();
    }
}
