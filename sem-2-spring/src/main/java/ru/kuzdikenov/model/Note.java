package ru.kuzdikenov.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@EqualsAndHashCode
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private Instant createdAt;

    @Column(name = "is_public")
    private boolean published;

    @ManyToOne(targetEntity = User.class)
    @JoinColumn(name = "author_id")
    private User author;
}
