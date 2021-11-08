package dev.fritz2.lenses

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.assertj.core.api.Assertions
import java.io.File
import java.nio.charset.StandardCharsets
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createTempDirectory
import kotlin.test.Test

class LensesProcessorTests {

    @ExperimentalPathApi
    private fun compileSource(vararg source: SourceFile) = KotlinCompilation().apply {
        sources = source.toList()
        symbolProcessorProviders = listOf(LensesGeneratorProcessorProvider())
        workingDir = createTempDirectory("fritz2-tests").toFile()
        inheritClassPath = true
        verbose = false
    }.compile()

    // workaround copied by https://github.com/tschuchortdev/kotlin-compile-testing/issues/129#issuecomment-804390310
    internal val KotlinCompilation.Result.workingDir: File
        get() =
            outputDirectory.parentFile!!

    // workaround inspired by https://github.com/tschuchortdev/kotlin-compile-testing/issues/129#issuecomment-804390310
    val KotlinCompilation.Result.kspGeneratedSources: List<File>
        get() {
            val kspWorkingDir = workingDir.resolve("ksp")
            val kspGeneratedDir = kspWorkingDir.resolve("sources")
            val kotlinGeneratedDir = kspGeneratedDir.resolve("kotlin")
            val javaGeneratedDir = kspGeneratedDir.resolve("java")
            return kotlinGeneratedDir.walkTopDown().toList() +
                    javaGeneratedDir.walkTopDown()
        }

    @ExperimentalPathApi
    @Test
    fun `validate lenses generation works`() {
        val kotlinSource = SourceFile.kotlin(
            "file_lenses.kt", """
                package dev.fritz2.lensetest

                import dev.fritz2.lenses.Lenses

                class MyType

                @Lenses
                data class Foo(
                    val bar: Int,
                    val foo: String,
                    val fooBar: MyType
                ) {
                    companion object
                }
            """
        )

        val compilationResult = compileSource(kotlinSource)

        Assertions.assertThat(compilationResult.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        Assertions.assertThat(
            compilationResult.kspGeneratedSources.find { it.name == "FooLenses.kt" }
        ).usingCharset(StandardCharsets.UTF_8).hasContent(
            """
            |// GENERATED by fritz2 - NEVER CHANGE CONTENT MANUALLY!
            |package dev.fritz2.lensetest
            |
            |import dev.fritz2.lenses.Lens
            |import kotlin.Int
            |import kotlin.String
            |
            |public val Foo.Companion.bar: Lens<Foo, Int>
            |  get() = buildLens("bar", { it.bar }, { p, v -> p.copy(bar = v)})
            |
            |public val Foo.Companion.foo: Lens<Foo, String>
            |  get() = buildLens("foo", { it.foo }, { p, v -> p.copy(foo = v)})
            |
            |public val Foo.Companion.fooBar: Lens<Foo, MyType>
            |  get() = buildLens("fooBar", { it.fooBar }, { p, v -> p.copy(fooBar = v)})
            """.trimMargin().trim()
        )
    }

    @ExperimentalPathApi
    @Test
    fun `lenses ignore none ctor properties`() {
        val kotlinSource = SourceFile.kotlin(
            "file_lenses.kt", """
                package dev.fritz2.lensetest

                import dev.fritz2.lenses.Lenses

                @Lenses
                data class Foo(val bar: Int) {
                    companion object
                    val ignored = bar + 1 // must not appear in lense!
                }
            """
        )

        val compilationResult = compileSource(kotlinSource)

        Assertions.assertThat(compilationResult.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
        Assertions.assertThat(
            compilationResult.kspGeneratedSources.find { it.name == "FooLenses.kt" }
        ).usingCharset(StandardCharsets.UTF_8).hasContent(
            """
            |// GENERATED by fritz2 - NEVER CHANGE CONTENT MANUALLY!
            |package dev.fritz2.lensetest
            |
            |import dev.fritz2.lenses.Lens
            |import kotlin.Int
            |
            |public val Foo.Companion.bar: Lens<Foo, Int>
            |  get() = buildLens("bar", { it.bar }, { p, v -> p.copy(bar = v)})
            """.trimMargin().trim()
        )
    }

}