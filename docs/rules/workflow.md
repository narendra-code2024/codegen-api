# Workflow Rules

How the agent operates on this codebase. These apply to every task.

1. **Research first.** Investigate the codebase before proposing changes.
2. **Plan before action.** For any modification, present a clear **Plan** and wait for
   user approval (or an explicit directive) before editing files.
3. **No edits during research.** Do not modify files during "Inquiry" or "Research"
   phases unless explicitly instructed.
4. **Validate.** After any change, run `./mvnw clean compile` to verify the build.
5. **Schema discipline.** All database changes go in `src/main/resources/db/migration/`.
   Never modify an existing migration file — always create a new one.
