package com.example.dpadplayer

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
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
 *   1. Focused state: high-contrast outline with a very light tint
 *   2. Activated state: subtle now-playing tint
 *   3. RippleDrawable for touch feedback
 *
 * Call this once per ViewHolder in its `init` block.
 */
fun applyItemFocusBackground(view: View) {
    val primary = resolveColor(view.context, com.google.android.material.R.attr.colorPrimary)
    val onSurface = resolveColor(view.context, com.google.android.material.R.attr.colorOnSurface)

    // Keep fill light so overlay-based focus targets do not hide underlying titles.
    val focusFill = Color.argb(0x16, Color.red(primary), Color.green(primary), Color.blue(primary))
    val activeFill = Color.argb(0x12, Color.red(primary), Color.green(primary), Color.blue(primary))
    val focusOuterStroke = Color.argb(0xD9, Color.red(onSurface), Color.green(onSurface), Color.blue(onSurface))
    val focusInnerStroke = Color.argb(0xFF, Color.red(primary), Color.green(primary), Color.blue(primary))
    val activeStroke = Color.argb(0xB3, Color.red(primary), Color.green(primary), Color.blue(primary))

    val focusOuter = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 10f)
        setColor(Color.TRANSPARENT)
        setStroke(dp(view.context, 2f).toInt(), focusOuterStroke)
    }

    val focusInner = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 8f)
        setColor(focusFill)
        setStroke(dp(view.context, 2f).toInt(), focusInnerStroke)
    }

    val focusDrawable = LayerDrawable(arrayOf(focusOuter, focusInner)).apply {
        val inset = dp(view.context, 2f).toInt()
        setLayerInset(1, inset, inset, inset, inset)
    }

    val activeDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 8f)
        setColor(activeFill)
        setStroke(dp(view.context, 1.5f).toInt(), activeStroke)
    }

    val stateDrawable = StateListDrawable().apply {
        // focused + playing: focus remains dominant over the now-playing state
        addState(intArrayOf(android.R.attr.state_selected, android.R.attr.state_activated), focusDrawable)
        addState(intArrayOf(android.R.attr.state_selected), focusDrawable)
        addState(intArrayOf(android.R.attr.state_activated), activeDrawable)
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

fun applyMiniPlayerFocusBackground(view: View) {
    val primary = resolveColor(view.context, com.google.android.material.R.attr.colorPrimary)
    val onSurface = resolveColor(view.context, com.google.android.material.R.attr.colorOnSurface)

    val focusFill = Color.argb(0x1E, Color.red(primary), Color.green(primary), Color.blue(primary))
    val activeFill = Color.argb(0x10, Color.red(primary), Color.green(primary), Color.blue(primary))
    val focusOuterStroke = Color.argb(0xFF, Color.red(onSurface), Color.green(onSurface), Color.blue(onSurface))
    val focusInnerStroke = Color.argb(0xFF, Color.red(primary), Color.green(primary), Color.blue(primary))
    val activeStroke = Color.argb(0xCC, Color.red(primary), Color.green(primary), Color.blue(primary))

    val focusOuter = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 14f)
        setColor(Color.TRANSPARENT)
        setStroke(dp(view.context, 3f).toInt(), focusOuterStroke)
    }

    val focusInner = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 11f)
        setColor(focusFill)
        setStroke(dp(view.context, 2f).toInt(), focusInnerStroke)
    }

    val focusDrawable = LayerDrawable(arrayOf(focusOuter, focusInner)).apply {
        val inset = dp(view.context, 3f).toInt()
        setLayerInset(1, inset, inset, inset, inset)
    }

    val activeDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = dp(view.context, 11f)
        setColor(activeFill)
        setStroke(dp(view.context, 2f).toInt(), activeStroke)
    }

    val stateDrawable = StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_selected, android.R.attr.state_activated), focusDrawable)
        addState(intArrayOf(android.R.attr.state_selected), focusDrawable)
        addState(intArrayOf(android.R.attr.state_activated), activeDrawable)
        addState(intArrayOf(), ColorDrawable(Color.TRANSPARENT))
    }

    val rippleColor = resolveColor(view.context, android.R.attr.colorControlHighlight)
    view.background = RippleDrawable(
        ColorStateList.valueOf(rippleColor),
        stateDrawable,
        null
    )
}

private fun dp(context: Context, value: Float): Float =
    value * context.resources.displayMetrics.density

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
