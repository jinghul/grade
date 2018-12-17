package twelve.team;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import twelve.team.controllers.MainPane;

import java.util.Stack;

public class Router {
    private static final int DEFAULT_ROW = 0;
    private static final int DEFAULT_COL = 1;

    private MainPane main;
    private StackPane stackPane;
    private Node current;
    private Stack<String> backStack;

    private static Router router;

    private Router(MainPane main) {
        this.main = main;
        stackPane = (StackPane) main.getRoot().getChildren().get(DEFAULT_COL);
        current = null;
        backStack = new Stack<>();
    }

    public static Router create(MainPane main) {
        if (router == null) {
            router = new Router(main);
        }

        router.main = main;
        router.current = main.getRoot().getChildren().get(DEFAULT_COL);
        router.clearStacks();
        return router;
    }

    public StackPane getStackPane() { return stackPane; }

    public static Router getRouter() {
        return router;
    }

    public boolean canGoBack() {
        return !backStack.empty();
    }

    public boolean goBack() {
        if (backStack.empty()) {
            return false;
        } else {
            backStack.pop();
            stackPane.getChildren().remove(stackPane.getChildren().size() - 1);
            return true;
        }
    }

    public boolean addPane(Node pane, boolean fromSidebar) {
        if (current != null && checkValidRequest(pane)) {
            if (!fromSidebar) {
                backStack.push(current.getClass().getName());
            } else {
                stackPane.getChildren().clear();
                clearStacks();
            }

            displayPane(pane);

            return true;
        } else {
            return false;
        }
    }

    private void displayPane(Node pane) {
        stackPane.getChildren().add(pane);
        main.getRoot().requestFocus();
        current = pane;

        System.out.println("Displaying pane: " + pane.getClass().getName());
        System.out.println("Current backstack: " + backStack.toString());
    }

    public void clearStacks() {
        backStack.clear();
    }

    private boolean checkValidRequest(Node request) {
        return !current.getClass().isInstance(request);
    }
}
