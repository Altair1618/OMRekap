# OMRekap

## Development
### Clone the repository
```bash
git clone https://gitlab.informatika.org/k-02-09/omrekap
```

### Run Unit Test
```bash
./gradlew test
```

### Code Formatting
```bash
./gradlew spotlessApply
```

### Changing Formatting Configuration
* Update spotless.gradle based on Ktlint rules [here](https://pinterest.github.io/ktlint/0.50.0/rules/configuration-ktlint/)
* Clean gradle cache
```bash
./gradlew clean
* ```
