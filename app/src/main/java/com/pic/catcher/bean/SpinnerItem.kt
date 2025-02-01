package com.pic.catcher.bean

class SpinnerItem(text: String, items: List<CharSequence>, selectIndex: Int = 0) : UiItem() {
    var title: CharSequence by observableProperty(text, "title")
    var items: List<CharSequence> by observableProperty(items, "items")
    var selectedIndex: Int by observableProperty(selectIndex, "selectedIndex")
    val selectedItem: CharSequence?
        get() {
            return if (selectedIndex < 0 || selectedIndex >= items.size) null else items[selectedIndex]
        }
}