# Contributing to Guest Gallery

We welcome contributions to Guest Gallery! Please review this document to ensure a smooth contribution workflow.

## Code of Conduct

By participating in this project, you agree to abide by our [Code of Conduct](CODE_OF_CONDUCT.md).

## Development Setup

1. Fork and clone the repository.
2. Download required Inter fonts:
   ```bash
   ./scripts/download-fonts.sh
   ```
3. Open the project in Android Studio.
4. Ensure your IDE is using JDK 17.

## Code Quality Standards

We enforce strict formatting and static code quality policies. Before submitting a PR, run the local analysis suite:

```bash
# Run Kotlin linting formatter checks
./gradlew ktlintCheck

# Run detekt static code analysis
./gradlew detekt

# Run Android SDK lint suite
./gradlew lintDebug

# Execute all unit tests
./gradlew testDebugUnitTest
```

### Commit Guidelines

We use conventional commit messages to maintain clean git histories. Format commits as:
- `feat(viewer): add double tap zoom physics`
- `fix(security): resolve biometric fallback crash`
- `docs(readme): update build prerequisites`

Please ensure all PRs target the `develop` or `main` branches and pass all GitHub Action CI checks before requesting review.
