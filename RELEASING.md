# Releasing guideline

## Maven/Sonatype release

### Release version
1. Raise `VERSION_NAME` in `gradle.properties` without a non-SNAPSHOT suffix
2. Update the `CHANGELOG.md`
3. Commit changes
4. Merge changes to `master`
5. Create new release with version name from `gradle.properties`
- library will be automatically deployed to Maven and Jitpack

### Snapshot version
1. Raise `VERSION_NAME` in `gradle.properties` with `-SNAPSHOT` suffix
2. Update the `CHANGELOG.md`
3. Commit changes
4. Merge changes to `develop`
5. Create new release with version name from `gradle.properties`
- library will be automatically deployed to Maven and Jitpack