package swa.poseidon.form;

import swa.poseidon.model.Rating;

public class RatingForm extends Rating // Extension to keep distinct classes for future evolution
{
	public RatingForm(Rating r)
	{
		super.setRatingId(r.getRatingId());
		super.setMoodysRating(r.getMoodysRating());
		super.setStandPoorRating(r.getStandPoorRating());
		super.setFitchRating(r.getFitchRating());
		super.setOrderNumber(r.getOrderNumber());
	}
}
