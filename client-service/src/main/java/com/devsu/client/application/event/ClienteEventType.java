package com.devsu.client.application.event;

public final class ClienteEventType {

    public static final String AGGREGATE_TYPE = "CLIENTE";
    public static final String CREADO = "ClienteCreado";
    public static final String ACTUALIZADO = "ClienteActualizado";
    public static final String ELIMINADO = "ClienteEliminado";

    private ClienteEventType() {
    }
}
