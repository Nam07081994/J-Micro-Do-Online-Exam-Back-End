package com.example.demo.config.security.SecurityCustom;

import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {
	@Autowired private UserRepository repository;

	@Autowired private RoleRepository roleRepository;

	@Override
	public CustomUserDetails loadUserByUsername(String email) {
		Optional<User> user = repository.findByEmail(email);
		if (user.isEmpty()) {
			throw new UsernameNotFoundException("user not found with name :" + email);
		}

		List<String> roles = new ArrayList<>();
		for (Long roleID : user.get().getRoles()) {
			String roleName = roleRepository.findById(roleID).get().getRoleName();
			roles.add(roleName);
		}

		return new CustomUserDetails(user.get(), roles);
	}
}
