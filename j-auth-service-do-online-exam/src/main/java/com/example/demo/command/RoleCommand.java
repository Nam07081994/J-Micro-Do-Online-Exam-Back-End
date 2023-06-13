package com.example.demo.command;

import com.example.demo.common.annotations.CheckListSize;
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

	@CheckListSize(message = "Endpoints are mandatory")
	private List<Long> endPoint;
}
