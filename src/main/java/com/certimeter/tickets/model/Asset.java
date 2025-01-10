package com.certimeter.tickets.model;

import com.certimeter.tickets.enumeration.AssetStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String modelName;

    @Column(name = "type")
    private String type;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "status")
    private AssetStatus status;

    @Column(name = "cost")
    private BigDecimal cost;

    @Column(name = "user_id")
    private Long userID;
//    maybe I don't need this
//    @ManyToOne
//    @JoinColumn(name = "user_id", insertable = false, updatable = false)
//    private User user;

    @Column(name = "deleted")
    private boolean deleted = false; // New field for soft delete
}

