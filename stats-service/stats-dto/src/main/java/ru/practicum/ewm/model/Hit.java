package ru.practicum.ewm.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder

@Entity
@Table(name = "hits")
public class Hit {
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "app", nullable = false, length = 64)
    private String app;

    @Column(name = "uri", nullable = false, length = 2048)
    private String uri;

    @Column(name = "ip", nullable = false, length = 16)
    private String ip;

    @Column(name = "hit_at")
    private LocalDateTime hitAt;
}
