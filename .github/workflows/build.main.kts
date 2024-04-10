#!/usr/bin/env kotlin
@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("io.github.typesafegithub:github-workflows-kt:1.13.0")

@file:Repository("https://github-workflows-kt-bindings.colman.com.br/binding/")
@file:DependsOn("actions:checkout:v4")
@file:DependsOn("actions:setup-java:v4")
@file:DependsOn("gradle:gradle-build-action:v3")

import io.github.typesafegithub.workflows.actions.actions.Checkout
import io.github.typesafegithub.workflows.actions.actions.SetupJava
import io.github.typesafegithub.workflows.actions.gradle.GradleBuildAction
import io.github.typesafegithub.workflows.domain.RunnerType
import io.github.typesafegithub.workflows.domain.triggers.PullRequest
import io.github.typesafegithub.workflows.domain.triggers.Push
import io.github.typesafegithub.workflows.dsl.expressions.Contexts
import io.github.typesafegithub.workflows.dsl.workflow
import io.github.typesafegithub.workflows.yaml.writeToFile


val GPG_KEY by Contexts.secrets

workflow(
  name = "Build Universal APK",
  on = listOf(Push(), PullRequest()),
  sourceFile = __FILE__.toPath()
) {
  job(id = "build", runsOn = RunnerType.UbuntuLatest) {
    uses(name = "Set up JDK", action = SetupJava(javaVersion = "17", distribution = SetupJava.Distribution.Adopt))
    uses(action = Checkout())
    uses(name = "Create Fdroid APK", action = GradleBuildAction(arguments = "packageFdroidReleaseUniversalApk"))
    uses(name = "Create Playstore APK", action = GradleBuildAction(arguments = "packagePlaystoreReleaseUniversalApk"))
    uses(name = "Create Github APK", action = GradleBuildAction(arguments = "packageGithubReleaseUniversalApk"))
  }
}.writeToFile()
