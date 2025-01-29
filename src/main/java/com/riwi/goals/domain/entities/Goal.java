package com.riwi.goals.domain.entities;

import com.riwi.goals.domain.enums.Status;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "goals") // Opcional: Cambiar el nombre de la tabla en la BD
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate; // Fecha de inicio definida por el usuario

    @Column(nullable = false)
    private LocalDate endDate; // Fecha de finalización

    @Column(nullable = true)
    private Double currentMount; // Monto actual alcanzado

    @Column(nullable = true)
    private Status status; // Estado de la meta

    @Column(nullable = false)
    private Double targetValue; // Valor objetivo

    @Column(nullable = false)
    private Long userId; // ID del usuario al que pertenece esta meta

    // Método para verificar si la meta está eliminada
    @Getter
    @Column(nullable = false)
    private boolean deleted = false; // Soft delete (no elimina físicamente el registro)

    // Método para verificar si la meta está cancelada
    public boolean isCancelled() {
        return Status.CANCELLED.equals(this.status);
    }


}
