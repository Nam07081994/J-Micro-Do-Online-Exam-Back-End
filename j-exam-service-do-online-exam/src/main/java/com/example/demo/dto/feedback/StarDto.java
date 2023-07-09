package com.example.demo.dto.feedback;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StarDto {
	private List<Integer> stars;
	private List<Float> values;
}
