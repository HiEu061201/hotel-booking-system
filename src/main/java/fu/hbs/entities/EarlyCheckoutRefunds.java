package fu.hbs.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(name = "early_checkout_refunds")
public class EarlyCheckoutRefunds {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "early_checkout_refund_id")
    private Long earlyCheckoutRefundId;
    private Long transactionId;
    private BigDecimal refundedAmount;
    private Instant refundDate;
    private String reason;
    private Boolean status;
}