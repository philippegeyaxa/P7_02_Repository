package swa.poseidon.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level=AccessLevel.PRIVATE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Rating 
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	Integer ratingId;

	@NotBlank
	@Column(length = 125)
	String moodysRating;

	@NotBlank
	@Column(length = 125)
	String standPoorRating;

	@NotBlank
	@Column(length = 125)
	String fitchRating;

	@NotNull
	@Positive
	Integer orderNumber;
	
	Rating(String moodysRating, String standPoorRating, String fitchRating, Integer orderNumber)
	{
		// ratingId set to 0
		this.moodysRating=moodysRating;
		this.standPoorRating=standPoorRating;
		this.fitchRating=fitchRating;
		this.orderNumber=orderNumber;
	}
}
