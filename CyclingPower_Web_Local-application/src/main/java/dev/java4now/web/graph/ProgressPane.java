package dev.java4now.web.graph;

import dev.webfx.extras.webtext.HtmlText;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.*;

public class ProgressPane {

    public static StackPane root;
    private static Timeline countdown;
    public static double graphicon_width = 0;
    public static double graphicon_height = 0;

    public static void create(double width, double height, Pane parent) {
        // padding: 40px; /* Top, Right, Bottom, Left */
        String loader = """
                    <div style="display: flex; justify-content: center; align-items: top;
                                    min-height: 200px; background: rgba(255, 255, 255, 0.0); padding: 40px 40px 40px 40px; font-family: sans-serif;">
                        <div class="loader"></div>
                        <div class="loader-text">
                            <h3>Server Waiting...</h3>
                            <p>
                              <span style="font-size: 1em;">Sorry !!!</span>
                              <span style="font-size: 1.5em;">🐌</span>
                            </p>
                        </div>
                    </div>
                
                    <style>
                        :root {
                            --loader-size: 40px;
                            --loader-gap: 30px;
                        }
                
                        .loader {
                            --c1: #673b14;
                            --c2: #f8b13b;
                            width: var(--loader-size);
                            height: calc(var(--loader-size) * 2);
                            border-top: 4px solid var(--c1);
                            border-bottom: 4px solid var(--c1);
                            background: linear-gradient(90deg, var(--c1) 2px, var(--c2) 0 5px, var(--c1) 0) 50%/7px 8px no-repeat;
                            display: grid;
                            overflow: hidden;
                            animation: l5-0 2s infinite linear;
                        }
                
                        .loader::before,
                        .loader::after {
                            content: "";
                            grid-area: 1/1;
                            width: 75%;
                            height: calc(50% - 4px);
                            margin: 0 auto;
                            border: 2px solid var(--c1);
                            border-top: 0;
                            box-sizing: content-box;
                            border-radius: 0 0 40% 40%;
                            -webkit-mask:
                                linear-gradient(#000 0 0) bottom/4px 2px no-repeat,
                                linear-gradient(#000 0 0);
                            -webkit-mask-composite: destination-out;
                            mask-composite: exclude;
                            background:
                                linear-gradient(var(--d,0deg), var(--c2) 50%, #0000 0) bottom /100% 205%,
                                linear-gradient(var(--c2) 0 0) center/0 100%;
                            background-repeat: no-repeat;
                            animation: inherit;
                            animation-name: l5-1;
                        }
                
                        .loader::after {
                            transform-origin: 50% calc(100% + 2px);
                            transform: scaleY(-1);
                            --s: 3px;
                            --d: 180deg;
                        }
                
                        .loader-text {
                            margin-left: var(--loader-gap);
                            color: black;
                        }
                
                        .loader-text h3 {
                            margin: 0 0 10px 0;
                        }
                
                        .loader-text p {
                            margin: 0;
                            opacity: 0.8;
                        }
                
                        @keyframes l5-0 {
                            80% { transform: rotate(0); }
                            100% { transform: rotate(0.5turn); }
                        }
                
                        @keyframes l5-1 {
                            10%, 70% { background-size: 100% 205%, var(--s,0) 100%; }
                            70%, 100% { background-position: top, center; }
                        }
                
                        @media (max-height: 650px) {
                            :root {
                                --loader-size: 25px;
                            }
                
                            display: grid;
                            margin-top: 0px;
                            overflow: hidden;
                
                            .loader-text {
                                margin-left: 0;
                                margin-top: none;
                                text-align: left;
                            }
                        }
                
                        @media (max-width: 520px) {
                            :root {
                                --loader-size: 25px;
                            }
                
                            div[style*="display: flex"] {
                                flex-direction: column;
                                align-items: center;
                            }
                
                            .loader-text {
                                margin-left: 0;
                                margin-top: 20px;
                                text-align: center;
                            }
                        }
                    </style>
                """;

        HtmlText loader_html = new HtmlText(loader);      // rotating hour glass

        root = new StackPane();
        root.getChildren().addAll(loader_html);
        root.setPrefSize(width , height );
        loader_html.setPrefSize(root.getPrefWidth(), root.getPrefHeight());
//        root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 200, 0, 1), null, null)));

        parent.getChildren().add(root);
    }
}
