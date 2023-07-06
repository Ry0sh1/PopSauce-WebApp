package com.ryoshi.PopSauce.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pictures {

    @Id
    private Long id;
    private String category;
    private byte[] content;
    @Value("right_guess")
    private String rightGuess;

}
