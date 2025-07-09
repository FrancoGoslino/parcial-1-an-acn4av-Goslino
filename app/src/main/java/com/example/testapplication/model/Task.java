package com.example.testapplication.model;

import java.util.Date;

public class Task {
    private String titulo;
    private String descripcion;
    private Date fecha;



    public Task (String titulo,String descripcion,Date fecha){
        this.titulo=titulo;
        this.descripcion=descripcion;
        this.fecha=fecha;
    }
    //GETTERS Y SETTERS
    public void setTitulo(String titulo){
        this.titulo=titulo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public String gettitulo(){
        return titulo;
    }
    public String getDescripcion(){
        return descripcion;
    }
    public Date getFecha(){
        return fecha;
    }

}

