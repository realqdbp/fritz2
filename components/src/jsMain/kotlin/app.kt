import dev.fritz2.binding.const
import dev.fritz2.components.f2Flex
import dev.fritz2.components.Link
import dev.fritz2.dom.html.render
import dev.fritz2.dom.mount
import dev.fritz2.routing.router
import dev.fritz2.styling.theme.currentTheme
import dev.fritz2.styling.theme.render
import kotlinx.coroutines.ExperimentalCoroutinesApi

val themes = listOf<Pair<String, ExtendedTheme>>(
    ("small Fonts") to SmallFonts(),
    ("large Fonts") to LargeFonts()
)

@ExperimentalCoroutinesApi
fun main() {
    currentTheme = themes.first().second

    val router = router("")

    render { theme: ExtendedTheme ->
        section {
            f2Flex {
                height { "60px" }
                wrap { nowrap }
                direction { row }
                justifyContent { spaceEvenly }
                alignItems { center }
            }.apply {
                Link{
                    flex {
                        //grow { "2" }
                        //order { "1" }
                        //alignSelf { flexStart }
                    }
                }.apply() {
                    href = const("#")
                    +"flex"
                }
                Link().apply() {
                    href = const("#grid")
                    +"grid"
                }
                Link().apply() {
                    href = const("#input")
                    +"input"
                }
                Link().apply() {
                    href = const("#formcontrol")
                    +"formcontrol"
                }
                Link().apply() {
                    href = const("#buttons")
                    +"buttons"
                }

            }
            router.render { site ->
                when (site) {
                    "grid" -> gridDemo()
                    "input" -> inputDemo()
                    "buttons" -> buttonDemo(theme)
                    "formcontrol" -> formControlDemo()
                    else -> flexDemo(theme)
                }
            }.bind()
        }
    }.mount("target")
}
