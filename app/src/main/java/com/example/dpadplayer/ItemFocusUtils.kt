package com.example.dpadplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View

/**
 * Applies a D-pad-friendly focus background to a list item view.
 *
 * Layers (bottom → top):
 *   1. Transparent base
 *   2. State-list overlay: colorPrimary@18% when focused (isSelected), colorPrimary@10% when activated
 *   3. RippleDrawable for touch feedback
 *
 * Call this once per ViewHolder in its `init` block.
 */
fun applyItemFocusBackground(view: View) {
    val primary = resolveColor(view.context, com.google.android.material.R.attr.colorPrimary)

    // Focus highlight (state_selected = hasFocus proxy)
    val focusColor   = Color.argb(0x2E, Color.red(primary), Color.green(primary), Color.blue(primary))
    // Activated highlight (currently-playing track)
    val activeColor  = Color.argb(0x1F, Color.red(primary), Color.green(primary), Color.blue(primary))

    val stateDrawable = StateListDrawable().apply {
        // focused + playing: show focus color (stronger, so user knows what's selected)
        addState(intArrayOf(android.R.attr.state_selected, android.R.attr.state_activated), ColorDrawable(focusColor))
        // focused only
        addState(intArrayOf(android.R.attr.state_selected),   ColorDrawable(focusColor))
        // playing only (not focused)
        addState(intArrayOf(android.R.attr.state_activated),  ColorDrawable(activeColor))
        addState(intArrayOf(),                                ColorDrawable(Color.TRANSPARENT))
    }

    val rippleColor = resolveColor(view.context, android.R.attr.colorControlHighlight)
    val ripple = RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        stateDrawable,
        null
    )
    view.background = ripple
}

/**
 * Resolves a theme color attribute to an ARGB int.
 */
fun resolveColor(context: Context, attrRes: Int): Int {
    val tv = TypedValue()
    context.theme.resolveAttribute(attrRes, tv, true)
    return if (tv.type >= TypedValue.TYPE_FIRST_COLOR_INT && tv.type <= TypedValue.TYPE_LAST_COLOR_INT) {
        tv.data
    } else {
        context.getColor(tv.resourceId)
    }
}

/**
 * Sets up standard D-pad behaviour on a RecyclerView item view:
 *  - onFocusChangeListener: sets isSelected = hasFocus (drives focus highlight)
 *  - onKeyListener: fires onClick when DPAD_CENTER or ENTER is pressed
 */
fun View.setupDpadItem(onClick: () -> Unit) {
    setOnFocusChangeListener { v, hasFocus -> v.isSelected = hasFocus }
    setOnKeyListener { _, keyCode, event ->
        if (event.action == KeyEvent.ACTION_DOWN &&
            (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
            onClick()
            true
        } else false
    }
}
