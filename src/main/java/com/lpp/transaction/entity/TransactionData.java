package com.lpp.transaction.entity;

import com.lpp.transaction.enums.OperationTypeEnum;
import com.lpp.transaction.enums.TradeStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author ：liupanpan
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_transaction_data", uniqueConstraints = {
        @UniqueConstraint(name = "uk_sys_transaction_data", columnNames = {"trade_id", "version"})})
public class TransactionData {
    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sys_transaction_id")
    @SequenceGenerator(name = "seq_sys_transaction_id", sequenceName = "seq_sys_transaction_id", allocationSize = 1)
    private Long transactionId;

    @Column(name = "trade_id", nullable = false)
    private Long tradeId;

    @Column(name = "version", nullable = false)
    private Long version;

    @Column(name = "security_code", columnDefinition = "char(3) not null")
    private String securityCode;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "operation_type", columnDefinition = "varchar(10) not null")
    @Enumerated(EnumType.STRING)
    private OperationTypeEnum operationType;

    @Column(name = "trade_status", columnDefinition = "varchar(4) not null")
    @Enumerated(EnumType.STRING)
    private TradeStatusEnum tradeStatus;
    
}
