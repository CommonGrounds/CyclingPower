// File managed by WebFX (DO NOT EDIT MANUALLY)

module CyclingPower_Web_Local.application {

    // Direct dependencies modules
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires service;
    requires webfx.extras.filepicker;
    requires webfx.extras.fonticons;
    requires webfx.extras.fonticons.feather;
    requires webfx.extras.type;
    requires webfx.extras.visual;
    requires webfx.extras.visual.charts;
    requires webfx.extras.webtext;
    requires webfx.platform.ast;
    requires webfx.platform.ast.json.plugin;
    requires webfx.platform.async;
    requires webfx.platform.audio;
    requires webfx.platform.blob;
    requires webfx.platform.console;
    requires webfx.platform.fetch;
    requires webfx.platform.file;
    requires webfx.platform.os;
    requires webfx.platform.resource;
    requires webfx.platform.scheduler;
    requires webfx.platform.shutdown;
    requires webfx.platform.storage;
    requires webfx.platform.useragent;
    requires webfx.platform.util;
    requires webfx.platform.windowlocation;
    requires webfx.stack.com.serial;
    requires webfx.stack.com.websocket;

    // Exported packages
    exports dev.java4now.web;
    exports dev.java4now.web.CityLocation;
    exports dev.java4now.web.charts;
    exports dev.java4now.web.custom_ui;
    exports dev.java4now.web.effects;
    exports dev.java4now.web.generator;
    exports dev.java4now.web.graph;
    exports dev.java4now.web.http;
    exports dev.java4now.web.icons;
    exports dev.java4now.web.maps;
    exports dev.java4now.web.model;
    exports dev.java4now.web.pojo;
    exports dev.java4now.web.util;
    exports dev.java4now.web.view;
    exports dev.java4now.web.websocket;

    // Resources packages
    opens dev.java4now.web.css;
    opens dev.java4now.web.data;
    opens dev.java4now.web.fonts;
    opens dev.java4now.web.mm_api_symbols;
    opens dev.java4now.web.pics;

    // Provided services
    provides dev.webfx.stack.com.serial.spi.SerialCodec with dev.java4now.web.pojo.UserSerialCodec;
    provides javafx.application.Application with dev.java4now.web.CyclingPower_Web_Local;

}