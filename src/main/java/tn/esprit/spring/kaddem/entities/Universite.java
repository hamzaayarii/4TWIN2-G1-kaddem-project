package tn.esprit.spring.kaddem.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "universite")
public class Universite implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUniversite;

    private String nomUniv;

    public Universite(Integer idUniversite, String nomUniv) {
        this.idUniversite = idUniversite;
        this.nomUniv = nomUniv;
    }
}
