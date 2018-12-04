package twelve.team;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import twelve.team.controllers.HomePane;
import twelve.team.controllers.MainPane;

import java.util.Stack;

public class Router {
    private static final int DEFAULT_ROW = 0;
    private static final int DEFAULT_COL = 1;

    private MainPane main;
    private Node current;
    private Stack<Node> backStack;
    private Stack<Node> forwardStack;

    private static Router router;

    private Router(MainPane main) {
        this.main = main;
        current = null;
        backStack = new Stack<>();
        forwardStack = new Stack<>();
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

    public MainPane getMainPane() {
        return main;
    }

    public static Router getRouter() {
        return router;
    }

    public MainPane getMain() {
        return main;
    }

    public boolean goBack() {
        if (backStack.empty()) {
            return false;
        } else {
            forwardStack.push(current);
            displayPane(backStack.pop());
            return true;
        }
    }

    public boolean goForward() {
        if (forwardStack.empty()) {
            return false;
        } else {
            backStack.push(current);
            displayPane(forwardStack.pop());
            return true;
        }
    }

    public boolean addPane(Node pane, boolean fromSidebar) {
        if (checkValidRequest(pane, fromSidebar)) {
            if (current != null && !fromSidebar) {
                backStack.push(current);
            }

            displayPane(pane);

            return true;
        } else {
            return false;
        }
    }

    private void displayPane(Node pane) {
        GridPane.setConstraints(pane, DEFAULT_COL, DEFAULT_ROW);
        System.out.println(main.getRoot().getChildren().toString());
        main.getRoot().getChildren().remove(DEFAULT_COL);
        main.getRoot().getChildren().add(pane);
        current = pane;

        System.out.println("Displaying pane: " + pane.getClass().getName());
        System.out.println("Current backstack: " + backStack.toString());
        System.out.println("Current forwardstack: " + forwardStack.toString() + "\n");
    }

    public void clearStacks() {
        backStack.clear();
        forwardStack.clear();
    }

    private boolean checkValidRequest(Node request, boolean fromSidebar) {
        return (fromSidebar && !(current.getClass().isInstance(request))) || (!fromSidebar && request != current);
    }
}
