// File managed by WebFX (DO NOT EDIT MANUALLY)

module gwt_service {

    // Direct dependencies modules
    requires elemental2.dom;
    requires javafx.graphics;
    requires jsinterop.annotations;
    requires service;
    requires webfx.kit.javafxgraphics.peers.elemental2;
    requires webfx.platform.console;

    // Exported packages
    exports gwt_service;

    // Provided services
    provides service.ServiceProvider with gwt_service.GWT_MyServiceProvider;

}