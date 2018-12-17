package twelve.team.utils;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import com.mysql.cj.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import twelve.team.Router;

public final class Dialog {
    public static void showAlertDialog(String headerText, String contentText,  DialogListener<Boolean> confirmHandler) {
        JFXDialogLayout content = new JFXDialogLayout();

        Text header = new Text(headerText);
        header.getStyleClass().add("text-md");
        content.setHeading(header);
        content.setBody(new Text(contentText));

        JFXDialog alert = new JFXDialog(Router.getRouter().getStackPane(), content, JFXDialog.DialogTransition.CENTER);

        JFXButton doneButton = new JFXButton("Confirm");
        doneButton.setButtonType(JFXButton.ButtonType.RAISED);

        doneButton.getStyleClass().add("button-primary");
        doneButton.getStyleClass().add("button-red");
        doneButton.setOnAction(e -> {
            alert.close();
            confirmHandler.receive(true);
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.getStyleClass().add("button-props");
        cancelButton.setOnAction(e -> {
            alert.close();
        });

        content.setActions(doneButton, cancelButton);
        alert.show();
    }

    public static void showCommentDialog(String name, String prevComment, DialogListener<String> saveHandler) {
        JFXDialogLayout content = new JFXDialogLayout();

        Text header = new Text(name);
        header.getStyleClass().add("text-md");
        content.setHeading(header);

        JFXTextField commentField = new JFXTextField();
        commentField.setPromptText("Enter a comment.");
        if (!StringUtils.isNullOrEmpty(prevComment)) {
            commentField.setText(prevComment);
        }

        content.setBody(commentField);

        JFXDialog alert = new JFXDialog(Router.getRouter().getStackPane(), content, JFXDialog.DialogTransition.CENTER);

        JFXButton doneButton = new JFXButton("Confirm");
        doneButton.setButtonType(JFXButton.ButtonType.RAISED);

        doneButton.getStyleClass().add("button-primary");
        doneButton.getStyleClass().add("background-gray");
        doneButton.setOnAction(e -> {
            alert.close();
            saveHandler.receive(commentField.getText());
        });

        JFXButton cancelButton = new JFXButton("Cancel");
        cancelButton.getStyleClass().add("button-props");
        cancelButton.setOnAction(e -> {
            alert.close();
        });

        content.setActions(doneButton, cancelButton);
        alert.show();
    }

}
