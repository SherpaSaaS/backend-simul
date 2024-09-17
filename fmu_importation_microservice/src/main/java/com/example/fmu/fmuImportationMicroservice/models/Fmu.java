package com.example.fmu.fmuImportationMicroservice.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table
public class Fmu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String filePath;

    private String fmiVersion;

    @Column(nullable = false , unique = true)
    private String modelName;

    private String description;

    private String author;

    private String version;

    private String modelImagePath;

    private String extensionWind;

    private String extensionLinux;

    @OneToMany(mappedBy = "fmu",cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    List<Variable> variableList;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(
            name = "fmu_blocks_controllers",
            joinColumns = @JoinColumn(name = "fmu_id"),
            inverseJoinColumns = @JoinColumn(name = "bloc_id"))
    List<InputVariableBloc> inputVariableBlocs;



    @Override
    public String toString() {
        return "Fmu{" +
                "id=" + id +
                ", filePath='" + filePath + '\'' +
                ", fmiVersion='" + fmiVersion + '\'' +
                ", modelName='" + modelName + '\'' +
                ", description='" + description + '\'' +
                ", author='" + author + '\'' +
                ", version='" + version + '\'' +
                ",modelImagePath= "+modelImagePath+ '\'' +
                ",extensionLinux= "+extensionLinux+ '\'' +
                ",extensionWind= "+extensionWind+ '\'' +
                '}';
    }
}
