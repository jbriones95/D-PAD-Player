package com.example.dpadplayer

import android.view.View
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator

/**
 * Controller for the shared `view_state_message` block.
 *
 * Every list screen in the app needs the same three states — still loading,
 * genuinely empty, and "empty because your search matched nothing" — and they
 * read very differently to the user. Keeping the binding logic in one place
 * means a new tab only has to say *which* state it is in.
 */
class StateViewBinder(private val root: View) {

    private val progress: CircularProgressIndicator = root.findViewById(R.id.state_progress)
    private val icon: android.widget.ImageView = root.findViewById(R.id.state_icon)
    private val title: TextView = root.findViewById(R.id.state_title)
    private val body: TextView = root.findViewById(R.id.state_body)
    private val action: MaterialButton = root.findViewById(R.id.state_action)

    /** True while the loading spinner is the visible state. */
    val isLoading: Boolean get() = progress.visibility == View.VISIBLE

    fun showLoading(message: String? = null) {
        root.visibility = View.VISIBLE
        progress.visibility = View.VISIBLE
        icon.visibility = View.GONE
        action.visibility = View.GONE
        title.visibility = if (message.isNullOrBlank()) View.GONE else View.VISIBLE
        title.text = message ?: ""
        body.visibility = View.GONE
    }

    fun showEmpty(
        titleText: CharSequence,
        bodyText: CharSequence? = null,
        iconRes: Int? = null,
        actionLabel: CharSequence? = null,
        onAction: (() -> Unit)? = null
    ) {
        root.visibility = View.VISIBLE
        progress.visibility = View.GONE
        icon.visibility = if (iconRes != null) View.VISIBLE else View.GONE
        if (iconRes != null) icon.setImageResource(iconRes)
        title.visibility = if (titleText.isBlank()) View.GONE else View.VISIBLE
        title.text = titleText
        body.visibility = if (bodyText.isNullOrBlank()) View.GONE else View.VISIBLE
        body.text = bodyText ?: ""
        if (actionLabel != null && onAction != null) {
            action.visibility = View.VISIBLE
            action.text = actionLabel
            action.setOnClickListener { onAction() }
            applyPlayerControlFocusBackground(action)
            action.setupDpadItem(onFocusChanged = materialButtonFocusChangeHandler(action)) { onAction() }
        } else {
            action.visibility = View.GONE
            action.setOnClickListener(null)
        }
    }

    fun showNoResults(query: String) {
        showEmpty(
            titleText = root.context.getString(R.string.search_empty_title),
            bodyText = root.context.getString(R.string.search_empty_body, query.trim()),
            iconRes = R.drawable.ic_search
        )
    }

    fun hide() {
        root.visibility = View.GONE
    }
}

/**
 * Applies the shared list-screen state to a tab/detail screen.
 *
 * @param isSearching true when a non-blank search query is narrowing the list,
 *   which turns "nothing here" into "nothing matched" rather than an empty library.
 */
fun bindListState(
    root: View,
    itemCount: Int,
    isLoading: Boolean,
    isSearching: Boolean,
    query: String,
    emptyTitle: CharSequence,
    emptyBody: CharSequence? = null,
    emptyIcon: Int? = null,
    actionLabel: CharSequence? = null,
    onAction: (() -> Unit)? = null
) {
    val state = StateViewBinder(root.findViewById(R.id.state_container))
    when {
        isLoading -> state.showLoading(root.context.getString(R.string.scanning_library))
        itemCount > 0 -> state.hide()
        isSearching -> state.showNoResults(query)
        else -> state.showEmpty(emptyTitle, emptyBody, emptyIcon, actionLabel, onAction)
    }
}
