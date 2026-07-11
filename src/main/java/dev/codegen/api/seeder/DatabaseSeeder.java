package dev.codegen.api.seeder;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.codegen.api.entity.Project;
import dev.codegen.api.entity.ProjectMember;
import dev.codegen.api.entity.User;
import dev.codegen.api.enums.ProjectMemberRole;
import dev.codegen.api.repository.ProjectMemberRepository;
import dev.codegen.api.repository.ProjectRepository;
import dev.codegen.api.repository.UserRepository;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DatabaseSeeder reads seed configurations from seed-data.json and seeds the database with initial
 * users and projects. This class runs automatically on application startup when the "dev" or
 * "local" profiles are active and the database has no existing users.
 */
@Component
@Profile({"dev", "local"})
@RequiredArgsConstructor
@Slf4j
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository projectMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("classpath:db/seed-data.json")
    private Resource seedDataResource;

    @Value("${seeder.default-password}")
    private String defaultPassword;

    // DTO records for parsing JSON data
    private record SeedDataDto(List<SeedUserDto> users, List<SeedProjectDto> projects) {}

    private record SeedUserDto(String email, String username) {}

    private record SeedProjectDto(
            String name, String createdByEmail, List<SeedMemberDto> members) {}

    private record SeedMemberDto(String email, ProjectMemberRole role) {}

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking if database seeding is required...");
        if (userRepository.count() > 0) {
            log.info("Database already contains users. Skipping seeding.");
            return;
        }

        if (!seedDataResource.exists()) {
            log.warn(
                    "Seed data resource (seed-data.json) not found on classpath. Skipping seeding.");
            return;
        }

        log.info("Seeding database with test data from JSON configuration...");
        try (InputStream inputStream = seedDataResource.getInputStream()) {
            SeedDataDto seedData = objectMapper.readValue(inputStream, SeedDataDto.class);

            // 1. Seed Users
            Map<String, User> userEmailMap = seedUsers(seedData.users());

            // 2. Seed Projects & Members
            seedProjectsAndMembers(seedData.projects(), userEmailMap);

            log.info("Database seeding completed successfully.");
        } catch (IOException e) {
            log.error("Failed to read or parse seed-data.json", e);
            throw new IllegalStateException("Database seeding failed", e);
        }
    }

    private Map<String, User> seedUsers(List<SeedUserDto> userDtos) {
        log.info("Creating seed users...");
        Map<String, User> userEmailMap = new HashMap<>();

        if (userDtos == null) {
            return userEmailMap;
        }

        String encodedPassword = passwordEncoder.encode(defaultPassword);

        for (SeedUserDto userDto : userDtos) {
            User user = new User();
            user.setEmail(userDto.email());
            user.setUsername(userDto.username());
            user.setPassword(encodedPassword);

            User savedUser = userRepository.save(user);
            userEmailMap.put(savedUser.getEmail(), savedUser);
            log.info("Seeded user: {} ({})", savedUser.getUsername(), savedUser.getEmail());
        }

        return userEmailMap;
    }

    private void seedProjectsAndMembers(
            List<SeedProjectDto> projectDtos, Map<String, User> userEmailMap) {
        log.info("Creating seed projects and members...");

        if (projectDtos == null) {
            return;
        }

        for (SeedProjectDto projectDto : projectDtos) {
            User creator = userEmailMap.get(projectDto.createdByEmail());
            if (creator == null) {
                log.warn(
                        "Creator email {} not found for project {}. Skipping project seeding.",
                        projectDto.createdByEmail(),
                        projectDto.name());
                continue;
            }

            Project project = new Project();
            project.setName(projectDto.name());
            project.setCreatedBy(creator);
            Project savedProject = projectRepository.save(project);

            // The creator is automatically added as OWNER
            addMember(savedProject, creator, ProjectMemberRole.OWNER);

            // Add other members defined in the JSON file
            if (projectDto.members() != null) {
                for (SeedMemberDto memberDto : projectDto.members()) {
                    User memberUser = userEmailMap.get(memberDto.email());
                    if (memberUser == null) {
                        log.warn(
                                "Member email {} not found for project {}. Skipping member.",
                                memberDto.email(),
                                projectDto.name());
                        continue;
                    }
                    addMember(savedProject, memberUser, memberDto.role());
                }
            }
            log.info(
                    "Seeded Project: {} (Owner: {})",
                    savedProject.getName(),
                    creator.getUsername());
        }
    }

    private void addMember(Project project, User user, ProjectMemberRole role) {
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(role);
        projectMemberRepository.save(member);
        log.debug(
                "Added user {} to project {} with role {}",
                user.getUsername(),
                project.getName(),
                role);
    }
}
