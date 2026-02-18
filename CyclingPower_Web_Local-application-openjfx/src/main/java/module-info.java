// File managed by WebFX (DO NOT EDIT MANUALLY)

module CyclingPower_Web_Local.application.openjfx {

    // Direct dependencies modules
    requires CyclingPower_Web_Local.application;
    requires javafx.web;
    requires service;
    requires webfx.extras.filepicker.openjfx;
    requires webfx.extras.visual.charts.peers.openjfx;
    requires webfx.extras.webtext.peers.openjfx;
    requires webfx.kit.javafxgraphics.openjfx;
    requires webfx.kit.platform.audio.openjfx.web;
    requires webfx.kit.platform.visibility.openjfx;
    requires webfx.platform.ast.factory.generic;
    requires webfx.platform.blob;
    requires webfx.platform.blob.jre;
    requires webfx.platform.boot.java;
    requires webfx.platform.console.java;
    requires webfx.platform.fetch.jre;
    requires webfx.platform.file.jre;
    requires webfx.platform.os.jre;
    requires webfx.platform.resource.jre;
    requires webfx.platform.scheduler.jre;
    requires webfx.platform.shutdown.jre;
    requires webfx.platform.storage.jre;
    requires webfx.platform.storagelocation.jre;
    requires webfx.platform.useragent.jre.client;
    requires webfx.platform.windowhistory.jre;
    requires webfx.platform.windowlocation.jre;
    requires webfx.stack.com.websocket.jre;

    // Exported packages
    exports openjfx.service;

    // Provided services
    provides service.ServiceProvider with openjfx.service.OpenJFXMyServiceProvider;

}