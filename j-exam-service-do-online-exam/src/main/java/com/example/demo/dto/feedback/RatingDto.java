package com.example.demo.dto.feedback;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RatingDto {
	private String ranking;
	private double totalRating;
	private Object ratingData;
}
