package com.example.dpadplayer;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0007H\u0002J\u0012\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u001a\u0010\u0017\u001a\u0004\u0018\u00010\u00162\u0006\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u0007H\u0016J*\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00162\b\u0010\u0018\u001a\u0004\u0018\u00010\u0016H\u0016R7\u0010\u0005\u001a\u001f\u0012\u0013\u0012\u00110\u0007\u00a2\u0006\f\b\b\u0012\b\b\t\u0012\u0004\b\b(\n\u0012\u0004\u0012\u00020\u000b\u0018\u00010\u0006X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\r\"\u0004\b\u000e\u0010\u000f\u00a8\u0006 "}, d2 = {"Lcom/example/dpadplayer/FocusLinearLayoutManager;", "Landroidx/recyclerview/widget/LinearLayoutManager;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "onFocusPosition", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "position", "", "getOnFocusPosition", "()Lkotlin/jvm/functions/Function1;", "setOnFocusPosition", "(Lkotlin/jvm/functions/Function1;)V", "dirName", "", "d", "findOwnerRecyclerView", "Landroidx/recyclerview/widget/RecyclerView;", "view", "Landroid/view/View;", "onInterceptFocusSearch", "focused", "direction", "onRequestChildFocus", "", "parent", "state", "Landroidx/recyclerview/widget/RecyclerView$State;", "child", "app_debug"})
public final class FocusLinearLayoutManager extends androidx.recyclerview.widget.LinearLayoutManager {
    
    /**
     * Called whenever a child at [position] receives focus. Use to track last-focused position.
     */
    @org.jetbrains.annotations.Nullable
    private kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onFocusPosition;
    
    public FocusLinearLayoutManager(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super(null);
    }
    
    /**
     * Called whenever a child at [position] receives focus. Use to track last-focused position.
     */
    @org.jetbrains.annotations.Nullable
    public final kotlin.jvm.functions.Function1<java.lang.Integer, kotlin.Unit> getOnFocusPosition() {
        return null;
    }
    
    /**
     * Called whenever a child at [position] receives focus. Use to track last-focused position.
     */
    public final void setOnFocusPosition(@org.jetbrains.annotations.Nullable
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> p0) {
    }
    
    @java.lang.Override
    public boolean onRequestChildFocus(@org.jetbrains.annotations.NotNull
    androidx.recyclerview.widget.RecyclerView parent, @org.jetbrains.annotations.NotNull
    androidx.recyclerview.widget.RecyclerView.State state, @org.jetbrains.annotations.NotNull
    android.view.View child, @org.jetbrains.annotations.Nullable
    android.view.View focused) {
        return false;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.Nullable
    public android.view.View onInterceptFocusSearch(@org.jetbrains.annotations.NotNull
    android.view.View focused, int direction) {
        return null;
    }
    
    private final androidx.recyclerview.widget.RecyclerView findOwnerRecyclerView(android.view.View view) {
        return null;
    }
    
    private final java.lang.String dirName(int d) {
        return null;
    }
}