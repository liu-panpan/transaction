package com.lpp.transaction.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author ：liupanpan
 */
@Entity
@Getter
@Setter
@Table(name = "sys_trade_sequence")
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TradeSequence {
    @Id
    @Column(name = "trade_id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_sys_trade_id")
    @SequenceGenerator(name = "seq_sys_trade_id", sequenceName = "seq_sys_trade_id", allocationSize = 1)
    private Long transactionId;
}
