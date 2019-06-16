package maryk

fun String.prepend0(totalCharacters: Int): String {
    var newString = this
    for (it in 0 .. totalCharacters - this.length) {
        newString = "0$newString"
    }
    return newString
}
