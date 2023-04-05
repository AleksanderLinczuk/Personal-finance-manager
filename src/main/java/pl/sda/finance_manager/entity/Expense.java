package pl.sda.finance_manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "expenses")
@NoArgsConstructor
@Getter
@Setter
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    private LocalDate date;
    private String commentary;

    @Override
    public String toString() {
        return "Expense{" +
                "id=" + id +
                ", amount=" + amount +
                ", category id=" + category.getId() +
                ", date=" + date +
                ", commentary='" + commentary + '\'' +
                '}';
    }
}
