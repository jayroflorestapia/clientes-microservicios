package com.examen.mx.clientedatos.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "clientes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_clientes_nombre_completo_normalizado",
                columnNames = "nombre_completo_normalizado"
        )
)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 50)
    private String apellidoMaterno;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(nullable = false, length = 1)
    private String sexo;

    @Column(nullable = false, length = 100)
    private String correo;

    @Column(nullable = false, length = 10)
    private String telefono;

    @Column(
            name = "nombre_completo_normalizado",
            nullable = false,
            length = 160
    )
    private String nombreCompletoNormalizado;

    public Cliente() {
        // Constructor requerido por JPA
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }

    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreCompletoNormalizado() {
        return nombreCompletoNormalizado;
    }

    public void setNombreCompletoNormalizado(String nombreCompletoNormalizado) {
        this.nombreCompletoNormalizado = nombreCompletoNormalizado;
    }
}