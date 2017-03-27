package com.foamtec.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="MaterialPlanning")
public class MaterialPlanning implements Serializable {

    @Id
    @Column(name="id")
    private Long id;

    @Column(name="materialFoamtec", unique = true)
    private String materialFoamtec;

    @Column(name="materialCustomer")
    private String materialCustomer;

    @Column(name="materialGroup")
    private String materialGroup;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaterialFoamtec() {
        return materialFoamtec;
    }

    public void setMaterialFoamtec(String materialFoamtec) {
        this.materialFoamtec = materialFoamtec;
    }

    public String getMaterialCustomer() {
        return materialCustomer;
    }

    public void setMaterialCustomer(String materialCustomer) {
        this.materialCustomer = materialCustomer;
    }

    public String getMaterialGroup() {
        return materialGroup;
    }

    public void setMaterialGroup(String materialGroup) {
        this.materialGroup = materialGroup;
    }
}
