package dev.java4now.web.custom_ui;

import dev.webfx.platform.console.Console;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class TooltipHelper {
    private static final Map<Node, CustomTooltip> nodeTooltips = new HashMap<>();

    public static void attachTooltip(Node node, String text) {
        attachTooltip(node, text, CustomTooltip.TooltipPosition.TOP);
    }

    public static void attachTooltip(Node node, String text,
                                     CustomTooltip.TooltipPosition position) {
        attachTooltip(node, text, position, CustomTooltip.TooltipType.DEFAULT,false);
    }

    public static void attachTooltip(Node node, String text,
                                     CustomTooltip.TooltipPosition position,
                                     CustomTooltip.TooltipType type , boolean showArrow) {
        // Console.log("=== Attaching tooltip ===");

        // Ukloni postojeći tooltip
        detachTooltip(node);

        // Kreiraj tooltip
        CustomTooltip tooltip = new CustomTooltip(text);
        tooltip.setPosition(position);
        tooltip.setType(type);
        if(showArrow) tooltip.setShowArrow(true);

        // Dodaj tooltip u scenu
        Scene scene = node.getScene();
        if (scene != null && scene.getRoot() instanceof Pane) {
            Pane root = (Pane) scene.getRoot();
            root.getChildren().add(tooltip);
            // Console.log("Tooltip added to scene root");
        } else if (node.getParent() instanceof Pane) {
            Pane parent = (Pane) node.getParent();
            parent.getChildren().add(tooltip);
            // Console.log("Tooltip added to node's parent");
        } else {
            // Console.log("WARNING: No suitable parent found");
            return;
        }

        // Simple hover handling
        node.hoverProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Console.log("Hover detected - showing tooltip");
                // Use the node's position relative to its parent
                showTooltipRelativeToNode(node, tooltip, position);
            } else {
                // Console.log("Hover ended - hiding tooltip");
                // Kratak delay pre sakrivanja
                var pause = new javafx.animation.PauseTransition();
                pause.setDuration(javafx.util.Duration.millis(100));
                pause.setOnFinished(ev -> {
                    if (!tooltip.isHover()) {
                        tooltip.hide();
                    }
                });
                pause.play();
            }
        });

        // Also handle mouse events for more reliability
        node.setOnMouseEntered(e -> {
            // Console.log("Mouse entered node");
            showTooltipRelativeToNode(node, tooltip, position);
        });

        node.setOnMouseExited(e -> {
            // Console.log("Mouse exited node");
            // Kratak delay pre sakrivanja
            var pause = new javafx.animation.PauseTransition();
            pause.setDuration(javafx.util.Duration.millis(100));
            pause.setOnFinished(ev -> {
                if (!isMouseOverTooltipOrNode(tooltip, node)) {
                    tooltip.hide();
                }
            });
            pause.play();
        });

        nodeTooltips.put(node, tooltip);
    }

    private static void showTooltipRelativeToNode(Node node, CustomTooltip tooltip,
                                                  CustomTooltip.TooltipPosition position) {
        // Get node bounds in its local coordinate space
        Bounds localBounds = node.getBoundsInLocal();
        double nodeWidth = localBounds.getWidth();
        double nodeHeight = localBounds.getHeight();

        // Start with node's layout position
        double x = node.getLayoutX();
        double y = node.getLayoutY();

        // Console.log("Node layout: " + x + ", " + y + " [" + nodeWidth + "x" + nodeHeight + "]");

        // Calculate position based on the specified direction
        double tooltipX = 0, tooltipY = 0;

        switch (position) {
            case TOP:
                tooltipX = x;// + nodeWidth / 2; // important - direktno iznad
                tooltipY = y - 10;
                break;
            case BOTTOM:
                tooltipX = x;// + nodeWidth / 2; // important - direktno ispod
                tooltipY = y + nodeHeight + 10;
                break;
            case LEFT:
//                Console.log("tooltip_width: " + tooltip.prefWidth(-1));
                tooltipX = x - tooltip.prefWidth(-1) - 10;
                tooltipY = y + nodeHeight / 2;
                break;
            case RIGHT:
                tooltipX = x + nodeWidth + 10;
                tooltipY = y + nodeHeight / 2;
                break;
        }

        // Console.log("Tooltip position: " + tooltipX + ", " + tooltipY);
        tooltip.showAt(tooltipX, tooltipY);
    }

    private static boolean isMouseOverTooltipOrNode(CustomTooltip tooltip, Node node) {
        return tooltip.isHover() || node.isHover();
    }

    public static void detachTooltip(Node node) {
        CustomTooltip tooltip = nodeTooltips.remove(node);
        if (tooltip != null) {
            tooltip.hide();
            if (tooltip.getParent() != null && tooltip.getParent() instanceof Pane) {
                ((Pane) tooltip.getParent()).getChildren().remove(tooltip);
            }
        }
    }

    public static void updateTooltipText(Node node, String newText) {
        CustomTooltip tooltip = nodeTooltips.get(node);
        if (tooltip != null) {
            tooltip.setText(newText);
        }
    }

    // Simple method for quick tooltips
    public static void showQuickTooltip(Node node, String text, CustomTooltip.TooltipPosition position) {
        CustomTooltip tooltip = new CustomTooltip(text);

        // Add to scene if possible
        Scene scene = node.getScene();
        if (scene != null && scene.getRoot() instanceof Pane) {
            Pane root = (Pane) scene.getRoot();
            root.getChildren().add(tooltip);
            tooltip.toFront();

            // Position near the node
            double x = node.getLayoutX();
            double y = node.getLayoutY();
            Bounds bounds = node.getBoundsInLocal();

//            tooltip.showAt(x + bounds.getWidth() + 5, y);
            tooltip.showForNode(node, position);

            // Auto-hide after 3 seconds
            // Kratak delay pre sakrivanja
            var pause = new javafx.animation.PauseTransition();
            pause.setDuration(javafx.util.Duration.millis(1500));
            pause.setOnFinished(e -> {
                tooltip.hide();
                root.getChildren().remove(tooltip);
            });
            pause.play();
        }
    }
}