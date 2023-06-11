package com.example.demo.command;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleCommand {
	@NotEmpty(message = "Role name is mandatory")
	private String name;

	private List<Long> endPoint;
}
