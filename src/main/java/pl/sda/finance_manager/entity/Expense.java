package pl.sda.finance_manager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
                ", category name=" + category.getName() +
                ", date=" + date +
                ", commentary='" + commentary + '\'' +
                '}';
    }

    public Expense(double amount, Category category, LocalDate date, String commentary) {
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.commentary = commentary;
    }
}
