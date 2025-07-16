package com.example.testapplication.model;

import java.util.Date;
import java.util.List;

public class Task {
    private String titulo;
    private String descripcion;
    private Date fecha;
    private List<String> diasRepeticion;
    private String id;

    public Task() {
        // Constructor vacío requerido por Firestore
    }

    public Task(String titulo, String descripcion, Date fecha, List<String> diasRepeticion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.diasRepeticion = diasRepeticion;
    }


    // Getter y setter
    public void setId(String id) { this.id = id; }
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Date getFecha() { return fecha; }
    public List<String> getDiasRepeticion() { return diasRepeticion; }

    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    public void setDiasRepeticion(List<String> diasRepeticion) { this.diasRepeticion = diasRepeticion; }
}


