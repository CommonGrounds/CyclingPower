package dev.java4now.web.custom_ui;

import dev.webfx.platform.console.Console;
import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.StackPane;
import dev.webfx.extras.webtext.HtmlText;
import javafx.util.Duration;

public class CustomTooltip extends Control {
    private String text;
    private TooltipPosition position = TooltipPosition.TOP;
    private TooltipType type = TooltipType.DEFAULT;
    private TooltipSize size = TooltipSize.MEDIUM;
    private boolean showArrow = false;
    private double offsetX = 0;
    private double offsetY = 0;

    // Za tracking pozicije
    private double targetX = 0;
    private double targetY = 0;

    public enum TooltipPosition {
        TOP, BOTTOM, LEFT, RIGHT
    }

    public enum TooltipType {
        DEFAULT, INFO, SUCCESS, WARNING, ERROR
    }

    public enum TooltipSize {
        SMALL, MEDIUM, LARGE
    }

    public CustomTooltip(String text) {
        this.text = text;
        getStyleClass().add("custom-tooltip");
        setVisible(false);
        setManaged(false);
    }

    public CustomTooltip() {
        this("");
    }

    // Getteri i setteri
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).updateText(text);
        }
    }

    public void setPosition(TooltipPosition position) {
        this.position = position;
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).updateStyleClasses();
        }
    }

    public void setType(TooltipType type) {
        this.type = type;
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).updateStyleClasses();
        }
    }

    public void setSize(TooltipSize size) {
        this.size = size;
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).updateStyleClasses();
        }
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).updateStyleClasses();
        }
    }

    public void setOffset(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CustomTooltipSkin(this);
    }

    // Metode za upravljanje prikazom
    public void show() {
        setVisible(true);
        toFront(); // Uvek na vrh
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).show();
        }
    }

    public void hide() {
        if (getSkin() != null) {
            ((CustomTooltipSkin) getSkin()).hide();
        }
        setVisible(false);
    }

    // Osnovna metoda za pozicioniranje
    public void showAt(double x, double y) {
        // Console.log("showAt: " + x + ", " + y);
        targetX = x;
        targetY = y;

        // Postavi apsolutnu poziciju
        setLayoutX(x + offsetX);
        setLayoutY(y + offsetY);

        show();

        // Console.log("Tooltip positioned at: " + getLayoutX() + ", " + getLayoutY());
    }

    // Metoda za node - koristi samo dostupne metode
    public void showForNode(Node node) {
        showForNode(node, position);
    }

    public void showForNode(Node node, TooltipPosition position) {
        // Console.log("showForNode for: " + node);

        // Dobavi osnovne podatke o node-u
        double nodeX = node.getLayoutX();
        double nodeY = node.getLayoutY();
        double nodeWidth = node.getBoundsInLocal().getWidth();
        double nodeHeight = node.getBoundsInLocal().getHeight();

        // Console.log("Node layout: " + nodeX + ", " + nodeY);
        // Console.log("Node size: " + nodeWidth + "x" + nodeHeight);

        // Izračunaj poziciju
        double x = 0, y = 0;

        switch (position) {
            case TOP:
                x = nodeX; // + nodeWidth / 2; // important - direktno iznad
                y = nodeY - 5;
                break;
            case BOTTOM:
                x = nodeX;// + nodeWidth / 2;  // important - direktno ispod
                y = nodeY + nodeHeight + 5;
                break;
            case LEFT:
                x = nodeX - 5;
                y = nodeY + nodeHeight / 2;
                break;
            case RIGHT:
                x = nodeX + nodeWidth + 5;
                y = nodeY + nodeHeight / 2;
                break;
        }

        // Ako je node u parent-u, dodaj parent poziciju
        if (node.getParent() != null) {
            double parentX = node.getParent().getLayoutX();
            double parentY = node.getParent().getLayoutY();
            x += parentX;
            y += parentY;
            // Console.log("Added parent offset: " + parentX + ", " + parentY);
        }

        // Console.log("Calculated position: " + x + ", " + y);
        showAt(x, y);
    }

    // Nova metoda: prikaz u odnosu na miš
    public void showAtMouse(double mouseX, double mouseY) {
        // Offset od miša
        double offsetFromMouse = 15;
        showAt(mouseX + offsetFromMouse, mouseY + offsetFromMouse);
    }

    // Skin klasa
    private static class CustomTooltipSkin extends SkinBase<CustomTooltip> {
        private final StackPane container;
        private final HtmlText textNode;

        protected CustomTooltipSkin(CustomTooltip control) {
            super(control);

            textNode = new HtmlText(control.text);
            textNode.getStyleClass().add("tooltip-text");

            container = new StackPane(textNode);
            container.getStyleClass().add("tooltip-container");

            updateStyleClasses();

            // Inicijalno sakrij
            container.setVisible(false);
            container.setOpacity(0);

            getChildren().add(container);
        }

        private void updateText(String text) {
            textNode.setText(text);
        }

        private void updateStyleClasses() {
            container.getStyleClass().removeAll("default", "info", "success", "warning", "error",
                    "small", "medium", "large", "with-arrow",
                    "arrow-top", "arrow-bottom", "arrow-left", "arrow-right");

            container.getStyleClass().add(getSkinnable().type.name().toLowerCase());
            container.getStyleClass().add(getSkinnable().size.name().toLowerCase());

            if (getSkinnable().showArrow) {
                container.getStyleClass().add("with-arrow");
                container.getStyleClass().add("arrow-" + getSkinnable().position.name().toLowerCase());
            }
        }

        public void show() {
            container.setVisible(true);
            container.setOpacity(1);
            container.getStyleClass().add("visible");
            container.getStyleClass().add("animate-fade-in");

            // Force layout
            container.requestLayout();
            getSkinnable().requestLayout();
        }

        public void hide() {
            container.getStyleClass().remove("visible");
            container.getStyleClass().add("animate-fade-out");

            PauseTransition delay = new PauseTransition(Duration.millis(200));
            delay.setOnFinished(e -> {
                container.setVisible(false);
                container.getStyleClass().remove("animate-fade-out");
                container.setOpacity(0);
            });
            delay.play();
        }

        @Override
        protected void layoutChildren(double contentX, double contentY,
                                      double contentWidth, double contentHeight) {
            super.layoutChildren(contentX, contentY, contentWidth, contentHeight);

            double containerWidth = container.prefWidth(-1) + 6;
            double containerHeight = container.prefHeight(-1) + 6;

            double x = container.getLayoutX();
            double y = container.getLayoutY();

//            Console.log("Container size: " + containerWidth + "x" + containerHeight);

            if (getSkinnable().position == TooltipPosition.LEFT) {
 //               Console.log("container width: " + containerWidth);
                // Centriraj container u tooltip-u
                container.resizeRelocate(
                        (contentWidth - containerWidth) / 2 ,
                        (contentHeight - containerHeight) / 2,
                        containerWidth,
                        containerHeight
                );
//                container.setLayoutX( - containerWidth);
            }else {
                // Centriraj container u tooltip-u
                container.resizeRelocate(
                        (contentWidth - containerWidth) / 2,
                        (contentHeight - containerHeight) / 2,
                        containerWidth,
                        containerHeight
                );
            }
        }

//        @Override
        protected double computeMinWidth(double height) {
            return Math.max(50, container.minWidth(height));
        }

//        @Override
        protected double computeMinHeight(double width) {
            return Math.max(20, container.minHeight(width));
        }

//        @Override
        protected double computePrefWidth(double height) {
            return container.prefWidth(height) + 20;
        }

//        @Override
        protected double computePrefHeight(double width) {
            return container.prefHeight(width) + 10;
        }

//        @Override
        protected double computeMaxWidth(double height) {
            return 300;
        }
    }

    // Helper metode
    public static CustomTooltip createInfo(String text) {
        CustomTooltip tooltip = new CustomTooltip(text);
        tooltip.setType(TooltipType.INFO);
        return tooltip;
    }

    public static CustomTooltip createSuccess(String text) {
        CustomTooltip tooltip = new CustomTooltip(text);
        tooltip.setType(TooltipType.SUCCESS);
        return tooltip;
    }

    public static CustomTooltip createWarning(String text) {
        CustomTooltip tooltip = new CustomTooltip(text);
        tooltip.setType(TooltipType.WARNING);
        return tooltip;
    }

    public static CustomTooltip createError(String text) {
        CustomTooltip tooltip = new CustomTooltip(text);
        tooltip.setType(TooltipType.ERROR);
        return tooltip;
    }
}