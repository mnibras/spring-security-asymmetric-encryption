package com.nibras.app;

import com.nibras.app.role.Role;
import com.nibras.app.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final RoleRepository roleRepository) {
        return args -> {
            final Optional<Role> role = roleRepository.findByName("ROLE_USER");
            if (role.isEmpty()) {
                final Role newRole = new Role();
                newRole.setName("ROLE_USER");
                newRole.setCreatedBy("system");
                roleRepository.save(newRole);
            }
        };
    }

}
