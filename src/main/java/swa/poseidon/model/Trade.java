package swa.poseidon.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.sql.Timestamp;


@Entity
@Table(name = "trade")
public class Trade {
    // TODO: Map columns in data table TRADE with corresponding java fields
	@Id
	int tradeId;
}
